package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class EventMacro
  extends FunctionCallImplementation
{
  private static final String ID = FunctionType.EVENT.toString();
  private final Runtime mRuntime;
  
  public EventMacro(Runtime paramRuntime)
  {
    super(ID, new String[0]);
    this.mRuntime = paramRuntime;
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  public TypeSystem.Value evaluate(Map<String, TypeSystem.Value> paramMap)
  {
    paramMap = this.mRuntime.getCurrentEventName();
    if (paramMap != null) {
      return Types.objectToValue(paramMap);
    }
    return Types.getDefaultValue();
  }
  
  public boolean isCacheable()
  {
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\EventMacro.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */