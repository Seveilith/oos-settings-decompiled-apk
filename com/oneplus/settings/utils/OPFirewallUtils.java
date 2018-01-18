package com.oneplus.settings.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class OPFirewallUtils
{
  public static final String COLUMN_MOBILE = "mobile";
  public static final String COLUMN_PACKAGE = "pkg";
  public static final String COLUMN_WLAN = "wlan";
  public static final String OPSAFE_AUTHORITY = "com.oneplus.security.database.SafeProvider";
  public static final String TABLE_NETWORK_RESTRICT = "network_restrict";
  private static final String TAG = "OPFirewallUtils";
  public static final Uri URI_NETWORK_RESTRICT = Uri.withAppendedPath(URI_OPSAFE_BASE, "network_restrict");
  public static final Uri URI_OPSAFE_BASE = Uri.parse("content://com.oneplus.security.database.SafeProvider");
  public static final String _ID = "_id";
  
  public static void addOrUpdateRole(Context paramContext, OPFirewallRule paramOPFirewallRule)
  {
    int j = 0;
    ContentValues localContentValues = new ContentValues();
    if (paramOPFirewallRule.getWlan() != null)
    {
      if (paramOPFirewallRule.getWlan().intValue() == 0)
      {
        i = 0;
        localContentValues.put("wlan", Integer.valueOf(i));
      }
    }
    else if (paramOPFirewallRule.getMobile() != null) {
      if (paramOPFirewallRule.getMobile().intValue() != 0) {
        break label151;
      }
    }
    label151:
    for (int i = j;; i = 1)
    {
      localContentValues.put("mobile", Integer.valueOf(i));
      if (selectFirewallRuleByPkg(paramContext, paramOPFirewallRule.getPkg()) == null) {}
      try
      {
        localContentValues.put("pkg", paramOPFirewallRule.getPkg());
        paramContext.getContentResolver().insert(URI_NETWORK_RESTRICT, localContentValues);
        return;
      }
      catch (Exception paramContext)
      {
        Log.e("OPFirewallUtils", paramContext.getMessage());
        return;
      }
      paramContext.getContentResolver().update(URI_NETWORK_RESTRICT, localContentValues, "pkg = ? ", new String[] { paramOPFirewallRule.getPkg() });
      return;
      i = 1;
      break;
    }
  }
  
  /* Error */
  public static java.util.List<OPFirewallRule> selectAllFirewallRules(Context paramContext)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_1
    //   4: aconst_null
    //   5: astore 5
    //   7: aconst_null
    //   8: astore 4
    //   10: aload_0
    //   11: invokevirtual 98	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   14: getstatic 47	com/oneplus/settings/utils/OPFirewallUtils:URI_NETWORK_RESTRICT	Landroid/net/Uri;
    //   17: aconst_null
    //   18: aconst_null
    //   19: aconst_null
    //   20: aconst_null
    //   21: invokevirtual 127	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   24: astore_0
    //   25: aload 5
    //   27: astore_3
    //   28: aload_0
    //   29: ifnull +155 -> 184
    //   32: aload 5
    //   34: astore_3
    //   35: aload_0
    //   36: astore_1
    //   37: aload_0
    //   38: astore_2
    //   39: aload_0
    //   40: invokeinterface 132 1 0
    //   45: ifle +139 -> 184
    //   48: aload_0
    //   49: astore_1
    //   50: aload_0
    //   51: astore_2
    //   52: new 134	java/util/ArrayList
    //   55: dup
    //   56: invokespecial 135	java/util/ArrayList:<init>	()V
    //   59: astore_3
    //   60: aload_0
    //   61: invokeinterface 139 1 0
    //   66: ifeq +118 -> 184
    //   69: aload_3
    //   70: new 60	com/oneplus/settings/utils/OPFirewallRule
    //   73: dup
    //   74: aload_0
    //   75: aload_0
    //   76: ldc 29
    //   78: invokeinterface 143 2 0
    //   83: invokeinterface 147 2 0
    //   88: invokestatic 74	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   91: aload_0
    //   92: aload_0
    //   93: ldc 11
    //   95: invokeinterface 143 2 0
    //   100: invokeinterface 151 2 0
    //   105: aload_0
    //   106: aload_0
    //   107: ldc 14
    //   109: invokeinterface 143 2 0
    //   114: invokeinterface 147 2 0
    //   119: invokestatic 74	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   122: aload_0
    //   123: aload_0
    //   124: ldc 8
    //   126: invokeinterface 143 2 0
    //   131: invokeinterface 147 2 0
    //   136: invokestatic 74	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   139: invokespecial 154	com/oneplus/settings/utils/OPFirewallRule:<init>	(Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;)V
    //   142: invokeinterface 160 2 0
    //   147: pop
    //   148: goto -88 -> 60
    //   151: astore_1
    //   152: aload_3
    //   153: astore_2
    //   154: aload_1
    //   155: astore_3
    //   156: aload_0
    //   157: astore_1
    //   158: ldc 23
    //   160: aload_3
    //   161: invokevirtual 115	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   164: invokestatic 121	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   167: pop
    //   168: aload_2
    //   169: astore_1
    //   170: aload_0
    //   171: ifnull +11 -> 182
    //   174: aload_0
    //   175: invokeinterface 163 1 0
    //   180: aload_2
    //   181: astore_1
    //   182: aload_1
    //   183: areturn
    //   184: aload_3
    //   185: astore_1
    //   186: aload_0
    //   187: ifnull -5 -> 182
    //   190: aload_0
    //   191: invokeinterface 163 1 0
    //   196: aload_3
    //   197: areturn
    //   198: astore_2
    //   199: aload_1
    //   200: astore_0
    //   201: aload_2
    //   202: astore_1
    //   203: aload_0
    //   204: ifnull +9 -> 213
    //   207: aload_0
    //   208: invokeinterface 163 1 0
    //   213: aload_1
    //   214: athrow
    //   215: astore_1
    //   216: goto -13 -> 203
    //   219: astore_3
    //   220: aload_2
    //   221: astore_0
    //   222: aload 4
    //   224: astore_2
    //   225: goto -69 -> 156
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	228	0	paramContext	Context
    //   3	47	1	localContext	Context
    //   151	4	1	localException1	Exception
    //   157	57	1	localObject1	Object
    //   215	1	1	localObject2	Object
    //   1	180	2	localObject3	Object
    //   198	23	2	localObject4	Object
    //   224	1	2	localObject5	Object
    //   27	170	3	localObject6	Object
    //   219	1	3	localException2	Exception
    //   8	215	4	localObject7	Object
    //   5	28	5	localObject8	Object
    // Exception table:
    //   from	to	target	type
    //   60	148	151	java/lang/Exception
    //   10	25	198	finally
    //   39	48	198	finally
    //   52	60	198	finally
    //   158	168	198	finally
    //   60	148	215	finally
    //   10	25	219	java/lang/Exception
    //   39	48	219	java/lang/Exception
    //   52	60	219	java/lang/Exception
  }
  
  public static OPFirewallRule selectFirewallRuleByPkg(Context paramContext, String paramString)
  {
    localObject = null;
    localContext = null;
    try
    {
      paramContext = paramContext.getContentResolver().query(URI_NETWORK_RESTRICT, null, "pkg = ? ", new String[] { paramString }, null);
      if (paramContext != null)
      {
        localContext = paramContext;
        localObject = paramContext;
        if (paramContext.getCount() > 0)
        {
          localContext = paramContext;
          localObject = paramContext;
          if (paramContext.moveToNext())
          {
            localContext = paramContext;
            localObject = paramContext;
            paramString = new OPFirewallRule(Integer.valueOf(paramContext.getInt(paramContext.getColumnIndex("_id"))), paramContext.getString(paramContext.getColumnIndex("pkg")), Integer.valueOf(paramContext.getInt(paramContext.getColumnIndex("wlan"))), Integer.valueOf(paramContext.getInt(paramContext.getColumnIndex("mobile"))));
            if (paramContext != null) {
              paramContext.close();
            }
            return paramString;
          }
        }
      }
      if (paramContext != null) {
        paramContext.close();
      }
    }
    catch (Exception paramContext)
    {
      do
      {
        localObject = localContext;
        Log.e("OPFirewallUtils", paramContext.getMessage());
      } while (localContext == null);
      localContext.close();
      return null;
    }
    finally
    {
      if (localObject == null) {
        break label194;
      }
      ((Cursor)localObject).close();
    }
    return null;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\utils\OPFirewallUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */