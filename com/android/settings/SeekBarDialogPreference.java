package com.android.settings;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

public class SeekBarDialogPreference
  extends CustomDialogPreference
{
  private final Drawable mMyIcon;
  
  public SeekBarDialogPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SeekBarDialogPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setDialogLayoutResource(2130968901);
    createActionButtons();
    this.mMyIcon = getDialogIcon();
    setDialogIcon(null);
  }
  
  protected static SeekBar getSeekBar(View paramView)
  {
    return (SeekBar)paramView.findViewById(2131362364);
  }
  
  public void createActionButtons()
  {
    setPositiveButtonText(17039370);
    setNegativeButtonText(17039360);
  }
  
  protected void onBindDialogView(View paramView)
  {
    super.onBindDialogView(paramView);
    paramView = (ImageView)paramView.findViewById(16908294);
    if (this.mMyIcon != null)
    {
      paramView.setImageDrawable(this.mMyIcon);
      return;
    }
    paramView.setVisibility(8);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SeekBarDialogPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */