package com.android.setupwizardlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.setupwizardlib.items.ItemAdapter;
import com.android.setupwizardlib.items.ItemGroup;
import com.android.setupwizardlib.items.ItemInflater;
import com.android.setupwizardlib.util.DrawableLayoutDirectionHelper;

public class GlifListLayout
  extends GlifLayout
{
  private static final String TAG = "GlifListLayout";
  private Drawable mDefaultDivider;
  private Drawable mDivider;
  private int mDividerInset;
  private ListView mListView;
  
  public GlifListLayout(Context paramContext)
  {
    this(paramContext, 0, 0);
  }
  
  public GlifListLayout(Context paramContext, int paramInt)
  {
    this(paramContext, paramInt, 0);
  }
  
  public GlifListLayout(Context paramContext, int paramInt1, int paramInt2)
  {
    super(paramContext, paramInt1, paramInt2);
    init(paramContext, null, 0);
  }
  
  public GlifListLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext, paramAttributeSet, 0);
  }
  
  @TargetApi(11)
  public GlifListLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext, paramAttributeSet, paramInt);
  }
  
  private void init(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SuwGlifListLayout, paramInt, 0);
    paramInt = paramAttributeSet.getResourceId(R.styleable.SuwGlifListLayout_android_entries, 0);
    if (paramInt != 0) {
      setAdapter(new ItemAdapter((ItemGroup)new ItemInflater(paramContext).inflate(paramInt)));
    }
    int i = paramAttributeSet.getDimensionPixelSize(R.styleable.SuwGlifListLayout_suwDividerInset, 0);
    paramInt = i;
    if (i == 0) {
      paramInt = getResources().getDimensionPixelSize(R.dimen.suw_items_glif_icon_divider_inset);
    }
    setDividerInset(paramInt);
    paramAttributeSet.recycle();
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
  
  public ListAdapter getAdapter()
  {
    ListAdapter localListAdapter = getListView().getAdapter();
    if ((localListAdapter instanceof HeaderViewListAdapter)) {
      return ((HeaderViewListAdapter)localListAdapter).getWrappedAdapter();
    }
    return localListAdapter;
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
      i = R.layout.suw_glif_list_template;
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


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\GlifListLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */