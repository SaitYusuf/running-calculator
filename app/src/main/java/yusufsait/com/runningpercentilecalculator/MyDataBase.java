package yusufsait.com.runningpercentilecalculator;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class MyDataBase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "running.db";
    private static final int DATABASE_VERSION = 1;

    public MyDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }
}