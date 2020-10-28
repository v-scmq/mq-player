package com.scmq.player.model;

import com.scmq.player.util.FileUtil;
import com.scmq.player.util.StringUtil;
import javafx.scene.image.Image;

import java.io.File;
import java.util.Objects;

/**
 * 视频(媒体)文件的抽象表示. 这个类应该继承Video,但是几乎不涉及这个类,于是这个Video类省略. MV与{@link Music}类都具有歌手和歌曲标题属性.它不是{@link Music}的子类,但它们都是Media的子类
 *
 * @author SCMQ
 * @see Music
 * @see Music#getSinger()
 * @see Music#getTitle()
 */
public class MV extends Media {
	/** MV id */
	private String vid;

	private String sdPath;
	private String hdPath;
	private String sqPath;
	private String rqPath;

	/** MV封面图片的URI */
	private String cover;

	/** 缓存这个hash值,避免每次计算 */
	private int hash;

	/** 构造一个默认的视频文件对象 */
	public MV() {
	}

	/**
	 * 通过本地文件构造视频文件对象
	 *
	 * @param file
	 *            本地文件
	 */
	public MV(File file) {
		super(file);
	}

	/**
	 * 通过一个URI字符串构造一个视频文件对象
	 *
	 * @param uri
	 *            视频文件URI
	 */
	public MV(String uri) {
		super(uri);
	}

	public MV(Singer singer, String platformId) {
		setSinger(singer);
		setPlatform(platformId);
	}

	@Override
	public boolean viewable() {
		return true;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getSdPath() {
		return sdPath;
	}

	public void setSdPath(String sdPath) {
		this.sdPath = sdPath;
	}

	public String getHdPath() {
		return hdPath;
	}

	public void setHdPath(String hdPath) {
		this.hdPath = hdPath;
	}

	public String getSqPath() {
		return sqPath;
	}

	public void setSqPath(String sqPath) {
		this.sqPath = sqPath;
	}

	public String getRqPath() {
		return rqPath;
	}

	public void setRqPath(String rqPath) {
		this.rqPath = rqPath;
	}

	/**
	 * 获取媒体封面图片所在URI. 如果是一个音频,通常是专辑图片;如果是一个视频,通常是视频预览图
	 *
	 * @return 封面图片的URI地址
	 */
	public String getCover() {
		return cover;
	}

	/**
	 * 设置媒体封面图片所在URI. 如果是一个音频,通常是专辑图片;如果是一个视频,通常是视频预览图
	 *
	 * @param cover
	 *            封面图片的URI地址
	 */
	public void setCover(String cover) {
		this.cover = cover;
	}

	/**
	 * 获取播放MV时需要显示的歌手图片
	 *
	 * @return 歌手图片
	 */
	@Override
	public Image getImageCover() {
		Singer singer = getSinger();
		// 若存在歌手信息,使用歌手作为封面显示
		if (singer != null) {
			// 获取歌手信息来源平台
			String platform = singer.getPlatform() == null ? "0" : singer.getPlatform();
			// 歌手图片在本地文件系统的文件名称(本地歌手信息使用歌手名称,否则使用歌手mid)
			String name = singer.getPlatform() == null ? singer.getName() : singer.getMid();
			File file = FileUtil.toFile(name, "jpg", "picture/singer", platform);
			if (file.isFile()) {
				return new Image(file.toURI().toString());
			} else if (!StringUtil.isEmpty(singer.getCover())) {
				return new Image(singer.getCover());
			}
		}
		// 若有MV封面图片,则使用MV封面图显示
		return StringUtil.isEmpty(getCover()) ? null : new Image(getCover());
	}

	/**
	 * 媒体对象的hash值由路径(path)决定,不同的path对应不同的媒体;<br>
	 * 但是本地文件的路径需要忽略大小写,必须在初始化path成员变量时,统一将path转换为小写
	 *
	 * @return 返回这个媒体对象的hash值
	 */
	@Override
	public int hashCode() {
		int hash = this.hash;
		if (hash != 0) {
			return hash;
		}
		// 若是本地媒体,则有本地路径(统一小写)生成hash,否则由mid信息生成hash
		String key = getPlatform() == null ? getPath() : getVid();
		return this.hash = key == null ? 0 : key.hashCode();
	}

	/**
	 * 2个媒体对象如果相同,那么它的路径一定是一样的(本地文件忽略大小写,已统一转为小写)
	 *
	 * @see Media#init(File file)
	 * @param obj
	 *            要与之比较的引用对象
	 * @return 若这个对象和参数引用对象相同,则返回true;否则返回false.
	 */
	@Override
	public boolean equals(Object obj) {
		// 若是同一对象,则返回true
		if (this == obj) {
			return true;
		}
		// 若类型不一致,则返回false
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		MV mv = (MV) obj;
		String platform = getPlatform(), platform2 = mv.getPlatform();
		// 若不是同一平台,则返回false
		if (!Objects.equals(platform, platform2)) {
			return false;
		}
		// 若是本地媒体,则比较路径(同一小写)是否相同;否则比较mid是否相同
		String key = platform == null ? getPath() : getVid();
		return Objects.equals(key, platform2 == null ? mv.getPath() : mv.getVid());
	}

	@Override
	public String toString() {
		return "MV[" + "vid='" + vid + '\'' + ", sdPath='" + sdPath + '\'' + ", hdPath='" + hdPath + '\'' + ", sqPath='"
				+ sqPath + '\'' + ", rqPath='" + rqPath + '\'' + ", cover='" + cover + '\'' + ']';
	}
}
