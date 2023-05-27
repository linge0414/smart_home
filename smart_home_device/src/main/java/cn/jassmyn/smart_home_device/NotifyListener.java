package cn.jassmyn.smart_home_device;

import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttSubscribeRequest;
import com.aliyun.alink.linksdk.cmp.core.base.AMessage;
import com.aliyun.alink.linksdk.cmp.core.base.ConnectState;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectNotifyListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSubscribeListener;
import com.aliyun.alink.linksdk.tools.AError;

/**
 * @Description: Topic监听类
 * @author: zhangYuLin
 * @date: 2023/5/20 22:07
 */
public class NotifyListener {

    public void setNotifyListener() {
        //设置订阅的Topic。
        MqttSubscribeRequest request = new MqttSubscribeRequest();
        request.topic = "/" + "inruRAbD7HE" + "/" + "device1" + "/user/getAllProperties";
        request.isSubscribe = true;
        //发出订阅请求并设置订阅成功或者失败的回调函数。
        LinkKit.getInstance().subscribe(request, new IConnectSubscribeListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(AError aError) {
            }
        });

        //设置订阅的下行消息到来时的回调函数。
        IConnectNotifyListener notifyListener = new IConnectNotifyListener() {
            //此处定义收到下行消息以后的回调函数。
            @Override
            public void onNotify(String connectId, String topic, AMessage aMessage) {
                System.out.println("received message from " + topic + ":" + new String((byte[])aMessage.getData()));
            }

            @Override
            public boolean shouldHandle(String s, String s1) {
                return false;
            }

            @Override
            public void onConnectStateChange(String s, ConnectState connectState) {

            }
        };
        LinkKit.getInstance().registerOnNotifyListener(notifyListener);
    }

}
