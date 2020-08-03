package com.scmq.player.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scmq.player.model.User;
import com.scmq.player.net.HttpClient;
import com.scmq.player.util.StringUtil;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;

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
		String qq = user.getQq();
		StringBuilder builder = new StringBuilder(562);
		String cookie = builder.append("o_cookie=").append(qq).append(";uin=").append(qq).toString();

		StringUtil.clear(builder).append("https://u.y.qq.com/cgi-bin/musicu.fcg?g_tk=5381&loginUin=").append(qq);
		builder.append("&hostUin=0&format=json&inCharset=utf8&outCharset=GB2312&notice=0&platform=yqq.json&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A0%7D%2C%22vip%22%3A%7B%22module%22%3A%22userInfo.VipQueryServer%22%2C%22method%22%3A%22SRFVipQuery_V2%22%2C%22param%22%3A%7B%22uin_list%22%3A%5B%22");
		builder.append(qq).append("%22%5D%7D%7D%2C%22base%22%3A%7B%22module%22%3A%22userInfo.BaseUserInfoServer%22%2C%22method%22%3A%22get_user_baseinfo_v2%22%2C%22param%22%3A%7B%22vec_uin%22%3A%5B%22");
		builder.append(qq).append("%22%5D%7D%7D%7D");

		HttpClient client = HttpClient.createClient(cookie);
		client.setReferer("https://y.qq.com/");

		BufferedReader reader = client.get(builder.toString()).openReader("UTF-8");
		if (reader == null) {
			client.close();
			return false;
		}
		JsonObject node = new JsonParser().parse(reader).getAsJsonObject();
		client.close();
		node = node.getAsJsonObject("base").getAsJsonObject("data").getAsJsonObject("map_userinfo").getAsJsonObject(qq);
		if (node == null) {
			return false;
		}
		user.setHeadURI(node.get("headurl").getAsString());
		user.setName(node.get("nick").getAsString());
		return StringUtil.isNotEmpty(user.getHeadURI());
	}
}
