package cn.jassmyn.smart_home_service.service;

import cn.jassmyn.smart_home_service.po.Property;
import com.aliyun.iot20180120.models.QueryDevicePropertiesDataResponseBody;

import java.util.List;

/**
 * @Description: 物联网服务类，用于与物联网平台交互，获取设备属性，设置设备属性等操作
 * @author: zhangYuLin
 * @date: 2023/5/20 16:07
 */
public interface IotService {

    /**
     * 更新设备属性
     */
    void updateAllProperties();

    /**
     * 设置属性
     * @param Items 属性
     */
    void setProperty(String Items);

    /**
     * 设置灯的属性
     * @param property 属性
     */
    void setLightProperty(String property);

    /**
     * 设置空调的属性
     * @param property
     */
    void setVehACProperty(String property);

    /**
     * 设置加湿器的属性
     * @param property
     */
    void setHumidifiedProperty(String property);

    /**
     * 获取设备属性
     */
    List<QueryDevicePropertiesDataResponseBody.QueryDevicePropertiesDataResponseBodyPropertyDataInfosPropertyDataInfo> getDeviceProperities(Long startTime, Long endTime);

}
