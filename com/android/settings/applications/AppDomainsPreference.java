package com.android.settings.applications;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.settings.accessibility.ListDialogPreference;

public class AppDomainsPreference
  extends ListDialogPreference
{
  private int mNumEntries;
  
  public AppDomainsPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setDialogLayoutResource(2130968611);
    setListItemLayoutResource(2130968612);
  }
  
  public CharSequence getSummary()
  {
    Context localContext = getContext();
    if (this.mNumEntries == 0) {
      return localContext.getString(2131693419);
    }
    CharSequence localCharSequence = super.getSummary();
    if (this.mNumEntries == 1) {}
    for (int i = 2131693420;; i = 2131693421) {
      return localContext.getString(i, new Object[] { localCharSequence });
    }
  }
  
  protected void onBindListItem(View paramView, int paramInt)
  {
    CharSequence localCharSequence = getTitleAt(paramInt);
    if (localCharSequence != null) {
      ((TextView)paramView.findViewById(2131361950)).setText(localCharSequence);
    }
  }
  
  public void setTitles(CharSequence[] paramArrayOfCharSequence)
  {
    if (paramArrayOfCharSequence != null) {}
    for (int i = paramArrayOfCharSequence.length;; i = 0)
    {
      this.mNumEntries = i;
      super.setTitles(paramArrayOfCharSequence);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppDomainsPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */