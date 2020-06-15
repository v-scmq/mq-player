package com.scmq.player.model;

import java.util.List;

/**
 * 这个类的一个对象实例表示一个榜单信息数据.榜单包含了许多的榜单项.
 *
 * @see RankItem
 * @author SCMQ
 */
public class Rank {
    /** 榜单名称 */
    private String name;
    /** 榜单项List集合 */
    private List<RankItem> items;

    public Rank(String name) {
        setName(name);
    }

    public Rank(String name, List<RankItem> items) {
        setName(name);
        setItems(items);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RankItem> getItems() {
        return items;
    }

    public void setItems(List<RankItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Rank [name=" + name + ", items=" + items + "]";
    }
}
