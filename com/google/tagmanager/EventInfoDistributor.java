package com.google.tagmanager;

abstract interface EventInfoDistributor
{
  public abstract EventInfoBuilder createDataLayerEventEvaluationEventInfo(String paramString);
  
  public abstract EventInfoBuilder createMacroEvalutionEventInfo(String paramString);
  
  public abstract boolean debugMode();
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\EventInfoDistributor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */