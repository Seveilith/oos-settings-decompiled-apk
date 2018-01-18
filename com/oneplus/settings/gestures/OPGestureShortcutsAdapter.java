package com.oneplus.settings.gestures;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class OPGestureShortcutsAdapter
  extends BaseAdapter
{
  private static final int VIEW_TYPE_ITEM = 1;
  private static final int VIEW_TYPE_TITLE = 0;
  private Context mContext;
  private List<OPGestureAppModel> mGestureAppList = new ArrayList();
  private String mGestureSummary;
  private LayoutInflater mInflater;
  public PackageManager mPackageManager;
  public int mSelectedPosition;
  
  public OPGestureShortcutsAdapter(Context paramContext, List<OPGestureAppModel> paramList, String paramString)
  {
    this.mContext = paramContext;
    this.mInflater = LayoutInflater.from(this.mContext);
    this.mGestureAppList = paramList;
    this.mGestureSummary = paramString;
  }
  
  public int getCount()
  {
    return this.mGestureAppList.size();
  }
  
  public OPGestureAppModel getItem(int paramInt)
  {
    return (OPGestureAppModel)this.mGestureAppList.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    OPGestureAppModel localOPGestureAppModel = (OPGestureAppModel)this.mGestureAppList.get(paramInt);
    if (paramView == null)
    {
      paramView = this.mInflater.inflate(2130968815, null);
      paramViewGroup = new ItemViewHolder();
      paramViewGroup.parent = ((RelativeLayout)paramView.findViewById(2131362336));
      paramViewGroup.titleTv = ((TextView)paramView.findViewById(2131362335));
      paramViewGroup.appIconIv = ((ImageView)paramView.findViewById(2131361793));
      paramViewGroup.appNameTv = ((TextView)paramView.findViewById(2131362120));
      paramViewGroup.bottomLine = paramView.findViewById(2131362338);
      paramViewGroup.groupDivider = paramView.findViewById(2131362334);
      paramViewGroup.radioButton = ((RadioButton)paramView.findViewById(2131362339));
      paramView.setTag(paramViewGroup);
      paramViewGroup.titleTv.setVisibility(0);
      if (paramInt != 1) {
        break label240;
      }
      paramViewGroup.titleTv.setText(2131690371);
      label151:
      paramViewGroup.appIconIv.setVisibility(0);
      paramViewGroup.appIconIv.setImageDrawable(localOPGestureAppModel.getAppIcon());
      paramViewGroup.appNameTv.setText(localOPGestureAppModel.getTitle());
      if (paramInt != 0) {
        break label252;
      }
      paramViewGroup.bottomLine.setVisibility(0);
    }
    for (;;)
    {
      paramViewGroup.groupDivider.setVisibility(8);
      if (!this.mGestureSummary.equals(localOPGestureAppModel.getTitle())) {
        break label264;
      }
      paramViewGroup.radioButton.setChecked(true);
      return paramView;
      paramViewGroup = (ItemViewHolder)paramView.getTag();
      break;
      label240:
      paramViewGroup.titleTv.setVisibility(8);
      break label151;
      label252:
      paramViewGroup.bottomLine.setVisibility(8);
    }
    label264:
    paramViewGroup.radioButton.setChecked(false);
    return paramView;
  }
  
  public void setData(List<OPGestureAppModel> paramList)
  {
    this.mGestureAppList = paramList;
    notifyDataSetChanged();
  }
  
  public void setSelectedPosition(int paramInt)
  {
    this.mSelectedPosition = paramInt;
    notifyDataSetChanged();
  }
  
  public void setSelectedSummary(String paramString)
  {
    this.mGestureSummary = paramString;
    notifyDataSetChanged();
  }
  
  class ItemViewHolder
  {
    ImageView appIconIv;
    TextView appNameTv;
    View bottomLine;
    View groupDivider;
    RelativeLayout parent;
    RadioButton radioButton;
    TextView titleTv;
    
    ItemViewHolder() {}
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\gestures\OPGestureShortcutsAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */