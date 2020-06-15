package com.scmq.player.view;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * 音乐频谱视图
 *
 * @author SCMQ
 */
public class AudioSpectrumView extends HBox {
	/** 矩形分段的颜色,颜色的数量决定矩形分段数量 */
	private Color[] colors;

	/** 每个矩形的分段平均高度 */
	private double averageHeight;

	/** 时间轴动画 */
	private Timeline timeLine;

	/**
	 * 设置矩形的填充色,这个颜色参数可以是一个或多个.如果没有有效的颜色参数,则使用默认的一个颜色值 <code>#7EC0EE</code>.<br>
	 * <code>barGraph.setColor("#7EC0EE", "#9AFF9A", "#FF86C1", "#FFA07A", "#FF00FF");</code>
	 *
	 * @param colors
	 *            颜色可以是RGB、HSB、16进制码、颜色单词
	 */
	public void setColors(String... colors) {
		if (colors == null || colors.length == 0) {
			this.colors = new Color[] { Color.valueOf("#7EC0EE") };
			return;
		}
		this.colors = new Color[colors.length];
		for (int i = 0; i < colors.length; i++) {
			this.colors[i] = Color.valueOf(colors[i]);
		}
	}

	/**
	 * 构造器音乐频谱视图
	 *
	 * @param width
	 *            视图宽度
	 * @param height
	 *            视图高度
	 * @param number
	 *            频谱矩形数量
	 * @param spacing
	 *            矩形之间的间隔距离
	 */

	public AudioSpectrumView(double width, double height, int number, double spacing) {
		super(spacing);
		// 视图旋转180度
		setRotate(180);
		// 视图最大尺寸
		setMaxSize(width, height);
		// 视图最佳尺寸
		setPrefSize(width, height);
		// 对齐方式
		setAlignment(Pos.TOP_CENTER);
		// 节点从右到左排列
		setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		// 平均高度
		averageHeight = height / 5;
		// 矩形默认填充色
		Color color = Color.valueOf("#7EC0EE");
		// 矩形宽度(不包含边框宽度)
		double rectWidth = (width + spacing) / number - spacing;
		ObservableList<Node> nodes = getChildren();
		for (int i = 0; i < number; i++) {
			Rectangle rect = new Rectangle(rectWidth, 0);
			rect.setEffect(new DropShadow(1, color));
			rect.setArcHeight(2);
			rect.setArcWidth(2);
			nodes.add(rect);
		}
		setDisable(true);
	}

	/**
	 * 更新频谱视图
	 *
	 * @param magnitudes
	 *            包含每个频带的分贝（dB）非正谱幅度的阵列,每个元素取值在[-60.0,0)区间
	 * @param bands
	 *            灵敏度阈值（以分贝为单位）
	 */
	public void update(float[] magnitudes, int bands) {
		// 频谱数据索引
		int index = 0;
		// 视图高度
		double height = getHeight();
		for (Node node : getChildren()) {
			Rectangle rect = (Rectangle) node;
			// 矩形高度 = 视图高度 * (每个频谱的值 + bands) / bands
			double rectHeight = height * (magnitudes[index++] + bands) / bands;
			rect.setHeight(rectHeight);

			// 计算每个矩形线性渐变填充颜色的数量
			int number = colors.length - 1;
			for (; number >= 0; number--) {
				if (rectHeight >= number * averageHeight) {
					number++;
					break;
				}
			}
			// 准备线性渐变填充色
			Stop[] stops = new Stop[number < 1 ? 1 : number];
			for (int i = 0; i < stops.length; i++) {
				stops[i] = new Stop(((float) i) / stops.length, colors[i]);
			}
			// 设置矩形的线性渐变颜色
			rect.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.REPEAT, stops));
		}
	}

	/** 以动画的方式,重置每个矩形高度为0. */
	public void reset() {
		int index = 0;
		// 只装箱一次,避免多次装箱
		Double mValue = 0.0;
		ObservableList<Node> nodes = getChildren();
		KeyValue[] keyValues1 = new KeyValue[nodes.size()];
		KeyValue[] keyValues2 = new KeyValue[nodes.size()];
		for (Node node : nodes) {
			Rectangle rect = (Rectangle) node;
			DoubleProperty value = rect.heightProperty();
			keyValues1[index] = new KeyValue(value, value.get(), Interpolator.EASE_OUT);
			keyValues2[index++] = new KeyValue(value, mValue, Interpolator.EASE_OUT);
		}
		KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValues1);
		KeyFrame keyFrame2 = new KeyFrame(Duration.millis(1000), keyValues2);
		// 若动画是第一次创建
		if (timeLine == null) {
			timeLine = new Timeline(keyFrame1, keyFrame2);
			timeLine.play();
			return;
		}
		// 清除之前的动画,重头开始播放
		timeLine.getKeyFrames().setAll(keyFrame1, keyFrame2);
		timeLine.playFromStart();
	}

	/** 停止动画 */
	public void stopAnimation() {
		if (timeLine != null && timeLine.getStatus() != Status.STOPPED) {
			timeLine.stop();
		}
	}
}