package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

class RegexGroupMacro
  extends FunctionCallImplementation
{
  private static final String GROUP = Key.GROUP.toString();
  private static final String ID = FunctionType.REGEX_GROUP.toString();
  private static final String IGNORE_CASE;
  private static final String REGEX;
  private static final String TO_MATCH = Key.ARG0.toString();
  
  static
  {
    REGEX = Key.ARG1.toString();
    IGNORE_CASE = Key.IGNORE_CASE.toString();
  }
  
  public RegexGroupMacro()
  {
    super(ID, new String[] { TO_MATCH, REGEX });
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  public TypeSystem.Value evaluate(Map<String, TypeSystem.Value> paramMap)
  {
    TypeSystem.Value localValue2 = (TypeSystem.Value)paramMap.get(TO_MATCH);
    TypeSystem.Value localValue1 = (TypeSystem.Value)paramMap.get(REGEX);
    if (localValue2 == null) {}
    while ((localValue2 == Types.getDefaultValue()) || (localValue1 == null) || (localValue1 == Types.getDefaultValue())) {
      return Types.getDefaultValue();
    }
    int i = 64;
    int j;
    if (!Types.valueToBoolean((TypeSystem.Value)paramMap.get(IGNORE_CASE)).booleanValue())
    {
      paramMap = (TypeSystem.Value)paramMap.get(GROUP);
      if (paramMap != null) {
        break label148;
      }
      j = 1;
    }
    for (;;)
    {
      try
      {
        paramMap = Types.valueToString(localValue2);
        paramMap = Pattern.compile(Types.valueToString(localValue1), i).matcher(paramMap);
        if (!paramMap.find())
        {
          break label210;
          if (paramMap == null) {
            continue;
          }
          paramMap = Types.objectToValue(paramMap);
          return paramMap;
          i = 66;
          break;
          label148:
          paramMap = Types.valueToInt64(paramMap);
          if (paramMap != Types.getDefaultInt64())
          {
            int k = paramMap.intValue();
            j = k;
            if (k >= 0) {
              continue;
            }
            return Types.getDefaultValue();
          }
          return Types.getDefaultValue();
        }
        if (paramMap.groupCount() >= j)
        {
          paramMap = paramMap.group(j);
          continue;
          paramMap = Types.getDefaultValue();
          return paramMap;
        }
      }
      catch (PatternSyntaxException paramMap)
      {
        return Types.getDefaultValue();
      }
      label210:
      paramMap = null;
    }
  }
  
  public boolean isCacheable()
  {
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\RegexGroupMacro.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */