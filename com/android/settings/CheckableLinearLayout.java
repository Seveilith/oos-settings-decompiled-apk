package com.android.settings;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout
  extends LinearLayout
  implements Checkable
{
  private boolean mChecked;
  private float mDisabledAlpha;
  
  public CheckableLinearLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramAttributeSet = new TypedValue();
    paramContext.getTheme().resolveAttribute(16842803, paramAttributeSet, true);
    this.mDisabledAlpha = paramAttributeSet.getFloat();
  }
  
  private void updateChecked()
  {
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = getChildAt(i);
      if ((localView instanceof Checkable)) {
        ((Checkable)localView).setChecked(this.mChecked);
      }
      i += 1;
    }
  }
  
  public boolean isChecked()
  {
    return this.mChecked;
  }
  
  public void setChecked(boolean paramBoolean)
  {
    this.mChecked = paramBoolean;
    updateChecked();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    int j = getChildCount();
    int i = 0;
    if (i < j)
    {
      View localView = getChildAt(i);
      if (paramBoolean) {}
      for (float f = 1.0F;; f = this.mDisabledAlpha)
      {
        localView.setAlpha(f);
        i += 1;
        break;
      }
    }
  }
  
  public void toggle()
  {
    if (this.mChecked) {}
    for (boolean bool = false;; bool = true)
    {
      setChecked(bool);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\CheckableLinearLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */