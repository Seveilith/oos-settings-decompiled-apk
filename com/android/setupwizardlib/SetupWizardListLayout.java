package com.android.setupwizardlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.setupwizardlib.util.DrawableLayoutDirectionHelper;
import com.android.setupwizardlib.util.ListViewRequireScrollHelper;
import com.android.setupwizardlib.view.NavigationBar;

public class SetupWizardListLayout
  extends SetupWizardLayout
{
  private static final String TAG = "SetupWizardListLayout";
  private Drawable mDefaultDivider;
  private Drawable mDivider;
  private int mDividerInset;
  private ListView mListView;
  
  public SetupWizardListLayout(Context paramContext)
  {
    this(paramContext, 0, 0);
  }
  
  public SetupWizardListLayout(Context paramContext, int paramInt)
  {
    this(paramContext, paramInt, 0);
  }
  
  public SetupWizardListLayout(Context paramContext, int paramInt1, int paramInt2)
  {
    super(paramContext, paramInt1, paramInt2);
    init(paramContext, null, 0);
  }
  
  public SetupWizardListLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext, paramAttributeSet, 0);
  }
  
  @TargetApi(11)
  public SetupWizardListLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext, paramAttributeSet, paramInt);
  }
  
  private void init(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SuwSetupWizardListLayout, paramInt, 0);
    setDividerInset(paramContext.getDimensionPixelSize(R.styleable.SuwSetupWizardListLayout_suwDividerInset, 0));
    paramContext.recycle();
  }
  
  private void updateDivider()
  {
    boolean bool = true;
    if (Build.VERSION.SDK_INT >= 19) {
      bool = isLayoutDirectionResolved();
    }
    if (bool)
    {
      ListView localListView = getListView();
      if (this.mDefaultDivider == null) {
        this.mDefaultDivider = localListView.getDivider();
      }
      this.mDivider = DrawableLayoutDirectionHelper.createRelativeInsetDrawable(this.mDefaultDivider, this.mDividerInset, 0, 0, 0, this);
      localListView.setDivider(this.mDivider);
    }
  }
  
  protected ViewGroup findContainer(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = 16908298;
    }
    return super.findContainer(i);
  }
  
  public Drawable getDivider()
  {
    return this.mDivider;
  }
  
  public int getDividerInset()
  {
    return this.mDividerInset;
  }
  
  public ListView getListView()
  {
    return this.mListView;
  }
  
  protected View onInflateTemplate(LayoutInflater paramLayoutInflater, int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = R.layout.suw_list_template;
    }
    return super.onInflateTemplate(paramLayoutInflater, i);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mDivider == null) {
      updateDivider();
    }
  }
  
  protected void onTemplateInflated()
  {
    this.mListView = ((ListView)findViewById(16908298));
  }
  
  public void requireScrollToBottom()
  {
    NavigationBar localNavigationBar = getNavigationBar();
    ListView localListView = getListView();
    if ((localNavigationBar != null) && (localListView != null))
    {
      ListViewRequireScrollHelper.requireScroll(localNavigationBar, localListView);
      return;
    }
    Log.e("SetupWizardListLayout", "Both suw_layout_navigation_bar and list must exist in the template to require scrolling.");
  }
  
  public void setAdapter(ListAdapter paramListAdapter)
  {
    getListView().setAdapter(paramListAdapter);
  }
  
  public void setDividerInset(int paramInt)
  {
    this.mDividerInset = paramInt;
    updateDivider();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\SetupWizardListLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */