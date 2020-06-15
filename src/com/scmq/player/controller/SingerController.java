package com.scmq.player.controller;

import com.scmq.player.app.Main;
import com.scmq.player.model.Album;
import com.scmq.player.model.MV;
import com.scmq.player.model.Music;
import com.scmq.player.model.Page;
import com.scmq.player.model.PlayList;
import com.scmq.player.model.Singer;
import com.scmq.player.net.NetSource;
import com.scmq.player.service.AlbumService;
import com.scmq.player.service.MVService;
import com.scmq.player.service.MusicService;
import com.scmq.player.util.StringUtil;
import com.scmq.player.util.Task;
import com.scmq.player.view.SingerView;
import com.scmq.view.control.Spinner;
import com.scmq.view.control.Tab;
import com.scmq.view.control.TabPane;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 歌手模块控制器
 *
 * @author SCMQ
 */
@Controller
public class SingerController implements ChangeListener<Tab> {
	@Autowired
	private MusicService musicService;
	@Autowired
	private AlbumService albumService;
	@Autowired
	private MVService mvService;

	@Autowired
	private AlbumController albumController;

	private SingerView view;

	private Spinner spinner;

	private NetSource netSource;

	private Singer singer;

	private boolean songUpdatable, albumUpdatable, mvUpdatable;

	private Page songPage = new Page(), albumPage = new Page(), mvPage = new Page();

	public SingerController() {
	}

	void show(Singer singer, NetSource netSource, ObjectProperty<Tab> tabProperty) {
		this.netSource = netSource;
		if (view == null) {
			view = new SingerView();
			// (进度)旋转器
			spinner = new Spinner();
			TabPane tabPane = view.getTabPane();
			// 添加选项卡切换监听器
			tabPane.tabProperty().addListener(this);

			view.getPagination().addListener((observable, oldPage, newPage) -> {
				Tab tab = tabPane.tabProperty().get();
				String tabText = tab.getText();
				int current = newPage.intValue();
				// 若是“单曲”选项卡 且 单曲分页对象的当前页和分页组件当前页相同,则不触发更新
				if ("单曲" == tabText && songPage.getCurrent() != current) {
					songPage.setCurrent(current);
					songUpdatable = true;
					changed(null, null, tab);
					return;
				}
				// 若是“专辑”选项卡 且 专辑分页对象的当前页和分页组件当前页相同,则不触发更新
				if ("专辑" == tabText && albumPage.getCurrent() != current) {
					albumPage.setCurrent(current);
					albumUpdatable = true;
					changed(null, null, tab);
					return;
				}
				// 若是“MV”选项卡 且 MV分页对象的当前页和分页组件当前页相同,则不触发更新
				if ("MV" == tabText && mvPage.getCurrent() != current) {
					mvPage.setCurrent(current);
					mvUpdatable = true;
					changed(null, null, tab);
				}
			});
		}
		// 切换到歌手视图
		tabProperty.get().setContent(view);
		// 还是同一个歌手,不执行任何操作
		if (this.singer == singer) {
			return;
		}
		this.singer = singer;
		// 更新歌手信息
		if (isEmptyInfo(singer)) {
			Task.async(() -> {
				netSource.handleSingerInfo(singer);
				Platform.runLater(() -> view.updateSinger(singer));
			});
		} else {
			view.updateSinger(singer);
		}

		mvPage.reset();
		songPage.reset();
		albumPage.reset();
		songUpdatable = albumUpdatable = mvUpdatable = true;
		view.getTableView().getItems().clear();
		if ("单曲" != view.getTabPane().tabProperty().get().getText()) {
			view.getTabPane().tabProperty().set(view.getTabPane().getTabs().get(0));
		} else {
			changed(null, null, view.getTabPane().tabProperty().get());
		}
	}

	@Override
	public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
		String tabText = newValue.getText();
		if ("单曲" == tabText) {
			if (!songUpdatable) {
				view.updatePagination(songPage);
				return;
			}
			spinner.centerTo(view);
			songUpdatable = false;
			Task.async(() -> {
				List<Music> list = netSource.songList(singer, songPage);
				Platform.runLater(() -> {
					view.updateSong(list, songPage);
					spinner.close();
				});
				musicService.save(list);
			});
		} else if ("专辑" == tabText) {
			if (!albumUpdatable) {
				view.updatePagination(albumPage);
				return;
			}
			spinner.centerTo(view);
			albumUpdatable = false;
			Task.async(() -> {
				List<Album> list = netSource.albumList(singer, albumPage);
				// 批量保存专辑信息
				albumService.save(list);
				// 保存专辑图片
				albumService.handlePictures(list);
				Platform.runLater(() -> {
					view.updateAlbum(list, albumPage, albumNodeHandler);
					spinner.close();
				});
			});
		} else if ("MV" == tabText) {
			if (!mvUpdatable) {
				view.updatePagination(mvPage);
				return;
			}
			spinner.centerTo(view);
			mvUpdatable = false;
			Task.async(() -> {
				List<MV> list = netSource.mvList(singer, mvPage);
				// 批量保存MV信息
				mvService.save(list);
				// 批量保存MV图片
				mvService.handlePictures(list);
				Platform.runLater(() -> {
					view.updateMVList(list, mvPage, mvNodeHandler);
					spinner.close();
				});
			});
		} else if ("简介" == tabText && singer != null) {
			view.updateIntroduce(singer);
		}
	}

	private EventHandler<MouseEvent> albumNodeHandler = e -> {
		if (e.getButton() == MouseButton.PRIMARY) {
			Node node = (Node) e.getSource();
			Album album = (Album) node.getUserData();
			// 获取主选项卡面板
			TabPane tabPane = (TabPane) Main.getRoot().lookup(".tab-pane:vertical");
			// 显示歌手内容页面
			albumController.show(album, tabPane.tabProperty(), netSource);
			// 获取返回图标节点
			Node back = Main.getRoot().lookup("#top-pane #back");
			EventHandler<? super MouseEvent> oldHandler = back.getOnMouseClicked();
			back.setOnMouseClicked(event -> {
				Tab tab = tabPane.tabProperty().get();
				if (tab.getText() == "网络乐库") {
					tab.setContent(view);
					back.setOnMouseClicked(oldHandler);
				}
			});
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

	private boolean isEmptyInfo(Singer singer) {
		return singer.getSongNum() == null || singer.getAlbumNum() == null || singer.getMvNum() == null
				|| StringUtil.isEmpty(singer.getFollowNum()) || StringUtil.isEmpty(singer.getIntroduce());
	}
}
