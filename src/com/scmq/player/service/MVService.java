package com.scmq.player.service;

import com.scmq.player.io.IOUtil;
import com.scmq.player.model.MV;
import com.scmq.player.net.HttpClient;
import com.scmq.player.util.FileUtil;
import com.scmq.player.util.StringUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class MVService {
    public void save(List<MV> mvList) {
    }

    /**
     * 批量保存MV封面图片到本地
     *
     * @param list MV信息列表
     */
    public void handlePictures(List<MV> list) {
        HttpClient client = HttpClient.createClient(null).removeAcceptHeader();
        // 默认MV图片地址(程序内部)
        String uri = null;
        for (MV mv : list) {
            String platform = mv.getPlatform(), cover = mv.getCover();
            File file = FileUtil.toFile(mv.getVid(), "jpg", "picture/mv", platform);
            // 若MV图片文件存在
            if (file.isFile()) {
                // 设置MV图片地址
                mv.setCover(file.toURI().toString());
                continue;
            }
            // 若没有MV图片地址
            if (StringUtil.isEmpty(cover)) {
                mv.setCover(uri = uri == null ? FileUtil.getImageURI("_mv") : uri);
                continue;
            }
            // 是否写入到本地文件
            boolean write = IOUtil.write(client.get(cover).openStream(), file);
            // 重置client
            client.reset();
            // 若没保存成功删除文件
            if (write) {
                mv.setCover(file.toURI().toString());
            } else {
                mv.setCover(uri = uri == null ? FileUtil.getImageURI("_mv") : uri);
                file.delete();
            }
        }
        client.close();
    }
}
