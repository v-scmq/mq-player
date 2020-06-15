package com.scmq.player.core;

import com.scmq.player.model.Media;

import javafx.scene.media.MediaPlayer.Status;

/**
 * 媒体播放器事件监听器
 *
 * @author SCMQ
 */
public interface MediaPlayerListener {
	/** 播放完成时回调 */
	void finished();

	/**
	 * 播放器状态改变
	 *
	 * @param status
	 *            播放器状态
	 * @see javafx.scene.media.MediaPlayer.Status
	 */
	void statusChanged(Status status);

	/**
	 * 播放位置改变
	 *
	 * @param position
	 *            0~1
	 */
	void positionChanged(float position);

	/**
	 * 媒体播放器播放时长改变
	 *
	 * @param duration
	 *            时长(已格式化为标准时间字符串)
	 */
	void durationChanged(String duration);

	/**
	 * 媒体资源改变回调
	 *
	 * @param media
	 *            新的媒体资源
	 */
	void mediaChanged(Media media);

	/**
	 * 播放时发生错误的回调方法
	 *
	 * @param exception
	 *            异常消息
	 */
	void error(String exception);

	/**
	 * 音乐频谱数据更新回调
	 *
	 * @param timestamp
	 *            时间戳
	 * @param duration
	 *            时长
	 * @param magnitudes
	 *            包含每个频带的分贝（dB）非正谱幅度的阵列
	 * @param phases
	 *            包含每个频带范围内的相位[Math.PI，Math.PI]
	 */
	void audioSpectrumUpdate(double timestamp, double duration, float[] magnitudes, float[] phases);
}
