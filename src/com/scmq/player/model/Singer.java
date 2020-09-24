package com.scmq.player.model;

import java.util.Objects;

/**
 * 歌手信息
 *
 * @author SCMQ
 */
public class Singer {
	/** 本地存储歌手id */
	private Integer id;
	/** 歌手名称 */
	private String name;
	/** 音乐平台歌手mid */
	private String mid;
	/** 歌手封面图片 URI */
	private String cover;
	/** 歌手介绍 */
	private String introduce;
	/** 歌手包含的歌曲数量 */
	private Integer songCount;
	/** 歌手包含的专辑数量 */
	private Integer albumCount;
	/** 歌手包含的MV数量 */
	private Integer mvCount;
	/** 歌手关注(粉丝)量 */
	private String fansCount;
	/** 歌手所属音乐平台(如“酷狗音乐”) */
	private String platform;

    public Singer() {
    }

    public Singer(String name) {
        setName(name);
    }

    public Singer(String name, String mid, String cover) {
        setName(name);
        setMid(mid);
        setCover(cover);
    }

    /**
     * 获取歌手id
     *
     * @return 歌手id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置歌手id
     *
     * @param id 歌手id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取歌手名
     *
     * @return 歌手名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置歌手名
     *
     * @param name 歌手名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取歌手id
     *
     * @return 歌手id
     */
    public String getMid() {
        return mid;
    }

    /**
     * 设置歌手id
     *
     * @param mid 歌手id
     */
    public void setMid(String mid) {
        this.mid = mid;
    }

    /***
     * 获取歌手封面图片地址
     *
     * @return 歌手封面图片地址
     */
    public String getCover() {
        return cover;
    }

    /**
     * 设置歌手封面图片地址
     *
     * @param cover 歌手封面图片地址
     */
    public void setCover(String cover) {
        this.cover = cover;
    }

    /**
     * 获取歌手介绍信息
     *
     * @return 歌手介绍
     */
    public String getIntroduce() {
        return introduce;
    }

    /**
     * 设置歌手介绍信息
     *
     * @param introduce 歌手介绍
     */
    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    /**
     * 获取歌手的歌曲数量
     *
     * @return 歌曲数量
     */
    public Integer getSongCount() {
        return songCount;
    }

    /**
     * 设置歌手的歌曲数量
     *
     * @param songCount 歌曲数量
     */
    public void setSongCount(Integer songCount) {
        this.songCount = songCount;
    }

    /**
     * 获取专辑数量
     *
     * @return 专辑数量
     */
    public Integer getAlbumCount() {
        return albumCount;
    }

    /**
     * 设置专辑数量
     *
     * @param albumCount 专辑数量
     */
    public void setAlbumCount(Integer albumCount) {
        this.albumCount = albumCount;
    }

    /**
     * 获取MV数量
     *
     * @return MV数量
     */
    public Integer getMvCount() {
        return mvCount;
    }

    /**
     * 设置MV数量
     *
     * @param mvCount MV数量
     */
    public void setMvCount(Integer mvCount) {
        this.mvCount = mvCount;
    }

    /**
     * 获取歌手关注量
     *
     * @return 关注量/粉丝数量
     */
    public String getFansCount() {
        return fansCount;
    }

    /**
     * 设置歌手关注量
     *
     * @param fansCount 关注(粉丝 )量
     */
    public void setFansCount(String fansCount) {
        this.fansCount = fansCount;
    }

    /**
     * 获取歌手所属音乐平台(如“酷狗音乐”)
     *
     * @return 歌手所属音乐平台(如 “ 酷狗音乐 ”)
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * 设置歌手所属音乐平台(如“酷狗音乐”)
     *
     * @param platform 音乐平台id
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }


	@Override
	public String toString() {
		return "Singer{" + "id=" + id + ", name='" + name + '\'' + ", mid='" + mid + '\'' + ", cover='" + cover + '\''
				+ ", introduce='" + introduce + '\'' + ", songCount=" + songCount + ", albumCount=" + albumCount
				+ ", mvCount=" + mvCount + ", fansCount='" + fansCount + '\'' + ", platform='" + platform + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof Singer) {
			Singer other = (Singer) o;
			String platform1 = getPlatform(), platform2 = getPlatform();
			if (!Objects.equals(platform1, platform2)) {
				return false;
			}
			String key = platform1 == null ? getName() : getMid();
			return Objects.equals(key, platform1 == null ? other.getName() : other.getMid());
		}
		return false;
	}

	@Override
	public int hashCode() {
		String key = getPlatform() == null ? getName() : getMid();
		return key == null ? 0 : 31 + key.hashCode();
	}
	
	/**
	 * 检查歌手的歌曲、专辑、MV、粉丝数量和歌手简介等信息是否不存在
	 * 
	 * @return 若信息不存在则返回true
	 */
	public boolean isEmptyInfo() {
		return getSongCount() == null || getAlbumCount() == null || //
				getMvCount() == null || getFansCount() == null || getIntroduce() == null;
	}
}
