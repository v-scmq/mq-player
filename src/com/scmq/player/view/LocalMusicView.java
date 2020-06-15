package com.scmq.player.view;

import com.scmq.player.app.Main;
import com.scmq.player.model.Album;
import com.scmq.player.model.Music;
import com.scmq.player.util.FileUtil;
import com.scmq.view.control.EditText;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class LocalMusicView extends AnchorPane {
	private Button playAllButton;
	private Button addButton;
	private Button deleteButton;
	private Button exitMultiOper;
	private Button batchOperButton;
	private EditText inputLocalSearchKey;
	private Button sortMethod;

	private MenuItem leadForFlie;
	private MenuItem leadForDir;

	private TableView<Music> tableView;

	public LocalMusicView() {
		playAllButton = new Button("播放全部", FileUtil.createView("play-all", 20, 20));// width=104|74
		playAllButton.setDisable(true);
		setTopAnchor(playAllButton, 28.0);
		setLeftAnchor(playAllButton, 0.0);

		addButton = new Button("添加到", FileUtil.createView("add-white", 20, 20));// width=89
		setTopAnchor(addButton, 28.0);
		setLeftAnchor(addButton, 94.0);// 74+20+0

		deleteButton = new Button("删除", FileUtil.createView("delete-white", 20, 20));// width=74
		setTopAnchor(deleteButton, 28.0);
		setLeftAnchor(deleteButton, 203.0);// 94+89+20

		exitMultiOper = new Button("退出批量操作", FileUtil.createView("exit-white", 20, 20));// width=134
		setTopAnchor(exitMultiOper, 28.0);
		setLeftAnchor(exitMultiOper, 297.0);// 203+74+20

		batchOperButton = new Button("批量操作", FileUtil.createView("multi-oper-white", 20, 20));// width=104
		batchOperButton.setDisable(true);
		setTopAnchor(batchOperButton, 28.0);
		setLeftAnchor(batchOperButton, 124.0);// 104+20+20

		inputLocalSearchKey = new EditText(null, FileUtil.createView("search", 20, 20), true);// width=200
		inputLocalSearchKey.setPromptText("搜索本地歌曲");
		inputLocalSearchKey.setPrefWidth(200);
		setTopAnchor(inputLocalSearchKey, 28.0);
		setRightAnchor(inputLocalSearchKey, 268.0);// 104+144+20

		Button leadingButton = new Button("导入歌曲", FileUtil.createView("leading-in", 20, 20));// width=104
		setTopAnchor(leadingButton, 28.0);
		setRightAnchor(leadingButton, 144.0);// 104+20+20

		leadForFlie = new MenuItem("添加本地歌曲");
		leadForDir = new MenuItem("添加本地目录");
		ContextMenu contextMenu = new ContextMenu(leadForFlie, leadForDir);
		contextMenu.setAnchorX(144);
		contextMenu.setAnchorY(28);

		sortMethod = new Button("排序方式", FileUtil.createView("sort-method", 20, 20));// width=104
		setTopAnchor(sortMethod, 28.0);
		setRightAnchor(sortMethod, 20.0);

		tableView = new TableView<>();
		tableView.setTableMenuButtonVisible(true);
		tableView.setPlaceholder(new Label("还未添加本地音乐"));
		tableView.setColumnResizePolicy(TableViewCell.RESIZE_POLICY);
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// TableView序号列上的复选框显示属性.用于批量操作TableView数据行时,列表题和列单元格上的复选框是可见状态还是隐藏状态
		BooleanProperty checkBoxProperty = new SimpleBooleanProperty();
		ImageView graphic = FileUtil.createGifView(TableViewCell.PLAY_GRAPHIC, 20, true);
		tableView.getProperties().put(TableViewCell.PLAY_GRAPHIC, graphic);
		tableView.getProperties().put(TableViewCell.CHECK_BOX, checkBoxProperty);

		// 设置行单元格工厂
		tableView.setRowFactory(TableViewCell.TABLE_ROW_CELL);

		// 设置序号列的单元格工厂
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
		durationColumn.setPrefWidth(80);
		durationColumn.setResizable(false);
		durationColumn.setCellFactory(TableViewCell.DURATION_CELL);
		TableColumn<Music, String> sizeColumn = new TableColumn<>("大小");
		sizeColumn.setPrefWidth(80);
		sizeColumn.setResizable(false);
		sizeColumn.setCellFactory(TableViewCell.SIZE_CELL);
		ObservableList<TableColumn<Music, ?>> columns = tableView.getColumns();
		columns.add(numColumn);
		columns.add(songColumn);
		columns.add(singerColumn);
		columns.add(albumColumn);
		columns.add(durationColumn);
		columns.add(sizeColumn);

		System.out.println(songColumn.getComparator());

		List<Music> list = tableView.getItems();

		Collator collator = Collator.getInstance(Locale.CHINA);
		songColumn.sortTypeProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("newValue=>" + newValue);
			Comparator<Music> comparator = Comparator.comparing(Music::getTitle, collator::compare).thenComparing(
					o -> o.getSinger() == null ? null : o.getSinger().getName(), collator::compare);
			comparator = Comparator.nullsFirst(comparator);
			list.sort(newValue == TableColumn.SortType.DESCENDING ? comparator.reversed() : comparator);
		});

		singerColumn.sortTypeProperty().addListener(((observable, oldValue, newValue) -> {
			Comparator<Music> comparator = Comparator.<Music, String> comparing(
					o -> o.getSinger() == null ? null : o.getSinger().getName(), collator::compare).thenComparing(
							Music::getTitle, collator::compare);
			comparator = Comparator.nullsFirst(comparator);
			list.sort(newValue == TableColumn.SortType.DESCENDING ? comparator.reversed() : comparator);
		}));

		albumColumn.sortTypeProperty().addListener(((observable, oldValue, newValue) -> {
			boolean desc = newValue == TableColumn.SortType.DESCENDING;
			list.sort((o1, o2) -> {
				Album album = desc ? o2.getAlbum() : o1.getAlbum();
				Album other = desc ? o1.getAlbum() : o2.getAlbum();
				String name1 = album == null ? "" : album.getName();
				String name2 = other == null ? "" : other.getName();
				int result = collator.compare(name1, name2);
				if (result == 0) {
					name1 = desc ? o2.getTitle() : o1.getTitle();
					name2 = desc ? o1.getTitle() : o2.getTitle();
					return collator.compare(name1 == null ? "" : name1, name2 == null ? "" : name2);
				}
				return result;
			});
		}));

		setTopAnchor(tableView, 90.0);
		setRightAnchor(tableView, 0.0);
		setBottomAnchor(tableView, 0.0);
		setLeftAnchor(tableView, 0.0);

		getChildren().addAll(playAllButton, batchOperButton, inputLocalSearchKey, sortMethod, tableView, leadingButton);
		getStyleClass().add("button-music-pane");

		/* **************** 注册事件 ***********/

		// 当鼠标移入按钮上,设置上下文菜单在屏幕上的位置
		leadingButton.setOnMouseClicked(e -> {
			// 若导入按钮的弹出菜单没有正在显示
			if (e.getButton() == MouseButton.PRIMARY && !contextMenu.isShowing()) {
				// screenX - e.x - (contextMenu.prefWidth - button.width) / 2
				contextMenu.setAnchorX(e.getScreenX() - e.getX() - 6);
				contextMenu.setAnchorY(e.getScreenY() - e.getY() + leadingButton.getHeight());
				// 显示上下文菜单项
				contextMenu.show(Main.getPrimaryStage());
			}
		});

		// 获得表格视图的选择模式
		MultipleSelectionModel<Music> selection = tableView.getSelectionModel();

		// TableView数据行改变事件,修改列值为行数
		tableView.getItems().addListener((Change<? extends Music> c) -> {
			int size = tableView.getItems().size();
			// 是否启用 “播放所选、批量操作” 按钮(表格视图有数据则启用,否则不启用)
			boolean disable = size == 0;
			playAllButton.setDisable(disable);
			batchOperButton.setDisable(disable);
			// 若未显示列表题上的复选框,则更新列表题的文本
			if (!checkBoxProperty.get()) {
				numColumn.setText(new StringBuilder().append(size).toString());
			}
			// 若复选框处于显示,并且表格视图没有数据时,通过代码触发“退出批量操作”按钮,以此更新UI
			else if (disable) {
				exitMultiOper.fire();
			}
		});
		// 批量操作时,在列标题上显示的复选框
		final CheckBox numCheckBox = new CheckBox();
		numCheckBox.setMinSize(50, 50);
		// 不可转移焦点到复选框
		numCheckBox.setFocusTraversable(false);
		// 复选框点击事件
		numCheckBox.setOnAction(e -> {
			// 从选择模型对象获得的已选数量
			int selectRow = selection.getSelectedItems().size();
			// 如果是全部选择(和表格视图数据量相等)
			if (selectRow == tableView.getItems().size()) {
				selection.clearSelection();// 则全部清除选择
			} else {
				selection.selectAll();// 全部选择
			}
		});
		// 如果需要显示CheckBox在列标题上
		checkBoxProperty.addListener(((observable, oldValue, newValue) -> {
			if (newValue) {
				// 将复选框显示到序号列的列表题上,并取消序号显示
				numColumn.setGraphic(numCheckBox);
				numColumn.setText(null);
			} else {
				// 取消复选框在序号列的列表题上显示,并显示序号
				numColumn.setGraphic(null);
				int size = tableView.getItems().size();
				numColumn.setText(new StringBuilder().append(size).toString());
			}
		}));

		// 表格视图行单元格选择列表改变事件
		selection.getSelectedItems().addListener((Change<? extends Music> c) -> {
			// 获取已经选择的列表
			int selectRow = selection.getSelectedItems().size();
			// 标记复选框是否选中,如果选择行数和表格视图总行数相等且不为0
			boolean select = selectRow != 0 && //
			selectRow == tableView.getItems().size();
			// 设置序号列标题上的复选框是否选中
			numCheckBox.setSelected(select);
		});

		// 批量操作按钮事件,处理UI组件的更换(不显示 “批量操作、本地搜索、排序方式、导入歌曲” 按钮)
		batchOperButton.setOnAction(e -> {
			ObservableList<Node> nodes = getChildren();
			nodes.set(1, addButton);
			nodes.set(2, deleteButton);
			nodes.set(3, exitMultiOper);
			nodes.remove(leadingButton);
			// 修改“播放全部” 按钮 的文 为 “播放”
			playAllButton.setText("播放");
			// 修改复选框显示属性为true,以触发复选框显示
			checkBoxProperty.set(true);
		});
		// 退出批量操作按钮事件,处理UI组件的更换
		exitMultiOper.setOnAction(e -> {
			ObservableList<Node> nodes = getChildren();
			nodes.set(1, batchOperButton);
			nodes.set(2, inputLocalSearchKey);
			nodes.set(3, sortMethod);
			nodes.add(leadingButton);
			// 修改“播放” 按钮 的文 为 “播放全部”
			playAllButton.setText("播放全部");
			// 修改复选框显示属性为false,以触发复选框不显示
			checkBoxProperty.set(false);
		});
	}

	public MenuItem getLeadForFlie() {
		return leadForFlie;
	}

	public MenuItem getLeadForDir() {
		return leadForDir;
	}

	public Button getPlayAllButton() {
		return playAllButton;
	}

	public Button getAddButton() {
		return addButton;
	}

	public Button getDeleteButton() {
		return deleteButton;
	}

	public Button getExitMultiOper() {
		return exitMultiOper;
	}

	public Button getBatchOperButton() {
		return batchOperButton;
	}

	public EditText getInputLocalSearchKey() {
		return inputLocalSearchKey;
	}

	public Button getSortMethod() {
		return sortMethod;
	}

	public TableView<Music> getTableView() {
		return tableView;
	}
}
