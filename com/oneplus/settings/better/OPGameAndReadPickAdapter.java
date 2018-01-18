package com.oneplus.settings.better;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class OPGameAndReadPickAdapter
  extends BaseAdapter
{
  private List<OPAppModel> mAppList = new ArrayList();
  private Context mContext;
  private LayoutInflater mInflater;
  private List<Boolean> mSelectedList = new ArrayList();
  
  public OPGameAndReadPickAdapter(Context paramContext, List<OPAppModel> paramList)
  {
    this.mAppList = paramList;
    this.mContext = paramContext;
    this.mInflater = LayoutInflater.from(this.mContext);
  }
  
  public int getCount()
  {
    return this.mAppList.size();
  }
  
  public OPAppModel getItem(int paramInt)
  {
    return (OPAppModel)this.mAppList.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public boolean getSelected(int paramInt)
  {
    return ((Boolean)this.mSelectedList.get(paramInt)).booleanValue();
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    OPAppModel localOPAppModel = (OPAppModel)this.mAppList.get(paramInt);
    if (paramView == null)
    {
      paramView = this.mInflater.inflate(2130968814, null);
      paramViewGroup = new ItemViewHolder();
      paramViewGroup.parent = ((RelativeLayout)paramView.findViewById(2131362336));
      paramViewGroup.titleTv = ((TextView)paramView.findViewById(2131362335));
      paramViewGroup.appIconIv = ((ImageView)paramView.findViewById(2131361793));
      paramViewGroup.appNameTv = ((TextView)paramView.findViewById(2131362120));
      paramViewGroup.summaryTv = ((TextView)paramView.findViewById(2131362024));
      paramViewGroup.bottomLine = paramView.findViewById(2131362338);
      paramViewGroup.groupDivider = paramView.findViewById(2131362334);
      paramViewGroup.checkBox = ((CheckBox)paramView.findViewById(2131362337));
      paramView.setTag(paramViewGroup);
      if (paramInt != 0) {
        break label225;
      }
      paramViewGroup.titleTv.setText(2131690453);
      paramViewGroup.titleTv.setVisibility(0);
    }
    for (;;)
    {
      paramViewGroup.appIconIv.setImageDrawable(localOPAppModel.getAppIcon());
      paramViewGroup.appNameTv.setText(localOPAppModel.getLabel());
      paramViewGroup.groupDivider.setVisibility(8);
      if (!getSelected(paramInt)) {
        break label237;
      }
      paramViewGroup.checkBox.setChecked(true);
      return paramView;
      paramViewGroup = (ItemViewHolder)paramView.getTag();
      break;
      label225:
      paramViewGroup.titleTv.setVisibility(8);
    }
    label237:
    paramViewGroup.checkBox.setChecked(false);
    return paramView;
  }
  
  public void setData(List<OPAppModel> paramList)
  {
    this.mAppList = paramList;
    this.mSelectedList.clear();
    int i = 0;
    while (i < this.mAppList.size())
    {
      this.mSelectedList.add(Boolean.valueOf(((OPAppModel)this.mAppList.get(i)).isSelected()));
      i += 1;
    }
    notifyDataSetChanged();
  }
  
  public void setSelected(int paramInt, boolean paramBoolean)
  {
    this.mSelectedList.set(paramInt, Boolean.valueOf(paramBoolean));
    notifyDataSetChanged();
  }
  
  class ItemViewHolder
  {
    ImageView appIconIv;
    TextView appNameTv;
    View bottomLine;
    CheckBox checkBox;
    View groupDivider;
    RelativeLayout parent;
    TextView summaryTv;
    TextView titleTv;
    
    ItemViewHolder() {}
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\better\OPGameAndReadPickAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */