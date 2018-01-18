package com.oneplus.lib.widget.button;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View.BaseSavedState;
import android.view.ViewDebug.ExportedProperty;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.Checkable;
import com.oneplus.commonctrl.R.styleable;
import java.lang.reflect.Method;

public abstract class OPCompoundButton
  extends Button
  implements Checkable
{
  private static final int[] CHECKED_STATE_SET = { 16842912 };
  public static String TAG = OPCompoundButton.class.getSimpleName();
  private boolean mBroadcasting;
  private Drawable mButtonDrawable;
  private int mButtonResource;
  private ColorStateList mButtonTintList = null;
  private PorterDuff.Mode mButtonTintMode = null;
  private boolean mChecked;
  private boolean mHasButtonTint = false;
  private boolean mHasButtonTintMode = false;
  private OnCheckedChangeListener mOnCheckedChangeListener;
  private OnCheckedChangeListener mOnCheckedChangeWidgetListener;
  
  public OPCompoundButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPCompoundButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public OPCompoundButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public OPCompoundButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OPCompoundbutton, paramInt1, paramInt2);
    paramAttributeSet = paramContext.getDrawable(R.styleable.OPCompoundbutton_android_button);
    if (paramAttributeSet != null) {
      setButtonDrawable(paramAttributeSet);
    }
    if (paramContext.hasValue(R.styleable.OPCompoundbutton_android_buttonTintMode))
    {
      this.mButtonTintMode = parseTintMode(paramContext.getInt(R.styleable.OPCompoundbutton_android_buttonTintMode, -1), this.mButtonTintMode);
      this.mHasButtonTintMode = true;
    }
    if (paramContext.hasValue(R.styleable.OPCompoundbutton_android_buttonTint))
    {
      this.mButtonTintList = paramContext.getColorStateList(R.styleable.OPCompoundbutton_android_buttonTint);
      this.mHasButtonTint = true;
    }
    setChecked(paramContext.getBoolean(R.styleable.OPCompoundbutton_android_checked, false));
    setRadius(paramContext.getDimensionPixelSize(R.styleable.OPCompoundbutton_android_radius, -1));
    paramContext.recycle();
    applyButtonTint();
  }
  
  private void applyButtonTint()
  {
    if ((this.mButtonDrawable != null) && ((this.mHasButtonTint) || (this.mHasButtonTintMode)))
    {
      this.mButtonDrawable = this.mButtonDrawable.mutate();
      if (this.mHasButtonTint) {
        this.mButtonDrawable.setTintList(this.mButtonTintList);
      }
      if (this.mHasButtonTintMode) {
        this.mButtonDrawable.setTintMode(this.mButtonTintMode);
      }
      if (this.mButtonDrawable.isStateful()) {
        this.mButtonDrawable.setState(getDrawableState());
      }
    }
  }
  
  private static PorterDuff.Mode parseTintMode(int paramInt, PorterDuff.Mode paramMode)
  {
    switch (paramInt)
    {
    case 4: 
    case 6: 
    case 7: 
    case 8: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    default: 
      return paramMode;
    case 3: 
      return PorterDuff.Mode.SRC_OVER;
    case 5: 
      return PorterDuff.Mode.SRC_IN;
    case 9: 
      return PorterDuff.Mode.SRC_ATOP;
    case 14: 
      return PorterDuff.Mode.MULTIPLY;
    case 15: 
      return PorterDuff.Mode.SCREEN;
    }
    return PorterDuff.Mode.ADD;
  }
  
  private void setRadius(int paramInt)
  {
    if (paramInt == -1) {
      return;
    }
    Drawable localDrawable = getBackground();
    if ((localDrawable != null) && ((localDrawable instanceof RippleDrawable)))
    {
      localDrawable.mutate();
      ((RippleDrawable)localDrawable).setRadius(paramInt);
      return;
    }
    Log.i(TAG, "setRaidus fail , background not a rippleDrawable");
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    super.drawableHotspotChanged(paramFloat1, paramFloat2);
    if (this.mButtonDrawable != null) {
      this.mButtonDrawable.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    if (this.mButtonDrawable != null)
    {
      int[] arrayOfInt = getDrawableState();
      this.mButtonDrawable.setState(arrayOfInt);
      invalidate();
    }
  }
  
  public Drawable getButtonDrawable()
  {
    return this.mButtonDrawable;
  }
  
  public ColorStateList getButtonTintList()
  {
    return this.mButtonTintList;
  }
  
  public PorterDuff.Mode getButtonTintMode()
  {
    return this.mButtonTintMode;
  }
  
  public int getCompoundPaddingLeft()
  {
    int j = super.getCompoundPaddingLeft();
    int i = j;
    if (!isLayoutRtl())
    {
      Drawable localDrawable = this.mButtonDrawable;
      i = j;
      if (localDrawable != null) {
        i = j + localDrawable.getIntrinsicWidth();
      }
    }
    return i;
  }
  
  public int getCompoundPaddingRight()
  {
    int j = super.getCompoundPaddingRight();
    int i = j;
    if (isLayoutRtl())
    {
      Drawable localDrawable = this.mButtonDrawable;
      i = j;
      if (localDrawable != null) {
        i = j + localDrawable.getIntrinsicWidth();
      }
    }
    return i;
  }
  
  public int getHorizontalOffsetForDrawables()
  {
    Drawable localDrawable = this.mButtonDrawable;
    if (localDrawable != null) {
      return localDrawable.getIntrinsicWidth();
    }
    return 0;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isChecked()
  {
    return this.mChecked;
  }
  
  public boolean isLayoutRtl()
  {
    return getLayoutDirection() == 1;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (this.mButtonDrawable != null) {
      this.mButtonDrawable.jumpToCurrentState();
    }
  }
  
  protected int[] onCreateDrawableState(int paramInt)
  {
    int[] arrayOfInt = super.onCreateDrawableState(paramInt + 1);
    if (isChecked()) {
      mergeDrawableStates(arrayOfInt, CHECKED_STATE_SET);
    }
    return arrayOfInt;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    Drawable localDrawable1 = this.mButtonDrawable;
    int i;
    int j;
    int k;
    int m;
    if (localDrawable1 != null)
    {
      i = getGravity();
      j = localDrawable1.getIntrinsicHeight();
      k = localDrawable1.getIntrinsicWidth();
      switch (i & 0x70)
      {
      default: 
        i = 0;
        m = i + j;
        if (isLayoutRtl())
        {
          j = getWidth() - k;
          label82:
          if (!isLayoutRtl()) {
            break label190;
          }
          k = getWidth();
        }
        break;
      }
    }
    label190:
    for (;;)
    {
      localDrawable1.setBounds(j, i, k, m);
      Drawable localDrawable2 = getBackground();
      if (localDrawable2 != null) {
        localDrawable2.setHotspotBounds(j, i, k, m);
      }
      super.onDraw(paramCanvas);
      if (localDrawable1 != null)
      {
        i = getScrollX();
        j = getScrollY();
        if ((i != 0) || (j != 0)) {
          break label193;
        }
        localDrawable1.draw(paramCanvas);
      }
      return;
      i = getHeight() - j;
      break;
      i = (getHeight() - j) / 2;
      break;
      j = 0;
      break label82;
    }
    label193:
    paramCanvas.translate(i, j);
    localDrawable1.draw(paramCanvas);
    paramCanvas.translate(-i, -j);
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName(OPCompoundButton.class.getName());
    paramAccessibilityEvent.setChecked(this.mChecked);
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setClassName(OPCompoundButton.class.getName());
    paramAccessibilityNodeInfo.setCheckable(true);
    paramAccessibilityNodeInfo.setChecked(this.mChecked);
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    setChecked(paramParcelable.checked);
    requestLayout();
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.checked = isChecked();
    return localSavedState;
  }
  
  public boolean performClick()
  {
    toggle();
    boolean bool = super.performClick();
    if (!bool) {
      playSoundEffect(0);
    }
    return bool;
  }
  
  public void setButtonDrawable(int paramInt)
  {
    if ((paramInt != 0) && (paramInt == this.mButtonResource)) {
      return;
    }
    this.mButtonResource = paramInt;
    Drawable localDrawable = null;
    if (this.mButtonResource != 0) {
      localDrawable = getContext().getDrawable(this.mButtonResource);
    }
    setButtonDrawable(localDrawable);
  }
  
  public void setButtonDrawable(Drawable paramDrawable)
  {
    boolean bool = true;
    if (this.mButtonDrawable != paramDrawable)
    {
      if (this.mButtonDrawable != null)
      {
        this.mButtonDrawable.setCallback(null);
        unscheduleDrawable(this.mButtonDrawable);
      }
      this.mButtonDrawable = paramDrawable;
      if (paramDrawable != null) {
        paramDrawable.setCallback(this);
      }
    }
    try
    {
      Class.forName("android.graphics.drawable.Drawable").getMethod("setLayoutDirection", new Class[] { Integer.TYPE }).invoke(paramDrawable, new Object[] { Integer.valueOf(getLayoutDirection()) });
      if (paramDrawable.isStateful()) {
        paramDrawable.setState(getDrawableState());
      }
      if (getVisibility() == 0)
      {
        paramDrawable.setVisible(bool, false);
        setMinHeight(paramDrawable.getIntrinsicHeight());
        applyButtonTint();
        return;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.e(TAG, "setLayoutDirection with Exception!", localException);
        continue;
        bool = false;
      }
    }
  }
  
  public void setButtonTintList(ColorStateList paramColorStateList)
  {
    this.mButtonTintList = paramColorStateList;
    this.mHasButtonTint = true;
    applyButtonTint();
  }
  
  public void setButtonTintMode(PorterDuff.Mode paramMode)
  {
    this.mButtonTintMode = paramMode;
    this.mHasButtonTintMode = true;
    applyButtonTint();
  }
  
  public void setChecked(boolean paramBoolean)
  {
    if (this.mChecked != paramBoolean)
    {
      this.mChecked = paramBoolean;
      refreshDrawableState();
      try
      {
        Class.forName("android.view.View").getMethod("notifyViewAccessibilityStateChangedIfNeeded", new Class[] { Integer.TYPE }).invoke(this, new Object[] { Integer.valueOf(0) });
        if (this.mBroadcasting) {
          return;
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Log.e(TAG, "notifyViewAccessibilityStateChangedIfNeeded with Exception!", localException);
        }
        this.mBroadcasting = true;
        if (this.mOnCheckedChangeListener != null) {
          this.mOnCheckedChangeListener.onCheckedChanged(this, this.mChecked);
        }
        if (this.mOnCheckedChangeWidgetListener != null) {
          this.mOnCheckedChangeWidgetListener.onCheckedChanged(this, this.mChecked);
        }
        this.mBroadcasting = false;
      }
    }
  }
  
  public void setOnCheckedChangeListener(OnCheckedChangeListener paramOnCheckedChangeListener)
  {
    this.mOnCheckedChangeListener = paramOnCheckedChangeListener;
  }
  
  void setOnCheckedChangeWidgetListener(OnCheckedChangeListener paramOnCheckedChangeListener)
  {
    this.mOnCheckedChangeWidgetListener = paramOnCheckedChangeListener;
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
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mButtonDrawable);
  }
  
  public static abstract interface OnCheckedChangeListener
  {
    public abstract void onCheckedChanged(OPCompoundButton paramOPCompoundButton, boolean paramBoolean);
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public OPCompoundButton.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new OPCompoundButton.SavedState(paramAnonymousParcel, null);
      }
      
      public OPCompoundButton.SavedState[] newArray(int paramAnonymousInt)
      {
        return new OPCompoundButton.SavedState[paramAnonymousInt];
      }
    };
    boolean checked;
    
    private SavedState(Parcel paramParcel)
    {
      super();
      this.checked = ((Boolean)paramParcel.readValue(null)).booleanValue();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      return "CompoundButton.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " checked=" + this.checked + "}";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeValue(Boolean.valueOf(this.checked));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\button\OPCompoundButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */