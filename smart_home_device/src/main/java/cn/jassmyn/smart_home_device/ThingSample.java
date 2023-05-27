package cn.jassmyn.smart_home_device;

import com.aliyun.alink.apiclient.utils.StringUtils;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttPublishRequest;
import com.aliyun.alink.linksdk.cmp.core.base.AMessage;
import com.aliyun.alink.linksdk.cmp.core.base.ARequest;
import com.aliyun.alink.linksdk.cmp.core.base.AResponse;
import com.aliyun.alink.linksdk.cmp.core.base.ConnectState;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectNotifyListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSendListener;
import com.aliyun.alink.linksdk.tmp.api.InputParams;
import com.aliyun.alink.linksdk.tmp.api.OutputParams;
import com.aliyun.alink.linksdk.tmp.device.payload.ValueWrapper;
import com.aliyun.alink.linksdk.tmp.devicemodel.Service;
import com.aliyun.alink.linksdk.tmp.listener.IPublishResourceListener;
import com.aliyun.alink.linksdk.tmp.listener.ITResRequestHandler;
import com.aliyun.alink.linksdk.tmp.listener.ITResResponseCallback;
import com.aliyun.alink.linksdk.tmp.utils.ErrorInfo;
import com.aliyun.alink.linksdk.tools.AError;
import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.alink.linksdk.tools.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingSample extends BaseSample {
    private static final String TAG = "ThingSample";

    private final static String SERVICE_SET = "set";
    private final static String SERVICE_GET = "get";
    private final static String CONNECT_ID = "LINK_PERSISTENT";

    private Integer lightSwitchDate = 0;

    public ThingSample(String pk, String dn) {
        super(pk, dn);
    }

    /*  上报属性  */
    public void reportProperty() {

        LinkKit.getInstance().getDeviceThing().thingPropertyPost(Property.propertyMap, new IPublishResourceListener() {
            @Override
            public void onSuccess(String s, Object o) {
                // 属性上报成功
                ALog.d(TAG, "上报成功 onSuccess() called with: s = [" + s + "], o = [" + o + "]");
            }

            @Override
            public void onError(String s, AError aError) {
                // 属性上报失败
                ALog.d(TAG, "上报失败onError() called with: s = [" + s + "], aError = [" + getError(aError) + "]");
            }
        });
    }


    /**
     * 设备端接收服务端的属性下发和服务下发的消息，并作出反馈
     */
    public void setServiceHandler() {
        ALog.d(TAG, "setServiceHandler() called");
        List<Service> srviceList = LinkKit.getInstance().getDeviceThing().getServices();
        for (int i = 0; srviceList != null && i < srviceList.size(); i++) {
            Service service = srviceList.get(i);
            LinkKit.getInstance().getDeviceThing().setServiceHandler(service.getIdentifier(), mCommonHandler);
        }
        LinkKit.getInstance().registerOnNotifyListener(connectNotifyListener);
    }

    private ITResRequestHandler mCommonHandler = new ITResRequestHandler() {
        @Override
        public void onProcess(String identify, Object result, ITResResponseCallback itResResponseCallback) {
            ALog.d(TAG, "onProcess() called with: s = [" + identify + "], o = [" + result + "], itResResponseCallback = [" + itResResponseCallback + "]");
            try {
                if (SERVICE_SET.equals(identify)) {

                    Map<String, ValueWrapper> data = (Map<String, ValueWrapper>) ((InputParams) result).getData();
                    // 如果控制台下发了属性OverTiltEnable，可以通过data.get("OverTiltEnable") 来获取相应的属性值
                    ALog.d(TAG, "收到下行数据 " + data);

                    data.forEach((k, v) -> {
                        if (Property.propertyMap.containsKey(k)) {
                            Property.propertyMap.put(k, v);
                        }
                    });
                    reportProperty();
                }
                if(SERVICE_GET.equals(identify)){
                    // 如果控制台下发了属性OverTiltEnable，可以通过data.get("OverTiltEnable") 来获取相应的属性值
                    ALog.d(TAG, "get请求 ");

                }
            } catch (Exception e) {
                e.printStackTrace();
                ALog.d(TAG, "TMP 返回数据格式异常");
            }
        }

        @Override
        public void onSuccess(Object o, OutputParams outputParams) {
            ALog.d(TAG, "onSuccess() called with: o = [" + o + "], outputParams = [" + outputParams + "]");
            ALog.d(TAG, "注册服务成功");
        }

        @Override
        public void onFail(Object o, ErrorInfo errorInfo) {
            ALog.d(TAG, "onFail() called with: o = [" + o + "], errorInfo = [" + errorInfo + "]");
            ALog.d(TAG, "注册服务失败");
        }
    };

    /**
     * 同步服务回调处理函数
     * 同步服务下行方式包括云端系统RRPC下行和用户自定义RRPC下行两种，在该函数中都分别进行处理
     * 设备收到同步服务后，需要通过LinkKit.getInstance().getMqttClient().publish接口进行及时回复，否则控制台会显示调用超时失败
     */
    private IConnectNotifyListener connectNotifyListener = new IConnectNotifyListener() {
        public void onNotify(String connectId, String topic, AMessage aMessage) {
            ALog.d(TAG, "onNotify() called with: connectId = [" + connectId + "], topic = [" + topic + "], aMessage = [" + printAMessage(aMessage) + "]");
            try {
                if (CONNECT_ID.equals(connectId) && !StringUtils.isEmptyString(topic) &&
                        topic.startsWith("/sys/" + productKey + "/" + deviceName + "/rrpc/request")) {
                    ALog.d(TAG, "收到云端系统RRPC下行" + printAMessage(aMessage));
                    // ALog.d(TAG, "receice Message=" + new String((byte[]) aMessage.data));
                    // 服务端返回数据示例  {"method":"thing.service.test_service","id":"123374967","params":{"vv":60},"version":"1.0.0"}
                    MqttPublishRequest request = new MqttPublishRequest();
                    request.isRPC = false;
                    request.topic = topic.replace("request", "response");
                    String resId = topic.substring(topic.indexOf("rrpc/request/") + 13);
                    request.msgId = resId;
                    request.payloadObj = "{\"id\":\"" + resId + "\", \"code\":\"200\"" + ",\"data\":{} }";
                    // aResponse.data =
                    LinkKit.getInstance().getMqttClient().publish(request, new IConnectSendListener() {
                        public void onResponse(ARequest aRequest, AResponse aResponse) {
                            ALog.d(TAG, "onResponse() called with: aRequest = [" + aRequest + "], aResponse = [" + aResponse + "]");
                        }

                        public void onFailure(ARequest aRequest, AError aError) {
                            ALog.d(TAG, "onFailure() called with: aRequest = [" + aRequest + "], aError = [" + getError(aError) + "]");
                        }
                    });
                } else if (CONNECT_ID.equals(connectId) && !TextUtils.isEmpty(topic) &&
                        topic.startsWith("/ext/rrpc/")) {
                    ALog.d(TAG, "收到云端自定义RRPC下行");
                    // ALog.d(TAG, "receice Message=" + new String((byte[]) aMessage.data));
                    // 服务端返回数据示例  {"method":"thing.service.test_service","id":"123374967","params":{"vv":60},"version":"1.0.0"}
                    MqttPublishRequest request = new MqttPublishRequest();
                    // 支持 0 和 1， 默认0
                    // request.qos = 0;
                    request.isRPC = false;
                    request.topic = topic.replace("request", "response");
                    String[] array = topic.split("/");
                    String resId = array[3];
                    request.msgId = resId;
                    request.payloadObj = "{\"id\":\"" + resId + "\", \"code\":\"200\"" + ",\"data\":{} }";
                    // aResponse.data =
                    LinkKit.getInstance().publish(request, new IConnectSendListener() {
                        @Override
                        public void onResponse(ARequest aRequest, AResponse aResponse) {
                            ALog.d(TAG, "onResponse() called with: aRequest = [" + aRequest + "], aResponse = [" + aResponse + "]");
                        }

                        @Override
                        public void onFailure(ARequest aRequest, AError aError) {
                            ALog.d(TAG, "onFailure() called with: aRequest = [" + aRequest + "], aError = [" + aError + "]");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean shouldHandle(String s, String s1) {
            return true;
        }

        public void onConnectStateChange(String s, ConnectState connectState) {
        }
    };

    private String printAMessage(AMessage aMessage) {
        return (aMessage == null || aMessage.data == null) ? "" : new String((byte[]) aMessage.data);
    }
}
