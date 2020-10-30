package com.scmq.player.service;

import com.scmq.player.io.IOUtil;
import com.scmq.player.model.Special;
import com.scmq.player.net.HttpClient;
import com.scmq.player.util.FileUtil;
import com.scmq.player.util.Resource;
import com.scmq.player.util.StringUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class SpecialService {
	public void save(List<Special> specials) {
	}

	/**
	 * 批量保存歌单图片到本地
	 *
	 * @param list
	 *            歌单信息列表
	 */
	public void handlePictures(List<Special> list) {
		HttpClient client = HttpClient.createClient(null).removeAcceptHeader();
		// 默认歌单图片地址(程序内部)
		String uri = null;
		for (Special special : list) {
			if (StringUtil.isEmpty(special.getCover())) {
				continue;
			}
			String platform = special.getPlatform(), cover = special.getCover();
			File file = FileUtil.toFile(special.getMid(), "jpg", "picture/special", platform);
			// 若歌单图片文件存在
			if (file.isFile()) {
				// 设置歌单图片地址
				special.setCover(file.toURI().toString());
				continue;
			}
			// 若没有歌单图片地址
			if (StringUtil.isEmpty(cover)) {
				special.setCover(uri = uri == null ? Resource.getImageURI("_special") : uri);
				continue;
			}
			// 是否写入到本地文件
			boolean write = IOUtil.write(client.get(cover).openStream(), file);
			// 重置client
			client.reset();
			// 若没保存成功删除文件
			if (write) {
				special.setCover(file.toURI().toString());
			} else {
				special.setCover(uri = uri == null ? Resource.getImageURI("_special") : uri);
				file.delete();
			}
		}
		client.close();
	}
}
