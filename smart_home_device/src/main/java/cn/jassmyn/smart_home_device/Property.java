package cn.jassmyn.smart_home_device;

import com.aliyun.alink.linksdk.tmp.device.payload.ValueWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: TODO
 * @author: zhangYuLin
 * @date: 2023/5/20 18:18
 */
public class Property {

    public static Map<String, ValueWrapper> propertyMap;

    public Property() {
        propertyMap = new HashMap<>();
        propertyMap.put("Humidified", new ValueWrapper.BooleanValueWrapper(0));
        propertyMap.put("Temperature", new ValueWrapper.IntValueWrapper(0));
        propertyMap.put("Humidity", new ValueWrapper.IntValueWrapper(0));
        propertyMap.put("AmbientBrightness", new ValueWrapper.IntValueWrapper(0));
        propertyMap.put("VehACSwitch", new ValueWrapper.BooleanValueWrapper(0));
        propertyMap.put("LightLuminance", new ValueWrapper.IntValueWrapper(0));
        propertyMap.put("LightSwitch", new ValueWrapper.BooleanValueWrapper(0));
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
