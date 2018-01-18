package com.oneplus.settings.laboratory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class OPLabPluginListAdapter
  extends BaseAdapter
{
  private Context mContext;
  private LayoutInflater mInflate;
  private List<OPLabPluginModel> mPluginData;
  
  public OPLabPluginListAdapter(Context paramContext, List<OPLabPluginModel> paramList)
  {
    this.mPluginData = paramList;
    this.mContext = paramContext;
    this.mInflate = LayoutInflater.from(paramContext);
  }
  
  public int getCount()
  {
    return this.mPluginData.size();
  }
  
  public OPLabPluginModel getItem(int paramInt)
  {
    return (OPLabPluginModel)this.mPluginData.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (paramView == null)
    {
      paramViewGroup = new ViewHolder(null);
      paramView = this.mInflate.inflate(2130968819, null);
      paramViewGroup.featureTitle = ((TextView)paramView.findViewById(2131362352));
      paramView.setTag(paramViewGroup);
    }
    for (;;)
    {
      paramViewGroup.featureTitle.setText(((OPLabPluginModel)this.mPluginData.get(paramInt)).getFeatureTitle());
      return paramView;
      paramViewGroup = (ViewHolder)paramView.getTag();
    }
  }
  
  public void setData(List<OPLabPluginModel> paramList)
  {
    this.mPluginData = paramList;
    notifyDataSetChanged();
  }
  
  private class ViewHolder
  {
    TextView featureTitle;
    
    private ViewHolder() {}
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\laboratory\OPLabPluginListAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */