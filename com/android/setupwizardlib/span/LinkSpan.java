package com.android.setupwizardlib.span;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

public class LinkSpan
  extends ClickableSpan
{
  private static final String TAG = "LinkSpan";
  private static final Typeface TYPEFACE_MEDIUM = Typeface.create("sans-serif-medium", 0);
  private final String mId;
  
  public LinkSpan(String paramString)
  {
    this.mId = paramString;
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  public void onClick(View paramView)
  {
    Context localContext = paramView.getContext();
    if ((localContext instanceof OnClickListener))
    {
      ((OnClickListener)localContext).onClick(this);
      if (Build.VERSION.SDK_INT >= 19) {
        paramView.cancelPendingInputEvents();
      }
      return;
    }
    Log.w("LinkSpan", "Dropping click event. No listener attached.");
  }
  
  public void updateDrawState(TextPaint paramTextPaint)
  {
    super.updateDrawState(paramTextPaint);
    paramTextPaint.setUnderlineText(false);
    paramTextPaint.setTypeface(TYPEFACE_MEDIUM);
  }
  
  public static abstract interface OnClickListener
  {
    public abstract void onClick(LinkSpan paramLinkSpan);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\span\LinkSpan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */