package com.scmq.view.control;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/**
 * 对话框组件.
 *
 * @author SCMQ
 */
public class Dialog extends Pane {
	/** 对话框内容布局面板 */
	private BorderPane dialogPane;
	/** 对话框标题 */
	private Label titleLabel;
	/** 关闭图标 */
	private Label closeIcon;
	/** 指定是否为模态框 */
	private boolean modality;

	/** 对话框面板的x坐标绑定 */
	private DoubleBinding xBinding;
	/** 对话框面板的y坐标绑定 */
	private DoubleBinding yBinding;

	/**
	 * 构造对话框组件对象(非模态框)
	 *
	 * @param title
	 *            对话框标题
	 * @param content
	 *            对话框内容节点
	 */
	public Dialog(String title, Node content) {
		this(title, content, false);
	}

	/**
	 * 构造对话框组件对象
	 *
	 * @param title
	 *            对话框标题
	 * @param content
	 *            对话框内容节点
	 * @param modality
	 *            指定是否为模态框
	 */
	public Dialog(String title, Node content, boolean modality) {
		setModality(modality);
		titleLabel = new Label(title);
		titleLabel.getStyleClass().add("dialog-title");

		// 大写是绝对路径,小写是相对路径
		// M=moveTo ; L=lineTo ; H=hLineTo ; V=vLineTo ; C=curveTo ; FS=smooth
		// Q=quadratic Belzier CurveTo ; A=elliptical Arc ; Z=closePath
		SVGPath path = new SVGPath();
		path.setStrokeWidth(2);
		path.setMouseTransparent(true);
		path.setStrokeLineCap(StrokeLineCap.BUTT);
		path.setContent("M0 0 L16 16 M16 0 L0 16");
		closeIcon = new Label(null, path);
		closeIcon.getStyleClass().add("close-icon");

		HBox box = new HBox(8, titleLabel, closeIcon);
		box.getStyleClass().add("decorative-pane");
		titleLabel.prefWidthProperty().bind(box.widthProperty());
		box.setAlignment(Pos.CENTER_LEFT);

		dialogPane = new BorderPane(content);
		dialogPane.getStyleClass().add("dialog-pane");
		dialogPane.setPrefSize(600, 300);
		dialogPane.resize(600, 300);
		dialogPane.setTop(box);

		getChildren().add(dialogPane);
		getStyleClass().add("dialog");
		content.getStyleClass().add("dialog-content");

		xBinding = widthProperty().subtract(dialogPane.widthProperty()).divide(2);
		yBinding = heightProperty().subtract(dialogPane.heightProperty()).divide(2);
		dialogPane.layoutXProperty().bind(xBinding);

		// 鼠标释放时的事件回调
		setOnMouseReleased(e -> {
			if (e.getButton() != MouseButton.PRIMARY) {
				return;
			}
			Object source = e.getSource(), target = e.getTarget();
			// 若事件源和事件目标 不是同一个; 若是模态框并且事件目标不是关闭图标
			if (source != target || (this.modality && target != closeIcon)) {
				return;
			}
			if (source == this || source == closeIcon) {
				Pane pane = (Pane) getParent();
				if (pane != null) {
					pane.getChildren().remove(this);
				}
			}
		});

		// 关闭标签的鼠标点击事件
		closeIcon.setOnMouseClicked(getOnMouseReleased());

		// 对话框在ESC按键按下并释放后的事件
		setOnKeyTyped(e -> {
			if (e.getCode() == KeyCode.ESCAPE) {
				Pane pane = (Pane) getParent();
				if (pane != null) {
					pane.getChildren().remove(this);
				}
			}
		});
	}

	/**
	 * 显示对话框
	 *
	 * @param parent
	 *            对话框的父布局容器
	 */
	public void show(Pane parent) {
		Pane oldValue = (Pane) getParent();
		if (oldValue != parent) {
			prefWidthProperty().unbind();
			prefHeightProperty().unbind();
			parent.getChildren().add(this);
			prefWidthProperty().bind(parent.widthProperty());
			prefHeightProperty().bind(parent.heightProperty());
		}
		requestFocus();

		dialogPane.layoutYProperty().unbind();

		double value = -dialogPane.getHeight();
		DoubleProperty property = dialogPane.layoutYProperty();
		KeyFrame kf1 = new KeyFrame(Duration.millis(0), new KeyValue(property, value));
		value = (parent.getHeight() + value) / 2;
		KeyFrame kf2 = new KeyFrame(Duration.millis(300), new KeyValue(property, value, Interpolator.EASE_OUT));
		Timeline timeLine = new Timeline(kf1, kf2);

		timeLine.setOnFinished(e -> dialogPane.layoutYProperty().bind(yBinding));
		timeLine.play();
	}

	/** 关闭对话框 */
	public void close() {
		Pane parent = (Pane) getParent();
		if (parent != null) {
			parent.getChildren().remove(this);
		}
	}

	/**
	 * 设置关闭对话框之前的事件处理器(实则是对话框本身的鼠标按下事件过滤器) <br>
	 * 若需要阻止关闭对话框 ,只需调用事件对象的consume()方法 <br>
	 * 例如： dialog.setOnRequestClosing(e -> e.consume());
	 *
	 * @param handler
	 *            鼠标事件处理器
	 */
	public void setOnRequestClosing(EventHandler<MouseEvent> handler) {
		addEventFilter(MouseEvent.MOUSE_CLICKED, handler);
	}

	/** 释放资源 */
	public void dispose() {
		titleLabel = null;
		closeIcon.setGraphic(null);
		closeIcon.setOnMouseReleased(null);
		closeIcon = null;

		dialogPane.layoutXProperty().unbind();
		dialogPane.getChildren().clear();
		dialogPane = null;

		xBinding.dispose();
		yBinding.dispose();
		xBinding = null;
		yBinding = null;
		setOnMouseReleased(null);
		prefWidthProperty().unbind();
		prefHeightProperty().unbind();
	}

	/**
	 * 设置对话框类型
	 *
	 * @param modality
	 *            若为true,则以模态框显示;否则以非模态框显示.
	 */
	public void setModality(boolean modality) {
		this.modality = modality;
	}
}