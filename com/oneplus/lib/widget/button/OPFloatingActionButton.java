package com.oneplus.lib.widget.button;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.dimen;
import com.oneplus.commonctrl.R.style;
import com.oneplus.commonctrl.R.styleable;
import com.oneplus.lib.design.widget.AppBarLayout;
import com.oneplus.lib.design.widget.CoordinatorLayout;
import com.oneplus.lib.design.widget.CoordinatorLayout.Behavior;
import com.oneplus.lib.design.widget.CoordinatorLayout.DefaultBehavior;
import com.oneplus.lib.design.widget.CoordinatorLayout.LayoutParams;
import com.oneplus.lib.design.widget.Utils;
import java.util.List;

@CoordinatorLayout.DefaultBehavior(Behavior.class)
public class OPFloatingActionButton
  extends ImageView
{
  private static final int SIZE_MINI = 1;
  private static final int SIZE_NORMAL = 0;
  private ColorStateList mBackgroundTint;
  private PorterDuff.Mode mBackgroundTintMode;
  private int mBorderWidth;
  private int mContentPadding;
  private final OPFloatingActionButtonImpl mImpl;
  private int mRippleColor;
  private final Rect mShadowPadding = new Rect();
  private int mSize;
  private int mUserSetVisibility = getVisibility();
  
  public OPFloatingActionButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPFloatingActionButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.OPFloatingActionButtonStyle);
  }
  
  public OPFloatingActionButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OPFloatingActionButton, paramInt, R.style.OnePlus_Widget_Design_FloatingActionButton);
    paramAttributeSet = paramContext.getDrawable(R.styleable.OPFloatingActionButton_android_background);
    this.mBackgroundTint = paramContext.getColorStateList(R.styleable.OPFloatingActionButton_op_backgroundTint);
    this.mBackgroundTintMode = parseTintMode(paramContext.getInt(R.styleable.OPFloatingActionButton_op_backgroundTintMode, -1), null);
    this.mRippleColor = paramContext.getColor(R.styleable.OPFloatingActionButton_op_rippleColor, 0);
    this.mSize = paramContext.getInt(R.styleable.OPFloatingActionButton_op_fabSize, 0);
    this.mBorderWidth = paramContext.getDimensionPixelSize(R.styleable.OPFloatingActionButton_op_borderWidth, 0);
    float f1 = paramContext.getDimension(R.styleable.OPFloatingActionButton_op_elevation, 0.0F);
    float f2 = paramContext.getDimension(R.styleable.OPFloatingActionButton_op_pressedTranslationZ, 0.0F);
    paramContext.recycle();
    this.mImpl = new OPFloatingActionButtonImpl(this, new OPShadowViewDelegate()
    {
      public float getRadius()
      {
        return OPFloatingActionButton.this.getSizeDimension() / 2.0F;
      }
      
      public void setBackground(Drawable paramAnonymousDrawable)
      {
        OPFloatingActionButton.-wrap0(OPFloatingActionButton.this, paramAnonymousDrawable);
      }
      
      public void setShadowPadding(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        OPFloatingActionButton.-get1(OPFloatingActionButton.this).set(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
        OPFloatingActionButton.this.setPadding(OPFloatingActionButton.-get0(OPFloatingActionButton.this) + paramAnonymousInt1, OPFloatingActionButton.-get0(OPFloatingActionButton.this) + paramAnonymousInt2, OPFloatingActionButton.-get0(OPFloatingActionButton.this) + paramAnonymousInt3, OPFloatingActionButton.-get0(OPFloatingActionButton.this) + paramAnonymousInt4);
      }
    });
    paramInt = (int)getResources().getDimension(R.dimen.design_fab_content_size);
    this.mContentPadding = ((getSizeDimension() - paramInt) / 2);
    this.mImpl.setBackground(paramAttributeSet, this.mBackgroundTint, this.mBackgroundTintMode, this.mRippleColor, this.mBorderWidth);
    this.mImpl.setElevation(f1);
    this.mImpl.setPressedTranslationZ(f2);
    setClickable(true);
  }
  
  static PorterDuff.Mode parseTintMode(int paramInt, PorterDuff.Mode paramMode)
  {
    switch (paramInt)
    {
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
    }
    return PorterDuff.Mode.SCREEN;
  }
  
  private static int resolveAdjustedSize(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt2);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    switch (i)
    {
    default: 
      return paramInt1;
    case 0: 
      return paramInt1;
    case -2147483648: 
      return Math.min(paramInt1, paramInt2);
    }
    return paramInt2;
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    this.mImpl.onDrawableStateChanged(getDrawableState());
  }
  
  public ColorStateList getBackgroundTintList()
  {
    return this.mBackgroundTint;
  }
  
  public PorterDuff.Mode getBackgroundTintMode()
  {
    return this.mBackgroundTintMode;
  }
  
  final int getSizeDimension()
  {
    switch (this.mSize)
    {
    default: 
      return getResources().getDimensionPixelSize(R.dimen.design_fab_size_normal);
    }
    return getResources().getDimensionPixelSize(R.dimen.design_fab_size_mini);
  }
  
  final int getUserSetVisibility()
  {
    return this.mUserSetVisibility;
  }
  
  public void hide()
  {
    hide(true);
  }
  
  public void hide(boolean paramBoolean)
  {
    this.mImpl.hide(paramBoolean);
  }
  
  final void internalSetVisibility(int paramInt, boolean paramBoolean)
  {
    super.setVisibility(paramInt);
    if (paramBoolean) {
      this.mUserSetVisibility = paramInt;
    }
  }
  
  @TargetApi(11)
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    this.mImpl.jumpDrawableToCurrentState();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = getSizeDimension();
    paramInt1 = Math.min(resolveAdjustedSize(i, paramInt1), resolveAdjustedSize(i, paramInt2));
    setMeasuredDimension(this.mShadowPadding.left + paramInt1 + this.mShadowPadding.right, this.mShadowPadding.top + paramInt1 + this.mShadowPadding.bottom);
  }
  
  public void setBackground(Drawable paramDrawable)
  {
    if (this.mImpl != null) {
      this.mImpl.setBackground(paramDrawable, this.mBackgroundTint, this.mBackgroundTintMode, this.mRippleColor, this.mBorderWidth);
    }
  }
  
  public void setBackgroundTintList(ColorStateList paramColorStateList)
  {
    if (this.mBackgroundTint != paramColorStateList)
    {
      this.mBackgroundTint = paramColorStateList;
      this.mImpl.setBackgroundTintList(paramColorStateList);
    }
  }
  
  public void setBackgroundTintMode(PorterDuff.Mode paramMode)
  {
    if (this.mBackgroundTintMode != paramMode)
    {
      this.mBackgroundTintMode = paramMode;
      this.mImpl.setBackgroundTintMode(paramMode);
    }
  }
  
  public void setRippleColor(int paramInt)
  {
    if (this.mRippleColor != paramInt)
    {
      this.mRippleColor = paramInt;
      this.mImpl.setRippleColor(paramInt);
    }
  }
  
  public void setVisibility(int paramInt)
  {
    internalSetVisibility(paramInt, true);
  }
  
  public void show()
  {
    show(true);
  }
  
  public void show(boolean paramBoolean)
  {
    this.mImpl.show(paramBoolean);
  }
  
  public static class Behavior
    extends CoordinatorLayout.Behavior<OPFloatingActionButton>
  {
    private static final boolean AUTO_HIDE_DEFAULT = true;
    private boolean mAutoHideEnabled;
    private OPFloatingActionButton.OnVisibilityChangedListener mInternalAutoHideListener;
    private Rect mTmpRect;
    
    public Behavior()
    {
      this.mAutoHideEnabled = true;
    }
    
    public Behavior(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OpFloatingActionButton_Behavior_Layout);
      this.mAutoHideEnabled = paramContext.getBoolean(R.styleable.OpFloatingActionButton_Behavior_Layout_op_behavior_autoHide, true);
      paramContext.recycle();
    }
    
    private static boolean isBottomSheet(@NonNull View paramView)
    {
      return false;
    }
    
    private void offsetIfNeeded(CoordinatorLayout paramCoordinatorLayout, OPFloatingActionButton paramOPFloatingActionButton)
    {
      Rect localRect = OPFloatingActionButton.-get1(paramOPFloatingActionButton);
      CoordinatorLayout.LayoutParams localLayoutParams;
      int j;
      int i;
      if ((localRect != null) && (localRect.centerX() > 0) && (localRect.centerY() > 0))
      {
        localLayoutParams = (CoordinatorLayout.LayoutParams)paramOPFloatingActionButton.getLayoutParams();
        j = 0;
        i = 0;
        if (paramOPFloatingActionButton.getRight() < paramCoordinatorLayout.getWidth() - localLayoutParams.rightMargin) {
          break label109;
        }
        i = localRect.right;
        if (paramOPFloatingActionButton.getBottom() < paramCoordinatorLayout.getHeight() - localLayoutParams.bottomMargin) {
          break label131;
        }
        j = localRect.bottom;
      }
      for (;;)
      {
        if (j != 0) {
          ViewCompat.offsetTopAndBottom(paramOPFloatingActionButton, j);
        }
        if (i != 0) {
          ViewCompat.offsetLeftAndRight(paramOPFloatingActionButton, i);
        }
        return;
        label109:
        if (paramOPFloatingActionButton.getLeft() > localLayoutParams.leftMargin) {
          break;
        }
        i = -localRect.left;
        break;
        label131:
        if (paramOPFloatingActionButton.getTop() <= localLayoutParams.topMargin) {
          j = -localRect.top;
        }
      }
    }
    
    private boolean shouldUpdateVisibility(View paramView, OPFloatingActionButton paramOPFloatingActionButton)
    {
      CoordinatorLayout.LayoutParams localLayoutParams = (CoordinatorLayout.LayoutParams)paramOPFloatingActionButton.getLayoutParams();
      if (!this.mAutoHideEnabled) {
        return false;
      }
      if (localLayoutParams.getAnchorId() != paramView.getId()) {
        return false;
      }
      return paramOPFloatingActionButton.getUserSetVisibility() == 0;
    }
    
    private boolean updateFabVisibilityForAppBarLayout(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout, OPFloatingActionButton paramOPFloatingActionButton)
    {
      if (!shouldUpdateVisibility(paramAppBarLayout, paramOPFloatingActionButton)) {
        return false;
      }
      if (this.mTmpRect == null) {
        this.mTmpRect = new Rect();
      }
      Rect localRect = this.mTmpRect;
      Utils.getDescendantRect(paramCoordinatorLayout, paramAppBarLayout, localRect);
      if (localRect.bottom <= paramAppBarLayout.getMinimumHeightForVisibleOverlappingContent()) {
        paramOPFloatingActionButton.hide(false);
      }
      for (;;)
      {
        return true;
        paramOPFloatingActionButton.show(false);
      }
    }
    
    private boolean updateFabVisibilityForBottomSheet(View paramView, OPFloatingActionButton paramOPFloatingActionButton)
    {
      if (!shouldUpdateVisibility(paramView, paramOPFloatingActionButton)) {
        return false;
      }
      CoordinatorLayout.LayoutParams localLayoutParams = (CoordinatorLayout.LayoutParams)paramOPFloatingActionButton.getLayoutParams();
      if (paramView.getTop() < paramOPFloatingActionButton.getHeight() / 2 + localLayoutParams.topMargin) {
        paramOPFloatingActionButton.hide(false);
      }
      for (;;)
      {
        return true;
        paramOPFloatingActionButton.show(false);
      }
    }
    
    public boolean getInsetDodgeRect(@NonNull CoordinatorLayout paramCoordinatorLayout, @NonNull OPFloatingActionButton paramOPFloatingActionButton, @NonNull Rect paramRect)
    {
      paramCoordinatorLayout = OPFloatingActionButton.-get1(paramOPFloatingActionButton);
      paramRect.set(paramOPFloatingActionButton.getLeft() + paramCoordinatorLayout.left, paramOPFloatingActionButton.getTop() + paramCoordinatorLayout.top, paramOPFloatingActionButton.getRight() - paramCoordinatorLayout.right, paramOPFloatingActionButton.getBottom() - paramCoordinatorLayout.bottom);
      return true;
    }
    
    public boolean isAutoHideEnabled()
    {
      return this.mAutoHideEnabled;
    }
    
    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams paramLayoutParams)
    {
      if (paramLayoutParams.dodgeInsetEdges == 0) {
        paramLayoutParams.dodgeInsetEdges = 80;
      }
    }
    
    public boolean onDependentViewChanged(CoordinatorLayout paramCoordinatorLayout, OPFloatingActionButton paramOPFloatingActionButton, View paramView)
    {
      if ((paramView instanceof AppBarLayout)) {
        updateFabVisibilityForAppBarLayout(paramCoordinatorLayout, (AppBarLayout)paramView, paramOPFloatingActionButton);
      }
      for (;;)
      {
        return false;
        if (isBottomSheet(paramView)) {
          updateFabVisibilityForBottomSheet(paramView, paramOPFloatingActionButton);
        }
      }
    }
    
    public boolean onLayoutChild(CoordinatorLayout paramCoordinatorLayout, OPFloatingActionButton paramOPFloatingActionButton, int paramInt)
    {
      List localList = paramCoordinatorLayout.getDependencies(paramOPFloatingActionButton);
      int i = 0;
      int j = localList.size();
      for (;;)
      {
        View localView;
        if (i < j)
        {
          localView = (View)localList.get(i);
          if (!(localView instanceof AppBarLayout)) {
            break label76;
          }
          if (!updateFabVisibilityForAppBarLayout(paramCoordinatorLayout, (AppBarLayout)localView, paramOPFloatingActionButton)) {
            break label94;
          }
        }
        label76:
        while ((isBottomSheet(localView)) && (updateFabVisibilityForBottomSheet(localView, paramOPFloatingActionButton)))
        {
          paramCoordinatorLayout.onLayoutChild(paramOPFloatingActionButton, paramInt);
          offsetIfNeeded(paramCoordinatorLayout, paramOPFloatingActionButton);
          return true;
        }
        label94:
        i += 1;
      }
    }
    
    public void setAutoHideEnabled(boolean paramBoolean)
    {
      this.mAutoHideEnabled = paramBoolean;
    }
    
    @VisibleForTesting
    void setInternalAutoHideListener(OPFloatingActionButton.OnVisibilityChangedListener paramOnVisibilityChangedListener)
    {
      this.mInternalAutoHideListener = paramOnVisibilityChangedListener;
    }
  }
  
  public static abstract class OnVisibilityChangedListener
  {
    public void onHidden(OPFloatingActionButton paramOPFloatingActionButton) {}
    
    public void onShown(OPFloatingActionButton paramOPFloatingActionButton) {}
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\button\OPFloatingActionButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */