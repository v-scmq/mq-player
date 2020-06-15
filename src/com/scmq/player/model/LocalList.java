package com.scmq.player.model;

import java.util.List;

/**
 * 本地音乐列表类,该类每个实例对应数据库表{@code local_list}的每一行记录
 *
 * @author SCMQ
 */
public class LocalList {
    /** 本地音乐列表id */
    private Integer id;

    /** 本地音乐List集合 */
    private List<Music> musics;

    /** 排序方式 */
    private SortMethod sort;

    /** 创建时间 */
    private String createTime;

    public LocalList() {
    }

    public LocalList(SortMethod sort) {
        setSort(sort);
    }

    public LocalList(SortMethod sort, String createTime) {
        setSort(sort);
        setCreateTime(createTime);
    }

    /**
     * 获取本地列表id
     *
     * @return 本地列表id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 获取本地列表id
     *
     * @param id 本地列表id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取本地音乐所有的音乐List集合
     *
     * @return 音乐List集合
     */
    public List<Music> getMusics() {
        return musics;
    }

    /**
     * 获取本地音乐所有的音乐List集合
     *
     * @param musics 音乐List集合
     */
    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }

    /**
     * 获取排序方式
     *
     * @return 排序方式
     */
    public SortMethod getSort() {
        return sort;
    }

    /**
     * 设置排序方式
     *
     * @param sort 排序方式
     */
    public void setSort(SortMethod sort) {
        this.sort = sort;
    }

    /**
     * 设置排序方式
     *
     * @param sort 排序方式
     */
    public void setSort(int sort) {
        for (SortMethod method : SortMethod.values()) {
            if (method.getCode() == sort) {
                setSort(method);
                return;
            }
        }
        // 否则按艺术家排序
        setSort(SortMethod.ARTIST);
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建本地音乐列表的时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "LocalList [id=" + id + ", musics=" + musics + ", sort=" + sort + ", createTime=" + createTime + "]";
    }
}
