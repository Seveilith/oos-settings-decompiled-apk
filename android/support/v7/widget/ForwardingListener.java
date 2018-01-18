package android.support.v7.widget;

import android.os.Build.VERSION;
import android.os.SystemClock;
import android.support.annotation.RestrictTo;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.ShowableListMenu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public abstract class ForwardingListener
  implements View.OnTouchListener
{
  private int mActivePointerId;
  private Runnable mDisallowIntercept;
  private boolean mForwarding;
  private final int mLongPressTimeout;
  private final float mScaledTouchSlop;
  final View mSrc;
  private final int mTapTimeout;
  private final int[] mTmpLocation = new int[2];
  private Runnable mTriggerLongPress;
  
  public ForwardingListener(View paramView)
  {
    this.mSrc = paramView;
    paramView.setLongClickable(true);
    if (Build.VERSION.SDK_INT >= 12) {
      addDetachListenerApi12(paramView);
    }
    for (;;)
    {
      this.mScaledTouchSlop = ViewConfiguration.get(paramView.getContext()).getScaledTouchSlop();
      this.mTapTimeout = ViewConfiguration.getTapTimeout();
      this.mLongPressTimeout = ((this.mTapTimeout + ViewConfiguration.getLongPressTimeout()) / 2);
      return;
      addDetachListenerBase(paramView);
    }
  }
  
  private void addDetachListenerApi12(View paramView)
  {
    paramView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
    {
      public void onViewAttachedToWindow(View paramAnonymousView) {}
      
      public void onViewDetachedFromWindow(View paramAnonymousView)
      {
        ForwardingListener.-wrap0(ForwardingListener.this);
      }
    });
  }
  
  private void addDetachListenerBase(View paramView)
  {
    paramView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
    {
      boolean mIsAttached = ViewCompat.isAttachedToWindow(ForwardingListener.this.mSrc);
      
      public void onGlobalLayout()
      {
        boolean bool = this.mIsAttached;
        this.mIsAttached = ViewCompat.isAttachedToWindow(ForwardingListener.this.mSrc);
        if ((!bool) || (this.mIsAttached)) {
          return;
        }
        ForwardingListener.-wrap0(ForwardingListener.this);
      }
    });
  }
  
  private void clearCallbacks()
  {
    if (this.mTriggerLongPress != null) {
      this.mSrc.removeCallbacks(this.mTriggerLongPress);
    }
    if (this.mDisallowIntercept != null) {
      this.mSrc.removeCallbacks(this.mDisallowIntercept);
    }
  }
  
  private void onDetachedFromWindow()
  {
    this.mForwarding = false;
    this.mActivePointerId = -1;
    if (this.mDisallowIntercept != null) {
      this.mSrc.removeCallbacks(this.mDisallowIntercept);
    }
  }
  
  private boolean onTouchForwarded(MotionEvent paramMotionEvent)
  {
    View localView = this.mSrc;
    Object localObject = getPopup();
    boolean bool2;
    boolean bool1;
    if ((localObject != null) && (((ShowableListMenu)localObject).isShowing()))
    {
      localObject = (DropDownListView)((ShowableListMenu)localObject).getListView();
      if ((localObject == null) || (!((DropDownListView)localObject).isShown())) {
        break label120;
      }
      MotionEvent localMotionEvent = MotionEvent.obtainNoHistory(paramMotionEvent);
      toGlobalMotionEvent(localView, localMotionEvent);
      toLocalMotionEvent((View)localObject, localMotionEvent);
      bool2 = ((DropDownListView)localObject).onForwardedEvent(localMotionEvent, this.mActivePointerId);
      localMotionEvent.recycle();
      int i = MotionEventCompat.getActionMasked(paramMotionEvent);
      if (i == 1) {
        break label127;
      }
      if (i == 3) {
        break label122;
      }
      bool1 = true;
    }
    while (bool2)
    {
      return bool1;
      return false;
      label120:
      return false;
      label122:
      bool1 = false;
      continue;
      label127:
      bool1 = false;
    }
    return false;
  }
  
  private boolean onTouchObserved(MotionEvent paramMotionEvent)
  {
    View localView = this.mSrc;
    if (!localView.isEnabled()) {
      return false;
    }
    switch (MotionEventCompat.getActionMasked(paramMotionEvent))
    {
    default: 
    case 0: 
    case 2: 
      int i;
      do
      {
        return false;
        this.mActivePointerId = paramMotionEvent.getPointerId(0);
        if (this.mDisallowIntercept == null) {
          this.mDisallowIntercept = new DisallowIntercept();
        }
        localView.postDelayed(this.mDisallowIntercept, this.mTapTimeout);
        if (this.mTriggerLongPress == null) {
          this.mTriggerLongPress = new TriggerLongPress();
        }
        localView.postDelayed(this.mTriggerLongPress, this.mLongPressTimeout);
        return false;
        i = paramMotionEvent.findPointerIndex(this.mActivePointerId);
      } while ((i < 0) || (pointInView(localView, paramMotionEvent.getX(i), paramMotionEvent.getY(i), this.mScaledTouchSlop)));
      clearCallbacks();
      localView.getParent().requestDisallowInterceptTouchEvent(true);
      return true;
    }
    clearCallbacks();
    return false;
  }
  
  private static boolean pointInView(View paramView, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramFloat1 >= -paramFloat3)
    {
      bool1 = bool2;
      if (paramFloat2 >= -paramFloat3)
      {
        bool1 = bool2;
        if (paramFloat1 < paramView.getRight() - paramView.getLeft() + paramFloat3)
        {
          bool1 = bool2;
          if (paramFloat2 < paramView.getBottom() - paramView.getTop() + paramFloat3) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  private boolean toGlobalMotionEvent(View paramView, MotionEvent paramMotionEvent)
  {
    int[] arrayOfInt = this.mTmpLocation;
    paramView.getLocationOnScreen(arrayOfInt);
    paramMotionEvent.offsetLocation(arrayOfInt[0], arrayOfInt[1]);
    return true;
  }
  
  private boolean toLocalMotionEvent(View paramView, MotionEvent paramMotionEvent)
  {
    int[] arrayOfInt = this.mTmpLocation;
    paramView.getLocationOnScreen(arrayOfInt);
    paramMotionEvent.offsetLocation(-arrayOfInt[0], -arrayOfInt[1]);
    return true;
  }
  
  public abstract ShowableListMenu getPopup();
  
  protected boolean onForwardingStarted()
  {
    ShowableListMenu localShowableListMenu = getPopup();
    if ((localShowableListMenu == null) || (localShowableListMenu.isShowing())) {}
    for (;;)
    {
      return true;
      localShowableListMenu.show();
    }
  }
  
  protected boolean onForwardingStopped()
  {
    ShowableListMenu localShowableListMenu = getPopup();
    if ((localShowableListMenu != null) && (localShowableListMenu.isShowing())) {
      localShowableListMenu.dismiss();
    }
    return true;
  }
  
  void onLongPress()
  {
    clearCallbacks();
    View localView = this.mSrc;
    if ((!localView.isEnabled()) || (localView.isLongClickable())) {
      return;
    }
    if (!onForwardingStarted()) {
      return;
    }
    localView.getParent().requestDisallowInterceptTouchEvent(true);
    long l = SystemClock.uptimeMillis();
    MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
    localView.onTouchEvent(localMotionEvent);
    localMotionEvent.recycle();
    this.mForwarding = true;
  }
  
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    boolean bool3 = this.mForwarding;
    boolean bool1;
    if (bool3)
    {
      if ((!onTouchForwarded(paramMotionEvent)) && (onForwardingStopped())) {}
      for (bool1 = false;; bool1 = true)
      {
        this.mForwarding = bool1;
        if (bool1) {
          break;
        }
        return bool3;
      }
    }
    if (onTouchObserved(paramMotionEvent)) {}
    for (boolean bool2 = onForwardingStarted();; bool2 = false)
    {
      bool1 = bool2;
      if (!bool2) {
        break;
      }
      long l = SystemClock.uptimeMillis();
      paramView = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
      this.mSrc.onTouchEvent(paramView);
      paramView.recycle();
      bool1 = bool2;
      break;
    }
    return true;
  }
  
  private class DisallowIntercept
    implements Runnable
  {
    DisallowIntercept() {}
    
    public void run()
    {
      ViewParent localViewParent = ForwardingListener.this.mSrc.getParent();
      if (localViewParent != null) {
        localViewParent.requestDisallowInterceptTouchEvent(true);
      }
    }
  }
  
  private class TriggerLongPress
    implements Runnable
  {
    TriggerLongPress() {}
    
    public void run()
    {
      ForwardingListener.this.onLongPress();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\ForwardingListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */