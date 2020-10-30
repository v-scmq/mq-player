package com.scmq.player.controller;

import com.scmq.player.app.App;
import com.scmq.player.util.NavigationManager;
import com.scmq.player.view.SettingView;
import com.scmq.view.control.Tab;
import com.scmq.view.control.TabPane;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import org.springframework.stereotype.Controller;

@Controller
public class SettingController {
	private SettingView view;

	private TabPane mainTabPane;

	void show() {
		if (view == null) {
			view = new SettingView();

			mainTabPane = (TabPane) App.getRoot().lookup(".tab-pane:vertical");
		}

		// 获得主选项卡
		Tab oldTab = mainTabPane.tabProperty().get();
		Node oldContent = oldTab.getContent();

		// 当搜素内容没有发生改变 并且 当前已经显示搜索视图,那么什么也不做
		if (oldContent == view) {
			return;
		}

		// 添加到后退视图列表
		NavigationManager.addToBack(new NavigationManager.Navigation(oldTab, oldContent, mainTabPane));

		ChangeListener<Tab> listener = mainTabPane.getTabChangeListener();
		mainTabPane.setTabChangeListener(null);
		Tab placeHolder = mainTabPane.getPlaceHolderTab();
		// 设置新的视图
		placeHolder.setContent(view);
		mainTabPane.tabProperty().set(placeHolder);
		// 重设置监听器
		mainTabPane.setTabChangeListener(listener);

		view.requestFocus();
	}
}
