package com.android.settings.deviceinfo;

import android.app.Activity;
import android.content.Intent;
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
import com.android.settings.InstrumentedFragment;

public class PrivateVolumeFormat
  extends InstrumentedFragment
{
  private final View.OnClickListener mConfirmListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      paramAnonymousView = new Intent(PrivateVolumeFormat.this.getActivity(), StorageWizardFormatProgress.class);
      paramAnonymousView.putExtra("android.os.storage.extra.DISK_ID", PrivateVolumeFormat.-get0(PrivateVolumeFormat.this).getId());
      paramAnonymousView.putExtra("format_private", false);
      paramAnonymousView.putExtra("forget_uuid", PrivateVolumeFormat.-get1(PrivateVolumeFormat.this).getFsUuid());
      PrivateVolumeFormat.this.startActivity(paramAnonymousView);
      PrivateVolumeFormat.this.getActivity().finish();
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
    paramLayoutInflater = paramLayoutInflater.inflate(2130969010, paramViewGroup, false);
    paramViewGroup = (TextView)paramLayoutInflater.findViewById(2131362374);
    paramBundle = (Button)paramLayoutInflater.findViewById(2131362563);
    paramViewGroup.setText(TextUtils.expandTemplate(getText(2131691784), new CharSequence[] { this.mDisk.getDescription() }));
    paramBundle.setOnClickListener(this.mConfirmListener);
    return paramLayoutInflater;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\PrivateVolumeFormat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */