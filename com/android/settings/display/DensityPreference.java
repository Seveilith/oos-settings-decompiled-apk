package com.android.settings.display;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.View;
import android.widget.EditText;
import com.android.settings.CustomEditTextPreference;
import com.android.settingslib.display.DisplayDensityUtils;

public class DensityPreference
  extends CustomEditTextPreference
{
  private static final String TAG = "DensityPreference";
  
  public DensityPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private int getCurrentSwDp()
  {
    DisplayMetrics localDisplayMetrics = getContext().getResources().getDisplayMetrics();
    float f = localDisplayMetrics.density;
    return (int)(Math.min(localDisplayMetrics.widthPixels, localDisplayMetrics.heightPixels) / f);
  }
  
  public void onAttached()
  {
    super.onAttached();
    setSummary(getContext().getString(2131693710, new Object[] { Integer.valueOf(getCurrentSwDp()) }));
  }
  
  protected void onBindDialogView(View paramView)
  {
    super.onBindDialogView(paramView);
    paramView = (EditText)paramView.findViewById(16908291);
    if (paramView != null)
    {
      paramView.setInputType(2);
      paramView.setText(getCurrentSwDp() + "");
    }
  }
  
  protected void onDialogClosed(boolean paramBoolean)
  {
    if (paramBoolean) {}
    try
    {
      DisplayMetrics localDisplayMetrics = getContext().getResources().getDisplayMetrics();
      int i = Math.max(Integer.parseInt(getText()), 320);
      DisplayDensityUtils.setForcedDisplayDensity(0, Math.max(Math.min(localDisplayMetrics.widthPixels, localDisplayMetrics.heightPixels) * 160 / i, 120));
      return;
    }
    catch (Exception localException)
    {
      Slog.e("DensityPreference", "Couldn't save density", localException);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\display\DensityPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */