package com.oneplus.lib.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import com.oneplus.commonctrl.R.style;

public class OPBottomSheet
{
  private Dialog mDialog;
  private View mView;
  
  public OPBottomSheet(Context paramContext)
  {
    this.mDialog = new Dialog(paramContext, R.style.Oneplus_bottom_fullscreen);
    this.mDialog.requestWindowFeature(1);
    paramContext = this.mDialog.getWindow();
    paramContext.setGravity(80);
    paramContext.setWindowAnimations(R.style.Oneplus_popup_bottom_animation);
  }
  
  public void dismiss()
  {
    if (isShowing()) {
      this.mDialog.dismiss();
    }
  }
  
  public boolean isShowing()
  {
    if (this.mDialog != null) {
      return this.mDialog.isShowing();
    }
    return false;
  }
  
  public void setView(View paramView)
  {
    this.mView = paramView;
  }
  
  public void show()
  {
    if ((this.mDialog != null) && (this.mView != null))
    {
      this.mDialog.setContentView(this.mView);
      this.mDialog.show();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\OPBottomSheet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */