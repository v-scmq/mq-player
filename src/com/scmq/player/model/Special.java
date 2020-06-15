package com.scmq.player.model;

import java.util.List;

/**
 * 歌单信息
 *
 * @author SCMQ
 */
public class Special {
    private Integer id;
    /** 歌单名称 */
    private String name;
    /** 歌单id */
    private String mid;
    /** 歌单封面图片 URI */
    private String cover;
    /** 歌单介绍 */
    private String introduce;
    /** 歌单包含的歌曲数量 */
    private Integer songNum;

    /** 创建人 */
    private String creater;
    /** 所属平台 */
    private String platform;
    /** 用户id */
    String userId;

    List<Music> songList;

    public Special() {
    }

    public Special(String name, String mid, String cover) {
        setName(name);
        setMid(mid);
        setCover(cover);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取歌单名
     *
     * @return 歌单名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置歌单名
     *
     * @param name 歌单名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取歌单mid
     *
     * @return 歌单mid
     */
    public String getMid() {
        return mid;
    }

    /**
     * 设置歌单mid
     *
     * @param mid 歌单mid
     */
    public void setMid(String mid) {
        this.mid = mid;
    }

    /***
     * 获取歌单封面图片地址
     *
     * @return 歌单封面图片地址
     */
    public String getCover() {
        return cover;
    }

    /**
     * 设置歌单封面图片地址
     *
     * @param cover 歌单封面图片地址
     */
    public void setCover(String cover) {
        this.cover = cover;
    }

    /**
     * 获取歌单介绍信息
     *
     * @return 歌单介绍
     */
    public String getIntroduce() {
        return introduce;
    }

    /**
     * 设置歌单介绍信息
     *
     * @param introduce 歌单介绍
     */
    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    /**
     * 获取歌单的歌曲数量
     *
     * @return 歌曲数量
     */
    public Integer getSongNum() {
        return songNum;
    }

    /**
     * 设置歌单的歌曲数量
     *
     * @param songNum 歌曲数量
     */
    public void setSongNum(Integer songNum) {
        this.songNum = songNum;
    }

    /**
     * 设置歌单创建人
     *
     * @param creater 创建人名称
     */
    public void setCreater(String creater) {
        this.creater = creater;
    }

    /**
     * 获取歌单创建人名称
     *
     * @return 创建人名称
     */
    public String getCreater() {
        return creater;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "Special [name=" + name + ", id=" + id + ", cover=" + cover + ", introduce=" + introduce + ", songNum=" + songNum + ", creater=" + creater + "]";
    }
}
