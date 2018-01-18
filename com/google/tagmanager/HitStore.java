package com.google.tagmanager;

abstract interface HitStore
{
  public abstract void close();
  
  public abstract void dispatch();
  
  public abstract Dispatcher getDispatcher();
  
  public abstract void putHit(long paramLong, String paramString);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\HitStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */