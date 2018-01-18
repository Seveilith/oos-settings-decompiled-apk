package com.android.settings;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.android.internal.widget.LockPatternUtils;

public class OwnerInfoSettings
  extends DialogFragment
  implements DialogInterface.OnClickListener
{
  private static final String TAG_OWNER_INFO = "ownerInfo";
  private LockPatternUtils mLockPatternUtils;
  private EditText mOwnerInfo;
  private int mUserId;
  private View mView;
  
  private void initView()
  {
    String str = this.mLockPatternUtils.getOwnerInfo(this.mUserId);
    this.mOwnerInfo = ((EditText)this.mView.findViewById(2131362395));
    if (!TextUtils.isEmpty(str)) {
      this.mOwnerInfo.setText(str);
    }
  }
  
  public static void show(Fragment paramFragment)
  {
    if (!paramFragment.isAdded()) {
      return;
    }
    OwnerInfoSettings localOwnerInfoSettings = new OwnerInfoSettings();
    localOwnerInfoSettings.setTargetFragment(paramFragment, 0);
    localOwnerInfoSettings.show(paramFragment.getFragmentManager(), "ownerInfo");
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    LockPatternUtils localLockPatternUtils;
    if (paramInt == -1)
    {
      paramDialogInterface = this.mOwnerInfo.getText().toString();
      localLockPatternUtils = this.mLockPatternUtils;
      if (!TextUtils.isEmpty(paramDialogInterface)) {
        break label76;
      }
    }
    label76:
    for (boolean bool = false;; bool = true)
    {
      localLockPatternUtils.setOwnerInfoEnabled(bool, this.mUserId);
      this.mLockPatternUtils.setOwnerInfo(paramDialogInterface, this.mUserId);
      if ((getTargetFragment() instanceof SecuritySettings.SecuritySubSettings)) {
        ((SecuritySettings.SecuritySubSettings)getTargetFragment()).updateOwnerInfo();
      }
      return;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUserId = UserHandle.myUserId();
    this.mLockPatternUtils = new LockPatternUtils(getActivity());
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    this.mView = LayoutInflater.from(getActivity()).inflate(2130968875, null);
    initView();
    return new AlertDialog.Builder(getActivity()).setTitle(2131691048).setView(this.mView).setPositiveButton(2131690997, this).setNegativeButton(2131690993, this).show();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\OwnerInfoSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */