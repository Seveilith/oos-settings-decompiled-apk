package com.android.setupwizardlib.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.android.setupwizardlib.R.styleable;

public class RecyclerItemAdapter
  extends RecyclerView.Adapter<ItemViewHolder>
  implements ItemHierarchy.Observer
{
  private static final String TAG = "RecyclerItemAdapter";
  private final ItemHierarchy mItemHierarchy;
  private OnItemSelectedListener mListener;
  
  public RecyclerItemAdapter(ItemHierarchy paramItemHierarchy)
  {
    this.mItemHierarchy = paramItemHierarchy;
    this.mItemHierarchy.registerObserver(this);
  }
  
  public ItemHierarchy findItemById(int paramInt)
  {
    return this.mItemHierarchy.findItemById(paramInt);
  }
  
  public IItem getItem(int paramInt)
  {
    return this.mItemHierarchy.getItemAt(paramInt);
  }
  
  public int getItemCount()
  {
    return this.mItemHierarchy.getCount();
  }
  
  public long getItemId(int paramInt)
  {
    long l = -1L;
    IItem localIItem = getItem(paramInt);
    if ((localIItem instanceof AbstractItem))
    {
      paramInt = ((AbstractItem)localIItem).getId();
      if (paramInt > 0) {
        l = paramInt;
      }
      return l;
    }
    return -1L;
  }
  
  public int getItemViewType(int paramInt)
  {
    return getItem(paramInt).getLayoutResource();
  }
  
  public ItemHierarchy getRootItemHierarchy()
  {
    return this.mItemHierarchy;
  }
  
  public void onBindViewHolder(ItemViewHolder paramItemViewHolder, int paramInt)
  {
    IItem localIItem = getItem(paramInt);
    localIItem.onBindView(paramItemViewHolder.itemView);
    paramItemViewHolder.setEnabled(localIItem.isEnabled());
    paramItemViewHolder.setItem(localIItem);
  }
  
  public void onChanged(ItemHierarchy paramItemHierarchy)
  {
    notifyDataSetChanged();
  }
  
  public ItemViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(paramInt, paramViewGroup, false);
    final ItemViewHolder localItemViewHolder = new ItemViewHolder(localView);
    TypedArray localTypedArray = paramViewGroup.getContext().obtainStyledAttributes(R.styleable.SuwRecyclerItemAdapter);
    Drawable localDrawable = localTypedArray.getDrawable(R.styleable.SuwRecyclerItemAdapter_android_selectableItemBackground);
    paramViewGroup = localDrawable;
    if (localDrawable == null) {
      paramViewGroup = localTypedArray.getDrawable(R.styleable.SuwRecyclerItemAdapter_selectableItemBackground);
    }
    localDrawable = localTypedArray.getDrawable(R.styleable.SuwRecyclerItemAdapter_android_colorBackground);
    if ((paramViewGroup == null) || (localDrawable == null)) {
      Log.e("RecyclerItemAdapter", "Cannot resolve required attributes. selectableItemBackground=" + paramViewGroup + " background=" + localDrawable);
    }
    for (;;)
    {
      localTypedArray.recycle();
      localView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = localItemViewHolder.getItem();
          if ((RecyclerItemAdapter.-get0(RecyclerItemAdapter.this) != null) && (paramAnonymousView != null) && (paramAnonymousView.isEnabled())) {
            RecyclerItemAdapter.-get0(RecyclerItemAdapter.this).onItemSelected(paramAnonymousView);
          }
        }
      });
      return localItemViewHolder;
      localView.setBackgroundDrawable(new LayerDrawable(new Drawable[] { localDrawable, paramViewGroup }));
    }
  }
  
  public void setOnItemSelectedListener(OnItemSelectedListener paramOnItemSelectedListener)
  {
    this.mListener = paramOnItemSelectedListener;
  }
  
  public static abstract interface OnItemSelectedListener
  {
    public abstract void onItemSelected(IItem paramIItem);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\RecyclerItemAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */