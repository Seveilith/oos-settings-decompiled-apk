package com.google.tagmanager;

import com.google.android.gms.common.util.VisibleForTesting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataLayer
{
  static final String LIFETIME_KEY = "gtm.lifetime";
  static final String[] LIFETIME_KEY_COMPONENTS = "gtm.lifetime".toString().split("\\.");
  private static final Pattern LIFETIME_PATTERN = Pattern.compile("(\\d+)\\s*([smhd]?)");
  static final int MAX_QUEUE_DEPTH = 500;
  public static final Object OBJECT_NOT_PRESENT = new Object();
  private final ConcurrentHashMap<Listener, Integer> mListeners;
  private final Map<Object, Object> mModel;
  private final PersistentStore mPersistentStore;
  private final CountDownLatch mPersistentStoreLoaded;
  private final ReentrantLock mPushLock;
  private final LinkedList<Map<Object, Object>> mUpdateQueue;
  
  @VisibleForTesting
  DataLayer()
  {
    this(new PersistentStore()
    {
      public void clearKeysWithPrefix(String paramAnonymousString) {}
      
      public void loadSaved(DataLayer.PersistentStore.Callback paramAnonymousCallback)
      {
        paramAnonymousCallback.onKeyValuesLoaded(new ArrayList());
      }
      
      public void saveKeyValues(List<DataLayer.KeyValue> paramAnonymousList, long paramAnonymousLong) {}
    });
  }
  
  DataLayer(PersistentStore paramPersistentStore)
  {
    this.mPersistentStore = paramPersistentStore;
    this.mListeners = new ConcurrentHashMap();
    this.mModel = new HashMap();
    this.mPushLock = new ReentrantLock();
    this.mUpdateQueue = new LinkedList();
    this.mPersistentStoreLoaded = new CountDownLatch(1);
    loadSavedMaps();
  }
  
  private List<KeyValue> flattenMap(Map<Object, Object> paramMap)
  {
    ArrayList localArrayList = new ArrayList();
    flattenMapHelper(paramMap, "", localArrayList);
    return localArrayList;
  }
  
  private void flattenMapHelper(Map<Object, Object> paramMap, String paramString, Collection<KeyValue> paramCollection)
  {
    Iterator localIterator = paramMap.entrySet().iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      StringBuilder localStringBuilder = new StringBuilder().append(paramString);
      if (paramString.length() != 0) {}
      for (paramMap = ".";; paramMap = "")
      {
        paramMap = paramMap + localEntry.getKey();
        if ((localEntry.getValue() instanceof Map)) {
          break label132;
        }
        if (paramMap.equals("gtm.lifetime")) {
          break;
        }
        paramCollection.add(new KeyValue(paramMap, localEntry.getValue()));
        break;
      }
      label132:
      flattenMapHelper((Map)localEntry.getValue(), paramMap, paramCollection);
    }
  }
  
  private Object getLifetimeObject(Map<Object, Object> paramMap)
  {
    String[] arrayOfString = LIFETIME_KEY_COMPONENTS;
    int j = arrayOfString.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return paramMap;
      }
      String str = arrayOfString[i];
      if (!(paramMap instanceof Map)) {
        break;
      }
      paramMap = ((Map)paramMap).get(str);
      i += 1;
    }
    return null;
  }
  
  private Long getLifetimeValue(Map<Object, Object> paramMap)
  {
    paramMap = getLifetimeObject(paramMap);
    if (paramMap != null) {
      return parseLifetime(paramMap.toString());
    }
    return null;
  }
  
  public static List<Object> listOf(Object... paramVarArgs)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    for (;;)
    {
      if (i >= paramVarArgs.length) {
        return localArrayList;
      }
      localArrayList.add(paramVarArgs[i]);
      i += 1;
    }
  }
  
  private void loadSavedMaps()
  {
    this.mPersistentStore.loadSaved(new DataLayer.PersistentStore.Callback()
    {
      public void onKeyValuesLoaded(List<DataLayer.KeyValue> paramAnonymousList)
      {
        paramAnonymousList = paramAnonymousList.iterator();
        for (;;)
        {
          if (!paramAnonymousList.hasNext())
          {
            DataLayer.this.mPersistentStoreLoaded.countDown();
            return;
          }
          DataLayer.KeyValue localKeyValue = (DataLayer.KeyValue)paramAnonymousList.next();
          DataLayer.this.pushWithoutWaitingForSaved(DataLayer.this.expandKeyValue(localKeyValue.mKey, localKeyValue.mValue));
        }
      }
    });
  }
  
  public static Map<Object, Object> mapOf(Object... paramVarArgs)
  {
    int i = 0;
    HashMap localHashMap;
    if (paramVarArgs.length % 2 == 0) {
      localHashMap = new HashMap();
    }
    for (;;)
    {
      if (i >= paramVarArgs.length)
      {
        return localHashMap;
        throw new IllegalArgumentException("expected even number of key-value pairs");
      }
      localHashMap.put(paramVarArgs[i], paramVarArgs[(i + 1)]);
      i += 2;
    }
  }
  
  private void notifyListeners(Map<Object, Object> paramMap)
  {
    Iterator localIterator = this.mListeners.keySet().iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      ((Listener)localIterator.next()).changed(paramMap);
    }
  }
  
  @VisibleForTesting
  static Long parseLifetime(String paramString)
  {
    int i = 1;
    Object localObject = LIFETIME_PATTERN.matcher(paramString);
    long l1;
    if (((Matcher)localObject).matches()) {
      l1 = 0L;
    }
    try
    {
      long l2 = Long.parseLong(((Matcher)localObject).group(1));
      l1 = l2;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;)
      {
        Log.w("illegal number in _lifetime value: " + paramString);
        continue;
        i = 0;
      }
      label128:
      localObject = ((Matcher)localObject).group(2);
      if (((String)localObject).length() == 0) {
        break label217;
      }
      switch (((String)localObject).charAt(0))
      {
      default: 
        Log.w("unknown units in _lifetime: " + paramString);
        return null;
        label217:
        return Long.valueOf(l1);
      }
    }
    if (l1 > 0L)
    {
      if (i != 0) {
        break label128;
      }
      Log.i("non-positive _lifetime: " + paramString);
      return null;
      Log.i("unknown _lifetime: " + paramString);
      return null;
    }
    return Long.valueOf(l1 * 1000L);
    return Long.valueOf(l1 * 1000L * 60L);
    return Long.valueOf(l1 * 1000L * 60L * 60L);
    return Long.valueOf(l1 * 1000L * 60L * 60L * 24L);
  }
  
  private void processQueuedUpdates()
  {
    int i = 0;
    do
    {
      Map localMap = (Map)this.mUpdateQueue.poll();
      if (localMap == null) {
        return;
      }
      processUpdate(localMap);
      i += 1;
    } while (i <= 500);
    this.mUpdateQueue.clear();
    throw new RuntimeException("Seems like an infinite loop of pushing to the data layer");
  }
  
  private void processUpdate(Map<Object, Object> paramMap)
  {
    synchronized (this.mModel)
    {
      Iterator localIterator = paramMap.keySet().iterator();
      if (!localIterator.hasNext())
      {
        notifyListeners(paramMap);
        return;
      }
      Object localObject = localIterator.next();
      mergeMap(expandKeyValue(localObject, paramMap.get(localObject)), this.mModel);
    }
  }
  
  /* Error */
  private void pushWithoutWaitingForSaved(Map<Object, Object> paramMap)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 103	com/google/tagmanager/DataLayer:mPushLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: invokevirtual 331	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   7: aload_0
    //   8: getfield 108	com/google/tagmanager/DataLayer:mUpdateQueue	Ljava/util/LinkedList;
    //   11: aload_1
    //   12: invokevirtual 334	java/util/LinkedList:offer	(Ljava/lang/Object;)Z
    //   15: pop
    //   16: aload_0
    //   17: getfield 103	com/google/tagmanager/DataLayer:mPushLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   20: invokevirtual 337	java/util/concurrent/locks/ReentrantLock:getHoldCount	()I
    //   23: iconst_1
    //   24: if_icmpeq +16 -> 40
    //   27: aload_0
    //   28: aload_1
    //   29: invokespecial 340	com/google/tagmanager/DataLayer:savePersistentlyIfNeeded	(Ljava/util/Map;)V
    //   32: aload_0
    //   33: getfield 103	com/google/tagmanager/DataLayer:mPushLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   36: invokevirtual 343	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   39: return
    //   40: aload_0
    //   41: invokespecial 345	com/google/tagmanager/DataLayer:processQueuedUpdates	()V
    //   44: goto -17 -> 27
    //   47: astore_1
    //   48: aload_0
    //   49: getfield 103	com/google/tagmanager/DataLayer:mPushLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   52: invokevirtual 343	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   55: aload_1
    //   56: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	57	0	this	DataLayer
    //   0	57	1	paramMap	Map<Object, Object>
    // Exception table:
    //   from	to	target	type
    //   7	27	47	finally
    //   27	32	47	finally
    //   40	44	47	finally
  }
  
  private void savePersistentlyIfNeeded(Map<Object, Object> paramMap)
  {
    Long localLong = getLifetimeValue(paramMap);
    if (localLong != null)
    {
      paramMap = flattenMap(paramMap);
      paramMap.remove("gtm.lifetime");
      this.mPersistentStore.saveKeyValues(paramMap, localLong.longValue());
      return;
    }
  }
  
  void clearPersistentKeysWithPrefix(String paramString)
  {
    push(paramString, null);
    this.mPersistentStore.clearKeysWithPrefix(paramString);
  }
  
  Map<Object, Object> expandKeyValue(Object paramObject1, Object paramObject2)
  {
    HashMap localHashMap1 = new HashMap();
    String[] arrayOfString = paramObject1.toString().split("\\.");
    int i = 0;
    HashMap localHashMap2;
    for (paramObject1 = localHashMap1;; paramObject1 = localHashMap2)
    {
      if (i >= arrayOfString.length - 1)
      {
        ((Map)paramObject1).put(arrayOfString[(arrayOfString.length - 1)], paramObject2);
        return localHashMap1;
      }
      localHashMap2 = new HashMap();
      ((Map)paramObject1).put(arrayOfString[i], localHashMap2);
      i += 1;
    }
  }
  
  public Object get(String paramString)
  {
    synchronized (this.mModel)
    {
      Map localMap1 = this.mModel;
      String[] arrayOfString = paramString.split("\\.");
      int j = arrayOfString.length;
      paramString = localMap1;
      int i = 0;
      for (;;)
      {
        if (i >= j) {
          return paramString;
        }
        localMap1 = arrayOfString[i];
        if (!(paramString instanceof Map)) {
          break;
        }
        paramString = ((Map)paramString).get(localMap1);
        if (paramString == null) {
          break label83;
        }
        i += 1;
      }
      return null;
      label83:
      return null;
    }
  }
  
  @VisibleForTesting
  void mergeList(List<Object> paramList1, List<Object> paramList2)
  {
    int i;
    for (;;)
    {
      if (paramList2.size() >= paramList1.size())
      {
        i = 0;
        if (i < paramList1.size()) {
          break;
        }
        return;
      }
      paramList2.add(null);
    }
    Object localObject = paramList1.get(i);
    if (!(localObject instanceof List))
    {
      if ((localObject instanceof Map)) {
        break label132;
      }
      if (localObject != OBJECT_NOT_PRESENT) {
        break label185;
      }
    }
    for (;;)
    {
      i += 1;
      break;
      if ((paramList2.get(i) instanceof List)) {}
      for (;;)
      {
        mergeList((List)localObject, (List)paramList2.get(i));
        break;
        paramList2.set(i, new ArrayList());
      }
      label132:
      if ((paramList2.get(i) instanceof Map)) {}
      for (;;)
      {
        mergeMap((Map)localObject, (Map)paramList2.get(i));
        break;
        paramList2.set(i, new HashMap());
      }
      label185:
      paramList2.set(i, localObject);
    }
  }
  
  @VisibleForTesting
  void mergeMap(Map<Object, Object> paramMap1, Map<Object, Object> paramMap2)
  {
    Iterator localIterator = paramMap1.keySet().iterator();
    Object localObject1;
    Object localObject2;
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      localObject1 = localIterator.next();
      localObject2 = paramMap1.get(localObject1);
      if ((localObject2 instanceof List)) {
        break;
      }
      if ((localObject2 instanceof Map)) {
        break label126;
      }
      paramMap2.put(localObject1, localObject2);
    }
    if ((paramMap2.get(localObject1) instanceof List)) {}
    for (;;)
    {
      mergeList((List)localObject2, (List)paramMap2.get(localObject1));
      break;
      paramMap2.put(localObject1, new ArrayList());
    }
    label126:
    if ((paramMap2.get(localObject1) instanceof Map)) {}
    for (;;)
    {
      mergeMap((Map)localObject2, (Map)paramMap2.get(localObject1));
      break;
      paramMap2.put(localObject1, new HashMap());
    }
  }
  
  public void push(Object paramObject1, Object paramObject2)
  {
    push(expandKeyValue(paramObject1, paramObject2));
  }
  
  public void push(Map<Object, Object> paramMap)
  {
    try
    {
      this.mPersistentStoreLoaded.await();
      pushWithoutWaitingForSaved(paramMap);
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;)
      {
        Log.w("DataLayer.push: unexpected InterruptedException");
      }
    }
  }
  
  void registerListener(Listener paramListener)
  {
    this.mListeners.put(paramListener, Integer.valueOf(0));
  }
  
  void unregisterListener(Listener paramListener)
  {
    this.mListeners.remove(paramListener);
  }
  
  static final class KeyValue
  {
    public final String mKey;
    public final Object mValue;
    
    KeyValue(String paramString, Object paramObject)
    {
      this.mKey = paramString;
      this.mValue = paramObject;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof KeyValue))
      {
        paramObject = (KeyValue)paramObject;
        if (this.mKey.equals(((KeyValue)paramObject).mKey)) {
          break label30;
        }
      }
      label30:
      while (!this.mValue.equals(((KeyValue)paramObject).mValue))
      {
        return false;
        return false;
      }
      return true;
    }
    
    public int hashCode()
    {
      return Arrays.hashCode(new Integer[] { Integer.valueOf(this.mKey.hashCode()), Integer.valueOf(this.mValue.hashCode()) });
    }
    
    public String toString()
    {
      return "Key: " + this.mKey + " value: " + this.mValue.toString();
    }
  }
  
  static abstract interface Listener
  {
    public abstract void changed(Map<Object, Object> paramMap);
  }
  
  static abstract interface PersistentStore
  {
    public abstract void clearKeysWithPrefix(String paramString);
    
    public abstract void loadSaved(Callback paramCallback);
    
    public abstract void saveKeyValues(List<DataLayer.KeyValue> paramList, long paramLong);
    
    public static abstract interface Callback
    {
      public abstract void onKeyValuesLoaded(List<DataLayer.KeyValue> paramList);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\DataLayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */