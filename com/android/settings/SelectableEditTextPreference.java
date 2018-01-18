package com.android.settings;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class SelectableEditTextPreference
  extends CustomEditTextPreference
{
  public static final int SELECTION_CURSOR_END = 0;
  public static final int SELECTION_CURSOR_START = 1;
  public static final int SELECTION_SELECT_ALL = 2;
  private int mSelectionMode;
  
  public SelectableEditTextPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected void onBindDialogView(View paramView)
  {
    super.onBindDialogView(paramView);
    paramView = getEditText();
    if (paramView.getText() != null) {}
    for (int i = paramView.getText().length();; i = 0)
    {
      if (!TextUtils.isEmpty(paramView.getText())) {}
      switch (this.mSelectionMode)
      {
      default: 
        return;
      }
    }
    paramView.setSelection(i);
    return;
    paramView.setSelection(0);
    return;
    paramView.setSelection(0, i);
  }
  
  public void setInitialSelectionMode(int paramInt)
  {
    this.mSelectionMode = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SelectableEditTextPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */