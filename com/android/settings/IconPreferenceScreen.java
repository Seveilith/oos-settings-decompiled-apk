package com.android.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

public class IconPreferenceScreen
  extends Preference
{
  private boolean mHighlight;
  private Drawable mIcon;
  
  public IconPreferenceScreen(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public IconPreferenceScreen(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setLayoutResource(2130968908);
    this.mIcon = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.IconPreferenceScreen, paramInt, 0).getDrawable(0);
  }
  
  public Drawable getIcon()
  {
    return this.mIcon;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    ImageView localImageView = (ImageView)paramPreferenceViewHolder.findViewById(2131361793);
    if ((localImageView != null) && (this.mIcon != null)) {
      localImageView.setImageDrawable(this.mIcon);
    }
    paramPreferenceViewHolder = (TextView)paramPreferenceViewHolder.findViewById(16908310);
  }
  
  public void setHighlighted(boolean paramBoolean)
  {
    this.mHighlight = paramBoolean;
    notifyChanged();
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    if ((paramDrawable == null) && (this.mIcon != null)) {}
    do
    {
      this.mIcon = paramDrawable;
      notifyChanged();
      do
      {
        return;
      } while (paramDrawable == null);
    } while (!paramDrawable.equals(this.mIcon));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\IconPreferenceScreen.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */