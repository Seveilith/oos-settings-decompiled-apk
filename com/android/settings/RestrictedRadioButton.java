package com.android.settings;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;

public class RestrictedRadioButton
  extends RadioButton
{
  private Context mContext;
  private boolean mDisabledByAdmin;
  private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
  
  public RestrictedRadioButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RestrictedRadioButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842878);
  }
  
  public RestrictedRadioButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public RestrictedRadioButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mContext = paramContext;
  }
  
  public boolean isDisabledByAdmin()
  {
    return this.mDisabledByAdmin;
  }
  
  public boolean performClick()
  {
    if (this.mDisabledByAdmin)
    {
      RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, this.mEnforcedAdmin);
      return true;
    }
    return super.performClick();
  }
  
  public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin paramEnforcedAdmin)
  {
    if (paramEnforcedAdmin != null) {}
    for (boolean bool = true;; bool = false)
    {
      this.mEnforcedAdmin = paramEnforcedAdmin;
      if (this.mDisabledByAdmin != bool)
      {
        this.mDisabledByAdmin = bool;
        RestrictedLockUtils.setTextViewAsDisabledByAdmin(this.mContext, this, this.mDisabledByAdmin);
        if (!this.mDisabledByAdmin) {
          break;
        }
        getButtonDrawable().setColorFilter(this.mContext.getColor(2131493777), PorterDuff.Mode.MULTIPLY);
      }
      return;
    }
    getButtonDrawable().clearColorFilter();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\RestrictedRadioButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */