package cn.jassmyn.smart_home_service.service.impl;

import cn.jassmyn.smart_home_service.client.IotClient;
import cn.jassmyn.smart_home_service.po.Property;
import cn.jassmyn.smart_home_service.service.IotService;
import cn.jassmyn.smart_home_service.ConstantPropertiesUtils;
import com.aliyun.iot20180120.Client;
import com.aliyun.iot20180120.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @author: zhangYuLin
 * @date: 2023/5/20 16:08
 */
@Service
public class IotServiceImpl implements IotService {

    private final static Logger logger = LoggerFactory.getLogger(IotServiceImpl.class);


    Client client = IotClient.createClient();

    public IotServiceImpl() throws Exception {

    }


    @Override
    public void updateAllProperties() {

        QueryDevicePropertyStatusRequest request = new QueryDevicePropertyStatusRequest()
                .setIotInstanceId(ConstantPropertiesUtils.IOT_INSTANCE_ID)
                .setProductKey(ConstantPropertiesUtils.PRODUCT_KEY)
                .setDeviceName(ConstantPropertiesUtils.DEVICE_NAME);

        try {
            QueryDevicePropertyStatusResponse response = client.queryDevicePropertyStatus(request);
            List<QueryDevicePropertyStatusResponseBody.QueryDevicePropertyStatusResponseBodyDataListPropertyStatusInfo> propertyStatusInfoList = response.getBody().getData().getList().getPropertyStatusInfo();
            for (QueryDevicePropertyStatusResponseBody.QueryDevicePropertyStatusResponseBodyDataListPropertyStatusInfo propertyStatusInfo : propertyStatusInfoList) {
                String identifier = propertyStatusInfo.getIdentifier();
                String value = propertyStatusInfo.getValue();
                Property.propertyMap.put(identifier, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setProperty(String Items) {
        SetDevicePropertyRequest request = new SetDevicePropertyRequest()
                .setIotInstanceId(ConstantPropertiesUtils.IOT_INSTANCE_ID)
                .setProductKey(ConstantPropertiesUtils.PRODUCT_KEY)
                .setDeviceName(ConstantPropertiesUtils.DEVICE_NAME)
                .setItems(Items);
        try {
            SetDevicePropertyResponse response = client.setDeviceProperty(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setLightProperty(String property) {
        Property.propertyMap.put("LightSwitch", property);
        String items = "{\"LightSwitch\":" + property + "}";
        setProperty(items);
    }

    @Override
    public void setVehACProperty(String property) {
        Property.propertyMap.put("VehACSwitch", property);
        String items = "{\"VehACSwitch\":" + property + "}";
        setProperty(items);
    }

    @Override
    public void setHumidifiedProperty(String property) {
        Property.propertyMap.put("Humidified", property);
        String items = "{\"Humidified\":" + property + "}";
        setProperty(items);
    }

    @Override
    public List<QueryDevicePropertiesDataResponseBody.QueryDevicePropertiesDataResponseBodyPropertyDataInfosPropertyDataInfo> getDeviceProperities(Long startTime, Long endTime) {
        QueryDevicePropertiesDataRequest request = new QueryDevicePropertiesDataRequest()
                .setIotInstanceId(ConstantPropertiesUtils.IOT_INSTANCE_ID)
                .setProductKey(ConstantPropertiesUtils.PRODUCT_KEY)
                .setDeviceName(ConstantPropertiesUtils.DEVICE_NAME)
                .setIdentifier(new ArrayList<>(Property.propertyMap.keySet()))
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setPageSize(50)
                .setAsc(1);

        try {
            QueryDevicePropertiesDataResponse response = client.queryDevicePropertiesData(request);
            List<QueryDevicePropertiesDataResponseBody.QueryDevicePropertiesDataResponseBodyPropertyDataInfosPropertyDataInfo> propertyDataInfoList = response.getBody().getPropertyDataInfos().getPropertyDataInfo();
            return propertyDataInfoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
