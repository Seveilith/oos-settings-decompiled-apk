package com.android.setupwizardlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.setupwizardlib.view.StatusBarBackgroundLayout;

public class GlifLayout
  extends TemplateLayout
{
  private static final String TAG = "GlifLayout";
  private ColorStateList mPrimaryColor;
  
  public GlifLayout(Context paramContext)
  {
    this(paramContext, 0, 0);
  }
  
  public GlifLayout(Context paramContext, int paramInt)
  {
    this(paramContext, paramInt, 0);
  }
  
  public GlifLayout(Context paramContext, int paramInt1, int paramInt2)
  {
    super(paramContext, paramInt1, paramInt2);
    init(null, R.attr.suwLayoutTheme);
  }
  
  public GlifLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramAttributeSet, R.attr.suwLayoutTheme);
  }
  
  @TargetApi(11)
  public GlifLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramAttributeSet, paramInt);
  }
  
  private ProgressBar getProgressBar()
  {
    if (peekProgressBar() == null)
    {
      ViewStub localViewStub = (ViewStub)findManagedViewById(R.id.suw_layout_progress_stub);
      if (localViewStub != null) {
        localViewStub.inflate();
      }
      setProgressBarColor(this.mPrimaryColor);
    }
    return peekProgressBar();
  }
  
  private void init(AttributeSet paramAttributeSet, int paramInt)
  {
    paramAttributeSet = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.SuwGlifLayout, paramInt, 0);
    Object localObject = paramAttributeSet.getDrawable(R.styleable.SuwGlifLayout_android_icon);
    if (localObject != null) {
      setIcon((Drawable)localObject);
    }
    localObject = paramAttributeSet.getColorStateList(R.styleable.SuwGlifLayout_suwHeaderColor);
    if (localObject != null) {
      setHeaderColor((ColorStateList)localObject);
    }
    localObject = paramAttributeSet.getText(R.styleable.SuwGlifLayout_suwHeaderText);
    if (localObject != null) {
      setHeaderText((CharSequence)localObject);
    }
    setPrimaryColor(paramAttributeSet.getColorStateList(R.styleable.SuwGlifLayout_android_colorPrimary));
    paramAttributeSet.recycle();
  }
  
  private void setGlifPatternColor(ColorStateList paramColorStateList)
  {
    View localView;
    if (Build.VERSION.SDK_INT >= 21)
    {
      setSystemUiVisibility(1024);
      localView = findManagedViewById(R.id.suw_pattern_bg);
      if (localView != null)
      {
        paramColorStateList = new GlifPatternDrawable(paramColorStateList.getDefaultColor());
        if (!(localView instanceof StatusBarBackgroundLayout)) {
          break label55;
        }
        ((StatusBarBackgroundLayout)localView).setStatusBarBackground(paramColorStateList);
      }
    }
    return;
    label55:
    localView.setBackground(paramColorStateList);
  }
  
  private void setProgressBarColor(ColorStateList paramColorStateList)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      ProgressBar localProgressBar = peekProgressBar();
      if (localProgressBar != null) {
        localProgressBar.setIndeterminateTintList(paramColorStateList);
      }
    }
  }
  
  protected ViewGroup findContainer(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = R.id.suw_layout_content;
    }
    return super.findContainer(i);
  }
  
  protected View findManagedViewById(int paramInt)
  {
    return findViewById(paramInt);
  }
  
  public ColorStateList getHeaderColor()
  {
    ColorStateList localColorStateList = null;
    TextView localTextView = getHeaderTextView();
    if (localTextView != null) {
      localColorStateList = localTextView.getTextColors();
    }
    return localColorStateList;
  }
  
  public CharSequence getHeaderText()
  {
    CharSequence localCharSequence = null;
    TextView localTextView = getHeaderTextView();
    if (localTextView != null) {
      localCharSequence = localTextView.getText();
    }
    return localCharSequence;
  }
  
  public TextView getHeaderTextView()
  {
    return (TextView)findManagedViewById(R.id.suw_layout_title);
  }
  
  public Drawable getIcon()
  {
    Drawable localDrawable = null;
    ImageView localImageView = getIconView();
    if (localImageView != null) {
      localDrawable = localImageView.getDrawable();
    }
    return localDrawable;
  }
  
  protected ImageView getIconView()
  {
    return (ImageView)findManagedViewById(R.id.suw_layout_icon);
  }
  
  public ColorStateList getPrimaryColor()
  {
    return this.mPrimaryColor;
  }
  
  public ScrollView getScrollView()
  {
    View localView = findManagedViewById(R.id.suw_scroll_view);
    if ((localView instanceof ScrollView)) {
      return (ScrollView)localView;
    }
    return null;
  }
  
  public boolean isProgressBarShown()
  {
    boolean bool2 = false;
    View localView = findManagedViewById(R.id.suw_layout_progress);
    boolean bool1 = bool2;
    if (localView != null)
    {
      bool1 = bool2;
      if (localView.getVisibility() == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  protected View onInflateTemplate(LayoutInflater paramLayoutInflater, int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = R.layout.suw_glif_template;
    }
    try
    {
      paramLayoutInflater = super.onInflateTemplate(paramLayoutInflater, i);
      return paramLayoutInflater;
    }
    catch (RuntimeException paramLayoutInflater)
    {
      throw new InflateException("Unable to inflate layout. Are you using @style/SuwThemeGlif (or its descendant) as your theme?", paramLayoutInflater);
    }
  }
  
  public ProgressBar peekProgressBar()
  {
    return (ProgressBar)findManagedViewById(R.id.suw_layout_progress);
  }
  
  public void setHeaderColor(ColorStateList paramColorStateList)
  {
    TextView localTextView = getHeaderTextView();
    if (localTextView != null) {
      localTextView.setTextColor(paramColorStateList);
    }
  }
  
  public void setHeaderText(int paramInt)
  {
    setHeaderText(getContext().getResources().getText(paramInt));
  }
  
  public void setHeaderText(CharSequence paramCharSequence)
  {
    TextView localTextView = getHeaderTextView();
    if (localTextView != null) {
      localTextView.setText(paramCharSequence);
    }
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    ImageView localImageView = getIconView();
    if (localImageView != null) {
      localImageView.setImageDrawable(paramDrawable);
    }
  }
  
  public void setPrimaryColor(ColorStateList paramColorStateList)
  {
    this.mPrimaryColor = paramColorStateList;
    setGlifPatternColor(paramColorStateList);
    setProgressBarColor(paramColorStateList);
  }
  
  public void setProgressBarShown(boolean paramBoolean)
  {
    ProgressBar localProgressBar;
    if (paramBoolean)
    {
      localProgressBar = getProgressBar();
      if (localProgressBar != null) {
        localProgressBar.setVisibility(0);
      }
    }
    do
    {
      return;
      localProgressBar = peekProgressBar();
    } while (localProgressBar == null);
    localProgressBar.setVisibility(8);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\GlifLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */