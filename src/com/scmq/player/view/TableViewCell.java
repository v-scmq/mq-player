package com.scmq.player.view;

import com.scmq.player.app.Main;
import com.scmq.player.model.Album;
import com.scmq.player.model.Music;
import com.scmq.player.model.PlayList;
import com.scmq.player.model.Singer;
import com.scmq.player.util.StringUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.ResizeFeatures;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * 表格视图单元格工具类.
 * 
 * @author SCMQ
 */
public class TableViewCell {
	/** 用于获取表格关联的复选框属性 */
	public static final String CHECK_BOX = "check-box";
	/** 用于展示正在播放音乐所在行单元格的ImageView动态 */
	public static final String PLAY_GRAPHIC = "player/wave2";

	/** not allow create TableViewCell of instance, because this is tool class. */
	private TableViewCell() {
	}

	/**
	 * 表格视图的列大小调整策略.
	 * 
	 * 这个调整策略把表格视图的总宽度减去不可改变大小的列的宽度之和,这个差值就是每个可以改变大小的列的宽度之和,这些列平分这个差值
	 */
	@SuppressWarnings("rawtypes")
	public static final Callback<ResizeFeatures, Boolean> RESIZE_POLICY = resizeFeatures -> {
		// select = ".column-header-background .show-hide-columns-button";
		// StackPane pane = (StackPane) table.lookup(select);
		ObservableList<? extends TableColumnBase<?, ?>> columns;
		TableView<?> table = resizeFeatures.getTable();
		columns = table.getVisibleLeafColumns();
		double width = table.getWidth() - 18.0;
		int count = 0; // 平分列宽度的列数量
		for (TableColumnBase<?, ?> column : columns) {
			if (!column.isResizable()) {
				width -= column.getPrefWidth();
				count++;
			}
		}
		count = columns.size() - count;
		if (count > 0) {
			width /= count;
			for (TableColumnBase<?, ?> column : columns) {
				if (column.isResizable()) {
					column.setPrefWidth(width);
				}
			}
		}
		return false;
	};

	/**
	 * 音乐标题单元格工厂回调器
	 * 
	 * @see Music#getTitle()
	 */
	public static Callback<TableColumn<Music, Music>, TableCell<Music, Music>> TITLE_CELL = column -> new TableCell<Music, Music>() {
		@Override
		public void updateIndex(int index) {
			super.updateIndex(index);
			Music music = (Music) getTableRow().getItem();
			setText(music == null ? null : music.getTitle());
		}
	};

	/**
	 * 音乐歌手信息单元格工厂回调器
	 * 
	 * @see Music#getSinger()
	 * @see Singer#getName()
	 */
	public static Callback<TableColumn<Music, Music>, TableCell<Music, Music>> SINGER_CELL = column -> new TableCell<Music, Music>() {
		@Override
		public void updateIndex(int index) {
			super.updateIndex(index);
			Music music = (Music) getTableRow().getItem();
			setText(music == null || music.getSinger() == null ? null : music.getSinger().getName());
		}
	};

	/**
	 * 音乐专辑信息单元格工厂回调器
	 * 
	 * @see Music#getAlbum()
	 * @see Album#getName()
	 */
	public static Callback<TableColumn<Music, Music>, TableCell<Music, Music>> ALBUM_CELL = column -> new TableCell<Music, Music>() {
		@Override
		public void updateIndex(int index) {
			super.updateIndex(index);
			Music music = (Music) getTableRow().getItem();
			setText(music == null || music.getAlbum() == null ? null : music.getAlbum().getName());
		}
	};

	/**
	 * 音乐时长信息单元格回调器
	 * 
	 * @see Music#getDuration()
	 */
	public static Callback<TableColumn<Music, Music>, TableCell<Music, Music>> DURATION_CELL = column -> new TableCell<Music, Music>() {
		@Override
		public void updateIndex(int index) {
			super.updateIndex(index);
			Music music = (Music) getTableRow().getItem();
			setText(music == null ? null : music.getDuration());
		}
	};

	/**
	 * 音乐文件大小信息数据单元格工厂回调器
	 * 
	 * @see Music#getSize()
	 */
	public static Callback<TableColumn<Music, Music>, TableCell<Music, Music>> SIZE_CELL = column -> new TableCell<Music, Music>() {
		@Override
		public void updateIndex(int index) {
			super.updateIndex(index);
			Music music = (Music) getTableRow().getItem();
			setText(music == null ? null : music.getSize());
		}
	};

	/**
	 * 表格视图音乐信息序号单元格回调工厂.
	 * 
	 * 序号单元格需要播放动态图标和复选框显示属性(BooleanProperty).<br>
	 * 当音乐正在播放时,那么表格视图对应的序号单元格就有播放动图显示,以此标记正在播放的音乐.<br>
	 * 当复选框需要显示的时候,那么所有非空的序号单元格都将显示复选框以此来标记被选中的行单元格.<br>
	 * 在设置单元格工厂之前,必须为TableView放入这个播放动态图标和复选框显示属性.以下是一段代码示例:
	 * 
	 * <pre>
	 * TableView&lt;Music&gt; tableView = new TableView&lt;&gt;();
	 * ObservableMap&lt;Object,Object&gt; map = tableView.getProperties();
	 * 
	 * // map.set("CHECK-BOX", new SimpleBooleanProperty());
	 * map.set(TableViewCell.CHECK_BOX, new SimpleBooleanProperty());
	 * 
	 * // map.set("player/wave2",new ImageView("player/wave2"));
	 * map.set(TableView.PLAY_GRAPHIC,new ImageView(TableView.PLAY_GRAPHIC);
	 * 
	 * TableColumn&lt;Music,String&gt; column = new TableColumn&lt;&gt;("0");
	 * column.setFactory(TableViewCell.NUMBER_CELL);
	 * 
	 * tableView.getColumns().add(column);
	 * </pre>
	 * 
	 * @see TableViewCell#PLAY_GRAPHIC
	 * @see TableViewCell#CHECK_BOX
	 */
	public static Callback<TableColumn<Music, Music>, TableCell<Music, Music>> NUMBER_CELL = column -> new NumberCell();

	/**
	 * 表格视图行单元格回调工厂.
	 *
	 * 行单元格需要复选框显示属性(BooleanProperty),以此检查是否单元格是否正在显示复选框.<br/>
	 * 这很有用处,因为在显示复选框时,需要阻止TableView组件的某些默认事件. <br/>
	 * 于是在设置行单元格工厂之前,必须为TableView放入这个复选框属性.以下是一段代码示例:
	 *
	 * <pre>
	 * TableView&lt;Music&gt; tableView = new TableView&lt;&gt;();
	 * // tableView.getProperties().set("CHECK-BOX", new SimpleBooleanProperty());
	 * tableView.getProperties().set(TableViewCell.CHECK_BOX, new SimpleBooleanProperty());
	 * tableView.setRowFactory(TableViewCell.Table_ROW_CELL);
	 * </pre>
	 *
	 * @see TableView#getProperties()
	 * @see TableViewCell#CHECK_BOX
	 *
	 */
	public static Callback<TableView<Music>, TableRow<Music>> TABLE_ROW_CELL = TableRowCell::new;

	/**
	 * 序号单元格,这个单元格包含了复选框和播放图标的显示.
	 * 
	 * @author SCMQ
	 *
	 */
	private static class NumberCell extends TableCell<Music, Music> implements ChangeListener<Object> {
		/** 默认4个字符容量的字符串构建器(减少内存占用) */
		private static StringBuilder builder = new StringBuilder(4);
		/** 序号列需要显示的复选框,用于批量操作时使用 */
		private CheckBox checkBox;
		/** 正在播放图标 */
		private ImageView playGraph;
		/** 复选框显示属性 */
		private BooleanProperty checkBoxProperty;

		@Override
		public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
			// 所有表格视图中序号列的单元格中复选框显示属性监听
			if (newValue instanceof Boolean) {
				// 显示或隐藏复选框之前,清除所有已经选中的行单元格
				getTableView().getSelectionModel().clearSelection();
				// 更新当前单元格
				updateIndex(getIndex());
				return;
			}
			// 当前单元格音乐信息
			Object item = getTableRow().getItem();
			// 确保只有2个单元格更新(之前的)
			if (item != null && (item.equals(oldValue) || item.equals(newValue))) {
				updateIndex(getIndex());
			}
		}

		@Override
		public void updateIndex(int index) {
			super.updateIndex(index);
			setGraphic(null);
			setText(null);

			// 当前单元格对应的音乐信息
			Object item = getTableRow().getItem();
			// 如果列未处于显示状态 或 是空行单元格(isEmpty()检测有些问题)
			if (item == null) {
				return;
			}

			// 若还未初始化复选框属性和播放动态图标,则执行初始化
			if (checkBoxProperty == null) {
				ObservableMap<Object, Object> map = getTableView().getProperties();
				checkBoxProperty = (BooleanProperty) map.get(CHECK_BOX);
				playGraph = (ImageView) map.get(PLAY_GRAPHIC);

				// 所有表格视图中序号列的单元格中复选框显示属性监听
				checkBoxProperty.addListener(this);
				// 正在播放的媒体 改变事件
				Main.mediaProperty().addListener(this);
			}

			// 如果需要显示复选框(进入批量操作时)
			if (checkBoxProperty.get()) {
				// 初始化复选框
				if (checkBox == null) {
					checkBox = new CheckBox();
					// 复选框不可用(不接受鼠标操作)
					checkBox.setDisable(true);
					// 复选框的选择由表格的行单元格选择决定(JavaFX属性绑定)
					checkBox.selectedProperty().bind(getTableRow().selectedProperty());
				}
				setGraphic(checkBox);
				return;
			}

			// 若当前单元格对应数据是本地音乐,则比较path; 否则比较mid
			if (item.equals(Main.mediaProperty().get())) {
				setGraphic(playGraph);
				return;
			}
			// 显示序号
			builder.delete(0, builder.length());
			setText(StringUtil.fillString(builder, ++index));
		}
	}

	/**
	 * 表格视图行单元格
	 * 
	 * @author SCMQ
	 *
	 */
	private static class TableRowCell extends TableRow<Music> implements EventHandler<MouseEvent> {
		/** 行单元格所关联的表格视图 */
		private TableView<Music> tableView;

		/** 复选框显示属性 */
		private BooleanProperty checkBoxProperty;

		/** 行单元格选择模式 */
		private MultipleSelectionModel<Music> selection;

		/** 构造一个行单元格 */
		public TableRowCell(TableView<Music> tableView) {
			this.tableView = tableView;
			selection = tableView.getSelectionModel();
			checkBoxProperty = (BooleanProperty) tableView.getProperties().get(CHECK_BOX);

			// 为行单元格注册鼠标按下事件过滤器(事件由父组件向子组件传递)
			addEventFilter(MouseEvent.MOUSE_PRESSED, this);
			// 注册鼠标双击事件
			setOnMouseClicked(this);
		}

		/**
		 * 鼠标在行单元格上按下时触发(filter类型:父组件传递到子组件)
		 * 
		 * @param event
		 *            鼠标事件
		 */
		private void onMousePressed(MouseEvent event) {
			boolean noHandle = isEmpty() || // 空行单元格
					event.getButton() != MouseButton.PRIMARY || // 不是鼠标左击
					!checkBoxProperty.get();// 复选框未显示
			// 若不处理标记为true,则终止执行
			if (noHandle) {
				return;
			}
			// 阻止事件向子组件传递(否则会导致下面的代码执行无效)
			event.consume();
			// 如果当前已经选择,则不选
			if (isSelected()) {
				selection.clearSelection(getIndex());
			} else {
				// 如果未选,则选择
				selection.select(getIndex());
			}
		}

		/**
		 * 鼠标在行单元格上按下并释放时触发(Handler类型:子组件传递到父组件)
		 * 
		 * @param event
		 *            鼠标事件
		 */
		private void onMouseClicked(MouseEvent event) {
			boolean noHandle = isEmpty() || // 是空行单元格
					event.getButton() != MouseButton.PRIMARY || // 不是鼠标左击
					event.getClickCount() != 2 || // 不是鼠标双击
					checkBoxProperty.get();// 正在显示复选框
			if (noHandle) {
				return;
			}
			// 获取当前播放列表对象
			PlayList oldValue = Main.playListProperty().get();
			Integer id = oldValue == null ? null : oldValue.getId();
			// 发送播放列表到主控制器
			Main.playListProperty().set(new PlayList(id, getIndex(), tableView.getItems()));
		}

		@Override
		public void handle(MouseEvent event) {
			// 若是鼠标按下
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
				onMousePressed(event);
			} else {
				// 否则是鼠标按下并已释放(因为这里只有2种类型的鼠标事件类型)
				onMouseClicked(event);
			}
		}
	}
}
