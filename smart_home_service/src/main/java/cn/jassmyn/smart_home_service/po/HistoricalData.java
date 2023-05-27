package cn.jassmyn.smart_home_service.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @Description: TODO
 * @author: zhangYuLin
 * @date: 2023/5/25 10:27
 */
@Data
public class HistoricalData {

    @TableId
    private String id;

    // 加湿开关
    private Integer humidified;

    // 温度
    private Integer temperature;

    // 湿度
    private Integer humidity;

    // 环境亮度
    private Integer ambientBrightness;

    // 空调开关
    private Integer vehACSwitch;

    // 主灯亮度
    private Integer lightLuminance;

    // 主灯开关
    private Integer lightSwitch;

    // 时间
    private Date time;

}
