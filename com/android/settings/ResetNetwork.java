package com.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.android.settingslib.RestrictedLockUtils;
import com.oneplus.settings.utils.OPSNSUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResetNetwork
  extends OptionsMenuFragment
{
  private static final int KEYGUARD_REQUEST = 55;
  private static final String TAG = "ResetNetwork";
  private View mContentView;
  private Button mInitiateButton;
  private final View.OnClickListener mInitiateListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!ResetNetwork.-wrap0(ResetNetwork.this, 55)) {
        ResetNetwork.-wrap1(ResetNetwork.this);
      }
    }
  };
  private Spinner mSubscriptionSpinner;
  private List<SubscriptionInfo> mSubscriptions;
  
  private void establishInitialState()
  {
    this.mSubscriptionSpinner = ((Spinner)this.mContentView.findViewById(2131362504));
    this.mSubscriptions = SubscriptionManager.from(getActivity()).getActiveSubscriptionInfoList();
    if ((this.mSubscriptions != null) && (this.mSubscriptions.size() > 0))
    {
      int j = SubscriptionManager.getDefaultDataSubscriptionId();
      int i = j;
      if (!SubscriptionManager.isUsableSubIdValue(j)) {
        i = SubscriptionManager.getDefaultVoiceSubscriptionId();
      }
      j = i;
      if (!SubscriptionManager.isUsableSubIdValue(i)) {
        j = SubscriptionManager.getDefaultSmsSubscriptionId();
      }
      i = j;
      if (!SubscriptionManager.isUsableSubIdValue(j)) {
        i = SubscriptionManager.getDefaultSubscriptionId();
      }
      j = 0;
      this.mSubscriptions.size();
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = this.mSubscriptions.iterator();
      while (localIterator.hasNext())
      {
        SubscriptionInfo localSubscriptionInfo = (SubscriptionInfo)localIterator.next();
        int k = j;
        if (localSubscriptionInfo.getSubscriptionId() == i) {
          k = localArrayList.size();
        }
        Object localObject2 = localSubscriptionInfo.getDisplayName().toString();
        localObject1 = localObject2;
        if (TextUtils.isEmpty((CharSequence)localObject2)) {
          localObject1 = localSubscriptionInfo.getNumber();
        }
        localObject2 = localObject1;
        if (TextUtils.isEmpty((CharSequence)localObject1)) {
          localObject2 = localSubscriptionInfo.getCarrierName().toString();
        }
        if (TextUtils.isEmpty((CharSequence)localObject2)) {
          String.format("MCC:%s MNC:%s Slot:%s Id:%s", new Object[] { Integer.valueOf(localSubscriptionInfo.getMcc()), Integer.valueOf(localSubscriptionInfo.getMnc()), Integer.valueOf(localSubscriptionInfo.getSimSlotIndex()), Integer.valueOf(localSubscriptionInfo.getSubscriptionId()) });
        }
        localObject1 = null;
        if (localArrayList.size() < 2) {
          localObject1 = OPSNSUtils.getSimName(getActivity(), localSubscriptionInfo.getSimSlotIndex(), false);
        }
        j = k;
        if (localObject1 != null)
        {
          localArrayList.add(localObject1);
          j = k;
        }
      }
      Object localObject1 = new ArrayAdapter(getActivity(), 17367048, localArrayList);
      ((ArrayAdapter)localObject1).setDropDownViewResource(17367049);
      this.mSubscriptionSpinner.setAdapter((SpinnerAdapter)localObject1);
      this.mSubscriptionSpinner.setSelection(j);
      this.mSubscriptionSpinner.setVisibility(0);
    }
    for (;;)
    {
      this.mInitiateButton = ((Button)this.mContentView.findViewById(2131362505));
      this.mInitiateButton.setOnClickListener(this.mInitiateListener);
      return;
      this.mSubscriptionSpinner.setVisibility(4);
    }
  }
  
  private boolean runKeyguardConfirmation(int paramInt)
  {
    Resources localResources = getActivity().getResources();
    return new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(paramInt, localResources.getText(2131691884));
  }
  
  private void showFinalConfirmation()
  {
    Bundle localBundle = new Bundle();
    if ((this.mSubscriptions != null) && (this.mSubscriptions.size() > 0))
    {
      int i = this.mSubscriptionSpinner.getSelectedItemPosition();
      localBundle.putInt("subscription", ((SubscriptionInfo)this.mSubscriptions.get(i)).getSubscriptionId());
    }
    ((SettingsActivity)getActivity()).startPreferencePanel(ResetNetworkConfirm.class.getName(), localBundle, 2131691889, null, null, 0);
  }
  
  protected int getMetricsCategory()
  {
    return 83;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 != 55) {
      return;
    }
    if (paramInt2 == -1)
    {
      showFinalConfirmation();
      return;
    }
    establishInitialState();
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramBundle = UserManager.get(getActivity());
    paramViewGroup = RestrictedLockUtils.checkIfRestrictionEnforced(getActivity(), "no_network_reset", UserHandle.myUserId());
    if ((!paramBundle.isAdminUser()) || (RestrictedLockUtils.hasBaseUserRestriction(getActivity(), "no_network_reset", UserHandle.myUserId()))) {
      return paramLayoutInflater.inflate(2130968753, null);
    }
    if (paramViewGroup != null)
    {
      paramLayoutInflater = paramLayoutInflater.inflate(2130968608, null);
      ShowAdminSupportDetailsDialog.setAdminSupportDetails(getActivity(), paramLayoutInflater, paramViewGroup, false);
      paramLayoutInflater.setVisibility(0);
      return paramLayoutInflater;
    }
    this.mContentView = paramLayoutInflater.inflate(2130968949, null);
    establishInitialState();
    return this.mContentView;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ResetNetwork.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */