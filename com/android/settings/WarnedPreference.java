package com.android.settings;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class WarnedPreference
  extends Preference
  implements RadioGroup.OnCheckedChangeListener, View.OnClickListener
{
  private static final String FILE_FONT_CHOOSE = "fontsize_choose";
  private static final String KEY_FONT_CHOOSE_ID = "radioButton_checkedId";
  private static final String KEY_FONT_CHOOSE_PRE_ID = "radioButton_checked_pre_Id";
  private AlertDialog.Builder mBuilder;
  private Context mContext;
  private AlertDialog mDialogFontChoose;
  private View mFontDialogView;
  private OnPreferenceClickListener mOnPreferenceClickListener;
  private OnPreferenceValueChangeListener mPreferenceValueChangeListener;
  private SharedPreferences mSharedPreferenceFontChoose;
  private SharedPreferences.Editor mSpFontEditor;
  
  public WarnedPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public WarnedPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    this.mFontDialogView = LayoutInflater.from(this.mContext).inflate(2130968711, null);
    this.mSharedPreferenceFontChoose = this.mContext.getSharedPreferences("fontsize_choose", 0);
  }
  
  public void click()
  {
    super.onClick();
  }
  
  public AlertDialog getDialog()
  {
    return this.mDialogFontChoose;
  }
  
  public String getWarnedPreferenceSummary()
  {
    RadioButton localRadioButton = (RadioButton)this.mFontDialogView.findViewById(this.mSharedPreferenceFontChoose.getInt("radioButton_checkedId", 0));
    if (localRadioButton != null) {
      return localRadioButton.getText().toString();
    }
    return this.mContext.getResources().getString(2131693814);
  }
  
  public void onCheckedChanged(RadioGroup paramRadioGroup, int paramInt)
  {
    int i = paramRadioGroup.getCheckedRadioButtonId();
    paramRadioGroup = (RadioButton)this.mFontDialogView.findViewById(paramInt);
    this.mSpFontEditor = this.mSharedPreferenceFontChoose.edit();
    boolean bool = this.mContext.getSharedPreferences("font_waring", 0).getBoolean("is_checked", false);
    if ((!paramRadioGroup.getText().equals(this.mContext.getResources().getString(2131693816))) || (bool)) {
      this.mSpFontEditor.putInt("radioButton_checkedId", i);
    }
    for (;;)
    {
      this.mSpFontEditor.commit();
      if (this.mPreferenceValueChangeListener != null) {
        this.mPreferenceValueChangeListener.onPreferenceValueChange(this, paramRadioGroup.getText());
      }
      if (this.mDialogFontChoose != null) {
        this.mDialogFontChoose.dismiss();
      }
      return;
      this.mSpFontEditor.putInt("radioButton_checked_pre_Id", i);
    }
  }
  
  protected void onClick()
  {
    if (this.mOnPreferenceClickListener != null) {
      this.mOnPreferenceClickListener.onPreferenceClick(this);
    }
    showDialog(null);
  }
  
  public void onClick(View paramView)
  {
    if (paramView != null) {
      this.mDialogFontChoose.dismiss();
    }
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable != null) && (paramParcelable.getClass().equals(SavedState.class)))
    {
      paramParcelable = (SavedState)paramParcelable;
      super.onRestoreInstanceState(paramParcelable.getSuperState());
      if (paramParcelable.isDialogShowing) {
        showDialog(paramParcelable.dialogBundle);
      }
      return;
    }
    super.onRestoreInstanceState(paramParcelable);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if ((this.mDialogFontChoose != null) && (this.mDialogFontChoose.isShowing()))
    {
      localObject = new SavedState((Parcelable)localObject);
      ((SavedState)localObject).isDialogShowing = true;
      ((SavedState)localObject).dialogBundle = this.mDialogFontChoose.onSaveInstanceState();
      return (Parcelable)localObject;
    }
    return (Parcelable)localObject;
  }
  
  public void setOnPreferenceClickListener(OnPreferenceClickListener paramOnPreferenceClickListener)
  {
    this.mOnPreferenceClickListener = paramOnPreferenceClickListener;
  }
  
  public void setPreferenceValueChangeListener(OnPreferenceValueChangeListener paramOnPreferenceValueChangeListener)
  {
    this.mPreferenceValueChangeListener = paramOnPreferenceValueChangeListener;
  }
  
  protected void showDialog(Bundle paramBundle)
  {
    ((RadioGroup)this.mFontDialogView.findViewById(2131362155)).setOnCheckedChangeListener(this);
    this.mBuilder = new AlertDialog.Builder(this.mContext);
    int i = this.mSharedPreferenceFontChoose.getInt("radioButton_checkedId", 0);
    RadioButton localRadioButton;
    if (i != 0)
    {
      localRadioButton = (RadioButton)this.mFontDialogView.findViewById(i);
      localRadioButton.setChecked(true);
      localRadioButton.setOnClickListener(this);
    }
    for (;;)
    {
      this.mBuilder.setView(this.mFontDialogView);
      this.mBuilder.setNegativeButton(17039360, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface.dismiss();
        }
      });
      if (this.mDialogFontChoose == null) {
        this.mDialogFontChoose = this.mBuilder.create();
      }
      if (paramBundle != null)
      {
        this.mDialogFontChoose.onRestoreInstanceState(paramBundle);
        this.mDialogFontChoose.show();
      }
      return;
      localRadioButton = (RadioButton)this.mFontDialogView.findViewById(2131362158);
      localRadioButton.setChecked(true);
      localRadioButton.setOnClickListener(this);
    }
  }
  
  public void waringDialogOk()
  {
    if (this.mSpFontEditor == null) {
      this.mSpFontEditor = this.mSharedPreferenceFontChoose.edit();
    }
    this.mSpFontEditor.putInt("radioButton_checkedId", this.mSharedPreferenceFontChoose.getInt("radioButton_checked_pre_Id", 0));
    this.mSpFontEditor.commit();
  }
  
  public static abstract interface OnPreferenceClickListener
  {
    public abstract void onPreferenceClick(Preference paramPreference);
  }
  
  public static abstract interface OnPreferenceValueChangeListener
  {
    public abstract void onPreferenceValueChange(Preference paramPreference, Object paramObject);
  }
  
  private static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public WarnedPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new WarnedPreference.SavedState(paramAnonymousParcel);
      }
      
      public WarnedPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new WarnedPreference.SavedState[paramAnonymousInt];
      }
    };
    Bundle dialogBundle;
    boolean isDialogShowing;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      if (paramParcel.readInt() == 1) {}
      for (;;)
      {
        this.isDialogShowing = bool;
        this.dialogBundle = paramParcel.readBundle();
        return;
        bool = false;
      }
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      if (this.isDialogShowing) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        paramParcel.writeBundle(this.dialogBundle);
        return;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\WarnedPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */