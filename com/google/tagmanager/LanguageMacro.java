package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Locale;
import java.util.Map;

class LanguageMacro
  extends FunctionCallImplementation
{
  private static final String ID = FunctionType.LANGUAGE.toString();
  
  public LanguageMacro()
  {
    super(ID, new String[0]);
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  public TypeSystem.Value evaluate(Map<String, TypeSystem.Value> paramMap)
  {
    paramMap = Locale.getDefault();
    if (paramMap != null)
    {
      paramMap = paramMap.getLanguage();
      if (paramMap != null) {
        return Types.objectToValue(paramMap.toLowerCase());
      }
    }
    else
    {
      return Types.getDefaultValue();
    }
    return Types.getDefaultValue();
  }
  
  public boolean isCacheable()
  {
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\LanguageMacro.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */