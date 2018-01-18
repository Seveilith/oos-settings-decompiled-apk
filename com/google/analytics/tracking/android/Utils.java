package com.google.analytics.tracking.android;

import android.text.TextUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class Utils
{
  private static final char[] HEXBYTES = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  
  public static String filterCampaign(String paramString)
  {
    String[] arrayOfString;
    StringBuilder localStringBuilder;
    int i;
    if (!TextUtils.isEmpty(paramString))
    {
      if (paramString.contains("?")) {
        break label116;
      }
      if (paramString.contains("%3D")) {
        break label136;
      }
      if (!paramString.contains("=")) {
        break label149;
      }
      paramString = parseURLParameters(paramString);
      arrayOfString = new String[9];
      arrayOfString[0] = "dclid";
      arrayOfString[1] = "utm_source";
      arrayOfString[2] = "gclid";
      arrayOfString[3] = "utm_campaign";
      arrayOfString[4] = "utm_medium";
      arrayOfString[5] = "utm_term";
      arrayOfString[6] = "utm_content";
      arrayOfString[7] = "utm_id";
      arrayOfString[8] = "gmob_t";
      localStringBuilder = new StringBuilder();
      i = 0;
    }
    for (;;)
    {
      if (i >= arrayOfString.length)
      {
        return localStringBuilder.toString();
        return null;
        label116:
        arrayOfString = paramString.split("[\\?]");
        if (arrayOfString.length <= 1) {
          break;
        }
        paramString = arrayOfString[1];
        break;
        try
        {
          label136:
          paramString = URLDecoder.decode(paramString, "UTF-8");
        }
        catch (UnsupportedEncodingException paramString)
        {
          return null;
        }
        label149:
        return null;
      }
      if (!TextUtils.isEmpty((CharSequence)paramString.get(arrayOfString[i]))) {
        break label176;
      }
      i += 1;
    }
    label176:
    if (localStringBuilder.length() <= 0) {}
    for (;;)
    {
      localStringBuilder.append(arrayOfString[i]).append("=").append((String)paramString.get(arrayOfString[i]));
      break;
      localStringBuilder.append("&");
    }
  }
  
  static int fromHexDigit(char paramChar)
  {
    paramChar -= '0';
    if (paramChar <= '\t') {
      return paramChar;
    }
    return paramChar - '\007';
  }
  
  static String getLanguage(Locale paramLocale)
  {
    StringBuilder localStringBuilder;
    if (paramLocale != null)
    {
      if (TextUtils.isEmpty(paramLocale.getLanguage())) {
        break label51;
      }
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramLocale.getLanguage().toLowerCase());
      if (!TextUtils.isEmpty(paramLocale.getCountry())) {
        break label53;
      }
    }
    for (;;)
    {
      return localStringBuilder.toString();
      return null;
      label51:
      return null;
      label53:
      localStringBuilder.append("-").append(paramLocale.getCountry().toLowerCase());
    }
  }
  
  static byte[] hexDecode(String paramString)
  {
    byte[] arrayOfByte = new byte[paramString.length() / 2];
    int i = 0;
    for (;;)
    {
      if (i >= arrayOfByte.length) {
        return arrayOfByte;
      }
      arrayOfByte[i] = ((byte)(byte)(fromHexDigit(paramString.charAt(i * 2)) << 4 | fromHexDigit(paramString.charAt(i * 2 + 1))));
      i += 1;
    }
  }
  
  static String hexEncode(byte[] paramArrayOfByte)
  {
    char[] arrayOfChar = new char[paramArrayOfByte.length * 2];
    int i = 0;
    for (;;)
    {
      if (i >= paramArrayOfByte.length) {
        return new String(arrayOfChar);
      }
      int j = paramArrayOfByte[i] & 0xFF;
      arrayOfChar[(i * 2)] = ((char)HEXBYTES[(j >> 4)]);
      arrayOfChar[(i * 2 + 1)] = ((char)HEXBYTES[(j & 0xF)]);
      i += 1;
    }
  }
  
  public static Map<String, String> parseURLParameters(String paramString)
  {
    HashMap localHashMap = new HashMap();
    paramString = paramString.split("&");
    int j = paramString.length;
    int i = 0;
    if (i >= j) {
      return localHashMap;
    }
    String[] arrayOfString = paramString[i].split("=");
    if (arrayOfString.length <= 1) {
      if (arrayOfString.length == 1) {
        break label76;
      }
    }
    for (;;)
    {
      i += 1;
      break;
      localHashMap.put(arrayOfString[0], arrayOfString[1]);
      continue;
      label76:
      if (arrayOfString[0].length() != 0) {
        localHashMap.put(arrayOfString[0], null);
      }
    }
  }
  
  public static void putIfAbsent(Map<String, String> paramMap, String paramString1, String paramString2)
  {
    if (paramMap.containsKey(paramString1)) {
      return;
    }
    paramMap.put(paramString1, paramString2);
  }
  
  public static boolean safeParseBoolean(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      return paramBoolean;
    }
    if (paramString.equalsIgnoreCase("true")) {}
    while ((paramString.equalsIgnoreCase("yes")) || (paramString.equalsIgnoreCase("1"))) {
      return true;
    }
    if (paramString.equalsIgnoreCase("false")) {}
    while ((paramString.equalsIgnoreCase("no")) || (paramString.equalsIgnoreCase("0"))) {
      return false;
    }
    return paramBoolean;
  }
  
  public static double safeParseDouble(String paramString)
  {
    return safeParseDouble(paramString, 0.0D);
  }
  
  public static double safeParseDouble(String paramString, double paramDouble)
  {
    if (paramString != null) {}
    try
    {
      double d = Double.parseDouble(paramString);
      return d;
    }
    catch (NumberFormatException paramString) {}
    return paramDouble;
    return paramDouble;
  }
  
  public static long safeParseLong(String paramString)
  {
    if (paramString != null) {}
    try
    {
      long l = Long.parseLong(paramString);
      return l;
    }
    catch (NumberFormatException paramString) {}
    return 0L;
    return 0L;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */