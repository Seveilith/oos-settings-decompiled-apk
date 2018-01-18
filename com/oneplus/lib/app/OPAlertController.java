package com.oneplus.lib.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.dimen;
import com.oneplus.commonctrl.R.id;
import com.oneplus.commonctrl.R.layout;
import com.oneplus.commonctrl.R.styleable;
import java.lang.ref.WeakReference;

public class OPAlertController
{
  private ListAdapter mAdapter;
  private int mAlertDialogLayout;
  private final View.OnClickListener mButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if ((paramAnonymousView == OPAlertController.-get4(OPAlertController.this)) && (OPAlertController.-get5(OPAlertController.this) != null)) {
        paramAnonymousView = Message.obtain(OPAlertController.-get5(OPAlertController.this));
      }
      for (;;)
      {
        if (paramAnonymousView != null) {
          paramAnonymousView.sendToTarget();
        }
        OPAlertController.-get8(OPAlertController.this).obtainMessage(1, OPAlertController.-get7(OPAlertController.this)).sendToTarget();
        return;
        if ((paramAnonymousView == OPAlertController.-get0(OPAlertController.this)) && (OPAlertController.-get1(OPAlertController.this) != null)) {
          paramAnonymousView = Message.obtain(OPAlertController.-get1(OPAlertController.this));
        } else if ((paramAnonymousView == OPAlertController.-get2(OPAlertController.this)) && (OPAlertController.-get3(OPAlertController.this) != null)) {
          paramAnonymousView = Message.obtain(OPAlertController.-get3(OPAlertController.this));
        } else {
          paramAnonymousView = null;
        }
      }
    }
  };
  private Button mButtonNegative;
  private Message mButtonNegativeMessage;
  private CharSequence mButtonNegativeText;
  private Button mButtonNeutral;
  private Message mButtonNeutralMessage;
  private CharSequence mButtonNeutralText;
  private Button mButtonPositive;
  private Message mButtonPositiveMessage;
  private CharSequence mButtonPositiveText;
  private int mCheckedItem = -1;
  private final Context mContext;
  private View mCustomTitleView;
  private final DialogInterface mDialogInterface;
  private boolean mForceInverseBackground;
  private Handler mHandler;
  private Drawable mIcon;
  private int mIconId = 0;
  private ImageView mIconView;
  private int mListItemLayout;
  private int mListLayout;
  private ListView mListView;
  private CharSequence mMessage;
  private TextView mMessageView;
  private int mMultiChoiceItemLayout;
  private ScrollView mScrollView;
  private int mSingleChoiceItemLayout;
  private CharSequence mTitle;
  private TextView mTitleView;
  private View mView;
  private int mViewLayoutResId;
  private int mViewSpacingBottom;
  private int mViewSpacingLeft;
  private int mViewSpacingRight;
  private boolean mViewSpacingSpecified = false;
  private int mViewSpacingTop;
  private final Window mWindow;
  
  public OPAlertController(Context paramContext, DialogInterface paramDialogInterface, Window paramWindow)
  {
    this.mContext = paramContext;
    this.mDialogInterface = paramDialogInterface;
    this.mWindow = paramWindow;
    this.mHandler = new ButtonHandler(paramDialogInterface);
    this.mAlertDialogLayout = R.layout.op_alert_dialog_material;
    this.mListLayout = R.layout.op_select_dialog_material;
    this.mMultiChoiceItemLayout = R.layout.op_select_dialog_multichoice_material;
    this.mSingleChoiceItemLayout = R.layout.op_select_dialog_singlechoice_material;
    this.mListItemLayout = R.layout.op_select_dialog_item_material;
    paramContext = paramContext.obtainStyledAttributes(null, R.styleable.OPAlertDialog, R.attr.OPAlertDialogStyle, 0);
    this.mAlertDialogLayout = paramContext.getResourceId(R.styleable.OPAlertDialog_android_layout, R.layout.op_alert_dialog_material);
    this.mListLayout = paramContext.getResourceId(R.styleable.OPAlertDialog_op_listLayout, R.layout.op_select_dialog_material);
    this.mMultiChoiceItemLayout = paramContext.getResourceId(R.styleable.OPAlertDialog_op_multiChoiceItemLayout, R.layout.op_select_dialog_multichoice_material);
    this.mSingleChoiceItemLayout = paramContext.getResourceId(R.styleable.OPAlertDialog_op_singleChoiceItemLayout, R.layout.op_select_dialog_singlechoice_material);
    this.mListItemLayout = paramContext.getResourceId(R.styleable.OPAlertDialog_op_listItemLayout, R.layout.op_select_dialog_item_material);
    paramContext.recycle();
  }
  
  static boolean canTextInput(View paramView)
  {
    if (paramView.onCheckIsTextEditor()) {
      return true;
    }
    if (!(paramView instanceof ViewGroup)) {
      return false;
    }
    paramView = (ViewGroup)paramView;
    int i = paramView.getChildCount();
    while (i > 0)
    {
      int j = i - 1;
      i = j;
      if (canTextInput(paramView.getChildAt(j))) {
        return true;
      }
    }
    return false;
  }
  
  private void centerButton(Button paramButton)
  {
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)paramButton.getLayoutParams();
    localLayoutParams.gravity = 1;
    localLayoutParams.weight = 0.5F;
    paramButton.setLayoutParams(localLayoutParams);
  }
  
  private static void manageScrollIndicators(View paramView1, View paramView2, View paramView3)
  {
    int j = 0;
    if (paramView2 != null)
    {
      if (paramView1.canScrollVertically(-1))
      {
        i = 0;
        paramView2.setVisibility(i);
      }
    }
    else if (paramView3 != null) {
      if (!paramView1.canScrollVertically(1)) {
        break label48;
      }
    }
    label48:
    for (int i = j;; i = 4)
    {
      paramView3.setVisibility(i);
      return;
      i = 4;
      break;
    }
  }
  
  private ViewGroup resolvePanel(View paramView1, View paramView2)
  {
    if (paramView1 == null)
    {
      paramView1 = paramView2;
      if ((paramView2 instanceof ViewStub)) {
        paramView1 = ((ViewStub)paramView2).inflate();
      }
      return (ViewGroup)paramView1;
    }
    if (paramView2 != null)
    {
      ViewParent localViewParent = paramView2.getParent();
      if ((localViewParent instanceof ViewGroup)) {
        ((ViewGroup)localViewParent).removeView(paramView2);
      }
    }
    paramView2 = paramView1;
    if ((paramView1 instanceof ViewStub)) {
      paramView2 = ((ViewStub)paramView1).inflate();
    }
    return (ViewGroup)paramView2;
  }
  
  private void setBackground(TypedArray paramTypedArray, View paramView1, View paramView2, View paramView3, View paramView4, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    View[] arrayOfView = new View[4];
    boolean[] arrayOfBoolean = new boolean[4];
    Object localObject = null;
    boolean bool = false;
    int i = 0;
    if (paramBoolean1)
    {
      arrayOfView[0] = paramView1;
      arrayOfBoolean[0] = false;
      i = 1;
    }
    paramTypedArray = paramView2;
    if (paramView2.getVisibility() == 8) {
      paramTypedArray = null;
    }
    arrayOfView[i] = paramTypedArray;
    if (this.mListView != null) {}
    int k;
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      arrayOfBoolean[i] = paramBoolean1;
      j = i + 1;
      i = j;
      if (paramBoolean2)
      {
        arrayOfView[j] = paramView3;
        arrayOfBoolean[j] = this.mForceInverseBackground;
        i = j + 1;
      }
      if (paramBoolean3)
      {
        arrayOfView[i] = paramView4;
        arrayOfBoolean[i] = true;
      }
      i = 0;
      k = 0;
      paramTypedArray = (TypedArray)localObject;
      paramBoolean1 = bool;
      for (;;)
      {
        if (k >= arrayOfView.length) {
          break label236;
        }
        paramView1 = arrayOfView[k];
        if (paramView1 != null) {
          break;
        }
        k += 1;
      }
    }
    int j = i;
    if (paramTypedArray != null)
    {
      if (i != 0) {
        break label220;
      }
      if (!paramBoolean1) {
        break label217;
      }
    }
    label217:
    for (;;)
    {
      paramTypedArray.setBackgroundResource(0);
      j = 1;
      paramTypedArray = paramView1;
      paramBoolean1 = arrayOfBoolean[k];
      i = j;
      break;
    }
    label220:
    if (paramBoolean1) {}
    for (;;)
    {
      paramTypedArray.setBackgroundResource(0);
      break;
    }
    label236:
    if (paramTypedArray != null)
    {
      if (i == 0) {
        break label316;
      }
      if (!paramBoolean1) {
        break label313;
      }
      if (!paramBoolean3) {
        break label310;
      }
    }
    label310:
    label313:
    for (;;)
    {
      paramTypedArray.setBackgroundResource(0);
      paramTypedArray = this.mListView;
      if ((paramTypedArray != null) && (this.mAdapter != null))
      {
        paramTypedArray.setAdapter(this.mAdapter);
        i = this.mCheckedItem;
        if (i > -1)
        {
          paramTypedArray.setItemChecked(i, true);
          paramTypedArray.setSelection(i);
        }
      }
      return;
    }
    label316:
    if (paramBoolean1) {}
    for (;;)
    {
      paramTypedArray.setBackgroundResource(0);
      break;
    }
  }
  
  private void setupButtons(ViewGroup paramViewGroup)
  {
    int j = 1;
    int i = 0;
    this.mButtonPositive = ((Button)paramViewGroup.findViewById(16908313));
    this.mButtonPositive.setOnClickListener(this.mButtonHandler);
    if (TextUtils.isEmpty(this.mButtonPositiveText))
    {
      this.mButtonPositive.setVisibility(8);
      this.mButtonNegative = ((Button)paramViewGroup.findViewById(16908314));
      this.mButtonNegative.setOnClickListener(this.mButtonHandler);
      if (!TextUtils.isEmpty(this.mButtonNegativeText)) {
        break label200;
      }
      this.mButtonNegative.setVisibility(8);
      label92:
      this.mButtonNeutral = ((Button)paramViewGroup.findViewById(16908315));
      this.mButtonNeutral.setOnClickListener(this.mButtonHandler);
      if (!TextUtils.isEmpty(this.mButtonNeutralText)) {
        break label226;
      }
      this.mButtonNeutral.setVisibility(8);
      label136:
      if (shouldCenterSingleButton(this.mContext))
      {
        if (i != 1) {
          break label252;
        }
        centerButton(this.mButtonPositive);
      }
      label159:
      if (i == 0) {
        break label284;
      }
    }
    label200:
    label226:
    label252:
    label284:
    for (i = j;; i = 0)
    {
      if (i == 0) {
        paramViewGroup.setVisibility(8);
      }
      return;
      this.mButtonPositive.setText(this.mButtonPositiveText);
      this.mButtonPositive.setVisibility(0);
      i = 1;
      break;
      this.mButtonNegative.setText(this.mButtonNegativeText);
      this.mButtonNegative.setVisibility(0);
      i |= 0x2;
      break label92;
      this.mButtonNeutral.setText(this.mButtonNeutralText);
      this.mButtonNeutral.setVisibility(0);
      i |= 0x4;
      break label136;
      if (i == 2)
      {
        centerButton(this.mButtonNegative);
        break label159;
      }
      if (i != 4) {
        break label159;
      }
      centerButton(this.mButtonNeutral);
      break label159;
    }
  }
  
  private void setupContent(ViewGroup paramViewGroup)
  {
    this.mScrollView = ((ScrollView)paramViewGroup.findViewById(R.id.scrollView));
    this.mScrollView.setFocusable(false);
    this.mMessageView = ((TextView)paramViewGroup.findViewById(16908299));
    if (this.mMessageView == null) {
      return;
    }
    if (this.mMessage != null)
    {
      this.mMessageView.setText(this.mMessage);
      return;
    }
    this.mMessageView.setVisibility(8);
    this.mScrollView.removeView(this.mMessageView);
    if (this.mListView != null)
    {
      paramViewGroup = (ViewGroup)this.mScrollView.getParent();
      int i = paramViewGroup.indexOfChild(this.mScrollView);
      paramViewGroup.removeViewAt(i);
      paramViewGroup.addView(this.mListView, i, new ViewGroup.LayoutParams(-1, -1));
      return;
    }
    paramViewGroup.setVisibility(8);
  }
  
  private void setupCustomContent(ViewGroup paramViewGroup)
  {
    int i = 0;
    View localView;
    if (this.mView != null)
    {
      localView = this.mView;
      if (localView != null) {
        i = 1;
      }
      if ((i == 0) || (!canTextInput(localView))) {
        break label144;
      }
    }
    for (;;)
    {
      if (i == 0) {
        break label160;
      }
      FrameLayout localFrameLayout = (FrameLayout)this.mWindow.findViewById(16908331);
      localFrameLayout.addView(localView, new ViewGroup.LayoutParams(-1, -1));
      if (this.mViewSpacingSpecified) {
        localFrameLayout.setPadding(this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
      }
      if (this.mListView != null) {
        ((LinearLayout.LayoutParams)paramViewGroup.getLayoutParams()).weight = 0.0F;
      }
      return;
      if (this.mViewLayoutResId != 0)
      {
        localView = LayoutInflater.from(this.mContext).inflate(this.mViewLayoutResId, paramViewGroup, false);
        break;
      }
      localView = null;
      break;
      label144:
      this.mWindow.setFlags(131072, 131072);
    }
    label160:
    paramViewGroup.setVisibility(8);
  }
  
  private void setupDecor()
  {
    View localView1 = this.mWindow.getDecorView();
    final View localView2 = this.mWindow.findViewById(R.id.parentPanel);
    if ((localView2 != null) && (localView1 != null))
    {
      localView1.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
      {
        public WindowInsets onApplyWindowInsets(View paramAnonymousView, WindowInsets paramAnonymousWindowInsets)
        {
          if (paramAnonymousWindowInsets.isRound())
          {
            int i = OPAlertController.-get6(OPAlertController.this).getResources().getDimensionPixelOffset(R.dimen.alert_dialog_round_padding);
            localView2.setPadding(i, i, i, i);
          }
          return paramAnonymousWindowInsets.consumeSystemWindowInsets();
        }
      });
      localView1.setFitsSystemWindows(true);
      localView1.requestApplyInsets();
    }
  }
  
  private void setupTitle(ViewGroup paramViewGroup)
  {
    int i = 0;
    if (this.mCustomTitleView != null)
    {
      ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(-1, -2);
      paramViewGroup.addView(this.mCustomTitleView, 0, localLayoutParams);
      this.mWindow.findViewById(R.id.title_template).setVisibility(8);
      return;
    }
    this.mIconView = ((ImageView)this.mWindow.findViewById(16908294));
    if (TextUtils.isEmpty(this.mTitle)) {}
    while (i != 0)
    {
      this.mTitleView = ((TextView)this.mWindow.findViewById(R.id.alertTitle));
      this.mTitleView.setText(this.mTitle);
      if (this.mIconId != 0)
      {
        this.mIconView.setImageResource(this.mIconId);
        return;
        i = 1;
      }
      else
      {
        if (this.mIcon != null)
        {
          this.mIconView.setImageDrawable(this.mIcon);
          return;
        }
        this.mTitleView.setPadding(this.mIconView.getPaddingLeft(), this.mIconView.getPaddingTop(), this.mIconView.getPaddingRight(), this.mIconView.getPaddingBottom());
        this.mIconView.setVisibility(8);
        return;
      }
    }
    this.mWindow.findViewById(R.id.title_template).setVisibility(8);
    this.mIconView.setVisibility(8);
    paramViewGroup.setVisibility(8);
  }
  
  private void setupView()
  {
    Object localObject2 = this.mWindow.findViewById(R.id.parentPanel);
    Object localObject3 = ((View)localObject2).findViewById(R.id.topPanel);
    Object localObject4 = ((View)localObject2).findViewById(R.id.contentPanel);
    Object localObject1 = ((View)localObject2).findViewById(R.id.buttonPanel);
    localObject2 = (ViewGroup)((View)localObject2).findViewById(R.id.customPanel);
    setupCustomContent((ViewGroup)localObject2);
    View localView2 = ((ViewGroup)localObject2).findViewById(R.id.topPanel);
    View localView1 = ((ViewGroup)localObject2).findViewById(R.id.contentPanel);
    Object localObject5 = ((ViewGroup)localObject2).findViewById(R.id.buttonPanel);
    localObject3 = resolvePanel(localView2, (View)localObject3);
    localObject4 = resolvePanel(localView1, (View)localObject4);
    localObject5 = resolvePanel((View)localObject5, (View)localObject1);
    setupContent((ViewGroup)localObject4);
    setupButtons((ViewGroup)localObject5);
    setupTitle((ViewGroup)localObject3);
    boolean bool1;
    boolean bool2;
    label174:
    boolean bool3;
    label192:
    label260:
    int i;
    if (localObject2 != null) {
      if (((ViewGroup)localObject2).getVisibility() != 8)
      {
        bool1 = true;
        if (localObject3 == null) {
          break label346;
        }
        if (((ViewGroup)localObject3).getVisibility() == 8) {
          break label340;
        }
        bool2 = true;
        if (localObject5 == null) {
          break label358;
        }
        if (((ViewGroup)localObject5).getVisibility() == 8) {
          break label352;
        }
        bool3 = true;
        if ((!bool3) && (localObject4 != null))
        {
          localObject1 = ((ViewGroup)localObject4).findViewById(R.id.textSpacerNoButtons);
          if (localObject1 != null) {
            ((View)localObject1).setVisibility(0);
          }
        }
        if ((bool2) && (this.mScrollView != null)) {
          this.mScrollView.setClipToPadding(true);
        }
        if (!bool1)
        {
          if (this.mListView == null) {
            break label364;
          }
          localObject1 = this.mListView;
          if (localObject1 != null)
          {
            if (!bool2) {
              break label373;
            }
            i = 1;
            label272:
            if (!bool3) {
              break label378;
            }
          }
        }
      }
    }
    label340:
    label346:
    label352:
    label358:
    label364:
    label373:
    label378:
    for (int j = 2;; j = 0)
    {
      ((View)localObject1).setScrollIndicators(i | j, 3);
      localObject1 = this.mContext.obtainStyledAttributes(null, R.styleable.OPAlertDialog, 16842845, 0);
      setBackground((TypedArray)localObject1, (View)localObject3, (View)localObject4, (View)localObject2, (View)localObject5, bool2, bool1, bool3);
      ((TypedArray)localObject1).recycle();
      return;
      bool1 = false;
      break;
      bool1 = false;
      break;
      bool2 = false;
      break label174;
      bool2 = false;
      break label174;
      bool3 = false;
      break label192;
      bool3 = false;
      break label192;
      localObject1 = this.mScrollView;
      break label260;
      i = 0;
      break label272;
    }
  }
  
  private static boolean shouldCenterSingleButton(Context paramContext)
  {
    return false;
  }
  
  public Button getButton(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case -1: 
      return this.mButtonPositive;
    case -2: 
      return this.mButtonNegative;
    }
    return this.mButtonNeutral;
  }
  
  public int getIconAttributeResId(int paramInt)
  {
    TypedValue localTypedValue = new TypedValue();
    this.mContext.getTheme().resolveAttribute(paramInt, localTypedValue, true);
    return localTypedValue.resourceId;
  }
  
  public ListView getListView()
  {
    return this.mListView;
  }
  
  public void installContent()
  {
    this.mWindow.requestFeature(1);
    this.mWindow.setContentView(this.mAlertDialogLayout);
    setupView();
    setupDecor();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.mScrollView != null) {
      return this.mScrollView.executeKeyEvent(paramKeyEvent);
    }
    return false;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.mScrollView != null) {
      return this.mScrollView.executeKeyEvent(paramKeyEvent);
    }
    return false;
  }
  
  public void setButton(int paramInt, CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener, Message paramMessage)
  {
    Message localMessage = paramMessage;
    if (paramMessage == null)
    {
      localMessage = paramMessage;
      if (paramOnClickListener != null) {
        localMessage = this.mHandler.obtainMessage(paramInt, paramOnClickListener);
      }
    }
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Button does not exist");
    case -1: 
      this.mButtonPositiveText = paramCharSequence;
      this.mButtonPositiveMessage = localMessage;
      return;
    case -2: 
      this.mButtonNegativeText = paramCharSequence;
      this.mButtonNegativeMessage = localMessage;
      return;
    }
    this.mButtonNeutralText = paramCharSequence;
    this.mButtonNeutralMessage = localMessage;
  }
  
  public void setCustomTitle(View paramView)
  {
    this.mCustomTitleView = paramView;
  }
  
  public void setIcon(int paramInt)
  {
    this.mIcon = null;
    this.mIconId = paramInt;
    if (this.mIconView != null)
    {
      if (paramInt != 0) {
        this.mIconView.setImageResource(this.mIconId);
      }
    }
    else {
      return;
    }
    this.mIconView.setVisibility(8);
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    this.mIcon = paramDrawable;
    this.mIconId = 0;
    if (this.mIconView != null)
    {
      if (paramDrawable != null) {
        this.mIconView.setImageDrawable(paramDrawable);
      }
    }
    else {
      return;
    }
    this.mIconView.setVisibility(8);
  }
  
  public void setInverseBackgroundForced(boolean paramBoolean)
  {
    this.mForceInverseBackground = paramBoolean;
  }
  
  public void setMessage(CharSequence paramCharSequence)
  {
    this.mMessage = paramCharSequence;
    if (this.mMessageView != null) {
      this.mMessageView.setText(paramCharSequence);
    }
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mTitle = paramCharSequence;
    if (this.mTitleView != null) {
      this.mTitleView.setText(paramCharSequence);
    }
  }
  
  public void setView(int paramInt)
  {
    this.mView = null;
    this.mViewLayoutResId = paramInt;
    this.mViewSpacingSpecified = false;
  }
  
  public void setView(View paramView)
  {
    this.mView = paramView;
    this.mViewLayoutResId = 0;
    this.mViewSpacingSpecified = false;
  }
  
  public void setView(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mView = paramView;
    this.mViewLayoutResId = 0;
    this.mViewSpacingSpecified = true;
    this.mViewSpacingLeft = paramInt1;
    this.mViewSpacingTop = paramInt2;
    this.mViewSpacingRight = paramInt3;
    this.mViewSpacingBottom = paramInt4;
  }
  
  public static class AlertParams
  {
    public ListAdapter mAdapter;
    public boolean mCancelable;
    public int mCheckedItem = -1;
    public boolean[] mCheckedItems;
    public final Context mContext;
    public Cursor mCursor;
    public View mCustomTitleView;
    public boolean mForceInverseBackground;
    public Drawable mIcon;
    public int mIconAttrId = 0;
    public int mIconId = 0;
    public final LayoutInflater mInflater;
    public String mIsCheckedColumn;
    public boolean mIsMultiChoice;
    public boolean mIsSingleChoice;
    public CharSequence[] mItems;
    public String mLabelColumn;
    public CharSequence mMessage;
    public DialogInterface.OnClickListener mNegativeButtonListener;
    public CharSequence mNegativeButtonText;
    public DialogInterface.OnClickListener mNeutralButtonListener;
    public CharSequence mNeutralButtonText;
    public DialogInterface.OnCancelListener mOnCancelListener;
    public DialogInterface.OnMultiChoiceClickListener mOnCheckboxClickListener;
    public DialogInterface.OnClickListener mOnClickListener;
    public DialogInterface.OnDismissListener mOnDismissListener;
    public AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    public DialogInterface.OnKeyListener mOnKeyListener;
    public OnPrepareListViewListener mOnPrepareListViewListener;
    public DialogInterface.OnClickListener mPositiveButtonListener;
    public CharSequence mPositiveButtonText;
    public boolean mRecycleOnMeasure = true;
    public CharSequence mTitle;
    public View mView;
    public int mViewLayoutResId;
    public int mViewSpacingBottom;
    public int mViewSpacingLeft;
    public int mViewSpacingRight;
    public boolean mViewSpacingSpecified = false;
    public int mViewSpacingTop;
    
    public AlertParams(Context paramContext)
    {
      this.mContext = paramContext;
      this.mCancelable = true;
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
    }
    
    private void createListView(final OPAlertController paramOPAlertController)
    {
      final OPAlertController.RecycleListView localRecycleListView = (OPAlertController.RecycleListView)this.mInflater.inflate(OPAlertController.-get10(paramOPAlertController), null);
      Object localObject;
      if (this.mIsMultiChoice) {
        if (this.mCursor == null)
        {
          localObject = new ArrayAdapter(this.mContext, OPAlertController.-get11(paramOPAlertController), 16908308, this.mItems)
          {
            public View getView(int paramAnonymousInt, View paramAnonymousView, ViewGroup paramAnonymousViewGroup)
            {
              paramAnonymousView = super.getView(paramAnonymousInt, paramAnonymousView, paramAnonymousViewGroup);
              if ((OPAlertController.AlertParams.this.mCheckedItems != null) && (OPAlertController.AlertParams.this.mCheckedItems[paramAnonymousInt] != 0)) {
                localRecycleListView.setItemChecked(paramAnonymousInt, true);
              }
              return paramAnonymousView;
            }
          };
          if (this.mOnPrepareListViewListener != null) {
            this.mOnPrepareListViewListener.onPrepareListView(localRecycleListView);
          }
          OPAlertController.-set0(paramOPAlertController, (ListAdapter)localObject);
          OPAlertController.-set1(paramOPAlertController, this.mCheckedItem);
          if (this.mOnClickListener == null) {
            break label283;
          }
          localRecycleListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
          {
            public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
            {
              OPAlertController.AlertParams.this.mOnClickListener.onClick(OPAlertController.-get7(paramOPAlertController), paramAnonymousInt);
              if (!OPAlertController.AlertParams.this.mIsSingleChoice) {
                OPAlertController.-get7(paramOPAlertController).dismiss();
              }
            }
          });
          label110:
          if (this.mOnItemSelectedListener != null) {
            localRecycleListView.setOnItemSelectedListener(this.mOnItemSelectedListener);
          }
          if (!this.mIsSingleChoice) {
            break label309;
          }
          localRecycleListView.setChoiceMode(1);
        }
      }
      for (;;)
      {
        localRecycleListView.mRecycleOnMeasure = this.mRecycleOnMeasure;
        OPAlertController.-set2(paramOPAlertController, localRecycleListView);
        return;
        localObject = new CursorAdapter(this.mContext, this.mCursor, false)
        {
          private final int mIsCheckedIndex;
          private final int mLabelIndex;
          
          public void bindView(View paramAnonymousView, Context paramAnonymousContext, Cursor paramAnonymousCursor)
          {
            boolean bool = true;
            ((CheckedTextView)paramAnonymousView.findViewById(16908308)).setText(paramAnonymousCursor.getString(this.mLabelIndex));
            paramAnonymousView = localRecycleListView;
            int i = paramAnonymousCursor.getPosition();
            if (paramAnonymousCursor.getInt(this.mIsCheckedIndex) == 1) {}
            for (;;)
            {
              paramAnonymousView.setItemChecked(i, bool);
              return;
              bool = false;
            }
          }
          
          public View newView(Context paramAnonymousContext, Cursor paramAnonymousCursor, ViewGroup paramAnonymousViewGroup)
          {
            return OPAlertController.AlertParams.this.mInflater.inflate(OPAlertController.-get11(paramOPAlertController), paramAnonymousViewGroup, false);
          }
        };
        break;
        if (this.mIsSingleChoice) {}
        for (int i = OPAlertController.-get12(paramOPAlertController);; i = OPAlertController.-get9(paramOPAlertController))
        {
          if (this.mCursor == null) {
            break label246;
          }
          localObject = new SimpleCursorAdapter(this.mContext, i, this.mCursor, new String[] { this.mLabelColumn }, new int[] { 16908308 });
          break;
        }
        label246:
        if (this.mAdapter != null)
        {
          localObject = this.mAdapter;
          break;
        }
        localObject = new OPAlertController.CheckedItemAdapter(this.mContext, i, 16908308, this.mItems);
        break;
        label283:
        if (this.mOnCheckboxClickListener == null) {
          break label110;
        }
        localRecycleListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
          public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
          {
            if (OPAlertController.AlertParams.this.mCheckedItems != null) {
              OPAlertController.AlertParams.this.mCheckedItems[paramAnonymousInt] = localRecycleListView.isItemChecked(paramAnonymousInt);
            }
            OPAlertController.AlertParams.this.mOnCheckboxClickListener.onClick(OPAlertController.-get7(paramOPAlertController), paramAnonymousInt, localRecycleListView.isItemChecked(paramAnonymousInt));
          }
        });
        break label110;
        label309:
        if (this.mIsMultiChoice) {
          localRecycleListView.setChoiceMode(2);
        }
      }
    }
    
    public void apply(OPAlertController paramOPAlertController)
    {
      if (this.mCustomTitleView != null)
      {
        paramOPAlertController.setCustomTitle(this.mCustomTitleView);
        if (this.mMessage != null) {
          paramOPAlertController.setMessage(this.mMessage);
        }
        if (this.mPositiveButtonText != null) {
          paramOPAlertController.setButton(-1, this.mPositiveButtonText, this.mPositiveButtonListener, null);
        }
        if (this.mNegativeButtonText != null) {
          paramOPAlertController.setButton(-2, this.mNegativeButtonText, this.mNegativeButtonListener, null);
        }
        if (this.mNeutralButtonText != null) {
          paramOPAlertController.setButton(-3, this.mNeutralButtonText, this.mNeutralButtonListener, null);
        }
        if (this.mForceInverseBackground) {
          paramOPAlertController.setInverseBackgroundForced(true);
        }
        if ((this.mItems == null) && (this.mCursor == null)) {
          break label232;
        }
        label121:
        createListView(paramOPAlertController);
        label126:
        if (this.mView == null) {
          break label251;
        }
        if (!this.mViewSpacingSpecified) {
          break label242;
        }
        paramOPAlertController.setView(this.mView, this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
      }
      label232:
      label242:
      label251:
      while (this.mViewLayoutResId == 0)
      {
        return;
        if (this.mTitle != null) {
          paramOPAlertController.setTitle(this.mTitle);
        }
        if (this.mIcon != null) {
          paramOPAlertController.setIcon(this.mIcon);
        }
        if (this.mIconId != 0) {
          paramOPAlertController.setIcon(this.mIconId);
        }
        if (this.mIconAttrId == 0) {
          break;
        }
        paramOPAlertController.setIcon(paramOPAlertController.getIconAttributeResId(this.mIconAttrId));
        break;
        if (this.mAdapter == null) {
          break label126;
        }
        break label121;
        paramOPAlertController.setView(this.mView);
        return;
      }
      paramOPAlertController.setView(this.mViewLayoutResId);
    }
    
    public static abstract interface OnPrepareListViewListener
    {
      public abstract void onPrepareListView(ListView paramListView);
    }
  }
  
  private static final class ButtonHandler
    extends Handler
  {
    private static final int MSG_DISMISS_DIALOG = 1;
    private WeakReference<DialogInterface> mDialog;
    
    public ButtonHandler(DialogInterface paramDialogInterface)
    {
      this.mDialog = new WeakReference(paramDialogInterface);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      case 0: 
      default: 
        return;
      case -3: 
      case -2: 
      case -1: 
        ((DialogInterface.OnClickListener)paramMessage.obj).onClick((DialogInterface)this.mDialog.get(), paramMessage.what);
        return;
      }
      ((DialogInterface)paramMessage.obj).dismiss();
    }
  }
  
  private static class CheckedItemAdapter
    extends ArrayAdapter<CharSequence>
  {
    public CheckedItemAdapter(Context paramContext, int paramInt1, int paramInt2, CharSequence[] paramArrayOfCharSequence)
    {
      super(paramInt1, paramInt2, paramArrayOfCharSequence);
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public boolean hasStableIds()
    {
      return true;
    }
  }
  
  public static class RecycleListView
    extends ListView
  {
    boolean mRecycleOnMeasure = true;
    
    public RecycleListView(Context paramContext)
    {
      super();
    }
    
    public RecycleListView(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public RecycleListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      super(paramAttributeSet, paramInt);
    }
    
    public RecycleListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
      super(paramAttributeSet, paramInt1, paramInt2);
    }
    
    protected boolean recycleOnMeasure()
    {
      return this.mRecycleOnMeasure;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\app\OPAlertController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */