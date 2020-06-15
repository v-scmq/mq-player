package com.scmq.player.model;

import java.util.List;

/**
 * 播放列表实体类,该类的实例对应数据库表play_list的每一行记录
 * 
 * 
 * @author SCMQ
 *
 */
public class PlayList {
	/** 播放列表id */
	private Integer id;

	/** 音乐播放队列 */
	private List<Music> list;

	/** 排序方式 */
	private SortMethod sort;

	/** 创建时间 */
	private String createTime;

	/** 播放索引 (不参与数据库操作) */
	private int index;

	/** MV播放队列 */
	private List<MV> mvList;

	/** 构造一个默认的播放列表对象 */
	public PlayList() {
	}

	/**
	 * 通过播放列表id、播放索引、音乐列表(List集合) 来构造一个PlayList对象
	 * 
	 * @param id
	 *            播放列表id
	 * @param index
	 *            播放索引
	 * @param list
	 *            音乐列表({@code List<Music>}集合)
	 */
	public PlayList(Integer id, int index, List<Music> list) {
		setId(id);
		setList(list);
		setIndex(index);
	}

	/**
	 * 通过播放索引和MV播放队列创建PlayList对象
	 * 
	 * @param index
	 *            播放索引
	 * @param mvList
	 *            MV播放列表
	 */
	public PlayList(int index, List<MV> mvList) {
		setIndex(index);
		setMvList(mvList);
	}

	/**
	 * 获取播放列表id
	 * 
	 * @return 播放列表id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 获取播放列表id
	 * 
	 * @param id
	 *            播放列表id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 获取播放列表所有的音乐List集合
	 * 
	 * @return 音乐List集合
	 */
	public List<Music> getList() {
		return list;
	}

	/**
	 * 设置播放列表所有的音乐List集合
	 * 
	 * @param musics
	 *            音乐List集合
	 */
	public void setList(List<Music> musics) {
		this.list = musics;
	}

	public List<MV> getMvList() {
		return mvList;
	}

	public void setMvList(List<MV> mvList) {
		this.mvList = mvList;
	}

	/**
	 * 获取排序方式
	 * 
	 * @return 排序方式
	 */
	public SortMethod getSort() {
		return sort;
	}

	/**
	 * 设置排序方式
	 * 
	 * @param sort
	 *            排序方式
	 */
	public void setSort(SortMethod sort) {
		this.sort = sort;
	}

	/**
	 * 获取创建时间
	 * 
	 * @return 创建时间
	 */
	public String getCreateTime() {
		return createTime;
	}

	/**
	 * 设置创建播放列表的时间
	 * 
	 * @param createTime
	 *            创建时间
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取播放索引
	 * 
	 * @return 当前播放位置索引;
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * 设置当前播放位置索引
	 * 
	 * @param index
	 *            播放位置索引;
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		return "PlayList [id=" + id + ", list=" + list + ", sort=" + sort + ", createTime=" + createTime + "]";
	}
}
