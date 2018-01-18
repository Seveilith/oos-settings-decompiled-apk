package com.android.setupwizardlib.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager.LayoutParams;

public class SystemBarHelper
{
  @SuppressLint({"InlinedApi"})
  private static final int DEFAULT_IMMERSIVE_FLAGS = 5634;
  @SuppressLint({"InlinedApi"})
  private static final int DIALOG_IMMERSIVE_FLAGS = 4098;
  private static final int PEEK_DECOR_VIEW_RETRIES = 3;
  private static final int STATUS_BAR_DISABLE_BACK = 4194304;
  private static final String TAG = "SystemBarHelper";
  
  @TargetApi(21)
  private static void addImmersiveFlagsToDecorView(Window paramWindow, int paramInt)
  {
    getDecorView(paramWindow, new OnDecorViewInstalledListener()
    {
      public void onDecorViewInstalled(View paramAnonymousView)
      {
        SystemBarHelper.addVisibilityFlag(paramAnonymousView, this.val$vis);
      }
    });
  }
  
  public static void addVisibilityFlag(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 11) {
      paramView.setSystemUiVisibility(paramView.getSystemUiVisibility() | paramInt);
    }
  }
  
  public static void addVisibilityFlag(Window paramWindow, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 11)
    {
      WindowManager.LayoutParams localLayoutParams = paramWindow.getAttributes();
      localLayoutParams.systemUiVisibility |= paramInt;
      paramWindow.setAttributes(localLayoutParams);
    }
  }
  
  private static int getBottomDistance(View paramView)
  {
    int[] arrayOfInt = new int[2];
    paramView.getLocationInWindow(arrayOfInt);
    return paramView.getRootView().getHeight() - arrayOfInt[1] - paramView.getHeight();
  }
  
  private static void getDecorView(Window paramWindow, OnDecorViewInstalledListener paramOnDecorViewInstalledListener)
  {
    new DecorViewFinder(null).getDecorView(paramWindow, paramOnDecorViewInstalledListener, 3);
  }
  
  public static void hideSystemBars(Dialog paramDialog)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramDialog = paramDialog.getWindow();
      temporarilyDisableDialogFocus(paramDialog);
      addVisibilityFlag(paramDialog, 4098);
      addImmersiveFlagsToDecorView(paramDialog, 4098);
      paramDialog.setNavigationBarColor(0);
      paramDialog.setStatusBarColor(0);
    }
  }
  
  public static void hideSystemBars(Window paramWindow)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      addVisibilityFlag(paramWindow, 5634);
      addImmersiveFlagsToDecorView(paramWindow, 5634);
      paramWindow.setNavigationBarColor(0);
      paramWindow.setStatusBarColor(0);
    }
  }
  
  @TargetApi(21)
  private static void removeImmersiveFlagsFromDecorView(Window paramWindow, int paramInt)
  {
    getDecorView(paramWindow, new OnDecorViewInstalledListener()
    {
      public void onDecorViewInstalled(View paramAnonymousView)
      {
        SystemBarHelper.removeVisibilityFlag(paramAnonymousView, this.val$vis);
      }
    });
  }
  
  public static void removeVisibilityFlag(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 11) {
      paramView.setSystemUiVisibility(paramInt & paramView.getSystemUiVisibility());
    }
  }
  
  public static void removeVisibilityFlag(Window paramWindow, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 11)
    {
      WindowManager.LayoutParams localLayoutParams = paramWindow.getAttributes();
      localLayoutParams.systemUiVisibility &= paramInt;
      paramWindow.setAttributes(localLayoutParams);
    }
  }
  
  public static void setBackButtonVisible(Window paramWindow, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 11)
    {
      if (paramBoolean) {
        removeVisibilityFlag(paramWindow, 4194304);
      }
    }
    else {
      return;
    }
    addVisibilityFlag(paramWindow, 4194304);
  }
  
  public static void setImeInsetView(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      paramView.setOnApplyWindowInsetsListener(new WindowInsetsListener(null));
    }
  }
  
  public static void showSystemBars(Dialog paramDialog, Context paramContext)
  {
    showSystemBars(paramDialog.getWindow(), paramContext);
  }
  
  public static void showSystemBars(Window paramWindow, Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      removeVisibilityFlag(paramWindow, 5634);
      removeImmersiveFlagsFromDecorView(paramWindow, 5634);
      if (paramContext != null)
      {
        paramContext = paramContext.obtainStyledAttributes(new int[] { 16843857, 16843858 });
        int i = paramContext.getColor(0, 0);
        int j = paramContext.getColor(1, 0);
        paramWindow.setStatusBarColor(i);
        paramWindow.setNavigationBarColor(j);
        paramContext.recycle();
      }
    }
  }
  
  private static void temporarilyDisableDialogFocus(Window paramWindow)
  {
    paramWindow.setFlags(8, 8);
    paramWindow.setSoftInputMode(256);
    new Handler().post(new Runnable()
    {
      public void run()
      {
        this.val$window.clearFlags(8);
      }
    });
  }
  
  private static class DecorViewFinder
  {
    private SystemBarHelper.OnDecorViewInstalledListener mCallback;
    private Runnable mCheckDecorViewRunnable = new Runnable()
    {
      public void run()
      {
        Object localObject = SystemBarHelper.DecorViewFinder.-get4(SystemBarHelper.DecorViewFinder.this).peekDecorView();
        if (localObject != null)
        {
          SystemBarHelper.DecorViewFinder.-get0(SystemBarHelper.DecorViewFinder.this).onDecorViewInstalled((View)localObject);
          return;
        }
        localObject = SystemBarHelper.DecorViewFinder.this;
        SystemBarHelper.DecorViewFinder.-set0((SystemBarHelper.DecorViewFinder)localObject, SystemBarHelper.DecorViewFinder.-get3((SystemBarHelper.DecorViewFinder)localObject) - 1);
        if (SystemBarHelper.DecorViewFinder.-get3(SystemBarHelper.DecorViewFinder.this) >= 0)
        {
          SystemBarHelper.DecorViewFinder.-get2(SystemBarHelper.DecorViewFinder.this).post(SystemBarHelper.DecorViewFinder.-get1(SystemBarHelper.DecorViewFinder.this));
          return;
        }
        Log.w("SystemBarHelper", "Cannot get decor view of window: " + SystemBarHelper.DecorViewFinder.-get4(SystemBarHelper.DecorViewFinder.this));
      }
    };
    private final Handler mHandler = new Handler();
    private int mRetries;
    private Window mWindow;
    
    public void getDecorView(Window paramWindow, SystemBarHelper.OnDecorViewInstalledListener paramOnDecorViewInstalledListener, int paramInt)
    {
      this.mWindow = paramWindow;
      this.mRetries = paramInt;
      this.mCallback = paramOnDecorViewInstalledListener;
      this.mCheckDecorViewRunnable.run();
    }
  }
  
  private static abstract interface OnDecorViewInstalledListener
  {
    public abstract void onDecorViewInstalled(View paramView);
  }
  
  @TargetApi(21)
  private static class WindowInsetsListener
    implements View.OnApplyWindowInsetsListener
  {
    private int mBottomOffset;
    private boolean mHasCalculatedBottomOffset = false;
    
    public WindowInsets onApplyWindowInsets(View paramView, WindowInsets paramWindowInsets)
    {
      if (!this.mHasCalculatedBottomOffset)
      {
        this.mBottomOffset = SystemBarHelper.-wrap0(paramView);
        this.mHasCalculatedBottomOffset = true;
      }
      int i = paramWindowInsets.getSystemWindowInsetBottom();
      int j = Math.max(paramWindowInsets.getSystemWindowInsetBottom() - this.mBottomOffset, 0);
      ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
      if (j < localMarginLayoutParams.bottomMargin + paramView.getHeight())
      {
        localMarginLayoutParams.setMargins(localMarginLayoutParams.leftMargin, localMarginLayoutParams.topMargin, localMarginLayoutParams.rightMargin, j);
        paramView.setLayoutParams(localMarginLayoutParams);
        i = 0;
      }
      return paramWindowInsets.replaceSystemWindowInsets(paramWindowInsets.getSystemWindowInsetLeft(), paramWindowInsets.getSystemWindowInsetTop(), paramWindowInsets.getSystemWindowInsetRight(), i);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\util\SystemBarHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */