package com.oneplus.lib.preference;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ListView;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.layout;
import com.oneplus.commonctrl.R.styleable;

public abstract class PreferenceFragment
  extends Fragment
  implements PreferenceManager.OnPreferenceTreeClickListener
{
  private static final int FIRST_REQUEST_CODE = 100;
  private static final int MSG_BIND_PREFERENCES = 1;
  private static final String PREFERENCES_TAG = "android:preferences";
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      PreferenceFragment.-wrap0(PreferenceFragment.this);
    }
  };
  private boolean mHavePrefs;
  private boolean mInitDone;
  private int mLayoutResId = R.layout.preference_list_fragment;
  private ListView mList;
  private View.OnKeyListener mListOnKeyListener = new View.OnKeyListener()
  {
    public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      paramAnonymousView = PreferenceFragment.-get0(PreferenceFragment.this).getSelectedItem();
      if ((paramAnonymousView instanceof Preference))
      {
        View localView = PreferenceFragment.-get0(PreferenceFragment.this).getSelectedView();
        return ((Preference)paramAnonymousView).onKey(localView, paramAnonymousInt, paramAnonymousKeyEvent);
      }
      return false;
    }
  };
  private PreferenceManager mPreferenceManager;
  private final Runnable mRequestFocus = new Runnable()
  {
    public void run()
    {
      PreferenceFragment.-get0(PreferenceFragment.this).focusableViewAvailable(PreferenceFragment.-get0(PreferenceFragment.this));
    }
  };
  
  private void bindPreferences()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (localPreferenceScreen != null) {
      localPreferenceScreen.bind(getListView());
    }
    onBindPreferences();
  }
  
  private void ensureList()
  {
    if (this.mList != null) {
      return;
    }
    View localView = getView();
    if (localView == null) {
      throw new IllegalStateException("Content view not yet created");
    }
    localView = localView.findViewById(16908298);
    if (!(localView instanceof ListView)) {
      throw new RuntimeException("Content has view with id attribute 'android.R.id.list' that is not a ListView class");
    }
    this.mList = ((ListView)localView);
    if (this.mList == null) {
      throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
    }
    this.mList.setOnKeyListener(this.mListOnKeyListener);
    this.mHandler.post(this.mRequestFocus);
  }
  
  private void postBindPreferences()
  {
    if (this.mHandler.hasMessages(1)) {
      return;
    }
    this.mHandler.obtainMessage(1).sendToTarget();
  }
  
  private void requirePreferenceManager()
  {
    if (this.mPreferenceManager == null) {
      throw new RuntimeException("This should be called after super.onCreate.");
    }
  }
  
  public void addPreferencesFromIntent(Intent paramIntent)
  {
    requirePreferenceManager();
    setPreferenceScreen(this.mPreferenceManager.inflateFromIntent(paramIntent, getPreferenceScreen()));
  }
  
  public void addPreferencesFromResource(int paramInt)
  {
    requirePreferenceManager();
    setPreferenceScreen(this.mPreferenceManager.inflateFromResource(getActivity(), paramInt, getPreferenceScreen()));
  }
  
  public Preference findPreference(CharSequence paramCharSequence)
  {
    if (this.mPreferenceManager == null) {
      return null;
    }
    return this.mPreferenceManager.findPreference(paramCharSequence);
  }
  
  public ListView getListView()
  {
    ensureList();
    return this.mList;
  }
  
  public PreferenceManager getPreferenceManager()
  {
    return this.mPreferenceManager;
  }
  
  public PreferenceScreen getPreferenceScreen()
  {
    return this.mPreferenceManager.getPreferenceScreen();
  }
  
  public boolean hasListView()
  {
    if (this.mList != null) {
      return true;
    }
    View localView = getView();
    if (localView == null) {
      return false;
    }
    localView = localView.findViewById(16908298);
    if (!(localView instanceof ListView)) {
      return false;
    }
    this.mList = ((ListView)localView);
    return this.mList != null;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (this.mHavePrefs) {
      bindPreferences();
    }
    this.mInitDone = true;
    if (paramBundle != null)
    {
      paramBundle = paramBundle.getBundle("android:preferences");
      if (paramBundle != null)
      {
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        if (localPreferenceScreen != null) {
          localPreferenceScreen.restoreHierarchyState(paramBundle);
        }
      }
    }
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    this.mPreferenceManager.dispatchActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  protected void onBindPreferences() {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mPreferenceManager = new PreferenceManager(getActivity(), 100);
    this.mPreferenceManager.setFragment(this);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramBundle = getActivity().obtainStyledAttributes(null, R.styleable.PreferenceFragment, R.attr.op_preferenceFragmentStyle, 0);
    this.mLayoutResId = paramBundle.getResourceId(R.styleable.PreferenceFragment_android_layout, this.mLayoutResId);
    paramBundle.recycle();
    return paramLayoutInflater.inflate(this.mLayoutResId, paramViewGroup, false);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.mPreferenceManager.dispatchActivityDestroy();
  }
  
  public void onDestroyView()
  {
    this.mList = null;
    this.mHandler.removeCallbacks(this.mRequestFocus);
    this.mHandler.removeMessages(1);
    super.onDestroyView();
  }
  
  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if ((paramPreference.getFragment() != null) && ((getActivity() instanceof OnPreferenceStartFragmentCallback))) {
      return ((OnPreferenceStartFragmentCallback)getActivity()).onPreferenceStartFragment(this, paramPreference);
    }
    return false;
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (localPreferenceScreen != null)
    {
      Bundle localBundle = new Bundle();
      localPreferenceScreen.saveHierarchyState(localBundle);
      paramBundle.putBundle("android:preferences", localBundle);
    }
  }
  
  public void onStart()
  {
    super.onStart();
    this.mPreferenceManager.setOnPreferenceTreeClickListener(this);
  }
  
  public void onStop()
  {
    super.onStop();
    this.mPreferenceManager.dispatchActivityStop();
    this.mPreferenceManager.setOnPreferenceTreeClickListener(null);
  }
  
  protected void onUnbindPreferences() {}
  
  public void setPreferenceScreen(PreferenceScreen paramPreferenceScreen)
  {
    if ((this.mPreferenceManager.setPreferences(paramPreferenceScreen)) && (paramPreferenceScreen != null))
    {
      onUnbindPreferences();
      this.mHavePrefs = true;
      if (this.mInitDone) {
        postBindPreferences();
      }
    }
  }
  
  public static abstract interface OnPreferenceStartFragmentCallback
  {
    public abstract boolean onPreferenceStartFragment(PreferenceFragment paramPreferenceFragment, Preference paramPreference);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\PreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */