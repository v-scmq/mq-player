package com.scmq.player.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.scmq.player.app.Main;
import com.scmq.player.model.User;
import com.scmq.player.service.UserService;
import com.scmq.player.util.FileUtil;
import com.scmq.player.util.Task;
import com.scmq.view.control.Dialog;
import com.scmq.view.control.EditText;
import com.scmq.view.control.Toast;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * 用户模块控制器
 *
 * @author SCMQ
 */
@Controller
public class UserController {
	/** 用户业务对象 */
	@Autowired
	private UserService userService;

	/** 显示用户头像 */
	private ImageView headImageView;
	/** 用户名称按钮 */
	private Button userNameButton;

	/** 用户信息 */
	private User user;
	/** 登录、退出对话框 */
	private Dialog dialog;
	/** QQ号输入框 */
	private EditText qqInput;
	/** 登录、注销按钮 */
	private Button login;

	/** 是否已登录(在线) */
	private boolean online;

	/** 创建对话框 */
	private void createDialog() {
		if (dialog == null) {
			qqInput = new EditText(FileUtil.createView("user"), true);
			qqInput.setPromptText("请输入QQ号");

			login = new Button("登录");

			VBox content = new VBox(20, qqInput, login);
			content.setAlignment(Pos.CENTER);
			dialog = new Dialog("QQ登录", content);

			qqInput.getStyleClass().add("qq-input");
			login.getStyleClass().setAll("login-button");

			qqInput.setOnAction(action);
			login.setOnAction(action);
		}
	}

	/**
	 * 初始化并绑定事件
	 *
	 * @param headImageView
	 *            显示用户头像的ImageView
	 * @param userNameButton
	 *            显示用户名称的Button
	 */
	public void bind(ImageView headImageView, Button userNameButton) {
		this.headImageView = headImageView;
		this.userNameButton = userNameButton;

		headImageView.setOnMouseClicked(handler);
		userNameButton.setOnMouseClicked(handler);
	}

	/** 登录、退出对话框弹出 */
	private EventHandler<MouseEvent> handler = e -> {
		if (e.getButton() == MouseButton.PRIMARY) {
			createDialog();
			// 若已登录,则不可再输入QQ号
			qqInput.setDisable(online);
			// 改变按钮文本
			login.setText(online ? "注销" : "登录");
			// 设置QQ好输入框的文本.若已登录过,则设置为之前的用户QQ号
			qqInput.setText(user == null ? null : user.getQq());
			dialog.show(Main.getRoot());
		}
	};

	/** 登录、退出操作 */
	private EventHandler<ActionEvent> action = e -> {
		// 若已登录,则退出登录
		if (online) {
			online = false;
			dialog.close();
			userNameButton.setText("点击登录");
			headImageView.setImage(FileUtil.createImage("user_head"));
			Toast.makeText(Main.getRoot(), "退出成功！").show();
			return;
		}
		// 获取新输入的QQ号
		String qq = qqInput.getText();
		// 检查QQ号是否合法,若不合法,则message消息表示了QQ号错误原因
		String message = userService.check(qq);
		if (message != null) {
			Toast.makeText(Main.getRoot(), message).show();
			return;
		}
		// 未登录,开始做登录准备.
		dialog.close();
		userNameButton.setText("登录中");
		if (user == null) {
			user = new User(qq);
		} else {
			user.setQq(qq);
		}
		Task.async(() -> {
			// 获取QQ用户基本信息(头像、昵称)
			boolean success = userService.login(user);
			Platform.runLater(() -> {
				// 登录成功
				if (success) {
					userNameButton.setText(user.getName());
					headImageView.setImage(new Image(user.getHeadURI(), true));
					Toast.makeText(Main.getRoot(), "登录成功！").show();
					online = true;
					return;
				}
				// 登录失败
				userNameButton.setText("点击登录");
				dialog.show(Main.getRoot());
				Toast.makeText(Main.getRoot(), "登录失败！").show();
			});
		});
	};
}
