package com.android.settingslib.graph;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.android.settingslib.R.id;
import com.android.settingslib.R.layout;
import com.android.settingslib.R.styleable;

public class UsageView
  extends FrameLayout
{
  private final TextView[] mBottomLabels;
  private final TextView[] mLabels;
  private final UsageGraph mUsageGraph;
  
  public UsageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    LayoutInflater.from(paramContext).inflate(R.layout.usage_view, this);
    this.mUsageGraph = ((UsageGraph)findViewById(R.id.usage_graph));
    this.mLabels = new TextView[] { (TextView)findViewById(R.id.label_bottom), (TextView)findViewById(R.id.label_middle), (TextView)findViewById(R.id.label_top) };
    this.mBottomLabels = new TextView[] { (TextView)findViewById(R.id.label_start), (TextView)findViewById(R.id.label_end) };
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.UsageView, 0, 0);
    if (paramContext.hasValue(R.styleable.UsageView_sideLabels)) {
      setSideLabels(paramContext.getTextArray(R.styleable.UsageView_sideLabels));
    }
    if (paramContext.hasValue(R.styleable.UsageView_bottomLabels)) {
      setBottomLabels(paramContext.getTextArray(R.styleable.UsageView_bottomLabels));
    }
    int i;
    if (paramContext.hasValue(R.styleable.UsageView_textColor))
    {
      int j = paramContext.getColor(R.styleable.UsageView_textColor, 0);
      paramAttributeSet = this.mLabels;
      i = 0;
      int k = paramAttributeSet.length;
      while (i < k)
      {
        paramAttributeSet[i].setTextColor(j);
        i += 1;
      }
      paramAttributeSet = this.mBottomLabels;
      i = 0;
      k = paramAttributeSet.length;
      while (i < k)
      {
        paramAttributeSet[i].setTextColor(j);
        i += 1;
      }
    }
    if (paramContext.hasValue(R.styleable.UsageView_android_gravity))
    {
      i = paramContext.getInt(R.styleable.UsageView_android_gravity, 0);
      if (i != 8388613) {
        break label365;
      }
      paramAttributeSet = (LinearLayout)findViewById(R.id.graph_label_group);
      localObject = (LinearLayout)findViewById(R.id.label_group);
      paramAttributeSet.removeView((View)localObject);
      paramAttributeSet.addView((View)localObject);
      ((LinearLayout)localObject).setGravity(8388613);
      paramAttributeSet = (LinearLayout)findViewById(R.id.bottom_label_group);
      localObject = paramAttributeSet.findViewById(R.id.bottom_label_space);
      paramAttributeSet.removeView((View)localObject);
      paramAttributeSet.addView((View)localObject);
    }
    label365:
    while (i == 8388611)
    {
      Object localObject;
      this.mUsageGraph.setAccentColor(paramContext.getColor(R.styleable.UsageView_android_colorAccent, 0));
      return;
    }
    throw new IllegalArgumentException("Unsupported gravity " + i);
  }
  
  private void setWeight(int paramInt, float paramFloat)
  {
    View localView = findViewById(paramInt);
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)localView.getLayoutParams();
    localLayoutParams.weight = paramFloat;
    localView.setLayoutParams(localLayoutParams);
  }
  
  public void addPath(SparseIntArray paramSparseIntArray)
  {
    this.mUsageGraph.addPath(paramSparseIntArray);
  }
  
  public void clearPaths()
  {
    this.mUsageGraph.clearPaths();
  }
  
  public void configureGraph(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mUsageGraph.setMax(paramInt1, paramInt2);
    this.mUsageGraph.setShowProjection(paramBoolean1, paramBoolean2);
  }
  
  public void setAccentColor(int paramInt)
  {
    this.mUsageGraph.setAccentColor(paramInt);
  }
  
  public void setBottomLabels(CharSequence[] paramArrayOfCharSequence)
  {
    if (paramArrayOfCharSequence.length != this.mBottomLabels.length) {
      throw new IllegalArgumentException("Invalid number of labels");
    }
    int i = 0;
    while (i < this.mBottomLabels.length)
    {
      this.mBottomLabels[i].setText(paramArrayOfCharSequence[i]);
      i += 1;
    }
  }
  
  public void setDividerColors(int paramInt1, int paramInt2)
  {
    this.mUsageGraph.setDividerColors(paramInt1, paramInt2);
  }
  
  public void setDividerLoc(int paramInt)
  {
    this.mUsageGraph.setDividerLoc(paramInt);
  }
  
  public void setSideLabelWeights(float paramFloat1, float paramFloat2)
  {
    setWeight(R.id.space1, paramFloat1);
    setWeight(R.id.space2, paramFloat2);
  }
  
  public void setSideLabels(CharSequence[] paramArrayOfCharSequence)
  {
    if (paramArrayOfCharSequence.length != this.mLabels.length) {
      throw new IllegalArgumentException("Invalid number of labels");
    }
    int i = 0;
    while (i < this.mLabels.length)
    {
      this.mLabels[i].setText(paramArrayOfCharSequence[i]);
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\graph\UsageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */