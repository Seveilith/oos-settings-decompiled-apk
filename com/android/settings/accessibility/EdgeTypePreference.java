package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import com.android.internal.widget.SubtitleView;

public class EdgeTypePreference
  extends ListDialogPreference
{
  private static final int DEFAULT_BACKGROUND_COLOR = 0;
  private static final int DEFAULT_EDGE_COLOR = -16777216;
  private static final float DEFAULT_FONT_SIZE = 32.0F;
  private static final int DEFAULT_FOREGROUND_COLOR = -1;
  
  public EdgeTypePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.getResources();
    setValues(paramContext.getIntArray(2131427425));
    setTitles(paramContext.getStringArray(2131427424));
    setDialogLayoutResource(2130968715);
    setListItemLayoutResource(2130968937);
  }
  
  protected void onBindListItem(View paramView, int paramInt)
  {
    Object localObject = (SubtitleView)paramView.findViewById(2131362126);
    ((SubtitleView)localObject).setForegroundColor(-1);
    ((SubtitleView)localObject).setBackgroundColor(0);
    ((SubtitleView)localObject).setTextSize(32.0F * getContext().getResources().getDisplayMetrics().density);
    ((SubtitleView)localObject).setEdgeType(getValueAt(paramInt));
    ((SubtitleView)localObject).setEdgeColor(-16777216);
    localObject = getTitleAt(paramInt);
    if (localObject != null) {
      ((TextView)paramView.findViewById(2131362024)).setText((CharSequence)localObject);
    }
  }
  
  public boolean shouldDisableDependents()
  {
    if (getValue() != 0) {
      return super.shouldDisableDependents();
    }
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\EdgeTypePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */