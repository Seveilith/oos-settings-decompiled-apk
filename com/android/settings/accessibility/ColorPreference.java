package com.android.settings.accessibility;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ColorPreference
  extends ListDialogPreference
{
  private ColorDrawable mPreviewColor;
  private boolean mPreviewEnabled;
  
  public ColorPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setDialogLayoutResource(2130968715);
    setListItemLayoutResource(2130968641);
  }
  
  protected CharSequence getTitleAt(int paramInt)
  {
    CharSequence localCharSequence = super.getTitleAt(paramInt);
    if (localCharSequence != null) {
      return localCharSequence;
    }
    int j = getValueAt(paramInt);
    paramInt = Color.red(j);
    int i = Color.green(j);
    j = Color.blue(j);
    return getContext().getString(2131692385, new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i), Integer.valueOf(j) });
  }
  
  protected void onBindListItem(View paramView, int paramInt)
  {
    int i = getValueAt(paramInt);
    int j = Color.alpha(i);
    Object localObject = (ImageView)paramView.findViewById(2131362023);
    if (j < 255)
    {
      ((ImageView)localObject).setBackgroundResource(2130838548);
      Drawable localDrawable = ((ImageView)localObject).getDrawable();
      if (!(localDrawable instanceof ColorDrawable)) {
        break label98;
      }
      ((ColorDrawable)localDrawable).setColor(i);
    }
    for (;;)
    {
      localObject = getTitleAt(paramInt);
      if (localObject != null) {
        ((TextView)paramView.findViewById(2131362024)).setText((CharSequence)localObject);
      }
      return;
      ((ImageView)localObject).setBackground(null);
      break;
      label98:
      ((ImageView)localObject).setImageDrawable(new ColorDrawable(i));
    }
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    int i;
    if (this.mPreviewEnabled)
    {
      paramPreferenceViewHolder = (ImageView)paramPreferenceViewHolder.findViewById(2131362414);
      i = getValue();
      if (Color.alpha(i) >= 255) {
        break label105;
      }
      paramPreferenceViewHolder.setBackgroundResource(2130838548);
      if (this.mPreviewColor != null) {
        break label113;
      }
      this.mPreviewColor = new ColorDrawable(i);
      paramPreferenceViewHolder.setImageDrawable(this.mPreviewColor);
      label70:
      CharSequence localCharSequence = getSummary();
      if (TextUtils.isEmpty(localCharSequence)) {
        break label124;
      }
      paramPreferenceViewHolder.setContentDescription(localCharSequence);
      label90:
      if (!isEnabled()) {
        break label132;
      }
    }
    label105:
    label113:
    label124:
    label132:
    for (float f = 1.0F;; f = 0.2F)
    {
      paramPreferenceViewHolder.setAlpha(f);
      return;
      paramPreferenceViewHolder.setBackground(null);
      break;
      this.mPreviewColor.setColor(i);
      break label70;
      paramPreferenceViewHolder.setContentDescription(null);
      break label90;
    }
  }
  
  public void setPreviewEnabled(boolean paramBoolean)
  {
    if (this.mPreviewEnabled != paramBoolean)
    {
      this.mPreviewEnabled = paramBoolean;
      if (paramBoolean) {
        setWidgetLayoutResource(2130968897);
      }
    }
    else
    {
      return;
    }
    setWidgetLayoutResource(0);
  }
  
  public boolean shouldDisableDependents()
  {
    if (Color.alpha(getValue()) != 0) {
      return super.shouldDisableDependents();
    }
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\ColorPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */