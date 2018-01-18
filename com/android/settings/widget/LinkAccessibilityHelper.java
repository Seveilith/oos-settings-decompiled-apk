package com.android.settings.widget;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import java.util.List;

public class LinkAccessibilityHelper
  extends ExploreByTouchHelper
{
  private static final String TAG = "LinkAccessibilityHelper";
  private final Rect mTempRect = new Rect();
  private final TextView mView;
  
  public LinkAccessibilityHelper(TextView paramTextView)
  {
    super(paramTextView);
    this.mView = paramTextView;
  }
  
  private Rect getBoundsForSpan(ClickableSpan paramClickableSpan, Rect paramRect)
  {
    Object localObject = this.mView.getText();
    paramRect.setEmpty();
    if ((localObject instanceof Spanned))
    {
      localObject = (Spanned)localObject;
      int j = ((Spanned)localObject).getSpanStart(paramClickableSpan);
      int i = ((Spanned)localObject).getSpanEnd(paramClickableSpan);
      paramClickableSpan = this.mView.getLayout();
      float f1 = paramClickableSpan.getPrimaryHorizontal(j);
      float f2 = paramClickableSpan.getPrimaryHorizontal(i);
      j = paramClickableSpan.getLineForOffset(j);
      i = paramClickableSpan.getLineForOffset(i);
      paramClickableSpan.getLineBounds(j, paramRect);
      paramRect.left = ((int)f1);
      if (i == j) {
        paramRect.right = ((int)f2);
      }
      paramRect.offset(this.mView.getTotalPaddingLeft(), this.mView.getTotalPaddingTop());
    }
    return paramRect;
  }
  
  private ClickableSpan getSpanForOffset(int paramInt)
  {
    Object localObject = this.mView.getText();
    if ((localObject instanceof Spanned))
    {
      localObject = (ClickableSpan[])((Spanned)localObject).getSpans(paramInt, paramInt, ClickableSpan.class);
      if (localObject.length == 1) {
        return localObject[0];
      }
    }
    return null;
  }
  
  private CharSequence getTextForSpan(ClickableSpan paramClickableSpan)
  {
    Object localObject = this.mView.getText();
    if ((localObject instanceof Spanned))
    {
      localObject = (Spanned)localObject;
      return ((Spanned)localObject).subSequence(((Spanned)localObject).getSpanStart(paramClickableSpan), ((Spanned)localObject).getSpanEnd(paramClickableSpan));
    }
    return (CharSequence)localObject;
  }
  
  protected int getVirtualViewAt(float paramFloat1, float paramFloat2)
  {
    Object localObject = this.mView.getText();
    if ((localObject instanceof Spanned))
    {
      localObject = (Spanned)localObject;
      int i = this.mView.getOffsetForPosition(paramFloat1, paramFloat2);
      ClickableSpan[] arrayOfClickableSpan = (ClickableSpan[])((Spanned)localObject).getSpans(i, i, ClickableSpan.class);
      if (arrayOfClickableSpan.length == 1) {
        return ((Spanned)localObject).getSpanStart(arrayOfClickableSpan[0]);
      }
    }
    return Integer.MIN_VALUE;
  }
  
  protected void getVisibleVirtualViews(List<Integer> paramList)
  {
    int i = 0;
    Object localObject = this.mView.getText();
    if ((localObject instanceof Spanned))
    {
      localObject = (Spanned)localObject;
      ClickableSpan[] arrayOfClickableSpan = (ClickableSpan[])((Spanned)localObject).getSpans(0, ((Spanned)localObject).length(), ClickableSpan.class);
      int j = arrayOfClickableSpan.length;
      while (i < j)
      {
        paramList.add(Integer.valueOf(((Spanned)localObject).getSpanStart(arrayOfClickableSpan[i])));
        i += 1;
      }
    }
  }
  
  protected boolean onPerformActionForVirtualView(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    if (paramInt2 == 16)
    {
      paramBundle = getSpanForOffset(paramInt1);
      if (paramBundle != null)
      {
        paramBundle.onClick(this.mView);
        return true;
      }
      Log.e("LinkAccessibilityHelper", "LinkSpan is null for offset: " + paramInt1);
    }
    return false;
  }
  
  protected void onPopulateEventForVirtualView(int paramInt, AccessibilityEvent paramAccessibilityEvent)
  {
    ClickableSpan localClickableSpan = getSpanForOffset(paramInt);
    if (localClickableSpan != null)
    {
      paramAccessibilityEvent.setContentDescription(getTextForSpan(localClickableSpan));
      return;
    }
    Log.e("LinkAccessibilityHelper", "ClickableSpan is null for offset: " + paramInt);
    paramAccessibilityEvent.setContentDescription(this.mView.getText());
  }
  
  protected void onPopulateNodeForVirtualView(int paramInt, AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    ClickableSpan localClickableSpan = getSpanForOffset(paramInt);
    if (localClickableSpan != null)
    {
      paramAccessibilityNodeInfo.setContentDescription(getTextForSpan(localClickableSpan));
      paramAccessibilityNodeInfo.setFocusable(true);
      paramAccessibilityNodeInfo.setClickable(true);
      getBoundsForSpan(localClickableSpan, this.mTempRect);
      if (this.mTempRect.isEmpty()) {
        break label108;
      }
      paramAccessibilityNodeInfo.setBoundsInParent(getBoundsForSpan(localClickableSpan, this.mTempRect));
    }
    for (;;)
    {
      paramAccessibilityNodeInfo.addAction(16);
      return;
      Log.e("LinkAccessibilityHelper", "ClickableSpan is null for offset: " + paramInt);
      paramAccessibilityNodeInfo.setContentDescription(this.mView.getText());
      break;
      label108:
      Log.e("LinkAccessibilityHelper", "LinkSpan bounds is empty for: " + paramInt);
      this.mTempRect.set(0, 0, 1, 1);
      paramAccessibilityNodeInfo.setBoundsInParent(this.mTempRect);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\LinkAccessibilityHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */