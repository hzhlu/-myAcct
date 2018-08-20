package com.macrolab.myAcct.model;

import java.io.File;

public class DBFile {

    /**
     * 资料库名称
     */
    String name;

    File dbFile;

    public DBFile(File file) {
        dbFile = file;
        name = file.getName().split("\\.")[0];
    }

    public String getName() {
        return name;
    }

    public File getDbFile() {
        return dbFile;
    }

    @Override
    public String toString() {
        return name;
    }
}
