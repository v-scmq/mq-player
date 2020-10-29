package com.scmq.player.controller;

import com.scmq.player.app.Main;
import com.scmq.player.core.MediaPlayer;
import com.scmq.player.core.MediaPlayerListener;
import com.scmq.player.io.LyricReader;
import com.scmq.player.io.LyricWriter;
import com.scmq.player.model.LyricLine;
import com.scmq.player.model.MV;
import com.scmq.player.model.Media;
import com.scmq.player.model.Music;
import com.scmq.player.model.PlayList;
import com.scmq.player.model.PlayModel;
import com.scmq.player.net.NetSource;
import com.scmq.player.service.PlayListService;
import com.scmq.player.util.FileUtil;
import com.scmq.player.util.NavigationManager;
import com.scmq.player.util.NavigationManager.Navigation;
import com.scmq.player.util.StringUtil;
import com.scmq.player.util.Task;
import com.scmq.player.util.TimeUtil;
import com.scmq.player.view.MainView;
import com.scmq.view.control.EditText;
import com.scmq.view.control.TabPane;
import com.scmq.view.control.Toast;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.List;
import java.util.Random;

import static com.scmq.player.app.StageHandler.STAGE_HANDLER;

/**
 * 主模块控制器
 *
 * @author SCMQ
 */
@Controller
public final class MainController implements MediaPlayerListener, ChangeListener<PlayList> {
	/** 播放列表数据业务对象 */
	@Autowired
	private PlayListService playListService;
	/** 网络音乐搜索模块控制器 */
	@Autowired
	private NetSearchController searchController;
	/** 用户控制器 */
	@Autowired
	private UserController userController;
	/** 系统设定模块控制器 */
	@Autowired
	private SettingController settingController;
	@Autowired
	private ConfigureController configureController;

	/** 媒体播放器 */
	private MediaPlayer player;
	/** 需要控制的主界面视图 */
	private MainView view;

	/**
	 * 标记是否正在移动进度条,以此改变播放时间;<br>
	 * 在进度条被点击时,seek为true表示正在移动,否则当鼠标从进度条上释放时,seek为false
	 */
	private boolean seeking;

	private Image pauseImage, playImage;

	/** 音乐播放队列 */
	private ObservableList<Music> musicQueue;
	/** MV播放队列 */
	private ObservableList<MV> mvQueue;
	/** 播放索引 */
	private int index;
	/** 是否单曲循环 */
	private boolean singleLoop;

	/*------------- MediaPlayer事件监听器 回调方法  start -------------*/
	@Override
	public void finished() {
		System.out.println("finished...");
		// 若是单曲循环,则直接停止(停止完成后,重新继续播放)
		if (singleLoop = view.playModelProperty().get() == PlayModel.SINGLE_LOOP) {
			player.stop();
		} else {
			play(index(true));
		}
	}

	@Override
	public void statusChanged(Status status) {
		System.out.println("status=" + status);
		Toast.makeText(Main.getRoot(), "status=>" + status).show();
		// 播放器是否正在播放
		boolean playing = status == Status.PLAYING;
		// 重置音乐频谱
		view.resetSpectrum(playing);
		// 若正在播放,则设置为暂停图标,否则设置为播放图标
		view.getPlay().setImage(playing ? pauseImage : playImage);
		// 是否正在播放音乐,而不是MV
		playing = playing && mvQueue.isEmpty() && check(musicQueue, index);
		// 更新播放动图
		Main.mediaProperty().set(playing ? musicQueue.get(index) : null);
		// 若播放器已停止且当前是单曲循环模式,则重新播放当前媒体
		if (status == Status.STOPPED && singleLoop) {
			singleLoop = false;
			player.play();
		}
	}

	@Override
	public void positionChanged(float position) {
		if (!seeking) {
			view.getProgressSlider().setValue(position);
		}
	}

	@Override
	public void durationChanged(String duration) {
		view.getDurationLabel().setText(duration);
	}

	@Override
	public void mediaChanged(Media media) {
		view.getTitleLabel().setText(media.getTitle());
		view.getSingerLabel().setText(media.getSinger() == null ? "未知歌手" : media.getSinger().getName());
		ObservableList<LyricLine> lines = view.getLyricView().getItems();
		lines.clear();

		// 异步处理图片、歌词
		Task.async(() -> {
			Image image = media.getImageCover();
			Platform.runLater(() -> view.setAlbumImage(image));
			if (media.viewable()) {
				return;
			}
			String platform = media.getPlatform(), name = FileUtil.resolve(media.getFileName());
			File file = FileUtil.toFile(name, "lrc", "lyric", platform == null ? "0" : platform);
			// 首先检查本地是否有歌词文件
			if (file.isFile()) {
				// 读取歌词文件,获取歌词行列表
				List<LyricLine> list = new LyricReader().read(file);
				if (!list.isEmpty()) {
					// 同步到UI线程 ,添加歌词数据到视图
					Platform.runLater(() -> lines.addAll(list));
					return;
				}
			}

			NetSource netSource = configureController.getNetSourceImpl(platform);
			// 网络音乐平台
			if (netSource != null) {
				List<LyricLine> list = netSource.handleLyric((Music) media);
				if (list != null && !list.isEmpty()) {
					Platform.runLater(() -> lines.addAll(list));
					// 保存歌词到本地文件
					new LyricWriter(file).write(list);
				}
			}
		});
	}

	@Override
	public void error(String exception) {
		singleLoop = false;
		Toast.makeText(Main.getRoot(), exception).show();
		play(index(true));// 播放当前音乐发生错误,播放下一首
	}

	@Override
	public void audioSpectrumUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		view.update(magnitudes);
	}
	/*-------------------- MediaPlayer事件监听器 回调方法 end -----------------*/

	/* 播放器和所有配置加载完成,绑定事件.(包括异步加载播放列表) */
	void bind() {
		// 从容器中获取并移除视图对象
		this.view = Main.remove(MainView.class);
		// 从容器中获取播放器对象
		this.player = Main.get(MediaPlayer.class);

		playImage = view.getPlay().getImage();
		mvQueue = view.getMvQueueView().getItems();
		musicQueue = view.getMusicQueueView().getItems();
		pauseImage = FileUtil.createImage("player/pause");

		// 在子线程中,加载播放列表
		Task.async(() -> {
			PlayList playList = playListService.findPlayListLast();
			List<Music> items = playList == null ? null : playList.getList();
			if (items != null && !items.isEmpty()) {
				// 同步到UI线程中
				Platform.runLater(() -> {
					musicQueue.setAll(items);
					// 移除监听器,不触发播放列表改变事件,否则会自动播放
					Main.playListProperty().removeListener(this);
					// 设置新的播放列表
					Main.playListProperty().set(playList);
					// 重新添加监听器
					Main.playListProperty().addListener(this);
				});
			}
			// 获取热搜关键词
			// List<String> hotKeys = netSource.hotKeys();
			// if (hotKeys != null && !hotKeys.isEmpty()) {
			// String key = hotKeys.get(0);
			// hotKeys.clear();
			// Platform.runLater(() -> view.getSearchInput().setPromptText(key));
			// }
		});

		player.setVolume(0.5f);

		// 注册播放数据源改变事件
		Main.playListProperty().addListener(this);

		// 如果播放器支持音乐频谱图
		if (player.supportAudioSpectrum()) {
			BooleanProperty property = view.audioSpectrumUpdateProperty();
			// 注册音乐频谱回调
			property.addListener(((observable, oldValue, newValue) -> player.bindAudioSpectrum(newValue)));
		}

		// 关闭加载提示
		view.closeSpinner();
		// 初始化并关联后退和前进图标的事件
		NavigationManager.initialize(view.getBackNode(), view.getForwardNode());

		// 监听主选项卡面板的选项卡切换事件
		TabPane tabPane = (TabPane) Main.getRoot().lookup(".tab-pane:vertical");
		tabPane.setTabChangeListener((observable, oldTab, newTab) -> //
		NavigationManager.addToBack(new Navigation(oldTab, oldTab.getContent(), tabPane)));

		/* 为 窗口 注册 键盘 (按下 后 释放)事件 */
		Main.getPrimaryStage().addEventFilter(KeyEvent.KEY_RELEASED, e -> {
			switch (e.getCode()) {
			// 若是空格键 且 事件目标不是文本输入框
			case SPACE:
				if (!(e.getTarget() instanceof TextField)) {
					// 空格键控制播放//暂停
					playOrPause();
				}
				break;
			case UP:
				// 获取音量滑动条的场景图(所属弹出式窗口的场景图)
				Scene scene = view.getVolumeSlider().getScene();
				// 只有当音量滑动条没有显示的时候,才手动增加音量.因为滑动条也有KeyCode.UP事件
				if (scene == null || !scene.getWindow().isShowing()) {
					view.getVolumeSlider().increment();
				}
				break;
			case DOWN:
				scene = view.getVolumeSlider().getScene();
				if (scene == null || !scene.getWindow().isShowing()) {
					view.getVolumeSlider().decrement();
				}
				break;
			case F:
				if (view.isFullAble() && !(e.getTarget() instanceof TextField)) {
					view.getScreenOperNode().getOnMouseClicked().handle(null);
				}
				break;
			case ESCAPE:
				if (STAGE_HANDLER.isFullScreen()) {
					view.getScreenOperNode().getOnMouseClicked().handle(null);
				}
				break;
			default:
				break;
			}
		});

		// “播放/暂停” 鼠标事件
		view.getPlay().setOnMouseClicked(e -> playOrPause());

		// “上一首” 鼠标事件
		view.getPrev().setOnMouseClicked(e -> play(index(false)));

		// “下一首” 鼠标事件
		view.getNext().setOnMouseClicked(e -> play(index(true)));

		/* **** 播放进度滑动条轨道的鼠标事件 *********/
		Slider slider = view.getProgressSlider();
		Node track = slider.lookup(".track");
		// 当鼠标在滑动条的轨道上按下时,标记需要seek操作
		track.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> seeking = true);
		// 进行播放器seek操作,重置为不需要seek;
		track.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
			seeking = false;
			player.seek(slider.getValue());
		});

		/*---------------播放进度滑动条滑块的鼠标事件----------*/
		Node thumb = slider.lookup(".thumb");
		// 1.seek为true必须有滑块移动才能seek;->发起拖放,标记seek
		thumb.setOnDragDetected(e -> seeking = true);
		// 2.若需要seek,则进行播放器seek操作,重置为不需要seek;->鼠标释放,如果有发起拖放操作则拖放终止
		thumb.setOnMouseReleased(e -> {
			if (seeking) {
				player.seek(slider.getValue());
			}
			seeking = false;
		});

		/* 播放进度条值改变事件监听 */
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			// 计算当前时间的毫秒数
			long millis = (long) (newValue.floatValue() * player.getDuration());
			String time = TimeUtil.millisToTime(millis);
			view.getCurrentTimeLabel().setText(time);
			// 显示歌词
			scrollLyric(view.getLyricView(), millis);
		});

		// 音量滑动条值改变事件,设置播放器音量
		view.getVolumeSlider().valueProperty().addListener((observable, ov, value) -> //
		player.setVolume(value.floatValue()));

		// 倍速控制滑动条值改变事件,设置播放器播放速率
		view.getSpeedSlider().valueProperty().addListener((observable, ov, value) -> {
			Float rate = (Float) view.getSpeedSlider().getUserData();
			player.setRate(rate == null ? 1 : rate);
		});

		// 搜索框回车事件
		EditText editText = view.getSearchInput();
		editText.setOnAction(e -> {
			String text = editText.getText();
			if (StringUtil.isEmpty(text)) {
				if (StringUtil.isEmpty(text = editText.promptTextProperty().get())) {
					Toast.makeText(Main.getRoot(), "搜索内容不能为空！").show();
					return;
				}
				editText.textProperty().set(text);
			}

			// 准备显示搜索视图
			searchController.show(text);
		});

		// 绑定用户控制模块
		userController.bind(view.getHeadImageView(), view.getUserNameButton());

		// 绑定系统设定模块
		view.getSettingNode().setOnAction(e -> settingController.show());

		// 改变窗口全屏状态
		view.getScreenOperNode().setOnMouseClicked(e -> {
			// 若已全屏显示,则不全屏; 否则全屏显示
			boolean value = !STAGE_HANDLER.isFullScreen();
			// 设置专辑图片是否能被点击(true:透过自身到父布局面板)
			view.getAlbumImageView().setMouseTransparent(value);
			// 设置是否全屏
			STAGE_HANDLER.setFullScreen(value);
			// 播放详情页面全屏显示
			view.fullScreen(value);
		});

		// 清除播放队列所有媒体
		view.getClearPlayList().setOnMouseClicked(e -> {
			// 若当前显示的是MV播放队列
			if (view.getMvQueueView().getParent() != null) {
				if (!mvQueue.isEmpty()) {
					mvQueue.clear();
					// 显示音乐播放队列
					view.setDetailView(null);
				}
				return;
			}
			if (!musicQueue.isEmpty()) {
				musicQueue.clear();
				Task.async(() -> playListService.deletePlayMediaItem());
			}
		});

		/* -----“播放队列 列表视图”的单元格 鼠标悬浮时呈现的4个ImageView----- */
		// 播放图标
		ImageView play = FileUtil.createView("play-all", 24, 24);
		play.setId("play-current");
		// 收藏图标
		ImageView like = FileUtil.createView("like-red", 24, 24);
		like.setId("like-current");
		// 删除图标
		ImageView delete = FileUtil.createView("delete-white", 24, 24);
		delete.setId("delete-current");
		// 更多操作图标
		ImageView more = FileUtil.createView("more", 24, 24);
		more.setId("more-oper");

		HBox rightBox = new HBox(6, play, like, delete, more);
		rightBox.setAlignment(Pos.CENTER);
		BorderPane.setMargin(rightBox, new Insets(0, 0, 0, 4));

		// 播放列表中的播放图标
		play.setOnMouseClicked(e -> {
			Integer index = (Integer) rightBox.getUserData();
			mvQueue.clear();
			view.setDetailView(null);
			play(index == null ? -1 : index);
		});
		// 播放列表中的收藏图标
		like.setOnMouseClicked(e -> {
			Integer index = (Integer) rightBox.getUserData();
			// index = index == null ? -1 : index;
			System.out.println("like->" + index);
		});
		// 从播放队列删除某一首音乐
		delete.setOnMouseClicked(e -> {
			Integer data = (Integer) rightBox.getUserData();
			int index = data == null ? -1 : data;
			if (check(musicQueue, index)) {
				Integer id = musicQueue.get(index).getId();
				// 先获取待删除音乐id,然后从列表移除
				musicQueue.remove(index);
				Task.async(() -> playListService.deletePlayMediaItem(id));
			}
		});

		// 播放动图
		ImageView graphic = FileUtil.createGifView("player/wave", 20, true);
		// 默认4个字符容量的字符串构建器(减少内存占用)
		StringBuilder builder = new StringBuilder(4);
		// MV图片
		Image mvImage = FileUtil.createImage("mv");

		// 播放列表视图单元格
		class Cell extends ListCell<Music> implements EventHandler<MouseEvent>, ChangeListener<Object> {
			/** 单元格内容面板 */
			private BorderPane content;
			/** 序号(如果是正在播放的媒体项,则显示播放动图) */
			private Label num;
			/** 文件名(不含格式) */
			private Label fileName;
			/** MV图标 */
			private ImageView mvIcon;

			Cell() {
				// 鼠标悬浮改变事件
				hoverProperty().addListener(this);
				// 正在播放的媒体 改变(事件)
				Main.mediaProperty().addListener(this);
			}

			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				if (isEmpty()) {
					return;
				}
				if (newValue instanceof Boolean) {
					boolean hover = isHover();
					// fileName.setMaxWidth(300) | // fileName.setMaxWidth(430);
					content.setRight(hover ? rightBox : null);
					// 设置盒子的临时数据
					rightBox.setUserData(hover ? getIndex() : null);
					return;
				}
				Media media = getItem();
				if (media != null && (media.equals(oldValue) || media.equals(newValue))) {
					updateIndex(getIndex());
				}
			}

			@Override
			public void handle(MouseEvent event) {
				mvQueue.clear();
				Media item = getItem();
				int index = 0;
				for (Music music : musicQueue) {
					if (StringUtil.isNotEmpty(music.getVid())) {
						MV mv = new MV();
						mvQueue.add(mv);
						mv.setVid(music.getVid());
						mv.setTitle(music.getTitle());
						mv.setSinger(music.getSinger());
						mv.setFileName(music.getFileName());
						mv.setPlatform(music.getPlatform());
						if (item != null && item != music) {
							index++;
						} else {
							item = null;
						}
					}
				}
				play(index);
				view.setDetailView(player.getMediaView());
				view.getAlbumImageView().getOnMouseClicked().handle(null);
			}

			@Override
			public void updateIndex(int index) {
				super.updateIndex(index);
				if (isEmpty()) {
					setGraphic(null);
					return;
				}
				if (content == null) {
					num = new Label();
					num.setPrefWidth(30);
					num.setAlignment(Pos.CENTER_LEFT);
					BorderPane.setMargin(num, new Insets(0, 4, 0, 0));

					fileName = new Label();
					fileName.getStyleClass().add("name-label");
					HBox centerBox = new HBox(fileName);
					centerBox.setAlignment(Pos.CENTER_LEFT);
					fileName.setContentDisplay(ContentDisplay.RIGHT);

					mvIcon = new ImageView(mvImage);
					mvIcon.getStyleClass().add("mv-icon");
					mvIcon.setOnMouseClicked(this);
					mvIcon.setPreserveRatio(true);
					mvIcon.setPickOnBounds(true);
					mvIcon.setFitHeight(16);
					mvIcon.setSmooth(true);

					content = new BorderPane(centerBox);
					content.setLeft(num);
				}
				Music item = getItem();
				mvIcon.setMouseTransparent(false);
				fileName.setGraphic(StringUtil.isEmpty(item.getVid()) ? null : mvIcon);
				if (item == Main.mediaProperty().get()) {
					num.setGraphic(graphic);
					num.setText(null);
				} else {
					// 否则显示编号
					num.setGraphic(null);
					// 显示序号
					builder.delete(0, builder.length());
					num.setText(StringUtil.fillString(builder, ++index));
				}
				// 设置文件名(不包含格式)
				fileName.setText(item.getFileName());
				setGraphic(content);
			}

		}
		// 设置“播放列表视图” 的 “单元格工厂”
		view.getMusicQueueView().setCellFactory(listView -> new Cell());

		// MV播放队列 (ListView => ListCell)
		class MVCell extends ListCell<MV> implements EventHandler<MouseEvent> {
			private Label name;

			public MVCell() {
				addEventFilter(MouseEvent.MOUSE_PRESSED, this);
			}

			public void updateIndex(int index) {
				super.updateIndex(index);
				if (isEmpty()) {
					setGraphic(null);
					setText(null);
					return;
				}
				if (name == null) {
					name = new Label();
				}
				name.setText(getItem().getFileName());
				builder.delete(0, builder.length());
				setText(StringUtil.fillString(builder, ++index));
				setGraphic(name);
			}

			@Override
			public void handle(MouseEvent event) {
				event.consume();
				if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
					play(getIndex());
				}
			}
		}
		view.getMvQueueView().setCellFactory(listView -> new MVCell());

	}

	/** 处理播放列表改变 */
	@Override
	public void changed(ObservableValue<? extends PlayList> observable, PlayList oldValue, PlayList newValue) {
		if (newValue == null) {
			return;
		}

		List<MV> mvList = newValue.getMvList();
		if (mvList != null && !mvList.isEmpty()) {
			// 显示MV播放详情页面
			view.setDetailView(player.getMediaView());
			mvQueue.setAll(mvList);
			play(newValue.getIndex());
			// 显示视频画面
			view.getAlbumImageView().getOnMouseClicked().handle(null);
			return;
		}

		// 清除MV播放队列,准备生成音乐播放队列
		mvQueue.clear();
		view.setDetailView(null);
		List<Music> list = newValue.getList();
		if (list == null || list.isEmpty()) {
			return;
		}
		// 是否改变了播放列表
		boolean changed = false;
		if (musicQueue.size() != list.size()) {
			changed = true;
		} else {
			int index = 0;
			for (Music music : musicQueue) {
				if (changed = !music.equals(list.get(index++))) {
					break;
				}
			}
		}
		// 播放数据源不同
		if (changed) {
			musicQueue.setAll(list);
			// 保存到数据库中
			Task.async(() -> {
				playListService.deletePlayMediaItem();
				playListService.savePlayList(newValue);
			});
		}
		int newIndex = newValue.getIndex();
		if (changed || newIndex != this.index) {
			play(newIndex);
			return;
		}
		// 如果之前播放的位置索引和新的位置索引相等
		// 如果播放器未就绪,则就绪播放器
		if (!player.isPlayable()) {
			play(newIndex);
			return;
		}
		// 若播放器未播放,则播放
		if (!player.isPlaying()) {
			player.play();
		}
	}

	/**
	 * 通过播放数据源对象播放媒体
	 *
	 * @param index
	 *            播放索引
	 */
	private void play(int index) {
		// 停止当前播放的媒体
		System.out.println("will be stopped...");
		player.stop();
		if (index == -1) {
			Toast.makeText(Main.getRoot(), "没有播放数据源，请选择一个播放源！").show();
			return;
		}
		// 若index==-2,则是列表播放模式,且列表播放已完成,不播放下一首,
		// 可以不处理,因为index==-2在下面的执行中无法通过
		this.index = index;
		List<? extends Media> playQueue = mvQueue.isEmpty() ? musicQueue : mvQueue;
		// 若播放索引在正常范围内,则准备播放媒体
		if (check(playQueue, index) && prepare(playQueue.get(index))) {
			// 延迟执行,避免由于 播放器停止 在 发出播放请求之后执行 而处于停止状态(这通常发生在同一个媒体文件)
			System.out.println("check then prepared");
			player.play();
			System.out.println("prepared then playing");
		}
		// 选择正在播放的索引对应的列表单元格
		if (playQueue == mvQueue) {
			view.getMvQueueView().getSelectionModel().select(index);
		}
	}

	/** 播放或暂停 */
	private void playOrPause() {
		// 若播放器是可播放的
		if (player.isPlayable()) {
			System.out.println("available");
			// 如果正在播放,则暂停
			if (player.isPlaying()) {
				System.out.println("playing , but will be paused");
				player.pause();
			} else {
				System.out.println("not playing , but will be playing");
				// 否则开始播放
				player.play();
			}
			return;
		}
		System.out.println("not available, will be replay");
		List<? extends Media> playQueue = mvQueue.isEmpty() ? musicQueue : mvQueue;
		// 若播放器不可播放,则准备新的媒体
		if (playQueue.isEmpty()) {
			Toast.makeText(Main.getRoot(), "没有播放数据源，请选择一个播放源！").show();
			return;
		}
		// 若播放索引合法,则开始准备播放
		if (check(playQueue, index) && prepare(playQueue.get(index))) {
			player.play();
		}
	}

	/**
	 * 准备播放媒体(音乐或MV)
	 *
	 * @param media
	 *            新的媒体资源
	 * @return 若播放器准备就绪, 则返回true
	 */
	private boolean prepare(Media media) {
		// 若是网络音乐平台资源 且 没有播放地址,先处理播放地址
		if (media.getPlatform() != null && StringUtil.isEmpty(media.getPath())) {
			NetSource netSource = configureController.getNetSourceImpl(media.getPlatform());
			if (netSource == null) {
				return false;
			}
			if (media.viewable()) {
				netSource.handleMVInfo((MV) media);
			} else {
				netSource.handleMusicInfo((Music) media);
			}
		}
		if (StringUtil.isEmpty(media.getPath())) {
			Toast.makeText(Main.getRoot(), "这个媒体没有播放地址").show();
			return false;
		}
		System.out.println("prepared - media ->");
		return player.prepareMedia(media);
	}

	/**
	 * 滚动ListView单元格,确保当前时间对应的歌词内容在可见范围内,并选中这一行单元格
	 *
	 * @param view
	 *            歌词列表视图
	 * @param timeMillis
	 *            歌曲当前播放时间(毫秒)
	 */
	private void scrollLyric(ListView<LyricLine> view, long timeMillis) {
		// 1.确保在List集合按照时间先后顺序排序(已在歌词解析器中排序)
		// 2.在区间[0~size-1]内 -> millis < index:millis
		// millis 所成立的条件当中,对应的index即是所找歌词索引位置
		List<LyricLine> list = view.getItems();
		if (list.size() > 0) {
			int index = -1, max = list.size() - 1;
			for (; index < max; index++) {
				LyricLine line = list.get(index + 1);
				if (timeMillis < line.getMillis()) {
					break;
				}
			}
			// 可见单元格数量除以2
			int count = (int) (view.getHeight() / view.getFixedCellSize()) >> 1;
			// 滚动会让指定索引的单元格在ListView的可视区域的第一个单元格
			view.scrollTo(index < count ? 0 : index - count);
			view.getSelectionModel().select(index);
		}
	}

	/**
	 * 通过播放模式,生成播放索引
	 *
	 * @param next
	 *            若为true,则生成下一个播放索引;否则生成上一个播放索引
	 * @return 新的播放索引, 若返回{@code -1},则表示没有播放数据源
	 */
	private int index(boolean next) {
		List<? extends Media> playQueue = mvQueue.isEmpty() ? musicQueue : mvQueue;
		if (playQueue.isEmpty()) {
			return -1;
		}
		int index = this.index, size = playQueue.size();
		if (view.playModelProperty().get() == null) {
			return index;
		}
		switch (view.playModelProperty().get()) {
		case LIST_LOOP: {
			return next ? (++index >= size ? 0 : index) : (--index < 0 ? --size : index);
		}
		case SINGLE_LOOP: {
			return index < 0 ? 0 : index;
		}
		case ORDER_PLAY: {
			// 若生成下一个索引,则直接增加;
			// 若生成上一个且生成的索引小于0时返回不等于-1且小于0的整数,表示列表播放完毕
			return next ? (++index) : (--index < 0 ? -2 : index);
		}
		case RANDOM_PLAY: {
			return new Random().nextInt(--size);
		}
		default:
			return -1;
		}
	}

	/**
	 * 检查位置索引是否正常
	 *
	 * @param list
	 *            任意List集合
	 * @param index
	 *            索引
	 * @param <T>
	 *            List集合泛型参数
	 * @return 若索引在List集合的[0, size)区间,则返回true.
	 */
	private <T> boolean check(java.util.List<T> list, int index) {
		return index >= 0 && index < list.size();
	}
}