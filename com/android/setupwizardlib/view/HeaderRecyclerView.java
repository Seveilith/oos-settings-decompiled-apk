package com.android.setupwizardlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.android.setupwizardlib.DividerItemDecoration.DividedViewHolder;
import com.android.setupwizardlib.R.styleable;

public class HeaderRecyclerView
  extends RecyclerView
{
  private View mHeader;
  private int mHeaderRes;
  
  public HeaderRecyclerView(Context paramContext)
  {
    super(paramContext);
    init(null, 0);
  }
  
  public HeaderRecyclerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramAttributeSet, 0);
  }
  
  public HeaderRecyclerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramAttributeSet, paramInt);
  }
  
  private void init(AttributeSet paramAttributeSet, int paramInt)
  {
    paramAttributeSet = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.SuwHeaderRecyclerView, paramInt, 0);
    this.mHeaderRes = paramAttributeSet.getResourceId(R.styleable.SuwHeaderRecyclerView_suwHeader, 0);
    paramAttributeSet.recycle();
  }
  
  public View getHeader()
  {
    return this.mHeader;
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    if (this.mHeader != null) {}
    for (int i = 1;; i = 0)
    {
      paramAccessibilityEvent.setItemCount(paramAccessibilityEvent.getItemCount() - i);
      paramAccessibilityEvent.setFromIndex(Math.max(paramAccessibilityEvent.getFromIndex() - i, 0));
      if (Build.VERSION.SDK_INT >= 14) {
        paramAccessibilityEvent.setToIndex(Math.max(paramAccessibilityEvent.getToIndex() - i, 0));
      }
      return;
    }
  }
  
  public void setAdapter(RecyclerView.Adapter paramAdapter)
  {
    Object localObject = paramAdapter;
    if (this.mHeader != null)
    {
      localObject = paramAdapter;
      if (paramAdapter != null)
      {
        localObject = new HeaderAdapter(paramAdapter);
        ((HeaderAdapter)localObject).setHeader(this.mHeader);
      }
    }
    super.setAdapter((RecyclerView.Adapter)localObject);
  }
  
  public void setHeader(View paramView)
  {
    this.mHeader = paramView;
  }
  
  public void setLayoutManager(RecyclerView.LayoutManager paramLayoutManager)
  {
    super.setLayoutManager(paramLayoutManager);
    if ((paramLayoutManager != null) && (this.mHeader == null) && (this.mHeaderRes != 0)) {
      this.mHeader = LayoutInflater.from(getContext()).inflate(this.mHeaderRes, this, false);
    }
  }
  
  public static class HeaderAdapter
    extends RecyclerView.Adapter<RecyclerView.ViewHolder>
  {
    private static final int HEADER_VIEW_TYPE = Integer.MAX_VALUE;
    private RecyclerView.Adapter mAdapter;
    private View mHeader;
    private final RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver()
    {
      public void onChanged()
      {
        HeaderRecyclerView.HeaderAdapter.this.notifyDataSetChanged();
      }
      
      public void onItemRangeChanged(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        HeaderRecyclerView.HeaderAdapter.this.notifyItemRangeChanged(paramAnonymousInt1, paramAnonymousInt2);
      }
      
      public void onItemRangeInserted(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        HeaderRecyclerView.HeaderAdapter.this.notifyItemRangeInserted(paramAnonymousInt1, paramAnonymousInt2);
      }
      
      public void onItemRangeMoved(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        HeaderRecyclerView.HeaderAdapter.this.notifyDataSetChanged();
      }
      
      public void onItemRangeRemoved(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        HeaderRecyclerView.HeaderAdapter.this.notifyItemRangeRemoved(paramAnonymousInt1, paramAnonymousInt2);
      }
    };
    
    public HeaderAdapter(RecyclerView.Adapter paramAdapter)
    {
      this.mAdapter = paramAdapter;
      this.mAdapter.registerAdapterDataObserver(this.mObserver);
      setHasStableIds(this.mAdapter.hasStableIds());
    }
    
    public int getItemCount()
    {
      int j = this.mAdapter.getItemCount();
      int i = j;
      if (this.mHeader != null) {
        i = j + 1;
      }
      return i;
    }
    
    public long getItemId(int paramInt)
    {
      int i = paramInt;
      if (this.mHeader != null) {
        i = paramInt - 1;
      }
      if (i < 0) {
        return Long.MAX_VALUE;
      }
      return this.mAdapter.getItemId(i);
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = paramInt;
      if (this.mHeader != null) {
        i = paramInt - 1;
      }
      if (i < 0) {
        return Integer.MAX_VALUE;
      }
      return this.mAdapter.getItemViewType(i);
    }
    
    public RecyclerView.Adapter getWrappedAdapter()
    {
      return this.mAdapter;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      int i = paramInt;
      if (this.mHeader != null) {
        i = paramInt - 1;
      }
      if ((paramViewHolder instanceof HeaderRecyclerView.HeaderViewHolder))
      {
        if (this.mHeader.getParent() != null) {
          ((ViewGroup)this.mHeader.getParent()).removeView(this.mHeader);
        }
        ((FrameLayout)paramViewHolder.itemView).addView(this.mHeader);
        return;
      }
      this.mAdapter.onBindViewHolder(paramViewHolder, i);
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      if (paramInt == Integer.MAX_VALUE)
      {
        paramViewGroup = new FrameLayout(paramViewGroup.getContext());
        paramViewGroup.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        return new HeaderRecyclerView.HeaderViewHolder(paramViewGroup);
      }
      return this.mAdapter.onCreateViewHolder(paramViewGroup, paramInt);
    }
    
    public void setHeader(View paramView)
    {
      this.mHeader = paramView;
    }
  }
  
  private static class HeaderViewHolder
    extends RecyclerView.ViewHolder
    implements DividerItemDecoration.DividedViewHolder
  {
    public HeaderViewHolder(View paramView)
    {
      super();
    }
    
    public boolean isDividerAllowedAbove()
    {
      return false;
    }
    
    public boolean isDividerAllowedBelow()
    {
      return false;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\view\HeaderRecyclerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */