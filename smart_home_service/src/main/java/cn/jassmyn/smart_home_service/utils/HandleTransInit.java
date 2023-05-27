package cn.jassmyn.smart_home_service.utils;

import cn.jassmyn.smart_home_service.mapper.HistoricalDataMapper;
import cn.jassmyn.smart_home_service.po.AttributeThreshold;
import cn.jassmyn.smart_home_service.po.Property;
import cn.jassmyn.smart_home_service.service.IotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;


import javax.annotation.PostConstruct;

import static java.lang.Thread.sleep;

@Configuration
public class HandleTransInit implements ApplicationRunner {




    private final static Logger logger = LoggerFactory.getLogger(HandleTransInit.class);

    @Autowired
    private IotService iotService;

    @Override
    @Async
    public void run(ApplicationArguments args) throws InterruptedException {
        while (true) {
            // 如果设置了自动打开空调
            if (AttributeThreshold.attributeThresholdMap.get("autoTemperature") == 1) {
                logger.info("设置了自动打开空调，当前温度为：{}", Property.propertyMap.get("Temperature"));
                if (Integer.parseInt(Property.propertyMap.get("Temperature")) < AttributeThreshold.attributeThresholdMap.get("minTemperature") || Integer.parseInt(Property.propertyMap.get("Temperature")) > AttributeThreshold.attributeThresholdMap.get("maxTemperature")) {
                    //打开空调
                    logger.info("打开空调");
                    iotService.setVehACProperty("1");
                }else{
                    //关闭空调
                    logger.info("关闭空调");
                    iotService.setVehACProperty("0");
                }
            }
            // 如果设置了自动打开加湿器
            if (AttributeThreshold.attributeThresholdMap.get("autoHumidity") == 1) {
                logger.info("设置了自动打开加湿器，当前湿度为：{}", Property.propertyMap.get("Humidity") + "%");
                if (Integer.parseInt(Property.propertyMap.get("Humidity")) < AttributeThreshold.attributeThresholdMap.get("openHumidity")) {
                    //打开加湿器
                    logger.info("打开加湿器");
                    iotService.setHumidifiedProperty("1");
                }else if(Integer.parseInt(Property.propertyMap.get("Humidity")) > AttributeThreshold.attributeThresholdMap.get("closeHumidity")) {
                    //关闭加湿器
                    logger.info("关闭加湿器");
                    iotService.setHumidifiedProperty("0");
                }
            }

            // 如果设置了自动打开灯
            if (AttributeThreshold.attributeThresholdMap.get("autoBrightness") == 1) {
                logger.info("设置了自动打开灯，当前亮度为：{}", Property.propertyMap.get("AmbientBrightness") + "%");
                if (Integer.parseInt(Property.propertyMap.get("AmbientBrightness")) < AttributeThreshold.attributeThresholdMap.get("openBrightness")) {
                    //打开灯
                    logger.info("打开灯");
                    iotService.setLightProperty("1");
                }else if (Integer.parseInt(Property.propertyMap.get("AmbientBrightness")) > AttributeThreshold.attributeThresholdMap.get("closeBrightness")) {
                    //关闭灯
                    logger.info("关闭灯");
                    iotService.setLightProperty("0");
                }
            }
            sleep(5000);
        }
    }
}
