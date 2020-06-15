package com.scmq.view.control;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * 自定义分页组件。该组件已经注册默认的事件处理器，如果需要拦截，可获取子组件针对某个组件添加事件过滤器。 例如“上一页”、“下一页”等.<br>
 * 需要注意的是：拦截的事件源都是Button或TextField.对于Button而言需要过滤鼠标按下事件，但不能重新注册ActionEvent事件，
 * 否则会取消默认的事件处理器；对于TextField需要重新注册ActionEvent事件。
 *
 * @author SCMQ
 */
public class Pagination extends HBox {
    /** 当前页编号按钮的样式类 */
    public static final String SELECTED_CLASS = "current";
    /** 普通分页按钮的样式类 */
    private static final String STYLE_CLASS = "page-button";

    /** 页编号 属性 */
    private IntegerProperty pageProperty = new SimpleIntegerProperty();
    /** 总页数 属性 */
    private IntegerProperty totalProperty = new SimpleIntegerProperty(1);

    /** “首页、上一页、下一页、尾页” 按钮 */
    private Button first, prev, next, last;
    /** 页编号输入框 */
    private TextField pageInput;

    /** 构造一个默认的分页组件 */
    public Pagination() {
        Label totalLabel = new Label("共1页");

        first = new Button("首页");
        prev = new Button("上一页");

        Button num1 = new Button("1");
        Button num2 = new Button("2");
        Button num3 = new Button("3");
        Button num4 = new Button("4");
        Button num5 = new Button("5");

        next = new Button("下一页");
        last = new Button("尾页");

        Button skip = new Button("转到");
        pageInput = new TextField() {
            boolean isNumber(String text) {
                for (int i = 0; i < text.length(); i++) {
                    if (text.charAt(i) < '0' || text.charAt(i) > '9') {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void replaceText(int start, int end, String text) {
                if (text.length() == 0 || isNumber(text)) {
                    super.replaceText(start, end, text);
                }
            }
        };
        pageInput.setFocusTraversable(false);
        pageInput.setPromptText("页编号");

        ObservableList<Node> nodes = getChildren();
        nodes.addAll(totalLabel, first, prev, num1, num2, num3, num4, num5, next, last, pageInput, skip);

        // first~last -> 都有相同的样式类
        for (int index = 1; index < 10; index++) {
            nodes.get(index).getStyleClass().set(0, STYLE_CLASS);
        }

        totalLabel.getStyleClass().set(0, "total-page");
        first.getStyleClass().add("page-first");
        num1.getStyleClass().set(0, SELECTED_CLASS);
        last.getStyleClass().add("page-last");
        pageInput.getStyleClass().set(0, "page-input");
        skip.getStyleClass().set(0, "page-skip");
        getStyleClass().add("pagination");

        setMargin(first, new Insets(0, 0, 0, 10));
        setMargin(pageInput, getMargin(first));
        setAlignment(Pos.CENTER);

        /* ***************************************
         * 				注册默认的事件处理器 		 *
         *****************************************/
        // 到首页
        first.setOnAction(e -> pageProperty.set(1));
        // 到上一页
        prev.setOnAction(e -> {
            int current = pageProperty.get() - 1;
            pageProperty.set(current < 0 ? 1 : current);
        });
        // 到下一页
        next.setOnAction(e -> {
            int current = pageProperty.get() + 1;
            int total = totalProperty.get();
            pageProperty.set(current > total ? total : current);
        });
        // 到尾页
        last.setOnAction(e -> pageProperty.set(totalProperty.get()));
        // 文本框的默认回车事件
        pageInput.setOnAction(e -> setPage(pageInput.getText()));
        // 跳转按钮触发文本框事件
        skip.setOnAction(pageInput.getOnAction());

        // 5个数字页编号(按钮)
        Button[] buttons = {num1, num2, num3, num4, num5};
        // 5个数字页编号(按钮) 的事件处理器
        EventHandler<ActionEvent> handler = e -> {
            Button button = (Button) e.getSource();
            if (button.getStyleClass().get(0) != SELECTED_CLASS) {
                pageProperty.set(Integer.parseInt(button.getText()));
            }
        };
        for (Button button : buttons) {
            button.setOnAction(handler);
        }

        // 当前页改变时,更新UI
        pageProperty.addListener((observable, oldPage, newPage) -> update(buttons));
        // 总页数改变时,更新UI
        totalProperty.addListener((observable, oldValue, total) -> {
            StringBuilder builder = new StringBuilder(10);
            builder.append('共').append(total.intValue()).append('页');
            totalLabel.setText(builder.toString());
            update(buttons);
        });
        // 设置当前页为第一页,目的在于触发当前页编号属性改变事件来更新UI
        pageProperty.set(1);
    }

    /**
     * 更新分页编号
     *
     * @param buttons 页编号1~5的按钮
     */
    private void update(Button[] buttons) {
        int current = pageProperty.get(), total = totalProperty.get();
        if (current > total) {
            return;
        }
        // 左边起始页编号, 右边结束页编号
        int left = current - 2, right = current + 2;
        // 如果左边页编号小于1,使其为1,同时右边结束页编号同时增加
        for (; left < 1; left++, right++) ;
        // 如果右边结束页编号超过总页数时,使其置为总页数
        if (right > total) {
            right = total;
        }
        // 如果从起始编号到结束编号没有5页,且左边页编号大于1,则左边页编号左移(逐减)
        for (; left > 1 && right - left < 4; left--) ;

        int index = 0;
        for (; left <= right; left++, index++) {
            buttons[index].setManaged(true);
            buttons[index].setVisible(true);
            buttons[index].getStyleClass().set(0, current == left ? SELECTED_CLASS : STYLE_CLASS);
            buttons[index].setText(Integer.toString(left));
        }
        // 总页数不超过5页时,多余部分不显示(不受布局容器约束,会自动不显示,且不占用有空间)
        for (; index < 5; index++) {
            buttons[index].setManaged(false);
            buttons[index].setVisible(false);
        }
        boolean disabled = current == 1;
        first.setDisable(disabled);
        prev.setDisable(disabled);
        disabled = current >= total;
        next.setDisable(disabled);
        last.setDisable(disabled);
    }

    /**
     * 设置当前页编号
     *
     * @param current 当前页编号
     */
    public void setPage(int current) {
        if (current > 0 && current <= totalProperty.get()) {
            pageProperty.set(current);
        }
    }

    /**
     * 通过一个字符来设置当前页编号
     *
     * @param current 当前页编号的字符串表示(必须是数值字符序列)
     */
    private void setPage(String current) {
        if (current.length() != 0) {
            setPage(Integer.parseInt(current));
        }
    }

    /**
     * 获取当前页编号
     *
     * @return 当前页编号
     */
    public int getPage() {
        return pageProperty.get();
    }

    /**
     * 设置总页数
     *
     * @param total 总页数
     */
    public void setTotal(int total) {
        if (total > 0) {
            totalProperty.set(total);
        }
    }

    /**
     * 获取 页编号输入框
     *
     * @return 页编号输入框
     */
    public TextField getPageInput() {
        return pageInput;
    }

    /**
     * 添加当前页改变事件监听器
     *
     * @param listener 当前页编号改变事件监听器
     */
    public void addListener(ChangeListener<? super Number> listener) {
        pageProperty.addListener(listener);
    }

    /**
     * 添加事件过滤器
     *
     * @param filter 按钮 鼠标按下时的事件处理器
     * @param action 转到按钮 和 页编号输入框 的动作事件
     */
    public void addEventFilter(EventHandler<MouseEvent> filter, EventHandler<ActionEvent> action) {
        for (Node node : getChildren()) {
            if (node instanceof Button) {
                if (node.getStyleClass().get(0) == "page-skip") {
                    Button button = (Button) node;
                    button.setOnAction(action);
                } else {
                    node.addEventFilter(MouseEvent.MOUSE_PRESSED, filter);
                }
            }
            if (node instanceof TextField) {
                pageInput.setOnAction(action);
            }
        }
    }
}
