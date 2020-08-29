
package com.scmq.player.view;

import com.scmq.player.app.Main;
import com.scmq.player.app.StageHandler;
import com.scmq.player.model.LyricLine;
import com.scmq.player.model.MV;
import com.scmq.player.model.Media;
import com.scmq.player.model.Music;
import com.scmq.player.model.PlayModel;
import com.scmq.player.util.FileUtil;
import com.scmq.view.control.EditText;
import com.scmq.view.control.PopupPane;
import com.scmq.view.control.Spinner;
import com.scmq.view.control.Tab;
import com.scmq.view.control.TabPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Set;

public class MainView {
	// 主界面顶部
	private ImageView headImageView;
	private Button userNameButton;

	private Button backButton;
	private Button forwardButton;
	private Button refreshButton;
	private EditText searchInput;

	private Button headsetButton;
	private Button skinButton;
	private Button settingButton;

	/** 进度旋转器(提示正在加载) */
	private Spinner spinner;

	// 主界面底部
	private ImageView albumImageView;
	private ImageView prev;
	private ImageView play;
	private ImageView next;
	private Label singerLabel;
	private Label titleLabel;
	private Label currentTimeLabel;
	private Label durationLabel;
	private Slider progressSlider;
	private BorderPane bottomPane;

	private ImageView like;
	private Slider volumeSlider;
	private Slider speedSlider;
	/** 播放模式属性 */
	private ObjectProperty<PlayModel> playModelProperty;
	private ImageView download;

	/** 清除播放队列 */
	private ImageView clearPlayList;
	/** 音乐播放队列视图 */
	private ListView<Music> musicQueueView;
	/** MV播放队列视图 */
	private ListView<MV> mvQueueView;

	/** 播放列表标题属性 */
	private StringProperty playQueueTitleProperty;

	/** 播放详情页 */
	private AnchorPane detailBox;
	private ImageView effectView;
	private ListView<LyricLine> lyricView;
	private AudioSpectrumView audioSpectrumView;
	private SVGPath fullScreenNode;

	/** 音乐频谱可更新属性 */
	private BooleanProperty audioSpectrumUpdateProperty;

	// 全屏图标
	private static final String fullIcon = "M23 25h-4v-2h2.63l-5.753-5.658 1.354-1.331 5.769 5.674v-2.685h2v6h-2zm0-15.669l-5.658 5.658-1.331-1.331 5.658-5.658h-2.669v-2h6v6h-2v-2.669zm-15.027 15.669h4.027v-2h-2.676l5.676-5.658-1.335-1.331-5.692 5.674v-2.685h-1.973v6h1.973zm0-15.669l5.581 5.658 1.313-1.331-5.582-5.658h2.715v-2h-6v6h1.973v-2.669z";
	// 退出全屏图标
	private static final String exitIcon = "M22 12v2h-6v-6h2v2.669l5.658-5.658 1.331 1.331-5.658 5.658h2.669zm-10 7.331l-5.658 5.658-1.331-1.331 5.658-5.658h-2.669v-2h6v6h-2v-2.669zm-4-7.331h2.669l-5.658-5.658 1.331-1.331 5.658 5.658v-2.669h2v6h-6v-2zm14 6h-2.669l5.658 5.658-1.331 1.331-5.658-5.658v2.669h-2v-6h6v2z";

