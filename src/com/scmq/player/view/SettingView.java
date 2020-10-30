package com.scmq.player.view;

import com.scmq.view.control.Tab;
import com.scmq.view.control.TabPane;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 * 系统设定视图
 * 
 * @since 2020-10-25
 * 
 */
public class SettingView extends TabPane {
	private ObservableList<Node> dataSourceList;

	public SettingView() {

		/*-----------------音乐数据源---------------*/
		FlowPane flowPane = new FlowPane(30, 20);
		dataSourceList = flowPane.getChildren();
		VBox box = new VBox(10, new Label("音乐数据源"), flowPane);

		getTabs().addAll(new Tab("常规设置", box));
		tabLineProperty().set(true);
	}

	/**
	 * 获取音乐数据源节点List
	 * 
	 * @return 音乐数据源视图节点List
	 */
	public ObservableList<Node> getDataSourceList() {
		return dataSourceList;
	}
}
