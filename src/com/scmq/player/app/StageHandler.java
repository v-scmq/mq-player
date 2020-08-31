package com.scmq.player.app;

import com.scmq.player.util.Task;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class StageHandler implements EventHandler<MouseEvent> {
	private static StageHandler handler = new StageHandler();

	private static User32 user32;

	static void loadWin32Library() {
		StageHandler.user32 = User32.INSTANCE;
	}

	private StageHandler() {
	}

	public static StageHandler getHandler() {
		return handler;
	}

	void bind() {
		Pointer pointer = new Pointer(com.sun.glass.ui.Window.getWindows().get(0).getNativeWindow());
		Task.async(() -> {
			WinDef.HWND window = new WinDef.HWND(pointer);
			try {
				Thread.sleep(1200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (user32 == null) {
				return;
			}
			Platform.runLater(() -> {
				int style = user32.GetWindowLong(window, WinUser.GWL_STYLE);// WS_MINIMIZEBOX = 0x00020000
				user32.SetWindowLong(window, WinUser.GWL_STYLE, (style | WinUser.WS_MINIMIZEBOX));
			});
		});

		Main.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, this);
		Main.getRoot().addEventFilter(MouseEvent.MOUSE_PRESSED, this);
		Main.getRoot().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
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
			if (cursor == Cursor.SE_RESIZE) {
				stage.setWidth(event.getX() < 180 ? 180 : event.getX());
				stage.setHeight(event.getY() < 60 ? 60 : event.getY());
			} else if (cursor == Cursor.E_RESIZE) {
				stage.setWidth(event.getX() < 180 ? 180 : event.getX());
			} else if (cursor == Cursor.S_RESIZE) {
				stage.setHeight(event.getY() < 60 ? 60 : event.getY());
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

	double x, y, width, height, x2, y2;
	boolean maximized;

	public void setMaximized(SVGPath node) {
		Stage stage = Main.getPrimaryStage();
		maximized = !maximized;
		node.setContent(maximized ? "M3 0 h9 v9 h-3 M3 0 v3 h-3 v9 h9 v-9 h-6" : "M0 0 h12 v12 h-12 v-12");

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
		setBounds(stage, x, y, width, height);
		Main.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, this);

	}

	public void bindMoveListener(Node node) {
		node.setOnMousePressed(e -> {
			if (e.getTarget() instanceof Pane) {
				x2 = e.getScreenX() - Main.getPrimaryStage().getX();
				y2 = e.getScreenY() - Main.getPrimaryStage().getY();
			}
		});

		node.setOnMouseReleased(e -> {
			if (maximized || e.getScreenY() > 0 || !(e.getTarget() instanceof Pane)) {
				return;
			}
			setMaximized((SVGPath) ((Labeled) node.lookup(".maximize-button")).getGraphic());
		});

		node.setOnMouseDragged(e -> {
			if (e.getTarget() instanceof Pane) {
				System.out.println("x=" + this.x + " | y=" + this.y);
				double y = e.getY();
				System.out.println("e-y=" + y);
				if (maximized && e.getScreenY() > 1) {
					maximized = false;
					Stage stage = Main.getPrimaryStage();
					SVGPath path = (SVGPath) ((Labeled) node.lookup(".maximize-button")).getGraphic();
//					path.setContent("M0 0 h12 v12 h-12 v-12");
//					setBounds(stage, this.x+e.getScreenX(), this.y, width, height);
//					Main.getRoot().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
					setMaximized(path);
					return;
				}

				Stage stage = Main.getPrimaryStage();
				stage.setX(e.getScreenX() - x2);
				if (e.getScreenY() < Screen.getPrimary().getVisualBounds().getHeight() - 5) {
					stage.setY(e.getScreenY() - y2);
				}
			}
		});
	}

	private static void setBounds(Stage stage, double x, double y, double width, double height) {
		stage.setX(x);
		stage.setY(y);
		stage.setWidth(width);
		stage.setHeight(height);
	}

}
