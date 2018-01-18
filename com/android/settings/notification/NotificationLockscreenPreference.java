package com.android.settings.notification;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.settings.RestrictedListPreference;
import com.android.settings.RestrictedListPreference.RestrictedArrayAdapter;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;

public class NotificationLockscreenPreference
  extends RestrictedListPreference
{
  private RestrictedLockUtils.EnforcedAdmin mAdminRestrictingRemoteInput;
  private boolean mAllowRemoteInput;
  private Listener mListener;
  private boolean mRemoteInputCheckBoxEnabled = true;
  private boolean mShowRemoteInput;
  private int mUserId = UserHandle.myUserId();
  
  public NotificationLockscreenPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private int checkboxVisibilityForSelectedIndex(int paramInt, boolean paramBoolean)
  {
    if ((paramInt == 1) && (paramBoolean) && (this.mRemoteInputCheckBoxEnabled)) {
      return 0;
    }
    return 8;
  }
  
  protected ListAdapter createListAdapter()
  {
    return new RestrictedListPreference.RestrictedArrayAdapter(this, getContext(), getEntries(), -1);
  }
  
  protected boolean isAutoClosePreference()
  {
    return false;
  }
  
  protected void onClick()
  {
    Context localContext = getContext();
    if (!Utils.startQuietModeDialogIfNecessary(localContext, UserManager.get(localContext), this.mUserId)) {
      super.onClick();
    }
  }
  
  protected void onDialogClosed(boolean paramBoolean)
  {
    super.onDialogClosed(paramBoolean);
    ContentResolver localContentResolver = getContext().getContentResolver();
    if (this.mAllowRemoteInput) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "lock_screen_allow_remote_input", i);
      return;
    }
  }
  
  protected void onDialogCreated(Dialog paramDialog)
  {
    boolean bool2 = true;
    super.onDialogCreated(paramDialog);
    paramDialog.create();
    CheckBox localCheckBox = (CheckBox)paramDialog.findViewById(2131362196);
    boolean bool1;
    label57:
    View localView;
    if (this.mAllowRemoteInput)
    {
      bool1 = false;
      localCheckBox.setChecked(bool1);
      localCheckBox.setOnCheckedChangeListener(this.mListener);
      if (this.mAdminRestrictingRemoteInput != null) {
        break label119;
      }
      bool1 = bool2;
      localCheckBox.setEnabled(bool1);
      localView = paramDialog.findViewById(2131362197);
      if (this.mAdminRestrictingRemoteInput != null) {
        break label124;
      }
    }
    label119:
    label124:
    for (int i = 8;; i = 0)
    {
      localView.setVisibility(i);
      if (this.mAdminRestrictingRemoteInput != null)
      {
        localCheckBox.setClickable(false);
        paramDialog.findViewById(16909100).setOnClickListener(this.mListener);
      }
      return;
      bool1 = true;
      break;
      bool1 = false;
      break label57;
    }
  }
  
  protected void onDialogStateRestored(Dialog paramDialog, Bundle paramBundle)
  {
    super.onDialogStateRestored(paramDialog, paramBundle);
    int i = ((AlertDialog)paramDialog).getListView().getCheckedItemPosition();
    paramDialog = paramDialog.findViewById(16909100);
    paramDialog.setVisibility(checkboxVisibilityForSelectedIndex(i, this.mShowRemoteInput));
    this.mListener.setView(paramDialog);
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder, DialogInterface.OnClickListener paramOnClickListener)
  {
    boolean bool2 = true;
    this.mListener = new Listener(paramOnClickListener);
    paramBuilder.setSingleChoiceItems(createListAdapter(), getSelectedValuePos(), this.mListener);
    if (getEntryValues().length == 3)
    {
      bool1 = true;
      this.mShowRemoteInput = bool1;
      if (Settings.Secure.getInt(getContext().getContentResolver(), "lock_screen_allow_remote_input", 0) == 0) {
        break label86;
      }
    }
    label86:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mAllowRemoteInput = bool1;
      paramBuilder.setView(2130968744);
      return;
      bool1 = false;
      break;
    }
  }
  
  public void setRemoteInputCheckBoxEnabled(boolean paramBoolean)
  {
    this.mRemoteInputCheckBoxEnabled = paramBoolean;
  }
  
  public void setRemoteInputRestricted(RestrictedLockUtils.EnforcedAdmin paramEnforcedAdmin)
  {
    this.mAdminRestrictingRemoteInput = paramEnforcedAdmin;
  }
  
  public void setUserId(int paramInt)
  {
    this.mUserId = paramInt;
  }
  
  private class Listener
    implements DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener
  {
    private final DialogInterface.OnClickListener mInner;
    private View mView;
    
    public Listener(DialogInterface.OnClickListener paramOnClickListener)
    {
      this.mInner = paramOnClickListener;
    }
    
    public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
    {
      paramCompoundButton = NotificationLockscreenPreference.this;
      if (paramBoolean) {}
      for (paramBoolean = false;; paramBoolean = true)
      {
        NotificationLockscreenPreference.-set0(paramCompoundButton, paramBoolean);
        return;
      }
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      this.mInner.onClick(paramDialogInterface, paramInt);
      paramInt = ((AlertDialog)paramDialogInterface).getListView().getCheckedItemPosition();
      if (this.mView != null) {
        this.mView.setVisibility(NotificationLockscreenPreference.-wrap0(NotificationLockscreenPreference.this, paramInt, NotificationLockscreenPreference.-get1(NotificationLockscreenPreference.this)));
      }
    }
    
    public void onClick(View paramView)
    {
      if (paramView.getId() == 16909100) {
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(NotificationLockscreenPreference.this.getContext(), NotificationLockscreenPreference.-get0(NotificationLockscreenPreference.this));
      }
    }
    
    public void setView(View paramView)
    {
      this.mView = paramView;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\NotificationLockscreenPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */