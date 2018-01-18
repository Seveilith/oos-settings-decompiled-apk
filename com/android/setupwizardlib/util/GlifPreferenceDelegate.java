package com.android.setupwizardlib.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.setupwizardlib.R.dimen;
import com.android.setupwizardlib.R.id;
import com.android.setupwizardlib.R.layout;
import com.android.setupwizardlib.view.HeaderRecyclerView;

@Deprecated
public class GlifPreferenceDelegate
{
  public static final int[] ATTRS_LIST_DIVIDER = { 16843284 };
  private boolean mHasIcons;
  private HeaderRecyclerView mRecyclerView;
  
  public GlifPreferenceDelegate(boolean paramBoolean)
  {
    this.mHasIcons = paramBoolean;
  }
  
  public Drawable getDividerDrawable(Context paramContext)
  {
    Object localObject = paramContext.obtainStyledAttributes(ATTRS_LIST_DIVIDER);
    Drawable localDrawable = ((TypedArray)localObject).getDrawable(0);
    ((TypedArray)localObject).recycle();
    localObject = paramContext.getResources();
    if (this.mHasIcons) {}
    for (int i = R.dimen.suw_items_glif_icon_divider_inset;; i = R.dimen.suw_items_glif_text_divider_inset) {
      return DrawableLayoutDirectionHelper.createRelativeInsetDrawable(localDrawable, ((Resources)localObject).getDimensionPixelSize(i), 0, 0, 0, paramContext);
    }
  }
  
  public RecyclerView onCreateRecyclerView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramViewGroup = paramLayoutInflater.getContext();
    this.mRecyclerView = new HeaderRecyclerView(paramViewGroup);
    this.mRecyclerView.setLayoutManager(new LinearLayoutManager(paramViewGroup));
    paramLayoutInflater = paramLayoutInflater.inflate(R.layout.suw_glif_header, this.mRecyclerView, false);
    this.mRecyclerView.setHeader(paramLayoutInflater);
    return this.mRecyclerView;
  }
  
  public void setHeaderText(CharSequence paramCharSequence)
  {
    View localView = this.mRecyclerView.getHeader().findViewById(R.id.suw_layout_title);
    if ((localView instanceof TextView)) {
      ((TextView)localView).setText(paramCharSequence);
    }
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    View localView = this.mRecyclerView.getHeader().findViewById(R.id.suw_layout_icon);
    if ((localView instanceof ImageView)) {
      ((ImageView)localView).setImageDrawable(paramDrawable);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\util\GlifPreferenceDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */