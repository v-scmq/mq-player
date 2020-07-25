package com.scmq.player.controller;

import com.scmq.player.app.Main;
import com.scmq.player.model.MV;
import com.scmq.player.model.Music;
import com.scmq.player.model.Page;
import com.scmq.player.model.PlayList;
import com.scmq.player.model.Singer;
import com.scmq.player.model.Special;
import com.scmq.player.net.NetSource;
import com.scmq.player.service.MVService;
import com.scmq.player.service.MusicService;
import com.scmq.player.service.SingerService;
import com.scmq.player.service.SpecialService;
import com.scmq.player.util.Task;
import com.scmq.player.view.NetSearchView;
import com.scmq.view.control.Spinner;
import com.scmq.view.control.Tab;
import com.scmq.view.control.TabPane;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;

/**
 * 网络音乐搜索模块控制器
 *
 * @author SCMQ
 */
@Controller
public class SearchMusicController implements ChangeListener<Tab> {
	/** MV业务 */
	@Autowired
	private MVService mvService;
	/** 音乐业务 */
	@Autowired
	private MusicService musicService;
	/** 歌单业务 */
	@Autowired
	private SpecialService specialService;
	/** 歌手业务 */
	@Autowired
	private SingerService singerService;

	/** 歌手模块控制器 */
	@Autowired
	private SingerController singerController;

	/** 搜索模块视图 */
	private NetSearchView view;
	/** 进度旋转器 */
	private Spinner spinner;

	/** 歌曲、歌单、MV更新标记 */
	private boolean songUpdatable, specialUpdatable, mvUpdatable;
	/** 歌曲、歌单、MV分页对象 */
	private Page songPage = new Page(), specialPage = new Page(), mvPage = new Page();
	/** 网络音乐平台 */
	public static NetSource netSource;

	/** 搜索关键词 */
	private String text;
	/** 搜索匹配的歌手 */
	private Singer singer;

	public SearchMusicController() {
	}

	void show(String text, TabPane mainTabPane) {
		if (view == null) {
			view = new NetSearchView();
			spinner = new Spinner();
			view.getTabPane().tabProperty().addListener(this);
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
				// 若是“歌单”选项卡 且 专辑分页对象的当前页和分页组件当前页相同,则不触发更新
				if ("歌单".equals(tabText) && specialPage.getCurrent() != current) {
					specialPage.setCurrent(current);
					specialUpdatable = true;
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

		mainTabPane.centerProperty().set(view);
		view.requestFocus();

		if (!Objects.equals(this.text, text)) {
			this.text = text;
			mvPage.setCurrent(1);
			songPage.setCurrent(1);
			specialPage.setCurrent(1);
			songUpdatable = specialUpdatable = mvUpdatable = true;

			if (!"单曲".equals(view.getTabPane().tabProperty().get().getText())) {
				view.getTabPane().tabProperty().set(view.getTabPane().getTabs().get(0));
			} else {
				changed(null, null, view.getTabPane().tabProperty().get());
			}
		}
	}

	@Override
	public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
		String keyword = this.text, tabText = newValue.getText();
		if ("单曲".equals(tabText)) {
			if (!songUpdatable) {
				view.updatePagination(songPage);
				return;
			}
			spinner.centerTo(view);
			songUpdatable = false;

			Task.async(() -> {
				// 先从本地数据库查找
				Singer singer = singerService.findSingerByName(keyword, netSource.platformId());
				if (singer == null) {
					// 若未找到歌手信息,则从网络平台上查找
					List<Singer> singerList = netSource.singerSearch(keyword);
					// 缓存歌手数据到数据库
					singerService.save(singerList);
					// 缓存歌手图片到本地磁盘
					singerService.handlePictures(singerList);
					singer = singerList == null || singerList.isEmpty() ? null : singerList.get(0);
				} else {
					singerService.handlePicture(singer);
				}

				final Singer entity = singer;
				List<Music> list = netSource.songSearch(keyword, songPage, null);
				Platform.runLater(() -> {
					view.updateSong(list, songPage, this.singer = entity);
					if (view.getSingerImageView() != null && view.getSingerImageView().getOnMouseClicked() == null) {
						bind();
					}
					spinner.close();
				});
				musicService.save(list);
			});

		} else if ("歌单".equals(tabText)) {
			if (!specialUpdatable) {
				view.updatePagination(specialPage);
				return;
			}
			spinner.centerTo(view);
			specialUpdatable = false;
			Task.async(() -> {
				List<Special> list = netSource.specialSearch(keyword, specialPage);
				specialService.save(list);
				specialService.handlePictures(list);
				Platform.runLater(() -> {
					view.updateSpecial(list, specialPage);
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
				List<MV> list = netSource.mvSearch(keyword, mvPage);
				// 批量保存MV信息
				mvService.save(list);
				// 批量保存MV图片
				mvService.handlePictures(list);
				Platform.runLater(() -> {
					view.updateMVList(list, mvPage, mvNodeHandler);
					spinner.close();
				});
			});
		}
	}

	public Node getView() {
		return view;
	}

	/**
	 * MV节点 鼠标事件,用于播放MV
	 */
	private EventHandler<MouseEvent> mvNodeHandler = e -> {
		if (e.getButton() == MouseButton.PRIMARY) {
			Node node = (Node) e.getSource();
			@SuppressWarnings("unchecked")
			List<MV> mvList = (List<MV>) node.getParent().getUserData();
			Integer index = (Integer) node.getUserData();
			Main.playListProperty().set(new PlayList(index, mvList));
		}
	};

	private void bind() {
		ImageView imageView = view.getSingerImageView();
		TabPane tabPane = (TabPane) Main.getRoot().lookup(".tab-pane:vertical");
		Node back = Main.getRoot().lookup("#top-pane #back");
		ObjectProperty<Tab> property = tabPane.tabProperty();

		imageView.setOnMouseClicked(e -> {
			Tab tab = property.get();
			if (tab == null) {
				tab = tabPane.getTabs().get(0);
				property.set(tab);
			}
			Node oldView = tab.getContent();
			singerController.show(singer, netSource, property);

			EventHandler<? super MouseEvent> oldHandler = back.getOnMouseClicked();
			back.setOnMouseClicked(event -> {
				property.get().setContent(oldView);
				back.setOnMouseClicked(oldHandler);
			});
		});
	}
}
