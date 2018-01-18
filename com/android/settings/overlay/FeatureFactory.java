package com.android.settings.overlay;

import android.content.Context;
import android.text.TextUtils;

public abstract class FeatureFactory
{
  private static final boolean DEBUG = false;
  private static final String LOG_TAG = "FeatureFactory";
  private static FeatureFactory sFactory;
  
  public static FeatureFactory getFactory(Context paramContext)
  {
    if (sFactory != null) {
      return sFactory;
    }
    String str = paramContext.getString(2131689892);
    if (TextUtils.isEmpty(str)) {
      throw new UnsupportedOperationException("No feature factory configured");
    }
    try
    {
      sFactory = (FeatureFactory)paramContext.getClassLoader().loadClass(str).newInstance();
      return sFactory;
    }
    catch (InstantiationException|IllegalAccessException|ClassNotFoundException paramContext)
    {
      throw new FactoryNotFoundException(paramContext);
    }
  }
  
  public abstract SupportFeatureProvider getSupportFeatureProvider(Context paramContext);
  
  public static final class FactoryNotFoundException
    extends RuntimeException
  {
    public FactoryNotFoundException(Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\overlay\FeatureFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */