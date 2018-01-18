package com.google.tagmanager.protobuf.nano;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public final class MessageNanoPrinter
{
  private static final String INDENT = "  ";
  private static final int MAX_STRING_LEN = 200;
  
  private static String deCamelCaseify(String paramString)
  {
    int i = 0;
    StringBuffer localStringBuffer = new StringBuffer();
    if (i >= paramString.length()) {
      return localStringBuffer.toString();
    }
    char c = paramString.charAt(i);
    if (i != 0)
    {
      if (Character.isUpperCase(c)) {
        break label65;
      }
      localStringBuffer.append(c);
    }
    for (;;)
    {
      i += 1;
      break;
      localStringBuffer.append(Character.toLowerCase(c));
      continue;
      label65:
      localStringBuffer.append('_').append(Character.toLowerCase(c));
    }
  }
  
  private static String escapeString(String paramString)
  {
    int j = paramString.length();
    StringBuilder localStringBuilder = new StringBuilder(j);
    int i = 0;
    if (i >= j) {
      return localStringBuilder.toString();
    }
    char c = paramString.charAt(i);
    if (c < ' ') {
      label40:
      localStringBuilder.append(String.format("\\u%04x", new Object[] { Integer.valueOf(c) }));
    }
    for (;;)
    {
      i += 1;
      break;
      if ((c > '~') || (c == '"') || (c == '\'')) {
        break label40;
      }
      localStringBuilder.append(c);
    }
  }
  
  public static <T extends MessageNano> String print(T paramT)
  {
    StringBuffer localStringBuffer;
    if (paramT != null) {
      localStringBuffer = new StringBuffer();
    }
    try
    {
      print(paramT.getClass().getSimpleName(), paramT.getClass(), paramT, new StringBuffer(), localStringBuffer);
      return localStringBuffer.toString();
    }
    catch (IllegalAccessException paramT) {}
    return "null";
    return "Error printing proto: " + paramT.getMessage();
  }
  
  private static void print(String paramString, Class<?> paramClass, Object paramObject, StringBuffer paramStringBuffer1, StringBuffer paramStringBuffer2)
    throws IllegalAccessException
  {
    if (!MessageNano.class.isAssignableFrom(paramClass))
    {
      if (paramObject == null) {
        break label322;
      }
      paramString = deCamelCaseify(paramString);
      paramStringBuffer2.append(paramStringBuffer1).append(paramString).append(": ");
      if ((paramObject instanceof String)) {
        break label323;
      }
      paramStringBuffer2.append(paramObject);
    }
    for (;;)
    {
      paramStringBuffer2.append("\n");
      return;
      int i;
      if (paramObject != null)
      {
        paramStringBuffer2.append(paramStringBuffer1).append(paramString);
        paramStringBuffer1.append("  ");
        paramStringBuffer2.append(" <\n");
        paramString = paramClass.getFields();
        int m = paramString.length;
        i = 0;
        if (i >= m)
        {
          paramStringBuffer1.delete(paramStringBuffer1.length() - "  ".length(), paramStringBuffer1.length());
          paramStringBuffer2.append(paramStringBuffer1).append(">\n");
        }
      }
      else
      {
        return;
      }
      Object localObject = paramString[i];
      int j = ((Field)localObject).getModifiers();
      paramClass = ((Field)localObject).getName();
      if ((j & 0x1) != 1) {}
      Class localClass1;
      for (;;)
      {
        i += 1;
        break;
        if (((j & 0x8) != 8) && (!paramClass.startsWith("_")) && (!paramClass.endsWith("_")))
        {
          localClass1 = ((Field)localObject).getType();
          localObject = ((Field)localObject).get(paramObject);
          if (localClass1.isArray()) {
            break label240;
          }
          print(paramClass, localClass1, localObject, paramStringBuffer1, paramStringBuffer2);
        }
      }
      label240:
      Class localClass2 = localClass1.getComponentType();
      if (localClass2 != Byte.TYPE) {
        if (localObject == null) {
          break label316;
        }
      }
      label316:
      for (j = Array.getLength(localObject);; j = 0)
      {
        int k = 0;
        while (k < j)
        {
          print(paramClass, localClass2, Array.get(localObject, k), paramStringBuffer1, paramStringBuffer2);
          k += 1;
        }
        break;
        print(paramClass, localClass1, localObject, paramStringBuffer1, paramStringBuffer2);
        break;
      }
      label322:
      return;
      label323:
      paramString = sanitizeString((String)paramObject);
      paramStringBuffer2.append("\"").append(paramString).append("\"");
    }
  }
  
  private static String sanitizeString(String paramString)
  {
    String str;
    if (paramString.startsWith("http")) {
      str = paramString;
    }
    for (;;)
    {
      return escapeString(str);
      str = paramString;
      if (paramString.length() > 200) {
        str = paramString.substring(0, 200) + "[...]";
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\protobuf\nano\MessageNanoPrinter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */