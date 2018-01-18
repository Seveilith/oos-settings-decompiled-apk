package com.android.settings.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;
import java.util.List;
import java.util.Map;

public class IndexDatabaseHelper
  extends SQLiteOpenHelper
{
  private static final String CREATE_INDEX_TABLE = "CREATE VIRTUAL TABLE prefs_index USING fts4(locale, data_rank, data_title, data_title_normalized, data_summary_on, data_summary_on_normalized, data_summary_off, data_summary_off_normalized, data_entries, data_keywords, screen_title, class_name, icon, intent_action, intent_target_package, intent_target_class, enabled, data_key_reference, user_id);";
  private static final String CREATE_META_TABLE = "CREATE TABLE meta_index(build VARCHAR(32) NOT NULL, build_id VARCHAR(64) NOT NULL)";
  private static final String CREATE_SAVED_QUERIES_TABLE = "CREATE TABLE saved_queries(query VARCHAR(64) NOT NULL, timestamp INTEGER)";
  private static final String DATABASE_NAME = "search_index.db";
  private static final int DATABASE_VERSION = 116;
  private static final String INDEX = "index";
  private static final String INSERT_BUILD_VERSION = "INSERT INTO meta_index VALUES ('" + Build.VERSION.INCREMENTAL + "', '" + Build.DISPLAY + "');";
  private static final String SELECT_BUILD_ID = "SELECT build_id FROM meta_index LIMIT 1;";
  private static final String SELECT_BUILD_VERSION = "SELECT build FROM meta_index LIMIT 1;";
  private static final String TAG = "IndexDatabaseHelper";
  private static final String VERSION_CODE = "versions";
  private static IndexDatabaseHelper sSingleton;
  private final Context mContext;
  
  public IndexDatabaseHelper(Context paramContext)
  {
    super(paramContext, "search_index.db", null, 116);
    this.mContext = paramContext;
  }
  
  private void bootstrapDB(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE VIRTUAL TABLE prefs_index USING fts4(locale, data_rank, data_title, data_title_normalized, data_summary_on, data_summary_on_normalized, data_summary_off, data_summary_off_normalized, data_entries, data_keywords, screen_title, class_name, icon, intent_action, intent_target_package, intent_target_class, enabled, data_key_reference, user_id);");
    paramSQLiteDatabase.execSQL("CREATE TABLE meta_index(build VARCHAR(32) NOT NULL, build_id VARCHAR(64) NOT NULL)");
    paramSQLiteDatabase.execSQL("CREATE TABLE saved_queries(query VARCHAR(64) NOT NULL, timestamp INTEGER)");
    paramSQLiteDatabase.execSQL(INSERT_BUILD_VERSION);
    Log.i("IndexDatabaseHelper", "Bootstrapped database");
  }
  
  public static void clearAppVersionCode(Context paramContext)
  {
    paramContext.getSharedPreferences("versions", 0).edit().clear().commit();
  }
  
  public static void clearLocalesIndexed(Context paramContext)
  {
    paramContext.getSharedPreferences("index", 0).edit().clear().commit();
  }
  
  private void dropTables(SQLiteDatabase paramSQLiteDatabase)
  {
    clearLocalesIndexed(this.mContext);
    clearAppVersionCode(this.mContext);
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS meta_index");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS prefs_index");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS saved_queries");
  }
  
  private String getBuildId(SQLiteDatabase paramSQLiteDatabase)
  {
    Object localObject4 = null;
    Object localObject5 = null;
    localObject3 = null;
    localObject1 = null;
    localObject2 = localObject4;
    try
    {
      Cursor localCursor = paramSQLiteDatabase.rawQuery("SELECT build_id FROM meta_index LIMIT 1;", null);
      paramSQLiteDatabase = (SQLiteDatabase)localObject5;
      localObject1 = localCursor;
      localObject2 = localObject4;
      localObject3 = localCursor;
      if (localCursor.moveToFirst())
      {
        localObject1 = localCursor;
        localObject2 = localObject4;
        localObject3 = localCursor;
        paramSQLiteDatabase = localCursor.getString(0);
      }
      localObject1 = localCursor;
      localObject2 = paramSQLiteDatabase;
      localObject3 = localCursor;
      Log.d("IndexDatabaseHelper", "getBuildId:" + paramSQLiteDatabase);
      localObject3 = paramSQLiteDatabase;
      if (localCursor != null)
      {
        localCursor.close();
        localObject3 = paramSQLiteDatabase;
      }
    }
    catch (Exception paramSQLiteDatabase)
    {
      localObject3 = localObject1;
      Log.e("IndexDatabaseHelper", "Cannot get build Id from Index metadata");
      localObject3 = localObject2;
      return (String)localObject2;
    }
    finally
    {
      if (localObject3 == null) {
        break label160;
      }
      ((Cursor)localObject3).close();
    }
    return (String)localObject3;
  }
  
  private String getBuildVersion(SQLiteDatabase paramSQLiteDatabase)
  {
    Object localObject4 = null;
    Object localObject5 = null;
    localObject3 = null;
    localObject1 = null;
    localObject2 = localObject4;
    try
    {
      Cursor localCursor = paramSQLiteDatabase.rawQuery("SELECT build FROM meta_index LIMIT 1;", null);
      paramSQLiteDatabase = (SQLiteDatabase)localObject5;
      localObject1 = localCursor;
      localObject2 = localObject4;
      localObject3 = localCursor;
      if (localCursor.moveToFirst())
      {
        localObject1 = localCursor;
        localObject2 = localObject4;
        localObject3 = localCursor;
        paramSQLiteDatabase = localCursor.getString(0);
      }
      localObject1 = localCursor;
      localObject2 = paramSQLiteDatabase;
      localObject3 = localCursor;
      Log.d("IndexDatabaseHelper", "getBuildVersion:" + paramSQLiteDatabase);
      localObject3 = paramSQLiteDatabase;
      if (localCursor != null)
      {
        localCursor.close();
        localObject3 = paramSQLiteDatabase;
      }
    }
    catch (Exception paramSQLiteDatabase)
    {
      localObject3 = localObject1;
      Log.e("IndexDatabaseHelper", "Cannot get build version from Index metadata");
      localObject3 = localObject2;
      return (String)localObject2;
    }
    finally
    {
      if (localObject3 == null) {
        break label160;
      }
      ((Cursor)localObject3).close();
    }
    return (String)localObject3;
  }
  
  public static IndexDatabaseHelper getInstance(Context paramContext)
  {
    try
    {
      if (sSingleton == null) {
        sSingleton = new IndexDatabaseHelper(paramContext);
      }
      paramContext = sSingleton;
      return paramContext;
    }
    finally {}
  }
  
  public static boolean isLocaleAlreadyIndexed(Context paramContext, String paramString)
  {
    return paramContext.getSharedPreferences("index", 0).getBoolean(paramString, false);
  }
  
  public static boolean isPackageNamesMatched(Context paramContext, List<String> paramList)
  {
    paramContext = paramContext.getSharedPreferences("versions", 0).getAll();
    if (paramContext.size() != paramList.size()) {
      return false;
    }
    int i = 0;
    while (i < paramList.size())
    {
      if (!paramContext.containsKey(paramList.get(i))) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public static boolean isVersionCodeMatched(Context paramContext, String paramString1, String paramString2)
  {
    paramContext = paramContext.getSharedPreferences("versions", 0).getString(paramString1, "");
    if (((!TextUtils.isEmpty(paramString2)) && (!paramString2.equals(paramContext))) || (TextUtils.isEmpty(paramContext)))
    {
      Log.i("IndexDatabaseHelper", "mismatched:" + paramString1 + ", newVersionCode" + paramString2 + ", oldVersionCode" + paramContext);
      return false;
    }
    return true;
  }
  
  private void reconstruct(SQLiteDatabase paramSQLiteDatabase)
  {
    dropTables(paramSQLiteDatabase);
    bootstrapDB(paramSQLiteDatabase);
  }
  
  public static void reconstructDB(Context paramContext, SQLiteDatabase paramSQLiteDatabase)
  {
    getInstance(paramContext).reconstruct(paramSQLiteDatabase);
  }
  
  public static void setAppVersionCode(Context paramContext, String paramString1, String paramString2)
  {
    paramContext.getSharedPreferences("versions", 0).edit().putString(paramString1, paramString2).commit();
  }
  
  public static void setLocaleIndexed(Context paramContext, String paramString)
  {
    paramContext.getSharedPreferences("index", 0).edit().putBoolean(paramString, true).commit();
  }
  
  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    bootstrapDB(paramSQLiteDatabase);
  }
  
  public void onDowngrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    Log.w("IndexDatabaseHelper", "Detected schema version '" + paramInt1 + "'. " + "Index needs to be rebuilt for schema version '" + paramInt2 + "'.");
    reconstruct(paramSQLiteDatabase);
  }
  
  public void onOpen(SQLiteDatabase paramSQLiteDatabase)
  {
    super.onOpen(paramSQLiteDatabase);
    Log.i("IndexDatabaseHelper", "Using schema version: " + paramSQLiteDatabase.getVersion());
    if ((Build.VERSION.INCREMENTAL.equals(getBuildVersion(paramSQLiteDatabase))) && (Build.DISPLAY.equals(getBuildId(paramSQLiteDatabase))))
    {
      Log.i("IndexDatabaseHelper", "Index is fine");
      return;
    }
    Log.w("IndexDatabaseHelper", "Index needs to be rebuilt as build-version is not the same");
    reconstruct(paramSQLiteDatabase);
  }
  
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    if (paramInt1 < 116)
    {
      Log.w("IndexDatabaseHelper", "Detected schema version '" + paramInt1 + "'. " + "Index needs to be rebuilt for schema version '" + paramInt2 + "'.");
      reconstruct(paramSQLiteDatabase);
    }
  }
  
  public static abstract interface IndexColumns
  {
    public static final String CLASS_NAME = "class_name";
    public static final String DATA_ENTRIES = "data_entries";
    public static final String DATA_KEYWORDS = "data_keywords";
    public static final String DATA_KEY_REF = "data_key_reference";
    public static final String DATA_RANK = "data_rank";
    public static final String DATA_SUMMARY_OFF = "data_summary_off";
    public static final String DATA_SUMMARY_OFF_NORMALIZED = "data_summary_off_normalized";
    public static final String DATA_SUMMARY_ON = "data_summary_on";
    public static final String DATA_SUMMARY_ON_NORMALIZED = "data_summary_on_normalized";
    public static final String DATA_TITLE = "data_title";
    public static final String DATA_TITLE_NORMALIZED = "data_title_normalized";
    public static final String DOCID = "docid";
    public static final String ENABLED = "enabled";
    public static final String ICON = "icon";
    public static final String INTENT_ACTION = "intent_action";
    public static final String INTENT_TARGET_CLASS = "intent_target_class";
    public static final String INTENT_TARGET_PACKAGE = "intent_target_package";
    public static final String LOCALE = "locale";
    public static final String SCREEN_TITLE = "screen_title";
    public static final String USER_ID = "user_id";
  }
  
  public static abstract interface MetaColumns
  {
    public static final String BUILD = "build";
    public static final String BUILD_ID = "build_id";
  }
  
  public static abstract interface SavedQueriesColums
  {
    public static final String QUERY = "query";
    public static final String TIME_STAMP = "timestamp";
  }
  
  public static abstract interface Tables
  {
    public static final String TABLE_META_INDEX = "meta_index";
    public static final String TABLE_PREFS_INDEX = "prefs_index";
    public static final String TABLE_SAVED_QUERIES = "saved_queries";
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\search\IndexDatabaseHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */