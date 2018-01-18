package com.google.tagmanager;

import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

abstract class Predicate
  extends FunctionCallImplementation
{
  private static final String ARG0 = Key.ARG0.toString();
  private static final String ARG1 = Key.ARG1.toString();
  
  public Predicate(String paramString)
  {
    super(paramString, new String[] { ARG0, ARG1 });
  }
  
  public static String getArg0Key()
  {
    return ARG0;
  }
  
  public static String getArg1Key()
  {
    return ARG1;
  }
  
  public TypeSystem.Value evaluate(Map<String, TypeSystem.Value> paramMap)
  {
    Object localObject = paramMap.values().iterator();
    TypeSystem.Value localValue;
    if (!((Iterator)localObject).hasNext())
    {
      localObject = (TypeSystem.Value)paramMap.get(ARG0);
      localValue = (TypeSystem.Value)paramMap.get(ARG1);
      if (localObject != null) {
        break label85;
      }
    }
    label52:
    for (boolean bool = false;; bool = evaluateNoDefaultValues((TypeSystem.Value)localObject, localValue, paramMap))
    {
      return Types.objectToValue(Boolean.valueOf(bool));
      if ((TypeSystem.Value)((Iterator)localObject).next() != Types.getDefaultValue()) {
        break;
      }
      return Types.objectToValue(Boolean.valueOf(false));
      label85:
      if (localValue == null) {
        break label52;
      }
    }
  }
  
  protected abstract boolean evaluateNoDefaultValues(TypeSystem.Value paramValue1, TypeSystem.Value paramValue2, Map<String, TypeSystem.Value> paramMap);
  
  public boolean isCacheable()
  {
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\Predicate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */