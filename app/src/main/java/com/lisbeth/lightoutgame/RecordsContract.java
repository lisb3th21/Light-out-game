package com.lisbeth.lightoutgame;

import android.provider.BaseColumns;

public class RecordsContract {

    public static abstract  class RecordEntry implements BaseColumns{
        public static final String  TABLE_NAME = "Records";
        public static final String ID = "id";
        public static final String DATE = "date";
        public static final String SECONDS  = "seconds";
    }
}
