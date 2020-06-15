package com.scmq.view.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * {@link Tab}类表示了一个选项卡组件,它不同与传统的Tab(选项卡),这个类本身就是组件,可直接像普通组件一样使用.
 *
 * @author SCMQ
 */
public final class Tab extends Button {
    /** Tab组件默认样式类 */
    private static final String STYLE_CLASS = "tab";
    /** Tab组件对应的内容节点的样式类 */
    private static final String CONTENT_CLASS = "content";
    /** 是否选中这个选项卡 */
    private boolean selected;

    /** Tab页面内容属性 */
    private final ObjectProperty<Node> contentProperty = new SimpleObjectProperty<Node>() {
        protected void invalidated() {
            Node content = get();
            if (content != null) {
                ObservableList<String> classList = content.getStyleClass();
                if (!classList.contains(CONTENT_CLASS)) {
                    classList.add(CONTENT_CLASS);
                }
            }
        }

        ;
    };

    /**
     * 构造一个选项卡(Tab)组件对象
     *
     * @param text    选项卡标题文本
     * @param content 选项卡的页面内容节点
     */
    public Tab(String text, Node content) {
        getStyleClass().setAll(STYLE_CLASS);
        setContent(content);
        setText(text);
    }

    /**
     * 获取选项卡的页面内容节点属性对象
     *
     * @return 页面内容节点属性对象
     */
    public ObjectProperty<Node> contentProperty() {
        return contentProperty;
    }

    /**
     * 获取选项卡的页面内容节点
     *
     * @return 页面内容节点
     */
    public Node getContent() {
        return contentProperty.get();
    }

    /**
     * 设置选项卡的页面内容节点
     */
    public void setContent(Node value) {
        contentProperty.set(value);
    }

    /**
     * 获取当前选项卡(Tab)是否选中
     *
     * @return 若Tab已被选中 则返回true.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 设置当前选项卡(Tab)是否选中.</br>
     * 注意,此方法不建议在外部调用.仅在{@link TabPane}中调用
     *
     * @param value 若为true,则标记选项卡已选中,否则为选中.
     * @see TabPane
     */
    void setSelected(boolean value) {
        this.selected = value;
    }
}
