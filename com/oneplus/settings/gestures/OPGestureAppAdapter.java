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
import com.oneplus.settings.better.OPAppModel;
import java.util.ArrayList;
import java.util.List;

public class OPGestureAppAdapter
  extends BaseAdapter
{
  private static final int VIEW_TYPE_ITEM = 1;
  private static final int VIEW_TYPE_TITLE = 0;
  private Context mContext;
  private List<OPAppModel> mGestureAppList = new ArrayList();
  private String mGesturePackageName;
  private String mGestureSummary;
  private int mGestureUid;
  private boolean mHasShortCut;
  private LayoutInflater mInflater;
  public PackageManager mPackageManager;
  public int mSelectedPosition;
  private String mShortcutName;
  
  public OPGestureAppAdapter(Context paramContext, PackageManager paramPackageManager, String paramString)
  {
    this.mContext = paramContext;
    this.mGestureSummary = paramString;
    this.mInflater = LayoutInflater.from(this.mContext);
    this.mPackageManager = paramPackageManager;
  }
  
  public int getCount()
  {
    return this.mGestureAppList.size();
  }
  
  public OPAppModel getItem(int paramInt)
  {
    return (OPAppModel)this.mGestureAppList.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    OPAppModel localOPAppModel = (OPAppModel)this.mGestureAppList.get(paramInt);
    if (paramView == null)
    {
      paramView = this.mInflater.inflate(2130968815, null);
      paramViewGroup = new ItemViewHolder();
      paramViewGroup.parent = ((RelativeLayout)paramView.findViewById(2131362336));
      paramViewGroup.titleTv = ((TextView)paramView.findViewById(2131362335));
      paramViewGroup.appIconIv = ((ImageView)paramView.findViewById(2131361793));
      paramViewGroup.appNameTv = ((TextView)paramView.findViewById(2131362120));
      paramViewGroup.summaryTv = ((TextView)paramView.findViewById(2131362024));
      paramViewGroup.bottomLine = paramView.findViewById(2131362338);
      paramViewGroup.groupDivider = paramView.findViewById(2131362334);
      paramViewGroup.radioButton = ((RadioButton)paramView.findViewById(2131362339));
      paramView.setTag(paramViewGroup);
      paramViewGroup.titleTv.setVisibility(0);
      if (paramInt != 1) {
        break label306;
      }
      paramViewGroup.titleTv.setText(2131690371);
      label164:
      if (paramInt >= 6) {
        break label336;
      }
      paramViewGroup.appIconIv.setVisibility(8);
      label179:
      paramViewGroup.appNameTv.setText(localOPAppModel.getLabel());
      if ((paramInt != 0) && (paramInt != 5)) {
        break label359;
      }
      paramViewGroup.bottomLine.setVisibility(0);
    }
    for (;;)
    {
      paramViewGroup.groupDivider.setVisibility(8);
      if (((paramInt >= 6) || (!this.mGestureSummary.equals(localOPAppModel.getLabel()))) && ((paramInt < 6) || (!this.mGesturePackageName.equals(localOPAppModel.getPkgName())))) {
        break label391;
      }
      paramViewGroup.radioButton.setChecked(true);
      if (!this.mHasShortCut) {
        break label371;
      }
      paramViewGroup.summaryTv.setText(this.mShortcutName);
      paramViewGroup.summaryTv.setVisibility(0);
      return paramView;
      paramViewGroup = (ItemViewHolder)paramView.getTag();
      break;
      label306:
      if (paramInt == 6)
      {
        paramViewGroup.titleTv.setText(2131690372);
        break label164;
      }
      paramViewGroup.titleTv.setVisibility(8);
      break label164;
      label336:
      paramViewGroup.appIconIv.setVisibility(0);
      paramViewGroup.appIconIv.setImageDrawable(localOPAppModel.getAppIcon());
      break label179;
      label359:
      paramViewGroup.bottomLine.setVisibility(8);
    }
    label371:
    paramViewGroup.summaryTv.setText("");
    paramViewGroup.summaryTv.setVisibility(8);
    return paramView;
    label391:
    paramViewGroup.radioButton.setChecked(false);
    paramViewGroup.summaryTv.setVisibility(8);
    return paramView;
  }
  
  public void setData(List<OPAppModel> paramList)
  {
    this.mGestureAppList = paramList;
    notifyDataSetChanged();
  }
  
  public void setSelectedItem(String paramString1, String paramString2, int paramInt, boolean paramBoolean, String paramString3)
  {
    this.mGestureSummary = paramString1;
    this.mGesturePackageName = paramString2;
    this.mGestureUid = paramInt;
    this.mHasShortCut = paramBoolean;
    this.mShortcutName = paramString3;
    notifyDataSetChanged();
  }
  
  public void setSelectedPosition(int paramInt)
  {
    this.mSelectedPosition = paramInt;
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
    TextView summaryTv;
    TextView titleTv;
    
    ItemViewHolder() {}
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\gestures\OPGestureAppAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */