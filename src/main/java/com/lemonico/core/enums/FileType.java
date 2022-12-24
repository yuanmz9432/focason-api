package com.lemonico.core.enums;

/**
 * ファイルタイプ定義
 */
public enum FileType
{

    CSV(1, "csv"), PDF(2, "pdf"), CREDENTIAL(3, "credential"), IMAGE(4, "image"), UNKNOWN(5, "unknown");

    private final int value;
    private final String name;

    FileType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
