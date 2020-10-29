package com.scmq.player.app;

import com.scmq.player.core.MediaPlayer;
import com.scmq.player.model.Music;
import com.scmq.player.model.PlayList;
import com.scmq.player.util.FileUtil;
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
import java.util.List;

import static com.scmq.player.app.StageHandler.STAGE_HANDLER;

/**
 * 音乐播放器应用主进程入口
 * 
 * @author SCMQ
 * @version 2.0
 * @since 2019-09-23
 */
public class Main extends javafx.application.Application {
	/** 主进程窗口 */
	private static Stage primaryStage;

	/** 场景图根布局面板 */
	private static final AnchorPane ROOT;
	/** “正在播放的媒体”属性 */
	private static final ObjectProperty<Music> MEDIA_PROPERTY;
	/** “播放列表”属性 */
	private static final ObjectProperty<PlayList> PLAY_LIST_PROPERTY;

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

		// 在子线程中加载Spring配置文件,并将spring上下文对象放入容器中
		Task.async(() -> put(new ClassPathXmlApplicationContext("spring-config.xml")));
	}

	@Override
	public void start(Stage stage) {
		primaryStage = stage;

		new MainView(new LocalMusicView(), new NetMusicView());
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
		ClassPathXmlApplicationContext context = remove(ClassPathXmlApplicationContext.class);
		// 关闭Spring上下文
		if (context != null) {
			context.close();
		}

		MediaPlayer player = remove(MediaPlayer.class);
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
		return primaryStage;
	}

	/**
	 * 从根节点属性缓存容器中获取指定Class对象对应的类的对象
	 * 
	 * @param clazz
	 *            Class对象
	 * @param <T>
	 *            Class对象对应的类
	 * @return Class对象对应的类的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> clazz) {
		return (T) ROOT.getProperties().get(clazz);
	}

	/**
	 * 从根节点属性缓存容器中获取并移除指定Class对象对应的类的对象
	 *
	 * @param clazz
	 *            Class对象
	 * @param <T>
	 *            Class对象对应的类
	 * @return Class对象对应的类的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T remove(Class<T> clazz) {
		return (T) ROOT.getProperties().remove(clazz);
	}

	/**
	 * 将指定Class对象对应的类的对象放入根节点属性缓存容器中
	 *
	 * @param clazz
	 *            Class对象
	 * @param value
	 *            Class对象对应的类的对象
	 * @param <T>
	 *            Class对象对应的类
	 */
	public static <T> void put(Class<T> clazz, Object value) {
		ROOT.getProperties().put(clazz, value);
	}

	/**
	 * 将指定Class对象对应的类的对象放入根节点属性缓存容器中
	 *
	 * @param value
	 *            Class对象对应的类的对象
	 * @param <T>
	 *            Class对象对应的类
	 */
	public static <T> void put(Object value) {
		ROOT.getProperties().put(value.getClass(), value);
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
