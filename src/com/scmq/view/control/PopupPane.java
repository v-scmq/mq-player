package com.scmq.view.control;

import com.scmq.player.app.Main;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.shape.VLineTo;
import javafx.stage.PopupWindow;
import javafx.util.Duration;

/**
 * 弹出式面板.下面是一个示意图
 *
 * <pre>
 *  ------
 * |	|
 * |	|
 * |	|
 *  --  --
 *    \/
 * </pre>
 *
 * @author SCMQ
 */
public class PopupPane extends PopupWindow {
	private Label rootNode = new Label();
	private Pane pane;

	/**
	 * 通过子节点构造一个弹出式面板
	 *
	 * @param nodes
	 *            子节点(可变参数)
	 */
	public PopupPane(Node... nodes) {
		pane = new VBox(nodes);
		createClip();
		bind();
	}

	/**
	 * 通过对齐方式和子节点构造一个弹出式面板
	 *
	 * @param alignment
	 *            对齐方式
	 * @param nodes
	 *            子节点(可变参数)
	 */
	public PopupPane(Pos alignment, Node... nodes) {
		this(nodes);
		((VBox) pane).setAlignment(alignment);
	}

	/**
	 * 通过任意布局面板,创建一个弹出式面板
	 *
	 * @param pane
	 *            布局面板
	 */
	public PopupPane(Pane pane) {
		this.pane = pane;
		bind();
	}

	/**
	 * 绑定视图(右键菜单项关联到面板)
	 */
	private void bind() {
		// 设置弹出式面板的“CSS类选择器”
		pane.getStyleClass().setAll("popup-pane");
		getScene().setRoot(rootNode);
		rootNode.setStyle("-fx-background-color:transparent;-fx-graphic-text-gap:0;");
		rootNode.setGraphic(pane);

		// 自动隐藏
		setAutoHide(true);
		// 关闭自动更正位置，可以超过屏幕边界
		setAutoFix(false);

		// 设置背景色
		setBackground(Color.rgb(30, 31, 35));
	}

	/**
	 * 创建一个默认的三角形剪辑
	 */
	void createClip() {
		// 采用属性绑定 来调整路径线段的坐标
		DoubleBinding lengthProperty = pane.widthProperty().multiply(0.1);
		DoubleBinding widthProperty = pane.widthProperty().multiply(0.4);
		DoubleBinding heightProperty = pane.heightProperty().subtract(lengthProperty);

		MoveTo moveTo = new MoveTo(0, 0);
		// 左边竖直线
		VLineTo vLineTo1 = new VLineTo();
		vLineTo1.yProperty().bind(heightProperty);

		// 使用相对坐标
		vLineTo1.setAbsolute(false);
		// 下边的左水平线
		HLineTo hLineTo1 = new HLineTo();
		hLineTo1.xProperty().bind(widthProperty);
		hLineTo1.setAbsolute(false);
		// 三角形左斜边线
		LineTo lineTo1 = new LineTo();
		lineTo1.xProperty().bind(lengthProperty);
		lineTo1.yProperty().bind(lengthProperty);
		lineTo1.setAbsolute(false);
		// 三角形右斜边线
		LineTo lineTo2 = new LineTo();
		lineTo2.xProperty().bind(lengthProperty);
		lineTo2.yProperty().bind(lengthProperty.multiply(-1));
		lineTo2.setAbsolute(false);
		// 下边的右水平线
		HLineTo hLineTo2 = new HLineTo();
		hLineTo2.xProperty().bind(widthProperty);
		hLineTo2.setAbsolute(false);
		// 右边竖直线
		VLineTo vLineTo2 = new VLineTo();
		vLineTo2.yProperty().bind(heightProperty.multiply(-1));
		vLineTo2.setAbsolute(false);
		// 上边水平线
		HLineTo hLineTo3 = new HLineTo();
		hLineTo3.xProperty().bind(widthProperty.multiply(-1));
		hLineTo3.setAbsolute(false);

		// 构造2D路径图形绘制对象
		Path path = new Path(moveTo, vLineTo1, hLineTo1, lineTo1, lineTo2, hLineTo2, vLineTo2, hLineTo3);
		// 设置线条类型
		path.setStrokeType(StrokeType.INSIDE);

		// 设置弹出式面板剪辑
		pane.setClip(path);
	}

	/**
	 * 设置背景填充色
	 *
	 * @param color
	 *            背景色
	 */
	private void setBackground(Color color) {
		// 2D图形(路径绘制)
		Shape path = (Shape) pane.getClip();
		if (path != null) {
			path.setFill(color);
		}
		pane.setBackground(new Background(new BackgroundFill(color, null, null)));
	}

	public ObservableList<Node> getChildren() {
		return pane.getChildren();
	}

	public void setOnScroll(EventHandler<? super ScrollEvent> e) {
		pane.setOnScroll(e);
	}

	public void setPrefSize(double prefWidth, double prefHeight) {
		pane.setPrefSize(prefWidth, prefHeight);
	}

	public void setPrefWidth(double width) {
		pane.setPrefWidth(width);
	}

	public void setPrefHeight(double height) {
		pane.setPrefHeight(height);
	}

	public DoubleProperty prefWidthProperty() {
		return pane.prefWidthProperty();
	}

	public DoubleProperty prefHeightProperty() {
		return pane.prefHeightProperty();
	}

	public void setId(String id) {
		pane.setId(id);
	}

	public void setPadding(Insets value) {
		pane.setPadding(value);
	}

	/**
	 * 绑定弹出式面板显示到指定节点之上
	 *
	 * @param node
	 *            任意组件(节点)
	 * @param owner
	 *            窗口所有者(必须在依赖的窗口之上显示)
	 */
	public void showUpTo(Node node, javafx.stage.Window owner) {
		// 鼠标移入弹出式图标时的事件处理
		node.setOnMouseEntered(e -> {
			// 鼠标移入时,弹出式面板不自动隐藏
			setAutoHide(false);
			// 若弹出式面板没有正在显示
			if (!isShowing()) {

				// 计算弹出式面板的x坐标(屏幕)
				double anchor = e.getScreenX() - e.getX() + node.prefWidth(-1) / 2;
				setAnchorX(anchor - pane.getPrefWidth() / 2);
				// 计算弹出式面板的y坐标(屏幕)
				anchor = e.getScreenY() - pane.getPrefHeight() - e.getY();
				setAnchorY(anchor - 10);// 减去10作为间隔距离
			}
		});

		// 鼠标移除时,弹出式面板自动隐藏
		node.setOnMouseExited(e -> setAutoHide(true));

		// 鼠标点击弹出式图标时,关闭或显示弹出式面板
		node.setOnMouseClicked(e -> {
			if (isShowing()) {
				hide();
			} else {
				show(owner);
			}
		});
	}

	private Timeline timeLine;

	public void animate(javafx.stage.Window owner) {
		if (timeLine == null) {
			// 构造时间线动画
			timeLine = new Timeline();
			// 动画播放完成 事件
			timeLine.setOnFinished(e -> {
				// 获取此时“弹出式内容面板”在屏幕上的坐标点
				Point2D point = pane.localToScreen(0, 0);// 可以使用Bounds代替
				// 从“主场景视图的根节点” 中移除“弹出式内容面板”
				Main.getRoot().getChildren().remove(pane);
				// 显示弹出式面板
				show(owner, point.getX() - 1, point.getY());
				// 重新关联到“弹出式面板根节点(***必须在显示后关联,否则导致CSS解析器计算时出现错误***)
				rootNode.setGraphic(pane);
			});
		} else {
			// 不论是否正在播放,都强制调用"stop()"方法试图停止动画
			timeLine.stop();
		}
		// “内容面板” 起始x坐标值
		double x1 = Main.getRoot().getWidth();
		double x2 = x1 - pane.getPrefWidth();
		// 弹出式面板内容面板 x坐标属性
		DoubleProperty property = pane.layoutXProperty();

		// 动画起始位置
		KeyValue startValue = new KeyValue(property, x1);
		// 动画起始帧
		KeyFrame startFrame = new KeyFrame(Duration.seconds(0), startValue);
		// 动画结束位置
		KeyValue endValue = new KeyValue(property, x2, Interpolator.EASE_OUT);
		// 动画结束帧
		KeyFrame endFrame = new KeyFrame(Duration.seconds(0.5), endValue);
		ObservableList<KeyFrame> keyFrames = timeLine.getKeyFrames();
		// 清除所有KeyFrame
		keyFrames.clear();
		// 添加新的KeyFrame
		keyFrames.add(startFrame);
		keyFrames.add(endFrame);

		// “弹出式内容面板” 脱离“根节点”的约束
		rootNode.setGraphic(null);
		// “弹出式内容面板”关联到主场景视图 根布局面板中
		if (Main.getRoot().getChildren().lastIndexOf(pane) == -1) {
			Main.getRoot().getChildren().add(pane);
		}
		// 重设内容面板y坐标
		pane.setLayoutY(Main.getRoot().getHeight() - pane.getPrefHeight() - 86);
		// 开始播放
		timeLine.play();
	}
}
