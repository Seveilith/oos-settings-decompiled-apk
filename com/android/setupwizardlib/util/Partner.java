package com.android.setupwizardlib.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class Partner
{
  private static final String ACTION_PARTNER_CUSTOMIZATION = "com.android.setupwizard.action.PARTNER_CUSTOMIZATION";
  private static final String TAG = "(SUW) Partner";
  private static Partner sPartner;
  private static boolean sSearched = false;
  private final String mPackageName;
  private final Resources mResources;
  
  private Partner(String paramString, Resources paramResources)
  {
    this.mPackageName = paramString;
    this.mResources = paramResources;
  }
  
  /* Error */
  public static Partner get(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 25	com/android/setupwizardlib/util/Partner:sSearched	Z
    //   6: ifne +100 -> 106
    //   9: aload_0
    //   10: invokevirtual 44	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   13: astore_0
    //   14: aload_0
    //   15: new 46	android/content/Intent
    //   18: dup
    //   19: ldc 11
    //   21: invokespecial 49	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   24: iconst_0
    //   25: invokevirtual 55	android/content/pm/PackageManager:queryBroadcastReceivers	(Landroid/content/Intent;I)Ljava/util/List;
    //   28: invokeinterface 61 1 0
    //   33: astore_2
    //   34: aload_2
    //   35: invokeinterface 67 1 0
    //   40: ifeq +62 -> 102
    //   43: aload_2
    //   44: invokeinterface 71 1 0
    //   49: checkcast 73	android/content/pm/ResolveInfo
    //   52: astore_3
    //   53: aload_3
    //   54: getfield 77	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   57: ifnull -23 -> 34
    //   60: aload_3
    //   61: getfield 77	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   64: getfield 83	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   67: astore_3
    //   68: aload_3
    //   69: getfield 89	android/content/pm/ApplicationInfo:flags	I
    //   72: istore_1
    //   73: iload_1
    //   74: iconst_1
    //   75: iand
    //   76: ifeq -42 -> 34
    //   79: aload_0
    //   80: aload_3
    //   81: invokevirtual 93	android/content/pm/PackageManager:getResourcesForApplication	(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/Resources;
    //   84: astore 4
    //   86: new 2	com/android/setupwizardlib/util/Partner
    //   89: dup
    //   90: aload_3
    //   91: getfield 96	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   94: aload 4
    //   96: invokespecial 98	com/android/setupwizardlib/util/Partner:<init>	(Ljava/lang/String;Landroid/content/res/Resources;)V
    //   99: putstatic 100	com/android/setupwizardlib/util/Partner:sPartner	Lcom/android/setupwizardlib/util/Partner;
    //   102: iconst_1
    //   103: putstatic 25	com/android/setupwizardlib/util/Partner:sSearched	Z
    //   106: getstatic 100	com/android/setupwizardlib/util/Partner:sPartner	Lcom/android/setupwizardlib/util/Partner;
    //   109: astore_0
    //   110: ldc 2
    //   112: monitorexit
    //   113: aload_0
    //   114: areturn
    //   115: astore 4
    //   117: ldc 14
    //   119: new 102	java/lang/StringBuilder
    //   122: dup
    //   123: invokespecial 103	java/lang/StringBuilder:<init>	()V
    //   126: ldc 105
    //   128: invokevirtual 109	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   131: aload_3
    //   132: getfield 96	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   135: invokevirtual 109	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: invokevirtual 113	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   141: invokestatic 119	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   144: pop
    //   145: goto -111 -> 34
    //   148: astore_0
    //   149: ldc 2
    //   151: monitorexit
    //   152: aload_0
    //   153: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	154	0	paramContext	Context
    //   72	4	1	i	int
    //   33	11	2	localIterator	java.util.Iterator
    //   52	80	3	localObject	Object
    //   84	11	4	localResources	Resources
    //   115	1	4	localNameNotFoundException	android.content.pm.PackageManager.NameNotFoundException
    // Exception table:
    //   from	to	target	type
    //   79	102	115	android/content/pm/PackageManager$NameNotFoundException
    //   3	34	148	finally
    //   34	73	148	finally
    //   79	102	148	finally
    //   102	106	148	finally
    //   106	110	148	finally
    //   117	145	148	finally
  }
  
  public static Drawable getDrawable(Context paramContext, int paramInt)
  {
    paramContext = getResourceEntry(paramContext, paramInt);
    return paramContext.resources.getDrawable(paramContext.id);
  }
  
  public static ResourceEntry getResourceEntry(Context paramContext, int paramInt)
  {
    Partner localPartner = get(paramContext);
    if (localPartner != null)
    {
      Resources localResources = paramContext.getResources();
      int i = localPartner.getIdentifier(localResources.getResourceEntryName(paramInt), localResources.getResourceTypeName(paramInt));
      if (i != 0) {
        return new ResourceEntry(localPartner.mResources, i, true);
      }
    }
    return new ResourceEntry(paramContext.getResources(), paramInt, false);
  }
  
  public static String getString(Context paramContext, int paramInt)
  {
    paramContext = getResourceEntry(paramContext, paramInt);
    return paramContext.resources.getString(paramContext.id);
  }
  
  public static void resetForTesting()
  {
    try
    {
      sSearched = false;
      sPartner = null;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int getIdentifier(String paramString1, String paramString2)
  {
    return this.mResources.getIdentifier(paramString1, paramString2, this.mPackageName);
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public Resources getResources()
  {
    return this.mResources;
  }
  
  public static class ResourceEntry
  {
    public int id;
    public boolean isOverlay;
    public Resources resources;
    
    ResourceEntry(Resources paramResources, int paramInt, boolean paramBoolean)
    {
      this.resources = paramResources;
      this.id = paramInt;
      this.isOverlay = paramBoolean;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\util\Partner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */