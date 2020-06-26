package com.scmq.view.control;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

/**
 * 文本输入框组件.它本身是一个布局面板,它可以为输入框设置左右的图标或其他任何节点.
 *
 * @author SCMQ
 */
public class EditText extends HBox {
	/** 文本输入框 */
	private final TextField textField;
	/** 左节点 */
	private Node leftNode;
	/** 右节点 */
	private Node rightNode;

	/**
	 * 通过文本、右节点 构造一个输入框
	 *
	 * @param text
	 *            输入框文本
	 * @param rightNode
	 *            输入框内的右节点
	 */
	public EditText(String text, Node rightNode) {
		this(text, null, rightNode);
	}

	/**
	 * 通过文本、ImageView类型的右节点 构造一个输入框
	 *
	 * @param text
	 *            输入框文本
	 * @param rightNode
	 *            输入框内的右节点
	 * @param registerListener
	 *            是否注册默认搜索事件
	 */
	public EditText(String text, ImageView rightNode, boolean registerListener) {
		this(text, rightNode);
		if (registerListener) {
			rightNode.setOnMouseClicked(e -> {
				rightNode.requestFocus();
				if (e.getButton() == MouseButton.PRIMARY) {
					textField.fireEvent(new ActionEvent());
				}
			});
		}
	}

	/**
	 * 通过文本、左节点、右节点 构造一个输入框
	 *
	 * @param text
	 *            输入框文本
	 * @param leftNode
	 *            输入框内左节点
	 * @param rightNode
	 *            输入框内右节点
	 */
	public EditText(String text, Node leftNode, Node rightNode) {
		this(text, leftNode, rightNode, false);
	}

	/**
	 * 通过左节点和是否仅输入数字来构造一个输入框
	 *
	 * @param left
	 *            左节点
	 * @param onlyNumber
	 *            是否仅输入数字
	 */
	public EditText(Node left, boolean onlyNumber) {
		this(null, left, null, onlyNumber);
	}

	/**
	 * 创建一个输入框组件对象
	 *
	 * @param text
	 *            输入框初始显示文本
	 * @param left
	 *            输入框左边节点
	 * @param right
	 *            输入框右边节点
	 * @param onlyNumber
	 *            是否尽可以输入数字
	 */
	public EditText(String text, Node left, Node right, boolean onlyNumber) {
		// 创建输入框
		textField = onlyNumber ? createNumberField() : new TextField(text);

		setLeftNode(left);
		getChildren().add(textField);
		setRightNode(right);
		setAlignment(Pos.CENTER);
		getStyleClass().add("edit-text");
		textField.prefWidthProperty().bind(widthProperty());
		setPadding(new Insets(0, 5, 0, 5));
	}

	/**
	 * 获取左节点
	 *
	 * @return 输入框内左节点
	 */
	public Node getLeftNode() {
		return leftNode;
	}

	/**
	 * 设置输入框左边节点
	 *
	 * @param leftNode
	 *            任意节点
	 */
	public void setLeftNode(Node leftNode) {
		ObservableList<Node> nodes = getChildren();
		if (!nodes.isEmpty() && nodes.get(0) != textField) {
			nodes.remove(0);
		}
		this.leftNode = leftNode;
		if (leftNode != null) {
			nodes.add(0, leftNode);
			leftNode.getStyleClass().add("left-node");
		}
	}

	/**
	 * 获取右节点
	 *
	 * @return 输入框右节点
	 */
	public Node getRightNode() {
		return rightNode;
	}

	/**
	 * 设置输入框右边节点
	 *
	 * @param rightNode
	 *            任意节点
	 */
	public void setRightNode(Node rightNode) {
		ObservableList<Node> nodes = getChildren();
		for (int index = nodes.size() - 1; index >= 0; index--) {
			Node node = nodes.get(index);
			if (node == textField || node == leftNode) {
				break;
			}
			nodes.remove(index);
		}
		this.rightNode = rightNode;
		if (rightNode != null) {
			nodes.add(rightNode);
			rightNode.getStyleClass().add("right-node");
		}
	}

	/**
	 * 设置提示文本
	 *
	 * @param text
	 *            提示文本
	 */
	public void setPromptText(String text) {
		textField.setPromptText(text);
	}

	/**
	 * 获取文本框中的文本
	 *
	 * @return 文本框的文本
	 */
	public String getText() {
		return textField.getText();
	}

	public void setText(String text) {
		textField.setText(text);
	}

	/**
	 * 获取输入框提示文本属性
	 *
	 * @return 提示文本属性
	 */
	public StringProperty promptTextProperty() {
		return textField.promptTextProperty();
	}

	/**
	 * 获取输入框文本属性
	 *
	 * @return 文本属性
	 */
	public StringProperty textProperty() {
		return textField.textProperty();
	}

	/**
	 * 设置输入框的回车事件
	 *
	 * @param value
	 *            事件处理器
	 */
	public void setOnAction(EventHandler<ActionEvent> value) {
		textField.setOnAction(value);
	}

	/**
	 * 创建仅能够输入数字的文本输入框
	 *
	 * @return TextField(文本输入框)
	 */
	private TextField createNumberField() {
		return new TextField() {
			/**
			 * 检查是否字符串仅是数字
			 * 
			 * @param text
			 *            字符串文本
			 * @return 若是数字字符序列, 则返回true.
			 */
			boolean isNumber(String text) {
				for (int index = 0; index < text.length(); index++) {
					char c = text.charAt(index);
					if (c < '0' || c > '9') {
						return false;
					}
				}
				return true;
			}

			/**
			 * 输入框文本改变之前,回调此方法
			 * 
			 * @param start
			 *            新的文本输入在之前的位置开始索引
			 * @param end
			 *            新的文本输入在之前的位置结束索引
			 * @param text
			 *            新的输入文本
			 */
			@Override
			public void replaceText(int start, int end, String text) {
				if (text.length() == 0 || isNumber(text)) {
					super.replaceText(start, end, text);
				}
			}
		};
	}
}
