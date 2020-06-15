package com.scmq.player.service;

import com.scmq.player.dao.AlbumDao;
import com.scmq.player.dao.MusicDao;
import com.scmq.player.dao.SingerDao;
import com.scmq.player.model.Album;
import com.scmq.player.model.Media;
import com.scmq.player.model.Music;
import com.scmq.player.model.Singer;
import com.scmq.player.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("musicService")
public class MusicService {
	@Autowired
	private MusicDao musicDao;
	@Autowired
	private SingerDao singerDao;
	@Autowired
	private AlbumDao albumDao;

	/**
	 * 查询所有音乐信息
	 *
	 * @param types
	 *            音乐类型,可参见{@link Media.AudioType}枚举类
	 * @return 音乐信息的List集合
	 */
	public List<Music> findAll(Media.AudioType... types) {
		return musicDao.findAll(types);
	}

	/**
	 * 批量保存音乐信息到数据表中
	 *
	 * @param list
	 *            Audio类型的List集合
	 * @return 保存成功的数量
	 * @throws NullPointerException
	 *             若音乐信息List集合的任一元素为null,将抛出此异常
	 */
	@Transactional
	public boolean save(List<Music> list) {
		if (list == null || list.isEmpty()) {
			return false;
		}
		// 临时缓存,用于缓存歌手id、专辑id; 指定合适的容量以减少扩容次数
		HashMap<String, Integer> cache = new HashMap<>(list.size() < 10 ? list.size() : list.size() >> 1);
		// 处理所有歌手id
		for (Music music : list) {
			Singer singer = music.getSinger();
			String key = singer == null ? null : singer.getPlatform() == null ? singer.getName() : singer.getMid();
			// 如果没有歌手信息,则忽略
			if (StringUtil.isEmpty(key)) {
				continue;
			}
			// 先从map中找是否有歌手id
			Integer id = cache.get(key);
			if (id == null) {
				// 查询歌手id
				id = singerDao.findIdByInfo(singer);
				// 若没有歌手id,则保存歌手信息以获得id
				if (id == null) {
					singerDao.saveOfSingle(singer);
				} else {
					// 否则设置通过查询而得的id
					singer.setId(id);
				}
				// 放入map,作为下一次使用
				cache.put(key, singer.getId());
			} else {
				// 直接使用缓存的歌手id
				singer.setId(id);
			}
		}
		// 清除缓存
		cache.clear();
		for (Music music : list) {
			Album album = music.getAlbum();
			// 若是本地歌曲专辑信息,则缓存专辑名称,否则缓存专辑mid.
			// 因为本地歌曲没有mid,而网络歌曲专辑信息会存在没有专辑名称但能获取到专辑mid,例如酷狗音乐
			String key = album == null ? null : album.getPlatform() == null ? album.getName() : album.getMid();
			if (StringUtil.isEmpty(key)) {
				continue;
			}
			// 先从map中找是否有专辑id
			Integer id = cache.get(key);
			if (id == null) {
				// 通过专辑信息查询专辑id
				id = albumDao.findIdByInfo(album);
				// 若没有专辑id,则保存专辑信息以获得id
				if (id == null) {
					albumDao.saveOfSingle(album);
				} else {
					// 否则设置通过查询而得的id
					album.setId(id);
				}
				// 放入缓存
				cache.put(key, album.getId());
			} else {
				// 设置专辑id
				album.setId(id);
			}
		}
		// 清除缓存
		cache.clear();
		ArrayList<Music> saveList = new ArrayList<>(list.size());
		for (Music music : list) {
			// 如果是本地音乐(没有平台id),缓存的是文件路径,因为只有路径是唯一的
			String key = music.getPlatform() == null ? music.getPath() : music.getMid();
			// 先从缓存中获取音乐id
			Integer id = cache.get(key);
			if (id == null) {
				// 查询以获得音乐id
				id = musicDao.findIdByInfo(music);
				if (id == null) {
					// 加入待保存音乐信息列表
					saveList.add(music);
				} else {
					// 设置id
					music.setId(id);
					// 放入缓存
					cache.put(key, id);
				}
			} else {
				// 设置id
				music.setId(id);
			}
		}
		if (!saveList.isEmpty()) {
			// 批量保存音乐信息
			boolean saved = musicDao.save(saveList) == saveList.size();
			// 批量重绑定音乐id
			bindIdOfMusic(saveList);
			return saved;
		}
		return true;
	}

	public void update(List<Music> musicList) {
		musicDao.update(musicList);
	}

	/**
	 * 批量重绑定音乐id,对于SQLite,MyBatis会返回最后添加的主键值到集合的第一个元素
	 *
	 * @param list
	 *            歌手信息列表
	 */
	private static void bindIdOfMusic(List<Music> list) {
		Integer id = list.get(0).getId();
		if (id != null) {
			id -= list.size();
			for (Music music : list) {
				music.setId(++id);
			}
		}
	}
}
