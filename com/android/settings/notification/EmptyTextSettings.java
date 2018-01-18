package com.android.settings.notification;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import com.android.settings.SettingsPreferenceFragment;

public abstract class EmptyTextSettings
  extends SettingsPreferenceFragment
{
  private TextView mEmpty;
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    this.mEmpty = new TextView(getContext());
    this.mEmpty.setGravity(17);
    paramBundle = new TypedValue();
    getContext().getTheme().resolveAttribute(16842817, paramBundle, true);
    this.mEmpty.setTextAppearance(paramBundle.resourceId);
    ((ViewGroup)paramView.findViewById(16908351)).addView(this.mEmpty, new ViewGroup.LayoutParams(-1, -1));
    setEmptyView(this.mEmpty);
  }
  
  protected void setEmptyText(int paramInt)
  {
    this.mEmpty.setText(paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\EmptyTextSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */