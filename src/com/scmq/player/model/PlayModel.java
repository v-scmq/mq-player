package com.scmq.player.model;

import com.scmq.player.util.Resource;

import javafx.scene.image.Image;

/**
 * 媒体播放器播放模式的枚举
 *
 * @author SCMQ
 */
public enum PlayModel {
	/** 列表循环 */
	LIST_LOOP("player/list-loop"),
	/** 顺序播放 */
	ORDER_PLAY("player/order-play"),
	/** 单曲循环 */
	SINGLE_LOOP("player/single-loop"),
	/** 随机播放 */
	RANDOM_PLAY("player/random-play");

	/** 播放模式图标 */
	private Image icon;

	/**
	 * 通过构造图标相对路径构造播放模式枚举对象
	 * 
	 * @param name
	 *            图标URI
	 */
	PlayModel(String name) {
		this.icon = Resource.createImage(name);
	}

	/**
	 * 获取播放模式图标
	 * 
	 * @return 播放模式图标
	 */
	public Image getIcon() {
		return icon;
	}
}
