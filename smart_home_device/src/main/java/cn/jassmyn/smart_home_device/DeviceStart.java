package cn.jassmyn.smart_home_device;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.apiclient.utils.StringUtils;
import com.aliyun.alink.dm.api.DeviceInfo;
import com.aliyun.alink.dm.api.InitResult;
import com.aliyun.alink.linkkit.api.ILinkKitConnectListener;
import com.aliyun.alink.linkkit.api.IoTMqttClientConfig;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linkkit.api.LinkKitInitParams;
import com.aliyun.alink.linksdk.channel.core.base.ARequest;
import com.aliyun.alink.linksdk.channel.core.base.IOnCallListener;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.MqttConfigure;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.MqttInitParams;
import com.aliyun.alink.linksdk.tmp.device.payload.ValueWrapper;
import com.aliyun.alink.linksdk.tools.AError;
import com.aliyun.alink.linksdk.tools.ALog;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * @Description: TODO
 * @author: zhangYuLin
 * @date: 2023/5/20 17:42
 */
public class DeviceStart {

    private static final String TAG = "DeviceStart";

    private String pk, dn;
    private ThingSample thingManager = null;

    private NotifyListener notifyListener = null;

    public static void main(String[] args) {
        ALog.d(TAG, "DeviceStart");
        ALog.setLevel(ALog.LEVEL_INFO);
        DeviceStart manager = new DeviceStart();
        Property property = new Property();
        ALog.d(TAG, "args=" + Arrays.toString(args));
        String diPath = manager.getClass().getClassLoader().getResource("device_id.json").getPath();
        String deviceInfo = FileUtils.readFile(diPath);
        if (deviceInfo == null) {
            ALog.e(TAG, "main - need device info path.");
            return;
        }
        Gson mGson = new Gson();
        DeviceInfoData deviceInfoData = mGson.fromJson(deviceInfo, DeviceInfoData.class);
        if (deviceInfoData == null) {
            ALog.e(TAG, "main - deviceInfo format error.");
            return;
        }

        // 如果device_id.json中没有设置deviceSecret, demo默认先走动态注册方式获取秘钥
        if (StringUtils.isEmptyString(deviceInfoData.deviceSecret)) {
            manager.deviceRegister(deviceInfoData);
            ALog.d(TAG, "测试一型一密动态注册，只测试动态注册");
            ALog.d(TAG, "请将获取到的deviceSecret填入到deviceId.json文件中继续一型一密的流程");
            return;
        }

        ALog.d(TAG, "测试一机一密和物模型");
        manager.init(deviceInfoData);
    }


    public void init(final DeviceInfoData deviceInfoData) {
        this.pk = deviceInfoData.productKey;
        this.dn = deviceInfoData.deviceName;
        LinkKitInitParams params = new LinkKitInitParams();
        /**
         * 设置 Mqtt 初始化参数
         */
        IoTMqttClientConfig config = new IoTMqttClientConfig();
        config.productKey = deviceInfoData.productKey;
        config.deviceName = deviceInfoData.deviceName;
        config.deviceSecret = deviceInfoData.deviceSecret;

        if (!deviceInfoData.instanceId.isEmpty()) {
            //如果实例详情页面有实例的id, 建议开发者填入实例id. 推荐的做法
            config.channelHost = "ssl://" + deviceInfoData.instanceId + ".mqtt.iothub.aliyuncs.com:443";
        } else {
            //如果实例详情页面没有实例的id, 建议开发者填入实例所在的region. 注：该用法不支持深圳和北京两个region
            config.channelHost = deviceInfoData.productKey + ".iot-as-mqtt." + deviceInfoData.region + ".aliyuncs.com:443";
        }

        /**
         * 是否接受离线消息
         * 对应 mqtt 的 cleanSession 字段
         */
        config.receiveOfflineMsg = false;
        params.mqttClientConfig = config;


        /**
         * 设置初始化三元组信息，用户传入
         */
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.productKey = pk;
        deviceInfo.deviceName = dn;
        deviceInfo.deviceSecret = deviceInfoData.deviceSecret;

        params.deviceInfo = deviceInfo;

        /**
         * 设置设备当前的初始状态值，属性需要和云端创建的物模型属性一致
         * 如果这里什么属性都不填，物模型就没有当前设备相关属性的初始值。
         * 用户调用物模型上报接口之后，物模型会有相关数据缓存。
         */
        Map<String, ValueWrapper> propertyValues = new HashMap<String, ValueWrapper>();
        params.propertyValues = propertyValues;
        params.fmVersion = "1.0.2";

        /**
         * 设备进行初始化，并连云
         */
        LinkKit.getInstance().init(params, new ILinkKitConnectListener() {
            @Override
            public void onError(AError aError) {
                ALog.e(TAG, "Init Error error=" + aError);
            }

            @Override
            public void onInitDone(InitResult initResult) {
                ALog.i(TAG, "onInitDone result=" + initResult);

                thingManager = new ThingSample(pk, dn);
                thingManager.setServiceHandler();
                thingManager.reportProperty();
                notifyListener = new NotifyListener();
                notifyListener.setNotifyListenner();
            }
        });
    }


    /**
     * 动态注册示例代码，适用于所有region
     * 1.现在云端创建产品和设备；
     * 2.在云端开启动态注册；
     * 3.填入pk、dn、ps；
     * 4.调用该方法；
     * 5.拿到deviceSecret返回之后 调初始化建联；
     */
    public void deviceRegister(DeviceInfoData deviceInfoData) {

        //动态注册step1: 确定一型一密的类型（免预注册, 还是非免预注册）
        //case 1: 如果registerType里面填写了regnwl, 表明设备的一型一密方式为免预注册（即无需创建设备）
        //case 2: 如果这个字段为空, 则表示为需要预注册的一型一密（需要实现创建设备）
        String registerType = "register";

        //动态注册step2: 设置动态注册的注册接入点域名
        if (!deviceInfoData.instanceId.isEmpty()) {
            //如果实例详情页面有实例的id, 建议开发者填入实例id. 推荐的做法
            MqttConfigure.mqttHost = "ssl://" + deviceInfoData.instanceId + ".mqtt.iothub.aliyuncs.com:443";
        } else {
            //如果实例详情页面没有实例的id, 建议开发者填入实例所在的region. 注：该用法不支持深圳和北京两个region
            MqttConfigure.mqttHost = deviceInfoData.productKey + ".iot-as-mqtt." + deviceInfoData.region + ".aliyuncs.com:443";
        }

        final MqttInitParams initParams = new MqttInitParams(deviceInfoData.productKey, deviceInfoData.productSecret, deviceInfoData.deviceName, "", registerType);

        //动态注册step3: 如果用户所用的实例为新版本的公共实例或者企业实例（控制台中有实例详情的页面）, 需设置动态注册的实例id
        initParams.instanceId = deviceInfoData.instanceId;

        final Object lock = new Object();
        LinkKit.getInstance().deviceDynamicRegister(initParams, new IOnCallListener() {
            @Override
            public void onSuccess(ARequest request, com.aliyun.alink.linksdk.channel.core.base.AResponse response) {
                try {
                    String responseData = new String((byte[]) response.data);
                    JSONObject jsonObject = JSONObject.parseObject(responseData);
                    // 一型一密免预注册返回
                    String clientId = jsonObject.getString("clientId");
                    String deviceToken = jsonObject.getString("deviceToken");
                    // 一型一密预注册返回
                    String deviceSecret = jsonObject.getString("deviceSecret");
                    ALog.e(TAG, "mqtt dynamic registration succeed，deviceSecret:" + deviceSecret);
                    synchronized (lock) {
                        lock.notify();
                    }

                } catch (Exception e) {
                }
            }

            @Override
            public void onFailed(ARequest aRequest, com.aliyun.alink.linksdk.channel.core.base.AError aError) {
                ALog.e(TAG, "mqtt dynamic registration failed");
                synchronized (lock) {
                    lock.notify();
                }
            }

            @Override
            public boolean needUISafety() {
                return false;
            }
        });

        try {
            //等待服务端下行报文，一般1s内就会返回
            synchronized (lock) {
                lock.wait(3000);
            }

            //动态注册step4: 关闭动态注册的实例.
            // 注意：该接口不能在LinkKit.getInstance().deviceDynamicRegister的onSuccess/onFailed回调中执行，否则会报错
            LinkKit.getInstance().stopDeviceDynamicRegister(2000, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    ALog.e(TAG, "mqtt dynamic registration success");
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    ALog.e(TAG, "mqtt dynamic registration failed");
                }
            });

        } catch (Exception e) {
        }
    }



}
