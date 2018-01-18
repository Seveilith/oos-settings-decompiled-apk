package com.oneplus.settings.utils;

import android.content.Context;
import android.content.res.Resources;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.NativeTextHelper;

public class OPSNSUtils
{
  public static int findSlotIdBySubId(int paramInt)
  {
    int j = TelephonyManager.getDefault().getPhoneCount();
    int i = 0;
    while (i < j)
    {
      int[] arrayOfInt = SubscriptionManager.getSubId(i);
      if ((arrayOfInt != null) && (arrayOfInt.length > 0) && (paramInt == arrayOfInt[0])) {
        return i;
      }
      i += 1;
    }
    return 0;
  }
  
  public static int findSubIdBySlotId(int paramInt)
  {
    int[] arrayOfInt = SubscriptionManager.getSubId(paramInt);
    if (arrayOfInt != null) {
      return arrayOfInt[0];
    }
    return -1;
  }
  
  public static String getLocalString(Context paramContext, String paramString)
  {
    String str = "";
    try
    {
      paramContext = NativeTextHelper.getLocalString(paramContext, paramString, 17236059, 17236060);
      Log.i("OPSNSUtils", " getLocalString result:" + paramContext);
      return paramContext;
    }
    catch (Exception paramContext)
    {
      for (;;)
      {
        paramContext.printStackTrace();
        Log.e("OPSNSUtils", "getLocalString error String:" + paramString);
        paramContext = str;
      }
    }
  }
  
  public static String getOpeName(Context paramContext, int paramInt)
  {
    if (paramContext == null) {
      return null;
    }
    String str = null;
    SubscriptionInfo localSubscriptionInfo = SubscriptionManager.from(paramContext).getActiveSubscriptionInfoForSimSlotIndex(paramInt);
    if (localSubscriptionInfo != null) {
      str = TelephonyManager.getDefault().getSimOperator(localSubscriptionInfo.getSubscriptionId());
    }
    logd("simOpe = " + str + ", slot = " + paramInt);
    if ((str != null) && ((str.startsWith("46000")) || (str.startsWith("46002")) || (str.startsWith("46007")))) {
      return paramContext.getResources().getString(2131690267);
    }
    if ((str != null) && ((str.startsWith("46001")) || (str.startsWith("46009")))) {
      return paramContext.getResources().getString(2131690268);
    }
    if ((str != null) && ((str.startsWith("46003")) || (str.startsWith("46006")) || (str.startsWith("46011")))) {
      return paramContext.getResources().getString(2131690269);
    }
    paramContext = null;
    if (localSubscriptionInfo != null) {
      paramContext = TelephonyManager.getDefault().getNetworkOperatorName(localSubscriptionInfo.getSubscriptionId());
    }
    if (paramContext != null) {
      return paramContext;
    }
    return null;
  }
  
  public static String getSimName(Context paramContext, int paramInt)
  {
    return getSimName(paramContext, paramInt, true);
  }
  
  public static String getSimName(Context paramContext, int paramInt, boolean paramBoolean)
  {
    if (paramContext == null) {
      return null;
    }
    Object localObject1 = null;
    Object localObject2 = null;
    SubscriptionInfo localSubscriptionInfo = SubscriptionManager.from(paramContext).getActiveSubscriptionInfoForSimSlotIndex(paramInt);
    if (localSubscriptionInfo != null)
    {
      localObject1 = localObject2;
      if (localSubscriptionInfo.getDisplayName() != null) {
        localObject1 = localSubscriptionInfo.getDisplayName().toString();
      }
      logd("mSlotName = " + (String)localObject1);
    }
    localObject2 = localObject1;
    if (TextUtils.isEmpty((CharSequence)localObject1))
    {
      localObject2 = localObject1;
      if (paramBoolean) {
        localObject2 = paramContext.getResources().getString(2131690266, new Object[] { Integer.valueOf(paramInt + 1) });
      }
    }
    return (String)localObject2;
  }
  
  private static void logd(String paramString)
  {
    Log.d("OPSNSUtils", paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\utils\OPSNSUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */