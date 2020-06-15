package com.scmq.player.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scmq.player.dao.PlayListDao;
import com.scmq.player.model.Media.AudioType;
import com.scmq.player.model.Music;
import com.scmq.player.model.PlayList;
import com.scmq.player.util.TimeUtil;

@Service("playListService")
public class PlayListService {
    @Autowired private PlayListDao playListDao;

    /**
     * 查询播放列表(数据库表中最后一个)
     *
     * @return 播放列表(PlayList)对象
     */
    public PlayList findPlayListLast() {
        // 查询表中最后一个id
        Integer id = playListDao.findPlayListIdOfLast();
        // 获取这个id对应的媒体
        return id == null ? null : playListDao.findPlayListById(id, AudioType.values());
    }

    /**
     * 保存播放列表到数据库,该方法开启一个事务,只有播放列表媒体项数据保存成功才算成功 <br>
     * 调用此方法前,必须确保每个Audio对象都有id值
     *
     * @param playList 播放列表对象
     * @return 保存成功的数量
     */
    @Transactional
    public boolean savePlayList(PlayList playList) {
        // 如果播放列表对象是null,则返回false
        if (playList == null) {
            return false;
        }
        List<Music> list = playList.getList();
        // 如果播放列表对象中没有任何音乐信息,则返回false
        if (list == null || list.isEmpty()) {
            return false;
        }
        // 如果需要新建播放列表
        if (playList.getId() == null) {
            // 查询最新的一个播放列表,以获得播放列表id
            Integer id = playListDao.findPlayListIdOfLast();
            // 若没有播放列表,则标记为需要新建播放列表
            if (id == null) {
                // 设置创建时间
                playList.setCreateTime(TimeUtil.currentTime());
                // 保存一个音乐列表,以获得播放列表id(SQLite支持单行数据添加返回主键)
                int row = playListDao.savePlayList(playList);
                // 如果没有保存成功,则返回false
                if (row != 1) {
                    return false;
                }
            } else {
                // 设置id到播放列表对象中
                playList.setId(id);
            }
        }
        return playListDao.savePlayMediaItem(playList.getId(), list) == list.size();
    }

    /**
     * 批量删除播放列表媒体项;如果传入的id数量为0,则会执行全部删除
     *
     * @param mediaIds 播放列表项媒体id(可变参数),如果没有则表示全部删除
     * @return 删除成功的数量
     */
    @Transactional
    public boolean deletePlayMediaItem(Integer... mediaIds) {
        // ****还需要处理,因为只是从local_media_item表删除了记录,如果其他表没有引用这些记录,则删除
        int row = playListDao.deletePlayMediaItem(mediaIds);
        return mediaIds.length == 0 ? row > 0 : row == mediaIds.length;
    }
}
