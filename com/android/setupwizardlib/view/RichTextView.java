package com.android.setupwizardlib.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewCompat;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.android.setupwizardlib.span.LinkSpan;
import com.android.setupwizardlib.span.SpanHelper;
import com.android.setupwizardlib.util.LinkAccessibilityHelper;

public class RichTextView
  extends TextView
{
  private static final String ANNOTATION_LINK = "link";
  private static final String ANNOTATION_TEXT_APPEARANCE = "textAppearance";
  private static final String TAG = "RichTextView";
  private LinkAccessibilityHelper mAccessibilityHelper;
  
  public RichTextView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public RichTextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  public static CharSequence getRichText(Context paramContext, CharSequence paramCharSequence)
  {
    int i = 0;
    if ((paramCharSequence instanceof Spanned))
    {
      paramCharSequence = new SpannableString(paramCharSequence);
      Annotation[] arrayOfAnnotation = (Annotation[])paramCharSequence.getSpans(0, paramCharSequence.length(), Annotation.class);
      int j = arrayOfAnnotation.length;
      if (i < j)
      {
        Annotation localAnnotation = arrayOfAnnotation[i];
        String str = localAnnotation.getKey();
        if ("textAppearance".equals(str))
        {
          str = localAnnotation.getValue();
          int k = paramContext.getResources().getIdentifier(str, "style", paramContext.getPackageName());
          if (k == 0) {
            Log.w("RichTextView", "Cannot find resource: " + k);
          }
          SpanHelper.replaceSpan(paramCharSequence, localAnnotation, new TextAppearanceSpan(paramContext, k));
        }
        for (;;)
        {
          i += 1;
          break;
          if ("link".equals(str)) {
            SpanHelper.replaceSpan(paramCharSequence, localAnnotation, new LinkSpan(localAnnotation.getValue()));
          }
        }
      }
      return paramCharSequence;
    }
    return paramCharSequence;
  }
  
  private boolean hasLinks(CharSequence paramCharSequence)
  {
    if ((paramCharSequence instanceof Spanned)) {
      return ((ClickableSpan[])((Spanned)paramCharSequence).getSpans(0, paramCharSequence.length(), ClickableSpan.class)).length > 0;
    }
    return false;
  }
  
  private void init()
  {
    this.mAccessibilityHelper = new LinkAccessibilityHelper(this);
    ViewCompat.setAccessibilityDelegate(this, this.mAccessibilityHelper);
  }
  
  protected boolean dispatchHoverEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mAccessibilityHelper != null) && (this.mAccessibilityHelper.dispatchHoverEvent(paramMotionEvent))) {
      return true;
    }
    return super.dispatchHoverEvent(paramMotionEvent);
  }
  
  public void setText(CharSequence paramCharSequence, TextView.BufferType paramBufferType)
  {
    paramCharSequence = getRichText(getContext(), paramCharSequence);
    super.setText(paramCharSequence, paramBufferType);
    boolean bool = hasLinks(paramCharSequence);
    if (bool) {
      setMovementMethod(LinkMovementMethod.getInstance());
    }
    for (;;)
    {
      setFocusable(bool);
      return;
      setMovementMethod(null);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\view\RichTextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */