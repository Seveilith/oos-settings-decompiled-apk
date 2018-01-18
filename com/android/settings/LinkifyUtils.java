package com.android.settings;

import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class LinkifyUtils
{
  private static final String PLACE_HOLDER_LINK_BEGIN = "LINK_BEGIN";
  private static final String PLACE_HOLDER_LINK_END = "LINK_END";
  
  public static boolean linkify(TextView paramTextView, StringBuilder paramStringBuilder, OnClickListener paramOnClickListener)
  {
    int i = paramStringBuilder.indexOf("LINK_BEGIN");
    if (i == -1)
    {
      paramTextView.setText(paramStringBuilder);
      return false;
    }
    paramStringBuilder.delete(i, "LINK_BEGIN".length() + i);
    int j = paramStringBuilder.indexOf("LINK_END");
    if (j == -1)
    {
      paramTextView.setText(paramStringBuilder);
      return false;
    }
    paramStringBuilder.delete(j, "LINK_END".length() + j);
    paramTextView.setText(paramStringBuilder.toString(), TextView.BufferType.SPANNABLE);
    paramTextView.setMovementMethod(LinkMovementMethod.getInstance());
    ((Spannable)paramTextView.getText()).setSpan(new ClickableSpan()
    {
      public void onClick(View paramAnonymousView)
      {
        this.val$listener.onClick();
      }
      
      public void updateDrawState(TextPaint paramAnonymousTextPaint)
      {
        super.updateDrawState(paramAnonymousTextPaint);
        paramAnonymousTextPaint.setUnderlineText(false);
      }
    }, i, j, 33);
    return true;
  }
  
  public static abstract interface OnClickListener
  {
    public abstract void onClick();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\LinkifyUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */