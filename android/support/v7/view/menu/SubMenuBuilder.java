package android.support.v7.view.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class SubMenuBuilder
  extends MenuBuilder
  implements SubMenu
{
  private MenuItemImpl mItem;
  private MenuBuilder mParentMenu;
  
  public SubMenuBuilder(Context paramContext, MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl)
  {
    super(paramContext);
    this.mParentMenu = paramMenuBuilder;
    this.mItem = paramMenuItemImpl;
  }
  
  public boolean collapseItemActionView(MenuItemImpl paramMenuItemImpl)
  {
    return this.mParentMenu.collapseItemActionView(paramMenuItemImpl);
  }
  
  boolean dispatchMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
  {
    if (!super.dispatchMenuItemSelected(paramMenuBuilder, paramMenuItem)) {
      return this.mParentMenu.dispatchMenuItemSelected(paramMenuBuilder, paramMenuItem);
    }
    return true;
  }
  
  public boolean expandItemActionView(MenuItemImpl paramMenuItemImpl)
  {
    return this.mParentMenu.expandItemActionView(paramMenuItemImpl);
  }
  
  public String getActionViewStatesKey()
  {
    int i = 0;
    if (this.mItem != null) {
      i = this.mItem.getItemId();
    }
    if (i == 0) {
      return null;
    }
    return super.getActionViewStatesKey() + ":" + i;
  }
  
  public MenuItem getItem()
  {
    return this.mItem;
  }
  
  public Menu getParentMenu()
  {
    return this.mParentMenu;
  }
  
  public MenuBuilder getRootMenu()
  {
    return this.mParentMenu.getRootMenu();
  }
  
  public boolean isQwertyMode()
  {
    return this.mParentMenu.isQwertyMode();
  }
  
  public boolean isShortcutsVisible()
  {
    return this.mParentMenu.isShortcutsVisible();
  }
  
  public void setCallback(MenuBuilder.Callback paramCallback)
  {
    this.mParentMenu.setCallback(paramCallback);
  }
  
  public SubMenu setHeaderIcon(int paramInt)
  {
    return (SubMenu)super.setHeaderIconInt(paramInt);
  }
  
  public SubMenu setHeaderIcon(Drawable paramDrawable)
  {
    return (SubMenu)super.setHeaderIconInt(paramDrawable);
  }
  
  public SubMenu setHeaderTitle(int paramInt)
  {
    return (SubMenu)super.setHeaderTitleInt(paramInt);
  }
  
  public SubMenu setHeaderTitle(CharSequence paramCharSequence)
  {
    return (SubMenu)super.setHeaderTitleInt(paramCharSequence);
  }
  
  public SubMenu setHeaderView(View paramView)
  {
    return (SubMenu)super.setHeaderViewInt(paramView);
  }
  
  public SubMenu setIcon(int paramInt)
  {
    this.mItem.setIcon(paramInt);
    return this;
  }
  
  public SubMenu setIcon(Drawable paramDrawable)
  {
    this.mItem.setIcon(paramDrawable);
    return this;
  }
  
  public void setQwertyMode(boolean paramBoolean)
  {
    this.mParentMenu.setQwertyMode(paramBoolean);
  }
  
  public void setShortcutsVisible(boolean paramBoolean)
  {
    this.mParentMenu.setShortcutsVisible(paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\view\menu\SubMenuBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */