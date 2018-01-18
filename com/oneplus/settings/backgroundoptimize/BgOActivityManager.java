package com.oneplus.settings.backgroundoptimize;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BgOActivityManager
  implements IBgOActivityManager
{
  private static final String TAG = BgOActivityManager.class.getSimpleName();
  private static BgOActivityManager instance;
  private Method getAllAppControlModes;
  private Method getAppControlMode;
  private Method getAppControlState;
  private ActivityManager mActivityManager;
  private Method setAppControlMode;
  private Method setAppControlState;
  
  private BgOActivityManager(Context paramContext)
  {
    try
    {
      this.mActivityManager = ((ActivityManager)paramContext.getSystemService("activity"));
      paramContext = this.mActivityManager.getClass();
      this.getAllAppControlModes = paramContext.getDeclaredMethod("getAllAppControlModes", new Class[] { Integer.TYPE });
      this.getAppControlMode = paramContext.getDeclaredMethod("getAppControlMode", new Class[] { String.class, Integer.TYPE });
      this.setAppControlMode = paramContext.getDeclaredMethod("setAppControlMode", new Class[] { String.class, Integer.TYPE, Integer.TYPE });
      this.getAppControlState = paramContext.getDeclaredMethod("getAppControlState", new Class[] { Integer.TYPE });
      this.setAppControlState = paramContext.getDeclaredMethod("setAppControlState", new Class[] { Integer.TYPE, Integer.TYPE });
      return;
    }
    catch (Exception paramContext)
    {
      Log.e(TAG, "BgOActivityManager Exception=" + paramContext);
      paramContext.printStackTrace();
    }
  }
  
  /* Error */
  private List<AppControlMode> convert(List<?> paramList)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 8
    //   3: aconst_null
    //   4: astore 7
    //   6: aconst_null
    //   7: astore 4
    //   9: new 110	java/util/ArrayList
    //   12: dup
    //   13: invokespecial 111	java/util/ArrayList:<init>	()V
    //   16: astore 9
    //   18: aload_1
    //   19: invokeinterface 117 1 0
    //   24: astore 10
    //   26: aload 4
    //   28: astore_1
    //   29: aload 10
    //   31: invokeinterface 123 1 0
    //   36: ifeq +336 -> 372
    //   39: aload 10
    //   41: invokeinterface 127 1 0
    //   46: astore 11
    //   48: aload 7
    //   50: astore 4
    //   52: aload 8
    //   54: astore 5
    //   56: aload_1
    //   57: astore 6
    //   59: aload 8
    //   61: ifnonnull +132 -> 193
    //   64: aload 7
    //   66: astore 4
    //   68: aload 8
    //   70: astore 5
    //   72: aload_1
    //   73: astore 6
    //   75: aload 11
    //   77: invokevirtual 128	java/lang/Object:getClass	()Ljava/lang/Class;
    //   80: ldc -126
    //   82: invokevirtual 134	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   85: astore 8
    //   87: aload 7
    //   89: astore 4
    //   91: aload 8
    //   93: astore 5
    //   95: aload_1
    //   96: astore 6
    //   98: aload 8
    //   100: iconst_1
    //   101: invokevirtual 140	java/lang/reflect/Field:setAccessible	(Z)V
    //   104: aload 7
    //   106: astore 4
    //   108: aload 8
    //   110: astore 5
    //   112: aload_1
    //   113: astore 6
    //   115: aload 11
    //   117: invokevirtual 128	java/lang/Object:getClass	()Ljava/lang/Class;
    //   120: ldc -114
    //   122: invokevirtual 134	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   125: astore 7
    //   127: aload 7
    //   129: astore 4
    //   131: aload 8
    //   133: astore 5
    //   135: aload_1
    //   136: astore 6
    //   138: aload 7
    //   140: iconst_1
    //   141: invokevirtual 140	java/lang/reflect/Field:setAccessible	(Z)V
    //   144: aload 7
    //   146: astore 4
    //   148: aload 8
    //   150: astore 5
    //   152: aload_1
    //   153: astore 6
    //   155: aload 11
    //   157: invokevirtual 128	java/lang/Object:getClass	()Ljava/lang/Class;
    //   160: ldc -112
    //   162: invokevirtual 134	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   165: astore_1
    //   166: aload 7
    //   168: astore 4
    //   170: aload 8
    //   172: astore 5
    //   174: aload_1
    //   175: astore 6
    //   177: aload_1
    //   178: iconst_1
    //   179: invokevirtual 140	java/lang/reflect/Field:setAccessible	(Z)V
    //   182: aload_1
    //   183: astore 6
    //   185: aload 8
    //   187: astore 5
    //   189: aload 7
    //   191: astore 4
    //   193: aload 5
    //   195: aload 11
    //   197: invokevirtual 148	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   200: checkcast 67	java/lang/String
    //   203: astore_1
    //   204: aload 4
    //   206: aload 11
    //   208: invokevirtual 148	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   211: checkcast 54	java/lang/Integer
    //   214: invokevirtual 152	java/lang/Integer:intValue	()I
    //   217: istore_2
    //   218: aload 6
    //   220: aload 11
    //   222: invokevirtual 148	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   225: checkcast 54	java/lang/Integer
    //   228: invokevirtual 152	java/lang/Integer:intValue	()I
    //   231: istore_3
    //   232: getstatic 28	com/oneplus/settings/backgroundoptimize/BgOActivityManager:TAG	Ljava/lang/String;
    //   235: new 80	java/lang/StringBuilder
    //   238: dup
    //   239: invokespecial 81	java/lang/StringBuilder:<init>	()V
    //   242: ldc -102
    //   244: invokevirtual 87	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   247: aload_1
    //   248: invokevirtual 87	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   251: ldc -100
    //   253: invokevirtual 87	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   256: iload_2
    //   257: invokevirtual 159	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   260: ldc -95
    //   262: invokevirtual 87	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   265: iload_3
    //   266: invokevirtual 159	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   269: invokevirtual 93	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   272: invokestatic 167	com/oneplus/settings/backgroundoptimize/Logutil:loge	(Ljava/lang/String;Ljava/lang/String;)V
    //   275: aload 9
    //   277: new 169	com/oneplus/settings/backgroundoptimize/AppControlMode
    //   280: dup
    //   281: aload_1
    //   282: iload_2
    //   283: iload_3
    //   284: invokespecial 172	com/oneplus/settings/backgroundoptimize/AppControlMode:<init>	(Ljava/lang/String;II)V
    //   287: invokeinterface 176 2 0
    //   292: pop
    //   293: aload 4
    //   295: astore 7
    //   297: aload 5
    //   299: astore 8
    //   301: aload 6
    //   303: astore_1
    //   304: goto -275 -> 29
    //   307: astore_1
    //   308: aload_1
    //   309: invokevirtual 177	java/lang/IllegalArgumentException:printStackTrace	()V
    //   312: aload 4
    //   314: astore 7
    //   316: aload 5
    //   318: astore 8
    //   320: aload 6
    //   322: astore_1
    //   323: goto -294 -> 29
    //   326: astore_1
    //   327: aload_1
    //   328: invokevirtual 102	java/lang/Exception:printStackTrace	()V
    //   331: goto -138 -> 193
    //   334: astore_1
    //   335: aload_1
    //   336: invokevirtual 102	java/lang/Exception:printStackTrace	()V
    //   339: aload 4
    //   341: astore 7
    //   343: aload 5
    //   345: astore 8
    //   347: aload 6
    //   349: astore_1
    //   350: goto -321 -> 29
    //   353: astore_1
    //   354: aload_1
    //   355: invokevirtual 178	java/lang/IllegalAccessException:printStackTrace	()V
    //   358: aload 4
    //   360: astore 7
    //   362: aload 5
    //   364: astore 8
    //   366: aload 6
    //   368: astore_1
    //   369: goto -340 -> 29
    //   372: aload 9
    //   374: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	375	0	this	BgOActivityManager
    //   0	375	1	paramList	List<?>
    //   217	66	2	i	int
    //   231	53	3	j	int
    //   7	352	4	localObject1	Object
    //   54	309	5	localObject2	Object
    //   57	310	6	localList	List<?>
    //   4	357	7	localObject3	Object
    //   1	364	8	localObject4	Object
    //   16	357	9	localArrayList	ArrayList
    //   24	16	10	localIterator	Iterator
    //   46	175	11	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   193	293	307	java/lang/IllegalArgumentException
    //   75	87	326	java/lang/Exception
    //   98	104	326	java/lang/Exception
    //   115	127	326	java/lang/Exception
    //   138	144	326	java/lang/Exception
    //   155	166	326	java/lang/Exception
    //   177	182	326	java/lang/Exception
    //   193	293	334	java/lang/Exception
    //   193	293	353	java/lang/IllegalAccessException
  }
  
  public static BgOActivityManager getInstance(Context paramContext)
  {
    if (instance == null) {}
    try
    {
      if (instance == null) {
        instance = new BgOActivityManager(paramContext);
      }
      return instance;
    }
    finally
    {
      paramContext = finally;
      throw paramContext;
    }
  }
  
  public List<AppControlMode> getAllAppControlModes(int paramInt)
  {
    Object localObject4 = invoke(this.getAllAppControlModes, new Object[] { Integer.valueOf(paramInt) });
    Object localObject3 = null;
    Object localObject1 = localObject3;
    if (localObject4 != null) {}
    try
    {
      localObject1 = convert((List)localObject4);
      localObject3 = localObject1;
      if (localObject1 == null) {
        localObject3 = new ArrayList();
      }
      return (List<AppControlMode>)localObject3;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        localException.printStackTrace();
        Object localObject2 = localObject3;
      }
    }
  }
  
  public Map<String, AppControlMode> getAllAppControlModesMap(int paramInt)
  {
    Object localObject = getAllAppControlModes(paramInt);
    HashMap localHashMap = new HashMap();
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      AppControlMode localAppControlMode = (AppControlMode)((Iterator)localObject).next();
      localHashMap.put(localAppControlMode.packageName, localAppControlMode);
    }
    return localHashMap;
  }
  
  public int getAppControlMode(String paramString, int paramInt)
  {
    Object localObject = invoke(this.getAppControlMode, new Object[] { paramString, Integer.valueOf(paramInt) });
    int i = 0;
    if (localObject != null) {
      i = ((Integer)localObject).intValue();
    }
    Logutil.loge(TAG, "AppControl # getAppControlMode packageName: " + paramString + ", mode=" + paramInt + ", value=" + i);
    return i;
  }
  
  public int getAppControlState(int paramInt)
  {
    Object localObject = invoke(this.getAppControlState, new Object[] { Integer.valueOf(paramInt) });
    int i = 0;
    if (localObject != null) {
      i = ((Integer)localObject).intValue();
    }
    Logutil.loge(TAG, "AppControl # getAppControlState mode=" + paramInt + ", value=" + i);
    return i;
  }
  
  public Object invoke(Method paramMethod, Object... paramVarArgs)
  {
    if ((this.mActivityManager == null) || (paramMethod == null)) {
      return null;
    }
    try
    {
      paramMethod = paramMethod.invoke(this.mActivityManager, paramVarArgs);
      return paramMethod;
    }
    catch (InvocationTargetException paramMethod)
    {
      paramMethod.printStackTrace();
      return null;
    }
    catch (IllegalArgumentException paramMethod)
    {
      paramMethod.printStackTrace();
      return null;
    }
    catch (IllegalAccessException paramMethod)
    {
      paramMethod.printStackTrace();
    }
    return null;
  }
  
  public int setAppControlMode(String paramString, int paramInt1, int paramInt2)
  {
    Logutil.loge(TAG, "AppControl # setAppControlMode packageName: " + paramString + ", mode=" + paramInt1 + ", value=" + paramInt2);
    invoke(this.setAppControlMode, new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
    return 0;
  }
  
  public int setAppControlState(int paramInt1, int paramInt2)
  {
    Logutil.loge(TAG, "AppControl # setAppControlState mode=" + paramInt1 + ", value=" + paramInt2);
    invoke(this.setAppControlState, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
    return 0;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\backgroundoptimize\BgOActivityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */