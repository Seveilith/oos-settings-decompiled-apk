package android.support.v7.view.menu;

import android.support.annotation.RestrictTo;
import android.widget.ListView;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public abstract interface ShowableListMenu
{
  public abstract void dismiss();
  
  public abstract ListView getListView();
  
  public abstract boolean isShowing();
  
  public abstract void show();
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\view\menu\ShowableListMenu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */