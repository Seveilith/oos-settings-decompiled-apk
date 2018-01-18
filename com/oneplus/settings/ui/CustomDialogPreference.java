package com.oneplus.settings.ui;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.BaseSavedState;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.android.internal.R.styleable;

public abstract class CustomDialogPreference
  extends Preference
  implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener
{
  private AlertDialog.Builder mBuilder;
  private Dialog mDialog;
  private int mDialogLayoutResId;
  private CharSequence mNegativeButtonText;
  private CharSequence mNeutralButtonText;
  private CharSequence mPositiveButtonText;
  private int mWhichButtonClicked;
  
  public CustomDialogPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public CustomDialogPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842897);
  }
  
  public CustomDialogPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public CustomDialogPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.DialogPreference, paramInt1, paramInt2);
    this.mPositiveButtonText = paramContext.getString(3);
    this.mNegativeButtonText = paramContext.getString(4);
    this.mDialogLayoutResId = paramContext.getResourceId(5, this.mDialogLayoutResId);
    paramContext.recycle();
  }
  
  public Dialog getDialog()
  {
    return this.mDialog;
  }
  
  public int getDialogLayoutResource()
  {
    return this.mDialogLayoutResId;
  }
  
  public CharSequence getNegativeButtonText()
  {
    return this.mNegativeButtonText;
  }
  
  public CharSequence getNeutralButtonText()
  {
    return this.mNeutralButtonText;
  }
  
  public CharSequence getPositiveButtonText()
  {
    return this.mPositiveButtonText;
  }
  
  public void onActivityDestroy()
  {
    if ((this.mDialog != null) && (this.mDialog.isShowing()))
    {
      this.mDialog.dismiss();
      return;
    }
  }
  
  protected void onBindDialogView(View paramView) {}
  
  protected void onClick()
  {
    if ((this.mDialog != null) && (this.mDialog.isShowing())) {
      return;
    }
    showDialog(null);
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    this.mWhichButtonClicked = paramInt;
  }
  
  protected View onCreateDialogView()
  {
    if (this.mDialogLayoutResId == 0) {
      return null;
    }
    return LayoutInflater.from(this.mBuilder.getContext()).inflate(this.mDialogLayoutResId, null);
  }
  
  protected void onDialogClosed(int paramInt) {}
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    this.mDialog = null;
    onDialogClosed(this.mWhichButtonClicked);
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder) {}
  
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
    if ((this.mDialog != null) && (this.mDialog.isShowing()))
    {
      localObject = new SavedState((Parcelable)localObject);
      ((SavedState)localObject).isDialogShowing = true;
      ((SavedState)localObject).dialogBundle = this.mDialog.onSaveInstanceState();
      return (Parcelable)localObject;
    }
    return (Parcelable)localObject;
  }
  
  public void setDialogLayoutResource(int paramInt)
  {
    this.mDialogLayoutResId = paramInt;
  }
  
  public void setNegativeButtonText(int paramInt)
  {
    setNegativeButtonText(getContext().getString(paramInt));
  }
  
  public void setNegativeButtonText(CharSequence paramCharSequence)
  {
    this.mNegativeButtonText = paramCharSequence;
  }
  
  public void setNeutralButtonText(int paramInt)
  {
    setNeutralButtonText(getContext().getString(paramInt));
  }
  
  public void setNeutralButtonText(CharSequence paramCharSequence)
  {
    this.mNeutralButtonText = paramCharSequence;
  }
  
  public void setPositiveButtonText(int paramInt)
  {
    setPositiveButtonText(getContext().getString(paramInt));
  }
  
  public void setPositiveButtonText(CharSequence paramCharSequence)
  {
    this.mPositiveButtonText = paramCharSequence;
  }
  
  protected void showDialog(Bundle paramBundle)
  {
    Object localObject = getContext();
    this.mWhichButtonClicked = -2;
    this.mBuilder = new AlertDialog.Builder((Context)localObject, 2131821596);
    if (this.mPositiveButtonText != null) {
      this.mBuilder.setPositiveButton(this.mPositiveButtonText, this);
    }
    if (this.mNegativeButtonText != null) {
      this.mBuilder.setNegativeButton(this.mNegativeButtonText, this);
    }
    if (this.mNeutralButtonText != null) {
      this.mBuilder.setNeutralButton(this.mNeutralButtonText, this);
    }
    localObject = onCreateDialogView();
    if (localObject != null)
    {
      onBindDialogView((View)localObject);
      this.mBuilder.setView((View)localObject);
    }
    onPrepareDialogBuilder(this.mBuilder);
    localObject = this.mBuilder.create();
    this.mDialog = ((Dialog)localObject);
    if (paramBundle != null) {
      ((Dialog)localObject).onRestoreInstanceState(paramBundle);
    }
    ((Dialog)localObject).setOnDismissListener(this);
    ((Dialog)localObject).show();
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public CustomDialogPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new CustomDialogPreference.SavedState(paramAnonymousParcel);
      }
      
      public CustomDialogPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new CustomDialogPreference.SavedState[paramAnonymousInt];
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


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\CustomDialogPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */