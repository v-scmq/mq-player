package com.scmq.player.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
	/**
	 * 将字节数组中的内容写入到指定文件中.这适用于数据小的写入
	 *
	 * @param buff
	 *            字节缓冲数组
	 * @param dest
	 *            目标文件
	 * @return 若写入成功则返回true, 否则返回false
	 */
	public static boolean write(byte[] buff, File dest) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dest);
			out.write(buff);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			close(out);
		}
	}

	public static boolean write(InputStream stream, File dest) {
		if (stream == null) {
			return false;
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dest);
			byte[] buff = new byte[1024 * 1024];
			int length;
			for (; (length = stream.read(buff)) != -1;) {
				out.write(buff, 0, length);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			// 不论前面是否有return语句,这里始终得以执行
			close(stream);
			close(out);
		}
	}

	/**
	 * 关闭IO流
	 *
	 * @param closeable
	 *            所有实现了{@link java.io.Closeable}接口的类的对象
	 */
	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
