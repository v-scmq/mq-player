package com.scmq.player.service;

import com.scmq.player.dao.AlbumDao;
import com.scmq.player.dao.LocalListDao;
import com.scmq.player.dao.MusicDao;
import com.scmq.player.dao.PlayListDao;
import com.scmq.player.dao.SingerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据库维护对象
 * 
 * @author SCMQ
 * @since 2020/10/03
 */
@Service
public class DataBaseService {
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

	/**
	 * 创建数据库表.<br>
	 * 若某些必须的数据库表还未创建,那么创建这些数据库表.
	 */
	@Transactional
	public void create() {
		singerDao.createTable();
		albumDao.createTable();
		musicDao.createTable();
		localListDao.createTable();
		localListDao.createItemTable();
		playListDao.createTable();
		playListDao.createItemTable();
		// TODO 还有部分数据库表,但是目前还未使用(可在使用到时,加入类似上面的语句)
	}
}
