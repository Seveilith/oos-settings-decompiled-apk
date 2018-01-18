package com.android.settings.localepicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.ItemAnimator.ItemAnimatorFinishedListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocaleStore.LocaleInfo;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

class LocaleDragAndDropAdapter
  extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
  private static final String CFGKEY_SELECTED_LOCALES = "selectedLocales";
  private static final String TAG = "LocaleDragAndDropAdapter";
  private final Context mContext;
  private boolean mDragEnabled = true;
  private final List<LocaleStore.LocaleInfo> mFeedItemList;
  private final ItemTouchHelper mItemTouchHelper;
  private View.OnClickListener mListener;
  private LocaleList mLocalesSetLast = null;
  private LocaleList mLocalesToSetNext = null;
  private NumberFormat mNumberFormatter = NumberFormat.getNumberInstance();
  private RecyclerView mParentView = null;
  private boolean mRemoveMode = false;
  
  public LocaleDragAndDropAdapter(Context paramContext, List<LocaleStore.LocaleInfo> paramList, View.OnClickListener paramOnClickListener)
  {
    this.mFeedItemList = paramList;
    this.mContext = paramContext;
    this.mListener = paramOnClickListener;
    this.mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(3, 0)
    {
      private static final int SELECTION_GAINED = 1;
      private static final int SELECTION_LOST = 0;
      private static final int SELECTION_UNCHANGED = -1;
      private int mSelectionStatus = -1;
      
      public int getMovementFlags(RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder)
      {
        if ((paramAnonymousViewHolder instanceof LocaleDragAndDropAdapter.ButtonViewHolder)) {
          return 0;
        }
        return super.getMovementFlags(paramAnonymousRecyclerView, paramAnonymousViewHolder);
      }
      
      public void onChildDraw(Canvas paramAnonymousCanvas, RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder, float paramAnonymousFloat1, float paramAnonymousFloat2, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        super.onChildDraw(paramAnonymousCanvas, paramAnonymousRecyclerView, paramAnonymousViewHolder, paramAnonymousFloat1, paramAnonymousFloat2, paramAnonymousInt, paramAnonymousBoolean);
        if (this.mSelectionStatus != -1)
        {
          paramAnonymousCanvas = paramAnonymousViewHolder.itemView;
          if (this.mSelectionStatus != 1) {
            break label54;
          }
        }
        label54:
        for (paramAnonymousFloat1 = this.val$dragElevation;; paramAnonymousFloat1 = 0.0F)
        {
          paramAnonymousCanvas.setElevation(paramAnonymousFloat1);
          this.mSelectionStatus = -1;
          return;
        }
      }
      
      public boolean onMove(RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder1, RecyclerView.ViewHolder paramAnonymousViewHolder2)
      {
        if ((!(paramAnonymousViewHolder1 instanceof LocaleDragAndDropAdapter.CustomViewHolder)) || ((paramAnonymousViewHolder2 instanceof LocaleDragAndDropAdapter.ButtonViewHolder))) {}
        for (;;)
        {
          return true;
          LocaleDragAndDropAdapter.this.onItemMove(paramAnonymousViewHolder1.getAdapterPosition(), paramAnonymousViewHolder2.getAdapterPosition());
        }
      }
      
      public void onSelectedChanged(RecyclerView.ViewHolder paramAnonymousViewHolder, int paramAnonymousInt)
      {
        super.onSelectedChanged(paramAnonymousViewHolder, paramAnonymousInt);
        if (paramAnonymousInt == 2) {
          this.mSelectionStatus = 1;
        }
        while (paramAnonymousInt != 0) {
          return;
        }
        this.mSelectionStatus = 0;
      }
      
      public void onSwiped(RecyclerView.ViewHolder paramAnonymousViewHolder, int paramAnonymousInt) {}
    });
  }
  
  private void setDragEnabled(boolean paramBoolean)
  {
    this.mDragEnabled = paramBoolean;
  }
  
  void addLocale(LocaleStore.LocaleInfo paramLocaleInfo)
  {
    if ((this.mFeedItemList.contains(paramLocaleInfo)) || ("zh-CN".equals(paramLocaleInfo.getId()))) {
      return;
    }
    this.mFeedItemList.add(paramLocaleInfo);
    notifyItemInserted(this.mFeedItemList.size() - 1);
    doTheUpdate();
  }
  
  public void doTheUpdate()
  {
    int j = this.mFeedItemList.size();
    Locale[] arrayOfLocale = new Locale[j];
    int i = 0;
    while (i < j)
    {
      arrayOfLocale[i] = ((LocaleStore.LocaleInfo)this.mFeedItemList.get(i)).getLocale();
      i += 1;
    }
    updateLocalesWhenAnimationStops(new LocaleList(arrayOfLocale));
  }
  
  int getCheckedCount()
  {
    int i = 0;
    Iterator localIterator = this.mFeedItemList.iterator();
    while (localIterator.hasNext()) {
      if (((LocaleStore.LocaleInfo)localIterator.next()).getChecked()) {
        i += 1;
      }
    }
    return i;
  }
  
  LocaleStore.LocaleInfo getFirstChecked()
  {
    Iterator localIterator = this.mFeedItemList.iterator();
    while (localIterator.hasNext())
    {
      LocaleStore.LocaleInfo localLocaleInfo = (LocaleStore.LocaleInfo)localIterator.next();
      if (localLocaleInfo.getChecked()) {
        return localLocaleInfo;
      }
    }
    return null;
  }
  
  public int getItemCount()
  {
    if (this.mRemoveMode)
    {
      if (this.mFeedItemList != null) {}
      for (i = this.mFeedItemList.size(); (i < 2) || (this.mRemoveMode); i = 0)
      {
        setDragEnabled(false);
        return i;
      }
      setDragEnabled(true);
      return i;
    }
    if (this.mFeedItemList != null) {}
    for (int i = this.mFeedItemList.size() + 1; (i < 3) || (this.mRemoveMode); i = 0)
    {
      setDragEnabled(false);
      return i;
    }
    setDragEnabled(true);
    return i;
  }
  
  public int getItemViewType(int paramInt)
  {
    if (paramInt == this.mFeedItemList.size()) {
      return ITEM_TYPE.ITEM2.ordinal();
    }
    return ITEM_TYPE.ITEM1.ordinal();
  }
  
  boolean isRemoveMode()
  {
    return this.mRemoveMode;
  }
  
  public void onBindViewHolder(final RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    boolean bool2 = false;
    if ((paramViewHolder instanceof CustomViewHolder))
    {
      localLocaleInfo = (LocaleStore.LocaleInfo)this.mFeedItemList.get(paramInt);
      paramViewHolder = ((CustomViewHolder)paramViewHolder).getLocaleDragCell();
      paramViewHolder.setLabelAndDescription(localLocaleInfo.getFullNameNative(), localLocaleInfo.getFullNameInUiLanguage());
      paramViewHolder.setLocalized(localLocaleInfo.isTranslated());
      paramViewHolder.setMiniLabel(this.mNumberFormatter.format(paramInt + 1));
      paramViewHolder.setShowCheckbox(this.mRemoveMode);
      if (this.mRemoveMode)
      {
        bool1 = false;
        paramViewHolder.setShowMiniLabel(bool1);
        if (this.mRemoveMode) {
          break label159;
        }
        bool1 = this.mDragEnabled;
        paramViewHolder.setShowHandle(bool1);
        bool1 = bool2;
        if (this.mRemoveMode) {
          bool1 = localLocaleInfo.getChecked();
        }
        paramViewHolder.setChecked(bool1);
        paramViewHolder.setTag(localLocaleInfo);
        paramViewHolder.getCheckbox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
          public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
          {
            ((LocaleStore.LocaleInfo)paramViewHolder.getTag()).setChecked(paramAnonymousBoolean);
          }
        });
      }
    }
    label159:
    while (!(paramViewHolder instanceof ButtonViewHolder)) {
      for (;;)
      {
        LocaleStore.LocaleInfo localLocaleInfo;
        return;
        boolean bool1 = true;
        continue;
        bool1 = false;
      }
    }
    ButtonViewHolder.-get0((ButtonViewHolder)paramViewHolder).setOnClickListener(this.mListener);
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    if (paramInt == ITEM_TYPE.ITEM1.ordinal()) {
      return new CustomViewHolder((LocaleDragCell)LayoutInflater.from(this.mContext).inflate(2130968740, paramViewGroup, false));
    }
    return new ButtonViewHolder(LayoutInflater.from(this.mContext).inflate(2130968824, paramViewGroup, false), null);
  }
  
  void onItemMove(int paramInt1, int paramInt2)
  {
    if ((paramInt1 >= 0) && (paramInt2 >= 0))
    {
      LocaleStore.LocaleInfo localLocaleInfo = (LocaleStore.LocaleInfo)this.mFeedItemList.get(paramInt1);
      this.mFeedItemList.remove(paramInt1);
      this.mFeedItemList.add(paramInt2, localLocaleInfo);
    }
    for (;;)
    {
      notifyItemChanged(paramInt1);
      notifyItemChanged(paramInt2);
      notifyItemMoved(paramInt1, paramInt2);
      return;
      Log.e("LocaleDragAndDropAdapter", String.format(Locale.US, "Negative position in onItemMove %d -> %d", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }));
    }
  }
  
  void removeChecked()
  {
    int i = this.mFeedItemList.size() - 1;
    while (i >= 0)
    {
      if (((LocaleStore.LocaleInfo)this.mFeedItemList.get(i)).getChecked()) {
        this.mFeedItemList.remove(i);
      }
      i -= 1;
    }
    notifyDataSetChanged();
    doTheUpdate();
  }
  
  void removeItem(int paramInt)
  {
    int i = this.mFeedItemList.size();
    if (i <= 1) {
      return;
    }
    if ((paramInt < 0) || (paramInt >= i)) {
      return;
    }
    this.mFeedItemList.remove(paramInt);
    notifyDataSetChanged();
  }
  
  public void restoreState(Bundle paramBundle)
  {
    if ((paramBundle != null) && (this.mRemoveMode))
    {
      paramBundle = paramBundle.getStringArrayList("selectedLocales");
      if ((paramBundle == null) || (paramBundle.isEmpty())) {
        return;
      }
      Iterator localIterator = this.mFeedItemList.iterator();
      while (localIterator.hasNext())
      {
        LocaleStore.LocaleInfo localLocaleInfo = (LocaleStore.LocaleInfo)localIterator.next();
        localLocaleInfo.setChecked(paramBundle.contains(localLocaleInfo.getId()));
      }
      notifyItemRangeChanged(0, this.mFeedItemList.size());
    }
  }
  
  public void saveState(Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = this.mFeedItemList.iterator();
      while (localIterator.hasNext())
      {
        LocaleStore.LocaleInfo localLocaleInfo = (LocaleStore.LocaleInfo)localIterator.next();
        if (localLocaleInfo.getChecked()) {
          localArrayList.add(localLocaleInfo.getId());
        }
      }
      paramBundle.putStringArrayList("selectedLocales", localArrayList);
    }
  }
  
  public void setRecyclerView(RecyclerView paramRecyclerView)
  {
    this.mParentView = paramRecyclerView;
    this.mItemTouchHelper.attachToRecyclerView(paramRecyclerView);
  }
  
  void setRemoveMode(boolean paramBoolean)
  {
    this.mRemoveMode = paramBoolean;
    int j = this.mFeedItemList.size();
    int i = 0;
    while (i < j)
    {
      ((LocaleStore.LocaleInfo)this.mFeedItemList.get(i)).setChecked(false);
      i += 1;
    }
    notifyDataSetChanged();
  }
  
  public void updateLocalesWhenAnimationStops(LocaleList paramLocaleList)
  {
    if (paramLocaleList.equals(this.mLocalesToSetNext)) {
      return;
    }
    LocaleList.setDefault(paramLocaleList);
    this.mLocalesToSetNext = paramLocaleList;
    this.mParentView.getItemAnimator().isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener()
    {
      public void onAnimationsFinished()
      {
        if ((LocaleDragAndDropAdapter.-get3(LocaleDragAndDropAdapter.this) == null) || (LocaleDragAndDropAdapter.-get3(LocaleDragAndDropAdapter.this).equals(LocaleDragAndDropAdapter.-get2(LocaleDragAndDropAdapter.this)))) {
          return;
        }
        LocalePicker.updateLocales(LocaleDragAndDropAdapter.-get3(LocaleDragAndDropAdapter.this));
        LocaleDragAndDropAdapter.-set0(LocaleDragAndDropAdapter.this, LocaleDragAndDropAdapter.-get3(LocaleDragAndDropAdapter.this));
        LocaleDragAndDropAdapter.-set1(LocaleDragAndDropAdapter.this, null);
        LocaleDragAndDropAdapter.-set2(LocaleDragAndDropAdapter.this, NumberFormat.getNumberInstance(Locale.getDefault()));
      }
    });
  }
  
  private class ButtonViewHolder
    extends RecyclerView.ViewHolder
  {
    private Button button;
    
    private ButtonViewHolder(View paramView)
    {
      super();
      this.button = ((Button)paramView.findViewById(2131362360));
    }
  }
  
  private class CustomViewHolder
    extends RecyclerView.ViewHolder
    implements View.OnTouchListener
  {
    private final LocaleDragCell mLocaleDragCell;
    
    public CustomViewHolder(LocaleDragCell paramLocaleDragCell)
    {
      super();
      this.mLocaleDragCell = paramLocaleDragCell;
      this.mLocaleDragCell.getDragHandle().setOnTouchListener(this);
    }
    
    public LocaleDragCell getLocaleDragCell()
    {
      return this.mLocaleDragCell;
    }
    
    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
      if (LocaleDragAndDropAdapter.-get0(LocaleDragAndDropAdapter.this)) {
        switch (MotionEventCompat.getActionMasked(paramMotionEvent))
        {
        }
      }
      for (;;)
      {
        return false;
        LocaleDragAndDropAdapter.-get1(LocaleDragAndDropAdapter.this).startDrag(this);
      }
    }
  }
  
  public static enum ITEM_TYPE
  {
    ITEM1,  ITEM2;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\localepicker\LocaleDragAndDropAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */