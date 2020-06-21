package com.scmq.player.view;

import com.scmq.player.model.Album;
import com.scmq.player.model.Music;
import com.scmq.player.model.Page;
import com.scmq.player.util.FileUtil;
import com.scmq.player.util.StringUtil;
import com.scmq.view.control.Pagination;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * 专辑详情视图
 *
 * @author SCMQ
 */
public class AlbumView extends BorderPane {
	/** 专辑图片视图 */
	private ImageView imageView;
	/** 专辑名称标签 */
	private Label nameLabel;
	/** 歌手名称标签 */
	private Label singerLabel;
	/** 发布日期标签 */
	private Label timeLabel;

	/** 专辑介绍标签 */
	private Label introLabel;

	/** 音乐表格视图 */
	private TableView<Music> tableView;

	/** 分页组件 */
	private Pagination pagination;

	/**
	 * 构造专辑详情视图对象
	 */
	public AlbumView() {
		// 顶部
		int size = 200;
		imageView = new ImageView();
		imageView.setFitWidth(size);
		imageView.setFitHeight(size);
		Rectangle clip = new Rectangle(size, size);
		clip.setArcWidth(18);
		clip.setArcHeight(18);
		imageView.setClip(clip);

		nameLabel = new Label("-", new Text("专辑："));
		nameLabel.getStyleClass().add("album-name");

		singerLabel = new Label("-", new Text(("歌手：")));
		singerLabel.getStyleClass().add("singer-name");

		timeLabel = new Label("-", new Text("发行时间："));
		timeLabel.getStyleClass().add("time-label");

		Button playButton = new Button("播放全部", FileUtil.createView("play-all", 20, 20));
		Button addButton = new Button("添加到", FileUtil.createView("add-white", 20, 20));
		Button downloadButton = new Button("下载", FileUtil.createView("download-white", 20, 20));
		Button multiOperButton = new Button("批量操作", FileUtil.createView("multi-oper-white", 20, 20));
		HBox box = new HBox(15, playButton, addButton, downloadButton, multiOperButton);
		box.getStyleClass().add("button-music-pane");

		box = new HBox(20, imageView, new VBox(18, nameLabel, singerLabel, timeLabel, box));
		box.setId("album-info-box");

		// 底部
		pagination = new Pagination();
		pagination.setVisible(false);
		pagination.setManaged(false);

		// 右边
		introLabel = new Label("没有专辑介绍", new Text("\t\t\t\t专辑简介："));
		introLabel.setContentDisplay(ContentDisplay.TOP);
		introLabel.setLineSpacing(20);
		introLabel.setWrapText(true);
		ScrollPane scrollPane = new ScrollPane(introLabel);
		scrollPane.setId("album-intro-pane");
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefWidth(280);

		// 中央
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
		tableView.getItems().addListener((Change<?> c) -> numColumn.setText(tableView.getItems().size() + ""));

		setTop(box);
		setRight(scrollPane);
		setCenter(tableView);
		setBottom(pagination);
		setMargin(box, new Insets(0, 0, 10, 0));
		setMargin(pagination, new Insets(10, 0, 0, 0));
		setMargin(scrollPane, new Insets(0, 0, 10, 10));
	}

	/**
	 * 更新专辑信息
	 *
	 * @param album
	 *            专辑信息对象
	 */
	public void updateAlbum(Album album) {
		imageView.setImage(new Image(album.getCover(), true));
		String name = album.getSinger() == null ? null : album.getSinger().getName();
		nameLabel.setText(StringUtil.isEmpty(album.getName()) ? "未知" : album.getName());
		timeLabel.setText(StringUtil.isEmpty(album.getYear()) ? "未知" : album.getYear());
		singerLabel.setText(StringUtil.isEmpty(name) ? "未知" : name);
	}

	/**
	 * 更新专辑简介信息
	 * 
	 * @param album
	 *            专辑信息
	 */
	public void updateIntroduce(Album album) {
		String intro = album.getIntroduce();
		introLabel.setText(StringUtil.isEmpty(intro) ? "没有专辑信息" : intro);
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
}
