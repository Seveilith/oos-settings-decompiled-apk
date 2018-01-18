package com.google.tagmanager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class SimpleCache<K, V>
  implements Cache<K, V>
{
  private final Map<K, V> mHashMap = new HashMap();
  private final int mMaxSize;
  private final CacheFactory.CacheSizeManager<K, V> mSizeManager;
  private int mTotalSize;
  
  SimpleCache(int paramInt, CacheFactory.CacheSizeManager<K, V> paramCacheSizeManager)
  {
    this.mMaxSize = paramInt;
    this.mSizeManager = paramCacheSizeManager;
  }
  
  public V get(K paramK)
  {
    try
    {
      paramK = this.mHashMap.get(paramK);
      return paramK;
    }
    finally
    {
      paramK = finally;
      throw paramK;
    }
  }
  
  public void put(K paramK, V paramV)
  {
    if (paramK == null) {}
    while (paramV == null) {
      try
      {
        throw new NullPointerException("key == null || value == null");
      }
      finally {}
    }
    this.mTotalSize += this.mSizeManager.sizeOf(paramK, paramV);
    if (this.mTotalSize <= this.mMaxSize) {}
    label166:
    for (;;)
    {
      this.mHashMap.put(paramK, paramV);
      return;
      Iterator localIterator = this.mHashMap.entrySet().iterator();
      for (;;)
      {
        if (!localIterator.hasNext()) {
          break label166;
        }
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        this.mTotalSize -= this.mSizeManager.sizeOf(localEntry.getKey(), localEntry.getValue());
        localIterator.remove();
        int i = this.mTotalSize;
        int j = this.mMaxSize;
        if (i <= j) {
          break;
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\SimpleCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */