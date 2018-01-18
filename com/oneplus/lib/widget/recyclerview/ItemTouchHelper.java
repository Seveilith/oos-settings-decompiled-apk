package com.oneplus.lib.widget.recyclerview;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import com.oneplus.commonctrl.R.dimen;
import com.oneplus.commonctrl.R.id;
import java.util.ArrayList;
import java.util.List;

public class ItemTouchHelper
  extends RecyclerView.ItemDecoration
  implements RecyclerView.OnChildAttachStateChangeListener
{
  private static final int ACTION_MODE_DRAG_MASK = 16711680;
  private static final int ACTION_MODE_IDLE_MASK = 255;
  private static final int ACTION_MODE_SWIPE_MASK = 65280;
  public static final int ACTION_STATE_DRAG = 2;
  public static final int ACTION_STATE_IDLE = 0;
  public static final int ACTION_STATE_SWIPE = 1;
  private static final int ACTIVE_POINTER_ID_NONE = -1;
  public static final int ANIMATION_TYPE_DRAG = 8;
  public static final int ANIMATION_TYPE_SWIPE_CANCEL = 4;
  public static final int ANIMATION_TYPE_SWIPE_SUCCESS = 2;
  private static final boolean DEBUG = false;
  private static final int DIRECTION_FLAG_COUNT = 8;
  public static final int DOWN = 2;
  public static final int END = 32;
  public static final int LEFT = 4;
  public static final int RIGHT = 8;
  public static final int START = 16;
  private static final String TAG = "ItemTouchHelper";
  public static final int UP = 1;
  int mActionState = 0;
  int mActivePointerId = -1;
  Callback mCallback;
  private RecyclerView.ChildDrawingOrderCallback mChildDrawingOrderCallback = null;
  private List<Integer> mDistances;
  private long mDragScrollStartTimeInMs;
  float mDx;
  float mDy;
  private GestureDetector mGestureDetector;
  float mInitialTouchX;
  float mInitialTouchY;
  private final RecyclerView.OnItemTouchListener mOnItemTouchListener = new RecyclerView.OnItemTouchListener()
  {
    public boolean onInterceptTouchEvent(RecyclerView paramAnonymousRecyclerView, MotionEvent paramAnonymousMotionEvent)
    {
      ItemTouchHelper.-get0(ItemTouchHelper.this).onTouchEvent(paramAnonymousMotionEvent);
      int i = paramAnonymousMotionEvent.getActionMasked();
      if (i == 0)
      {
        ItemTouchHelper.this.mActivePointerId = paramAnonymousMotionEvent.getPointerId(0);
        ItemTouchHelper.this.mInitialTouchX = paramAnonymousMotionEvent.getX();
        ItemTouchHelper.this.mInitialTouchY = paramAnonymousMotionEvent.getY();
        ItemTouchHelper.-wrap7(ItemTouchHelper.this);
        if (ItemTouchHelper.this.mSelected == null)
        {
          paramAnonymousRecyclerView = ItemTouchHelper.-wrap4(ItemTouchHelper.this, paramAnonymousMotionEvent);
          if (paramAnonymousRecyclerView != null)
          {
            ItemTouchHelper localItemTouchHelper = ItemTouchHelper.this;
            localItemTouchHelper.mInitialTouchX -= paramAnonymousRecyclerView.mX;
            localItemTouchHelper = ItemTouchHelper.this;
            localItemTouchHelper.mInitialTouchY -= paramAnonymousRecyclerView.mY;
            ItemTouchHelper.-wrap5(ItemTouchHelper.this, paramAnonymousRecyclerView.mViewHolder, true);
            if (ItemTouchHelper.this.mPendingCleanup.remove(paramAnonymousRecyclerView.mViewHolder.itemView)) {
              ItemTouchHelper.this.mCallback.clearView(ItemTouchHelper.-get3(ItemTouchHelper.this), paramAnonymousRecyclerView.mViewHolder);
            }
            ItemTouchHelper.-wrap10(ItemTouchHelper.this, paramAnonymousRecyclerView.mViewHolder, paramAnonymousRecyclerView.mActionState);
            ItemTouchHelper.-wrap11(ItemTouchHelper.this, paramAnonymousMotionEvent, ItemTouchHelper.this.mSelectedFlags, 0);
          }
        }
      }
      for (;;)
      {
        if (ItemTouchHelper.-get5(ItemTouchHelper.this) != null) {
          ItemTouchHelper.-get5(ItemTouchHelper.this).addMovement(paramAnonymousMotionEvent);
        }
        if (ItemTouchHelper.this.mSelected == null) {
          break;
        }
        return true;
        if ((i == 3) || (i == 1))
        {
          ItemTouchHelper.this.mActivePointerId = -1;
          ItemTouchHelper.-wrap10(ItemTouchHelper.this, null, 0);
        }
        else if (ItemTouchHelper.this.mActivePointerId != -1)
        {
          int j = paramAnonymousMotionEvent.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
          if (j >= 0) {
            ItemTouchHelper.-wrap1(ItemTouchHelper.this, i, paramAnonymousMotionEvent, j);
          }
        }
      }
      return false;
    }
    
    public void onRequestDisallowInterceptTouchEvent(boolean paramAnonymousBoolean)
    {
      if (!paramAnonymousBoolean) {
        return;
      }
      ItemTouchHelper.-wrap10(ItemTouchHelper.this, null, 0);
    }
    
    public void onTouchEvent(RecyclerView paramAnonymousRecyclerView, MotionEvent paramAnonymousMotionEvent)
    {
      ItemTouchHelper.-get0(ItemTouchHelper.this).onTouchEvent(paramAnonymousMotionEvent);
      if (ItemTouchHelper.-get5(ItemTouchHelper.this) != null) {
        ItemTouchHelper.-get5(ItemTouchHelper.this).addMovement(paramAnonymousMotionEvent);
      }
      if (ItemTouchHelper.this.mActivePointerId == -1) {
        return;
      }
      int i = paramAnonymousMotionEvent.getActionMasked();
      int j = paramAnonymousMotionEvent.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
      if (j >= 0) {
        ItemTouchHelper.-wrap1(ItemTouchHelper.this, i, paramAnonymousMotionEvent, j);
      }
      paramAnonymousRecyclerView = ItemTouchHelper.this.mSelected;
      if (paramAnonymousRecyclerView == null) {
        return;
      }
      switch (i)
      {
      }
      do
      {
        do
        {
          return;
        } while (j < 0);
        ItemTouchHelper.-wrap11(ItemTouchHelper.this, paramAnonymousMotionEvent, ItemTouchHelper.this.mSelectedFlags, j);
        ItemTouchHelper.-wrap6(ItemTouchHelper.this, paramAnonymousRecyclerView);
        ItemTouchHelper.-get3(ItemTouchHelper.this).removeCallbacks(ItemTouchHelper.-get4(ItemTouchHelper.this));
        ItemTouchHelper.-get4(ItemTouchHelper.this).run();
        ItemTouchHelper.-get3(ItemTouchHelper.this).invalidate();
        return;
        if (ItemTouchHelper.-get5(ItemTouchHelper.this) != null) {
          ItemTouchHelper.-get5(ItemTouchHelper.this).computeCurrentVelocity(1000, ItemTouchHelper.-get3(ItemTouchHelper.this).getMaxFlingVelocity());
        }
        ItemTouchHelper.-wrap10(ItemTouchHelper.this, null, 0);
        ItemTouchHelper.this.mActivePointerId = -1;
        return;
        j = paramAnonymousMotionEvent.getActionIndex();
      } while (paramAnonymousMotionEvent.getPointerId(j) != ItemTouchHelper.this.mActivePointerId);
      if (ItemTouchHelper.-get5(ItemTouchHelper.this) != null) {
        ItemTouchHelper.-get5(ItemTouchHelper.this).computeCurrentVelocity(1000, ItemTouchHelper.-get3(ItemTouchHelper.this).getMaxFlingVelocity());
      }
      if (j == 0) {}
      for (i = 1;; i = 0)
      {
        ItemTouchHelper.this.mActivePointerId = paramAnonymousMotionEvent.getPointerId(i);
        ItemTouchHelper.-wrap11(ItemTouchHelper.this, paramAnonymousMotionEvent, ItemTouchHelper.this.mSelectedFlags, j);
        return;
      }
    }
  };
  private View mOverdrawChild = null;
  private int mOverdrawChildPosition = -1;
  final List<View> mPendingCleanup = new ArrayList();
  List<RecoverAnimation> mRecoverAnimations = new ArrayList();
  private RecyclerView mRecyclerView;
  private final Runnable mScrollRunnable = new Runnable()
  {
    public void run()
    {
      if ((ItemTouchHelper.this.mSelected != null) && (ItemTouchHelper.-wrap3(ItemTouchHelper.this)))
      {
        if (ItemTouchHelper.this.mSelected != null) {
          ItemTouchHelper.-wrap6(ItemTouchHelper.this, ItemTouchHelper.this.mSelected);
        }
        ItemTouchHelper.-get3(ItemTouchHelper.this).removeCallbacks(ItemTouchHelper.-get4(ItemTouchHelper.this));
        ItemTouchHelper.-get3(ItemTouchHelper.this).postOnAnimation(this);
      }
    }
  };
  RecyclerView.ViewHolder mSelected = null;
  int mSelectedFlags;
  float mSelectedStartX;
  float mSelectedStartY;
  private int mSlop;
  private List<RecyclerView.ViewHolder> mSwapTargets;
  private final float[] mTmpPosition = new float[2];
  private Rect mTmpRect;
  private VelocityTracker mVelocityTracker;
  
  public ItemTouchHelper(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  private void addChildDrawingOrderCallback()
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return;
    }
    if (this.mChildDrawingOrderCallback == null) {
      this.mChildDrawingOrderCallback = new RecyclerView.ChildDrawingOrderCallback()
      {
        public int onGetChildDrawingOrder(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          if (ItemTouchHelper.-get1(ItemTouchHelper.this) == null) {
            return paramAnonymousInt2;
          }
          int j = ItemTouchHelper.-get2(ItemTouchHelper.this);
          int i = j;
          if (j == -1)
          {
            i = ItemTouchHelper.-get3(ItemTouchHelper.this).indexOfChild(ItemTouchHelper.-get1(ItemTouchHelper.this));
            ItemTouchHelper.-set0(ItemTouchHelper.this, i);
          }
          if (paramAnonymousInt2 == paramAnonymousInt1 - 1) {
            return i;
          }
          if (paramAnonymousInt2 < i) {
            return paramAnonymousInt2;
          }
          return paramAnonymousInt2 + 1;
        }
      };
    }
    this.mRecyclerView.setChildDrawingOrderCallback(this.mChildDrawingOrderCallback);
  }
  
  private int checkHorizontalSwipe(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    if ((paramInt & 0xC) != 0)
    {
      int i;
      if (this.mDx > 0.0F)
      {
        i = 8;
        if ((this.mVelocityTracker == null) || (this.mActivePointerId <= -1)) {
          break label102;
        }
        f1 = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
        if (f1 <= 0.0F) {
          break label96;
        }
      }
      label96:
      for (int j = 8;; j = 4)
      {
        if (((j & paramInt) == 0) || (i != j) || (Math.abs(f1) < this.mRecyclerView.getMinFlingVelocity())) {
          break label102;
        }
        return j;
        i = 4;
        break;
      }
      label102:
      float f1 = this.mRecyclerView.getWidth();
      float f2 = this.mCallback.getSwipeThreshold(paramViewHolder);
      if (((paramInt & i) != 0) && (Math.abs(this.mDx) > f1 * f2)) {
        return i;
      }
    }
    return 0;
  }
  
  private boolean checkSelectForSwipe(int paramInt1, MotionEvent paramMotionEvent, int paramInt2)
  {
    if ((this.mSelected != null) || (paramInt1 != 2)) {}
    while ((this.mActionState == 2) || (!this.mCallback.isItemViewSwipeEnabled())) {
      return false;
    }
    if (this.mRecyclerView.getScrollState() == 1) {
      return false;
    }
    RecyclerView.ViewHolder localViewHolder = findSwipedView(paramMotionEvent);
    if (localViewHolder == null) {
      return false;
    }
    paramInt1 = (0xFF00 & this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, localViewHolder)) >> 8;
    if (paramInt1 == 0) {
      return false;
    }
    float f1 = paramMotionEvent.getX(paramInt2);
    float f2 = paramMotionEvent.getY(paramInt2);
    f1 -= this.mInitialTouchX;
    f2 -= this.mInitialTouchY;
    float f3 = Math.abs(f1);
    float f4 = Math.abs(f2);
    if ((f3 < this.mSlop) && (f4 < this.mSlop)) {
      return false;
    }
    if (f3 > f4)
    {
      if ((f1 < 0.0F) && ((paramInt1 & 0x4) == 0)) {
        return false;
      }
      if ((f1 > 0.0F) && ((paramInt1 & 0x8) == 0)) {
        return false;
      }
    }
    else
    {
      if ((f2 < 0.0F) && ((paramInt1 & 0x1) == 0)) {
        return false;
      }
      if ((f2 > 0.0F) && ((paramInt1 & 0x2) == 0)) {
        return false;
      }
    }
    this.mDy = 0.0F;
    this.mDx = 0.0F;
    this.mActivePointerId = paramMotionEvent.getPointerId(0);
    select(localViewHolder, 1);
    return true;
  }
  
  private int checkVerticalSwipe(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    if ((paramInt & 0x3) != 0)
    {
      int i;
      if (this.mDy > 0.0F)
      {
        i = 2;
        if ((this.mVelocityTracker == null) || (this.mActivePointerId <= -1)) {
          break label99;
        }
        f1 = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
        if (f1 <= 0.0F) {
          break label93;
        }
      }
      label93:
      for (int j = 2;; j = 1)
      {
        if (((j & paramInt) == 0) || (j != i) || (Math.abs(f1) < this.mRecyclerView.getMinFlingVelocity())) {
          break label99;
        }
        return j;
        i = 1;
        break;
      }
      label99:
      float f1 = this.mRecyclerView.getHeight();
      float f2 = this.mCallback.getSwipeThreshold(paramViewHolder);
      if (((paramInt & i) != 0) && (Math.abs(this.mDy) > f1 * f2)) {
        return i;
      }
    }
    return 0;
  }
  
  private void destroyCallbacks()
  {
    this.mRecyclerView.removeItemDecoration(this);
    this.mRecyclerView.removeOnItemTouchListener(this.mOnItemTouchListener);
    this.mRecyclerView.removeOnChildAttachStateChangeListener(this);
    int i = this.mRecoverAnimations.size() - 1;
    while (i >= 0)
    {
      RecoverAnimation localRecoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(0);
      this.mCallback.clearView(this.mRecyclerView, localRecoverAnimation.mViewHolder);
      i -= 1;
    }
    this.mRecoverAnimations.clear();
    this.mOverdrawChild = null;
    this.mOverdrawChildPosition = -1;
    releaseVelocityTracker();
  }
  
  private int endRecoverAnimation(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    int i = this.mRecoverAnimations.size() - 1;
    while (i >= 0)
    {
      RecoverAnimation localRecoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(i);
      if (localRecoverAnimation.mViewHolder == paramViewHolder)
      {
        localRecoverAnimation.mOverridden |= paramBoolean;
        if (!RecoverAnimation.-get1(localRecoverAnimation)) {
          localRecoverAnimation.cancel();
        }
        this.mRecoverAnimations.remove(i);
        localRecoverAnimation.mViewHolder.setIsRecyclable(true);
        return RecoverAnimation.-get0(localRecoverAnimation);
      }
      i -= 1;
    }
    return 0;
  }
  
  private RecoverAnimation findAnimation(MotionEvent paramMotionEvent)
  {
    if (this.mRecoverAnimations.isEmpty()) {
      return null;
    }
    paramMotionEvent = findChildView(paramMotionEvent);
    int i = this.mRecoverAnimations.size() - 1;
    while (i >= 0)
    {
      RecoverAnimation localRecoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(i);
      if (localRecoverAnimation.mViewHolder.itemView == paramMotionEvent) {
        return localRecoverAnimation;
      }
      i -= 1;
    }
    return null;
  }
  
  private View findChildView(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    if (this.mSelected != null)
    {
      paramMotionEvent = this.mSelected.itemView;
      if (hitTest(paramMotionEvent, f1, f2, this.mSelectedStartX + this.mDx, this.mSelectedStartY + this.mDy)) {
        return paramMotionEvent;
      }
    }
    int i = this.mRecoverAnimations.size() - 1;
    while (i >= 0)
    {
      paramMotionEvent = (RecoverAnimation)this.mRecoverAnimations.get(i);
      View localView = paramMotionEvent.mViewHolder.itemView;
      if (hitTest(localView, f1, f2, paramMotionEvent.mX, paramMotionEvent.mY)) {
        return localView;
      }
      i -= 1;
    }
    return this.mRecyclerView.findChildViewUnder(f1, f2);
  }
  
  private List<RecyclerView.ViewHolder> findSwapTargets(RecyclerView.ViewHolder paramViewHolder)
  {
    int i;
    int m;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    label137:
    View localView;
    if (this.mSwapTargets == null)
    {
      this.mSwapTargets = new ArrayList();
      this.mDistances = new ArrayList();
      i = this.mCallback.getBoundingBoxMargin();
      m = Math.round(this.mSelectedStartX + this.mDx) - i;
      n = Math.round(this.mSelectedStartY + this.mDy) - i;
      i1 = paramViewHolder.itemView.getWidth() + m + i * 2;
      i2 = paramViewHolder.itemView.getHeight() + n + i * 2;
      i3 = (m + i1) / 2;
      i4 = (n + i2) / 2;
      RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
      int i5 = localLayoutManager.getChildCount();
      i = 0;
      if (i >= i5) {
        break label399;
      }
      localView = localLayoutManager.getChildAt(i);
      if (localView != paramViewHolder.itemView) {
        break label188;
      }
    }
    for (;;)
    {
      i += 1;
      break label137;
      this.mSwapTargets.clear();
      this.mDistances.clear();
      break;
      label188:
      if ((localView.getBottom() >= n) && (localView.getTop() <= i2) && (localView.getRight() >= m) && (localView.getLeft() <= i1))
      {
        RecyclerView.ViewHolder localViewHolder = this.mRecyclerView.getChildViewHolder(localView);
        if (this.mCallback.canDropOver(this.mRecyclerView, this.mSelected, localViewHolder))
        {
          int j = Math.abs(i3 - (localView.getLeft() + localView.getRight()) / 2);
          int k = Math.abs(i4 - (localView.getTop() + localView.getBottom()) / 2);
          int i6 = j * j + k * k;
          k = 0;
          int i7 = this.mSwapTargets.size();
          j = 0;
          while ((j < i7) && (i6 > ((Integer)this.mDistances.get(j)).intValue()))
          {
            k += 1;
            j += 1;
          }
          this.mSwapTargets.add(k, localViewHolder);
          this.mDistances.add(k, Integer.valueOf(i6));
        }
      }
    }
    label399:
    return this.mSwapTargets;
  }
  
  private RecyclerView.ViewHolder findSwipedView(MotionEvent paramMotionEvent)
  {
    RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
    if (this.mActivePointerId == -1) {
      return null;
    }
    int i = paramMotionEvent.findPointerIndex(this.mActivePointerId);
    float f3 = paramMotionEvent.getX(i);
    float f4 = this.mInitialTouchX;
    float f1 = paramMotionEvent.getY(i);
    float f2 = this.mInitialTouchY;
    f3 = Math.abs(f3 - f4);
    f1 = Math.abs(f1 - f2);
    if ((f3 < this.mSlop) && (f1 < this.mSlop)) {
      return null;
    }
    if ((f3 > f1) && (localLayoutManager.canScrollHorizontally())) {
      return null;
    }
    if ((f1 > f3) && (localLayoutManager.canScrollVertically())) {
      return null;
    }
    paramMotionEvent = findChildView(paramMotionEvent);
    if (paramMotionEvent == null) {
      return null;
    }
    return this.mRecyclerView.getChildViewHolder(paramMotionEvent);
  }
  
  private void getSelectedDxDy(float[] paramArrayOfFloat)
  {
    if ((this.mSelectedFlags & 0xC) != 0) {
      paramArrayOfFloat[0] = (this.mSelectedStartX + this.mDx - this.mSelected.itemView.getLeft());
    }
    while ((this.mSelectedFlags & 0x3) != 0)
    {
      paramArrayOfFloat[1] = (this.mSelectedStartY + this.mDy - this.mSelected.itemView.getTop());
      return;
      paramArrayOfFloat[0] = this.mSelected.itemView.getTranslationX();
    }
    paramArrayOfFloat[1] = this.mSelected.itemView.getTranslationY();
  }
  
  private boolean hasRunningRecoverAnim()
  {
    int j = this.mRecoverAnimations.size();
    int i = 0;
    while (i < j)
    {
      if (!RecoverAnimation.-get1((RecoverAnimation)this.mRecoverAnimations.get(i))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private static boolean hitTest(View paramView, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramFloat1 >= paramFloat3)
    {
      bool1 = bool2;
      if (paramFloat1 <= paramView.getWidth() + paramFloat3)
      {
        bool1 = bool2;
        if (paramFloat2 >= paramFloat4)
        {
          bool1 = bool2;
          if (paramFloat2 <= paramView.getHeight() + paramFloat4) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  private void initGestureDetector()
  {
    if (this.mGestureDetector != null) {
      return;
    }
    this.mGestureDetector = new GestureDetector(this.mRecyclerView.getContext(), new ItemTouchHelperGestureListener(null));
  }
  
  private void moveIfNecessary(RecyclerView.ViewHolder paramViewHolder)
  {
    if (this.mRecyclerView.isLayoutRequested()) {
      return;
    }
    if (this.mActionState != 2) {
      return;
    }
    float f = this.mCallback.getMoveThreshold(paramViewHolder);
    int i = (int)(this.mSelectedStartX + this.mDx);
    int j = (int)(this.mSelectedStartY + this.mDy);
    if ((Math.abs(j - paramViewHolder.itemView.getTop()) < paramViewHolder.itemView.getHeight() * f) && (Math.abs(i - paramViewHolder.itemView.getLeft()) < paramViewHolder.itemView.getWidth() * f)) {
      return;
    }
    Object localObject = findSwapTargets(paramViewHolder);
    if (((List)localObject).size() == 0) {
      return;
    }
    localObject = this.mCallback.chooseDropTarget(paramViewHolder, (List)localObject, i, j);
    if (localObject == null)
    {
      this.mSwapTargets.clear();
      this.mDistances.clear();
      return;
    }
    int k = ((RecyclerView.ViewHolder)localObject).getAdapterPosition();
    int m = paramViewHolder.getAdapterPosition();
    if (this.mCallback.onMove(this.mRecyclerView, paramViewHolder, (RecyclerView.ViewHolder)localObject)) {
      this.mCallback.onMoved(this.mRecyclerView, paramViewHolder, m, (RecyclerView.ViewHolder)localObject, k, i, j);
    }
  }
  
  private void obtainVelocityTracker()
  {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.recycle();
    }
    this.mVelocityTracker = VelocityTracker.obtain();
  }
  
  private void postDispatchSwipe(final RecoverAnimation paramRecoverAnimation, final int paramInt)
  {
    this.mRecyclerView.post(new Runnable()
    {
      public void run()
      {
        if ((ItemTouchHelper.-get3(ItemTouchHelper.this) == null) || (!ItemTouchHelper.-get3(ItemTouchHelper.this).isAttachedToWindow()) || (paramRecoverAnimation.mOverridden)) {}
        while (paramRecoverAnimation.mViewHolder.getAdapterPosition() == -1) {
          return;
        }
        RecyclerView.ItemAnimator localItemAnimator = ItemTouchHelper.-get3(ItemTouchHelper.this).getItemAnimator();
        if ((localItemAnimator != null) && (localItemAnimator.isRunning(null))) {}
        while (ItemTouchHelper.-wrap2(ItemTouchHelper.this))
        {
          ItemTouchHelper.-get3(ItemTouchHelper.this).post(this);
          return;
        }
        ItemTouchHelper.this.mCallback.onSwiped(paramRecoverAnimation.mViewHolder, paramInt);
      }
    });
  }
  
  private void releaseVelocityTracker()
  {
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
  
  private void removeChildDrawingOrderCallbackIfNecessary(View paramView)
  {
    if (paramView == this.mOverdrawChild)
    {
      this.mOverdrawChild = null;
      if (this.mChildDrawingOrderCallback != null) {
        this.mRecyclerView.setChildDrawingOrderCallback(null);
      }
    }
  }
  
  private boolean scrollIfNecessary()
  {
    if (this.mSelected == null)
    {
      this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
      return false;
    }
    long l2 = System.currentTimeMillis();
    long l1;
    int j;
    int k;
    int i;
    int m;
    if (this.mDragScrollStartTimeInMs == Long.MIN_VALUE)
    {
      l1 = 0L;
      RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
      if (this.mTmpRect == null) {
        this.mTmpRect = new Rect();
      }
      j = 0;
      k = 0;
      localLayoutManager.calculateItemDecorationsForChild(this.mSelected.itemView, this.mTmpRect);
      i = j;
      if (localLayoutManager.canScrollHorizontally())
      {
        m = (int)(this.mSelectedStartX + this.mDx);
        i = m - this.mTmpRect.left - this.mRecyclerView.getPaddingLeft();
        if ((this.mDx >= 0.0F) || (i >= 0)) {
          break label314;
        }
      }
      label136:
      j = k;
      if (localLayoutManager.canScrollVertically())
      {
        m = (int)(this.mSelectedStartY + this.mDy);
        j = m - this.mTmpRect.top - this.mRecyclerView.getPaddingTop();
        if ((this.mDy >= 0.0F) || (j >= 0)) {
          break label377;
        }
      }
    }
    for (;;)
    {
      k = i;
      if (i != 0) {
        k = this.mCallback.interpolateOutOfBoundsScroll(this.mRecyclerView, this.mSelected.itemView.getWidth(), i, this.mRecyclerView.getWidth(), l1);
      }
      i = j;
      if (j != 0) {
        i = this.mCallback.interpolateOutOfBoundsScroll(this.mRecyclerView, this.mSelected.itemView.getHeight(), j, this.mRecyclerView.getHeight(), l1);
      }
      if ((k == 0) && (i == 0)) {
        break label440;
      }
      if (this.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
        this.mDragScrollStartTimeInMs = l2;
      }
      this.mRecyclerView.scrollBy(k, i);
      return true;
      l1 = l2 - this.mDragScrollStartTimeInMs;
      break;
      label314:
      i = j;
      if (this.mDx <= 0.0F) {
        break label136;
      }
      m = this.mSelected.itemView.getWidth() + m + this.mTmpRect.right - (this.mRecyclerView.getWidth() - this.mRecyclerView.getPaddingRight());
      i = j;
      if (m <= 0) {
        break label136;
      }
      i = m;
      break label136;
      label377:
      j = k;
      if (this.mDy > 0.0F)
      {
        m = this.mSelected.itemView.getHeight() + m + this.mTmpRect.bottom - (this.mRecyclerView.getHeight() - this.mRecyclerView.getPaddingBottom());
        j = k;
        if (m > 0) {
          j = m;
        }
      }
    }
    label440:
    this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
    return false;
  }
  
  private void select(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    if ((paramViewHolder == this.mSelected) && (paramInt == this.mActionState)) {
      return;
    }
    this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
    int k = this.mActionState;
    endRecoverAnimation(paramViewHolder, true);
    this.mActionState = paramInt;
    if (paramInt == 2)
    {
      this.mOverdrawChild = paramViewHolder.itemView;
      addChildDrawingOrderCallback();
    }
    int i = 0;
    final int j = 0;
    final Object localObject;
    float f1;
    float f2;
    if (this.mSelected != null)
    {
      localObject = this.mSelected;
      if (((RecyclerView.ViewHolder)localObject).itemView.getParent() == null) {
        break label511;
      }
      if (k == 2)
      {
        j = 0;
        releaseVelocityTracker();
      }
    }
    else
    {
      switch (j)
      {
      default: 
        f1 = 0.0F;
        f2 = 0.0F;
        label169:
        if (k == 2)
        {
          i = 8;
          label179:
          getSelectedDxDy(this.mTmpPosition);
          float f3 = this.mTmpPosition[0];
          float f4 = this.mTmpPosition[1];
          localObject = new RecoverAnimation(this, (RecyclerView.ViewHolder)localObject, i, k, f3, f4, f1, f2)
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              super.onAnimationEnd(paramAnonymousAnimator);
              if (this.mOverridden) {
                return;
              }
              if (j <= 0) {
                jdField_this.mCallback.clearView(ItemTouchHelper.-get3(jdField_this), localObject);
              }
              for (;;)
              {
                if (ItemTouchHelper.-get1(jdField_this) == localObject.itemView) {
                  ItemTouchHelper.-wrap9(jdField_this, localObject.itemView);
                }
                return;
                jdField_this.mPendingCleanup.add(localObject.itemView);
                this.mIsPendingCleanup = true;
                if (j > 0) {
                  ItemTouchHelper.-wrap8(jdField_this, this, j);
                }
              }
            }
          };
          ((RecoverAnimation)localObject).setDuration(this.mCallback.getAnimationDuration(this.mRecyclerView, i, f1 - f3, f2 - f4));
          this.mRecoverAnimations.add(localObject);
          ((RecoverAnimation)localObject).start();
          i = 1;
          label278:
          this.mSelected = null;
          if (paramViewHolder != null)
          {
            this.mSelectedFlags = ((this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, paramViewHolder) & (1 << paramInt * 8 + 8) - 1) >> this.mActionState * 8);
            this.mSelectedStartX = paramViewHolder.itemView.getLeft();
            this.mSelectedStartY = paramViewHolder.itemView.getTop();
            this.mSelected = paramViewHolder;
            if (paramInt == 2) {
              this.mSelected.itemView.performHapticFeedback(0);
            }
          }
          paramViewHolder = this.mRecyclerView.getParent();
          if (paramViewHolder != null) {
            if (this.mSelected == null) {
              break label540;
            }
          }
        }
        break;
      }
    }
    label511:
    label540:
    for (boolean bool = true;; bool = false)
    {
      paramViewHolder.requestDisallowInterceptTouchEvent(bool);
      if (i == 0) {
        this.mRecyclerView.getLayoutManager().requestSimpleAnimationsInNextLayout();
      }
      this.mCallback.onSelectedChanged(this.mSelected, this.mActionState);
      this.mRecyclerView.invalidate();
      return;
      j = swipeIfNecessary((RecyclerView.ViewHolder)localObject);
      break;
      f2 = 0.0F;
      f1 = Math.signum(this.mDx) * this.mRecyclerView.getWidth();
      break label169;
      f1 = 0.0F;
      f2 = Math.signum(this.mDy) * this.mRecyclerView.getHeight();
      break label169;
      if (j > 0)
      {
        i = 2;
        break label179;
      }
      i = 4;
      break label179;
      removeChildDrawingOrderCallbackIfNecessary(((RecyclerView.ViewHolder)localObject).itemView);
      this.mCallback.clearView(this.mRecyclerView, (RecyclerView.ViewHolder)localObject);
      i = j;
      break label278;
    }
  }
  
  private void setupCallbacks()
  {
    this.mSlop = ViewConfiguration.get(this.mRecyclerView.getContext()).getScaledTouchSlop();
    this.mRecyclerView.addItemDecoration(this);
    this.mRecyclerView.addOnItemTouchListener(this.mOnItemTouchListener);
    this.mRecyclerView.addOnChildAttachStateChangeListener(this);
    initGestureDetector();
  }
  
  private int swipeIfNecessary(RecyclerView.ViewHolder paramViewHolder)
  {
    if (this.mActionState == 2) {
      return 0;
    }
    int j = this.mCallback.getMovementFlags(this.mRecyclerView, paramViewHolder);
    int i = (this.mCallback.convertToAbsoluteDirection(j, this.mRecyclerView.getLayoutDirection()) & 0xFF00) >> 8;
    if (i == 0) {
      return 0;
    }
    j = (j & 0xFF00) >> 8;
    int k;
    if (Math.abs(this.mDx) > Math.abs(this.mDy))
    {
      k = checkHorizontalSwipe(paramViewHolder, i);
      if (k > 0)
      {
        if ((j & k) == 0) {
          return Callback.convertToRelativeDirection(k, this.mRecyclerView.getLayoutDirection());
        }
        return k;
      }
      i = checkVerticalSwipe(paramViewHolder, i);
      if (i > 0) {
        return i;
      }
    }
    else
    {
      k = checkVerticalSwipe(paramViewHolder, i);
      if (k > 0) {
        return k;
      }
      i = checkHorizontalSwipe(paramViewHolder, i);
      if (i > 0)
      {
        if ((j & i) == 0) {
          return Callback.convertToRelativeDirection(i, this.mRecyclerView.getLayoutDirection());
        }
        return i;
      }
    }
    return 0;
  }
  
  private void updateDxDy(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
  {
    float f1 = paramMotionEvent.getX(paramInt2);
    float f2 = paramMotionEvent.getY(paramInt2);
    this.mDx = (f1 - this.mInitialTouchX);
    this.mDy = (f2 - this.mInitialTouchY);
    if ((paramInt1 & 0x4) == 0) {
      this.mDx = Math.max(0.0F, this.mDx);
    }
    if ((paramInt1 & 0x8) == 0) {
      this.mDx = Math.min(0.0F, this.mDx);
    }
    if ((paramInt1 & 0x1) == 0) {
      this.mDy = Math.max(0.0F, this.mDy);
    }
    if ((paramInt1 & 0x2) == 0) {
      this.mDy = Math.min(0.0F, this.mDy);
    }
  }
  
  public void attachToRecyclerView(RecyclerView paramRecyclerView)
  {
    if (this.mRecyclerView == paramRecyclerView) {
      return;
    }
    if (this.mRecyclerView != null) {
      destroyCallbacks();
    }
    this.mRecyclerView = paramRecyclerView;
    if (this.mRecyclerView != null) {
      setupCallbacks();
    }
  }
  
  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    paramRect.setEmpty();
  }
  
  public void onChildViewAttachedToWindow(View paramView) {}
  
  public void onChildViewDetachedFromWindow(View paramView)
  {
    removeChildDrawingOrderCallbackIfNecessary(paramView);
    paramView = this.mRecyclerView.getChildViewHolder(paramView);
    if (paramView == null) {
      return;
    }
    if ((this.mSelected != null) && (paramView == this.mSelected)) {
      select(null, 0);
    }
    do
    {
      return;
      endRecoverAnimation(paramView, false);
    } while (!this.mPendingCleanup.remove(paramView.itemView));
    this.mCallback.clearView(this.mRecyclerView, paramView);
  }
  
  public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    this.mOverdrawChildPosition = -1;
    float f1 = 0.0F;
    float f2 = 0.0F;
    if (this.mSelected != null)
    {
      getSelectedDxDy(this.mTmpPosition);
      f1 = this.mTmpPosition[0];
      f2 = this.mTmpPosition[1];
    }
    Callback.-wrap3(this.mCallback, paramCanvas, paramRecyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f1, f2);
  }
  
  public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    float f1 = 0.0F;
    float f2 = 0.0F;
    if (this.mSelected != null)
    {
      getSelectedDxDy(this.mTmpPosition);
      f1 = this.mTmpPosition[0];
      f2 = this.mTmpPosition[1];
    }
    Callback.-wrap2(this.mCallback, paramCanvas, paramRecyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f1, f2);
  }
  
  public void startDrag(RecyclerView.ViewHolder paramViewHolder)
  {
    if (!Callback.-wrap0(this.mCallback, this.mRecyclerView, paramViewHolder))
    {
      Log.e("ItemTouchHelper", "Start drag has been called but swiping is not enabled");
      return;
    }
    if (paramViewHolder.itemView.getParent() != this.mRecyclerView)
    {
      Log.e("ItemTouchHelper", "Start drag has been called with a view holder which is not a child of the RecyclerView which is controlled by this ItemTouchHelper.");
      return;
    }
    obtainVelocityTracker();
    this.mDy = 0.0F;
    this.mDx = 0.0F;
    select(paramViewHolder, 2);
  }
  
  public void startSwipe(RecyclerView.ViewHolder paramViewHolder)
  {
    if (!Callback.-wrap1(this.mCallback, this.mRecyclerView, paramViewHolder))
    {
      Log.e("ItemTouchHelper", "Start swipe has been called but dragging is not enabled");
      return;
    }
    if (paramViewHolder.itemView.getParent() != this.mRecyclerView)
    {
      Log.e("ItemTouchHelper", "Start swipe has been called with a view holder which is not a child of the RecyclerView controlled by this ItemTouchHelper.");
      return;
    }
    obtainVelocityTracker();
    this.mDy = 0.0F;
    this.mDx = 0.0F;
    select(paramViewHolder, 1);
  }
  
  public static abstract class Callback
  {
    private static final int ABS_HORIZONTAL_DIR_FLAGS = 789516;
    public static final int DEFAULT_DRAG_ANIMATION_DURATION = 200;
    public static final int DEFAULT_SWIPE_ANIMATION_DURATION = 250;
    private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 2000L;
    static final int RELATIVE_DIR_FLAGS = 3158064;
    private static final Interpolator sDragScrollInterpolator = new Interpolator()
    {
      public float getInterpolation(float paramAnonymousFloat)
      {
        return paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat;
      }
    };
    private static final Interpolator sDragViewScrollCapInterpolator = new Interpolator()
    {
      public float getInterpolation(float paramAnonymousFloat)
      {
        paramAnonymousFloat -= 1.0F;
        return paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat + 1.0F;
      }
    };
    private static final ItemTouchUIUtil sUICallback = new ItemTouchUIUtilImplLollipop();
    private int mCachedMaxScrollSpeed = -1;
    
    public static int convertToRelativeDirection(int paramInt1, int paramInt2)
    {
      int i = paramInt1 & 0xC0C0C;
      if (i == 0) {
        return paramInt1;
      }
      paramInt1 &= i;
      if (paramInt2 == 0) {
        return paramInt1 | i << 2;
      }
      return paramInt1 | i << 1 & 0xFFF3F3F3 | (i << 1 & 0xC0C0C) << 2;
    }
    
    public static ItemTouchUIUtil getDefaultUIUtil()
    {
      return sUICallback;
    }
    
    private int getMaxDragScroll(RecyclerView paramRecyclerView)
    {
      if (this.mCachedMaxScrollSpeed == -1) {
        this.mCachedMaxScrollSpeed = paramRecyclerView.getResources().getDimensionPixelSize(R.dimen.item_touch_helper_max_drag_scroll_per_frame);
      }
      return this.mCachedMaxScrollSpeed;
    }
    
    private boolean hasDragFlag(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool = false;
      if ((0xFF0000 & getAbsoluteMovementFlags(paramRecyclerView, paramViewHolder)) != 0) {
        bool = true;
      }
      return bool;
    }
    
    private boolean hasSwipeFlag(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool = false;
      if ((0xFF00 & getAbsoluteMovementFlags(paramRecyclerView, paramViewHolder)) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public static int makeFlag(int paramInt1, int paramInt2)
    {
      return paramInt2 << paramInt1 * 8;
    }
    
    public static int makeMovementFlags(int paramInt1, int paramInt2)
    {
      return makeFlag(0, paramInt2 | paramInt1) | makeFlag(1, paramInt2) | makeFlag(2, paramInt1);
    }
    
    private void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, List<ItemTouchHelper.RecoverAnimation> paramList, int paramInt, float paramFloat1, float paramFloat2)
    {
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        ItemTouchHelper.RecoverAnimation localRecoverAnimation = (ItemTouchHelper.RecoverAnimation)paramList.get(i);
        localRecoverAnimation.update();
        int k = paramCanvas.save();
        onChildDraw(paramCanvas, paramRecyclerView, localRecoverAnimation.mViewHolder, localRecoverAnimation.mX, localRecoverAnimation.mY, localRecoverAnimation.mActionState, false);
        paramCanvas.restoreToCount(k);
        i += 1;
      }
      if (paramViewHolder != null)
      {
        i = paramCanvas.save();
        onChildDraw(paramCanvas, paramRecyclerView, paramViewHolder, paramFloat1, paramFloat2, paramInt, true);
        paramCanvas.restoreToCount(i);
      }
    }
    
    private void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, List<ItemTouchHelper.RecoverAnimation> paramList, int paramInt, float paramFloat1, float paramFloat2)
    {
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        ItemTouchHelper.RecoverAnimation localRecoverAnimation = (ItemTouchHelper.RecoverAnimation)paramList.get(i);
        int k = paramCanvas.save();
        onChildDrawOver(paramCanvas, paramRecyclerView, localRecoverAnimation.mViewHolder, localRecoverAnimation.mX, localRecoverAnimation.mY, localRecoverAnimation.mActionState, false);
        paramCanvas.restoreToCount(k);
        i += 1;
      }
      if (paramViewHolder != null)
      {
        i = paramCanvas.save();
        onChildDrawOver(paramCanvas, paramRecyclerView, paramViewHolder, paramFloat1, paramFloat2, paramInt, true);
        paramCanvas.restoreToCount(i);
      }
      i = 0;
      paramInt = j - 1;
      if (paramInt >= 0)
      {
        paramCanvas = (ItemTouchHelper.RecoverAnimation)paramList.get(paramInt);
        if ((!ItemTouchHelper.RecoverAnimation.-get1(paramCanvas)) || (paramCanvas.mIsPendingCleanup)) {
          if (!ItemTouchHelper.RecoverAnimation.-get1(paramCanvas)) {
            i = 1;
          }
        }
        for (;;)
        {
          paramInt -= 1;
          break;
          paramList.remove(paramInt);
          paramCanvas.mViewHolder.setIsRecyclable(true);
        }
      }
      if (i != 0) {
        paramRecyclerView.invalidate();
      }
    }
    
    public boolean canDropOver(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2)
    {
      return true;
    }
    
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder paramViewHolder, List<RecyclerView.ViewHolder> paramList, int paramInt1, int paramInt2)
    {
      int n = paramViewHolder.itemView.getWidth();
      int i1 = paramViewHolder.itemView.getHeight();
      Object localObject2 = null;
      int j = -1;
      int i2 = paramInt1 - paramViewHolder.itemView.getLeft();
      int i3 = paramInt2 - paramViewHolder.itemView.getTop();
      int i4 = paramList.size();
      int k = 0;
      while (k < i4)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)paramList.get(k);
        Object localObject1 = localObject2;
        int i = j;
        int m;
        if (i2 > 0)
        {
          m = localViewHolder.itemView.getRight() - (paramInt1 + n);
          localObject1 = localObject2;
          i = j;
          if (m < 0)
          {
            localObject1 = localObject2;
            i = j;
            if (localViewHolder.itemView.getRight() > paramViewHolder.itemView.getRight())
            {
              m = Math.abs(m);
              localObject1 = localObject2;
              i = j;
              if (m > j)
              {
                i = m;
                localObject1 = localViewHolder;
              }
            }
          }
        }
        localObject2 = localObject1;
        j = i;
        if (i2 < 0)
        {
          m = localViewHolder.itemView.getLeft() - paramInt1;
          localObject2 = localObject1;
          j = i;
          if (m > 0)
          {
            localObject2 = localObject1;
            j = i;
            if (localViewHolder.itemView.getLeft() < paramViewHolder.itemView.getLeft())
            {
              m = Math.abs(m);
              localObject2 = localObject1;
              j = i;
              if (m > i)
              {
                j = m;
                localObject2 = localViewHolder;
              }
            }
          }
        }
        localObject1 = localObject2;
        i = j;
        if (i3 < 0)
        {
          m = localViewHolder.itemView.getTop() - paramInt2;
          localObject1 = localObject2;
          i = j;
          if (m > 0)
          {
            localObject1 = localObject2;
            i = j;
            if (localViewHolder.itemView.getTop() < paramViewHolder.itemView.getTop())
            {
              m = Math.abs(m);
              localObject1 = localObject2;
              i = j;
              if (m > j)
              {
                i = m;
                localObject1 = localViewHolder;
              }
            }
          }
        }
        localObject2 = localObject1;
        j = i;
        if (i3 > 0)
        {
          m = localViewHolder.itemView.getBottom() - (paramInt2 + i1);
          localObject2 = localObject1;
          j = i;
          if (m < 0)
          {
            localObject2 = localObject1;
            j = i;
            if (localViewHolder.itemView.getBottom() > paramViewHolder.itemView.getBottom())
            {
              m = Math.abs(m);
              localObject2 = localObject1;
              j = i;
              if (m > i)
              {
                j = m;
                localObject2 = localViewHolder;
              }
            }
          }
        }
        k += 1;
      }
      return (RecyclerView.ViewHolder)localObject2;
    }
    
    public void clearView(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      sUICallback.clearView(paramViewHolder.itemView);
    }
    
    public int convertToAbsoluteDirection(int paramInt1, int paramInt2)
    {
      int i = paramInt1 & 0x303030;
      if (i == 0) {
        return paramInt1;
      }
      paramInt1 &= i;
      if (paramInt2 == 0) {
        return paramInt1 | i >> 2;
      }
      return paramInt1 | i >> 1 & 0xFFCFCFCF | (i >> 1 & 0x303030) >> 2;
    }
    
    final int getAbsoluteMovementFlags(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return convertToAbsoluteDirection(getMovementFlags(paramRecyclerView, paramViewHolder), paramRecyclerView.getLayoutDirection());
    }
    
    public long getAnimationDuration(RecyclerView paramRecyclerView, int paramInt, float paramFloat1, float paramFloat2)
    {
      paramRecyclerView = paramRecyclerView.getItemAnimator();
      if (paramRecyclerView == null)
      {
        if (paramInt == 8) {}
        for (paramInt = 200;; paramInt = 250) {
          return paramInt;
        }
      }
      if (paramInt == 8) {
        return paramRecyclerView.getMoveDuration();
      }
      return paramRecyclerView.getRemoveDuration();
    }
    
    public int getBoundingBoxMargin()
    {
      return 0;
    }
    
    public float getMoveThreshold(RecyclerView.ViewHolder paramViewHolder)
    {
      return 0.5F;
    }
    
    public abstract int getMovementFlags(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder);
    
    public float getSwipeThreshold(RecyclerView.ViewHolder paramViewHolder)
    {
      return 0.5F;
    }
    
    public int interpolateOutOfBoundsScroll(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3, long paramLong)
    {
      paramInt3 = getMaxDragScroll(paramRecyclerView);
      int i = Math.abs(paramInt2);
      int j = (int)Math.signum(paramInt2);
      float f = Math.min(1.0F, i * 1.0F / paramInt1);
      paramInt1 = (int)(j * paramInt3 * sDragViewScrollCapInterpolator.getInterpolation(f));
      if (paramLong > 2000L) {}
      for (f = 1.0F;; f = (float)paramLong / 2000.0F)
      {
        paramInt1 = (int)(paramInt1 * sDragScrollInterpolator.getInterpolation(f));
        if (paramInt1 != 0) {
          return paramInt1;
        }
        if (paramInt2 <= 0) {
          break;
        }
        return 1;
      }
      return -1;
      return paramInt1;
    }
    
    public boolean isItemViewSwipeEnabled()
    {
      return true;
    }
    
    public boolean isLongPressDragEnabled()
    {
      return true;
    }
    
    public void onChildDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      sUICallback.onDraw(paramCanvas, paramRecyclerView, paramViewHolder.itemView, paramFloat1, paramFloat2, paramInt, paramBoolean);
    }
    
    public void onChildDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      sUICallback.onDrawOver(paramCanvas, paramRecyclerView, paramViewHolder.itemView, paramFloat1, paramFloat2, paramInt, paramBoolean);
    }
    
    public abstract boolean onMove(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2);
    
    public void onMoved(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, int paramInt1, RecyclerView.ViewHolder paramViewHolder2, int paramInt2, int paramInt3, int paramInt4)
    {
      RecyclerView.LayoutManager localLayoutManager = paramRecyclerView.getLayoutManager();
      if ((localLayoutManager instanceof ItemTouchHelper.ViewDropHandler))
      {
        ((ItemTouchHelper.ViewDropHandler)localLayoutManager).prepareForDrop(paramViewHolder1.itemView, paramViewHolder2.itemView, paramInt3, paramInt4);
        return;
      }
      if (localLayoutManager.canScrollHorizontally())
      {
        if (localLayoutManager.getDecoratedLeft(paramViewHolder2.itemView) <= paramRecyclerView.getPaddingLeft()) {
          paramRecyclerView.scrollToPosition(paramInt2);
        }
        if (localLayoutManager.getDecoratedRight(paramViewHolder2.itemView) >= paramRecyclerView.getWidth() - paramRecyclerView.getPaddingRight()) {
          paramRecyclerView.scrollToPosition(paramInt2);
        }
      }
      if (localLayoutManager.canScrollVertically())
      {
        if (localLayoutManager.getDecoratedTop(paramViewHolder2.itemView) <= paramRecyclerView.getPaddingTop()) {
          paramRecyclerView.scrollToPosition(paramInt2);
        }
        if (localLayoutManager.getDecoratedBottom(paramViewHolder2.itemView) >= paramRecyclerView.getHeight() - paramRecyclerView.getPaddingBottom()) {
          paramRecyclerView.scrollToPosition(paramInt2);
        }
      }
    }
    
    public void onSelectedChanged(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (paramViewHolder != null) {
        sUICallback.onSelected(paramViewHolder.itemView);
      }
    }
    
    public abstract void onSwiped(RecyclerView.ViewHolder paramViewHolder, int paramInt);
    
    static class ItemTouchUIUtilImplHoneycomb
      implements ItemTouchUIUtil
    {
      public void clearView(View paramView)
      {
        paramView.setTranslationX(0.0F);
        paramView.setTranslationY(0.0F);
      }
      
      public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
      {
        paramView.setTranslationX(paramFloat1);
        paramView.setTranslationY(paramFloat2);
      }
      
      public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean) {}
      
      public void onSelected(View paramView) {}
    }
    
    static class ItemTouchUIUtilImplLollipop
      extends ItemTouchHelper.Callback.ItemTouchUIUtilImplHoneycomb
    {
      private float findMaxElevation(RecyclerView paramRecyclerView, View paramView)
      {
        int j = paramRecyclerView.getChildCount();
        float f1 = 0.0F;
        int i = 0;
        if (i < j)
        {
          View localView = paramRecyclerView.getChildAt(i);
          float f2;
          if (localView == paramView) {
            f2 = f1;
          }
          for (;;)
          {
            i += 1;
            f1 = f2;
            break;
            float f3 = localView.getElevation();
            f2 = f1;
            if (f3 > f1) {
              f2 = f3;
            }
          }
        }
        return f1;
      }
      
      public void clearView(View paramView)
      {
        Object localObject = paramView.getTag(R.id.item_touch_helper_previous_elevation);
        if ((localObject != null) && ((localObject instanceof Float))) {
          paramView.setElevation(((Float)localObject).floatValue());
        }
        paramView.setTag(R.id.item_touch_helper_previous_elevation, null);
        super.clearView(paramView);
      }
      
      public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
      {
        if ((paramBoolean) && (paramView.getTag(R.id.item_touch_helper_previous_elevation) == null))
        {
          float f = paramView.getElevation();
          paramView.setElevation(1.0F + findMaxElevation(paramRecyclerView, paramView));
          paramView.setTag(R.id.item_touch_helper_previous_elevation, Float.valueOf(f));
        }
        super.onDraw(paramCanvas, paramRecyclerView, paramView, paramFloat1, paramFloat2, paramInt, paramBoolean);
      }
    }
  }
  
  private class ItemTouchHelperGestureListener
    extends GestureDetector.SimpleOnGestureListener
  {
    private ItemTouchHelperGestureListener() {}
    
    public boolean onDown(MotionEvent paramMotionEvent)
    {
      return true;
    }
    
    public void onLongPress(MotionEvent paramMotionEvent)
    {
      Object localObject = ItemTouchHelper.-wrap0(ItemTouchHelper.this, paramMotionEvent);
      if (localObject != null)
      {
        localObject = ItemTouchHelper.-get3(ItemTouchHelper.this).getChildViewHolder((View)localObject);
        if (localObject != null)
        {
          if (!ItemTouchHelper.Callback.-wrap0(ItemTouchHelper.this.mCallback, ItemTouchHelper.-get3(ItemTouchHelper.this), (RecyclerView.ViewHolder)localObject)) {
            return;
          }
          if (paramMotionEvent.getPointerId(0) == ItemTouchHelper.this.mActivePointerId)
          {
            int i = paramMotionEvent.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
            float f1 = paramMotionEvent.getX(i);
            float f2 = paramMotionEvent.getY(i);
            ItemTouchHelper.this.mInitialTouchX = f1;
            ItemTouchHelper.this.mInitialTouchY = f2;
            paramMotionEvent = ItemTouchHelper.this;
            ItemTouchHelper.this.mDy = 0.0F;
            paramMotionEvent.mDx = 0.0F;
            if (ItemTouchHelper.this.mCallback.isLongPressDragEnabled()) {
              ItemTouchHelper.-wrap10(ItemTouchHelper.this, (RecyclerView.ViewHolder)localObject, 2);
            }
          }
        }
      }
    }
  }
  
  private class RecoverAnimation
    implements Animator.AnimatorListener
  {
    final int mActionState;
    private final int mAnimationType;
    private boolean mEnded = false;
    private float mFraction;
    public boolean mIsPendingCleanup;
    boolean mOverridden = false;
    final float mStartDx;
    final float mStartDy;
    final float mTargetX;
    final float mTargetY;
    private final ValueAnimator mValueAnimator;
    final RecyclerView.ViewHolder mViewHolder;
    float mX;
    float mY;
    
    public RecoverAnimation(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      this.mActionState = paramInt2;
      this.mAnimationType = paramInt1;
      this.mViewHolder = paramViewHolder;
      this.mStartDx = paramFloat1;
      this.mStartDy = paramFloat2;
      this.mTargetX = paramFloat3;
      this.mTargetY = paramFloat4;
      this.mValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
      this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          ItemTouchHelper.RecoverAnimation.this.setFraction(paramAnonymousValueAnimator.getAnimatedFraction());
        }
      });
      this.mValueAnimator.setTarget(paramViewHolder.itemView);
      this.mValueAnimator.addListener(this);
      setFraction(0.0F);
    }
    
    public void cancel()
    {
      this.mValueAnimator.cancel();
    }
    
    public void onAnimationCancel(Animator paramAnimator)
    {
      setFraction(1.0F);
    }
    
    public void onAnimationEnd(Animator paramAnimator)
    {
      this.mEnded = true;
    }
    
    public void onAnimationRepeat(Animator paramAnimator) {}
    
    public void onAnimationStart(Animator paramAnimator) {}
    
    public void setDuration(long paramLong)
    {
      this.mValueAnimator.setDuration(paramLong);
    }
    
    public void setFraction(float paramFloat)
    {
      this.mFraction = paramFloat;
    }
    
    public void start()
    {
      this.mViewHolder.setIsRecyclable(false);
      this.mValueAnimator.start();
    }
    
    public void update()
    {
      if (this.mStartDx == this.mTargetX) {}
      for (this.mX = this.mViewHolder.itemView.getTranslationX(); this.mStartDy == this.mTargetY; this.mX = (this.mStartDx + this.mFraction * (this.mTargetX - this.mStartDx)))
      {
        this.mY = this.mViewHolder.itemView.getTranslationY();
        return;
      }
      this.mY = (this.mStartDy + this.mFraction * (this.mTargetY - this.mStartDy));
    }
  }
  
  public static abstract class SimpleCallback
    extends ItemTouchHelper.Callback
  {
    private int mDefaultDragDirs;
    private int mDefaultSwipeDirs;
    
    public SimpleCallback(int paramInt1, int paramInt2)
    {
      this.mDefaultSwipeDirs = paramInt2;
      this.mDefaultDragDirs = paramInt1;
    }
    
    public int getDragDirs(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return this.mDefaultDragDirs;
    }
    
    public int getMovementFlags(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return makeMovementFlags(getDragDirs(paramRecyclerView, paramViewHolder), getSwipeDirs(paramRecyclerView, paramViewHolder));
    }
    
    public int getSwipeDirs(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return this.mDefaultSwipeDirs;
    }
    
    public void setDefaultDragDirs(int paramInt)
    {
      this.mDefaultDragDirs = paramInt;
    }
    
    public void setDefaultSwipeDirs(int paramInt)
    {
      this.mDefaultSwipeDirs = paramInt;
    }
  }
  
  public static abstract interface ViewDropHandler
  {
    public abstract void prepareForDrop(View paramView1, View paramView2, int paramInt1, int paramInt2);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\recyclerview\ItemTouchHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */