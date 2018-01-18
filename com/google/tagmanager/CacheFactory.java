package com.google.tagmanager;

import android.os.Build.VERSION;
import com.google.android.gms.common.util.VisibleForTesting;

class CacheFactory<K, V>
{
  @VisibleForTesting
  final CacheSizeManager<K, V> mDefaultSizeManager = new CacheSizeManager()
  {
    public int sizeOf(K paramAnonymousK, V paramAnonymousV)
    {
      return 1;
    }
  };
  
  public Cache<K, V> createCache(int paramInt)
  {
    return createCache(paramInt, this.mDefaultSizeManager);
  }
  
  public Cache<K, V> createCache(int paramInt, CacheSizeManager<K, V> paramCacheSizeManager)
  {
    if (paramInt > 0)
    {
      if (getSdkVersion() >= 12) {
        return new LRUCache(paramInt, paramCacheSizeManager);
      }
    }
    else {
      throw new IllegalArgumentException("maxSize <= 0");
    }
    return new SimpleCache(paramInt, paramCacheSizeManager);
  }
  
  @VisibleForTesting
  int getSdkVersion()
  {
    return Build.VERSION.SDK_INT;
  }
  
  public static abstract interface CacheSizeManager<K, V>
  {
    public abstract int sizeOf(K paramK, V paramV);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\CacheFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */