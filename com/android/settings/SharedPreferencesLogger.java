package com.android.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.android.internal.logging.MetricsLogger;
import java.util.Map;
import java.util.Set;

public class SharedPreferencesLogger
  implements SharedPreferences
{
  private final Context mContext;
  private final String mTag;
  
  public SharedPreferencesLogger(Context paramContext, String paramString)
  {
    this.mContext = paramContext;
    this.mTag = paramString;
  }
  
  private void logPackageName(String paramString1, String paramString2)
  {
    MetricsLogger.count(this.mContext, this.mTag + "/" + paramString1, 1);
    MetricsLogger.action(this.mContext, 350, this.mTag + "/" + paramString1 + "|" + paramString2);
  }
  
  private void logValue(String paramString1, String paramString2)
  {
    MetricsLogger.count(this.mContext, this.mTag + "/" + paramString1 + "|" + paramString2, 1);
  }
  
  private void safeLogValue(String paramString1, String paramString2)
  {
    new AsyncPackageCheck(null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[] { paramString1, paramString2 });
  }
  
  public boolean contains(String paramString)
  {
    return false;
  }
  
  public SharedPreferences.Editor edit()
  {
    return new EditorLogger();
  }
  
  public Map<String, ?> getAll()
  {
    return null;
  }
  
  public boolean getBoolean(String paramString, boolean paramBoolean)
  {
    return paramBoolean;
  }
  
  public float getFloat(String paramString, float paramFloat)
  {
    return paramFloat;
  }
  
  public int getInt(String paramString, int paramInt)
  {
    return paramInt;
  }
  
  public long getLong(String paramString, long paramLong)
  {
    return paramLong;
  }
  
  public String getString(String paramString1, String paramString2)
  {
    return paramString2;
  }
  
  public Set<String> getStringSet(String paramString, Set<String> paramSet)
  {
    return paramSet;
  }
  
  public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener paramOnSharedPreferenceChangeListener) {}
  
  public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener paramOnSharedPreferenceChangeListener) {}
  
  private class AsyncPackageCheck
    extends AsyncTask<String, Void, Void>
  {
    private AsyncPackageCheck() {}
    
    protected Void doInBackground(String... paramVarArgs)
    {
      String str2 = paramVarArgs[0];
      String str1 = paramVarArgs[1];
      PackageManager localPackageManager = SharedPreferencesLogger.-get0(SharedPreferencesLogger.this).getPackageManager();
      for (;;)
      {
        try
        {
          ComponentName localComponentName = ComponentName.unflattenFromString(str1);
          paramVarArgs = str1;
          if (str1 != null) {
            paramVarArgs = localComponentName.getPackageName();
          }
        }
        catch (Exception paramVarArgs)
        {
          paramVarArgs = localNameNotFoundException;
          continue;
        }
        try
        {
          localPackageManager.getPackageInfo(paramVarArgs, 8192);
          SharedPreferencesLogger.-wrap0(SharedPreferencesLogger.this, str2, paramVarArgs);
          return null;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          SharedPreferencesLogger.-wrap1(SharedPreferencesLogger.this, str2, paramVarArgs);
          return null;
        }
      }
    }
  }
  
  public class EditorLogger
    implements SharedPreferences.Editor
  {
    public EditorLogger() {}
    
    public void apply() {}
    
    public SharedPreferences.Editor clear()
    {
      return this;
    }
    
    public boolean commit()
    {
      return true;
    }
    
    public SharedPreferences.Editor putBoolean(String paramString, boolean paramBoolean)
    {
      SharedPreferencesLogger.-wrap1(SharedPreferencesLogger.this, paramString, String.valueOf(paramBoolean));
      return this;
    }
    
    public SharedPreferences.Editor putFloat(String paramString, float paramFloat)
    {
      SharedPreferencesLogger.-wrap1(SharedPreferencesLogger.this, paramString, String.valueOf(paramFloat));
      return this;
    }
    
    public SharedPreferences.Editor putInt(String paramString, int paramInt)
    {
      SharedPreferencesLogger.-wrap1(SharedPreferencesLogger.this, paramString, String.valueOf(paramInt));
      return this;
    }
    
    public SharedPreferences.Editor putLong(String paramString, long paramLong)
    {
      SharedPreferencesLogger.-wrap1(SharedPreferencesLogger.this, paramString, String.valueOf(paramLong));
      return this;
    }
    
    public SharedPreferences.Editor putString(String paramString1, String paramString2)
    {
      SharedPreferencesLogger.-wrap2(SharedPreferencesLogger.this, paramString1, paramString2);
      return this;
    }
    
    public SharedPreferences.Editor putStringSet(String paramString, Set<String> paramSet)
    {
      SharedPreferencesLogger.-wrap2(SharedPreferencesLogger.this, paramString, TextUtils.join(",", paramSet));
      return this;
    }
    
    public SharedPreferences.Editor remove(String paramString)
    {
      return this;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SharedPreferencesLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */