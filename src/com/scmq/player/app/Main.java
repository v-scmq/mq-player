package com.scmq.player.app;

import com.scmq.player.controller.LocalMusicController;
import com.scmq.player.controller.MainController;
import com.scmq.player.controller.NetMusicController;
import com.scmq.player.core.FXMediaPlayer;
import com.scmq.player.core.MediaPlayer;
import com.scmq.player.model.Music;
import com.scmq.player.model.PlayList;
import com.scmq.player.util.FileUtil;
import com.scmq.player.util.NavigationManager;
import com.scmq.player.util.Reflect;
import com.scmq.player.util.Task;
import com.scmq.player.util.TimeUtil;
import com.scmq.player.view.LocalMusicView;
import com.scmq.player.view.MainView;
import com.scmq.player.view.NetMusicView;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.List;

import static com.scmq.player.app.StageHandler.STAGE_HANDLER;

public class Main extends javafx.application.Application {
	/** 媒体播放器 */
	private MediaPlayer player;
	/** 主进程窗口 */
	private Stage primaryStage;
	/** 本地音乐模块视图 */
	private LocalMusicView localMusicView;
	/** 网络音乐模块视图 */
	private NetMusicView netMusicView;
	/** 主界面视图 */
	private MainView mainView;

	/** 主进程入口实例 */
	private static Main app;
	/** 场景图根布局面板 */
	private static final AnchorPane ROOT;
	/** “正在播放的媒体”属性 */
	private static final ObjectProperty<Music> MEDIA_PROPERTY;
	/** “播放列表”属性 */
	private static final ObjectProperty<PlayList> PLAY_LIST_PROPERTY;
	/** Spring上下文 */
	private static ClassPathXmlApplicationContext context;

	static {
		// 加载本地库然后设置本地窗口可最小化
		Task.async(StageHandler::loadAndSetStyle);
		// 重定向输出流,以打印日志.
		// setLog();
		// 销毁空格按键的相关事件
		disposeSpaceEvent();
		// 主进程开始就创建根布局面板(锚布局面板)
		ROOT = new AnchorPane();
		// “正在播放的媒体”属性(用于监听正在播放的媒体)
		MEDIA_PROPERTY = new SimpleObjectProperty<>();
		// 播放数据源属性(用于监听数据源改变时,切换播放源)
		PLAY_LIST_PROPERTY = new SimpleObjectProperty<>();
		// 在子线程中加载配置文件和创建播放器(因为比较耗时)
		Task.async(() -> {
			// 加载Spring容器
			context = new ClassPathXmlApplicationContext("spring-config.xml");
			// 创建媒体播放器
			MediaPlayer player = new FXMediaPlayer(context.getBean(MainController.class));
			// 同步到UI线程
			Platform.runLater(() -> {
				app.player = player;
				// 关闭加载提示
				app.mainView.closeSpinner();

				// 通过反射调用bind方法关联视图并绑定事件
				Class<?> clazz = LocalMusicController.class;
				Method method = Reflect.getMethod(clazz, "bind", LocalMusicView.class);
				Reflect.invoke(context.getBean(clazz), method, app.localMusicView);

				clazz = MainController.class;
				method = Reflect.getMethod(clazz, "bind", MediaPlayer.class, MainView.class);
				Reflect.invoke(context.getBean(clazz), method, app.player, app.mainView);

				clazz = NetMusicController.class;
				method = Reflect.getMethod(clazz, "bind", NetMusicView.class);
				Reflect.invoke(context.getBean(clazz), method, app.netMusicView);

				// 初始化并关联后退和前进图标的事件
				NavigationManager.initialize(app.mainView.getBackNode(), app.mainView.getForwardNode());
			});
		});
	}

	@Override
	public void start(Stage stage) {
		app = this;
		primaryStage = stage;
		netMusicView = new NetMusicView();
		localMusicView = new LocalMusicView();

		mainView = new MainView(localMusicView, netMusicView);
		stage.setScene(new Scene(ROOT, 1200, 800));
		stage.getScene().getStylesheets().add(FileUtil.getStyleSheet("style"));
		// stage.getScene().setCursor(new ImageCursor(new Image("cursor.png")));
		stage.getIcons().add(FileUtil.createImage("player"));
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setResizable(false);
		stage.setTitle("MQ音乐");
		stage.show();
		STAGE_HANDLER.bind();
	}

	/** 主进程结束的回调方法 */
	@Override
	public void stop() {
		// 关闭Spring上下文
		if (context != null) {
			context.close();
		}
		// 释放播放器资源
		if (player != null) {
			player.release();
		}
		// 关闭线程池
		Task.shutdown();
		// 退出JavaFX平台
		Platform.exit();
		// System.exit(0);
	}

	/**
	 * 获取场景图根布局面板
	 *
	 * @return 根布局面板
	 */
	public static AnchorPane getRoot() {
		return ROOT;
	}

	/**
	 * 获取播放列表(可观察改变的)属性对象
	 * 
	 * @return 播放列表属性对象
	 */
	public static ObjectProperty<PlayList> playListProperty() {
		return PLAY_LIST_PROPERTY;
	}

	/**
	 * 获取正在播放的音乐(可观察改变的)属性对象
	 * 
	 * @return 音乐属性对象
	 */
	public static ObjectProperty<Music> mediaProperty() {
		return MEDIA_PROPERTY;
	}

	/**
	 * 获取主进程窗口,有可能会返回null;因为创建是在UI线程的非静态方法,而此方法是静态方法.<br>
	 * 为了避免抛出异常,最好是在主进程开始创建视图时访问这个对象, 否则会抛出{@code NullPointerException}
	 *
	 * @return 主窗口
	 */
	public static Stage getPrimaryStage() {
		return app.primaryStage;
	}

	/** 销毁滚动面板和按钮的默认空格事件 */
	@SuppressWarnings("restriction")
	private static void disposeSpaceEvent() {
		List<com.sun.javafx.scene.control.behavior.KeyBinding> list;
		// 取消Button默认的键盘事件(在JDK8中只有空格键,所以直接清空)
		Class<?> clazz = com.sun.javafx.scene.control.behavior.ButtonBehavior.class;
		list = Reflect.getValue(clazz, "BUTTON_BINDINGS", null);
		list.clear();

		// 取消滚动面板的默认空格键盘事件
		clazz = com.sun.javafx.scene.control.behavior.ScrollPaneBehavior.class;
		list = Reflect.getValue(clazz, "SCROLL_PANE_BINDINGS", null);
		list.removeIf(keyBind -> keyBind.getCode() == KeyCode.SPACE);
	}

	/** 重定向以设置日志输出. */
	private static void setLog() {
		try {
			System.out.close();
			System.err.close();
			System.setOut(new PrintStream(new FileOutputStream("mq.log", true)));
			System.setErr(System.out);
			System.out.print("\n\n================== ");
			System.out.print(TimeUtil.currentTime());
			System.out.println(" ==================");
		} catch (FileNotFoundException ignore) {
		}
	}
}
