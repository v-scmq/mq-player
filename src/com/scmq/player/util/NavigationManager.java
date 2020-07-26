package com.scmq.player.util;

import com.scmq.view.control.Tab;
import com.scmq.view.control.TabPane;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.LinkedList;

/**
 * 视图导航管理器.<br>
 * 这个类的方法操作虽然不是线程安全的,但是它的应用场景只是作为一个视图导航的管理作用. 因此,它不应该在非JavaFX的UI线程中运行.
 * 
 * @author SCMQ
 */
public final class NavigationManager {

	/**
	 * 这个类包装了视图导航的基本信息
	 */
	public static class Navigation {
		private Tab tab;
		private Node content;
		private TabPane tabPane;

		public Navigation(Tab tab, Node content, TabPane tabPane) {
			this.tab = tab;
			this.content = content;
			this.tabPane = tabPane;
		}

		@Override
		public String toString() {
			return "Navigation{" + "tab=" + tab + ", content=" + content + ", tabPane=" + tabPane + '}';
		}
	}

	/** 后退视图列表 */
	private final static LinkedList<Navigation> BACK_VIEW_LIST = new LinkedList<>();
	/** 前进视图列表 */
	private final static LinkedList<Navigation> FORWARD_VIEW_LIST = new LinkedList<>();

	private static Node backNode;
	private static Node forwardNode;

	private NavigationManager() throws IllegalAccessException {
		throw new IllegalAccessException("不允许创建此类的对象实例！");
	}

	public static void init(Node backNode, Node forwardNode) {
		NavigationManager.backNode = backNode;
		NavigationManager.forwardNode = forwardNode;
		backNode.setOnMouseClicked(NavigationManager::back);
		forwardNode.setOnMouseClicked(NavigationManager::forward);
	}

	private static void back(MouseEvent event) {
		LinkedList<Navigation> list = BACK_VIEW_LIST;

		System.out.println("\n=============== back =============");
		list.forEach(System.out::println);

		if (!list.isEmpty()) {
			Navigation navigation = list.removeLast();

			ChangeListener<Tab> listener = navigation.tabPane.getTabChangeListener();

			// 移除监听器,避免本次选项卡切换被加入到后退视图列表(即本次切换不作为导航)
			navigation.tabPane.setTabChangeListener(null);

			navigation.tabPane.tabProperty().set(navigation.tab);
			navigation.tab.setContent(navigation.content);

			// 重新设置之前的监听器
			navigation.tabPane.setTabChangeListener(listener);

			// if (backNode != null) {
			// backNode.setDisable(list.isEmpty());
			// }
		}
	}

	private static void forward(MouseEvent event) {
		LinkedList<Navigation> list = FORWARD_VIEW_LIST;

		System.out.println("\n=============== forward =============");
		list.forEach(System.out::println);

		if (!list.isEmpty()) {
			Navigation navigation = list.removeLast();

			ChangeListener<Tab> listener = navigation.tabPane.getTabChangeListener();
			// 移除监听器,避免本次选项卡切换被加入到前进视图列表(即本次切换不作为导航)
			navigation.tabPane.setTabChangeListener(null);

			navigation.tabPane.tabProperty().set(navigation.tab);
			navigation.tab.setContent(navigation.content);

			// if (forwardNode != null) {
			// forwardNode.setDisable(list.isEmpty());
			// }

			// 重新设置选项卡监听器
			navigation.tabPane.setTabChangeListener(listener);
		}
	}

	public static void addToBack(Navigation navigation) {
		LinkedList<Navigation> list = BACK_VIEW_LIST;
		System.out.println("add-back=>" + list);
		if (list.isEmpty()) {
			// 清除所有可以前进的视图
			FORWARD_VIEW_LIST.clear();
			// if (forwardNode != null) {
			// forwardNode.setDisable(true);
			// }
			// 禁用前进按钮
			// forwardButton.disable();
		}
		if (backNode != null) {
			backNode.setDisable(false);
		}
		list.addLast(navigation);
	}

	public static void addToForward(Navigation navigation) {
		FORWARD_VIEW_LIST.addLast(navigation);
		System.out.println("add-forward=>" + FORWARD_VIEW_LIST);
		if (forwardNode != null) {
			forwardNode.setDisable(false);
		}
	}
}
