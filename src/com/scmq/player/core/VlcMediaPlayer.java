package com.scmq.player.core;

import com.scmq.player.model.Media;
import com.scmq.player.util.TimeUtil;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Screen;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public class VlcMediaPlayer implements com.scmq.player.core.MediaPlayer {
	/** 媒体文件时长(毫秒) */
	private long durationMills;
	/** 媒体播放器 */
	private MediaPlayer player;
	/** 媒体视图 */
	private MediaView mediaView;
	/** 媒体播放器监听器 */
	private final MediaPlayerListener listener;

	public VlcMediaPlayer(MediaPlayerListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("listener can not null");
		this.listener = listener;
		createNativePlayer();
	}

	/** 创建本地播放器 */
	private void createNativePlayer() {
		MediaPlayerFactory factory = new MediaPlayerFactory();
		player = factory.mediaPlayers().newMediaPlayer();
		factory.release();

		/* 媒体播放器事件监听器 */
		player.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void lengthChanged(MediaPlayer mediaPlayer, long length) {
				String time = TimeUtil.millisToTime(length);
				Platform.runLater(() -> {
					durationMills = length;
					listener.durationChanged(time);
				});
			}

			@Override
			public void finished(MediaPlayer mediaPlayer) {
				// finished方法在stop方法之前回调
				Platform.runLater(listener::finished);
			}

			@Override
			public void positionChanged(MediaPlayer mediaPlayer, float position) {
				Platform.runLater(() -> listener.positionChanged(position));
			}

			@Override
			public void playing(MediaPlayer mediaPlayer) {
				Platform.runLater(() -> listener.statusChanged(Status.PLAYING));
			}

			@Override
			public void paused(MediaPlayer mediaPlayer) {
				Platform.runLater(() -> listener.statusChanged(Status.PAUSED));
			}

			@Override
			public void stopped(MediaPlayer mediaPlayer) {
				Platform.runLater(() -> listener.statusChanged(Status.STOPPED));
			}

			@Override
			public void error(MediaPlayer mediaPlayer) {
				Platform.runLater(() -> listener.error("发生了未知错误！"));
			}
		});
	}

	@Override
	public void play() {
		if (!player.status().isPlaying()) {
			player.controls().play();
		}
	}

	@Override
	public void pause() {
		if (player.status().isPlaying()) {
			player.controls().pause();
		}
	}

	@Override
	public void stop() {
		player.controls().stop();
	}

	@Override
	public void release() {
		player.release();
	}

	@Override
	public void seek(double value) {
		player.controls().setTime((long) (durationMills * value));
	}

	@Override
	public void seek(long position) {
		player.controls().setTime(position);
	}

	@Override
	public void setRate(float rate) {
		player.controls().setRate(rate);
	}

	@Override
	public long getDuration() {
		return durationMills;
	}

	@Override
	public long getTime() {
		return player.status().time();
	}

	@Override
	public void setVolume(float volume) {
		player.audio().setVolume((int) (volume * 100));
	}

	@Override
	public float getVolume() {
		return player.audio().volume();
	}

	@Override
	public boolean isPlayable() {
		return player.status().isPlayable();
	}

	@Override
	public boolean isPlaying() {
		return player.status().isPlaying();
	}

	@Override
	public boolean isPause() {
		return !player.status().isPlaying();
	}

	@Override
	public boolean supportAudioSpectrum() {
		return false;
	}

	@Override
	public void bindAudioSpectrum(boolean bind) {
	}

	@Override
	public boolean prepareMedia(Media media) {
		listener.mediaChanged(media);
		return player.media().prepare(media.getPath());
	}

	@Override
	public void resume(float volume, float value) {
		// 本地播放器事件监听器
		MediaPlayerEventListener listener = new MediaPlayerEventAdapter() {
			@Override
			public void mediaPlayerReady(MediaPlayer mediaPlayer) {
				Platform.runLater(() -> {
					player.events().removeMediaPlayerEventListener(this);
					// 更改播放器播放时间位置
					seek(value);
					// 暂停
					player.controls().setPause(true);
					// 通知播放器进度已被改变
					VlcMediaPlayer.this.listener.positionChanged(value);
					setVolume(volume);
				});
			}
		};

		WeakReference<MediaPlayerEventListener> reference = new WeakReference<>(listener);

		player.events().addMediaPlayerEventListener(reference.get());
		// 必须先播放,媒体播放器才能就绪
		player.controls().play();
	}

	@Override
	public Node getMediaView() {
		if (mediaView == null) {
			mediaView = new MediaView();
		}
		return mediaView;
	}

	class MediaView extends Pane implements BufferFormatCallback, RenderCallback {
		/** 图像展示组件 */
		private ImageView imageView;
		/** 像素写入器 */
		private PixelWriter pixelWriter;
		/** 可写入像素格式,ByteBuff(字节缓冲)作为像素来源 */
		private WritablePixelFormat<ByteBuffer> pixelFormat;
		/** 视频资源的宽高(浮点数)比例属性 */
		private FloatProperty videoRatioProperty;

		private int maxWidth, maxHeight;

		public MediaView() {
			// 获得设备屏幕的矩形对象
			Rectangle2D rectangle = Screen.getPrimary().getBounds();
			maxWidth = (int) rectangle.getWidth();
			maxHeight = (int) rectangle.getHeight();
			// 可写入图像(Image的子类对象)初始化可写入图像的对象(Image的子类对象)
			WritableImage image = new WritableImage(maxWidth, maxHeight);
			// 通过可写入的图像来初始化图像展示组件,视频图像展示在ImageView组件上
			imageView = new ImageView(image);
			// 初始化像素写入器
			pixelWriter = image.getPixelWriter();
			// 初始化可写入像素格式,ByteBuff(字节缓冲)作为像素来源
			pixelFormat = PixelFormat.getByteBgraPreInstance();
			// 构造 视频宽高(浮点数)比例属性,初始为0.1
			videoRatioProperty = new SimpleFloatProperty(0.1f);

			getStyleClass().add("media-view");
			// 将图像展示组件添加到面板中
			getChildren().add(imageView);

			// 面板宽度改变事件的监听
			widthProperty().addListener(
					(observable, oldWidth, newWidth) -> adapt(newWidth.floatValue(), (float) getHeight()));
			// 面板高度属性改变事件的监听
			heightProperty().addListener(
					(observable, oldHeight, newHeight) -> adapt((float) getWidth(), newHeight.floatValue()));

			// 视频资源宽高属性比例值改变事件的监听
			videoRatioProperty.addListener(
					(observable, oldRatio, newRatio) -> adapt((float) getWidth(), (float) getHeight()));

			// MediaPlayerFactory factory = new MediaPlayerFactory();
			// videoSurface = factory.videoSurfaces().newVideoSurface(this,
			// this, true);
			// factory.release();
			CallbackVideoSurface videoSurface = new CallbackVideoSurface(this, this, true,
					VideoSurfaceAdapters.getVideoSurfaceAdapter());
			// 设置播放器的用户数据 为"回调类型视频表面" 对象->避免该对象被回收,保存到播放器对象引用中
			player.userData(videoSurface);
			// 连接"回调类型视频表面"到 本地播放器,即 本地播放器 与"回调类型视频表面"进行关联
			videoSurface.attach(player);
		}

		/**
		 * 通过视频资源的宽高比例值来计算图像显示组件的尺寸大小,以此来适配图像显示组件在面板(this)中的大小和位置
		 *
		 * @param width
		 *            面板的宽度
		 * @param height
		 *            面板的高度
		 */
		private void adapt(float width, float height) {
			// 计算图像展示组件的高度
			float fitHeight = width / videoRatioProperty.get();
			// 如果计算得到的高度值小于等于面板高度值,则以面板宽度适配成功
			if (fitHeight <= height) {
				// 将图像展示组件的宽度设置为面板宽度
				imageView.setFitWidth(width);
				// 将图像展示组件的高度设置为计算得到的适配高度
				imageView.setFitHeight(fitHeight);
				// 以面板宽度适配,在X方向上,图像展示组件的X坐标为0,表示填充整个面板宽度
				imageView.setX(0);
				// 以面板宽度适配,在Y方向上,图像展示组件的Y坐标为高度只差除以2
				imageView.setY((height - fitHeight) / 2);
			}
			// 如果计算得到的高度值大于面板高度,则以面板高度适配
			else {
				// 计算图像展示组件的宽度
				float fitWidth = height * videoRatioProperty.get();
				// 设置图像展示组件到宽度
				imageView.setFitWidth(fitWidth);
				// 将图像展示组件的高度设置为面板高度
				imageView.setFitHeight(height);
				// 以面板高度适配,在X方向上,图像展示组件的X坐标为宽度值之差除以2
				imageView.setX((width - fitWidth) / 2);
				// 以面板高度适配,在Y方向上,图像展示组件的Y坐标为0,表示填充整个高度
				imageView.setY(0);
			}
		}

		/** 默认的视频缓冲格式回调,这将控制视频在图像组件显示的尺寸大小,并返回视频缓冲格式的对象 */
		@Override
		public final BufferFormat getBufferFormat(int width, int height) {
			// 在JavaFX的UI线程中设置视频资源宽高比例属性的比例值
			// 注意必须将宽度或高度其中之一转换为float类型才进行除法运算,否则比例误差过大
			Platform.runLater(() -> videoRatioProperty.set((float) width / height));
			int[] pitches = { maxWidth << 2 }, lines = { maxHeight };
			return new BufferFormat("RV32", maxWidth, maxHeight, pitches, lines);
		}

		/**
		 * 重写display方法,以此在展示视频在图像组件上
		 * 
		 * @param mediaPlayer
		 *            Vlc本地播放器
		 * @param buffers
		 *            视频数据,字节缓冲数组
		 * @param format
		 *            缓冲格式
		 */
		@Override
		public final void display(MediaPlayer mediaPlayer, ByteBuffer[] buffers, BufferFormat format) {
			Platform.runLater(() -> pixelWriter.setPixels(0, 0, format.getWidth(), format.getHeight(), pixelFormat,
					buffers[0], format.getPitches()[0]));
		}

		@Override
		public void allocatedBuffers(ByteBuffer[] buffers) {
			System.out.println("==============================");
			for (ByteBuffer buffer : buffers) {
				System.out.println(buffer);
			}
		}
	}
}