package cn.acewill.mobile.pos.factory;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Acewill on 2016/5/24.
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION, foreignKeysSupported = true)
public class AppDatabase {
    public static final String NAME = "App";

    public static final int VERSION = 1;
}
