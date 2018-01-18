package com.android.settings;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.R.styleable;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public class TrustAgentUtils
{
  private static final String PERMISSION_PROVIDE_AGENT = "android.permission.PROVIDE_TRUST_AGENT";
  static final String TAG = "TrustAgentUtils";
  private static final String TRUST_AGENT_META_DATA = "android.service.trust.trustagent";
  
  public static boolean checkProvidePermission(ResolveInfo paramResolveInfo, PackageManager paramPackageManager)
  {
    paramResolveInfo = paramResolveInfo.serviceInfo.packageName;
    if (paramPackageManager.checkPermission("android.permission.PROVIDE_TRUST_AGENT", paramResolveInfo) != 0)
    {
      Log.w("TrustAgentUtils", "Skipping agent because package " + paramResolveInfo + " does not have permission " + "android.permission.PROVIDE_TRUST_AGENT" + ".");
      return false;
    }
    return true;
  }
  
  public static ComponentName getComponentName(ResolveInfo paramResolveInfo)
  {
    if ((paramResolveInfo == null) || (paramResolveInfo.serviceInfo == null)) {
      return null;
    }
    return new ComponentName(paramResolveInfo.serviceInfo.packageName, paramResolveInfo.serviceInfo.name);
  }
  
  public static TrustAgentComponentInfo getSettingsComponent(PackageManager paramPackageManager, ResolveInfo paramResolveInfo)
  {
    if ((paramResolveInfo == null) || (paramResolveInfo.serviceInfo == null)) {}
    while (paramResolveInfo.serviceInfo.metaData == null) {
      return null;
    }
    Object localObject9 = null;
    Object localObject10 = null;
    String str1 = null;
    TrustAgentComponentInfo localTrustAgentComponentInfo = new TrustAgentComponentInfo();
    Object localObject6 = null;
    Object localObject7 = null;
    Object localObject2 = null;
    Object localObject5 = null;
    Object localObject8 = null;
    String str2 = str1;
    Object localObject3 = localObject9;
    Object localObject4 = localObject10;
    label638:
    try
    {
      XmlResourceParser localXmlResourceParser = paramResolveInfo.serviceInfo.loadXmlMetaData(paramPackageManager, "android.service.trust.trustagent");
      if (localXmlResourceParser == null)
      {
        str2 = str1;
        localObject5 = localXmlResourceParser;
        localObject3 = localObject9;
        localObject6 = localXmlResourceParser;
        localObject4 = localObject10;
        localObject7 = localXmlResourceParser;
        localObject2 = localXmlResourceParser;
        Slog.w("TrustAgentUtils", "Can't find android.service.trust.trustagent meta-data");
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return null;
      }
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = localObject9;
      localObject6 = localXmlResourceParser;
      localObject4 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      paramPackageManager = paramPackageManager.getResourcesForApplication(paramResolveInfo.serviceInfo.applicationInfo);
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = localObject9;
      localObject6 = localXmlResourceParser;
      localObject4 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      AttributeSet localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
      int i;
      do
      {
        str2 = str1;
        localObject5 = localXmlResourceParser;
        localObject3 = localObject9;
        localObject6 = localXmlResourceParser;
        localObject4 = localObject10;
        localObject7 = localXmlResourceParser;
        localObject2 = localXmlResourceParser;
        i = localXmlResourceParser.next();
      } while ((i != 1) && (i != 2));
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = localObject9;
      localObject6 = localXmlResourceParser;
      localObject4 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      if (!"trust-agent".equals(localXmlResourceParser.getName()))
      {
        str2 = str1;
        localObject5 = localXmlResourceParser;
        localObject3 = localObject9;
        localObject6 = localXmlResourceParser;
        localObject4 = localObject10;
        localObject7 = localXmlResourceParser;
        localObject2 = localXmlResourceParser;
        Slog.w("TrustAgentUtils", "Meta-data does not start with trust-agent tag");
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return null;
      }
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = localObject9;
      localObject6 = localXmlResourceParser;
      localObject4 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      paramPackageManager = paramPackageManager.obtainAttributes(localAttributeSet, R.styleable.TrustAgent);
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = localObject9;
      localObject6 = localXmlResourceParser;
      localObject4 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      localTrustAgentComponentInfo.summary = paramPackageManager.getString(1);
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = localObject9;
      localObject6 = localXmlResourceParser;
      localObject4 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      localTrustAgentComponentInfo.title = paramPackageManager.getString(0);
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = localObject9;
      localObject6 = localXmlResourceParser;
      localObject4 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      str1 = paramPackageManager.getString(2);
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = str1;
      localObject6 = localXmlResourceParser;
      localObject4 = str1;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      paramPackageManager.recycle();
      localObject2 = localObject8;
      paramPackageManager = str1;
      if (localXmlResourceParser != null)
      {
        localXmlResourceParser.close();
        paramPackageManager = str1;
        localObject2 = localObject8;
      }
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      for (;;)
      {
        localObject2 = localXmlPullParserException;
        paramPackageManager = str2;
        if (localObject5 != null)
        {
          ((XmlResourceParser)localObject5).close();
          localObject2 = localXmlPullParserException;
          paramPackageManager = str2;
        }
      }
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        localObject2 = localIOException;
        paramPackageManager = (PackageManager)localObject3;
        if (localObject6 != null)
        {
          ((XmlResourceParser)localObject6).close();
          localObject2 = localIOException;
          paramPackageManager = (PackageManager)localObject3;
        }
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        localObject2 = localNameNotFoundException;
        paramPackageManager = (PackageManager)localObject4;
        if (localObject7 != null)
        {
          ((XmlResourceParser)localObject7).close();
          localObject2 = localNameNotFoundException;
          paramPackageManager = (PackageManager)localObject4;
        }
      }
    }
    finally
    {
      if (localObject2 == null) {
        break label638;
      }
      ((XmlResourceParser)localObject2).close();
    }
    if (localObject2 != null)
    {
      Slog.w("TrustAgentUtils", "Error parsing : " + paramResolveInfo.serviceInfo.packageName, (Throwable)localObject2);
      return null;
    }
    Object localObject1 = paramPackageManager;
    if (paramPackageManager != null)
    {
      localObject1 = paramPackageManager;
      if (paramPackageManager.indexOf('/') < 0) {
        localObject1 = paramResolveInfo.serviceInfo.packageName + "/" + paramPackageManager;
      }
    }
    if (localObject1 == null) {}
    for (paramPackageManager = null;; paramPackageManager = ComponentName.unflattenFromString((String)localObject1))
    {
      localTrustAgentComponentInfo.componentName = paramPackageManager;
      return localTrustAgentComponentInfo;
    }
  }
  
  public static class TrustAgentComponentInfo
  {
    RestrictedLockUtils.EnforcedAdmin admin = null;
    ComponentName componentName;
    String summary;
    String title;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\TrustAgentUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */