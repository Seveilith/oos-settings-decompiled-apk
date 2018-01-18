package android.support.v13.view;

import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

public class DragStartHelper
{
  private int mLastTouchX;
  private int mLastTouchY;
  private final OnDragStartListener mListener;
  private final View.OnLongClickListener mLongClickListener = new View.OnLongClickListener()
  {
    public boolean onLongClick(View paramAnonymousView)
    {
      return DragStartHelper.this.onLongClick(paramAnonymousView);
    }
  };
  private final View.OnTouchListener mTouchListener = new View.OnTouchListener()
  {
    public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
    {
      return DragStartHelper.this.onTouch(paramAnonymousView, paramAnonymousMotionEvent);
    }
  };
  private final View mView;
  
  public DragStartHelper(View paramView, OnDragStartListener paramOnDragStartListener)
  {
    this.mView = paramView;
    this.mListener = paramOnDragStartListener;
  }
  
  public void attach()
  {
    this.mView.setOnLongClickListener(this.mLongClickListener);
    this.mView.setOnTouchListener(this.mTouchListener);
  }
  
  public void detach()
  {
    this.mView.setOnLongClickListener(null);
    this.mView.setOnTouchListener(null);
  }
  
  public void getTouchPosition(Point paramPoint)
  {
    paramPoint.set(this.mLastTouchX, this.mLastTouchY);
  }
  
  public boolean onLongClick(View paramView)
  {
    return this.mListener.onDragStart(paramView, this);
  }
  
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2))
    {
      this.mLastTouchX = ((int)paramMotionEvent.getX());
      this.mLastTouchY = ((int)paramMotionEvent.getY());
    }
    if ((paramMotionEvent.getAction() == 2) && (MotionEventCompat.isFromSource(paramMotionEvent, 8194)) && ((MotionEventCompat.getButtonState(paramMotionEvent) & 0x1) != 0)) {
      return this.mListener.onDragStart(paramView, this);
    }
    return false;
  }
  
  public static abstract interface OnDragStartListener
  {
    public abstract boolean onDragStart(View paramView, DragStartHelper paramDragStartHelper);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v13\view\DragStartHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */