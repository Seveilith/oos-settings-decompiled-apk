package com.android.settings.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import java.util.List;

public class LabeledSeekBar
  extends SeekBar
{
  private final ExploreByTouchHelper mAccessHelper = new LabeledSeekBarExploreByTouchHelper(this);
  private String[] mLabels;
  private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
  private final SeekBar.OnSeekBarChangeListener mProxySeekBarListener = new SeekBar.OnSeekBarChangeListener()
  {
    public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      if (LabeledSeekBar.-get1(LabeledSeekBar.this) != null)
      {
        LabeledSeekBar.-get1(LabeledSeekBar.this).onProgressChanged(paramAnonymousSeekBar, paramAnonymousInt, paramAnonymousBoolean);
        LabeledSeekBar.-wrap0(LabeledSeekBar.this, paramAnonymousInt);
      }
    }
    
    public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar)
    {
      if (LabeledSeekBar.-get1(LabeledSeekBar.this) != null) {
        LabeledSeekBar.-get1(LabeledSeekBar.this).onStartTrackingTouch(paramAnonymousSeekBar);
      }
    }
    
    public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar)
    {
      if (LabeledSeekBar.-get1(LabeledSeekBar.this) != null) {
        LabeledSeekBar.-get1(LabeledSeekBar.this).onStopTrackingTouch(paramAnonymousSeekBar);
      }
    }
  };
  
  public LabeledSeekBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842875);
  }
  
  public LabeledSeekBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public LabeledSeekBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    ViewCompat.setAccessibilityDelegate(this, this.mAccessHelper);
    super.setOnSeekBarChangeListener(this.mProxySeekBarListener);
  }
  
  private void sendClickEventForAccessibility(int paramInt)
  {
    this.mAccessHelper.invalidateRoot();
    this.mAccessHelper.sendEventForVirtualView(paramInt, 1);
  }
  
  protected boolean dispatchHoverEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mAccessHelper.dispatchHoverEvent(paramMotionEvent)) {
      return super.dispatchHoverEvent(paramMotionEvent);
    }
    return true;
  }
  
  public void setLabels(String[] paramArrayOfString)
  {
    this.mLabels = paramArrayOfString;
  }
  
  public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener paramOnSeekBarChangeListener)
  {
    this.mOnSeekBarChangeListener = paramOnSeekBarChangeListener;
  }
  
  public void setProgress(int paramInt)
  {
    try
    {
      if (this.mAccessHelper != null) {
        this.mAccessHelper.invalidateRoot();
      }
      super.setProgress(paramInt);
      return;
    }
    finally {}
  }
  
  private class LabeledSeekBarExploreByTouchHelper
    extends ExploreByTouchHelper
  {
    private boolean mIsLayoutRtl;
    
    public LabeledSeekBarExploreByTouchHelper(LabeledSeekBar paramLabeledSeekBar)
    {
      super();
      if (paramLabeledSeekBar.getResources().getConfiguration().getLayoutDirection() == 1) {}
      for (;;)
      {
        this.mIsLayoutRtl = bool;
        return;
        bool = false;
      }
    }
    
    private Rect getBoundsInParentFromVirtualViewId(int paramInt)
    {
      if (this.mIsLayoutRtl) {
        paramInt = LabeledSeekBar.this.getMax() - paramInt;
      }
      for (;;)
      {
        int i = (paramInt * 2 - 1) * getHalfVirtualViewWidth() + LabeledSeekBar.this.getPaddingStart();
        int j = (paramInt * 2 + 1) * getHalfVirtualViewWidth() + LabeledSeekBar.this.getPaddingStart();
        if (paramInt == 0) {
          i = 0;
        }
        if (paramInt == LabeledSeekBar.this.getMax()) {
          j = LabeledSeekBar.this.getWidth();
        }
        Rect localRect = new Rect();
        localRect.set(i, 0, j, LabeledSeekBar.this.getHeight());
        return localRect;
      }
    }
    
    private int getHalfVirtualViewWidth()
    {
      return Math.max(0, (LabeledSeekBar.this.getWidth() - LabeledSeekBar.this.getPaddingStart() - LabeledSeekBar.this.getPaddingEnd()) / (LabeledSeekBar.this.getMax() * 2));
    }
    
    private int getVirtualViewIdIndexFromX(float paramFloat)
    {
      int j = (Math.max(0, ((int)paramFloat - LabeledSeekBar.this.getPaddingStart()) / getHalfVirtualViewWidth()) + 1) / 2;
      int i = j;
      if (this.mIsLayoutRtl) {
        i = LabeledSeekBar.this.getMax() - j;
      }
      return i;
    }
    
    protected int getVirtualViewAt(float paramFloat1, float paramFloat2)
    {
      return getVirtualViewIdIndexFromX(paramFloat1);
    }
    
    protected void getVisibleVirtualViews(List<Integer> paramList)
    {
      int i = 0;
      int j = LabeledSeekBar.this.getMax();
      while (i <= j)
      {
        paramList.add(Integer.valueOf(i));
        i += 1;
      }
    }
    
    protected boolean onPerformActionForVirtualView(int paramInt1, int paramInt2, Bundle paramBundle)
    {
      if (paramInt1 == -1) {
        return false;
      }
      switch (paramInt2)
      {
      default: 
        return false;
      }
      LabeledSeekBar.this.setProgress(paramInt1);
      sendEventForVirtualView(paramInt1, 1);
      return true;
    }
    
    protected void onPopulateEventForHost(AccessibilityEvent paramAccessibilityEvent)
    {
      paramAccessibilityEvent.setClassName(RadioGroup.class.getName());
    }
    
    protected void onPopulateEventForVirtualView(int paramInt, AccessibilityEvent paramAccessibilityEvent)
    {
      paramAccessibilityEvent.setClassName(RadioButton.class.getName());
      paramAccessibilityEvent.setContentDescription(LabeledSeekBar.-get0(LabeledSeekBar.this)[paramInt]);
      if (paramInt == LabeledSeekBar.this.getProgress()) {}
      for (boolean bool = true;; bool = false)
      {
        paramAccessibilityEvent.setChecked(bool);
        return;
      }
    }
    
    protected void onPopulateNodeForHost(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      paramAccessibilityNodeInfoCompat.setClassName(RadioGroup.class.getName());
    }
    
    protected void onPopulateNodeForVirtualView(int paramInt, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      boolean bool = true;
      paramAccessibilityNodeInfoCompat.setClassName(RadioButton.class.getName());
      paramAccessibilityNodeInfoCompat.setBoundsInParent(getBoundsInParentFromVirtualViewId(paramInt));
      paramAccessibilityNodeInfoCompat.addAction(16);
      paramAccessibilityNodeInfoCompat.setContentDescription(LabeledSeekBar.-get0(LabeledSeekBar.this)[paramInt]);
      paramAccessibilityNodeInfoCompat.setClickable(true);
      paramAccessibilityNodeInfoCompat.setCheckable(true);
      if (paramInt == LabeledSeekBar.this.getProgress()) {}
      for (;;)
      {
        paramAccessibilityNodeInfoCompat.setChecked(bool);
        return;
        bool = false;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\LabeledSeekBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */