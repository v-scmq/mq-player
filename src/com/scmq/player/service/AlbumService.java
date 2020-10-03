package com.scmq.player.service;

import com.scmq.player.dao.AlbumDao;
import com.scmq.player.io.IOUtil;
import com.scmq.player.model.Album;
import com.scmq.player.net.HttpClient;
import com.scmq.player.util.FileUtil;
import com.scmq.player.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class AlbumService {
	@Autowired
	private AlbumDao albumDao;
	@Autowired
	private SingerService singerService;

	/**
	 * 通过专辑信息查找专辑id.<br>
	 * 若是本地歌曲专辑,则通过{@link Album#getName()}查找; 否则通过{@link Album#getMid()}}和{@link Album#getPlatform()}查找
	 *
	 * @param album
	 *            专辑信息对象
	 * @return 专辑id
	 */
	public Integer findIdByInfo(Album album) {
		return albumDao.findIdByInfo(album);
	}

	/**
	 * 查询所有专辑信息
	 *
	 * @return 专辑信息List集合
	 */
	public List<Album> findAll() {
		return albumDao.findAll();
	}

	/**
	 * 通过id查询专辑信息
	 *
	 * @return 专辑信息对象
	 */
	public Album findAlbumById(Integer id) {
		return albumDao.findAlbumById(id);
	}

	/**
	 * 批量保存专辑信息
	 *
	 * @param albums
	 *            多个专辑信息
	 * @return 是否保存成功
	 * @throws NullPointerException
	 *             专辑信息List集合的任一元素为null,将抛出此异常
	 */
	public boolean save(List<Album> albums) {
		if (albums == null || albums.isEmpty()) {
			return false;
		}
		// 存储需要保存专辑信息的List集合
		HashMap<String, Integer> cache = new HashMap<>(albums.size() < 10 ? albums.size() : albums.size() >> 1);
		for (Album album : albums) {
			singerService.handleSingerId(album.getSinger(), cache);
		}

		cache.clear();// 清除缓存
		List<Album> saveList = new ArrayList<>(albums.size() < 10 ? albums.size() : albums.size() >> 1);
		for (Album album : albums) {
			// 若是本地歌曲专辑信息,则缓存专辑名称,否则缓存专辑mid.
			// 因为本地歌曲没有mid,而网络歌曲专辑信息会存在没有专辑名称但能获取到专辑mid,例如酷狗音乐
			String key = album.getPlatform() == null ? album.getName() : album.getMid();
			if (StringUtil.isEmpty(key)) {
				continue;
			}

			// 先从map缓存中查找是否有专辑id
			Integer id = cache.get(key);
			if (id == null) {
				// 通过专辑信息查询专辑id
				id = albumDao.findIdByInfo(album);
				// 若没有专辑id,则保存专辑信息以获得id
				if (id == null) {
					// 加入待保存专辑信息列表
					saveList.add(album);
				} else {
					// 否则设置通过查询而得的id
					album.setId(id);
					// 放入缓存
					cache.put(key, id);
				}
			} else {
				// 设置专辑id
				album.setId(id);
			}
		}
		if (!saveList.isEmpty()) {
			// 批量保存专辑信息
			boolean saved = albumDao.save(saveList) == saveList.size();
			// 批量重绑定专辑id
			bindIdOfAlbum(saveList);
			return saved;
		}
		return false;
	}

	/**
	 * 若本地数据库没有此专辑信息,那么保存到本地数据库;最后获取专辑信息ID.
	 * 
	 * @param album
	 *            专辑信息对象
	 * @param albumInfoCache
	 *            专辑ID缓存集合容器
	 */
	void handleAlbumId(Album album, HashMap<String, Integer> albumInfoCache) {
		// 若是本地歌曲专辑信息,则缓存专辑名称,否则缓存专辑mid.
		// 因为本地歌曲没有mid,而网络歌曲专辑信息会存在没有专辑名称但能获取到专辑mid,例如酷狗音乐
		String key = album == null ? null : album.getPlatform() == null ? album.getName() : album.getMid();
		if (StringUtil.isEmpty(key)) {
			return;
		}
		// 先从map中找是否有专辑id
		Integer id = albumInfoCache.get(key);
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
			albumInfoCache.put(key, album.getId());
		} else {
			// 设置专辑id
			album.setId(id);
		}
	}

	/**
	 * 修改专辑信息
	 *
	 * @param album
	 *            专辑信息
	 * @return 修改成功的数量
	 */
	public Integer update(Album album) {
		return albumDao.update(album);
	}

	/**
	 * 批量重绑定专辑id,对于SQLite,MyBatis会返回最后添加的主键值到集合的第一个元素
	 *
	 * @param list
	 *            歌手信息列表
	 */
	private static void bindIdOfAlbum(List<Album> list) {
		Integer id = list.get(0).getId();
		if (id != null) {
			id -= list.size();
			for (Album album : list) {
				album.setId(++id);
			}
		}
	}

	/**
	 * 批量保存网络音乐专辑图片到本地
	 *
	 * @param list
	 *            专辑信息List集合
	 */
	public void handlePictures(List<Album> list) {
		HttpClient client = HttpClient.createClient(null).removeAcceptHeader();
		// 默认专辑图片地址(程序内部)
		String uri = null;
		for (Album album : list) {
			String platform = album.getPlatform(), cover = album.getCover();
			File file = FileUtil.toFile(album.getMid(), "jpg", "picture\\album", platform);
			// 若专辑图片文件存在
			if (file.isFile()) {
				// 设置专辑图片地址
				album.setCover(file.toURI().toString());
				continue;
			}
			// 若没有专辑图片地址
			if (StringUtil.isEmpty(cover)) {
				album.setCover(uri = uri == null ? FileUtil.getImageURI("_album") : uri);
				continue;
			}
			// 是否写入到本地文件
			boolean write = IOUtil.write(client.get(cover).openStream(), file);
			// 重置client
			client.reset();
			// 若没保存成功删除文件
			if (write) {
				album.setCover(file.toURI().toString());
			} else {
				album.setCover(uri = uri == null ? FileUtil.getImageURI("_album") : uri);
				file.delete();
			}
		}
		client.close();
	}
}
