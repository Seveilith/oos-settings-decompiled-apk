package com.android.settings.deviceinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.setupwizardlib.SetupWizardLayout;
import com.android.setupwizardlib.view.Illustration;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Objects;

public abstract class StorageWizardBase
  extends Activity
{
  protected static final int ILLUSTRATION_INTERNAL = 1;
  protected static final int ILLUSTRATION_PORTABLE = 2;
  protected static final int ILLUSTRATION_SETUP = 0;
  private View mCustomNav;
  private Button mCustomNext;
  protected DiskInfo mDisk;
  protected StorageManager mStorage;
  private final StorageEventListener mStorageListener = new StorageEventListener()
  {
    public void onDiskDestroyed(DiskInfo paramAnonymousDiskInfo)
    {
      if (StorageWizardBase.this.mDisk.id.equals(paramAnonymousDiskInfo.id)) {
        StorageWizardBase.this.finish();
      }
    }
  };
  protected VolumeInfo mVolume;
  
  protected VolumeInfo findFirstVolume(int paramInt)
  {
    Iterator localIterator = this.mStorage.getVolumes().iterator();
    while (localIterator.hasNext())
    {
      VolumeInfo localVolumeInfo = (VolumeInfo)localIterator.next();
      if ((Objects.equals(this.mDisk.getId(), localVolumeInfo.getDiskId())) && (localVolumeInfo.getType() == paramInt)) {
        return localVolumeInfo;
      }
    }
    return null;
  }
  
  protected Button getNextButton()
  {
    return this.mCustomNext;
  }
  
  protected ProgressBar getProgressBar()
  {
    return (ProgressBar)findViewById(2131362577);
  }
  
  protected SetupWizardLayout getSetupWizardLayout()
  {
    return (SetupWizardLayout)findViewById(2131362138);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mStorage = ((StorageManager)getSystemService(StorageManager.class));
    paramBundle = getIntent().getStringExtra("android.os.storage.extra.VOLUME_ID");
    if (!TextUtils.isEmpty(paramBundle)) {
      this.mVolume = this.mStorage.findVolumeById(paramBundle);
    }
    paramBundle = getIntent().getStringExtra("android.os.storage.extra.DISK_ID");
    if (!TextUtils.isEmpty(paramBundle)) {
      this.mDisk = this.mStorage.findDiskById(paramBundle);
    }
    for (;;)
    {
      setTheme(2131821557);
      if (this.mDisk != null) {
        this.mStorage.registerListener(this.mStorageListener);
      }
      return;
      if (this.mVolume != null) {
        this.mDisk = this.mVolume.getDisk();
      }
    }
  }
  
  protected void onDestroy()
  {
    this.mStorage.unregisterListener(this.mStorageListener);
    super.onDestroy();
  }
  
  public void onNavigateNext()
  {
    throw new UnsupportedOperationException();
  }
  
  protected void onPostCreate(Bundle paramBundle)
  {
    super.onPostCreate(paramBundle);
    paramBundle = getWindow();
    paramBundle.addFlags(-2147417856);
    paramBundle.setStatusBarColor(0);
    this.mCustomNav.setSystemUiVisibility(1280);
    paramBundle = findViewById(2131362604);
    paramBundle.setVerticalFadingEdgeEnabled(true);
    paramBundle.setFadingEdgeLength(paramBundle.getVerticalFadingEdgeLength() * 2);
    if ((findViewById(2131362603) instanceof Illustration))
    {
      paramBundle = findViewById(2131362140);
      paramBundle.setPadding(paramBundle.getPaddingLeft(), 0, paramBundle.getPaddingRight(), paramBundle.getPaddingBottom());
    }
  }
  
  protected void setBodyText(int paramInt, String... paramVarArgs)
  {
    ((TextView)findViewById(2131362565)).setText(TextUtils.expandTemplate(getText(paramInt), paramVarArgs));
  }
  
  public void setContentView(int paramInt)
  {
    super.setContentView(paramInt);
    ViewGroup localViewGroup = (ViewGroup)findViewById(2131362573).getParent();
    this.mCustomNav = getLayoutInflater().inflate(2130969018, localViewGroup, false);
    this.mCustomNext = ((Button)this.mCustomNav.findViewById(2131362576));
    this.mCustomNext.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        StorageWizardBase.this.onNavigateNext();
      }
    });
    paramInt = 0;
    for (;;)
    {
      if (paramInt < localViewGroup.getChildCount())
      {
        if (localViewGroup.getChildAt(paramInt).getId() == 2131362573)
        {
          localViewGroup.removeViewAt(paramInt);
          localViewGroup.addView(this.mCustomNav, paramInt);
        }
      }
      else {
        return;
      }
      paramInt += 1;
    }
  }
  
  protected void setCurrentProgress(int paramInt)
  {
    getProgressBar().setProgress(paramInt);
    ((TextView)findViewById(2131362578)).setText(NumberFormat.getPercentInstance().format(paramInt / 100.0D));
  }
  
  protected void setHeaderText(int paramInt, String... paramVarArgs)
  {
    paramVarArgs = TextUtils.expandTemplate(getText(paramInt), paramVarArgs);
    getSetupWizardLayout().setHeaderText(paramVarArgs);
    setTitle(paramVarArgs);
  }
  
  protected void setIllustrationType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      getSetupWizardLayout().setIllustration(2130837828, 2130837825);
      return;
    case 1: 
      getSetupWizardLayout().setIllustration(2130837826, 2130837825);
      return;
    }
    getSetupWizardLayout().setIllustration(2130837827, 2130837825);
  }
  
  protected void setKeepScreenOn(boolean paramBoolean)
  {
    getSetupWizardLayout().setKeepScreenOn(paramBoolean);
  }
  
  protected void setSecondaryBodyText(int paramInt, String... paramVarArgs)
  {
    TextView localTextView = (TextView)findViewById(2131362566);
    localTextView.setText(TextUtils.expandTemplate(getText(paramInt), paramVarArgs));
    localTextView.setVisibility(0);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageWizardBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */