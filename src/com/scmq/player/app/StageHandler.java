package com.scmq.player.app;

import com.scmq.player.util.Resource;
import com.scmq.view.control.Toast;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.media.jfxmediaimpl.HostUtils;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * JavaFX窗口处理器.<br>
 * 因为去除本地窗口的装饰栏,有很多功能需要重新实现.<br>
 * 这些功能包括窗口拖动、窗口resize、任务栏图标能够最小化等.
 * 
 * @author SCMQ
 * @since 2020-08-23
 */
public enum StageHandler implements EventHandler<MouseEvent>, ListChangeListener<Screen> {
	/** JavaFX窗口 处理器(改用枚举实现单例模式) */
	STAGE_HANDLER;

	/** 本地窗口指针标记 */
	private static long nativeWindow;

	/** 记录窗口在最大化之前的x和y坐标 */
	private double x, y;
	/** 窗口是否最大化 */
	private boolean maximized;
	/** 窗口是否全屏 */
	private boolean fullScreen;
	/** 记录窗口在最大化之前的宽度和高度,以及鼠标按下装饰栏时的误差x和y坐标 */
	private double width, height, offsetX, offsetY;
	/** 非全屏状态下,窗口的x和y坐标已经窗口宽度和高度 */
	private double normalX, normalY, normalWidth, normalHeight;

	/** 加载本地库,然后重设本地窗口在任务栏上能够最小化(此方法需要运行在子线程中) */
	static void loadAndSetStyle() {
		// 若不是window系统,则不执行后续操作
		if (!HostUtils.isWindows()) {
			return;
		}
		// 加载本地库(比较耗时,很有可能窗口显示后还未能加载)
		User32 user32 = User32.INSTANCE;

		// 同步到JavaFX UI线程执行(确保本地窗口已创建,才能获得本地窗口指针)
		Platform.runLater(() -> {
			// 获得本地窗口指针
			Pointer pointer = new Pointer(nativeWindow);
			// 获得本地窗口句柄
			WinDef.HWND window = new WinDef.HWND(pointer);

			// 获得本地窗口原有样式
			int style = user32.GetWindowLong(window, WinUser.GWL_STYLE);
			// 设置本地窗口可最小化 (WS_MINIMIZEBOX = 0x00020000)
			user32.SetWindowLong(window, WinUser.GWL_STYLE, (style | WinUser.WS_MINIMIZEBOX));
		});
	}

	/** 绑定根节点鼠标事件,以支持窗口resize(需要运行在窗口显示后) */
	void bind() {
		nativeWindow = com.sun.glass.ui.Window.getWindows().get(0).getNativeWindow();
		App.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, this);
		App.getRoot().addEventFilter(MouseEvent.MOUSE_PRESSED, this);
		App.getRoot().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
		Screen.getScreens().addListener(this);
	}

	/**
	 * 最大化窗口或还原窗口
	 *
	 * @param node
	 *            最大化图标
	 */
	public void setMaximized(SVGPath node) {
		Stage stage = App.getPrimaryStage();
		node.setContent((maximized = !maximized) ? Resource.MAXIMIZED_ICON : Resource.MAXIMIZE_ICON);

		// 需要最大化
		if (maximized) {
			x = stage.getX();
			y = stage.getY();
			width = stage.getWidth();
			height = stage.getHeight();

			Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
			setBounds(stage, bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
			App.getRoot().removeEventFilter(MouseEvent.MOUSE_MOVED, this);
			return;
		}

		// 还原窗口
		setBounds(stage, x, y, width, height);
		App.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, this);
	}

	/**
	 * 设置窗口是否全屏
	 *
	 * @param value
	 *            若为true则窗口全屏
	 */
	public void setFullScreen(boolean value) {
		if (fullScreen == value) {
			return;
		}

		fullScreen = value;

		Stage stage = App.getPrimaryStage();

		// 窗口全屏
		if (fullScreen) {
			normalX = stage.getX();
			normalY = stage.getY();
			normalWidth = stage.getWidth();
			normalHeight = stage.getHeight();
			Rectangle2D rect = Screen.getPrimary().getBounds();
			setBounds(stage, 0, 0, rect.getWidth(), rect.getHeight());
			App.getRoot().removeEventFilter(MouseEvent.MOUSE_MOVED, this);
			Toast.makeText(App.getRoot(), "按ESC或F键以退出全屏").show();
			return;
		}

		// 还原窗口
		setBounds(stage, normalX, normalY, normalWidth, normalHeight);
		App.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, this);
	}

	/** 检查窗口是否已全屏显示 */
	public boolean isFullScreen() {
		return fullScreen;
	}

	@Override
	public void handle(MouseEvent event) {
		EventType<? extends MouseEvent> type = event.getEventType();

		// 鼠标按下
		if (type == MouseEvent.MOUSE_PRESSED) {
			if (App.getRoot().getCursor() != Cursor.DEFAULT) {
				event.consume();
			}
			return;
		}

		// 鼠标按下并拖动
		if (type == MouseEvent.MOUSE_DRAGGED) {
			if (maximized) {
				return;
			}

			Stage stage = App.getPrimaryStage();
			Cursor cursor = App.getRoot().getCursor();

			// 鼠标窗口右下角边界上,可以改变宽度和高度
			if (cursor == Cursor.SE_RESIZE) {
				Rectangle2D rect = Screen.getPrimary().getVisualBounds();
				double size = (size = event.getX()) < 180 ? 180 : Math.min(size, rect.getWidth());
				stage.setWidth(size);
				size = (size = event.getY()) < 60 ? 60 : Math.min(size, rect.getHeight());
				stage.setHeight(size);

				// 鼠标在窗口右水平方向时,可以改变宽度
			} else if (cursor == Cursor.E_RESIZE) {
				double max = Screen.getPrimary().getVisualBounds().getWidth();
				double size = (size = event.getX()) < 180 ? 180 : Math.min(size, max);
				stage.setWidth(size);

				// 鼠标在窗口下垂直方向时,可以改变高度
			} else if (cursor == Cursor.S_RESIZE) {
				double max = Screen.getPrimary().getVisualBounds().getHeight();
				double size = (size = event.getY()) < 60 ? 60 : Math.min(size, max);
				stage.setHeight(size);
			}

			return;
		}

		// 鼠标移动(该方法回调只有这3中类型,因为只注册了这3个类型)
		if (maximized) {
			App.getRoot().setCursor(Cursor.DEFAULT);
			return;
		}

		boolean x = event.getX() + 5 > App.getRoot().getWidth(), y = event.getY() + 5 > App.getRoot().getHeight();
		Cursor cursor = x && y ? Cursor.SE_RESIZE : x ? Cursor.E_RESIZE : y ? Cursor.S_RESIZE : Cursor.DEFAULT;
		App.getRoot().setCursor(cursor);
	}

	/**
	 * 设置装饰栏拖动处理
	 * 
	 * @param decorative
	 *            装饰栏
	 * @param maximizeNode
	 *            最大化或还原节点
	 */
	public void setDragHandler(Node decorative, SVGPath maximizeNode) {
		// 鼠标在装饰栏上按下时
		decorative.setOnMousePressed(e -> {
			if (e.getTarget() instanceof Pane) {
				offsetX = e.getScreenX() - App.getPrimaryStage().getX();
				offsetY = e.getScreenY() - App.getPrimaryStage().getY();
			}
		});

		// 鼠标在装饰栏上释放时
		decorative.setOnMouseReleased(e -> {
			if (!maximized && e.getScreenY() == 0 && e.getTarget() instanceof Pane) {
				setMaximized(maximizeNode);
			}
		});

		// 鼠标正在拖动装饰栏
		decorative.setOnMouseDragged(e -> {
			// 若不是装饰栏(布局面板)本身,则什么也不做
			if (!(e.getTarget() instanceof Pane)) {
				return;
			}

			Stage stage = App.getPrimaryStage();
			Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
			// 当前窗口已经最大化 并且 鼠标向下拖动. 那么先还原窗口,然后继续拖动
			if (maximized && e.getScreenY() > 1) {
				maximized = false;
				maximizeNode.setContent(Resource.MAXIMIZE_ICON);

				// 获取屏幕可见最大宽度
				double max = bounds.getWidth();
				// 当前鼠标在屏幕上的x坐标, 新设置窗口坐标x=鼠标在屏幕上的x坐标-之前窗口宽度的一半
				double screenX = e.getScreenX(), x = screenX - (width / 2);
				// 需要注意计算出的x坐标 不能超过 (屏幕最大宽度 - 窗口之前的宽度)
				x = x < 0 ? 0 : x > (max -= width) ? max : x;

				setBounds(stage, x, e.getScreenY(), width, height);

				// 必须重新计算offsetX(这个时候认为是鼠标的重新按下,所以误差x重算)
				offsetX = screenX - x;

				// 重新设置鼠标移动监听
				App.getRoot().setOnMouseMoved(this);
				return;
			}

			stage.setX(e.getScreenX() - offsetX);
			// 保证窗口y坐标是在可视范围内
			double screenY = (screenY = e.getScreenY() - offsetY) < bounds.getMinY() ? bounds.getMinY()
					: screenY + 10 > bounds.getMaxY() ? bounds.getMaxY() - 10 : screenY;
			stage.setY(screenY);
		});
	}

	/**
	 * 设置窗口坐标和大小
	 * 
	 * @param stage
	 *            窗口
	 * @param x
	 *            屏幕x坐标
	 * @param y
	 *            屏幕y坐标
	 * @param width
	 *            窗口宽度
	 * @param height
	 *            窗口高度
	 */
	private void setBounds(Stage stage, double x, double y, double width, double height) {
		stage.setWidth(width);
		stage.setHeight(height);
		stage.setX(x);
		stage.setY(y);
	}

	/**
	 * 屏幕发生变化时,回调此方法
	 * 
	 * @param c
	 *            屏幕List改变维护对象
	 */
	@Override
	public void onChanged(Change<? extends Screen> c) {
		// 若已全屏或未最大化,则什么也不做
		// 理论情况下没有全屏且没有最大化时屏幕切换后,需要考虑窗口边界,但是目前windows10会自动重置窗口边界
		if (fullScreen || !maximized) {
			return;
		}

		Stage stage = App.getPrimaryStage();
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		setBounds(stage, bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
	}
}
