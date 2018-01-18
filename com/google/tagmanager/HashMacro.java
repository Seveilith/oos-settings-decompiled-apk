package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

class HashMacro
  extends FunctionCallImplementation
{
  private static final String ALGORITHM = Key.ALGORITHM.toString();
  private static final String ARG0;
  private static final String DEFAULT_ALGORITHM = "MD5";
  private static final String DEFAULT_INPUT_FORMAT = "text";
  private static final String ID = FunctionType.HASH.toString();
  private static final String INPUT_FORMAT = Key.INPUT_FORMAT.toString();
  
  static
  {
    ARG0 = Key.ARG0.toString();
  }
  
  public HashMacro()
  {
    super(ID, new String[] { ARG0 });
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  private byte[] hash(String paramString, byte[] paramArrayOfByte)
    throws NoSuchAlgorithmException
  {
    paramString = MessageDigest.getInstance(paramString);
    paramString.update(paramArrayOfByte);
    return paramString.digest();
  }
  
  public TypeSystem.Value evaluate(Map<String, TypeSystem.Value> paramMap)
  {
    Object localObject = (TypeSystem.Value)paramMap.get(ARG0);
    if (localObject == null) {}
    while (localObject == Types.getDefaultValue()) {
      return Types.getDefaultValue();
    }
    String str = Types.valueToString((TypeSystem.Value)localObject);
    localObject = (TypeSystem.Value)paramMap.get(ALGORITHM);
    if (localObject != null)
    {
      localObject = Types.valueToString((TypeSystem.Value)localObject);
      paramMap = (TypeSystem.Value)paramMap.get(INPUT_FORMAT);
      if (paramMap == null) {
        break label127;
      }
    }
    label127:
    for (paramMap = Types.valueToString(paramMap);; paramMap = "text")
    {
      if ("text".equals(paramMap)) {
        break label133;
      }
      if ("base16".equals(paramMap)) {
        break label153;
      }
      Log.e("Hash: unknown input format: " + paramMap);
      return Types.getDefaultValue();
      localObject = "MD5";
      break;
    }
    label133:
    for (paramMap = str.getBytes();; paramMap = Base16.decode(str)) {
      try
      {
        paramMap = Types.objectToValue(Base16.encode(hash((String)localObject, paramMap)));
        return paramMap;
      }
      catch (NoSuchAlgorithmException paramMap)
      {
        label153:
        Log.e("Hash: unknown algorithm: " + (String)localObject);
      }
    }
    return Types.getDefaultValue();
  }
  
  public boolean isCacheable()
  {
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\HashMacro.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */