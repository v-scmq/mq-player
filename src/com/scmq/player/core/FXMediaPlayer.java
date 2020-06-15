package com.scmq.player.core;

import com.scmq.player.model.Media;
import com.scmq.player.util.TimeUtil;
import com.sun.media.jfxmediaimpl.HostUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.lang.ref.WeakReference;

public final class FXMediaPlayer implements com.scmq.player.core.MediaPlayer, ChangeListener<Status> {
	/** 媒体播放器 */
	private MediaPlayer player;
	/** 播放器事件监听器 */
	private MediaPlayerListener listener;
	/** 音乐频谱监听器 */
	private AudioSpectrumListener audioSpectrumListener;
	/* 获取当前操作系统是否为windows平台 */
	private boolean windows = HostUtils.isWindows();
	/** 视频视图 */
	private MediaView mediaView;
	/** 视频视图的父布局面板 */
	private BorderPane box;

	/** 播放器音量 */
	private float volume;
	/** 媒体文件时长(毫秒) */
	private long durationMills;
	/** 播放速率 0.5~2.0 默认为1倍速 */
	private float rate = 1.0F;
	/** 媒体资源 */
	private Media media;

	/**
	 * 构造一个JavaFX媒体播放器
	 * 
	 * @param listener
	 *            播放器监听器
	 */
	public FXMediaPlayer(MediaPlayerListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("listener can not null");
		this.listener = listener;
	}

	@Override
	public void play() {
		if (player != null) {
			player.play();
		}
	}

	@Override
	public void pause() {
		if (player != null) {
			player.pause();
		}
	}

	@Override
	public void stop() {
		if (player != null) {
			player.stop();
		}
	}

	@Override
	public void release() {
		MediaPlayer oldValue = player;
		durationMills = 0;
		player = null;
		media = null;
		// 取消视频视图和播放器的关联
		if (mediaView != null) {
			mediaView.setMediaPlayer(null);
		}
		if (oldValue != null) {
			oldValue.setOnEndOfMedia(null);
			oldValue.statusProperty().removeListener(this);
			oldValue.setAudioSpectrumListener(null);
			oldValue.setOnError(null);
			oldValue.dispose();
		}
	}

	@Override
	public void seek(double value) {
		if (player != null) {
			player.seek(new Duration(value * durationMills));
		}
	}

	@Override
	public void seek(long position) {
		if (player != null) {
			player.seek(new Duration(position));
		}
	}

	@Override
	public void setRate(float rate) {
		this.rate = rate;
		if (player != null) {
			player.setRate(rate);
		}
	}

	@Override
	public long getDuration() {
		return durationMills;
	}

	@Override
	public long getTime() {
		return player == null ? 0 : (long) player.getCurrentTime().toMillis();
	}

	@Override
	public void setVolume(float volume) {
		this.volume = volume;
		if (player != null) {
			player.setVolume(volume);
		}
	}

	@Override
	public float getVolume() {
		return volume;
	}

	@Override
	public boolean prepareMedia(Media media) {
		if (media.equals(this.media)) {
			return isPlayable();
		}
		release();
		this.media = media;
		String path = media.getPath();
		if (path == null || path.length() < 2) {
			listener.error("这不是一个标准的路径！");
			return false;
		}
		// windows => (path = "D:\music\... .mp3") ; linux | mac => (path = "/media/... .mp3")
		if (windows ? path.charAt(1) == ':' : path.charAt(0) == '/') {
			File file = new File(path);
			if (!file.exists()) {
				listener.error("媒体文件不存在！");
				return false;
			}
			path = file.toURI().toString();
		}
		try {
			createPlayer(new javafx.scene.media.Media(path));
			listener.mediaChanged(media);
			// 媒体视图组件关联播放器
			if (mediaView != null && media.viewable()) {
				mediaView.setMediaPlayer(player);
			}
			return true;
		} catch (javafx.scene.media.MediaException e) {
			boolean flag = e.getType() == MediaException.Type.MEDIA_UNSUPPORTED;
			listener.error(flag ? "不支持的媒体格式！" : "媒体错误！");
			return false;
		} catch (Exception e) {
			listener.error("未知错误！");
			return false;
		}
	}

	@Override
	public boolean isPlayable() {
		return player != null && player.getStatus() != Status.DISPOSED;
	}

	@Override
	public boolean isPlaying() {
		return player != null && player.getStatus() == Status.PLAYING;
	}

	@Override
	public boolean isPause() {
		return player != null && player.getStatus() == Status.PAUSED;
	}

	@Override
	public boolean supportAudioSpectrum() {
		return true;
	}

	@Override
	public void registerAudioSpectrum() {
		if (audioSpectrumListener == null) {
			audioSpectrumListener = listener::audioSpectrumUpdate;

		}
		if (player != null) {
			player.setAudioSpectrumListener(audioSpectrumListener);
		}
	}

	@Override
	public void resume(float volume, float progress) {
		this.volume = volume;
		if (player == null) {
			return;
		}

		// 媒体播放器就绪事件监听器,通过此监听器可以知道媒体已经准备就绪,这个时候才能调整播放器播放时间
		Runnable run = () -> {
			// 设置就绪任务为null
			player.setOnReady(null);
			// 更改播放器播放时间位置
			seek(progress);
			// 更改播放器进度属性的值
			listener.positionChanged(progress);
			player.setVolume(volume);
		};

		player.setOnReady(new WeakReference<>(run).get());
	}

	@Override
	public void changed(ObservableValue<? extends Status> observable, Status oldValue, Status newValue) {
		// Status.STALLED 当数据进入缓冲区的速度减慢或停止并且播放缓冲区没有足够的数据来继续播放时播放机的状态。
		// 当缓冲足够的数据以恢复播放时，播放将自动继续。 如果在此状态下暂停或停止，则缓冲将继续，但如果缓冲了足够的数据，则播放将不会自动恢复。
		listener.statusChanged(newValue);
	}

	private void createPlayer(javafx.scene.media.Media media) {
		player = new MediaPlayer(media);
		player.setVolume(volume);
		player.setRate(rate);

		if (audioSpectrumListener != null && !this.media.viewable()) {
			player.setAudioSpectrumListener(audioSpectrumListener);
		}
		// player.bufferProgressTimeProperty().addListener((observable,oldValue, newValue) -> {});

		// 播放器当前时间改变事件监听
		player.currentTimeProperty().addListener((observable, oldValue, newValue) -> //
		listener.positionChanged((float) (newValue.toMillis() / durationMills)));

		// 播放器总时长改变事件监听
		player.totalDurationProperty().addListener((observable, oldValue, newValue) -> {
			durationMills = (long) newValue.toMillis();
			listener.durationChanged(TimeUtil.millisToTime(durationMills));
		});

		// 播放状态回调
		player.statusProperty().addListener(this);
		// 若是单曲循环,则取消播放完成时的回调
		player.setOnEndOfMedia(listener::finished);
		// 播放器播放完成一个媒体资源时回调
		player.setOnError(() -> listener.error("发生了错误！"));

		// 音频频谱中的频带数。默认值为128；最小值为2。音频信号的频率范围将被划分为指定数量的频箱。
		// 例如，一个典型的数字音乐信号的频率范围是[0.0，22050]赫兹。如果在这种情况下，谱带的数目设置为10，
		// 则频谱中每个频点的宽度将为2205Hz，最低频点的下限等于0.0。
		// player.setAudioSpectrumNumBands(128);

		// 频谱更新之间的间隔（秒）。默认值为0.1秒。
		// player.setAudioSpectrumInterval(0.1);

		// 灵敏度阈值（以分贝为单位）；必须为非正值。对于给定谱带中的峰值频率，低于此阈值的值将设置为阈值的值。默认值为-60 db。
		// player.setAudioSpectrumThreshold(-60);

		// time stamp:事件的时间戳(秒)
		// duration:计算频谱的持续时间(秒)
		// magnitudes:包含每个波段非正频谱量级（分贝）的阵列。数组的大小等于带区数，应视为只读。
		// phases(相位):数组，包含每个波段范围[math.pi,math.pi]内的相位。数组的大小等于带区数，应视为只读。

		// player.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {
		// // 将频谱阈值由负值转换为对应的正值
		// audioSpectrumView.update(magnitudes, ~player.getAudioSpectrumThreshold() + 1);
		// });
	}

	@Override
	public Node getMediaView() {
		if (box == null) {
			box = new BorderPane(mediaView = new MediaView());
			box.getStyleClass().add("media-view");
			mediaView.fitWidthProperty().bind(box.widthProperty());
			mediaView.fitHeightProperty().bind(box.heightProperty());
			mediaView.setPreserveRatio(true);
			if (player != null) {
				mediaView.setMediaPlayer(player);
			}
		}
		return box;
	}
}
