package com.android.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.oneplus.settings.ui.OPFactoryResetConfirmCategory;
import com.oneplus.settings.ui.OPFactoryResetConfirmCategory.OnFactoryResetConfirmListener;
import java.util.List;

public class MasterClear
  extends OptionsMenuFragment
  implements Preference.OnPreferenceChangeListener, OPFactoryResetConfirmCategory.OnFactoryResetConfirmListener
{
  static final String ERASE_EXTERNAL_EXTRA = "erase_sd";
  private static final int KEYGUARD_REQUEST = 55;
  private static final String TAG = "MasterClear";
  private View mContentView;
  private CheckBox mExternalStorage;
  private View mExternalStorageContainer;
  private Button mInitiateButton;
  private final View.OnClickListener mInitiateListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!MasterClear.-wrap0(MasterClear.this, 55)) {
        MasterClear.-wrap1(MasterClear.this, "");
      }
    }
  };
  private OPFactoryResetConfirmCategory mOPFactoryResetConfirmCategory;
  private SwitchPreference mOptionalSwitchPreference;
  
  private void establishInitialState()
  {
    this.mOptionalSwitchPreference = ((SwitchPreference)findPreference("op_optional_reset"));
    this.mOptionalSwitchPreference.setChecked(false);
    this.mOptionalSwitchPreference.setOnPreferenceChangeListener(this);
    this.mOPFactoryResetConfirmCategory = ((OPFactoryResetConfirmCategory)findPreference("op_factory_reset_confirm"));
    this.mOPFactoryResetConfirmCategory.setOnFactoryResetConfirmListener(this);
  }
  
  private void getContentDescription(View paramView, StringBuffer paramStringBuffer)
  {
    if (paramView.getVisibility() != 0) {
      return;
    }
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      int i = 0;
      while (i < paramView.getChildCount())
      {
        getContentDescription(paramView.getChildAt(i), paramStringBuffer);
        i += 1;
      }
    }
    if ((paramView instanceof TextView))
    {
      paramStringBuffer.append(((TextView)paramView).getText());
      paramStringBuffer.append(",");
    }
  }
  
  private boolean isExtStorageEncrypted()
  {
    return !"".equals(SystemProperties.get("vold.decrypt"));
  }
  
  private void loadAccountList(UserManager paramUserManager)
  {
    View localView = this.mContentView.findViewById(2131362202);
    LinearLayout localLinearLayout = (LinearLayout)this.mContentView.findViewById(2131362203);
    localLinearLayout.removeAllViews();
    Activity localActivity = getActivity();
    List localList = paramUserManager.getProfiles(UserHandle.myUserId());
    int n = localList.size();
    AccountManager localAccountManager = AccountManager.get(localActivity);
    LayoutInflater localLayoutInflater = (LayoutInflater)localActivity.getSystemService("layout_inflater");
    int k = 0;
    int i = 0;
    if (i < n)
    {
      Object localObject1 = (UserInfo)localList.get(i);
      int j = ((UserInfo)localObject1).id;
      UserHandle localUserHandle = new UserHandle(j);
      Account[] arrayOfAccount = localAccountManager.getAccountsAsUser(j);
      int i1 = arrayOfAccount.length;
      if (i1 == 0) {}
      AuthenticatorDescription[] arrayOfAuthenticatorDescription;
      int i2;
      Object localObject4;
      TextView localTextView;
      label208:
      do
      {
        i += 1;
        break;
        int m = k + i1;
        arrayOfAuthenticatorDescription = AccountManager.get(localActivity).getAuthenticatorTypesAsUser(j);
        i2 = arrayOfAuthenticatorDescription.length;
        localObject4 = Utils.inflateCategoryHeader(localLayoutInflater, localLinearLayout);
        localTextView = (TextView)((View)localObject4).findViewById(16908310);
        if (!((UserInfo)localObject1).isManagedProfile()) {
          break label244;
        }
        j = 2131689625;
        localTextView.setText(j);
        localLinearLayout.addView((View)localObject4);
        j = 0;
        k = m;
      } while (j >= i1);
      Account localAccount = arrayOfAccount[j];
      if (!Utils.showAccount(localActivity, localAccount.type)) {}
      for (;;)
      {
        j += 1;
        break label208;
        label244:
        j = 2131689624;
        break;
        localObject1 = null;
        k = 0;
        for (;;)
        {
          localObject4 = localObject1;
          if (k < i2)
          {
            if (localAccount.type.equals(arrayOfAuthenticatorDescription[k].type)) {
              localObject4 = arrayOfAuthenticatorDescription[k];
            }
          }
          else
          {
            if (localObject4 != null) {
              break label352;
            }
            Log.w("MasterClear", "No descriptor for account name=" + localAccount.name + " type=" + localAccount.type);
            break;
          }
          k += 1;
        }
        label352:
        localTextView = null;
        localObject1 = localTextView;
        try
        {
          if (((AuthenticatorDescription)localObject4).iconId != 0)
          {
            localObject1 = localActivity.createPackageContextAsUser(((AuthenticatorDescription)localObject4).packageName, 0, localUserHandle);
            localObject1 = localActivity.getPackageManager().getUserBadgedIcon(((Context)localObject1).getDrawable(((AuthenticatorDescription)localObject4).iconId), localUserHandle);
          }
          localObject4 = localObject1;
          if (localObject1 == null) {
            localObject4 = localActivity.getPackageManager().getDefaultActivityIcon();
          }
          localObject1 = (TextView)localLayoutInflater.inflate(2130968749, localLinearLayout, false);
          ((TextView)localObject1).setText(localAccount.name);
          ((TextView)localObject1).setCompoundDrawablesWithIntrinsicBounds((Drawable)localObject4, null, null, null);
          localLinearLayout.addView((View)localObject1);
        }
        catch (Resources.NotFoundException localNotFoundException)
        {
          for (;;)
          {
            Log.w("MasterClear", "Invalid icon id for account type " + ((AuthenticatorDescription)localObject4).type, localNotFoundException);
            Object localObject2 = localTextView;
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          for (;;)
          {
            Log.w("MasterClear", "Bad package name for account type " + ((AuthenticatorDescription)localObject4).type);
            localObject3 = localTextView;
          }
        }
      }
    }
    if (k > 0)
    {
      localView.setVisibility(0);
      localLinearLayout.setVisibility(0);
    }
    Object localObject3 = this.mContentView.findViewById(2131362204);
    if (paramUserManager.getUserCount() - n > 0)
    {
      i = 1;
      if (i == 0) {
        break label608;
      }
    }
    label608:
    for (i = 0;; i = 8)
    {
      ((View)localObject3).setVisibility(i);
      return;
      i = 0;
      break;
    }
  }
  
  private boolean runKeyguardConfirmation(int paramInt)
  {
    Resources localResources = getActivity().getResources();
    return new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(paramInt, localResources.getText(2131691893));
  }
  
  private void showFinalConfirmation(String paramString)
  {
    Bundle localBundle = new Bundle();
    localBundle.putBoolean("erase_sd", this.mOptionalSwitchPreference.isChecked());
    localBundle.putString("power_on_psw", paramString);
    ((SettingsActivity)getActivity()).startPreferencePanel(MasterClearConfirm.class.getName(), localBundle, 2131691905, null, null, 0);
  }
  
  protected int getMetricsCategory()
  {
    return 66;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 != 55) {
      return;
    }
    if (paramInt2 == -1)
    {
      showFinalConfirmation(paramIntent.getStringExtra("power_on_psw"));
      return;
    }
    establishInitialState();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230809);
    establishInitialState();
  }
  
  public void onFactoryResetConfirmClick()
  {
    if (!runKeyguardConfirmation(55)) {
      showFinalConfirmation("");
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    return paramPreference == this.mOptionalSwitchPreference;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\MasterClear.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */