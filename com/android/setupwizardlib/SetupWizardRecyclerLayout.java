package com.android.setupwizardlib;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.setupwizardlib.items.ItemGroup;
import com.android.setupwizardlib.items.ItemInflater;
import com.android.setupwizardlib.items.RecyclerItemAdapter;
import com.android.setupwizardlib.util.DrawableLayoutDirectionHelper;
import com.android.setupwizardlib.util.RecyclerViewRequireScrollHelper;
import com.android.setupwizardlib.view.HeaderRecyclerView;
import com.android.setupwizardlib.view.NavigationBar;

public class SetupWizardRecyclerLayout
  extends SetupWizardLayout
{
  private static final String TAG = "RecyclerLayout";
  private RecyclerView.Adapter mAdapter;
  private Drawable mDefaultDivider;
  private Drawable mDivider;
  private DividerItemDecoration mDividerDecoration;
  private int mDividerInset;
  private View mHeader;
  private RecyclerView mRecyclerView;
  
  public SetupWizardRecyclerLayout(Context paramContext)
  {
    this(paramContext, 0, 0);
  }
  
  public SetupWizardRecyclerLayout(Context paramContext, int paramInt1, int paramInt2)
  {
    super(paramContext, paramInt1, paramInt2);
    init(paramContext, null, 0);
  }
  
  public SetupWizardRecyclerLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext, paramAttributeSet, 0);
  }
  
  public SetupWizardRecyclerLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext, paramAttributeSet, paramInt);
  }
  
  private void init(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SuwSetupWizardRecyclerItemsLayout, paramInt, 0);
    paramInt = paramAttributeSet.getResourceId(R.styleable.SuwSetupWizardRecyclerItemsLayout_android_entries, 0);
    if (paramInt != 0)
    {
      this.mAdapter = new RecyclerItemAdapter((ItemGroup)new ItemInflater(paramContext).inflate(paramInt));
      this.mAdapter.setHasStableIds(paramAttributeSet.getBoolean(R.styleable.SuwSetupWizardRecyclerItemsLayout_suwHasStableIds, false));
      setAdapter(this.mAdapter);
    }
    int i = paramAttributeSet.getDimensionPixelSize(R.styleable.SuwSetupWizardRecyclerItemsLayout_suwDividerInset, 0);
    paramInt = i;
    if (i == 0) {
      paramInt = getResources().getDimensionPixelSize(R.dimen.suw_items_icon_divider_inset);
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
      if (this.mDefaultDivider == null) {
        this.mDefaultDivider = this.mDividerDecoration.getDivider();
      }
      this.mDivider = DrawableLayoutDirectionHelper.createRelativeInsetDrawable(this.mDefaultDivider, this.mDividerInset, 0, 0, 0, this);
      this.mDividerDecoration.setDivider(this.mDivider);
    }
  }
  
  protected ViewGroup findContainer(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = R.id.suw_recycler_view;
    }
    return super.findContainer(i);
  }
  
  protected View findManagedViewById(int paramInt)
  {
    if (this.mHeader != null)
    {
      View localView = this.mHeader.findViewById(paramInt);
      if (localView != null) {
        return localView;
      }
    }
    return super.findViewById(paramInt);
  }
  
  public RecyclerView.Adapter getAdapter()
  {
    return this.mAdapter;
  }
  
  public Drawable getDivider()
  {
    return this.mDivider;
  }
  
  public int getDividerInset()
  {
    return this.mDividerInset;
  }
  
  public RecyclerView getRecyclerView()
  {
    return this.mRecyclerView;
  }
  
  protected void initRecyclerView(RecyclerView paramRecyclerView)
  {
    this.mRecyclerView = paramRecyclerView;
    this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    if ((this.mRecyclerView instanceof HeaderRecyclerView)) {
      this.mHeader = ((HeaderRecyclerView)this.mRecyclerView).getHeader();
    }
    this.mDividerDecoration = new DividerItemDecoration(getContext());
    this.mRecyclerView.addItemDecoration(this.mDividerDecoration);
  }
  
  protected View onInflateTemplate(LayoutInflater paramLayoutInflater, int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = R.layout.suw_recycler_template;
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
    initRecyclerView((RecyclerView)findViewById(R.id.suw_recycler_view));
  }
  
  public void requireScrollToBottom()
  {
    NavigationBar localNavigationBar = getNavigationBar();
    RecyclerView localRecyclerView = getRecyclerView();
    if ((localNavigationBar != null) && (localRecyclerView != null))
    {
      RecyclerViewRequireScrollHelper.requireScroll(localNavigationBar, localRecyclerView);
      return;
    }
    Log.e("RecyclerLayout", "Both suw_layout_navigation_bar and suw_recycler_view must exist in the template to require scrolling.");
  }
  
  public void setAdapter(RecyclerView.Adapter paramAdapter)
  {
    this.mAdapter = paramAdapter;
    getRecyclerView().setAdapter(paramAdapter);
  }
  
  public void setDividerInset(int paramInt)
  {
    this.mDividerInset = paramInt;
    updateDivider();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\SetupWizardRecyclerLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */