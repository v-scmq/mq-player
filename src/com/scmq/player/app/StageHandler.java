package com.scmq.player.app;

import com.scmq.view.control.Toast;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
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
public class StageHandler implements EventHandler<MouseEvent> {
	/** JavaFX窗口 处理器 */
	private static StageHandler handler = new StageHandler();

	/** 窗口最大化时显示的图标 */
	private static final String MAXIMIZED_ICON = "M3 0 h9 v9 h-3 M3 0 v3 h-3 v9 h9 v-9 h-6";
	/** 窗口为最大化时显示的图标 */
	private static final String MAXIMIZE_ICON = "M0 0 h12 v12 h-12 v-12";

	/** 本地窗口指针标记 */
	private static long nativeWindow;

	/** 加载本地库,然后重设本地窗口在任务栏上能够最小化(此方法需要运行在子线程中) */
	static void loadAndSetStyle() {
		// 加载本地库(比较耗时,窗口显示后还未能加载)
		User32 user32 = User32.INSTANCE;

		// 获得本地窗口指针
		Pointer pointer = new Pointer(nativeWindow);
		// 获得本地窗口句柄
		WinDef.HWND window = new WinDef.HWND(pointer);

		// 获得本地窗口原有样式
		int style = user32.GetWindowLong(window, WinUser.GWL_STYLE);
		// 设置本地窗口可最小化 (WS_MINIMIZEBOX = 0x00020000)
		user32.SetWindowLong(window, WinUser.GWL_STYLE, (style | WinUser.WS_MINIMIZEBOX));
	}

	private StageHandler() {
	}

	public static StageHandler getHandler() {
		return handler;
	}

	/** 绑定根节点鼠标事件,以支持窗口resize(需要运行在窗口显示后) */
	void bind() {
		nativeWindow = com.sun.glass.ui.Window.getWindows().get(0).getNativeWindow();
		Main.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, this);
		Main.getRoot().addEventFilter(MouseEvent.MOUSE_PRESSED, this);
		Main.getRoot().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
	}

	/** 记录窗口在最大化之前的x和y坐标 */
	double x, y;
	/** 记录窗口在最大化之前的宽度和高度,以及鼠标按下装饰栏时的误差x和y坐标 */
	double width, height, offsetX, offsetY;
	/** 窗口是否最大化 */
	boolean maximized;

	/** 最大化和还原SVG图标节点 */
	private SVGPath maximizeNode;

	/**
	 * 最大化窗口或还原窗口
	 *
	 * @param node
	 *            最大化图标
	 */
	public void setMaximized(SVGPath node) {
		Stage stage = Main.getPrimaryStage();
		node.setContent((maximized = !maximized) ? MAXIMIZED_ICON : MAXIMIZE_ICON);

		// 需要最大化
		if (maximized) {
			x = stage.getX();
			y = stage.getY();
			width = stage.getWidth();
			height = stage.getHeight();

			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();
			setBounds(stage, 0, 0, bounds.getWidth(), bounds.getHeight());
			Main.getRoot().removeEventFilter(MouseEvent.MOUSE_MOVED, this);
			return;
		}

		// 还原窗口
		setBounds(stage, x, y, width, height);
		Main.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, this);
	}

	/** 窗口是否全屏 */
	private boolean fullScreen;
	/** 非全屏状态下,窗口的x和y坐标已经窗口宽度和高度 */
	double normalX, normalY, normalWidth, normalHeight;

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

		Stage stage = Main.getPrimaryStage();

		// 窗口全屏
		if (fullScreen) {
			normalX = stage.getX();
			normalY = stage.getY();
			normalWidth = stage.getWidth();
			normalHeight = stage.getHeight();
			Rectangle2D rect = Screen.getPrimary().getBounds();
			setBounds(stage, 0, 0, rect.getWidth(), rect.getHeight());
			Main.getRoot().removeEventFilter(MouseEvent.MOUSE_MOVED, this);
			Toast.makeText(Main.getRoot(), "按ESC或F键以退出全屏").show();
			return;
		}

		// 还原窗口
		setBounds(stage, normalX, normalY, normalWidth, normalHeight);
		Main.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, this);
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
			if (Main.getRoot().getCursor() != Cursor.DEFAULT) {
				event.consume();
			}
			return;
		}

		// 鼠标按下并拖动
		if (type == MouseEvent.MOUSE_DRAGGED) {
			if (maximized) {
				return;
			}

			Stage stage = Main.getPrimaryStage();
			Cursor cursor = Main.getRoot().getCursor();

			// 鼠标窗口右下角边界上,可以改变宽度和高度
			if (cursor == Cursor.SE_RESIZE) {
				Rectangle2D rect = Screen.getPrimary().getVisualBounds();
				double size = (size = event.getX()) < 180 ? 180 : size > rect.getWidth() ? rect.getWidth() : size;
				stage.setWidth(size);
				size = (size = event.getY()) < 60 ? 60 : size > rect.getHeight() ? rect.getHeight() : size;
				stage.setHeight(size);

				// 鼠标在窗口右水平方向时,可以改变宽度
			} else if (cursor == Cursor.E_RESIZE) {
				double max = Screen.getPrimary().getVisualBounds().getWidth();
				double size = (size = event.getX()) < 180 ? 180 : size > max ? max : size;
				stage.setWidth(size);

				// 鼠标在窗口上垂直方向时,可以改变高度
			} else if (cursor == Cursor.S_RESIZE) {
				double max = Screen.getPrimary().getVisualBounds().getHeight();
				double size = (size = event.getY()) < 60 ? 60 : size > max ? max : size;
				stage.setHeight(size);
			}

			return;
		}

		// 鼠标移动(该方法回调只有这3中类型,因为只注册了这3个类型)
		if (maximized) {
			Main.getRoot().setCursor(Cursor.DEFAULT);
			return;
		}

		boolean x = event.getX() + 5 > Main.getRoot().getWidth(), y = event.getY() + 5 > Main.getRoot().getHeight();
		Cursor cursor = x && y ? Cursor.SE_RESIZE : x ? Cursor.E_RESIZE : y ? Cursor.S_RESIZE : Cursor.DEFAULT;
		Main.getRoot().setCursor(cursor);
	}

	/**
	 * 设置装饰栏拖动处理
	 * 
	 * @param decorative
	 *            装饰栏
	 */
	public void setDragHandler(Node decorative, SVGPath maximizeNode) {
		// 鼠标在装饰栏上按下时
		decorative.setOnMousePressed(e -> {
			if (e.getTarget() instanceof Pane) {
				offsetX = e.getScreenX() - Main.getPrimaryStage().getX();
				offsetY = e.getScreenY() - Main.getPrimaryStage().getY();
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

			Stage stage = Main.getPrimaryStage();

			// 当前窗口已经最大化 并且 鼠标向下拖动. 那么先还原窗口,然后继续拖动
			if (maximized && e.getScreenY() > 1) {
				maximized = false;
				maximizeNode.setContent(MAXIMIZE_ICON);

				// 获取屏幕可见最大宽度
				double max = Screen.getPrimary().getVisualBounds().getWidth();
				// 当前鼠标在屏幕上的x坐标, 新设置窗口坐标x=鼠标在屏幕上的x坐标-之前窗口宽度的一半
				double screenX = e.getScreenX(), x = screenX - (width / 2);
				// 需要注意计算出的x坐标 不能超过 (屏幕最大宽度 - 窗口之前的宽度)
				x = x < 0 ? 0 : x > (max -= width) ? max : x;

				setBounds(stage, x, e.getScreenY(), width, height);

				// 必须重新计算offsetX(这个时候认为是鼠标的重新按下,所以误差x重算)
				offsetX = screenX - x;

				Main.getRoot().removeEventFilter(MouseEvent.MOUSE_MOVED, this);
				Main.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, this);
				return;
			}

			stage.setX(e.getScreenX() - offsetX);
			if (e.getScreenY() + 5 < Screen.getPrimary().getVisualBounds().getHeight()) {
				stage.setY(e.getScreenY() - offsetY);
			}
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
	private static void setBounds(Stage stage, double x, double y, double width, double height) {
		stage.setWidth(width);
		stage.setHeight(height);
		stage.setX(x);
		stage.setY(y);
	}

}
