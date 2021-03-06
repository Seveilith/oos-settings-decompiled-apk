package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

class RegexPredicate
  extends StringPredicate
{
  private static final String ID = FunctionType.REGEX.toString();
  private static final String IGNORE_CASE = Key.IGNORE_CASE.toString();
  
  public RegexPredicate()
  {
    super(ID);
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  public static String getIgnoreCaseKey()
  {
    return IGNORE_CASE;
  }
  
  protected boolean evaluateString(String paramString1, String paramString2, Map<String, TypeSystem.Value> paramMap)
  {
    if (!Types.valueToBoolean((TypeSystem.Value)paramMap.get(IGNORE_CASE)).booleanValue()) {}
    for (int i = 64;; i = 66) {
      try
      {
        boolean bool = Pattern.compile(paramString2, i).matcher(paramString1).find();
        return bool;
      }
      catch (PatternSyntaxException paramString1) {}
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\RegexPredicate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */