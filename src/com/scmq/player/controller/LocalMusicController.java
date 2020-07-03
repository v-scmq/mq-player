package com.scmq.player.controller;

import com.scmq.player.app.Main;
import com.scmq.player.model.LocalList;
import com.scmq.player.model.Media;
import com.scmq.player.model.Music;
import com.scmq.player.model.PlayList;
import com.scmq.player.service.LocalListService;
import com.scmq.player.util.StringUtil;
import com.scmq.player.util.Task;
import com.scmq.player.view.LocalMusicView;
import com.scmq.view.control.Spinner;
import com.scmq.view.control.Toast;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地模块控制器
 *
 * @author SCMQ
 */
@Controller
public class LocalMusicController {
	/** 本地列表数据层业务对象 */
	@Autowired
	private LocalListService service;

	/** 本地音乐列表对象 */
	private LocalList localList;

	private Spinner spinner;

	public LocalMusicController() {
	}

	void bind(LocalMusicView view) {
		// 在子线程中查询所有本地音乐
		Task.async(() -> {
			LocalList localList = service.findLocalListOfFirst();
			List<Music> items = localList == null ? null : localList.getMusics();
			if (items != null && !items.isEmpty()) {
				// 同步到UI线程,更新本地音乐列表
				Platform.runLater(() -> {
					this.localList = localList;
					view.getTableView().getItems().addAll(items);
				});
			}
		});

		// 播放全部 或 播放所选 按钮
		view.getPlayAllButton().setOnAction(e -> {
			String text = view.getPlayAllButton().getText();
			ObservableList<Music> items;
			if ("播放" == text) {
				items = view.getTableView().getSelectionModel().getSelectedItems();
				if (items.isEmpty()) {
					Toast.makeText(Main.getRoot(), "至少选择一首音乐，才能播放！").show();
					return;
				}
			} else {
				items = view.getTableView().getItems();
				if (items.isEmpty()) {
					Toast.makeText(Main.getRoot(), "本地音乐列表没有任何歌曲！").show();
					return;
				}
			}
			// 获取当前播放列表对象
			PlayList oldValue = Main.playListProperty().get();
			Integer id = oldValue == null ? null : oldValue.getId();
			// 发送播放列表到主控制器
			Main.playListProperty().set(new PlayList(id, 0, items));
		});

		// 添加本地音乐事件
		view.getLeadForFile().setOnAction(e -> {
			if (!StringUtil.isEmpty(view.getInputLocalSearchKey().getText())) {
				Toast.makeText(Main.getRoot(), "请先清除搜索记录！").show();
				return;
			}
			FileChooser fileChooser = new FileChooser();
			ObservableList<ExtensionFilter> filters = fileChooser.getExtensionFilters();
			filters.add(new ExtensionFilter("所有音乐文件", "*.mp3", "*.flac", "*.wav", "*.ape", "*.m4a", "*.wma", "*.aac",
					"*.ogg"));
			filters.add(new ExtensionFilter("MP3音乐文件", "*.mp3"));
			filters.add(new ExtensionFilter("FLAC无损音乐文件", "*.flac"));
			filters.add(new ExtensionFilter("APE无损音乐文件", "*.ape"));
			filters.add(new ExtensionFilter("Wave音乐文件", "*.wav"));
			// 打开文件选择器(多选), 在关闭时获得所选文件List集合
			List<File> files = fileChooser.showOpenMultipleDialog(Main.getPrimaryStage());
			// 如果未选择任何文件
			if (files == null || files.isEmpty()) {
				return;
			}
			// 本地音乐列表
			ObservableList<Music> list = view.getTableView().getItems();
			// 准备添加的音乐
			List<Music> saveList = new ArrayList<>(files.size());
			for (File file : files) {
				Music music = new Music(file);
				if (!list.contains(music)) {
					// 不是重复的则添加
					saveList.add(music);
				}
			}
			// 如果没有需要添加的音乐,则结束下面的执行
			if (saveList.isEmpty()) {
				return;
			}
			// 添加到表格中
			list.addAll(saveList);
			LocalList localList = this.localList == null ? new LocalList() : this.localList;
			// 在子线程保存新增音乐
			localList.setMusics(saveList);
			Task.async(() -> service.saveLocalList(localList));
		});

		// 从本地目录添加动作事件
		view.getLeadForDir().setOnAction(e -> {
			if (!StringUtil.isEmpty(view.getInputLocalSearchKey().getText())) {
				Toast.makeText(Main.getRoot(), "请先清除搜索记录！").show();
				return;
			}
			// 打开目录选择器
			File path = new DirectoryChooser().showDialog(Main.getPrimaryStage());
			// 若未选择目录,则终止执行
			File[] files = path == null ? null : path.listFiles(Media.getAudioFileFilter());
			// 若没有任何歌曲,则终止执行
			if (files == null || files.length == 0) {
				return;
			}
			List<Music> saveList = new ArrayList<>(files.length);
			// 本地音乐视图中的音乐信息列表
			ObservableList<Music> list = view.getTableView().getItems();
			for (File file : files) {
				Music music = new Music(file);
				// 如果不重复则添加
				if (!list.contains(music)) {
					saveList.add(music);
				}
			}
			// 如果没有需要添加的音乐,则结束下面的执行
			if (saveList.isEmpty()) {
				return;
			}
			// 添加到表格中
			list.addAll(saveList);
			LocalList localList = this.localList == null ? new LocalList() : this.localList;
			localList.setMusics(saveList);
			// 在子线程保存新增音乐
			Task.async(() -> service.saveLocalList(localList));
		});

		// 排序类型回调
		EventHandler<ActionEvent> action = e -> {
			Object menuItem = e.getSource();

			@SuppressWarnings("unchecked")
			TableColumn<Music, ?> column = (TableColumn<Music, ?>) view.getTableView().getProperties().get(menuItem);

			SortType type = column.getSortType() == SortType.DESCENDING ? SortType.ASCENDING : SortType.DESCENDING;

			column.setSortType(type);
			view.getTableView().getSortOrder().clear();
			view.getTableView().getSortOrder().add(column);
		};

		// 绑定排序菜单项动作事件
		view.getTitleSort().setOnAction(action);
		view.getSingerSort().setOnAction(action);
		view.getAlbumSort().setOnAction(action);
		view.getDurationSort().setOnAction(action);
		view.getSizeSort().setOnAction(action);

		// 批量删除按钮事件处理
		view.getDeleteButton().setOnAction(e -> {
			MultipleSelectionModel<Music> selection = view.getTableView().getSelectionModel();
			// 本地音乐视图中的音乐信息列表
			ObservableList<Music> list = selection.getSelectedItems();
			if (list == null || list.isEmpty()) {
				Toast.makeText(Main.getRoot(), "至少选择一项，才能操作！").show();
				return;
			}
			Integer[] musicIds = new Integer[list.size()];
			int index = 0;
			for (Music music : list) {
				musicIds[index++] = music.getId();
			}
			// 子线程中删除音乐记录(**需要考虑数据库删除失败的问题**)
			Task.async(() -> service.deleteLocalMediaItem(musicIds));
			// 同步视图删除所选音乐记录
			if (view.getTableView().getItems().removeAll(list)) {
				// 视图同步删除成功 后 清除选择
				selection.clearSelection();
			}
		});

		// 模糊搜索本地音乐
		view.getInputLocalSearchKey().textProperty().addListener((observable, oldValue, newValue) -> {
			// 获取本地音乐列表ID
			Integer id = localList == null ? null : localList.getId();
			// 准备进度旋转器组件
			spinner = spinner == null ? new Spinner() : spinner;
			// 显示进度旋转器
			spinner.centerTo(view);
			// 异步检索音乐信息
			Task.async(() -> {
				// 开始执行检索
				LocalList localList = service.findByInfo(id, newValue);
				// 获取检索音乐列表记录
				List<Music> list = localList == null ? null : localList.getMusics();
				// 同步到UI线程
				Platform.runLater(() -> {
					// 清空表格数据
					view.getTableView().getItems().clear();
					// 若没有搜索到任何记录
					if (list == null || list.isEmpty()) {
						Toast.makeText(Main.getRoot(), "没有找到任何匹配的音乐").show();
					} else {
						// 否则添加记录到表格视图中
						view.getTableView().getItems().addAll(list);
					}
					// 关闭进度旋转器
					spinner.close();
				});
			});
		});
	}
}
