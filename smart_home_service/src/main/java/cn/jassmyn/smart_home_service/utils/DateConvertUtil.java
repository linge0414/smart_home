package cn.jassmyn.smart_home_service.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间转化工具
 */
public class DateConvertUtil {

    /**
     * 根据时间转换为时间戳
     * @param date
     * @param timestampType 转换类型 0毫秒 1秒
     * @return
     */
    public static long getTimeStamp(Date date, int timestampType)
    {
        long times = date.getTime();
        if (timestampType == 1)
        {
            times = times/1000L;
        }
        return  times;
    }

    /**
     * 时间戳转时间
     * @param timestamp
     * @param timestampType 时间戳格式 0毫秒 1秒
     * @return
     */
    public static Date getDateTime(long timestamp,int timestampType)
    {
        if (timestampType == 1)
        {
            //如果时间戳格式是秒，需要江时间戳变为毫秒
            timestamp = timestamp * 1000L;
        }
        Date dateTime = new Date(timestamp);
       return  dateTime;

    }

    /**
     * 格式化传入的时间，将时间转化为指定格式字符串
     * @param date
     * @param format 时间格式，如：yyyy-MM-dd HH:mm:ss SSS 或 yyyy年MM月dd日 HH:mm:ss
     * @return
     */
    public static String getDateTimeString(Date date,String format )
    {
        if (format == null || format.length() <=0)
        {
            return  null;
        }
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String timeString = sdf.format(date);
        return  timeString;
    }

    /**
     * 格式化传入的时间戳，将时间戳转化为指定格式字符串
     * @param timestamp
     * @param format 时间格式，如：yyyy-MM-dd HH:mm:ss SSS 或 yyyy年MM月dd日 HH:mm:ss     *
     * @param timestampType 时间戳格式 0毫秒 1秒
     * @return
     */
    public static String getTimeStampString(long timestamp,String format ,int timestampType)
    {
        if (format == null || format.length() <=0)
        {
            return  null;
        }
        if (timestampType == 1)
        {
            //如果时间戳格式是秒，需要江时间戳变为毫秒
            timestamp = timestamp * 1000L;
        }
        Date dateTime = new Date(timestamp);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String timeString = sdf.format(dateTime);
        return  timeString;
    }

}
