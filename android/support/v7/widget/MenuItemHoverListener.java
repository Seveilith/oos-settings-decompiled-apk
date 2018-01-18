package android.support.v7.widget;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v7.view.menu.MenuBuilder;
import android.view.MenuItem;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public abstract interface MenuItemHoverListener
{
  public abstract void onItemHoverEnter(@NonNull MenuBuilder paramMenuBuilder, @NonNull MenuItem paramMenuItem);
  
  public abstract void onItemHoverExit(@NonNull MenuBuilder paramMenuBuilder, @NonNull MenuItem paramMenuItem);
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\MenuItemHoverListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */