package com.android.settings.deviceinfo;

import android.app.Activity;
import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.SettingsPreferenceFragment;

public class PrivateVolumeUnmount
  extends SettingsPreferenceFragment
{
  private final View.OnClickListener mConfirmListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      new StorageSettings.UnmountTask(PrivateVolumeUnmount.this.getActivity(), PrivateVolumeUnmount.-get0(PrivateVolumeUnmount.this)).execute(new Void[0]);
      PrivateVolumeUnmount.this.getActivity().finish();
    }
  };
  private DiskInfo mDisk;
  private VolumeInfo mVolume;
  
  protected int getMetricsCategory()
  {
    return 42;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramBundle = (StorageManager)getActivity().getSystemService(StorageManager.class);
    this.mVolume = paramBundle.findVolumeById(getArguments().getString("android.os.storage.extra.VOLUME_ID"));
    this.mDisk = paramBundle.findDiskById(this.mVolume.getDiskId());
    paramLayoutInflater = paramLayoutInflater.inflate(2130969011, paramViewGroup, false);
    paramViewGroup = (TextView)paramLayoutInflater.findViewById(2131362374);
    paramBundle = (Button)paramLayoutInflater.findViewById(2131362563);
    paramViewGroup.setText(TextUtils.expandTemplate(getText(2131691785), new CharSequence[] { this.mDisk.getDescription() }));
    paramBundle.setOnClickListener(this.mConfirmListener);
    return paramLayoutInflater;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\PrivateVolumeUnmount.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */