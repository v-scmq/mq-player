package com.scmq.player.model;

/**
 * 音乐专辑信息
 *
 * @author SCMQ
 */
public class Album {
	/** 专辑id */
	private Integer id;
	/** 专辑名称 */
	private String name;
	/** 所属音乐平台的专辑mid */
	private String mid;
	/** 专辑封面图 */
	private String cover;
	/** 所属歌手 */
	private Singer singer;
	/** 专辑介绍 */
	private String introduce;
	/** 发行时间 */
	private String year;
	/** 专辑所包含的歌曲总数 */
	private Integer songCount;
	/** 专辑所属音乐平台 */
	private String platform;

	/**
	 * 构造一个默认的专辑信息对象
	 */
	public Album() {
	}

	/**
	 * 通过mid和专辑名称 构造一个专辑信息对象
	 *
	 * @param mid
	 *            专辑mid
	 * @param name
	 *            专辑名称
	 */
	public Album(String mid, String name) {
		setMid(mid);
		setName(name);
	}

	/**
	 * 通过歌手信息和专辑名称来构造一个专辑信息对象
	 *
	 * @param singer
	 *            歌手信息对象
	 * @param name
	 *            专辑名称
	 */
	public Album(Singer singer, String name) {
		setSinger(singer);
		setName(name);
	}

	/**
	 * 通过专辑mid和专辑名称和专辑封面图片的URI 来构造一个专辑信息对象
	 *
	 * @param mid
	 *            专辑mid
	 * @param name
	 *            专辑名称
	 * @param cover
	 *            专辑封面图片的URI
	 */
	public Album(String mid, String name, String cover) {
		setMid(mid);
		setName(name);
		setCover(cover);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String id) {
		this.mid = id;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public Singer getSinger() {
		return singer;
	}

	public void setSinger(Singer singer) {
		this.singer = singer;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Integer getSongCount() {
		return songCount;
	}

	public void setSongCount(Integer songCount) {
		this.songCount = songCount;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	@Override
	public String toString() {
		return "Album [name=" + name + ", mid=" + mid + ", cover=" + cover + ", singerName=" + singer + ", introduce="
				+ introduce + ", year=" + year + ", songNum=" + songCount + "]";
	}
}
