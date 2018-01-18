package com.android.setupwizardlib.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.setupwizardlib.R.style;
import com.android.setupwizardlib.R.styleable;

public class ButtonItem
  extends AbstractItem
  implements View.OnClickListener
{
  private Button mButton;
  private boolean mEnabled = true;
  private OnClickListener mListener;
  private CharSequence mText;
  private int mTheme = R.style.SuwButtonItem;
  
  public ButtonItem() {}
  
  public ButtonItem(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SuwButtonItem);
    this.mEnabled = paramContext.getBoolean(R.styleable.SuwButtonItem_android_enabled, true);
    this.mText = paramContext.getText(R.styleable.SuwButtonItem_android_text);
    this.mTheme = paramContext.getResourceId(R.styleable.SuwButtonItem_android_theme, R.style.SuwButtonItem);
    paramContext.recycle();
  }
  
  protected Button createButton(ViewGroup paramViewGroup)
  {
    if (this.mButton == null)
    {
      Context localContext = paramViewGroup.getContext();
      paramViewGroup = localContext;
      if (this.mTheme != 0) {
        paramViewGroup = new ContextThemeWrapper(localContext, this.mTheme);
      }
      this.mButton = new Button(paramViewGroup);
      this.mButton.setOnClickListener(this);
    }
    for (;;)
    {
      this.mButton.setEnabled(this.mEnabled);
      this.mButton.setText(this.mText);
      return this.mButton;
      if ((this.mButton.getParent() instanceof ViewGroup)) {
        ((ViewGroup)this.mButton.getParent()).removeView(this.mButton);
      }
    }
  }
  
  public int getCount()
  {
    return 0;
  }
  
  public int getLayoutResource()
  {
    return 0;
  }
  
  public CharSequence getText()
  {
    return this.mText;
  }
  
  public int getTheme()
  {
    return this.mTheme;
  }
  
  public boolean isEnabled()
  {
    return this.mEnabled;
  }
  
  public final void onBindView(View paramView)
  {
    throw new UnsupportedOperationException("Cannot bind to ButtonItem's view");
  }
  
  public void onClick(View paramView)
  {
    if (this.mListener != null) {
      this.mListener.onClick(this);
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    this.mEnabled = paramBoolean;
  }
  
  public void setOnClickListener(OnClickListener paramOnClickListener)
  {
    this.mListener = paramOnClickListener;
  }
  
  public void setText(CharSequence paramCharSequence)
  {
    this.mText = paramCharSequence;
  }
  
  public void setTheme(int paramInt)
  {
    this.mTheme = paramInt;
    this.mButton = null;
  }
  
  public static abstract interface OnClickListener
  {
    public abstract void onClick(ButtonItem paramButtonItem);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\ButtonItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */