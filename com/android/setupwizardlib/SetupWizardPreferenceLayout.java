package com.android.setupwizardlib;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SetupWizardPreferenceLayout
  extends SetupWizardRecyclerLayout
{
  private RecyclerView mRecyclerView;
  
  public SetupWizardPreferenceLayout(Context paramContext)
  {
    super(paramContext);
  }
  
  public SetupWizardPreferenceLayout(Context paramContext, int paramInt1, int paramInt2)
  {
    super(paramContext, paramInt1, paramInt2);
  }
  
  public SetupWizardPreferenceLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public SetupWizardPreferenceLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  protected ViewGroup findContainer(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = R.id.suw_layout_content;
    }
    return super.findContainer(i);
  }
  
  public RecyclerView getRecyclerView()
  {
    return this.mRecyclerView;
  }
  
  public RecyclerView onCreateRecyclerView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return this.mRecyclerView;
  }
  
  protected View onInflateTemplate(LayoutInflater paramLayoutInflater, int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = R.layout.suw_preference_template;
    }
    return super.onInflateTemplate(paramLayoutInflater, i);
  }
  
  protected void onTemplateInflated()
  {
    this.mRecyclerView = ((RecyclerView)LayoutInflater.from(getContext()).inflate(R.layout.suw_preference_recycler_view, this, false));
    initRecyclerView(this.mRecyclerView);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\SetupWizardPreferenceLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */