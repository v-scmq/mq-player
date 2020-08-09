package com.scmq.player.core;

import com.scmq.player.model.Media;
import javafx.scene.Node;

public interface MediaPlayer {
	/** 播放媒体资源 */
	void play();

	/** 暂停播放 */
	void pause();

	/** 停止播放 */
	void stop();

	/**
	 * 调整播放器进度
	 * 
	 * @param value
	 *            0 <= value <= 1 之间的值
	 */
	void seek(double value);

	/**
	 * 调整播放器播放位置到position处
	 * 
	 * @param position
	 *            媒体播放位置(毫秒时间值)
	 */
	void seek(long position);

	/**
	 * 设置播放速率,默认为1倍速率.但是这个速率最好是大于等于0.5,如果小于0.5播放器几乎没有声音
	 * 
	 * @param rate
	 *            播放速率 0 <= rate <= 2
	 */
	void setRate(float rate);

	/**
	 * 释放资源
	 */
	void release();

	/**
	 * 获取媒体总时长
	 * 
	 * @return 总时长(毫秒)
	 */
	long getDuration();

	/**
	 * 获取已经播放的时间
	 * 
	 * @return 播放时间(毫秒)
	 */
	long getTime();

	/**
	 * 检查播放器中是否有媒体资源可播放
	 * 
	 * @return 若播放器可播放则返回true
	 */
	boolean isPlayable();

	/**
	 * 检查播放器是否正在播放
	 * 
	 * @return 如正在播放则返回true
	 */
	boolean isPlaying();

	/**
	 * 检查播放器是否暂停播放
	 * 
	 * @return 如已暂停播放则返回true
	 */
	boolean isPause();

	/**
	 * 设置播放器音量
	 * 
	 * @param volume
	 *            播放器音量, 0 <= volume <= 1
	 */
	void setVolume(float volume);

	/**
	 * 获取播放器音量
	 * 
	 * @return 播放器音量, 0 <= volume <= 1
	 */
	float getVolume();

	/**
	 * 准备媒体资源
	 * 
	 * @param media
	 *            媒体
	 * @return 若媒体播放器准备就绪则返回true
	 */
	boolean prepareMedia(Media media);

	/**
	 * 恢复上一次播放进度
	 * 
	 * @param volume
	 *            上一次关闭播放器时的音量, 0 <= volume <= 1
	 * @param position
	 *            上一次关闭播放器时的进度值, 0 <= position <= 1
	 */
	void resume(float volume, float position);

	/**
	 * 是否支持音乐频谱
	 * 
	 * @return 若支持则返回true
	 */
	boolean supportAudioSpectrum();

	/**
	 * 绑定或解绑音乐频谱数据回调.若播放器不支持,则此方法将没有任何作用.
	 * 
	 * @param isBind
	 *            是否绑定
	 */
	void bindAudioSpectrum(boolean isBind);

	/**
	 * 获取媒体播放器对应的视频显示组件
	 * 
	 * @return 视频组件(节点)
	 */
	Node getMediaView();
}
