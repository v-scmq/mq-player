package com.scmq.player.util;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * 时间工具类
 *
 * @author SCMQ
 */
public class TimeUtil {
    /**
     * 将毫秒时间值转换为 00:00 或 00:00:00的时间格式字符串
     *
     * @param millis 毫秒时间值
     * @return 毫秒表示的时间字符串
     */
    public static String millisToTime(long millis) {
        // 先将总毫秒数转换为总秒数,然后交给总秒数转换为标准时间值的方法处理
        return secondToTime(millis / 1000);
    }

    /**
     * 将一个秒数时间值转换为 00:00 或 00:00:00的时间格式字符串
     *
     * @param second 总秒数
     * @return 秒数表示的标准时间字符串
     */
    public static String secondToTime(long second) {
        StringBuilder builder = new StringBuilder();
        long number;
        // 获取总秒数包含的小时数
        if (second >= 3600) {
            number = second / 3600;
            if (number < 10) {
                builder.append('0');
            }
            builder.append(number).append(':');
            // 计算剩余的总秒数
            second %= 3600;
        }
        // 计算包含的分钟数;如果小于60,则分钟数为0
        number = second < 60 ? 0 : second / 60;
        // 如果数字小于10,补0
        if (number < 10) {
            builder.append('0');
        }
        builder.append(number).append(':');
        // 计算秒数;如果小于60,则秒数为time,否则为 time % 60
        number = second < 60 ? second : second % 60;
        if (number < 10) {
            builder.append('0');
        }
        return builder.append(number).toString();
    }

    /**
     * 将一个标准的时间字符串转换为 毫秒时间值.<br>
     * 这个时间字符串必须是匹配正则表达式 {@code (\\d+:)*\\d+(\\.\\d+)? }<br>
     * 要使得该方法是安全的,在调用之前必须使用正则表达式验证.例如：
     *
     * <pre>
     * String time = "00:03:58.80";
     * Pattern pattern = Pattern.compile("(\\d+:)*\\d+(\\.\\d+)?");
     * Matcher matcher = pattern.matcher(time);
     * if (matcher.matches()) {
     * 	long millis = TimeUtil.toMillis(time);
     * 	System.out.println(millis);
     * }
     * </pre>
     *
     * @param time 标准的时间字符串
     * @return 这个时间字符串的毫秒值
     */
    public static long toMillis(String time) {
        long millis = 0;
        // 先检查字符串是否包含毫秒值(毫秒值在“.”之后)
        int index = time.lastIndexOf('.');
        if (index != -1) {
            // 获取毫秒值
            millis = Integer.parseInt(time.substring(index + 1));
            // 如果这个值小于100,还需要乘以10,才是毫秒值
            if (millis < 100) {
                millis *= 10;
            }
            // 截取前面的时间字符串,因为前面的时间分割符都是“:”
            time = time.substring(0, index);
        }
        // 转换为毫秒值的倍数
        int convert = 1000;
        // 截取字符串的结束位置(开区间)
        int end = time.length();
        // 从字符串尾部,向前扫描
        for (index = end - 1; index >= 0; index--) {
            // 如果是“:”时间分割符
            if (time.charAt(index) == ':') {
                // 毫秒值 = 原毫秒值 + 新的时间值 * 转换倍数 (叠加过程)
                millis += Integer.parseInt(time.substring(index + 1, end)) * convert;
                // 转换倍数 = 原转换倍数 * 60 (叠加过程)
                convert *= 60;
                // 截取字符串结束位置=当前位置
                end = index;
            }
        }
        // 处理最前面的一个时间值(因为上面的循环中只处理到“00:”后的“:”)
        // 毫秒值 = 原毫秒值 + 新的时间值 * 转换倍数 (叠加过程)
        return millis + Integer.parseInt(time.substring(0, end)) * convert;
    }

    /**
     * 将时间字符串转换为毫秒表示的时间值.已被{@link TimeUtil#toMillis(String time)}代替
     *
     * @param time 时间字符串(形如 00:00.00或00:00:00.00)
     * @return 毫秒表示的时间值
     */
    @Deprecated
    public static long toTimeMillis(String time) {
        long millis = 0;
        String regex = "^(\\d{2}:)?(\\d{2}:\\d{2})(\\.\\d{2})?";
        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(time).matches()) {
            return millis;
        }
        int increment = 60;
        pattern = Pattern.compile(":");
        String[] times = pattern.split(time);
        for (int index = times.length - 2; index >= 0; index--) {
            millis += (Integer.parseInt(times[index]) * increment);
            increment *= 60;
        }
        time = times[times.length - 1];
        pattern = Pattern.compile("\\.");
        times = pattern.split(time);
        if (times != null && times.length == 2) {
            millis += Integer.parseInt(times[0]);
            millis *= 1000;
            millis += Integer.parseInt(times[1]) * 10;
        } else {
            millis += Integer.parseInt(time);
            millis *= 1000;
        }
        return millis;
    }

    /**
     * 将时间字符串转换为毫秒表示的时间值.已被{@link TimeUtil#toMillis(String time)}代替
     *
     * @param pattern 时间值分割正则表达式(Pattern)对象
     * @param time    时间字符串(形如 00:00.00或00:00:00.00)
     * @return 毫秒表示的时间值
     */
    @Deprecated
    public static long toTimeMillis(Pattern pattern, String time) {
        String[] times = pattern.split(time);
        long millis = Integer.parseInt(times[0]) * 60;
        millis += Integer.parseInt(times[1]);
        millis *= 1000;
        if (times.length >= 3) {
            millis += Integer.parseInt(times[2]) * 10;
        }
        return millis;
    }

    /**
     * 获取当前已被格式化时间字符串
     *
     * @return 格式为 {@code 年-月-日 时:分:秒}的时间字符串
     */
    public static String currentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
    }

    //    /**
    //     * 将毫秒时间值转换为 00:00 或 00:00:00的时间格式字符串
    //     *
    //     * @param millis 毫秒时间值
    //     * @return 毫秒表示的时间字符串
    //     */
    //    public static String parseTime(long millis) {
    //        millis /= 1000;
    //        int hours = (int) millis / 3600;
    //        int remainder = (int) millis - hours * 3600;
    //        int minutes = remainder / 60;
    //        remainder = remainder - minutes * 60;
    //        int seconds = remainder;
    //        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    //    }
}
