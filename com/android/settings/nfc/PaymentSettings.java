package com.android.settings.nfc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import java.util.List;

public class PaymentSettings
  extends SettingsPreferenceFragment
{
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new PaymentSettings.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader);
    }
  };
  public static final String TAG = "PaymentSettings";
  private PaymentBackend mPaymentBackend;
  
  protected int getMetricsCategory()
  {
    return 70;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mPaymentBackend = new PaymentBackend(getActivity());
    setHasOptionsMenu(true);
    paramBundle = getPreferenceManager().createPreferenceScreen(getActivity());
    Object localObject = this.mPaymentBackend.getPaymentAppInfos();
    if ((localObject != null) && (((List)localObject).size() > 0))
    {
      localObject = new NfcPaymentPreference(getPrefContext(), this.mPaymentBackend);
      ((NfcPaymentPreference)localObject).setKey("payment");
      paramBundle.addPreference((Preference)localObject);
      paramBundle.addPreference(new NfcForegroundPreference(getPrefContext(), this.mPaymentBackend));
    }
    setPreferenceScreen(paramBundle);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    paramMenu = paramMenu.add(2131692978);
    paramMenu.setIntent(new Intent(getActivity(), HowItWorks.class));
    paramMenu.setShowAsActionFlags(0);
  }
  
  public void onPause()
  {
    super.onPause();
    this.mPaymentBackend.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    this.mPaymentBackend.onResume();
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    paramView = (ViewGroup)getListView().getParent();
    paramBundle = getActivity().getLayoutInflater().inflate(2130968755, paramView, false);
    paramView.addView(paramBundle);
    setEmptyView(paramBundle);
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mSummaryLoader;
    
    public SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mSummaryLoader = paramSummaryLoader;
    }
    
    public void setListening(boolean paramBoolean)
    {
      if ((paramBoolean) && (NfcAdapter.getDefaultAdapter(this.mContext) != null))
      {
        Object localObject = new PaymentBackend(this.mContext);
        ((PaymentBackend)localObject).refresh();
        localObject = ((PaymentBackend)localObject).getDefaultApp();
        if (localObject != null) {
          this.mSummaryLoader.setSummary(this, this.mContext.getString(2131693590, new Object[] { ((PaymentBackend.PaymentAppInfo)localObject).label }));
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\nfc\PaymentSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */