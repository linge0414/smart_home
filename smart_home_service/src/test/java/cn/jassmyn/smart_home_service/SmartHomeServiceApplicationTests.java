package cn.jassmyn.smart_home_service;

import cn.jassmyn.smart_home_service.service.IotService;
import cn.jassmyn.smart_home_service.utils.DateConvertUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class SmartHomeServiceApplicationTests {

    private final static Logger logger = LoggerFactory.getLogger(SmartHomeServiceApplicationTests.class);

    @Autowired
    private IotService iotService;

    @Test
    void contextLoads() {
    }

    @Test
    void testGetAllProperties() {
        iotService.updateAllProperties();
    }

    @Test
    void testGetDeviceProperities() {
        Date date = new Date();
        long endTime = DateConvertUtil.getTimeStamp(date, 0);
        iotService.getDeviceProperities(1684551289000l, 1685000992039l);
    }

}
