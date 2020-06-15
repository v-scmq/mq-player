package com.scmq.player.model;

import com.scmq.player.util.TimeUtil;

/**
 * LyricLine类的实例即表示一行歌词数据<br>
 * 该类实现类Comparable接口,用于按照歌词内容时间先后进行排序.<br>
 * 同时该类重写了toString()方法,用于返回这一行歌词内容(在歌词模块ListView单元格中得以体现)
 *
 * @author SCMQ
 * @since 2019/02/15 22:15
 */
public class LyricLine implements Comparable<LyricLine> {
    /** 当前这一行歌词的时间的毫秒表示 */
    private long millis;

    /** 当前这一行歌词的内容 */
    private String content;

    public LyricLine() {
    }

    public LyricLine(long millis, String content) {
        setMillis(millis);
        setContent(content);
    }

    public LyricLine(String time, String content) {
        setMillis(TimeUtil.toMillis(time));
        setContent(content);
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 按照歌词时间大小升序排序的比较器
     */
    @Override
    public int compareTo(LyricLine line) {
        return line == null || millis < line.millis ? -1 : millis == line.millis ? 0 : 1;
    }

    @Override
    public String toString() {
        return "LyricLine [millis=" + millis + ", content=" + content + "]";
    }
}
