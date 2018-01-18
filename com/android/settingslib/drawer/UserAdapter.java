package com.android.settingslib.drawer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.database.DataSetObserver;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.android.settingslib.R.layout;
import com.android.settingslib.R.string;
import com.android.settingslib.drawable.UserIconDrawable;
import com.android.settingslib.drawable.UserIcons;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserAdapter
  implements SpinnerAdapter, ListAdapter
{
  private ArrayList<UserDetails> data;
  private final LayoutInflater mInflater;
  
  public UserAdapter(Context paramContext, ArrayList<UserDetails> paramArrayList)
  {
    if (paramArrayList == null) {
      throw new IllegalArgumentException("A list of user details must be provided");
    }
    this.data = paramArrayList;
    this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
  }
  
  private View createUser(ViewGroup paramViewGroup)
  {
    return this.mInflater.inflate(R.layout.user_preference, paramViewGroup, false);
  }
  
  public static UserAdapter createUserAdapter(UserManager paramUserManager, Context paramContext, List<UserHandle> paramList)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      localArrayList.add(new UserDetails((UserHandle)paramList.get(i), paramUserManager, paramContext));
      i += 1;
    }
    return new UserAdapter(paramContext, localArrayList);
  }
  
  public static UserAdapter createUserSpinnerAdapter(UserManager paramUserManager, Context paramContext)
  {
    List localList = paramUserManager.getUserProfiles();
    int j = 0;
    Object localObject = localList.iterator();
    do
    {
      i = j;
      if (!((Iterator)localObject).hasNext()) {
        break;
      }
    } while (((UserHandle)((Iterator)localObject).next()).getIdentifier() != 999);
    int i = 1;
    if ((i != 0) && (localList.size() < 3)) {}
    while ((i == 0) && (localList.size() < 2)) {
      return null;
    }
    localObject = new UserHandle(UserHandle.myUserId());
    localList.remove(localObject);
    localList.add(0, localObject);
    return createUserAdapter(paramUserManager, paramContext, localList);
  }
  
  private int getTitle(UserDetails paramUserDetails)
  {
    int i = UserDetails.-get1(paramUserDetails).getIdentifier();
    if ((i == -2) || (i == ActivityManager.getCurrentUser())) {
      return R.string.category_personal;
    }
    return R.string.category_work;
  }
  
  public boolean areAllItemsEnabled()
  {
    return true;
  }
  
  public int getCount()
  {
    return this.data.size();
  }
  
  public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (paramView != null) {}
    for (;;)
    {
      paramViewGroup = (UserDetails)this.data.get(paramInt);
      ((ImageView)paramView.findViewById(16908294)).setImageDrawable(UserDetails.-get0(paramViewGroup));
      ((TextView)paramView.findViewById(16908310)).setText(getTitle(paramViewGroup));
      return paramView;
      paramView = createUser(paramViewGroup);
    }
  }
  
  public UserDetails getItem(int paramInt)
  {
    return (UserDetails)this.data.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return UserDetails.-get1((UserDetails)this.data.get(paramInt)).getIdentifier();
  }
  
  public int getItemViewType(int paramInt)
  {
    return 0;
  }
  
  public UserHandle getUserHandle(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.data.size())) {
      return null;
    }
    return UserDetails.-get1((UserDetails)this.data.get(paramInt));
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    return getDropDownView(paramInt, paramView, paramViewGroup);
  }
  
  public int getViewTypeCount()
  {
    return 1;
  }
  
  public boolean hasStableIds()
  {
    return false;
  }
  
  public boolean isEmpty()
  {
    return this.data.isEmpty();
  }
  
  public boolean isEnabled(int paramInt)
  {
    return true;
  }
  
  public void registerDataSetObserver(DataSetObserver paramDataSetObserver) {}
  
  public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver) {}
  
  public static class UserDetails
  {
    private final Drawable mIcon;
    private final String mName;
    private final UserHandle mUserHandle;
    
    public UserDetails(UserHandle paramUserHandle, UserManager paramUserManager, Context paramContext)
    {
      this.mUserHandle = paramUserHandle;
      paramUserHandle = paramUserManager.getUserInfo(this.mUserHandle.getIdentifier());
      if (paramUserHandle.isManagedProfile())
      {
        this.mName = paramContext.getString(R.string.managed_user_title);
        paramUserHandle = paramContext.getDrawable(17302314);
      }
      for (;;)
      {
        this.mIcon = encircle(paramContext, paramUserHandle);
        return;
        this.mName = paramUserHandle.name;
        int i = paramUserHandle.id;
        if (paramUserManager.getUserIcon(i) != null) {
          paramUserHandle = new BitmapDrawable(paramContext.getResources(), paramUserManager.getUserIcon(i));
        } else {
          paramUserHandle = UserIcons.getDefaultUserIcon(i, false);
        }
      }
    }
    
    private static Drawable encircle(Context paramContext, Drawable paramDrawable)
    {
      return new UserIconDrawable(UserIconDrawable.getSizeForList(paramContext)).setIconDrawable(paramDrawable).bake();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\drawer\UserAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */