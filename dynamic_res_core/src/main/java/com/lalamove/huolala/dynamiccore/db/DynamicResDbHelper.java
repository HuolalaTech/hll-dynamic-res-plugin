package com.lalamove.huolala.dynamiccore.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicResDbHelper
 * @author: huangyuchen
 * @date: 3/16/22
 * @description:
 * @history:
 */
public class DynamicResDbHelper extends SQLiteOpenHelper {

    private static DynamicResDbHelper sHelper;

    private static final String DB_NAME = "dynamic_res.db";
    private static final int DB_VERSION = 1;

    /**
     * 资源包信息表名称
     */
    public static final String RES_TABLE_NAME = "res_info";
    /**
     * 资源状态表名称
     */
    public static final String STATE_TABLE_NAME = "state_info";

    /**
     * 资源包信息数据库表
     */
    public interface RES_TABLE {
        String KEY = "res_key";
        String VERSION = "version";
        String PATH = "path";
        String VERIFY_TYPE = "verify_type";
        String EXTRA = "extra";
    }

    /**
     * 资源包状态数据库表
     */
    public interface STATE_TABEL {
        String KEY = "res_key";
        String STATE = "state";
        String STATE_PATH = "state_path";
        String EXTRA = "extra";
    }

    public synchronized static DynamicResDbHelper getInstance(Context c) {
        if (sHelper == null) {
            sHelper = new DynamicResDbHelper(c);
        }
        return sHelper;
    }

    private DynamicResDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + RES_TABLE_NAME + "("
                + RES_TABLE.KEY + " TEXT PRIMARY KEY ,"
                + RES_TABLE.VERSION + " INTEGER , "
                + RES_TABLE.PATH + " TEXT , "
                + RES_TABLE.VERIFY_TYPE + " INTEGER , "
                + RES_TABLE.EXTRA + " TEXT ) ");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + STATE_TABLE_NAME + "("
                + STATE_TABEL.KEY + " TEXT PRIMARY KEY ,"
                + STATE_TABEL.STATE + " INTEGER , "
                + STATE_TABEL.STATE_PATH + " TEXT , "
                + STATE_TABEL.EXTRA + " TEXT ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STATE_TABLE_NAME);
        onCreate(db);
    }
}
