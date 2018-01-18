package com.android.settings.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class LinkTextView
  extends TextView
{
  private LinkAccessibilityHelper mAccessibilityHelper = new LinkAccessibilityHelper(this);
  
  public LinkTextView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public LinkTextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setAccessibilityDelegate(this.mAccessibilityHelper);
  }
  
  protected boolean dispatchHoverEvent(@NonNull MotionEvent paramMotionEvent)
  {
    if (this.mAccessibilityHelper.dispatchHoverEvent(paramMotionEvent)) {
      return true;
    }
    return super.dispatchHoverEvent(paramMotionEvent);
  }
  
  public void setText(CharSequence paramCharSequence, TextView.BufferType paramBufferType)
  {
    super.setText(paramCharSequence, paramBufferType);
    if (((paramCharSequence instanceof Spanned)) && (((ClickableSpan[])((Spanned)paramCharSequence).getSpans(0, paramCharSequence.length(), ClickableSpan.class)).length > 0)) {
      setMovementMethod(LinkMovementMethod.getInstance());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\LinkTextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */