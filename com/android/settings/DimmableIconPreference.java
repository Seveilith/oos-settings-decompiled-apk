package com.android.settings;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.settingslib.RestrictedPreference;

public class DimmableIconPreference
  extends RestrictedPreference
{
  private static final int ICON_ALPHA_DISABLED = 102;
  private static final int ICON_ALPHA_ENABLED = 255;
  private final CharSequence mContentDescription;
  
  public DimmableIconPreference(Context paramContext)
  {
    this(paramContext, (AttributeSet)null);
  }
  
  public DimmableIconPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContentDescription = null;
    useAdminDisabledSummary(true);
    setLayoutResource(2130968837);
  }
  
  public DimmableIconPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt, CharSequence paramCharSequence)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mContentDescription = paramCharSequence;
    setLayoutResource(2130968837);
  }
  
  public DimmableIconPreference(Context paramContext, CharSequence paramCharSequence)
  {
    super(paramContext);
    this.mContentDescription = paramCharSequence;
    useAdminDisabledSummary(true);
    setLayoutResource(2130968837);
  }
  
  protected void dimIcon(boolean paramBoolean)
  {
    Drawable localDrawable1 = getIcon();
    Drawable localDrawable2;
    if (localDrawable1 != null)
    {
      localDrawable2 = localDrawable1.mutate();
      if (!paramBoolean) {
        break label34;
      }
    }
    label34:
    for (int i = 102;; i = 255)
    {
      localDrawable2.setAlpha(i);
      setIcon(localDrawable1);
      return;
    }
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    if (!TextUtils.isEmpty(this.mContentDescription)) {
      ((TextView)paramPreferenceViewHolder.findViewById(16908310)).setContentDescription(this.mContentDescription);
    }
    if (isEnabled()) {}
    for (boolean bool = false;; bool = true)
    {
      dimIcon(bool);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\DimmableIconPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */