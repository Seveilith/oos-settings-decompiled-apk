package com.oneplus.settings;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.oneplus.config.ConfigGrabber;
import com.oneplus.config.ConfigObserver;
import com.oneplus.config.ConfigObserver.ConfigUpdater;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;

public class OPOnlineConfigManager
{
  private static String TAG = OPOnlineConfigManager.class.getSimpleName();
  private static List<String> localMultiAppWhiteList;
  private static Object lock = new Object();
  private static final List<String> multiAppWhiteList = new ArrayList();
  private static OPOnlineConfigManager onlineConfigManager = null;
  private String CONFIG_NAME = "ROM_APP_OPSettings";
  final int MSG_GET_ONLINECONFIG = 1;
  private String OP_MULTIAPP_WHITE_LIST = "op_multiapp_white_list";
  private ConfigObserver mBackgroundConfigObserver;
  private Context mContext;
  private Handler mHandler;
  private HandlerThread mHandlerThread;
  
  private OPOnlineConfigManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHandlerThread = new HandlerThread(TAG);
    this.mHandlerThread.start();
    this.mHandler = new Handler(this.mHandlerThread.getLooper())
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        Log.d(OPOnlineConfigManager.-get1(), "settings handleMessage....");
        switch (paramAnonymousMessage.what)
        {
        default: 
          return;
        }
        paramAnonymousMessage = new ConfigGrabber(OPOnlineConfigManager.-get2(OPOnlineConfigManager.this), OPOnlineConfigManager.-get0(OPOnlineConfigManager.this)).grabConfig();
        OPOnlineConfigManager.-wrap0(OPOnlineConfigManager.this, paramAnonymousMessage);
      }
    };
    localMultiAppWhiteList = Arrays.asList(paramContext.getResources().getStringArray(2131427502));
  }
  
  public static OPOnlineConfigManager getInstence(Context paramContext)
  {
    synchronized (lock)
    {
      if (onlineConfigManager == null) {
        onlineConfigManager = new OPOnlineConfigManager(paramContext);
      }
      return onlineConfigManager;
    }
  }
  
  public static List<String> getMultiAppWhiteList()
  {
    synchronized (multiAppWhiteList)
    {
      if (!multiAppWhiteList.isEmpty())
      {
        localList2 = multiAppWhiteList;
        return localList2;
      }
      List localList2 = localMultiAppWhiteList;
      return localList2;
    }
  }
  
  /* Error */
  private void parseConfigFromJson(JSONArray arg1)
  {
    // Byte code:
    //   0: aload_1
    //   1: astore 4
    //   3: aload_1
    //   4: ifnonnull +62 -> 66
    //   7: aload_0
    //   8: getfield 76	com/oneplus/settings/OPOnlineConfigManager:OP_MULTIAPP_WHITE_LIST	Ljava/lang/String;
    //   11: aconst_null
    //   12: invokestatic 142	com/oneplus/settings/utils/OPPrefUtil:getString	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   15: astore 4
    //   17: getstatic 148	android/os/Build:DEBUG_ONEPLUS	Z
    //   20: ifeq +30 -> 50
    //   23: getstatic 40	com/oneplus/settings/OPOnlineConfigManager:TAG	Ljava/lang/String;
    //   26: new 150	java/lang/StringBuilder
    //   29: dup
    //   30: invokespecial 151	java/lang/StringBuilder:<init>	()V
    //   33: ldc -103
    //   35: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   38: aload 4
    //   40: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   43: invokevirtual 160	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   46: invokestatic 166	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   49: pop
    //   50: aload 4
    //   52: ifnull +198 -> 250
    //   55: new 168	org/json/JSONArray
    //   58: dup
    //   59: aload 4
    //   61: invokespecial 169	org/json/JSONArray:<init>	(Ljava/lang/String;)V
    //   64: astore 4
    //   66: getstatic 148	android/os/Build:DEBUG_ONEPLUS	Z
    //   69: ifeq +33 -> 102
    //   72: getstatic 40	com/oneplus/settings/OPOnlineConfigManager:TAG	Ljava/lang/String;
    //   75: new 150	java/lang/StringBuilder
    //   78: dup
    //   79: invokespecial 151	java/lang/StringBuilder:<init>	()V
    //   82: ldc -85
    //   84: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   87: aload 4
    //   89: invokevirtual 172	org/json/JSONArray:toString	()Ljava/lang/String;
    //   92: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   95: invokevirtual 160	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   98: invokestatic 166	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   101: pop
    //   102: iconst_0
    //   103: istore_2
    //   104: iload_2
    //   105: aload 4
    //   107: invokevirtual 176	org/json/JSONArray:length	()I
    //   110: if_icmpge +192 -> 302
    //   113: aload 4
    //   115: iload_2
    //   116: invokevirtual 180	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   119: astore_1
    //   120: aload_1
    //   121: ldc -74
    //   123: invokevirtual 187	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   126: aload_0
    //   127: getfield 76	com/oneplus/settings/OPOnlineConfigManager:OP_MULTIAPP_WHITE_LIST	Ljava/lang/String;
    //   130: invokevirtual 193	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   133: ifeq +201 -> 334
    //   136: aload_1
    //   137: ldc -61
    //   139: invokevirtual 199	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   142: astore 5
    //   144: aload_0
    //   145: getfield 76	com/oneplus/settings/OPOnlineConfigManager:OP_MULTIAPP_WHITE_LIST	Ljava/lang/String;
    //   148: aload 5
    //   150: invokevirtual 172	org/json/JSONArray:toString	()Ljava/lang/String;
    //   153: invokestatic 203	com/oneplus/settings/utils/OPPrefUtil:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   156: getstatic 69	com/oneplus/settings/OPOnlineConfigManager:multiAppWhiteList	Ljava/util/List;
    //   159: astore_1
    //   160: aload_1
    //   161: monitorenter
    //   162: getstatic 69	com/oneplus/settings/OPOnlineConfigManager:multiAppWhiteList	Ljava/util/List;
    //   165: invokeinterface 206 1 0
    //   170: iconst_0
    //   171: istore_3
    //   172: iload_3
    //   173: aload 5
    //   175: invokevirtual 176	org/json/JSONArray:length	()I
    //   178: if_icmpge +82 -> 260
    //   181: aload 5
    //   183: iload_3
    //   184: invokevirtual 209	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   187: astore 6
    //   189: getstatic 69	com/oneplus/settings/OPOnlineConfigManager:multiAppWhiteList	Ljava/util/List;
    //   192: aload 6
    //   194: invokeinterface 212 2 0
    //   199: pop
    //   200: iload_3
    //   201: iconst_1
    //   202: iadd
    //   203: istore_3
    //   204: goto -32 -> 172
    //   207: astore 4
    //   209: getstatic 40	com/oneplus/settings/OPOnlineConfigManager:TAG	Ljava/lang/String;
    //   212: new 150	java/lang/StringBuilder
    //   215: dup
    //   216: invokespecial 151	java/lang/StringBuilder:<init>	()V
    //   219: ldc -42
    //   221: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   224: aload 4
    //   226: invokevirtual 217	org/json/JSONException:getMessage	()Ljava/lang/String;
    //   229: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   232: invokevirtual 160	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   235: invokestatic 220	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   238: pop
    //   239: aload 4
    //   241: invokevirtual 223	org/json/JSONException:printStackTrace	()V
    //   244: aload_1
    //   245: astore 4
    //   247: goto -181 -> 66
    //   250: getstatic 40	com/oneplus/settings/OPOnlineConfigManager:TAG	Ljava/lang/String;
    //   253: ldc -31
    //   255: invokestatic 166	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   258: pop
    //   259: return
    //   260: aload_1
    //   261: monitorexit
    //   262: goto +72 -> 334
    //   265: astore 4
    //   267: aload_1
    //   268: monitorexit
    //   269: aload 4
    //   271: athrow
    //   272: astore_1
    //   273: getstatic 40	com/oneplus/settings/OPOnlineConfigManager:TAG	Ljava/lang/String;
    //   276: new 150	java/lang/StringBuilder
    //   279: dup
    //   280: invokespecial 151	java/lang/StringBuilder:<init>	()V
    //   283: ldc -42
    //   285: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   288: aload_1
    //   289: invokevirtual 217	org/json/JSONException:getMessage	()Ljava/lang/String;
    //   292: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   295: invokevirtual 160	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   298: invokestatic 220	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   301: pop
    //   302: return
    //   303: astore_1
    //   304: getstatic 40	com/oneplus/settings/OPOnlineConfigManager:TAG	Ljava/lang/String;
    //   307: new 150	java/lang/StringBuilder
    //   310: dup
    //   311: invokespecial 151	java/lang/StringBuilder:<init>	()V
    //   314: ldc -29
    //   316: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   319: aload_1
    //   320: invokevirtual 228	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   323: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   326: invokevirtual 160	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   329: invokestatic 220	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   332: pop
    //   333: return
    //   334: iload_2
    //   335: iconst_1
    //   336: iadd
    //   337: istore_2
    //   338: goto -234 -> 104
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	341	0	this	OPOnlineConfigManager
    //   103	235	2	i	int
    //   171	33	3	j	int
    //   1	113	4	localObject1	Object
    //   207	33	4	localJSONException	org.json.JSONException
    //   245	1	4	localJSONArray1	JSONArray
    //   265	5	4	localObject2	Object
    //   142	40	5	localJSONArray2	JSONArray
    //   187	6	6	str	String
    // Exception table:
    //   from	to	target	type
    //   55	66	207	org/json/JSONException
    //   162	170	265	finally
    //   172	200	265	finally
    //   104	162	272	org/json/JSONException
    //   260	262	272	org/json/JSONException
    //   267	272	272	org/json/JSONException
    //   104	162	303	java/lang/Exception
    //   260	262	303	java/lang/Exception
    //   267	272	303	java/lang/Exception
  }
  
  public void init()
  {
    this.mBackgroundConfigObserver = new ConfigObserver(this.mContext, this.mHandler, new BackgroundConfigUpdater(), this.CONFIG_NAME);
    this.mBackgroundConfigObserver.register();
    this.mHandler.sendEmptyMessage(1);
  }
  
  class BackgroundConfigUpdater
    implements ConfigObserver.ConfigUpdater
  {
    BackgroundConfigUpdater() {}
    
    public void updateConfig(JSONArray paramJSONArray)
    {
      OPOnlineConfigManager.-wrap0(OPOnlineConfigManager.this, paramJSONArray);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPOnlineConfigManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */