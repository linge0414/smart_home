package cn.jassmyn.smart_home_service.client;

import cn.jassmyn.smart_home_service.ConstantPropertiesUtils;
import com.aliyun.iot20180120.Client;
import com.aliyun.teaopenapi.models.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description: TODO
 * @author: zhangYuLin
 * @date: 2023/5/20 16:00
 */
@Component
public class IotClient {

    private final static Logger logger = LoggerFactory.getLogger(IotClient.class);

    private static String accessKey = ConstantPropertiesUtils.ACCESS_KEY;

    private static String accessKeySecret = ConstantPropertiesUtils.ACCESS_SECRET;

    /**
     * 使用AK&SK初始化Client。
     */
    public static Client createClient() throws Exception {
        Config config = new Config();
        config.accessKeyId = accessKey;
        config.accessKeySecret = accessKeySecret;
        // 您的可用区ID。
        config.regionId = "cn-shanghai";
        return new Client(config);
    }





}
