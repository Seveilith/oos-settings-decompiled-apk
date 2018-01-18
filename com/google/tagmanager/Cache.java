package com.google.tagmanager;

abstract interface Cache<K, V>
{
  public abstract V get(K paramK);
  
  public abstract void put(K paramK, V paramV);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\Cache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */