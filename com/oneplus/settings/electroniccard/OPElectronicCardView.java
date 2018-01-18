package com.oneplus.settings.electroniccard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OPElectronicCardView
  extends RelativeLayout
{
  TextView deviceImeiTv;
  TextView deviceModelTv;
  TextView warrantyExpriedDateTv;
  
  public OPElectronicCardView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public OPElectronicCardView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  public OPElectronicCardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init();
  }
  
  public OPElectronicCardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    init();
  }
  
  private void init()
  {
    View localView = LayoutInflater.from(getContext()).inflate(2130968800, this, true);
    this.deviceModelTv = ((TextView)localView.findViewById(2131362305));
    this.deviceImeiTv = ((TextView)localView.findViewById(2131362307));
    this.warrantyExpriedDateTv = ((TextView)localView.findViewById(2131362309));
  }
  
  public TextView getDeviceImeiTv()
  {
    return this.deviceImeiTv;
  }
  
  public TextView getDeviceModelTv()
  {
    return this.deviceModelTv;
  }
  
  public TextView getWarrantyExpriedDateTv()
  {
    return this.warrantyExpriedDateTv;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\electroniccard\OPElectronicCardView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */