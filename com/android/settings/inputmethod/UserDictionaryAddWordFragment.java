package com.android.settings.inputmethod;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import com.android.internal.app.LocalePicker.LocaleSelectionListener;
import com.android.settings.InstrumentedFragment;
import com.android.settings.SettingsActivity;
import java.util.ArrayList;
import java.util.Locale;

public class UserDictionaryAddWordFragment
  extends InstrumentedFragment
  implements AdapterView.OnItemSelectedListener, LocalePicker.LocaleSelectionListener
{
  private static final int OPTIONS_MENU_DELETE = 1;
  private UserDictionaryAddWordContents mContents;
  private boolean mIsDeleting = false;
  private View mRootView;
  
  private void updateSpinner()
  {
    ArrayList localArrayList = this.mContents.getLocalesList(getActivity());
    new ArrayAdapter(getActivity(), 17367048, localArrayList).setDropDownViewResource(17367049);
  }
  
  protected int getMetricsCategory()
  {
    return 62;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    setHasOptionsMenu(true);
    getActivity().getActionBar().setTitle(2131692263);
    setRetainInstance(true);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 1, 0, 2131692747).setIcon(17301564).setShowAsAction(5);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mRootView = paramLayoutInflater.inflate(2130969098, null);
    this.mIsDeleting = false;
    if (this.mContents == null) {}
    for (this.mContents = new UserDictionaryAddWordContents(this.mRootView, getArguments());; this.mContents = new UserDictionaryAddWordContents(this.mRootView, this.mContents))
    {
      getActivity().getActionBar().setSubtitle(UserDictionarySettingsUtils.getLocaleDisplayName(getActivity(), this.mContents.getCurrentUserDictionaryLocale()));
      return this.mRootView;
    }
  }
  
  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    paramAdapterView = (UserDictionaryAddWordContents.LocaleRenderer)paramAdapterView.getItemAtPosition(paramInt);
    if (paramAdapterView.isMoreLanguages())
    {
      ((SettingsActivity)getActivity()).startPreferenceFragment(new UserDictionaryLocalePicker(this), true);
      return;
    }
    this.mContents.updateLocale(paramAdapterView.getLocaleString());
  }
  
  public void onLocaleSelected(Locale paramLocale)
  {
    this.mContents.updateLocale(paramLocale.toString());
    getActivity().onBackPressed();
  }
  
  public void onNothingSelected(AdapterView<?> paramAdapterView)
  {
    paramAdapterView = getArguments();
    this.mContents.updateLocale(paramAdapterView.getString("locale"));
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 1)
    {
      this.mContents.delete(getActivity());
      this.mIsDeleting = true;
      getActivity().onBackPressed();
      return true;
    }
    return false;
  }
  
  public void onPause()
  {
    super.onPause();
    if (!this.mIsDeleting) {
      this.mContents.apply(getActivity(), null);
    }
  }
  
  public void onResume()
  {
    super.onResume();
    updateSpinner();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\UserDictionaryAddWordFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */