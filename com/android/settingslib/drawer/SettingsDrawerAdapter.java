package com.android.settingslib.drawer;

import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settingslib.R.drawable;
import com.android.settingslib.R.id;
import com.android.settingslib.R.layout;
import com.android.settingslib.R.string;
import java.util.ArrayList;
import java.util.List;

public class SettingsDrawerAdapter
  extends BaseAdapter
{
  private final SettingsDrawerActivity mActivity;
  private final ArrayList<Item> mItems = new ArrayList();
  
  public SettingsDrawerAdapter(SettingsDrawerActivity paramSettingsDrawerActivity)
  {
    this.mActivity = paramSettingsDrawerActivity;
  }
  
  public int getCount()
  {
    return this.mItems.size();
  }
  
  public Object getItem(int paramInt)
  {
    return this.mItems.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public int getItemViewType(int paramInt)
  {
    Item localItem = (Item)this.mItems.get(paramInt);
    if (localItem == null) {
      return 0;
    }
    if (localItem.icon != null) {
      return 1;
    }
    return 2;
  }
  
  public Tile getTile(int paramInt)
  {
    Tile localTile = null;
    if (this.mItems.get(paramInt) != null) {
      localTile = ((Item)this.mItems.get(paramInt)).tile;
    }
    return localTile;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    Item localItem = (Item)this.mItems.get(paramInt);
    if (localItem == null) {
      return LayoutInflater.from(this.mActivity).inflate(R.layout.drawer_spacer, paramViewGroup, false);
    }
    Object localObject = paramView;
    if (paramView != null)
    {
      localObject = paramView;
      if (paramView.getId() == R.id.spacer) {
        localObject = null;
      }
    }
    int i;
    if (localItem.icon != null)
    {
      paramInt = 1;
      if (localObject != null) {
        break label192;
      }
      paramView = new ViewHolder(null);
      localObject = LayoutInflater.from(this.mActivity);
      if (paramInt == 0) {
        break label184;
      }
      i = R.layout.drawer_item;
      label99:
      localObject = ((LayoutInflater)localObject).inflate(i, paramViewGroup, false);
      if (paramInt != 0) {
        paramView.icon = ((ImageView)((View)localObject).findViewById(16908294));
      }
      paramView.title = ((TextView)((View)localObject).findViewById(16908310));
      ((View)localObject).setTag(paramView);
    }
    for (;;)
    {
      if (paramInt != 0) {
        paramView.icon.setImageIcon(localItem.icon);
      }
      paramView.title.setText(localItem.label);
      return (View)localObject;
      paramInt = 0;
      break;
      label184:
      i = R.layout.drawer_category;
      break label99;
      label192:
      paramView = (ViewHolder)((View)localObject).getTag();
    }
  }
  
  public int getViewTypeCount()
  {
    return 3;
  }
  
  public boolean isEnabled(int paramInt)
  {
    return (this.mItems.get(paramInt) != null) && (((Item)this.mItems.get(paramInt)).icon != null);
  }
  
  void updateCategories()
  {
    List localList = this.mActivity.getDashboardCategories();
    this.mItems.clear();
    this.mItems.add(null);
    Object localObject = new Item(null);
    ((Item)localObject).label = this.mActivity.getString(R.string.home);
    ((Item)localObject).icon = Icon.createWithResource(this.mActivity, R.drawable.home);
    this.mItems.add(localObject);
    int i = 0;
    while (i < localList.size())
    {
      Item localItem = new Item(null);
      localItem.icon = null;
      localObject = (DashboardCategory)localList.get(i);
      localItem.label = ((DashboardCategory)localObject).title;
      this.mItems.add(localItem);
      int j = 0;
      while (j < ((DashboardCategory)localObject).tiles.size())
      {
        localItem = new Item(null);
        Tile localTile = (Tile)((DashboardCategory)localObject).tiles.get(j);
        localItem.label = localTile.title;
        localItem.icon = localTile.icon;
        localItem.tile = localTile;
        this.mItems.add(localItem);
        j += 1;
      }
      i += 1;
    }
    notifyDataSetChanged();
  }
  
  private static class Item
  {
    public Icon icon;
    public CharSequence label;
    public Tile tile;
  }
  
  private static class ViewHolder
  {
    ImageView icon;
    TextView title;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\drawer\SettingsDrawerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */