package com.android.setupwizardlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import com.android.setupwizardlib.R.styleable;

public class StatusBarBackgroundLayout
  extends FrameLayout
{
  private Object mLastInsets;
  private Drawable mStatusBarBackground;
  
  public StatusBarBackgroundLayout(Context paramContext)
  {
    super(paramContext);
    init(paramContext, null, 0);
  }
  
  public StatusBarBackgroundLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext, paramAttributeSet, 0);
  }
  
  @TargetApi(11)
  public StatusBarBackgroundLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext, paramAttributeSet, paramInt);
  }
  
  private void init(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SuwStatusBarBackgroundLayout, paramInt, 0);
    setStatusBarBackground(paramContext.getDrawable(R.styleable.SuwStatusBarBackgroundLayout_suwStatusBarBackground));
    paramContext.recycle();
  }
  
  public Drawable getStatusBarBackground()
  {
    return this.mStatusBarBackground;
  }
  
  public WindowInsets onApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    this.mLastInsets = paramWindowInsets;
    return super.onApplyWindowInsets(paramWindowInsets);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if ((Build.VERSION.SDK_INT >= 21) && (this.mLastInsets == null)) {
      requestApplyInsets();
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if ((Build.VERSION.SDK_INT >= 21) && (this.mLastInsets != null))
    {
      int i = ((WindowInsets)this.mLastInsets).getSystemWindowInsetTop();
      if (i > 0)
      {
        this.mStatusBarBackground.setBounds(0, 0, getWidth(), i);
        this.mStatusBarBackground.draw(paramCanvas);
      }
    }
  }
  
  public void setStatusBarBackground(Drawable paramDrawable)
  {
    boolean bool2 = true;
    this.mStatusBarBackground = paramDrawable;
    if (Build.VERSION.SDK_INT >= 21)
    {
      if (paramDrawable != null) {
        break label42;
      }
      bool1 = true;
      setWillNotDraw(bool1);
      if (paramDrawable == null) {
        break label47;
      }
    }
    label42:
    label47:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      setFitsSystemWindows(bool1);
      invalidate();
      return;
      bool1 = false;
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\view\StatusBarBackgroundLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */