package com.android.settings.localepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

class LocaleDragCell
  extends RelativeLayout
{
  private CheckBox mCheckbox;
  private ImageView mDragHandle;
  private TextView mLabel;
  private TextView mLocalized;
  private TextView mMiniLabel;
  
  public LocaleDragCell(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public CheckBox getCheckbox()
  {
    return this.mCheckbox;
  }
  
  public ImageView getDragHandle()
  {
    return this.mDragHandle;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mLabel = ((TextView)findViewById(2131362186));
    this.mLocalized = ((TextView)findViewById(2131362193));
    this.mMiniLabel = ((TextView)findViewById(2131362192));
    this.mCheckbox = ((CheckBox)findViewById(2131361909));
    this.mDragHandle = ((ImageView)findViewById(2131362191));
  }
  
  public void setChecked(boolean paramBoolean)
  {
    this.mCheckbox.setChecked(paramBoolean);
  }
  
  public void setLabelAndDescription(String paramString1, String paramString2)
  {
    this.mLabel.setText(paramString1);
    this.mCheckbox.setText(paramString1);
    this.mLabel.setContentDescription(paramString2);
    this.mCheckbox.setContentDescription(paramString2);
    invalidate();
  }
  
  public void setLocalized(boolean paramBoolean)
  {
    TextView localTextView = this.mLocalized;
    if (paramBoolean) {}
    for (int i = 8;; i = 0)
    {
      localTextView.setVisibility(i);
      invalidate();
      return;
    }
  }
  
  public void setMiniLabel(String paramString)
  {
    this.mMiniLabel.setText(paramString);
    invalidate();
  }
  
  public void setShowCheckbox(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mCheckbox.setVisibility(0);
      this.mLabel.setVisibility(4);
    }
    for (;;)
    {
      invalidate();
      requestLayout();
      return;
      this.mCheckbox.setVisibility(4);
      this.mLabel.setVisibility(0);
    }
  }
  
  public void setShowHandle(boolean paramBoolean)
  {
    ImageView localImageView = this.mDragHandle;
    if (paramBoolean) {}
    for (int i = 0;; i = 4)
    {
      localImageView.setVisibility(i);
      invalidate();
      requestLayout();
      return;
    }
  }
  
  public void setShowMiniLabel(boolean paramBoolean)
  {
    TextView localTextView = this.mMiniLabel;
    if (paramBoolean) {}
    for (int i = 0;; i = 8)
    {
      localTextView.setVisibility(i);
      invalidate();
      requestLayout();
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\localepicker\LocaleDragCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */