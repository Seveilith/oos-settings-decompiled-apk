package android.support.v7.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v7.appcompat.R.attr;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.WindowCallbackWrapper;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.TintTypedArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.Window.Callback;

abstract class AppCompatDelegateImplBase
  extends AppCompatDelegate
{
  static final boolean DEBUG = false;
  static final String EXCEPTION_HANDLER_MESSAGE_SUFFIX = ". If the resource you are trying to use is a vector resource, you may be referencing it in an unsupported way. See AppCompatDelegate.setCompatVectorFromResourcesEnabled() for more info.";
  private static final boolean SHOULD_INSTALL_EXCEPTION_HANDLER;
  private static boolean sInstalledExceptionHandler;
  private static final int[] sWindowBackgroundStyleable;
  ActionBar mActionBar;
  final AppCompatCallback mAppCompatCallback;
  final Window.Callback mAppCompatWindowCallback;
  final Context mContext;
  boolean mHasActionBar;
  private boolean mIsDestroyed;
  boolean mIsFloating;
  private boolean mIsStarted;
  MenuInflater mMenuInflater;
  final Window.Callback mOriginalWindowCallback;
  boolean mOverlayActionBar;
  boolean mOverlayActionMode;
  private CharSequence mTitle;
  final Window mWindow;
  boolean mWindowNoTitle;
  
  static
  {
    boolean bool;
    if (Build.VERSION.SDK_INT < 21)
    {
      bool = true;
      SHOULD_INSTALL_EXCEPTION_HANDLER = bool;
      if ((SHOULD_INSTALL_EXCEPTION_HANDLER) && (!sInstalledExceptionHandler)) {
        break label43;
      }
    }
    for (;;)
    {
      sWindowBackgroundStyleable = new int[] { 16842836 };
      return;
      bool = false;
      break;
      label43:
      Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
      {
        private boolean shouldWrapException(Throwable paramAnonymousThrowable)
        {
          boolean bool = false;
          if ((paramAnonymousThrowable instanceof Resources.NotFoundException))
          {
            paramAnonymousThrowable = paramAnonymousThrowable.getMessage();
            if (paramAnonymousThrowable != null)
            {
              if (!paramAnonymousThrowable.contains("drawable")) {
                bool = paramAnonymousThrowable.contains("Drawable");
              }
            }
            else {
              return bool;
            }
            return true;
          }
          return false;
        }
        
        public void uncaughtException(Thread paramAnonymousThread, Throwable paramAnonymousThrowable)
        {
          if (shouldWrapException(paramAnonymousThrowable))
          {
            Resources.NotFoundException localNotFoundException = new Resources.NotFoundException(paramAnonymousThrowable.getMessage() + ". If the resource you are trying to use is a vector resource, you may be referencing it in an unsupported way. See AppCompatDelegate.setCompatVectorFromResourcesEnabled() for more info.");
            localNotFoundException.initCause(paramAnonymousThrowable.getCause());
            localNotFoundException.setStackTrace(paramAnonymousThrowable.getStackTrace());
            this.val$defHandler.uncaughtException(paramAnonymousThread, localNotFoundException);
            return;
          }
          this.val$defHandler.uncaughtException(paramAnonymousThread, paramAnonymousThrowable);
        }
      });
      sInstalledExceptionHandler = true;
    }
  }
  
  AppCompatDelegateImplBase(Context paramContext, Window paramWindow, AppCompatCallback paramAppCompatCallback)
  {
    this.mContext = paramContext;
    this.mWindow = paramWindow;
    this.mAppCompatCallback = paramAppCompatCallback;
    this.mOriginalWindowCallback = this.mWindow.getCallback();
    if ((this.mOriginalWindowCallback instanceof AppCompatWindowCallbackBase)) {
      throw new IllegalStateException("AppCompat has already installed itself into the Window");
    }
    this.mAppCompatWindowCallback = wrapWindowCallback(this.mOriginalWindowCallback);
    this.mWindow.setCallback(this.mAppCompatWindowCallback);
    paramContext = TintTypedArray.obtainStyledAttributes(paramContext, null, sWindowBackgroundStyleable);
    paramWindow = paramContext.getDrawableIfKnown(0);
    if (paramWindow != null) {
      this.mWindow.setBackgroundDrawable(paramWindow);
    }
    paramContext.recycle();
  }
  
  public boolean applyDayNight()
  {
    return false;
  }
  
  abstract boolean dispatchKeyEvent(KeyEvent paramKeyEvent);
  
  final Context getActionBarThemedContext()
  {
    Context localContext = null;
    Object localObject = getSupportActionBar();
    if (localObject != null) {
      localContext = ((ActionBar)localObject).getThemedContext();
    }
    localObject = localContext;
    if (localContext == null) {
      localObject = this.mContext;
    }
    return (Context)localObject;
  }
  
  public final ActionBarDrawerToggle.Delegate getDrawerToggleDelegate()
  {
    return new ActionBarDrawableToggleImpl();
  }
  
  public MenuInflater getMenuInflater()
  {
    if (this.mMenuInflater == null)
    {
      initWindowDecorActionBar();
      if (this.mActionBar == null) {
        break label43;
      }
    }
    label43:
    for (Context localContext = this.mActionBar.getThemedContext();; localContext = this.mContext)
    {
      this.mMenuInflater = new SupportMenuInflater(localContext);
      return this.mMenuInflater;
    }
  }
  
  public ActionBar getSupportActionBar()
  {
    initWindowDecorActionBar();
    return this.mActionBar;
  }
  
  final CharSequence getTitle()
  {
    if ((this.mOriginalWindowCallback instanceof Activity)) {
      return ((Activity)this.mOriginalWindowCallback).getTitle();
    }
    return this.mTitle;
  }
  
  final Window.Callback getWindowCallback()
  {
    return this.mWindow.getCallback();
  }
  
  abstract void initWindowDecorActionBar();
  
  final boolean isDestroyed()
  {
    return this.mIsDestroyed;
  }
  
  public boolean isHandleNativeActionModesEnabled()
  {
    return false;
  }
  
  final boolean isStarted()
  {
    return this.mIsStarted;
  }
  
  public void onDestroy()
  {
    this.mIsDestroyed = true;
  }
  
  abstract boolean onKeyShortcut(int paramInt, KeyEvent paramKeyEvent);
  
  abstract boolean onMenuOpened(int paramInt, Menu paramMenu);
  
  abstract void onPanelClosed(int paramInt, Menu paramMenu);
  
  public void onSaveInstanceState(Bundle paramBundle) {}
  
  public void onStart()
  {
    this.mIsStarted = true;
  }
  
  public void onStop()
  {
    this.mIsStarted = false;
  }
  
  abstract void onTitleChanged(CharSequence paramCharSequence);
  
  final ActionBar peekSupportActionBar()
  {
    return this.mActionBar;
  }
  
  public void setHandleNativeActionModesEnabled(boolean paramBoolean) {}
  
  public void setLocalNightMode(int paramInt) {}
  
  public final void setTitle(CharSequence paramCharSequence)
  {
    this.mTitle = paramCharSequence;
    onTitleChanged(paramCharSequence);
  }
  
  abstract ActionMode startSupportActionModeFromWindow(ActionMode.Callback paramCallback);
  
  Window.Callback wrapWindowCallback(Window.Callback paramCallback)
  {
    return new AppCompatWindowCallbackBase(paramCallback);
  }
  
  private class ActionBarDrawableToggleImpl
    implements ActionBarDrawerToggle.Delegate
  {
    ActionBarDrawableToggleImpl() {}
    
    public Context getActionBarThemedContext()
    {
      return AppCompatDelegateImplBase.this.getActionBarThemedContext();
    }
    
    public Drawable getThemeUpIndicator()
    {
      TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(getActionBarThemedContext(), null, new int[] { R.attr.homeAsUpIndicator });
      Drawable localDrawable = localTintTypedArray.getDrawable(0);
      localTintTypedArray.recycle();
      return localDrawable;
    }
    
    public boolean isNavigationVisible()
    {
      boolean bool2 = false;
      ActionBar localActionBar = AppCompatDelegateImplBase.this.getSupportActionBar();
      boolean bool1 = bool2;
      if (localActionBar != null)
      {
        bool1 = bool2;
        if ((localActionBar.getDisplayOptions() & 0x4) != 0) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public void setActionBarDescription(int paramInt)
    {
      ActionBar localActionBar = AppCompatDelegateImplBase.this.getSupportActionBar();
      if (localActionBar != null) {
        localActionBar.setHomeActionContentDescription(paramInt);
      }
    }
    
    public void setActionBarUpIndicator(Drawable paramDrawable, int paramInt)
    {
      ActionBar localActionBar = AppCompatDelegateImplBase.this.getSupportActionBar();
      if (localActionBar != null)
      {
        localActionBar.setHomeAsUpIndicator(paramDrawable);
        localActionBar.setHomeActionContentDescription(paramInt);
      }
    }
  }
  
  class AppCompatWindowCallbackBase
    extends WindowCallbackWrapper
  {
    AppCompatWindowCallbackBase(Window.Callback paramCallback)
    {
      super();
    }
    
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
    {
      if (!AppCompatDelegateImplBase.this.dispatchKeyEvent(paramKeyEvent)) {
        return super.dispatchKeyEvent(paramKeyEvent);
      }
      return true;
    }
    
    public boolean dispatchKeyShortcutEvent(KeyEvent paramKeyEvent)
    {
      if (!super.dispatchKeyShortcutEvent(paramKeyEvent)) {
        return AppCompatDelegateImplBase.this.onKeyShortcut(paramKeyEvent.getKeyCode(), paramKeyEvent);
      }
      return true;
    }
    
    public void onContentChanged() {}
    
    public boolean onCreatePanelMenu(int paramInt, Menu paramMenu)
    {
      if ((paramInt != 0) || ((paramMenu instanceof MenuBuilder))) {
        return super.onCreatePanelMenu(paramInt, paramMenu);
      }
      return false;
    }
    
    public boolean onMenuOpened(int paramInt, Menu paramMenu)
    {
      super.onMenuOpened(paramInt, paramMenu);
      AppCompatDelegateImplBase.this.onMenuOpened(paramInt, paramMenu);
      return true;
    }
    
    public void onPanelClosed(int paramInt, Menu paramMenu)
    {
      super.onPanelClosed(paramInt, paramMenu);
      AppCompatDelegateImplBase.this.onPanelClosed(paramInt, paramMenu);
    }
    
    public boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu)
    {
      if ((paramMenu instanceof MenuBuilder)) {}
      for (MenuBuilder localMenuBuilder = (MenuBuilder)paramMenu; (paramInt == 0) && (localMenuBuilder == null); localMenuBuilder = null) {
        return false;
      }
      if (localMenuBuilder != null) {
        localMenuBuilder.setOverrideVisibleItems(true);
      }
      boolean bool = super.onPreparePanel(paramInt, paramView, paramMenu);
      if (localMenuBuilder != null) {
        localMenuBuilder.setOverrideVisibleItems(false);
      }
      return bool;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\app\AppCompatDelegateImplBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */