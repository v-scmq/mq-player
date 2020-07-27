package com.scmq.player.util;

import com.scmq.view.control.Tab;
import com.scmq.view.control.TabPane;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.LinkedList;

/**
 * 视图导航管理器.<br>
 * 这个类的方法操作虽然不是线程安全的,但是它的应用场景只是作为一个视图导航的管理作用. 因此,它不应该在非JavaFX的UI线程中运行.
 * 
 * @author SCMQ
 */
public final class NavigationManager {

	/** 回退图标 */
	private static Node backNode;

	/** 前进图标 */
	private static Node forwardNode;

	/** 后退视图列表 */
	private final static LinkedList<Navigation> BACK_VIEW_LIST = new LinkedList<>();
	/** 前进视图列表 */
	private final static LinkedList<Navigation> FORWARD_VIEW_LIST = new LinkedList<>();

	private NavigationManager() throws IllegalAccessException {
		throw new IllegalAccessException("不允许创建此类的对象实例！");
	}

	/**
	 * 初始化并关联后退和前进图标的事件
	 * 
	 * @param backNode
	 *            后退图标(不允许为null)
	 * @param forwardNode
	 *            前进图标(不允许为null)
	 */
	public static void initialize(Node backNode, Node forwardNode) {
		NavigationManager.backNode = backNode;
		NavigationManager.forwardNode = forwardNode;
		backNode.setOnMouseClicked(NavigationManager::back);
		forwardNode.setOnMouseClicked(NavigationManager::forward);
	}

	/**
	 * 开始后退
	 * 
	 * @param event
	 *            鼠标事件(后退图标被点击时触发)
	 */
	private static void back(MouseEvent event) {
		if (event.getButton() != MouseButton.PRIMARY) {
			return;
		}

		LinkedList<Navigation> list = BACK_VIEW_LIST;

		// 若可以后退
		if (!list.isEmpty()) {
			// 获取并移除 最近一个可以后退的视图导航数据对象
			Navigation navigation = list.removeLast();

			// 获取当前导航选项卡面板
			TabPane tabPane = navigation.tabPane;
			// 获得当前选项卡
			Tab current = tabPane.tabProperty().get();
			// 获得当前选项卡的内容视图
			Node content = current.getContent();
			// 添加到前进视图导航列表
			addToForward(new Navigation(current, content, tabPane));

			// 获取选项卡面板监听器,方便下面移除(避免重复添加导航对象,因为选项卡切换会触发添加导航对象)
			ChangeListener<Tab> listener = tabPane.getTabChangeListener();

			// 移除监听器,避免本次选项卡切换被加入到后退视图列表(即本次切换不作为导航)
			tabPane.setTabChangeListener(null);

			// 设置新的视图
			navigation.tab.setContent(navigation.content);
			// 设置新的选项卡
			tabPane.tabProperty().set(navigation.tab);

			// 重新设置之前的监听器
			tabPane.setTabChangeListener(listener);

			// 根据后退视图列表大小决定是否启用后退图标
			backNode.setDisable(list.isEmpty());
			// 让导航对象内部成员属性脱离引用(使得JVM尽快回收这个对象)
			navigation.release();
		}
	}

	/**
	 * 开始前进
	 * 
	 * @param event
	 *            鼠标事件(前进图标被点击时所触发)
	 */
	private static void forward(MouseEvent event) {
		if (event.getButton() != MouseButton.PRIMARY) {
			return;
		}

		LinkedList<Navigation> list = FORWARD_VIEW_LIST;

		// 若可以前进
		if (!list.isEmpty()) {
			// 获取并移除 最近一个可以前进的视图导航对象
			Navigation navigation = list.removeLast();

			// 获取当前导航选项卡面板
			TabPane tabPane = navigation.tabPane;
			// 获得当前选项卡
			Tab current = tabPane.tabProperty().get();
			// 获得当前选项卡的内容视图
			Node content = current.getContent();
			// 添加到后退视图导航列表
			BACK_VIEW_LIST.addLast(new Navigation(current, content, tabPane));
			// 启用回退图标
			backNode.setDisable(false);

			// 获取选项卡面板监听器,方便下面移除(避免重复添加导航对象,因为选项卡切换会触发添加导航对象)
			ChangeListener<Tab> listener = tabPane.getTabChangeListener();
			// 移除监听器,避免本次选项卡切换被加入到前进视图列表(即本次切换不作为导航)
			tabPane.setTabChangeListener(null);

			// 设置新的视图
			navigation.tab.setContent(navigation.content);
			// 设置新的选项卡
			tabPane.tabProperty().set(navigation.tab);

			// 重新设置选项卡监听器
			tabPane.setTabChangeListener(listener);

			// 根据前进视图列表大小决定是否启用前进图标
			forwardNode.setDisable(list.isEmpty());
			// 让导航对象内部成员属性脱离引用(使得JVM尽快回收这个对象)
			navigation.release();
		}
	}

	/**
	 * 添加一个新的后退导航对象
	 * 
	 * @param navigation
	 *            导航数据对象
	 */
	public static void addToBack(Navigation navigation) {
		LinkedList<Navigation> list = BACK_VIEW_LIST;

		if (list.isEmpty()) {
			// 清除所有可以前进的视图
			FORWARD_VIEW_LIST.forEach(Navigation::release);
			FORWARD_VIEW_LIST.clear();
			// 禁用前进按钮
			forwardNode.setDisable(true);
		}

		// 启用后退图标
		backNode.setDisable(false);
		// 添加到后退导航视图列表的最后一个
		list.addLast(navigation);
	}

	/**
	 * 添加一个新的前进导航对象
	 * 
	 * @param navigation
	 *            导航数据对象
	 */
	public static void addToForward(Navigation navigation) {
		// 添加到前进导航视图列表的最后一个
		FORWARD_VIEW_LIST.addLast(navigation);
		// 启用前进图标
		forwardNode.setDisable(false);
	}

	/** 这个类包装了视图导航的基本信息 */
	public static class Navigation {
		/** 导航必需的选项卡 */
		private Tab tab;
		/** 导航必需的选项卡内容视图 */
		private Node content;
		/** 导航必需的选项卡面板 */
		private TabPane tabPane;

		/**
		 * 构造一个视图导航数据对象
		 * 
		 * @param tab
		 *            导航必需的选项卡属性
		 * @param content
		 *            导航必需的选项卡内容视图
		 * @param tabPane
		 *            导航必需的选项卡面板
		 */
		public Navigation(Tab tab, Node content, TabPane tabPane) {
			this.tab = tab;
			this.content = content;
			this.tabPane = tabPane;
		}

		/** 让对象内部成员属性脱离引用 */
		private void release() {
			this.tab = null;
			this.tabPane = null;
			this.content = null;
		}

		@Override
		public String toString() {
			return "Navigation{" + "tab=" + tab + ", content=" + content + ", tabPane=" + tabPane + '}';
		}
	}
}