	public MainView(LocalMusicView localModuleView, NetMusicView netView) {
		// ---------用户登录部分-----------
		int size = 50;
		headImageView = FileUtil.createView("user_head", size, size);
		headImageView.getStyleClass().add("head-view");
		Circle circle = new Circle(size >>= 1, size, size);
		headImageView.setClip(circle);

		// 左边布局面板的宽度(放置头像部分的HBox、顶部布局锚面板的左边约束、中央TabPane的选项卡布局面板VBox 在使用)
		size = 200;
		userNameButton = new Button("点击登录");
		HBox box = new HBox(headImageView, userNameButton);
		box.getStyleClass().add("head-img-pane");
		box.setAlignment(Pos.CENTER_LEFT);
		box.setPrefSize(size, 60);

		// ---------------顶部面板部分----------
		SVGPath backPath = new SVGPath();
		backPath.setContent("M8 0 L0 8 L8 16");
		backPath.setStrokeWidth(1.5);
		backButton = new Button(null, backPath);
		backButton.getStyleClass().setAll("svg-button");
		backButton.setDisable(true);

		SVGPath forwardPath = new SVGPath();
		forwardPath.setContent("M8 0 L16 8 L8 16");
		forwardPath.setStroke(Color.rgb(34, 34, 34));
		forwardPath.setStrokeWidth(1.5);
		forwardButton = new Button(null, forwardPath);
		forwardButton.getStyleClass().setAll("svg-button");
		forwardButton.setDisable(true);

		SVGPath refreshPath = new SVGPath();
		refreshPath.setContent(
				"M8 3a5 5 0 1 0 4.546 2.914.5.5 0 0 1 .908-.417A6 6 0 1 1 8 2v1z M8 4.466V.534a.25.25 0 0 1 .41-.192l2.36 1.966c.12.1.12.284 0 .384L8.41 4.658A.25.25 0 0 1 8 4.466z");
		refreshPath.setStrokeWidth(0.65);
		refreshPath.setRotate(20);
		refreshButton = new Button(null, refreshPath);
		refreshButton.getStyleClass().setAll("svg-button");

		searchInput = new EditText(null, FileUtil.createView("search", 20, 20), true);
		searchInput.setPromptText("九张机");
		searchInput.setPrefWidth(300);
		searchInput.setMaxHeight(30);
		searchInput.setFocusTraversable(false);

		HBox hBox = new HBox(20, backButton, forwardButton, refreshButton, searchInput);
		hBox.setAlignment(Pos.CENTER_LEFT);

		SVGPath headsetPath = new SVGPath();
		headsetPath.setContent(
				"M8 3a5 5 0 0 0-5 5v4.5H2V8a6 6 0 1 1 12 0v4.5h-1V8a5 5 0 0 0-5-5z M11 10a1 1 0 0 1 1-1h2v4a1 1 0 0 1-1 1h-1a1 1 0 0 1-1-1v-3zm-6 0a1 1 0 0 0-1-1H2v4a1 1 0 0 0 1 1h1a1 1 0 0 0 1-1v-3z");
		headsetPath.setScaleX(1.2);
		headsetPath.setScaleY(1.2);
		headsetPath.setFillRule(FillRule.EVEN_ODD);
		headsetButton = new Button(null, headsetPath);
		headsetButton.getStyleClass().setAll("svg-button");

		SVGPath skinPath = new SVGPath();
		skinPath.setManaged(false);
		skinPath.setContent(
				"M772.8 96v64L936 321.6l-91.2 91.2c-12.8-11.2-27.2-16-43.2-16-36.8 0-65.6 28.8-65.6 65.6V800c0 35.2-28.8 64-64 64H352c-35.2 0-64-28.8-64-64V462.4c0-36.8-28.8-65.6-65.6-65.6-16 0-32 6.4-43.2 16L88 321.6 249.6 160h40l1.6 1.6C336 228.8 420.8 272 512 272c91.2 0 176-41.6 220.8-110.4 0-1.6 1.6-1.6 1.6-1.6h38.4V96m-481.6 0H256c-22.4 0-38.4 6.4-49.6 19.2L43.2 276.8c-25.6 25.6-25.6 65.6 0 89.6l94.4 94.4c11.2 11.2 27.2 17.6 41.6 17.6s30.4-6.4 41.6-17.6h1.6c1.6 0 1.6 0 1.6 1.6V800c0 70.4 57.6 128 128 128h320c70.4 0 128-57.6 128-128V462.4c0-1.6 0-1.6 1.6-1.6h1.6c11.2 11.2 27.2 17.6 41.6 17.6 16 0 30.4-6.4 41.6-17.6l94.4-94.4c25.6-25.6 25.6-65.6 0-89.6L819.2 115.2C806.4 102.4 790.4 96 772.8 96h-40c-22.4 0-41.6 11.2-54.4 30.4-33.6 49.6-96 81.6-168 81.6s-134.4-33.6-168-81.6C332.8 107.2 312 96 291.2 96z");
		skinPath.setLayoutX(-500);
		skinPath.setLayoutY(-490);
		HBox skin = new HBox(skinPath);
		skin.setStyle("-fx-scale-x:0.02;-fx-scale-y:0.02;-fx-min-width:16px;-fx-min-height:16px");

		skinButton = new Button(null, skin);
		skinButton.getStyleClass().setAll("svg-button", "skin-button");

		SVGPath settingPath = new SVGPath();
		settingPath.setContent(
				"M8.837 1.626c-.246-.835-1.428-.835-1.674 0l-.094.319A1.873 1.873 0 0 1 4.377 3.06l-.292-.16c-.764-.415-1.6.42-1.184 1.185l.159.292a1.873 1.873 0 0 1-1.115 2.692l-.319.094c-.835.246-.835 1.428 0 1.674l.319.094a1.873 1.873 0 0 1 1.115 2.693l-.16.291c-.415.764.42 1.6 1.185 1.184l.292-.159a1.873 1.873 0 0 1 2.692 1.116l.094.318c.246.835 1.428.835 1.674 0l.094-.319a1.873 1.873 0 0 1 2.693-1.115l.291.16c.764.415 1.6-.42 1.184-1.185l-.159-.291a1.873 1.873 0 0 1 1.116-2.693l.318-.094c.835-.246.835-1.428 0-1.674l-.319-.094a1.873 1.873 0 0 1-1.115-2.692l.16-.292c.415-.764-.42-1.6-1.185-1.184l-.291.159A1.873 1.873 0 0 1 8.93 1.945l-.094-.319zm-2.633-.283c.527-1.79 3.065-1.79 3.592 0l.094.319a.873.873 0 0 0 1.255.52l.292-.16c1.64-.892 3.434.901 2.54 2.541l-.159.292a.873.873 0 0 0 .52 1.255l.319.094c1.79.527 1.79 3.065 0 3.592l-.319.094a.873.873 0 0 0-.52 1.255l.16.292c.893 1.64-.902 3.434-2.541 2.54l-.292-.159a.873.873 0 0 0-1.255.52l-.094.319c-.527 1.79-3.065 1.79-3.592 0l-.094-.319a.873.873 0 0 0-1.255-.52l-.292.16c-1.64.893-3.433-.902-2.54-2.541l.159-.292a.873.873 0 0 0-.52-1.255l-.319-.094c-1.79-.527-1.79-3.065 0-3.592l.319-.094a.873.873 0 0 0 .52-1.255l-.16-.292c-.892-1.64.902-3.433 2.541-2.54l.292.159a.873.873 0 0 0 1.255-.52l.094-.319z M8 5.754a2.246 2.246 0 1 0 0 4.492 2.246 2.246 0 0 0 0-4.492zM4.754 8a3.246 3.246 0 1 1 6.492 0 3.246 3.246 0 0 1-6.492 0z");
		settingButton = new Button(null, settingPath);
		settingButton.getStyleClass().setAll("svg-button");

		SVGPath minimize = new SVGPath();
		minimize.setContent("M6 6 h12");
		minimize.setStrokeWidth(0.65);
		Button minimized = new Button(null, minimize);
		minimized.getStyleClass().setAll("minimize-button");
		minimized.setOnAction(e -> Main.getPrimaryStage().setIconified(true));

		SVGPath maximize = new SVGPath();
		maximize.setContent("M0 0 h12 v12 h-12 v-12");
		maximize.setStrokeWidth(0.65);
		Button maximized = new Button(null, maximize);
		maximized.getStyleClass().setAll("maximize-button");
		maximized.setOnAction(e -> StageHandler.getHandler().setMaximized(maximize));

		SVGPath closeable = new SVGPath();
		closeable.setContent("M0 0 L12 12 M12 0 L0 12");
		Button closed = new Button(null, closeable);
		closed.getStyleClass().setAll("close-button");
		closed.setOnAction(e -> Main.getPrimaryStage().close());
		HBox.setMargin(closed, new Insets(0, 16, 0, 0));

		HBox hBox1Top = new HBox(20, headsetButton, skinButton, settingButton, minimized, maximized, closed);
		hBox1Top.setAlignment(Pos.CENTER_LEFT);

		BorderPane topPane = new BorderPane(hBox);
		topPane.setRight(hBox1Top);
		topPane.setId("top-pane");
		topPane.setPrefHeight(60);
		AnchorPane.setTopAnchor(topPane, 0.0);
		AnchorPane.setRightAnchor(topPane, 0.0);
		AnchorPane.setLeftAnchor(topPane, (double) size);
		StageHandler.getHandler().bindMoveListener(topPane);

		// -----------主选项卡面板部分-----------
		Tab localTab = new Tab("本地音乐", localModuleView);
		localTab.setGraphic(FileUtil.createView("computer", 20, 20));

		Tab netTab = new Tab("网络乐库", netView);
		netTab.setGraphic(FileUtil.createView("music-hall", 20, 20));

		AnchorPane paneRoot = new AnchorPane();
		Tab likeTab = new Tab("我的收藏", paneRoot);
		likeTab.setGraphic(FileUtil.createView("my-like", 20, 20));

		Tab specialTab = new Tab("我的歌单", paneRoot);
		specialTab.setGraphic(FileUtil.createView("special", 20, 20));

		paneRoot = new AnchorPane();
		Tab downloadTab = new Tab("下载管理", paneRoot);
		downloadTab.setGraphic(FileUtil.createView("down-manage", 20, 20));

		TabPane mainTabPane = new TabPane(Side.LEFT, localTab, netTab, likeTab, specialTab, downloadTab);
		mainTabPane.setPlaceHolderTab(new Tab());
		AnchorPane.setTopAnchor(mainTabPane, 60.0);
		AnchorPane.setRightAnchor(mainTabPane, 0.0);
		AnchorPane.setLeftAnchor(mainTabPane, 0.0);
		AnchorPane.setBottomAnchor(mainTabPane, 86.0);

		spinner = new Spinner();
		spinner.startAnimation();
		spinner.setText("执行初始化中...");
		AnchorPane.setLeftAnchor(spinner, 25.0);
		AnchorPane.setBottomAnchor(spinner, 98.0);

		// --------------场景图底部---------
		// 播放进度控制 (滑动条)
		progressSlider = new Slider(0, 1, 0);
		BorderPane.setMargin(progressSlider, new Insets(-4, 0, 10, 0));

		// 专辑图片
		albumImageView = FileUtil.createView("default", 64, 64);
		Rectangle clip = new Rectangle(64, 64);
		clip.setArcHeight(12);
		clip.setArcWidth(12);
		albumImageView.setClip(clip);

		singerLabel = new Label("MQ音乐");
		titleLabel = new Label("聆听世界");

		HBox hBox1 = new HBox(singerLabel, new Label(" - "), titleLabel);
		hBox1.setAlignment(Pos.CENTER_LEFT);

		currentTimeLabel = new Label("00:00");
		durationLabel = new Label("00:00");
		HBox hBox2 = new HBox(4, currentTimeLabel, new Label("/"), durationLabel);
		hBox2.setAlignment(Pos.CENTER_LEFT);

		VBox vBox = new VBox(5, hBox1, hBox2);
		vBox.setAlignment(Pos.CENTER_LEFT);
		vBox.setMinWidth(120);

		HBox leftBox = new HBox(8, albumImageView, vBox);
		leftBox.setAlignment(Pos.CENTER_LEFT);

		// 播放模式
		ImageView playModelView = FileUtil.createView(PlayModel.LIST_LOOP.getIcon());
		Label listLoop = new Label("列表循环", new ImageView(PlayModel.LIST_LOOP.getIcon()));
		Label orderPlay = new Label("顺序播放", new ImageView(PlayModel.ORDER_PLAY.getIcon()));
		Label singleLoop = new Label("单曲循环", new ImageView(PlayModel.SINGLE_LOOP.getIcon()));
		Label randomPlay = new Label("随机播放", new ImageView(PlayModel.RANDOM_PLAY.getIcon()));
		randomPlay.getStyleClass().add("last-child-node");
		// 播放模式选项 (弹出式面板)
		PopupPane playModelPane = new PopupPane(Pos.TOP_CENTER, listLoop, orderPlay, singleLoop, randomPlay);
		playModelPane.setPrefSize(128, 240);
		playModelPane.setId("play-model-pane");

		// 上一首
		prev = FileUtil.createView("player/prev", 40, true);

		// 播放或暂停
		play = FileUtil.createView("player/play", 54, true);

		// 下一首
		next = FileUtil.createView("player/next", 40, true);

		// 音量
		ImageView volume = FileUtil.createView("player/volume");
		// 音量滑动条
		volumeSlider = new Slider(0, 1, 0.5);
		volumeSlider.setPrefHeight(160);
		volumeSlider.setBlockIncrement(0.05);// 滑块增量
		volumeSlider.setOrientation(Orientation.VERTICAL);
		// 音量大小提示标签
		Label volumeLabel = new Label("50%");
		// 音量设置 (弹出式面板)
		PopupPane volumePane = new PopupPane(Pos.CENTER, volumeSlider, volumeLabel);
		volumePane.setPrefSize(70, 230);

		HBox centerBox = new HBox(16, playModelView, prev, play, next, volume);
		centerBox.setAlignment(Pos.CENTER);

		// 收藏
		like = FileUtil.createView("like");
		Image likeImage = FileUtil.createImage("like-red");
		Image unlikeImage = like.getImage();
		like.setOnMouseClicked(e -> like.setImage(like.getImage() == unlikeImage ? likeImage : unlikeImage));

		// 倍速播放
		Label speed = new Label("1.0X");
		speed.setId("speed-node");

		// 倍速播放滑动条
		speedSlider = new Slider(0.5, 2, 1);
		speedSlider.setMajorTickUnit(0.5);
		speedSlider.setBlockIncrement(0.1);// 滑块增量
		speedSlider.setShowTickLabels(true);
		speedSlider.setOrientation(Orientation.VERTICAL);

		// 倍速播放设置 (弹出式面板)
		PopupPane speedPane = new PopupPane(Pos.CENTER, speedSlider);
		speedPane.setPadding(new Insets(10));
		speedPane.setPrefSize(90, 280);
		speedSlider.prefHeightProperty().bind(speedPane.heightProperty().subtract(50));

		download = FileUtil.createView("download");

		// -----------播放队列部分-----------
		// 底部面板用于 打开播放队列视图的组件
		ImageView playQueue = FileUtil.createView("play-queue");

		Label title = new Label("播放队列（音乐）");
		playQueueTitleProperty = title.textProperty();
		AnchorPane.setTopAnchor(title, 5.0);
		AnchorPane.setLeftAnchor(title, 10.0);

		Label playNum = new Label("0首");
		AnchorPane.setTopAnchor(playNum, 45.0);
		AnchorPane.setLeftAnchor(playNum, 10.0);

		ImageView prevQueue = FileUtil.createView("m-prev", 24, 24);
		AnchorPane.setTopAnchor(prevQueue, 56.0);
		AnchorPane.setRightAnchor(prevQueue, 130.0);
		prevQueue.setStyle("-fx-cursor:hand;");

		ImageView nextQueue = FileUtil.createView("m-prev", 24, 24);
		nextQueue.setRotate(180);
		AnchorPane.setTopAnchor(nextQueue, 56.0);
		AnchorPane.setRightAnchor(nextQueue, 90.0);
		nextQueue.setStyle("-fx-cursor:hand;");

		// “清除播放队列”标签
		clearPlayList = FileUtil.createView("delete-white", 24, 24);
		clearPlayList.setId("clear-play-list");
		AnchorPane.setTopAnchor(clearPlayList, 56.0);
		AnchorPane.setRightAnchor(clearPlayList, 50.0);

		ImageView closeQueue = FileUtil.createView("close", 24, 24);
		closeQueue.setId("close-play-queue");
		AnchorPane.setTopAnchor(closeQueue, 56.0);
		AnchorPane.setRightAnchor(closeQueue, 10.0);

		musicQueueView = new ListView<>();
		musicQueueView.getStyleClass().add("play-list-view");
		AnchorPane.setTopAnchor(musicQueueView, 90.0);
		AnchorPane.setRightAnchor(musicQueueView, 0.0);
		AnchorPane.setBottomAnchor(musicQueueView, 20.0);
		AnchorPane.setLeftAnchor(musicQueueView, 0.0);

		mvQueueView = new ListView<>();
		mvQueueView.setId("mv-queue-view");
		mvQueueView.getStyleClass().add("play-list-view");
		AnchorPane.setTopAnchor(mvQueueView, 90.0);
		AnchorPane.setRightAnchor(mvQueueView, 0.0);
		AnchorPane.setBottomAnchor(mvQueueView, 20.0);
		AnchorPane.setLeftAnchor(mvQueueView, 0.0);
		AnchorPane pane = new AnchorPane(title, prevQueue, nextQueue, playNum, clearPlayList, closeQueue,
				musicQueueView);
		// 播放队列面板(“弹出式面板”)
		PopupPane playQueuePane = new PopupPane(pane);
		playQueuePane.setId("play-queue-pane");
		playQueuePane.setPrefWidth(500);
		// “播放队列面板” 的高度绑定到“主场景视图根布局面板”的高度-86
		playQueuePane.prefHeightProperty().bind(Main.getRoot().heightProperty().subtract(126));
		// “播放队列 列表视图” 的高度绑定到 “播放队列面板” 的高度
		musicQueueView.prefHeightProperty().bind(playQueuePane.prefHeightProperty());

		fullScreenNode = new SVGPath();
		fullScreenNode.setContent(fullIcon);
		fullScreenNode.setId("full-node");
		fullScreenNode.setPickOnBounds(true);
		setVisibility(fullScreenNode, false);

		// 底部面板的内容右水平盒子
		HBox rightBox = new HBox(16, like, speed, download, fullScreenNode, playQueue);
		rightBox.setAlignment(Pos.CENTER_RIGHT);

		// 底部面板
		bottomPane = new BorderPane() {
			protected void layoutChildren() {
				Insets insets = (insets = getInsets()) == null ? Insets.EMPTY : insets;
				// 面板可用内容区域的x和y坐标
				double x = insets.getLeft(), y = insets.getTop();
				// 面板实际可用宽度(总宽度 - 左右内边距)
				double width = getWidth() - x - insets.getRight();
				// 面板实际可用高度(总高度 - 上下内边距)
				double height = getHeight() - y - insets.getBottom();

				// 上、中、右、左 区域的节点
				Node top = getTop(), center = getCenter();
				Node right = getRight(), left = getLeft();

				// 是否捕捉到像素
				boolean snap = isSnapToPixel();

				Insets margin = (margin = getMargin(top)) == null ? Insets.EMPTY : margin;
				double th = top.prefHeight(-1);
				top.resize(width, th);// 忽略左右外边距,但不忽略上下边距
				th += margin.getTop() + margin.getBottom();
				positionInArea(top, x, y, width, th, 0, margin, HPos.LEFT, VPos.TOP, snap);

				margin = (margin = getMargin(center)) == null ? Insets.EMPTY : margin;
				// 在中央的节点宽度
				double cw = center.prefWidth(-1);
				// 在中央的节点高度
				double ch = height - th;
				// 重新调整节点大小 ; 在布局面板中,占用大小 = 实际大小 + 外边距
				center.resize(cw, ch);
				cw += margin.getLeft() + margin.getRight();
				ch += margin.getTop() + margin.getBottom();

				// 在右边的节点最佳宽度
				double pref = right.prefWidth(-1);
				// 在右边的节点宽度
				double rw = (rw = (width - cw) / 2) < pref ? pref : rw;
				// 在右边的节点高度等于在中央的节点高度
				// 重新调整节点大小,已忽略外边距
				right.resize(rw, ch);

				// 在左边的节点宽度和右边的节点宽度总是相等,在左边的节点高度等于在中央的节点高度
				// 重新调整节点大小,已忽略外边距
				left.resize(rw, ch);

				positionInArea(left, x, y + th, rw, ch, 0, null, HPos.LEFT, VPos.CENTER, snap);
				positionInArea(right, x + rw + cw, y + th, rw, ch, 0, null, HPos.RIGHT, VPos.TOP, snap);
				positionInArea(center, x + rw, y + th, cw, ch, 0, margin, HPos.CENTER, VPos.CENTER, snap);
			}
		};
		bottomPane.setId("bottom-pane");
		bottomPane.setTop(progressSlider);
		bottomPane.setLeft(leftBox);
		bottomPane.setCenter(centerBox);
		bottomPane.setRight(rightBox);
		AnchorPane.setRightAnchor(bottomPane, 0.0);
		AnchorPane.setBottomAnchor(bottomPane, 0.0);
		AnchorPane.setLeftAnchor(bottomPane, 0.0);

		// 添加子组件到在根布局面板(节点)中
		Main.getRoot().getChildren().addAll(box, topPane, mainTabPane, spinner, bottomPane);

		/* *************播放详情*********** */
		effectView = new ImageView();
		effectView.fitWidthProperty().bind(Main.getRoot().widthProperty());
		effectView.fitHeightProperty().bind(Main.getRoot().heightProperty());
		effectView.setEffect(new BoxBlur(255, 255, 3));
		effectView.imageProperty().bind(albumImageView.imageProperty());

		lyricView = new ListView<>();
		lyricView.setCellFactory(listView -> new ListCell<LyricLine>() {
			@Override
			protected void updateItem(LyricLine item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? null : item.getContent());
			}
		});
		lyricView.setId("lyric-view");
		lyricView.setFixedCellSize(36.0);
		lyricView.setPlaceholder(new Text("未找到歌词！"));
		AnchorPane.setTopAnchor(lyricView, 20.0);
		AnchorPane.setRightAnchor(lyricView, 0.0);
		AnchorPane.setBottomAnchor(lyricView, 260.0);
		AnchorPane.setLeftAnchor(lyricView, 0.0);

		audioSpectrumView = new AudioSpectrumView(800, 230, 80, 2);
		// audioSpectrumView.setColors("#7EC0EE", "#9AFF9A");
		audioSpectrumView.setColors("#7EC0EE", "#9AFF9A", "#FF86C1", "#FFA07A", "#FF00FF");
		AnchorPane.setRightAnchor(audioSpectrumView, 0.0);
		AnchorPane.setBottomAnchor(audioSpectrumView, 0.0);
		AnchorPane.setLeftAnchor(audioSpectrumView, 0.0);

		detailBox = new AnchorPane(lyricView, audioSpectrumView);
		AnchorPane.setTopAnchor(detailBox, 0.0);
		AnchorPane.setRightAnchor(detailBox, 0.0);
		AnchorPane.setBottomAnchor(detailBox, 86.0);
		AnchorPane.setLeftAnchor(detailBox, 0.0);

		/* ********* 注册默认的处理事件 *******/
		albumImageView.setOnMouseClicked(e -> {
			// 是否需要显示播放详情页面
			boolean need = Main.getRoot().getId() == null;
			// 若事件对象为null(手动调用的情况),并且已显示播放详情页,则不作任何操作
			if (e == null && !need) {
				return;
			}
			Main.getRoot().setId(need ? "root" : null);
			ObservableList<Node> nodes = Main.getRoot().getChildren();
			if (need) {
				// 若歌词列表视图在播放详情页,则显示歌词视图
				need = detailBox.getChildren().contains(lyricView);

				// 否则显示视频视图,不需要 effectView
				nodes.setAll(need ? new Node[] { effectView, detailBox, bottomPane } //
						: new Node[] { detailBox, bottomPane });

				setVisibility(fullScreenNode, !need);

			} else {
				nodes.setAll(box, topPane, mainTabPane, bottomPane);
				setVisibility(fullScreenNode, false);
				if (spinner != null) {
					nodes.add(spinner);
				}
			}

			if (audioSpectrumUpdateProperty != null) {
				audioSpectrumUpdateProperty.set(need);
			}
		});

		Image volumeImage = volume.getImage();
		Image muteImage = FileUtil.createImage("player/mute");
		// 音量滑动条值改变时,修改音量提示标签的值
		volumeSlider.valueProperty().addListener((observable, oldValue, value) -> {
			int volumeSize = (int) (value.floatValue() * 100);
			volume.setImage(volumeSize > 0 ? volumeImage : muteImage);
			// 最大4个字符 (0% ~ 100%)
			volumeLabel.setText(new StringBuilder(4).append(volumeSize).append('%').toString());
		});

		// 绑定弹出式面板显示到音量图标之上
		volumePane.showUpTo(volume, Main.getPrimaryStage());

		// 音量面板的鼠标滚动事件处理
		volumePane.setOnScroll(e -> {
			if (e.getDeltaY() > 0) {
				volumeSlider.increment();
			} else {
				volumeSlider.decrement();
			}
		});

		// 倍速播放控制 滑动条值 改变时,修改倍速播放提示标签的值
		speedSlider.valueProperty().addListener((observable, oldValue, value) -> {
			// 速率大小(浮点数 ,精度1);
			float speedSize = (int) (value.floatValue() * 10) / 10F;
			speedSlider.setUserData(speedSize);
			// 最大4个字符 (0.5X ~ 2.0X)
			speed.setText(new StringBuilder(4).append(speedSize).append('X').toString());
		});

		// 绑定弹出式面板显示到倍速组件之上
		speedPane.showUpTo(speed, Main.getPrimaryStage());

		// 倍速播放控制面板鼠标滚动事件处理
		speedPane.setOnScroll(e -> {
			if (e.getDeltaY() > 0) {
				speedSlider.increment();
			} else {
				speedSlider.decrement();
			}
		});

		// 由于Text对象无法设置内边距和外边距 , 所以使用空格字符来“拉开”滑动条与刻度标签之间的距离
		// 在弹出式面板显示后,重新设置“播放速率滑动条”上的刻度标签值
		speedPane.setOnShown(e -> {
			// 移除此事件处理,只需要做一次处理即可
			speedPane.setOnShown(null);
			// 获取“播放速率控制滑动条”的刻度标签文本(获取的有一个是多余的,特征文本字符串是空串)
			Set<Node> nodes = speedSlider.lookupAll("Text");
			// 每个刻度值的文本(字符串)长度(字符个数)
			StringBuilder builder = new StringBuilder(8);
			for (Node item : nodes) {
				Text node = (Text) item;
				String text = node.getText();
				int length = text.length();
				if (length != 0) {
					builder.append(' ').append(' ').append(text);
					(length == 1 ? builder.append('.').append('0') : builder).append(' ');
					node.setText(builder.append(' ').append(' ').toString());
					builder.delete(0, builder.length());
				}
			}
		});

		// 绑定“播放模式弹出式控制面板” 显示到播放模式组件的上方
		playModelPane.showUpTo(playModelView, Main.getPrimaryStage());
		// 播放模式属性
		playModelProperty = new SimpleObjectProperty<>(PlayModel.LIST_LOOP);
		// “列表循环 标签” 鼠标事件
		listLoop.setOnMouseClicked(e -> changePlayModel(playModelPane, playModelView, PlayModel.LIST_LOOP));
		// “单曲循环 标签” 鼠标事件
		singleLoop.setOnMouseClicked(e -> changePlayModel(playModelPane, playModelView, PlayModel.SINGLE_LOOP));
		// “顺序播放 标签” 鼠标事件
		orderPlay.setOnMouseClicked(e -> changePlayModel(playModelPane, playModelView, PlayModel.ORDER_PLAY));
		// “随机播放 标签” 鼠标事件
		randomPlay.setOnMouseClicked(e -> changePlayModel(playModelPane, playModelView, PlayModel.RANDOM_PLAY));

		// 播放队列 的播放数量发生变化时,更新显示播放数量的Label组件
		// 若MV播放队列为空时,表示正在使用音乐播放队列,此时显示音乐队列的播放数量; 否则显示MV播放队列的播放数量
		ListChangeListener<? super Media> listener = (Change<? extends Media> c) -> {
			ListView<?> node = mvQueueView.getItems().isEmpty() ? musicQueueView : mvQueueView;
			playNum.setText(new StringBuilder().append(node.getItems().size()).append('首').toString());
		};
		// 播放列表视图数据量改变事件,设置有多少首音乐
		musicQueueView.getItems().addListener(listener);
		mvQueueView.getItems().addListener(listener);

		// 回到上一个播放队列
		prevQueue.setOnMouseClicked(e -> {
			ObservableList<Node> nodes = ((Pane) prevQueue.getParent()).getChildren();
			boolean empty = mvQueueView.getItems().isEmpty();
			// 若正在使用音乐播放队列,则将返回到音乐播放队列 ; 否则回到MV播放队列
			ListView<?> addNode = empty ? musicQueueView : mvQueueView;
			// 若正在使用音乐播放队列,则不显示MV播放队列 ; 否则不显示音乐播放队列
			ListView<?> removeNode = empty ? mvQueueView : musicQueueView;
			playQueueTitleProperty.set(empty ? "播放队列（音乐）" : "播放队列（MV）");
			int num = addNode.getItems().size();
			// 移除不需要显示的播放队列视图
			nodes.remove(removeNode);
			// 添加需要显示的播放队列视图
			if (!pane.getChildren().contains(addNode)) {
				nodes.add(addNode);
				// 更新播放队列中的播放数量
				playNum.setText(new StringBuilder().append(num).append('首').toString());
			}
		});

		// 显示下一个播放队列
		nextQueue.setOnMouseClicked(e -> {
			ObservableList<Node> nodes = ((Pane) prevQueue.getParent()).getChildren();
			boolean empty = mvQueueView.getItems().isEmpty();
			// 若正在使用音乐播放队列,则显示MV播放队列 ; 否则显示音乐播放队列
			ListView<?> addNode = empty ? mvQueueView : musicQueueView;
			// 若正在使用音乐播放队列,则不显示音乐播放队列 ; 否则不显示MV播放队列
			ListView<?> removeNode = empty ? musicQueueView : mvQueueView;
			playQueueTitleProperty.set(empty ? "播放队列（MV）" : "播放队列（音乐）");
			int num = addNode.getItems().size();
			// 移除不需要显示的播放队列视图
			nodes.remove(removeNode);
			// 添加需要显示的播放队列视图
			if (!pane.getChildren().contains(addNode)) {
				nodes.add(addNode);
				playNum.setText(new StringBuilder().append(num).append('首').toString());
			}
		});

		// 点击底部的“播放队列” 图标 以显示 “播放队列面板”
		playQueue.setOnMouseClicked(e -> playQueuePane.animate(Main.getPrimaryStage()));
		// 点击“关闭播放队列” 标签时 ,关闭“播放队列面板”
		closeQueue.setOnMouseClicked(e -> playQueuePane.hide());

		// 为底部面板添加鼠标点击过滤器,如果不是鼠标左击,阻止事件向子组件传递
		bottomPane.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
			if (e.getButton() != MouseButton.PRIMARY) {
				e.consume();
			}
		});
	}

	/**
	 * 改变播放模式，并关闭播放模式所属弹出式面板
	 * 
	 * @param popPane
	 *            播放模式控制面板 所属弹出式面板
	 * @param view
	 *            底部面板下面的播放模式ImageView
	 * @param model
	 *            播放模式
	 */
	private void changePlayModel(PopupPane popPane, ImageView view, PlayModel model) {
		playModelProperty.set(model);
		view.setImage(model.getIcon());
		popPane.hide();
	}

	/** 关闭进度旋转器 */
	public void closeSpinner() {
		if (spinner != null) {
			spinner.stopAnimation();
			Main.getRoot().getChildren().remove(spinner);
			spinner = null;
		}
	}

	/**
	 * 更新频谱视图
	 * 
	 * @param magnitudes
	 *            包含每个频带的分贝（dB）非正谱幅度的阵列
	 */
	public void update(float[] magnitudes) {
		audioSpectrumView.update(magnitudes, 60);
	}

	/**
	 * 重置频谱视图
	 * 
	 * @param playing
	 *            是否正在播放
	 */
	public void resetSpectrum(boolean playing) {
		if (playing) {
			audioSpectrumView.stopAnimation();
		} else {
			audioSpectrumView.reset();
		}
	}

	/**
	 * 设置播放详情视图. 当调用此方法将修改播放详情页面的视图显示,只有音乐播放详情和MV播放详情2中类型的视图. 与此同时,也会更新播放队列的类型.
	 * 
	 * @param mediaView
	 *            显示MV(视频)的视图组件.若为null,则显示歌词视图,否则显示MV(视频)视图
	 */
	public void setDetailView(Node mediaView) {
		ObservableList<Node> nodes = detailBox.getChildren();
		ObservableList<Node> rootNodes = Main.getRoot().getChildren();
		boolean value = mediaView == null;
		// 显示歌词视图
		if (value) {
			if (!nodes.contains(lyricView)) {
				// 若正在显示播放详情页面,且没有显示 模糊效果图片视图,则显示模糊效果图片
				if (rootNodes.contains(detailBox) && !rootNodes.contains(effectView)) {
					rootNodes.add(0, effectView);
				}
				nodes.setAll(lyricView, audioSpectrumView);
			}
		}
		// 显示MV视图
		else if (!nodes.contains(mediaView)) {
			nodes.setAll(mediaView);
			if (AnchorPane.getTopAnchor(mediaView) == null) {
				AnchorPane.setTopAnchor(mediaView, 0.0);
				AnchorPane.setRightAnchor(mediaView, 0.0);
				AnchorPane.setBottomAnchor(mediaView, 0.0);
				AnchorPane.setLeftAnchor(mediaView, 0.0);
			}
			// 移除 模糊效果图片组件
			if (rootNodes.contains(detailBox)) {
				rootNodes.remove(effectView);
			}
		}
		// 更新播放队列类型
		Node addNode = value ? musicQueueView : mvQueueView;
		Node removeNode = value ? mvQueueView : musicQueueView;
		// 默认显示音乐播放队列
		nodes = ((Pane) clearPlayList.getParent()).getChildren();
		if (!nodes.contains(addNode)) {
			nodes.remove(removeNode);
			nodes.add(addNode);
		}
	}

	/**
	 * 检查当前是否可以进行窗口全屏化或退出全屏
	 * 
	 * @return 若可以进行窗口全屏或退出全屏,则返回true
	 */
	public boolean isFullAble() {
		return Main.getRoot().getChildren().contains(detailBox) && !detailBox.getChildren().contains(lyricView);
	}

	private Timeline timeLine;

	public void fullScreen(boolean value) {
		ObservableList<Node> nodes = Main.getRoot().getChildren();
		fullScreenNode.setContent(value ? exitIcon : fullIcon);
		// 不是全屏
		if (!value) {
			if (timeLine != null) {
				timeLine.setOnFinished(null);
				timeLine.stop();
			}
			timeLine = null;
			detailBox.setOnMouseMoved(null);
			detailBox.setCursor(null);

			if (!nodes.contains(bottomPane)) {
				nodes.add(bottomPane);
			}
			AnchorPane.setBottomAnchor(detailBox, 86.0);
			return;
		}
		// 处于全屏状态
		nodes.remove(bottomPane);
		detailBox.setOnMouseMoved(e -> {
			if (!nodes.contains(bottomPane)) {
				detailBox.setCursor(null);
				nodes.add(bottomPane);
			}
			if (timeLine == null) {
				KeyFrame frame1 = new KeyFrame(Duration.ZERO, (KeyValue) null);
				KeyFrame frame2 = new KeyFrame(Duration.millis(2800), (KeyValue) null);
				timeLine = new Timeline(frame1, frame2);
				timeLine.setOnFinished(event -> {
					if (!bottomPane.isHover()) {
						nodes.remove(bottomPane);
						detailBox.setCursor(Cursor.NONE);
					}
				});
			}
			timeLine.playFromStart();
		});
		AnchorPane.setBottomAnchor(detailBox, 0.0);
	}

	/**
	 * 设置组件是否显示,当组件不可见时,不会占用任何空间.
	 * 
	 * @param node
	 *            任意节点
	 * @param visibility
	 *            可见性
	 */
	private static void setVisibility(Node node, boolean visibility) {
		node.setManaged(visibility);
		node.setVisible(visibility);
	}

	public ImageView getHeadImageView() {
		return headImageView;
	}

	public Button getUserNameButton() {
		return userNameButton;
	}

	public Button getBackNode() {
		return backButton;
	}

	public Button getForwardNode() {
		return forwardButton;
	}

	public Button getRefreshNode() {
		return refreshButton;
	}

	public EditText getSearchInput() {
		return searchInput;
	}

	public Button getHeadsetButton() {
		return headsetButton;
	}

	public Button getSkinNode() {
		return skinButton;
	}

	public Button getSettingNode() {
		return settingButton;
	}

	public ImageView getAlbumImageView() {
		return albumImageView;
	}

	public void setAlbumImage(Image image) {
		albumImageView.setImage(image != null ? image : FileUtil.createImage("default"));
	}

	public ImageView getPrev() {
		return prev;
	}

	public ImageView getPlay() {
		return play;
	}

	public ImageView getNext() {
		return next;
	}

	public Label getSingerLabel() {
		return singerLabel;
	}

	public Label getTitleLabel() {
		return titleLabel;
	}

	public Label getCurrentTimeLabel() {
		return currentTimeLabel;
	}

	public Label getDurationLabel() {
		return durationLabel;
	}

	public Slider getProgressSlider() {
		return progressSlider;
	}

	public ImageView getLike() {
		return like;
	}

	public Slider getVolumeSlider() {
		return volumeSlider;
	}

	public Slider getSpeedSlider() {
		return speedSlider;
	}

	public ObjectProperty<PlayModel> playModelProperty() {
		return playModelProperty;
	}

	public ImageView getDownload() {
		return download;
	}

	public ImageView getClearPlayList() {
		return clearPlayList;
	}

	public ListView<Music> getMusicQueueView() {
		return musicQueueView;
	}

	public ListView<MV> getMvQueueView() {
		return mvQueueView;
	}

	public ListView<LyricLine> getLyricView() {
		return lyricView;
	}

	public Node getScreenOperNode() {
		return fullScreenNode;
	}

	/** 获取音乐频谱可更新属性 */
	public BooleanProperty audioSpectrumUpdateProperty() {
		BooleanProperty property = audioSpectrumUpdateProperty;
		return property == null ? audioSpectrumUpdateProperty = new SimpleBooleanProperty() : property;
	}
}
