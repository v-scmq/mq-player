package com.scmq.player.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * 统一维护所有必须资源的一个工具类,这些资源包括SVG图标、图片、CSS样式等.
 * 
 * <pre>
 *	SVGPath语法: 大写是绝对路径,小写是相对路径
 *	M=moveTo ; L=lineTo ; H=hLineTo ; V=vLineTo ; C=curveTo ; S=smooth
 *	Q=quadratic Belzier CurveTo ; A=elliptical Arc ; Z=closePath
 * </pre>
 * 
 * @author SCMQ
 * @since 2020/10/03
 */
public class Resource {
	/** 最小化图标 */
	public static final String MINIMIZE_ICON = "M6 6 h12";

	/** 窗口已最大化时显示的图标 */
	public static final String MAXIMIZED_ICON = "M3 0 h9 v9 h-3 M3 0 v3 h-3 v9 h9 v-9 h-6";
	/** 窗口未最大化时显示的图标 */
	public static final String MAXIMIZE_ICON = "M0 0 h12 v12 h-12 v-12";

	/** 关闭图标 */
	public static final String CLOSEABLE_ICON = "M0 0 L12 12 M12 0 L0 12";
	/** 对话框关闭图标 */
	public static final String DIALOG_CLOSEABLE_ICON = "M0 0 L16 16 M16 0 L0 16";

	// 未全屏图标
	public static final String FULL_SCREEN_ICON = "M23 25h-4v-2h2.63l-5.753-5.658 1.354-1.331 5.769 5.674v-2.685h2v6h-2zm0-15.669l-5.658 5.658-1.331-1.331 5.658-5.658h-2.669v-2h6v6h-2v-2.669zm-15.027 15.669h4.027v-2h-2.676l5.676-5.658-1.335-1.331-5.692 5.674v-2.685h-1.973v6h1.973zm0-15.669l5.581 5.658 1.313-1.331-5.582-5.658h2.715v-2h-6v6h1.973v-2.669z";
	// 已全屏图标
	public static final String FULL_SCREENED_ICON = "M22 12v2h-6v-6h2v2.669l5.658-5.658 1.331 1.331-5.658 5.658h2.669zm-10 7.331l-5.658 5.658-1.331-1.331 5.658-5.658h-2.669v-2h6v6h-2v-2.669zm-4-7.331h2.669l-5.658-5.658 1.331-1.331 5.658 5.658v-2.669h2v6h-6v-2zm14 6h-2.669l5.658 5.658-1.331 1.331-5.658-5.658v2.669h-2v-6h6v2z";

	/** 后退图标 */
	public static final String BACK_ICON = "M8 0 L0 8 L8 16";
	/** 前进图标 */
	public static final String FORWARD_ICON = "M8 0 L16 8 L8 16";

	/** 刷新图标 */
	public static final String REFRESH_ICON = "M8 3a5 5 0 1 0 4.546 2.914.5.5 0 0 1 .908-.417A6 6 0 1 1 8 2v1z M8 4.466V.534a.25.25 0 0 1 .41-.192l2.36 1.966c.12.1.12.284 0 .384L8.41 4.658A.25.25 0 0 1 8 4.466z";

	/** 耳机图标 */
	public static final String HEADSET_ICON = "M8 3a5 5 0 0 0-5 5v4.5H2V8a6 6 0 1 1 12 0v4.5h-1V8a5 5 0 0 0-5-5z M11 10a1 1 0 0 1 1-1h2v4a1 1 0 0 1-1 1h-1a1 1 0 0 1-1-1v-3zm-6 0a1 1 0 0 0-1-1H2v4a1 1 0 0 0 1 1h1a1 1 0 0 0 1-1v-3z";
	/** 皮肤图标 */
	public static final String SKIN_ICON = "M772.8 96v64L936 321.6l-91.2 91.2c-12.8-11.2-27.2-16-43.2-16-36.8 0-65.6 28.8-65.6 65.6V800c0 35.2-28.8 64-64 64H352c-35.2 0-64-28.8-64-64V462.4c0-36.8-28.8-65.6-65.6-65.6-16 0-32 6.4-43.2 16L88 321.6 249.6 160h40l1.6 1.6C336 228.8 420.8 272 512 272c91.2 0 176-41.6 220.8-110.4 0-1.6 1.6-1.6 1.6-1.6h38.4V96m-481.6 0H256c-22.4 0-38.4 6.4-49.6 19.2L43.2 276.8c-25.6 25.6-25.6 65.6 0 89.6l94.4 94.4c11.2 11.2 27.2 17.6 41.6 17.6s30.4-6.4 41.6-17.6h1.6c1.6 0 1.6 0 1.6 1.6V800c0 70.4 57.6 128 128 128h320c70.4 0 128-57.6 128-128V462.4c0-1.6 0-1.6 1.6-1.6h1.6c11.2 11.2 27.2 17.6 41.6 17.6 16 0 30.4-6.4 41.6-17.6l94.4-94.4c25.6-25.6 25.6-65.6 0-89.6L819.2 115.2C806.4 102.4 790.4 96 772.8 96h-40c-22.4 0-41.6 11.2-54.4 30.4-33.6 49.6-96 81.6-168 81.6s-134.4-33.6-168-81.6C332.8 107.2 312 96 291.2 96z";
	/** 设置图标 */
	public static final String SETTING_ICON = "M8.837 1.626c-.246-.835-1.428-.835-1.674 0l-.094.319A1.873 1.873 0 0 1 4.377 3.06l-.292-.16c-.764-.415-1.6.42-1.184 1.185l.159.292a1.873 1.873 0 0 1-1.115 2.692l-.319.094c-.835.246-.835 1.428 0 1.674l.319.094a1.873 1.873 0 0 1 1.115 2.693l-.16.291c-.415.764.42 1.6 1.185 1.184l.292-.159a1.873 1.873 0 0 1 2.692 1.116l.094.318c.246.835 1.428.835 1.674 0l.094-.319a1.873 1.873 0 0 1 2.693-1.115l.291.16c.764.415 1.6-.42 1.184-1.185l-.159-.291a1.873 1.873 0 0 1 1.116-2.693l.318-.094c.835-.246.835-1.428 0-1.674l-.319-.094a1.873 1.873 0 0 1-1.115-2.692l.16-.292c.415-.764-.42-1.6-1.185-1.184l-.291.159A1.873 1.873 0 0 1 8.93 1.945l-.094-.319zm-2.633-.283c.527-1.79 3.065-1.79 3.592 0l.094.319a.873.873 0 0 0 1.255.52l.292-.16c1.64-.892 3.434.901 2.54 2.541l-.159.292a.873.873 0 0 0 .52 1.255l.319.094c1.79.527 1.79 3.065 0 3.592l-.319.094a.873.873 0 0 0-.52 1.255l.16.292c.893 1.64-.902 3.434-2.541 2.54l-.292-.159a.873.873 0 0 0-1.255.52l-.094.319c-.527 1.79-3.065 1.79-3.592 0l-.094-.319a.873.873 0 0 0-1.255-.52l-.292.16c-1.64.893-3.433-.902-2.54-2.541l.159-.292a.873.873 0 0 0-.52-1.255l-.319-.094c-1.79-.527-1.79-3.065 0-3.592l.319-.094a.873.873 0 0 0 .52-1.255l-.16-.292c-.892-1.64.902-3.433 2.541-2.54l.292.159a.873.873 0 0 0 1.255-.52l.094-.319z M8 5.754a2.246 2.246 0 1 0 0 4.492 2.246 2.246 0 0 0 0-4.492zM4.754 8a3.246 3.246 0 1 1 6.492 0 3.246 3.246 0 0 1-6.492 0z";

	// Resource.class.getResource("") 类所在目录
	// Resource.class.getResource("/") bin目录下
	// Resource.class.getResource("..") 类的所在目录的父目录

	/**
	 * 获取程序内部资源URL地址
	 *
	 * @param names
	 *            名称(可变参数)
	 * @return URL字符串
	 */
	private static String getResource(String... names) {
		StringBuilder builder = new StringBuilder();
		for (String name : names) {
			builder.append(name);
		}
		return Resource.class.getResource(builder.toString()).toString();
	}

	/**
	 * 获取程序内部CSS样式资源文件地址
	 *
	 * @param name
	 *            CSS样式资源文件名称(不包含文件格式名)
	 * @return 返回一个CSS样式资源文件的URL地址的字符串表示
	 */
	public static String getStyleSheet(String name) {
		return getResource("/style/", name, ".css");
	}

	/**
	 * 获取程序内部的PNG格式图片URI
	 *
	 * @param name
	 *            图片名称
	 * @return 图片URI
	 */
	public static String getImageURI(String name) {
		return getImageURI(name, ".png");
	}

	/**
	 * 获取程序内部的图片URI
	 *
	 * @param name
	 *            图片名称
	 * @param format
	 *            图片格式(例如'.png').
	 * @return 图片URI
	 */
	public static String getImageURI(String name, String format) {
		return getResource("/icon/", name, format);
	}

	/**
	 * 获取Image对象表示的图像
	 *
	 * @param name
	 *            图片文件名称(不包含文件格式)
	 * @return Image对象
	 */
	public static Image createImage(String name) {
		return new Image(getImageURI(name));
	}

	/**
	 * 获取Image对象表示的图像
	 *
	 * @param name
	 *            图片文件名称(不包含文件格式)
	 * @param format
	 *            图片格式(如'.png')
	 * @return Image对象
	 */
	public static Image createImage(String name, String format) {
		return new Image(getImageURI(name, format));
	}

	/**
	 * 通过资源名来创建一个ImageView组件,默认具有空白域可点击性
	 *
	 * @param resource
	 *            资源名(程序内部)
	 * @return 图片视图组件
	 */
	public static ImageView createView(String resource) {
		ImageView view = new ImageView(createImage(resource));
		view.setPickOnBounds(true);
		return view;
	}

	/**
	 * 通过Image对象来创建一个ImageView组件,默认具有空白域可点击性
	 *
	 * @param image
	 *            Image对象(图像)
	 * @return 图片视图组件
	 */
	public static ImageView createView(Image image) {
		ImageView view = new ImageView(image);
		view.setPickOnBounds(true);
		return view;
	}

	/**
	 * 通过资源名、宽度、高度和空白域可点击性来创建一个ImageView组件
	 *
	 * @param resource
	 *            资源名(程序内部)
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @return 图片视图组件
	 */
	public static ImageView createView(String resource, double width, double height) {
		ImageView view = new ImageView(createImage(resource));
		view.setPickOnBounds(true);
		view.setFitHeight(height);
		view.setFitWidth(width);
		return view;
	}

	/**
	 * 通过资源名、宽度、空白域可点击性来创建一个ImageView组件
	 *
	 * @param resource
	 *            资源名(程序内部)
	 * @param width
	 *            宽度
	 * @param ratio
	 *            是否保持图片比例
	 * @return 图片视图组件
	 */
	public static ImageView createView(String resource, double width, boolean ratio) {
		ImageView view = new ImageView(createImage(resource));
		view.setPreserveRatio(ratio);
		view.setPickOnBounds(true);
		view.setFitWidth(width);
		return view;
	}

	/**
	 * 通过资源名、宽度、是否保持宽高比例来创建一个ImageView组件
	 *
	 * @param resource
	 *            资源名(程序内部GIF图片)
	 * @param width
	 *            宽度
	 * @param ratio
	 *            是否保持图片比例
	 * @return 图片视图组件
	 */
	public static ImageView createGifView(String resource, double width, boolean ratio) {
		ImageView view = new ImageView(createImage(resource, ".gif"));
		view.setPreserveRatio(ratio);
		view.setFitWidth(width);
		return view;
	}
}
