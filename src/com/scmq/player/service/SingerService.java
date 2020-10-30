package com.scmq.player.service;

import com.scmq.player.dao.SingerDao;
import com.scmq.player.io.IOUtil;
import com.scmq.player.model.Singer;
import com.scmq.player.net.HttpClient;
import com.scmq.player.util.FileUtil;
import com.scmq.player.util.Resource;
import com.scmq.player.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 歌手信息数据业务
 *
 * @author SCMQ
 */
@Service
public class SingerService {
	/** 歌手数据访问、操作对象 */
	@Autowired
	private SingerDao dao;

	/**
	 * 查询所有歌手信息
	 *
	 * @return 歌手信息列表集合
	 */
	public List<Singer> findAll() {
		return dao.findAll();
	}

	/**
	 * 通过歌手信息,来查找对应的歌手id.
	 *
	 * @param singer
	 *            歌手信息
	 * @return 歌手id, 总是返回查询结果的第一个
	 */
	public Integer findIdByInfo(Singer singer) {
		return dao.findIdByInfo(singer);
	}

	/**
	 * 通过id查询歌手信息
	 *
	 * @return 歌手信息
	 */
	public Singer findSingerById(Integer id) {
		return dao.findSingerById(id);
	}

	/**
	 * 通过歌手名精确查找指定音乐平台的歌手信息
	 *
	 * @param name
	 *            歌手名
	 * @param platformId
	 *            音乐平台id(若为null,则为本地音乐平台的歌手数据)
	 * @return 歌手信息
	 */
	public Singer findSingerByName(String name, String platformId) {
		return StringUtil.isEmpty(name) ? null : dao.findSingerByName(name, platformId);
	}

	/**
	 * 通过歌手mid精确查找指定音乐平台的歌手信息
	 * 
	 * @param mid
	 *            歌手mid
	 * @param platformId
	 *            音乐平台id(若为null,则为本地音乐平台的歌手数据)
	 * @return 歌手信息
	 */
	public Singer findSingerByMid(String mid, String platformId) {
		return dao.findSingerByMid(mid, platformId);
	}

	/**
	 * 从一个歌手复制基本信息到另一个歌手对象.
	 * 
	 * @param source
	 *            复制信息源对象
	 * @param target
	 *            复制信息目标对象
	 */
	public void cloneOfBaseInfo(Singer source, Singer target) {
		target.setId(source.getId());
		target.setSongCount(source.getSongCount());
		target.setAlbumCount(source.getAlbumCount());
		target.setMvCount(source.getMvCount());
		target.setFansCount(source.getFansCount());
		target.setIntroduce(source.getIntroduce());
	}

	/**
	 * 批量保存歌手信息
	 *
	 * @param singers
	 *            多个歌手信息
	 * @return 是否保存成功
	 */
	public boolean save(List<Singer> singers) {
		if (singers == null || singers.isEmpty()) {
			return false;
		}
		// 缓存歌手id,减少查询歌手的次数(保证同一个平台的歌手信息不重复)
		HashMap<String, Integer> cache = new HashMap<>(singers.size() < 10 ? 10 : singers.size() >> 1);
		List<Singer> saveList = new ArrayList<>(singers.size() >> 1);
		for (Singer singer : singers) {
			String key = singer.getPlatform() == null ? singer.getName() : singer.getMid();
			if (StringUtil.isEmpty(key)) {
				continue;
			}

			// 先从缓存中获取id
			Integer id = cache.get(key);
			if (id == null) {
				// 通过歌手信息查询,获得歌手id
				id = dao.findIdByInfo(singer);
				if (id == null) {
					// 加入待保存信息List集合
					saveList.add(singer);
				} else {
					singer.setId(id);
					// 放入缓存
					cache.put(key, id);
				}
			} else {
				// 设置歌手id
				singer.setId(id);
			}
		}
		boolean saved = false;
		if (!saveList.isEmpty()) {
			// 批量保存歌手信息
			saved = dao.save(saveList) == saveList.size();
			// 批量重绑定歌手id
			bindIdOfSinger(saveList);
		}
		return saved;
	}

	/**
	 * 若本地数据库没有此歌手信息,那么保存到本地数据库;最后获取歌手信息ID.
	 * 
	 * @param singer
	 *            歌手信息对象
	 * @param singerInfoCache
	 *            歌手ID缓存集合容器
	 */
	void handleSingerId(Singer singer, HashMap<String, Integer> singerInfoCache) {
		String key = singer == null ? null : singer.getPlatform() == null ? singer.getName() : singer.getMid();
		// 如果没有歌手信息,则忽略
		if (StringUtil.isEmpty(key)) {
			return;
		}

		// 先从map中找是否有歌手id
		Integer id = singerInfoCache.get(key);
		if (id == null) {
			// 查询歌手id
			id = dao.findIdByInfo(singer);
			// 若没有歌手id,则保存歌手信息以获得id
			if (id == null) {
				dao.saveOfSingle(singer);
			} else {
				// 否则设置通过查询而得的id
				singer.setId(id);
			}
			// 放入map,作为下一次使用
			singerInfoCache.put(key, singer.getId());
		} else {
			// 直接使用缓存的歌手id
			singer.setId(id);
		}
	}

	/**
	 * 修改歌手信息
	 *
	 * @param singer
	 *            歌手信息
	 * @return 修改成功的数量
	 */
	public Integer update(Singer singer) {
		return dao.update(singer);
	}

	/**
	 * 批量重绑定歌手id,对于SQLite,MyBatis会返回最后添加的主键值到集合的第一个元素
	 *
	 * @param list
	 *            歌手信息列表
	 */
	private static void bindIdOfSinger(List<Singer> list) {
		Integer id = list.get(0).getId();
		if (id != null) {
			id -= list.size();
			for (Singer singer : list) {
				singer.setId(++id);
			}
		}
	}

	/**
	 * 批量保存歌手图片到本地
	 *
	 * @param list
	 *            歌手信息List集合
	 */
	public void handlePictures(List<Singer> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		HttpClient client = HttpClient.createClient(null).removeAcceptHeader();
		// 默认歌手图片地址(程序内部)
		String uri = null;
		for (Singer singer : list) {
			String platform = singer.getPlatform(), cover = singer.getCover();
			File file = FileUtil.toFile(singer.getMid(), "jpg", "picture/singer", platform);
			// 若歌手图片文件存在
			if (file.isFile()) {
				// 设置歌手图片地址
				singer.setCover(file.toURI().toString());
				continue;
			}
			// 若没有歌手图片地址
			if (StringUtil.isEmpty(cover)) {
				singer.setCover(uri = uri == null ? Resource.getImageURI("_singer") : uri);
				continue;
			}
			// 是否写入到本地文件
			boolean write = IOUtil.write(client.get(cover).openStream(), file);
			// 重置client
			client.reset();
			// 若没保存成功删除文件
			if (write) {
				singer.setCover(file.toURI().toString());
			} else {
				singer.setCover(uri = uri == null ? Resource.getImageURI("_singer") : uri);
				file.delete();
			}
		}
		client.close();
	}

	/**
	 * 处理歌手图片
	 * 
	 * @param singer
	 *            歌手信息
	 */
	public void handlePicture(Singer singer) {
		String platform = singer.getPlatform(), cover = singer.getCover();
		File file = FileUtil.toFile(singer.getMid(), "jpg", "picture/singer", platform);
		// 若歌手图片文件存在
		if (file.isFile()) {
			// 设置歌手图片地址
			singer.setCover(file.toURI().toString());
			return;
		}
		// 若没有歌手图片地址
		if (StringUtil.isEmpty(cover)) {
			singer.setCover(Resource.getImageURI("_singer"));
			return;
		}
		HttpClient client = HttpClient.createClient(null).removeAcceptHeader();
		// 是否写入到本地文件
		boolean write = IOUtil.write(client.get(cover).openStream(), file);
		// 重置client
		client.close();
		// 若没保存成功删除文件
		if (write) {
			singer.setCover(file.toURI().toString());
		} else {
			singer.setCover(Resource.getImageURI("_singer"));
			file.delete();
		}
	}
}
