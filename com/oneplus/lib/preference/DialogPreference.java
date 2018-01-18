package com.oneplus.lib.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.styleable;
import com.oneplus.lib.app.OPAlertDialog.Builder;

public abstract class DialogPreference
  extends Preference
  implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, PreferenceManager.OnActivityDestroyListener
{
  private OPAlertDialog.Builder mBuilder;
  private Dialog mDialog;
  private Drawable mDialogIcon;
  private int mDialogLayoutResId;
  private CharSequence mDialogMessage;
  private CharSequence mDialogTitle;
  private CharSequence mNegativeButtonText;
  private CharSequence mPositiveButtonText;
  private int mWhichButtonClicked;
  
  public DialogPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DialogPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.op_dialogPreferenceStyle);
  }
  
  public DialogPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public DialogPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.DialogPreference, paramInt1, paramInt2);
    this.mDialogTitle = paramContext.getString(R.styleable.DialogPreference_android_dialogTitle);
    if (this.mDialogTitle == null) {
      this.mDialogTitle = getTitle();
    }
    this.mDialogMessage = paramContext.getString(R.styleable.DialogPreference_android_dialogMessage);
    this.mDialogIcon = paramContext.getDrawable(R.styleable.DialogPreference_android_dialogIcon);
    this.mPositiveButtonText = paramContext.getString(R.styleable.DialogPreference_android_positiveButtonText);
    this.mNegativeButtonText = paramContext.getString(R.styleable.DialogPreference_android_negativeButtonText);
    this.mDialogLayoutResId = paramContext.getResourceId(R.styleable.DialogPreference_android_dialogLayout, this.mDialogLayoutResId);
    paramContext.recycle();
  }
  
  private void requestInputMethod(Dialog paramDialog)
  {
    paramDialog.getWindow().setSoftInputMode(5);
  }
  
  public Dialog getDialog()
  {
    return this.mDialog;
  }
  
  public Drawable getDialogIcon()
  {
    return this.mDialogIcon;
  }
  
  public int getDialogLayoutResource()
  {
    return this.mDialogLayoutResId;
  }
  
  public CharSequence getDialogMessage()
  {
    return this.mDialogMessage;
  }
  
  public CharSequence getDialogTitle()
  {
    return this.mDialogTitle;
  }
  
  public CharSequence getNegativeButtonText()
  {
    return this.mNegativeButtonText;
  }
  
  public CharSequence getPositiveButtonText()
  {
    return this.mPositiveButtonText;
  }
  
  protected boolean needInputMethod()
  {
    return false;
  }
  
  public void onActivityDestroy()
  {
    if ((this.mDialog != null) && (this.mDialog.isShowing()))
    {
      this.mDialog.dismiss();
      return;
    }
  }
  
  protected void onBindDialogView(View paramView)
  {
    paramView = paramView.findViewById(16908299);
    if (paramView != null)
    {
      CharSequence localCharSequence = getDialogMessage();
      int i = 8;
      if (!TextUtils.isEmpty(localCharSequence))
      {
        if ((paramView instanceof TextView)) {
          ((TextView)paramView).setText(localCharSequence);
        }
        i = 0;
      }
      if (paramView.getVisibility() != i) {
        paramView.setVisibility(i);
      }
    }
  }
  
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
  
  protected void onDialogClosed(boolean paramBoolean) {}
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    getPreferenceManager().unregisterOnActivityDestroyListener(this);
    this.mDialog = null;
    if (this.mWhichButtonClicked == -1) {}
    for (boolean bool = true;; bool = false)
    {
      onDialogClosed(bool);
      return;
    }
  }
  
  protected void onPrepareDialogBuilder(OPAlertDialog.Builder paramBuilder) {}
  
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
  
  public void setDialogIcon(int paramInt)
  {
    this.mDialogIcon = getContext().getDrawable(paramInt);
  }
  
  public void setDialogIcon(Drawable paramDrawable)
  {
    this.mDialogIcon = paramDrawable;
  }
  
  public void setDialogLayoutResource(int paramInt)
  {
    this.mDialogLayoutResId = paramInt;
  }
  
  public void setDialogMessage(int paramInt)
  {
    setDialogMessage(getContext().getString(paramInt));
  }
  
  public void setDialogMessage(CharSequence paramCharSequence)
  {
    this.mDialogMessage = paramCharSequence;
  }
  
  public void setDialogTitle(int paramInt)
  {
    setDialogTitle(getContext().getString(paramInt));
  }
  
  public void setDialogTitle(CharSequence paramCharSequence)
  {
    this.mDialogTitle = paramCharSequence;
  }
  
  public void setNegativeButtonText(int paramInt)
  {
    setNegativeButtonText(getContext().getString(paramInt));
  }
  
  public void setNegativeButtonText(CharSequence paramCharSequence)
  {
    this.mNegativeButtonText = paramCharSequence;
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
    this.mBuilder = new OPAlertDialog.Builder((Context)localObject).setTitle(this.mDialogTitle).setIcon(this.mDialogIcon).setPositiveButton(this.mPositiveButtonText, this).setNegativeButton(this.mNegativeButtonText, this);
    localObject = onCreateDialogView();
    if (localObject != null)
    {
      onBindDialogView((View)localObject);
      this.mBuilder.setView((View)localObject);
    }
    for (;;)
    {
      onPrepareDialogBuilder(this.mBuilder);
      getPreferenceManager().registerOnActivityDestroyListener(this);
      localObject = this.mBuilder.create();
      this.mDialog = ((Dialog)localObject);
      if (paramBundle != null) {
        ((Dialog)localObject).onRestoreInstanceState(paramBundle);
      }
      if (needInputMethod()) {
        requestInputMethod((Dialog)localObject);
      }
      ((Dialog)localObject).setOnDismissListener(this);
      ((Dialog)localObject).show();
      return;
      this.mBuilder.setMessage(this.mDialogMessage);
    }
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public DialogPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new DialogPreference.SavedState(paramAnonymousParcel);
      }
      
      public DialogPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new DialogPreference.SavedState[paramAnonymousInt];
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


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\DialogPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */