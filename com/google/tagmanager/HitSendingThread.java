package com.google.tagmanager;

abstract interface HitSendingThread
{
  public abstract void queueToThread(Runnable paramRunnable);
  
  public abstract void sendHit(String paramString);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\HitSendingThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */