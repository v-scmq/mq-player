package com.scmq.player.controller;

import com.scmq.player.app.Main;
import com.scmq.player.model.MV;
import com.scmq.player.model.Music;
import com.scmq.player.model.Page;
import com.scmq.player.model.PlayList;
import com.scmq.player.model.Rank;
import com.scmq.player.model.RankItem;
import com.scmq.player.model.Singer;
import com.scmq.player.model.Special;
import com.scmq.player.model.Tag;
import com.scmq.player.net.NetSource;
import com.scmq.player.service.AlbumService;
import com.scmq.player.service.MVService;
import com.scmq.player.service.MusicService;
import com.scmq.player.service.SingerService;
import com.scmq.player.service.SpecialService;
import com.scmq.player.util.NavigationManager;
import com.scmq.player.util.NavigationManager.Navigation;
import com.scmq.player.util.Task;
import com.scmq.player.view.NetMusicView;
import com.scmq.view.control.Pagination;
import com.scmq.view.control.Spinner;
import com.scmq.view.control.Tab;
import com.scmq.view.control.TabPane;
import com.scmq.view.control.Toast;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 网络曲库模块控制器
 *
 * @author SCMQ
 */
public class NetMusicController implements ChangeListener<Tab> {
	@Autowired
	private MVService mvService;
	@Autowired
	private AlbumService albumService;
	@Autowired
	private SingerService singerService;
	@Autowired
	private SpecialService specialService;
	@Autowired
	private MusicService musicService;
	@Autowired
	private SingerController singerController;

	/** 网络音乐模块视图 */
	private NetMusicView view;
	/** 进度旋转器 */
	private Spinner spinner;
	/** 当前已选榜单项 */
	private RankItem rankItem;
	/** 网络音乐平台 */
	private NetSource netSource;

	/** MV数据分页 */
	private Page mvPage = new Page();
	/** 榜单中的歌曲数据分页 */
	private Page rankPage = new Page();
	/** 歌手数据分页 */
	private Page singerPage = new Page();
	/** 歌单数据分页 */
	private Page specialPage = new Page();

	/* 歌手信息是否可更新 */
	private boolean singerUpdatable = true;
	/** 歌单信息是否可更新 */
	private boolean specialUpdatable = true;
	/** 榜单信息是否可更新 */
	private boolean rankUpdatable = true;
	/** MV信息是否可更新 */
	private boolean mvUpdatable = true;

	/** 歌手分类、歌手拼音、歌单分类、MV分类选择 */
	private SelectionModel<Tag> kindSection, enSelection, specialSelection, mvSelection;

	/**
	 * Tag(标签)、榜单项(RankItem)改变事件. 这两种类型的改变都通过一个监听器来处理,对于榜单项必须重设为newValue,否则无法获取最新已选榜单项
	 */
	private ChangeListener<Object> listener = (observable, oldValue, newValue) -> {
		Tab tab = view.tabProperty().get();
		String tabText = tab.getText();
		// 若是“歌手”选项卡,则触发歌手数据更新
		if ("歌手".equals(tabText)) {
			singerPage.reset();
			singerUpdatable = true;
			changed(null, null, tab);
			return;
		}
		// 若是“歌单”选项卡,则触发歌单数据更新
		if ("歌单".equals(tabText)) {
			specialPage.reset();
			specialUpdatable = true;
			changed(null, null, tab);
			return;
		}
		// 若是“MV”选项卡,则触发MV数据更新
		if ("MV".equals(tabText)) {
			mvPage.reset();
			mvUpdatable = true;
			changed(null, null, tab);
			return;
		}
		// 若是“排行榜”选项卡 且 newValue(RankItem)不是null,则触发排行榜数据更新
		if ("排行榜".equals(tabText) && newValue != null) {
			rankPage.reset();
			rankUpdatable = true;
			// 重设当前已选 榜单项
			rankItem = (RankItem) newValue;
			changed(null, null, tab);
		}
	};

	/** Tab(选项卡)、Pagination(分页组件)、ListCell(列表单元格) 鼠标事件过滤器 */
	private EventHandler<MouseEvent> filter = e -> {
		if (!spinner.isPlaying()) {
			return;
		}
		Node node = (Node) e.getSource();
		// 若是 选项卡 或 分页组件
		if (node instanceof Tab || Pagination.SELECTED_CLASS.equals(node.getStyleClass().get(0))) {
			e.consume();
			Toast.makeText(Main.getRoot(), "上一次请求还未完成，请等待！").show();
			return;
		}
		// 若是 列表单元格
		if (node instanceof ListCell) {
			if (e.getButton() != MouseButton.PRIMARY) {
				e.consume();
				return;
			}
			ListCell<?> cell = (ListCell<?>) node;
			if (!cell.isSelected() && !cell.isEmpty()) {
				Toast.makeText(Main.getRoot(), "上一次请求还未完成，请等待！").show();
				e.consume();
			}
		}
	};

	/** 歌手节点的鼠标单击事件(打开歌手子模块视图) */
	private EventHandler<MouseEvent> singerNodeHandler = e -> {
		if (e.getButton() == MouseButton.PRIMARY) {
			Singer singer = (Singer) ((Node) e.getSource()).getUserData();
			// 获取主选项卡面板
			TabPane tabPane = (TabPane) Main.getRoot().lookup(".tab-pane:vertical");
			// 显示歌手内容页面
			singerController.show(singer, netSource);
			// // 获取返回图标节点
			// Node back = Main.getRoot().lookup("#top-pane #back");
			// back.setOnMouseClicked(event -> {
			// Tab tab = tabPane.tabProperty().get();
			// if ("网络乐库".equals(tab.getText())) {
			// tab.setContent(view);
			// back.setOnMouseClicked(null);
			// }
			// });
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

	/**
	 * 构造一个网络音乐控制器
	 * 
	 * @param netSourceList
	 *            网络音乐平台实现类
	 */
	public NetMusicController(List<NetSource> netSourceList) {
		this.netSource = netSourceList.get(0);
		MainController.netSource = this.netSource;
		NetSearchController.netSource = this.netSource;
	}

	@Override
	public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
		if (observable != null && oldValue != null) {
			// 获得主选项卡
			// 添加到后退视图列表
			NavigationManager.addToBack(new Navigation(oldValue, oldValue.getContent(), view));
		}

		String tabText = newValue.getText();
		// 切换的“歌手”选项卡
		if ("歌手".equals(tabText)) {
			if (!singerUpdatable) {
				view.updatePagination(singerPage);
				return;
			}
			singerUpdatable = false;
			spinner.centerTo(view);
			Tag kind = kindSection.getSelectedItem(), en = enSelection.getSelectedItem();
			Task.async(() -> {
				List<Singer> list = netSource.singerList(singerPage, kind, en);
				singerService.save(list);
				singerService.handlePictures(list);
				Platform.runLater(() -> updateSinger(list));
			});
		}
		// 切换的“歌单”选项卡
		else if ("歌单".equals(tabText)) {
			if (!specialUpdatable) {
				view.updatePagination(specialPage);
				return;
			}
			specialUpdatable = false;
			spinner.centerTo(view);
			Task.async(() -> {
				List<Special> list = netSource.specialList(specialSelection.getSelectedItem(), specialPage);
				specialService.save(list);
				specialService.handlePictures(list);
				Platform.runLater(() -> updateSpecial(list));
			});
		}
		// 切换的“MV”选项卡
		else if ("MV".equals(tabText)) {
			if (!mvUpdatable) {
				view.updatePagination(mvPage);
				return;
			}
			mvUpdatable = false;
			spinner.centerTo(view);
			Task.async(() -> {
				List<MV> list = netSource.mvList(mvSelection.getSelectedItem(), mvPage);
				mvService.save(list);
				mvService.handlePictures(list);
				Platform.runLater(() -> updateMV(list));
			});
		}
		// 切换的“排行榜”选项卡
		else if ("排行榜".equals(tabText)) {
			if (!rankUpdatable) {
				view.updatePagination(rankPage);
				return;
			}
			rankUpdatable = false;
			spinner.centerTo(view);
			Task.async(() -> {
				List<Music> list = netSource.songList(rankItem, rankPage);
				Platform.runLater(() -> updateRank(list));
				musicService.save(list);
			});
		}
	}

	void bind(NetMusicView view) {
		this.view = view;
		spinner = new Spinner();
		mvSelection = view.getMvTagListView().getSelectionModel();
		enSelection = view.getSingerEnListView().getSelectionModel();
		kindSection = view.getSingerKindListView().getSelectionModel();
		specialSelection = view.getSpecialTagListView().getSelectionModel();

		Callback<ListView<Tag>, ListCell<Tag>> call = listVew -> new TagListCell();
		view.getMvTagListView().setCellFactory(call);
		view.getSingerEnListView().setCellFactory(call);
		view.getSingerKindListView().setCellFactory(call);
		view.getSpecialTagListView().setCellFactory(call);

		// 选项卡切换事件
		view.setTabChangeListener(this);

		// 分页组件,当前页编号改变事件
		view.getPagination().addListener((observable, oldPage, newPage) -> {
			Tab tab = view.tabProperty().get();
			String tabText = tab.getText();
			int current = newPage.intValue();
			// 若是“歌手”选项卡,且歌手分页对象的当前页和分页组件当前页相同,则不触发更新
			if ("歌手".equals(tabText) && singerPage.getCurrent() != current) {
				singerPage.setCurrent(current);
				singerUpdatable = true;
				changed(null, null, tab);
				return;
			}
			// 若是“歌单”选项卡,且歌单分页对象的当前页和分页组件当前页相同,则不触发更新
			if ("歌单".equals(tabText) && specialPage.getCurrent() != current) {
				specialPage.setCurrent(current);
				specialUpdatable = true;
				changed(null, null, tab);
				return;
			}
			// 若是“MV”选项卡,且MV分页对象的当前页和分页组件当前页相同,则不触发更新
			if ("MV".equals(tabText) && mvPage.getCurrent() != current) {
				mvPage.setCurrent(current);
				mvUpdatable = true;
				changed(null, null, tab);
				return;
			}
			// 若是“排行榜”选项卡,且排行榜分页对象的当前页和分页组件当前页相同,则不触发更新
			if ("排行榜".equals(tabText) && rankPage.getCurrent() != current) {
				rankPage.setCurrent(current);
				rankUpdatable = true;
				changed(null, null, tab);
			}
		});

		// 主选项卡面板的 “网络乐库”选项卡 被选择时,网络乐库视图得以显示,此时开始加载数据
		view.parentProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				view.parentProperty().removeListener(this);
				changed(null, null, view.tabProperty().get());
			}
		});

		// 分页组件 内部 跳转页按钮 和 页编号输入框的 动作事件
		EventHandler<ActionEvent> action = e -> {
			Pagination pagination = view.getPagination();
			String in = pagination.getPageInput().getText();
			int current;
			if (in.length() == 0 || (current = Integer.parseInt(in)) == pagination.getPage()) {
				return;
			}
			if (spinner.isPlaying()) {
				Toast.makeText(Main.getRoot(), "上一次请求还未完成，请等待！").show();
			} else {
				pagination.setPage(current);
			}
		};
		view.getPagination().addEventFilter(filter, action);
		for (Tab tab : view.getTabs()) {
			tab.addEventFilter(MouseEvent.MOUSE_PRESSED, filter);
		}
	}

	private void updateRank(List<Music> list) {
		List<Rank> ranks = netSource.rankList();
		ObservableList<TitledPane> nodes = view.getRankAccordion().getPanes();
		// 如果需要更新榜单分类标签信息
		if (nodes.isEmpty() && ranks != null && !ranks.isEmpty()) {
			@SuppressWarnings("unchecked")
			ListView<RankItem>[] listViews = new ListView[ranks.size()];
			int index = 0;
			for (Rank rank : ranks) {
				ListView<RankItem> rankListView = listViews[index++] = new ListView<>();
				rankListView.getStyleClass().add("rank-list-view");
				rankListView.getItems().addAll(rank.getItems());
				if (rankItem == null) {
					rankListView.getSelectionModel().select(0);
					rankItem = rankListView.getSelectionModel().getSelectedItem();
				}
				TitledPane rankType = new TitledPane(rank.getName(), rankListView);
				rankType.setAnimated(true);
				nodes.add(rankType);
				rankListView.setCellFactory(listView -> new ListCell<RankItem>() {
					{
						addEventFilter(MouseEvent.MOUSE_PRESSED, filter);
					}

					@Override
					public void updateItem(RankItem item, boolean empty) {
						super.updateItem(item, empty);
						setText(empty ? null : item.getName());
					}
				});
			}
			if (!nodes.isEmpty()) {
				view.getRankAccordion().setExpandedPane(nodes.get(0));
			}
			registerRankItemChangeListener(listViews);
		}
		// 更新音乐数据表格
		view.getTableView().getItems().setAll(list);
		view.updatePagination(rankPage);
		spinner.close();
	}

	private void updateMV(List<MV> list) {
		if (view.getMvTagListView().getItems().isEmpty()) {
			List<Tag> tags = netSource.mvTags();
			if (!tags.isEmpty()) {
				view.getMvTagListView().getItems().setAll(tags);
				mvSelection.select(0);
				mvSelection.selectedItemProperty().addListener(listener);
			}
		}
		view.updateMVList(list, mvPage, mvNodeHandler);
		spinner.close();
	}

	private void updateSpecial(List<Special> list) {
		if (view.getSpecialTagListView().getItems().isEmpty()) {
			List<Tag> tags = netSource.specialTags();
			if (!tags.isEmpty()) {
				view.getSpecialTagListView().getItems().setAll(tags);
				specialSelection.select(0);
				specialSelection.selectedItemProperty().addListener(listener);
			}
		}
		view.updateSpecialList(list, specialPage);
		spinner.close();
	}

	private void updateSinger(List<Singer> list) {
		if (view.getSingerKindListView().getItems().isEmpty()) {
			List<Tag> tags = netSource.singerKindTags();
			if (tags != null && !tags.isEmpty()) {
				view.getSingerKindListView().getItems().setAll(tags);
				kindSection.select(0);
				kindSection.selectedItemProperty().addListener(listener);
			}
		}
		if (view.getSingerEnListView().getItems().isEmpty()) {
			List<Tag> tags = netSource.singerEnTags();
			if (tags != null && !tags.isEmpty()) {
				view.getSingerEnListView().getItems().setAll(tags);
				enSelection.select(0);
				enSelection.selectedItemProperty().addListener(listener);
			}
		}
		view.updateSingerList(list, singerPage, singerNodeHandler);// 更新歌手列表
		spinner.close();// 关闭进度旋转器
	}

	/**
	 * 所有ListView的所有项看成一个组,一个组只能有一项被选中<br>
	 * 原理:若当前被单击的ListView有选定项,则其余ListView所有选中项将被清除
	 *
	 * @param listViews
	 *            ListView数组(可变参数)
	 */
	private void registerRankItemChangeListener(ListView<RankItem>[] listViews) {
		for (ListView<RankItem> listView : listViews) {
			MultipleSelectionModel<RankItem> selection = listView.getSelectionModel();
			selection.selectedItemProperty().addListener(listener);
			listView.setOnMouseClicked(e -> {
				// 若不是同一个ListView,并且被单机的ListView有选定项,则清除其余ListView的选定项
				for (ListView<RankItem> node : listViews) {
					if (node != listView && selection.getSelectedIndex() != -1) {
						node.getSelectionModel().clearSelection();
					}
				}
			});
		}
	}

	private class TagListCell extends ListCell<Tag> {
		public TagListCell() {
			addEventFilter(MouseEvent.MOUSE_PRESSED, filter);
		}

		@Override
		protected void updateItem(Tag item, boolean empty) {
			super.updateItem(item, empty);
			setText(empty ? null : item.getName());
		}
	}
}
