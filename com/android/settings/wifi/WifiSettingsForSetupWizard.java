package com.android.settings.wifi;

import android.app.Dialog;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.android.settings.SetupWizardUtils;
import com.android.setupwizardlib.SetupWizardListLayout;

public class WifiSettingsForSetupWizard
  extends WifiSettings
{
  private static final String TAG = "WifiSettingsForSetupWizard";
  private View mAddOtherNetworkItem;
  private TextView mEmptyFooter;
  private SetupWizardListLayout mLayout;
  private boolean mListLastEmpty = false;
  private View mMacAddressFooter;
  
  private void updateMacAddress()
  {
    TextView localTextView;
    Object localObject;
    if (this.mMacAddressFooter != null)
    {
      localTextView = null;
      localObject = localTextView;
      if (this.mWifiManager != null)
      {
        WifiInfo localWifiInfo = this.mWifiManager.getConnectionInfo();
        localObject = localTextView;
        if (localWifiInfo != null) {
          localObject = localWifiInfo.getMacAddress();
        }
      }
      localTextView = (TextView)this.mMacAddressFooter.findViewById(2131362560);
      if (TextUtils.isEmpty((CharSequence)localObject)) {
        break label63;
      }
    }
    for (;;)
    {
      localTextView.setText((CharSequence)localObject);
      return;
      label63:
      localObject = getString(2131691712);
    }
  }
  
  protected void connect(int paramInt)
  {
    ((WifiSetupActivity)getActivity()).networkSelected();
    super.connect(paramInt);
  }
  
  protected void connect(WifiConfiguration paramWifiConfiguration)
  {
    ((WifiSetupActivity)getActivity()).networkSelected();
    super.connect(paramWifiConfiguration);
  }
  
  WifiEnabler createWifiEnabler()
  {
    return null;
  }
  
  protected TextView initEmptyTextView()
  {
    this.mEmptyFooter = ((TextView)LayoutInflater.from(getActivity()).inflate(2130968999, getListView(), false));
    return this.mEmptyFooter;
  }
  
  public void onAccessPointsChanged()
  {
    boolean bool2 = true;
    super.onAccessPointsChanged();
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    boolean bool1 = bool2;
    if (localPreferenceScreen != null) {
      if (localPreferenceScreen.getPreferenceCount() != 0) {
        break label32;
      }
    }
    label32:
    for (bool1 = bool2;; bool1 = false)
    {
      updateFooter(bool1);
      return;
    }
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (hasNextButton()) {
      getNextButton().setVisibility(8);
    }
    updateMacAddress();
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    Dialog localDialog = super.onCreateDialog(paramInt);
    SetupWizardUtils.applyImmersiveFlags(localDialog);
    return localDialog;
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater) {}
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mLayout = ((SetupWizardListLayout)paramLayoutInflater.inflate(2130969000, paramViewGroup, false));
    paramViewGroup = this.mLayout.getListView();
    this.mAddOtherNetworkItem = paramLayoutInflater.inflate(2130968998, paramViewGroup, false);
    paramViewGroup.addFooterView(this.mAddOtherNetworkItem, null, true);
    this.mAddOtherNetworkItem.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (WifiSettingsForSetupWizard.this.mWifiManager.isWifiEnabled()) {
          WifiSettingsForSetupWizard.this.onAddNetworkPressed();
        }
      }
    });
    this.mMacAddressFooter = paramLayoutInflater.inflate(2130969001, paramViewGroup, false);
    paramViewGroup.addFooterView(this.mMacAddressFooter, null, false);
    paramLayoutInflater = this.mLayout.getNavigationBar();
    if (paramLayoutInflater != null) {
      ((WifiSetupActivity)getActivity()).onNavigationBarCreated(paramLayoutInflater);
    }
    return this.mLayout;
  }
  
  public void onWifiStateChanged(int paramInt)
  {
    super.onWifiStateChanged(paramInt);
    updateMacAddress();
  }
  
  public void registerForContextMenu(View paramView) {}
  
  public View setPinnedHeaderView(int paramInt)
  {
    return null;
  }
  
  public void setPinnedHeaderView(View paramView) {}
  
  protected void setProgressBarVisible(boolean paramBoolean)
  {
    if (this.mLayout != null)
    {
      if (paramBoolean) {
        this.mLayout.showProgressBar();
      }
    }
    else {
      return;
    }
    this.mLayout.hideProgressBar();
  }
  
  protected void updateFooter(boolean paramBoolean)
  {
    if (paramBoolean != this.mListLastEmpty)
    {
      if (!paramBoolean) {
        break label26;
      }
      setFooterView(this.mEmptyFooter);
    }
    for (;;)
    {
      this.mListLastEmpty = paramBoolean;
      return;
      label26:
      LinearLayout localLinearLayout = new LinearLayout(getContext());
      localLinearLayout.setOrientation(1);
      localLinearLayout.addView(this.mAddOtherNetworkItem);
      localLinearLayout.addView(this.mMacAddressFooter);
      setFooterView(localLinearLayout);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiSettingsForSetupWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */