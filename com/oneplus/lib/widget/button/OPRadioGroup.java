package com.oneplus.lib.widget.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.oneplus.commonctrl.R.styleable;

public class OPRadioGroup
  extends LinearLayout
{
  private int mCheckedId = -1;
  private OPCompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
  private OnCheckedChangeListener mOnCheckedChangeListener;
  private PassThroughHierarchyChangeListener mPassThroughListener;
  private boolean mProtectFromCheckedChange = false;
  
  public OPRadioGroup(Context paramContext)
  {
    super(paramContext);
    setOrientation(1);
    init();
  }
  
  public OPRadioGroup(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OPRadioGroup, 16842878, 0);
    int i = paramContext.getResourceId(R.styleable.OPRadioGroup_android_checkedButton, -1);
    if (i != -1) {
      this.mCheckedId = i;
    }
    setOrientation(paramContext.getInt(R.styleable.OPRadioGroup_android_orientation, 1));
    paramContext.recycle();
    init();
  }
  
  private void init()
  {
    this.mChildOnCheckedChangeListener = new CheckedStateTracker(null);
    this.mPassThroughListener = new PassThroughHierarchyChangeListener(null);
    super.setOnHierarchyChangeListener(this.mPassThroughListener);
  }
  
  private void setCheckedId(int paramInt)
  {
    this.mCheckedId = paramInt;
    if (this.mOnCheckedChangeListener != null) {
      this.mOnCheckedChangeListener.onCheckedChanged(this, this.mCheckedId);
    }
  }
  
  private void setCheckedStateForView(int paramInt, boolean paramBoolean)
  {
    View localView = findViewById(paramInt);
    if ((localView != null) && ((localView instanceof OPRadioButton))) {
      ((OPRadioButton)localView).setChecked(paramBoolean);
    }
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramView instanceof OPRadioButton))
    {
      OPRadioButton localOPRadioButton = (OPRadioButton)paramView;
      if (localOPRadioButton.isChecked())
      {
        this.mProtectFromCheckedChange = true;
        if (this.mCheckedId != -1) {
          setCheckedStateForView(this.mCheckedId, false);
        }
        this.mProtectFromCheckedChange = false;
        setCheckedId(localOPRadioButton.getId());
      }
    }
    super.addView(paramView, paramInt, paramLayoutParams);
  }
  
  public void check(int paramInt)
  {
    if ((paramInt != -1) && (paramInt == this.mCheckedId)) {
      return;
    }
    if (this.mCheckedId != -1) {
      setCheckedStateForView(this.mCheckedId, false);
    }
    if (paramInt != -1) {
      setCheckedStateForView(paramInt, true);
    }
    setCheckedId(paramInt);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  public void clearCheck()
  {
    check(-1);
  }
  
  protected LinearLayout.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  public int getCheckedRadioButtonId()
  {
    return this.mCheckedId;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    if (this.mCheckedId != -1)
    {
      this.mProtectFromCheckedChange = true;
      setCheckedStateForView(this.mCheckedId, true);
      this.mProtectFromCheckedChange = false;
      setCheckedId(this.mCheckedId);
    }
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName(OPRadioGroup.class.getName());
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setClassName(OPRadioGroup.class.getName());
  }
  
  public void setOnCheckedChangeListener(OnCheckedChangeListener paramOnCheckedChangeListener)
  {
    this.mOnCheckedChangeListener = paramOnCheckedChangeListener;
  }
  
  public void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener paramOnHierarchyChangeListener)
  {
    PassThroughHierarchyChangeListener.-set0(this.mPassThroughListener, paramOnHierarchyChangeListener);
  }
  
  private class CheckedStateTracker
    implements OPCompoundButton.OnCheckedChangeListener
  {
    private CheckedStateTracker() {}
    
    public void onCheckedChanged(OPCompoundButton paramOPCompoundButton, boolean paramBoolean)
    {
      if (OPRadioGroup.-get2(OPRadioGroup.this)) {
        return;
      }
      OPRadioGroup.-set0(OPRadioGroup.this, true);
      if (OPRadioGroup.-get0(OPRadioGroup.this) != -1) {
        OPRadioGroup.-wrap1(OPRadioGroup.this, OPRadioGroup.-get0(OPRadioGroup.this), false);
      }
      OPRadioGroup.-set0(OPRadioGroup.this, false);
      int i = paramOPCompoundButton.getId();
      OPRadioGroup.-wrap0(OPRadioGroup.this, i);
    }
  }
  
  public static class LayoutParams
    extends LinearLayout.LayoutParams
  {
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(int paramInt1, int paramInt2, float paramFloat)
    {
      super(paramInt2, paramFloat);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
    
    protected void setBaseAttributes(TypedArray paramTypedArray, int paramInt1, int paramInt2)
    {
      if (paramTypedArray.hasValue(paramInt1)) {}
      for (this.width = paramTypedArray.getLayoutDimension(paramInt1, "layout_width"); paramTypedArray.hasValue(paramInt2); this.width = -2)
      {
        this.height = paramTypedArray.getLayoutDimension(paramInt2, "layout_height");
        return;
      }
      this.height = -2;
    }
  }
  
  public static abstract interface OnCheckedChangeListener
  {
    public abstract void onCheckedChanged(OPRadioGroup paramOPRadioGroup, int paramInt);
  }
  
  private class PassThroughHierarchyChangeListener
    implements ViewGroup.OnHierarchyChangeListener
  {
    private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;
    
    private PassThroughHierarchyChangeListener() {}
    
    public void onChildViewAdded(View paramView1, View paramView2)
    {
      if ((paramView1 == OPRadioGroup.this) && ((paramView2 instanceof OPRadioButton)))
      {
        if (paramView2.getId() == -1) {
          paramView2.setId(View.generateViewId());
        }
        ((OPRadioButton)paramView2).setOnCheckedChangeWidgetListener(OPRadioGroup.-get1(OPRadioGroup.this));
      }
      if (this.mOnHierarchyChangeListener != null) {
        this.mOnHierarchyChangeListener.onChildViewAdded(paramView1, paramView2);
      }
    }
    
    public void onChildViewRemoved(View paramView1, View paramView2)
    {
      if ((paramView1 == OPRadioGroup.this) && ((paramView2 instanceof OPRadioButton))) {
        ((OPRadioButton)paramView2).setOnCheckedChangeWidgetListener(null);
      }
      if (this.mOnHierarchyChangeListener != null) {
        this.mOnHierarchyChangeListener.onChildViewRemoved(paramView1, paramView2);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\button\OPRadioGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */