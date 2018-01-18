package com.android.settings.dashboard.conditional;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.dashboard.DashboardAdapter;
import com.android.settings.dashboard.DashboardAdapter.DashboardItemHolder;

public class ConditionAdapterUtils
{
  public static void addDismiss(final RecyclerView paramRecyclerView)
  {
    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, 48)
    {
      public int getSwipeDirs(RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder)
      {
        if (paramAnonymousViewHolder.getItemViewType() == 2130968642) {
          return super.getSwipeDirs(paramAnonymousRecyclerView, paramAnonymousViewHolder);
        }
        return 0;
      }
      
      public boolean onMove(RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder1, RecyclerView.ViewHolder paramAnonymousViewHolder2)
      {
        return true;
      }
      
      public void onSwiped(RecyclerView.ViewHolder paramAnonymousViewHolder, int paramAnonymousInt)
      {
        paramAnonymousViewHolder = ((DashboardAdapter)paramRecyclerView.getAdapter()).getItem(paramAnonymousViewHolder.getItemId());
        if ((paramAnonymousViewHolder instanceof Condition)) {
          ((Condition)paramAnonymousViewHolder).silence();
        }
      }
    }).attachToRecyclerView(paramRecyclerView);
  }
  
  private static void animateChange(View paramView1, View paramView2, final View paramView3, final boolean paramBoolean1, boolean paramBoolean2)
  {
    setViewVisibility(paramView3, 2131362029, paramBoolean2);
    setViewVisibility(paramView3, 2131362030, paramBoolean2);
    final int j = paramView2.getBottom();
    if (paramBoolean1) {}
    for (int i = -2;; i = 0)
    {
      setHeight(paramView3, i);
      paramView3.setVisibility(0);
      paramView1.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
      {
        public static final long DURATION = 250L;
        
        public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
        {
          paramAnonymousInt1 = this.val$content.getBottom();
          paramAnonymousView.removeOnLayoutChangeListener(this);
          paramAnonymousView = ObjectAnimator.ofInt(this.val$content, "bottom", new int[] { j, paramAnonymousInt1 });
          paramAnonymousView.setDuration(250L);
          paramAnonymousView.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymous2Animator)
            {
              if (!this.val$visible) {
                this.val$detailGroup.setVisibility(8);
              }
            }
          });
          paramAnonymousView.start();
        }
      });
      return;
    }
  }
  
  public static void bindViews(Condition paramCondition, DashboardAdapter.DashboardItemHolder paramDashboardItemHolder, boolean paramBoolean, View.OnClickListener paramOnClickListener1, View.OnClickListener paramOnClickListener2)
  {
    Object localObject1 = paramDashboardItemHolder.itemView.findViewById(2131362025);
    ((View)localObject1).setTag(paramCondition);
    paramDashboardItemHolder.icon.setImageIcon(paramCondition.getIcon());
    paramDashboardItemHolder.title.setText(paramCondition.getTitle());
    if (paramCondition.getSummary() != null) {
      ((View)localObject1).setOnClickListener(paramOnClickListener1);
    }
    int i;
    label92:
    label112:
    boolean bool;
    label157:
    Object localObject2;
    label208:
    label239:
    final int j;
    label301:
    int k;
    if (paramCondition.getTitle() == null)
    {
      ((View)localObject1).setVisibility(8);
      paramOnClickListener1 = (ImageView)paramDashboardItemHolder.itemView.findViewById(2131362027);
      paramOnClickListener1.setTag(paramCondition);
      if (!paramBoolean) {
        break label505;
      }
      i = 2130837974;
      paramOnClickListener1.setImageResource(i);
      localObject1 = paramOnClickListener1.getContext();
      if (!paramBoolean) {
        break label512;
      }
      i = 2131693604;
      paramOnClickListener1.setContentDescription(((Context)localObject1).getString(i));
      paramOnClickListener1.setOnClickListener(paramOnClickListener2);
      paramOnClickListener2 = paramDashboardItemHolder.itemView.findViewById(2131362028);
      localObject1 = paramCondition.getActions();
      if (paramOnClickListener2.getVisibility() != 0) {
        break label519;
      }
      bool = true;
      if ((paramBoolean != bool) && (!paramOnClickListener1.getContext().getString(2131690470).equals(paramCondition.getTitle())))
      {
        localObject2 = paramDashboardItemHolder.itemView;
        localObject3 = paramDashboardItemHolder.itemView.findViewById(2131362025);
        if (localObject1.length <= 0) {
          break label525;
        }
        bool = true;
        animateChange((View)localObject2, (View)localObject3, paramOnClickListener2, paramBoolean, bool);
      }
      i = 0;
      if (paramCondition.getSummary() != null) {
        break label531;
      }
      i = 1;
      paramOnClickListener1.setVisibility(8);
      if (paramOnClickListener1.getContext().getString(2131690470).equals(paramCondition.getTitle())) {
        paramOnClickListener1.setVisibility(8);
      }
      if (!paramBoolean) {
        return;
      }
      if (!paramOnClickListener1.getContext().getString(2131690470).equals(paramCondition.getTitle())) {
        paramDashboardItemHolder.summary.setText(paramCondition.getSummary());
      }
      j = 0;
      if (j >= 2) {
        return;
      }
      if (j != 0) {
        break label539;
      }
      k = 2131362031;
      label316:
      localObject2 = (Button)paramOnClickListener2.findViewById(k);
      if (localObject1.length <= j) {
        break label546;
      }
      ((Button)localObject2).setVisibility(0);
      ((Button)localObject2).setText(localObject1[j]);
      ((Button)localObject2).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          MetricsLogger.action(paramAnonymousView.getContext(), 376, this.val$condition.getMetricsConstant());
          this.val$condition.onActionClick(j);
        }
      });
      label367:
      Object localObject3 = paramOnClickListener1.getContext().getString(2131690329);
      paramBoolean = paramOnClickListener1.getContext().getPackageManager().hasSystemFeature("oem.wifi.stasap.concurrency.support");
      if ((!((String)localObject3).equals(paramCondition.getTitle())) || (!paramBoolean)) {
        break label556;
      }
      paramDashboardItemHolder.summary.setVisibility(8);
    }
    for (;;)
    {
      if (i != 0)
      {
        ((Button)localObject2).setVisibility(8);
        paramOnClickListener2.findViewById(2131362029).setVisibility(8);
      }
      if (paramOnClickListener1.getContext().getString(2131690470).equals(paramCondition.getTitle()))
      {
        paramDashboardItemHolder.summary.setVisibility(8);
        ((Button)localObject2).setVisibility(8);
        paramOnClickListener2.findViewById(2131362029).setVisibility(8);
      }
      j += 1;
      break label301;
      ((View)localObject1).setVisibility(0);
      break;
      label505:
      i = 2130837975;
      break label92;
      label512:
      i = 2131693603;
      break label112;
      label519:
      bool = false;
      break label157;
      label525:
      bool = false;
      break label208;
      label531:
      paramOnClickListener1.setVisibility(0);
      break label239;
      label539:
      k = 2131362032;
      break label316;
      label546:
      ((Button)localObject2).setVisibility(8);
      break label367;
      label556:
      paramDashboardItemHolder.summary.setVisibility(0);
    }
  }
  
  private static void setHeight(View paramView, int paramInt)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    localLayoutParams.height = paramInt;
    paramView.setLayoutParams(localLayoutParams);
  }
  
  private static void setViewVisibility(View paramView, int paramInt, boolean paramBoolean)
  {
    paramView = paramView.findViewById(paramInt);
    if (paramView != null) {
      if (!paramBoolean) {
        break label22;
      }
    }
    label22:
    for (paramInt = 0;; paramInt = 8)
    {
      paramView.setVisibility(paramInt);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\conditional\ConditionAdapterUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */