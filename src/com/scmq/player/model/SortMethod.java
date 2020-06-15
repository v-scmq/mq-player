package com.scmq.player.model;

public enum SortMethod {
    ARTIST(1), TITLE(2), ALBUM(3), DURATION(4), SIZE(5), FILE_NAME(6);

    private final int code;

    SortMethod(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SortMethod valueOf(int code) {
        switch (code) {
            case 1:
                return ARTIST;
            case 2:
                return TITLE;
            case 3:
                return ALBUM;
            case 4:
                return DURATION;
            case 5:
                return SIZE;
            default:
                return FILE_NAME;
        }
    }
}
