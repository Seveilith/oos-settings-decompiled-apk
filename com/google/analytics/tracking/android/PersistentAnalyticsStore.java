package com.google.analytics.tracking.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.google.android.gms.analytics.internal.Command;
import com.google.android.gms.common.util.VisibleForTesting;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.http.impl.client.DefaultHttpClient;

class PersistentAnalyticsStore
  implements AnalyticsStore
{
  private static final String CREATE_HITS_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( '%s' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '%s' INTEGER NOT NULL, '%s' TEXT NOT NULL, '%s' TEXT NOT NULL, '%s' INTEGER);", new Object[] { "hits2", "hit_id", "hit_time", "hit_url", "hit_string", "hit_app_id" });
  private static final String DATABASE_FILENAME = "google_analytics_v2.db";
  @VisibleForTesting
  static final String HITS_TABLE = "hits2";
  @VisibleForTesting
  static final String HIT_APP_ID = "hit_app_id";
  @VisibleForTesting
  static final String HIT_ID = "hit_id";
  @VisibleForTesting
  static final String HIT_STRING = "hit_string";
  @VisibleForTesting
  static final String HIT_TIME = "hit_time";
  @VisibleForTesting
  static final String HIT_URL = "hit_url";
  private Clock mClock;
  private final Context mContext;
  private final String mDatabaseName;
  private final AnalyticsDatabaseHelper mDbHelper;
  private volatile Dispatcher mDispatcher;
  private long mLastDeleteStaleHitsTime;
  private final AnalyticsStoreStateListener mListener;
  
  PersistentAnalyticsStore(AnalyticsStoreStateListener paramAnalyticsStoreStateListener, Context paramContext)
  {
    this(paramAnalyticsStoreStateListener, paramContext, "google_analytics_v2.db");
  }
  
  @VisibleForTesting
  PersistentAnalyticsStore(AnalyticsStoreStateListener paramAnalyticsStoreStateListener, Context paramContext, String paramString)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mDatabaseName = paramString;
    this.mListener = paramAnalyticsStoreStateListener;
    this.mClock = new Clock()
    {
      public long currentTimeMillis()
      {
        return System.currentTimeMillis();
      }
    };
    this.mDbHelper = new AnalyticsDatabaseHelper(this.mContext, this.mDatabaseName);
    this.mDispatcher = new SimpleNetworkDispatcher(new DefaultHttpClient(), this.mContext);
    this.mLastDeleteStaleHitsTime = 0L;
  }
  
  private void fillVersionParameter(Map<String, String> paramMap, Collection<Command> paramCollection)
  {
    String str = "&_v".substring(1);
    if (paramCollection == null) {}
    Command localCommand;
    do
    {
      return;
      while (!paramCollection.hasNext()) {
        paramCollection = paramCollection.iterator();
      }
      localCommand = (Command)paramCollection.next();
    } while (!"appendVersion".equals(localCommand.getId()));
    paramMap.put(str, localCommand.getValue());
  }
  
  static String generateHitString(Map<String, String> paramMap)
  {
    ArrayList localArrayList = new ArrayList(paramMap.size());
    paramMap = paramMap.entrySet().iterator();
    for (;;)
    {
      if (!paramMap.hasNext()) {
        return TextUtils.join("&", localArrayList);
      }
      Map.Entry localEntry = (Map.Entry)paramMap.next();
      localArrayList.add(HitBuilder.encode((String)localEntry.getKey()) + "=" + HitBuilder.encode((String)localEntry.getValue()));
    }
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
  
  private void writeHitToDatabase(Map<String, String> paramMap, long paramLong, String paramString)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase("Error opening database for putHit");
    ContentValues localContentValues;
    if (localSQLiteDatabase != null)
    {
      localContentValues = new ContentValues();
      localContentValues.put("hit_string", generateHitString(paramMap));
      localContentValues.put("hit_time", Long.valueOf(paramLong));
      if (paramMap.containsKey("AppUID")) {
        break label115;
      }
      paramLong = 0L;
      localContentValues.put("hit_app_id", Long.valueOf(paramLong));
      if (paramString == null) {
        break label140;
      }
    }
    for (;;)
    {
      if (paramString.length() == 0) {
        break label148;
      }
      localContentValues.put("hit_url", paramString);
      try
      {
        localSQLiteDatabase.insert("hits2", null, localContentValues);
        this.mListener.reportStoreIsEmpty(false);
        return;
      }
      catch (SQLiteException paramMap)
      {
        Log.w("Error storing hit");
      }
      return;
      try
      {
        label115:
        paramLong = Long.parseLong((String)paramMap.get("AppUID"));
      }
      catch (NumberFormatException paramMap)
      {
        paramLong = 0L;
      }
      break;
      label140:
      paramString = "http://www.google-analytics.com/collect";
    }
    label148:
    Log.w("Empty path: not sending hit");
    return;
  }
  
  public void clearHits(long paramLong)
  {
    boolean bool = false;
    Object localObject = getWritableDatabase("Error opening database for clearHits");
    if (localObject == null) {
      return;
    }
    if (paramLong == 0L)
    {
      ((SQLiteDatabase)localObject).delete("hits2", null, null);
      localObject = this.mListener;
      if (getNumStoredHits() == 0) {
        break label83;
      }
    }
    for (;;)
    {
      ((AnalyticsStoreStateListener)localObject).reportStoreIsEmpty(bool);
      return;
      ((SQLiteDatabase)localObject).delete("hits2", "hit_app_id = ?", new String[] { Long.valueOf(paramLong).toString() });
      break;
      label83:
      bool = true;
    }
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
  
  @Deprecated
  void deleteHits(Collection<Hit> paramCollection)
  {
    if (paramCollection == null) {}
    while (paramCollection.isEmpty())
    {
      Log.w("Empty/Null collection passed to deleteHits.");
      return;
    }
    String[] arrayOfString = new String[paramCollection.size()];
    paramCollection = paramCollection.iterator();
    int i = 0;
    for (;;)
    {
      if (!paramCollection.hasNext())
      {
        deleteHits(arrayOfString);
        return;
      }
      arrayOfString[i] = String.valueOf(((Hit)paramCollection.next()).getHitId());
      i += 1;
    }
  }
  
  void deleteHits(String[] paramArrayOfString)
  {
    boolean bool = false;
    if (paramArrayOfString == null) {}
    while (paramArrayOfString.length == 0)
    {
      Log.w("Empty hitIds passed to deleteHits.");
      return;
    }
    Object localObject = getWritableDatabase("Error opening database for deleteHits.");
    String str;
    if (localObject != null) {
      str = String.format("HIT_ID in (%s)", new Object[] { TextUtils.join(",", Collections.nCopies(paramArrayOfString.length, "?")) });
    }
    try
    {
      ((SQLiteDatabase)localObject).delete("hits2", str, paramArrayOfString);
      localObject = this.mListener;
      if (getNumStoredHits() != 0) {}
      for (;;)
      {
        ((AnalyticsStoreStateListener)localObject).reportStoreIsEmpty(bool);
        return;
        return;
        bool = true;
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      Log.w("Error deleting hits " + paramArrayOfString);
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
      i = ((SQLiteDatabase)localObject).delete("hits2", "HIT_TIME < ?", new String[] { Long.toString(this.mClock.currentTimeMillis() - 2592000000L) });
      localObject = this.mListener;
      if (getNumStoredHits() == 0) {
        break label116;
      }
    }
    for (;;)
    {
      ((AnalyticsStoreStateListener)localObject).reportStoreIsEmpty(bool);
      return i;
      return 0;
      label116:
      bool = true;
    }
  }
  
  public void dispatch()
  {
    Log.v("Dispatch running...");
    if (this.mDispatcher.okToDispatch())
    {
      List localList = peekHits(40);
      if (localList.isEmpty()) {
        break label122;
      }
      int i = this.mDispatcher.dispatchHits(localList);
      Log.v("sent " + i + " of " + localList.size() + " hits");
      deleteHits(localList.subList(0, Math.min(i, localList.size())));
      if (i == localList.size()) {
        break label139;
      }
    }
    label122:
    label139:
    while (getNumStoredHits() <= 0)
    {
      return;
      return;
      Log.v("...nothing to dispatch");
      this.mListener.reportStoreIsEmpty(true);
      return;
    }
    GAServiceManager.getInstance().dispatchLocalHits();
  }
  
  @VisibleForTesting
  public AnalyticsDatabaseHelper getDbHelper()
  {
    return this.mDbHelper;
  }
  
  public Dispatcher getDispatcher()
  {
    return this.mDispatcher;
  }
  
  @VisibleForTesting
  AnalyticsDatabaseHelper getHelper()
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
          localCursor = ((SQLiteDatabase)localObject4).rawQuery("SELECT COUNT(*) from hits2", null);
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
            break label153;
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
            break label148;
          }
          localObject1 = localCursor;
          localObject4 = localCursor;
          l = localCursor.getLong(0);
          i = (int)l;
        }
        finally
        {
          continue;
          label148:
          if (localCursor != null) {
            continue;
          }
        }
      }
      localCursor.close();
      return i;
      label153:
      return i;
    }
    return 0;
  }
  
  /* Error */
  List<String> peekHitIds(int paramInt)
  {
    // Byte code:
    //   0: new 161	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 460	java/util/ArrayList:<init>	()V
    //   7: astore 6
    //   9: iload_1
    //   10: ifle +86 -> 96
    //   13: aload_0
    //   14: ldc_w 462
    //   17: invokespecial 264	com/google/analytics/tracking/android/PersistentAnalyticsStore:getWritableDatabase	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteDatabase;
    //   20: astore_3
    //   21: aload_3
    //   22: ifnull +83 -> 105
    //   25: ldc_w 464
    //   28: iconst_1
    //   29: anewarray 4	java/lang/Object
    //   32: dup
    //   33: iconst_0
    //   34: ldc 26
    //   36: aastore
    //   37: invokestatic 58	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   40: astore 4
    //   42: iload_1
    //   43: invokestatic 468	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   46: astore 5
    //   48: aload_3
    //   49: ldc 19
    //   51: iconst_1
    //   52: anewarray 54	java/lang/String
    //   55: dup
    //   56: iconst_0
    //   57: ldc 26
    //   59: aastore
    //   60: aconst_null
    //   61: aconst_null
    //   62: aconst_null
    //   63: aconst_null
    //   64: aload 4
    //   66: aload 5
    //   68: invokevirtual 472	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   71: astore 4
    //   73: aload 4
    //   75: astore_3
    //   76: aload 4
    //   78: invokeinterface 452 1 0
    //   83: istore_2
    //   84: iload_2
    //   85: ifne +23 -> 108
    //   88: aload 4
    //   90: ifnonnull +58 -> 148
    //   93: aload 6
    //   95: areturn
    //   96: ldc_w 474
    //   99: invokestatic 227	com/google/analytics/tracking/android/Log:w	(Ljava/lang/String;)V
    //   102: aload 6
    //   104: areturn
    //   105: aload 6
    //   107: areturn
    //   108: aload 4
    //   110: astore_3
    //   111: aload 6
    //   113: aload 4
    //   115: iconst_0
    //   116: invokeinterface 456 2 0
    //   121: invokestatic 352	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   124: invokeinterface 213 2 0
    //   129: pop
    //   130: aload 4
    //   132: astore_3
    //   133: aload 4
    //   135: invokeinterface 477 1 0
    //   140: istore_2
    //   141: iload_2
    //   142: ifeq -54 -> 88
    //   145: goto -37 -> 108
    //   148: aload 4
    //   150: invokeinterface 457 1 0
    //   155: aload 6
    //   157: areturn
    //   158: astore 5
    //   160: aconst_null
    //   161: astore 4
    //   163: aload 4
    //   165: astore_3
    //   166: new 187	java/lang/StringBuilder
    //   169: dup
    //   170: invokespecial 188	java/lang/StringBuilder:<init>	()V
    //   173: ldc_w 479
    //   176: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   179: aload 5
    //   181: invokevirtual 482	android/database/sqlite/SQLiteException:getMessage	()Ljava/lang/String;
    //   184: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   187: invokevirtual 208	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   190: invokestatic 227	com/google/analytics/tracking/android/Log:w	(Ljava/lang/String;)V
    //   193: aload 4
    //   195: ifnull -102 -> 93
    //   198: aload 4
    //   200: invokeinterface 457 1 0
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
    //   220: invokeinterface 457 1 0
    //   225: goto -9 -> 216
    //   228: astore 4
    //   230: goto -18 -> 212
    //   233: astore 5
    //   235: goto -72 -> 163
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	238	0	this	PersistentAnalyticsStore
    //   0	238	1	paramInt	int
    //   83	59	2	bool	boolean
    //   20	200	3	localObject1	Object
    //   40	159	4	localObject2	Object
    //   208	9	4	localObject3	Object
    //   228	1	4	localObject4	Object
    //   46	21	5	str	String
    //   158	22	5	localSQLiteException1	SQLiteException
    //   233	1	5	localSQLiteException2	SQLiteException
    //   7	199	6	localArrayList	ArrayList
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
    //   2: new 161	java/util/ArrayList
    //   5: dup
    //   6: invokespecial 460	java/util/ArrayList:<init>	()V
    //   9: astore 5
    //   11: aload_0
    //   12: ldc_w 485
    //   15: invokespecial 264	com/google/analytics/tracking/android/PersistentAnalyticsStore:getWritableDatabase	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteDatabase;
    //   18: astore 7
    //   20: aload 7
    //   22: ifnull +177 -> 199
    //   25: aload_3
    //   26: astore 4
    //   28: ldc_w 464
    //   31: iconst_1
    //   32: anewarray 4	java/lang/Object
    //   35: dup
    //   36: iconst_0
    //   37: ldc 26
    //   39: aastore
    //   40: invokestatic 58	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   43: astore 6
    //   45: aload_3
    //   46: astore 4
    //   48: iload_1
    //   49: invokestatic 468	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   52: astore 8
    //   54: aload_3
    //   55: astore 4
    //   57: aload 7
    //   59: ldc 19
    //   61: iconst_2
    //   62: anewarray 54	java/lang/String
    //   65: dup
    //   66: iconst_0
    //   67: ldc 26
    //   69: aastore
    //   70: dup
    //   71: iconst_1
    //   72: ldc 32
    //   74: aastore
    //   75: aconst_null
    //   76: aconst_null
    //   77: aconst_null
    //   78: aconst_null
    //   79: aload 6
    //   81: aload 8
    //   83: invokevirtual 472	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   86: astore_3
    //   87: aload_3
    //   88: astore 4
    //   90: new 161	java/util/ArrayList
    //   93: dup
    //   94: invokespecial 460	java/util/ArrayList:<init>	()V
    //   97: astore 6
    //   99: aload_3
    //   100: astore 4
    //   102: aload_3
    //   103: invokeinterface 452 1 0
    //   108: istore_2
    //   109: iload_2
    //   110: ifne +92 -> 202
    //   113: aload_3
    //   114: ifnonnull +138 -> 252
    //   117: ldc_w 464
    //   120: iconst_1
    //   121: anewarray 4	java/lang/Object
    //   124: dup
    //   125: iconst_0
    //   126: ldc 26
    //   128: aastore
    //   129: invokestatic 58	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   132: astore 4
    //   134: iload_1
    //   135: invokestatic 468	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   138: astore 5
    //   140: aload 7
    //   142: ldc 19
    //   144: iconst_3
    //   145: anewarray 54	java/lang/String
    //   148: dup
    //   149: iconst_0
    //   150: ldc 26
    //   152: aastore
    //   153: dup
    //   154: iconst_1
    //   155: ldc 29
    //   157: aastore
    //   158: dup
    //   159: iconst_2
    //   160: ldc 35
    //   162: aastore
    //   163: aconst_null
    //   164: aconst_null
    //   165: aconst_null
    //   166: aconst_null
    //   167: aload 4
    //   169: aload 5
    //   171: invokevirtual 472	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   174: astore 4
    //   176: aload 4
    //   178: astore_3
    //   179: aload 4
    //   181: invokeinterface 452 1 0
    //   186: istore_2
    //   187: iload_2
    //   188: ifne +485 -> 673
    //   191: aload 4
    //   193: ifnonnull +337 -> 530
    //   196: aload 6
    //   198: areturn
    //   199: aload 5
    //   201: areturn
    //   202: aload_3
    //   203: astore 4
    //   205: aload 6
    //   207: new 345	com/google/analytics/tracking/android/Hit
    //   210: dup
    //   211: aconst_null
    //   212: aload_3
    //   213: iconst_0
    //   214: invokeinterface 456 2 0
    //   219: aload_3
    //   220: iconst_1
    //   221: invokeinterface 456 2 0
    //   226: invokespecial 488	com/google/analytics/tracking/android/Hit:<init>	(Ljava/lang/String;JJ)V
    //   229: invokeinterface 213 2 0
    //   234: pop
    //   235: aload_3
    //   236: astore 4
    //   238: aload_3
    //   239: invokeinterface 477 1 0
    //   244: istore_2
    //   245: iload_2
    //   246: ifeq -133 -> 113
    //   249: goto -47 -> 202
    //   252: aload_3
    //   253: invokeinterface 457 1 0
    //   258: goto -141 -> 117
    //   261: astore 4
    //   263: aconst_null
    //   264: astore_3
    //   265: new 187	java/lang/StringBuilder
    //   268: dup
    //   269: invokespecial 188	java/lang/StringBuilder:<init>	()V
    //   272: ldc_w 479
    //   275: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   278: aload 4
    //   280: invokevirtual 482	android/database/sqlite/SQLiteException:getMessage	()Ljava/lang/String;
    //   283: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   286: invokevirtual 208	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   289: invokestatic 227	com/google/analytics/tracking/android/Log:w	(Ljava/lang/String;)V
    //   292: aload_3
    //   293: ifnonnull +6 -> 299
    //   296: aload 5
    //   298: areturn
    //   299: aload_3
    //   300: invokeinterface 457 1 0
    //   305: aload 5
    //   307: areturn
    //   308: astore_3
    //   309: aload 4
    //   311: ifnonnull +5 -> 316
    //   314: aload_3
    //   315: athrow
    //   316: aload 4
    //   318: invokeinterface 457 1 0
    //   323: goto -9 -> 314
    //   326: aload 4
    //   328: astore_3
    //   329: aload 6
    //   331: iload_1
    //   332: invokeinterface 491 2 0
    //   337: checkcast 345	com/google/analytics/tracking/android/Hit
    //   340: aload 4
    //   342: iconst_1
    //   343: invokeinterface 494 2 0
    //   348: invokevirtual 497	com/google/analytics/tracking/android/Hit:setHitString	(Ljava/lang/String;)V
    //   351: aload 4
    //   353: astore_3
    //   354: aload 6
    //   356: iload_1
    //   357: invokeinterface 491 2 0
    //   362: checkcast 345	com/google/analytics/tracking/android/Hit
    //   365: aload 4
    //   367: iconst_2
    //   368: invokeinterface 494 2 0
    //   373: invokevirtual 500	com/google/analytics/tracking/android/Hit:setHitUrl	(Ljava/lang/String;)V
    //   376: aload 4
    //   378: astore_3
    //   379: aload 4
    //   381: invokeinterface 477 1 0
    //   386: ifeq -195 -> 191
    //   389: iload_1
    //   390: iconst_1
    //   391: iadd
    //   392: istore_1
    //   393: aload 4
    //   395: astore_3
    //   396: aload 4
    //   398: checkcast 502	android/database/sqlite/SQLiteCursor
    //   401: invokevirtual 506	android/database/sqlite/SQLiteCursor:getWindow	()Landroid/database/CursorWindow;
    //   404: invokevirtual 511	android/database/CursorWindow:getNumRows	()I
    //   407: ifgt -81 -> 326
    //   410: aload 4
    //   412: astore_3
    //   413: ldc_w 513
    //   416: iconst_1
    //   417: anewarray 4	java/lang/Object
    //   420: dup
    //   421: iconst_0
    //   422: aload 6
    //   424: iload_1
    //   425: invokeinterface 491 2 0
    //   430: checkcast 345	com/google/analytics/tracking/android/Hit
    //   433: invokevirtual 349	com/google/analytics/tracking/android/Hit:getHitId	()J
    //   436: invokestatic 278	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   439: aastore
    //   440: invokestatic 58	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   443: invokestatic 227	com/google/analytics/tracking/android/Log:w	(Ljava/lang/String;)V
    //   446: goto -70 -> 376
    //   449: astore 5
    //   451: aload 4
    //   453: astore_3
    //   454: new 187	java/lang/StringBuilder
    //   457: dup
    //   458: invokespecial 188	java/lang/StringBuilder:<init>	()V
    //   461: ldc_w 515
    //   464: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   467: aload 5
    //   469: invokevirtual 482	android/database/sqlite/SQLiteException:getMessage	()Ljava/lang/String;
    //   472: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   475: invokevirtual 208	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   478: invokestatic 227	com/google/analytics/tracking/android/Log:w	(Ljava/lang/String;)V
    //   481: aload 4
    //   483: astore_3
    //   484: new 161	java/util/ArrayList
    //   487: dup
    //   488: invokespecial 460	java/util/ArrayList:<init>	()V
    //   491: astore 5
    //   493: aload 4
    //   495: astore_3
    //   496: aload 6
    //   498: invokeinterface 516 1 0
    //   503: astore 6
    //   505: iconst_0
    //   506: istore_1
    //   507: aload 4
    //   509: astore_3
    //   510: aload 6
    //   512: invokeinterface 131 1 0
    //   517: istore_2
    //   518: iload_2
    //   519: ifne +21 -> 540
    //   522: aload 4
    //   524: ifnonnull +85 -> 609
    //   527: aload 5
    //   529: areturn
    //   530: aload 4
    //   532: invokeinterface 457 1 0
    //   537: aload 6
    //   539: areturn
    //   540: aload 4
    //   542: astore_3
    //   543: aload 6
    //   545: invokeinterface 135 1 0
    //   550: checkcast 345	com/google/analytics/tracking/android/Hit
    //   553: astore 7
    //   555: aload 4
    //   557: astore_3
    //   558: aload 7
    //   560: invokevirtual 519	com/google/analytics/tracking/android/Hit:getHitParams	()Ljava/lang/String;
    //   563: invokestatic 522	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   566: ifne +34 -> 600
    //   569: aload 4
    //   571: astore_3
    //   572: aload 5
    //   574: aload 7
    //   576: invokeinterface 213 2 0
    //   581: pop
    //   582: goto -75 -> 507
    //   585: astore 5
    //   587: aload_3
    //   588: astore 4
    //   590: aload 5
    //   592: astore_3
    //   593: aload 4
    //   595: ifnonnull +24 -> 619
    //   598: aload_3
    //   599: athrow
    //   600: iload_1
    //   601: ifne -79 -> 522
    //   604: iconst_1
    //   605: istore_1
    //   606: goto -37 -> 569
    //   609: aload 4
    //   611: invokeinterface 457 1 0
    //   616: aload 5
    //   618: areturn
    //   619: aload 4
    //   621: invokeinterface 457 1 0
    //   626: goto -28 -> 598
    //   629: astore 5
    //   631: aload_3
    //   632: astore 4
    //   634: aload 5
    //   636: astore_3
    //   637: goto -44 -> 593
    //   640: astore 5
    //   642: aload_3
    //   643: astore 4
    //   645: goto -194 -> 451
    //   648: astore 5
    //   650: aload_3
    //   651: astore 4
    //   653: aload 5
    //   655: astore_3
    //   656: goto -347 -> 309
    //   659: astore 4
    //   661: goto -396 -> 265
    //   664: astore 4
    //   666: aload 6
    //   668: astore 5
    //   670: goto -405 -> 265
    //   673: iconst_0
    //   674: istore_1
    //   675: goto -282 -> 393
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	678	0	this	PersistentAnalyticsStore
    //   0	678	1	paramInt	int
    //   108	411	2	bool	boolean
    //   1	299	3	localObject1	Object
    //   308	7	3	localObject2	Object
    //   328	328	3	localObject3	Object
    //   26	211	4	localObject4	Object
    //   261	309	4	localSQLiteException1	SQLiteException
    //   588	64	4	localObject5	Object
    //   659	1	4	localSQLiteException2	SQLiteException
    //   664	1	4	localSQLiteException3	SQLiteException
    //   9	297	5	localObject6	Object
    //   449	19	5	localSQLiteException4	SQLiteException
    //   491	82	5	localArrayList	ArrayList
    //   585	32	5	localList	List<Hit>
    //   629	6	5	localObject7	Object
    //   640	1	5	localSQLiteException5	SQLiteException
    //   648	6	5	localObject8	Object
    //   668	1	5	localObject9	Object
    //   43	624	6	localObject10	Object
    //   18	557	7	localObject11	Object
    //   52	30	8	str	String
    // Exception table:
    //   from	to	target	type
    //   28	45	261	android/database/sqlite/SQLiteException
    //   48	54	261	android/database/sqlite/SQLiteException
    //   57	87	261	android/database/sqlite/SQLiteException
    //   28	45	308	finally
    //   48	54	308	finally
    //   57	87	308	finally
    //   90	99	308	finally
    //   102	109	308	finally
    //   205	235	308	finally
    //   238	245	308	finally
    //   179	187	449	android/database/sqlite/SQLiteException
    //   329	351	449	android/database/sqlite/SQLiteException
    //   354	376	449	android/database/sqlite/SQLiteException
    //   379	389	449	android/database/sqlite/SQLiteException
    //   396	410	449	android/database/sqlite/SQLiteException
    //   413	446	449	android/database/sqlite/SQLiteException
    //   179	187	585	finally
    //   329	351	585	finally
    //   354	376	585	finally
    //   379	389	585	finally
    //   396	410	585	finally
    //   413	446	585	finally
    //   454	481	585	finally
    //   484	493	585	finally
    //   496	505	585	finally
    //   510	518	585	finally
    //   543	555	585	finally
    //   558	569	585	finally
    //   572	582	585	finally
    //   117	176	629	finally
    //   117	176	640	android/database/sqlite/SQLiteException
    //   265	292	648	finally
    //   90	99	659	android/database/sqlite/SQLiteException
    //   102	109	664	android/database/sqlite/SQLiteException
    //   205	235	664	android/database/sqlite/SQLiteException
    //   238	245	664	android/database/sqlite/SQLiteException
  }
  
  public void putHit(Map<String, String> paramMap, long paramLong, String paramString, Collection<Command> paramCollection)
  {
    deleteStaleHits();
    removeOldHitIfFull();
    fillVersionParameter(paramMap, paramCollection);
    writeHitToDatabase(paramMap, paramLong, paramString);
  }
  
  @VisibleForTesting
  public void setClock(Clock paramClock)
  {
    this.mClock = paramClock;
  }
  
  public void setDispatch(boolean paramBoolean)
  {
    if (!paramBoolean) {}
    for (Object localObject = new NoopDispatcher();; localObject = new SimpleNetworkDispatcher(new DefaultHttpClient(), this.mContext))
    {
      this.mDispatcher = ((Dispatcher)localObject);
      return;
    }
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
  class AnalyticsDatabaseHelper
    extends SQLiteOpenHelper
  {
    private boolean mBadDatabase;
    private long mLastDatabaseCheckTime = 0L;
    
    AnalyticsDatabaseHelper(Context paramContext, String paramString)
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
      //   88: invokestatic 72	com/google/analytics/tracking/android/Log:w	(Ljava/lang/String;)V
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
      //   0	134	0	this	AnalyticsDatabaseHelper
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
      int j = 0;
      Cursor localCursor = paramSQLiteDatabase.rawQuery("SELECT * FROM hits2 WHERE 0", null);
      HashSet localHashSet = new HashSet();
      do
      {
        try
        {
          String[] arrayOfString = localCursor.getColumnNames();
          i = 0;
          int k = arrayOfString.length;
          if (i >= k)
          {
            localCursor.close();
            if (localHashSet.remove("hit_id")) {
              continue;
            }
          }
          while (!localHashSet.remove("hit_url"))
          {
            throw new SQLiteException("Database column missing");
            localHashSet.add(arrayOfString[i]);
            i += 1;
            break;
          }
        }
        finally
        {
          localCursor.close();
        }
      } while ((!localHashSet.remove("hit_string")) || (!localHashSet.remove("hit_time")));
      if (localHashSet.remove("hit_app_id")) {}
      for (int i = j; localHashSet.isEmpty(); i = 1)
      {
        if (i != 0) {
          break label180;
        }
        return;
      }
      throw new SQLiteException("Database has extra columns");
      label180:
      paramSQLiteDatabase.execSQL("ALTER TABLE hits2 ADD COLUMN hit_app_id");
    }
    
    public SQLiteDatabase getWritableDatabase()
    {
      localObject = null;
      if (!this.mBadDatabase) {}
      for (;;)
      {
        this.mBadDatabase = true;
        this.mLastDatabaseCheckTime = PersistentAnalyticsStore.this.mClock.currentTimeMillis();
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
            PersistentAnalyticsStore.this.mContext.getDatabasePath(PersistentAnalyticsStore.this.mDatabaseName).delete();
            continue;
            localObject = super.getWritableDatabase();
          }
        }
        if (localObject == null) {
          break;
        }
        this.mBadDatabase = false;
        return (SQLiteDatabase)localObject;
        if (this.mLastDatabaseCheckTime + 3600000L <= PersistentAnalyticsStore.this.mClock.currentTimeMillis()) {}
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
        if (tablePresent("hits2", paramSQLiteDatabase))
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


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\PersistentAnalyticsStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */