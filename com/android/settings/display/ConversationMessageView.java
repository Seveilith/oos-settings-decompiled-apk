package com.android.settings.display;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R.styleable;

public class ConversationMessageView
  extends FrameLayout
{
  private TextView mContactIconView;
  private final int mIconBackgroundColor;
  private final CharSequence mIconText;
  private final int mIconTextColor;
  private final boolean mIncoming;
  private LinearLayout mMessageBubble;
  private final CharSequence mMessageText;
  private ViewGroup mMessageTextAndInfoView;
  private TextView mMessageTextView;
  private TextView mStatusTextView;
  private final CharSequence mTimestampText;
  
  public ConversationMessageView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ConversationMessageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ConversationMessageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ConversationMessageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ConversationMessageView);
    this.mIncoming = paramAttributeSet.getBoolean(0, true);
    this.mMessageText = paramAttributeSet.getString(1);
    this.mTimestampText = paramAttributeSet.getString(2);
    this.mIconText = paramAttributeSet.getString(3);
    this.mIconTextColor = paramAttributeSet.getColor(4, 0);
    this.mIconBackgroundColor = paramAttributeSet.getColor(5, 0);
    LayoutInflater.from(paramContext).inflate(2130968650, this);
    LayoutInflater.from(paramContext).inflate(2130968649, this);
  }
  
  private static Drawable getTintedDrawable(Context paramContext, Drawable paramDrawable, int paramInt)
  {
    Drawable.ConstantState localConstantState = paramDrawable.getConstantState();
    if (localConstantState != null) {}
    for (paramContext = localConstantState.newDrawable(paramContext.getResources()).mutate();; paramContext = paramDrawable)
    {
      paramContext.setColorFilter(paramInt, PorterDuff.Mode.SRC_ATOP);
      return paramContext;
    }
  }
  
  private static boolean isLayoutRtl(View paramView)
  {
    return 1 == paramView.getLayoutDirection();
  }
  
  private void updateTextAppearance()
  {
    int i;
    if (this.mIncoming)
    {
      i = 2131493703;
      if (!this.mIncoming) {
        break label60;
      }
    }
    label60:
    for (int j = 2131493706;; j = 2131493705)
    {
      i = getContext().getColor(i);
      this.mMessageTextView.setTextColor(i);
      this.mMessageTextView.setLinkTextColor(i);
      this.mStatusTextView.setTextColor(j);
      return;
      i = 2131493704;
      break;
    }
  }
  
  private void updateViewAppearance()
  {
    Object localObject = getResources();
    int k = ((Resources)localObject).getDimensionPixelOffset(2131755611);
    int i = ((Resources)localObject).getDimensionPixelOffset(2131755613);
    int i1 = ((Resources)localObject).getDimensionPixelOffset(2131755614);
    int i2 = ((Resources)localObject).getDimensionPixelOffset(2131755615);
    int j;
    label65:
    int i3;
    int i4;
    int m;
    label94:
    int n;
    if (this.mIncoming)
    {
      k = i + k;
      j = i;
      i = k;
      if (!this.mIncoming) {
        break label205;
      }
      k = 8388627;
      i3 = ((Resources)localObject).getDimensionPixelSize(2131755612);
      i4 = ((Resources)localObject).getDimensionPixelOffset(2131755616);
      if (!this.mIncoming) {
        break label211;
      }
      m = 2130838147;
      if (!this.mIncoming) {
        break label218;
      }
      n = 2131493707;
      label105:
      localObject = getContext();
      localObject = getTintedDrawable((Context)localObject, ((Context)localObject).getDrawable(m), ((Context)localObject).getColor(n));
      this.mMessageTextAndInfoView.setBackground((Drawable)localObject);
      if (!isLayoutRtl(this)) {
        break label225;
      }
      this.mMessageTextAndInfoView.setPadding(j, i1 + i4, i, i2);
    }
    for (;;)
    {
      setPadding(getPaddingLeft(), i3, getPaddingRight(), 0);
      this.mMessageBubble.setGravity(k);
      updateTextAppearance();
      return;
      j = i;
      k = i + k;
      i = j;
      j = k;
      break;
      label205:
      k = 8388629;
      break label65;
      label211:
      m = 2130838148;
      break label94;
      label218:
      n = 2131493708;
      break label105;
      label225:
      this.mMessageTextAndInfoView.setPadding(i, i1 + i4, j, i2);
    }
  }
  
  private void updateViewContent()
  {
    this.mMessageTextView.setText(this.mMessageText);
    this.mStatusTextView.setText(this.mTimestampText);
    this.mContactIconView.setText(this.mIconText);
    this.mContactIconView.setTextColor(this.mIconTextColor);
    Drawable localDrawable = getContext().getDrawable(2130837893);
    this.mContactIconView.setBackground(getTintedDrawable(getContext(), localDrawable, this.mIconBackgroundColor));
  }
  
  protected void onFinishInflate()
  {
    this.mMessageBubble = ((LinearLayout)findViewById(2131362042));
    this.mMessageTextAndInfoView = ((ViewGroup)findViewById(2131362043));
    this.mMessageTextView = ((TextView)findViewById(2131362044));
    this.mStatusTextView = ((TextView)findViewById(2131362045));
    this.mContactIconView = ((TextView)findViewById(2131362046));
    updateViewContent();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramBoolean = isLayoutRtl(this);
    paramInt4 = this.mContactIconView.getMeasuredWidth();
    int i = this.mContactIconView.getMeasuredHeight();
    int j = getPaddingTop();
    int k = paramInt3 - paramInt1 - paramInt4 - getPaddingLeft() - getPaddingRight();
    int m = this.mMessageBubble.getMeasuredHeight();
    if (this.mIncoming) {
      if (paramBoolean)
      {
        paramInt2 = paramInt3 - paramInt1 - getPaddingRight() - paramInt4;
        paramInt1 = paramInt2 - k;
      }
    }
    for (;;)
    {
      this.mContactIconView.layout(paramInt2, j, paramInt2 + paramInt4, j + i);
      this.mMessageBubble.layout(paramInt1, j, paramInt1 + k, j + m);
      return;
      paramInt2 = getPaddingLeft();
      paramInt1 = paramInt2 + paramInt4;
      continue;
      if (paramBoolean)
      {
        paramInt2 = getPaddingLeft();
        paramInt1 = paramInt2 + paramInt4;
      }
      else
      {
        paramInt2 = paramInt3 - paramInt1 - getPaddingRight() - paramInt4;
        paramInt1 = paramInt2 - k;
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    updateViewAppearance();
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    paramInt2 = View.MeasureSpec.makeMeasureSpec(0, 0);
    int i = View.MeasureSpec.makeMeasureSpec(0, 0);
    this.mContactIconView.measure(i, i);
    i = View.MeasureSpec.makeMeasureSpec(Math.max(this.mContactIconView.getMeasuredWidth(), this.mContactIconView.getMeasuredHeight()), 1073741824);
    this.mContactIconView.measure(i, i);
    i = getResources().getDimensionPixelSize(2131755611);
    i = View.MeasureSpec.makeMeasureSpec(paramInt1 - this.mContactIconView.getMeasuredWidth() * 2 - i - getPaddingLeft() - getPaddingRight(), Integer.MIN_VALUE);
    this.mMessageBubble.measure(i, paramInt2);
    paramInt2 = Math.max(this.mContactIconView.getMeasuredHeight(), this.mMessageBubble.getMeasuredHeight());
    setMeasuredDimension(paramInt1, getPaddingBottom() + paramInt2 + getPaddingTop());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\display\ConversationMessageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */