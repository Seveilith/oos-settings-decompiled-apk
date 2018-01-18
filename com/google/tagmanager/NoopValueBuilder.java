package com.google.tagmanager;

class NoopValueBuilder
  implements ValueBuilder
{
  public MacroEvaluationInfoBuilder createValueMacroEvaluationInfoExtension()
  {
    return new NoopMacroEvaluationInfoBuilder();
  }
  
  public ValueBuilder getListItem(int paramInt)
  {
    return new NoopValueBuilder();
  }
  
  public ValueBuilder getMapKey(int paramInt)
  {
    return new NoopValueBuilder();
  }
  
  public ValueBuilder getMapValue(int paramInt)
  {
    return new NoopValueBuilder();
  }
  
  public ValueBuilder getTemplateToken(int paramInt)
  {
    return new NoopValueBuilder();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\NoopValueBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */