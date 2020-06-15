package com.scmq.view.control;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

import java.util.List;

/**
 * 这是一个进度旋转器,表示未知进度的任务提示组件.
 *
 * @author SCMQ
 */
public final class Spinner extends AnchorPane {
	/** 扇形线条颜色,接近红色 */
	private static final Color RED = Color.rgb(219, 68, 55);
	/** 扇形线条颜色,接近绿色 */
	private static final Color GREEN = Color.rgb(15, 157, 88);
	/** 扇形线条颜色,接近蓝色 */
	private static final Color BLUE = Color.rgb(66, 133, 244);
	/** 扇形线条颜色,接近黄色 */
	private static final Color YELLOW = Color.rgb(244, 180, 0);

	/** 扇形(2D图形) */
	private final Arc arc = new Arc();
	/** 时间轴动画 */
	private Timeline timeLine;

	/** 显示消息内容 */
	private Label content;

	/** 构造一个默认的进度旋转器对象 */
	public Spinner() {
		initialize();
		createAnimation();
	}

	/**
	 * 通过提示信息文本构造一个进度旋转器对象
	 *
	 * @param text
	 *            提示文本
	 */
	public Spinner(String text) {
		this();
		setText(text);
	}

	/**
	 * 设置提示信息内容
	 *
	 * @param text
	 *            提示信息文本
	 */
	public void setText(String text) {
		if (content == null) {
			content = new Label(text);
			// 此时布局面板的宽高会被Label撑开
			getChildren().add(content);
			AnchorPane.setTopAnchor(content, arc.getRadiusY() * 2 + 10);
			// 扇形圆心x坐标绑定到布局容器宽度的一半
			arc.centerXProperty().bind(widthProperty().multiply(0.5));
		} else {
			content.setText(text);
		}
	}

	/**
	 * 进度旋转器显示到布局容器的中央,并播放动画
	 *
	 * @param pane
	 *            布局容器
	 */
	public void centerTo(Pane pane) {
		// 不被布局管理器所管理(对于布局容器来说，这个组件是绝对定位)
		setManaged(false);
		layoutXProperty().unbind();
		layoutYProperty().unbind();
		// 采用绑定策略,将进度旋转器定位到布局容器的中央
		layoutXProperty().bind(pane.widthProperty().subtract(widthProperty()).multiply(0.5));
		layoutYProperty().bind(pane.heightProperty().subtract(heightProperty()).multiply(0.5));

		ObservableList<Node> nodes = pane.getChildren();
		if (!nodes.contains(this)) {
			pane.getChildren().add(this);
		}
		if (!isPlaying()) {
			startAnimation();
		}
	}

	/**
	 * 关闭这个进度旋转器
	 */
	public void close() {
		Pane pane = (Pane) getParent();
		if (pane != null) {
			pane.getChildren().remove(this);
		}
		layoutXProperty().unbind();
		layoutYProperty().unbind();
		stopAnimation();
	}

	/** 停止动画 */
	public void stopAnimation() {
		timeLine.stop();
	}

	/** 重新播放动画 */
	public void startAnimation() {
		timeLine.playFromStart();
	}

	/**
	 * 旋转器是否正在播放动画
	 *
	 * @return 若正在播放动画则返回true, 否则返回false
	 */
	public boolean isPlaying() {
		return timeLine.getStatus() == Status.RUNNING;
	}

	private void initialize() {
		// 圆弧起始角度(单位：度)
		arc.setStartAngle(0);
		// 圆弧的角度(单位：度)
		arc.setLength(180);
		// 扇行区域透明
		arc.setFill(Color.TRANSPARENT);
		// 扇形线条宽度
		arc.setStrokeWidth(3);
		// 默认线条颜色
		arc.setStroke(GREEN);
		// 扇形水平半径
		arc.setRadiusX(30);
		// 扇形垂直半径
		arc.setRadiusY(30);
		// 设置扇形的圆心x坐标
		arc.setCenterX(30);
		// 设置扇形的圆心y坐标
		arc.setCenterY(30);
		// 设置扇形节点不被布局容器管理(避免布局容器对扇形约束,造成效果错乱)
		arc.setManaged(false);
		// arc.getStyleClass().setAll("arc");
		getChildren().add(arc);
	}

	/** 创建动画 */
	private void createAnimation() {
		timeLine = new Timeline();
		// 循环次数(无限)
		timeLine.setCycleCount(Timeline.INDEFINITE);
		// 动画关键帧列表
		ObservableList<KeyFrame> frames = timeLine.getKeyFrames();

		addKeyFrames(frames, 0, 0, BLUE);
		addKeyFrames(frames, 450, 1.4, RED);
		addKeyFrames(frames, 900, 2.8, YELLOW);
		addKeyFrames(frames, 1350, 4.2, GREEN);

		KeyFrame endFrame = new KeyFrame(Duration.seconds(5.6), new KeyValue(arc.lengthProperty(), 5),
				new KeyValue(arc.startAngleProperty(), 1845 + arc.getStartAngle()));
		frames.add(endFrame);
	}

	/**
	 * 添加动画关键帧
	 *
	 * @param frames
	 *            动画帧
	 * @param angle
	 *            旋转器角度
	 * @param duration
	 *            时长
	 * @param color
	 *            颜色
	 */
	private void addKeyFrames(List<KeyFrame> frames, double angle, double duration, Color color) {
		KeyValue mValue1 = new KeyValue(arc.lengthProperty(), 5);// 默认的插值器是“线性插值器”
		KeyValue mValue2 = new KeyValue(arc.lengthProperty(), 250);

		KeyValue value = new KeyValue(arc.startAngleProperty(), angle += 45 + arc.getStartAngle());
		frames.add(new KeyFrame(Duration.seconds(duration), mValue1, value));

		value = new KeyValue(arc.startAngleProperty(), angle += 45);
		frames.add(new KeyFrame(Duration.seconds(duration + 0.4), mValue2, value));

		value = new KeyValue(arc.startAngleProperty(), angle += 45);
		frames.add(new KeyFrame(Duration.seconds(duration + 0.7), mValue2, value));

		value = new KeyValue(arc.startAngleProperty(), angle += 300);
		KeyValue strokeValue = new KeyValue(arc.strokeProperty(), color, Interpolator.EASE_BOTH);
		frames.add(new KeyFrame(Duration.seconds(duration + 1.1), mValue1, value, strokeValue));
	}
}
