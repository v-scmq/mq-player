package com.scmq.player.service;

import com.scmq.player.model.User;
import com.scmq.player.util.StringUtil;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	/**
	 * 检查QQ号是否合法
	 *
	 * @param qq
	 *            QQ号(必须是数字字符串)
	 * @return 若QQ号是合法的则返回null, 否则返回错误信息
	 */
	public String check(String qq) {
		if (StringUtil.isEmpty(qq)) {
			return "QQ号不能为空！";
		}
		if (qq.length() < 5 || qq.length() > 11) {
			return "QQ必须在5~11位之间！";
		}
		return qq.charAt(0) == '0' ? "QQ号第一位不能是0！" : null;
	}

	/**
	 * QQ用户登录
	 *
	 * @param user
	 *            QQ用户信息
	 * @return 若登录成功, 则返回true
	 */
	public boolean login(User user) {
		return false;
	}
}
