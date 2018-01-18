package com.google.tagmanager;

import java.util.List;

abstract interface Dispatcher
{
  public abstract void close();
  
  public abstract void dispatchHits(List<Hit> paramList);
  
  public abstract boolean okToDispatch();
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\Dispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */