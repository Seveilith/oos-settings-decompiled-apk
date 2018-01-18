package com.android.setupwizardlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import com.android.setupwizardlib.R.attr;
import com.android.setupwizardlib.R.id;
import com.android.setupwizardlib.R.layout;
import com.android.setupwizardlib.R.style;

public class NavigationBar
  extends LinearLayout
  implements View.OnClickListener
{
  private Button mBackButton;
  private NavigationBarListener mListener;
  private Button mMoreButton;
  private Button mNextButton;
  
  public NavigationBar(Context paramContext)
  {
    super(getThemedContext(paramContext));
    init();
  }
  
  public NavigationBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(getThemedContext(paramContext), paramAttributeSet);
    init();
  }
  
  @TargetApi(11)
  public NavigationBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(getThemedContext(paramContext), paramAttributeSet, paramInt);
    init();
  }
  
  private static int getNavbarTheme(Context paramContext)
  {
    int j = 1;
    paramContext = paramContext.obtainStyledAttributes(new int[] { R.attr.suwNavBarTheme, 16842800, 16842801 });
    int k = paramContext.getResourceId(0, 0);
    int i = k;
    if (k == 0)
    {
      float[] arrayOfFloat1 = new float[3];
      float[] arrayOfFloat2 = new float[3];
      Color.colorToHSV(paramContext.getColor(1, 0), arrayOfFloat1);
      Color.colorToHSV(paramContext.getColor(2, 0), arrayOfFloat2);
      if (arrayOfFloat1[2] <= arrayOfFloat2[2]) {
        break label99;
      }
      i = j;
      if (i == 0) {
        break label104;
      }
    }
    label99:
    label104:
    for (i = R.style.SuwNavBarThemeDark;; i = R.style.SuwNavBarThemeLight)
    {
      paramContext.recycle();
      return i;
      i = 0;
      break;
    }
  }
  
  private static Context getThemedContext(Context paramContext)
  {
    return new ContextThemeWrapper(paramContext, getNavbarTheme(paramContext));
  }
  
  private void init()
  {
    View.inflate(getContext(), R.layout.suw_navbar_view, this);
    this.mNextButton = ((Button)findViewById(R.id.suw_navbar_next));
    this.mBackButton = ((Button)findViewById(R.id.suw_navbar_back));
    this.mMoreButton = ((Button)findViewById(R.id.suw_navbar_more));
  }
  
  public Button getBackButton()
  {
    return this.mBackButton;
  }
  
  public Button getMoreButton()
  {
    return this.mMoreButton;
  }
  
  public Button getNextButton()
  {
    return this.mNextButton;
  }
  
  public void onClick(View paramView)
  {
    if (this.mListener != null)
    {
      if (paramView != getBackButton()) {
        break label25;
      }
      this.mListener.onNavigateBack();
    }
    label25:
    while (paramView != getNextButton()) {
      return;
    }
    this.mListener.onNavigateNext();
  }
  
  public void setNavigationBarListener(NavigationBarListener paramNavigationBarListener)
  {
    this.mListener = paramNavigationBarListener;
    if (this.mListener != null)
    {
      getBackButton().setOnClickListener(this);
      getNextButton().setOnClickListener(this);
    }
  }
  
  public static abstract interface NavigationBarListener
  {
    public abstract void onNavigateBack();
    
    public abstract void onNavigateNext();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\view\NavigationBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */