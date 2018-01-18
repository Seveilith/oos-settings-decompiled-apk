package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class RandomMacro
  extends FunctionCallImplementation
{
  private static final String ID = FunctionType.RANDOM.toString();
  private static final String MAX = Key.MAX.toString();
  private static final String MIN = Key.MIN.toString();
  
  public RandomMacro()
  {
    super(ID, new String[0]);
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  public TypeSystem.Value evaluate(Map<String, TypeSystem.Value> paramMap)
  {
    Object localObject = (TypeSystem.Value)paramMap.get(MIN);
    paramMap = (TypeSystem.Value)paramMap.get(MAX);
    double d1;
    double d2;
    if (localObject == null)
    {
      d1 = 2.147483647E9D;
      d2 = 0.0D;
    }
    for (;;)
    {
      return Types.objectToValue(Long.valueOf(Math.round((d1 - d2) * Math.random() + d2)));
      if ((localObject == Types.getDefaultValue()) || (paramMap == null) || (paramMap == Types.getDefaultValue())) {
        break;
      }
      localObject = Types.valueToNumber((TypeSystem.Value)localObject);
      paramMap = Types.valueToNumber(paramMap);
      if (localObject == Types.getDefaultNumber()) {}
      while (paramMap == Types.getDefaultNumber())
      {
        d1 = 2.147483647E9D;
        d2 = 0.0D;
        break;
      }
      d2 = ((TypedNumber)localObject).doubleValue();
      d1 = paramMap.doubleValue();
      if (d2 > d1)
      {
        d1 = 2.147483647E9D;
        d2 = 0.0D;
      }
    }
  }
  
  public boolean isCacheable()
  {
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\RandomMacro.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */