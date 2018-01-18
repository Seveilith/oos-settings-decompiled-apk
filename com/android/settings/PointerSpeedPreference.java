package com.android.settings;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.provider.Settings.System;
import android.support.v7.preference.Preference.BaseSavedState;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PointerSpeedPreference
  extends SeekBarDialogPreference
  implements SeekBar.OnSeekBarChangeListener
{
  private final InputManager mIm = (InputManager)getContext().getSystemService("input");
  private int mOldSpeed;
  private boolean mRestoredOldState;
  private SeekBar mSeekBar;
  private ContentObserver mSpeedObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      PointerSpeedPreference.-wrap0(PointerSpeedPreference.this);
    }
  };
  private boolean mTouchInProgress;
  
  public PointerSpeedPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void onSpeedChanged()
  {
    int i = this.mIm.getPointerSpeed(getContext());
    this.mSeekBar.setProgress(i + 7);
  }
  
  private void restoreOldState()
  {
    if (this.mRestoredOldState) {
      return;
    }
    this.mIm.tryPointerSpeed(this.mOldSpeed);
    this.mRestoredOldState = true;
  }
  
  protected void onBindDialogView(View paramView)
  {
    super.onBindDialogView(paramView);
    this.mSeekBar = getSeekBar(paramView);
    this.mSeekBar.setMax(14);
    this.mOldSpeed = this.mIm.getPointerSpeed(getContext());
    this.mSeekBar.setProgress(this.mOldSpeed + 7);
    this.mSeekBar.setOnSeekBarChangeListener(this);
  }
  
  protected void onClick()
  {
    super.onClick();
    getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor("pointer_speed"), true, this.mSpeedObserver);
    this.mRestoredOldState = false;
  }
  
  protected void onDialogClosed(boolean paramBoolean)
  {
    super.onDialogClosed(paramBoolean);
    ContentResolver localContentResolver = getContext().getContentResolver();
    if (paramBoolean) {
      this.mIm.setPointerSpeed(getContext(), this.mSeekBar.getProgress() - 7);
    }
    for (;;)
    {
      localContentResolver.unregisterContentObserver(this.mSpeedObserver);
      return;
      restoreOldState();
    }
  }
  
  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {
    if (!this.mTouchInProgress) {
      this.mIm.tryPointerSpeed(paramInt - 7);
    }
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable != null) && (paramParcelable.getClass().equals(SavedState.class)))
    {
      paramParcelable = (SavedState)paramParcelable;
      super.onRestoreInstanceState(paramParcelable.getSuperState());
      this.mOldSpeed = paramParcelable.oldSpeed;
      this.mIm.tryPointerSpeed(paramParcelable.progress - 7);
      return;
    }
    super.onRestoreInstanceState(paramParcelable);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if ((getDialog() != null) && (getDialog().isShowing()))
    {
      localObject = new SavedState((Parcelable)localObject);
      ((SavedState)localObject).progress = this.mSeekBar.getProgress();
      ((SavedState)localObject).oldSpeed = this.mOldSpeed;
      restoreOldState();
      return (Parcelable)localObject;
    }
    return (Parcelable)localObject;
  }
  
  public void onStartTrackingTouch(SeekBar paramSeekBar)
  {
    this.mTouchInProgress = true;
  }
  
  public void onStopTrackingTouch(SeekBar paramSeekBar)
  {
    this.mTouchInProgress = false;
    this.mIm.tryPointerSpeed(paramSeekBar.getProgress() - 7);
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public PointerSpeedPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new PointerSpeedPreference.SavedState(paramAnonymousParcel);
      }
      
      public PointerSpeedPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new PointerSpeedPreference.SavedState[paramAnonymousInt];
      }
    };
    int oldSpeed;
    int progress;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.progress = paramParcel.readInt();
      this.oldSpeed = paramParcel.readInt();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.progress);
      paramParcel.writeInt(this.oldSpeed);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\PointerSpeedPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */