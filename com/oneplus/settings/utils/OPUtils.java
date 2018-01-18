package com.oneplus.settings.utils;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.LocaleList;
import android.os.StatFs;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.System;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.OpFeatures;
import android.util.TypedValue;
import android.widget.ListView;
import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocaleStore;
import com.android.internal.app.LocaleStore.LocaleInfo;
import com.android.settings.SettingsPreferenceFragment;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.oneplus.settings.SettingsBaseApplication;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.oneplus.odm.insight.tracker.AppTracker;

public class OPUtils
{
  public static final int ANDROID_SYSTEM_UID = 1000;
  public static final String COMPANY = "oneplus";
  public static final String OEM_REPEATE_INCALL_LIMITE = "oem_repeate_incall_unlimite";
  public static final String OEM_TREE_KEY_DEFINE = "oem_three_key_define";
  public static final String ONEPLUS_CLOUD_PACKAGE = "com.oneplus.cloud";
  public static final int ONEPLUS_METRICSLOGGER = 9999;
  public static final String TAG = "OPUtils";
  public static final String TRACKER_CATEGORY = "OPSettings";
  public static final String ZH_CN_HANS_ID = "zh-Hans-CN";
  public static final String ZH_CN_ID = "zh-CN";
  public static final String ZH_CN_LABEL = "zh_CN";
  public static Boolean isExist_Cloud_Package = null;
  private static AppTracker mAppTracker;
  public static boolean mAppUpdated = false;
  
  public static boolean checkNetworkAviliable(Context paramContext)
  {
    return isConnected(paramContext);
  }
  
  public static int compositeColor(int paramInt1, int paramInt2)
  {
    int i = Color.alpha(paramInt1);
    int j = Color.alpha(paramInt2);
    int k = 255 - (255 - j) * (255 - i) / 255;
    return Color.argb(k, compositeColorComponent(Color.red(paramInt1), i, Color.red(paramInt2), j, k), compositeColorComponent(Color.green(paramInt1), i, Color.green(paramInt2), j, k), compositeColorComponent(Color.blue(paramInt1), i, Color.blue(paramInt2), j, k));
  }
  
  private static int compositeColorComponent(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (paramInt5 == 0) {
      return 0;
    }
    return (paramInt3 * 255 * paramInt4 + paramInt1 * paramInt2 * (255 - paramInt4)) / paramInt5 / 255;
  }
  
  public static ColorStateList creatOneplusPrimaryColorStateList(Context paramContext)
  {
    int i = getOnePlusPrimaryColor(paramContext);
    int j = paramContext.getResources().getColor(2131493759);
    int k = paramContext.getResources().getColor(2131493760);
    return createColorStateList(k, k, i, j);
  }
  
  public static ColorStateList createColorStateList(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int[] arrayOfInt1 = { 16842919 };
    int[] arrayOfInt2 = new int[0];
    return new ColorStateList(new int[][] { arrayOfInt1, { 16842913 }, { 16842910 }, arrayOfInt2 }, new int[] { paramInt1, paramInt2, paramInt3, paramInt4 });
  }
  
  public static int dip2px(Context paramContext, float paramFloat)
  {
    return (int)(paramFloat * paramContext.getResources().getDisplayMetrics().density + 0.5F);
  }
  
  public static int dp2Px(DisplayMetrics paramDisplayMetrics, float paramFloat)
  {
    return (int)TypedValue.applyDimension(1, paramFloat, paramDisplayMetrics);
  }
  
  private static String formatMemoryDisplay(long paramLong)
  {
    paramLong = 1024L * paramLong / 1000000L;
    int i = (int)(paramLong / 512L);
    int j = (int)(paramLong % 512L);
    if (i == 0) {
      return paramLong + "MB";
    }
    if (j > 256)
    {
      i += 1;
      if (i % 2 == 0) {
        return (int)(i * 0.5F) + "GB";
      }
      return i * 0.5F + "GB";
    }
    return i * 0.5F + 0.25F + "GB";
  }
  
  public static int getAccentColor(Context paramContext)
  {
    TypedValue localTypedValue = new TypedValue();
    paramContext.getTheme().resolveAttribute(16843829, localTypedValue, true);
    return paramContext.getColor(localTypedValue.resourceId);
  }
  
  public static Bitmap getActivityIcon(Bitmap paramBitmap, @ColorInt int paramInt)
  {
    if (paramBitmap == null) {
      return paramBitmap;
    }
    Paint localPaint = new Paint();
    localPaint.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.SRC_IN));
    Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
    new Canvas(localBitmap).drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint);
    return localBitmap;
  }
  
  private static ApplicationInfo getApplicationInfo(Context paramContext, String paramString)
  {
    paramContext = paramContext.getPackageManager();
    try
    {
      paramContext = paramContext.getApplicationInfo(paramString, 0);
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      paramContext.printStackTrace();
    }
    return null;
  }
  
  public static Bitmap getBitmap(Context paramContext, int paramInt)
    throws Resources.NotFoundException
  {
    if (Build.VERSION.SDK_INT > 21)
    {
      paramContext = paramContext.getDrawable(paramInt);
      Bitmap localBitmap = Bitmap.createBitmap(paramContext.getIntrinsicWidth(), paramContext.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
      Canvas localCanvas = new Canvas(localBitmap);
      paramContext.setBounds(0, 0, localCanvas.getWidth(), localCanvas.getHeight());
      paramContext.draw(localCanvas);
      return localBitmap;
    }
    return BitmapFactory.decodeResource(paramContext.getResources(), paramInt);
  }
  
  public static int getColor(Resources.Theme paramTheme, int paramInt)
  {
    TypedValue localTypedValue = new TypedValue();
    paramTheme.resolveAttribute(paramInt, localTypedValue, true);
    return localTypedValue.data;
  }
  
  public static UserInfo getCorpUserInfo(Context paramContext)
  {
    paramContext = (UserManager)paramContext.getSystemService("user");
    int i = paramContext.getUserHandle();
    Iterator localIterator = paramContext.getUsers().iterator();
    while (localIterator.hasNext())
    {
      UserInfo localUserInfo1 = (UserInfo)localIterator.next();
      if (localUserInfo1.isManagedProfile())
      {
        UserInfo localUserInfo2 = paramContext.getProfileParent(localUserInfo1.id);
        if ((localUserInfo2 != null) && (localUserInfo2.id == i)) {
          return localUserInfo1;
        }
      }
    }
    return null;
  }
  
  public static String getDeviceModel()
  {
    Log.i("OPUtils", "DeviceModel = " + Build.MODEL);
    return Build.MODEL;
  }
  
  public static String getFileNameNoEx(String paramString)
  {
    if ((paramString != null) && (paramString.length() > 0))
    {
      int i = paramString.lastIndexOf('.');
      if ((i > -1) && (i < paramString.length())) {
        return paramString.substring(0, i);
      }
    }
    return paramString;
  }
  
  public static int getFingerprintScaleAnimStep(Context paramContext)
  {
    if (isSurportBackFingerprint(paramContext)) {
      return 8;
    }
    return 10;
  }
  
  public static String getImei(Context paramContext)
  {
    paramContext = ((TelephonyManager)paramContext.getSystemService("phone")).getDeviceId();
    if (paramContext == null)
    {
      Log.i("OPUtils", "IMEI is null");
      return "";
    }
    Log.i("OPUtils", "IMEI = " + paramContext);
    return paramContext;
  }
  
  public static int getOnePlusPrimaryColor(Context paramContext)
  {
    return paramContext.getColor(2131493777);
  }
  
  public static int getRightTheme(ContentResolver paramContentResolver, @StyleRes int paramInt1, @StyleRes int paramInt2)
  {
    if (isBlackModeOn(paramContentResolver)) {
      return paramInt2;
    }
    return paramInt1;
  }
  
  public static int getRightTheme(Context paramContext, @StyleRes int paramInt1, @StyleRes int paramInt2)
  {
    return getRightTheme(paramContext.getContentResolver(), paramInt1, paramInt2);
  }
  
  public static Bitmap getTintSvgBitmap(Context paramContext, int paramInt1, @ColorInt int paramInt2)
    throws Resources.NotFoundException
  {
    paramContext = getBitmap(paramContext, paramInt1);
    Bitmap localBitmap = tintBitmap(paramContext, paramInt2);
    paramContext.recycle();
    return localBitmap;
  }
  
  public static String getTotalMemory()
  {
    Object localObject = "";
    try
    {
      String str = new BufferedReader(new FileReader("/proc/meminfo"), 8192).readLine();
      localObject = str;
      str = str.substring(10);
      localObject = str;
      str = str.trim();
      localObject = str;
      str = str.substring(0, str.length() - 2);
      localObject = str;
      str = str.trim();
      localObject = str;
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
    return formatMemoryDisplay(Long.parseLong((String)localObject));
  }
  
  private static List<LocaleStore.LocaleInfo> getUserLocaleList(Context paramContext)
  {
    ArrayList localArrayList = new ArrayList();
    LocaleList localLocaleList = LocalePicker.getLocales();
    int i = 0;
    while (i < localLocaleList.size())
    {
      Locale localLocale = localLocaleList.get(i);
      paramContext = localLocale;
      if ("zh-CN".equals(LocaleStore.getLocaleInfo(localLocale).getId())) {
        paramContext = Locale.forLanguageTag("zh-Hans-CN");
      }
      localArrayList.add(LocaleStore.getLocaleInfo(paramContext));
      i += 1;
    }
    return localArrayList;
  }
  
  public static boolean hasMultiAppProfiles(UserManager paramUserManager)
  {
    boolean bool2 = false;
    paramUserManager = paramUserManager.getProfiles(UserHandle.myUserId()).iterator();
    do
    {
      bool1 = bool2;
      if (!paramUserManager.hasNext()) {
        break;
      }
    } while (((UserInfo)paramUserManager.next()).id != 999);
    boolean bool1 = true;
    return bool1;
  }
  
  public static void installMultiApp(Context paramContext, String paramString, int paramInt)
  {
    Log.e("OPUtils", "installMultiApp" + paramString);
    paramContext = paramContext.getPackageManager();
    for (;;)
    {
      try
      {
        paramInt = paramContext.installExistingPackageAsUser(paramString, paramInt);
        switch (paramInt)
        {
        case 1: 
          Log.e("OPUtils", "Could not install mobile device management app on managed profile. Unknown status: " + paramInt);
          return;
        }
      }
      catch (Exception paramContext)
      {
        Log.e("OPUtils", "This should not happen.", paramContext);
        return;
      }
      Log.e("OPUtils", "installMultiApp" + paramString + "success");
      return;
      Log.e("OPUtils", "Could not install mobile device management app on managed profile because the user is restricted");
      Log.e("OPUtils", "Could not install mobile device management app on managed profile because the package could not be found");
    }
  }
  
  public static boolean isActionExist(Context paramContext, Intent paramIntent, String paramString)
  {
    boolean bool = false;
    PackageManager localPackageManager = paramContext.getPackageManager();
    if (paramIntent == null) {}
    for (paramContext = new Intent();; paramContext = (Intent)paramIntent.clone())
    {
      paramContext.setAction(paramString);
      if (localPackageManager.queryIntentActivities(paramContext, 65536).size() > 0) {
        bool = true;
      }
      return bool;
    }
  }
  
  public static boolean isAndroidModeOn(ContentResolver paramContentResolver)
  {
    boolean bool = false;
    if (Settings.System.getIntForUser(paramContentResolver, "oem_black_mode", 0, 0) == 2) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isAppExist(Context paramContext, String paramString)
  {
    boolean bool = false;
    if ("com.oneplus.cloud".equals(paramString))
    {
      if (isExist_Cloud_Package == null)
      {
        if (getApplicationInfo(paramContext, paramString) != null) {}
        for (isExist_Cloud_Package = Boolean.valueOf(true);; isExist_Cloud_Package = Boolean.valueOf(false)) {
          return isExist_Cloud_Package.booleanValue();
        }
      }
      return isExist_Cloud_Package.booleanValue();
    }
    if (getApplicationInfo(paramContext, paramString) != null) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isAppPakExist(Context paramContext, String paramString)
  {
    boolean bool = false;
    PackageManager localPackageManager = paramContext.getPackageManager();
    paramContext = null;
    try
    {
      paramString = localPackageManager.getApplicationInfo(paramString, 0);
      paramContext = paramString;
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      for (;;)
      {
        paramString.printStackTrace();
      }
    }
    if (paramContext != null) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isBetaRom()
  {
    String str1 = SystemProperties.get("ro.build.beta");
    String str2 = SystemProperties.get("persist.op.ga");
    return ("1".equals(str1)) || ("1".equals(str2));
  }
  
  public static boolean isBlackModeOn(ContentResolver paramContentResolver)
  {
    return Settings.System.getIntForUser(paramContentResolver, "oem_black_mode", 0, 0) == 1;
  }
  
  public static boolean isConnected(Context paramContext)
  {
    try
    {
      paramContext = (ConnectivityManager)paramContext.getSystemService("connectivity");
      if (paramContext != null)
      {
        paramContext = paramContext.getActiveNetworkInfo();
        if ((paramContext != null) && (paramContext.isConnected()))
        {
          paramContext = paramContext.getState();
          NetworkInfo.State localState = NetworkInfo.State.CONNECTED;
          if (paramContext == localState) {
            return true;
          }
        }
      }
    }
    catch (Exception paramContext)
    {
      paramContext.printStackTrace();
    }
    return false;
  }
  
  public static boolean isEn(Context paramContext)
  {
    return "en".equals(paramContext.getResources().getConfiguration().locale.getLanguage());
  }
  
  public static boolean isFeatureSupport(Context paramContext, String paramString)
  {
    return paramContext.getPackageManager().hasSystemFeature(paramString);
  }
  
  public static boolean isGuestMode()
  {
    boolean bool = false;
    if (UserHandle.myUserId() != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isLTRLayout(Context paramContext)
  {
    boolean bool = false;
    if (paramContext.getResources().getConfiguration().getLayoutDirection() == 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isMultiAppProfiles(UserHandle paramUserHandle)
  {
    return 999 == paramUserHandle.getIdentifier();
  }
  
  public static boolean isNumeric(String paramString)
  {
    return Pattern.compile("[0-9]*").matcher(paramString).matches();
  }
  
  public static boolean isO2()
  {
    return OpFeatures.isSupport(new int[] { 1 });
  }
  
  public static boolean isStarWarModeOn(ContentResolver paramContentResolver)
  {
    return Settings.System.getIntForUser(paramContentResolver, "oem_special_theme", 0, 0) == 1;
  }
  
  public static boolean isSupportFontStyleSetting()
  {
    Locale.getDefault().getCountry();
    String str = Locale.getDefault().getLanguage();
    Log.d("OPUtils", "language = " + str);
    String[] arrayOfString = SettingsBaseApplication.mApplication.getResources().getStringArray(2131427499);
    int i = 0;
    while (i < arrayOfString.length)
    {
      if (arrayOfString[i].equalsIgnoreCase(str)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static boolean isSurportBackFingerprint(Context paramContext)
  {
    return paramContext.getResources().getBoolean(17956971);
  }
  
  public static boolean isSurportGesture20(Context paramContext)
  {
    return paramContext.getPackageManager().hasSystemFeature("oem.blackScreenGesture_2.support");
  }
  
  public static boolean isSurportNavigationBarOnly(Context paramContext)
  {
    return paramContext.getResources().getBoolean(17956971);
  }
  
  public static boolean isSurportNoNeedPowerOnPassword(Context paramContext)
  {
    return paramContext.getPackageManager().hasSystemFeature("oem.no_need_power_on_password.support");
  }
  
  public static boolean isSurportProductInfo(Context paramContext)
  {
    if (!isSurportProductInfo16859(paramContext)) {
      return isSurportProductInfo17801(paramContext);
    }
    return true;
  }
  
  public static boolean isSurportProductInfo16859(Context paramContext)
  {
    return paramContext.getPackageManager().hasSystemFeature("oem.product_info_cheeseburger.support");
  }
  
  public static boolean isSurportProductInfo17801(Context paramContext)
  {
    return paramContext.getPackageManager().hasSystemFeature("oem.product_info_dumpling.support");
  }
  
  public static boolean isSurportSimNfc(Context paramContext)
  {
    return paramContext.getPackageManager().hasSystemFeature("oem.sim_nfc.support");
  }
  
  public static boolean isWhiteModeOn(ContentResolver paramContentResolver)
  {
    boolean bool = false;
    if (Settings.System.getIntForUser(paramContentResolver, "oem_black_mode", 0, 0) == 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isZh(Context paramContext)
  {
    return paramContext.getResources().getConfiguration().locale.getLanguage().endsWith("zh");
  }
  
  public static boolean isZhCn(Context paramContext)
  {
    return paramContext.getResources().getConfiguration().locale.getCountry().equals("CN");
  }
  
  public static int px2dip(Context paramContext, float paramFloat)
  {
    return (int)(paramFloat / paramContext.getResources().getDisplayMetrics().density + 0.5F);
  }
  
  /* Error */
  public static String readFile(String paramString)
  {
    // Byte code:
    //   0: ldc_w 737
    //   3: astore_3
    //   4: aconst_null
    //   5: astore_1
    //   6: aconst_null
    //   7: astore_2
    //   8: new 424	java/io/BufferedReader
    //   11: dup
    //   12: new 426	java/io/FileReader
    //   15: dup
    //   16: aload_0
    //   17: invokespecial 431	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   20: invokespecial 740	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   23: astore_0
    //   24: aload_0
    //   25: invokevirtual 437	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   28: astore_1
    //   29: aload_0
    //   30: ifnull +7 -> 37
    //   33: aload_0
    //   34: invokevirtual 743	java/io/BufferedReader:close	()V
    //   37: aload_1
    //   38: areturn
    //   39: astore_0
    //   40: aload_0
    //   41: invokevirtual 744	java/io/IOException:printStackTrace	()V
    //   44: goto -7 -> 37
    //   47: astore_1
    //   48: aload_2
    //   49: astore_0
    //   50: aload_1
    //   51: astore_2
    //   52: aload_0
    //   53: astore_1
    //   54: aload_2
    //   55: invokevirtual 744	java/io/IOException:printStackTrace	()V
    //   58: aload_3
    //   59: astore_1
    //   60: aload_0
    //   61: ifnull -24 -> 37
    //   64: aload_0
    //   65: invokevirtual 743	java/io/BufferedReader:close	()V
    //   68: ldc_w 737
    //   71: areturn
    //   72: astore_0
    //   73: aload_0
    //   74: invokevirtual 744	java/io/IOException:printStackTrace	()V
    //   77: ldc_w 737
    //   80: areturn
    //   81: astore_0
    //   82: aload_1
    //   83: ifnull +7 -> 90
    //   86: aload_1
    //   87: invokevirtual 743	java/io/BufferedReader:close	()V
    //   90: aload_0
    //   91: athrow
    //   92: astore_1
    //   93: aload_1
    //   94: invokevirtual 744	java/io/IOException:printStackTrace	()V
    //   97: goto -7 -> 90
    //   100: astore_2
    //   101: aload_0
    //   102: astore_1
    //   103: aload_2
    //   104: astore_0
    //   105: goto -23 -> 82
    //   108: astore_2
    //   109: goto -57 -> 52
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	112	0	paramString	String
    //   5	33	1	str1	String
    //   47	4	1	localIOException1	IOException
    //   53	34	1	str2	String
    //   92	2	1	localIOException2	IOException
    //   102	1	1	str3	String
    //   7	48	2	localIOException3	IOException
    //   100	4	2	localObject	Object
    //   108	1	2	localIOException4	IOException
    //   3	56	3	str4	String
    // Exception table:
    //   from	to	target	type
    //   33	37	39	java/io/IOException
    //   8	24	47	java/io/IOException
    //   64	68	72	java/io/IOException
    //   8	24	81	finally
    //   54	58	81	finally
    //   86	90	92	java/io/IOException
    //   24	29	100	finally
    //   24	29	108	java/io/IOException
  }
  
  public static void removeMultiApp(Context paramContext, String paramString)
  {
    Log.e("OPUtils", "removeMultiApp ," + paramString);
    PackageManager localPackageManager = paramContext.getPackageManager();
    try
    {
      paramContext = getCorpUserInfo(paramContext);
      if (paramContext != null)
      {
        Log.d("OPUtils", "removeMultiApp-uid:" + paramContext.id);
        if (paramContext.id == 999) {
          localPackageManager.deletePackageAsUser(paramString, null, 0, paramContext.id);
        }
      }
      return;
    }
    catch (Exception paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  public static void removeMultiApp(Context paramContext, String paramString, int paramInt)
  {
    Log.e("OPUtils", "removeMultiApp ," + paramString);
    paramContext = paramContext.getPackageManager();
    try
    {
      paramContext.deletePackageAsUser(paramString, null, 0, paramInt);
      return;
    }
    catch (Exception paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  public static void replaceZhCnToZhCnHANS(Context paramContext)
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        Object localObject = OPUtils.-wrap0(this.val$context);
        int j = ((List)localObject).size();
        Locale[] arrayOfLocale = new Locale[j];
        int i = 0;
        while (i < j)
        {
          arrayOfLocale[i] = ((LocaleStore.LocaleInfo)((List)localObject).get(i)).getLocale();
          i += 1;
        }
        localObject = new LocaleList(arrayOfLocale);
        LocaleList.setDefault((LocaleList)localObject);
        LocalePicker.updateLocales((LocaleList)localObject);
      }
    }).start();
  }
  
  public static void sendAppTracker(String paramString, int paramInt)
  {
    mAppTracker = new AppTracker(SettingsBaseApplication.mApplication);
    HashMap localHashMap = new HashMap();
    localHashMap.put(paramString, Integer.toString(paramInt));
    if ((localHashMap != null) && (localHashMap.size() > 0)) {
      mAppTracker.onEvent(paramString, localHashMap);
    }
    sendGoogleTracker("OPSettings", paramString, Integer.toString(paramInt));
  }
  
  public static void sendAppTracker(String paramString, Long paramLong)
  {
    mAppTracker = new AppTracker(SettingsBaseApplication.mApplication);
    HashMap localHashMap = new HashMap();
    localHashMap.put(paramString, Long.toString(paramLong.longValue()));
    if ((localHashMap != null) && (localHashMap.size() > 0)) {
      mAppTracker.onEvent(paramString, localHashMap);
    }
    sendGoogleTracker("OPSettings", paramString, Long.toString(paramLong.longValue()));
  }
  
  public static void sendAppTracker(String paramString1, String paramString2)
  {
    mAppTracker = new AppTracker(SettingsBaseApplication.mApplication);
    HashMap localHashMap = new HashMap();
    localHashMap.put(paramString1, paramString2);
    if ((localHashMap != null) && (localHashMap.size() > 0)) {
      mAppTracker.onEvent(paramString1, localHashMap);
    }
    sendGoogleTracker("OPSettings", paramString1, paramString2);
  }
  
  public static void sendAppTracker(String paramString, boolean paramBoolean)
  {
    mAppTracker = new AppTracker(SettingsBaseApplication.mApplication);
    HashMap localHashMap = new HashMap();
    localHashMap.put(paramString, Boolean.toString(paramBoolean));
    if ((localHashMap != null) && (localHashMap.size() > 0)) {
      mAppTracker.onEvent(paramString, localHashMap);
    }
    sendGoogleTracker("OPSettings", paramString, Boolean.toString(paramBoolean));
  }
  
  public static void sendGoogleTracker(String paramString1, String paramString2, String paramString3)
  {
    SettingsBaseApplication localSettingsBaseApplication = (SettingsBaseApplication)SettingsBaseApplication.mApplication;
    if ((localSettingsBaseApplication != null) && (localSettingsBaseApplication.isBetaRom())) {
      localSettingsBaseApplication.getDefaultTracker().send(MapBuilder.createEvent(paramString1, paramString3, paramString2, null).build());
    }
  }
  
  public static void setAppUpdated(boolean paramBoolean)
  {
    mAppUpdated = paramBoolean;
    Log.i("OPUtils", "setAppUpdated:" + mAppUpdated);
  }
  
  public static void setListDivider(Context paramContext, ListView paramListView, int paramInt1, int paramInt2, int paramInt3)
  {
    paramContext.getResources().getConfiguration();
    Resources localResources = paramContext.getResources();
    if (isLTRLayout(paramContext)) {
      paramListView.setDivider(localResources.getDrawable(paramInt1));
    }
    for (;;)
    {
      paramListView.setDividerHeight(localResources.getDimensionPixelSize(paramInt3));
      return;
      paramListView.setDivider(localResources.getDrawable(paramInt2));
    }
  }
  
  public static void setPreferenceDivider(Context paramContext, SettingsPreferenceFragment paramSettingsPreferenceFragment, int paramInt1, int paramInt2, int paramInt3)
  {
    paramContext.getResources().getConfiguration();
    Resources localResources = paramContext.getResources();
    if (isLTRLayout(paramContext)) {
      paramSettingsPreferenceFragment.setDivider(localResources.getDrawable(paramInt1));
    }
    for (;;)
    {
      float f = localResources.getDimensionPixelSize(paramInt3);
      paramSettingsPreferenceFragment.setDividerHeight(dip2px(paramContext, 1.0F));
      return;
      paramSettingsPreferenceFragment.setDivider(localResources.getDrawable(paramInt2));
    }
  }
  
  public static String showROMStorage()
  {
    StatFs localStatFs = new StatFs(Environment.getDataDirectory().getPath());
    long l = localStatFs.getBlockSize();
    l = localStatFs.getBlockCount() * (l / 1024L);
    if (l >= 268435456L) {
      return "512GB";
    }
    if (l >= 134217728L) {
      return "256GB";
    }
    if (l >= 67108864L) {
      return "128GB";
    }
    if (l >= 33554432L) {
      return "64GB";
    }
    if (l >= 16777216L) {
      return "32GB";
    }
    return "16GB";
  }
  
  public static Bitmap tintBitmap(Bitmap paramBitmap, @ColorInt int paramInt)
  {
    if (paramBitmap == null) {
      return paramBitmap;
    }
    Paint localPaint = new Paint();
    localPaint.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.SRC_IN));
    Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
    new Canvas(localBitmap).drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint);
    return localBitmap;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\utils\OPUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */