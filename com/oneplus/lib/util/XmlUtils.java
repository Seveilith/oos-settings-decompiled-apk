package com.oneplus.lib.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Xml;
import com.oneplus.lib.preference.FastXmlSerializer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class XmlUtils
{
  private static final String STRING_ARRAY_SEPARATOR = ":";
  
  public static final void beginDocument(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException, IOException
  {
    int i;
    do
    {
      i = paramXmlPullParser.next();
    } while ((i != 2) && (i != 1));
    if (i != 2) {
      throw new XmlPullParserException("No start tag found");
    }
    if (!paramXmlPullParser.getName().equals(paramString)) {
      throw new XmlPullParserException("Unexpected start tag: found " + paramXmlPullParser.getName() + ", expected " + paramString);
    }
  }
  
  public static final boolean convertValueToBoolean(CharSequence paramCharSequence, boolean paramBoolean)
  {
    boolean bool = false;
    if (paramCharSequence == null) {
      return paramBoolean;
    }
    if ((!paramCharSequence.equals("1")) && (!paramCharSequence.equals("true")))
    {
      paramBoolean = bool;
      if (!paramCharSequence.equals("TRUE")) {}
    }
    else
    {
      paramBoolean = true;
    }
    return paramBoolean;
  }
  
  public static final int convertValueToInt(CharSequence paramCharSequence, int paramInt)
  {
    if (paramCharSequence == null) {
      return paramInt;
    }
    paramCharSequence = paramCharSequence.toString();
    int k = 1;
    int j = 0;
    int i = paramCharSequence.length();
    paramInt = 10;
    if ('-' == paramCharSequence.charAt(0))
    {
      k = -1;
      j = 1;
    }
    if ('0' == paramCharSequence.charAt(j))
    {
      if (j == i - 1) {
        return 0;
      }
      paramInt = paramCharSequence.charAt(j + 1);
      if ((120 == paramInt) || (88 == paramInt))
      {
        i = j + 2;
        paramInt = 16;
      }
    }
    for (;;)
    {
      return Integer.parseInt(paramCharSequence.substring(i), paramInt) * k;
      i = j + 1;
      paramInt = 8;
      continue;
      i = j;
      if ('#' == paramCharSequence.charAt(j))
      {
        i = j + 1;
        paramInt = 16;
      }
    }
  }
  
  public static final int convertValueToList(CharSequence paramCharSequence, String[] paramArrayOfString, int paramInt)
  {
    if (paramCharSequence != null)
    {
      int i = 0;
      while (i < paramArrayOfString.length)
      {
        if (paramCharSequence.equals(paramArrayOfString[i])) {
          return i;
        }
        i += 1;
      }
    }
    return paramInt;
  }
  
  public static int convertValueToUnsignedInt(String paramString, int paramInt)
  {
    if (paramString == null) {
      return paramInt;
    }
    return parseUnsignedIntAttribute(paramString);
  }
  
  public static final void nextElement(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    int i;
    do
    {
      i = paramXmlPullParser.next();
    } while ((i != 2) && (i != 1));
  }
  
  public static boolean nextElementWithin(XmlPullParser paramXmlPullParser, int paramInt)
    throws IOException, XmlPullParserException
  {
    int i;
    do
    {
      i = paramXmlPullParser.next();
      if ((i == 1) || ((i == 3) && (paramXmlPullParser.getDepth() == paramInt))) {
        return false;
      }
    } while ((i != 2) || (paramXmlPullParser.getDepth() != paramInt + 1));
    return true;
  }
  
  public static int parseUnsignedIntAttribute(CharSequence paramCharSequence)
  {
    paramCharSequence = paramCharSequence.toString();
    int j = 0;
    int k = paramCharSequence.length();
    int i = 10;
    if ('0' == paramCharSequence.charAt(0))
    {
      if (k - 1 == 0) {
        return 0;
      }
      i = paramCharSequence.charAt(1);
      if ((120 == i) || (88 == i))
      {
        j = 2;
        i = 16;
      }
    }
    for (;;)
    {
      return (int)Long.parseLong(paramCharSequence.substring(j), i);
      j = 1;
      i = 8;
      continue;
      if ('#' == paramCharSequence.charAt(0))
      {
        j = 1;
        i = 16;
      }
    }
  }
  
  public static Bitmap readBitmapAttribute(XmlPullParser paramXmlPullParser, String paramString)
  {
    paramXmlPullParser = readByteArrayAttribute(paramXmlPullParser, paramString);
    if (paramXmlPullParser != null) {
      return BitmapFactory.decodeByteArray(paramXmlPullParser, 0, paramXmlPullParser.length);
    }
    return null;
  }
  
  public static boolean readBooleanAttribute(XmlPullParser paramXmlPullParser, String paramString)
  {
    return Boolean.parseBoolean(paramXmlPullParser.getAttributeValue(null, paramString));
  }
  
  public static boolean readBooleanAttribute(XmlPullParser paramXmlPullParser, String paramString, boolean paramBoolean)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramBoolean;
    }
    return Boolean.parseBoolean(paramXmlPullParser);
  }
  
  public static byte[] readByteArrayAttribute(XmlPullParser paramXmlPullParser, String paramString)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser != null) {
      return Base64.decode(paramXmlPullParser, 0);
    }
    return null;
  }
  
  public static float readFloatAttribute(XmlPullParser paramXmlPullParser, String paramString)
    throws IOException
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    try
    {
      float f = Float.parseFloat(paramXmlPullParser);
      return f;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new ProtocolException("problem parsing " + paramString + "=" + paramXmlPullParser + " as long");
    }
  }
  
  public static int readIntAttribute(XmlPullParser paramXmlPullParser, String paramString)
    throws IOException
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    try
    {
      int i = Integer.parseInt(paramXmlPullParser);
      return i;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new ProtocolException("problem parsing " + paramString + "=" + paramXmlPullParser + " as int");
    }
  }
  
  public static int readIntAttribute(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    try
    {
      int i = Integer.parseInt(paramXmlPullParser);
      return i;
    }
    catch (NumberFormatException paramXmlPullParser) {}
    return paramInt;
  }
  
  public static final ArrayList readListXml(InputStream paramInputStream)
    throws XmlPullParserException, IOException
  {
    XmlPullParser localXmlPullParser = Xml.newPullParser();
    localXmlPullParser.setInput(paramInputStream, StandardCharsets.UTF_8.name());
    return (ArrayList)readValueXml(localXmlPullParser, new String[1]);
  }
  
  public static long readLongAttribute(XmlPullParser paramXmlPullParser, String paramString)
    throws IOException
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    try
    {
      long l = Long.parseLong(paramXmlPullParser);
      return l;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new ProtocolException("problem parsing " + paramString + "=" + paramXmlPullParser + " as long");
    }
  }
  
  public static long readLongAttribute(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    try
    {
      long l = Long.parseLong(paramXmlPullParser);
      return l;
    }
    catch (NumberFormatException paramXmlPullParser) {}
    return paramLong;
  }
  
  public static final HashMap<String, ?> readMapXml(InputStream paramInputStream)
    throws XmlPullParserException, IOException
  {
    XmlPullParser localXmlPullParser = Xml.newPullParser();
    localXmlPullParser.setInput(paramInputStream, StandardCharsets.UTF_8.name());
    return (HashMap)readValueXml(localXmlPullParser, new String[1]);
  }
  
  public static final HashSet readSetXml(InputStream paramInputStream)
    throws XmlPullParserException, IOException
  {
    XmlPullParser localXmlPullParser = Xml.newPullParser();
    localXmlPullParser.setInput(paramInputStream, null);
    return (HashSet)readValueXml(localXmlPullParser, new String[1]);
  }
  
  public static String readStringAttribute(XmlPullParser paramXmlPullParser, String paramString)
  {
    return paramXmlPullParser.getAttributeValue(null, paramString);
  }
  
  public static final ArrayMap<String, ?> readThisArrayMapXml(XmlPullParser paramXmlPullParser, String paramString, String[] paramArrayOfString, ReadMapCallback paramReadMapCallback)
    throws XmlPullParserException, IOException
  {
    ArrayMap localArrayMap = new ArrayMap();
    int i = paramXmlPullParser.getEventType();
    if (i == 2)
    {
      localObject = readThisValueXml(paramXmlPullParser, paramArrayOfString, paramReadMapCallback, true);
      localArrayMap.put(paramArrayOfString[0], localObject);
    }
    while (i != 3)
    {
      Object localObject;
      int j = paramXmlPullParser.next();
      i = j;
      if (j != 1) {
        break;
      }
      throw new XmlPullParserException("Document ended before " + paramString + " end tag");
    }
    if (paramXmlPullParser.getName().equals(paramString)) {
      return localArrayMap;
    }
    throw new XmlPullParserException("Expected " + paramString + " end tag at: " + paramXmlPullParser.getName());
  }
  
  public static final boolean[] readThisBooleanArrayXml(XmlPullParser paramXmlPullParser, String paramString, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    int i;
    int j;
    try
    {
      i = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "num"));
      paramXmlPullParser.next();
      paramArrayOfString = new boolean[i];
      i = 0;
      j = paramXmlPullParser.getEventType();
    }
    catch (NumberFormatException paramXmlPullParser)
    {
      try
      {
        do
        {
          paramArrayOfString[i] = Boolean.valueOf(paramXmlPullParser.getAttributeValue(null, "value")).booleanValue();
          k = i;
          m = paramXmlPullParser.next();
          j = m;
          i = k;
        } while (m != 1);
        throw new XmlPullParserException("Document ended before " + paramString + " end tag");
      }
      catch (NumberFormatException paramXmlPullParser)
      {
        throw new XmlPullParserException("Not a number in value attribute in item");
      }
      catch (NullPointerException paramXmlPullParser)
      {
        throw new XmlPullParserException("Need value attribute in item");
      }
      paramXmlPullParser = paramXmlPullParser;
      throw new XmlPullParserException("Not a number in num attribute in string-array");
    }
    catch (NullPointerException paramXmlPullParser)
    {
      throw new XmlPullParserException("Need num attribute in string-array");
    }
    if (j == 2) {
      if (!paramXmlPullParser.getName().equals("item")) {}
    }
    for (;;)
    {
      int m;
      throw new XmlPullParserException("Expected item tag at: " + paramXmlPullParser.getName());
      int k = i;
      if (j == 3)
      {
        if (paramXmlPullParser.getName().equals(paramString)) {
          return paramArrayOfString;
        }
        if (!paramXmlPullParser.getName().equals("item")) {
          break;
        }
        k = i + 1;
      }
    }
    throw new XmlPullParserException("Expected " + paramString + " end tag at: " + paramXmlPullParser.getName());
  }
  
  public static final double[] readThisDoubleArrayXml(XmlPullParser paramXmlPullParser, String paramString, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    int i;
    int j;
    try
    {
      i = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "num"));
      paramXmlPullParser.next();
      paramArrayOfString = new double[i];
      i = 0;
      j = paramXmlPullParser.getEventType();
    }
    catch (NumberFormatException paramXmlPullParser)
    {
      try
      {
        do
        {
          paramArrayOfString[i] = Double.parseDouble(paramXmlPullParser.getAttributeValue(null, "value"));
          k = i;
          m = paramXmlPullParser.next();
          j = m;
          i = k;
        } while (m != 1);
        throw new XmlPullParserException("Document ended before " + paramString + " end tag");
      }
      catch (NumberFormatException paramXmlPullParser)
      {
        throw new XmlPullParserException("Not a number in value attribute in item");
      }
      catch (NullPointerException paramXmlPullParser)
      {
        throw new XmlPullParserException("Need value attribute in item");
      }
      paramXmlPullParser = paramXmlPullParser;
      throw new XmlPullParserException("Not a number in num attribute in double-array");
    }
    catch (NullPointerException paramXmlPullParser)
    {
      throw new XmlPullParserException("Need num attribute in double-array");
    }
    if (j == 2) {
      if (!paramXmlPullParser.getName().equals("item")) {}
    }
    for (;;)
    {
      int m;
      throw new XmlPullParserException("Expected item tag at: " + paramXmlPullParser.getName());
      int k = i;
      if (j == 3)
      {
        if (paramXmlPullParser.getName().equals(paramString)) {
          return paramArrayOfString;
        }
        if (!paramXmlPullParser.getName().equals("item")) {
          break;
        }
        k = i + 1;
      }
    }
    throw new XmlPullParserException("Expected " + paramString + " end tag at: " + paramXmlPullParser.getName());
  }
  
  public static final int[] readThisIntArrayXml(XmlPullParser paramXmlPullParser, String paramString, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    int i;
    int j;
    try
    {
      i = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "num"));
      paramXmlPullParser.next();
      paramArrayOfString = new int[i];
      i = 0;
      j = paramXmlPullParser.getEventType();
    }
    catch (NumberFormatException paramXmlPullParser)
    {
      try
      {
        do
        {
          paramArrayOfString[i] = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value"));
          k = i;
          m = paramXmlPullParser.next();
          j = m;
          i = k;
        } while (m != 1);
        throw new XmlPullParserException("Document ended before " + paramString + " end tag");
      }
      catch (NumberFormatException paramXmlPullParser)
      {
        throw new XmlPullParserException("Not a number in value attribute in item");
      }
      catch (NullPointerException paramXmlPullParser)
      {
        throw new XmlPullParserException("Need value attribute in item");
      }
      paramXmlPullParser = paramXmlPullParser;
      throw new XmlPullParserException("Not a number in num attribute in byte-array");
    }
    catch (NullPointerException paramXmlPullParser)
    {
      throw new XmlPullParserException("Need num attribute in byte-array");
    }
    if (j == 2) {
      if (!paramXmlPullParser.getName().equals("item")) {}
    }
    for (;;)
    {
      int m;
      throw new XmlPullParserException("Expected item tag at: " + paramXmlPullParser.getName());
      int k = i;
      if (j == 3)
      {
        if (paramXmlPullParser.getName().equals(paramString)) {
          return paramArrayOfString;
        }
        if (!paramXmlPullParser.getName().equals("item")) {
          break;
        }
        k = i + 1;
      }
    }
    throw new XmlPullParserException("Expected " + paramString + " end tag at: " + paramXmlPullParser.getName());
  }
  
  public static final ArrayList readThisListXml(XmlPullParser paramXmlPullParser, String paramString, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    return readThisListXml(paramXmlPullParser, paramString, paramArrayOfString, null, false);
  }
  
  private static final ArrayList readThisListXml(XmlPullParser paramXmlPullParser, String paramString, String[] paramArrayOfString, ReadMapCallback paramReadMapCallback, boolean paramBoolean)
    throws XmlPullParserException, IOException
  {
    ArrayList localArrayList = new ArrayList();
    int i = paramXmlPullParser.getEventType();
    if (i == 2) {
      localArrayList.add(readThisValueXml(paramXmlPullParser, paramArrayOfString, paramReadMapCallback, paramBoolean));
    }
    while (i != 3)
    {
      int j = paramXmlPullParser.next();
      i = j;
      if (j != 1) {
        break;
      }
      throw new XmlPullParserException("Document ended before " + paramString + " end tag");
    }
    if (paramXmlPullParser.getName().equals(paramString)) {
      return localArrayList;
    }
    throw new XmlPullParserException("Expected " + paramString + " end tag at: " + paramXmlPullParser.getName());
  }
  
  public static final long[] readThisLongArrayXml(XmlPullParser paramXmlPullParser, String paramString, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    int i;
    int j;
    try
    {
      i = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "num"));
      paramXmlPullParser.next();
      paramArrayOfString = new long[i];
      i = 0;
      j = paramXmlPullParser.getEventType();
    }
    catch (NumberFormatException paramXmlPullParser)
    {
      try
      {
        do
        {
          paramArrayOfString[i] = Long.parseLong(paramXmlPullParser.getAttributeValue(null, "value"));
          k = i;
          m = paramXmlPullParser.next();
          j = m;
          i = k;
        } while (m != 1);
        throw new XmlPullParserException("Document ended before " + paramString + " end tag");
      }
      catch (NumberFormatException paramXmlPullParser)
      {
        throw new XmlPullParserException("Not a number in value attribute in item");
      }
      catch (NullPointerException paramXmlPullParser)
      {
        throw new XmlPullParserException("Need value attribute in item");
      }
      paramXmlPullParser = paramXmlPullParser;
      throw new XmlPullParserException("Not a number in num attribute in long-array");
    }
    catch (NullPointerException paramXmlPullParser)
    {
      throw new XmlPullParserException("Need num attribute in long-array");
    }
    if (j == 2) {
      if (!paramXmlPullParser.getName().equals("item")) {}
    }
    for (;;)
    {
      int m;
      throw new XmlPullParserException("Expected item tag at: " + paramXmlPullParser.getName());
      int k = i;
      if (j == 3)
      {
        if (paramXmlPullParser.getName().equals(paramString)) {
          return paramArrayOfString;
        }
        if (!paramXmlPullParser.getName().equals("item")) {
          break;
        }
        k = i + 1;
      }
    }
    throw new XmlPullParserException("Expected " + paramString + " end tag at: " + paramXmlPullParser.getName());
  }
  
  public static final HashMap<String, ?> readThisMapXml(XmlPullParser paramXmlPullParser, String paramString, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    return readThisMapXml(paramXmlPullParser, paramString, paramArrayOfString, null);
  }
  
  public static final HashMap<String, ?> readThisMapXml(XmlPullParser paramXmlPullParser, String paramString, String[] paramArrayOfString, ReadMapCallback paramReadMapCallback)
    throws XmlPullParserException, IOException
  {
    HashMap localHashMap = new HashMap();
    int i = paramXmlPullParser.getEventType();
    if (i == 2)
    {
      localObject = readThisValueXml(paramXmlPullParser, paramArrayOfString, paramReadMapCallback, false);
      localHashMap.put(paramArrayOfString[0], localObject);
    }
    while (i != 3)
    {
      Object localObject;
      int j = paramXmlPullParser.next();
      i = j;
      if (j != 1) {
        break;
      }
      throw new XmlPullParserException("Document ended before " + paramString + " end tag");
    }
    if (paramXmlPullParser.getName().equals(paramString)) {
      return localHashMap;
    }
    throw new XmlPullParserException("Expected " + paramString + " end tag at: " + paramXmlPullParser.getName());
  }
  
  private static final Object readThisPrimitiveValueXml(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException, IOException
  {
    try
    {
      if (paramString.equals("int")) {
        return Integer.valueOf(Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value")));
      }
      if (paramString.equals("long")) {
        return Long.valueOf(paramXmlPullParser.getAttributeValue(null, "value"));
      }
      if (paramString.equals("float")) {
        return new Float(paramXmlPullParser.getAttributeValue(null, "value"));
      }
      if (paramString.equals("double")) {
        return new Double(paramXmlPullParser.getAttributeValue(null, "value"));
      }
      if (paramString.equals("boolean"))
      {
        paramXmlPullParser = Boolean.valueOf(paramXmlPullParser.getAttributeValue(null, "value"));
        return paramXmlPullParser;
      }
      return null;
    }
    catch (NumberFormatException paramXmlPullParser)
    {
      throw new XmlPullParserException("Not a number in value attribute in <" + paramString + ">");
    }
    catch (NullPointerException paramXmlPullParser)
    {
      throw new XmlPullParserException("Need value attribute in <" + paramString + ">");
    }
  }
  
  public static final HashSet readThisSetXml(XmlPullParser paramXmlPullParser, String paramString, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    return readThisSetXml(paramXmlPullParser, paramString, paramArrayOfString, null, false);
  }
  
  private static final HashSet readThisSetXml(XmlPullParser paramXmlPullParser, String paramString, String[] paramArrayOfString, ReadMapCallback paramReadMapCallback, boolean paramBoolean)
    throws XmlPullParserException, IOException
  {
    HashSet localHashSet = new HashSet();
    int i = paramXmlPullParser.getEventType();
    if (i == 2) {
      localHashSet.add(readThisValueXml(paramXmlPullParser, paramArrayOfString, paramReadMapCallback, paramBoolean));
    }
    while (i != 3)
    {
      int j = paramXmlPullParser.next();
      i = j;
      if (j != 1) {
        break;
      }
      throw new XmlPullParserException("Document ended before " + paramString + " end tag");
    }
    if (paramXmlPullParser.getName().equals(paramString)) {
      return localHashSet;
    }
    throw new XmlPullParserException("Expected " + paramString + " end tag at: " + paramXmlPullParser.getName());
  }
  
  public static final String[] readThisStringArrayXml(XmlPullParser paramXmlPullParser, String paramString, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    int i;
    int j;
    try
    {
      i = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "num"));
      paramXmlPullParser.next();
      paramArrayOfString = new String[i];
      i = 0;
      j = paramXmlPullParser.getEventType();
    }
    catch (NumberFormatException paramXmlPullParser)
    {
      try
      {
        do
        {
          paramArrayOfString[i] = paramXmlPullParser.getAttributeValue(null, "value");
          k = i;
          m = paramXmlPullParser.next();
          j = m;
          i = k;
        } while (m != 1);
        throw new XmlPullParserException("Document ended before " + paramString + " end tag");
      }
      catch (NumberFormatException paramXmlPullParser)
      {
        throw new XmlPullParserException("Not a number in value attribute in item");
      }
      catch (NullPointerException paramXmlPullParser)
      {
        throw new XmlPullParserException("Need value attribute in item");
      }
      paramXmlPullParser = paramXmlPullParser;
      throw new XmlPullParserException("Not a number in num attribute in string-array");
    }
    catch (NullPointerException paramXmlPullParser)
    {
      throw new XmlPullParserException("Need num attribute in string-array");
    }
    if (j == 2) {
      if (!paramXmlPullParser.getName().equals("item")) {}
    }
    for (;;)
    {
      int m;
      throw new XmlPullParserException("Expected item tag at: " + paramXmlPullParser.getName());
      int k = i;
      if (j == 3)
      {
        if (paramXmlPullParser.getName().equals(paramString)) {
          return paramArrayOfString;
        }
        if (!paramXmlPullParser.getName().equals("item")) {
          break;
        }
        k = i + 1;
      }
    }
    throw new XmlPullParserException("Expected " + paramString + " end tag at: " + paramXmlPullParser.getName());
  }
  
  private static final Object readThisValueXml(XmlPullParser paramXmlPullParser, String[] paramArrayOfString, ReadMapCallback paramReadMapCallback, boolean paramBoolean)
    throws XmlPullParserException, IOException
  {
    String str1 = paramXmlPullParser.getAttributeValue(null, "name");
    String str2 = paramXmlPullParser.getName();
    Object localObject1;
    if (str2.equals("null")) {
      localObject1 = null;
    }
    int i;
    label238:
    label619:
    do
    {
      Object localObject2;
      do
      {
        i = paramXmlPullParser.next();
        if (i == 1) {
          break label719;
        }
        if (i != 3) {
          break label619;
        }
        if (!paramXmlPullParser.getName().equals(str2)) {
          break;
        }
        paramArrayOfString[0] = str1;
        return localObject1;
        if (str2.equals("string"))
        {
          paramReadMapCallback = "";
          do
          {
            for (;;)
            {
              i = paramXmlPullParser.next();
              if (i == 1) {
                break label238;
              }
              if (i == 3)
              {
                if (paramXmlPullParser.getName().equals("string"))
                {
                  paramArrayOfString[0] = str1;
                  return paramReadMapCallback;
                }
                throw new XmlPullParserException("Unexpected end tag in <string>: " + paramXmlPullParser.getName());
              }
              if (i != 4) {
                break;
              }
              paramReadMapCallback = paramReadMapCallback + paramXmlPullParser.getText();
            }
          } while (i != 2);
          throw new XmlPullParserException("Unexpected start tag in <string>: " + paramXmlPullParser.getName());
          throw new XmlPullParserException("Unexpected end of document in <string>");
        }
        localObject2 = readThisPrimitiveValueXml(paramXmlPullParser, str2);
        localObject1 = localObject2;
      } while (localObject2 != null);
      if (str2.equals("int-array"))
      {
        paramXmlPullParser = readThisIntArrayXml(paramXmlPullParser, "int-array", paramArrayOfString);
        paramArrayOfString[0] = str1;
        return paramXmlPullParser;
      }
      if (str2.equals("long-array"))
      {
        paramXmlPullParser = readThisLongArrayXml(paramXmlPullParser, "long-array", paramArrayOfString);
        paramArrayOfString[0] = str1;
        return paramXmlPullParser;
      }
      if (str2.equals("double-array"))
      {
        paramXmlPullParser = readThisDoubleArrayXml(paramXmlPullParser, "double-array", paramArrayOfString);
        paramArrayOfString[0] = str1;
        return paramXmlPullParser;
      }
      if (str2.equals("string-array"))
      {
        paramXmlPullParser = readThisStringArrayXml(paramXmlPullParser, "string-array", paramArrayOfString);
        paramArrayOfString[0] = str1;
        return paramXmlPullParser;
      }
      if (str2.equals("boolean-array"))
      {
        paramXmlPullParser = readThisBooleanArrayXml(paramXmlPullParser, "boolean-array", paramArrayOfString);
        paramArrayOfString[0] = str1;
        return paramXmlPullParser;
      }
      if (str2.equals("map"))
      {
        paramXmlPullParser.next();
        if (paramBoolean) {}
        for (paramXmlPullParser = readThisArrayMapXml(paramXmlPullParser, "map", paramArrayOfString, paramReadMapCallback);; paramXmlPullParser = readThisMapXml(paramXmlPullParser, "map", paramArrayOfString, paramReadMapCallback))
        {
          paramArrayOfString[0] = str1;
          return paramXmlPullParser;
        }
      }
      if (str2.equals("list"))
      {
        paramXmlPullParser.next();
        paramXmlPullParser = readThisListXml(paramXmlPullParser, "list", paramArrayOfString, paramReadMapCallback, paramBoolean);
        paramArrayOfString[0] = str1;
        return paramXmlPullParser;
      }
      if (str2.equals("set"))
      {
        paramXmlPullParser.next();
        paramXmlPullParser = readThisSetXml(paramXmlPullParser, "set", paramArrayOfString, paramReadMapCallback, paramBoolean);
        paramArrayOfString[0] = str1;
        return paramXmlPullParser;
      }
      if (paramReadMapCallback != null)
      {
        paramXmlPullParser = paramReadMapCallback.readThisUnknownObjectXml(paramXmlPullParser, str2);
        paramArrayOfString[0] = str1;
        return paramXmlPullParser;
      }
      throw new XmlPullParserException("Unknown tag: " + str2);
      throw new XmlPullParserException("Unexpected end tag in <" + str2 + ">: " + paramXmlPullParser.getName());
      if (i == 4) {
        throw new XmlPullParserException("Unexpected text in <" + str2 + ">: " + paramXmlPullParser.getName());
      }
    } while (i != 2);
    throw new XmlPullParserException("Unexpected start tag in <" + str2 + ">: " + paramXmlPullParser.getName());
    label719:
    throw new XmlPullParserException("Unexpected end of document in <" + str2 + ">");
  }
  
  public static Uri readUriAttribute(XmlPullParser paramXmlPullParser, String paramString)
  {
    Object localObject = null;
    paramString = paramXmlPullParser.getAttributeValue(null, paramString);
    paramXmlPullParser = (XmlPullParser)localObject;
    if (paramString != null) {
      paramXmlPullParser = Uri.parse(paramString);
    }
    return paramXmlPullParser;
  }
  
  public static final Object readValueXml(XmlPullParser paramXmlPullParser, String[] paramArrayOfString)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getEventType();
    int j;
    do
    {
      if (i == 2) {
        return readThisValueXml(paramXmlPullParser, paramArrayOfString, null, false);
      }
      if (i == 3) {
        throw new XmlPullParserException("Unexpected end tag at: " + paramXmlPullParser.getName());
      }
      if (i == 4) {
        throw new XmlPullParserException("Unexpected text: " + paramXmlPullParser.getText());
      }
      j = paramXmlPullParser.next();
      i = j;
    } while (j != 1);
    throw new XmlPullParserException("Unexpected end of document");
  }
  
  public static void skipCurrentTag(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    int j;
    do
    {
      j = paramXmlPullParser.next();
    } while ((j != 1) && ((j != 3) || (paramXmlPullParser.getDepth() > i)));
  }
  
  @Deprecated
  public static void writeBitmapAttribute(XmlSerializer paramXmlSerializer, String paramString, Bitmap paramBitmap)
    throws IOException
  {
    if (paramBitmap != null)
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      paramBitmap.compress(Bitmap.CompressFormat.PNG, 90, localByteArrayOutputStream);
      writeByteArrayAttribute(paramXmlSerializer, paramString, localByteArrayOutputStream.toByteArray());
    }
  }
  
  public static final void writeBooleanArrayXml(boolean[] paramArrayOfBoolean, String paramString, XmlSerializer paramXmlSerializer)
    throws XmlPullParserException, IOException
  {
    if (paramArrayOfBoolean == null)
    {
      paramXmlSerializer.startTag(null, "null");
      paramXmlSerializer.endTag(null, "null");
      return;
    }
    paramXmlSerializer.startTag(null, "boolean-array");
    if (paramString != null) {
      paramXmlSerializer.attribute(null, "name", paramString);
    }
    int j = paramArrayOfBoolean.length;
    paramXmlSerializer.attribute(null, "num", Integer.toString(j));
    int i = 0;
    while (i < j)
    {
      paramXmlSerializer.startTag(null, "item");
      paramXmlSerializer.attribute(null, "value", Boolean.toString(paramArrayOfBoolean[i]));
      paramXmlSerializer.endTag(null, "item");
      i += 1;
    }
    paramXmlSerializer.endTag(null, "boolean-array");
  }
  
  public static void writeBooleanAttribute(XmlSerializer paramXmlSerializer, String paramString, boolean paramBoolean)
    throws IOException
  {
    paramXmlSerializer.attribute(null, paramString, Boolean.toString(paramBoolean));
  }
  
  public static void writeByteArrayAttribute(XmlSerializer paramXmlSerializer, String paramString, byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte != null) {
      paramXmlSerializer.attribute(null, paramString, Base64.encodeToString(paramArrayOfByte, 0));
    }
  }
  
  public static final void writeByteArrayXml(byte[] paramArrayOfByte, String paramString, XmlSerializer paramXmlSerializer)
    throws XmlPullParserException, IOException
  {
    if (paramArrayOfByte == null)
    {
      paramXmlSerializer.startTag(null, "null");
      paramXmlSerializer.endTag(null, "null");
      return;
    }
    paramXmlSerializer.startTag(null, "byte-array");
    if (paramString != null) {
      paramXmlSerializer.attribute(null, "name", paramString);
    }
    int k = paramArrayOfByte.length;
    paramXmlSerializer.attribute(null, "num", Integer.toString(k));
    paramString = new StringBuilder(paramArrayOfByte.length * 2);
    int i = 0;
    if (i < k)
    {
      int m = paramArrayOfByte[i];
      int j = m >> 4;
      if (j >= 10)
      {
        j = j + 97 - 10;
        label121:
        paramString.append(j);
        j = m & 0xFF;
        if (j < 10) {
          break label177;
        }
        j = j + 97 - 10;
      }
      for (;;)
      {
        paramString.append(j);
        i += 1;
        break;
        j += 48;
        break label121;
        label177:
        j += 48;
      }
    }
    paramXmlSerializer.text(paramString.toString());
    paramXmlSerializer.endTag(null, "byte-array");
  }
  
  public static final void writeDoubleArrayXml(double[] paramArrayOfDouble, String paramString, XmlSerializer paramXmlSerializer)
    throws XmlPullParserException, IOException
  {
    if (paramArrayOfDouble == null)
    {
      paramXmlSerializer.startTag(null, "null");
      paramXmlSerializer.endTag(null, "null");
      return;
    }
    paramXmlSerializer.startTag(null, "double-array");
    if (paramString != null) {
      paramXmlSerializer.attribute(null, "name", paramString);
    }
    int j = paramArrayOfDouble.length;
    paramXmlSerializer.attribute(null, "num", Integer.toString(j));
    int i = 0;
    while (i < j)
    {
      paramXmlSerializer.startTag(null, "item");
      paramXmlSerializer.attribute(null, "value", Double.toString(paramArrayOfDouble[i]));
      paramXmlSerializer.endTag(null, "item");
      i += 1;
    }
    paramXmlSerializer.endTag(null, "double-array");
  }
  
  public static void writeFloatAttribute(XmlSerializer paramXmlSerializer, String paramString, float paramFloat)
    throws IOException
  {
    paramXmlSerializer.attribute(null, paramString, Float.toString(paramFloat));
  }
  
  public static final void writeIntArrayXml(int[] paramArrayOfInt, String paramString, XmlSerializer paramXmlSerializer)
    throws XmlPullParserException, IOException
  {
    if (paramArrayOfInt == null)
    {
      paramXmlSerializer.startTag(null, "null");
      paramXmlSerializer.endTag(null, "null");
      return;
    }
    paramXmlSerializer.startTag(null, "int-array");
    if (paramString != null) {
      paramXmlSerializer.attribute(null, "name", paramString);
    }
    int j = paramArrayOfInt.length;
    paramXmlSerializer.attribute(null, "num", Integer.toString(j));
    int i = 0;
    while (i < j)
    {
      paramXmlSerializer.startTag(null, "item");
      paramXmlSerializer.attribute(null, "value", Integer.toString(paramArrayOfInt[i]));
      paramXmlSerializer.endTag(null, "item");
      i += 1;
    }
    paramXmlSerializer.endTag(null, "int-array");
  }
  
  public static void writeIntAttribute(XmlSerializer paramXmlSerializer, String paramString, int paramInt)
    throws IOException
  {
    paramXmlSerializer.attribute(null, paramString, Integer.toString(paramInt));
  }
  
  public static final void writeListXml(List paramList, OutputStream paramOutputStream)
    throws XmlPullParserException, IOException
  {
    XmlSerializer localXmlSerializer = Xml.newSerializer();
    localXmlSerializer.setOutput(paramOutputStream, StandardCharsets.UTF_8.name());
    localXmlSerializer.startDocument(null, Boolean.valueOf(true));
    localXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
    writeListXml(paramList, null, localXmlSerializer);
    localXmlSerializer.endDocument();
  }
  
  public static final void writeListXml(List paramList, String paramString, XmlSerializer paramXmlSerializer)
    throws XmlPullParserException, IOException
  {
    if (paramList == null)
    {
      paramXmlSerializer.startTag(null, "null");
      paramXmlSerializer.endTag(null, "null");
      return;
    }
    paramXmlSerializer.startTag(null, "list");
    if (paramString != null) {
      paramXmlSerializer.attribute(null, "name", paramString);
    }
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      writeValueXml(paramList.get(i), null, paramXmlSerializer);
      i += 1;
    }
    paramXmlSerializer.endTag(null, "list");
  }
  
  public static final void writeLongArrayXml(long[] paramArrayOfLong, String paramString, XmlSerializer paramXmlSerializer)
    throws XmlPullParserException, IOException
  {
    if (paramArrayOfLong == null)
    {
      paramXmlSerializer.startTag(null, "null");
      paramXmlSerializer.endTag(null, "null");
      return;
    }
    paramXmlSerializer.startTag(null, "long-array");
    if (paramString != null) {
      paramXmlSerializer.attribute(null, "name", paramString);
    }
    int j = paramArrayOfLong.length;
    paramXmlSerializer.attribute(null, "num", Integer.toString(j));
    int i = 0;
    while (i < j)
    {
      paramXmlSerializer.startTag(null, "item");
      paramXmlSerializer.attribute(null, "value", Long.toString(paramArrayOfLong[i]));
      paramXmlSerializer.endTag(null, "item");
      i += 1;
    }
    paramXmlSerializer.endTag(null, "long-array");
  }
  
  public static void writeLongAttribute(XmlSerializer paramXmlSerializer, String paramString, long paramLong)
    throws IOException
  {
    paramXmlSerializer.attribute(null, paramString, Long.toString(paramLong));
  }
  
  public static final void writeMapXml(Map paramMap, OutputStream paramOutputStream)
    throws XmlPullParserException, IOException
  {
    FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
    localFastXmlSerializer.setOutput(paramOutputStream, StandardCharsets.UTF_8.name());
    localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
    localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
    writeMapXml(paramMap, null, localFastXmlSerializer);
    localFastXmlSerializer.endDocument();
  }
  
  public static final void writeMapXml(Map paramMap, String paramString, XmlSerializer paramXmlSerializer)
    throws XmlPullParserException, IOException
  {
    writeMapXml(paramMap, paramString, paramXmlSerializer, null);
  }
  
  public static final void writeMapXml(Map paramMap, String paramString, XmlSerializer paramXmlSerializer, WriteMapCallback paramWriteMapCallback)
    throws XmlPullParserException, IOException
  {
    if (paramMap == null)
    {
      paramXmlSerializer.startTag(null, "null");
      paramXmlSerializer.endTag(null, "null");
      return;
    }
    paramXmlSerializer.startTag(null, "map");
    if (paramString != null) {
      paramXmlSerializer.attribute(null, "name", paramString);
    }
    writeMapXml(paramMap, paramXmlSerializer, paramWriteMapCallback);
    paramXmlSerializer.endTag(null, "map");
  }
  
  public static final void writeMapXml(Map paramMap, XmlSerializer paramXmlSerializer, WriteMapCallback paramWriteMapCallback)
    throws XmlPullParserException, IOException
  {
    if (paramMap == null) {
      return;
    }
    paramMap = paramMap.entrySet().iterator();
    while (paramMap.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramMap.next();
      writeValueXml(localEntry.getValue(), (String)localEntry.getKey(), paramXmlSerializer, paramWriteMapCallback);
    }
  }
  
  public static final void writeSetXml(Set paramSet, String paramString, XmlSerializer paramXmlSerializer)
    throws XmlPullParserException, IOException
  {
    if (paramSet == null)
    {
      paramXmlSerializer.startTag(null, "null");
      paramXmlSerializer.endTag(null, "null");
      return;
    }
    paramXmlSerializer.startTag(null, "set");
    if (paramString != null) {
      paramXmlSerializer.attribute(null, "name", paramString);
    }
    paramSet = paramSet.iterator();
    while (paramSet.hasNext()) {
      writeValueXml(paramSet.next(), null, paramXmlSerializer);
    }
    paramXmlSerializer.endTag(null, "set");
  }
  
  public static final void writeStringArrayXml(String[] paramArrayOfString, String paramString, XmlSerializer paramXmlSerializer)
    throws XmlPullParserException, IOException
  {
    if (paramArrayOfString == null)
    {
      paramXmlSerializer.startTag(null, "null");
      paramXmlSerializer.endTag(null, "null");
      return;
    }
    paramXmlSerializer.startTag(null, "string-array");
    if (paramString != null) {
      paramXmlSerializer.attribute(null, "name", paramString);
    }
    int j = paramArrayOfString.length;
    paramXmlSerializer.attribute(null, "num", Integer.toString(j));
    int i = 0;
    while (i < j)
    {
      paramXmlSerializer.startTag(null, "item");
      paramXmlSerializer.attribute(null, "value", paramArrayOfString[i]);
      paramXmlSerializer.endTag(null, "item");
      i += 1;
    }
    paramXmlSerializer.endTag(null, "string-array");
  }
  
  public static void writeStringAttribute(XmlSerializer paramXmlSerializer, String paramString1, String paramString2)
    throws IOException
  {
    if (paramString2 != null) {
      paramXmlSerializer.attribute(null, paramString1, paramString2);
    }
  }
  
  public static void writeUriAttribute(XmlSerializer paramXmlSerializer, String paramString, Uri paramUri)
    throws IOException
  {
    if (paramUri != null) {
      paramXmlSerializer.attribute(null, paramString, paramUri.toString());
    }
  }
  
  public static final void writeValueXml(Object paramObject, String paramString, XmlSerializer paramXmlSerializer)
    throws XmlPullParserException, IOException
  {
    writeValueXml(paramObject, paramString, paramXmlSerializer, null);
  }
  
  private static final void writeValueXml(Object paramObject, String paramString, XmlSerializer paramXmlSerializer, WriteMapCallback paramWriteMapCallback)
    throws XmlPullParserException, IOException
  {
    if (paramObject == null)
    {
      paramXmlSerializer.startTag(null, "null");
      if (paramString != null) {
        paramXmlSerializer.attribute(null, "name", paramString);
      }
      paramXmlSerializer.endTag(null, "null");
      return;
    }
    if ((paramObject instanceof String))
    {
      paramXmlSerializer.startTag(null, "string");
      if (paramString != null) {
        paramXmlSerializer.attribute(null, "name", paramString);
      }
      paramXmlSerializer.text(paramObject.toString());
      paramXmlSerializer.endTag(null, "string");
      return;
    }
    if ((paramObject instanceof Integer)) {
      paramWriteMapCallback = "int";
    }
    for (;;)
    {
      paramXmlSerializer.startTag(null, paramWriteMapCallback);
      if (paramString != null) {
        paramXmlSerializer.attribute(null, "name", paramString);
      }
      paramXmlSerializer.attribute(null, "value", paramObject.toString());
      paramXmlSerializer.endTag(null, paramWriteMapCallback);
      return;
      if ((paramObject instanceof Long))
      {
        paramWriteMapCallback = "long";
      }
      else if ((paramObject instanceof Float))
      {
        paramWriteMapCallback = "float";
      }
      else if ((paramObject instanceof Double))
      {
        paramWriteMapCallback = "double";
      }
      else
      {
        if (!(paramObject instanceof Boolean)) {
          break;
        }
        paramWriteMapCallback = "boolean";
      }
    }
    if ((paramObject instanceof byte[]))
    {
      writeByteArrayXml((byte[])paramObject, paramString, paramXmlSerializer);
      return;
    }
    if ((paramObject instanceof int[]))
    {
      writeIntArrayXml((int[])paramObject, paramString, paramXmlSerializer);
      return;
    }
    if ((paramObject instanceof long[]))
    {
      writeLongArrayXml((long[])paramObject, paramString, paramXmlSerializer);
      return;
    }
    if ((paramObject instanceof double[]))
    {
      writeDoubleArrayXml((double[])paramObject, paramString, paramXmlSerializer);
      return;
    }
    if ((paramObject instanceof String[]))
    {
      writeStringArrayXml((String[])paramObject, paramString, paramXmlSerializer);
      return;
    }
    if ((paramObject instanceof boolean[]))
    {
      writeBooleanArrayXml((boolean[])paramObject, paramString, paramXmlSerializer);
      return;
    }
    if ((paramObject instanceof Map))
    {
      writeMapXml((Map)paramObject, paramString, paramXmlSerializer);
      return;
    }
    if ((paramObject instanceof List))
    {
      writeListXml((List)paramObject, paramString, paramXmlSerializer);
      return;
    }
    if ((paramObject instanceof Set))
    {
      writeSetXml((Set)paramObject, paramString, paramXmlSerializer);
      return;
    }
    if ((paramObject instanceof CharSequence))
    {
      paramXmlSerializer.startTag(null, "string");
      if (paramString != null) {
        paramXmlSerializer.attribute(null, "name", paramString);
      }
      paramXmlSerializer.text(paramObject.toString());
      paramXmlSerializer.endTag(null, "string");
      return;
    }
    if (paramWriteMapCallback != null)
    {
      paramWriteMapCallback.writeUnknownObject(paramObject, paramString, paramXmlSerializer);
      return;
    }
    throw new RuntimeException("writeValueXml: unable to write value " + paramObject);
  }
  
  public static abstract interface ReadMapCallback
  {
    public abstract Object readThisUnknownObjectXml(XmlPullParser paramXmlPullParser, String paramString)
      throws XmlPullParserException, IOException;
  }
  
  public static abstract interface WriteMapCallback
  {
    public abstract void writeUnknownObject(Object paramObject, String paramString, XmlSerializer paramXmlSerializer)
      throws XmlPullParserException, IOException;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\util\XmlUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */