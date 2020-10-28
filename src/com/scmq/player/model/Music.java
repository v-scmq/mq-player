package com.scmq.player.model;

import com.scmq.player.io.IOUtil;
import com.scmq.player.net.HttpClient;
import com.scmq.player.util.FileUtil;
import com.scmq.player.util.StringUtil;
import com.scmq.player.util.TimeUtil;
import javafx.scene.image.Image;
import myorg.jaudiotagger.audio.AudioFile;
import myorg.jaudiotagger.audio.AudioHeader;
import myorg.jaudiotagger.audio.flac.FlacFileReader;
import myorg.jaudiotagger.audio.mp3.MP3FileReader;
import myorg.jaudiotagger.audio.ogg.OggFileReader;
import myorg.jaudiotagger.audio.wav.WavFileReader;
import myorg.jaudiotagger.tag.FieldKey;
import myorg.jaudiotagger.tag.Tag;
import myorg.jaudiotagger.tag.images.Artwork;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Objects;

/**
 * 音频(媒体)文件的抽象表示.如果是是一个本地音频文件,那么它的路径应该忽略大小写. 为了解决hashCode方法计算对象hash值,所有本地音频文件的路径都转换为小写
 *
 * @author SCMQ
 */
public final class Music extends Media {

	/** 歌曲所属专辑 */
	private Album album;

	/** 年份 */
	private String year;

	/** 比特率 */
	private String bitRate;

	/** 采样率 */
	private String sampleRate;

	/** 流派 */
	private String genre;

	/** 音乐mid(各个音乐平台所用,本地音乐可以不用) */
	private String mid;
	/** 音乐的MV id(各个音乐平台所用,本地音乐可以不用) */
	private String vid;

	/** 缓存这个hash值,避免每次计算 */
	private int hash;

	public Music() {
	}

	/**
	 * 通过File文件对象实例,创建一个音频文件对象
	 *
	 * @param file
	 *            本地文件对象
	 */
	public Music(File file) {
		init(file);
	}

	/**
	 * 通过一个URI字符串构造一个音频文件对象
	 *
	 * @param uri
	 *            音频文件URI
	 */
	public Music(String uri) {
		super(uri);
	}

	/**
	 * 获取歌曲mid(各个音乐平台所用,本地音乐可以不用)
	 *
	 * @return 歌曲mid
	 */
	public String getMid() {
		return mid;
	}

	/**
	 * 设置歌曲mid(各个音乐平台所用,本地音乐可以不用)
	 *
	 * @param mid
	 *            歌曲mid
	 */
	public void setMid(String mid) {
		this.mid = mid;
	}

	/**
	 * 获取歌曲的MV id
	 *
	 * @return 歌曲MV id
	 */
	public String getVid() {
		return vid;
	}

	/**
	 * 设置歌曲MV id
	 *
	 * @param vid
	 *            歌曲MV id
	 */
	public void setVid(String vid) {
		this.vid = vid;
	}

	/**
	 * 初始化音频文件对象的部分属性
	 *
	 * @param file
	 *            音频文件
	 */
	@Override
	void init(File file) {
		super.init(file);
		try {
			AudioFile audioFile;
			String format = getFormat() == null ? null : getFormat().toLowerCase();
			if ("mp3".equals(format)) {
				audioFile = new MP3FileReader().read(file);
			} else if ("flac".equals(format)) {
				audioFile = new FlacFileReader().read(file);
			} else if ("wav".equals(format)) {
				audioFile = new WavFileReader().read(file);
			} else if ("ogg".equals(format)) {
				audioFile = new OggFileReader().read(file);
			} else {
				// 一般执行不到此
				// 处理歌曲标题和艺术家信息
				handleTitleAndArtist();
				return;
			}
			// 音频头信息对象
			AudioHeader header = audioFile.getAudioHeader();
			// 音轨长度(时长),单位->秒
			int second = header.getTrackLength();
			setDuration(TimeUtil.secondToTime(second));

			// 编码类型/音频格式
			// setFormat(header.getEncodingType());

			// 音频元数据标签对象
			Tag tag = audioFile.getTag();

			// 标题
			setTitle(tag.getFirst(FieldKey.TITLE));
			// 如果有乱码置为null
			if (StringUtil.isNotEmpty(getTitle()) && StringUtil.isMessyCode(getTitle())) {
				setTitle(null);
			}
			// 艺术家
			String artist = tag.getFirst(FieldKey.ARTIST);
			// 若有歌手信息,且不包含乱码字符
			if (StringUtil.isNotEmpty(artist) && !StringUtil.isMessyCode(artist)) {
				setSinger(new Singer(artist));
			}

			// 专辑
			String album = tag.getFirst(FieldKey.ALBUM);
			// 若有专辑信息,且不包含乱码字符
			if (StringUtil.isNotEmpty(album) && !StringUtil.isMessyCode(album)) {
				setAlbum(new Album(getSinger(), album));
			}

			// 年代
			setYear(tag.getFirst(FieldKey.YEAR));

			// 获取比特率
			setBitRate(header.getBitRate());

			// 声道数量
			setChannels(header.getChannels());

			// 采样率
			setSampleRate(header.getSampleRate());

			// 流派
			setGenre(tag.getFirst(FieldKey.GENRE));

			// Tag类型
			// header.getFormat();

			// 轨道
			// tag.getFirst(FieldKey.TRACK);

			// 编码(目前只看到FLAC文件有信息)
			// tag.getFirst(FieldKey.ENCODER);
		} catch (Exception ignored) {
		}

		handleTitleAndArtist();
	}

	/** 处理歌曲标题和艺术家(歌手)为空的方法 */
	private void handleTitleAndArtist() {
		if (getTitle() != null && getSinger() != null) {
			return;
		}
		String name = getFileName();
		int index = name.indexOf('-');
		if (index > 0) {
			setSinger(new Singer(name.substring(0, index).trim()));
			setTitle(name.substring(index + 1).trim());
		} else {
			setTitle(name);
		}
	}

	@Override
	public boolean viewable() {
		return false;
	}

	/**
	 * 获歌曲所属专辑信息
	 *
	 * @return 专辑信息
	 */
	public Album getAlbum() {
		return album;
	}

	/**
	 * 设置歌曲专辑信息
	 *
	 * @param album
	 *            专辑信息
	 */
	public void setAlbum(Album album) {
		this.album = album;
	}

	/**
	 * 获取歌曲年份
	 *
	 * @return 歌曲所属年份
	 */
	public String getYear() {
		return year;
	}

	/**
	 * 设置歌曲所属年份
	 *
	 * @param year
	 *            歌曲年份
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * 获取歌曲比特率
	 *
	 * @return 音频比特率
	 */
	public String getBitRate() {
		return bitRate;
	}

	/**
	 * 设置歌曲比特率
	 *
	 * @param bitRate
	 *            音频比特率
	 */
	public void setBitRate(String bitRate) {
		this.bitRate = bitRate;
	}

	/**
	 * 获取歌曲采样率
	 *
	 * @return 采样率
	 */
	public String getSampleRate() {
		return sampleRate;
	}

	/**
	 * 设置歌曲采样率
	 *
	 * @param sampleRate
	 *            歌曲采样率
	 */
	public void setSampleRate(String sampleRate) {
		this.sampleRate = sampleRate;
	}

	/**
	 * 获取歌曲流派
	 *
	 * @return 歌曲流派类型. 如 流行、古典、摇滚...
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * 设置歌曲流派
	 *
	 * @param genre
	 *            歌曲流派类型. 如 流行、古典、摇滚...
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	/**
	 * 获取音乐的专辑封面. <br>
	 * 本地音乐专辑封面图片,用专辑名称保存;网络平台音乐专辑封面图,用专辑mid保存
	 *
	 * @return 专辑封面图片
	 */
	@Override
	public Image getImageCover() {
		Album album = getAlbum();
		if (album == null) {
			// 若无专辑信息且来自本地音乐,则获取音乐文件内部的封面图
			return getPlatform() != null ? null : getAudioFileCover(getPath(), getFormat(), null);
		}

		String platform = album.getPlatform();
		// 1.若是来自本地音乐专辑
		if (platform == null) {
			// 若没有专辑名称
			if (StringUtil.isEmpty(album.getName())) {
				return null;
			}
			// 若专辑封面图片文件存在,则加载
			File file = FileUtil.toFile(FileUtil.resolve(album.getName()), "jpg", "picture/album/0");
			if (file.isFile()) {
				Image image = new Image(file.toURI().toString());
				if (!image.isError()) {
					return image;
				}
			}
			// 尝试从音乐文件内部获取封面图
			return getAudioFileCover(getPath(), getFormat(), file);
		}
		// 若专辑mid不存在
		if (StringUtil.isEmpty(album.getMid())) {
			return null;
		}

		// 2.若是来自网络的音乐专辑
		File file = FileUtil.toFile(album.getMid(), "jpg", "picture/album", platform);
		// 若有图片地址,并且本地图片文件不存在
		if (!StringUtil.isEmpty(album.getCover()) && !file.isFile()) {
			HttpClient client = HttpClient.createClient(null).removeAcceptHeader();
			// 若未保存成功,则删除文件
			if (!IOUtil.write(client.get(album.getCover()).openStream(), file)) {
				file.delete();
			}
			client.close();
		}
		// 若是本地图片文件
		if (file.isFile()) {
			Image image = new Image(file.toURI().toString());
			return image.isError() ? null : image;
		}
		return null;
	}

	/**
	 * 从音乐文件内部获取歌曲封面图片
	 *
	 * @param path
	 *            歌曲文件路径
	 * @param format
	 *            歌曲格式
	 * @param out
	 *            将封面图片保存的文件
	 * @return 歌曲封面图片
	 */
	private Image getAudioFileCover(String path, String format, File out) {
		File file = new File(path);
		try {
			myorg.jaudiotagger.tag.Tag tag;
			if ("mp3".equalsIgnoreCase(format)) {
				tag = new MP3FileReader().read(file).getTag();
			} else if ("flac".equalsIgnoreCase(format)) {
				tag = new FlacFileReader().read(file).getTag();
			} else if ("wav".equalsIgnoreCase(format)) {
				tag = new WavFileReader().read(file).getTag();
			} else if ("ogg".equalsIgnoreCase(format)) {
				tag = new OggFileReader().read(file).getTag();
			} else {
				return null;
			}
			Artwork artWork = tag.getFirstArtwork();
			byte[] data = artWork == null ? null : artWork.getBinaryData();
			if (data != null && data.length > 0) {
				if (out != null) {
					IOUtil.write(data, out);
				}
				Image image = new Image(new ByteArrayInputStream(data));
				return image.isError() ? null : image;
			}
		} catch (Exception ignore) {
		}
		return null;
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
		String key = getPlatform() == null ? getPath() : getMid();
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
		Music music = (Music) obj;
		String platform = getPlatform(), platform2 = music.getPlatform();
		// 若不是同一平台,则返回false
		if (!Objects.equals(platform, platform2)) {
			return false;
		}
		// 若是本地媒体,则比较路径(同一小写)是否相同;否则比较mid是否相同
		String key = platform == null ? getPath() : getMid();
		return Objects.equals(key, platform2 == null ? music.getPath() : music.getMid());
	}

	@Override
	public String toString() {
		return "Music [singer=" + getSinger() + ", title=" + getTitle() + ", album=" + album + ", year=" + year
				+ ", path=" + getPath() + "]";
	}
}
