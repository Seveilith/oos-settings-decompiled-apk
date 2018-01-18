package com.android.settings;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import java.util.ArrayList;

public class TimeoutListPreference
  extends RestrictedListPreference
{
  private RestrictedLockUtils.EnforcedAdmin mAdmin;
  private final CharSequence[] mInitialEntries = getEntries();
  private final CharSequence[] mInitialValues = getEntryValues();
  
  public TimeoutListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected void onDialogCreated(Dialog paramDialog)
  {
    super.onDialogCreated(paramDialog);
    paramDialog.create();
    if (this.mAdmin != null) {
      paramDialog.findViewById(2131361940).findViewById(2131361941).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          RestrictedLockUtils.sendShowAdminSupportDetailsIntent(TimeoutListPreference.this.getContext(), TimeoutListPreference.-get0(TimeoutListPreference.this));
        }
      });
    }
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder, DialogInterface.OnClickListener paramOnClickListener)
  {
    super.onPrepareDialogBuilder(paramBuilder, paramOnClickListener);
    if (this.mAdmin != null)
    {
      paramBuilder.setView(2130968605);
      return;
    }
    paramBuilder.setView(null);
  }
  
  public void removeUnusableTimeouts(long paramLong, RestrictedLockUtils.EnforcedAdmin paramEnforcedAdmin)
  {
    if ((DevicePolicyManager)getContext().getSystemService("device_policy") == null) {
      return;
    }
    ArrayList localArrayList1;
    ArrayList localArrayList2;
    int i;
    if ((paramEnforcedAdmin != null) || (this.mAdmin != null) || (isDisabledByAdmin()))
    {
      if (paramEnforcedAdmin == null) {
        paramLong = Long.MAX_VALUE;
      }
      localArrayList1 = new ArrayList();
      localArrayList2 = new ArrayList();
      i = 0;
    }
    while (i < this.mInitialValues.length)
    {
      if (Long.parseLong(this.mInitialValues[i].toString()) <= paramLong)
      {
        localArrayList1.add(this.mInitialEntries[i]);
        localArrayList2.add(this.mInitialValues[i]);
      }
      i += 1;
      continue;
      return;
    }
    if (localArrayList2.size() == 0)
    {
      setDisabledByAdmin(paramEnforcedAdmin);
      return;
    }
    setDisabledByAdmin(null);
    if (localArrayList1.size() != getEntries().length)
    {
      i = Integer.parseInt(getValue());
      setEntries((CharSequence[])localArrayList1.toArray(new CharSequence[0]));
      setEntryValues((CharSequence[])localArrayList2.toArray(new CharSequence[0]));
      this.mAdmin = paramEnforcedAdmin;
      if (i > paramLong) {
        break label225;
      }
      setValue(String.valueOf(i));
    }
    label225:
    while ((localArrayList2.size() <= 0) || (Long.parseLong(((CharSequence)localArrayList2.get(localArrayList2.size() - 1)).toString()) != paramLong)) {
      return;
    }
    setValue(String.valueOf(paramLong));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\TimeoutListPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */