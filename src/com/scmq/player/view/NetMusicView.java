package com.scmq.player.view;

import com.scmq.player.model.MV;
import com.scmq.player.model.Music;
import com.scmq.player.model.Page;
import com.scmq.player.model.Singer;
import com.scmq.player.model.Special;
import com.scmq.player.model.Tag;
import com.scmq.player.util.FileUtil;
import com.scmq.view.control.Pagination;
import com.scmq.view.control.Tab;
import com.scmq.view.control.TabPane;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class NetMusicView extends TabPane {
	/** 歌手分类列表视图 */
	private ListView<Tag> singerKindListView;
	/** 歌手拼音检索列表视图 */
	private ListView<Tag> singerEnListView;
	private ScrollPane singerScrollPane;
	/** 歌手节点列表 */
	private ObservableList<Node> singerNodes;

	/** 歌单分类列表视图 */
	private ListView<Tag> specialTagListView;
	private ScrollPane specialScrollPane;
	/** 歌单节点列表 */
	private ObservableList<Node> specialNodes;

	/** MV分类列表视图 */
	private ListView<Tag> mvTagListView;
	private ScrollPane mvScrollPane;
	/** MV节点列表 */
	private ObservableList<Node> mvNodes;

	/** 歌曲表格 */
	private TableView<Music> tableView;
	/** 手风琴组件,显示榜单分类信息 */
	private Accordion rankAccordion;

	/** 分页组件 */
	private Pagination pagination;

	public NetMusicView() {
		/*------------------ 歌手 --------------------*/
		singerKindListView = new ListView<>();
		singerKindListView.getStyleClass().add("tag-list-view");
		singerKindListView.setOrientation(Orientation.HORIZONTAL);
		AnchorPane.setTopAnchor(singerKindListView, 0.0);
		AnchorPane.setRightAnchor(singerKindListView, 0.0);
		AnchorPane.setLeftAnchor(singerKindListView, 0.0);

		singerEnListView = new ListView<>();
		singerEnListView.getStyleClass().add("tag-list-view");
		singerEnListView.setOrientation(Orientation.HORIZONTAL);
		AnchorPane.setTopAnchor(singerEnListView, 40.0);
		AnchorPane.setRightAnchor(singerEnListView, 0.0);
		AnchorPane.setLeftAnchor(singerEnListView, 0.0);

		FlowPane flowPane = new FlowPane(50, 50);
		singerNodes = flowPane.getChildren();

		singerScrollPane = new ScrollPane(flowPane);
		singerScrollPane.setFitToWidth(true);
		singerScrollPane.setFitToHeight(true);
		AnchorPane.setTopAnchor(singerScrollPane, 80.0);
		AnchorPane.setRightAnchor(singerScrollPane, 0.0);
		AnchorPane.setBottomAnchor(singerScrollPane, 0.0);
		AnchorPane.setLeftAnchor(singerScrollPane, 0.0);

		Tab singerTab = new Tab("歌手", new AnchorPane(singerKindListView, singerEnListView, singerScrollPane));

		/*------------------ 歌单 --------------------*/
		specialTagListView = new ListView<>();
		specialTagListView.getStyleClass().add("tag-list-view");
		specialTagListView.setOrientation(Orientation.HORIZONTAL);
		AnchorPane.setTopAnchor(specialTagListView, 0.0);
		AnchorPane.setRightAnchor(specialTagListView, 0.0);
		AnchorPane.setLeftAnchor(specialTagListView, 0.0);

		flowPane = new FlowPane(20, 20);
		specialNodes = flowPane.getChildren();

		specialScrollPane = new ScrollPane(flowPane);
		specialScrollPane.setFitToWidth(true);
		specialScrollPane.setFitToHeight(true);
		AnchorPane.setTopAnchor(specialScrollPane, 48.0);
		AnchorPane.setRightAnchor(specialScrollPane, 0.0);
		AnchorPane.setBottomAnchor(specialScrollPane, 0.0);
		AnchorPane.setLeftAnchor(specialScrollPane, 0.0);

		Tab specialTab = new Tab("歌单", new AnchorPane(specialTagListView, specialScrollPane));

		/*------------------ MV --------------------*/
		mvTagListView = new ListView<>();
		mvTagListView.getStyleClass().add("tag-list-view");
		mvTagListView.setOrientation(Orientation.HORIZONTAL);
		AnchorPane.setTopAnchor(mvTagListView, 0.0);
		AnchorPane.setRightAnchor(mvTagListView, 0.0);
		AnchorPane.setLeftAnchor(mvTagListView, 0.0);

		flowPane = new FlowPane(30, 50);
		mvNodes = flowPane.getChildren();

		mvScrollPane = new ScrollPane(flowPane);
		mvScrollPane.setFitToWidth(true);
		mvScrollPane.setFitToHeight(true);
		AnchorPane.setTopAnchor(mvScrollPane, 48.0);
		AnchorPane.setRightAnchor(mvScrollPane, 0.0);
		AnchorPane.setBottomAnchor(mvScrollPane, 0.0);
		AnchorPane.setLeftAnchor(mvScrollPane, 0.0);

		Tab mvTab = new Tab("MV", new AnchorPane(mvTagListView, mvScrollPane));

		/*------------------ 排行榜 --------------------*/
		tableView = new TableView<>();
		tableView.setTableMenuButtonVisible(true);
		tableView.setPlaceholder(new Label("没有任何音乐"));
		tableView.setColumnResizePolicy(TableViewCell.RESIZE_POLICY);

		// TableView序号列上的复选框显示属性.用于批量操作TableView数据行时,列表题和列单元格上的复选框是可见状态还是隐藏状态
		BooleanProperty checkBoxProperty = new SimpleBooleanProperty();
		ImageView graphic = FileUtil.createGifView(TableViewCell.PLAY_GRAPHIC, 20, true);
		tableView.getProperties().put(TableViewCell.PLAY_GRAPHIC, graphic);
		tableView.getProperties().put(TableViewCell.CHECK_BOX, checkBoxProperty);

		// 设置行单元格工厂
		tableView.setRowFactory(TableViewCell.TABLE_ROW_CELL);

		TableColumn<Music, Music> numColumn = new TableColumn<>("0");
		numColumn.setCellFactory(TableViewCell.NUMBER_CELL);
		numColumn.setPrefWidth(68);
		numColumn.setResizable(false);
		TableColumn<Music, Music> songColumn = new TableColumn<>("歌曲");
		songColumn.setCellFactory(TableViewCell.TITLE_CELL);
		TableColumn<Music, Music> singerColumn = new TableColumn<>("歌手");
		singerColumn.setCellFactory(TableViewCell.SINGER_CELL);
		TableColumn<Music, Music> albumColumn = new TableColumn<>("专辑");
		albumColumn.setCellFactory(TableViewCell.ALBUM_CELL);
		TableColumn<Music, Music> durationColumn = new TableColumn<>("时长");
		durationColumn.setCellFactory(TableViewCell.DURATION_CELL);
		durationColumn.setPrefWidth(88);
		durationColumn.setResizable(false);
		ObservableList<TableColumn<Music, ?>> columns = tableView.getColumns();
		columns.add(numColumn);
		columns.add(songColumn);
		columns.add(singerColumn);
		columns.add(albumColumn);
		columns.add(durationColumn);
		AnchorPane.setTopAnchor(tableView, 0.0);
		AnchorPane.setRightAnchor(tableView, 202.0);
		AnchorPane.setBottomAnchor(tableView, 0.0);
		AnchorPane.setLeftAnchor(tableView, 0.0);

		rankAccordion = new Accordion();
		rankAccordion.setPrefWidth(200);
		AnchorPane.setTopAnchor(rankAccordion, 0.0);
		AnchorPane.setRightAnchor(rankAccordion, 0.0);
		AnchorPane.setBottomAnchor(rankAccordion, 0.0);

		Tab rankTab = new Tab("排行榜", new AnchorPane(tableView, rankAccordion));
		getTabs().addAll(singerTab, specialTab, mvTab, rankTab);
		tabLineProperty().set(true);

		// TableView数据行改变事件,修改列值为行数
		tableView.getItems().addListener((Change<? extends Music> c) -> {
			int size = tableView.getItems().size();
			numColumn.setText(new StringBuilder().append(size).toString());
		});

		pagination = new Pagination();
		AnchorPane.setRightAnchor(pagination, 0.0);
		AnchorPane.setBottomAnchor(pagination, 0.0);
		AnchorPane.setLeftAnchor(pagination, 0.0);
	}

	/**
	 * 更新歌手部分的UI
	 * 
	 * @param singers
	 *            歌手列表集合
	 */
	public void updateSingerList(List<Singer> singers, Page page, EventHandler<MouseEvent> handler) {
		if (!singerNodes.isEmpty()) {
			singerNodes.clear();
		}
		int size = 200, xYR = size >> 1;
		for (Singer singer : singers) {
			// 让Image异步加载，否则将阻塞UI线程
			ImageView imageView = new ImageView(new Image(singer.getCover(), true));
			imageView.setFitWidth(size);
			imageView.setFitHeight(size);
			imageView.setUserData(singer);
			imageView.setPickOnBounds(true);
			imageView.setOnMouseClicked(handler);
			imageView.getStyleClass().add("image-icon");
			imageView.setClip(new Circle(xYR, xYR, xYR));
			Label label = new Label(singer.getName());
			label.getStyleClass().add("singer-name");
			VBox box = new VBox(imageView, label);
			box.setPrefSize(size, size + 20);
			box.getStyleClass().add("singer-node");
			box.setAlignment(Pos.TOP_CENTER);
			singerNodes.add(box);
		}
		updatePagination(page);
	}

	/**
	 * 更新歌单部分的UI
	 * 
	 * @param specials
	 *            歌单列表集合
	 */
	public void updateSpecialList(List<Special> specials, Page page) {
		if (!specialNodes.isEmpty()) {
			specialNodes.clear();
		}
		int size = 200, xYR = size >> 1;
		for (Special special : specials) {
			ImageView imageView = new ImageView(new Image(special.getCover(), true));
			imageView.setFitWidth(size);
			imageView.setFitHeight(size);
			imageView.setPickOnBounds(true);
			imageView.getStyleClass().add("image-icon");
			imageView.setClip(new Circle(xYR, xYR, xYR));
			Label label = new Label(special.getName());
			VBox box = new VBox(imageView, label);
			box.setPrefSize(300, size + 20);
			box.setAlignment(Pos.TOP_CENTER);
			specialNodes.add(box);
		}
		updatePagination(page);
	}

	/**
	 * 更新MV视图
	 * 
	 * @param list
	 *            MV信息List集合
	 * @param page
	 *            分页对象
	 * @param handler
	 *            事件处理器,用于播放MV
	 */
	public void updateMVList(List<MV> list, Page page, EventHandler<MouseEvent> handler) {
		if (!mvNodes.isEmpty()) {
			mvNodes.clear();
		}
		int width = 250, height = 150, index = 0;
		for (MV mv : list) {
			ImageView imageView = new ImageView(new Image(mv.getCover(), true));
			imageView.setFitWidth(width);
			imageView.setFitHeight(height);
			imageView.setPickOnBounds(true);
			imageView.setOnMouseClicked(handler);
			imageView.getStyleClass().add("image-icon");
			Rectangle rect = new Rectangle(width, height);
			rect.setArcWidth(20);
			rect.setArcHeight(20);
			imageView.setClip(rect);
			Label song = new Label(mv.getTitle());
			song.getStyleClass().add("mv-song");
			Label singer = new Label(mv.getSinger() == null ? null : mv.getSinger().getName());
			singer.getStyleClass().add("mv-singer");
			VBox box = new VBox(imageView, song, singer);
			box.setPrefSize(width, height);
			box.setAlignment(Pos.CENTER_LEFT);
			mvNodes.add(box);
			imageView.setUserData(index++);
			box.setUserData(list);
		}
		updatePagination(page);
	}

	public void updatePagination(Page page) {
		Tab tab = tabProperty().get();
		String tabText = tab.getText();

		Node node = "歌手" == tabText ? singerScrollPane : "歌单" == tabText ? //
				specialScrollPane : "MV" == tabText ? mvScrollPane : tableView;
		// 重设分页组件在布局面板中的右约束
		AnchorPane.setRightAnchor(pagination, node == tableView ? 202.0 : 0.0);

		boolean visible = page != null && page.getTotal() > 1;
		// 重设其他组件的下约束
		AnchorPane.setBottomAnchor(node, visible ? 36.0 : 0.0);

		ObservableList<Node> nodes = ((AnchorPane) tab.getContent()).getChildren();
		if (!visible) {
			nodes.remove(pagination);
			return;
		}
		// 必须先设置总页数,然后设置当前页数,因为当前页数总是小于等于总页数
		pagination.setTotal(page.getTotal());
		pagination.setPage(page.getCurrent());
		if (!nodes.contains(pagination)) {
			nodes.add(pagination);
		}
	}

	public ListView<Tag> getSingerKindListView() {
		return singerKindListView;
	}

	public ListView<Tag> getSingerEnListView() {
		return singerEnListView;
	}

	public ListView<Tag> getSpecialTagListView() {
		return specialTagListView;
	}

	public ListView<Tag> getMvTagListView() {
		return mvTagListView;
	}

	public Accordion getRankAccordion() {
		return rankAccordion;
	}

	public TableView<Music> getTableView() {
		return tableView;
	}

	public Pagination getPagination() {
		return pagination;
	}
}
