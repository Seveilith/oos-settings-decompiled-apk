package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class ContainerVersionMacro
  extends FunctionCallImplementation
{
  private static final String ID = FunctionType.CONTAINER_VERSION.toString();
  private final Runtime mRuntime;
  
  public ContainerVersionMacro(Runtime paramRuntime)
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
    paramMap = this.mRuntime.getResource().getVersion();
    if (paramMap != null) {
      return Types.objectToValue(paramMap);
    }
    return Types.getDefaultValue();
  }
  
  public boolean isCacheable()
  {
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\ContainerVersionMacro.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */