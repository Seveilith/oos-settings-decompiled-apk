package com.oneplus.lib.widget.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.id;
import com.oneplus.commonctrl.R.layout;
import com.oneplus.commonctrl.R.style;
import com.oneplus.commonctrl.R.styleable;
import com.oneplus.lib.preference.Preference.BaseSavedState;
import com.oneplus.lib.widget.OPSeekBar;
import com.oneplus.lib.widget.OPSeekBar.OnSeekBarChangeListener;
import com.oneplus.lib.widget.util.utils;

public class OPSeekBarPreference
  extends OPPreference
  implements OPSeekBar.OnSeekBarChangeListener
{
  private int mMax;
  private int mProgress;
  private boolean mTrackingTouch;
  
  public OPSeekBarPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.op_seekBarPreferenceStyle);
  }
  
  public OPSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, R.style.OnePlus_DeviceDefault_Preference_Material_SeekBarPreference);
  }
  
  public OPSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, utils.resolveDefStyleAttr(paramContext, paramInt1), paramInt2);
    setMax(100);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SeekBarPreference, utils.resolveDefStyleAttr(paramContext, paramInt1), paramInt2);
    paramInt1 = paramContext.getResourceId(R.styleable.SeekBarPreference_android_layout, R.layout.preference_widget_seekbar);
    paramContext.recycle();
    setLayoutResource(paramInt1);
  }
  
  private void setProgress(int paramInt, boolean paramBoolean)
  {
    int i = paramInt;
    if (paramInt > this.mMax) {
      i = this.mMax;
    }
    paramInt = i;
    if (i < 0) {
      paramInt = 0;
    }
    if (paramInt != this.mProgress)
    {
      this.mProgress = paramInt;
      persistInt(paramInt);
      if (paramBoolean) {
        notifyChanged();
      }
    }
  }
  
  public int getProgress()
  {
    return this.mProgress;
  }
  
  public CharSequence getSummary()
  {
    return null;
  }
  
  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    ((SeekBar)paramView.findViewById(R.id.seekbar)).setVisibility(8);
    paramView = (OPSeekBar)paramView.findViewById(R.id.opseekbar);
    if (paramView != null)
    {
      paramView.setOnSeekBarChangeListener(this);
      paramView.setMax(this.mMax);
      paramView.setProgress(this.mProgress);
      paramView.setEnabled(isEnabled());
      paramView.setVisibility(0);
    }
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return Integer.valueOf(paramTypedArray.getInt(paramInt, 0));
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getAction() != 1)
    {
      if ((paramInt == 81) || (paramInt == 70))
      {
        setProgress(getProgress() + 1);
        return true;
      }
      if (paramInt == 69)
      {
        setProgress(getProgress() - 1);
        return true;
      }
    }
    return false;
  }
  
  public void onProgressChanged(OPSeekBar paramOPSeekBar, int paramInt, boolean paramBoolean)
  {
    if ((!paramBoolean) || (this.mTrackingTouch)) {
      return;
    }
    syncProgress(paramOPSeekBar);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!paramParcelable.getClass().equals(SavedState.class))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    this.mProgress = paramParcelable.progress;
    this.mMax = paramParcelable.max;
    notifyChanged();
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if (isPersistent()) {
      return (Parcelable)localObject;
    }
    localObject = new SavedState((Parcelable)localObject);
    ((SavedState)localObject).progress = this.mProgress;
    ((SavedState)localObject).max = this.mMax;
    return (Parcelable)localObject;
  }
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
  {
    if (paramBoolean) {}
    for (int i = getPersistedInt(this.mProgress);; i = ((Integer)paramObject).intValue())
    {
      setProgress(i);
      return;
    }
  }
  
  public void onStartTrackingTouch(OPSeekBar paramOPSeekBar)
  {
    this.mTrackingTouch = true;
  }
  
  public void onStopTrackingTouch(OPSeekBar paramOPSeekBar)
  {
    this.mTrackingTouch = false;
    if (paramOPSeekBar.getProgress() != this.mProgress) {
      syncProgress(paramOPSeekBar);
    }
  }
  
  public void setMax(int paramInt)
  {
    if (paramInt != this.mMax)
    {
      this.mMax = paramInt;
      notifyChanged();
    }
  }
  
  public void setProgress(int paramInt)
  {
    setProgress(paramInt, true);
  }
  
  void syncProgress(OPSeekBar paramOPSeekBar)
  {
    int i = paramOPSeekBar.getProgress();
    if (i != this.mProgress)
    {
      if (callChangeListener(Integer.valueOf(i))) {
        setProgress(i, false);
      }
    }
    else {
      return;
    }
    paramOPSeekBar.setProgress(this.mProgress);
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public OPSeekBarPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new OPSeekBarPreference.SavedState(paramAnonymousParcel);
      }
      
      public OPSeekBarPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new OPSeekBarPreference.SavedState[paramAnonymousInt];
      }
    };
    int max;
    int progress;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.progress = paramParcel.readInt();
      this.max = paramParcel.readInt();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.progress);
      paramParcel.writeInt(this.max);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\preference\OPSeekBarPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */