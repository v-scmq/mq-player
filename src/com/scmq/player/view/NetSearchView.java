package com.scmq.player.view;

import com.scmq.player.model.MV;
import com.scmq.player.model.Music;
import com.scmq.player.model.Page;
import com.scmq.player.model.Special;
import com.scmq.player.util.FileUtil;
import com.scmq.view.control.Pagination;
import com.scmq.view.control.Tab;
import com.scmq.view.control.TabPane;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * 网络资源 搜索/结果 视图
 * 
 * @author SCMQ
 *
 */
public class NetSearchView extends TabPane {
	private TableView<Music> tableView;
	private Pagination pagination;

	private ObservableList<Node> specialNodes;
	private ObservableList<Node> mvNodes;

	public NetSearchView() {
		tableView = new TableView<>();
		tableView.setColumnResizePolicy(TableViewCell.RESIZE_POLICY);
		tableView.setTableMenuButtonVisible(true);
		tableView.setPlaceholder(new Label("未搜索到任何歌曲！"));
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// TableView序号列上的复选框显示属性.用于批量操作TableView数据行时,列表题和列单元格上的复选框是可见状态还是隐藏状态
		BooleanProperty checkBoxProperty = new SimpleBooleanProperty();
		ImageView graphic = FileUtil.createGifView(TableViewCell.PLAY_GRAPHIC, 20, true);
		tableView.getProperties().put(TableViewCell.PLAY_GRAPHIC, graphic);
		tableView.getProperties().put(TableViewCell.CHECK_BOX, checkBoxProperty);

		// 设置行单元格工厂
		tableView.setRowFactory(TableViewCell.TABLE_ROW_CELL);

		TableColumn<Music, String> numColumn = new TableColumn<>("0");
		numColumn.setCellFactory(TableViewCell.NUMBER_CELL);
		numColumn.setPrefWidth(68);
		numColumn.setResizable(false);
		numColumn.setSortable(false);// 不可排序

		TableColumn<Music, String> songColumn = new TableColumn<>("歌曲");
		songColumn.setCellFactory(TableViewCell.TITLE_CELL);
		TableColumn<Music, String> singerColumn = new TableColumn<>("歌手");
		singerColumn.setCellFactory(TableViewCell.SINGER_CELL);
		TableColumn<Music, String> albumColumn = new TableColumn<>("专辑");
		albumColumn.setCellFactory(TableViewCell.ALBUM_CELL);
		TableColumn<Music, String> durationColumn = new TableColumn<>("时长");
		durationColumn.setCellFactory(TableViewCell.DURATION_CELL);
		durationColumn.setPrefWidth(80);
		durationColumn.setResizable(false);
		ObservableList<TableColumn<Music, ?>> columns = tableView.getColumns();
		columns.add(numColumn);
		columns.add(songColumn);
		columns.add(singerColumn);
		columns.add(albumColumn);
		columns.add(durationColumn);

		// TableView数据行改变事件,修改列值为行数
		tableView.getItems().addListener((Change<? extends Music> c) -> numColumn.setText(
				new StringBuilder().append(tableView.getItems().size()).toString()));

		// 歌单模块
		FlowPane flowPane = new FlowPane(20, 20);
		specialNodes = flowPane.getChildren();
		ScrollPane specialPane = new ScrollPane(flowPane);
		specialPane.setFitToWidth(true);
		specialPane.setFitToHeight(true);

		flowPane = new FlowPane(30, 50);
		mvNodes = flowPane.getChildren();
		ScrollPane mvPane = new ScrollPane(flowPane);
		mvPane.setFitToWidth(true);
		mvPane.setFitToHeight(true);

		pagination = new Pagination();
		pagination.setManaged(false);
		pagination.setVisible(false);
		setBottom(pagination);
		setMargin(pagination, new Insets(4, 0, 0, 0));

		getTabs().addAll(new Tab("单曲", tableView), new Tab("歌单", specialPane), new Tab("MV", mvPane));

		tabLineProperty().set(true);
		setId("search-result-tab-pane");
		getStyleClass().add("content");
	}

	/**
	 * 更新歌曲表格视图
	 * 
	 * @param list
	 *            音乐信息List集合
	 * @param page
	 *            分页对象
	 */
	public void updateSong(List<Music> list, Page page) {
		tableView.getItems().setAll(list);
		updatePagination(page);
	}

	/**
	 * 更新歌单部分的UI
	 * 
	 * @param specials
	 *            歌单列表集合
	 */
	public void updateSpecial(List<Special> specials, Page page) {
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

	/**
	 * 更新分页组件
	 * 
	 * @param page
	 *            分页对象
	 */
	public void updatePagination(Page page) {
		if (page != null && page.getTotal() > 1) {
			// 必须先设置总页数,然后设置当前页数,因为当前页数总是小于等于总页数
			pagination.setTotal(page.getTotal());
			pagination.setPage(page.getCurrent());
			pagination.setManaged(true);
			pagination.setVisible(true);
		} else {
			pagination.setManaged(false);
			pagination.setVisible(false);
		}
	}

	public Pagination getPagination() {
		return pagination;
	}
}
