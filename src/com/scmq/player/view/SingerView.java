package com.scmq.player.view;

import com.scmq.player.model.Album;
import com.scmq.player.model.MV;
import com.scmq.player.model.Music;
import com.scmq.player.model.Page;
import com.scmq.player.model.Singer;
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
import javafx.scene.control.Button;
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

public class SingerView extends AnchorPane {
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

	/** 选项卡面板 */
	private TabPane tabPane;
	/** 显示歌手介绍的标签 */
	private Label introLabel;
	/** 分页组件 */
	private Pagination pagination;
	/** 音乐信息表格 */
	private TableView<Music> tableView;
	/** MV列表 */
	private ObservableList<Node> mvNodes;
	/** 专辑列表 */
	private ObservableList<Node> albumNodes;

	public SingerView() {
		int size = 180;
		singerImageView = new ImageView();
		singerImageView.setFitWidth(size);
		singerImageView.setFitHeight(size);
		singerImageView.setClip(new Circle(size >>= 1, size, size));
		AnchorPane.setTopAnchor(singerImageView, 0.0);
		AnchorPane.setLeftAnchor(singerImageView, 0.0);

		size <<= 1;
		nameLabel = new Label();
		AnchorPane.setTopAnchor(nameLabel, 30.0);
		AnchorPane.setRightAnchor(nameLabel, 0.0);
		AnchorPane.setLeftAnchor(nameLabel, size + 40.0);

		songNumLabel = new Label("-", new Text("单曲："));
		albumNumLabel = new Label("-", new Text("专辑："));
		mvNumLabel = new Label("-", new Text("MV："));
		followNumLabel = new Label("-", new Text("粉丝："));

		HBox hBox = new HBox(30, songNumLabel, new Text("|"), albumNumLabel, new Text("|"), mvNumLabel, new Text("|"),
				followNumLabel);
		hBox.setId("singer-info-box");
		AnchorPane.setTopAnchor(hBox, 72.0);
		AnchorPane.setRightAnchor(hBox, 0.0);
		AnchorPane.setLeftAnchor(hBox, size + 40.0);

		Button playButton = new Button("播放全部", FileUtil.createView("play-all", 20, 20));
		Button addButton = new Button("添加到", FileUtil.createView("add-white", 20, 20));
		Button downloadButton = new Button("下载", FileUtil.createView("download-white", 20, 20));
		Button multiOperButton = new Button("批量操作", FileUtil.createView("multi-oper-white", 20, 20));
		HBox box = new HBox(15, playButton, addButton, downloadButton, multiOperButton);
		box.getStyleClass().add("button-music-pane");
		AnchorPane.setTopAnchor(box, 120.0);
		AnchorPane.setLeftAnchor(box, size + 40.0);

		tableView = new TableView<>();
		tableView.setTableMenuButtonVisible(true);
		tableView.setPlaceholder(new Label("没有任何歌曲！"));
		tableView.setColumnResizePolicy(TableViewCell.RESIZE_POLICY);
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
		tableView.getItems().addListener((Change<? extends Music> c) -> numColumn.setText(
				new StringBuilder().append(tableView.getItems().size()).toString()));

		FlowPane albumFlowPane = new FlowPane(30, 50);
		albumNodes = albumFlowPane.getChildren();
		ScrollPane albumScrollPane = new ScrollPane(albumFlowPane);
		albumScrollPane.setFitToWidth(true);
		albumScrollPane.setFitToHeight(true);

		FlowPane mvFlowPane = new FlowPane(30, 50);
		mvNodes = mvFlowPane.getChildren();
		ScrollPane mvScrollPane = new ScrollPane(mvFlowPane);
		mvScrollPane.setFitToWidth(true);
		mvScrollPane.setFitToHeight(true);

		introLabel = new Label();
		introLabel.setWrapText(true);
		introLabel.setLineSpacing(20);
		introLabel.getStyleClass().add("singer-intro");
		ScrollPane introScrollPane = new ScrollPane(introLabel);
		introScrollPane.setFitToWidth(true);

		Tab songTab = new Tab("单曲", tableView);
		Tab albumTab = new Tab("专辑", albumScrollPane);
		Tab mvTab = new Tab("MV", mvScrollPane);
		Tab introTab = new Tab("简介", introScrollPane);

		tabPane = new TabPane(songTab, albumTab, mvTab, introTab);
		tabPane.tabLineProperty().set(true);

		tabPane.getTabs().addAll(songTab, albumTab, mvTab, introTab);
		AnchorPane.setTopAnchor(tabPane, (double) size);
		AnchorPane.setRightAnchor(tabPane, 0.0);
		AnchorPane.setBottomAnchor(tabPane, 0.0);
		AnchorPane.setLeftAnchor(tabPane, 0.0);

		pagination = new Pagination();
		pagination.setManaged(false);
		pagination.setVisible(false);
		tabPane.setBottom(pagination);
		TabPane.setMargin(pagination, new Insets(4, 0, 0, 0));

		getChildren().addAll(singerImageView, nameLabel, hBox, box, tabPane);
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
	 * 更新专辑视图
	 * 
	 * @param list
	 *            专辑信息List集合
	 * @param page
	 *            分页对象
	 * @param handler
	 *            事件处理器,用于打开专辑详情页面
	 */
	public void updateAlbum(List<Album> list, Page page, EventHandler<MouseEvent> handler) {
		if (!albumNodes.isEmpty()) {
			albumNodes.clear();
		}
		int size = 200, xYR = size >> 1;
		for (Album album : list) {
			// 让Image异步加载，否则将阻塞UI线程
			ImageView imageView = new ImageView(new Image(album.getCover(), true));
			imageView.setFitWidth(size);
			imageView.setFitHeight(size);
			imageView.setPickOnBounds(true);
			imageView.setOnMouseClicked(handler);
			imageView.getStyleClass().add("image-icon");
			imageView.setClip(new Circle(xYR, xYR, xYR));

			Label nameLabel = new Label(album.getName());
			Label publishLabel = new Label(album.getYear());
			VBox box = new VBox(imageView, nameLabel, publishLabel);
			box.setPrefSize(size, size + 40);
			box.setAlignment(Pos.TOP_CENTER);
			albumNodes.add(box);

			imageView.setUserData(album);
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
	 * 更新歌手简介
	 * 
	 * @param singer
	 *            歌手信息对象
	 */
	public void updateIntroduce(Singer singer) {
		updatePagination(null);
		introLabel.setText(singer.getIntroduce());
	}

	public void updateSinger(Singer singer) {
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

	public TableView<Music> getTableView() {
		return tableView;
	}

	public Pagination getPagination() {
		return pagination;
	}

	public TabPane getTabPane() {
		return tabPane;
	}
}
