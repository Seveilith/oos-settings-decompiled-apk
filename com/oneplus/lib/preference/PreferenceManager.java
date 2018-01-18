package com.oneplus.lib.preference;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

public class PreferenceManager
{
  public static final String KEY_HAS_SET_DEFAULT_VALUES = "_has_set_default_values";
  public static final String METADATA_KEY_PREFERENCES = "com.oneplus.lib.preference";
  private static final int STORAGE_DEFAULT = 0;
  private static final int STORAGE_DEVICE_PROTECTED = 1;
  private static final String TAG = "PreferenceManager";
  private Activity mActivity;
  private List<OnActivityDestroyListener> mActivityDestroyListeners;
  private List<OnActivityResultListener> mActivityResultListeners;
  private List<OnActivityStopListener> mActivityStopListeners;
  private Context mContext;
  private SharedPreferences.Editor mEditor;
  private PreferenceFragment mFragment;
  private long mNextId = 0L;
  private int mNextRequestCode;
  private boolean mNoCommit;
  private OnPreferenceTreeClickListener mOnPreferenceTreeClickListener;
  private PreferenceScreen mPreferenceScreen;
  private List<DialogInterface> mPreferencesScreens;
  private SharedPreferences mSharedPreferences;
  private int mSharedPreferencesMode;
  private String mSharedPreferencesName;
  private int mStorage = 0;
  
  public PreferenceManager(Activity paramActivity, int paramInt)
  {
    this.mActivity = paramActivity;
    this.mNextRequestCode = paramInt;
    init(paramActivity);
  }
  
  PreferenceManager(Context paramContext)
  {
    init(paramContext);
  }
  
  private void dismissAllScreens()
  {
    try
    {
      Object localObject1 = this.mPreferencesScreens;
      if (localObject1 == null) {
        return;
      }
      localObject1 = new ArrayList(this.mPreferencesScreens);
      this.mPreferencesScreens.clear();
      int i = ((ArrayList)localObject1).size() - 1;
      while (i >= 0)
      {
        ((DialogInterface)((ArrayList)localObject1).get(i)).dismiss();
        i -= 1;
      }
      return;
    }
    finally {}
  }
  
  public static SharedPreferences getDefaultSharedPreferences(Context paramContext)
  {
    return paramContext.getSharedPreferences(getDefaultSharedPreferencesName(paramContext), getDefaultSharedPreferencesMode());
  }
  
  private static int getDefaultSharedPreferencesMode()
  {
    return 0;
  }
  
  private static String getDefaultSharedPreferencesName(Context paramContext)
  {
    return paramContext.getPackageName() + "_preferences";
  }
  
  private void init(Context paramContext)
  {
    this.mContext = paramContext;
    setSharedPreferencesName(getDefaultSharedPreferencesName(paramContext));
  }
  
  private List<ResolveInfo> queryIntentActivities(Intent paramIntent)
  {
    return this.mContext.getPackageManager().queryIntentActivities(paramIntent, 128);
  }
  
  public static void setDefaultValues(Context paramContext, int paramInt, boolean paramBoolean)
  {
    setDefaultValues(paramContext, getDefaultSharedPreferencesName(paramContext), getDefaultSharedPreferencesMode(), paramInt, paramBoolean);
  }
  
  public static void setDefaultValues(Context paramContext, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    SharedPreferences localSharedPreferences = paramContext.getSharedPreferences("_has_set_default_values", 0);
    if ((!paramBoolean) && (localSharedPreferences.getBoolean("_has_set_default_values", false))) {
      return;
    }
    PreferenceManager localPreferenceManager = new PreferenceManager(paramContext);
    localPreferenceManager.setSharedPreferencesName(paramString);
    localPreferenceManager.setSharedPreferencesMode(paramInt1);
    localPreferenceManager.inflateFromResource(paramContext, paramInt2, null);
    paramContext = localSharedPreferences.edit().putBoolean("_has_set_default_values", true);
    try
    {
      paramContext.apply();
      return;
    }
    catch (AbstractMethodError paramString)
    {
      paramContext.commit();
    }
  }
  
  private void setNoCommit(boolean paramBoolean)
  {
    if ((!paramBoolean) && (this.mEditor != null)) {}
    try
    {
      this.mEditor.apply();
      this.mNoCommit = paramBoolean;
      return;
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      for (;;)
      {
        this.mEditor.commit();
      }
    }
  }
  
  void addPreferencesScreen(DialogInterface paramDialogInterface)
  {
    try
    {
      if (this.mPreferencesScreens == null) {
        this.mPreferencesScreens = new ArrayList();
      }
      this.mPreferencesScreens.add(paramDialogInterface);
      return;
    }
    finally {}
  }
  
  public PreferenceScreen createPreferenceScreen(Context paramContext)
  {
    paramContext = new PreferenceScreen(paramContext, null);
    paramContext.onAttachedToHierarchy(this);
    return paramContext;
  }
  
  /* Error */
  void dispatchActivityDestroy()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aload_0
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 223	com/oneplus/lib/preference/PreferenceManager:mActivityDestroyListeners	Ljava/util/List;
    //   8: ifnull +15 -> 23
    //   11: new 85	java/util/ArrayList
    //   14: dup
    //   15: aload_0
    //   16: getfield 223	com/oneplus/lib/preference/PreferenceManager:mActivityDestroyListeners	Ljava/util/List;
    //   19: invokespecial 88	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
    //   22: astore_3
    //   23: aload_0
    //   24: monitorexit
    //   25: aload_3
    //   26: ifnull +44 -> 70
    //   29: aload_3
    //   30: invokeinterface 224 1 0
    //   35: istore_2
    //   36: iconst_0
    //   37: istore_1
    //   38: iload_1
    //   39: iload_2
    //   40: if_icmpge +30 -> 70
    //   43: aload_3
    //   44: iload_1
    //   45: invokeinterface 225 2 0
    //   50: checkcast 6	com/oneplus/lib/preference/PreferenceManager$OnActivityDestroyListener
    //   53: invokeinterface 228 1 0
    //   58: iload_1
    //   59: iconst_1
    //   60: iadd
    //   61: istore_1
    //   62: goto -24 -> 38
    //   65: astore_3
    //   66: aload_0
    //   67: monitorexit
    //   68: aload_3
    //   69: athrow
    //   70: aload_0
    //   71: invokespecial 230	com/oneplus/lib/preference/PreferenceManager:dismissAllScreens	()V
    //   74: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	75	0	this	PreferenceManager
    //   37	25	1	i	int
    //   35	6	2	j	int
    //   1	43	3	localArrayList	ArrayList
    //   65	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	23	65	finally
  }
  
  void dispatchActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    for (;;)
    {
      int i;
      try
      {
        Object localObject = this.mActivityResultListeners;
        if (localObject == null) {
          return;
        }
        localObject = new ArrayList(this.mActivityResultListeners);
        int j = ((List)localObject).size();
        i = 0;
        if ((i >= j) || (((OnActivityResultListener)((List)localObject).get(i)).onActivityResult(paramInt1, paramInt2, paramIntent))) {
          return;
        }
      }
      finally {}
      i += 1;
    }
  }
  
  void dispatchActivityStop()
  {
    try
    {
      Object localObject1 = this.mActivityStopListeners;
      if (localObject1 == null) {
        return;
      }
      localObject1 = new ArrayList(this.mActivityStopListeners);
      int j = ((List)localObject1).size();
      int i = 0;
      while (i < j)
      {
        ((OnActivityStopListener)((List)localObject1).get(i)).onActivityStop();
        i += 1;
      }
      return;
    }
    finally {}
  }
  
  void dispatchNewIntent(Intent paramIntent)
  {
    dismissAllScreens();
  }
  
  public Preference findPreference(CharSequence paramCharSequence)
  {
    if (this.mPreferenceScreen == null) {
      return null;
    }
    return this.mPreferenceScreen.findPreference(paramCharSequence);
  }
  
  Activity getActivity()
  {
    return this.mActivity;
  }
  
  Context getContext()
  {
    return this.mContext;
  }
  
  SharedPreferences.Editor getEditor()
  {
    if (this.mNoCommit)
    {
      if (this.mEditor == null) {
        this.mEditor = getSharedPreferences().edit();
      }
      return this.mEditor;
    }
    return getSharedPreferences().edit();
  }
  
  PreferenceFragment getFragment()
  {
    return this.mFragment;
  }
  
  long getNextId()
  {
    try
    {
      long l = this.mNextId;
      this.mNextId = (1L + l);
      return l;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  int getNextRequestCode()
  {
    try
    {
      int i = this.mNextRequestCode;
      this.mNextRequestCode = (i + 1);
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  OnPreferenceTreeClickListener getOnPreferenceTreeClickListener()
  {
    return this.mOnPreferenceTreeClickListener;
  }
  
  PreferenceScreen getPreferenceScreen()
  {
    return this.mPreferenceScreen;
  }
  
  public SharedPreferences getSharedPreferences()
  {
    Context localContext;
    if (this.mSharedPreferences == null) {
      switch (this.mStorage)
      {
      default: 
        localContext = this.mContext;
      }
    }
    for (;;)
    {
      this.mSharedPreferences = localContext.getSharedPreferences(this.mSharedPreferencesName, this.mSharedPreferencesMode);
      return this.mSharedPreferences;
      if (Build.VERSION.SDK_INT >= 24) {
        localContext = this.mContext.createDeviceProtectedStorageContext();
      } else {
        localContext = null;
      }
    }
  }
  
  public int getSharedPreferencesMode()
  {
    return this.mSharedPreferencesMode;
  }
  
  public String getSharedPreferencesName()
  {
    return this.mSharedPreferencesName;
  }
  
  PreferenceScreen inflateFromIntent(Intent paramIntent, PreferenceScreen paramPreferenceScreen)
  {
    List localList = queryIntentActivities(paramIntent);
    HashSet localHashSet = new HashSet();
    int i = localList.size() - 1;
    Object localObject1;
    for (;;)
    {
      if (i < 0) {
        break label248;
      }
      localObject1 = ((ResolveInfo)localList.get(i)).activityInfo;
      Object localObject2 = ((ActivityInfo)localObject1).metaData;
      paramIntent = paramPreferenceScreen;
      if (localObject2 != null)
      {
        paramIntent = paramPreferenceScreen;
        if (((Bundle)localObject2).containsKey("com.oneplus.lib.preference"))
        {
          localObject2 = ((ActivityInfo)localObject1).packageName + ":" + ((ActivityInfo)localObject1).metaData.getInt("com.oneplus.lib.preference");
          paramIntent = paramPreferenceScreen;
          if (!localHashSet.contains(localObject2)) {
            localHashSet.add(localObject2);
          }
        }
      }
      try
      {
        localObject2 = this.mContext.createPackageContext(((ActivityInfo)localObject1).packageName, 0);
        paramIntent = new PreferenceInflater((Context)localObject2, this);
        localObject1 = ((ActivityInfo)localObject1).loadXmlMetaData(((Context)localObject2).getPackageManager(), "com.oneplus.lib.preference");
        paramIntent = (PreferenceScreen)paramIntent.inflate((XmlPullParser)localObject1, paramPreferenceScreen, true);
        ((XmlResourceParser)localObject1).close();
      }
      catch (PackageManager.NameNotFoundException paramIntent)
      {
        for (;;)
        {
          Log.w("PreferenceManager", "Could not create context for " + ((ActivityInfo)localObject1).packageName + ": " + Log.getStackTraceString(paramIntent));
          paramIntent = paramPreferenceScreen;
        }
      }
      i -= 1;
      paramPreferenceScreen = paramIntent;
    }
    label248:
    paramPreferenceScreen.onAttachedToHierarchy(this);
    return paramPreferenceScreen;
  }
  
  public PreferenceScreen inflateFromResource(Context paramContext, int paramInt, PreferenceScreen paramPreferenceScreen)
  {
    setNoCommit(true);
    paramContext = (PreferenceScreen)new PreferenceInflater(paramContext, this).inflate(paramInt, paramPreferenceScreen, true);
    paramContext.onAttachedToHierarchy(this);
    setNoCommit(false);
    return paramContext;
  }
  
  public boolean isStorageDefault()
  {
    if (Build.VERSION.SDK_INT >= 24) {
      return this.mStorage == 0;
    }
    return true;
  }
  
  public boolean isStorageDeviceProtected()
  {
    if (Build.VERSION.SDK_INT >= 24) {
      return this.mStorage == 1;
    }
    return false;
  }
  
  void registerOnActivityDestroyListener(OnActivityDestroyListener paramOnActivityDestroyListener)
  {
    try
    {
      if (this.mActivityDestroyListeners == null) {
        this.mActivityDestroyListeners = new ArrayList();
      }
      if (!this.mActivityDestroyListeners.contains(paramOnActivityDestroyListener)) {
        this.mActivityDestroyListeners.add(paramOnActivityDestroyListener);
      }
      return;
    }
    finally {}
  }
  
  void registerOnActivityResultListener(OnActivityResultListener paramOnActivityResultListener)
  {
    try
    {
      if (this.mActivityResultListeners == null) {
        this.mActivityResultListeners = new ArrayList();
      }
      if (!this.mActivityResultListeners.contains(paramOnActivityResultListener)) {
        this.mActivityResultListeners.add(paramOnActivityResultListener);
      }
      return;
    }
    finally {}
  }
  
  public void registerOnActivityStopListener(OnActivityStopListener paramOnActivityStopListener)
  {
    try
    {
      if (this.mActivityStopListeners == null) {
        this.mActivityStopListeners = new ArrayList();
      }
      if (!this.mActivityStopListeners.contains(paramOnActivityStopListener)) {
        this.mActivityStopListeners.add(paramOnActivityStopListener);
      }
      return;
    }
    finally {}
  }
  
  void removePreferencesScreen(DialogInterface paramDialogInterface)
  {
    try
    {
      List localList = this.mPreferencesScreens;
      if (localList == null) {
        return;
      }
      this.mPreferencesScreens.remove(paramDialogInterface);
      return;
    }
    finally {}
  }
  
  void setFragment(PreferenceFragment paramPreferenceFragment)
  {
    this.mFragment = paramPreferenceFragment;
  }
  
  void setOnPreferenceTreeClickListener(OnPreferenceTreeClickListener paramOnPreferenceTreeClickListener)
  {
    this.mOnPreferenceTreeClickListener = paramOnPreferenceTreeClickListener;
  }
  
  boolean setPreferences(PreferenceScreen paramPreferenceScreen)
  {
    if (paramPreferenceScreen != this.mPreferenceScreen)
    {
      this.mPreferenceScreen = paramPreferenceScreen;
      return true;
    }
    return false;
  }
  
  public void setSharedPreferencesMode(int paramInt)
  {
    this.mSharedPreferencesMode = paramInt;
    this.mSharedPreferences = null;
  }
  
  public void setSharedPreferencesName(String paramString)
  {
    this.mSharedPreferencesName = paramString;
    this.mSharedPreferences = null;
  }
  
  public void setStorageDefault()
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      this.mStorage = 0;
      this.mSharedPreferences = null;
    }
  }
  
  @Deprecated
  public void setStorageDeviceEncrypted()
  {
    setStorageDeviceProtected();
  }
  
  public void setStorageDeviceProtected()
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      this.mStorage = 1;
      this.mSharedPreferences = null;
    }
  }
  
  boolean shouldCommit()
  {
    return !this.mNoCommit;
  }
  
  void unregisterOnActivityDestroyListener(OnActivityDestroyListener paramOnActivityDestroyListener)
  {
    try
    {
      if (this.mActivityDestroyListeners != null) {
        this.mActivityDestroyListeners.remove(paramOnActivityDestroyListener);
      }
      return;
    }
    finally
    {
      paramOnActivityDestroyListener = finally;
      throw paramOnActivityDestroyListener;
    }
  }
  
  void unregisterOnActivityResultListener(OnActivityResultListener paramOnActivityResultListener)
  {
    try
    {
      if (this.mActivityResultListeners != null) {
        this.mActivityResultListeners.remove(paramOnActivityResultListener);
      }
      return;
    }
    finally
    {
      paramOnActivityResultListener = finally;
      throw paramOnActivityResultListener;
    }
  }
  
  public void unregisterOnActivityStopListener(OnActivityStopListener paramOnActivityStopListener)
  {
    try
    {
      if (this.mActivityStopListeners != null) {
        this.mActivityStopListeners.remove(paramOnActivityStopListener);
      }
      return;
    }
    finally
    {
      paramOnActivityStopListener = finally;
      throw paramOnActivityStopListener;
    }
  }
  
  public static abstract interface OnActivityDestroyListener
  {
    public abstract void onActivityDestroy();
  }
  
  public static abstract interface OnActivityResultListener
  {
    public abstract boolean onActivityResult(int paramInt1, int paramInt2, Intent paramIntent);
  }
  
  public static abstract interface OnActivityStopListener
  {
    public abstract void onActivityStop();
  }
  
  public static abstract interface OnPreferenceTreeClickListener
  {
    public abstract boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\PreferenceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */