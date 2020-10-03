package com.scmq.player.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class FileUtil {
	/** 程序运行环境根路径 */
	private static final File ROOT_PATH;

	static {
		Properties prop = System.getProperties();
		ROOT_PATH = new File(prop.getProperty("user.dir"));
		// 方案1
		// path = prop.getProperty("user.dir");

		// 方案2(若是jar包,则获取的jar包名称;若是开发环境,获取的是bin目录)
		// path = prop.getProperty("java.class.path");
		// path = path.substring(path.indexOf(';'));

		// 方案3
		// path = new File("").getAbsolutePath();
	}

	private FileUtil() {
	}

	/**
	 * 解决字符序列不能用于Windows操作系统平台的文件或文件夹名称. <br>
	 * 对于Windows平台,文件或文件夹名称一定不能包含 “{@code / \ * ? " : | < >}”中的任一字符.
	 *
	 * @param name
	 *            文件或文件夹名称
	 * @return 标准的文件或文件夹名称
	 */
	public static String resolve(String name) {
		// Pattern pattern = Pattern.compile("[/\\\\*\\?\":<>|]*");
		boolean replace = false;
		char[] values = name.toCharArray();
		for (int index = 0; index < values.length; index++) {
			char c = values[index];
			if (c == '/' || c == '\\' || c == '*' || c == '?' || c == '"' || c == ':' || c == '|' || c == '<'
					|| c == '>') {
				replace = true;
				values[index] = '~';
			}
		}
		return replace ? new String(values) : name;
	}

	/**
	 * 创建多层级目录(如果不能存在则创建)
	 *
	 * @param names
	 *            可变参数,每一个参数(数组的每一个元素)表示每一层目录的名称
	 * @return 返回目录的File对象表示
	 */
	public static File mkdirs(String... names) {
		if (names.length == 1) {
			return mkdirs(new File(ROOT_PATH, names[0]));
		} else {
			StringBuilder builder = new StringBuilder();
			for (String name : names) {
				builder.append(name).append(File.separatorChar);
			}
			return mkdirs(new File(ROOT_PATH, builder.toString()));
		}
	}

	/**
	 * 创建path表示的目录
	 *
	 * @param path
	 *            需要创建的目录
	 * @return path所表示的目录
	 */
	public static File mkdirs(File path) {
		if (path.isFile()) {
			if (path.delete()) {
				path.mkdirs();
			}
		} else if (!path.exists()) {
			path.mkdirs();
		}
		return path;
	}

	/**
	 * 将字符串序列转换为物理路径,并返回这个文件的File对象表示
	 *
	 * @param name
	 *            文件名
	 * @param format
	 *            文件格式,允许为null;若为null,则完全依赖于name决定文件格式(若name中有包含文件格式名)
	 * @param parents
	 *            这个文件所在父目录,它可以是多层级的,可变参数(数组)的每一个参数表示一层目录
	 * @return 这个由字符序列构成的文件路径 的File对象表示
	 */
	public static File toFile(String name, String format, String... parents) {
		StringBuilder builder = new StringBuilder();
		File path;
		if (parents.length == 1) {
			path = new File(ROOT_PATH, parents[0]);
		} else {
			for (String dir : parents) {
				builder.append(dir).append(File.separatorChar);
			}
			builder.delete(builder.length() - 1, builder.length());
			path = new File(ROOT_PATH, builder.toString());
			builder.delete(0, builder.length());
		}
		if (format != null) {
			name = builder.append(name).append('.').append(format).toString();
		}
		return new File(mkdirs(path), name);
	}

	/**
	 * 根据文件file对象获得文件格式名称
	 *
	 * @param fileName
	 *            文件名
	 * @return 返回文件和格式名的字符串表示
	 */
	public static String getFileFormat(String fileName) {
		int index = fileName.lastIndexOf('.');
		return index == -1 ? null : fileName.substring(index + 1);
	}

	/**
	 * 根据File对象获得文件名称,不包含文件格式
	 *
	 * @param file
	 *            文件对象
	 * @return 文件标题
	 */
	public static String getFileTitle(File file) {
		String fileName = file.getName();
		return file.isDirectory() ? fileName : getFileTitle(fileName);
	}

	/**
	 * 根据File对象获得文件名称,不包含文件格式
	 *
	 * @param fileName
	 *            文件名称
	 * @return 文件标题名
	 */
	public static String getFileTitle(String fileName) {
		int index = fileName.lastIndexOf('.');
		return index == -1 ? fileName : fileName.substring(0, index);
	}

	/**
	 * 文件大小格式化
	 *
	 * @param scale
	 *            精度
	 * @param size
	 *            文件字节大小
	 * @return 返回格式化后的文件大小的字符串表示
	 */
	// 注意这里的除法运算必须有一个是浮点数,否则计算精度相差较大(这里将size参数用double类型接收)
	public static String toFileSize(int scale, double size) {
		int B = 1024;
		StringBuilder builder = new StringBuilder();
		if (size < B) {
			builder.append(size);
			stringHandle(builder, scale);
			builder.append("B");
			return builder.toString();
		}
		int KB = 1048576;
		if (size < KB) {
			builder.append(size / B);
			stringHandle(builder, scale);
			builder.append("KB");
			return builder.toString();
		}
		int MB = 1073741824;
		if (size < MB) {
			builder.append(size / KB);
			stringHandle(builder, scale);
			builder.append("MB");
			return builder.toString();
		}
		// long GB = 1099511627776L;
		if (size < 1099511627776L) {
			builder.append(size / MB);
			stringHandle(builder, scale);
			builder.append("GB");
			return builder.toString();
		}
		return "";
	}

	/**
	 * 将文件大小的字符串转换为long类型的字节表示<br>
	 * 字符串可以是"5.8MB"、"0.8MB"、"800.2KB"、".9MB"、"50KB"等, 但必须符合前面是数字类型后面是单位
	 * 
	 * @param size
	 *            文件大小，
	 * @return long类型所表示的字节大小
	 */
	public static long toLength(String size) {
		char[] array = size.toCharArray();
		int covert;
		int end = array.length - 2;
		char c = end > 0 ? Character.toUpperCase(array[end]) : 'B';
		// 避免直接跳到倒数第2个元素时是数字的情况
		if (c >= '0' && c <= '9') {
			c = 'B';
			++end;
		}
		switch (c) {
		case 'K':
			covert = 1024;
			break;
		case 'M':
			covert = 1048576;
			break;
		case 'G':
			covert = 1073741824;
			break;
		default:
			covert = 1;
			break;
		}
		double result = 0;
		int middle = (middle = size.indexOf('.')) == -1 ? end : middle;
		for (int rate = 1, index = middle - 1; index > -1; index--, rate *= 10) {
			result += (array[index] - 48) * rate;
		}
		float rate = 0.1F;
		for (int index = middle + 1; index < end; index++, rate *= 0.1F) {
			result += (array[index] - 48) * rate;
		}
		return (long) (result * covert);
	}

	/**
	 * 处理字符串
	 *
	 * @param builder
	 *            字符串构建器
	 * @param scale
	 *            精度
	 */
	private static void stringHandle(StringBuilder builder, int scale) {
		int index = builder.indexOf(".");
		// 如果精度为0则直接删除小数点开始的所有字符
		if (scale == 0) {
			builder.delete(index, builder.length());
		}
		// 如果精度等于1则直接删除小数点后一位数字之后的所有字符
		else if (scale == 1) {
			builder.delete(index + 2, builder.length());
		}
		// 如果精度大于1
		else if (scale > 1) {
			// 如果精度大于原有精度则补位,以0填充
			if (builder.length() - index == 2) {
				scale--;
				for (int i = 0; i < scale; i++) {
					builder.append(0);
				}
			}
			// 删除精度之后的所有字符
			else {
				builder.delete(index + scale + 1, builder.length());
			}
		}
	}

	// FileUtil.class.getResource("") 类所在目录
	// FileUtil.class.getResource("/") bin目录下
	// FileUtil.class.getResource("..") 类的所在目录的父目录

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
		try {
			return FileUtil.class.getResource(builder.toString()).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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

	/**
	 * 通过系统文件资源管理器打开文件所在路径
	 *
	 * @param path
	 *            文件对象
	 */
	public static void openFileBySystemExplorer(File path) {
		openFileBySystemExplorer(path.getPath());
	}

	/**
	 * 通过系统文件资源管理器打开文件所在路径
	 *
	 * @param path
	 *            文件路径
	 */
	public static void openFileBySystemExplorer(String path) {
		try {
			Runtime.getRuntime().exec("explorer /select,\"" + path + '"');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}