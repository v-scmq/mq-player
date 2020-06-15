package com.scmq.view.control;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;

/**
 * 这个{@link TabPane}类,它充分利用{@link BorderPane}布局面板的特性,即上、右、下、左、中5个部分.<br>
 * 通常来说,选项卡面板最大区域放置的是Tab对应的内容节点(Node),而这个内容节点正好在BorderPane的中央是最理想的布局方式.<br>
 * 所有选项卡(Tab)都应该放在一个布局面板中,然后再放入选项卡面板中. Tab组件的排列由{@link Side}的枚举值决定<br>
 * 作为Tab的布局面板,若Tab水平排列({@link Side#TOP}||{@link Side#BOTTOM}),则可以采用{@link HBox}布局;
 * 若Tab垂直排列({@link Side#RIGHT}||{@link Side#LEFT},则可以采用{@link VBox}布局.
 *
 * @see HBox
 * @see VBox
 * @see Side
 * @see BorderPane
 * @author SCMQ
 */
public class TabPane extends BorderPane {
	/** 选项卡面板的样式类 */
	private static final String STYLE_CLASS = "tab-pane";

	/** 所有选项卡的布局面板样式类 */
	private static final String HEADER_CLASS = "header-container";

	/** 选项卡被选中时的CSS伪类 */
	private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");

	/** 选项卡垂直方向排列时,选项卡面板的CSS伪类 */
	private static final PseudoClass VERTICAL = PseudoClass.getPseudoClass("vertical");

	/** 选项卡水平方向排列时,选项卡面板的CSS伪类 */
	private static final PseudoClass HORIZONTAL = PseudoClass.getPseudoClass("horizontal");

	/** 选项卡排列方向属性 */
	private ObjectProperty<Side> sideProperty = new SimpleObjectProperty<>();

	/** 被选中选项卡 属性 */
	private ObjectProperty<Tab> tabProperty = new SimpleObjectProperty<>();

	/** 存储所有的选项卡 的可观察列表(List集合) */
	private ObservableList<Tab> tabs = FXCollections.observableArrayList();

	/** 时间轴动画 */
	private Timeline timeLine;

	/** 选项卡的布局面板 */
	private Pane tabHeaderPane;

	/** 是否启用选项卡的线条 属性 */
	private BooleanProperty tabLineProperty;

	/** 鼠标悬浮在 选项卡 上的事件处理器 */
	private EventHandler<MouseEvent> hoverHandler;

	/**
	 * 构造一个选项卡面板
	 *
	 * @param tabs
	 *            选项卡(可变参数|数组)
	 */
	public TabPane(Tab... tabs) {
		this(Side.TOP, tabs);
	}

	/**
	 * 构造一个选项卡面板
	 *
	 * @param side
	 *            选项卡排列方向
	 * @param tabs
	 *            选项卡(可变参数|数组)
	 */
	public TabPane(Side side, Tab... tabs) {
		getStyleClass().add(STYLE_CLASS);
		this.tabs.addAll(tabs);
		bindListener();
		sideProperty.set(side);
		if (tabs.length > 0) {
			tabProperty.set(tabs[0]);
		}
	}

	/** 注册事件监听器 */
	private void bindListener() {
		// Tab 所处位置改变事件
		sideProperty.addListener((observable, oldSide, newSide) -> {
			getChildren().remove(tabHeaderPane);
			boolean horizontal = newSide.isHorizontal();
			pseudoClassStateChanged(HORIZONTAL, horizontal);
			pseudoClassStateChanged(VERTICAL, !horizontal);
			// 当Tab位置是水平方向时,采用HBox作为Tab布局面板
			if (horizontal) {
				tabHeaderPane = tabHeaderPane instanceof HBox ? tabHeaderPane : new HBox();
				tabHeaderPane.getChildren().setAll(tabs);
				if (newSide == Side.TOP) {
					setTop(tabHeaderPane);
				} else {
					setBottom(tabHeaderPane);
				}
			}
			// 当Tab位置是垂直方向时,采用VBox作为Tab布局面板
			else {
				tabHeaderPane = tabHeaderPane instanceof VBox ? tabHeaderPane : new VBox();
				tabHeaderPane.getChildren().setAll(tabs);
				if (newSide == Side.LEFT) {
					setLeft(tabHeaderPane);
				} else {
					setRight(tabHeaderPane);
				}
			}
			ObservableList<String> classList = tabHeaderPane.getStyleClass();
			if (classList.isEmpty())
				classList.add(HEADER_CLASS);
			else
				classList.set(0, HEADER_CLASS);
		});

		// 已选择的Tab改变事件
		tabProperty.addListener((observable, oldTab, newTab) -> {
			if (oldTab != null && tabLineProperty != null && tabLineProperty.get()) {
				((Label) oldTab.getGraphic()).setPrefWidth(0);
			}
			boolean active;
			for (Tab tabItem : tabs) {
				tabItem.setSelected(active = tabItem == newTab);
				tabItem.pseudoClassStateChanged(SELECTED, active);
			}
			centerProperty().unbind();
			// 选项卡面板的内容节点绑定到选项卡的内容节点
			if (newTab != null) {
				centerProperty().bind(newTab.contentProperty());
			}
		});

		// 选项卡 动作事件
		EventHandler<ActionEvent> tabHandler = e -> tabProperty.set((Tab) e.getSource());
		// 注册选项卡 动作事件
		for (Tab tab : tabs) {
			tab.setOnAction(tabHandler);
		}

		// Tab List集合发生变化的改变事件
		tabs.addListener((Change<? extends Tab> c) -> {
			ObservableList<Node> list = tabHeaderPane.getChildren();
			while (c.next()) {
				if (c.wasRemoved()) {
					for (Tab tab : c.getRemoved()) {
						tab.setOnAction(null);
						tab.setOnMouseEntered(null);
						tab.setOnMouseExited(null);
						list.remove(tab);
					}
				} else if (c.wasAdded()) {
					List<? extends Tab> addedTabs = c.getAddedSubList();
					for (Tab tab : addedTabs) {
						tab.setOnAction(tabHandler);
						tab.setOnMouseEntered(hoverHandler);
						tab.setOnMouseExited(hoverHandler);
						if (!list.contains(tab)) {
							list.add(tab);
						}
					}
					// 若未选择任何选项卡,则默认选中第一个
					if (tabProperty.get() == null && !addedTabs.isEmpty()) {
						tabProperty.set(addedTabs.get(0));
					}
				}
			}
		});
	}

	/**
	 * 获取所有选项卡
	 *
	 * @return 选项卡可观察改变的List集合
	 */
	public ObservableList<Tab> getTabs() {
		return tabs;
	}

	/**
	 * 获取选项卡排列方向属性
	 *
	 * @return 排列方向属性
	 */
	public ObjectProperty<Side> sideProperty() {
		return sideProperty;
	}

	/**
	 * 获取选项卡属性
	 *
	 * @return 选项卡属性
	 */
	public ObjectProperty<Tab> tabProperty() {
		return tabProperty;
	}

	/**
	 * 获取选项卡线条属性
	 *
	 * @return 选项卡线条属性
	 */
	public BooleanProperty tabLineProperty() {
		if (tabLineProperty != null) {
			return tabLineProperty;
		}
		return tabLineProperty = new SimpleBooleanProperty(this, "tabLine", false) {
			@Override
			protected void invalidated() {
				boolean enable = get();
				timeLine = enable && timeLine == null ? new Timeline() : timeLine;
				hoverHandler = enable ? createHoverHandler() : null;
				for (Tab tab : tabs) {
					Label tabLine = enable ? new Label() : null;
					tab.setContentDisplay(ContentDisplay.BOTTOM);
					tab.setGraphic(tabLine);
					tab.setOnMouseEntered(hoverHandler);
					tab.setOnMouseExited(hoverHandler);
					if (enable) {
						tabLine.getStyleClass().set(0, "tab-line");
					}
				}
			}
		};
	}

	/**
	 * 创建鼠标悬浮时的事件处理器
	 *
	 * @return 鼠标悬浮事件处理器
	 */
	private EventHandler<MouseEvent> createHoverHandler() {
		return e -> {
			Tab tab = (Tab) e.getSource();
			if (tab.isSelected()) {
				return;
			}
			for (Tab tabItem : tabs) {
				if (!tabItem.isSelected()) {
					Label tabLine = (Label) tabItem.getGraphic();
					tabLine.setPrefWidth(0);
				}
			}
			timeLine.stop();
			Label label = (Label) tab.getGraphic();
			DoubleProperty property = label.prefWidthProperty();
			double min = (min = label.getMinWidth()) < 0 ? 0 : min;
			double max = (max = label.getMaxWidth()) <= min ? tab.getWidth() : max;
			KeyValue kv1 = new KeyValue(property, tab.isHover() ? min : max);
			KeyValue kv2 = new KeyValue(property, tab.isHover() ? max : min);
			KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1);
			KeyFrame kf2 = new KeyFrame(Duration.millis(300), kv2);
			timeLine.getKeyFrames().setAll(kf1, kf2);
			timeLine.play();
		};
	}
}