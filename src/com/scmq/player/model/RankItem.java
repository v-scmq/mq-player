package com.scmq.player.model;

/**
 * 这个类的一个实例表示一个榜单项。这通常不会用于本地音乐,而是网络乐库.
 *
 * @author SCMQ
 */
public class RankItem {
    /** 榜单项名称 */
    private String name;
    /** 榜单项id */
    private String id;

    /**
     * 通过名称和id来构造一个榜单项(对象)实例
     *
     * @param name 榜单项名称
     * @param id   榜单项id
     */
    public RankItem(String name, String id) {
        this.name = name;
        this.id = id;
    }

    /**
     * 获取榜单项名称
     *
     * @return 绑定项名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置榜单项名称
     *
     * @param name 榜单项名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取榜单id
     *
     * @return 榜单id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置榜单id
     *
     * @param id 榜单id
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RankItem [name=" + name + ", id=" + id + "]";
    }
}
