package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class PlatformMacro
  extends FunctionCallImplementation
{
  private static final String ID = FunctionType.PLATFORM.toString();
  private static final TypeSystem.Value PLATFORM = Types.objectToValue("Android");
  
  public PlatformMacro()
  {
    super(ID, new String[0]);
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  public TypeSystem.Value evaluate(Map<String, TypeSystem.Value> paramMap)
  {
    return PLATFORM;
  }
  
  public boolean isCacheable()
  {
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\PlatformMacro.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */