package com.scmq.player.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scmq.player.app.App;
import com.scmq.player.core.FXMediaPlayer;
import com.scmq.player.core.MediaPlayer;
import com.scmq.player.dao.AlbumDao;
import com.scmq.player.dao.LocalListDao;
import com.scmq.player.dao.MusicDao;
import com.scmq.player.dao.PlayListDao;
import com.scmq.player.dao.SingerDao;
import com.scmq.player.net.NetSource;
import com.scmq.player.util.FileUtil;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置模块控制器
 * 
 * @author SCMQ
 * @since 2020-10-27
 */
@Controller
public class ConfigureController {
	/** 歌手信息表访问对象 */
	@Autowired
	private SingerDao singerDao;
	/** 专辑信息表访问对象 */
	@Autowired
	private AlbumDao albumDao;
	/** 媒体信息表访问对象 */
	@Autowired
	private MusicDao musicDao;
	/** 本地音乐信息表访问对象 */
	@Autowired
	private LocalListDao localListDao;
	/** 播放队列信息表访问对象 */
	@Autowired
	private PlayListDao playListDao;

	@Autowired
	private MainController mainController;
	@Autowired
	private LocalMusicController localMusicController;
	@Autowired
	private NetMusicController netMusicController;

	/** 第三方网络音乐资源平台 */
	private NetSource netSource;
	/** 存储所有可用的第三方网络音乐资源平台List集合 */
	private final List<NetSource> netSourceList = new ArrayList<>();

	/** Spring容器加载完成后, 回调此方法以执行性初始化. */
	@PostConstruct
	private void initialize() {
		createTable();
		loadConfigure();

		MediaPlayer player = new FXMediaPlayer(mainController);
		// 将播放器对象放入容器中
		App.put(MediaPlayer.class, player);
		Platform.runLater(() -> {
			localMusicController.bind();
			mainController.bind();
			netMusicController.bind();
		});
	}

	/**
	 * 创建数据库表.<br>
	 * 若某些必须的数据库表还未创建,那么创建这些数据库表.
	 */
	@Transactional
	void createTable() {
		singerDao.createTable();
		albumDao.createTable();
		musicDao.createTable();
		localListDao.createTable();
		localListDao.createItemTable();
		playListDao.createTable();
		playListDao.createItemTable();
		// TODO 还有部分数据库表,但是目前还未使用(可在使用到时,加入类似上面的语句)
	}

	/**
	 * 加载配置文件
	 */
	private void loadConfigure() {
		File file = FileUtil.toFile("config.json", null, "configure");
		if (!file.isFile()) {
			return;
		}

		try (FileReader reader = new FileReader(file)) {
			JsonObject node = new JsonParser().parse(reader).getAsJsonObject();
			JsonArray array = node.getAsJsonArray("net-source-platform");
			for (JsonElement element : array) {
				node = element.getAsJsonObject();
				Class<?> clazz = Class.forName(node.get("class").getAsString());
				netSourceList.add((NetSource) clazz.newInstance());
			}
		} catch (FileNotFoundException ignore) {
			// 断言异常不会发生
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取第三方网络音乐资源平台. <br>
	 * 通常情况下,主要提供了第三方音乐资源平台,都可以获取.但是若没有任何提供的可用实现,那么将返回null.
	 * 
	 * @return 第三方网络音乐资源平台.
	 */
	public NetSource getNetSourceImpl() {
		if (netSource != null) {
			return netSource;
		}

		return netSourceList.isEmpty() ? null : (netSource = netSourceList.get(0));
	}

	/**
	 * 根据平台ID,获取指定第三方网络音乐资源平台.<br>
	 * 从已经加装的所有第三方网络音乐资源平台List集合中获取匹配的可用实现, 当指定平台ID不存在已经加装的第三方平台实现中,这将导致获取失败,并返回null.
	 * 
	 * @param platform
	 *            第三方网络音乐资源平台
	 * @return 第三方网络音乐资源平台对象
	 */
	public NetSource getNetSourceImpl(String platform) {
		if (platform == null) {
			return null;
		}

		if (netSource != null && platform.equals(netSource.platformId())) {
			return netSource;
		}

		return netSourceList.stream().filter(e -> platform.equals(e.platformId())).findFirst().orElse(null);
	}
}
