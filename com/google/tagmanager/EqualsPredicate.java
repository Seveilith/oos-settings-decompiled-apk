package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class EqualsPredicate
  extends StringPredicate
{
  private static final String ID = FunctionType.EQUALS.toString();
  
  public EqualsPredicate()
  {
    super(ID);
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  protected boolean evaluateString(String paramString1, String paramString2, Map<String, TypeSystem.Value> paramMap)
  {
    return paramString1.equals(paramString2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\EqualsPredicate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */