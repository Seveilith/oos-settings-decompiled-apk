package com.google.tagmanager;

import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class ValueEscapeUtil
{
  private static ObjectAndStatic<TypeSystem.Value> applyEscaping(ObjectAndStatic<TypeSystem.Value> paramObjectAndStatic, int paramInt)
  {
    if (isValidStringType((TypeSystem.Value)paramObjectAndStatic.getObject())) {}
    switch (paramInt)
    {
    default: 
      Log.e("Unsupported Value Escaping: " + paramInt);
      return paramObjectAndStatic;
      Log.e("Escaping can only be applied to strings.");
      return paramObjectAndStatic;
    }
    return escapeUri(paramObjectAndStatic);
  }
  
  static ObjectAndStatic<TypeSystem.Value> applyEscapings(ObjectAndStatic<TypeSystem.Value> paramObjectAndStatic, int... paramVarArgs)
  {
    int j = paramVarArgs.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return paramObjectAndStatic;
      }
      paramObjectAndStatic = applyEscaping(paramObjectAndStatic, paramVarArgs[i]);
      i += 1;
    }
  }
  
  private static ObjectAndStatic<TypeSystem.Value> escapeUri(ObjectAndStatic<TypeSystem.Value> paramObjectAndStatic)
  {
    try
    {
      ObjectAndStatic localObjectAndStatic = new ObjectAndStatic(Types.objectToValue(urlEncode(Types.valueToString((TypeSystem.Value)paramObjectAndStatic.getObject()))), paramObjectAndStatic.isStatic());
      return localObjectAndStatic;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      Log.e("Escape URI: unsupported encoding", localUnsupportedEncodingException);
    }
    return paramObjectAndStatic;
  }
  
  private static boolean isValidStringType(TypeSystem.Value paramValue)
  {
    return Types.valueToObject(paramValue) instanceof String;
  }
  
  static String urlEncode(String paramString)
    throws UnsupportedEncodingException
  {
    return URLEncoder.encode(paramString, "UTF-8").replaceAll("\\+", "%20");
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\ValueEscapeUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */