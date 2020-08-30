package com.scmq.player.app;

import com.scmq.player.util.Task;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class StageHandler {
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

		Stage stage = Main.getPrimaryStage();
		Main.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
			if (maximized) {
				Main.getRoot().setCursor(Cursor.DEFAULT);
				return;
			}

			boolean x = e.getX() + 5 > Main.getRoot().getWidth(), y = e.getY() + 5 > Main.getRoot().getHeight();
			Cursor cursor = x && y ? Cursor.SE_RESIZE : x ? Cursor.E_RESIZE : y ? Cursor.S_RESIZE : Cursor.DEFAULT;
			Main.getRoot().setCursor(cursor);
		});

		Main.getRoot().addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
			if (Main.getRoot().getCursor() != Cursor.DEFAULT) {
				e.consume();
			}
		});
		Main.getRoot().addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			if (maximized) {
				return;
			}
			System.out.println(stage.getWidth() + " | " + stage.getHeight() + " x=" + e.getX() + " | y=" + e.getY());
			Cursor cursor = Main.getRoot().getCursor();
			if (cursor == Cursor.SE_RESIZE) {
				stage.setWidth(e.getX() < 180 ? 180 : e.getX());
				stage.setHeight(e.getY() < 60 ? 60 : e.getY());
			} else if (cursor == Cursor.E_RESIZE) {
				stage.setWidth(e.getX() < 180 ? 180 : e.getX());
			} else if (cursor == Cursor.S_RESIZE) {
				stage.setHeight(e.getY() < 60 ? 60 : e.getY());
			}
		});
	}

	double x, y, width, height, x2, y2;
	boolean maximized;

	public void setMaximized(SVGPath node) {
		Stage stage = Main.getPrimaryStage();
		boolean value = maximized;
		maximized = !maximized;
		node.setContent(maximized ? "M3 0 h9 v9 h-3 M3 0 v3 h-3 v9 h9 v-9 h-6" : "M0 0 h12 v12 h-12 v-12");
		if (value) {
			setBounds(stage, x, y, width, height);
			return;
		}

		x = stage.getX();
		y = stage.getY();
		width = stage.getWidth();
		height = stage.getHeight();

		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();
		setBounds(stage, 0, 0, bounds.getWidth(), bounds.getHeight());
	}

	public void bindMoveListener(Node node) {
		node.setOnMousePressed(e -> {
			if (e.getTarget() instanceof Pane) {
				x2 = e.getScreenX() - Main.getPrimaryStage().getX();
				y2 = e.getScreenY() - Main.getPrimaryStage().getY();
			}
		});

		node.setOnMouseDragged(e -> {
			if (!maximized && (e.getTarget() instanceof Pane)) {
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
