package com.android.settings;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Toast;

public class CopyablePreference
  extends Preference
{
  public CopyablePreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public CopyablePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public static void copyPreference(Context paramContext, CopyablePreference paramCopyablePreference)
  {
    ((ClipboardManager)paramContext.getSystemService("clipboard")).setText(paramCopyablePreference.getCopyableText());
    Toast.makeText(paramContext, 17040206, 0).show();
  }
  
  public CharSequence getCopyableText()
  {
    return getSummary();
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder.setDividerAllowedAbove(true);
    paramPreferenceViewHolder.setDividerAllowedBelow(true);
    paramPreferenceViewHolder.itemView.setLongClickable(true);
    paramPreferenceViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener()
    {
      public boolean onLongClick(View paramAnonymousView)
      {
        CopyablePreference.copyPreference(CopyablePreference.this.getContext(), CopyablePreference.this);
        return true;
      }
    });
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\CopyablePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */