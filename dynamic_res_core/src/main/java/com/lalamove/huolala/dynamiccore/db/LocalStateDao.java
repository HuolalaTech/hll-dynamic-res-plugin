package com.lalamove.huolala.dynamiccore.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.lalamove.huolala.dynamicbase.util.CloseUtil;
import com.lalamove.huolala.dynamiccore.bean.LocalResStateInfo;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: StateDao
 * @author: huangyuchen
 * @date: 3/16/22
 * @description:
 * @history:
 */
public class LocalStateDao {

    public LocalStateDao(Context c) {
        mDbHelper = DynamicResDbHelper.getInstance(c);
    }

    private final DynamicResDbHelper mDbHelper;

    public void replace(LocalResStateInfo info) {
        if (info == null) {
            return;
        }
        synchronized (mDbHelper) {
            SQLiteDatabase db = null;
            try {
                db = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DynamicResDbHelper.STATE_TABEL.KEY, info.getKey());
                values.put(DynamicResDbHelper.STATE_TABEL.STATE, info.getState());
                values.put(DynamicResDbHelper.STATE_TABEL.STATE_PATH, info.getStatePath());
                values.put(DynamicResDbHelper.STATE_TABEL.EXTRA, info.getExtra());
                db.replace(DynamicResDbHelper.STATE_TABLE_NAME, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CloseUtil.close(db);
            }
        }
    }

    public LocalResStateInfo findByKey(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        synchronized (mDbHelper) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = mDbHelper.getReadableDatabase();
                cursor = db.query(DynamicResDbHelper.STATE_TABLE_NAME, null,
                        DynamicResDbHelper.STATE_TABEL.KEY + " = ?", new String[]{key}
                        , null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String resKey = cursor.getString(cursor.getColumnIndex(DynamicResDbHelper.STATE_TABEL.KEY));
                    int state = cursor.getInt(cursor.getColumnIndex(DynamicResDbHelper.STATE_TABEL.STATE));
                    String statePath = cursor.getString(cursor.getColumnIndex(DynamicResDbHelper.STATE_TABEL.STATE_PATH));
                    String extra = cursor.getString(cursor.getColumnIndex(DynamicResDbHelper.STATE_TABEL.EXTRA));
                    LocalResStateInfo info = new LocalResStateInfo(resKey, state, statePath, extra);
                    return info;
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
                db.delete(DynamicResDbHelper.STATE_TABLE_NAME, DynamicResDbHelper.STATE_TABEL.KEY + " = ? ", new String[]{key});
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CloseUtil.close(db);
            }
        }
    }
}
