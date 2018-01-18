package com.google.tagmanager;

abstract interface ValueBuilder
{
  public abstract MacroEvaluationInfoBuilder createValueMacroEvaluationInfoExtension();
  
  public abstract ValueBuilder getListItem(int paramInt);
  
  public abstract ValueBuilder getMapKey(int paramInt);
  
  public abstract ValueBuilder getMapValue(int paramInt);
  
  public abstract ValueBuilder getTemplateToken(int paramInt);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\ValueBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */