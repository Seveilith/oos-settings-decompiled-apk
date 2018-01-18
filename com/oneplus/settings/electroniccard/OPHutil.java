package com.oneplus.settings.electroniccard;

import java.util.HashMap;
import java.util.Map;

public class OPHutil
{
  public static Map<String, Object> $m(Object... paramVarArgs)
  {
    int i = 0;
    HashMap localHashMap = new HashMap();
    if ((paramVarArgs.length < 2) || ((paramVarArgs.length & 0x1) != 0)) {
      return localHashMap;
    }
    while (i < paramVarArgs.length)
    {
      int j = i + 1;
      String str = obj2str(paramVarArgs[i]);
      i = j + 1;
      localHashMap.put(str, paramVarArgs[j]);
    }
    return localHashMap;
  }
  
  public static boolean isBlank(Object paramObject)
  {
    if ((paramObject instanceof CharSequence))
    {
      paramObject = (CharSequence)paramObject;
      int j;
      if (paramObject != null)
      {
        j = ((CharSequence)paramObject).length();
        if (j != 0) {}
      }
      else
      {
        return true;
      }
      int i = 0;
      while (i < j)
      {
        if (!Character.isWhitespace(((CharSequence)paramObject).charAt(i))) {
          return false;
        }
        i += 1;
      }
      return true;
    }
    if (paramObject == null) {
      return true;
    }
    return isBlank(paramObject.toString());
  }
  
  public static String obj2str(Object paramObject)
  {
    String str = null;
    if (paramObject != null) {
      str = paramObject.toString();
    }
    return str;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\electroniccard\OPHutil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */