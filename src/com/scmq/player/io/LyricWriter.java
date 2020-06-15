package com.scmq.player.io;

import com.scmq.player.model.LyricLine;
import com.scmq.player.util.StringUtil;
import com.scmq.player.util.TimeUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class LyricWriter {
	/** 歌词文件 */
	private File file;

	/**
	 * 构造一个歌词写入器
	 *
	 * @param file
	 *            文件对象
	 */
	public LyricWriter(File file) {
		this.file = file;
	}

	/**
	 * 将歌词文本写入到本地文件,这个文本包含了所有歌词数据信息,每行歌词有换行符
	 *
	 * @param text
	 *            歌词文本
	 */
	public void write(String text) {
		BufferedWriter writer = null;
		try {
			FileOutputStream stream = new FileOutputStream(file);
			writer = new BufferedWriter(new OutputStreamWriter(stream, StringUtil.GBK));
			writer.write(text);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtil.close(writer);
		}
	}

	/**
	 * 写入歌词内容到文件中
	 *
	 * @param lines
	 *            歌词行列表集合
	 */
	public void write(List<LyricLine> lines) {
		StringBuilder builder = new StringBuilder();
		for (LyricLine line : lines) {
			long millis = line.getMillis();
			builder.append('[').append(TimeUtil.millisToTime(millis)).append('.');
			millis %= 1000;
			// 2009 % 1000 = 9 -> 009 -> 00
			// 2099 % 1000 = 99 -> 099 -> 09
			// 2999 % 1000 = 999 -> 999 -> 99
			if (millis < 10) {
				builder.append('0').append('0');
			} else if (millis < 100) {
				builder.append('0').append(millis);
				builder.delete(builder.length() - 1, builder.length());
			} else {
				builder.append(millis);
				builder.delete(builder.length() - 1, builder.length());
			}
			builder.append(']').append(line.getContent()).append('\n');
		}
		// 删除最后一个换行符,然后写入到本地文件
		write(builder.delete(builder.length() - 1, builder.length()).toString());
	}
}
