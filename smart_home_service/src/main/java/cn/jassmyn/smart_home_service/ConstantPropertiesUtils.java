package cn.jassmyn.smart_home_service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @author: zhangYuLin
 * @date: 2023/5/20 15:23
 */
@Component
public class ConstantPropertiesUtils implements InitializingBean {

    @Value("${iot.accessKey}")
    private String accessKey;
    @Value("${iot.accessSecret}")
    private String accessSecret;
    @Value("${iot.consumerGroupId}")
    private String consumerGroupId;

    //iotInstanceId：实例ID。若是2021年07月30日之前（不含当日）开通的公共实例，请填空字符串。
    @Value("${iot.iotInstanceId}")
    private String iotInstanceId;

    //控制台服务端订阅中消费组状态页客户端ID一栏将显示clientId参数。
    //建议使用机器UUID、MAC地址、IP等唯一标识等作为clientId。便于您区分识别不同的客户端。
    @Value("${iot.clientId}")
    private String clientId;

    //${YourHost}为接入域名，请参见AMQP客户端接入说明文档。
    @Value("${iot.host}")
    private String host;

    @Value("${iot.productKey}")
    private String productKey;

    @Value("${iot.deviceName}")
    private String deviceName;

    public static String ACCESS_KEY;

    public static String ACCESS_SECRET;

    public static String CONSUMER_GROUP_ID;

    public static String IOT_INSTANCE_ID;

    public static String CLIENT_ID;

    public static String HOST;

    public static String PRODUCT_KEY;

    public static String DEVICE_NAME;


    @Override
    public void afterPropertiesSet() throws Exception {
        ACCESS_KEY = accessKey;
        ACCESS_SECRET = accessSecret;
        CONSUMER_GROUP_ID = consumerGroupId;
        IOT_INSTANCE_ID = iotInstanceId;
        CLIENT_ID = clientId;
        HOST = host;
        PRODUCT_KEY = productKey;
        DEVICE_NAME = deviceName;
    }
}
