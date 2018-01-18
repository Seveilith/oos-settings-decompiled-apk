package com.android.settings.print;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.print.PrintJob;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.print.PrintManager.PrintJobStateChangeListener;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.SettingsPreferenceFragment;

public class PrintJobSettingsFragment
  extends SettingsPreferenceFragment
{
  private static final String EXTRA_PRINT_JOB_ID = "EXTRA_PRINT_JOB_ID";
  private static final int MENU_ITEM_ID_CANCEL = 1;
  private static final int MENU_ITEM_ID_RESTART = 2;
  private static final String PRINT_JOB_MESSAGE_PREFERENCE = "print_job_message_preference";
  private static final String PRINT_JOB_PREFERENCE = "print_job_preference";
  private Preference mMessagePreference;
  private PrintJobId mPrintJobId;
  private Preference mPrintJobPreference;
  private final PrintManager.PrintJobStateChangeListener mPrintJobStateChangeListener = new PrintManager.PrintJobStateChangeListener()
  {
    public void onPrintJobStateChanged(PrintJobId paramAnonymousPrintJobId)
    {
      PrintJobSettingsFragment.-wrap0(PrintJobSettingsFragment.this);
    }
  };
  private PrintManager mPrintManager;
  
  private PrintJob getPrintJob()
  {
    return this.mPrintManager.getPrintJob(this.mPrintJobId);
  }
  
  private void processArguments()
  {
    String str = getArguments().getString("EXTRA_PRINT_JOB_ID");
    if (str == null)
    {
      finish();
      return;
    }
    this.mPrintJobId = PrintJobId.unflattenFromString(str);
  }
  
  private void updateUi()
  {
    Object localObject = getPrintJob();
    if (localObject == null)
    {
      finish();
      return;
    }
    if ((((PrintJob)localObject).isCancelled()) || (((PrintJob)localObject).isCompleted()))
    {
      finish();
      return;
    }
    PrintJobInfo localPrintJobInfo = ((PrintJob)localObject).getInfo();
    switch (localPrintJobInfo.getState())
    {
    case 5: 
    default: 
      this.mPrintJobPreference.setSummary(getString(2131692418, new Object[] { localPrintJobInfo.getPrinterName(), DateUtils.formatSameDayTime(localPrintJobInfo.getCreationTime(), localPrintJobInfo.getCreationTime(), 3, 3) }));
      switch (localPrintJobInfo.getState())
      {
      case 5: 
      default: 
        label156:
        localObject = localPrintJobInfo.getStatus(getPackageManager());
        if (!TextUtils.isEmpty((CharSequence)localObject))
        {
          if (getPreferenceScreen().findPreference("print_job_message_preference") == null) {
            getPreferenceScreen().addPreference(this.mMessagePreference);
          }
          this.mMessagePreference.setSummary((CharSequence)localObject);
        }
        break;
      }
      break;
    }
    for (;;)
    {
      getActivity().invalidateOptionsMenu();
      return;
      if (!((PrintJob)localObject).getInfo().isCancelling())
      {
        this.mPrintJobPreference.setTitle(getString(2131692419, new Object[] { localPrintJobInfo.getLabel() }));
        break;
      }
      this.mPrintJobPreference.setTitle(getString(2131692420, new Object[] { localPrintJobInfo.getLabel() }));
      break;
      this.mPrintJobPreference.setTitle(getString(2131692421, new Object[] { localPrintJobInfo.getLabel() }));
      break;
      if (!((PrintJob)localObject).getInfo().isCancelling())
      {
        this.mPrintJobPreference.setTitle(getString(2131692422, new Object[] { localPrintJobInfo.getLabel() }));
        break;
      }
      this.mPrintJobPreference.setTitle(getString(2131692420, new Object[] { localPrintJobInfo.getLabel() }));
      break;
      this.mPrintJobPreference.setIcon(2130838021);
      break label156;
      this.mPrintJobPreference.setIcon(2130838022);
      break label156;
      getPreferenceScreen().removePreference(this.mMessagePreference);
    }
  }
  
  protected int getMetricsCategory()
  {
    return 78;
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    paramMenuInflater = getPrintJob();
    if (paramMenuInflater == null) {
      return;
    }
    if (!paramMenuInflater.getInfo().isCancelling()) {
      paramMenu.add(0, 1, 0, getString(2131692417)).setShowAsAction(1);
    }
    if (paramMenuInflater.isFailed()) {
      paramMenu.add(0, 2, 0, getString(2131692416)).setShowAsAction(1);
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    addPreferencesFromResource(2131230828);
    this.mPrintJobPreference = findPreference("print_job_preference");
    this.mMessagePreference = findPreference("print_job_message_preference");
    this.mPrintManager = ((PrintManager)getActivity().getSystemService("print")).getGlobalPrintManagerForUser(getActivity().getUserId());
    getActivity().getActionBar().setTitle(2131692415);
    processArguments();
    setHasOptionsMenu(true);
    return paramLayoutInflater;
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    PrintJob localPrintJob = getPrintJob();
    if (localPrintJob != null) {}
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    case 1: 
      localPrintJob.cancel();
      finish();
      return true;
    }
    localPrintJob.restart();
    finish();
    return true;
  }
  
  public void onStart()
  {
    super.onStart();
    this.mPrintManager.addPrintJobStateChangeListener(this.mPrintJobStateChangeListener);
    updateUi();
  }
  
  public void onStop()
  {
    super.onStop();
    this.mPrintManager.removePrintJobStateChangeListener(this.mPrintJobStateChangeListener);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    getListView().setEnabled(false);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\print\PrintJobSettingsFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */