package com.scmq.player.controller;

import com.scmq.player.app.Main;
import com.scmq.player.model.MV;
import com.scmq.player.model.Music;
import com.scmq.player.model.Page;
import com.scmq.player.model.PlayList;
import com.scmq.player.model.Special;
import com.scmq.player.net.NetSource;
import com.scmq.player.service.MVService;
import com.scmq.player.service.MusicService;
import com.scmq.player.service.SpecialService;
import com.scmq.player.util.Task;
import com.scmq.player.view.NetSearchView;
import com.scmq.view.control.Spinner;
import com.scmq.view.control.Tab;
import com.scmq.view.control.TabPane;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 网络音乐搜索模块控制器
 *
 * @author SCMQ
 */
@Controller
public class SearchMusicController implements ChangeListener<Tab> {
    @Autowired
    private MusicService musicService;
    @Autowired
    private SpecialService specialService;
    @Autowired
    private MVService mvService;

    private NetSearchView view;
    private Spinner spinner;
    private String text;

    private boolean songUpdateable, specialUpdateable, mvUpdateable;
    private Page songPage = new Page(), specialPage = new Page(), mvPage = new Page();
    public static NetSource netSource;

    public SearchMusicController() {
    }

    public void show(String text, TabPane mainTabPane) {
        if (view == null) {
            view = new NetSearchView();
            spinner = new Spinner();
            view.tabProperty().addListener(this);
            view.getPagination().addListener((observable, oldPage, newPage) -> {
                Tab tab = view.tabProperty().get();
                String tabText = tab.getText();
                int current = newPage.intValue();
                // 若是“单曲”选项卡 且 单曲分页对象的当前页和分页组件当前页相同,则不触发更新
                if ("单曲" == tabText && songPage.getCurrent() != current) {
                    songPage.setCurrent(current);
                    songUpdateable = true;
                    changed(null, null, tab);
                    return;
                }
                // 若是“歌单”选项卡 且 专辑分页对象的当前页和分页组件当前页相同,则不触发更新
                if ("歌单" == tabText && specialPage.getCurrent() != current) {
                    specialPage.setCurrent(current);
                    specialUpdateable = true;
                    changed(null, null, tab);
                    return;
                }
                // 若是“MV”选项卡 且 MV分页对象的当前页和分页组件当前页相同,则不触发更新
                if ("MV" == tabText && mvPage.getCurrent() != current) {
                    mvPage.setCurrent(current);
                    mvUpdateable = true;
                    changed(null, null, tab);
                }
            });
        }
        mainTabPane.centerProperty().set(view);
        view.requestFocus();
        if (this.text == null || !this.text.equals(text)) {
            this.text = text;
            mvPage.setCurrent(1);
            songPage.setCurrent(1);
            specialPage.setCurrent(1);
            songUpdateable = specialUpdateable = mvUpdateable = true;
            if ("单曲" != view.tabProperty().get().getText()) {
                view.tabProperty().set(view.getTabs().get(0));
            } else {
                changed(null, null, view.tabProperty().get());
            }
        }
    }

    @Override
    public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
        String tabText = newValue.getText();
        if ("单曲" == tabText) {
            if (!songUpdateable) {
                view.updatePagination(songPage);
                return;
            }
            spinner.centerTo(view);
            songUpdateable = false;
            Task.async(() -> {
                List<Music> list = netSource.songSearch(text, songPage);
                Platform.runLater(() -> {
                    view.updateSong(list, songPage);
                    spinner.close();
                });
                musicService.save(list);
            });
        } else if ("歌单" == tabText) {
            if (!specialUpdateable) {
                view.updatePagination(specialPage);
                return;
            }
            spinner.centerTo(view);
            specialUpdateable = false;
            Task.async(() -> {
                List<Special> list = netSource.specialSearch(text, specialPage);
                specialService.save(list);
                specialService.handlePictures(list);
                Platform.runLater(() -> {
                    view.updateSpecial(list, specialPage);
                    spinner.close();
                });
            });
        } else if ("MV" == tabText) {
            if (!mvUpdateable) {
                view.updatePagination(mvPage);
                return;
            }
            spinner.centerTo(view);
            mvUpdateable = false;
            Task.async(() -> {
                List<MV> list = netSource.mvSearch(text, mvPage);
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
            @SuppressWarnings("unchecked") List<MV> mvList = (List<MV>) node.getParent().getUserData();
            Integer index = (Integer) node.getUserData();
            Main.playListProperty().set(new PlayList(index, mvList));
        }
    };
}
