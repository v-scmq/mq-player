package com.scmq.player.model;

import com.scmq.player.util.FileUtil;
import com.scmq.player.util.StringUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;

/**
 * 媒体文件的抽象表示.媒体可以是音频,也可以是视频,这是由format(媒体格式)所决定的
 *
 * @author SCMQ
 */
public abstract class Media {
	/** 媒体id */
	private Integer id;

	/** 歌手 */
	private Singer singer;

	/** 歌曲标题 */
	private String title;

	/** 路径,这个路径可以是本地路径,也可以是网络上的一个资源地址 */
	private String path;

	/** 媒体文件名 */
	private String fileName;

	/** 媒体格式 */
	private String format;

	/** 媒体时长 */
	private String duration;

	/** 媒体文件大小 */
	private String size;

	/** 媒体声道 */
	private String channels;

	private int quality;

	/** 媒体的平台来源(如“酷狗音乐”) */
	private String platform;

	/** 构造一个默认的媒体对象 */
	Media() {
	}

	/**
	 * 通过一个本地文件构造一个媒体对象
	 *
	 * @param file
	 *            媒体文件
	 */
	Media(File file) {
		init(file);
	}

	/**
	 * 通过URI字符串构造一个媒体对象
	 *
	 * @param uri
	 *            媒体资源URI地址
	 */
	Media(String uri) {
		// 如果URI是空的将抛出空指针异常
		if (StringUtil.isEmpty(uri)) {
			throw new NullPointerException("媒体的URI地址不能为空");
		}
		// 如果是本地文件
		if (uri.startsWith("file:/")) {
			init(new File(URI.create(uri)));
		} else {
			// 否则直接设置路径
			setPath(uri);
		}
	}

	/**
	 * 通过本地文件构造是媒体对象时的初始化方法
	 */
	void init(File file) {
		// 路径(路径中的字母统一转换为小写)
		setPath(file.getPath().toLowerCase());
		// 文件大小
		setSize(FileUtil.toFileSize(1, file.length()));
		// 文件名(不含音频格式)
		setFileName(FileUtil.getFileTitle(file));
		// 音频格式
		setFormat(FileUtil.getFileFormat(file.getName()));
	}

	/**
	 * 获取媒体id
	 *
	 * @return 媒体id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 设置媒体id
	 *
	 * @param id
	 *            媒体id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 获取歌手信息
	 *
	 * @return 歌手信息
	 */
	public Singer getSinger() {
		return singer;
	}

	/**
	 * 设置歌手信息
	 *
	 * @param singer
	 *            歌手信息
	 */
	public void setSinger(Singer singer) {
		this.singer = singer;
	}

	/**
	 * 获取歌曲标题
	 *
	 * @return 歌曲标题
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置歌曲标题
	 *
	 * @param title
	 *            歌曲名称(标题)
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取媒体路径,这个路径可以是本地路径,也可以是网络上的一个资源地址
	 *
	 * @return 媒体资源路径
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 设置媒体资源路径
	 *
	 * @param path
	 *            资源路径
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 获取媒体文件名,这个文件名不包含媒体格式
	 *
	 * @return 文件名
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置媒体文件名,这个文件名不包含媒体格式
	 *
	 * @param fileName
	 *            文件名
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 获取媒体格式
	 *
	 * @return 媒体格式
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * 设置媒体格式名称
	 *
	 * @param format
	 *            媒体格式
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * 获取媒体时长.这个播放时长已经是格式化为标准的时间.例如03:50、10:02、01:02:05
	 *
	 * @return 媒体播放时长
	 */
	public String getDuration() {
		return duration;
	}

	/**
	 * 设置媒体播放时长.
	 *
	 * @param duration
	 *            这个播放时长已经是格式化为标准的时间.例如03:50、10:02、01:02:05
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	/**
	 * 获取媒体文件大小
	 *
	 * @return 文件大小.例如'5.8M'
	 */
	public String getSize() {
		return size;
	}

	/**
	 * 设置媒体文件大小
	 *
	 * @param size
	 *            文件大小.例如'5.8M'
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * 获取媒体的音质级别
	 *
	 * @return 音质级别
	 */
	public int getQuality() {
		return quality;
	}

	/**
	 * 设置这个媒体的音质级别
	 *
	 * @param quality
	 *            音质级别
	 */
	public void getQuality(int quality) {
		this.quality = quality;
	}

	/**
	 * 获取媒体声道.这通常有左声道、右声道、立体声
	 *
	 * @return 媒体声道
	 */
	public String getChannels() {
		return channels;
	}

	/**
	 * 设置媒体声道.这通常有左声道、右声道、立体声
	 *
	 * @param channels
	 *            媒体声道
	 */
	public void setChannels(String channels) {
		this.channels = channels;
	}

	/**
	 * 获取平台来源
	 *
	 * @return 音乐平台id
	 */
	public String getPlatform() {
		return platform;
	}

	/**
	 * 设置音乐平台
	 *
	 * @param platform
	 *            音乐平台id
	 */
	public void setPlatform(String platform) {
		this.platform = platform;
	}

	/**
	 * 是否可显示视频视图,当媒体文件是MV时,才返回true
	 *
	 * @return 若是视频文件则返回true
	 */
	public abstract boolean viewable();

	/**
	 * 列举了常见的一些音频类型
	 *
	 * @author SCMQ
	 */
	public enum AudioType {
		MP3("mp3"),
		FLAC("flac"),
		WAV("wav"),
		AAC("aac"),
		APE("ape"), //
		M4A("m4a"),
		WMA("wma"),
		AMR("amr"),
		OGG("ogg");

		final String format;

		AudioType(String format) {
			this.format = format;
		}
	}

	/**
	 * 列举了常见的视频类型
	 *
	 * @author SCMQ
	 */
	public enum VideoType {
		MP4("mp4"),
		MKV("mkv"),
		FLV("flv"),
		AVI("avi"), //
		_3GP("3gp"),
		RMVB("rmvb"),
		MOV("mov"),
		VOB("vob"),
		RM("rm");

		final String format;

		VideoType(String format) {
			this.format = format;
		}
	}

	/**
	 * 获取音乐文件过滤器
	 *
	 * @return 文件名称过滤器
	 */
	public static FilenameFilter getAudioFileFilter() {
		return (file, name) -> {
			for (AudioType type : AudioType.values()) {
				if (name.endsWith(type.format)) {
					return true;
				}
			}
			return false;
		};
	}

	/**
	 * 获取视频文件过滤器
	 *
	 * @return 文件名过滤器
	 */
	public static FilenameFilter getVideoFileFilter() {
		return (file, name) -> {
			for (VideoType type : VideoType.values()) {
				if (name.endsWith(type.format)) {
					return true;
				}
			}
			return false;
		};
	}
}
