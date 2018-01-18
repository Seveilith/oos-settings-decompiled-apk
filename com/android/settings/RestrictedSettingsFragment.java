package com.android.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionsManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.View;
import android.widget.TextView;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;

public abstract class RestrictedSettingsFragment
  extends SettingsPreferenceFragment
{
  private static final String KEY_CHALLENGE_REQUESTED = "chrq";
  private static final String KEY_CHALLENGE_SUCCEEDED = "chsc";
  private static final int REQUEST_PIN_CHALLENGE = 12309;
  protected static final String RESTRICT_IF_OVERRIDABLE = "restrict_if_overridable";
  private View mAdminSupportDetails;
  private boolean mChallengeRequested;
  private boolean mChallengeSucceeded;
  private TextView mEmptyTextView;
  private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
  private boolean mIsAdminUser;
  private boolean mOnlyAvailableForAdmins = false;
  private final String mRestrictionKey;
  private RestrictionsManager mRestrictionsManager;
  private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (!RestrictedSettingsFragment.-get0(RestrictedSettingsFragment.this))
      {
        RestrictedSettingsFragment.-set1(RestrictedSettingsFragment.this, false);
        RestrictedSettingsFragment.-set0(RestrictedSettingsFragment.this, false);
      }
    }
  };
  private UserManager mUserManager;
  
  public RestrictedSettingsFragment(String paramString)
  {
    this.mRestrictionKey = paramString;
  }
  
  private void ensurePin()
  {
    if ((this.mChallengeSucceeded) || (this.mChallengeRequested)) {}
    Intent localIntent;
    do
    {
      do
      {
        return;
      } while (!this.mRestrictionsManager.hasRestrictionsProvider());
      localIntent = this.mRestrictionsManager.createLocalApprovalIntent();
    } while (localIntent == null);
    this.mChallengeRequested = true;
    this.mChallengeSucceeded = false;
    PersistableBundle localPersistableBundle = new PersistableBundle();
    localPersistableBundle.putString("android.request.mesg", getResources().getString(2131693334));
    localIntent.putExtra("android.content.extra.REQUEST_BUNDLE", localPersistableBundle);
    startActivityForResult(localIntent, 12309);
  }
  
  private View initAdminSupportDetailsView()
  {
    return getActivity().findViewById(2131361945);
  }
  
  public TextView getEmptyTextView()
  {
    return this.mEmptyTextView;
  }
  
  public RestrictedLockUtils.EnforcedAdmin getRestrictionEnforcedAdmin()
  {
    this.mEnforcedAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(getActivity(), this.mRestrictionKey, UserHandle.myUserId());
    if ((this.mEnforcedAdmin != null) && (this.mEnforcedAdmin.userId == 55536)) {
      this.mEnforcedAdmin.userId = UserHandle.myUserId();
    }
    return this.mEnforcedAdmin;
  }
  
  protected boolean hasChallengeSucceeded()
  {
    boolean bool2 = true;
    boolean bool1;
    if (this.mChallengeRequested)
    {
      bool1 = bool2;
      if (this.mChallengeSucceeded) {}
    }
    else
    {
      bool1 = bool2;
      if (this.mChallengeRequested) {
        bool1 = false;
      }
    }
    return bool1;
  }
  
  protected TextView initEmptyTextView()
  {
    return (TextView)getActivity().findViewById(16908292);
  }
  
  protected boolean isRestrictedAndNotProviderProtected()
  {
    if ((this.mRestrictionKey == null) || ("restrict_if_overridable".equals(this.mRestrictionKey))) {
      return false;
    }
    return (this.mUserManager.hasUserRestriction(this.mRestrictionKey)) && (!this.mRestrictionsManager.hasRestrictionsProvider());
  }
  
  protected boolean isUiRestricted()
  {
    if ((!isRestrictedAndNotProviderProtected()) && (hasChallengeSucceeded()))
    {
      if (!this.mIsAdminUser) {
        return this.mOnlyAvailableForAdmins;
      }
    }
    else {
      return true;
    }
    return false;
  }
  
  protected boolean isUiRestrictedByOnlyAdmin()
  {
    boolean bool2 = true;
    boolean bool1;
    if ((!isUiRestricted()) || (this.mUserManager.hasBaseUserRestriction(this.mRestrictionKey, UserHandle.of(UserHandle.myUserId())))) {
      bool1 = false;
    }
    do
    {
      do
      {
        return bool1;
        bool1 = bool2;
      } while (this.mIsAdminUser);
      bool1 = bool2;
    } while (!this.mOnlyAvailableForAdmins);
    return false;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mAdminSupportDetails = initAdminSupportDetailsView();
    this.mEmptyTextView = initEmptyTextView();
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 12309)
    {
      if (paramInt2 == -1)
      {
        this.mChallengeSucceeded = true;
        this.mChallengeRequested = false;
        return;
      }
      this.mChallengeSucceeded = false;
      return;
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mRestrictionsManager = ((RestrictionsManager)getSystemService("restrictions"));
    this.mUserManager = ((UserManager)getSystemService("user"));
    this.mIsAdminUser = this.mUserManager.isAdminUser();
    if (paramBundle != null)
    {
      this.mChallengeSucceeded = paramBundle.getBoolean("chsc", false);
      this.mChallengeRequested = paramBundle.getBoolean("chrq", false);
    }
    paramBundle = new IntentFilter("android.intent.action.SCREEN_OFF");
    paramBundle.addAction("android.intent.action.USER_PRESENT");
    getActivity().registerReceiver(this.mScreenOffReceiver, paramBundle);
  }
  
  protected void onDataSetChanged()
  {
    highlightPreferenceIfNeeded();
    if ((this.mAdminSupportDetails != null) && (isUiRestrictedByOnlyAdmin()))
    {
      RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = getRestrictionEnforcedAdmin();
      ShowAdminSupportDetailsDialog.setAdminSupportDetails(getActivity(), this.mAdminSupportDetails, localEnforcedAdmin, false);
      setEmptyView(this.mAdminSupportDetails);
    }
    for (;;)
    {
      super.onDataSetChanged();
      return;
      if (this.mEmptyTextView != null) {
        setEmptyView(this.mEmptyTextView);
      }
    }
  }
  
  public void onDestroy()
  {
    getActivity().unregisterReceiver(this.mScreenOffReceiver);
    super.onDestroy();
  }
  
  public void onResume()
  {
    super.onResume();
    if (shouldBeProviderProtected(this.mRestrictionKey)) {
      ensurePin();
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (getActivity().isChangingConfigurations())
    {
      paramBundle.putBoolean("chrq", this.mChallengeRequested);
      paramBundle.putBoolean("chsc", this.mChallengeSucceeded);
    }
  }
  
  public void setIfOnlyAvailableForAdmins(boolean paramBoolean)
  {
    this.mOnlyAvailableForAdmins = paramBoolean;
  }
  
  protected boolean shouldBeProviderProtected(String paramString)
  {
    boolean bool2 = false;
    if (paramString == null) {
      return false;
    }
    if (!"restrict_if_overridable".equals(paramString)) {}
    for (boolean bool1 = this.mUserManager.hasUserRestriction(this.mRestrictionKey);; bool1 = true)
    {
      if (bool1) {
        bool2 = this.mRestrictionsManager.hasRestrictionsProvider();
      }
      return bool2;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\RestrictedSettingsFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */