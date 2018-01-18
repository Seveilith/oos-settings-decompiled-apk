package com.android.setupwizardlib.util;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
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
  
  private static float convertToLocalHorizontalCoordinate(TextView paramTextView, float paramFloat)
  {
    paramFloat = Math.max(0.0F, paramFloat - paramTextView.getTotalPaddingLeft());
    return Math.min(paramTextView.getWidth() - paramTextView.getTotalPaddingRight() - 1, paramFloat) + paramTextView.getScrollX();
  }
  
  private Rect getBoundsForSpan(ClickableSpan paramClickableSpan, Rect paramRect)
  {
    Object localObject = this.mView.getText();
    paramRect.setEmpty();
    Layout localLayout;
    int j;
    float f1;
    if ((localObject instanceof Spanned))
    {
      localLayout = this.mView.getLayout();
      if (localLayout != null)
      {
        localObject = (Spanned)localObject;
        j = ((Spanned)localObject).getSpanStart(paramClickableSpan);
        int i = ((Spanned)localObject).getSpanEnd(paramClickableSpan);
        f1 = localLayout.getPrimaryHorizontal(j);
        float f2 = localLayout.getPrimaryHorizontal(i);
        j = localLayout.getLineForOffset(j);
        i = localLayout.getLineForOffset(i);
        localLayout.getLineBounds(j, paramRect);
        if (i != j) {
          break label155;
        }
        paramRect.left = ((int)Math.min(f1, f2));
        paramRect.right = ((int)Math.max(f1, f2));
      }
    }
    for (;;)
    {
      paramRect.offset(this.mView.getTotalPaddingLeft(), this.mView.getTotalPaddingTop());
      return paramRect;
      label155:
      if (localLayout.getParagraphDirection(j) == -1) {
        paramRect.right = ((int)f1);
      } else {
        paramRect.left = ((int)f1);
      }
    }
  }
  
  private static int getLineAtCoordinate(TextView paramTextView, float paramFloat)
  {
    paramFloat = Math.max(0.0F, paramFloat - paramTextView.getTotalPaddingTop());
    paramFloat = Math.min(paramTextView.getHeight() - paramTextView.getTotalPaddingBottom() - 1, paramFloat);
    float f = paramTextView.getScrollY();
    return paramTextView.getLayout().getLineForVertical((int)(paramFloat + f));
  }
  
  private static int getOffsetAtCoordinate(TextView paramTextView, int paramInt, float paramFloat)
  {
    paramFloat = convertToLocalHorizontalCoordinate(paramTextView, paramFloat);
    return paramTextView.getLayout().getOffsetForHorizontal(paramInt, paramFloat);
  }
  
  private static int getOffsetForPosition(TextView paramTextView, float paramFloat1, float paramFloat2)
  {
    if (paramTextView.getLayout() == null) {
      return -1;
    }
    return getOffsetAtCoordinate(paramTextView, getLineAtCoordinate(paramTextView, paramFloat2), paramFloat1);
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
      int i = getOffsetForPosition(this.mView, paramFloat1, paramFloat2);
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
    Log.e("LinkAccessibilityHelper", "LinkSpan is null for offset: " + paramInt);
    paramAccessibilityEvent.setContentDescription(this.mView.getText());
  }
  
  protected void onPopulateNodeForVirtualView(int paramInt, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    ClickableSpan localClickableSpan = getSpanForOffset(paramInt);
    if (localClickableSpan != null) {
      paramAccessibilityNodeInfoCompat.setContentDescription(getTextForSpan(localClickableSpan));
    }
    for (;;)
    {
      paramAccessibilityNodeInfoCompat.setFocusable(true);
      paramAccessibilityNodeInfoCompat.setClickable(true);
      getBoundsForSpan(localClickableSpan, this.mTempRect);
      if (this.mTempRect.isEmpty())
      {
        Log.e("LinkAccessibilityHelper", "LinkSpan bounds is empty for: " + paramInt);
        this.mTempRect.set(0, 0, 1, 1);
      }
      paramAccessibilityNodeInfoCompat.setBoundsInParent(this.mTempRect);
      paramAccessibilityNodeInfoCompat.addAction(16);
      return;
      Log.e("LinkAccessibilityHelper", "LinkSpan is null for offset: " + paramInt);
      paramAccessibilityNodeInfoCompat.setContentDescription(this.mView.getText());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\util\LinkAccessibilityHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */