package com.android.settingslib;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceInfoUtils
{
  private static final String FILENAME_MSV = "/sys/board_properties/soc/msv";
  private static final String FILENAME_PROC_VERSION = "/proc/version";
  private static final String TAG = "DeviceInfoUtils";
  
  public static String customizeFormatKernelVersion(boolean paramBoolean)
  {
    if (paramBoolean) {
      try
      {
        String str = readLine("/proc/version");
        Matcher localMatcher = Pattern.compile("Linux version (\\S+) \\((\\S+?)\\) (?:\\(gcc.+? \\)) (#\\d+) (?:.*?)?((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)").matcher(str);
        if (!localMatcher.matches())
        {
          Log.e("DeviceInfoUtils", "Regex did not match on /proc/version: " + str);
          return "Unavailable";
        }
        if (localMatcher.groupCount() < 4)
        {
          Log.e("DeviceInfoUtils", "Regex match on /proc/version only returned " + localMatcher.groupCount() + " groups");
          return "Unavailable";
        }
        str = localMatcher.group(1) + "\n" + localMatcher.group(4);
        return str;
      }
      catch (IOException localIOException)
      {
        Log.e("DeviceInfoUtils", "IO Exception when getting kernel version for Device Info screen", localIOException);
        return "Unavailable";
      }
    }
    return getFormattedKernelVersion();
  }
  
  public static String formatKernelVersion(String paramString)
  {
    Matcher localMatcher = Pattern.compile("Linux version (\\S+) \\((\\S+?)\\) (?:\\(gcc.+? \\)) (#\\d+) (?:.*?)?((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)").matcher(paramString);
    if (!localMatcher.matches())
    {
      Log.e("DeviceInfoUtils", "Regex did not match on /proc/version: " + paramString);
      return "Unavailable";
    }
    if (localMatcher.groupCount() < 4)
    {
      Log.e("DeviceInfoUtils", "Regex match on /proc/version only returned " + localMatcher.groupCount() + " groups");
      return "Unavailable";
    }
    return localMatcher.group(1) + "\n" + localMatcher.group(2) + " " + localMatcher.group(3) + "\n" + localMatcher.group(4);
  }
  
  public static String getFeedbackReporterPackage(Context paramContext)
  {
    String str = paramContext.getResources().getString(R.string.oem_preferred_feedback_reporter);
    if (TextUtils.isEmpty(str)) {
      return str;
    }
    Object localObject = new Intent("android.intent.action.BUG_REPORT");
    paramContext = paramContext.getPackageManager();
    localObject = paramContext.queryIntentActivities((Intent)localObject, 64).iterator();
    for (;;)
    {
      ResolveInfo localResolveInfo;
      if (((Iterator)localObject).hasNext())
      {
        localResolveInfo = (ResolveInfo)((Iterator)localObject).next();
        if ((localResolveInfo.activityInfo == null) || (TextUtils.isEmpty(localResolveInfo.activityInfo.packageName))) {}
      }
      else
      {
        try
        {
          if ((paramContext.getApplicationInfo(localResolveInfo.activityInfo.packageName, 0).flags & 0x1) == 0) {
            continue;
          }
          boolean bool = TextUtils.equals(localResolveInfo.activityInfo.packageName, str);
          if (!bool) {
            continue;
          }
          return str;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
        return null;
      }
    }
  }
  
  public static String getFormattedKernelVersion()
  {
    try
    {
      String str = formatKernelVersion(readLine("/proc/version"));
      return str;
    }
    catch (IOException localIOException)
    {
      Log.e("DeviceInfoUtils", "IO Exception when getting kernel version for Device Info screen", localIOException);
    }
    return "Unavailable";
  }
  
  public static String getFormattedPhoneNumber(Context paramContext, SubscriptionInfo paramSubscriptionInfo)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (paramSubscriptionInfo != null)
    {
      paramContext = ((TelephonyManager)paramContext.getSystemService("phone")).getLine1Number(paramSubscriptionInfo.getSubscriptionId());
      localObject1 = localObject2;
      if (!TextUtils.isEmpty(paramContext)) {
        localObject1 = PhoneNumberUtils.formatNumber(paramContext);
      }
    }
    return (String)localObject1;
  }
  
  public static String getFormattedPhoneNumbers(Context paramContext, List<SubscriptionInfo> paramList)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramList != null)
    {
      paramContext = (TelephonyManager)paramContext.getSystemService("phone");
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        String str = paramContext.getLine1Number(((SubscriptionInfo)paramList.get(i)).getSubscriptionId());
        if (!TextUtils.isEmpty(str))
        {
          localStringBuilder.append(PhoneNumberUtils.formatNumber(str));
          if (i < j - 1) {
            localStringBuilder.append("\n");
          }
        }
        i += 1;
      }
    }
    return localStringBuilder.toString();
  }
  
  public static String getMsvSuffix()
  {
    try
    {
      if (Long.parseLong(readLine("/sys/board_properties/soc/msv"), 16) == 0L) {
        return " (ENGINEERING)";
      }
    }
    catch (IOException|NumberFormatException localIOException) {}
    return "";
  }
  
  public static String getSecurityPatch()
  {
    String str = Build.VERSION.SECURITY_PATCH;
    if (!"".equals(str)) {}
    try
    {
      Object localObject = new SimpleDateFormat("yyyy-MM-dd").parse(str);
      localObject = DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "dMMMMyyyy"), (Date)localObject).toString();
      return (String)localObject;
    }
    catch (ParseException localParseException) {}
    return null;
    return str;
  }
  
  private static String readLine(String paramString)
    throws IOException
  {
    paramString = new BufferedReader(new FileReader(paramString), 256);
    try
    {
      String str = paramString.readLine();
      return str;
    }
    finally
    {
      paramString.close();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\DeviceInfoUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */