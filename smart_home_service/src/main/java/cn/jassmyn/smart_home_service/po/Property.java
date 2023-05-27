package cn.jassmyn.smart_home_service.po;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: TODO
 * @author: zhangYuLin
 * @date: 2023/5/20 18:18
 */
@Component
@Data
public class Property {

    public static Map<String, String> propertyMap;

    public Property() {
        propertyMap = new HashMap<>();
        propertyMap.put("Humidified", "0");
        propertyMap.put("Temperature", "0");
        propertyMap.put("Humidity", "0");
        propertyMap.put("AmbientBrightness", "0");
        propertyMap.put("VehACSwitch", "0");
        propertyMap.put("LightLuminance", "0");
        propertyMap.put("LightSwitch", "0");
    }

    // 加湿开关
    private boolean Humidified;

    // 温度
    private Integer Temperature;

    // 湿度
    private Integer Humidity;

    // 环境亮度
    private Integer AmbientBrightness;

    // 空调开关
    private boolean VehACSwitch;

    // 主灯亮度
    private Integer LightLuminance;

    // 主灯开关
    private boolean LightSwitch;

}
