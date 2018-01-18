package com.android.settings.location;

import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import com.android.settings.DimmableIconPreference;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DimmableIZatIconPreference
  extends DimmableIconPreference
{
  private static final String TAG = "DimmableIZatIconPreference";
  private static Method mGetConsentMethod;
  private static Method mGetXtProxyMethod;
  private static String mIzatPackage;
  private static DexClassLoader mLoader;
  private static Class mNotifierClz;
  private static Method mShowIzatMethod;
  private static Class mXtProxyClz;
  boolean mChecked;
  
  private DimmableIZatIconPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt, null);
    paramAttributeSet = mLoader;
    Class localClass = mNotifierClz;
    InvocationHandler local1 = new InvocationHandler()
    {
      public Object invoke(Object paramAnonymousObject, Method paramAnonymousMethod, Object[] paramAnonymousArrayOfObject)
        throws Throwable
      {
        if ((paramAnonymousMethod.getName().equals("userConsentNotify")) && (paramAnonymousArrayOfObject[0] != null) && ((paramAnonymousArrayOfObject[0] instanceof Boolean)))
        {
          bool = ((Boolean)paramAnonymousArrayOfObject[0]).booleanValue();
          if (DimmableIZatIconPreference.this.mChecked != bool)
          {
            DimmableIZatIconPreference.this.mChecked = bool;
            paramAnonymousObject = DimmableIZatIconPreference.this;
            if ((!DimmableIZatIconPreference.this.isEnabled()) || (!DimmableIZatIconPreference.this.mChecked)) {
              break label95;
            }
          }
        }
        label95:
        for (boolean bool = false;; bool = true)
        {
          DimmableIZatIconPreference.-wrap0((DimmableIZatIconPreference)paramAnonymousObject, bool);
          return null;
        }
      }
    };
    paramAttributeSet = Proxy.newProxyInstance(paramAttributeSet, new Class[] { localClass }, local1);
    try
    {
      paramContext = mGetXtProxyMethod.invoke(null, new Object[] { paramContext, paramAttributeSet });
      this.mChecked = ((Boolean)mGetConsentMethod.invoke(paramContext, new Object[0])).booleanValue();
      return;
    }
    catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException|ExceptionInInitializerError paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  private static void load(Context paramContext)
  {
    if (mLoader == null) {}
    try
    {
      if ((mXtProxyClz == null) || (mNotifierClz == null))
      {
        mLoader = new DexClassLoader("/system/framework/izat.xt.srv.jar", paramContext.getFilesDir().getAbsolutePath(), null, ClassLoader.getSystemClassLoader());
        mXtProxyClz = Class.forName("com.qti.izat.XTProxy", true, mLoader);
        mNotifierClz = Class.forName("com.qti.izat.XTProxy$Notifier", true, mLoader);
        mIzatPackage = (String)mXtProxyClz.getField("IZAT_XT_PACKAGE").get(null);
        mGetXtProxyMethod = mXtProxyClz.getMethod("getXTProxy", new Class[] { Context.class, mNotifierClz });
        mGetConsentMethod = mXtProxyClz.getMethod("getUserConsent", new Class[0]);
        mShowIzatMethod = mXtProxyClz.getMethod("showIzat", new Class[] { Context.class, String.class });
      }
      return;
    }
    catch (NoSuchMethodException|NullPointerException|SecurityException|NoSuchFieldException|LinkageError|IllegalAccessException|ClassNotFoundException paramContext)
    {
      mXtProxyClz = null;
      mNotifierClz = null;
      mIzatPackage = null;
      mGetXtProxyMethod = null;
      mGetConsentMethod = null;
      mShowIzatMethod = null;
      paramContext.printStackTrace();
    }
  }
  
  public static DimmableIconPreference newInstance(Context paramContext, CharSequence paramCharSequence, InjectedSetting paramInjectedSetting)
  {
    load(paramContext);
    if ((mIzatPackage != null) && (mIzatPackage.equals(paramInjectedSetting.packageName))) {
      return new DimmableIZatIconPreference(paramContext, null, 16842895);
    }
    return new DimmableIconPreference(paramContext, paramCharSequence);
  }
  
  static boolean showIzat(Context paramContext, String paramString)
  {
    load(paramContext);
    boolean bool = true;
    try
    {
      if (mShowIzatMethod != null) {
        bool = ((Boolean)mShowIzatMethod.invoke(null, new Object[] { paramContext, paramString })).booleanValue();
      }
      return bool;
    }
    catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException|ExceptionInInitializerError paramContext)
    {
      paramContext.printStackTrace();
    }
    return true;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    boolean bool2 = true;
    super.onBindViewHolder(paramPreferenceViewHolder);
    boolean bool1 = bool2;
    if (isEnabled())
    {
      bool1 = bool2;
      if (this.mChecked) {
        bool1 = false;
      }
    }
    dimIcon(bool1);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\location\DimmableIZatIconPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */