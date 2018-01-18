package com.android.settings.deviceinfo;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeRecord;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.InstrumentedFragment;

public class PrivateVolumeForget
  extends InstrumentedFragment
{
  private static final String TAG_FORGET_CONFIRM = "forget_confirm";
  private final View.OnClickListener mConfirmListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      PrivateVolumeForget.ForgetConfirmFragment.show(PrivateVolumeForget.this, PrivateVolumeForget.-get0(PrivateVolumeForget.this).getFsUuid());
    }
  };
  private VolumeRecord mRecord;
  
  protected int getMetricsCategory()
  {
    return 42;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mRecord = ((StorageManager)getActivity().getSystemService(StorageManager.class)).findRecordByUuid(getArguments().getString("android.os.storage.extra.FS_UUID"));
    paramLayoutInflater = paramLayoutInflater.inflate(2130969009, paramViewGroup, false);
    paramViewGroup = (TextView)paramLayoutInflater.findViewById(2131362374);
    paramBundle = (Button)paramLayoutInflater.findViewById(2131362563);
    paramViewGroup.setText(TextUtils.expandTemplate(getText(2131691786), new CharSequence[] { this.mRecord.getNickname() }));
    paramBundle.setOnClickListener(this.mConfirmListener);
    return paramLayoutInflater;
  }
  
  public static class ForgetConfirmFragment
    extends DialogFragment
  {
    public static void show(Fragment paramFragment, String paramString)
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("android.os.storage.extra.FS_UUID", paramString);
      paramString = new ForgetConfirmFragment();
      paramString.setArguments(localBundle);
      paramString.setTargetFragment(paramFragment, 0);
      paramString.show(paramFragment.getFragmentManager(), "forget_confirm");
    }
    
    public Dialog onCreateDialog(final Bundle paramBundle)
    {
      Object localObject = getActivity();
      paramBundle = (StorageManager)((Context)localObject).getSystemService(StorageManager.class);
      final String str = getArguments().getString("android.os.storage.extra.FS_UUID");
      VolumeRecord localVolumeRecord = paramBundle.findRecordByUuid(str);
      localObject = new AlertDialog.Builder((Context)localObject);
      ((AlertDialog.Builder)localObject).setTitle(TextUtils.expandTemplate(getText(2131691787), new CharSequence[] { localVolumeRecord.getNickname() }));
      ((AlertDialog.Builder)localObject).setMessage(TextUtils.expandTemplate(getText(2131691788), new CharSequence[] { localVolumeRecord.getNickname() }));
      ((AlertDialog.Builder)localObject).setPositiveButton(2131691754, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramBundle.forgetVolume(str);
          PrivateVolumeForget.ForgetConfirmFragment.this.getActivity().finish();
        }
      });
      ((AlertDialog.Builder)localObject).setNegativeButton(2131690993, null);
      return ((AlertDialog.Builder)localObject).create();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\PrivateVolumeForget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */