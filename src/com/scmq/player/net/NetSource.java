package com.scmq.player.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.scmq.player.model.Album;
import com.scmq.player.model.LyricLine;
import com.scmq.player.model.MV;
import com.scmq.player.model.Music;
import com.scmq.player.model.Page;
import com.scmq.player.model.Rank;
import com.scmq.player.model.RankItem;
import com.scmq.player.model.Singer;
import com.scmq.player.model.Special;
import com.scmq.player.model.Tag;

import java.util.List;

/**
 * 音乐平台网络资源库
 *
 * @author SCMQ
 */
public interface NetSource {
	/** 使用StringBuilder处理URI字符串的最小长度,减少StringBuilder内部字符数组扩容的次数 */
	int URI_MIN_LENGTH = 150;

	/**
	 * 获取支持的歌手分类标签信息组
	 *
	 * @return 歌手分类(Tag)信息List集合
	 */
	List<Tag> singerKindTags();

	/**
	 * 获取支持的歌手检索信息组
	 *
	 * @return 歌手检索(Tag)信息List集合
	 */
	List<Tag> singerEnTags();

	/**
	 * 获取歌手列表信息集合
	 *
	 * @param page
	 *            分页对象,不能为null
	 * @param singerKind
	 *            歌手分类标签信息,可以为null
	 * @param singerEn
	 *            歌手字母检索标签信息,可以为null
	 * @return 歌手信息列表集合
	 */
	List<Singer> singerList(Page page, Tag singerKind, Tag singerEn);

	/**
	 * 获取歌手的歌曲列表
	 *
	 * @param singer
	 *            歌手信息,不能为null
	 * @param page
	 *            分页对象,不能为null
	 * @return 歌曲列表集合
	 */
	List<Music> songList(Singer singer, Page page);

	/**
	 * 获取歌手的专辑列表
	 *
	 * @param singer
	 *            歌手信息,不能为null
	 * @param page
	 *            分页对象,不能为null
	 * @return 专辑信息列表集合
	 */
	List<Album> albumList(Singer singer, Page page);

	/**
	 * 获取专辑的歌曲列表
	 *
	 * @param album
	 *            专辑信息对象,不能为null
	 * @param page
	 *            分页信息对象,不能为null
	 * @return 专辑包含的歌曲List集合
	 */
	List<Music> songList(Album album, Page page);

	/**
	 * 获取歌手的MV列表
	 *
	 * @param singer
	 *            歌手信息,不能为null
	 * @param page
	 *            分页对象,不能为null
	 * @return MV信息列表集合
	 */
	List<MV> mvList(Singer singer, Page page);

	/**
	 * 处理MV信息,比如播放地址
	 *
	 * @param mv
	 *            MV(视频)信息,不能为null
	 * @return 如果处理成功则返回true
	 */
	boolean handleMVInfo(MV mv);

	/**
	 * 处理歌手简介、单曲数量、专辑数量信息
	 *
	 * @param singer
	 *            歌手信息
	 * @return 若处理成功则返回true
	 */
	boolean handleSingerInfo(Singer singer);

	/**
	 * 搜索歌曲,这个搜索关键词可以是歌曲名、歌手名、专辑名.<br>
	 * 在之前的基础之上新增了一个map集合参数,可以收集某些信息.这些信息可以是异常信息,也可以是歌手、歌手热门歌曲等信息.
	 * 当map集合不能空且能够获得直达歌手信息时,那么map集合就一定有一个key为"callback-singer"值为new Singer()的键值对.
	 * 
	 * @param key
	 *            搜索关键词(也可以是拼音),参数可以是null或是一个空串
	 * @param page
	 *            分页对象,不能为null
	 * @return 音乐信息列表集合
	 */
	List<Music> songSearch(String key, Page page);

	/**
	 * 处理音乐信息,以获得播放地址
	 *
	 * @param music
	 *            音乐信息对象
	 * @return 如果处理成功则返回true
	 */
	boolean handleMusicInfo(Music music);

	/**
	 * 获取歌单分类标签信息List集合
	 *
	 * @return 歌单分类标签信息List集合
	 */
	List<Tag> specialTags();

	/**
	 * 获取歌单列表
	 *
	 * @param tag
	 *            歌单分类标签信息(可以为null)
	 * @param page
	 *            分页对象(若支持分页,则不能为null,这依赖于实现类)
	 * @return 歌单List集合
	 */
	List<Special> specialList(Tag tag, Page page);

	/**
	 * 获取歌单包含的歌曲列表
	 *
	 * @param special
	 *            歌单
	 * @param page
	 *            分页对象
	 * @return 歌单中的歌曲的List集合
	 * @throws NullPointerException
	 *             如果方法任一参数为null,将抛出此异常
	 */
	List<Music> songList(Special special, Page page);

	/**
	 * 获取MV分类标签列表
	 *
	 * @return MV分类分类标签List集合
	 */
	List<Tag> mvTags();

	/**
	 * 获取指定MV分类下的MV列表
	 *
	 * @param mvTag
	 *            MV分类标签信息,可以为null
	 * @param page
	 *            分页对象 不能为null
	 * @return MV信息(Video)的List集合
	 */
	List<MV> mvList(Tag mvTag, Page page);

	/**
	 * 获取榜单列表
	 *
	 * @return 音乐榜单List集合
	 */
	List<Rank> rankList();

	/**
	 * 获取指定榜单项包含的音乐列表
	 *
	 * @param item
	 *            榜单项,可为null
	 * @param page
	 *            分页对象,不能为null
	 * @return 榜单项包含的音乐的List集合
	 */
	List<Music> songList(RankItem item, Page page);

	/**
	 * 根据关键词,搜索歌单 以获得歌单列表
	 *
	 * @param key
	 *            搜索关键词(也可以是拼音),参数可以是null或是一个空串
	 * @param page
	 *            分页对象,不能为null
	 * @return 歌单信息列表集合
	 */
	List<Special> specialSearch(String key, Page page);

	/**
	 * 搜索MV,这个搜索关键词可以是歌曲名、歌手名、专辑名
	 *
	 * @param key
	 *            搜索关键词(也可以是拼音),参数可以是null或是一个空串
	 * @param page
	 *            分页对象,不能为null
	 * @return MV信息列表集合
	 */
	List<MV> mvSearch(String key, Page page);

	/**
	 * 搜索专辑,这个搜索关键词可以是歌曲名、歌手名、专辑名
	 *
	 * @param key
	 *            搜索关键词(也可以是拼音),参数可以是null或是一个空串
	 * @param page
	 *            分页对象,不能为null
	 * @return 专辑信息列表集合
	 */
	List<Album> albumSearch(String key, Page page);

	/**
	 * 搜索歌手
	 * 
	 * @param keyword
	 *            歌手名关键词
	 * @return 歌手信息列表集合
	 */
	List<Singer> singerSearch(String keyword);

	/**
	 * 获得音乐歌词信息,
	 *
	 * @param music
	 *            音乐信息
	 * @return 歌词行List集合
	 */
	List<LyricLine> handleLyric(Music music);

	/**
	 * 通过音乐信息获取歌手写真URL的List集合
	 * 
	 * @param music
	 *            音乐信息
	 * @return 歌手写真URL的List集合
	 */
	List<String> singerImageList(Music music);

	/**
	 * 获取热搜词列表
	 *
	 * @return 热搜词列表
	 */
	List<String> hotKeys();

	/** 音乐平台id */
	String platformId();

	/**
	 * 音乐关联平台、歌手、专辑; MV关联平台、歌手; 歌手关联平台; 专辑关联平台、歌手
	 *
	 * @param music
	 *            音乐信息,可为null
	 * @param mv
	 *            MV信息,可为null
	 * @param singer
	 *            歌手信息,可为null
	 * @param album
	 *            专辑信息,可为null
	 */
	default void bind(Music music, MV mv, Singer singer, Album album) {
		String platform = platformId();
		// 音乐 关联 平台id、歌手、专辑
		if (music != null) {
			music.setPlatform(platform);
			music.setSinger(singer);
			music.setAlbum(album);
		}
		// MV 关联 平台id、歌手
		if (mv != null) {
			mv.setPlatform(platform);
			mv.setSinger(singer);
		}
		// 歌手 关联 平台id
		if (singer != null) {
			singer.setPlatform(platform);
		}
		// 专辑 关联 平台id、歌手
		if (album != null) {
			album.setPlatform(platform);
			album.setSinger(singer);
		}
	}

	/**
	 * 处理字符串所用的字符编码, 默认使用 {@code UTF-8}.
	 *
	 * @return 字符编码
	 */
	default String charset() {
		return "UTF-8";
	}

	/**
	 * 从JsonObject对象中取出指定属性对应的值(String)
	 *
	 * @param obj
	 *            JsonObject对象
	 * @param key
	 *            JSON对象中的属性
	 * @return JSON对象中的属性对应的值
	 */
	default String valueOf(JsonObject obj, String key) {
		JsonElement element = obj.get(key);
		return element == null ? "" : element.getAsString();
	}

	/**
	 * 取JSON数组的第index索引的元素值(String)
	 *
	 * @param array
	 *            JSON数组
	 * @param index
	 *            位置索引
	 * @return JSON数组的index索引对应的元素值(String)
	 */
	default String valueOf(JsonArray array, int index) {
		JsonElement element = array == null || !array.isJsonArray() || array.size() == 0 ? null : array.get(index);
		return element == null ? "" : element.getAsString();
	}

	/**
	 * 从JsonObject对象中取出指定属性对应的值(int)
	 *
	 * @param obj
	 *            JsonObject对象
	 * @param key
	 *            JSON对象中的属性
	 * @return JSON对象中的属性对应的值
	 */
	default int valueOfInt(JsonObject obj, String key) {
		JsonElement element = obj.get(key);
		return element == null || !element.isJsonPrimitive() ? 0 : element.getAsInt();
	}

	/**
	 * 从JsonObject对象中取出指定属性对应的值(long)
	 *
	 * @param obj
	 *            JsonObject对象
	 * @param key
	 *            JSON对象中的属性
	 * @return JSON对象中的属性对应的值
	 */
	default long valueOfLong(JsonObject obj, String key) {
		JsonElement element = obj.get(key);
		return element == null || !element.isJsonPrimitive() ? 0 : element.getAsLong();
	}

	/**
	 * 从JsonObject对象中取出指定属性对应的值(float)
	 *
	 * @param obj
	 *            JsonObject对象
	 * @param key
	 *            JSON对象中的属性
	 * @return JSON对象中的属性对应的值
	 */
	default float valueOfFloat(JsonObject obj, String key) {
		JsonElement element = obj.get(key);
		return element == null || !element.isJsonPrimitive() ? 0 : element.getAsFloat();
	}
}