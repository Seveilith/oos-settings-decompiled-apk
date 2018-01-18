package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class EncodeMacro
  extends FunctionCallImplementation
{
  private static final String ARG0;
  private static final String DEFAULT_INPUT_FORMAT = "text";
  private static final String DEFAULT_OUTPUT_FORMAT = "base16";
  private static final String ID = FunctionType.ENCODE.toString();
  private static final String INPUT_FORMAT = Key.INPUT_FORMAT.toString();
  private static final String NO_PADDING;
  private static final String OUTPUT_FORMAT = Key.OUTPUT_FORMAT.toString();
  
  static
  {
    ARG0 = Key.ARG0.toString();
    NO_PADDING = Key.NO_PADDING.toString();
  }
  
  public EncodeMacro()
  {
    super(ID, new String[] { ARG0 });
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  public TypeSystem.Value evaluate(Map<String, TypeSystem.Value> paramMap)
  {
    Object localObject = (TypeSystem.Value)paramMap.get(ARG0);
    if (localObject == null) {}
    while (localObject == Types.getDefaultValue()) {
      return Types.getDefaultValue();
    }
    String str2 = Types.valueToString((TypeSystem.Value)localObject);
    localObject = (TypeSystem.Value)paramMap.get(INPUT_FORMAT);
    String str1;
    if (localObject != null) {
      str1 = Types.valueToString((TypeSystem.Value)localObject);
    }
    label79:
    label110:
    int i;
    for (;;)
    {
      localObject = (TypeSystem.Value)paramMap.get(OUTPUT_FORMAT);
      if (localObject != null)
      {
        localObject = Types.valueToString((TypeSystem.Value)localObject);
        TypeSystem.Value localValue = (TypeSystem.Value)paramMap.get(INPUT_FORMAT);
        paramMap = (TypeSystem.Value)paramMap.get(NO_PADDING);
        if (paramMap != null) {
          break label194;
        }
        i = 0;
      }
      try
      {
        for (;;)
        {
          if ("text".equals(str1)) {
            break label209;
          }
          if ("base16".equals(str1)) {
            break label268;
          }
          if ("base64".equals(str1)) {
            break label277;
          }
          if ("base64url".equals(str1)) {
            break label287;
          }
          Log.e("Encode: unknown input format: " + str1);
          paramMap = Types.getDefaultValue();
          return paramMap;
          str1 = "text";
          break;
          localObject = "base16";
          break label79;
          label194:
          if (!Types.valueToBoolean(paramMap).booleanValue()) {
            break label110;
          }
          i = 1;
        }
        label209:
        paramMap = str2.getBytes();
        while (!"base16".equals(localObject))
        {
          if ("base64".equals(localObject)) {
            break label319;
          }
          if ("base64url".equals(localObject)) {
            break label328;
          }
          Log.e("Encode: unknown output format: " + (String)localObject);
          return Types.getDefaultValue();
          label268:
          paramMap = Base16.decode(str2);
          continue;
          label277:
          paramMap = Base64Encoder.decode(str2, i);
          continue;
          label287:
          paramMap = Base64Encoder.decode(str2, i | 0x2);
        }
        paramMap = Base16.encode(paramMap);
      }
      catch (IllegalArgumentException paramMap)
      {
        Log.e("Encode: invalid input:");
        return Types.getDefaultValue();
      }
    }
    for (;;)
    {
      return Types.objectToValue(paramMap);
      label319:
      paramMap = Base64Encoder.encodeToString(paramMap, i);
      continue;
      label328:
      paramMap = Base64Encoder.encodeToString(paramMap, i | 0x2);
    }
  }
  
  public boolean isCacheable()
  {
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\EncodeMacro.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */