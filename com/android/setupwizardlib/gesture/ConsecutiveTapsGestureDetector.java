package com.android.setupwizardlib.gesture;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public final class ConsecutiveTapsGestureDetector
{
  private final int mConsecutiveTapTouchSlopSquare;
  private int mConsecutiveTapsCounter = 0;
  private final OnConsecutiveTapsListener mListener;
  private MotionEvent mPreviousTapEvent;
  private final View mView;
  
  public ConsecutiveTapsGestureDetector(OnConsecutiveTapsListener paramOnConsecutiveTapsListener, View paramView)
  {
    this.mListener = paramOnConsecutiveTapsListener;
    this.mView = paramView;
    int i = ViewConfiguration.get(this.mView.getContext()).getScaledDoubleTapSlop();
    this.mConsecutiveTapTouchSlopSquare = (i * i);
  }
  
  private boolean isConsecutiveTap(MotionEvent paramMotionEvent)
  {
    boolean bool = false;
    if (this.mPreviousTapEvent == null) {
      return false;
    }
    double d1 = this.mPreviousTapEvent.getX() - paramMotionEvent.getX();
    double d2 = this.mPreviousTapEvent.getY() - paramMotionEvent.getY();
    if (d1 * d1 + d2 * d2 <= this.mConsecutiveTapTouchSlopSquare) {
      bool = true;
    }
    return bool;
  }
  
  public void onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getAction() == 1)
    {
      Rect localRect = new Rect();
      int[] arrayOfInt = new int[2];
      this.mView.getLocationOnScreen(arrayOfInt);
      localRect.set(arrayOfInt[0], arrayOfInt[1], arrayOfInt[0] + this.mView.getWidth(), arrayOfInt[1] + this.mView.getHeight());
      if (!localRect.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())) {
        break label139;
      }
      if (!isConsecutiveTap(paramMotionEvent)) {
        break label131;
      }
      this.mConsecutiveTapsCounter += 1;
      this.mListener.onConsecutiveTaps(this.mConsecutiveTapsCounter);
    }
    for (;;)
    {
      if (this.mPreviousTapEvent != null) {
        this.mPreviousTapEvent.recycle();
      }
      this.mPreviousTapEvent = MotionEvent.obtain(paramMotionEvent);
      return;
      label131:
      this.mConsecutiveTapsCounter = 1;
      break;
      label139:
      this.mConsecutiveTapsCounter = 0;
    }
  }
  
  public void resetCounter()
  {
    this.mConsecutiveTapsCounter = 0;
  }
  
  public static abstract interface OnConsecutiveTapsListener
  {
    public abstract void onConsecutiveTaps(int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\gesture\ConsecutiveTapsGestureDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */