package com.oneplus.lib.widget.recyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultItemAnimator
  extends RecyclerView.ItemAnimator
{
  private static final boolean DEBUG = true;
  private ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList();
  private ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList();
  private ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList();
  private ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList();
  private TimeInterpolator mDefaultInterpolator;
  private ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList();
  private ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList();
  private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList();
  private ArrayList<ChangeInfo> mPendingChanges = new ArrayList();
  private ArrayList<MoveInfo> mPendingMoves = new ArrayList();
  private ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList();
  private ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList();
  
  private void animateAddImpl(final RecyclerView.ViewHolder paramViewHolder)
  {
    ViewPropertyAnimator localViewPropertyAnimator = paramViewHolder.itemView.animate();
    this.mAddAnimations.add(paramViewHolder);
    localViewPropertyAnimator.alpha(1.0F).setDuration(getAddDuration()).setListener(new AnimatorListenerAdapter()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        DefaultItemAnimator.this.dispatchAddStarting(paramViewHolder);
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        paramAnonymousAnimator.removeAllListeners();
        DefaultItemAnimator.this.dispatchAddFinished(paramViewHolder);
        DefaultItemAnimator.-get0(DefaultItemAnimator.this).remove(paramViewHolder);
        DefaultItemAnimator.-wrap3(DefaultItemAnimator.this);
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        DefaultItemAnimator.this.dispatchAddStarting(paramViewHolder);
      }
    }).start();
  }
  
  private void animateChangeImpl(final ChangeInfo paramChangeInfo)
  {
    final Object localObject1 = paramChangeInfo.oldHolder;
    if (localObject1 == null)
    {
      localObject1 = null;
      localObject2 = paramChangeInfo.newHolder;
      if (localObject2 == null) {
        break label177;
      }
    }
    label177:
    for (final Object localObject2 = ((RecyclerView.ViewHolder)localObject2).itemView;; localObject2 = null)
    {
      if (localObject1 != null)
      {
        final ViewPropertyAnimator localViewPropertyAnimator = ((View)localObject1).animate().setDuration(getChangeDuration());
        this.mChangeAnimations.add(paramChangeInfo.oldHolder);
        localViewPropertyAnimator.translationX(paramChangeInfo.toX - paramChangeInfo.fromX);
        localViewPropertyAnimator.translationY(paramChangeInfo.toY - paramChangeInfo.fromY);
        localViewPropertyAnimator.alpha(0.0F).setListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            localViewPropertyAnimator.setListener(null);
            localObject1.setAlpha(1.0F);
            localObject1.setTranslationX(0.0F);
            localObject1.setTranslationY(0.0F);
            DefaultItemAnimator.this.dispatchChangeFinished(paramChangeInfo.oldHolder, true);
            DefaultItemAnimator.-get2(DefaultItemAnimator.this).remove(paramChangeInfo.oldHolder);
            DefaultItemAnimator.-wrap3(DefaultItemAnimator.this);
          }
          
          public void onAnimationStart(Animator paramAnonymousAnimator)
          {
            DefaultItemAnimator.this.dispatchChangeStarting(paramChangeInfo.oldHolder, true);
          }
        }).start();
      }
      if (localObject2 != null)
      {
        localObject1 = ((View)localObject2).animate();
        this.mChangeAnimations.add(paramChangeInfo.newHolder);
        ((ViewPropertyAnimator)localObject1).translationX(0.0F).translationY(0.0F).setDuration(getChangeDuration()).alpha(1.0F).setListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            localObject1.setListener(null);
            localObject2.setAlpha(1.0F);
            localObject2.setTranslationX(0.0F);
            localObject2.setTranslationY(0.0F);
            DefaultItemAnimator.this.dispatchChangeFinished(paramChangeInfo.newHolder, false);
            DefaultItemAnimator.-get2(DefaultItemAnimator.this).remove(paramChangeInfo.newHolder);
            DefaultItemAnimator.-wrap3(DefaultItemAnimator.this);
          }
          
          public void onAnimationStart(Animator paramAnonymousAnimator)
          {
            DefaultItemAnimator.this.dispatchChangeStarting(paramChangeInfo.newHolder, false);
          }
        }).start();
      }
      return;
      localObject1 = ((RecyclerView.ViewHolder)localObject1).itemView;
      break;
    }
  }
  
  private void animateMoveImpl(final RecyclerView.ViewHolder paramViewHolder, final int paramInt1, final int paramInt2, int paramInt3, int paramInt4)
  {
    final View localView = paramViewHolder.itemView;
    paramInt1 = paramInt3 - paramInt1;
    paramInt2 = paramInt4 - paramInt2;
    if (paramInt1 != 0) {
      localView.animate().translationX(0.0F);
    }
    if (paramInt2 != 0) {
      localView.animate().translationY(0.0F);
    }
    ViewPropertyAnimator localViewPropertyAnimator = localView.animate();
    this.mMoveAnimations.add(paramViewHolder);
    localViewPropertyAnimator.setDuration(getMoveDuration()).setListener(new AnimatorListenerAdapter()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        if (paramInt1 != 0) {
          localView.setTranslationX(0.0F);
        }
        if (paramInt2 != 0) {
          localView.setTranslationY(0.0F);
        }
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        paramAnonymousAnimator.removeAllListeners();
        DefaultItemAnimator.this.dispatchMoveFinished(paramViewHolder);
        DefaultItemAnimator.-get4(DefaultItemAnimator.this).remove(paramViewHolder);
        DefaultItemAnimator.-wrap3(DefaultItemAnimator.this);
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        DefaultItemAnimator.this.dispatchMoveStarting(paramViewHolder);
      }
    }).start();
  }
  
  private void animateRemoveImpl(final RecyclerView.ViewHolder paramViewHolder)
  {
    final View localView = paramViewHolder.itemView;
    ViewPropertyAnimator localViewPropertyAnimator = localView.animate();
    this.mRemoveAnimations.add(paramViewHolder);
    localViewPropertyAnimator.setDuration(getRemoveDuration()).alpha(0.0F).setListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        paramAnonymousAnimator.removeAllListeners();
        localView.setAlpha(1.0F);
        DefaultItemAnimator.this.dispatchRemoveFinished(paramViewHolder);
        DefaultItemAnimator.-get6(DefaultItemAnimator.this).remove(paramViewHolder);
        DefaultItemAnimator.-wrap3(DefaultItemAnimator.this);
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        DefaultItemAnimator.this.dispatchRemoveStarting(paramViewHolder);
      }
    }).start();
  }
  
  private void dispatchFinishedWhenDone()
  {
    if (!isRunning()) {
      dispatchAnimationsFinished();
    }
  }
  
  private void endChangeAnimation(List<ChangeInfo> paramList, RecyclerView.ViewHolder paramViewHolder)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      ChangeInfo localChangeInfo = (ChangeInfo)paramList.get(i);
      if ((endChangeAnimationIfNecessary(localChangeInfo, paramViewHolder)) && (localChangeInfo.oldHolder == null) && (localChangeInfo.newHolder == null)) {
        paramList.remove(localChangeInfo);
      }
      i -= 1;
    }
  }
  
  private void endChangeAnimationIfNecessary(ChangeInfo paramChangeInfo)
  {
    if (paramChangeInfo.oldHolder != null) {
      endChangeAnimationIfNecessary(paramChangeInfo, paramChangeInfo.oldHolder);
    }
    if (paramChangeInfo.newHolder != null) {
      endChangeAnimationIfNecessary(paramChangeInfo, paramChangeInfo.newHolder);
    }
  }
  
  private boolean endChangeAnimationIfNecessary(ChangeInfo paramChangeInfo, RecyclerView.ViewHolder paramViewHolder)
  {
    boolean bool = false;
    if (paramChangeInfo.newHolder == paramViewHolder) {
      paramChangeInfo.newHolder = null;
    }
    for (;;)
    {
      paramViewHolder.itemView.setAlpha(1.0F);
      paramViewHolder.itemView.setTranslationX(0.0F);
      paramViewHolder.itemView.setTranslationY(0.0F);
      dispatchChangeFinished(paramViewHolder, bool);
      return true;
      if (paramChangeInfo.oldHolder != paramViewHolder) {
        break;
      }
      paramChangeInfo.oldHolder = null;
      bool = true;
    }
    return false;
  }
  
  private void resetAnimation(RecyclerView.ViewHolder paramViewHolder)
  {
    if (this.mDefaultInterpolator == null) {
      this.mDefaultInterpolator = new ValueAnimator().getInterpolator();
    }
    paramViewHolder.itemView.animate().setInterpolator(this.mDefaultInterpolator);
    endAnimation(paramViewHolder);
  }
  
  public boolean animateAdd(RecyclerView.ViewHolder paramViewHolder)
  {
    resetAnimation(paramViewHolder);
    paramViewHolder.itemView.setAlpha(0.0F);
    this.mPendingAdditions.add(paramViewHolder);
    return true;
  }
  
  public boolean animateChange(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    float f1 = paramViewHolder1.itemView.getTranslationX();
    float f2 = paramViewHolder1.itemView.getTranslationY();
    float f3 = paramViewHolder1.itemView.getAlpha();
    resetAnimation(paramViewHolder1);
    int i = (int)(paramInt3 - paramInt1 - f1);
    int j = (int)(paramInt4 - paramInt2 - f2);
    paramViewHolder1.itemView.setTranslationX(f1);
    paramViewHolder1.itemView.setTranslationY(f2);
    paramViewHolder1.itemView.setAlpha(f3);
    if ((paramViewHolder2 != null) && (paramViewHolder2.itemView != null))
    {
      resetAnimation(paramViewHolder2);
      paramViewHolder2.itemView.setTranslationX(-i);
      paramViewHolder2.itemView.setTranslationY(-j);
      paramViewHolder2.itemView.setAlpha(0.0F);
    }
    this.mPendingChanges.add(new ChangeInfo(paramViewHolder1, paramViewHolder2, paramInt1, paramInt2, paramInt3, paramInt4, null));
    return true;
  }
  
  public boolean animateMove(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    View localView = paramViewHolder.itemView;
    paramInt1 = (int)(paramInt1 + paramViewHolder.itemView.getTranslationX());
    paramInt2 = (int)(paramInt2 + paramViewHolder.itemView.getTranslationY());
    resetAnimation(paramViewHolder);
    int i = paramInt3 - paramInt1;
    int j = paramInt4 - paramInt2;
    if ((i == 0) && (j == 0))
    {
      dispatchMoveFinished(paramViewHolder);
      return false;
    }
    if (i != 0) {
      localView.setTranslationX(-i);
    }
    if (j != 0) {
      localView.setTranslationY(-j);
    }
    this.mPendingMoves.add(new MoveInfo(paramViewHolder, paramInt1, paramInt2, paramInt3, paramInt4, null));
    return true;
  }
  
  public boolean animateRemove(RecyclerView.ViewHolder paramViewHolder)
  {
    resetAnimation(paramViewHolder);
    this.mPendingRemovals.add(paramViewHolder);
    return true;
  }
  
  void cancelAll(List<RecyclerView.ViewHolder> paramList)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      ((RecyclerView.ViewHolder)paramList.get(i)).itemView.animate().cancel();
      i -= 1;
    }
  }
  
  public void endAnimation(RecyclerView.ViewHolder paramViewHolder)
  {
    View localView = paramViewHolder.itemView;
    localView.animate().cancel();
    int i = this.mPendingMoves.size() - 1;
    while (i >= 0)
    {
      if (((MoveInfo)this.mPendingMoves.get(i)).holder == paramViewHolder)
      {
        localView.setTranslationY(0.0F);
        localView.setTranslationX(0.0F);
        dispatchMoveFinished(paramViewHolder);
        this.mPendingMoves.remove(i);
      }
      i -= 1;
    }
    endChangeAnimation(this.mPendingChanges, paramViewHolder);
    if (this.mPendingRemovals.remove(paramViewHolder))
    {
      localView.setAlpha(1.0F);
      dispatchRemoveFinished(paramViewHolder);
    }
    if (this.mPendingAdditions.remove(paramViewHolder))
    {
      localView.setAlpha(1.0F);
      dispatchAddFinished(paramViewHolder);
    }
    i = this.mChangesList.size() - 1;
    ArrayList localArrayList;
    while (i >= 0)
    {
      localArrayList = (ArrayList)this.mChangesList.get(i);
      endChangeAnimation(localArrayList, paramViewHolder);
      if (localArrayList.isEmpty()) {
        this.mChangesList.remove(i);
      }
      i -= 1;
    }
    i = this.mMovesList.size() - 1;
    if (i >= 0)
    {
      localArrayList = (ArrayList)this.mMovesList.get(i);
      int j = localArrayList.size() - 1;
      for (;;)
      {
        if (j >= 0)
        {
          if (((MoveInfo)localArrayList.get(j)).holder != paramViewHolder) {
            break label293;
          }
          localView.setTranslationY(0.0F);
          localView.setTranslationX(0.0F);
          dispatchMoveFinished(paramViewHolder);
          localArrayList.remove(j);
          if (localArrayList.isEmpty()) {
            this.mMovesList.remove(i);
          }
        }
        i -= 1;
        break;
        label293:
        j -= 1;
      }
    }
    i = this.mAdditionsList.size() - 1;
    while (i >= 0)
    {
      localArrayList = (ArrayList)this.mAdditionsList.get(i);
      if (localArrayList.remove(paramViewHolder))
      {
        localView.setAlpha(1.0F);
        dispatchAddFinished(paramViewHolder);
        if (localArrayList.isEmpty()) {
          this.mAdditionsList.remove(i);
        }
      }
      i -= 1;
    }
    if (this.mRemoveAnimations.remove(paramViewHolder)) {
      throw new IllegalStateException("after animation is cancelled, item should not be in mRemoveAnimations list");
    }
    if (this.mAddAnimations.remove(paramViewHolder)) {
      throw new IllegalStateException("after animation is cancelled, item should not be in mAddAnimations list");
    }
    if (this.mChangeAnimations.remove(paramViewHolder)) {
      throw new IllegalStateException("after animation is cancelled, item should not be in mChangeAnimations list");
    }
    if (this.mMoveAnimations.remove(paramViewHolder)) {
      throw new IllegalStateException("after animation is cancelled, item should not be in mMoveAnimations list");
    }
    dispatchFinishedWhenDone();
  }
  
  public void endAnimations()
  {
    int i = this.mPendingMoves.size() - 1;
    Object localObject1;
    Object localObject2;
    while (i >= 0)
    {
      localObject1 = (MoveInfo)this.mPendingMoves.get(i);
      localObject2 = ((MoveInfo)localObject1).holder.itemView;
      ((View)localObject2).setTranslationY(0.0F);
      ((View)localObject2).setTranslationX(0.0F);
      dispatchMoveFinished(((MoveInfo)localObject1).holder);
      this.mPendingMoves.remove(i);
      i -= 1;
    }
    i = this.mPendingRemovals.size() - 1;
    while (i >= 0)
    {
      dispatchRemoveFinished((RecyclerView.ViewHolder)this.mPendingRemovals.get(i));
      this.mPendingRemovals.remove(i);
      i -= 1;
    }
    i = this.mPendingAdditions.size() - 1;
    while (i >= 0)
    {
      localObject1 = (RecyclerView.ViewHolder)this.mPendingAdditions.get(i);
      ((RecyclerView.ViewHolder)localObject1).itemView.setAlpha(1.0F);
      dispatchAddFinished((RecyclerView.ViewHolder)localObject1);
      this.mPendingAdditions.remove(i);
      i -= 1;
    }
    i = this.mPendingChanges.size() - 1;
    while (i >= 0)
    {
      endChangeAnimationIfNecessary((ChangeInfo)this.mPendingChanges.get(i));
      i -= 1;
    }
    this.mPendingChanges.clear();
    if (!isRunning()) {
      return;
    }
    i = this.mMovesList.size() - 1;
    int j;
    while (i >= 0)
    {
      localObject1 = (ArrayList)this.mMovesList.get(i);
      j = ((ArrayList)localObject1).size() - 1;
      while (j >= 0)
      {
        localObject2 = (MoveInfo)((ArrayList)localObject1).get(j);
        View localView = ((MoveInfo)localObject2).holder.itemView;
        localView.setTranslationY(0.0F);
        localView.setTranslationX(0.0F);
        dispatchMoveFinished(((MoveInfo)localObject2).holder);
        ((ArrayList)localObject1).remove(j);
        if (((ArrayList)localObject1).isEmpty()) {
          this.mMovesList.remove(localObject1);
        }
        j -= 1;
      }
      i -= 1;
    }
    i = this.mAdditionsList.size() - 1;
    while (i >= 0)
    {
      localObject1 = (ArrayList)this.mAdditionsList.get(i);
      j = ((ArrayList)localObject1).size() - 1;
      while (j >= 0)
      {
        localObject2 = (RecyclerView.ViewHolder)((ArrayList)localObject1).get(j);
        ((RecyclerView.ViewHolder)localObject2).itemView.setAlpha(1.0F);
        dispatchAddFinished((RecyclerView.ViewHolder)localObject2);
        ((ArrayList)localObject1).remove(j);
        if (((ArrayList)localObject1).isEmpty()) {
          this.mAdditionsList.remove(localObject1);
        }
        j -= 1;
      }
      i -= 1;
    }
    i = this.mChangesList.size() - 1;
    while (i >= 0)
    {
      localObject1 = (ArrayList)this.mChangesList.get(i);
      j = ((ArrayList)localObject1).size() - 1;
      while (j >= 0)
      {
        endChangeAnimationIfNecessary((ChangeInfo)((ArrayList)localObject1).get(j));
        if (((ArrayList)localObject1).isEmpty()) {
          this.mChangesList.remove(localObject1);
        }
        j -= 1;
      }
      i -= 1;
    }
    cancelAll(this.mRemoveAnimations);
    cancelAll(this.mMoveAnimations);
    cancelAll(this.mAddAnimations);
    cancelAll(this.mChangeAnimations);
    dispatchAnimationsFinished();
  }
  
  public boolean isRunning()
  {
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (this.mPendingAdditions.isEmpty())
    {
      bool1 = bool2;
      if (this.mPendingChanges.isEmpty())
      {
        bool1 = bool2;
        if (this.mPendingMoves.isEmpty())
        {
          bool1 = bool2;
          if (this.mPendingRemovals.isEmpty())
          {
            bool1 = bool2;
            if (this.mMoveAnimations.isEmpty())
            {
              bool1 = bool2;
              if (this.mRemoveAnimations.isEmpty())
              {
                bool1 = bool2;
                if (this.mAddAnimations.isEmpty())
                {
                  bool1 = bool2;
                  if (this.mChangeAnimations.isEmpty())
                  {
                    bool1 = bool2;
                    if (this.mMovesList.isEmpty())
                    {
                      bool1 = bool2;
                      if (this.mAdditionsList.isEmpty())
                      {
                        bool1 = bool2;
                        if (this.mChangesList.isEmpty()) {
                          bool1 = false;
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public void runPendingAnimations()
  {
    int i;
    int j;
    label24:
    int k;
    label36:
    int m;
    if (this.mPendingRemovals.isEmpty())
    {
      i = 0;
      if (!this.mPendingMoves.isEmpty()) {
        break label100;
      }
      j = 0;
      if (!this.mPendingChanges.isEmpty()) {
        break label105;
      }
      k = 0;
      if (!this.mPendingAdditions.isEmpty()) {
        break label110;
      }
      m = 0;
      label49:
      if ((i == 0) && (j == 0)) {
        break label116;
      }
    }
    final Object localObject1;
    label100:
    label105:
    label110:
    label116:
    while ((m != 0) || (k != 0))
    {
      localObject1 = this.mPendingRemovals.iterator();
      while (((Iterator)localObject1).hasNext()) {
        animateRemoveImpl((RecyclerView.ViewHolder)((Iterator)localObject1).next());
      }
      i = 1;
      break;
      j = 1;
      break label24;
      k = 1;
      break label36;
      m = 1;
      break label49;
    }
    return;
    this.mPendingRemovals.clear();
    Object localObject2;
    label293:
    long l1;
    label368:
    long l2;
    if (j != 0)
    {
      localObject1 = new ArrayList();
      ((ArrayList)localObject1).addAll(this.mPendingMoves);
      this.mMovesList.add(localObject1);
      this.mPendingMoves.clear();
      localObject2 = new Runnable()
      {
        public void run()
        {
          Iterator localIterator = localObject1.iterator();
          while (localIterator.hasNext())
          {
            DefaultItemAnimator.MoveInfo localMoveInfo = (DefaultItemAnimator.MoveInfo)localIterator.next();
            DefaultItemAnimator.-wrap2(DefaultItemAnimator.this, localMoveInfo.holder, localMoveInfo.fromX, localMoveInfo.fromY, localMoveInfo.toX, localMoveInfo.toY);
          }
          localObject1.clear();
          DefaultItemAnimator.-get5(DefaultItemAnimator.this).remove(localObject1);
        }
      };
      if (i != 0) {
        ((MoveInfo)((ArrayList)localObject1).get(0)).holder.itemView.postOnAnimationDelayed((Runnable)localObject2, getRemoveDuration());
      }
    }
    else
    {
      if (k != 0)
      {
        localObject1 = new ArrayList();
        ((ArrayList)localObject1).addAll(this.mPendingChanges);
        this.mChangesList.add(localObject1);
        this.mPendingChanges.clear();
        localObject2 = new Runnable()
        {
          public void run()
          {
            Iterator localIterator = localObject1.iterator();
            while (localIterator.hasNext())
            {
              DefaultItemAnimator.ChangeInfo localChangeInfo = (DefaultItemAnimator.ChangeInfo)localIterator.next();
              DefaultItemAnimator.-wrap1(DefaultItemAnimator.this, localChangeInfo);
            }
            localObject1.clear();
            DefaultItemAnimator.-get3(DefaultItemAnimator.this).remove(localObject1);
          }
        };
        if (i == 0) {
          break label430;
        }
        ((ChangeInfo)((ArrayList)localObject1).get(0)).oldHolder.itemView.postOnAnimationDelayed((Runnable)localObject2, getRemoveDuration());
      }
      if (m != 0)
      {
        localObject1 = new ArrayList();
        ((ArrayList)localObject1).addAll(this.mPendingAdditions);
        this.mAdditionsList.add(localObject1);
        this.mPendingAdditions.clear();
        localObject2 = new Runnable()
        {
          public void run()
          {
            Iterator localIterator = localObject1.iterator();
            while (localIterator.hasNext())
            {
              RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)localIterator.next();
              DefaultItemAnimator.-wrap0(DefaultItemAnimator.this, localViewHolder);
            }
            localObject1.clear();
            DefaultItemAnimator.-get1(DefaultItemAnimator.this).remove(localObject1);
          }
        };
        if ((i == 0) && (j == 0) && (k == 0)) {
          break label458;
        }
        if (i == 0) {
          break label440;
        }
        l1 = getRemoveDuration();
        if (j == 0) {
          break label446;
        }
        l2 = getMoveDuration();
        label378:
        if (k == 0) {
          break label452;
        }
      }
    }
    label430:
    label440:
    label446:
    label452:
    for (long l3 = getChangeDuration();; l3 = 0L)
    {
      l2 = Math.max(l2, l3);
      ((RecyclerView.ViewHolder)((ArrayList)localObject1).get(0)).itemView.postOnAnimationDelayed((Runnable)localObject2, l1 + l2);
      return;
      ((Runnable)localObject2).run();
      break;
      ((Runnable)localObject2).run();
      break label293;
      l1 = 0L;
      break label368;
      l2 = 0L;
      break label378;
    }
    label458:
    ((Runnable)localObject2).run();
  }
  
  private static class ChangeInfo
  {
    public int fromX;
    public int fromY;
    public RecyclerView.ViewHolder newHolder;
    public RecyclerView.ViewHolder oldHolder;
    public int toX;
    public int toY;
    
    private ChangeInfo(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2)
    {
      this.oldHolder = paramViewHolder1;
      this.newHolder = paramViewHolder2;
    }
    
    private ChangeInfo(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this(paramViewHolder1, paramViewHolder2);
      this.fromX = paramInt1;
      this.fromY = paramInt2;
      this.toX = paramInt3;
      this.toY = paramInt4;
    }
    
    public String toString()
    {
      return "ChangeInfo{oldHolder=" + this.oldHolder + ", newHolder=" + this.newHolder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
    }
  }
  
  private static class MoveInfo
  {
    public int fromX;
    public int fromY;
    public RecyclerView.ViewHolder holder;
    public int toX;
    public int toY;
    
    private MoveInfo(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.holder = paramViewHolder;
      this.fromX = paramInt1;
      this.fromY = paramInt2;
      this.toX = paramInt3;
      this.toY = paramInt4;
    }
  }
  
  private static abstract interface ViewPropertyAnimatorListener
  {
    public abstract void onAnimationCancel(View paramView);
    
    public abstract void onAnimationEnd(View paramView);
    
    public abstract void onAnimationStart(View paramView);
  }
  
  private static class VpaListenerAdapter
    implements DefaultItemAnimator.ViewPropertyAnimatorListener
  {
    public void onAnimationCancel(View paramView) {}
    
    public void onAnimationEnd(View paramView) {}
    
    public void onAnimationStart(View paramView) {}
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\recyclerview\DefaultItemAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */