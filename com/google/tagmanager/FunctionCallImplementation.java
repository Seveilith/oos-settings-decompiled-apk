package com.google.tagmanager;

import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

abstract class FunctionCallImplementation
{
  private static final String FUNCTION_KEY = "function";
  private final String mFunctionId;
  private final Set<String> mRequiredKeys;
  
  public FunctionCallImplementation(String paramString, String... paramVarArgs)
  {
    this.mFunctionId = paramString;
    this.mRequiredKeys = new HashSet(paramVarArgs.length);
    int j = paramVarArgs.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      paramString = paramVarArgs[i];
      this.mRequiredKeys.add(paramString);
      i += 1;
    }
  }
  
  public static String getFunctionKey()
  {
    return "function";
  }
  
  public abstract TypeSystem.Value evaluate(Map<String, TypeSystem.Value> paramMap);
  
  public String getInstanceFunctionId()
  {
    return this.mFunctionId;
  }
  
  public Set<String> getRequiredKeys()
  {
    return this.mRequiredKeys;
  }
  
  boolean hasRequiredKeys(Set<String> paramSet)
  {
    return paramSet.containsAll(this.mRequiredKeys);
  }
  
  public abstract boolean isCacheable();
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\FunctionCallImplementation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */