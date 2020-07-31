package com.scmq.player.controller;

import com.scmq.player.app.Main;
import com.scmq.player.model.Album;
import com.scmq.player.model.Music;
import com.scmq.player.model.Page;
import com.scmq.player.net.NetSource;
import com.scmq.player.service.MusicService;
import com.scmq.player.util.NavigationManager;
import com.scmq.player.util.NavigationManager.Navigation;
import com.scmq.player.util.Task;
import com.scmq.player.view.AlbumView;
import com.scmq.view.control.Spinner;
import com.scmq.view.control.Tab;
import com.scmq.view.control.TabPane;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;

/**
 * 专辑控制器
 *
 * @author SCMQ
 */
@Controller
public class AlbumController implements ChangeListener<Number> {
	/** 音乐业务对象 */
	@Autowired
	private MusicService musicService;
	/** 专辑视图 */
	private AlbumView view;
	/** 进度旋转器 */
	private Spinner spinner;
	/** 专辑歌曲列表分页对象 */
	private Page page = new Page();
	/** 网络音乐平台 */
	private NetSource netSource;
	/** 当前专辑视图的专辑信息 */
	private Album album;

	/** 主选项卡面板 */
	private TabPane mainTabPane;

	/** 构造一个默认的专辑控制器 */
	public AlbumController() {
	}

	/**
	 * 显示专辑视图页面
	 *
	 * @param album
	 *            专辑信息
	 * @param netSource
	 *            网络音乐平台
	 */
	void show(Album album, NetSource netSource) {
		if (view == null) {
			view = new AlbumView();
			spinner = new Spinner();
			this.netSource = netSource;
			view.getPagination().addListener(this);
			mainTabPane = (TabPane) Main.getRoot().lookup(".tab-pane:vertical");
		}

		Tab tab = mainTabPane.tabProperty().get();
		// 添加到后退导航视图列表
		NavigationManager.addToBack(new Navigation(tab, tab.getContent(), mainTabPane));

		ChangeListener<Tab> listener = mainTabPane.getTabChangeListener();
		mainTabPane.setTabChangeListener(null);

		// 设置新的视图(专辑页面)
		Tab placeHolder = mainTabPane.getPlaceHolderTab();
		placeHolder.setContent(view);
		mainTabPane.tabProperty().set(placeHolder);
		// 重设置监听器
		mainTabPane.setTabChangeListener(listener);

		if (Objects.equals(this.album, album)) {
			return;
		}

		page.reset();
		this.album = album;
		spinner.centerTo(view);
		view.updateAlbum(album);

		Task.async(() -> {
			List<Music> list = netSource.songList(album, page);
			Platform.runLater(() -> updateSongList(list, album));
		});
	}

	/**
	 * 专辑的歌曲数据分页发生改变时,回调此方法.
	 * 
	 * @param observable
	 *            可观察对象
	 * @param oldValue
	 *            上一次的分页
	 * @param newValue
	 *            新的分页
	 */
	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		page.setCurrent(newValue.intValue());
		spinner.centerTo(view);
		Task.async(() -> {
			List<Music> list = netSource.songList(album, page);
			Platform.runLater(() -> updateSongList(list, null));
			musicService.save(list);
		});
	}

	/**
	 * 更新歌曲列表
	 * 
	 * @param list
	 *            歌曲数据List集合
	 * @param album
	 *            专辑信息。若不为null,将更新专辑简介信息。
	 */
	private void updateSongList(List<Music> list, Album album) {
		if (album != null) {
			view.updateIntroduce(album);
		}
		TableView<Music> tableView = view.getTableView();
		tableView.getItems().setAll(list);
		view.updatePagination(page);
		spinner.close();
	}
}
