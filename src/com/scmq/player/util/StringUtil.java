package com.scmq.player.util;

import java.lang.Character.UnicodeBlock;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {
	/** GBK编码 */
	public static final Charset GBK = Charset.forName("GBK");
	/** UTF8编码 */
	public static final Charset UTF_8 = Charset.forName("UTF8");

	/**
	 * 清空StringBuilder内部字符序列
	 *
	 * @param builder
	 *            字符串构建器
	 */
	public static StringBuilder clear(StringBuilder builder) {
		return builder.delete(0, builder.length());
	}

	/**
	 * 判断字符序列是否为空
	 *
	 * @param sequence
	 *            字符序列
	 * @return 若字符序列对象为null或是空字符序列则返回true, 否则返回false
	 */
	public static boolean isEmpty(CharSequence sequence) {
		return sequence == null || sequence.length() == 0;
	}

	/**
	 * 判断字符序列是否不为空
	 *
	 * @param sequence
	 *            字符序列
	 * @return 若字符序列对象不为null且不是空字符序列则返回true, 否则返回false
	 */
	public static boolean isNotEmpty(CharSequence sequence) {
		return sequence != null && sequence.length() != 0;
	}

	/**
	 * 填充字符序列.若value小于10,则在其前面补0.
	 * 
	 * @param builder
	 *            字符序列构建器
	 * @param value
	 *            数值
	 * @return 填充后的字符序列
	 */
	public static String fillString(StringBuilder builder, int value) {
		return (value < 10 ? builder.append('0') : builder).append(value).toString();
	}

	/**
	 * 保留一个浮点数的指定有效数字位.
	 *
	 * @param value
	 *            需要计算的浮点数
	 * @param scale
	 *            保留有效数字个数(精度)
	 * @return 指定有效数值为的数值字符串
	 */
	public static String retainDigits(double value, int scale) {
		return retainDigits(value, scale, false);
	}

	/**
	 * 保留一个浮点数的指定有效数字位. 但是若指定去除有效数字内的0,那么精度不会是指定的.
	 *
	 * @param value
	 *            需要计算的浮点数
	 * @param scale
	 *            保留有效数字个数(精度)
	 * @param trim
	 *            是否去除有效数字部分的0
	 * @return 指定有效数值为的数值字符串
	 */
	public static String retainDigits(double value, int scale, boolean trim) {
		StringBuilder builder = new StringBuilder().append(value);
		int point = builder.indexOf(".");

		// 若精度小于1直接删除删除小数点开始的所有字符
		if (scale <= 0) {
			return builder.delete(point, builder.length()).toString();
		}

		// 精度值加1 , 同时point增加精度值
		point += ++scale;

		// 若精度大于已有的有效位数 且 不需要去除0, 那么补0.
		if (!trim && point > builder.length()) {
			for (int index = point - builder.length(); index > 0; --index) {
				builder.append('0');
			}
		}

		// 若精度小于已有有效位数那么删除进度之后的所有数
		if (point < builder.length()) {
			builder.delete(point, builder.length());
		}

		// 若不需要去除有效位数的0
		if (!trim) {
			return builder.toString();
		}

		point -= scale;

		// 去除所有小数点后的0
		for (int index = builder.length() - 1; index >= point; --index) {
			char c = builder.charAt(index);
			// 若不是 “0” 和 “.” , 则结束
			if (c != '0' && c != '.') {
				break;
			}

			// 删除当前索引字符
			builder.delete(index, index + 1);
			// 若已经达到小数点位置, 则结束
			if (c == '.') {
				break;
			}
		}

		return builder.toString();
	}

	/** MD5算法实例对象 */
	private static MessageDigest md5;

	/** 获取MD5算法实例对象 */
	private static MessageDigest getMd5() {
		if (md5 != null) {
			return md5;
		}

		synchronized (String.class) {
			if (md5 != null) {
				return md5;
			}

			try {
				return md5 = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				// 通常这里不会执行
				throw new NullPointerException();
			}
		}
	}

	/**
	 * 获取字符序列对应的MD5字符序列
	 *
	 * @param sequence
	 *            需要转换的字符串
	 * @return text对应的MD5字符序列
	 */
	public static String md5(String sequence) {
		byte[] bytes = getMd5().digest(sequence.getBytes(UTF_8));
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		StringBuilder builder = new StringBuilder(bytes.length << 1);
		for (byte aByte : bytes) {
			builder.append(hexDigits[(aByte >> 4) & 0xF]);
			builder.append(hexDigits[aByte & 0xF]);
		}
		return builder.toString();
	}

	/**
	 * 将一个数值字符串转换为int纯数值类型
	 *
	 * @param text
	 *            数值字符串
	 * @return 数值字符串的int表示
	 */
	public static int parseInt(String text) {
		// Pattern pattern = Pattern.compile("^-?\\d+$");
		// return pattern.matcher(text).matches() ? Integer.parseInt(text) : -1;
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * 将一个数值字符串转换为float纯数值类型
	 *
	 * @param text
	 *            数值字符串
	 * @return 数值字符串的float表示
	 */
	public static float parseFloat(String text) {
		try {
			return Float.parseFloat(text);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	// /**
	// * 检查一个字符是否是中文字符 <br>
	// * 这里检查范围只包含基本汉字,更多可搜索“汉字Unicode编码范围”
	// *
	// * @param c 任意字符
	// * @return 若是中文字符, 则返回true, 否则返回false
	// */
	// public static boolean isChinese(char c) {
	// return c >= '\u4e00' && c <= '\u9fa5';
	// }

	/**
	 * 检查字符是否是中文字符<br>
	 * 代码来源 https://www.iteye.com/blog/minghe-zy-1242002
	 *
	 * @param c
	 *            任意字符
	 * @return 若是中文字符则返回true
	 */
	public static boolean isChinese(char c) {
		UnicodeBlock ub = UnicodeBlock.of(c);
		return ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS//
				|| ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == UnicodeBlock.GENERAL_PUNCTUATION//
				|| ub == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
	}

	/**
	 * 判断字符串是否是乱码 代码来源<br>
	 * https://www.iteye.com/blog/minghe-zy-1242002
	 *
	 * @param text
	 *            字符串
	 * @return 是否是乱码
	 */
	public static boolean isMessyCode(String text) {
		text = text.replaceAll("\\s*|t*|r*|n*", "");
		String temp = text.replaceAll("\\p{P}", "");
		char[] ch = temp.trim().toCharArray();
		int count = 0;
		for (char c : ch) {
			if (!Character.isLetterOrDigit(c) && !isChinese(c)) {
				count++;
			}
		}
		return count / (float) ch.length > 0.4F;
	}
}
