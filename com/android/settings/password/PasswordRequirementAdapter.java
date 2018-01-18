package com.android.settings.password;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PasswordRequirementAdapter
  extends RecyclerView.Adapter<PasswordRequirementViewHolder>
{
  private String[] mRequirements;
  
  public PasswordRequirementAdapter()
  {
    setHasStableIds(true);
  }
  
  public int getItemCount()
  {
    return this.mRequirements.length;
  }
  
  public long getItemId(int paramInt)
  {
    return this.mRequirements[paramInt].hashCode();
  }
  
  public void onBindViewHolder(PasswordRequirementViewHolder paramPasswordRequirementViewHolder, int paramInt)
  {
    PasswordRequirementViewHolder.-get0(paramPasswordRequirementViewHolder).setText(this.mRequirements[paramInt]);
  }
  
  public PasswordRequirementViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    return new PasswordRequirementViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968885, paramViewGroup, false));
  }
  
  public void setRequirements(String[] paramArrayOfString)
  {
    this.mRequirements = paramArrayOfString;
    notifyDataSetChanged();
  }
  
  public static class PasswordRequirementViewHolder
    extends RecyclerView.ViewHolder
  {
    private TextView mDescriptionText;
    
    public PasswordRequirementViewHolder(View paramView)
    {
      super();
      this.mDescriptionText = ((TextView)paramView);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\password\PasswordRequirementAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */