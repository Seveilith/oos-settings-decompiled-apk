package android.support.v4.content;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.BuildCompat;
import android.util.Log;
import android.util.TypedValue;
import java.io.File;

public class ContextCompat
{
  private static final String DIR_ANDROID = "Android";
  private static final String DIR_OBB = "obb";
  private static final String TAG = "ContextCompat";
  private static final Object sLock = new Object();
  private static TypedValue sTempValue;
  
  private static File buildPath(File paramFile, String... paramVarArgs)
  {
    int i = 0;
    int j = paramVarArgs.length;
    String str;
    if (i < j)
    {
      str = paramVarArgs[i];
      if (paramFile == null) {
        paramFile = new File(str);
      }
    }
    for (;;)
    {
      i += 1;
      break;
      if (str != null)
      {
        paramFile = new File(paramFile, str);
        continue;
        return paramFile;
      }
    }
  }
  
  public static int checkSelfPermission(@NonNull Context paramContext, @NonNull String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("permission is null");
    }
    return paramContext.checkPermission(paramString, Process.myPid(), Process.myUid());
  }
  
  public static Context createDeviceProtectedStorageContext(Context paramContext)
  {
    if (BuildCompat.isAtLeastN()) {
      return ContextCompatApi24.createDeviceProtectedStorageContext(paramContext);
    }
    return null;
  }
  
  private static File createFilesDir(File paramFile)
  {
    try
    {
      if ((!paramFile.exists()) && (!paramFile.mkdirs()))
      {
        boolean bool = paramFile.exists();
        if (bool) {
          return paramFile;
        }
        Log.w("ContextCompat", "Unable to create files subdir " + paramFile.getPath());
        return null;
      }
      return paramFile;
    }
    finally {}
  }
  
  public static File getCodeCacheDir(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return ContextCompatApi21.getCodeCacheDir(paramContext);
    }
    return createFilesDir(new File(paramContext.getApplicationInfo().dataDir, "code_cache"));
  }
  
  @ColorInt
  public static final int getColor(Context paramContext, @ColorRes int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return ContextCompatApi23.getColor(paramContext, paramInt);
    }
    return paramContext.getResources().getColor(paramInt);
  }
  
  public static final ColorStateList getColorStateList(Context paramContext, @ColorRes int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return ContextCompatApi23.getColorStateList(paramContext, paramInt);
    }
    return paramContext.getResources().getColorStateList(paramInt);
  }
  
  public static File getDataDir(Context paramContext)
  {
    Object localObject = null;
    if (BuildCompat.isAtLeastN()) {
      return ContextCompatApi24.getDataDir(paramContext);
    }
    String str = paramContext.getApplicationInfo().dataDir;
    paramContext = (Context)localObject;
    if (str != null) {
      paramContext = new File(str);
    }
    return paramContext;
  }
  
  public static final Drawable getDrawable(Context paramContext, @DrawableRes int paramInt)
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 21) {
      return ContextCompatApi21.getDrawable(paramContext, paramInt);
    }
    if (i >= 16) {
      return paramContext.getResources().getDrawable(paramInt);
    }
    synchronized (sLock)
    {
      if (sTempValue == null) {
        sTempValue = new TypedValue();
      }
      paramContext.getResources().getValue(paramInt, sTempValue, true);
      paramInt = sTempValue.resourceId;
      return paramContext.getResources().getDrawable(paramInt);
    }
  }
  
  public static File[] getExternalCacheDirs(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return ContextCompatKitKat.getExternalCacheDirs(paramContext);
    }
    return new File[] { paramContext.getExternalCacheDir() };
  }
  
  public static File[] getExternalFilesDirs(Context paramContext, String paramString)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return ContextCompatKitKat.getExternalFilesDirs(paramContext, paramString);
    }
    return new File[] { paramContext.getExternalFilesDir(paramString) };
  }
  
  public static final File getNoBackupFilesDir(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return ContextCompatApi21.getNoBackupFilesDir(paramContext);
    }
    return createFilesDir(new File(paramContext.getApplicationInfo().dataDir, "no_backup"));
  }
  
  public static File[] getObbDirs(Context paramContext)
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 19) {
      return ContextCompatKitKat.getObbDirs(paramContext);
    }
    if (i >= 11) {}
    for (paramContext = ContextCompatHoneycomb.getObbDir(paramContext);; paramContext = buildPath(Environment.getExternalStorageDirectory(), new String[] { "Android", "obb", paramContext.getPackageName() })) {
      return new File[] { paramContext };
    }
  }
  
  public static boolean isDeviceProtectedStorage(Context paramContext)
  {
    if (BuildCompat.isAtLeastN()) {
      return ContextCompatApi24.isDeviceProtectedStorage(paramContext);
    }
    return false;
  }
  
  public static boolean startActivities(Context paramContext, Intent[] paramArrayOfIntent)
  {
    return startActivities(paramContext, paramArrayOfIntent, null);
  }
  
  public static boolean startActivities(Context paramContext, Intent[] paramArrayOfIntent, Bundle paramBundle)
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 16)
    {
      ContextCompatJellybean.startActivities(paramContext, paramArrayOfIntent, paramBundle);
      return true;
    }
    if (i >= 11)
    {
      ContextCompatHoneycomb.startActivities(paramContext, paramArrayOfIntent);
      return true;
    }
    return false;
  }
  
  public static void startActivity(Context paramContext, Intent paramIntent, @Nullable Bundle paramBundle)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      ContextCompatJellybean.startActivity(paramContext, paramIntent, paramBundle);
      return;
    }
    paramContext.startActivity(paramIntent);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\content\ContextCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */