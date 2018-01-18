package com.android.settings.applications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class RunningServices
  extends SettingsPreferenceFragment
{
  private static final int SHOW_BACKGROUND_PROCESSES = 2;
  private static final int SHOW_RUNNING_SERVICES = 1;
  private View mLoadingContainer;
  private Menu mOptionsMenu;
  private final Runnable mRunningProcessesAvail = new Runnable()
  {
    public void run()
    {
      Utils.handleLoadingContainer(RunningServices.-get0(RunningServices.this), RunningServices.-get1(RunningServices.this), true, true);
    }
  };
  private RunningProcessesView mRunningProcessesView;
  
  private void updateOptionsMenu()
  {
    boolean bool1 = true;
    boolean bool2 = this.mRunningProcessesView.mAdapter.getShowBackground();
    this.mOptionsMenu.findItem(1).setVisible(bool2);
    MenuItem localMenuItem = this.mOptionsMenu.findItem(2);
    if (bool2) {
      bool1 = false;
    }
    localMenuItem.setVisible(bool1);
  }
  
  protected int getMetricsCategory()
  {
    return 404;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setHasOptionsMenu(true);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    this.mOptionsMenu = paramMenu;
    paramMenu.add(0, 1, 1, 2131692109).setShowAsAction(1);
    paramMenu.add(0, 2, 2, 2131692110).setShowAsAction(1);
    updateOptionsMenu();
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = paramLayoutInflater.inflate(2130968747, null);
    this.mRunningProcessesView = ((RunningProcessesView)paramLayoutInflater.findViewById(2131362199));
    this.mRunningProcessesView.doCreate();
    this.mLoadingContainer = paramLayoutInflater.findViewById(2131362189);
    return paramLayoutInflater;
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return false;
    case 1: 
      this.mRunningProcessesView.mAdapter.setShowBackground(false);
    }
    for (;;)
    {
      updateOptionsMenu();
      return true;
      this.mRunningProcessesView.mAdapter.setShowBackground(true);
    }
  }
  
  public void onPause()
  {
    super.onPause();
    this.mRunningProcessesView.doPause();
  }
  
  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    updateOptionsMenu();
  }
  
  public void onResume()
  {
    super.onResume();
    boolean bool = this.mRunningProcessesView.doResume(this, this.mRunningProcessesAvail);
    Utils.handleLoadingContainer(this.mLoadingContainer, this.mRunningProcessesView, bool, false);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\RunningServices.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */