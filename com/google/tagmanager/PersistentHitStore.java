package com.google.tagmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.google.android.gms.common.util.VisibleForTesting;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.http.impl.client.DefaultHttpClient;

class PersistentHitStore
  implements HitStore
{
  private static final String CREATE_HITS_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( '%s' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '%s' INTEGER NOT NULL, '%s' TEXT NOT NULL,'%s' INTEGER NOT NULL);", new Object[] { "gtm_hits", "hit_id", "hit_time", "hit_url", "hit_first_send_time" });
  private static final String DATABASE_FILENAME = "gtm_urls.db";
  @VisibleForTesting
  static final String HITS_TABLE = "gtm_hits";
  static final long HIT_DISPATCH_RETRY_WINDOW = 14400000L;
  @VisibleForTesting
  static final String HIT_FIRST_DISPATCH_TIME = "hit_first_send_time";
  @VisibleForTesting
  static final String HIT_ID = "hit_id";
  private static final String HIT_ID_WHERE_CLAUSE = "hit_id=?";
  @VisibleForTesting
  static final String HIT_TIME = "hit_time";
  @VisibleForTesting
  static final String HIT_URL = "hit_url";
  private Clock mClock;
  private final Context mContext;
  private final String mDatabaseName;
  private final UrlDatabaseHelper mDbHelper;
  private volatile Dispatcher mDispatcher;
  private long mLastDeleteStaleHitsTime;
  private final HitStoreStateListener mListener;
  
  PersistentHitStore(HitStoreStateListener paramHitStoreStateListener, Context paramContext)
  {
    this(paramHitStoreStateListener, paramContext, "gtm_urls.db");
  }
  
  @VisibleForTesting
  PersistentHitStore(HitStoreStateListener paramHitStoreStateListener, Context paramContext, String paramString)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mDatabaseName = paramString;
    this.mListener = paramHitStoreStateListener;
    this.mClock = new Clock()
    {
      public long currentTimeMillis()
      {
        return System.currentTimeMillis();
      }
    };
    this.mDbHelper = new UrlDatabaseHelper(this.mContext, this.mDatabaseName);
    this.mDispatcher = new SimpleNetworkDispatcher(new DefaultHttpClient(), this.mContext, new StoreDispatchListener());
    this.mLastDeleteStaleHitsTime = 0L;
  }
  
  private void deleteHit(long paramLong)
  {
    deleteHits(new String[] { String.valueOf(paramLong) });
  }
  
  private SQLiteDatabase getWritableDatabase(String paramString)
  {
    try
    {
      SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getWritableDatabase();
      return localSQLiteDatabase;
    }
    catch (SQLiteException localSQLiteException)
    {
      Log.w(paramString);
    }
    return null;
  }
  
  private void removeOldHitIfFull()
  {
    int i = getNumStoredHits() - 2000 + 1;
    if (i <= 0) {
      return;
    }
    List localList = peekHitIds(i);
    Log.v("Store full, deleting " + localList.size() + " hits to make room.");
    deleteHits((String[])localList.toArray(new String[0]));
  }
  
  private void setHitFirstDispatchTime(long paramLong1, long paramLong2)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase("Error opening database for getNumStoredHits.");
    ContentValues localContentValues;
    if (localSQLiteDatabase != null)
    {
      localContentValues = new ContentValues();
      localContentValues.put("hit_first_send_time", Long.valueOf(paramLong2));
    }
    try
    {
      localSQLiteDatabase.update("gtm_hits", localContentValues, "hit_id=?", new String[] { String.valueOf(paramLong1) });
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      Log.w("Error setting HIT_FIRST_DISPATCH_TIME for hitId: " + paramLong1);
      deleteHit(paramLong1);
    }
    return;
  }
  
  private void writeHitToDatabase(long paramLong, String paramString)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase("Error opening database for putHit");
    ContentValues localContentValues;
    if (localSQLiteDatabase != null)
    {
      localContentValues = new ContentValues();
      localContentValues.put("hit_time", Long.valueOf(paramLong));
      localContentValues.put("hit_url", paramString);
      localContentValues.put("hit_first_send_time", Integer.valueOf(0));
    }
    try
    {
      localSQLiteDatabase.insert("gtm_hits", null, localContentValues);
      this.mListener.reportStoreIsEmpty(false);
      return;
    }
    catch (SQLiteException paramString)
    {
      Log.w("Error storing hit");
    }
    return;
  }
  
  public void close()
  {
    try
    {
      this.mDbHelper.getWritableDatabase().close();
      this.mDispatcher.close();
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      Log.w("Error opening database for close");
    }
  }
  
  void deleteHits(String[] paramArrayOfString)
  {
    boolean bool = false;
    if (paramArrayOfString == null) {}
    while (paramArrayOfString.length == 0) {
      return;
    }
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase("Error opening database for deleteHits.");
    String str;
    if (localSQLiteDatabase != null) {
      str = String.format("HIT_ID in (%s)", new Object[] { TextUtils.join(",", Collections.nCopies(paramArrayOfString.length, "?")) });
    }
    try
    {
      localSQLiteDatabase.delete("gtm_hits", str, paramArrayOfString);
      paramArrayOfString = this.mListener;
      if (getNumStoredHits() != 0) {}
      for (;;)
      {
        paramArrayOfString.reportStoreIsEmpty(bool);
        return;
        return;
        bool = true;
      }
      return;
    }
    catch (SQLiteException paramArrayOfString)
    {
      Log.w("Error deleting hits");
    }
  }
  
  int deleteStaleHits()
  {
    boolean bool = false;
    long l = this.mClock.currentTimeMillis();
    if (l > this.mLastDeleteStaleHitsTime + 86400000L) {}
    for (int i = 1; i == 0; i = 0) {
      return 0;
    }
    this.mLastDeleteStaleHitsTime = l;
    Object localObject = getWritableDatabase("Error opening database for deleteStaleHits.");
    if (localObject != null)
    {
      i = ((SQLiteDatabase)localObject).delete("gtm_hits", "HIT_TIME < ?", new String[] { Long.toString(this.mClock.currentTimeMillis() - 2592000000L) });
      localObject = this.mListener;
      if (getNumStoredHits() == 0) {
        break label116;
      }
    }
    for (;;)
    {
      ((HitStoreStateListener)localObject).reportStoreIsEmpty(bool);
      return i;
      return 0;
      label116:
      bool = true;
    }
  }
  
  public void dispatch()
  {
    Log.v("GTM Dispatch running...");
    if (this.mDispatcher.okToDispatch())
    {
      List localList = peekHits(40);
      if (!localList.isEmpty())
      {
        this.mDispatcher.dispatchHits(localList);
        if (getNumStoredUntriedHits() > 0) {
          break label70;
        }
      }
    }
    else
    {
      return;
    }
    Log.v("...nothing to dispatch");
    this.mListener.reportStoreIsEmpty(true);
    return;
    label70:
    ServiceManagerImpl.getInstance().dispatch();
  }
  
  @VisibleForTesting
  public UrlDatabaseHelper getDbHelper()
  {
    return this.mDbHelper;
  }
  
  public Dispatcher getDispatcher()
  {
    return this.mDispatcher;
  }
  
  @VisibleForTesting
  UrlDatabaseHelper getHelper()
  {
    return this.mDbHelper;
  }
  
  int getNumStoredHits()
  {
    int i = 0;
    int k = 0;
    int j = 0;
    Object localObject1 = null;
    Object localObject4 = getWritableDatabase("Error opening database for getNumStoredHits.");
    if (localObject4 != null)
    {
      for (;;)
      {
        Cursor localCursor;
        try
        {
          localCursor = ((SQLiteDatabase)localObject4).rawQuery("SELECT COUNT(*) from gtm_hits", null);
          localObject1 = localCursor;
          localObject4 = localCursor;
        }
        catch (SQLiteException localSQLiteException)
        {
          long l;
          localSQLiteException = localSQLiteException;
          localObject5 = localObject1;
          Log.w("Error getting numStoredHits");
          i = k;
          if (localObject1 == null) {
            break label152;
          }
          ((Cursor)localObject1).close();
          i = j;
          continue;
        }
        finally
        {
          localObject2 = finally;
          Object localObject5 = null;
          if (localObject5 == null) {
            throw ((Throwable)localObject2);
          }
          ((Cursor)localObject5).close();
          continue;
        }
        try
        {
          if (!localCursor.moveToFirst()) {
            break label147;
          }
          localObject1 = localCursor;
          localObject4 = localCursor;
          l = localCursor.getLong(0);
          i = (int)l;
        }
        finally
        {
          continue;
          label147:
          if (localCursor != null) {
            continue;
          }
        }
      }
      localCursor.close();
      return i;
      label152:
      return i;
    }
    return 0;
  }
  
  /* Error */
  int getNumStoredUntriedHits()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aload_0
    //   3: ldc -63
    //   5: invokespecial 195	com/google/tagmanager/PersistentHitStore:getWritableDatabase	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteDatabase;
    //   8: astore_3
    //   9: aload_3
    //   10: ifnull +46 -> 56
    //   13: aload_3
    //   14: ldc 22
    //   16: iconst_2
    //   17: anewarray 60	java/lang/String
    //   20: dup
    //   21: iconst_0
    //   22: ldc 33
    //   24: aastore
    //   25: dup
    //   26: iconst_1
    //   27: ldc 30
    //   29: aastore
    //   30: ldc_w 351
    //   33: aconst_null
    //   34: aconst_null
    //   35: aconst_null
    //   36: aconst_null
    //   37: invokevirtual 355	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   40: astore_3
    //   41: aload_3
    //   42: astore_2
    //   43: aload_3
    //   44: invokeinterface 358 1 0
    //   49: istore_1
    //   50: aload_3
    //   51: ifnonnull +7 -> 58
    //   54: iload_1
    //   55: ireturn
    //   56: iconst_0
    //   57: ireturn
    //   58: aload_3
    //   59: invokeinterface 347 1 0
    //   64: iload_1
    //   65: ireturn
    //   66: astore_2
    //   67: aconst_null
    //   68: astore_3
    //   69: aload_3
    //   70: astore_2
    //   71: ldc_w 360
    //   74: invokestatic 151	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   77: aload_3
    //   78: ifnonnull +5 -> 83
    //   81: iconst_0
    //   82: ireturn
    //   83: aload_3
    //   84: invokeinterface 347 1 0
    //   89: iconst_0
    //   90: ireturn
    //   91: astore_3
    //   92: aload_2
    //   93: ifnonnull +5 -> 98
    //   96: aload_3
    //   97: athrow
    //   98: aload_2
    //   99: invokeinterface 347 1 0
    //   104: goto -8 -> 96
    //   107: astore_3
    //   108: goto -16 -> 92
    //   111: astore_2
    //   112: goto -43 -> 69
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	115	0	this	PersistentHitStore
    //   49	16	1	i	int
    //   1	42	2	localObject1	Object
    //   66	1	2	localSQLiteException1	SQLiteException
    //   70	29	2	localObject2	Object
    //   111	1	2	localSQLiteException2	SQLiteException
    //   8	76	3	localObject3	Object
    //   91	6	3	localObject4	Object
    //   107	1	3	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   13	41	66	android/database/sqlite/SQLiteException
    //   13	41	91	finally
    //   43	50	107	finally
    //   71	77	107	finally
    //   43	50	111	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  List<String> peekHitIds(int paramInt)
  {
    // Byte code:
    //   0: new 362	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 363	java/util/ArrayList:<init>	()V
    //   7: astore 6
    //   9: iload_1
    //   10: ifle +86 -> 96
    //   13: aload_0
    //   14: ldc_w 365
    //   17: invokespecial 195	com/google/tagmanager/PersistentHitStore:getWritableDatabase	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteDatabase;
    //   20: astore_3
    //   21: aload_3
    //   22: ifnull +83 -> 105
    //   25: ldc_w 367
    //   28: iconst_1
    //   29: anewarray 4	java/lang/Object
    //   32: dup
    //   33: iconst_0
    //   34: ldc 33
    //   36: aastore
    //   37: invokestatic 64	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   40: astore 4
    //   42: iload_1
    //   43: invokestatic 370	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   46: astore 5
    //   48: aload_3
    //   49: ldc 22
    //   51: iconst_1
    //   52: anewarray 60	java/lang/String
    //   55: dup
    //   56: iconst_0
    //   57: ldc 33
    //   59: aastore
    //   60: aconst_null
    //   61: aconst_null
    //   62: aconst_null
    //   63: aconst_null
    //   64: aload 4
    //   66: aload 5
    //   68: invokevirtual 373	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   71: astore 4
    //   73: aload 4
    //   75: astore_3
    //   76: aload 4
    //   78: invokeinterface 342 1 0
    //   83: istore_2
    //   84: iload_2
    //   85: ifne +23 -> 108
    //   88: aload 4
    //   90: ifnonnull +58 -> 148
    //   93: aload 6
    //   95: areturn
    //   96: ldc_w 375
    //   99: invokestatic 151	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   102: aload 6
    //   104: areturn
    //   105: aload 6
    //   107: areturn
    //   108: aload 4
    //   110: astore_3
    //   111: aload 6
    //   113: aload 4
    //   115: iconst_0
    //   116: invokeinterface 346 2 0
    //   121: invokestatic 134	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   124: invokeinterface 379 2 0
    //   129: pop
    //   130: aload 4
    //   132: astore_3
    //   133: aload 4
    //   135: invokeinterface 382 1 0
    //   140: istore_2
    //   141: iload_2
    //   142: ifeq -54 -> 88
    //   145: goto -37 -> 108
    //   148: aload 4
    //   150: invokeinterface 347 1 0
    //   155: aload 6
    //   157: areturn
    //   158: astore 5
    //   160: aconst_null
    //   161: astore 4
    //   163: aload 4
    //   165: astore_3
    //   166: new 162	java/lang/StringBuilder
    //   169: dup
    //   170: invokespecial 163	java/lang/StringBuilder:<init>	()V
    //   173: ldc_w 384
    //   176: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   179: aload 5
    //   181: invokevirtual 387	android/database/sqlite/SQLiteException:getMessage	()Ljava/lang/String;
    //   184: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   187: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   190: invokestatic 151	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   193: aload 4
    //   195: ifnull -102 -> 93
    //   198: aload 4
    //   200: invokeinterface 347 1 0
    //   205: aload 6
    //   207: areturn
    //   208: astore 4
    //   210: aconst_null
    //   211: astore_3
    //   212: aload_3
    //   213: ifnonnull +6 -> 219
    //   216: aload 4
    //   218: athrow
    //   219: aload_3
    //   220: invokeinterface 347 1 0
    //   225: goto -9 -> 216
    //   228: astore 4
    //   230: goto -18 -> 212
    //   233: astore 5
    //   235: goto -72 -> 163
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	238	0	this	PersistentHitStore
    //   0	238	1	paramInt	int
    //   83	59	2	bool	boolean
    //   20	200	3	localObject1	Object
    //   40	159	4	localObject2	Object
    //   208	9	4	localObject3	Object
    //   228	1	4	localObject4	Object
    //   46	21	5	str	String
    //   158	22	5	localSQLiteException1	SQLiteException
    //   233	1	5	localSQLiteException2	SQLiteException
    //   7	199	6	localArrayList	java.util.ArrayList
    // Exception table:
    //   from	to	target	type
    //   25	73	158	android/database/sqlite/SQLiteException
    //   25	73	208	finally
    //   76	84	228	finally
    //   111	130	228	finally
    //   133	141	228	finally
    //   166	193	228	finally
    //   76	84	233	android/database/sqlite/SQLiteException
    //   111	130	233	android/database/sqlite/SQLiteException
    //   133	141	233	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  public List<Hit> peekHits(int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: new 362	java/util/ArrayList
    //   5: dup
    //   6: invokespecial 363	java/util/ArrayList:<init>	()V
    //   9: astore 5
    //   11: aload_0
    //   12: ldc_w 391
    //   15: invokespecial 195	com/google/tagmanager/PersistentHitStore:getWritableDatabase	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteDatabase;
    //   18: astore 7
    //   20: aload 7
    //   22: ifnull +177 -> 199
    //   25: aload_3
    //   26: astore 4
    //   28: ldc_w 367
    //   31: iconst_1
    //   32: anewarray 4	java/lang/Object
    //   35: dup
    //   36: iconst_0
    //   37: ldc 33
    //   39: aastore
    //   40: invokestatic 64	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   43: astore 6
    //   45: aload_3
    //   46: astore 4
    //   48: iload_1
    //   49: invokestatic 370	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   52: astore 8
    //   54: aload_3
    //   55: astore 4
    //   57: aload 7
    //   59: ldc 22
    //   61: iconst_3
    //   62: anewarray 60	java/lang/String
    //   65: dup
    //   66: iconst_0
    //   67: ldc 33
    //   69: aastore
    //   70: dup
    //   71: iconst_1
    //   72: ldc 39
    //   74: aastore
    //   75: dup
    //   76: iconst_2
    //   77: ldc 30
    //   79: aastore
    //   80: aconst_null
    //   81: aconst_null
    //   82: aconst_null
    //   83: aconst_null
    //   84: aload 6
    //   86: aload 8
    //   88: invokevirtual 373	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   91: astore_3
    //   92: aload_3
    //   93: astore 4
    //   95: new 362	java/util/ArrayList
    //   98: dup
    //   99: invokespecial 363	java/util/ArrayList:<init>	()V
    //   102: astore 6
    //   104: aload_3
    //   105: astore 4
    //   107: aload_3
    //   108: invokeinterface 342 1 0
    //   113: istore_2
    //   114: iload_2
    //   115: ifne +87 -> 202
    //   118: aload_3
    //   119: ifnonnull +139 -> 258
    //   122: ldc_w 367
    //   125: iconst_1
    //   126: anewarray 4	java/lang/Object
    //   129: dup
    //   130: iconst_0
    //   131: ldc 33
    //   133: aastore
    //   134: invokestatic 64	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   137: astore 4
    //   139: iload_1
    //   140: invokestatic 370	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   143: astore 5
    //   145: aload 7
    //   147: ldc 22
    //   149: iconst_2
    //   150: anewarray 60	java/lang/String
    //   153: dup
    //   154: iconst_0
    //   155: ldc 33
    //   157: aastore
    //   158: dup
    //   159: iconst_1
    //   160: ldc 42
    //   162: aastore
    //   163: aconst_null
    //   164: aconst_null
    //   165: aconst_null
    //   166: aconst_null
    //   167: aload 4
    //   169: aload 5
    //   171: invokevirtual 373	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   174: astore 4
    //   176: aload 4
    //   178: astore_3
    //   179: aload 4
    //   181: invokeinterface 342 1 0
    //   186: istore_2
    //   187: iload_2
    //   188: ifne +466 -> 654
    //   191: aload 4
    //   193: ifnonnull +318 -> 511
    //   196: aload 6
    //   198: areturn
    //   199: aload 5
    //   201: areturn
    //   202: aload_3
    //   203: astore 4
    //   205: aload 6
    //   207: new 393	com/google/tagmanager/Hit
    //   210: dup
    //   211: aload_3
    //   212: iconst_0
    //   213: invokeinterface 346 2 0
    //   218: aload_3
    //   219: iconst_1
    //   220: invokeinterface 346 2 0
    //   225: aload_3
    //   226: iconst_2
    //   227: invokeinterface 346 2 0
    //   232: invokespecial 396	com/google/tagmanager/Hit:<init>	(JJJ)V
    //   235: invokeinterface 379 2 0
    //   240: pop
    //   241: aload_3
    //   242: astore 4
    //   244: aload_3
    //   245: invokeinterface 382 1 0
    //   250: istore_2
    //   251: iload_2
    //   252: ifeq -134 -> 118
    //   255: goto -53 -> 202
    //   258: aload_3
    //   259: invokeinterface 347 1 0
    //   264: goto -142 -> 122
    //   267: astore 4
    //   269: aconst_null
    //   270: astore_3
    //   271: new 162	java/lang/StringBuilder
    //   274: dup
    //   275: invokespecial 163	java/lang/StringBuilder:<init>	()V
    //   278: ldc_w 384
    //   281: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   284: aload 4
    //   286: invokevirtual 387	android/database/sqlite/SQLiteException:getMessage	()Ljava/lang/String;
    //   289: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   292: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   295: invokestatic 151	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   298: aload_3
    //   299: ifnonnull +6 -> 305
    //   302: aload 5
    //   304: areturn
    //   305: aload_3
    //   306: invokeinterface 347 1 0
    //   311: aload 5
    //   313: areturn
    //   314: astore_3
    //   315: aload 4
    //   317: ifnonnull +5 -> 322
    //   320: aload_3
    //   321: athrow
    //   322: aload 4
    //   324: invokeinterface 347 1 0
    //   329: goto -9 -> 320
    //   332: aload 4
    //   334: astore_3
    //   335: aload 6
    //   337: iload_1
    //   338: invokeinterface 400 2 0
    //   343: checkcast 393	com/google/tagmanager/Hit
    //   346: aload 4
    //   348: iconst_1
    //   349: invokeinterface 403 2 0
    //   354: invokevirtual 406	com/google/tagmanager/Hit:setHitUrl	(Ljava/lang/String;)V
    //   357: aload 4
    //   359: astore_3
    //   360: aload 4
    //   362: invokeinterface 382 1 0
    //   367: ifeq -176 -> 191
    //   370: iload_1
    //   371: iconst_1
    //   372: iadd
    //   373: istore_1
    //   374: aload 4
    //   376: astore_3
    //   377: aload 4
    //   379: checkcast 408	android/database/sqlite/SQLiteCursor
    //   382: invokevirtual 412	android/database/sqlite/SQLiteCursor:getWindow	()Landroid/database/CursorWindow;
    //   385: invokevirtual 417	android/database/CursorWindow:getNumRows	()I
    //   388: ifgt -56 -> 332
    //   391: aload 4
    //   393: astore_3
    //   394: ldc_w 419
    //   397: iconst_1
    //   398: anewarray 4	java/lang/Object
    //   401: dup
    //   402: iconst_0
    //   403: aload 6
    //   405: iload_1
    //   406: invokeinterface 400 2 0
    //   411: checkcast 393	com/google/tagmanager/Hit
    //   414: invokevirtual 422	com/google/tagmanager/Hit:getHitId	()J
    //   417: invokestatic 203	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   420: aastore
    //   421: invokestatic 64	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   424: invokestatic 151	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   427: goto -70 -> 357
    //   430: astore 5
    //   432: aload 4
    //   434: astore_3
    //   435: new 162	java/lang/StringBuilder
    //   438: dup
    //   439: invokespecial 163	java/lang/StringBuilder:<init>	()V
    //   442: ldc_w 424
    //   445: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   448: aload 5
    //   450: invokevirtual 387	android/database/sqlite/SQLiteException:getMessage	()Ljava/lang/String;
    //   453: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   456: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   459: invokestatic 151	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   462: aload 4
    //   464: astore_3
    //   465: new 362	java/util/ArrayList
    //   468: dup
    //   469: invokespecial 363	java/util/ArrayList:<init>	()V
    //   472: astore 5
    //   474: aload 4
    //   476: astore_3
    //   477: aload 6
    //   479: invokeinterface 428 1 0
    //   484: astore 6
    //   486: iconst_0
    //   487: istore_1
    //   488: aload 4
    //   490: astore_3
    //   491: aload 6
    //   493: invokeinterface 433 1 0
    //   498: istore_2
    //   499: iload_2
    //   500: ifne +21 -> 521
    //   503: aload 4
    //   505: ifnonnull +85 -> 590
    //   508: aload 5
    //   510: areturn
    //   511: aload 4
    //   513: invokeinterface 347 1 0
    //   518: aload 6
    //   520: areturn
    //   521: aload 4
    //   523: astore_3
    //   524: aload 6
    //   526: invokeinterface 437 1 0
    //   531: checkcast 393	com/google/tagmanager/Hit
    //   534: astore 7
    //   536: aload 4
    //   538: astore_3
    //   539: aload 7
    //   541: invokevirtual 440	com/google/tagmanager/Hit:getHitUrl	()Ljava/lang/String;
    //   544: invokestatic 443	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   547: ifne +34 -> 581
    //   550: aload 4
    //   552: astore_3
    //   553: aload 5
    //   555: aload 7
    //   557: invokeinterface 379 2 0
    //   562: pop
    //   563: goto -75 -> 488
    //   566: astore 5
    //   568: aload_3
    //   569: astore 4
    //   571: aload 5
    //   573: astore_3
    //   574: aload 4
    //   576: ifnonnull +24 -> 600
    //   579: aload_3
    //   580: athrow
    //   581: iload_1
    //   582: ifne -79 -> 503
    //   585: iconst_1
    //   586: istore_1
    //   587: goto -37 -> 550
    //   590: aload 4
    //   592: invokeinterface 347 1 0
    //   597: aload 5
    //   599: areturn
    //   600: aload 4
    //   602: invokeinterface 347 1 0
    //   607: goto -28 -> 579
    //   610: astore 5
    //   612: aload_3
    //   613: astore 4
    //   615: aload 5
    //   617: astore_3
    //   618: goto -44 -> 574
    //   621: astore 5
    //   623: aload_3
    //   624: astore 4
    //   626: goto -194 -> 432
    //   629: astore 5
    //   631: aload_3
    //   632: astore 4
    //   634: aload 5
    //   636: astore_3
    //   637: goto -322 -> 315
    //   640: astore 4
    //   642: goto -371 -> 271
    //   645: astore 4
    //   647: aload 6
    //   649: astore 5
    //   651: goto -380 -> 271
    //   654: iconst_0
    //   655: istore_1
    //   656: goto -282 -> 374
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	659	0	this	PersistentHitStore
    //   0	659	1	paramInt	int
    //   113	387	2	bool	boolean
    //   1	305	3	localObject1	Object
    //   314	7	3	localObject2	Object
    //   334	303	3	localObject3	Object
    //   26	217	4	localObject4	Object
    //   267	284	4	localSQLiteException1	SQLiteException
    //   569	64	4	localObject5	Object
    //   640	1	4	localSQLiteException2	SQLiteException
    //   645	1	4	localSQLiteException3	SQLiteException
    //   9	303	5	localObject6	Object
    //   430	19	5	localSQLiteException4	SQLiteException
    //   472	82	5	localArrayList	java.util.ArrayList
    //   566	32	5	localList	List<Hit>
    //   610	6	5	localObject7	Object
    //   621	1	5	localSQLiteException5	SQLiteException
    //   629	6	5	localObject8	Object
    //   649	1	5	localObject9	Object
    //   43	605	6	localObject10	Object
    //   18	538	7	localObject11	Object
    //   52	35	8	str	String
    // Exception table:
    //   from	to	target	type
    //   28	45	267	android/database/sqlite/SQLiteException
    //   48	54	267	android/database/sqlite/SQLiteException
    //   57	92	267	android/database/sqlite/SQLiteException
    //   28	45	314	finally
    //   48	54	314	finally
    //   57	92	314	finally
    //   95	104	314	finally
    //   107	114	314	finally
    //   205	241	314	finally
    //   244	251	314	finally
    //   179	187	430	android/database/sqlite/SQLiteException
    //   335	357	430	android/database/sqlite/SQLiteException
    //   360	370	430	android/database/sqlite/SQLiteException
    //   377	391	430	android/database/sqlite/SQLiteException
    //   394	427	430	android/database/sqlite/SQLiteException
    //   179	187	566	finally
    //   335	357	566	finally
    //   360	370	566	finally
    //   377	391	566	finally
    //   394	427	566	finally
    //   435	462	566	finally
    //   465	474	566	finally
    //   477	486	566	finally
    //   491	499	566	finally
    //   524	536	566	finally
    //   539	550	566	finally
    //   553	563	566	finally
    //   122	176	610	finally
    //   122	176	621	android/database/sqlite/SQLiteException
    //   271	298	629	finally
    //   95	104	640	android/database/sqlite/SQLiteException
    //   107	114	645	android/database/sqlite/SQLiteException
    //   205	241	645	android/database/sqlite/SQLiteException
    //   244	251	645	android/database/sqlite/SQLiteException
  }
  
  public void putHit(long paramLong, String paramString)
  {
    deleteStaleHits();
    removeOldHitIfFull();
    writeHitToDatabase(paramLong, paramString);
  }
  
  @VisibleForTesting
  public void setClock(Clock paramClock)
  {
    this.mClock = paramClock;
  }
  
  @VisibleForTesting
  void setDispatcher(Dispatcher paramDispatcher)
  {
    this.mDispatcher = paramDispatcher;
  }
  
  @VisibleForTesting
  void setLastDeleteStaleHitsTime(long paramLong)
  {
    this.mLastDeleteStaleHitsTime = paramLong;
  }
  
  @VisibleForTesting
  class StoreDispatchListener
    implements SimpleNetworkDispatcher.DispatchListener
  {
    StoreDispatchListener() {}
    
    public void onHitDispatched(Hit paramHit)
    {
      PersistentHitStore.this.deleteHit(paramHit.getHitId());
    }
    
    public void onHitPermanentDispatchFailure(Hit paramHit)
    {
      PersistentHitStore.this.deleteHit(paramHit.getHitId());
      Log.v("Permanent failure dispatching hitId: " + paramHit.getHitId());
    }
    
    public void onHitTransientDispatchFailure(Hit paramHit)
    {
      long l = paramHit.getHitFirstDispatchTime();
      if (l == 0L) {
        PersistentHitStore.this.setHitFirstDispatchTime(paramHit.getHitId(), PersistentHitStore.this.mClock.currentTimeMillis());
      }
      for (;;)
      {
        return;
        if (l + 14400000L >= PersistentHitStore.this.mClock.currentTimeMillis()) {}
        for (int i = 1; i == 0; i = 0)
        {
          PersistentHitStore.this.deleteHit(paramHit.getHitId());
          Log.v("Giving up on failed hitId: " + paramHit.getHitId());
          return;
        }
      }
    }
  }
  
  @VisibleForTesting
  class UrlDatabaseHelper
    extends SQLiteOpenHelper
  {
    private boolean mBadDatabase;
    private long mLastDatabaseCheckTime = 0L;
    
    UrlDatabaseHelper(Context paramContext, String paramString)
    {
      super(paramString, null, 1);
    }
    
    /* Error */
    private boolean tablePresent(String paramString, SQLiteDatabase paramSQLiteDatabase)
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore 4
      //   3: aload_2
      //   4: ldc 30
      //   6: iconst_1
      //   7: anewarray 32	java/lang/String
      //   10: dup
      //   11: iconst_0
      //   12: ldc 34
      //   14: aastore
      //   15: ldc 36
      //   17: iconst_1
      //   18: anewarray 32	java/lang/String
      //   21: dup
      //   22: iconst_0
      //   23: aload_1
      //   24: aastore
      //   25: aconst_null
      //   26: aconst_null
      //   27: aconst_null
      //   28: invokevirtual 42	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
      //   31: astore_2
      //   32: aload_2
      //   33: astore 4
      //   35: aload 4
      //   37: astore_2
      //   38: aload 4
      //   40: invokeinterface 48 1 0
      //   45: istore_3
      //   46: aload 4
      //   48: ifnonnull +5 -> 53
      //   51: iload_3
      //   52: ireturn
      //   53: aload 4
      //   55: invokeinterface 52 1 0
      //   60: iload_3
      //   61: ireturn
      //   62: astore_2
      //   63: aconst_null
      //   64: astore 4
      //   66: aload 4
      //   68: astore_2
      //   69: new 54	java/lang/StringBuilder
      //   72: dup
      //   73: invokespecial 56	java/lang/StringBuilder:<init>	()V
      //   76: ldc 58
      //   78: invokevirtual 62	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   81: aload_1
      //   82: invokevirtual 62	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   85: invokevirtual 66	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   88: invokestatic 72	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
      //   91: aload 4
      //   93: ifnonnull +5 -> 98
      //   96: iconst_0
      //   97: ireturn
      //   98: aload 4
      //   100: invokeinterface 52 1 0
      //   105: iconst_0
      //   106: ireturn
      //   107: astore_1
      //   108: aload 4
      //   110: astore_2
      //   111: aload_2
      //   112: ifnonnull +5 -> 117
      //   115: aload_1
      //   116: athrow
      //   117: aload_2
      //   118: invokeinterface 52 1 0
      //   123: goto -8 -> 115
      //   126: astore_1
      //   127: goto -16 -> 111
      //   130: astore_2
      //   131: goto -65 -> 66
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	134	0	this	UrlDatabaseHelper
      //   0	134	1	paramString	String
      //   0	134	2	paramSQLiteDatabase	SQLiteDatabase
      //   45	16	3	bool	boolean
      //   1	108	4	localSQLiteDatabase	SQLiteDatabase
      // Exception table:
      //   from	to	target	type
      //   3	32	62	android/database/sqlite/SQLiteException
      //   3	32	107	finally
      //   38	46	126	finally
      //   69	91	126	finally
      //   38	46	130	android/database/sqlite/SQLiteException
    }
    
    private void validateColumnsPresent(SQLiteDatabase paramSQLiteDatabase)
    {
      int i = 0;
      paramSQLiteDatabase = paramSQLiteDatabase.rawQuery("SELECT * FROM gtm_hits WHERE 0", null);
      HashSet localHashSet = new HashSet();
      do
      {
        try
        {
          String[] arrayOfString = paramSQLiteDatabase.getColumnNames();
          int j = arrayOfString.length;
          if (i >= j)
          {
            paramSQLiteDatabase.close();
            if (localHashSet.remove("hit_id")) {
              continue;
            }
          }
          while (!((Set)localObject).remove("hit_url"))
          {
            throw new SQLiteException("Database column missing");
            localHashSet.add(arrayOfString[i]);
            i += 1;
            break;
          }
        }
        finally
        {
          paramSQLiteDatabase.close();
        }
      } while ((!((Set)localObject).remove("hit_time")) || (!((Set)localObject).remove("hit_first_send_time")));
      if (((Set)localObject).isEmpty()) {
        return;
      }
      throw new SQLiteException("Database has extra columns");
    }
    
    public SQLiteDatabase getWritableDatabase()
    {
      localObject = null;
      if (!this.mBadDatabase) {}
      for (;;)
      {
        this.mBadDatabase = true;
        this.mLastDatabaseCheckTime = PersistentHitStore.this.mClock.currentTimeMillis();
        try
        {
          SQLiteDatabase localSQLiteDatabase = super.getWritableDatabase();
          localObject = localSQLiteDatabase;
        }
        catch (SQLiteException localSQLiteException)
        {
          for (;;)
          {
            int i;
            PersistentHitStore.this.mContext.getDatabasePath(PersistentHitStore.this.mDatabaseName).delete();
            continue;
            localObject = super.getWritableDatabase();
          }
        }
        if (localObject == null) {
          break;
        }
        this.mBadDatabase = false;
        return (SQLiteDatabase)localObject;
        if (this.mLastDatabaseCheckTime + 3600000L <= PersistentHitStore.this.mClock.currentTimeMillis()) {}
        for (i = 1; i == 0; i = 0) {
          throw new SQLiteException("Database creation failed");
        }
      }
    }
    
    boolean isBadDatabase()
    {
      return this.mBadDatabase;
    }
    
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      FutureApis.setOwnerOnlyReadWrite(paramSQLiteDatabase.getPath());
    }
    
    public void onOpen(SQLiteDatabase paramSQLiteDatabase)
    {
      if (Build.VERSION.SDK_INT >= 15) {}
      for (;;)
      {
        Cursor localCursor;
        if (tablePresent("gtm_hits", paramSQLiteDatabase))
        {
          validateColumnsPresent(paramSQLiteDatabase);
          return;
          localCursor = paramSQLiteDatabase.rawQuery("PRAGMA journal_mode=memory", null);
        }
        try
        {
          localCursor.moveToFirst();
          localCursor.close();
        }
        finally
        {
          localCursor.close();
        }
      }
    }
    
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
    
    void setBadDatabase(boolean paramBoolean)
    {
      this.mBadDatabase = paramBoolean;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\PersistentHitStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */