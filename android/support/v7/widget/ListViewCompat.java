package android.support.v7.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.lang.reflect.Field;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class ListViewCompat
  extends ListView
{
  public static final int INVALID_POSITION = -1;
  public static final int NO_POSITION = -1;
  private static final int[] STATE_SET_NOTHING = { 0 };
  private Field mIsChildViewEnabled;
  protected int mMotionPosition;
  int mSelectionBottomPadding = 0;
  int mSelectionLeftPadding = 0;
  int mSelectionRightPadding = 0;
  int mSelectionTopPadding = 0;
  private GateKeeperDrawable mSelector;
  final Rect mSelectorRect = new Rect();
  
  public ListViewCompat(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ListViewCompat(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ListViewCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    try
    {
      this.mIsChildViewEnabled = AbsListView.class.getDeclaredField("mIsChildViewEnabled");
      this.mIsChildViewEnabled.setAccessible(true);
      return;
    }
    catch (NoSuchFieldException paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    drawSelectorCompat(paramCanvas);
    super.dispatchDraw(paramCanvas);
  }
  
  protected void drawSelectorCompat(Canvas paramCanvas)
  {
    if (!this.mSelectorRect.isEmpty())
    {
      Drawable localDrawable = getSelector();
      if (localDrawable != null)
      {
        localDrawable.setBounds(this.mSelectorRect);
        localDrawable.draw(paramCanvas);
      }
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    setSelectorEnabled(true);
    updateSelectorStateCompat();
  }
  
  public int lookForSelectablePosition(int paramInt, boolean paramBoolean)
  {
    ListAdapter localListAdapter = getAdapter();
    if ((localListAdapter == null) || (isInTouchMode())) {
      return -1;
    }
    int j = localListAdapter.getCount();
    if (!getAdapter().areAllItemsEnabled())
    {
      int i;
      if (paramBoolean)
      {
        paramInt = Math.max(0, paramInt);
        for (;;)
        {
          i = paramInt;
          if (paramInt < j)
          {
            if (localListAdapter.isEnabled(paramInt)) {
              i = paramInt;
            }
          }
          else
          {
            if ((i >= 0) && (i < j)) {
              break;
            }
            return -1;
          }
          paramInt += 1;
        }
      }
      paramInt = Math.min(paramInt, j - 1);
      for (;;)
      {
        i = paramInt;
        if (paramInt < 0) {
          break;
        }
        i = paramInt;
        if (localListAdapter.isEnabled(paramInt)) {
          break;
        }
        paramInt -= 1;
      }
      return i;
    }
    if ((paramInt < 0) || (paramInt >= j)) {
      return -1;
    }
    return paramInt;
  }
  
  public int measureHeightOfChildrenCompat(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paramInt2 = getListPaddingTop();
    int i = getListPaddingBottom();
    getListPaddingLeft();
    getListPaddingRight();
    paramInt3 = getDividerHeight();
    Object localObject = getDivider();
    ListAdapter localListAdapter = getAdapter();
    if (localListAdapter == null) {
      return paramInt2 + i;
    }
    paramInt2 += i;
    int j;
    int m;
    int i1;
    if ((paramInt3 > 0) && (localObject != null))
    {
      j = 0;
      localObject = null;
      m = 0;
      i1 = localListAdapter.getCount();
      i = 0;
    }
    for (;;)
    {
      if (i >= i1) {
        return paramInt2;
      }
      int n = localListAdapter.getItemViewType(i);
      int k = m;
      if (n != m)
      {
        localObject = null;
        k = n;
      }
      View localView = localListAdapter.getView(i, (View)localObject, this);
      ViewGroup.LayoutParams localLayoutParams = localView.getLayoutParams();
      localObject = localLayoutParams;
      if (localLayoutParams == null)
      {
        localObject = generateDefaultLayoutParams();
        localView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      }
      if (((ViewGroup.LayoutParams)localObject).height > 0) {}
      for (m = View.MeasureSpec.makeMeasureSpec(((ViewGroup.LayoutParams)localObject).height, 1073741824);; m = View.MeasureSpec.makeMeasureSpec(0, 0))
      {
        localView.measure(paramInt1, m);
        localView.forceLayout();
        m = paramInt2;
        if (i > 0) {
          m = paramInt2 + paramInt3;
        }
        paramInt2 = m + localView.getMeasuredHeight();
        if (paramInt2 < paramInt4) {
          break label267;
        }
        if ((paramInt5 < 0) || (i <= paramInt5) || (j <= 0) || (paramInt2 == paramInt4)) {
          break label264;
        }
        return j;
        paramInt3 = 0;
        break;
      }
      label264:
      return paramInt4;
      label267:
      m = j;
      if (paramInt5 >= 0)
      {
        m = j;
        if (i >= paramInt5) {
          m = paramInt2;
        }
      }
      i += 1;
      localObject = localView;
      j = m;
      m = k;
    }
    return paramInt2;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getAction())
    {
    }
    for (;;)
    {
      return super.onTouchEvent(paramMotionEvent);
      this.mMotionPosition = pointToPosition((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
    }
  }
  
  protected void positionSelectorCompat(int paramInt, View paramView)
  {
    Rect localRect = this.mSelectorRect;
    localRect.set(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
    localRect.left -= this.mSelectionLeftPadding;
    localRect.top -= this.mSelectionTopPadding;
    localRect.right += this.mSelectionRightPadding;
    localRect.bottom += this.mSelectionBottomPadding;
    try
    {
      boolean bool = this.mIsChildViewEnabled.getBoolean(this);
      if (paramView.isEnabled() != bool)
      {
        paramView = this.mIsChildViewEnabled;
        if (!bool) {
          break label134;
        }
      }
      label134:
      for (bool = false;; bool = true)
      {
        paramView.set(this, Boolean.valueOf(bool));
        if (paramInt != -1) {
          refreshDrawableState();
        }
        return;
      }
      return;
    }
    catch (IllegalAccessException paramView)
    {
      paramView.printStackTrace();
    }
  }
  
  protected void positionSelectorLikeFocusCompat(int paramInt, View paramView)
  {
    boolean bool = true;
    Drawable localDrawable = getSelector();
    int i;
    float f1;
    float f2;
    if ((localDrawable != null) && (paramInt != -1))
    {
      i = 1;
      if (i != 0) {
        localDrawable.setVisible(false, false);
      }
      positionSelectorCompat(paramInt, paramView);
      if (i != 0)
      {
        paramView = this.mSelectorRect;
        f1 = paramView.exactCenterX();
        f2 = paramView.exactCenterY();
        if (getVisibility() != 0) {
          break label93;
        }
      }
    }
    for (;;)
    {
      localDrawable.setVisible(bool, false);
      DrawableCompat.setHotspot(localDrawable, f1, f2);
      return;
      i = 0;
      break;
      label93:
      bool = false;
    }
  }
  
  protected void positionSelectorLikeTouchCompat(int paramInt, View paramView, float paramFloat1, float paramFloat2)
  {
    positionSelectorLikeFocusCompat(paramInt, paramView);
    paramView = getSelector();
    if ((paramView != null) && (paramInt != -1)) {
      DrawableCompat.setHotspot(paramView, paramFloat1, paramFloat2);
    }
  }
  
  public void setSelector(Drawable paramDrawable)
  {
    Object localObject = null;
    if (paramDrawable != null) {
      localObject = new GateKeeperDrawable(paramDrawable);
    }
    this.mSelector = ((GateKeeperDrawable)localObject);
    super.setSelector(this.mSelector);
    localObject = new Rect();
    if (paramDrawable != null) {
      paramDrawable.getPadding((Rect)localObject);
    }
    this.mSelectionLeftPadding = ((Rect)localObject).left;
    this.mSelectionTopPadding = ((Rect)localObject).top;
    this.mSelectionRightPadding = ((Rect)localObject).right;
    this.mSelectionBottomPadding = ((Rect)localObject).bottom;
  }
  
  protected void setSelectorEnabled(boolean paramBoolean)
  {
    if (this.mSelector != null) {
      this.mSelector.setEnabled(paramBoolean);
    }
  }
  
  protected boolean shouldShowSelectorCompat()
  {
    if (touchModeDrawsInPressedStateCompat()) {
      return isPressed();
    }
    return false;
  }
  
  protected boolean touchModeDrawsInPressedStateCompat()
  {
    return false;
  }
  
  protected void updateSelectorStateCompat()
  {
    Drawable localDrawable = getSelector();
    if ((localDrawable != null) && (shouldShowSelectorCompat())) {
      localDrawable.setState(getDrawableState());
    }
  }
  
  private static class GateKeeperDrawable
    extends DrawableWrapper
  {
    private boolean mEnabled = true;
    
    public GateKeeperDrawable(Drawable paramDrawable)
    {
      super();
    }
    
    public void draw(Canvas paramCanvas)
    {
      if (this.mEnabled) {
        super.draw(paramCanvas);
      }
    }
    
    void setEnabled(boolean paramBoolean)
    {
      this.mEnabled = paramBoolean;
    }
    
    public void setHotspot(float paramFloat1, float paramFloat2)
    {
      if (this.mEnabled) {
        super.setHotspot(paramFloat1, paramFloat2);
      }
    }
    
    public void setHotspotBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (this.mEnabled) {
        super.setHotspotBounds(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public boolean setState(int[] paramArrayOfInt)
    {
      if (this.mEnabled) {
        return super.setState(paramArrayOfInt);
      }
      return false;
    }
    
    public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
    {
      if (this.mEnabled) {
        return super.setVisible(paramBoolean1, paramBoolean2);
      }
      return false;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\ListViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */