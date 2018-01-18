package com.google.tagmanager;

import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class Types
{
  private static Boolean DEFAULT_BOOLEAN = new Boolean(false);
  private static Double DEFAULT_DOUBLE;
  private static Long DEFAULT_INT64;
  private static List<Object> DEFAULT_LIST = new ArrayList(0);
  private static Map<Object, Object> DEFAULT_MAP = new HashMap();
  private static TypedNumber DEFAULT_NUMBER;
  private static final Object DEFAULT_OBJECT = null;
  private static String DEFAULT_STRING;
  private static TypeSystem.Value DEFAULT_VALUE = objectToValue(DEFAULT_STRING);
  
  static
  {
    DEFAULT_INT64 = new Long(0L);
    DEFAULT_DOUBLE = new Double(0.0D);
    DEFAULT_NUMBER = TypedNumber.numberWithInt64(0L);
    DEFAULT_STRING = new String("");
  }
  
  public static TypeSystem.Value functionIdToValue(String paramString)
  {
    TypeSystem.Value localValue = new TypeSystem.Value();
    localValue.type = 5;
    localValue.functionId = paramString;
    return localValue;
  }
  
  public static Boolean getDefaultBoolean()
  {
    return DEFAULT_BOOLEAN;
  }
  
  public static Double getDefaultDouble()
  {
    return DEFAULT_DOUBLE;
  }
  
  public static Long getDefaultInt64()
  {
    return DEFAULT_INT64;
  }
  
  public static List<Object> getDefaultList()
  {
    return DEFAULT_LIST;
  }
  
  public static Map<Object, Object> getDefaultMap()
  {
    return DEFAULT_MAP;
  }
  
  public static TypedNumber getDefaultNumber()
  {
    return DEFAULT_NUMBER;
  }
  
  public static Object getDefaultObject()
  {
    return DEFAULT_OBJECT;
  }
  
  public static String getDefaultString()
  {
    return DEFAULT_STRING;
  }
  
  public static TypeSystem.Value getDefaultValue()
  {
    return DEFAULT_VALUE;
  }
  
  private static double getDouble(Object paramObject)
  {
    if (!(paramObject instanceof Number))
    {
      Log.e("getDouble received non-Number");
      return 0.0D;
    }
    return ((Number)paramObject).doubleValue();
  }
  
  private static long getInt64(Object paramObject)
  {
    if (!(paramObject instanceof Number))
    {
      Log.e("getInt64 received non-Number");
      return 0L;
    }
    return ((Number)paramObject).longValue();
  }
  
  private static boolean isDoubleableNumber(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject instanceof Double)) {}
    do
    {
      bool = true;
      do
      {
        return bool;
        if ((paramObject instanceof Float)) {
          break;
        }
      } while (!(paramObject instanceof TypedNumber));
    } while (((TypedNumber)paramObject).isDouble());
    return false;
  }
  
  private static boolean isInt64ableNumber(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject instanceof Byte)) {}
    do
    {
      bool = true;
      do
      {
        return bool;
        if (((paramObject instanceof Short)) || ((paramObject instanceof Integer)) || ((paramObject instanceof Long))) {
          break;
        }
      } while (!(paramObject instanceof TypedNumber));
    } while (((TypedNumber)paramObject).isInt64());
    return false;
  }
  
  public static TypeSystem.Value macroReferenceToValue(String paramString, int... paramVarArgs)
  {
    TypeSystem.Value localValue = new TypeSystem.Value();
    localValue.type = 4;
    localValue.macroReference = paramString;
    localValue.containsReferences = true;
    localValue.escaping = ((int[])paramVarArgs.clone());
    return localValue;
  }
  
  public static Boolean objectToBoolean(Object paramObject)
  {
    if (!(paramObject instanceof Boolean)) {
      return parseBoolean(objectToString(paramObject));
    }
    return (Boolean)paramObject;
  }
  
  public static Double objectToDouble(Object paramObject)
  {
    if (!isDoubleableNumber(paramObject)) {
      return parseDouble(objectToString(paramObject));
    }
    return Double.valueOf(getDouble(paramObject));
  }
  
  public static Long objectToInt64(Object paramObject)
  {
    if (!isInt64ableNumber(paramObject)) {
      return parseInt64(objectToString(paramObject));
    }
    return Long.valueOf(getInt64(paramObject));
  }
  
  public static TypedNumber objectToNumber(Object paramObject)
  {
    if (!(paramObject instanceof TypedNumber))
    {
      if (!isInt64ableNumber(paramObject))
      {
        if (isDoubleableNumber(paramObject)) {
          break label42;
        }
        return parseNumber(objectToString(paramObject));
      }
    }
    else {
      return (TypedNumber)paramObject;
    }
    return TypedNumber.numberWithInt64(getInt64(paramObject));
    label42:
    return TypedNumber.numberWithDouble(Double.valueOf(getDouble(paramObject)));
  }
  
  public static String objectToString(Object paramObject)
  {
    if (paramObject != null) {
      return paramObject.toString();
    }
    return DEFAULT_STRING;
  }
  
  public static TypeSystem.Value objectToValue(Object paramObject)
  {
    boolean bool = false;
    Object localObject1 = new TypeSystem.Value();
    if (!(paramObject instanceof TypeSystem.Value))
    {
      if ((paramObject instanceof String)) {
        break label104;
      }
      if ((paramObject instanceof List)) {
        break label124;
      }
      if ((paramObject instanceof Map)) {
        break label240;
      }
      if (isDoubleableNumber(paramObject)) {
        break label448;
      }
      if (isInt64ableNumber(paramObject)) {
        break label464;
      }
      if ((paramObject instanceof Boolean)) {
        break label481;
      }
      localObject1 = new StringBuilder().append("Converting to Value from unknown object type: ");
      if (paramObject == null) {
        break label501;
      }
    }
    label104:
    label124:
    label209:
    label227:
    label240:
    label404:
    label448:
    label464:
    label481:
    label501:
    for (paramObject = paramObject.getClass().toString();; paramObject = "null")
    {
      Log.e((String)paramObject);
      return DEFAULT_VALUE;
      return (TypeSystem.Value)paramObject;
      ((TypeSystem.Value)localObject1).type = 1;
      ((TypeSystem.Value)localObject1).string = ((String)paramObject);
      for (;;)
      {
        ((TypeSystem.Value)localObject1).containsReferences = bool;
        return (TypeSystem.Value)localObject1;
        ((TypeSystem.Value)localObject1).type = 2;
        Object localObject2 = (List)paramObject;
        paramObject = new ArrayList(((List)localObject2).size());
        localObject2 = ((List)localObject2).iterator();
        bool = false;
        if (!((Iterator)localObject2).hasNext())
        {
          ((TypeSystem.Value)localObject1).listItem = ((TypeSystem.Value[])((List)paramObject).toArray(new TypeSystem.Value[0]));
        }
        else
        {
          Object localObject3 = objectToValue(((Iterator)localObject2).next());
          if (localObject3 != DEFAULT_VALUE) {
            if (!bool) {
              break label227;
            }
          }
          for (bool = true;; bool = false)
          {
            ((List)paramObject).add(localObject3);
            break;
            return DEFAULT_VALUE;
            if (((TypeSystem.Value)localObject3).containsReferences) {
              break label209;
            }
          }
          ((TypeSystem.Value)localObject1).type = 3;
          localObject3 = ((Map)paramObject).entrySet();
          paramObject = new ArrayList(((Set)localObject3).size());
          localObject2 = new ArrayList(((Set)localObject3).size());
          localObject3 = ((Set)localObject3).iterator();
          bool = false;
          if (!((Iterator)localObject3).hasNext())
          {
            ((TypeSystem.Value)localObject1).mapKey = ((TypeSystem.Value[])((List)paramObject).toArray(new TypeSystem.Value[0]));
            ((TypeSystem.Value)localObject1).mapValue = ((TypeSystem.Value[])((List)localObject2).toArray(new TypeSystem.Value[0]));
          }
          else
          {
            Object localObject4 = (Map.Entry)((Iterator)localObject3).next();
            TypeSystem.Value localValue = objectToValue(((Map.Entry)localObject4).getKey());
            localObject4 = objectToValue(((Map.Entry)localObject4).getValue());
            if (localValue == DEFAULT_VALUE) {}
            while (localObject4 == DEFAULT_VALUE) {
              return DEFAULT_VALUE;
            }
            if (bool) {}
            for (bool = true;; bool = false)
            {
              ((List)paramObject).add(localValue);
              ((List)localObject2).add(localObject4);
              break;
              if ((localValue.containsReferences) || (((TypeSystem.Value)localObject4).containsReferences)) {
                break label404;
              }
            }
            ((TypeSystem.Value)localObject1).type = 1;
            ((TypeSystem.Value)localObject1).string = paramObject.toString();
            continue;
            ((TypeSystem.Value)localObject1).type = 6;
            ((TypeSystem.Value)localObject1).integer = getInt64(paramObject);
            continue;
            ((TypeSystem.Value)localObject1).type = 8;
            ((TypeSystem.Value)localObject1).boolean_ = ((Boolean)paramObject).booleanValue();
          }
        }
      }
    }
  }
  
  private static Boolean parseBoolean(String paramString)
  {
    if (!"true".equalsIgnoreCase(paramString))
    {
      if (!"false".equalsIgnoreCase(paramString)) {
        return DEFAULT_BOOLEAN;
      }
    }
    else {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
  
  private static Double parseDouble(String paramString)
  {
    paramString = parseNumber(paramString);
    if (paramString != DEFAULT_NUMBER) {
      return Double.valueOf(paramString.doubleValue());
    }
    return DEFAULT_DOUBLE;
  }
  
  private static Long parseInt64(String paramString)
  {
    paramString = parseNumber(paramString);
    if (paramString != DEFAULT_NUMBER) {
      return Long.valueOf(paramString.longValue());
    }
    return DEFAULT_INT64;
  }
  
  private static TypedNumber parseNumber(String paramString)
  {
    try
    {
      TypedNumber localTypedNumber = TypedNumber.numberWithString(paramString);
      return localTypedNumber;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.e("Failed to convert '" + paramString + "' to a number.");
    }
    return DEFAULT_NUMBER;
  }
  
  public static TypeSystem.Value templateToValue(TypeSystem.Value... paramVarArgs)
  {
    TypeSystem.Value localValue = new TypeSystem.Value();
    localValue.type = 7;
    localValue.templateToken = new TypeSystem.Value[paramVarArgs.length];
    int i = 0;
    boolean bool = false;
    if (i >= paramVarArgs.length)
    {
      localValue.containsReferences = bool;
      return localValue;
    }
    localValue.templateToken[i] = paramVarArgs[i];
    if (bool) {}
    label53:
    for (bool = true;; bool = false)
    {
      i += 1;
      break;
      if (paramVarArgs[i].containsReferences) {
        break label53;
      }
    }
  }
  
  public static Boolean valueToBoolean(TypeSystem.Value paramValue)
  {
    return objectToBoolean(valueToObject(paramValue));
  }
  
  public static Double valueToDouble(TypeSystem.Value paramValue)
  {
    return objectToDouble(valueToObject(paramValue));
  }
  
  public static Long valueToInt64(TypeSystem.Value paramValue)
  {
    return objectToInt64(valueToObject(paramValue));
  }
  
  public static TypedNumber valueToNumber(TypeSystem.Value paramValue)
  {
    return objectToNumber(valueToObject(paramValue));
  }
  
  public static Object valueToObject(TypeSystem.Value paramValue)
  {
    int k = 0;
    int j = 0;
    int i = 0;
    if (paramValue != null) {}
    Object localObject1;
    Object localObject2;
    switch (paramValue.type)
    {
    default: 
      Log.e("Failed to convert a value of type: " + paramValue.type);
      return DEFAULT_OBJECT;
      return DEFAULT_OBJECT;
    case 1: 
      return paramValue.string;
    case 2: 
      localObject1 = new ArrayList(paramValue.listItem.length);
      paramValue = paramValue.listItem;
      j = paramValue.length;
      for (;;)
      {
        if (i >= j) {
          return localObject1;
        }
        localObject2 = valueToObject(paramValue[i]);
        if (localObject2 == DEFAULT_OBJECT) {
          break;
        }
        ((ArrayList)localObject1).add(localObject2);
        i += 1;
      }
      return DEFAULT_OBJECT;
    case 3: 
      if (paramValue.mapKey.length == paramValue.mapValue.length)
      {
        localObject1 = new LinkedHashMap(paramValue.mapValue.length);
        i = k;
      }
      for (;;)
      {
        if (i >= paramValue.mapKey.length)
        {
          return localObject1;
          Log.e("Converting an invalid value to object: " + paramValue.toString());
          return DEFAULT_OBJECT;
        }
        localObject2 = valueToObject(paramValue.mapKey[i]);
        Object localObject3 = valueToObject(paramValue.mapValue[i]);
        if (localObject2 == DEFAULT_OBJECT) {}
        while (localObject3 == DEFAULT_OBJECT) {
          return DEFAULT_OBJECT;
        }
        ((Map)localObject1).put(localObject2, localObject3);
        i += 1;
      }
    case 4: 
      Log.e("Trying to convert a macro reference to object");
      return DEFAULT_OBJECT;
    case 5: 
      Log.e("Trying to convert a function id to object");
      return DEFAULT_OBJECT;
    case 6: 
      return Long.valueOf(paramValue.integer);
    case 7: 
      localObject1 = new StringBuffer();
      paramValue = paramValue.templateToken;
      k = paramValue.length;
      i = j;
      for (;;)
      {
        if (i >= k) {
          return ((StringBuffer)localObject1).toString();
        }
        localObject2 = valueToString(paramValue[i]);
        if (localObject2 == DEFAULT_STRING) {
          break;
        }
        ((StringBuffer)localObject1).append((String)localObject2);
        i += 1;
      }
      return DEFAULT_OBJECT;
    }
    return Boolean.valueOf(paramValue.boolean_);
  }
  
  public static String valueToString(TypeSystem.Value paramValue)
  {
    return objectToString(valueToObject(paramValue));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\Types.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */