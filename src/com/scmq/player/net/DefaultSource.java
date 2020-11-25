package com.scmq.player.net;

import com.scmq.player.model.Album;
import com.scmq.player.model.LyricLine;
import com.scmq.player.model.MV;
import com.scmq.player.model.Music;
import com.scmq.player.model.Page;
import com.scmq.player.model.Rank;
import com.scmq.player.model.RankItem;
import com.scmq.player.model.Singer;
import com.scmq.player.model.Special;
import com.scmq.player.model.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供一个默认获取网络资源空实现类
 */
public class DefaultSource implements NetSource {
	private List<Tag> singerTags = new ArrayList<>(5);

	private List<Tag> singerEnTags = new ArrayList<>(28);

	private List<Tag> specialTags = new ArrayList<>(17);

	private List<Tag> mvTags = new ArrayList<>(4);

	public DefaultSource() {
		singerTags.add(new Tag("全部", "10"));
		singerTags.add(new Tag("华语男", "12"));
		singerTags.add(new Tag("华语女", "15"));
		singerTags.add(new Tag("华语组合", "18"));
		singerTags.add(new Tag("其他", "20"));

		specialTags.add(new Tag("经典", "0"));// 主题
		specialTags.add(new Tag("翻唱", "1"));
		specialTags.add(new Tag("怀旧", "2"));
		specialTags.add(new Tag("影视", "3"));
		specialTags.add(new Tag("二次元", "4"));
		specialTags.add(new Tag("3D", "5"));
		specialTags.add(new Tag("纯音乐", "6"));

		mvTags.add(new Tag("华语", "0"));
		mvTags.add(new Tag("剧情", "1"));
		mvTags.add(new Tag("热舞", "2"));
		mvTags.add(new Tag("网络", "3"));

		// 26个字母+热门+其他
		singerEnTags.add(new Tag("热门", ""));
		// A->65 ; a->97 ; 相差32
		for (int letter = 65; letter <= 90; letter++) {
			String name = String.valueOf((char) letter);// 大写字母
			singerEnTags.add(new Tag(name, name));
		}
		singerEnTags.add(new Tag("#", "%2523"));
	}

	@Override
	public List<Tag> singerKindTags() {
		return singerTags;
	}

	@Override
	public List<Tag> singerEnTags() {
		return singerEnTags;
	}

	@Override
	public List<Singer> singerList(Page page, Tag singerKind, Tag singerEn) {
		return new ArrayList<>();
	}

	@Override
	public List<Music> songList(Singer singer, Page page) {
		return new ArrayList<>();
	}

	@Override
	public List<Album> albumList(Singer singer, Page page) {
		return new ArrayList<>();
	}

	@Override
	public List<Music> songList(Album album, Page page) {
		return new ArrayList<>();
	}

	@Override
	public List<MV> mvList(Singer singer, Page page) {
		return new ArrayList<>();
	}

	@Override
	public boolean handleMVInfo(MV mv) {
		return false;
	}

	@Override
	public boolean handleSingerInfo(Singer singer) {
		return false;
	}

	@Override
	public List<Music> songSearch(String key, Page page) {
		return new ArrayList<>();
	}

	@Override
	public boolean handleMusicInfo(Music music) {
		return false;
	}

	@Override
	public List<Tag> specialTags() {
		return specialTags;
	}

	@Override
	public List<Special> specialList(Tag tag, Page page) {
		return new ArrayList<>();
	}

	@Override
	public List<Music> songList(Special special, Page page) {
		return new ArrayList<>();
	}

	@Override
	public List<Tag> mvTags() {
		return mvTags;
	}

	@Override
	public List<MV> mvList(Tag mvTag, Page page) {
		return new ArrayList<>();
	}

	@Override
	public List<Rank> rankList() {
		List<Rank> list = new ArrayList<>(2);

		List<RankItem> items = new ArrayList<>(2);
		items.add(new RankItem("热歌榜", "26"));
		items.add(new RankItem("新歌榜", "27"));
		list.add(new Rank("巅峰榜", items));

		items = new ArrayList<>(3);
		items.add(new RankItem("国风热歌榜", "65"));
		items.add(new RankItem("综艺新歌榜", "64"));
		items.add(new RankItem("影视金曲榜", "29"));
		list.add(new Rank("巅峰榜", items));

		return list;
	}

	@Override
	public List<Music> songList(RankItem item, Page page) {
		return new ArrayList<>();
	}

	@Override
	public List<Special> specialSearch(String key, Page page) {
		return new ArrayList<>();
	}

	@Override
	public List<MV> mvSearch(String key, Page page) {
		return new ArrayList<>();
	}

	@Override
	public List<Album> albumSearch(String key, Page page) {
		return new ArrayList<>();
	}

	@Override
	public List<Singer> singerSearch(String keyword) {
		return new ArrayList<>();
	}

	@Override
	public List<LyricLine> handleLyric(Music music) {
		return null;
	}

	@Override
	public List<String> hotKeys() {
		return null;
	}

	@Override
	public List<String> singerImageList(Music music) {
		return new ArrayList<>(0);
	}

	@Override
	public String platformId() {
		return "not-implement";
	}
}
