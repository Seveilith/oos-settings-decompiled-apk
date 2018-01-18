package com.airbnb.lottie;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import java.util.HashMap;
import java.util.Map;

public class TextDelegate
{
  @Nullable
  private final LottieAnimationView animationView;
  private boolean cacheText = true;
  @Nullable
  private final LottieDrawable drawable;
  private final Map<String, String> stringMap = new HashMap();
  
  @VisibleForTesting
  TextDelegate()
  {
    this.animationView = null;
    this.drawable = null;
  }
  
  public TextDelegate(LottieAnimationView paramLottieAnimationView)
  {
    this.animationView = paramLottieAnimationView;
    this.drawable = null;
  }
  
  public TextDelegate(LottieDrawable paramLottieDrawable)
  {
    this.drawable = paramLottieDrawable;
    this.animationView = null;
  }
  
  private void invalidate()
  {
    if (this.animationView == null) {}
    while (this.drawable == null)
    {
      return;
      this.animationView.invalidate();
    }
    this.drawable.invalidateSelf();
  }
  
  public String getText(String paramString)
  {
    return paramString;
  }
  
  public final String getTextInternal(String paramString)
  {
    if (!this.cacheText) {}
    String str;
    while (!this.stringMap.containsKey(paramString))
    {
      str = getText(paramString);
      if (this.cacheText) {
        break;
      }
      return str;
    }
    return (String)this.stringMap.get(paramString);
    this.stringMap.put(paramString, str);
    return str;
  }
  
  public void invalidateAllText()
  {
    this.stringMap.clear();
    invalidate();
  }
  
  public void invalidateText(String paramString)
  {
    this.stringMap.remove(paramString);
    invalidate();
  }
  
  public void setCacheText(boolean paramBoolean)
  {
    this.cacheText = paramBoolean;
  }
  
  public void setText(String paramString1, String paramString2)
  {
    this.stringMap.put(paramString1, paramString2);
    invalidate();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\TextDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */