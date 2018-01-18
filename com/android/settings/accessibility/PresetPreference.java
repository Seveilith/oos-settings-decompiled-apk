package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import android.widget.TextView;
import com.android.internal.widget.SubtitleView;

public class PresetPreference
  extends ListDialogPreference
{
  private static final float DEFAULT_FONT_SIZE = 32.0F;
  private final CaptioningManager mCaptioningManager;
  
  public PresetPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setDialogLayoutResource(2130968715);
    setListItemLayoutResource(2130968937);
    this.mCaptioningManager = ((CaptioningManager)paramContext.getSystemService("captioning"));
  }
  
  protected void onBindListItem(View paramView, int paramInt)
  {
    Object localObject = paramView.findViewById(2131362006);
    SubtitleView localSubtitleView = (SubtitleView)paramView.findViewById(2131362126);
    int i = getValueAt(paramInt);
    CaptionPropertiesFragment.applyCaptionProperties(this.mCaptioningManager, localSubtitleView, (View)localObject, i);
    localSubtitleView.setTextSize(32.0F * getContext().getResources().getDisplayMetrics().density);
    localObject = getTitleAt(paramInt);
    if (localObject != null) {
      ((TextView)paramView.findViewById(2131362024)).setText((CharSequence)localObject);
    }
  }
  
  public boolean shouldDisableDependents()
  {
    if (getValue() == -1) {
      return super.shouldDisableDependents();
    }
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\PresetPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */