package com.google.tagmanager;

import com.google.analytics.containertag.proto.Debug.EventInfo;

abstract interface DebugInformationHandler
{
  public abstract void receiveEventInfo(Debug.EventInfo paramEventInfo);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\DebugInformationHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */