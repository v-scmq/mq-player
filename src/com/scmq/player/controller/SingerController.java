package com.scmq.player.controller;

import com.scmq.player.app.Main;
import com.scmq.player.model.Album;
import com.scmq.player.model.MV;
import com.scmq.player.model.Music;
import com.scmq.player.model.Page;
import com.scmq.player.model.PlayList;
import com.scmq.player.model.Singer;
import com.scmq.player.service.AlbumService;
import com.scmq.player.service.MVService;
import com.scmq.player.service.MusicService;
import com.scmq.player.service.SingerService;
import com.scmq.player.util.NavigationManager;
import com.scmq.player.util.NavigationManager.Navigation;
import com.scmq.player.util.Task;
import com.scmq.player.util.ViewRestore;
import com.scmq.player.view.SingerView;
import com.scmq.view.control.Spinner;
import com.scmq.view.control.Tab;
import com.scmq.view.control.TabPane;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;

/**
 * 歌手模块控制器
 *
 * @author SCMQ
 */
@Controller
public class SingerController implements ChangeListener<Tab> {
	/** 音乐业务 */
	@Autowired
	private MusicService musicService;
	/** 专辑业务 */
	@Autowired
	private AlbumService albumService;
	/** MV业务 */
	@Autowired
	private MVService mvService;
	/** 歌手业务 */
	@Autowired
	private SingerService singerService;
	/** 专辑控制器 */
	@Autowired
	private AlbumController albumController;
	@Autowired
	private ConfigureController configureController;

	/** 歌手视图 */
	private SingerView view;
	/** 进度旋转器(网络请求时使用) */
	private Spinner spinner;
	/** 当前歌手视图的歌手信息 */
	private Singer singer;
	/** 歌曲、专辑、MV的可更新标记 */
	private boolean songUpdatable, albumUpdatable, mvUpdatable;
	/** 歌曲、专辑、MV的分页对象 */
	private Page songPage = new Page(), albumPage = new Page(), mvPage = new Page();

	private TabPane mainTabPane;

	private void restore(Object data) {
		if (Objects.equals(singer, data)) {
			return;
		}

		ViewRestore.setData(view, singer = (Singer) data);
		// 更新歌手信息
		if (singer.isEmptyInfo()) {
			Task.async(() -> {
				// 先从数据数据库表查询
				Singer info = singerService.findSingerByMid(singer.getMid(), singer.getPlatform());
				if (info != null && !info.isEmptyInfo()) {
					singerService.cloneOfBaseInfo(info, singer);
					Platform.runLater(() -> view.updateSinger(singer));
					return;
				}
				configureController.getNetSourceImpl().handleSingerInfo(singer);
				Platform.runLater(() -> view.updateSinger(singer));
				singerService.update(singer);
			});
		} else {
			view.updateSinger(singer);
		}

		mvPage.reset();
		songPage.reset();
		albumPage.reset();
		songUpdatable = albumUpdatable = mvUpdatable = true;
		view.getTableView().getItems().clear();

		if (!"单曲".equals(view.getTabPane().tabProperty().get().getText())) {
			view.getTabPane().tabProperty().set(view.getTabPane().getTabs().get(0));
		} else {
			changed(null, null, view.getTabPane().tabProperty().get());
		}
	}

	/**
	 * 显示歌手视图
	 * 
	 * @param singer
	 *            歌手信息对象
	 *
	 */
	void show(Singer singer) {
		if (view == null) {
			// (进度)旋转器
			spinner = new Spinner();
			view = new SingerView();
			// 绑定视图数据恢复
			ViewRestore.bind(view, this::restore);
			// 添加选项卡切换监听器
			view.getTabPane().setTabChangeListener(this);
			mainTabPane = (TabPane) Main.getRoot().lookup(".tab-pane:vertical");

			view.getPagination().addListener((observable, oldPage, newPage) -> {
				Tab tab = view.getTabPane().tabProperty().get();
				String tabText = tab.getText();
				int current = newPage.intValue();
				// 若是“单曲”选项卡 且 单曲分页对象的当前页和分页组件当前页相同,则不触发更新
				if ("单曲".equals(tabText) && songPage.getCurrent() != current) {
					songPage.setCurrent(current);
					songUpdatable = true;
					changed(null, null, tab);
					return;
				}
				// 若是“专辑”选项卡 且 专辑分页对象的当前页和分页组件当前页相同,则不触发更新
				if ("专辑".equals(tabText) && albumPage.getCurrent() != current) {
					albumPage.setCurrent(current);
					albumUpdatable = true;
					changed(null, null, tab);
					return;
				}
				// 若是“MV”选项卡 且 MV分页对象的当前页和分页组件当前页相同,则不触发更新
				if ("MV".equals(tabText) && mvPage.getCurrent() != current) {
					mvPage.setCurrent(current);
					mvUpdatable = true;
					changed(null, null, tab);
				}
			});
		}

		Tab oldTab = mainTabPane.tabProperty().get();
		Node oldContent = oldTab.getContent();

		boolean noChanged = Objects.equals(this.singer, singer);
		if (noChanged && oldContent == view) {
			return;
		}

		NavigationManager.addToBack(new Navigation(oldTab, oldContent, mainTabPane));

		ChangeListener<Tab> listener = mainTabPane.getTabChangeListener();
		mainTabPane.setTabChangeListener(null);
		// 切换到歌手视图
		Tab placeHolder = mainTabPane.getPlaceHolderTab();
		placeHolder.setContent(view);
		mainTabPane.tabProperty().set(placeHolder);
		// 重设置监听器
		mainTabPane.setTabChangeListener(listener);

		// 还是同一个歌手,不执行任何操作
		if (noChanged) {
			return;
		}

		ViewRestore.setData(view, this.singer = singer);
		// 更新歌手信息
		if (singer.isEmptyInfo()) {
			Task.async(() -> {
				// 先从数据数据库表查询
				Singer data = singerService.findSingerByMid(singer.getMid(), singer.getPlatform());
				if (data != null && !data.isEmptyInfo()) {
					singerService.cloneOfBaseInfo(data, singer);
					Platform.runLater(() -> view.updateSinger(singer));
					return;
				}
				configureController.getNetSourceImpl().handleSingerInfo(singer);
				Platform.runLater(() -> view.updateSinger(singer));
				singerService.update(singer);
			});
		} else {
			view.updateSinger(singer);
		}

		mvPage.reset();
		songPage.reset();
		albumPage.reset();
		songUpdatable = albumUpdatable = mvUpdatable = true;
		view.getTableView().getItems().clear();

		if (!"单曲".equals(view.getTabPane().tabProperty().get().getText())) {
			view.getTabPane().tabProperty().set(view.getTabPane().getTabs().get(0));
		} else {
			changed(null, null, view.getTabPane().tabProperty().get());
		}
	}

	// 歌手视图中的选项卡选择改变事件回调
	@Override
	public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
		if (observable != null && oldValue != null) {
			// 添加到后退视图列表
			NavigationManager.addToBack(new Navigation(oldValue, oldValue.getContent(), view.getTabPane()));
		}

		String tabText = newValue.getText();
		if ("单曲".equals(tabText)) {
			if (!songUpdatable) {
				view.updatePagination(songPage);
				return;
			}
			spinner.centerTo(view);
			songUpdatable = false;
			Task.async(() -> {
				List<Music> list = configureController.getNetSourceImpl().songList(singer, songPage);
				Platform.runLater(() -> {
					view.updateSong(list, songPage);
					spinner.close();
				});
				musicService.save(list);
			});
		} else if ("专辑".equals(tabText)) {
			if (!albumUpdatable) {
				view.updatePagination(albumPage);
				return;
			}
			spinner.centerTo(view);
			albumUpdatable = false;
			Task.async(() -> {
				List<Album> list = configureController.getNetSourceImpl().albumList(singer, albumPage);
				// 批量保存专辑信息
				albumService.save(list);
				// 保存专辑图片
				albumService.handlePictures(list);
				Platform.runLater(() -> {
					view.updateAlbum(list, albumPage, albumNodeHandler);
					spinner.close();
				});
			});
		} else if ("MV".equals(tabText)) {
			if (!mvUpdatable) {
				view.updatePagination(mvPage);
				return;
			}
			spinner.centerTo(view);
			mvUpdatable = false;
			Task.async(() -> {
				List<MV> list = configureController.getNetSourceImpl().mvList(singer, mvPage);
				// 批量保存MV信息
				mvService.save(list);
				// 批量保存MV图片
				mvService.handlePictures(list);
				Platform.runLater(() -> {
					view.updateMVList(list, mvPage, mvNodeHandler);
					spinner.close();
				});
			});
		} else if ("简介".equals(tabText) && singer != null) {
			view.updateIntroduce(singer);
		}
	}

	/** 专辑数据节点鼠标点击事件处理器 */
	private EventHandler<MouseEvent> albumNodeHandler = e -> {
		if (e.getButton() == MouseButton.PRIMARY) {
			Node node = (Node) e.getSource();
			Album album = (Album) node.getUserData();

			// 显示歌手内容页面
			albumController.show(album);
		}
	};

	/** MV节点 鼠标事件,用于播放MV */
	private EventHandler<MouseEvent> mvNodeHandler = e -> {
		if (e.getButton() == MouseButton.PRIMARY) {
			Node node = (Node) e.getSource();
			@SuppressWarnings("unchecked")
			List<MV> mvList = (List<MV>) node.getParent().getUserData();
			Integer index = (Integer) node.getUserData();
			Main.playListProperty().set(new PlayList(index, mvList));
		}
	};
}
