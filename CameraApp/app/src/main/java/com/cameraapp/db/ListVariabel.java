package com.cameraapp.db;

public class ListVariabel {

    public String NAMEDB = "CAMERAOCR";
    public String NAMETBL = "LIST_OF_FILE";
    public String COL_ID = "ID";
    public String COL_PATH = "NAME_FILE";
    public String COL_SIZE = "SIZE_FILE";
    public String COL_TYPE = "TYPEOF";
    public String COL_CREATEAT = "CREATEAT";
    public String CREATE_NAMETBL =
            "CREATE TABLE "+NAMETBL+" (" +
            COL_ID+" INTEGER AUTO_INCREMENT PRIMARY KEY, "+
            COL_PATH+" TEXT, "+
            COL_SIZE+" INTEGER, "+ 
            COL_TYPE+" TEXT, "+
            COL_CREATEAT+" TIMESTAMP)";
}
