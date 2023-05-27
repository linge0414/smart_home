package cn.jassmyn.smart_home_service.po;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 属性阈值类，用于存储属性阈值，以及对属性阈值的操作
 * @author: zhangYuLin
 * @date: 2023/5/25 23:14
 */
@Data
@Component
public class AttributeThreshold {

    public static Map<String, Integer> attributeThresholdMap;
    private  Integer autoTemperature = 0;
    private Integer autoHumidity = 0;
    private Integer autoBrightness = 0;

    private Integer minTemperature = 0;
    private Integer maxTemperature = 0;
    private Integer openHumidity = 0;
    private Integer closeHumidity = 0;
    private Integer openBrightness = 0;
    private Integer closeBrightness = 0;

    public AttributeThreshold() {
        attributeThresholdMap = new HashMap<>();
        attributeThresholdMap.put("autoTemperature", 0);
        attributeThresholdMap.put("autoHumidity", 0);
        attributeThresholdMap.put("autoBrightness", 0);
        attributeThresholdMap.put("minTemperature", 0);
        attributeThresholdMap.put("maxTemperature", 0);
        attributeThresholdMap.put("openHumidity", 0);
        attributeThresholdMap.put("closeHumidity", 0);
        attributeThresholdMap.put("openBrightness", 0);
        attributeThresholdMap.put("closeBrightness", 0);
    }
}
