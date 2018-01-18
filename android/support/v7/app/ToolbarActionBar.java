package android.support.v7.app;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.layout;
import android.support.v7.appcompat.R.style;
import android.support.v7.view.WindowCallbackWrapper;
import android.support.v7.view.menu.ListMenuPresenter;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.support.v7.widget.DecorToolbar;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window.Callback;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import java.util.ArrayList;

class ToolbarActionBar
  extends ActionBar
{
  DecorToolbar mDecorToolbar;
  private boolean mLastMenuVisibility;
  private ListMenuPresenter mListMenuPresenter;
  private boolean mMenuCallbackSet;
  private final Toolbar.OnMenuItemClickListener mMenuClicker = new Toolbar.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      return ToolbarActionBar.this.mWindowCallback.onMenuItemSelected(0, paramAnonymousMenuItem);
    }
  };
  private final Runnable mMenuInvalidator = new Runnable()
  {
    public void run()
    {
      ToolbarActionBar.this.populateOptionsMenu();
    }
  };
  private ArrayList<ActionBar.OnMenuVisibilityListener> mMenuVisibilityListeners = new ArrayList();
  boolean mToolbarMenuPrepared;
  Window.Callback mWindowCallback;
  
  public ToolbarActionBar(Toolbar paramToolbar, CharSequence paramCharSequence, Window.Callback paramCallback)
  {
    this.mDecorToolbar = new ToolbarWidgetWrapper(paramToolbar, false);
    this.mWindowCallback = new ToolbarCallbackWrapper(paramCallback);
    this.mDecorToolbar.setWindowCallback(this.mWindowCallback);
    paramToolbar.setOnMenuItemClickListener(this.mMenuClicker);
    this.mDecorToolbar.setWindowTitle(paramCharSequence);
  }
  
  private void ensureListMenuPresenter(Menu paramMenu)
  {
    Object localObject;
    Resources.Theme localTheme;
    if ((this.mListMenuPresenter == null) && ((paramMenu instanceof MenuBuilder)))
    {
      paramMenu = (MenuBuilder)paramMenu;
      localObject = this.mDecorToolbar.getContext();
      TypedValue localTypedValue = new TypedValue();
      localTheme = ((Context)localObject).getResources().newTheme();
      localTheme.setTo(((Context)localObject).getTheme());
      localTheme.resolveAttribute(R.attr.actionBarPopupTheme, localTypedValue, true);
      if (localTypedValue.resourceId != 0) {
        localTheme.applyStyle(localTypedValue.resourceId, true);
      }
      localTheme.resolveAttribute(R.attr.panelMenuListTheme, localTypedValue, true);
      if (localTypedValue.resourceId == 0) {
        break label169;
      }
      localTheme.applyStyle(localTypedValue.resourceId, true);
    }
    for (;;)
    {
      localObject = new ContextThemeWrapper((Context)localObject, 0);
      ((Context)localObject).getTheme().setTo(localTheme);
      this.mListMenuPresenter = new ListMenuPresenter((Context)localObject, R.layout.abc_list_menu_item_layout);
      this.mListMenuPresenter.setCallback(new PanelMenuPresenterCallback());
      paramMenu.addMenuPresenter(this.mListMenuPresenter);
      return;
      label169:
      localTheme.applyStyle(R.style.Theme_AppCompat_CompactMenu, true);
    }
  }
  
  private Menu getMenu()
  {
    if (!this.mMenuCallbackSet)
    {
      this.mDecorToolbar.setMenuCallbacks(new ActionMenuPresenterCallback(), new MenuBuilderCallback());
      this.mMenuCallbackSet = true;
    }
    return this.mDecorToolbar.getMenu();
  }
  
  public void addOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener paramOnMenuVisibilityListener)
  {
    this.mMenuVisibilityListeners.add(paramOnMenuVisibilityListener);
  }
  
  public void addTab(ActionBar.Tab paramTab)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void addTab(ActionBar.Tab paramTab, int paramInt)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void addTab(ActionBar.Tab paramTab, int paramInt, boolean paramBoolean)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void addTab(ActionBar.Tab paramTab, boolean paramBoolean)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public boolean collapseActionView()
  {
    if (this.mDecorToolbar.hasExpandedActionView())
    {
      this.mDecorToolbar.collapseActionView();
      return true;
    }
    return false;
  }
  
  public void dispatchMenuVisibilityChanged(boolean paramBoolean)
  {
    if (paramBoolean == this.mLastMenuVisibility) {
      return;
    }
    this.mLastMenuVisibility = paramBoolean;
    int j = this.mMenuVisibilityListeners.size();
    int i = 0;
    while (i < j)
    {
      ((ActionBar.OnMenuVisibilityListener)this.mMenuVisibilityListeners.get(i)).onMenuVisibilityChanged(paramBoolean);
      i += 1;
    }
  }
  
  public View getCustomView()
  {
    return this.mDecorToolbar.getCustomView();
  }
  
  public int getDisplayOptions()
  {
    return this.mDecorToolbar.getDisplayOptions();
  }
  
  public float getElevation()
  {
    return ViewCompat.getElevation(this.mDecorToolbar.getViewGroup());
  }
  
  public int getHeight()
  {
    return this.mDecorToolbar.getHeight();
  }
  
  View getListMenuView(Menu paramMenu)
  {
    ensureListMenuPresenter(paramMenu);
    if ((paramMenu == null) || (this.mListMenuPresenter == null)) {
      return null;
    }
    if (this.mListMenuPresenter.getAdapter().getCount() > 0) {
      return (View)this.mListMenuPresenter.getMenuView(this.mDecorToolbar.getViewGroup());
    }
    return null;
  }
  
  public int getNavigationItemCount()
  {
    return 0;
  }
  
  public int getNavigationMode()
  {
    return 0;
  }
  
  public int getSelectedNavigationIndex()
  {
    return -1;
  }
  
  public ActionBar.Tab getSelectedTab()
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public CharSequence getSubtitle()
  {
    return this.mDecorToolbar.getSubtitle();
  }
  
  public ActionBar.Tab getTabAt(int paramInt)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public int getTabCount()
  {
    return 0;
  }
  
  public Context getThemedContext()
  {
    return this.mDecorToolbar.getContext();
  }
  
  public CharSequence getTitle()
  {
    return this.mDecorToolbar.getTitle();
  }
  
  public Window.Callback getWrappedWindowCallback()
  {
    return this.mWindowCallback;
  }
  
  public void hide()
  {
    this.mDecorToolbar.setVisibility(8);
  }
  
  public boolean invalidateOptionsMenu()
  {
    this.mDecorToolbar.getViewGroup().removeCallbacks(this.mMenuInvalidator);
    ViewCompat.postOnAnimation(this.mDecorToolbar.getViewGroup(), this.mMenuInvalidator);
    return true;
  }
  
  public boolean isShowing()
  {
    boolean bool = false;
    if (this.mDecorToolbar.getVisibility() == 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isTitleTruncated()
  {
    return super.isTitleTruncated();
  }
  
  public ActionBar.Tab newTab()
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
  }
  
  void onDestroy()
  {
    this.mDecorToolbar.getViewGroup().removeCallbacks(this.mMenuInvalidator);
  }
  
  public boolean onKeyShortcut(int paramInt, KeyEvent paramKeyEvent)
  {
    Menu localMenu = getMenu();
    int i;
    if (localMenu != null)
    {
      if (paramKeyEvent == null) {
        break label56;
      }
      i = paramKeyEvent.getDeviceId();
      if (KeyCharacterMap.load(i).getKeyboardType() == 1) {
        break label61;
      }
    }
    label56:
    label61:
    for (boolean bool = true;; bool = false)
    {
      localMenu.setQwertyMode(bool);
      localMenu.performShortcut(paramInt, paramKeyEvent, 0);
      return true;
      i = -1;
      break;
    }
  }
  
  public boolean onMenuKeyEvent(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getAction() == 1) {
      openOptionsMenu();
    }
    return true;
  }
  
  public boolean openOptionsMenu()
  {
    return this.mDecorToolbar.showOverflowMenu();
  }
  
  /* Error */
  void populateOptionsMenu()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aload_0
    //   3: invokespecial 309	android/support/v7/app/ToolbarActionBar:getMenu	()Landroid/view/Menu;
    //   6: astore_3
    //   7: aload_3
    //   8: instanceof 90
    //   11: ifeq +8 -> 19
    //   14: aload_3
    //   15: checkcast 90	android/support/v7/view/menu/MenuBuilder
    //   18: astore_2
    //   19: aload_2
    //   20: ifnull +7 -> 27
    //   23: aload_2
    //   24: invokevirtual 347	android/support/v7/view/menu/MenuBuilder:stopDispatchingItemsChanged	()V
    //   27: aload_3
    //   28: invokeinterface 350 1 0
    //   33: aload_0
    //   34: getfield 67	android/support/v7/app/ToolbarActionBar:mWindowCallback	Landroid/view/Window$Callback;
    //   37: iconst_0
    //   38: aload_3
    //   39: invokeinterface 356 3 0
    //   44: ifeq +29 -> 73
    //   47: aload_0
    //   48: getfield 67	android/support/v7/app/ToolbarActionBar:mWindowCallback	Landroid/view/Window$Callback;
    //   51: iconst_0
    //   52: aconst_null
    //   53: aload_3
    //   54: invokeinterface 360 4 0
    //   59: istore_1
    //   60: iload_1
    //   61: ifeq +12 -> 73
    //   64: aload_2
    //   65: ifnull +7 -> 72
    //   68: aload_2
    //   69: invokevirtual 363	android/support/v7/view/menu/MenuBuilder:startDispatchingItemsChanged	()V
    //   72: return
    //   73: aload_3
    //   74: invokeinterface 350 1 0
    //   79: goto -15 -> 64
    //   82: astore_3
    //   83: aload_2
    //   84: ifnull +7 -> 91
    //   87: aload_2
    //   88: invokevirtual 363	android/support/v7/view/menu/MenuBuilder:startDispatchingItemsChanged	()V
    //   91: aload_3
    //   92: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	93	0	this	ToolbarActionBar
    //   59	2	1	bool	boolean
    //   1	87	2	localMenuBuilder	MenuBuilder
    //   6	68	3	localMenu	Menu
    //   82	10	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   27	60	82	finally
    //   73	79	82	finally
  }
  
  public void removeAllTabs()
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void removeOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener paramOnMenuVisibilityListener)
  {
    this.mMenuVisibilityListeners.remove(paramOnMenuVisibilityListener);
  }
  
  public void removeTab(ActionBar.Tab paramTab)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void removeTabAt(int paramInt)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public boolean requestFocus()
  {
    ViewGroup localViewGroup = this.mDecorToolbar.getViewGroup();
    if ((localViewGroup == null) || (localViewGroup.hasFocus())) {
      return false;
    }
    localViewGroup.requestFocus();
    return true;
  }
  
  public void selectTab(ActionBar.Tab paramTab)
  {
    throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
  }
  
  public void setBackgroundDrawable(@Nullable Drawable paramDrawable)
  {
    this.mDecorToolbar.setBackgroundDrawable(paramDrawable);
  }
  
  public void setCustomView(int paramInt)
  {
    setCustomView(LayoutInflater.from(this.mDecorToolbar.getContext()).inflate(paramInt, this.mDecorToolbar.getViewGroup(), false));
  }
  
  public void setCustomView(View paramView)
  {
    setCustomView(paramView, new ActionBar.LayoutParams(-2, -2));
  }
  
  public void setCustomView(View paramView, ActionBar.LayoutParams paramLayoutParams)
  {
    if (paramView != null) {
      paramView.setLayoutParams(paramLayoutParams);
    }
    this.mDecorToolbar.setCustomView(paramView);
  }
  
  public void setDefaultDisplayHomeAsUpEnabled(boolean paramBoolean) {}
  
  public void setDisplayHomeAsUpEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 4;; i = 0)
    {
      setDisplayOptions(i, 4);
      return;
    }
  }
  
  public void setDisplayOptions(int paramInt)
  {
    setDisplayOptions(paramInt, -1);
  }
  
  public void setDisplayOptions(int paramInt1, int paramInt2)
  {
    int i = this.mDecorToolbar.getDisplayOptions();
    this.mDecorToolbar.setDisplayOptions(paramInt1 & paramInt2 | paramInt2 & i);
  }
  
  public void setDisplayShowCustomEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 16;; i = 0)
    {
      setDisplayOptions(i, 16);
      return;
    }
  }
  
  public void setDisplayShowHomeEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 2;; i = 0)
    {
      setDisplayOptions(i, 2);
      return;
    }
  }
  
  public void setDisplayShowTitleEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 8;; i = 0)
    {
      setDisplayOptions(i, 8);
      return;
    }
  }
  
  public void setDisplayUseLogoEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      setDisplayOptions(i, 1);
      return;
    }
  }
  
  public void setElevation(float paramFloat)
  {
    ViewCompat.setElevation(this.mDecorToolbar.getViewGroup(), paramFloat);
  }
  
  public void setHomeActionContentDescription(int paramInt)
  {
    this.mDecorToolbar.setNavigationContentDescription(paramInt);
  }
  
  public void setHomeActionContentDescription(CharSequence paramCharSequence)
  {
    this.mDecorToolbar.setNavigationContentDescription(paramCharSequence);
  }
  
  public void setHomeAsUpIndicator(int paramInt)
  {
    this.mDecorToolbar.setNavigationIcon(paramInt);
  }
  
  public void setHomeAsUpIndicator(Drawable paramDrawable)
  {
    this.mDecorToolbar.setNavigationIcon(paramDrawable);
  }
  
  public void setHomeButtonEnabled(boolean paramBoolean) {}
  
  public void setIcon(int paramInt)
  {
    this.mDecorToolbar.setIcon(paramInt);
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    this.mDecorToolbar.setIcon(paramDrawable);
  }
  
  public void setListNavigationCallbacks(SpinnerAdapter paramSpinnerAdapter, ActionBar.OnNavigationListener paramOnNavigationListener)
  {
    this.mDecorToolbar.setDropdownParams(paramSpinnerAdapter, new NavItemSelectedListener(paramOnNavigationListener));
  }
  
  public void setLogo(int paramInt)
  {
    this.mDecorToolbar.setLogo(paramInt);
  }
  
  public void setLogo(Drawable paramDrawable)
  {
    this.mDecorToolbar.setLogo(paramDrawable);
  }
  
  public void setNavigationMode(int paramInt)
  {
    if (paramInt == 2) {
      throw new IllegalArgumentException("Tabs not supported in this configuration");
    }
    this.mDecorToolbar.setNavigationMode(paramInt);
  }
  
  public void setSelectedNavigationItem(int paramInt)
  {
    switch (this.mDecorToolbar.getNavigationMode())
    {
    default: 
      throw new IllegalStateException("setSelectedNavigationIndex not valid for current navigation mode");
    }
    this.mDecorToolbar.setDropdownSelectedPosition(paramInt);
  }
  
  public void setShowHideAnimationEnabled(boolean paramBoolean) {}
  
  public void setSplitBackgroundDrawable(Drawable paramDrawable) {}
  
  public void setStackedBackgroundDrawable(Drawable paramDrawable) {}
  
  public void setSubtitle(int paramInt)
  {
    DecorToolbar localDecorToolbar = this.mDecorToolbar;
    if (paramInt != 0) {}
    for (CharSequence localCharSequence = this.mDecorToolbar.getContext().getText(paramInt);; localCharSequence = null)
    {
      localDecorToolbar.setSubtitle(localCharSequence);
      return;
    }
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    this.mDecorToolbar.setSubtitle(paramCharSequence);
  }
  
  public void setTitle(int paramInt)
  {
    DecorToolbar localDecorToolbar = this.mDecorToolbar;
    if (paramInt != 0) {}
    for (CharSequence localCharSequence = this.mDecorToolbar.getContext().getText(paramInt);; localCharSequence = null)
    {
      localDecorToolbar.setTitle(localCharSequence);
      return;
    }
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mDecorToolbar.setTitle(paramCharSequence);
  }
  
  public void setWindowTitle(CharSequence paramCharSequence)
  {
    this.mDecorToolbar.setWindowTitle(paramCharSequence);
  }
  
  public void show()
  {
    this.mDecorToolbar.setVisibility(0);
  }
  
  private final class ActionMenuPresenterCallback
    implements MenuPresenter.Callback
  {
    private boolean mClosingActionMenu;
    
    ActionMenuPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
    {
      if (this.mClosingActionMenu) {
        return;
      }
      this.mClosingActionMenu = true;
      ToolbarActionBar.this.mDecorToolbar.dismissPopupMenus();
      if (ToolbarActionBar.this.mWindowCallback != null) {
        ToolbarActionBar.this.mWindowCallback.onPanelClosed(108, paramMenuBuilder);
      }
      this.mClosingActionMenu = false;
    }
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      if (ToolbarActionBar.this.mWindowCallback != null)
      {
        ToolbarActionBar.this.mWindowCallback.onMenuOpened(108, paramMenuBuilder);
        return true;
      }
      return false;
    }
  }
  
  private final class MenuBuilderCallback
    implements MenuBuilder.Callback
  {
    MenuBuilderCallback() {}
    
    public boolean onMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
    {
      return false;
    }
    
    public void onMenuModeChange(MenuBuilder paramMenuBuilder)
    {
      if (ToolbarActionBar.this.mWindowCallback != null)
      {
        if (!ToolbarActionBar.this.mDecorToolbar.isOverflowMenuShowing()) {
          break label41;
        }
        ToolbarActionBar.this.mWindowCallback.onPanelClosed(108, paramMenuBuilder);
      }
      label41:
      while (!ToolbarActionBar.this.mWindowCallback.onPreparePanel(0, null, paramMenuBuilder)) {
        return;
      }
      ToolbarActionBar.this.mWindowCallback.onMenuOpened(108, paramMenuBuilder);
    }
  }
  
  private final class PanelMenuPresenterCallback
    implements MenuPresenter.Callback
  {
    PanelMenuPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
    {
      if (ToolbarActionBar.this.mWindowCallback != null) {
        ToolbarActionBar.this.mWindowCallback.onPanelClosed(0, paramMenuBuilder);
      }
    }
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      if ((paramMenuBuilder == null) && (ToolbarActionBar.this.mWindowCallback != null)) {
        ToolbarActionBar.this.mWindowCallback.onMenuOpened(0, paramMenuBuilder);
      }
      return true;
    }
  }
  
  private class ToolbarCallbackWrapper
    extends WindowCallbackWrapper
  {
    public ToolbarCallbackWrapper(Window.Callback paramCallback)
    {
      super();
    }
    
    public View onCreatePanelView(int paramInt)
    {
      switch (paramInt)
      {
      }
      Menu localMenu;
      do
      {
        return super.onCreatePanelView(paramInt);
        localMenu = ToolbarActionBar.this.mDecorToolbar.getMenu();
      } while ((!onPreparePanel(paramInt, null, localMenu)) || (!onMenuOpened(paramInt, localMenu)));
      return ToolbarActionBar.this.getListMenuView(localMenu);
    }
    
    public boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu)
    {
      boolean bool = super.onPreparePanel(paramInt, paramView, paramMenu);
      if ((!bool) || (ToolbarActionBar.this.mToolbarMenuPrepared)) {
        return bool;
      }
      ToolbarActionBar.this.mDecorToolbar.setMenuPrepared();
      ToolbarActionBar.this.mToolbarMenuPrepared = true;
      return bool;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\app\ToolbarActionBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */