package cn.jassmyn.smart_home_service.controller;

import cn.jassmyn.smart_home_service.mapper.HistoricalDataMapper;
import cn.jassmyn.smart_home_service.po.AttributeThreshold;
import cn.jassmyn.smart_home_service.po.HistoricalData;
import cn.jassmyn.smart_home_service.po.Property;
import cn.jassmyn.smart_home_service.service.IotService;
import com.aliyun.iot20180120.models.QueryDevicePropertiesDataResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Description: TODO
 * @author: zhangYuLin
 * @date: 2023/5/25 9:14
 */
@RestController
@RequestMapping("/iot")
@CrossOrigin
public class IotController {


    @Autowired
    private IotService iotService;

    @Autowired
    private HistoricalDataMapper historicalDataMapper;

    @GetMapping("/setHumidifiedProperty/{property}")
    public Map<String, String> setHumidifiedProperty(@PathVariable String property) {
        Property.propertyMap.put("Humidified", property);
        iotService.setHumidifiedProperty(property);
        return Property.propertyMap;
    }

    @GetMapping("/setVehACProperty/{property}")
    public Map<String, String> setVehACProperty(@PathVariable String property) {
        Property.propertyMap.put("VehACSwitch", property);
        iotService.setVehACProperty(property);
        return Property.propertyMap;
    }

    @GetMapping("/setLightProperty/{property}")
    public Map<String, String> setLightProperty(@PathVariable String property) {
        Property.propertyMap.put("LightSwitch", property);
        iotService.setLightProperty(property);
        return Property.propertyMap;
    }

    @GetMapping("/updateAllProperties")
    public Map<String, String> updateAllProperties() {
        return Property.propertyMap;
    }

    @GetMapping("/getAllProperties")
    public Map<String, String> getAllProperties() {
        iotService.updateAllProperties();
        return Property.propertyMap;
    }

    @GetMapping("/getDeviceProperities/{startTime}/{endTime}")
    public List<QueryDevicePropertiesDataResponseBody.QueryDevicePropertiesDataResponseBodyPropertyDataInfosPropertyDataInfo> getDeviceProperities(@PathVariable Long startTime, @PathVariable Long endTime) {
        List<QueryDevicePropertiesDataResponseBody.QueryDevicePropertiesDataResponseBodyPropertyDataInfosPropertyDataInfo> deviceProperitiesList = iotService.getDeviceProperities(startTime, endTime);
        return deviceProperitiesList;
    }

    @PostMapping("/setAutoTemperature")
    public Map<String,Integer> setAutoTemperature(@RequestParam Map<String,String> map){
        AttributeThreshold.attributeThresholdMap.put("autoTemperature", Integer.valueOf(map.get("autoTemperature")));
        AttributeThreshold.attributeThresholdMap.put("minTemperature", Integer.valueOf(map.get("minTemperature")));
        AttributeThreshold.attributeThresholdMap.put("maxTemperature", Integer.valueOf(map.get("maxTemperature")));
        return AttributeThreshold.attributeThresholdMap;
    }

    @PostMapping("/setAutoHumidity")
    public Map<String,Integer> setAutoHumidity(@RequestParam Map<String,String> map){
        AttributeThreshold.attributeThresholdMap.put("autoHumidity", Integer.valueOf(map.get("autoHumidity")));
        AttributeThreshold.attributeThresholdMap.put("openHumidity", Integer.valueOf(map.get("openHumidity")));
        AttributeThreshold.attributeThresholdMap.put("closeHumidity", Integer.valueOf(map.get("closeHumidity")));
        return AttributeThreshold.attributeThresholdMap;
    }

    @PostMapping("/setAutoBrightness")
    public Map<String,Integer> setAutoBrightness(@RequestParam Map<String,String> map){
        AttributeThreshold.attributeThresholdMap.put("autoBrightness", Integer.valueOf(map.get("autoBrightness")));
        AttributeThreshold.attributeThresholdMap.put("openBrightness", Integer.valueOf(map.get("openBrightness")));
        AttributeThreshold.attributeThresholdMap.put("closeBrightness", Integer.valueOf(map.get("closeBrightness")));
        return AttributeThreshold.attributeThresholdMap;
    }

    @GetMapping("/getAttributeThreshold")
    public Map<String,Integer> getAttributeThreshold(){
        return AttributeThreshold.attributeThresholdMap;
    }

    @GetMapping("/getHistoricalData")
    public List<HistoricalData> getHistoricalData(){
        return historicalDataMapper.selectList(null);
    }

}
