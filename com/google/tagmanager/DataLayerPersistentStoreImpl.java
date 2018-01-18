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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class DataLayerPersistentStoreImpl
  implements DataLayer.PersistentStore
{
  private static final String CREATE_MAPS_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( '%s' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '%s' STRING NOT NULL, '%s' BLOB NOT NULL, '%s' INTEGER NOT NULL);", new Object[] { "datalayer", "ID", "key", "value", "expires" });
  private static final String DATABASE_NAME = "google_tagmanager.db";
  private static final String EXPIRE_FIELD = "expires";
  private static final String ID_FIELD = "ID";
  private static final String KEY_FIELD = "key";
  private static final String MAPS_TABLE = "datalayer";
  private static final int MAX_NUM_STORED_ITEMS = 2000;
  private static final String VALUE_FIELD = "value";
  private Clock mClock;
  private final Context mContext;
  private DatabaseHelper mDbHelper;
  private final Executor mExecutor;
  private int mMaxNumStoredItems;
  
  public DataLayerPersistentStoreImpl(Context paramContext)
  {
    this(paramContext, new Clock()
    {
      public long currentTimeMillis()
      {
        return System.currentTimeMillis();
      }
    }, "google_tagmanager.db", 2000, Executors.newSingleThreadExecutor());
  }
  
  @VisibleForTesting
  DataLayerPersistentStoreImpl(Context paramContext, Clock paramClock, String paramString, int paramInt, Executor paramExecutor)
  {
    this.mContext = paramContext;
    this.mClock = paramClock;
    this.mMaxNumStoredItems = paramInt;
    this.mExecutor = paramExecutor;
    this.mDbHelper = new DatabaseHelper(this.mContext, paramString);
  }
  
  private void clearKeysWithPrefixSingleThreaded(String paramString)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase("Error opening database for clearKeysWithPrefix.");
    if (localSQLiteDatabase != null) {}
    try
    {
      int i = localSQLiteDatabase.delete("datalayer", "key = ? OR key LIKE ?", new String[] { paramString, paramString + ".%" });
      Log.v("Cleared " + i + " items");
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      Log.w("Error deleting entries with key prefix: " + paramString + " (" + localSQLiteException + ").");
      return;
    }
    finally
    {
      closeDatabaseConnection();
    }
  }
  
  private void closeDatabaseConnection()
  {
    try
    {
      this.mDbHelper.close();
      return;
    }
    catch (SQLiteException localSQLiteException) {}
  }
  
  private void deleteEntries(String[] paramArrayOfString)
  {
    if (paramArrayOfString == null) {}
    while (paramArrayOfString.length == 0) {
      return;
    }
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase("Error opening database for deleteEntries.");
    String str;
    if (localSQLiteDatabase != null) {
      str = String.format("%s in (%s)", new Object[] { "ID", TextUtils.join(",", Collections.nCopies(paramArrayOfString.length, "?")) });
    }
    try
    {
      localSQLiteDatabase.delete("datalayer", str, paramArrayOfString);
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      Log.w("Error deleting entries " + Arrays.toString(paramArrayOfString));
    }
    return;
  }
  
  private void deleteEntriesOlderThan(long paramLong)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase("Error opening database for deleteOlderThan.");
    if (localSQLiteDatabase != null) {}
    try
    {
      int i = localSQLiteDatabase.delete("datalayer", "expires <= ?", new String[] { Long.toString(paramLong) });
      Log.v("Deleted " + i + " expired items");
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      Log.w("Error deleting old entries.");
    }
    return;
  }
  
  private int getNumStoredEntries()
  {
    int i = 0;
    int k = 0;
    int j = 0;
    Object localObject1 = null;
    Object localObject4 = getWritableDatabase("Error opening database for getNumStoredEntries.");
    if (localObject4 != null)
    {
      for (;;)
      {
        Cursor localCursor;
        try
        {
          localCursor = ((SQLiteDatabase)localObject4).rawQuery("SELECT COUNT(*) from datalayer", null);
          localObject1 = localCursor;
          localObject4 = localCursor;
        }
        catch (SQLiteException localSQLiteException)
        {
          long l;
          localSQLiteException = localSQLiteException;
          localObject5 = localObject1;
          Log.w("Error getting numStoredEntries");
          i = k;
          if (localObject1 == null) {
            break label150;
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
            break label145;
          }
          localObject1 = localCursor;
          localObject4 = localCursor;
          l = localCursor.getLong(0);
          i = (int)l;
        }
        finally
        {
          continue;
          label145:
          if (localCursor != null) {
            continue;
          }
        }
      }
      localCursor.close();
      return i;
      label150:
      return i;
    }
    return 0;
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
  
  /* Error */
  private List<KeyAndSerialized> loadSerialized()
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc -7
    //   3: invokespecial 124	com/google/tagmanager/DataLayerPersistentStoreImpl:getWritableDatabase	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteDatabase;
    //   6: astore_3
    //   7: new 251	java/util/ArrayList
    //   10: dup
    //   11: invokespecial 252	java/util/ArrayList:<init>	()V
    //   14: astore_2
    //   15: aload_3
    //   16: ifnull +50 -> 66
    //   19: aload_3
    //   20: ldc 37
    //   22: iconst_2
    //   23: anewarray 58	java/lang/String
    //   26: dup
    //   27: iconst_0
    //   28: ldc 34
    //   30: aastore
    //   31: dup
    //   32: iconst_1
    //   33: ldc 43
    //   35: aastore
    //   36: aconst_null
    //   37: aconst_null
    //   38: aconst_null
    //   39: aconst_null
    //   40: ldc 31
    //   42: aconst_null
    //   43: invokevirtual 256	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   46: astore_3
    //   47: aload_3
    //   48: invokeinterface 259 1 0
    //   53: istore_1
    //   54: iload_1
    //   55: ifne +13 -> 68
    //   58: aload_3
    //   59: invokeinterface 241 1 0
    //   64: aload_2
    //   65: areturn
    //   66: aload_2
    //   67: areturn
    //   68: aload_2
    //   69: new 19	com/google/tagmanager/DataLayerPersistentStoreImpl$KeyAndSerialized
    //   72: dup
    //   73: aload_3
    //   74: iconst_0
    //   75: invokeinterface 263 2 0
    //   80: aload_3
    //   81: iconst_1
    //   82: invokeinterface 267 2 0
    //   87: invokespecial 270	com/google/tagmanager/DataLayerPersistentStoreImpl$KeyAndSerialized:<init>	(Ljava/lang/String;[B)V
    //   90: invokeinterface 276 2 0
    //   95: pop
    //   96: goto -49 -> 47
    //   99: astore_2
    //   100: aload_3
    //   101: invokeinterface 241 1 0
    //   106: aload_2
    //   107: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	108	0	this	DataLayerPersistentStoreImpl
    //   53	2	1	bool	boolean
    //   14	55	2	localArrayList	ArrayList
    //   99	8	2	localObject1	Object
    //   6	95	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   47	54	99	finally
    //   68	96	99	finally
  }
  
  private List<DataLayer.KeyValue> loadSingleThreaded()
  {
    try
    {
      deleteEntriesOlderThan(this.mClock.currentTimeMillis());
      List localList = unserializeValues(loadSerialized());
      return localList;
    }
    finally
    {
      closeDatabaseConnection();
    }
  }
  
  private void makeRoomForEntries(int paramInt)
  {
    paramInt = getNumStoredEntries() - this.mMaxNumStoredItems + paramInt;
    if (paramInt <= 0) {
      return;
    }
    List localList = peekEntryIds(paramInt);
    Log.i("DataLayer store full, deleting " + localList.size() + " entries to make room.");
    deleteEntries((String[])localList.toArray(new String[0]));
  }
  
  /* Error */
  private List<String> peekEntryIds(int paramInt)
  {
    // Byte code:
    //   0: new 251	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 252	java/util/ArrayList:<init>	()V
    //   7: astore 6
    //   9: iload_1
    //   10: ifle +86 -> 96
    //   13: aload_0
    //   14: ldc_w 321
    //   17: invokespecial 124	com/google/tagmanager/DataLayerPersistentStoreImpl:getWritableDatabase	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteDatabase;
    //   20: astore_3
    //   21: aload_3
    //   22: ifnull +83 -> 105
    //   25: ldc_w 323
    //   28: iconst_1
    //   29: anewarray 4	java/lang/Object
    //   32: dup
    //   33: iconst_0
    //   34: ldc 31
    //   36: aastore
    //   37: invokestatic 62	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   40: astore 4
    //   42: iload_1
    //   43: invokestatic 327	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   46: astore 5
    //   48: aload_3
    //   49: ldc 37
    //   51: iconst_1
    //   52: anewarray 58	java/lang/String
    //   55: dup
    //   56: iconst_0
    //   57: ldc 31
    //   59: aastore
    //   60: aconst_null
    //   61: aconst_null
    //   62: aconst_null
    //   63: aconst_null
    //   64: aload 4
    //   66: aload 5
    //   68: invokevirtual 256	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   71: astore 4
    //   73: aload 4
    //   75: astore_3
    //   76: aload 4
    //   78: invokeinterface 236 1 0
    //   83: istore_2
    //   84: iload_2
    //   85: ifne +23 -> 108
    //   88: aload 4
    //   90: ifnonnull +58 -> 148
    //   93: aload 6
    //   95: areturn
    //   96: ldc_w 329
    //   99: invokestatic 171	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   102: aload 6
    //   104: areturn
    //   105: aload 6
    //   107: areturn
    //   108: aload 4
    //   110: astore_3
    //   111: aload 6
    //   113: aload 4
    //   115: iconst_0
    //   116: invokeinterface 240 2 0
    //   121: invokestatic 332	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   124: invokeinterface 276 2 0
    //   129: pop
    //   130: aload 4
    //   132: astore_3
    //   133: aload 4
    //   135: invokeinterface 259 1 0
    //   140: istore_2
    //   141: iload_2
    //   142: ifeq -54 -> 88
    //   145: goto -37 -> 108
    //   148: aload 4
    //   150: invokeinterface 241 1 0
    //   155: aload 6
    //   157: areturn
    //   158: astore 5
    //   160: aconst_null
    //   161: astore 4
    //   163: aload 4
    //   165: astore_3
    //   166: new 128	java/lang/StringBuilder
    //   169: dup
    //   170: invokespecial 129	java/lang/StringBuilder:<init>	()V
    //   173: ldc_w 334
    //   176: invokevirtual 133	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   179: aload 5
    //   181: invokevirtual 337	android/database/sqlite/SQLiteException:getMessage	()Ljava/lang/String;
    //   184: invokevirtual 133	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   187: invokevirtual 138	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   190: invokestatic 171	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   193: aload 4
    //   195: ifnull -102 -> 93
    //   198: aload 4
    //   200: invokeinterface 241 1 0
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
    //   220: invokeinterface 241 1 0
    //   225: goto -9 -> 216
    //   228: astore 4
    //   230: goto -18 -> 212
    //   233: astore 5
    //   235: goto -72 -> 163
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	238	0	this	DataLayerPersistentStoreImpl
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
  private void saveSingleThreaded(List<KeyAndSerialized> paramList, long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 84	com/google/tagmanager/DataLayerPersistentStoreImpl:mClock	Lcom/google/tagmanager/Clock;
    //   6: invokeinterface 284 1 0
    //   11: lstore 4
    //   13: aload_0
    //   14: lload 4
    //   16: invokespecial 286	com/google/tagmanager/DataLayerPersistentStoreImpl:deleteEntriesOlderThan	(J)V
    //   19: aload_0
    //   20: aload_1
    //   21: invokeinterface 306 1 0
    //   26: invokespecial 340	com/google/tagmanager/DataLayerPersistentStoreImpl:makeRoomForEntries	(I)V
    //   29: aload_0
    //   30: aload_1
    //   31: lload 4
    //   33: lload_2
    //   34: ladd
    //   35: invokespecial 343	com/google/tagmanager/DataLayerPersistentStoreImpl:writeEntriesToDatabase	(Ljava/util/List;J)V
    //   38: aload_0
    //   39: invokespecial 159	com/google/tagmanager/DataLayerPersistentStoreImpl:closeDatabaseConnection	()V
    //   42: aload_0
    //   43: monitorexit
    //   44: return
    //   45: astore_1
    //   46: aload_0
    //   47: invokespecial 159	com/google/tagmanager/DataLayerPersistentStoreImpl:closeDatabaseConnection	()V
    //   50: aload_1
    //   51: athrow
    //   52: astore_1
    //   53: aload_0
    //   54: monitorexit
    //   55: aload_1
    //   56: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	57	0	this	DataLayerPersistentStoreImpl
    //   0	57	1	paramList	List<KeyAndSerialized>
    //   0	57	2	paramLong	long
    //   11	21	4	l	long
    // Exception table:
    //   from	to	target	type
    //   2	38	45	finally
    //   38	42	52	finally
    //   46	52	52	finally
  }
  
  /* Error */
  private byte[] serialize(Object paramObject)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: new 350	java/io/ByteArrayOutputStream
    //   5: dup
    //   6: invokespecial 351	java/io/ByteArrayOutputStream:<init>	()V
    //   9: astore 4
    //   11: new 353	java/io/ObjectOutputStream
    //   14: dup
    //   15: aload 4
    //   17: invokespecial 356	java/io/ObjectOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   20: astore_2
    //   21: aload_2
    //   22: aload_1
    //   23: invokevirtual 360	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
    //   26: aload 4
    //   28: invokevirtual 364	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   31: astore_1
    //   32: aload_2
    //   33: ifnonnull +10 -> 43
    //   36: aload 4
    //   38: invokevirtual 365	java/io/ByteArrayOutputStream:close	()V
    //   41: aload_1
    //   42: areturn
    //   43: aload_2
    //   44: invokevirtual 366	java/io/ObjectOutputStream:close	()V
    //   47: goto -11 -> 36
    //   50: astore_2
    //   51: aload_1
    //   52: areturn
    //   53: astore_1
    //   54: aconst_null
    //   55: astore_2
    //   56: aload_2
    //   57: ifnonnull +10 -> 67
    //   60: aload 4
    //   62: invokevirtual 365	java/io/ByteArrayOutputStream:close	()V
    //   65: aconst_null
    //   66: areturn
    //   67: aload_2
    //   68: invokevirtual 366	java/io/ObjectOutputStream:close	()V
    //   71: goto -11 -> 60
    //   74: astore_1
    //   75: aconst_null
    //   76: areturn
    //   77: astore_1
    //   78: aload_3
    //   79: astore_2
    //   80: aload_2
    //   81: ifnonnull +10 -> 91
    //   84: aload 4
    //   86: invokevirtual 365	java/io/ByteArrayOutputStream:close	()V
    //   89: aload_1
    //   90: athrow
    //   91: aload_2
    //   92: invokevirtual 366	java/io/ObjectOutputStream:close	()V
    //   95: goto -11 -> 84
    //   98: astore_2
    //   99: goto -10 -> 89
    //   102: astore_1
    //   103: goto -23 -> 80
    //   106: astore_1
    //   107: goto -51 -> 56
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	110	0	this	DataLayerPersistentStoreImpl
    //   0	110	1	paramObject	Object
    //   20	24	2	localObjectOutputStream	java.io.ObjectOutputStream
    //   50	1	2	localIOException1	java.io.IOException
    //   55	37	2	localObject1	Object
    //   98	1	2	localIOException2	java.io.IOException
    //   1	78	3	localObject2	Object
    //   9	76	4	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    // Exception table:
    //   from	to	target	type
    //   36	41	50	java/io/IOException
    //   43	47	50	java/io/IOException
    //   11	21	53	java/io/IOException
    //   60	65	74	java/io/IOException
    //   67	71	74	java/io/IOException
    //   11	21	77	finally
    //   84	89	98	java/io/IOException
    //   91	95	98	java/io/IOException
    //   21	32	102	finally
    //   21	32	106	java/io/IOException
  }
  
  private List<KeyAndSerialized> serializeValues(List<DataLayer.KeyValue> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    paramList = paramList.iterator();
    for (;;)
    {
      if (!paramList.hasNext()) {
        return localArrayList;
      }
      DataLayer.KeyValue localKeyValue = (DataLayer.KeyValue)paramList.next();
      localArrayList.add(new KeyAndSerialized(localKeyValue.mKey, serialize(localKeyValue.mValue)));
    }
  }
  
  /* Error */
  private Object unserialize(byte[] paramArrayOfByte)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: new 398	java/io/ByteArrayInputStream
    //   5: dup
    //   6: aload_1
    //   7: invokespecial 401	java/io/ByteArrayInputStream:<init>	([B)V
    //   10: astore 4
    //   12: new 403	java/io/ObjectInputStream
    //   15: dup
    //   16: aload 4
    //   18: invokespecial 406	java/io/ObjectInputStream:<init>	(Ljava/io/InputStream;)V
    //   21: astore_1
    //   22: aload_1
    //   23: invokevirtual 409	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   26: astore_2
    //   27: aload_1
    //   28: ifnonnull +10 -> 38
    //   31: aload 4
    //   33: invokevirtual 410	java/io/ByteArrayInputStream:close	()V
    //   36: aload_2
    //   37: areturn
    //   38: aload_1
    //   39: invokevirtual 411	java/io/ObjectInputStream:close	()V
    //   42: goto -11 -> 31
    //   45: astore_1
    //   46: aload_2
    //   47: areturn
    //   48: astore_1
    //   49: aconst_null
    //   50: astore_1
    //   51: aload_1
    //   52: ifnonnull +10 -> 62
    //   55: aload 4
    //   57: invokevirtual 410	java/io/ByteArrayInputStream:close	()V
    //   60: aconst_null
    //   61: areturn
    //   62: aload_1
    //   63: invokevirtual 411	java/io/ObjectInputStream:close	()V
    //   66: goto -11 -> 55
    //   69: astore_1
    //   70: aconst_null
    //   71: areturn
    //   72: astore_1
    //   73: aconst_null
    //   74: astore_1
    //   75: aload_1
    //   76: ifnonnull +10 -> 86
    //   79: aload 4
    //   81: invokevirtual 410	java/io/ByteArrayInputStream:close	()V
    //   84: aconst_null
    //   85: areturn
    //   86: aload_1
    //   87: invokevirtual 411	java/io/ObjectInputStream:close	()V
    //   90: goto -11 -> 79
    //   93: astore_1
    //   94: aconst_null
    //   95: areturn
    //   96: astore_1
    //   97: aload_2
    //   98: ifnonnull +10 -> 108
    //   101: aload 4
    //   103: invokevirtual 410	java/io/ByteArrayInputStream:close	()V
    //   106: aload_1
    //   107: athrow
    //   108: aload_2
    //   109: invokevirtual 411	java/io/ObjectInputStream:close	()V
    //   112: goto -11 -> 101
    //   115: astore_2
    //   116: goto -10 -> 106
    //   119: astore_3
    //   120: aload_1
    //   121: astore_2
    //   122: aload_3
    //   123: astore_1
    //   124: goto -27 -> 97
    //   127: astore_2
    //   128: goto -53 -> 75
    //   131: astore_2
    //   132: goto -81 -> 51
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	135	0	this	DataLayerPersistentStoreImpl
    //   0	135	1	paramArrayOfByte	byte[]
    //   1	108	2	localObject1	Object
    //   115	1	2	localIOException1	java.io.IOException
    //   121	1	2	arrayOfByte	byte[]
    //   127	1	2	localClassNotFoundException	ClassNotFoundException
    //   131	1	2	localIOException2	java.io.IOException
    //   119	4	3	localObject2	Object
    //   10	92	4	localByteArrayInputStream	java.io.ByteArrayInputStream
    // Exception table:
    //   from	to	target	type
    //   31	36	45	java/io/IOException
    //   38	42	45	java/io/IOException
    //   12	22	48	java/io/IOException
    //   55	60	69	java/io/IOException
    //   62	66	69	java/io/IOException
    //   12	22	72	java/lang/ClassNotFoundException
    //   79	84	93	java/io/IOException
    //   86	90	93	java/io/IOException
    //   12	22	96	finally
    //   101	106	115	java/io/IOException
    //   108	112	115	java/io/IOException
    //   22	27	119	finally
    //   22	27	127	java/lang/ClassNotFoundException
    //   22	27	131	java/io/IOException
  }
  
  private List<DataLayer.KeyValue> unserializeValues(List<KeyAndSerialized> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    paramList = paramList.iterator();
    for (;;)
    {
      if (!paramList.hasNext()) {
        return localArrayList;
      }
      KeyAndSerialized localKeyAndSerialized = (KeyAndSerialized)paramList.next();
      localArrayList.add(new DataLayer.KeyValue(localKeyAndSerialized.mKey, unserialize(localKeyAndSerialized.mSerialized)));
    }
  }
  
  private void writeEntriesToDatabase(List<KeyAndSerialized> paramList, long paramLong)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase("Error opening database for writeEntryToDatabase.");
    if (localSQLiteDatabase != null) {
      paramList = paramList.iterator();
    }
    for (;;)
    {
      if (!paramList.hasNext())
      {
        return;
        return;
      }
      KeyAndSerialized localKeyAndSerialized = (KeyAndSerialized)paramList.next();
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("expires", Long.valueOf(paramLong));
      localContentValues.put("key", localKeyAndSerialized.mKey);
      localContentValues.put("value", localKeyAndSerialized.mSerialized);
      localSQLiteDatabase.insert("datalayer", null, localContentValues);
    }
  }
  
  public void clearKeysWithPrefix(final String paramString)
  {
    this.mExecutor.execute(new Runnable()
    {
      public void run()
      {
        DataLayerPersistentStoreImpl.this.clearKeysWithPrefixSingleThreaded(paramString);
      }
    });
  }
  
  public void loadSaved(final DataLayer.PersistentStore.Callback paramCallback)
  {
    this.mExecutor.execute(new Runnable()
    {
      public void run()
      {
        paramCallback.onKeyValuesLoaded(DataLayerPersistentStoreImpl.this.loadSingleThreaded());
      }
    });
  }
  
  public void saveKeyValues(final List<DataLayer.KeyValue> paramList, final long paramLong)
  {
    paramList = serializeValues(paramList);
    this.mExecutor.execute(new Runnable()
    {
      public void run()
      {
        DataLayerPersistentStoreImpl.this.saveSingleThreaded(paramList, paramLong);
      }
    });
  }
  
  @VisibleForTesting
  class DatabaseHelper
    extends SQLiteOpenHelper
  {
    DatabaseHelper(Context paramContext, String paramString)
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
      //   4: ldc 24
      //   6: iconst_1
      //   7: anewarray 26	java/lang/String
      //   10: dup
      //   11: iconst_0
      //   12: ldc 28
      //   14: aastore
      //   15: ldc 30
      //   17: iconst_1
      //   18: anewarray 26	java/lang/String
      //   21: dup
      //   22: iconst_0
      //   23: aload_1
      //   24: aastore
      //   25: aconst_null
      //   26: aconst_null
      //   27: aconst_null
      //   28: invokevirtual 36	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
      //   31: astore_2
      //   32: aload_2
      //   33: astore 4
      //   35: aload 4
      //   37: astore_2
      //   38: aload 4
      //   40: invokeinterface 42 1 0
      //   45: istore_3
      //   46: aload 4
      //   48: ifnonnull +5 -> 53
      //   51: iload_3
      //   52: ireturn
      //   53: aload 4
      //   55: invokeinterface 46 1 0
      //   60: iload_3
      //   61: ireturn
      //   62: astore_2
      //   63: aconst_null
      //   64: astore 4
      //   66: aload 4
      //   68: astore_2
      //   69: new 48	java/lang/StringBuilder
      //   72: dup
      //   73: invokespecial 50	java/lang/StringBuilder:<init>	()V
      //   76: ldc 52
      //   78: invokevirtual 56	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   81: aload_1
      //   82: invokevirtual 56	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   85: invokevirtual 60	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   88: invokestatic 66	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
      //   91: aload 4
      //   93: ifnonnull +5 -> 98
      //   96: iconst_0
      //   97: ireturn
      //   98: aload 4
      //   100: invokeinterface 46 1 0
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
      //   118: invokeinterface 46 1 0
      //   123: goto -8 -> 115
      //   126: astore_1
      //   127: goto -16 -> 111
      //   130: astore_2
      //   131: goto -65 -> 66
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	134	0	this	DatabaseHelper
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
      paramSQLiteDatabase = paramSQLiteDatabase.rawQuery("SELECT * FROM datalayer WHERE 0", null);
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
            if (localHashSet.remove("key")) {
              continue;
            }
          }
          while (!((Set)localObject).remove("value"))
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
      } while ((!((Set)localObject).remove("ID")) || (!((Set)localObject).remove("expires")));
      if (((Set)localObject).isEmpty()) {
        return;
      }
      throw new SQLiteException("Database has extra columns");
    }
    
    public SQLiteDatabase getWritableDatabase()
    {
      Object localObject = null;
      try
      {
        SQLiteDatabase localSQLiteDatabase = super.getWritableDatabase();
        localObject = localSQLiteDatabase;
      }
      catch (SQLiteException localSQLiteException)
      {
        for (;;)
        {
          DataLayerPersistentStoreImpl.this.mContext.getDatabasePath("google_tagmanager.db").delete();
        }
      }
      if (localObject != null) {
        return (SQLiteDatabase)localObject;
      }
      return super.getWritableDatabase();
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
        if (tablePresent("datalayer", paramSQLiteDatabase))
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
  }
  
  private static class KeyAndSerialized
  {
    final String mKey;
    final byte[] mSerialized;
    
    KeyAndSerialized(String paramString, byte[] paramArrayOfByte)
    {
      this.mKey = paramString;
      this.mSerialized = paramArrayOfByte;
    }
    
    public String toString()
    {
      return "KeyAndSerialized: key = " + this.mKey + " serialized hash = " + Arrays.hashCode(this.mSerialized);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\DataLayerPersistentStoreImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */