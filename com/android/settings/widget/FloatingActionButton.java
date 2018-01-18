package com.android.settings.widget;

import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class FloatingActionButton
  extends ImageView
{
  public FloatingActionButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setScaleType(ImageView.ScaleType.CENTER);
    setStateListAnimator(AnimatorInflater.loadStateListAnimator(paramContext, 2131034132));
    setOutlineProvider(new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setOval(0, 0, FloatingActionButton.this.getWidth(), FloatingActionButton.this.getHeight());
      }
    });
    setClipToOutline(true);
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    invalidateOutline();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\FloatingActionButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */