package com.oneplus.lib.widget.listview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.dimen;
import com.oneplus.commonctrl.R.styleable;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class OPListView
  extends ListView
{
  static final String TAG = "OPListView";
  private boolean mAnimRunning;
  private ArrayList<ObjectAnimator> mAnimatorList = new ArrayList();
  ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      OPListView.this.invalidate();
    }
  };
  private DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(1.2F);
  AnimatorSet mDelAniSet = null;
  private boolean mDelAnimationFlag;
  private ArrayList<Integer> mDelOriViewTopList = null;
  private ArrayList<Integer> mDelPosList = null;
  private ArrayList<View> mDelViewList = null;
  private DeleteAnimationListener mDeleteAnimationListener;
  private boolean mDisableTouchEvent;
  private Drawable mDivider;
  private IOPDividerController mDividerController = null;
  private int mDividerHeight = 1;
  private boolean mFooterDividersEnabled = true;
  private boolean mHeaderDividersEnabled = true;
  private boolean mInDeleteAnimation;
  private boolean mIsClipToPadding = true;
  private boolean mIsDisableAnimation = true;
  private ArrayList<View> mNowViewList = null;
  private int mOriBelowLeftCount;
  private int mOriCurDeleteCount;
  private int mOriCurLeftCount;
  private int mOriFirstPosition;
  private boolean mOriLastPage;
  private int mOriUpperDeleteCount;
  Rect mTempRect = new Rect();
  
  public OPListView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPListView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842868);
  }
  
  public OPListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public OPListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    init(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private ObjectAnimator getAnimator(int paramInt, View paramView, float paramFloat)
  {
    if (paramInt >= this.mAnimatorList.size())
    {
      paramView = ObjectAnimator.ofPropertyValuesHolder(paramView, new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat("y", new float[] { paramFloat, paramView.getTop() }) });
      this.mAnimatorList.add(paramView);
      return paramView;
    }
    ObjectAnimator localObjectAnimator = (ObjectAnimator)this.mAnimatorList.get(paramInt);
    localObjectAnimator.getValues()[0].setFloatValues(new float[] { paramFloat, paramView.getTop() });
    localObjectAnimator.setTarget(paramView);
    return localObjectAnimator;
  }
  
  private int getDividerType(int paramInt)
  {
    if (this.mDividerController == null) {
      return -1;
    }
    return this.mDividerController.getDividerType(paramInt);
  }
  
  private void init(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OPListView, R.attr.OPListViewStyle, 0);
    paramContext = paramAttributeSet.getDrawable(R.styleable.OPListView_android_divider);
    paramAttributeSet = paramAttributeSet.getDrawable(R.styleable.OPListView_android_background);
    if (paramContext != null) {
      setDivider(paramContext);
    }
    if (paramAttributeSet != null) {
      setBackground(paramAttributeSet);
    }
    this.mDividerHeight = getResources().getDimensionPixelSize(R.dimen.listview_divider_height);
    setOverScrollMode(0);
    super.setDivider(new ColorDrawable(17170445));
    setDividerHeight(this.mDividerHeight);
    setFooterDividersEnabled(false);
  }
  
  private boolean isClipToPadding()
  {
    return this.mIsClipToPadding;
  }
  
  private void setDelViewLocation()
  {
    int i = getFirstVisiblePosition();
    int i3 = getChildCount();
    int j = 0;
    if (getLastVisiblePosition() == getAdapter().getCount() - 1) {
      j = 1;
    }
    int k = 0;
    if (i == 0) {
      k = 1;
    }
    getTop();
    int i2 = getBottom();
    int i1 = getChildCount();
    i = 0;
    if (this.mNowViewList == null) {
      this.mNowViewList = new ArrayList();
    }
    int m;
    Object localObject;
    for (;;)
    {
      m = 0;
      while (m < i1)
      {
        localObject = getChildAt(m);
        this.mNowViewList.add(localObject);
        int n = i;
        if (m == 0)
        {
          n = i;
          if (localObject != null) {
            n = ((View)localObject).getHeight();
          }
        }
        m += 1;
        i = n;
      }
      this.mNowViewList.clear();
    }
    if (!this.mOriLastPage)
    {
      if (j == 0) {
        if (this.mOriUpperDeleteCount == 0) {
          Log.d("OPListView", "DeleteAnimation Case 1");
        }
      }
      for (;;)
      {
        k = this.mNowViewList.size();
        m = this.mDelOriViewTopList.size();
        j = 0;
        while (j < k - m)
        {
          this.mDelOriViewTopList.add(Integer.valueOf((j + 1) * i + i2));
          j += 1;
        }
        if (this.mOriUpperDeleteCount >= this.mOriCurLeftCount)
        {
          Log.d("OPListView", "DeleteAnimation Case 3 ");
          this.mDelOriViewTopList.clear();
        }
        else
        {
          Log.d("OPListView", "DeleteAnimation Case 2 ");
          j = 0;
          while (j < this.mOriUpperDeleteCount)
          {
            this.mDelOriViewTopList.remove(0);
            j += 1;
          }
          continue;
          if (k == 0) {
            if (this.mOriUpperDeleteCount == 0) {
              Log.d("OPListView", "DeleteAnimation Case 4 ");
            }
          }
          for (;;)
          {
            j = 0;
            while (j < this.mOriBelowLeftCount)
            {
              this.mDelOriViewTopList.add(Integer.valueOf((j + 1) * i + i2));
              j += 1;
            }
            if (this.mOriCurDeleteCount == 0)
            {
              if (this.mOriUpperDeleteCount >= this.mOriCurLeftCount) {
                Log.d("OPListView", "DeleteAnimation Case 9 ");
              } else {
                Log.d("OPListView", "DeleteAnimation Case 10 ");
              }
            }
            else if (this.mOriUpperDeleteCount >= this.mOriCurLeftCount)
            {
              Log.d("OPListView", "DeleteAnimation Case 5 ");
            }
            else
            {
              Log.d("OPListView", "DeleteAnimation Case 6 ");
              continue;
              if (this.mOriCurDeleteCount == 0) {
                Log.d("OPListView", "DeleteAnimation Case 11 ");
              } else if (this.mOriUpperDeleteCount >= this.mOriCurLeftCount) {
                Log.d("OPListView", "DeleteAnimation Case 7 ");
              } else {
                Log.d("OPListView", "DeleteAnimation Case 8 ");
              }
            }
          }
          k = this.mDelOriViewTopList.size();
          j = 0;
          while (j < k - i3)
          {
            this.mDelOriViewTopList.remove(0);
            j += 1;
          }
          j = 1;
          while (i3 > this.mDelOriViewTopList.size())
          {
            this.mDelOriViewTopList.add(0, Integer.valueOf(-i * j));
            j += 1;
          }
        }
      }
    }
    if (this.mOriUpperDeleteCount == 0) {
      if (this.mOriCurDeleteCount != 0) {}
    }
    for (;;)
    {
      j = 1;
      while (i3 > this.mDelOriViewTopList.size())
      {
        this.mDelOriViewTopList.add(0, Integer.valueOf(-i * j));
        j += 1;
      }
      break;
      Log.d("OPListView", "DeleteAnimation Case 14 ");
      continue;
      if (this.mOriCurDeleteCount == 0)
      {
        if (this.mOriUpperDeleteCount >= this.mOriCurLeftCount)
        {
          Log.d("OPListView", "DeleteAnimation Case 12 ");
          this.mDelOriViewTopList.clear();
        }
        else
        {
          Log.d("OPListView", "DeleteAnimation Case 13 ");
          j = 0;
          while (j < this.mOriUpperDeleteCount)
          {
            this.mDelOriViewTopList.remove(0);
            j += 1;
          }
        }
      }
      else if (k == 0)
      {
        if (this.mOriUpperDeleteCount >= this.mOriCurLeftCount) {
          Log.d("OPListView", "DeleteAnimation Case 15 ");
        } else {
          Log.d("OPListView", "DeleteAnimation Case 16 ");
        }
      }
      else {
        Log.d("OPListView", "DeleteAnimation Case 17 ");
      }
    }
    j = 0;
    i = i1 - 1;
    if (i >= 0)
    {
      if (((View)this.mNowViewList.get(i)).getTop() == ((Integer)this.mDelOriViewTopList.get(i)).intValue())
      {
        this.mNowViewList.remove(i);
        this.mDelOriViewTopList.remove(i);
        k = j;
      }
      for (;;)
      {
        i -= 1;
        j = k;
        break;
        k = j;
        if (((Integer)this.mDelOriViewTopList.get(i)).intValue() < ((View)this.mNowViewList.get(i)).getTop()) {
          k = j + 1;
        }
      }
    }
    if (j > 1)
    {
      localObject = (ArrayList)this.mNowViewList.clone();
      ArrayList localArrayList = (ArrayList)this.mDelOriViewTopList.clone();
      this.mNowViewList.clear();
      this.mDelOriViewTopList.clear();
      i = 0;
      if (i < ((ArrayList)localObject).size())
      {
        if (i < j) {}
        for (k = j - i - 1;; k = i)
        {
          this.mNowViewList.add((View)((ArrayList)localObject).get(k));
          this.mDelOriViewTopList.add((Integer)localArrayList.get(k));
          i += 1;
          break;
        }
      }
    }
  }
  
  private boolean shouldDrawDivider(int paramInt)
  {
    paramInt = getDividerType(paramInt + getFirstVisiblePosition());
    return (this.mDividerController == null) || ((this.mDividerController != null) && (paramInt > 0));
  }
  
  private void startDelDropAnimation()
  {
    this.mDelAniSet = new AnimatorSet();
    setDelViewLocation();
    int i = 0;
    while (i < this.mNowViewList.size())
    {
      ObjectAnimator localObjectAnimator = getAnimator(i, (View)this.mNowViewList.get(i), ((Integer)this.mDelOriViewTopList.get(i)).intValue());
      localObjectAnimator.setDuration(200L);
      localObjectAnimator.setInterpolator(this.mDecelerateInterpolator);
      localObjectAnimator.addUpdateListener(this.mAnimatorUpdateListener);
      this.mDelAniSet.playTogether(new Animator[] { localObjectAnimator });
      i += 1;
    }
    this.mDelAniSet.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        OPListView.-set0(OPListView.this, false);
        OPListView.-set3(OPListView.this, false);
        OPListView.-set2(OPListView.this, false);
        OPListView.-get1(OPListView.this).clear();
        OPListView.-get0(OPListView.this).clear();
        OPListView.-get2(OPListView.this).clear();
        OPListView.-get4(OPListView.this).clear();
        OPListView.this.invalidate();
        if (OPListView.-get3(OPListView.this) != null) {
          OPListView.-get3(OPListView.this).onAnimationEnd();
        }
      }
    });
    this.mDelAniSet.start();
  }
  
  private void startDelGoneAnimation()
  {
    this.mAnimRunning = true;
    int j = this.mDelViewList.size();
    if (j == 0)
    {
      this.mDelAnimationFlag = true;
      if (this.mDeleteAnimationListener != null) {
        this.mDeleteAnimationListener.onAnimationUpdate();
      }
      this.mDisableTouchEvent = false;
      return;
    }
    this.mDelAniSet = new AnimatorSet();
    PropertyValuesHolder localPropertyValuesHolder = PropertyValuesHolder.ofFloat("alpha", new float[] { 1.0F, 0.0F });
    int i = 0;
    while (i < j)
    {
      ObjectAnimator localObjectAnimator = ObjectAnimator.ofPropertyValuesHolder((View)this.mDelViewList.get(i), new PropertyValuesHolder[] { localPropertyValuesHolder });
      localObjectAnimator.setDuration(200L);
      localObjectAnimator.setInterpolator(this.mDecelerateInterpolator);
      localObjectAnimator.addUpdateListener(this.mAnimatorUpdateListener);
      this.mDelAniSet.playTogether(new Animator[] { localObjectAnimator });
      i += 1;
    }
    this.mDelAniSet.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        int j = OPListView.-get2(OPListView.this).size();
        int i = 0;
        while (i < j)
        {
          ((View)OPListView.-get2(OPListView.this).get(i)).setAlpha(1.0F);
          i += 1;
        }
        if ((OPListView.this.getAdapter() != null) && ((OPListView.this.getAdapter().getCount() == 0) || ((OPListView.this.getEmptyView() != null) && (OPListView.this.getAdapter().isEmpty()))))
        {
          OPListView.-set0(OPListView.this, false);
          OPListView.-set3(OPListView.this, false);
          OPListView.-set2(OPListView.this, false);
          OPListView.-get1(OPListView.this).clear();
          OPListView.-get0(OPListView.this).clear();
          OPListView.-get2(OPListView.this).clear();
          if (OPListView.-get3(OPListView.this) != null)
          {
            OPListView.-get3(OPListView.this).onAnimationUpdate();
            OPListView.-get3(OPListView.this).onAnimationEnd();
          }
        }
        do
        {
          return;
          OPListView.-set1(OPListView.this, true);
        } while (OPListView.-get3(OPListView.this) == null);
        OPListView.-get3(OPListView.this).onAnimationUpdate();
      }
    });
    this.mDelAniSet.start();
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    super.dispatchDraw(paramCanvas);
    Object localObject1 = getOverscrollHeader();
    Object localObject2 = getOverscrollFooter();
    int m;
    int i;
    label32:
    int j;
    label41:
    int i3;
    int i4;
    int i5;
    boolean bool1;
    boolean bool2;
    int i6;
    int k;
    int n;
    int i7;
    if (localObject1 != null)
    {
      m = 1;
      if (localObject2 == null) {
        break label291;
      }
      i = 1;
      if (getDivider() == null) {
        break label296;
      }
      j = 1;
      if ((j == 0) && (m == 0) && (i == 0)) {
        break label744;
      }
      localObject1 = this.mTempRect;
      ((Rect)localObject1).left = getPaddingLeft();
      ((Rect)localObject1).right = (getRight() - getLeft() - getPaddingRight());
      i3 = getChildCount();
      i4 = getHeaderViewsCount();
      i5 = getCount() - getFooterViewsCount();
      bool1 = this.mHeaderDividersEnabled;
      bool2 = this.mFooterDividersEnabled;
      i6 = getFirstVisiblePosition();
      getAdapter();
      k = 0;
      n = 0;
      if (isClipToPadding())
      {
        k = getListPaddingTop();
        n = getListPaddingBottom();
      }
      i7 = getBottom() - getTop() - n + getScrollY();
      if (isStackFromBottom()) {
        break label483;
      }
      k = getScrollY();
      if ((i3 > 0) && (k < 0) && (j != 0))
      {
        ((Rect)localObject1).bottom = 0;
        ((Rect)localObject1).top = (-getDividerHeight());
        drawDivider(paramCanvas, (Rect)localObject1, -1);
      }
      k = 0;
      label232:
      if (k >= i3) {
        break label744;
      }
      i2 = i6 + k;
      if (i2 >= i4) {
        break label301;
      }
      m = 1;
      label256:
      if (i2 < i5) {
        break label307;
      }
      n = 1;
      label266:
      if ((bool1) || (m == 0)) {
        break label313;
      }
    }
    label291:
    label296:
    label301:
    label307:
    label313:
    int i1;
    label481:
    for (;;)
    {
      k += 1;
      break label232;
      m = 0;
      break;
      i = 0;
      break label32;
      j = 0;
      break label41;
      m = 0;
      break label256;
      n = 0;
      break label266;
      if ((bool2) || (n == 0))
      {
        localObject2 = getChildAt(k);
        i8 = ((View)localObject2).getBottom();
        if (k == i3 - 1) {}
        for (i1 = 1;; i1 = 0)
        {
          if ((j == 0) || (!shouldDrawDivider(k)) || (((View)localObject2).getHeight() <= 0) || (i8 >= i7) || ((i != 0) && (i1 != 0))) {
            break label481;
          }
          i2 += 1;
          if (((!bool1) && ((m != 0) || (i2 < i4))) || ((i1 == 0) && (!bool2) && ((n != 0) || (i2 >= i5)))) {
            break;
          }
          m = (int)((View)localObject2).getTranslationY();
          ((Rect)localObject1).top = (i8 + m);
          ((Rect)localObject1).bottom = (getDividerHeight() + i8 + m);
          drawDivider(paramCanvas, (Rect)localObject1, k);
          break;
        }
      }
    }
    label483:
    int i8 = getScrollY();
    label499:
    int i9;
    if (m != 0)
    {
      i = 1;
      m = i;
      if (m >= i3) {
        break label703;
      }
      i9 = i6 + m;
      if (i9 >= i4) {
        break label557;
      }
      n = 1;
      label523:
      if (i9 < i5) {
        break label563;
      }
      i1 = 1;
      label533:
      if ((bool1) || (n == 0)) {
        break label569;
      }
    }
    label557:
    label563:
    label569:
    int i10;
    do
    {
      do
      {
        m += 1;
        break label499;
        i = 0;
        break;
        n = 0;
        break label523;
        i1 = 0;
        break label533;
      } while ((!bool2) && (i1 != 0));
      i10 = getChildAt(m).getTop();
    } while ((j == 0) || (!shouldDrawDivider(m)) || (i10 <= k));
    if (m == i) {}
    for (int i2 = 1;; i2 = 0)
    {
      i9 -= 1;
      if (((!bool1) && ((n != 0) || (i9 < i4))) || ((i2 == 0) && (!bool2) && ((i1 != 0) || (i9 >= i5)))) {
        break;
      }
      ((Rect)localObject1).top = (i10 - getDividerHeight());
      ((Rect)localObject1).bottom = i10;
      drawDivider(paramCanvas, (Rect)localObject1, m - 1);
      break;
    }
    label703:
    if ((i3 > 0) && (i8 > 0) && (j != 0))
    {
      ((Rect)localObject1).top = i7;
      ((Rect)localObject1).bottom = (getDividerHeight() + i7);
      drawDivider(paramCanvas, (Rect)localObject1, -1);
    }
    label744:
    if (this.mDelAnimationFlag)
    {
      this.mDelAnimationFlag = false;
      startDelDropAnimation();
    }
  }
  
  void drawDivider(Canvas paramCanvas, Rect paramRect, int paramInt)
  {
    Drawable localDrawable = getDivider();
    paramInt = getDividerType(paramInt + getFirstVisiblePosition());
    if (this.mDividerController != null)
    {
      if (paramInt != 1) {
        break label55;
      }
      paramRect.left = 0;
      paramRect.right = getWidth();
    }
    for (;;)
    {
      localDrawable.setBounds(paramRect);
      localDrawable.draw(paramCanvas);
      return;
      label55:
      if (paramInt == 2)
      {
        paramRect.left = 100;
        paramRect.right = (getWidth() - 32);
      }
    }
  }
  
  public Drawable getDivider()
  {
    return this.mDivider;
  }
  
  public int getDividerHeight()
  {
    return this.mDividerHeight;
  }
  
  public boolean isDeleteAnimationEnabled()
  {
    return this.mIsDisableAnimation;
  }
  
  public void setClipToPadding(boolean paramBoolean)
  {
    super.setClipToPadding(paramBoolean);
    this.mIsClipToPadding = paramBoolean;
  }
  
  public void setDelPositionsList(ArrayList<Integer> paramArrayList)
  {
    if (paramArrayList == null)
    {
      this.mDisableTouchEvent = false;
      throw new InvalidParameterException("The input parameter d is null!");
    }
    if (this.mAnimRunning)
    {
      this.mDisableTouchEvent = false;
      return;
    }
    if (!isDeleteAnimationEnabled())
    {
      if (this.mDeleteAnimationListener != null)
      {
        this.mDeleteAnimationListener.onAnimationUpdate();
        this.mDeleteAnimationListener.onAnimationStart();
        this.mDeleteAnimationListener.onAnimationEnd();
      }
      this.mDisableTouchEvent = false;
      return;
    }
    int k = paramArrayList.size();
    if (k == 0)
    {
      this.mDisableTouchEvent = false;
      return;
    }
    this.mAnimRunning = true;
    if (this.mDeleteAnimationListener != null) {
      this.mDeleteAnimationListener.onAnimationStart();
    }
    this.mInDeleteAnimation = true;
    this.mOriFirstPosition = getFirstVisiblePosition();
    int m = getChildCount();
    label202:
    label220:
    label238:
    int i;
    if (this.mOriFirstPosition + m == getAdapter().getCount() + k)
    {
      this.mOriLastPage = true;
      this.mOriUpperDeleteCount = 0;
      this.mOriCurDeleteCount = 0;
      this.mOriCurLeftCount = 0;
      this.mOriBelowLeftCount = 0;
      if (this.mDelOriViewTopList != null) {
        break label295;
      }
      this.mDelOriViewTopList = new ArrayList();
      if (this.mDelViewList != null) {
        break label305;
      }
      this.mDelViewList = new ArrayList();
      if (this.mDelPosList != null) {
        break label315;
      }
      this.mDelPosList = new ArrayList();
      i = 0;
      j = 0;
      label242:
      if (j >= k) {
        break label389;
      }
      n = ((Integer)paramArrayList.get(j)).intValue();
      if (n >= this.mOriFirstPosition) {
        break label325;
      }
      this.mOriUpperDeleteCount += 1;
    }
    for (;;)
    {
      j += 1;
      break label242;
      this.mOriLastPage = false;
      break;
      label295:
      this.mDelOriViewTopList.clear();
      break label202;
      label305:
      this.mDelViewList.clear();
      break label220;
      label315:
      this.mDelPosList.clear();
      break label238;
      label325:
      if (n < this.mOriFirstPosition + m)
      {
        this.mDelPosList.add(Integer.valueOf(n));
        this.mDelViewList.add(getChildAt(n - this.mOriFirstPosition));
        this.mOriCurDeleteCount += 1;
      }
      else
      {
        i += 1;
      }
    }
    label389:
    int j = 0;
    if ((this.mOriUpperDeleteCount > 0) || (this.mDelPosList.size() > 0)) {
      j = 1;
    }
    if (j == 0)
    {
      this.mAnimRunning = false;
      this.mInDeleteAnimation = false;
      this.mDisableTouchEvent = false;
      if (this.mDeleteAnimationListener != null)
      {
        this.mDeleteAnimationListener.onAnimationUpdate();
        this.mDeleteAnimationListener.onAnimationEnd();
      }
      return;
    }
    int n = this.mDelPosList.size();
    j = 0;
    if (j < m)
    {
      if (n > 0)
      {
        int i1 = this.mOriFirstPosition;
        if (!this.mDelPosList.contains(Integer.valueOf(i1 + j)))
        {
          paramArrayList = getChildAt(j);
          if (paramArrayList != null) {
            this.mDelOriViewTopList.add(Integer.valueOf(paramArrayList.getTop()));
          }
        }
      }
      for (;;)
      {
        j += 1;
        break;
        paramArrayList = getChildAt(j);
        if (paramArrayList != null) {
          this.mDelOriViewTopList.add(Integer.valueOf(paramArrayList.getTop()));
        }
      }
    }
    this.mOriCurLeftCount = (getChildCount() - this.mOriCurDeleteCount);
    this.mOriBelowLeftCount = (getAdapter().getCount() + k - getLastVisiblePosition() - 1 - i);
    startDelGoneAnimation();
  }
  
  public void setDeleteAnimationEnabled(boolean paramBoolean)
  {
    this.mIsDisableAnimation = paramBoolean;
  }
  
  public void setDeleteAnimationListener(DeleteAnimationListener paramDeleteAnimationListener)
  {
    this.mDeleteAnimationListener = paramDeleteAnimationListener;
  }
  
  public void setDivider(Drawable paramDrawable)
  {
    this.mDivider = paramDrawable;
    requestLayout();
    invalidate();
  }
  
  public void setDividerController(IOPDividerController paramIOPDividerController)
  {
    this.mDividerController = paramIOPDividerController;
  }
  
  public void setFooterDividersEnabled(boolean paramBoolean)
  {
    this.mFooterDividersEnabled = paramBoolean;
  }
  
  public void setHeaderDividersEnabled(boolean paramBoolean)
  {
    this.mHeaderDividersEnabled = paramBoolean;
  }
  
  public static abstract interface DeleteAnimationListener
  {
    public abstract void onAnimationEnd();
    
    public abstract void onAnimationStart();
    
    public abstract void onAnimationUpdate();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\listview\OPListView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */