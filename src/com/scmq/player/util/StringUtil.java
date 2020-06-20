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
	public static void clear(StringBuilder builder) {
		builder.delete(0, builder.length());
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
		return value < 10 ? builder.append('0').append(value).toString() : builder.append(value).toString();
	}

	public static int compare(String text1, String text2) {
		if (text1 == null) {
			return text2 == null ? 0 : -1;
		}
		if (text2 == null) {
			return -1;
		}
		return (int) (FileUtil.toFileLength(text1) - FileUtil.toFileLength(text2));
	}

	/**
	 * 获取字符序列对应的MD5字符序列
	 *
	 * @param sequence
	 *            需要转换的字符串
	 * @return text对应的MD5字符序列
	 */
	public static String md5(String sequence) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] bytes = md5.digest(sequence.getBytes(UTF_8));
			char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
			StringBuilder builder = new StringBuilder(bytes.length << 1);
			for (byte aByte : bytes) {
				builder.append(hexDigits[(aByte >> 4) & 0xF]);
				builder.append(hexDigits[aByte & 0xF]);
			}
			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			return sequence;
		}
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
		float count = 0;
		for (char c : ch) {
			if (!Character.isLetterOrDigit(c) && !isChinese(c)) {
				count++;
			}
		}
		return count / (float) ch.length > 0.4;
	}
}
