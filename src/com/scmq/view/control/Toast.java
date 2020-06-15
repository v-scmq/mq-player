package com.scmq.view.control;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * 模拟安卓的Toast(消息提示组件),通常弹出的消息内容是位于布局容器的中央.<br>
 * Toast类的实例是单例的 ,使用时必须保证是UI线程(UI更新也必须是UI线程).下面是一段示例代码
 *
 * <pre>
 * public class Main extends Application {
 * 	public void start(Stage primaryStage) {
 * 		Button button = new Button("显示Toast消息");
 * 		AnchorPane root = new AnchorPane(button);
 * 		Scene scene = new Scene(root, 1000, 800);
 * 		primaryStage.setScene(scene);
 * 		primaryStage.show();
 * 		button.setOnAction(e -> Toast.makeText(root, "这是一个Toast消息提示!").show());
 * 	}
 * }
 * </pre>
 *
 * @author SCMQ
 */
public final class Toast {
	/** 布局面板 */
	private Pane root;
	/** 标签,用于显示消息内容 */
	private Label label;
	/** 内容布局容器 */
	private AnchorPane pane;
	/** 时间轴动画 */
	private Timeline timeLine = new Timeline();
	/** 单例Toast */
	private static final Toast TOAST = new Toast();

	/** 构造一个默认的Toast(消息提示)对象 */
	private Toast() {
		label = new Label();
		label.setFont(new Font(18));
		label.setTextFill(Color.WHITE);
		label.setAlignment(Pos.CENTER);
		label.setPadding(new Insets(32, 36, 32, 36));

		BackgroundFill fill = new BackgroundFill(Color.rgb(56, 56, 56), new CornerRadii(18), null);
		label.setBackground(new Background(fill));

		pane = new AnchorPane(label);
		// 不接受鼠标事件
		pane.setMouseTransparent(true);
		pane.setManaged(false);

		DoubleProperty opacity = label.opacityProperty();

		KeyValue kv1 = new KeyValue(opacity, 1);
		KeyFrame kf1 = new KeyFrame(Duration.millis(0), kv1);

		KeyValue kv2 = new KeyValue(opacity, 0.6);
		KeyFrame kf2 = new KeyFrame(Duration.millis(3000), kv2);

		timeLine.getKeyFrames().addAll(kf1, kf2);
	}

	/**
	 * 通过布局面板和消息内容来获取 “消息显示组件”
	 *
	 * @param root
	 *            消息显示的父布局面板,
	 * @param text
	 *            消息内容字符串
	 * @return 消息提示(Toast)对象
	 */
	public static Toast makeText(Pane root, String text) {
		TOAST.root = root;
		Label label = TOAST.label;
		label.setText(text);
		AnchorPane pane = TOAST.pane;
		pane.layoutXProperty().unbind();
		pane.layoutYProperty().unbind();
		// 必须使用Label的宽高属性,它的父布局容器,因为它的布局容器不被最底层布局容器管理,不能获得宽高
		// 如果直接让Label不被布局容器管理,Label不会显示,无法达到预期目标
		pane.layoutXProperty().bind(root.widthProperty().subtract(label.widthProperty()).multiply(0.5));
		pane.layoutYProperty().bind(root.heightProperty().subtract(label.heightProperty()).multiply(0.5));

		TOAST.timeLine.setOnFinished(e -> TOAST.root.getChildren().remove(pane));
		return TOAST;
	}

	/** 显示Toast消息 */
	public void show() {
		timeLine.stop();
		ObservableList<Node> nodes = root.getChildren();
		if (!nodes.contains(pane)) {
			nodes.add(pane);
		}
		timeLine.play();
	}
}
