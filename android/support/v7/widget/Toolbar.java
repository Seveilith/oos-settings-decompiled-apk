package android.support.v7.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.CollapsibleActionView;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.SubMenuBuilder;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class Toolbar
  extends ViewGroup
{
  private static final String TAG = "Toolbar";
  private MenuPresenter.Callback mActionMenuPresenterCallback;
  int mButtonGravity;
  ImageButton mCollapseButtonView;
  private CharSequence mCollapseDescription;
  private Drawable mCollapseIcon;
  private boolean mCollapsible;
  private int mContentInsetEndWithActions;
  private int mContentInsetStartWithNavigation;
  private RtlSpacingHelper mContentInsets;
  private boolean mEatingHover;
  private boolean mEatingTouch;
  View mExpandedActionView;
  private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
  private int mGravity = 8388627;
  private final ArrayList<View> mHiddenViews = new ArrayList();
  private ImageView mLogoView;
  private int mMaxButtonHeight;
  private MenuBuilder.Callback mMenuBuilderCallback;
  private ActionMenuView mMenuView;
  private final ActionMenuView.OnMenuItemClickListener mMenuViewItemClickListener = new ActionMenuView.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      if (Toolbar.this.mOnMenuItemClickListener != null) {
        return Toolbar.this.mOnMenuItemClickListener.onMenuItemClick(paramAnonymousMenuItem);
      }
      return false;
    }
  };
  private ImageButton mNavButtonView;
  OnMenuItemClickListener mOnMenuItemClickListener;
  private ActionMenuPresenter mOuterActionMenuPresenter;
  private Context mPopupContext;
  private int mPopupTheme;
  private final Runnable mShowOverflowMenuRunnable = new Runnable()
  {
    public void run()
    {
      Toolbar.this.showOverflowMenu();
    }
  };
  private CharSequence mSubtitleText;
  private int mSubtitleTextAppearance;
  private int mSubtitleTextColor;
  private TextView mSubtitleTextView;
  private final int[] mTempMargins = new int[2];
  private final ArrayList<View> mTempViews = new ArrayList();
  private int mTitleMarginBottom;
  private int mTitleMarginEnd;
  private int mTitleMarginStart;
  private int mTitleMarginTop;
  private CharSequence mTitleText;
  private int mTitleTextAppearance;
  private int mTitleTextColor;
  private TextView mTitleTextView;
  private ToolbarWidgetWrapper mWrapper;
  
  public Toolbar(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Toolbar(Context paramContext, @Nullable AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.toolbarStyle);
  }
  
  public Toolbar(Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramContext = TintTypedArray.obtainStyledAttributes(getContext(), paramAttributeSet, R.styleable.Toolbar, paramInt, 0);
    this.mTitleTextAppearance = paramContext.getResourceId(R.styleable.Toolbar_titleTextAppearance, 0);
    this.mSubtitleTextAppearance = paramContext.getResourceId(R.styleable.Toolbar_subtitleTextAppearance, 0);
    this.mGravity = paramContext.getInteger(R.styleable.Toolbar_android_gravity, this.mGravity);
    this.mButtonGravity = paramContext.getInteger(R.styleable.Toolbar_buttonGravity, 48);
    int i = paramContext.getDimensionPixelOffset(R.styleable.Toolbar_titleMargin, 0);
    paramInt = i;
    if (paramContext.hasValue(R.styleable.Toolbar_titleMargins)) {
      paramInt = paramContext.getDimensionPixelOffset(R.styleable.Toolbar_titleMargins, i);
    }
    this.mTitleMarginBottom = paramInt;
    this.mTitleMarginTop = paramInt;
    this.mTitleMarginEnd = paramInt;
    this.mTitleMarginStart = paramInt;
    paramInt = paramContext.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginStart, -1);
    if (paramInt >= 0) {
      this.mTitleMarginStart = paramInt;
    }
    paramInt = paramContext.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginEnd, -1);
    if (paramInt >= 0) {
      this.mTitleMarginEnd = paramInt;
    }
    paramInt = paramContext.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginTop, -1);
    if (paramInt >= 0) {
      this.mTitleMarginTop = paramInt;
    }
    paramInt = paramContext.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginBottom, -1);
    if (paramInt >= 0) {
      this.mTitleMarginBottom = paramInt;
    }
    this.mMaxButtonHeight = paramContext.getDimensionPixelSize(R.styleable.Toolbar_maxButtonHeight, -1);
    paramInt = paramContext.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetStart, Integer.MIN_VALUE);
    i = paramContext.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetEnd, Integer.MIN_VALUE);
    int j = paramContext.getDimensionPixelSize(R.styleable.Toolbar_contentInsetLeft, 0);
    int k = paramContext.getDimensionPixelSize(R.styleable.Toolbar_contentInsetRight, 0);
    ensureContentInsets();
    this.mContentInsets.setAbsolute(j, k);
    if ((paramInt != Integer.MIN_VALUE) || (i != Integer.MIN_VALUE)) {
      this.mContentInsets.setRelative(paramInt, i);
    }
    this.mContentInsetStartWithNavigation = paramContext.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetStartWithNavigation, Integer.MIN_VALUE);
    this.mContentInsetEndWithActions = paramContext.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetEndWithActions, Integer.MIN_VALUE);
    this.mCollapseIcon = paramContext.getDrawable(R.styleable.Toolbar_collapseIcon);
    this.mCollapseDescription = paramContext.getText(R.styleable.Toolbar_collapseContentDescription);
    paramAttributeSet = paramContext.getText(R.styleable.Toolbar_title);
    if (!TextUtils.isEmpty(paramAttributeSet)) {
      setTitle(paramAttributeSet);
    }
    paramAttributeSet = paramContext.getText(R.styleable.Toolbar_subtitle);
    if (!TextUtils.isEmpty(paramAttributeSet)) {
      setSubtitle(paramAttributeSet);
    }
    this.mPopupContext = getContext();
    setPopupTheme(paramContext.getResourceId(R.styleable.Toolbar_popupTheme, 0));
    paramAttributeSet = paramContext.getDrawable(R.styleable.Toolbar_navigationIcon);
    if (paramAttributeSet != null) {
      setNavigationIcon(paramAttributeSet);
    }
    paramAttributeSet = paramContext.getText(R.styleable.Toolbar_navigationContentDescription);
    if (!TextUtils.isEmpty(paramAttributeSet)) {
      setNavigationContentDescription(paramAttributeSet);
    }
    paramAttributeSet = paramContext.getDrawable(R.styleable.Toolbar_logo);
    if (paramAttributeSet != null) {
      setLogo(paramAttributeSet);
    }
    paramAttributeSet = paramContext.getText(R.styleable.Toolbar_logoDescription);
    if (!TextUtils.isEmpty(paramAttributeSet)) {
      setLogoDescription(paramAttributeSet);
    }
    if (paramContext.hasValue(R.styleable.Toolbar_titleTextColor)) {
      setTitleTextColor(paramContext.getColor(R.styleable.Toolbar_titleTextColor, -1));
    }
    if (paramContext.hasValue(R.styleable.Toolbar_subtitleTextColor)) {
      setSubtitleTextColor(paramContext.getColor(R.styleable.Toolbar_subtitleTextColor, -1));
    }
    paramContext.recycle();
  }
  
  private void addCustomViewsWithGravity(List<View> paramList, int paramInt)
  {
    if (ViewCompat.getLayoutDirection(this) == 1) {}
    int k;
    int j;
    View localView;
    LayoutParams localLayoutParams;
    for (int i = 1;; i = 0)
    {
      k = getChildCount();
      j = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
      paramList.clear();
      if (i == 0) {
        break;
      }
      paramInt = k - 1;
      while (paramInt >= 0)
      {
        localView = getChildAt(paramInt);
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if ((localLayoutParams.mViewType == 0) && (shouldLayout(localView)) && (getChildHorizontalGravity(localLayoutParams.gravity) == j)) {
          paramList.add(localView);
        }
        paramInt -= 1;
      }
    }
    paramInt = 0;
    while (paramInt < k)
    {
      localView = getChildAt(paramInt);
      localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if ((localLayoutParams.mViewType == 0) && (shouldLayout(localView)) && (getChildHorizontalGravity(localLayoutParams.gravity) == j)) {
        paramList.add(localView);
      }
      paramInt += 1;
    }
  }
  
  private void addSystemView(View paramView, boolean paramBoolean)
  {
    Object localObject = paramView.getLayoutParams();
    if (localObject == null) {
      localObject = generateDefaultLayoutParams();
    }
    for (;;)
    {
      ((LayoutParams)localObject).mViewType = 1;
      if ((!paramBoolean) || (this.mExpandedActionView == null)) {
        break;
      }
      paramView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.mHiddenViews.add(paramView);
      return;
      if (!checkLayoutParams((ViewGroup.LayoutParams)localObject)) {
        localObject = generateLayoutParams((ViewGroup.LayoutParams)localObject);
      } else {
        localObject = (LayoutParams)localObject;
      }
    }
    addView(paramView, (ViewGroup.LayoutParams)localObject);
  }
  
  private void ensureContentInsets()
  {
    if (this.mContentInsets == null) {
      this.mContentInsets = new RtlSpacingHelper();
    }
  }
  
  private void ensureLogoView()
  {
    if (this.mLogoView == null) {
      this.mLogoView = new AppCompatImageView(getContext());
    }
  }
  
  private void ensureMenu()
  {
    ensureMenuView();
    if (this.mMenuView.peekMenu() == null)
    {
      MenuBuilder localMenuBuilder = (MenuBuilder)this.mMenuView.getMenu();
      if (this.mExpandedMenuPresenter == null) {
        this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
      }
      this.mMenuView.setExpandedActionViewsExclusive(true);
      localMenuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
    }
  }
  
  private void ensureMenuView()
  {
    if (this.mMenuView == null)
    {
      this.mMenuView = new ActionMenuView(getContext());
      this.mMenuView.setPopupTheme(this.mPopupTheme);
      this.mMenuView.setOnMenuItemClickListener(this.mMenuViewItemClickListener);
      this.mMenuView.setMenuCallbacks(this.mActionMenuPresenterCallback, this.mMenuBuilderCallback);
      LayoutParams localLayoutParams = generateDefaultLayoutParams();
      localLayoutParams.gravity = (this.mButtonGravity & 0x70 | 0x800005);
      this.mMenuView.setLayoutParams(localLayoutParams);
      addSystemView(this.mMenuView, false);
    }
  }
  
  private void ensureNavButtonView()
  {
    if (this.mNavButtonView == null)
    {
      this.mNavButtonView = new AppCompatImageButton(getContext(), null, R.attr.toolbarNavigationButtonStyle);
      LayoutParams localLayoutParams = generateDefaultLayoutParams();
      localLayoutParams.gravity = (this.mButtonGravity & 0x70 | 0x800003);
      this.mNavButtonView.setLayoutParams(localLayoutParams);
    }
  }
  
  private int getChildHorizontalGravity(int paramInt)
  {
    int i = ViewCompat.getLayoutDirection(this);
    paramInt = GravityCompat.getAbsoluteGravity(paramInt, i) & 0x7;
    switch (paramInt)
    {
    case 2: 
    case 4: 
    default: 
      if (i == 1) {
        return 5;
      }
      break;
    case 1: 
    case 3: 
    case 5: 
      return paramInt;
    }
    return 3;
  }
  
  private int getChildTop(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int j = paramView.getMeasuredHeight();
    int k;
    int m;
    int i;
    if (paramInt > 0)
    {
      paramInt = (j - paramInt) / 2;
      switch (getChildVerticalGravity(localLayoutParams.gravity))
      {
      case 16: 
      default: 
        k = getPaddingTop();
        paramInt = getPaddingBottom();
        m = getHeight();
        i = (m - k - paramInt - j) / 2;
        if (i < localLayoutParams.topMargin) {
          paramInt = localLayoutParams.topMargin;
        }
        break;
      }
    }
    for (;;)
    {
      return k + paramInt;
      paramInt = 0;
      break;
      return getPaddingTop() - paramInt;
      return getHeight() - getPaddingBottom() - j - localLayoutParams.bottomMargin - paramInt;
      j = m - paramInt - j - i - k;
      paramInt = i;
      if (j < localLayoutParams.bottomMargin) {
        paramInt = Math.max(0, i - (localLayoutParams.bottomMargin - j));
      }
    }
  }
  
  private int getChildVerticalGravity(int paramInt)
  {
    paramInt &= 0x70;
    switch (paramInt)
    {
    default: 
      return this.mGravity & 0x70;
    }
    return paramInt;
  }
  
  private int getHorizontalMargins(View paramView)
  {
    paramView = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    return MarginLayoutParamsCompat.getMarginStart(paramView) + MarginLayoutParamsCompat.getMarginEnd(paramView);
  }
  
  private MenuInflater getMenuInflater()
  {
    return new SupportMenuInflater(getContext());
  }
  
  private int getVerticalMargins(View paramView)
  {
    paramView = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    return paramView.topMargin + paramView.bottomMargin;
  }
  
  private int getViewListMeasuredWidth(List<View> paramList, int[] paramArrayOfInt)
  {
    int m = paramArrayOfInt[0];
    int k = paramArrayOfInt[1];
    int j = 0;
    int n = paramList.size();
    int i = 0;
    while (i < n)
    {
      paramArrayOfInt = (View)paramList.get(i);
      LayoutParams localLayoutParams = (LayoutParams)paramArrayOfInt.getLayoutParams();
      m = localLayoutParams.leftMargin - m;
      k = localLayoutParams.rightMargin - k;
      int i1 = Math.max(0, m);
      int i2 = Math.max(0, k);
      m = Math.max(0, -m);
      k = Math.max(0, -k);
      j += paramArrayOfInt.getMeasuredWidth() + i1 + i2;
      i += 1;
    }
    return j;
  }
  
  private boolean isChildOrHidden(View paramView)
  {
    if (paramView.getParent() != this) {
      return this.mHiddenViews.contains(paramView);
    }
    return true;
  }
  
  private static boolean isCustomView(View paramView)
  {
    return ((LayoutParams)paramView.getLayoutParams()).mViewType == 0;
  }
  
  private int layoutChildLeft(View paramView, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = localLayoutParams.leftMargin - paramArrayOfInt[0];
    paramInt1 += Math.max(0, i);
    paramArrayOfInt[0] = Math.max(0, -i);
    paramInt2 = getChildTop(paramView, paramInt2);
    i = paramView.getMeasuredWidth();
    paramView.layout(paramInt1, paramInt2, paramInt1 + i, paramView.getMeasuredHeight() + paramInt2);
    return paramInt1 + (localLayoutParams.rightMargin + i);
  }
  
  private int layoutChildRight(View paramView, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = localLayoutParams.rightMargin - paramArrayOfInt[1];
    paramInt1 -= Math.max(0, i);
    paramArrayOfInt[1] = Math.max(0, -i);
    paramInt2 = getChildTop(paramView, paramInt2);
    i = paramView.getMeasuredWidth();
    paramView.layout(paramInt1 - i, paramInt2, paramInt1, paramView.getMeasuredHeight() + paramInt2);
    return paramInt1 - (localLayoutParams.leftMargin + i);
  }
  
  private int measureChildCollapseMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    int i = localMarginLayoutParams.leftMargin - paramArrayOfInt[0];
    int j = localMarginLayoutParams.rightMargin - paramArrayOfInt[1];
    int k = Math.max(0, i) + Math.max(0, j);
    paramArrayOfInt[0] = Math.max(0, -i);
    paramArrayOfInt[1] = Math.max(0, -j);
    paramView.measure(getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight() + k + paramInt2, localMarginLayoutParams.width), getChildMeasureSpec(paramInt3, getPaddingTop() + getPaddingBottom() + localMarginLayoutParams.topMargin + localMarginLayoutParams.bottomMargin + paramInt4, localMarginLayoutParams.height));
    return paramView.getMeasuredWidth() + k;
  }
  
  private void measureChildConstrained(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    int i = getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight() + localMarginLayoutParams.leftMargin + localMarginLayoutParams.rightMargin + paramInt2, localMarginLayoutParams.width);
    paramInt2 = getChildMeasureSpec(paramInt3, getPaddingTop() + getPaddingBottom() + localMarginLayoutParams.topMargin + localMarginLayoutParams.bottomMargin + paramInt4, localMarginLayoutParams.height);
    paramInt3 = View.MeasureSpec.getMode(paramInt2);
    paramInt1 = paramInt2;
    if (paramInt3 != 1073741824)
    {
      paramInt1 = paramInt2;
      if (paramInt5 >= 0) {
        if (paramInt3 == 0) {
          break label132;
        }
      }
    }
    label132:
    for (paramInt1 = Math.min(View.MeasureSpec.getSize(paramInt2), paramInt5);; paramInt1 = paramInt5)
    {
      paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824);
      paramView.measure(i, paramInt1);
      return;
    }
  }
  
  private void postShowOverflowMenu()
  {
    removeCallbacks(this.mShowOverflowMenuRunnable);
    post(this.mShowOverflowMenuRunnable);
  }
  
  private boolean shouldCollapse()
  {
    if (!this.mCollapsible) {
      return false;
    }
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = getChildAt(i);
      if ((shouldLayout(localView)) && (localView.getMeasuredWidth() > 0) && (localView.getMeasuredHeight() > 0)) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private boolean shouldLayout(View paramView)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramView != null)
    {
      bool1 = bool2;
      if (paramView.getParent() == this)
      {
        bool1 = bool2;
        if (paramView.getVisibility() != 8) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  void addChildrenForExpandedActionView()
  {
    int i = this.mHiddenViews.size() - 1;
    while (i >= 0)
    {
      addView((View)this.mHiddenViews.get(i));
      i -= 1;
    }
    this.mHiddenViews.clear();
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public boolean canShowOverflowMenu()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (getVisibility() == 0)
    {
      bool1 = bool2;
      if (this.mMenuView != null) {
        bool1 = this.mMenuView.isOverflowReserved();
      }
    }
    return bool1;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (super.checkLayoutParams(paramLayoutParams)) {
      return paramLayoutParams instanceof LayoutParams;
    }
    return false;
  }
  
  public void collapseActionView()
  {
    MenuItemImpl localMenuItemImpl = null;
    if (this.mExpandedMenuPresenter == null) {}
    for (;;)
    {
      if (localMenuItemImpl != null) {
        localMenuItemImpl.collapseActionView();
      }
      return;
      localMenuItemImpl = this.mExpandedMenuPresenter.mCurrentExpandedItem;
    }
  }
  
  public void dismissPopupMenus()
  {
    if (this.mMenuView != null) {
      this.mMenuView.dismissPopupMenus();
    }
  }
  
  void ensureCollapseButtonView()
  {
    if (this.mCollapseButtonView == null)
    {
      this.mCollapseButtonView = new AppCompatImageButton(getContext(), null, R.attr.toolbarNavigationButtonStyle);
      this.mCollapseButtonView.setImageDrawable(this.mCollapseIcon);
      this.mCollapseButtonView.setContentDescription(this.mCollapseDescription);
      LayoutParams localLayoutParams = generateDefaultLayoutParams();
      localLayoutParams.gravity = (this.mButtonGravity & 0x70 | 0x800003);
      localLayoutParams.mViewType = 2;
      this.mCollapseButtonView.setLayoutParams(localLayoutParams);
      this.mCollapseButtonView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          Toolbar.this.collapseActionView();
        }
      });
    }
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams)) {
      return new LayoutParams((LayoutParams)paramLayoutParams);
    }
    if ((paramLayoutParams instanceof ActionBar.LayoutParams)) {
      return new LayoutParams((ActionBar.LayoutParams)paramLayoutParams);
    }
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  public int getContentInsetEnd()
  {
    if (this.mContentInsets != null) {
      return this.mContentInsets.getEnd();
    }
    return 0;
  }
  
  public int getContentInsetEndWithActions()
  {
    if (this.mContentInsetEndWithActions != Integer.MIN_VALUE) {
      return this.mContentInsetEndWithActions;
    }
    return getContentInsetEnd();
  }
  
  public int getContentInsetLeft()
  {
    if (this.mContentInsets != null) {
      return this.mContentInsets.getLeft();
    }
    return 0;
  }
  
  public int getContentInsetRight()
  {
    if (this.mContentInsets != null) {
      return this.mContentInsets.getRight();
    }
    return 0;
  }
  
  public int getContentInsetStart()
  {
    if (this.mContentInsets != null) {
      return this.mContentInsets.getStart();
    }
    return 0;
  }
  
  public int getContentInsetStartWithNavigation()
  {
    if (this.mContentInsetStartWithNavigation != Integer.MIN_VALUE) {
      return this.mContentInsetStartWithNavigation;
    }
    return getContentInsetStart();
  }
  
  public int getCurrentContentInsetEnd()
  {
    boolean bool = false;
    MenuBuilder localMenuBuilder;
    if (this.mMenuView != null)
    {
      localMenuBuilder = this.mMenuView.peekMenu();
      if (localMenuBuilder == null) {
        break label46;
      }
    }
    label46:
    for (bool = localMenuBuilder.hasVisibleItems(); bool; bool = false) {
      return Math.max(getContentInsetEnd(), Math.max(this.mContentInsetEndWithActions, 0));
    }
    return getContentInsetEnd();
  }
  
  public int getCurrentContentInsetLeft()
  {
    if (ViewCompat.getLayoutDirection(this) == 1) {
      return getCurrentContentInsetEnd();
    }
    return getCurrentContentInsetStart();
  }
  
  public int getCurrentContentInsetRight()
  {
    if (ViewCompat.getLayoutDirection(this) == 1) {
      return getCurrentContentInsetStart();
    }
    return getCurrentContentInsetEnd();
  }
  
  public int getCurrentContentInsetStart()
  {
    if (getNavigationIcon() != null) {
      return Math.max(getContentInsetStart(), Math.max(this.mContentInsetStartWithNavigation, 0));
    }
    return getContentInsetStart();
  }
  
  public Drawable getLogo()
  {
    Drawable localDrawable = null;
    if (this.mLogoView != null) {
      localDrawable = this.mLogoView.getDrawable();
    }
    return localDrawable;
  }
  
  public CharSequence getLogoDescription()
  {
    CharSequence localCharSequence = null;
    if (this.mLogoView != null) {
      localCharSequence = this.mLogoView.getContentDescription();
    }
    return localCharSequence;
  }
  
  public Menu getMenu()
  {
    ensureMenu();
    return this.mMenuView.getMenu();
  }
  
  @Nullable
  public CharSequence getNavigationContentDescription()
  {
    CharSequence localCharSequence = null;
    if (this.mNavButtonView != null) {
      localCharSequence = this.mNavButtonView.getContentDescription();
    }
    return localCharSequence;
  }
  
  @Nullable
  public Drawable getNavigationIcon()
  {
    Drawable localDrawable = null;
    if (this.mNavButtonView != null) {
      localDrawable = this.mNavButtonView.getDrawable();
    }
    return localDrawable;
  }
  
  @Nullable
  public Drawable getOverflowIcon()
  {
    ensureMenu();
    return this.mMenuView.getOverflowIcon();
  }
  
  public int getPopupTheme()
  {
    return this.mPopupTheme;
  }
  
  public CharSequence getSubtitle()
  {
    return this.mSubtitleText;
  }
  
  public CharSequence getTitle()
  {
    return this.mTitleText;
  }
  
  public int getTitleMarginBottom()
  {
    return this.mTitleMarginBottom;
  }
  
  public int getTitleMarginEnd()
  {
    return this.mTitleMarginEnd;
  }
  
  public int getTitleMarginStart()
  {
    return this.mTitleMarginStart;
  }
  
  public int getTitleMarginTop()
  {
    return this.mTitleMarginTop;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public DecorToolbar getWrapper()
  {
    if (this.mWrapper == null) {
      this.mWrapper = new ToolbarWidgetWrapper(this, true);
    }
    return this.mWrapper;
  }
  
  public boolean hasExpandedActionView()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mExpandedMenuPresenter != null)
    {
      bool1 = bool2;
      if (this.mExpandedMenuPresenter.mCurrentExpandedItem != null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean hideOverflowMenu()
  {
    if (this.mMenuView != null) {
      return this.mMenuView.hideOverflowMenu();
    }
    return false;
  }
  
  public void inflateMenu(@MenuRes int paramInt)
  {
    getMenuInflater().inflate(paramInt, getMenu());
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public boolean isOverflowMenuShowPending()
  {
    if (this.mMenuView != null) {
      return this.mMenuView.isOverflowMenuShowPending();
    }
    return false;
  }
  
  public boolean isOverflowMenuShowing()
  {
    if (this.mMenuView != null) {
      return this.mMenuView.isOverflowMenuShowing();
    }
    return false;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public boolean isTitleTruncated()
  {
    if (this.mTitleTextView == null) {
      return false;
    }
    Layout localLayout = this.mTitleTextView.getLayout();
    if (localLayout == null) {
      return false;
    }
    int j = localLayout.getLineCount();
    int i = 0;
    while (i < j)
    {
      if (localLayout.getEllipsisCount(i) > 0) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    removeCallbacks(this.mShowOverflowMenuRunnable);
  }
  
  public boolean onHoverEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if (i == 9) {
      this.mEatingHover = false;
    }
    if (!this.mEatingHover)
    {
      boolean bool = super.onHoverEvent(paramMotionEvent);
      if ((i == 9) && (!bool)) {
        break label57;
      }
    }
    for (;;)
    {
      if ((i == 10) || (i == 3)) {
        this.mEatingHover = false;
      }
      return true;
      label57:
      this.mEatingHover = true;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int k;
    int i2;
    int i4;
    int i1;
    int i3;
    int n;
    int i5;
    int[] arrayOfInt;
    int j;
    label93:
    label133:
    label173:
    label213:
    label317:
    label357:
    boolean bool;
    Object localObject1;
    int m;
    label484:
    Object localObject2;
    if (ViewCompat.getLayoutDirection(this) == 1)
    {
      k = 1;
      i2 = getWidth();
      i4 = getHeight();
      i1 = getPaddingLeft();
      i3 = getPaddingRight();
      n = getPaddingTop();
      i5 = getPaddingBottom();
      paramInt3 = i1;
      i = i2 - i3;
      arrayOfInt = this.mTempMargins;
      arrayOfInt[1] = 0;
      arrayOfInt[0] = 0;
      paramInt1 = ViewCompat.getMinimumHeight(this);
      if (paramInt1 < 0) {
        break label906;
      }
      j = Math.min(paramInt1, paramInt4 - paramInt2);
      paramInt1 = paramInt3;
      paramInt2 = i;
      if (shouldLayout(this.mNavButtonView))
      {
        if (k == 0) {
          break label912;
        }
        paramInt2 = layoutChildRight(this.mNavButtonView, i, arrayOfInt, j);
        paramInt1 = paramInt3;
      }
      paramInt3 = paramInt1;
      paramInt4 = paramInt2;
      if (shouldLayout(this.mCollapseButtonView))
      {
        if (k == 0) {
          break label933;
        }
        paramInt4 = layoutChildRight(this.mCollapseButtonView, paramInt2, arrayOfInt, j);
        paramInt3 = paramInt1;
      }
      paramInt2 = paramInt3;
      paramInt1 = paramInt4;
      if (shouldLayout(this.mMenuView))
      {
        if (k == 0) {
          break label954;
        }
        paramInt2 = layoutChildLeft(this.mMenuView, paramInt3, arrayOfInt, j);
        paramInt1 = paramInt4;
      }
      paramInt3 = getCurrentContentInsetLeft();
      paramInt4 = getCurrentContentInsetRight();
      arrayOfInt[0] = Math.max(0, paramInt3 - paramInt2);
      arrayOfInt[1] = Math.max(0, paramInt4 - (i2 - i3 - paramInt1));
      paramInt3 = Math.max(paramInt2, paramInt3);
      paramInt4 = Math.min(paramInt1, i2 - i3 - paramInt4);
      paramInt1 = paramInt3;
      paramInt2 = paramInt4;
      if (shouldLayout(this.mExpandedActionView))
      {
        if (k == 0) {
          break label975;
        }
        paramInt2 = layoutChildRight(this.mExpandedActionView, paramInt4, arrayOfInt, j);
        paramInt1 = paramInt3;
      }
      paramInt4 = paramInt1;
      i = paramInt2;
      if (shouldLayout(this.mLogoView))
      {
        if (k == 0) {
          break label996;
        }
        i = layoutChildRight(this.mLogoView, paramInt2, arrayOfInt, j);
        paramInt4 = paramInt1;
      }
      paramBoolean = shouldLayout(this.mTitleTextView);
      bool = shouldLayout(this.mSubtitleTextView);
      paramInt1 = 0;
      if (paramBoolean)
      {
        localObject1 = (LayoutParams)this.mTitleTextView.getLayoutParams();
        paramInt1 = ((LayoutParams)localObject1).topMargin + this.mTitleTextView.getMeasuredHeight() + ((LayoutParams)localObject1).bottomMargin + 0;
      }
      m = paramInt1;
      if (bool)
      {
        localObject1 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
        m = paramInt1 + (((LayoutParams)localObject1).topMargin + this.mSubtitleTextView.getMeasuredHeight() + ((LayoutParams)localObject1).bottomMargin);
      }
      if (!paramBoolean)
      {
        paramInt2 = paramInt4;
        paramInt1 = i;
        if (!bool) {}
      }
      else
      {
        if (!paramBoolean) {
          break label1017;
        }
        localObject1 = this.mTitleTextView;
        if (!bool) {
          break label1026;
        }
        localObject2 = this.mSubtitleTextView;
        label495:
        localObject1 = (LayoutParams)((View)localObject1).getLayoutParams();
        localObject2 = (LayoutParams)((View)localObject2).getLayoutParams();
        if ((!paramBoolean) || (this.mTitleTextView.getMeasuredWidth() <= 0)) {
          break label1035;
        }
        paramInt3 = 1;
        label532:
        switch (this.mGravity & 0x70)
        {
        case 16: 
        default: 
          paramInt2 = (i4 - n - i5 - m) / 2;
          if (paramInt2 < ((LayoutParams)localObject1).topMargin + this.mTitleMarginTop)
          {
            paramInt1 = ((LayoutParams)localObject1).topMargin + this.mTitleMarginTop;
            label611:
            paramInt1 = n + paramInt1;
            label616:
            if (k == 0) {
              break label1164;
            }
            if (paramInt3 == 0) {
              break label1159;
            }
          }
          break;
        }
      }
    }
    label906:
    label912:
    label933:
    label954:
    label975:
    label996:
    label1017:
    label1026:
    label1035:
    label1159:
    for (paramInt2 = this.mTitleMarginStart;; paramInt2 = 0)
    {
      paramInt2 -= arrayOfInt[1];
      i -= Math.max(0, paramInt2);
      arrayOfInt[1] = Math.max(0, -paramInt2);
      k = i;
      paramInt2 = i;
      m = paramInt1;
      if (paramBoolean)
      {
        localObject1 = (LayoutParams)this.mTitleTextView.getLayoutParams();
        k = i - this.mTitleTextView.getMeasuredWidth();
        m = paramInt1 + this.mTitleTextView.getMeasuredHeight();
        this.mTitleTextView.layout(k, paramInt1, i, m);
        k -= this.mTitleMarginEnd;
        m += ((LayoutParams)localObject1).bottomMargin;
      }
      n = paramInt2;
      if (bool)
      {
        localObject1 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
        paramInt1 = m + ((LayoutParams)localObject1).topMargin;
        paramInt2 = this.mSubtitleTextView.getMeasuredWidth();
        m = paramInt1 + this.mSubtitleTextView.getMeasuredHeight();
        this.mSubtitleTextView.layout(i - paramInt2, paramInt1, i, m);
        n = i - this.mTitleMarginEnd;
        paramInt1 = ((LayoutParams)localObject1).bottomMargin;
      }
      paramInt2 = paramInt4;
      paramInt1 = i;
      if (paramInt3 != 0)
      {
        paramInt1 = Math.min(k, n);
        paramInt2 = paramInt4;
      }
      addCustomViewsWithGravity(this.mTempViews, 3);
      paramInt4 = this.mTempViews.size();
      paramInt3 = 0;
      while (paramInt3 < paramInt4)
      {
        paramInt2 = layoutChildLeft((View)this.mTempViews.get(paramInt3), paramInt2, arrayOfInt, j);
        paramInt3 += 1;
      }
      k = 0;
      break;
      j = 0;
      break label93;
      paramInt1 = layoutChildLeft(this.mNavButtonView, i1, arrayOfInt, j);
      paramInt2 = i;
      break label133;
      paramInt3 = layoutChildLeft(this.mCollapseButtonView, paramInt1, arrayOfInt, j);
      paramInt4 = paramInt2;
      break label173;
      paramInt1 = layoutChildRight(this.mMenuView, paramInt4, arrayOfInt, j);
      paramInt2 = paramInt3;
      break label213;
      paramInt1 = layoutChildLeft(this.mExpandedActionView, paramInt3, arrayOfInt, j);
      paramInt2 = paramInt4;
      break label317;
      paramInt4 = layoutChildLeft(this.mLogoView, paramInt1, arrayOfInt, j);
      i = paramInt2;
      break label357;
      localObject1 = this.mSubtitleTextView;
      break label484;
      localObject2 = this.mTitleTextView;
      break label495;
      if ((bool) && (this.mSubtitleTextView.getMeasuredWidth() > 0))
      {
        paramInt3 = 1;
        break label532;
      }
      paramInt3 = 0;
      break label532;
      paramInt1 = getPaddingTop() + ((LayoutParams)localObject1).topMargin + this.mTitleMarginTop;
      break label616;
      m = i4 - i5 - m - paramInt2 - n;
      paramInt1 = paramInt2;
      if (m >= ((LayoutParams)localObject1).bottomMargin + this.mTitleMarginBottom) {
        break label611;
      }
      paramInt1 = Math.max(0, paramInt2 - (((LayoutParams)localObject2).bottomMargin + this.mTitleMarginBottom - m));
      break label611;
      paramInt1 = i4 - i5 - ((LayoutParams)localObject2).bottomMargin - this.mTitleMarginBottom - m;
      break label616;
    }
    label1164:
    if (paramInt3 != 0) {}
    for (paramInt2 = this.mTitleMarginStart;; paramInt2 = 0)
    {
      k = paramInt2 - arrayOfInt[0];
      paramInt2 = paramInt4 + Math.max(0, k);
      arrayOfInt[0] = Math.max(0, -k);
      paramInt4 = paramInt2;
      k = paramInt2;
      m = paramInt1;
      if (paramBoolean)
      {
        localObject1 = (LayoutParams)this.mTitleTextView.getLayoutParams();
        paramInt4 = paramInt2 + this.mTitleTextView.getMeasuredWidth();
        m = paramInt1 + this.mTitleTextView.getMeasuredHeight();
        this.mTitleTextView.layout(paramInt2, paramInt1, paramInt4, m);
        paramInt4 += this.mTitleMarginEnd;
        m += ((LayoutParams)localObject1).bottomMargin;
      }
      if (bool)
      {
        localObject1 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
        paramInt1 = m + ((LayoutParams)localObject1).topMargin;
        k = paramInt2 + this.mSubtitleTextView.getMeasuredWidth();
        m = paramInt1 + this.mSubtitleTextView.getMeasuredHeight();
        this.mSubtitleTextView.layout(paramInt2, paramInt1, k, m);
        k += this.mTitleMarginEnd;
        paramInt1 = ((LayoutParams)localObject1).bottomMargin;
      }
      paramInt1 = i;
      if (paramInt3 == 0) {
        break;
      }
      paramInt2 = Math.max(paramInt4, k);
      paramInt1 = i;
      break;
    }
    addCustomViewsWithGravity(this.mTempViews, 5);
    int i = this.mTempViews.size();
    paramInt4 = 0;
    paramInt3 = paramInt1;
    paramInt1 = paramInt4;
    while (paramInt1 < i)
    {
      paramInt3 = layoutChildRight((View)this.mTempViews.get(paramInt1), paramInt3, arrayOfInt, j);
      paramInt1 += 1;
    }
    addCustomViewsWithGravity(this.mTempViews, 1);
    paramInt1 = getViewListMeasuredWidth(this.mTempViews, arrayOfInt);
    paramInt4 = i1 + (i2 - i1 - i3) / 2 - paramInt1 / 2;
    i = paramInt4 + paramInt1;
    if (paramInt4 < paramInt2) {
      paramInt1 = paramInt2;
    }
    for (;;)
    {
      paramInt3 = this.mTempViews.size();
      paramInt2 = 0;
      while (paramInt2 < paramInt3)
      {
        paramInt1 = layoutChildLeft((View)this.mTempViews.get(paramInt2), paramInt1, arrayOfInt, j);
        paramInt2 += 1;
      }
      paramInt1 = paramInt4;
      if (i > paramInt3) {
        paramInt1 = paramInt4 - (i - paramInt3);
      }
    }
    this.mTempViews.clear();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int m = 0;
    int k = 0;
    int[] arrayOfInt = this.mTempMargins;
    if (ViewUtils.isLayoutRtl(this)) {
      i2 = 1;
    }
    int n;
    for (int i1 = 0;; i1 = 1)
    {
      n = 0;
      if (shouldLayout(this.mNavButtonView))
      {
        measureChildConstrained(this.mNavButtonView, paramInt1, 0, paramInt2, 0, this.mMaxButtonHeight);
        n = this.mNavButtonView.getMeasuredWidth() + getHorizontalMargins(this.mNavButtonView);
        m = Math.max(0, this.mNavButtonView.getMeasuredHeight() + getVerticalMargins(this.mNavButtonView));
        k = ViewUtils.combineMeasuredStates(0, ViewCompat.getMeasuredState(this.mNavButtonView));
      }
      i = k;
      j = m;
      if (shouldLayout(this.mCollapseButtonView))
      {
        measureChildConstrained(this.mCollapseButtonView, paramInt1, 0, paramInt2, 0, this.mMaxButtonHeight);
        n = this.mCollapseButtonView.getMeasuredWidth() + getHorizontalMargins(this.mCollapseButtonView);
        j = Math.max(m, this.mCollapseButtonView.getMeasuredHeight() + getVerticalMargins(this.mCollapseButtonView));
        i = ViewUtils.combineMeasuredStates(k, ViewCompat.getMeasuredState(this.mCollapseButtonView));
      }
      k = getCurrentContentInsetStart();
      i3 = Math.max(k, n) + 0;
      arrayOfInt[i2] = Math.max(0, k - n);
      n = 0;
      k = i;
      m = j;
      if (shouldLayout(this.mMenuView))
      {
        measureChildConstrained(this.mMenuView, paramInt1, i3, paramInt2, 0, this.mMaxButtonHeight);
        n = this.mMenuView.getMeasuredWidth() + getHorizontalMargins(this.mMenuView);
        m = Math.max(j, this.mMenuView.getMeasuredHeight() + getVerticalMargins(this.mMenuView));
        k = ViewUtils.combineMeasuredStates(i, ViewCompat.getMeasuredState(this.mMenuView));
      }
      i = getCurrentContentInsetEnd();
      i2 = i3 + Math.max(i, n);
      arrayOfInt[i1] = Math.max(0, i - n);
      i1 = i2;
      i = k;
      j = m;
      if (shouldLayout(this.mExpandedActionView))
      {
        i1 = i2 + measureChildCollapseMargins(this.mExpandedActionView, paramInt1, i2, paramInt2, 0, arrayOfInt);
        j = Math.max(m, this.mExpandedActionView.getMeasuredHeight() + getVerticalMargins(this.mExpandedActionView));
        i = ViewUtils.combineMeasuredStates(k, ViewCompat.getMeasuredState(this.mExpandedActionView));
      }
      k = i1;
      m = i;
      n = j;
      if (shouldLayout(this.mLogoView))
      {
        k = i1 + measureChildCollapseMargins(this.mLogoView, paramInt1, i1, paramInt2, 0, arrayOfInt);
        n = Math.max(j, this.mLogoView.getMeasuredHeight() + getVerticalMargins(this.mLogoView));
        m = ViewUtils.combineMeasuredStates(i, ViewCompat.getMeasuredState(this.mLogoView));
      }
      i3 = getChildCount();
      j = 0;
      i1 = n;
      i = m;
      n = k;
      while (j < i3)
      {
        View localView = getChildAt(j);
        i2 = n;
        m = i;
        k = i1;
        if (((LayoutParams)localView.getLayoutParams()).mViewType == 0)
        {
          i2 = n;
          m = i;
          k = i1;
          if (shouldLayout(localView))
          {
            i2 = n + measureChildCollapseMargins(localView, paramInt1, n, paramInt2, 0, arrayOfInt);
            k = Math.max(i1, localView.getMeasuredHeight() + getVerticalMargins(localView));
            m = ViewUtils.combineMeasuredStates(i, ViewCompat.getMeasuredState(localView));
          }
        }
        j += 1;
        n = i2;
        i = m;
        i1 = k;
      }
      i2 = 0;
    }
    m = 0;
    k = 0;
    int i4 = this.mTitleMarginTop + this.mTitleMarginBottom;
    int i5 = this.mTitleMarginStart + this.mTitleMarginEnd;
    int j = i;
    if (shouldLayout(this.mTitleTextView))
    {
      measureChildCollapseMargins(this.mTitleTextView, paramInt1, n + i5, paramInt2, i4, arrayOfInt);
      m = this.mTitleTextView.getMeasuredWidth() + getHorizontalMargins(this.mTitleTextView);
      k = this.mTitleTextView.getMeasuredHeight() + getVerticalMargins(this.mTitleTextView);
      j = ViewUtils.combineMeasuredStates(i, ViewCompat.getMeasuredState(this.mTitleTextView));
    }
    int i2 = j;
    int i3 = k;
    int i = m;
    if (shouldLayout(this.mSubtitleTextView))
    {
      i = Math.max(m, measureChildCollapseMargins(this.mSubtitleTextView, paramInt1, n + i5, paramInt2, k + i4, arrayOfInt));
      i3 = k + (this.mSubtitleTextView.getMeasuredHeight() + getVerticalMargins(this.mSubtitleTextView));
      i2 = ViewUtils.combineMeasuredStates(j, ViewCompat.getMeasuredState(this.mSubtitleTextView));
    }
    j = Math.max(i1, i3);
    i1 = getPaddingLeft();
    i3 = getPaddingRight();
    k = getPaddingTop();
    m = getPaddingBottom();
    i = ViewCompat.resolveSizeAndState(Math.max(n + i + (i1 + i3), getSuggestedMinimumWidth()), paramInt1, 0xFF000000 & i2);
    paramInt1 = ViewCompat.resolveSizeAndState(Math.max(j + (k + m), getSuggestedMinimumHeight()), paramInt2, i2 << 16);
    if (shouldCollapse()) {
      paramInt1 = 0;
    }
    setMeasuredDimension(i, paramInt1);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    if (this.mMenuView != null) {}
    for (paramParcelable = this.mMenuView.peekMenu();; paramParcelable = null)
    {
      if ((localSavedState.expandedMenuItemId != 0) && (this.mExpandedMenuPresenter != null) && (paramParcelable != null))
      {
        paramParcelable = paramParcelable.findItem(localSavedState.expandedMenuItemId);
        if (paramParcelable != null) {
          MenuItemCompat.expandActionView(paramParcelable);
        }
      }
      if (localSavedState.isOverflowOpen) {
        postShowOverflowMenu();
      }
      return;
    }
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    boolean bool = true;
    if (Build.VERSION.SDK_INT >= 17) {
      super.onRtlPropertiesChanged(paramInt);
    }
    ensureContentInsets();
    RtlSpacingHelper localRtlSpacingHelper = this.mContentInsets;
    if (paramInt == 1) {}
    for (;;)
    {
      localRtlSpacingHelper.setDirection(bool);
      return;
      bool = false;
    }
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    if ((this.mExpandedMenuPresenter != null) && (this.mExpandedMenuPresenter.mCurrentExpandedItem != null)) {
      localSavedState.expandedMenuItemId = this.mExpandedMenuPresenter.mCurrentExpandedItem.getItemId();
    }
    localSavedState.isOverflowOpen = isOverflowMenuShowing();
    return localSavedState;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if (i == 0) {
      this.mEatingTouch = false;
    }
    if (!this.mEatingTouch)
    {
      boolean bool = super.onTouchEvent(paramMotionEvent);
      if ((i == 0) && (!bool)) {
        break label52;
      }
    }
    for (;;)
    {
      if ((i == 1) || (i == 3)) {
        this.mEatingTouch = false;
      }
      return true;
      label52:
      this.mEatingTouch = true;
    }
  }
  
  void removeChildrenForExpandedActionView()
  {
    int i = getChildCount() - 1;
    while (i >= 0)
    {
      View localView = getChildAt(i);
      if ((((LayoutParams)localView.getLayoutParams()).mViewType != 2) && (localView != this.mMenuView))
      {
        removeViewAt(i);
        this.mHiddenViews.add(localView);
      }
      i -= 1;
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public void setCollapsible(boolean paramBoolean)
  {
    this.mCollapsible = paramBoolean;
    requestLayout();
  }
  
  public void setContentInsetEndWithActions(int paramInt)
  {
    int i = paramInt;
    if (paramInt < 0) {
      i = Integer.MIN_VALUE;
    }
    if (i != this.mContentInsetEndWithActions)
    {
      this.mContentInsetEndWithActions = i;
      if (getNavigationIcon() != null) {
        requestLayout();
      }
    }
  }
  
  public void setContentInsetStartWithNavigation(int paramInt)
  {
    int i = paramInt;
    if (paramInt < 0) {
      i = Integer.MIN_VALUE;
    }
    if (i != this.mContentInsetStartWithNavigation)
    {
      this.mContentInsetStartWithNavigation = i;
      if (getNavigationIcon() != null) {
        requestLayout();
      }
    }
  }
  
  public void setContentInsetsAbsolute(int paramInt1, int paramInt2)
  {
    ensureContentInsets();
    this.mContentInsets.setAbsolute(paramInt1, paramInt2);
  }
  
  public void setContentInsetsRelative(int paramInt1, int paramInt2)
  {
    ensureContentInsets();
    this.mContentInsets.setRelative(paramInt1, paramInt2);
  }
  
  public void setLogo(@DrawableRes int paramInt)
  {
    setLogo(AppCompatResources.getDrawable(getContext(), paramInt));
  }
  
  public void setLogo(Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      ensureLogoView();
      if (!isChildOrHidden(this.mLogoView)) {
        addSystemView(this.mLogoView, true);
      }
    }
    for (;;)
    {
      if (this.mLogoView != null) {
        this.mLogoView.setImageDrawable(paramDrawable);
      }
      return;
      if ((this.mLogoView != null) && (isChildOrHidden(this.mLogoView)))
      {
        removeView(this.mLogoView);
        this.mHiddenViews.remove(this.mLogoView);
      }
    }
  }
  
  public void setLogoDescription(@StringRes int paramInt)
  {
    setLogoDescription(getContext().getText(paramInt));
  }
  
  public void setLogoDescription(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence)) {
      ensureLogoView();
    }
    if (this.mLogoView != null) {
      this.mLogoView.setContentDescription(paramCharSequence);
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public void setMenu(MenuBuilder paramMenuBuilder, ActionMenuPresenter paramActionMenuPresenter)
  {
    if ((paramMenuBuilder == null) && (this.mMenuView == null)) {
      return;
    }
    ensureMenuView();
    MenuBuilder localMenuBuilder = this.mMenuView.peekMenu();
    if (localMenuBuilder == paramMenuBuilder) {
      return;
    }
    if (localMenuBuilder != null)
    {
      localMenuBuilder.removeMenuPresenter(this.mOuterActionMenuPresenter);
      localMenuBuilder.removeMenuPresenter(this.mExpandedMenuPresenter);
    }
    if (this.mExpandedMenuPresenter == null) {
      this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
    }
    paramActionMenuPresenter.setExpandedActionViewsExclusive(true);
    if (paramMenuBuilder != null)
    {
      paramMenuBuilder.addMenuPresenter(paramActionMenuPresenter, this.mPopupContext);
      paramMenuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
    }
    for (;;)
    {
      this.mMenuView.setPopupTheme(this.mPopupTheme);
      this.mMenuView.setPresenter(paramActionMenuPresenter);
      this.mOuterActionMenuPresenter = paramActionMenuPresenter;
      return;
      paramActionMenuPresenter.initForMenu(this.mPopupContext, null);
      this.mExpandedMenuPresenter.initForMenu(this.mPopupContext, null);
      paramActionMenuPresenter.updateMenuView(true);
      this.mExpandedMenuPresenter.updateMenuView(true);
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public void setMenuCallbacks(MenuPresenter.Callback paramCallback, MenuBuilder.Callback paramCallback1)
  {
    this.mActionMenuPresenterCallback = paramCallback;
    this.mMenuBuilderCallback = paramCallback1;
    if (this.mMenuView != null) {
      this.mMenuView.setMenuCallbacks(paramCallback, paramCallback1);
    }
  }
  
  public void setNavigationContentDescription(@StringRes int paramInt)
  {
    if (paramInt != 0) {}
    for (CharSequence localCharSequence = getContext().getText(paramInt);; localCharSequence = null)
    {
      setNavigationContentDescription(localCharSequence);
      return;
    }
  }
  
  public void setNavigationContentDescription(@Nullable CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence)) {
      ensureNavButtonView();
    }
    if (this.mNavButtonView != null) {
      this.mNavButtonView.setContentDescription(paramCharSequence);
    }
  }
  
  public void setNavigationIcon(@DrawableRes int paramInt)
  {
    setNavigationIcon(AppCompatResources.getDrawable(getContext(), paramInt));
  }
  
  public void setNavigationIcon(@Nullable Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      ensureNavButtonView();
      if (!isChildOrHidden(this.mNavButtonView)) {
        addSystemView(this.mNavButtonView, true);
      }
    }
    for (;;)
    {
      if (this.mNavButtonView != null) {
        this.mNavButtonView.setImageDrawable(paramDrawable);
      }
      return;
      if ((this.mNavButtonView != null) && (isChildOrHidden(this.mNavButtonView)))
      {
        removeView(this.mNavButtonView);
        this.mHiddenViews.remove(this.mNavButtonView);
      }
    }
  }
  
  public void setNavigationOnClickListener(View.OnClickListener paramOnClickListener)
  {
    ensureNavButtonView();
    this.mNavButtonView.setOnClickListener(paramOnClickListener);
  }
  
  public void setOnMenuItemClickListener(OnMenuItemClickListener paramOnMenuItemClickListener)
  {
    this.mOnMenuItemClickListener = paramOnMenuItemClickListener;
  }
  
  public void setOverflowIcon(@Nullable Drawable paramDrawable)
  {
    ensureMenu();
    this.mMenuView.setOverflowIcon(paramDrawable);
  }
  
  public void setPopupTheme(@StyleRes int paramInt)
  {
    if (this.mPopupTheme != paramInt)
    {
      this.mPopupTheme = paramInt;
      if (paramInt == 0) {
        this.mPopupContext = getContext();
      }
    }
    else
    {
      return;
    }
    this.mPopupContext = new ContextThemeWrapper(getContext(), paramInt);
  }
  
  public void setSubtitle(@StringRes int paramInt)
  {
    setSubtitle(getContext().getText(paramInt));
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence))
    {
      if (this.mSubtitleTextView == null)
      {
        Context localContext = getContext();
        this.mSubtitleTextView = new AppCompatTextView(localContext);
        this.mSubtitleTextView.setSingleLine();
        this.mSubtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        if (this.mSubtitleTextAppearance != 0) {
          this.mSubtitleTextView.setTextAppearance(localContext, this.mSubtitleTextAppearance);
        }
        if (this.mSubtitleTextColor != 0) {
          this.mSubtitleTextView.setTextColor(this.mSubtitleTextColor);
        }
      }
      if (!isChildOrHidden(this.mSubtitleTextView)) {
        addSystemView(this.mSubtitleTextView, true);
      }
    }
    for (;;)
    {
      if (this.mSubtitleTextView != null) {
        this.mSubtitleTextView.setText(paramCharSequence);
      }
      this.mSubtitleText = paramCharSequence;
      return;
      if ((this.mSubtitleTextView != null) && (isChildOrHidden(this.mSubtitleTextView)))
      {
        removeView(this.mSubtitleTextView);
        this.mHiddenViews.remove(this.mSubtitleTextView);
      }
    }
  }
  
  public void setSubtitleTextAppearance(Context paramContext, @StyleRes int paramInt)
  {
    this.mSubtitleTextAppearance = paramInt;
    if (this.mSubtitleTextView != null) {
      this.mSubtitleTextView.setTextAppearance(paramContext, paramInt);
    }
  }
  
  public void setSubtitleTextColor(@ColorInt int paramInt)
  {
    this.mSubtitleTextColor = paramInt;
    if (this.mSubtitleTextView != null) {
      this.mSubtitleTextView.setTextColor(paramInt);
    }
  }
  
  public void setTitle(@StringRes int paramInt)
  {
    setTitle(getContext().getText(paramInt));
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence))
    {
      if (this.mTitleTextView == null)
      {
        Context localContext = getContext();
        this.mTitleTextView = new AppCompatTextView(localContext);
        this.mTitleTextView.setSingleLine();
        this.mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        if (this.mTitleTextAppearance != 0) {
          this.mTitleTextView.setTextAppearance(localContext, this.mTitleTextAppearance);
        }
        if (this.mTitleTextColor != 0) {
          this.mTitleTextView.setTextColor(this.mTitleTextColor);
        }
      }
      if (!isChildOrHidden(this.mTitleTextView)) {
        addSystemView(this.mTitleTextView, true);
      }
    }
    for (;;)
    {
      if (this.mTitleTextView != null) {
        this.mTitleTextView.setText(paramCharSequence);
      }
      this.mTitleText = paramCharSequence;
      return;
      if ((this.mTitleTextView != null) && (isChildOrHidden(this.mTitleTextView)))
      {
        removeView(this.mTitleTextView);
        this.mHiddenViews.remove(this.mTitleTextView);
      }
    }
  }
  
  public void setTitleMargin(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mTitleMarginStart = paramInt1;
    this.mTitleMarginTop = paramInt2;
    this.mTitleMarginEnd = paramInt3;
    this.mTitleMarginBottom = paramInt4;
    requestLayout();
  }
  
  public void setTitleMarginBottom(int paramInt)
  {
    this.mTitleMarginBottom = paramInt;
    requestLayout();
  }
  
  public void setTitleMarginEnd(int paramInt)
  {
    this.mTitleMarginEnd = paramInt;
    requestLayout();
  }
  
  public void setTitleMarginStart(int paramInt)
  {
    this.mTitleMarginStart = paramInt;
    requestLayout();
  }
  
  public void setTitleMarginTop(int paramInt)
  {
    this.mTitleMarginTop = paramInt;
    requestLayout();
  }
  
  public void setTitleTextAppearance(Context paramContext, @StyleRes int paramInt)
  {
    this.mTitleTextAppearance = paramInt;
    if (this.mTitleTextView != null) {
      this.mTitleTextView.setTextAppearance(paramContext, paramInt);
    }
  }
  
  public void setTitleTextColor(@ColorInt int paramInt)
  {
    this.mTitleTextColor = paramInt;
    if (this.mTitleTextView != null) {
      this.mTitleTextView.setTextColor(paramInt);
    }
  }
  
  public boolean showOverflowMenu()
  {
    if (this.mMenuView != null) {
      return this.mMenuView.showOverflowMenu();
    }
    return false;
  }
  
  private class ExpandedActionViewMenuPresenter
    implements MenuPresenter
  {
    MenuItemImpl mCurrentExpandedItem;
    MenuBuilder mMenu;
    
    ExpandedActionViewMenuPresenter() {}
    
    public boolean collapseItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl)
    {
      if ((Toolbar.this.mExpandedActionView instanceof CollapsibleActionView)) {
        ((CollapsibleActionView)Toolbar.this.mExpandedActionView).onActionViewCollapsed();
      }
      Toolbar.this.removeView(Toolbar.this.mExpandedActionView);
      Toolbar.this.removeView(Toolbar.this.mCollapseButtonView);
      Toolbar.this.mExpandedActionView = null;
      Toolbar.this.addChildrenForExpandedActionView();
      this.mCurrentExpandedItem = null;
      Toolbar.this.requestLayout();
      paramMenuItemImpl.setActionViewExpanded(false);
      return true;
    }
    
    public boolean expandItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl)
    {
      Toolbar.this.ensureCollapseButtonView();
      if (Toolbar.this.mCollapseButtonView.getParent() != Toolbar.this) {
        Toolbar.this.addView(Toolbar.this.mCollapseButtonView);
      }
      Toolbar.this.mExpandedActionView = paramMenuItemImpl.getActionView();
      this.mCurrentExpandedItem = paramMenuItemImpl;
      if (Toolbar.this.mExpandedActionView.getParent() != Toolbar.this)
      {
        paramMenuBuilder = Toolbar.this.generateDefaultLayoutParams();
        paramMenuBuilder.gravity = (Toolbar.this.mButtonGravity & 0x70 | 0x800003);
        paramMenuBuilder.mViewType = 2;
        Toolbar.this.mExpandedActionView.setLayoutParams(paramMenuBuilder);
        Toolbar.this.addView(Toolbar.this.mExpandedActionView);
      }
      Toolbar.this.removeChildrenForExpandedActionView();
      Toolbar.this.requestLayout();
      paramMenuItemImpl.setActionViewExpanded(true);
      if ((Toolbar.this.mExpandedActionView instanceof CollapsibleActionView)) {
        ((CollapsibleActionView)Toolbar.this.mExpandedActionView).onActionViewExpanded();
      }
      return true;
    }
    
    public boolean flagActionItems()
    {
      return false;
    }
    
    public int getId()
    {
      return 0;
    }
    
    public MenuView getMenuView(ViewGroup paramViewGroup)
    {
      return null;
    }
    
    public void initForMenu(Context paramContext, MenuBuilder paramMenuBuilder)
    {
      if ((this.mMenu != null) && (this.mCurrentExpandedItem != null)) {
        this.mMenu.collapseItemActionView(this.mCurrentExpandedItem);
      }
      this.mMenu = paramMenuBuilder;
    }
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {}
    
    public void onRestoreInstanceState(Parcelable paramParcelable) {}
    
    public Parcelable onSaveInstanceState()
    {
      return null;
    }
    
    public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder)
    {
      return false;
    }
    
    public void setCallback(MenuPresenter.Callback paramCallback) {}
    
    public void updateMenuView(boolean paramBoolean)
    {
      int k;
      int j;
      int m;
      int i;
      if (this.mCurrentExpandedItem != null)
      {
        k = 0;
        j = k;
        if (this.mMenu != null)
        {
          m = this.mMenu.size();
          i = 0;
        }
      }
      for (;;)
      {
        j = k;
        if (i < m)
        {
          if (this.mMenu.getItem(i) == this.mCurrentExpandedItem) {
            j = 1;
          }
        }
        else
        {
          if (j == 0) {
            collapseItemActionView(this.mMenu, this.mCurrentExpandedItem);
          }
          return;
        }
        i += 1;
      }
    }
  }
  
  public static class LayoutParams
    extends ActionBar.LayoutParams
  {
    static final int CUSTOM = 0;
    static final int EXPANDED = 2;
    static final int SYSTEM = 1;
    int mViewType = 0;
    
    public LayoutParams(int paramInt)
    {
      this(-2, -1, paramInt);
    }
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.gravity = 8388627;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3)
    {
      super(paramInt2);
      this.gravity = paramInt3;
    }
    
    public LayoutParams(@NonNull Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ActionBar.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.mViewType = paramLayoutParams.mViewType;
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
      copyMarginsFromCompat(paramMarginLayoutParams);
    }
    
    void copyMarginsFromCompat(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      this.leftMargin = paramMarginLayoutParams.leftMargin;
      this.topMargin = paramMarginLayoutParams.topMargin;
      this.rightMargin = paramMarginLayoutParams.rightMargin;
      this.bottomMargin = paramMarginLayoutParams.bottomMargin;
    }
  }
  
  public static abstract interface OnMenuItemClickListener
  {
    public abstract boolean onMenuItemClick(MenuItem paramMenuItem);
  }
  
  public static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public Toolbar.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new Toolbar.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public Toolbar.SavedState[] newArray(int paramAnonymousInt)
      {
        return new Toolbar.SavedState[paramAnonymousInt];
      }
    });
    int expandedMenuItemId;
    boolean isOverflowOpen;
    
    public SavedState(Parcel paramParcel)
    {
      this(paramParcel, null);
    }
    
    public SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      this.expandedMenuItemId = paramParcel.readInt();
      if (paramParcel.readInt() != 0) {
        bool = true;
      }
      this.isOverflowOpen = bool;
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.expandedMenuItemId);
      if (this.isOverflowOpen) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        return;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\android\support\v7\widget\Toolbar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */