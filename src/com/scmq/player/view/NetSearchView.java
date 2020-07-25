package com.scmq.player.view;

import com.scmq.player.model.MV;
import com.scmq.player.model.Music;
import com.scmq.player.model.Page;
import com.scmq.player.model.Singer;
import com.scmq.player.model.Special;
import com.scmq.player.util.FileUtil;
import com.scmq.player.util.StringUtil;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

/**
 * 网络资源 搜索/结果 视图
 * 
 * @author SCMQ
 *
 */
public class NetSearchView extends AnchorPane {
	/** 显示歌手图片 */
	private ImageView singerImageView;
	/** 显示歌手名称 */
	private Label nameLabel;
	/** 歌曲数量 */
	private Label songNumLabel;
	/** 专辑数量 */
	private Label albumNumLabel;
	/** MV数量 */
	private Label mvNumLabel;
	/** 粉丝数量 */
	private Label followNumLabel;

	private TabPane tabPane;
	private Pagination pagination;
	private TableView<Music> tableView;

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

		TableColumn<Music, Music> numColumn = new TableColumn<>("0");
		numColumn.setCellFactory(TableViewCell.NUMBER_CELL);
		numColumn.setPrefWidth(68);
		numColumn.setResizable(false);
		numColumn.setSortable(false);// 不可排序

		TableColumn<Music, Music> songColumn = new TableColumn<>("歌曲");
		songColumn.setCellFactory(TableViewCell.TITLE_CELL);
		TableColumn<Music, Music> singerColumn = new TableColumn<>("歌手");
		singerColumn.setCellFactory(TableViewCell.SINGER_CELL);
		TableColumn<Music, Music> albumColumn = new TableColumn<>("专辑");
		albumColumn.setCellFactory(TableViewCell.ALBUM_CELL);
		TableColumn<Music, Music> durationColumn = new TableColumn<>("时长");
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
		tableView.getItems().addListener((Change<? extends Music> c) -> //
		numColumn.setText(new StringBuilder().append(tableView.getItems().size()).toString()));

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

		tabPane = new TabPane();
		tabPane.setBottom(pagination);
		tabPane.getTabs().addAll(new Tab("单曲", tableView), new Tab("歌单", specialPane), new Tab("MV", mvPane));
		tabPane.tabLineProperty().set(true);
		AnchorPane.setTopAnchor(tabPane, 0.0);
		AnchorPane.setRightAnchor(tabPane, 0.0);
		AnchorPane.setBottomAnchor(tabPane, 0.0);
		AnchorPane.setLeftAnchor(tabPane, 0.0);

		getChildren().add(tabPane);
		getStyleClass().add("content");
		TabPane.setMargin(pagination, new Insets(4, 0, 0, 0));
	}

	/** 创建直达歌手部分的视图 */
	private void createSingerView() {
		int size = 120;
		singerImageView = new ImageView();
		singerImageView.setFitWidth(size);
		singerImageView.setFitHeight(size);
		singerImageView.setClip(new Circle(size >>= 1, size, size));
		AnchorPane.setLeftAnchor(singerImageView, 0.0);

		nameLabel = new Label("-");
		nameLabel.getStyleClass().add("singer-name");
		AnchorPane.setTopAnchor(nameLabel, (double) ((size >> 1) - 4));
		AnchorPane.setRightAnchor(nameLabel, 0.0);
		AnchorPane.setLeftAnchor(nameLabel, (double) ((size <<= 1) + 20));

		songNumLabel = new Label("-", new Text("单曲："));
		albumNumLabel = new Label("-", new Text("专辑："));
		mvNumLabel = new Label("-", new Text("MV："));
		followNumLabel = new Label("-", new Text("粉丝："));

		HBox box = new HBox(28, songNumLabel, albumNumLabel, mvNumLabel, followNumLabel);
		box.setAlignment(Pos.CENTER_LEFT);
		box.getStyleClass().add("singer-info-box");
		AnchorPane.setRightAnchor(box, 0.0);
		AnchorPane.setLeftAnchor(box, getLeftAnchor(nameLabel));
		AnchorPane.setTopAnchor(box, getTopAnchor(nameLabel) + 40.0);

		// 重新设置约束选项卡面板的上方向约束
		setTopAnchor(tabPane, (double) size);
		// 歌手信息布局面板放入缓存
		getProperties().put("singer-info-box", box);
		// 添加歌手相关展示的节点
		getChildren().addAll(singerImageView, nameLabel, box);
	}

	/** 移除歌手视图 */
	public void removeSingerView() {
		if (singerImageView == null) {
			return;
		}
		// 重新设置约束选项卡面板的上方向约束
		setTopAnchor(tabPane, 0.0);
		getChildren().removeAll(singerImageView, nameLabel, (Node) getProperties().get("singer-info-box"));
	}

	private void updateSinger(Singer singer) {
		// 设置歌手图片
		singerImageView.setImage(new Image(singer.getCover(), true));
		// 设置歌手名称
		nameLabel.setText(StringUtil.isEmpty(singer.getName()) ? "-" : singer.getName());
		// 设置歌曲数量
		songNumLabel.setText(singer.getSongNum() == null ? "-" : singer.getSongNum().toString());
		// 设置专辑数量
		albumNumLabel.setText(singer.getAlbumNum() == null ? "-" : singer.getAlbumNum().toString());
		// 设置MV数量
		mvNumLabel.setText(singer.getMvNum() == null ? "-" : singer.getMvNum().toString());
		// 设置粉丝数量
		followNumLabel.setText(StringUtil.isEmpty(singer.getFollowNum()) ? "-" : singer.getFollowNum());
	}

	/**
	 * 更新歌曲表格视图
	 * 
	 * @param list
	 *            音乐信息List集合
	 * @param page
	 *            分页对象
	 * @param singer
	 *            (直达)歌手信息对象
	 */
	public void updateSong(List<Music> list, Page page, Singer singer) {
		// 若搜索的是歌手且已获得歌手信息
		if (singer != null) {
			if (singerImageView == null) {
				createSingerView();
			} else {
				ObservableList<Node> nodes = getChildren();
				if (!nodes.contains(singerImageView)) {
					nodes.addAll(singerImageView, nameLabel, (Node) getProperties().get("singer-info-box"));
				}
			}
			updateSinger(singer);
		} else {
			removeSingerView();
		}

		// 更新音乐表格视图
		tableView.getItems().setAll(list);
		// 更新分页数据
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
		int size = 200, circle = size >> 1;
		for (Special special : specials) {
			ImageView imageView = new ImageView(new Image(special.getCover(), true));
			imageView.setFitWidth(size);
			imageView.setFitHeight(size);
			imageView.setPickOnBounds(true);
			imageView.getStyleClass().add("image-icon");
			imageView.setClip(new Circle(circle, circle, circle));
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

	public TableView<Music> getTableView() {
		return tableView;
	}

	public TabPane getTabPane() {
		return tabPane;
	}

	public ImageView getSingerImageView() {
		return singerImageView;
	}
}
