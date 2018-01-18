package com.android.settings.datausage;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.NetworkPolicy;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.text.Editable;
import android.text.format.Formatter;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import com.android.settingslib.NetworkPolicyEditor;
import com.android.settingslib.net.DataUsageController;

public class BillingCycleSettings
  extends DataUsageBase
  implements Preference.OnPreferenceChangeListener, DataUsageEditController
{
  private static final String KEY_BILLING_CYCLE = "billing_cycle";
  private static final String KEY_DATA_LIMIT = "data_limit";
  private static final String KEY_DATA_WARNING = "data_warning";
  private static final String KEY_SET_DATA_LIMIT = "set_data_limit";
  private static final String KEY_SET_DATA_TIME_RANGE = "set_data_time_range";
  private static final String KEY_SET_DATA_WARNING = "set_data_warning";
  private static final boolean LOGD = false;
  public static final String PREF_FILE = "data_usage";
  public static final String PREF_SHOW_DATA_USAGE = "show_data_usage";
  private static final String TAG = "BillingCycleSettings";
  private static final String TAG_CONFIRM_LIMIT = "confirmLimit";
  private static final String TAG_CYCLE_EDITOR = "cycleEditor";
  private static final String TAG_WARNING_EDITOR = "warningEditor";
  private Preference mBillingCycle;
  private Preference mDataLimit;
  private DataUsageController mDataUsageController;
  private Preference mDataWarning;
  private SwitchPreference mEnableDataLimit;
  private SwitchPreference mEnableDataTimeRange;
  private SwitchPreference mEnableDataWarning;
  private NetworkTemplate mNetworkTemplate;
  private boolean mShowDataUsage = false;
  
  public static boolean isDataSelectionEnable(Context paramContext)
  {
    return paramContext.getResources().getBoolean(2131558433);
  }
  
  public static boolean isShowDataUsage(Context paramContext)
  {
    return paramContext.getSharedPreferences("data_usage", 0).getBoolean("show_data_usage", false);
  }
  
  private void setPolicyLimitBytes(long paramLong)
  {
    this.services.mPolicyEditor.setPolicyLimitBytes(this.mNetworkTemplate, paramLong);
    updatePrefs();
  }
  
  private void setPolicyWarningBytes(long paramLong)
  {
    this.services.mPolicyEditor.setPolicyWarningBytes(this.mNetworkTemplate, paramLong);
    updatePrefs();
  }
  
  private void updatePrefs()
  {
    NetworkPolicy localNetworkPolicy = this.services.mPolicyEditor.getPolicy(this.mNetworkTemplate);
    Preference localPreference = this.mBillingCycle;
    int i;
    if (localNetworkPolicy != null)
    {
      i = localNetworkPolicy.cycleDay;
      localPreference.setSummary(getString(2131693637, new Object[] { Integer.valueOf(i) }));
      if ((localNetworkPolicy == null) || (localNetworkPolicy.warningBytes == -1L)) {
        break label165;
      }
      this.mDataWarning.setSummary(Formatter.formatFileSize(getContext(), localNetworkPolicy.warningBytes));
      this.mDataWarning.setEnabled(true);
      this.mEnableDataWarning.setChecked(true);
      label99:
      if ((localNetworkPolicy == null) || (localNetworkPolicy.limitBytes == -1L)) {
        break label192;
      }
      this.mDataLimit.setSummary(Formatter.formatFileSize(getContext(), localNetworkPolicy.limitBytes));
      this.mDataLimit.setEnabled(true);
      this.mEnableDataLimit.setChecked(true);
    }
    for (;;)
    {
      this.mEnableDataTimeRange.setChecked(this.mShowDataUsage);
      return;
      i = 1;
      break;
      label165:
      this.mDataWarning.setSummary(null);
      this.mDataWarning.setEnabled(false);
      this.mEnableDataWarning.setChecked(false);
      break label99;
      label192:
      this.mDataLimit.setSummary(null);
      this.mDataLimit.setEnabled(false);
      this.mEnableDataLimit.setChecked(false);
    }
  }
  
  protected int getMetricsCategory()
  {
    return 342;
  }
  
  public NetworkPolicyEditor getNetworkPolicyEditor()
  {
    return this.services.mPolicyEditor;
  }
  
  public NetworkTemplate getNetworkTemplate()
  {
    return this.mNetworkTemplate;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mDataUsageController = new DataUsageController(getContext());
    this.mNetworkTemplate = ((NetworkTemplate)getArguments().getParcelable("network_template"));
    addPreferencesFromResource(2131230742);
    this.mBillingCycle = findPreference("billing_cycle");
    this.mEnableDataWarning = ((SwitchPreference)findPreference("set_data_warning"));
    this.mEnableDataWarning.setOnPreferenceChangeListener(this);
    this.mDataWarning = findPreference("data_warning");
    this.mEnableDataLimit = ((SwitchPreference)findPreference("set_data_limit"));
    this.mEnableDataLimit.setOnPreferenceChangeListener(this);
    this.mDataLimit = findPreference("data_limit");
    this.mEnableDataTimeRange = ((SwitchPreference)findPreference("set_data_time_range"));
    this.mEnableDataTimeRange.setOnPreferenceChangeListener(this);
    paramBundle = getActivity();
    this.mShowDataUsage = isShowDataUsage(paramBundle);
    this.mEnableDataTimeRange.setVisible(isDataSelectionEnable(paramBundle));
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (this.mEnableDataLimit == paramPreference)
    {
      if (((Boolean)paramObject).booleanValue())
      {
        ConfirmLimitFragment.show(this);
        return true;
      }
      setPolicyLimitBytes(-1L);
      return true;
    }
    if (this.mEnableDataTimeRange == paramPreference)
    {
      ((Boolean)paramObject).booleanValue();
      if (this.mShowDataUsage) {}
      for (boolean bool = false;; bool = true)
      {
        this.mShowDataUsage = bool;
        getActivity().getSharedPreferences("data_usage", 0).edit().putBoolean("show_data_usage", this.mShowDataUsage).apply();
        return true;
      }
    }
    if (this.mEnableDataWarning == paramPreference)
    {
      if (((Boolean)paramObject).booleanValue())
      {
        setPolicyWarningBytes(this.mDataUsageController.getDefaultWarningLevel());
        return true;
      }
      setPolicyWarningBytes(-1L);
      return true;
    }
    return false;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if (paramPreference == this.mBillingCycle)
    {
      CycleEditorFragment.show(this);
      return true;
    }
    if (paramPreference == this.mDataWarning)
    {
      BytesEditorFragment.show(this, false);
      return true;
    }
    if (paramPreference == this.mDataLimit)
    {
      BytesEditorFragment.show(this, true);
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onResume()
  {
    super.onResume();
    updatePrefs();
  }
  
  public void updateDataUsage()
  {
    updatePrefs();
  }
  
  public static class BytesEditorFragment
    extends DialogFragment
    implements DialogInterface.OnClickListener
  {
    private static final String EXTRA_LIMIT = "limit";
    private static final String EXTRA_TEMPLATE = "template";
    private View mView;
    
    private String formatText(float paramFloat)
    {
      return String.valueOf(Math.round(paramFloat * 100.0F) / 100.0F);
    }
    
    private void setupPicker(EditText paramEditText, Spinner paramSpinner)
    {
      Object localObject = ((DataUsageEditController)getTargetFragment()).getNetworkPolicyEditor();
      NetworkTemplate localNetworkTemplate = (NetworkTemplate)getArguments().getParcelable("template");
      boolean bool = getArguments().getBoolean("limit");
      if (bool) {}
      for (long l = ((NetworkPolicyEditor)localObject).getPolicyLimitBytes(localNetworkTemplate); (!bool) || ((float)l > 1.61061274E9F); l = ((NetworkPolicyEditor)localObject).getPolicyWarningBytes(localNetworkTemplate))
      {
        localObject = formatText((float)l / 1.07374182E9F);
        paramEditText.setText((CharSequence)localObject);
        paramEditText.setSelection(0, ((String)localObject).length());
        paramSpinner.setSelection(1);
        return;
      }
      localObject = formatText((float)l / 1048576.0F);
      paramEditText.setText((CharSequence)localObject);
      paramEditText.setSelection(0, ((String)localObject).length());
      paramSpinner.setSelection(0);
    }
    
    public static void show(DataUsageEditController paramDataUsageEditController, boolean paramBoolean)
    {
      if (!(paramDataUsageEditController instanceof Fragment)) {
        return;
      }
      Fragment localFragment = (Fragment)paramDataUsageEditController;
      if (!localFragment.isAdded()) {
        return;
      }
      Bundle localBundle = new Bundle();
      localBundle.putParcelable("template", paramDataUsageEditController.getNetworkTemplate());
      localBundle.putBoolean("limit", paramBoolean);
      paramDataUsageEditController = new BytesEditorFragment();
      paramDataUsageEditController.setArguments(localBundle);
      paramDataUsageEditController.setTargetFragment(localFragment, 0);
      paramDataUsageEditController.show(localFragment.getFragmentManager(), "warningEditor");
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt != -1) {
        return;
      }
      DataUsageEditController localDataUsageEditController = (DataUsageEditController)getTargetFragment();
      NetworkPolicyEditor localNetworkPolicyEditor = localDataUsageEditController.getNetworkPolicyEditor();
      NetworkTemplate localNetworkTemplate = (NetworkTemplate)getArguments().getParcelable("template");
      boolean bool = getArguments().getBoolean("limit");
      paramDialogInterface = (EditText)this.mView.findViewById(2131362072);
      Spinner localSpinner = (Spinner)this.mView.findViewById(2131362073);
      String str = paramDialogInterface.getText().toString();
      paramDialogInterface = str;
      if (str.isEmpty()) {
        paramDialogInterface = "0";
      }
      float f = Float.valueOf(paramDialogInterface).floatValue();
      long l;
      if (localSpinner.getSelectedItemPosition() == 0)
      {
        l = 1048576L;
        l = (f * (float)l);
        if (!bool) {
          break label160;
        }
        localNetworkPolicyEditor.setPolicyLimitBytes(localNetworkTemplate, l);
      }
      for (;;)
      {
        localDataUsageEditController.updateDataUsage();
        return;
        l = 1073741824L;
        break;
        label160:
        localNetworkPolicyEditor.setPolicyWarningBytes(localNetworkTemplate, l);
      }
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      paramBundle = getActivity();
      LayoutInflater localLayoutInflater = LayoutInflater.from(paramBundle);
      boolean bool = getArguments().getBoolean("limit");
      this.mView = localLayoutInflater.inflate(2130968671, null, false);
      setupPicker((EditText)this.mView.findViewById(2131362072), (Spinner)this.mView.findViewById(2131362073));
      paramBundle = new AlertDialog.Builder(paramBundle);
      if (bool) {}
      for (int i = 2131692815;; i = 2131692814) {
        return paramBundle.setTitle(i).setView(this.mView).setPositiveButton(2131692813, this).create();
      }
    }
  }
  
  public static class ConfirmLimitFragment
    extends DialogFragment
    implements DialogInterface.OnClickListener
  {
    private static final String EXTRA_LIMIT_BYTES = "limitBytes";
    private static final String EXTRA_MESSAGE = "message";
    public static final float FLOAT = 1.2F;
    
    public static void show(BillingCycleSettings paramBillingCycleSettings)
    {
      if (!paramBillingCycleSettings.isAdded()) {
        return;
      }
      Object localObject1 = paramBillingCycleSettings.services.mPolicyEditor.getPolicy(BillingCycleSettings.-get0(paramBillingCycleSettings));
      if (localObject1 == null) {
        return;
      }
      Object localObject2 = paramBillingCycleSettings.getResources();
      long l = ((float)((NetworkPolicy)localObject1).warningBytes * 1.2F);
      localObject2 = ((Resources)localObject2).getString(2131692817);
      l = Math.max(5368709120L, l);
      localObject1 = new Bundle();
      ((Bundle)localObject1).putCharSequence("message", (CharSequence)localObject2);
      ((Bundle)localObject1).putLong("limitBytes", l);
      localObject2 = new ConfirmLimitFragment();
      ((ConfirmLimitFragment)localObject2).setArguments((Bundle)localObject1);
      ((ConfirmLimitFragment)localObject2).setTargetFragment(paramBillingCycleSettings, 0);
      ((ConfirmLimitFragment)localObject2).show(paramBillingCycleSettings.getFragmentManager(), "confirmLimit");
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt != -1) {
        return;
      }
      long l = getArguments().getLong("limitBytes");
      paramDialogInterface = (BillingCycleSettings)getTargetFragment();
      if (paramDialogInterface != null) {
        BillingCycleSettings.-wrap0(paramDialogInterface, l);
      }
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      paramBundle = getActivity();
      CharSequence localCharSequence = getArguments().getCharSequence("message");
      return new AlertDialog.Builder(paramBundle).setTitle(2131692816).setMessage(localCharSequence).setPositiveButton(17039370, this).setNegativeButton(17039360, null).create();
    }
  }
  
  public static class CycleEditorFragment
    extends DialogFragment
    implements DialogInterface.OnClickListener
  {
    private static final String EXTRA_TEMPLATE = "template";
    private NumberPicker mCycleDayPicker;
    
    public static void show(BillingCycleSettings paramBillingCycleSettings)
    {
      if (!paramBillingCycleSettings.isAdded()) {
        return;
      }
      Bundle localBundle = new Bundle();
      localBundle.putParcelable("template", BillingCycleSettings.-get0(paramBillingCycleSettings));
      CycleEditorFragment localCycleEditorFragment = new CycleEditorFragment();
      localCycleEditorFragment.setArguments(localBundle);
      localCycleEditorFragment.setTargetFragment(paramBillingCycleSettings, 0);
      localCycleEditorFragment.show(paramBillingCycleSettings.getFragmentManager(), "cycleEditor");
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      paramDialogInterface = (NetworkTemplate)getArguments().getParcelable("template");
      DataUsageEditController localDataUsageEditController = (DataUsageEditController)getTargetFragment();
      NetworkPolicyEditor localNetworkPolicyEditor = localDataUsageEditController.getNetworkPolicyEditor();
      this.mCycleDayPicker.clearFocus();
      localNetworkPolicyEditor.setPolicyCycleDay(paramDialogInterface, this.mCycleDayPicker.getValue(), new Time().timezone);
      localDataUsageEditController.updateDataUsage();
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      Object localObject = getActivity();
      paramBundle = ((DataUsageEditController)getTargetFragment()).getNetworkPolicyEditor();
      localObject = new AlertDialog.Builder((Context)localObject);
      View localView = LayoutInflater.from(((AlertDialog.Builder)localObject).getContext()).inflate(2130968673, null, false);
      this.mCycleDayPicker = ((NumberPicker)localView.findViewById(2131362082));
      int i = paramBundle.getPolicyCycleDay((NetworkTemplate)getArguments().getParcelable("template"));
      this.mCycleDayPicker.setMinValue(1);
      this.mCycleDayPicker.setMaxValue(31);
      this.mCycleDayPicker.setValue(i);
      this.mCycleDayPicker.setWrapSelectorWheel(true);
      return ((AlertDialog.Builder)localObject).setTitle(2131692811).setView(localView).setPositiveButton(2131692813, this).create();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\BillingCycleSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */