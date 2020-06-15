package com.scmq.player.model;

/**
 * 用户实体类
 *
 * @author SCMQ
 */
public class User {
    private String qq;
    private String name;
    private String headURI;

    public User() {
    }

    public User(String qq) {
        this.qq = qq;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadURI() {
        return headURI;
    }

    public void setHeadURI(String headURI) {
        this.headURI = headURI;
    }

    @Override
    public String toString() {
        return "User [qq=" + qq + ", name=" + name + ", headURI=" + headURI + "]";
    }
}
