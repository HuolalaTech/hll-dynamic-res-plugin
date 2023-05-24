package com.lalamove.huolala.dynamiccore.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.lalamove.huolala.dynamicbase.util.CloseUtil;
import com.lalamove.huolala.dynamiccore.bean.LocalResInfo;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: ResDao
 * @author: huangyuchen
 * @date: 3/16/22
 * @description:
 * @history:
 */
public class LocalResDao {

    public LocalResDao(Context c) {
        mDbHelper = DynamicResDbHelper.getInstance(c);
    }

    private final DynamicResDbHelper mDbHelper;

    /**
     * 插入或替换资源信息
     *
     * @param info
     */
    public void replace(LocalResInfo info) {
        if (info == null) {
            return;
        }
        synchronized (mDbHelper) {
            SQLiteDatabase db = null;
            try {
                db = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DynamicResDbHelper.RES_TABLE.KEY, info.getKey());
                values.put(DynamicResDbHelper.RES_TABLE.VERSION, info.getVersion());
                values.put(DynamicResDbHelper.RES_TABLE.VERIFY_TYPE, info.getVerifyType());
                values.put(DynamicResDbHelper.RES_TABLE.EXTRA, info.getExtra());
                values.put(DynamicResDbHelper.RES_TABLE.PATH, info.getPath());
                db.replace(DynamicResDbHelper.RES_TABLE_NAME, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CloseUtil.close(db);
            }
        }
    }

    /**
     * 根据key，查找资源信息
     *
     * @param key
     * @return
     */
    public LocalResInfo findByKey(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        synchronized (mDbHelper) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = mDbHelper.getReadableDatabase();
                cursor = db.query(DynamicResDbHelper.RES_TABLE_NAME, null,
                        DynamicResDbHelper.RES_TABLE.KEY + " = ?", new String[]{key}
                        , null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String resKey = cursor.getString(cursor.getColumnIndex(DynamicResDbHelper.RES_TABLE.KEY));
                    int version = cursor.getInt(cursor.getColumnIndex(DynamicResDbHelper.RES_TABLE.VERSION));
                    String path = cursor.getString(cursor.getColumnIndex(DynamicResDbHelper.RES_TABLE.PATH));
                    int verifyType = cursor.getInt(cursor.getColumnIndex(DynamicResDbHelper.RES_TABLE.VERIFY_TYPE));
                    String extra = cursor.getString(cursor.getColumnIndex(DynamicResDbHelper.RES_TABLE.EXTRA));
                    return new LocalResInfo(resKey, version, path, verifyType, extra);
                }
                return null;
            } catch (Exception e) {
                return null;
            } finally {
                CloseUtil.close(cursor, db);
            }
        }
    }

    public void delete(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SQLiteDatabase db = null;
        synchronized (mDbHelper) {
            try {
                db = mDbHelper.getWritableDatabase();
                db.delete(DynamicResDbHelper.RES_TABLE_NAME, DynamicResDbHelper.STATE_TABEL.KEY + " = ? ", new String[]{key});
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CloseUtil.close(db);
            }
        }
    }
}
