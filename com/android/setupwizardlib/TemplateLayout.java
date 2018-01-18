package com.android.setupwizardlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import com.android.setupwizardlib.annotations.Keep;

public class TemplateLayout
  extends FrameLayout
{
  private ViewGroup mContainer;
  private ViewTreeObserver.OnPreDrawListener mPreDrawListener;
  private float mXFraction;
  
  public TemplateLayout(Context paramContext, int paramInt1, int paramInt2)
  {
    super(paramContext);
    init(paramInt1, paramInt2, null, R.attr.suwLayoutTheme);
  }
  
  public TemplateLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(0, 0, paramAttributeSet, R.attr.suwLayoutTheme);
  }
  
  @TargetApi(11)
  public TemplateLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(0, 0, paramAttributeSet, paramInt);
  }
  
  private void addViewInternal(View paramView)
  {
    super.addView(paramView, -1, generateDefaultLayoutParams());
  }
  
  private void inflateTemplate(int paramInt1, int paramInt2)
  {
    addViewInternal(onInflateTemplate(LayoutInflater.from(getContext()), paramInt1));
    this.mContainer = findContainer(paramInt2);
    if (this.mContainer == null) {
      throw new IllegalArgumentException("Container cannot be null in TemplateLayout");
    }
    onTemplateInflated();
  }
  
  private void init(int paramInt1, int paramInt2, AttributeSet paramAttributeSet, int paramInt3)
  {
    paramAttributeSet = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.SuwTemplateLayout, paramInt3, 0);
    paramInt3 = paramInt1;
    if (paramInt1 == 0) {
      paramInt3 = paramAttributeSet.getResourceId(R.styleable.SuwTemplateLayout_android_layout, 0);
    }
    paramInt1 = paramInt2;
    if (paramInt2 == 0) {
      paramInt1 = paramAttributeSet.getResourceId(R.styleable.SuwTemplateLayout_suwContainer, 0);
    }
    inflateTemplate(paramInt3, paramInt1);
    paramAttributeSet.recycle();
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    this.mContainer.addView(paramView, paramInt, paramLayoutParams);
  }
  
  protected ViewGroup findContainer(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = getContainerId();
    }
    return (ViewGroup)findViewById(i);
  }
  
  @Deprecated
  protected int getContainerId()
  {
    return 0;
  }
  
  @TargetApi(11)
  @Keep
  public float getXFraction()
  {
    return this.mXFraction;
  }
  
  protected View onInflateTemplate(LayoutInflater paramLayoutInflater, int paramInt)
  {
    if (paramInt == 0) {
      throw new IllegalArgumentException("android:layout not specified for TemplateLayout");
    }
    return paramLayoutInflater.inflate(paramInt, this, false);
  }
  
  protected void onTemplateInflated() {}
  
  @TargetApi(11)
  @Keep
  public void setXFraction(float paramFloat)
  {
    this.mXFraction = paramFloat;
    int i = getWidth();
    if (i != 0) {
      setTranslationX(i * paramFloat);
    }
    while (this.mPreDrawListener != null) {
      return;
    }
    this.mPreDrawListener = new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        TemplateLayout.this.getViewTreeObserver().removeOnPreDrawListener(TemplateLayout.-get0(TemplateLayout.this));
        TemplateLayout.this.setXFraction(TemplateLayout.-get1(TemplateLayout.this));
        return true;
      }
    };
    getViewTreeObserver().addOnPreDrawListener(this.mPreDrawListener);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\TemplateLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */