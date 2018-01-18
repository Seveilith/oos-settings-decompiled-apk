package com.google.tagmanager;

import android.os.Build;
import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class DeviceNameMacro
  extends FunctionCallImplementation
{
  private static final String ID = FunctionType.DEVICE_NAME.toString();
  
  public DeviceNameMacro()
  {
    super(ID, new String[0]);
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  public TypeSystem.Value evaluate(Map<String, TypeSystem.Value> paramMap)
  {
    String str2 = Build.MANUFACTURER;
    String str1 = Build.MODEL;
    if (str1.startsWith(str2)) {
      paramMap = str1;
    }
    for (;;)
    {
      return Types.objectToValue(paramMap);
      paramMap = str1;
      if (!str2.equals("unknown")) {
        paramMap = str2 + " " + str1;
      }
    }
  }
  
  public boolean isCacheable()
  {
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\DeviceNameMacro.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */