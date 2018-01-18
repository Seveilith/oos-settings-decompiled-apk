package com.oneplus.settings.ringtone;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.oneplus.lib.widget.button.OPRadioButton;
import java.util.ArrayList;
import java.util.List;

public class OPLocalRingtoneAdapter
  extends BaseAdapter
{
  private Context mContext;
  private List mData = new ArrayList();
  
  public OPLocalRingtoneAdapter(Context paramContext, List paramList)
  {
    this.mContext = paramContext;
    this.mData = paramList;
  }
  
  public int getCount()
  {
    return this.mData.size();
  }
  
  public Object getItem(int paramInt)
  {
    return this.mData.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (paramView == null)
    {
      paramViewGroup = new HoldView();
      paramView = LayoutInflater.from(this.mContext).inflate(2130968825, null);
      paramViewGroup.mTitle = ((TextView)paramView.findViewById(16908310));
      paramViewGroup.button = ((OPRadioButton)paramView.findViewById(2131362361));
      paramView.setTag(paramViewGroup);
    }
    for (;;)
    {
      if (this.mData != null)
      {
        RingtoneData localRingtoneData = (RingtoneData)this.mData.get(paramInt);
        if (localRingtoneData != null)
        {
          paramViewGroup.mTitle.setText(localRingtoneData.title);
          paramViewGroup.button.setChecked(localRingtoneData.isCheck);
        }
      }
      return paramView;
      paramViewGroup = (HoldView)paramView.getTag();
    }
  }
  
  static class HoldView
  {
    OPRadioButton button;
    TextView mTitle;
  }
  
  public static class RingtoneData
  {
    public String filepath;
    public boolean isCheck;
    public Uri mUri;
    public String mimetype;
    public String title;
    
    public RingtoneData(Uri paramUri, String paramString, boolean paramBoolean)
    {
      this.mUri = paramUri;
      this.title = paramString;
      this.isCheck = paramBoolean;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ringtone\OPLocalRingtoneAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */