package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;
import com.android.settingslib.dream.DreamBackend;
import com.android.settingslib.dream.DreamBackend.DreamInfo;
import java.util.List;

public class DreamSettings
  extends SettingsPreferenceFragment
  implements SwitchBar.OnSwitchChangeListener
{
  static final boolean DEBUG = false;
  private static final int DIALOG_WHEN_TO_DREAM = 1;
  private static final String PACKAGE_SCHEME = "package";
  private static final String TAG = DreamSettings.class.getSimpleName();
  private DreamBackend mBackend;
  private Context mContext;
  private MenuItem[] mMenuItemsWhenEnabled;
  private final PackageReceiver mPackageReceiver = new PackageReceiver(null);
  private boolean mRefreshing;
  private SwitchBar mSwitchBar;
  
  private MenuItem createMenuItem(Menu paramMenu, int paramInt1, int paramInt2, boolean paramBoolean, final Runnable paramRunnable)
  {
    paramMenu = paramMenu.add(paramInt1);
    paramMenu.setShowAsAction(paramInt2);
    paramMenu.setEnabled(paramBoolean);
    paramMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
    {
      public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
      {
        paramRunnable.run();
        return true;
      }
    });
    return paramMenu;
  }
  
  private Dialog createWhenToDreamDialog()
  {
    String str1 = this.mContext.getString(2131691634);
    String str2 = this.mContext.getString(2131691633);
    String str3 = this.mContext.getString(2131691632);
    int i;
    if ((this.mBackend.isActivatedOnDock()) && (this.mBackend.isActivatedOnSleep())) {
      i = 2;
    }
    for (;;)
    {
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.mContext).setTitle(2131691637);
      DialogInterface.OnClickListener local4 = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          boolean bool2 = true;
          DreamBackend localDreamBackend = DreamSettings.-get0(DreamSettings.this);
          if ((paramAnonymousInt == 0) || (paramAnonymousInt == 2))
          {
            bool1 = true;
            localDreamBackend.setActivatedOnDock(bool1);
            localDreamBackend = DreamSettings.-get0(DreamSettings.this);
            bool1 = bool2;
            if (paramAnonymousInt != 1) {
              if (paramAnonymousInt != 2) {
                break label72;
              }
            }
          }
          label72:
          for (boolean bool1 = bool2;; bool1 = false)
          {
            localDreamBackend.setActivatedOnSleep(bool1);
            paramAnonymousDialogInterface.dismiss();
            return;
            bool1 = false;
            break;
          }
        }
      };
      return localBuilder.setSingleChoiceItems(new CharSequence[] { str1, str2, str3 }, i, local4).create();
      if (this.mBackend.isActivatedOnDock()) {
        i = 0;
      } else if (this.mBackend.isActivatedOnSleep()) {
        i = 1;
      } else {
        i = -1;
      }
    }
  }
  
  public static int getSummaryResource(Context paramContext)
  {
    paramContext = new DreamBackend(paramContext);
    boolean bool3 = paramContext.isEnabled();
    boolean bool4 = paramContext.isActivatedOnSleep();
    boolean bool2 = paramContext.isActivatedOnDock();
    if (bool4) {}
    for (boolean bool1 = bool2; !bool3; bool1 = false) {
      return 2131691635;
    }
    if (bool1) {
      return 2131691631;
    }
    if (bool4) {
      return 2131691633;
    }
    if (bool2) {
      return 2131691634;
    }
    return 0;
  }
  
  public static CharSequence getSummaryTextWithDreamName(Context paramContext)
  {
    DreamBackend localDreamBackend = new DreamBackend(paramContext);
    if (!localDreamBackend.isEnabled()) {
      return paramContext.getString(2131691635);
    }
    return localDreamBackend.getActiveDreamName();
  }
  
  private static void logd(String paramString, Object... paramVarArgs) {}
  
  private void refreshFromBackend()
  {
    logd("refreshFromBackend()", new Object[0]);
    this.mRefreshing = true;
    boolean bool = this.mBackend.isEnabled();
    if (this.mSwitchBar.isChecked() != bool) {
      this.mSwitchBar.setChecked(bool);
    }
    if (getPreferenceScreen() == null) {
      setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
    }
    getPreferenceScreen().removeAll();
    Object localObject;
    int j;
    int i;
    if (bool)
    {
      localObject = this.mBackend.getDreamInfos();
      j = ((List)localObject).size();
      i = 0;
      while (i < j)
      {
        getPreferenceScreen().addPreference(new DreamInfoPreference(getPrefContext(), (DreamBackend.DreamInfo)((List)localObject).get(i)));
        i += 1;
      }
    }
    if (this.mMenuItemsWhenEnabled != null)
    {
      localObject = this.mMenuItemsWhenEnabled;
      j = localObject.length;
      i = 0;
      while (i < j)
      {
        localObject[i].setEnabled(bool);
        i += 1;
      }
    }
    this.mRefreshing = false;
  }
  
  public int getHelpResource()
  {
    return 2131693024;
  }
  
  protected int getMetricsCategory()
  {
    return 47;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    logd("onActivityCreated(%s)", new Object[] { paramBundle });
    super.onActivityCreated(paramBundle);
    paramBundle = (TextView)getView().findViewById(16908292);
    paramBundle.setText(2131691636);
    setEmptyView(paramBundle);
    this.mSwitchBar = ((SettingsActivity)getActivity()).getSwitchBar();
    this.mSwitchBar.addOnSwitchChangeListener(this);
    this.mSwitchBar.show();
  }
  
  public void onAttach(Activity paramActivity)
  {
    logd("onAttach(%s)", new Object[] { paramActivity.getClass().getSimpleName() });
    super.onAttach(paramActivity);
    this.mContext = paramActivity;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    logd("onCreate(%s)", new Object[] { paramBundle });
    super.onCreate(paramBundle);
    this.mBackend = new DreamBackend(getActivity());
    setHasOptionsMenu(true);
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    logd("onCreateDialog(%s)", new Object[] { Integer.valueOf(paramInt) });
    if (paramInt == 1) {
      return createWhenToDreamDialog();
    }
    return super.onCreateDialog(paramInt);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    logd("onCreateOptionsMenu()", new Object[0]);
    boolean bool = this.mBackend.isEnabled();
    MenuItem localMenuItem1 = createMenuItem(paramMenu, 2131691638, 0, bool, new Runnable()
    {
      public void run()
      {
        DreamSettings.-get0(DreamSettings.this).startDreaming();
      }
    });
    MenuItem localMenuItem2 = createMenuItem(paramMenu, 2131691637, 0, bool, new Runnable()
    {
      public void run()
      {
        DreamSettings.this.showDialog(1);
      }
    });
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    this.mMenuItemsWhenEnabled = new MenuItem[] { localMenuItem1, localMenuItem2 };
  }
  
  public void onDestroyView()
  {
    logd("onDestroyView()", new Object[0]);
    super.onDestroyView();
    this.mSwitchBar.removeOnSwitchChangeListener(this);
    this.mSwitchBar.hide();
  }
  
  public void onPause()
  {
    logd("onPause()", new Object[0]);
    super.onPause();
    this.mContext.unregisterReceiver(this.mPackageReceiver);
  }
  
  public void onResume()
  {
    logd("onResume()", new Object[0]);
    super.onResume();
    refreshFromBackend();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
    localIntentFilter.addDataScheme("package");
    this.mContext.registerReceiver(this.mPackageReceiver, localIntentFilter);
  }
  
  public void onStart()
  {
    logd("onStart()", new Object[0]);
    super.onStart();
  }
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    if (!this.mRefreshing)
    {
      this.mBackend.setEnabled(paramBoolean);
      refreshFromBackend();
    }
  }
  
  private class DreamInfoPreference
    extends Preference
  {
    private final DreamBackend.DreamInfo mInfo;
    
    public DreamInfoPreference(Context paramContext, DreamBackend.DreamInfo paramDreamInfo)
    {
      super();
      this.mInfo = paramDreamInfo;
      setLayoutResource(2130968693);
      setTitle(this.mInfo.caption);
      setIcon(this.mInfo.icon);
    }
    
    public void onBindViewHolder(final PreferenceViewHolder paramPreferenceViewHolder)
    {
      int k = 0;
      super.onBindViewHolder(paramPreferenceViewHolder);
      Object localObject = (RadioButton)paramPreferenceViewHolder.findViewById(16908313);
      ((RadioButton)localObject).setChecked(this.mInfo.isActive);
      ((RadioButton)localObject).setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          paramPreferenceViewHolder.itemView.onTouchEvent(paramAnonymousMotionEvent);
          return false;
        }
      });
      int i;
      int j;
      if (this.mInfo.settingsComponentName != null)
      {
        i = 1;
        localObject = paramPreferenceViewHolder.findViewById(2131362029);
        if (i == 0) {
          break label158;
        }
        j = 0;
        label72:
        ((View)localObject).setVisibility(j);
        paramPreferenceViewHolder = (ImageView)paramPreferenceViewHolder.findViewById(16908314);
        if (i == 0) {
          break label164;
        }
        i = k;
        label96:
        paramPreferenceViewHolder.setVisibility(i);
        if (!this.mInfo.isActive) {
          break label169;
        }
      }
      label158:
      label164:
      label169:
      for (float f = 1.0F;; f = 0.4F)
      {
        paramPreferenceViewHolder.setAlpha(f);
        paramPreferenceViewHolder.setEnabled(this.mInfo.isActive);
        paramPreferenceViewHolder.setFocusable(this.mInfo.isActive);
        paramPreferenceViewHolder.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            DreamSettings.-get0(DreamSettings.this).launchSettings(DreamSettings.DreamInfoPreference.-get0(DreamSettings.DreamInfoPreference.this));
          }
        });
        return;
        i = 0;
        break;
        j = 4;
        break label72;
        i = 4;
        break label96;
      }
    }
    
    public void performClick()
    {
      if (this.mInfo.isActive) {
        return;
      }
      int i = 0;
      while (i < DreamSettings.this.getPreferenceScreen().getPreferenceCount())
      {
        DreamInfoPreference localDreamInfoPreference = (DreamInfoPreference)DreamSettings.this.getPreferenceScreen().getPreference(i);
        localDreamInfoPreference.mInfo.isActive = false;
        localDreamInfoPreference.notifyChanged();
        i += 1;
      }
      this.mInfo.isActive = true;
      DreamSettings.-get0(DreamSettings.this).setActiveDream(this.mInfo.componentName);
      notifyChanged();
    }
  }
  
  private class PackageReceiver
    extends BroadcastReceiver
  {
    private PackageReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      DreamSettings.-wrap0("PackageReceiver.onReceive", new Object[0]);
      DreamSettings.-wrap1(DreamSettings.this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\DreamSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */