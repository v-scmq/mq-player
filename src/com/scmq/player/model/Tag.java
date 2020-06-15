package com.scmq.player.model;

/**
 * 标签信息
 *
 * @author SCMQ
 */
public class Tag {
    /** 标签名称 */
    private String name;
    /** 标签id */
    private String id;

    /** 构造一个默认的Tag对象 */
    public Tag() {
    }

    /**
     * 通过标签名和标签id,来构造一个Tag对象
     *
     * @param name 标签名
     * @param id   标签id
     */
    public Tag(String name, String id) {
        this.name = name;
        this.id = id;
    }

    /**
     * 获取标签名
     *
     * @return 标签名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置标签名
     *
     * @param name 标签名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取标签id
     *
     * @return 标签id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置标签id
     *
     * @param id 标签id
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Tag [name=" + name + ", id=" + id + "]";
    }
}
