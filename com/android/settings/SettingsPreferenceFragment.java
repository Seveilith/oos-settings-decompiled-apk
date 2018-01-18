package com.android.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.XmlRes;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceGroupAdapter;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.settings.applications.LayoutPreference;
import com.android.settings.widget.FloatingActionButton;
import com.android.settingslib.HelpUtils;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPSettingsDividerItemDecoration;
import java.util.Iterator;
import java.util.UUID;

public abstract class SettingsPreferenceFragment
  extends InstrumentedPreferenceFragment
  implements DialogCreatable
{
  private static final int DELAY_HIGHLIGHT_DURATION_MILLIS = 600;
  public static final String HELP_URI_RESOURCE_KEY = "help_uri_resource";
  private static final int ORDER_FIRST = -1;
  private static final int ORDER_LAST = 2147483646;
  private static final String SAVE_HIGHLIGHTED_KEY = "android:preference_highlighted";
  private static final String TAG = "SettingsPreference";
  private ActionBar mActionBar;
  private HighlightablePreferenceGroupAdapter mAdapter;
  private boolean mAnimationAllowed;
  private ViewGroup mButtonBar;
  private ContentResolver mContentResolver;
  private RecyclerView.Adapter mCurrentRootAdapter;
  private RecyclerView.AdapterDataObserver mDataSetObserver = new RecyclerView.AdapterDataObserver()
  {
    public void onChanged()
    {
      SettingsPreferenceFragment.this.onDataSetChanged();
    }
  };
  private SettingsDialogFragment mDialogFragment;
  private View mEmptyView;
  private FloatingActionButton mFloatingActionButton;
  private LayoutPreference mFooter;
  private LayoutPreference mHeader;
  private String mHelpUri;
  private boolean mIsDataSetObserverRegistered = false;
  private LinearLayoutManager mLayoutManager;
  private ViewGroup mPinnedHeaderFrameLayout;
  private ArrayMap<String, Preference> mPreferenceCache;
  private boolean mPreferenceHighlighted = false;
  private String mPreferenceKey;
  
  private void addPreferenceToTop(LayoutPreference paramLayoutPreference)
  {
    paramLayoutPreference.setOrder(-1);
    if (getPreferenceScreen() != null) {
      getPreferenceScreen().addPreference(paramLayoutPreference);
    }
  }
  
  private int canUseListViewForHighLighting(String paramString)
  {
    if (getListView() == null) {
      return -1;
    }
    RecyclerView.Adapter localAdapter = getListView().getAdapter();
    if ((localAdapter != null) && ((localAdapter instanceof PreferenceGroupAdapter))) {
      return findListPositionFromKey((PreferenceGroupAdapter)localAdapter, paramString);
    }
    return -1;
  }
  
  private void checkAvailablePrefs(PreferenceGroup paramPreferenceGroup)
  {
    if (paramPreferenceGroup == null) {
      return;
    }
    int i = 0;
    if (i < paramPreferenceGroup.getPreferenceCount())
    {
      Preference localPreference = paramPreferenceGroup.getPreference(i);
      if ((!(localPreference instanceof SelfAvailablePreference)) || (((SelfAvailablePreference)localPreference).isAvailable(getContext()))) {
        if ((localPreference instanceof PreferenceGroup)) {
          checkAvailablePrefs((PreferenceGroup)localPreference);
        }
      }
      for (;;)
      {
        i += 1;
        break;
        paramPreferenceGroup.removePreference(localPreference);
      }
    }
  }
  
  private int findListPositionFromKey(PreferenceGroupAdapter paramPreferenceGroupAdapter, String paramString)
  {
    int j = paramPreferenceGroupAdapter.getItemCount();
    int i = 0;
    while (i < j)
    {
      String str = paramPreferenceGroupAdapter.getItem(i).getKey();
      if ((str != null) && (str.equals(paramString))) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private void highlightPreference(String paramString)
  {
    final int i = canUseListViewForHighLighting(paramString);
    if (i >= 0)
    {
      this.mPreferenceHighlighted = true;
      this.mLayoutManager.scrollToPosition(i);
      getView().postDelayed(new Runnable()
      {
        public void run()
        {
          SettingsPreferenceFragment.-get0(SettingsPreferenceFragment.this).highlight(i);
        }
      }, 600L);
    }
  }
  
  private void setFooterView(LayoutPreference paramLayoutPreference)
  {
    if ((getPreferenceScreen() != null) && (this.mFooter != null)) {
      getPreferenceScreen().removePreference(this.mFooter);
    }
    if (paramLayoutPreference != null)
    {
      this.mFooter = paramLayoutPreference;
      this.mFooter.setOrder(2147483646);
      if (getPreferenceScreen() != null) {
        getPreferenceScreen().addPreference(this.mFooter);
      }
      return;
    }
    this.mFooter = null;
  }
  
  private void updateEmptyView()
  {
    int j = 1;
    int k = 0;
    if (this.mEmptyView == null) {
      return;
    }
    if (getPreferenceScreen() != null)
    {
      int m = getPreferenceScreen().getPreferenceCount();
      label44:
      label55:
      View localView;
      if (this.mHeader != null)
      {
        i = 1;
        if (this.mFooter == null) {
          break label79;
        }
        if (m - i - j > 0) {
          break label84;
        }
        i = 1;
        localView = this.mEmptyView;
        if (i == 0) {
          break label89;
        }
      }
      label79:
      label84:
      label89:
      for (int i = k;; i = 8)
      {
        localView.setVisibility(i);
        return;
        i = 0;
        break;
        j = 0;
        break label44;
        i = 0;
        break label55;
      }
    }
    this.mEmptyView.setVisibility(0);
  }
  
  public void addPreferencesFromResource(@XmlRes int paramInt)
  {
    super.addPreferencesFromResource(paramInt);
    checkAvailablePrefs(getPreferenceScreen());
  }
  
  protected void cacheRemoveAllPrefs(PreferenceGroup paramPreferenceGroup)
  {
    this.mPreferenceCache = new ArrayMap();
    int j = paramPreferenceGroup.getPreferenceCount();
    int i = 0;
    if (i < j)
    {
      Preference localPreference = paramPreferenceGroup.getPreference(i);
      if (TextUtils.isEmpty(localPreference.getKey())) {}
      for (;;)
      {
        i += 1;
        break;
        this.mPreferenceCache.put(localPreference.getKey(), localPreference);
      }
    }
  }
  
  public void finish()
  {
    Activity localActivity = getActivity();
    if (localActivity == null) {
      return;
    }
    if (getFragmentManager().getBackStackEntryCount() > 0)
    {
      getFragmentManager().popBackStack();
      return;
    }
    localActivity.finish();
  }
  
  public final void finishFragment()
  {
    Activity localActivity = getActivity();
    if (localActivity != null) {
      localActivity.onBackPressed();
    }
  }
  
  public ViewGroup getButtonBar()
  {
    return this.mButtonBar;
  }
  
  protected int getCachedCount()
  {
    return this.mPreferenceCache.size();
  }
  
  protected Preference getCachedPreference(String paramString)
  {
    Preference localPreference = null;
    if (this.mPreferenceCache != null) {
      localPreference = (Preference)this.mPreferenceCache.remove(paramString);
    }
    return localPreference;
  }
  
  protected ContentResolver getContentResolver()
  {
    Activity localActivity = getActivity();
    if (localActivity != null) {
      this.mContentResolver = localActivity.getContentResolver();
    }
    return this.mContentResolver;
  }
  
  public View getEmptyView()
  {
    return this.mEmptyView;
  }
  
  public FloatingActionButton getFloatingActionButton()
  {
    return this.mFloatingActionButton;
  }
  
  public LayoutPreference getFooterView()
  {
    return this.mFooter;
  }
  
  public LayoutPreference getHeaderView()
  {
    return this.mHeader;
  }
  
  protected int getHelpResource()
  {
    return 2131692999;
  }
  
  protected Intent getIntent()
  {
    if (getActivity() == null) {
      return null;
    }
    return getActivity().getIntent();
  }
  
  protected Button getNextButton()
  {
    return ((ButtonBarHandler)getActivity()).getNextButton();
  }
  
  protected PackageManager getPackageManager()
  {
    return SettingsBaseApplication.mApplication.getPackageManager();
  }
  
  protected final Context getPrefContext()
  {
    return getPreferenceManager().getContext();
  }
  
  protected Object getSystemService(String paramString)
  {
    return getActivity().getSystemService(paramString);
  }
  
  protected boolean hasNextButton()
  {
    return ((ButtonBarHandler)getActivity()).hasNextButton();
  }
  
  public void highlightPreferenceIfNeeded()
  {
    if ((!isAdded()) || (this.mPreferenceHighlighted)) {}
    while (!TextUtils.isEmpty(this.mPreferenceKey)) {
      return;
    }
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    setHasOptionsMenu(true);
  }
  
  protected void onBindPreferences()
  {
    registerObserverIfNeeded();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null) {
      this.mPreferenceHighlighted = paramBundle.getBoolean("android:preference_highlighted");
    }
    paramBundle = getArguments();
    if ((paramBundle != null) && (paramBundle.containsKey("help_uri_resource"))) {}
    for (int i = paramBundle.getInt("help_uri_resource");; i = getHelpResource())
    {
      if (i != 0) {
        this.mHelpUri = getResources().getString(i);
      }
      return;
    }
  }
  
  protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen paramPreferenceScreen)
  {
    this.mAdapter = new HighlightablePreferenceGroupAdapter(paramPreferenceScreen);
    return this.mAdapter;
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    return null;
  }
  
  public RecyclerView.LayoutManager onCreateLayoutManager()
  {
    this.mLayoutManager = new LinearLayoutManager(getContext());
    return this.mLayoutManager;
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if ((this.mHelpUri != null) && (getActivity() != null)) {
      HelpUtils.prepareHelpMenuItem(getActivity(), paramMenu, this.mHelpUri, getClass().getName());
    }
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString) {}
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    this.mPinnedHeaderFrameLayout = ((ViewGroup)paramLayoutInflater.findViewById(2131362092));
    this.mFloatingActionButton = ((FloatingActionButton)paramLayoutInflater.findViewById(2131362447));
    this.mButtonBar = ((ViewGroup)paramLayoutInflater.findViewById(2131362444));
    setDivider(null);
    getListView().addItemDecoration(new OPSettingsDividerItemDecoration(SettingsBaseApplication.mApplication, 2130838246, 2131755334));
    return paramLayoutInflater;
  }
  
  protected void onDataSetChanged()
  {
    highlightPreferenceIfNeeded();
    updateEmptyView();
  }
  
  public void onDetach()
  {
    if ((isRemoving()) && (this.mDialogFragment != null))
    {
      this.mDialogFragment.dismiss();
      this.mDialogFragment = null;
    }
    super.onDetach();
  }
  
  public void onDialogShowing() {}
  
  public void onDisplayPreferenceDialog(Preference paramPreference)
  {
    if (paramPreference.getKey() == null) {
      paramPreference.setKey(UUID.randomUUID().toString());
    }
    if ((paramPreference instanceof RestrictedListPreference)) {
      paramPreference = RestrictedListPreference.RestrictedListPreferenceDialogFragment.newInstance(paramPreference.getKey());
    }
    for (;;)
    {
      paramPreference.setTargetFragment(this, 0);
      paramPreference.show(getFragmentManager(), "dialog_preference");
      onDialogShowing();
      return;
      if ((paramPreference instanceof CustomListPreference))
      {
        paramPreference = CustomListPreference.CustomListPreferenceDialogFragment.newInstance(paramPreference.getKey());
      }
      else if ((paramPreference instanceof CustomDialogPreference))
      {
        paramPreference = CustomDialogPreference.CustomPreferenceDialogFragment.newInstance(paramPreference.getKey());
      }
      else
      {
        if (!(paramPreference instanceof CustomEditTextPreference)) {
          break;
        }
        paramPreference = CustomEditTextPreference.CustomPreferenceDialogFragment.newInstance(paramPreference.getKey());
      }
    }
    super.onDisplayPreferenceDialog(paramPreference);
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 16908332)
    {
      finishFragment();
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
  
  public void onResume()
  {
    super.onResume();
    Bundle localBundle = getArguments();
    if (localBundle != null)
    {
      this.mPreferenceKey = localBundle.getString(":settings:fragment_args_key");
      highlightPreferenceIfNeeded();
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean("android:preference_highlighted", this.mPreferenceHighlighted);
  }
  
  protected void onUnbindPreferences()
  {
    unregisterObserverIfNeeded();
  }
  
  public void registerObserverIfNeeded()
  {
    if (!this.mIsDataSetObserverRegistered)
    {
      if (this.mCurrentRootAdapter != null) {
        this.mCurrentRootAdapter.unregisterAdapterDataObserver(this.mDataSetObserver);
      }
      this.mCurrentRootAdapter = getListView().getAdapter();
      this.mCurrentRootAdapter.registerAdapterDataObserver(this.mDataSetObserver);
      this.mIsDataSetObserverRegistered = true;
      onDataSetChanged();
    }
  }
  
  protected void removeCachedPrefs(PreferenceGroup paramPreferenceGroup)
  {
    Iterator localIterator = this.mPreferenceCache.values().iterator();
    while (localIterator.hasNext())
    {
      Preference localPreference = (Preference)localIterator.next();
      Log.w("SettingsPreference", "p = " + localPreference);
      paramPreferenceGroup.removePreference(localPreference);
    }
  }
  
  protected void removeDialog(int paramInt)
  {
    if ((this.mDialogFragment != null) && (this.mDialogFragment.getDialogId() == paramInt)) {
      this.mDialogFragment.dismiss();
    }
    this.mDialogFragment = null;
  }
  
  protected void removeDialog(int paramInt, boolean paramBoolean)
  {
    Activity localActivity = getActivity();
    if ((localActivity == null) || (localActivity.isFinishing())) {
      return;
    }
    if (paramBoolean)
    {
      if ((this.mDialogFragment != null) && (this.mDialogFragment.getDialogId() == paramInt)) {
        getFragmentManager().beginTransaction().remove(this.mDialogFragment).commitAllowingStateLoss();
      }
      this.mDialogFragment = null;
      return;
    }
    removeDialog(paramInt);
  }
  
  protected void removePreference(String paramString)
  {
    paramString = findPreference(paramString);
    if (paramString != null) {
      getPreferenceScreen().removePreference(paramString);
    }
  }
  
  protected void setAnimationAllowed(boolean paramBoolean)
  {
    this.mAnimationAllowed = paramBoolean;
  }
  
  public void setEmptyView(View paramView)
  {
    if (this.mEmptyView != null) {
      this.mEmptyView.setVisibility(8);
    }
    this.mEmptyView = paramView;
    updateEmptyView();
  }
  
  protected void setFooterView(int paramInt)
  {
    if (paramInt != 0) {}
    for (LayoutPreference localLayoutPreference = new LayoutPreference(getPrefContext(), paramInt);; localLayoutPreference = null)
    {
      setFooterView(localLayoutPreference);
      return;
    }
  }
  
  protected void setFooterView(View paramView)
  {
    LayoutPreference localLayoutPreference = null;
    if (paramView != null) {
      localLayoutPreference = new LayoutPreference(getPrefContext(), paramView);
    }
    setFooterView(localLayoutPreference);
  }
  
  protected void setHeaderView(int paramInt)
  {
    this.mHeader = new LayoutPreference(getPrefContext(), paramInt);
    addPreferenceToTop(this.mHeader);
  }
  
  protected void setHeaderView(View paramView)
  {
    this.mHeader = new LayoutPreference(getPrefContext(), paramView);
    addPreferenceToTop(this.mHeader);
  }
  
  public void setLoading(boolean paramBoolean1, boolean paramBoolean2)
  {
    View localView = getView().findViewById(2131362189);
    RecyclerView localRecyclerView = getListView();
    if (paramBoolean1) {}
    for (paramBoolean1 = false;; paramBoolean1 = true)
    {
      Utils.handleLoadingContainer(localView, localRecyclerView, paramBoolean1, paramBoolean2);
      return;
    }
  }
  
  protected void setOnCancelListener(DialogInterface.OnCancelListener paramOnCancelListener)
  {
    if (this.mDialogFragment != null) {
      SettingsDialogFragment.-set0(this.mDialogFragment, paramOnCancelListener);
    }
  }
  
  protected void setOnDismissListener(DialogInterface.OnDismissListener paramOnDismissListener)
  {
    if (this.mDialogFragment != null) {
      SettingsDialogFragment.-set1(this.mDialogFragment, paramOnDismissListener);
    }
  }
  
  public View setPinnedHeaderView(int paramInt)
  {
    View localView = getActivity().getLayoutInflater().inflate(paramInt, this.mPinnedHeaderFrameLayout, false);
    setPinnedHeaderView(localView);
    return localView;
  }
  
  public void setPinnedHeaderView(View paramView)
  {
    this.mPinnedHeaderFrameLayout.addView(paramView);
    this.mPinnedHeaderFrameLayout.setVisibility(0);
  }
  
  public void setPreferenceScreen(PreferenceScreen paramPreferenceScreen)
  {
    if ((paramPreferenceScreen == null) || (paramPreferenceScreen.isAttached())) {}
    for (;;)
    {
      super.setPreferenceScreen(paramPreferenceScreen);
      if (paramPreferenceScreen != null)
      {
        if (this.mHeader != null) {
          paramPreferenceScreen.addPreference(this.mHeader);
        }
        if (this.mFooter != null) {
          paramPreferenceScreen.addPreference(this.mFooter);
        }
      }
      return;
      paramPreferenceScreen.setShouldUseGeneratedIds(this.mAnimationAllowed);
    }
  }
  
  protected void setResult(int paramInt)
  {
    if (getActivity() == null) {
      return;
    }
    getActivity().setResult(paramInt);
  }
  
  protected void setResult(int paramInt, Intent paramIntent)
  {
    if (getActivity() == null) {
      return;
    }
    getActivity().setResult(paramInt, paramIntent);
  }
  
  protected void showDialog(int paramInt)
  {
    Activity localActivity = getActivity();
    if ((localActivity == null) || (localActivity.isFinishing())) {
      return;
    }
    if (this.mDialogFragment != null) {
      Log.e("SettingsPreference", "Old dialog fragment not null!");
    }
    try
    {
      this.mDialogFragment = new SettingsDialogFragment(this, paramInt);
      this.mDialogFragment.show(getChildFragmentManager(), Integer.toString(paramInt));
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      localIllegalStateException.printStackTrace();
    }
  }
  
  public void showLoadingWhenEmpty()
  {
    setEmptyView(getView().findViewById(2131362189));
  }
  
  public boolean startFragment(Fragment paramFragment, String paramString, int paramInt1, int paramInt2, Bundle paramBundle)
  {
    Activity localActivity = getActivity();
    if ((localActivity instanceof SettingsActivity))
    {
      ((SettingsActivity)localActivity).startPreferencePanel(paramString, paramBundle, paramInt1, null, paramFragment, paramInt2);
      return true;
    }
    Log.w("SettingsPreference", "Parent isn't SettingsActivity nor PreferenceActivity, thus there's no way to launch the given Fragment (name: " + paramString + ", requestCode: " + paramInt2 + ")");
    return false;
  }
  
  public void unregisterObserverIfNeeded()
  {
    if (this.mIsDataSetObserverRegistered)
    {
      if (this.mCurrentRootAdapter != null)
      {
        this.mCurrentRootAdapter.unregisterAdapterDataObserver(this.mDataSetObserver);
        this.mCurrentRootAdapter = null;
      }
      this.mIsDataSetObserverRegistered = false;
    }
  }
  
  public static class HighlightablePreferenceGroupAdapter
    extends PreferenceGroupAdapter
  {
    private int mHighlightPosition = -1;
    
    public HighlightablePreferenceGroupAdapter(PreferenceGroup paramPreferenceGroup)
    {
      super();
    }
    
    public void highlight(int paramInt)
    {
      this.mHighlightPosition = paramInt;
      notifyDataSetChanged();
    }
    
    public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder, int paramInt)
    {
      super.onBindViewHolder(paramPreferenceViewHolder, paramInt);
      if (paramInt == this.mHighlightPosition)
      {
        paramPreferenceViewHolder = paramPreferenceViewHolder.itemView;
        if (paramPreferenceViewHolder.getBackground() != null)
        {
          paramInt = paramPreferenceViewHolder.getWidth() / 2;
          int i = paramPreferenceViewHolder.getHeight() / 2;
          paramPreferenceViewHolder.getBackground().setHotspot(paramInt, i);
        }
        paramPreferenceViewHolder.setPressed(true);
        paramPreferenceViewHolder.setPressed(false);
        this.mHighlightPosition = -1;
      }
    }
  }
  
  public static class SettingsDialogFragment
    extends DialogFragment
  {
    private static final String KEY_DIALOG_ID = "key_dialog_id";
    private static final String KEY_PARENT_FRAGMENT_ID = "key_parent_fragment_id";
    private int mDialogId;
    private DialogInterface.OnCancelListener mOnCancelListener;
    private DialogInterface.OnDismissListener mOnDismissListener;
    private Fragment mParentFragment;
    
    public SettingsDialogFragment() {}
    
    public SettingsDialogFragment(DialogCreatable paramDialogCreatable, int paramInt)
    {
      this.mDialogId = paramInt;
      if (!(paramDialogCreatable instanceof Fragment)) {
        throw new IllegalArgumentException("fragment argument must be an instance of " + Fragment.class.getName());
      }
      this.mParentFragment = ((Fragment)paramDialogCreatable);
    }
    
    public int getDialogId()
    {
      return this.mDialogId;
    }
    
    public void onCancel(DialogInterface paramDialogInterface)
    {
      super.onCancel(paramDialogInterface);
      if (this.mOnCancelListener != null) {
        this.mOnCancelListener.onCancel(paramDialogInterface);
      }
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      if (paramBundle != null)
      {
        this.mDialogId = paramBundle.getInt("key_dialog_id", 0);
        this.mParentFragment = getParentFragment();
        int i = paramBundle.getInt("key_parent_fragment_id", -1);
        if (this.mParentFragment == null) {
          this.mParentFragment = getFragmentManager().findFragmentById(i);
        }
        if (!(this.mParentFragment instanceof DialogCreatable))
        {
          StringBuilder localStringBuilder = new StringBuilder();
          if (this.mParentFragment != null) {}
          for (paramBundle = this.mParentFragment.getClass().getName();; paramBundle = Integer.valueOf(i)) {
            throw new IllegalArgumentException(paramBundle + " must implement " + DialogCreatable.class.getName());
          }
        }
        if ((this.mParentFragment instanceof SettingsPreferenceFragment)) {
          SettingsPreferenceFragment.-set0((SettingsPreferenceFragment)this.mParentFragment, this);
        }
      }
      return ((DialogCreatable)this.mParentFragment).onCreateDialog(this.mDialogId);
    }
    
    public void onDetach()
    {
      super.onDetach();
      if (((this.mParentFragment instanceof SettingsPreferenceFragment)) && (SettingsPreferenceFragment.-get1((SettingsPreferenceFragment)this.mParentFragment) == this)) {
        SettingsPreferenceFragment.-set0((SettingsPreferenceFragment)this.mParentFragment, null);
      }
    }
    
    public void onDismiss(DialogInterface paramDialogInterface)
    {
      super.onDismiss(paramDialogInterface);
      if (this.mOnDismissListener != null) {
        this.mOnDismissListener.onDismiss(paramDialogInterface);
      }
    }
    
    public void onSaveInstanceState(Bundle paramBundle)
    {
      super.onSaveInstanceState(paramBundle);
      if (this.mParentFragment != null)
      {
        paramBundle.putInt("key_dialog_id", this.mDialogId);
        paramBundle.putInt("key_parent_fragment_id", this.mParentFragment.getId());
      }
    }
    
    public void onStart()
    {
      super.onStart();
      if ((this.mParentFragment != null) && ((this.mParentFragment instanceof SettingsPreferenceFragment))) {
        ((SettingsPreferenceFragment)this.mParentFragment).onDialogShowing();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SettingsPreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */