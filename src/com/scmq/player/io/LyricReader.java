package com.scmq.player.io;

import com.scmq.player.model.LyricLine;
import com.scmq.player.util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 歌词读取器
 * 
 * @author SCMQ
 *
 */
public class LyricReader {
	/** 歌词行列表集合 */
	private List<LyricLine> lines = new ArrayList<>();
	/** 歌词时间解析正则表达式对象 */
	private static final Pattern PATTERN = Pattern.compile("\\[(\\d{1,2}:\\d{1,2}.\\d{1,2})\\]");

	public LyricReader() {
	}

	/**
	 * 从本地歌词文件读取歌词内容,默认使用GBK编码解码字符串。
	 * 
	 * @param file
	 *            歌词文件
	 * @return 歌词行列表集合
	 */
	public List<LyricLine> read(File file) {
		return read(file, StringUtil.GBK);
	}

	/**
	 * 从本地歌词文件读取歌词内容
	 * 
	 * @param file
	 *            歌词文件
	 * @param charset
	 *            字符编码
	 * @return 歌词行列表集合
	 */
	public List<LyricLine> read(File file, Charset charset) {
		try {
			FileInputStream stream = new FileInputStream(file);
			return read(new BufferedReader(new InputStreamReader(stream, charset)));
		} catch (FileNotFoundException e) {
			return lines;
		}
	}

	/**
	 * 通过字符缓冲输入流,按行读取歌词数据. 歌词数据读取完成后,会关闭输入流.
	 * 
	 * @param reader
	 *            字符缓冲输入流
	 * @return 歌词行List集合
	 */
	public List<LyricLine> read(BufferedReader reader) {
		ArrayList<String> list = new ArrayList<>();
		try {
			for (String line; (line = reader.readLine()) != null;) {
				handle(line, list);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtil.close(reader);
		}
		lines.sort(null);
		return lines;
	}

	/**
	 * 从歌词文本中读取歌词内容,这个文本包含了所有歌词数据信息,每行歌词有换行符
	 * 
	 * @param text
	 *            歌词文本
	 * @return 歌词行列表集合
	 */
	public List<LyricLine> read(String text) {
		String[] lyrics = text.split("\r\n|\n");// 按换行符分割歌词文本
		// 保存每一行歌词中包含的时间(如 “[00:50.80][01:20.90]ABCDEF” ->“00:50.80”,“01:20.90”)
		ArrayList<String> list = new ArrayList<>(3);
		for (String lyric : lyrics) {
			handle(lyric, list);
		}
		return lines;
	}

	private void handle(String line, List<String> list) {
		// 获得正则匹配器
		Matcher matcher = PATTERN.matcher(line);
		// 记录歌词时间与歌词内容分隔位置
		int end = -1;
		for (; matcher.find();) {
			// 添加时间(“00:09.20”)
			list.add(matcher.group(1));// 获得 "分:秒"
			// 改变记录位置
			end = matcher.end();
		}
		// 若歌词时间与内容分隔位置存在
		if (end != -1) {
			// 获得歌词内容
			String content = line.substring(end);// 获得歌词内容
			for (String time : list) {
				// 添加歌词行对象到List集合中
				lines.add(new LyricLine(time, content));
			}
		}
		list.clear();
	}
}
