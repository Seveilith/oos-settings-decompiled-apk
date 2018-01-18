package com.android.settingslib.datetime;

import android.content.Context;
import android.icu.text.TimeZoneNames.NameType;
import android.text.BidiFormatter;
import android.text.TextDirectionHeuristics;
import android.text.TextUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class ZoneGetter
{
  public static final String KEY_DISPLAYNAME = "name";
  public static final String KEY_GMT = "gmt";
  public static final String KEY_ID = "id";
  public static final String KEY_OFFSET = "offset";
  private static final String TAG = "ZoneGetter";
  private static final String XMLTAG_TIMEZONE = "timezone";
  
  private static Map<String, Object> createDisplayEntry(TimeZone paramTimeZone, String paramString1, String paramString2, int paramInt)
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("id", paramTimeZone.getID());
    localHashMap.put("name", paramString2);
    localHashMap.put("gmt", paramString1);
    localHashMap.put("offset", Integer.valueOf(paramInt));
    return localHashMap;
  }
  
  private static String getGmtOffsetString(Locale paramLocale, TimeZone paramTimeZone, Date paramDate)
  {
    int i = 1;
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("ZZZZ");
    localSimpleDateFormat.setTimeZone(paramTimeZone);
    paramTimeZone = localSimpleDateFormat.format(paramDate);
    paramDate = BidiFormatter.getInstance();
    if (TextUtils.getLayoutDirectionFromLocale(paramLocale) == 1) {
      if (i == 0) {
        break label58;
      }
    }
    label58:
    for (paramLocale = TextDirectionHeuristics.RTL;; paramLocale = TextDirectionHeuristics.LTR)
    {
      return paramDate.unicodeWrap(paramTimeZone, paramLocale);
      i = 0;
      break;
    }
  }
  
  public static String getTimeZoneOffsetAndName(TimeZone paramTimeZone, Date paramDate)
  {
    Locale localLocale = Locale.getDefault();
    String str = getGmtOffsetString(localLocale, paramTimeZone, paramDate);
    paramTimeZone = getZoneLongName(android.icu.text.TimeZoneNames.getInstance(localLocale), paramTimeZone, paramDate);
    if (paramTimeZone == null) {
      return str;
    }
    return str + " " + paramTimeZone;
  }
  
  private static String getZoneLongName(android.icu.text.TimeZoneNames paramTimeZoneNames, TimeZone paramTimeZone, Date paramDate)
  {
    if (paramTimeZone.inDaylightTime(paramDate)) {}
    for (TimeZoneNames.NameType localNameType = TimeZoneNames.NameType.LONG_DAYLIGHT;; localNameType = TimeZoneNames.NameType.LONG_STANDARD) {
      return paramTimeZoneNames.getDisplayName(paramTimeZone.getID(), localNameType, paramDate.getTime());
    }
  }
  
  public static List<Map<String, Object>> getZonesList(Context paramContext)
  {
    Object localObject1 = Locale.getDefault();
    Date localDate = new Date();
    android.icu.text.TimeZoneNames localTimeZoneNames = android.icu.text.TimeZoneNames.getInstance((Locale)localObject1);
    paramContext = readTimezonesToDisplay(paramContext);
    int m = paramContext.size();
    String[] arrayOfString1 = new String[m];
    TimeZone[] arrayOfTimeZone = new TimeZone[m];
    String[] arrayOfString2 = new String[m];
    int i = 0;
    while (i < m)
    {
      localObject2 = (String)paramContext.get(i);
      arrayOfString1[i] = localObject2;
      localObject2 = TimeZone.getTimeZone((String)localObject2);
      arrayOfTimeZone[i] = localObject2;
      arrayOfString2[i] = getGmtOffsetString((Locale)localObject1, (TimeZone)localObject2, localDate);
      i += 1;
    }
    HashSet localHashSet = new HashSet();
    paramContext = libcore.icu.TimeZoneNames.forLocale((Locale)localObject1);
    i = 0;
    int j = paramContext.length;
    while (i < j)
    {
      localHashSet.add(paramContext[i]);
      i += 1;
    }
    Object localObject2 = new HashSet();
    int k = 0;
    j = 0;
    i = k;
    ArrayList localArrayList;
    label242:
    TimeZone localTimeZone;
    if (j < m)
    {
      if (localHashSet.contains(arrayOfString1[j]))
      {
        localObject1 = getZoneLongName(localTimeZoneNames, arrayOfTimeZone[j], localDate);
        paramContext = (Context)localObject1;
        if (localObject1 == null) {
          paramContext = arrayOfString2[j];
        }
        if (!((Set)localObject2).add(paramContext)) {
          i = 1;
        }
      }
    }
    else
    {
      localArrayList = new ArrayList();
      j = 0;
      if (j >= m) {
        break label401;
      }
      paramContext = arrayOfString1[j];
      localTimeZone = arrayOfTimeZone[j];
      localObject2 = arrayOfString2[j];
      if ((localHashSet.contains(paramContext)) && (i == 0)) {
        break label355;
      }
      k = 0;
      label282:
      if (k == 0) {
        break label360;
      }
      paramContext = getZoneLongName(localTimeZoneNames, localTimeZone, localDate);
    }
    for (;;)
    {
      if (paramContext != null)
      {
        localObject1 = paramContext;
        if (!paramContext.isEmpty()) {}
      }
      else
      {
        localObject1 = localObject2;
      }
      localArrayList.add(createDisplayEntry(localTimeZone, (String)localObject2, (String)localObject1, localTimeZone.getOffset(localDate.getTime())));
      j += 1;
      break label242;
      j += 1;
      break;
      label355:
      k = 1;
      break label282;
      label360:
      localObject1 = localTimeZoneNames.getExemplarLocationName(localTimeZone.getID());
      if (localObject1 != null)
      {
        paramContext = (Context)localObject1;
        if (!((String)localObject1).isEmpty()) {}
      }
      else
      {
        paramContext = getZoneLongName(localTimeZoneNames, localTimeZone, localDate);
      }
    }
    label401:
    return localArrayList;
  }
  
  /* Error */
  private static List<String> readTimezonesToDisplay(Context paramContext)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 6
    //   6: aconst_null
    //   7: astore 5
    //   9: new 191	java/util/ArrayList
    //   12: dup
    //   13: invokespecial 192	java/util/ArrayList:<init>	()V
    //   16: astore 7
    //   18: aconst_null
    //   19: astore_3
    //   20: aconst_null
    //   21: astore_2
    //   22: aload_0
    //   23: invokevirtual 220	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   26: getstatic 226	com/android/settingslib/R$xml:timezones	I
    //   29: invokevirtual 232	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   32: astore_0
    //   33: aload_0
    //   34: astore_2
    //   35: aload_0
    //   36: astore_3
    //   37: aload_0
    //   38: invokeinterface 237 1 0
    //   43: iconst_2
    //   44: if_icmpne -11 -> 33
    //   47: aload_0
    //   48: astore_2
    //   49: aload_0
    //   50: astore_3
    //   51: aload_0
    //   52: invokeinterface 237 1 0
    //   57: pop
    //   58: aload_0
    //   59: astore_2
    //   60: aload_0
    //   61: astore_3
    //   62: aload_0
    //   63: invokeinterface 240 1 0
    //   68: iconst_3
    //   69: if_icmpeq +207 -> 276
    //   72: aload_0
    //   73: astore_2
    //   74: aload_0
    //   75: astore_3
    //   76: aload_0
    //   77: invokeinterface 240 1 0
    //   82: iconst_2
    //   83: if_icmpeq +114 -> 197
    //   86: aload_0
    //   87: astore_2
    //   88: aload_0
    //   89: astore_3
    //   90: aload_0
    //   91: invokeinterface 240 1 0
    //   96: istore_1
    //   97: iload_1
    //   98: iconst_1
    //   99: if_icmpne +44 -> 143
    //   102: aload 5
    //   104: astore_2
    //   105: aload_0
    //   106: ifnull +12 -> 118
    //   109: aload_0
    //   110: invokeinterface 243 1 0
    //   115: aload 5
    //   117: astore_2
    //   118: aload_2
    //   119: ifnull +21 -> 140
    //   122: aload_2
    //   123: athrow
    //   124: astore_0
    //   125: ldc 20
    //   127: ldc -11
    //   129: invokestatic 251	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   132: pop
    //   133: aload 7
    //   135: areturn
    //   136: astore_2
    //   137: goto -19 -> 118
    //   140: aload 7
    //   142: areturn
    //   143: aload_0
    //   144: astore_2
    //   145: aload_0
    //   146: astore_3
    //   147: aload_0
    //   148: invokeinterface 237 1 0
    //   153: pop
    //   154: goto -82 -> 72
    //   157: astore_0
    //   158: aload_0
    //   159: athrow
    //   160: astore_3
    //   161: aload_0
    //   162: astore 4
    //   164: aload_2
    //   165: ifnull +12 -> 177
    //   168: aload_2
    //   169: invokeinterface 243 1 0
    //   174: aload_0
    //   175: astore 4
    //   177: aload 4
    //   179: ifnull +138 -> 317
    //   182: aload 4
    //   184: athrow
    //   185: astore_0
    //   186: ldc 20
    //   188: ldc -3
    //   190: invokestatic 251	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   193: pop
    //   194: aload 7
    //   196: areturn
    //   197: aload_0
    //   198: astore_2
    //   199: aload_0
    //   200: astore_3
    //   201: aload_0
    //   202: invokeinterface 256 1 0
    //   207: ldc 23
    //   209: invokevirtual 259	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   212: ifeq +22 -> 234
    //   215: aload_0
    //   216: astore_2
    //   217: aload_0
    //   218: astore_3
    //   219: aload 7
    //   221: aload_0
    //   222: iconst_0
    //   223: invokeinterface 263 2 0
    //   228: invokeinterface 203 2 0
    //   233: pop
    //   234: aload_0
    //   235: astore_2
    //   236: aload_0
    //   237: astore_3
    //   238: aload_0
    //   239: invokeinterface 240 1 0
    //   244: iconst_3
    //   245: if_icmpeq +17 -> 262
    //   248: aload_0
    //   249: astore_2
    //   250: aload_0
    //   251: astore_3
    //   252: aload_0
    //   253: invokeinterface 237 1 0
    //   258: pop
    //   259: goto -25 -> 234
    //   262: aload_0
    //   263: astore_2
    //   264: aload_0
    //   265: astore_3
    //   266: aload_0
    //   267: invokeinterface 237 1 0
    //   272: pop
    //   273: goto -215 -> 58
    //   276: aload 6
    //   278: astore_2
    //   279: aload_0
    //   280: ifnull +12 -> 292
    //   283: aload_0
    //   284: invokeinterface 243 1 0
    //   289: aload 6
    //   291: astore_2
    //   292: aload_2
    //   293: ifnull -160 -> 133
    //   296: aload_2
    //   297: athrow
    //   298: aload_0
    //   299: astore 4
    //   301: aload_0
    //   302: aload_2
    //   303: if_acmpeq -126 -> 177
    //   306: aload_0
    //   307: aload_2
    //   308: invokevirtual 267	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   311: aload_0
    //   312: astore 4
    //   314: goto -137 -> 177
    //   317: aload_3
    //   318: athrow
    //   319: astore_0
    //   320: aload_3
    //   321: astore_2
    //   322: aload_0
    //   323: astore_3
    //   324: aload 4
    //   326: astore_0
    //   327: goto -166 -> 161
    //   330: astore_2
    //   331: goto -39 -> 292
    //   334: astore_2
    //   335: aload_0
    //   336: ifnonnull -38 -> 298
    //   339: aload_2
    //   340: astore 4
    //   342: goto -165 -> 177
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	345	0	paramContext	Context
    //   96	4	1	i	int
    //   21	102	2	localObject1	Object
    //   136	1	2	localThrowable1	Throwable
    //   144	178	2	localObject2	Object
    //   330	1	2	localThrowable2	Throwable
    //   334	6	2	localThrowable3	Throwable
    //   19	128	3	localContext1	Context
    //   160	1	3	localObject3	Object
    //   200	124	3	localContext2	Context
    //   1	340	4	localObject4	Object
    //   7	109	5	localObject5	Object
    //   4	286	6	localObject6	Object
    //   16	204	7	localArrayList	ArrayList
    // Exception table:
    //   from	to	target	type
    //   109	115	124	org/xmlpull/v1/XmlPullParserException
    //   122	124	124	org/xmlpull/v1/XmlPullParserException
    //   168	174	124	org/xmlpull/v1/XmlPullParserException
    //   182	185	124	org/xmlpull/v1/XmlPullParserException
    //   283	289	124	org/xmlpull/v1/XmlPullParserException
    //   296	298	124	org/xmlpull/v1/XmlPullParserException
    //   306	311	124	org/xmlpull/v1/XmlPullParserException
    //   317	319	124	org/xmlpull/v1/XmlPullParserException
    //   109	115	136	java/lang/Throwable
    //   22	33	157	java/lang/Throwable
    //   37	47	157	java/lang/Throwable
    //   51	58	157	java/lang/Throwable
    //   62	72	157	java/lang/Throwable
    //   76	86	157	java/lang/Throwable
    //   90	97	157	java/lang/Throwable
    //   147	154	157	java/lang/Throwable
    //   201	215	157	java/lang/Throwable
    //   219	234	157	java/lang/Throwable
    //   238	248	157	java/lang/Throwable
    //   252	259	157	java/lang/Throwable
    //   266	273	157	java/lang/Throwable
    //   158	160	160	finally
    //   109	115	185	java/io/IOException
    //   122	124	185	java/io/IOException
    //   168	174	185	java/io/IOException
    //   182	185	185	java/io/IOException
    //   283	289	185	java/io/IOException
    //   296	298	185	java/io/IOException
    //   306	311	185	java/io/IOException
    //   317	319	185	java/io/IOException
    //   22	33	319	finally
    //   37	47	319	finally
    //   51	58	319	finally
    //   62	72	319	finally
    //   76	86	319	finally
    //   90	97	319	finally
    //   147	154	319	finally
    //   201	215	319	finally
    //   219	234	319	finally
    //   238	248	319	finally
    //   252	259	319	finally
    //   266	273	319	finally
    //   283	289	330	java/lang/Throwable
    //   168	174	334	java/lang/Throwable
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\datetime\ZoneGetter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */