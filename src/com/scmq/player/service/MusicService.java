package com.scmq.player.service;

import com.scmq.player.dao.MusicDao;
import com.scmq.player.model.Media;
import com.scmq.player.model.Music;
import com.scmq.player.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class MusicService {
	@Autowired
	private MusicDao musicDao;
	@Autowired
	private SingerService singerService;
	@Autowired
	private AlbumService albumService;

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
			singerService.handleSingerId(music.getSinger(), cache);
		}

		// 清除缓存
		cache.clear();
		for (Music music : list) {
			albumService.handleAlbumId(music.getAlbum(), cache);
		}

		// 清除缓存
		cache.clear();
		ArrayList<Music> saveList = new ArrayList<>(list.size());
		for (Music music : list) {
			// 如果是本地音乐(没有平台id),缓存的是文件路径,因为只有路径是唯一的
			String key = music.getPlatform() == null ? music.getPath() : music.getMid();
			if (StringUtil.isEmpty(key)) {
				continue;
			}

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
