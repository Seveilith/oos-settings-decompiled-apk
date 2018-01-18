package com.android.settings.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class ExploreByTouchHelper
  extends View.AccessibilityDelegate
{
  private static final String DEFAULT_CLASS_NAME = View.class.getName();
  public static final int INVALID_ID = Integer.MIN_VALUE;
  private Context mContext;
  private int mFocusedVirtualViewId = Integer.MIN_VALUE;
  private int mHoveredVirtualViewId = Integer.MIN_VALUE;
  private final AccessibilityManager mManager;
  private ExploreByTouchNodeProvider mNodeProvider;
  private final int[] mTempGlobalRect = new int[2];
  private final Rect mTempParentRect = new Rect();
  private final Rect mTempScreenRect = new Rect();
  private final Rect mTempVisibleRect = new Rect();
  private final View mView;
  
  public ExploreByTouchHelper(View paramView)
  {
    if (paramView == null) {
      throw new IllegalArgumentException("View may not be null");
    }
    this.mView = paramView;
    this.mContext = paramView.getContext();
    this.mManager = ((AccessibilityManager)this.mContext.getSystemService("accessibility"));
  }
  
  private boolean clearAccessibilityFocus(int paramInt)
  {
    if (isAccessibilityFocused(paramInt))
    {
      this.mFocusedVirtualViewId = Integer.MIN_VALUE;
      this.mView.invalidate();
      sendEventForVirtualView(paramInt, 65536);
      return true;
    }
    return false;
  }
  
  private AccessibilityEvent createEvent(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      return createEventForChild(paramInt1, paramInt2);
    }
    return createEventForHost(paramInt2);
  }
  
  private AccessibilityEvent createEventForChild(int paramInt1, int paramInt2)
  {
    AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(paramInt2);
    localAccessibilityEvent.setEnabled(true);
    localAccessibilityEvent.setClassName(DEFAULT_CLASS_NAME);
    onPopulateEventForVirtualView(paramInt1, localAccessibilityEvent);
    if ((localAccessibilityEvent.getText().isEmpty()) && (localAccessibilityEvent.getContentDescription() == null)) {
      throw new RuntimeException("Callbacks must add text or a content description in populateEventForVirtualViewId()");
    }
    localAccessibilityEvent.setPackageName(this.mView.getContext().getPackageName());
    localAccessibilityEvent.setSource(this.mView, paramInt1);
    return localAccessibilityEvent;
  }
  
  private AccessibilityEvent createEventForHost(int paramInt)
  {
    AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(paramInt);
    this.mView.onInitializeAccessibilityEvent(localAccessibilityEvent);
    return localAccessibilityEvent;
  }
  
  private AccessibilityNodeInfo createNode(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return createNodeForChild(paramInt);
    }
    return createNodeForHost();
  }
  
  private AccessibilityNodeInfo createNodeForChild(int paramInt)
  {
    AccessibilityNodeInfo localAccessibilityNodeInfo = AccessibilityNodeInfo.obtain();
    localAccessibilityNodeInfo.setEnabled(true);
    localAccessibilityNodeInfo.setClassName(DEFAULT_CLASS_NAME);
    onPopulateNodeForVirtualView(paramInt, localAccessibilityNodeInfo);
    if ((localAccessibilityNodeInfo.getText() == null) && (localAccessibilityNodeInfo.getContentDescription() == null)) {
      throw new RuntimeException("Callbacks must add text or a content description in populateNodeForVirtualViewId()");
    }
    localAccessibilityNodeInfo.getBoundsInParent(this.mTempParentRect);
    if (this.mTempParentRect.isEmpty()) {
      throw new RuntimeException("Callbacks must set parent bounds in populateNodeForVirtualViewId()");
    }
    int i = localAccessibilityNodeInfo.getActions();
    if ((i & 0x40) != 0) {
      throw new RuntimeException("Callbacks must not add ACTION_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
    }
    if ((i & 0x80) != 0) {
      throw new RuntimeException("Callbacks must not add ACTION_CLEAR_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
    }
    localAccessibilityNodeInfo.setPackageName(this.mView.getContext().getPackageName());
    localAccessibilityNodeInfo.setSource(this.mView, paramInt);
    localAccessibilityNodeInfo.setParent(this.mView);
    if (this.mFocusedVirtualViewId == paramInt)
    {
      localAccessibilityNodeInfo.setAccessibilityFocused(true);
      localAccessibilityNodeInfo.addAction(128);
    }
    for (;;)
    {
      if (intersectVisibleToUser(this.mTempParentRect))
      {
        localAccessibilityNodeInfo.setVisibleToUser(true);
        localAccessibilityNodeInfo.setBoundsInParent(this.mTempParentRect);
      }
      this.mView.getLocationOnScreen(this.mTempGlobalRect);
      paramInt = this.mTempGlobalRect[0];
      i = this.mTempGlobalRect[1];
      this.mTempScreenRect.set(this.mTempParentRect);
      this.mTempScreenRect.offset(paramInt, i);
      localAccessibilityNodeInfo.setBoundsInScreen(this.mTempScreenRect);
      return localAccessibilityNodeInfo;
      localAccessibilityNodeInfo.setAccessibilityFocused(false);
      localAccessibilityNodeInfo.addAction(64);
    }
  }
  
  private AccessibilityNodeInfo createNodeForHost()
  {
    AccessibilityNodeInfo localAccessibilityNodeInfo = AccessibilityNodeInfo.obtain(this.mView);
    this.mView.onInitializeAccessibilityNodeInfo(localAccessibilityNodeInfo);
    Object localObject = new LinkedList();
    getVisibleVirtualViews((List)localObject);
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      Integer localInteger = (Integer)((Iterator)localObject).next();
      localAccessibilityNodeInfo.addChild(this.mView, localInteger.intValue());
    }
    return localAccessibilityNodeInfo;
  }
  
  private boolean intersectVisibleToUser(Rect paramRect)
  {
    if ((paramRect == null) || (paramRect.isEmpty())) {
      return false;
    }
    if (this.mView.getWindowVisibility() != 0) {
      return false;
    }
    for (Object localObject = this.mView.getParent(); (localObject instanceof View); localObject = ((View)localObject).getParent())
    {
      localObject = (View)localObject;
      if ((((View)localObject).getAlpha() <= 0.0F) || (((View)localObject).getVisibility() != 0)) {
        return false;
      }
    }
    if (localObject == null) {
      return false;
    }
    if (!this.mView.getLocalVisibleRect(this.mTempVisibleRect)) {
      return false;
    }
    return paramRect.intersect(this.mTempVisibleRect);
  }
  
  private boolean isAccessibilityFocused(int paramInt)
  {
    return this.mFocusedVirtualViewId == paramInt;
  }
  
  private boolean manageFocusForChild(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    switch (paramInt2)
    {
    default: 
      return false;
    case 64: 
      return requestAccessibilityFocus(paramInt1);
    }
    return clearAccessibilityFocus(paramInt1);
  }
  
  private boolean performAction(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    switch (paramInt1)
    {
    default: 
      return performActionForChild(paramInt1, paramInt2, paramBundle);
    }
    return performActionForHost(paramInt2, paramBundle);
  }
  
  private boolean performActionForChild(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    switch (paramInt2)
    {
    default: 
      return onPerformActionForVirtualView(paramInt1, paramInt2, paramBundle);
    }
    return manageFocusForChild(paramInt1, paramInt2, paramBundle);
  }
  
  private boolean performActionForHost(int paramInt, Bundle paramBundle)
  {
    return this.mView.performAccessibilityAction(paramInt, paramBundle);
  }
  
  private boolean requestAccessibilityFocus(int paramInt)
  {
    AccessibilityManager localAccessibilityManager = (AccessibilityManager)this.mContext.getSystemService("accessibility");
    if ((this.mManager.isEnabled()) && (localAccessibilityManager.isTouchExplorationEnabled()))
    {
      if (!isAccessibilityFocused(paramInt))
      {
        this.mFocusedVirtualViewId = paramInt;
        this.mView.invalidate();
        sendEventForVirtualView(paramInt, 32768);
        return true;
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  private void updateHoveredVirtualView(int paramInt)
  {
    if (this.mHoveredVirtualViewId == paramInt) {
      return;
    }
    int i = this.mHoveredVirtualViewId;
    this.mHoveredVirtualViewId = paramInt;
    sendEventForVirtualView(paramInt, 128);
    sendEventForVirtualView(i, 256);
  }
  
  public boolean dispatchHoverEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mManager.isEnabled()) && (this.mManager.isTouchExplorationEnabled())) {}
    switch (paramMotionEvent.getAction())
    {
    case 8: 
    default: 
      return false;
      return false;
    case 7: 
    case 9: 
      int i = getVirtualViewAt(paramMotionEvent.getX(), paramMotionEvent.getY());
      updateHoveredVirtualView(i);
      return i != Integer.MIN_VALUE;
    }
    if (this.mFocusedVirtualViewId != Integer.MIN_VALUE)
    {
      updateHoveredVirtualView(Integer.MIN_VALUE);
      return true;
    }
    return false;
  }
  
  public AccessibilityNodeProvider getAccessibilityNodeProvider(View paramView)
  {
    if (this.mNodeProvider == null) {
      this.mNodeProvider = new ExploreByTouchNodeProvider(null);
    }
    return this.mNodeProvider;
  }
  
  public int getFocusedVirtualView()
  {
    return this.mFocusedVirtualViewId;
  }
  
  protected abstract int getVirtualViewAt(float paramFloat1, float paramFloat2);
  
  protected abstract void getVisibleVirtualViews(List<Integer> paramList);
  
  public void invalidateRoot()
  {
    invalidateVirtualView(-1);
  }
  
  public void invalidateVirtualView(int paramInt)
  {
    sendEventForVirtualView(paramInt, 2048);
  }
  
  protected abstract boolean onPerformActionForVirtualView(int paramInt1, int paramInt2, Bundle paramBundle);
  
  protected abstract void onPopulateEventForVirtualView(int paramInt, AccessibilityEvent paramAccessibilityEvent);
  
  protected abstract void onPopulateNodeForVirtualView(int paramInt, AccessibilityNodeInfo paramAccessibilityNodeInfo);
  
  public boolean sendEventForVirtualView(int paramInt1, int paramInt2)
  {
    ViewParent localViewParent;
    if ((paramInt1 != Integer.MIN_VALUE) && (this.mManager.isEnabled()))
    {
      localViewParent = this.mView.getParent();
      if (localViewParent == null) {
        return false;
      }
    }
    else
    {
      return false;
    }
    AccessibilityEvent localAccessibilityEvent = createEvent(paramInt1, paramInt2);
    return localViewParent.requestSendAccessibilityEvent(this.mView, localAccessibilityEvent);
  }
  
  private class ExploreByTouchNodeProvider
    extends AccessibilityNodeProvider
  {
    private ExploreByTouchNodeProvider() {}
    
    public AccessibilityNodeInfo createAccessibilityNodeInfo(int paramInt)
    {
      return ExploreByTouchHelper.-wrap0(ExploreByTouchHelper.this, paramInt);
    }
    
    public boolean performAction(int paramInt1, int paramInt2, Bundle paramBundle)
    {
      return ExploreByTouchHelper.-wrap1(ExploreByTouchHelper.this, paramInt1, paramInt2, paramBundle);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\ExploreByTouchHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */