package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class LessEqualsPredicate
  extends NumberPredicate
{
  private static final String ID = FunctionType.LESS_EQUALS.toString();
  
  public LessEqualsPredicate()
  {
    super(ID);
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  protected boolean evaluateNumber(TypedNumber paramTypedNumber1, TypedNumber paramTypedNumber2, Map<String, TypeSystem.Value> paramMap)
  {
    return paramTypedNumber1.compareTo(paramTypedNumber2) <= 0;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\LessEqualsPredicate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */