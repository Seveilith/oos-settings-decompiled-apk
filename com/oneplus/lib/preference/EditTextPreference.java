package com.oneplus.lib.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.id;

public class EditTextPreference
  extends DialogPreference
{
  private EditText mEditText;
  private String mText;
  
  public EditTextPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public EditTextPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.op_editTextPreferenceStyle);
  }
  
  public EditTextPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public EditTextPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mEditText = new EditText(paramContext, paramAttributeSet);
    this.mEditText.setId(16908291);
    this.mEditText.setEnabled(true);
  }
  
  public EditText getEditText()
  {
    return this.mEditText;
  }
  
  public String getText()
  {
    return this.mText;
  }
  
  protected boolean needInputMethod()
  {
    return true;
  }
  
  protected void onAddEditTextToDialogView(View paramView, EditText paramEditText)
  {
    paramView = (ViewGroup)paramView.findViewById(R.id.edittext_container);
    if (paramView != null) {
      paramView.addView(paramEditText, -1, -2);
    }
  }
  
  protected void onBindDialogView(View paramView)
  {
    super.onBindDialogView(paramView);
    EditText localEditText = this.mEditText;
    localEditText.setText(getText());
    ViewParent localViewParent = localEditText.getParent();
    if (localViewParent != paramView)
    {
      if (localViewParent != null) {
        ((ViewGroup)localViewParent).removeView(localEditText);
      }
      onAddEditTextToDialogView(paramView, localEditText);
    }
  }
  
  protected void onDialogClosed(boolean paramBoolean)
  {
    super.onDialogClosed(paramBoolean);
    if (paramBoolean)
    {
      String str = this.mEditText.getText().toString();
      if (callChangeListener(str)) {
        setText(str);
      }
    }
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return paramTypedArray.getString(paramInt);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable != null) && (paramParcelable.getClass().equals(SavedState.class)))
    {
      paramParcelable = (SavedState)paramParcelable;
      super.onRestoreInstanceState(paramParcelable.getSuperState());
      setText(paramParcelable.text);
      return;
    }
    super.onRestoreInstanceState(paramParcelable);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if (isPersistent()) {
      return (Parcelable)localObject;
    }
    localObject = new SavedState((Parcelable)localObject);
    ((SavedState)localObject).text = getText();
    return (Parcelable)localObject;
  }
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
  {
    if (paramBoolean) {}
    for (paramObject = getPersistedString(this.mText);; paramObject = (String)paramObject)
    {
      setText((String)paramObject);
      return;
    }
  }
  
  public void setText(String paramString)
  {
    boolean bool1 = shouldDisableDependents();
    this.mText = paramString;
    persistString(paramString);
    boolean bool2 = shouldDisableDependents();
    if (bool2 != bool1) {
      notifyDependencyChange(bool2);
    }
  }
  
  public boolean shouldDisableDependents()
  {
    if (!TextUtils.isEmpty(this.mText)) {
      return super.shouldDisableDependents();
    }
    return true;
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public EditTextPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new EditTextPreference.SavedState(paramAnonymousParcel);
      }
      
      public EditTextPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new EditTextPreference.SavedState[paramAnonymousInt];
      }
    };
    String text;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.text = paramParcel.readString();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeString(this.text);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\EditTextPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */