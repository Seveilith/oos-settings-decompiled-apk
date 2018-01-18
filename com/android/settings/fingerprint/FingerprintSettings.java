package com.android.settings.fingerprint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.AuthenticationCallback;
import android.hardware.fingerprint.FingerprintManager.AuthenticationResult;
import android.hardware.fingerprint.FingerprintManager.RemovalCallback;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.Annotation;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.ChooseLockGeneric;
import com.android.settings.ChooseLockSettingsHelper;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SubSettings;
import com.android.settings.Utils;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import java.util.List;

public class FingerprintSettings
  extends SubSettings
{
  public static final String KEY_FINGERPRINT_SETTINGS = "fingerprint_settings";
  private static final long LOCKOUT_DURATION = 30000L;
  protected static final int RESULT_FINISHED = 1;
  protected static final int RESULT_SKIP = 2;
  protected static final int RESULT_TIMEOUT = 3;
  private static final String TAG = "FingerprintSettings";
  
  public static Preference getFingerprintPreferenceForUser(final Context paramContext, int paramInt)
  {
    Object localObject = (FingerprintManager)paramContext.getSystemService("fingerprint");
    Preference localPreference;
    int i;
    if ((localObject != null) && (((FingerprintManager)localObject).isHardwareDetected()))
    {
      localPreference = new Preference(paramContext);
      localPreference.setKey("fingerprint_settings");
      localPreference.setTitle(2131691065);
      localObject = ((FingerprintManager)localObject).getEnrolledFingerprints(paramInt);
      if (localObject == null) {
        break label126;
      }
      i = ((List)localObject).size();
      if (i <= 0) {
        break label131;
      }
      localPreference.setSummary(paramContext.getResources().getQuantityString(2131951620, i, new Object[] { Integer.valueOf(i) }));
    }
    for (paramContext = FingerprintSettings.class.getName();; paramContext = FingerprintEnrollIntroduction.class.getName())
    {
      localPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
      {
        public boolean onPreferenceClick(Preference paramAnonymousPreference)
        {
          paramAnonymousPreference = paramAnonymousPreference.getContext();
          if (Utils.startQuietModeDialogIfNecessary(paramAnonymousPreference, UserManager.get(paramAnonymousPreference), this.val$userId)) {
            return false;
          }
          Intent localIntent = new Intent();
          localIntent.setClassName("com.android.settings", paramContext);
          localIntent.putExtra("android.intent.extra.USER_ID", this.val$userId);
          paramAnonymousPreference.startActivity(localIntent);
          return true;
        }
      });
      return localPreference;
      Log.v("FingerprintSettings", "No fingerprint hardware detected!!");
      return null;
      label126:
      i = 0;
      break;
      label131:
      localPreference.setSummary(2131691070);
    }
  }
  
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":settings:show_fragment", FingerprintSettingsFragment.class.getName());
    return localIntent;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return FingerprintSettingsFragment.class.getName().equals(paramString);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setTitle(getText(2131691065));
  }
  
  public static class FingerprintPreference
    extends Preference
  {
    private Fingerprint mFingerprint;
    private View mView;
    
    public FingerprintPreference(Context paramContext)
    {
      super();
    }
    
    public FingerprintPreference(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public FingerprintPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      super(paramAttributeSet, paramInt);
    }
    
    public FingerprintPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
      super(paramAttributeSet, paramInt1, paramInt2);
    }
    
    public Fingerprint getFingerprint()
    {
      return this.mFingerprint;
    }
    
    public View getView()
    {
      return this.mView;
    }
    
    public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
    {
      super.onBindViewHolder(paramPreferenceViewHolder);
      this.mView = paramPreferenceViewHolder.itemView;
    }
    
    public void setFingerprint(Fingerprint paramFingerprint)
    {
      this.mFingerprint = paramFingerprint;
    }
  }
  
  public static class FingerprintSettingsFragment
    extends SettingsPreferenceFragment
    implements Preference.OnPreferenceChangeListener
  {
    private static final int ADD_FINGERPRINT_REQUEST = 10;
    private static final int CHOOSE_LOCK_GENERIC_REQUEST = 102;
    private static final int CONFIRM_REQUEST = 101;
    protected static final boolean DEBUG = true;
    private static final String KEY_FINGERPRINT_ADD = "key_fingerprint_add";
    private static final String KEY_FINGERPRINT_ENABLE_KEYGUARD_TOGGLE = "fingerprint_enable_keyguard_toggle";
    private static final String KEY_FINGERPRINT_ITEM_PREFIX = "key_fingerprint_item";
    private static final String KEY_LAUNCHED_CONFIRM = "launched_confirm";
    private static final int MAX_RETRY_ATTEMPTS = 20;
    private static final int MSG_FINGER_AUTH_ERROR = 1003;
    private static final int MSG_FINGER_AUTH_FAIL = 1002;
    private static final int MSG_FINGER_AUTH_HELP = 1004;
    private static final int MSG_FINGER_AUTH_SUCCESS = 1001;
    private static final int MSG_REFRESH_FINGERPRINT_TEMPLATES = 1000;
    private static final int RESET_HIGHLIGHT_DELAY_MS = 500;
    private static final String TAG = "FingerprintSettings";
    private FingerprintManager.AuthenticationCallback mAuthCallback = new FingerprintManager.AuthenticationCallback()
    {
      public void onAuthenticationError(int paramAnonymousInt, CharSequence paramAnonymousCharSequence)
      {
        FingerprintSettings.FingerprintSettingsFragment.-get1(FingerprintSettings.FingerprintSettingsFragment.this).obtainMessage(1003, paramAnonymousInt, 0, paramAnonymousCharSequence).sendToTarget();
      }
      
      public void onAuthenticationFailed()
      {
        FingerprintSettings.FingerprintSettingsFragment.-get1(FingerprintSettings.FingerprintSettingsFragment.this).obtainMessage(1002).sendToTarget();
      }
      
      public void onAuthenticationHelp(int paramAnonymousInt, CharSequence paramAnonymousCharSequence)
      {
        FingerprintSettings.FingerprintSettingsFragment.-get1(FingerprintSettings.FingerprintSettingsFragment.this).obtainMessage(1004, paramAnonymousInt, 0, paramAnonymousCharSequence).sendToTarget();
      }
      
      public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult paramAnonymousAuthenticationResult)
      {
        int i = paramAnonymousAuthenticationResult.getFingerprint().getFingerId();
        FingerprintSettings.FingerprintSettingsFragment.-get1(FingerprintSettings.FingerprintSettingsFragment.this).obtainMessage(1001, i, 0).sendToTarget();
      }
    };
    private CancellationSignal mFingerprintCancel;
    private final Runnable mFingerprintLockoutReset = new Runnable()
    {
      public void run()
      {
        FingerprintSettings.FingerprintSettingsFragment.-set1(FingerprintSettings.FingerprintSettingsFragment.this, false);
        FingerprintSettings.FingerprintSettingsFragment.-wrap3(FingerprintSettings.FingerprintSettingsFragment.this);
      }
    };
    private FingerprintManager mFingerprintManager;
    private final Handler mHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        switch (paramAnonymousMessage.what)
        {
        case 1002: 
        case 1004: 
        default: 
          return;
        case 1000: 
          FingerprintSettings.FingerprintSettingsFragment.this.removeFingerprintPreference(paramAnonymousMessage.arg1);
          FingerprintSettings.FingerprintSettingsFragment.-wrap4(FingerprintSettings.FingerprintSettingsFragment.this);
          FingerprintSettings.FingerprintSettingsFragment.-wrap3(FingerprintSettings.FingerprintSettingsFragment.this);
          return;
        case 1001: 
          FingerprintSettings.FingerprintSettingsFragment.-set0(FingerprintSettings.FingerprintSettingsFragment.this, null);
          FingerprintSettings.FingerprintSettingsFragment.-wrap1(FingerprintSettings.FingerprintSettingsFragment.this, paramAnonymousMessage.arg1);
          FingerprintSettings.FingerprintSettingsFragment.-wrap3(FingerprintSettings.FingerprintSettingsFragment.this);
          return;
        }
        FingerprintSettings.FingerprintSettingsFragment.this.handleError(paramAnonymousMessage.arg1, (CharSequence)paramAnonymousMessage.obj);
      }
    };
    private Drawable mHighlightDrawable;
    private boolean mInFingerprintLockout;
    private boolean mLaunchedConfirm;
    private FingerprintManager.RemovalCallback mRemoveCallback = new FingerprintManager.RemovalCallback()
    {
      public void onRemovalError(Fingerprint paramAnonymousFingerprint, int paramAnonymousInt, CharSequence paramAnonymousCharSequence)
      {
        paramAnonymousFingerprint = FingerprintSettings.FingerprintSettingsFragment.this.getActivity();
        if (paramAnonymousFingerprint != null) {
          Toast.makeText(paramAnonymousFingerprint, paramAnonymousCharSequence, 0);
        }
      }
      
      public void onRemovalSucceeded(Fingerprint paramAnonymousFingerprint)
      {
        FingerprintSettings.FingerprintSettingsFragment.-get1(FingerprintSettings.FingerprintSettingsFragment.this).obtainMessage(1000, paramAnonymousFingerprint.getFingerId(), 0).sendToTarget();
      }
    };
    private byte[] mToken;
    private int mUserId;
    
    private void addFingerprintItemPreferences(PreferenceGroup paramPreferenceGroup)
    {
      paramPreferenceGroup.removeAll();
      Object localObject = this.mFingerprintManager.getEnrolledFingerprints(this.mUserId);
      int j = ((List)localObject).size();
      int i = 0;
      while (i < j)
      {
        Fingerprint localFingerprint = (Fingerprint)((List)localObject).get(i);
        FingerprintSettings.FingerprintPreference localFingerprintPreference = new FingerprintSettings.FingerprintPreference(paramPreferenceGroup.getContext());
        localFingerprintPreference.setKey(genKey(localFingerprint.getFingerId()));
        localFingerprintPreference.setTitle(localFingerprint.getName());
        localFingerprintPreference.setFingerprint(localFingerprint);
        localFingerprintPreference.setPersistent(false);
        localFingerprintPreference.setIcon(2130837977);
        paramPreferenceGroup.addPreference(localFingerprintPreference);
        localFingerprintPreference.setOnPreferenceChangeListener(this);
        i += 1;
      }
      localObject = new Preference(paramPreferenceGroup.getContext());
      ((Preference)localObject).setKey("key_fingerprint_add");
      ((Preference)localObject).setTitle(2131691068);
      ((Preference)localObject).setIcon(2130837930);
      paramPreferenceGroup.addPreference((Preference)localObject);
      ((Preference)localObject).setOnPreferenceChangeListener(this);
      updateAddPreference();
    }
    
    private PreferenceScreen createPreferenceHierarchy()
    {
      PreferenceScreen localPreferenceScreen = getPreferenceScreen();
      if (localPreferenceScreen != null) {
        localPreferenceScreen.removeAll();
      }
      addPreferencesFromResource(2131230839);
      localPreferenceScreen = getPreferenceScreen();
      addFingerprintItemPreferences(localPreferenceScreen);
      setPreferenceScreen(localPreferenceScreen);
      return localPreferenceScreen;
    }
    
    private void deleteFingerPrint(Fingerprint paramFingerprint)
    {
      this.mFingerprintManager.remove(paramFingerprint, this.mUserId, this.mRemoveCallback);
    }
    
    private static String genKey(int paramInt)
    {
      return "key_fingerprint_item_" + paramInt;
    }
    
    private Drawable getHighlightDrawable()
    {
      if (this.mHighlightDrawable == null)
      {
        Activity localActivity = getActivity();
        if (localActivity != null) {
          this.mHighlightDrawable = localActivity.getDrawable(2130838311);
        }
      }
      return this.mHighlightDrawable;
    }
    
    private void highlightFingerprintItem(int paramInt)
    {
      final Object localObject = (FingerprintSettings.FingerprintPreference)findPreference(genKey(paramInt));
      Drawable localDrawable = getHighlightDrawable();
      if (localDrawable != null)
      {
        localObject = ((FingerprintSettings.FingerprintPreference)localObject).getView();
        paramInt = ((View)localObject).getWidth() / 2;
        int i = ((View)localObject).getHeight() / 2;
        localDrawable.setHotspot(paramInt, i);
        ((View)localObject).setBackground(localDrawable);
        ((View)localObject).setPressed(true);
        ((View)localObject).setPressed(false);
        this.mHandler.postDelayed(new Runnable()
        {
          public void run()
          {
            localObject.setBackground(null);
          }
        }, 500L);
      }
    }
    
    private void launchChooseOrConfirmLock()
    {
      Intent localIntent = new Intent();
      long l = this.mFingerprintManager.preEnroll();
      if (!new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(101, getString(2131691065), null, null, l, this.mUserId))
      {
        localIntent.setClassName("com.android.settings", ChooseLockGeneric.class.getName());
        localIntent.putExtra("minimum_quality", 65536);
        localIntent.putExtra("hide_disabled_prefs", true);
        localIntent.putExtra("has_challenge", true);
        localIntent.putExtra("android.intent.extra.USER_ID", this.mUserId);
        localIntent.putExtra("challenge", l);
        localIntent.putExtra("android.intent.extra.USER_ID", this.mUserId);
        startActivityForResult(localIntent, 102);
      }
    }
    
    private void renameFingerPrint(int paramInt, String paramString)
    {
      this.mFingerprintManager.rename(paramInt, this.mUserId, paramString);
      updatePreferences();
    }
    
    private void retryFingerprint()
    {
      if (!this.mInFingerprintLockout)
      {
        this.mFingerprintCancel = new CancellationSignal();
        this.mFingerprintManager.authenticate(null, this.mFingerprintCancel, 0, this.mAuthCallback, null, this.mUserId);
      }
    }
    
    private void showRenameDeleteDialog(Fingerprint paramFingerprint)
    {
      RenameDeleteDialog localRenameDeleteDialog = new RenameDeleteDialog();
      Bundle localBundle = new Bundle();
      localBundle.putParcelable("fingerprint", paramFingerprint);
      localRenameDeleteDialog.setArguments(localBundle);
      localRenameDeleteDialog.setTargetFragment(this, 0);
      localRenameDeleteDialog.show(getFragmentManager(), RenameDeleteDialog.class.getName());
    }
    
    private void stopFingerprint()
    {
      if ((this.mFingerprintCancel == null) || (this.mFingerprintCancel.isCanceled())) {}
      for (;;)
      {
        this.mFingerprintCancel = null;
        return;
        this.mFingerprintCancel.cancel();
      }
    }
    
    private void updateAddPreference()
    {
      boolean bool = false;
      int j = getContext().getResources().getInteger(17694881);
      int i;
      String str;
      label65:
      Preference localPreference;
      if (this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() >= j)
      {
        i = 1;
        if (i == 0) {
          break label96;
        }
        str = getContext().getString(2131691112, new Object[] { Integer.valueOf(j) });
        localPreference = findPreference("key_fingerprint_add");
        localPreference.setSummary(str);
        if (i == 0) {
          break label104;
        }
      }
      for (;;)
      {
        localPreference.setEnabled(bool);
        return;
        i = 0;
        break;
        label96:
        str = "";
        break label65;
        label104:
        bool = true;
      }
    }
    
    private void updatePreferences()
    {
      createPreferenceHierarchy();
      retryFingerprint();
    }
    
    protected int getHelpResource()
    {
      return 2131693030;
    }
    
    protected int getMetricsCategory()
    {
      return 49;
    }
    
    protected void handleError(int paramInt, CharSequence paramCharSequence)
    {
      this.mFingerprintCancel = null;
      switch (paramInt)
      {
      }
      for (;;)
      {
        Activity localActivity = getActivity();
        if (localActivity != null) {
          Toast.makeText(localActivity, paramCharSequence, 0);
        }
        retryFingerprint();
        return;
        return;
        this.mInFingerprintLockout = true;
        if (!this.mHandler.hasCallbacks(this.mFingerprintLockoutReset)) {
          this.mHandler.postDelayed(this.mFingerprintLockoutReset, 30000L);
        }
      }
    }
    
    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
      super.onActivityResult(paramInt1, paramInt2, paramIntent);
      if ((paramInt1 == 102) || (paramInt1 == 101)) {
        if (((paramInt2 == 1) || (paramInt2 == -1)) && (paramIntent != null)) {
          this.mToken = paramIntent.getByteArrayExtra("hw_auth_token");
        }
      }
      for (;;)
      {
        if (this.mToken == null) {
          getActivity().finish();
        }
        return;
        if ((paramInt1 == 10) && (paramInt2 == 3))
        {
          paramIntent = getActivity();
          paramIntent.setResult(3);
          paramIntent.finish();
        }
      }
    }
    
    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      if (paramBundle != null)
      {
        this.mToken = paramBundle.getByteArray("hw_auth_token");
        this.mLaunchedConfirm = paramBundle.getBoolean("launched_confirm", false);
      }
      this.mUserId = getActivity().getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
      this.mFingerprintManager = ((FingerprintManager)getActivity().getSystemService("fingerprint"));
      if ((this.mToken == null) && (!this.mLaunchedConfirm))
      {
        this.mLaunchedConfirm = true;
        launchChooseOrConfirmLock();
      }
    }
    
    public void onDestroy()
    {
      super.onDestroy();
      if (getActivity().isFinishing())
      {
        int i = this.mFingerprintManager.postEnroll();
        if (i < 0) {
          Log.w("FingerprintSettings", "postEnroll failed: result = " + i);
        }
      }
    }
    
    public void onPause()
    {
      super.onPause();
      stopFingerprint();
    }
    
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
    {
      paramPreference = paramPreference.getKey();
      if ("fingerprint_enable_keyguard_toggle".equals(paramPreference)) {
        return true;
      }
      Log.v("FingerprintSettings", "Unknown key:" + paramPreference);
      return true;
    }
    
    public boolean onPreferenceTreeClick(Preference paramPreference)
    {
      if ("key_fingerprint_add".equals(paramPreference.getKey()))
      {
        paramPreference = new Intent();
        paramPreference.setClassName("com.android.settings", FingerprintEnrollEnrolling.class.getName());
        paramPreference.putExtra("android.intent.extra.USER_ID", this.mUserId);
        paramPreference.putExtra("hw_auth_token", this.mToken);
        startActivityForResult(paramPreference, 10);
      }
      while (!(paramPreference instanceof FingerprintSettings.FingerprintPreference)) {
        return true;
      }
      showRenameDeleteDialog(((FingerprintSettings.FingerprintPreference)paramPreference).getFingerprint());
      return super.onPreferenceTreeClick(paramPreference);
    }
    
    public void onResume()
    {
      super.onResume();
      updatePreferences();
    }
    
    public void onSaveInstanceState(Bundle paramBundle)
    {
      paramBundle.putByteArray("hw_auth_token", this.mToken);
      paramBundle.putBoolean("launched_confirm", this.mLaunchedConfirm);
    }
    
    public void onViewCreated(View paramView, Bundle paramBundle)
    {
      super.onViewCreated(paramView, paramBundle);
      paramView = (TextView)LayoutInflater.from(paramView.getContext()).inflate(2130968710, null);
      paramBundle = RestrictedLockUtils.checkIfKeyguardFeaturesDisabled(getActivity(), 32, this.mUserId);
      if (paramBundle != null) {}
      for (int i = 2131691110;; i = 2131691109)
      {
        paramView.setText(FingerprintSettings.LearnMoreSpan.linkify(getText(i), getString(getHelpResource()), paramBundle));
        paramView.setMovementMethod(new LinkMovementMethod());
        setFooterView(paramView);
        return;
      }
    }
    
    protected void removeFingerprintPreference(int paramInt)
    {
      String str = genKey(paramInt);
      Preference localPreference = findPreference(str);
      if (localPreference != null)
      {
        if (!getPreferenceScreen().removePreference(localPreference)) {
          Log.w("FingerprintSettings", "Failed to remove preference with key " + str);
        }
        return;
      }
      Log.w("FingerprintSettings", "Can't find preference to remove: " + str);
    }
    
    public static class ConfirmLastDeleteDialog
      extends DialogFragment
    {
      private Fingerprint mFp;
      
      public Dialog onCreateDialog(Bundle paramBundle)
      {
        this.mFp = ((Fingerprint)getArguments().getParcelable("fingerprint"));
        boolean bool = getArguments().getBoolean("isProfileChallengeUser");
        paramBundle = new AlertDialog.Builder(getActivity()).setTitle(2131691113);
        if (bool) {}
        for (int i = 2131691115;; i = 2131691114) {
          paramBundle.setMessage(i).setPositiveButton(2131691116, new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              FingerprintSettings.FingerprintSettingsFragment.-wrap0((FingerprintSettings.FingerprintSettingsFragment)FingerprintSettings.FingerprintSettingsFragment.ConfirmLastDeleteDialog.this.getTargetFragment(), FingerprintSettings.FingerprintSettingsFragment.ConfirmLastDeleteDialog.-get0(FingerprintSettings.FingerprintSettingsFragment.ConfirmLastDeleteDialog.this));
              paramAnonymousDialogInterface.dismiss();
            }
          }).setNegativeButton(2131690993, new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              paramAnonymousDialogInterface.dismiss();
            }
          }).create();
        }
      }
    }
    
    public static class RenameDeleteDialog
      extends DialogFragment
    {
      private EditText mDialogTextField;
      private String mFingerName;
      private Fingerprint mFp;
      private Boolean mTextHadFocus;
      private int mTextSelectionEnd;
      private int mTextSelectionStart;
      
      private void onDeleteClick(DialogInterface paramDialogInterface)
      {
        Log.v("FingerprintSettings", "Removing fpId=" + this.mFp.getFingerId());
        MetricsLogger.action(getContext(), 253, this.mFp.getFingerId());
        Object localObject = (FingerprintSettings.FingerprintSettingsFragment)getTargetFragment();
        boolean bool = Utils.isManagedProfile(UserManager.get(getContext()), FingerprintSettings.FingerprintSettingsFragment.-get2((FingerprintSettings.FingerprintSettingsFragment)localObject));
        if (FingerprintSettings.FingerprintSettingsFragment.-get0((FingerprintSettings.FingerprintSettingsFragment)localObject).getEnrolledFingerprints(FingerprintSettings.FingerprintSettingsFragment.-get2((FingerprintSettings.FingerprintSettingsFragment)localObject)).size() > 1) {
          FingerprintSettings.FingerprintSettingsFragment.-wrap0((FingerprintSettings.FingerprintSettingsFragment)localObject, this.mFp);
        }
        for (;;)
        {
          paramDialogInterface.dismiss();
          return;
          localObject = new FingerprintSettings.FingerprintSettingsFragment.ConfirmLastDeleteDialog();
          Bundle localBundle = new Bundle();
          localBundle.putParcelable("fingerprint", this.mFp);
          localBundle.putBoolean("isProfileChallengeUser", bool);
          ((FingerprintSettings.FingerprintSettingsFragment.ConfirmLastDeleteDialog)localObject).setArguments(localBundle);
          ((FingerprintSettings.FingerprintSettingsFragment.ConfirmLastDeleteDialog)localObject).setTargetFragment(getTargetFragment(), 0);
          ((FingerprintSettings.FingerprintSettingsFragment.ConfirmLastDeleteDialog)localObject).show(getFragmentManager(), FingerprintSettings.FingerprintSettingsFragment.ConfirmLastDeleteDialog.class.getName());
        }
      }
      
      public Dialog onCreateDialog(final Bundle paramBundle)
      {
        this.mFp = ((Fingerprint)getArguments().getParcelable("fingerprint"));
        if (paramBundle != null)
        {
          this.mFingerName = paramBundle.getString("fingerName");
          this.mTextHadFocus = Boolean.valueOf(paramBundle.getBoolean("textHadFocus"));
          this.mTextSelectionStart = paramBundle.getInt("startSelection");
          this.mTextSelectionEnd = paramBundle.getInt("endSelection");
        }
        paramBundle = new AlertDialog.Builder(getActivity()).setView(2130968709).setPositiveButton(2131691088, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            String str = FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-get0(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this).getText().toString();
            CharSequence localCharSequence = FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-get2(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this).getName();
            if (!str.equals(localCharSequence))
            {
              Log.v("FingerprintSettings", "rename " + localCharSequence + " to " + str);
              MetricsLogger.action(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this.getContext(), 254, FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-get2(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this).getFingerId());
              FingerprintSettings.FingerprintSettingsFragment.-wrap2((FingerprintSettings.FingerprintSettingsFragment)FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this.getTargetFragment(), FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-get2(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this).getFingerId(), str);
            }
            paramAnonymousDialogInterface.dismiss();
          }
        }).setNegativeButton(2131691089, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-wrap0(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this, paramAnonymousDialogInterface);
          }
        }).create();
        paramBundle.setOnShowListener(new DialogInterface.OnShowListener()
        {
          public void onShow(DialogInterface paramAnonymousDialogInterface)
          {
            FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-set0(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this, (EditText)paramBundle.findViewById(2131362154));
            if (FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-get1(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this) == null) {}
            for (paramAnonymousDialogInterface = FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-get2(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this).getName();; paramAnonymousDialogInterface = FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-get1(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this))
            {
              FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-get0(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this).setText(paramAnonymousDialogInterface);
              if (FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-get3(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this) != null) {
                break;
              }
              FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-get0(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this).selectAll();
              return;
            }
            FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-get0(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this).setSelection(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-get5(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this), FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.-get4(FingerprintSettings.FingerprintSettingsFragment.RenameDeleteDialog.this));
          }
        });
        if ((this.mTextHadFocus == null) || (this.mTextHadFocus.booleanValue())) {
          paramBundle.getWindow().setSoftInputMode(5);
        }
        return paramBundle;
      }
      
      public void onSaveInstanceState(Bundle paramBundle)
      {
        super.onSaveInstanceState(paramBundle);
        if (this.mDialogTextField != null)
        {
          paramBundle.putString("fingerName", this.mDialogTextField.getText().toString());
          paramBundle.putBoolean("textHadFocus", this.mDialogTextField.hasFocus());
          paramBundle.putInt("startSelection", this.mDialogTextField.getSelectionStart());
          paramBundle.putInt("endSelection", this.mDialogTextField.getSelectionEnd());
        }
      }
    }
  }
  
  private static class LearnMoreSpan
    extends URLSpan
  {
    private static final String ANNOTATION_ADMIN_DETAILS = "admin_details";
    private static final String ANNOTATION_URL = "url";
    private static final Typeface TYPEFACE_MEDIUM = Typeface.create("sans-serif-medium", 0);
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin = null;
    
    private LearnMoreSpan(RestrictedLockUtils.EnforcedAdmin paramEnforcedAdmin)
    {
      super();
      this.mEnforcedAdmin = paramEnforcedAdmin;
    }
    
    private LearnMoreSpan(String paramString)
    {
      super();
    }
    
    public static CharSequence linkify(CharSequence paramCharSequence, String paramString, RestrictedLockUtils.EnforcedAdmin paramEnforcedAdmin)
    {
      int i = 0;
      SpannableString localSpannableString = new SpannableString(paramCharSequence);
      Annotation[] arrayOfAnnotation = (Annotation[])localSpannableString.getSpans(0, localSpannableString.length(), Annotation.class);
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(localSpannableString);
      int j = arrayOfAnnotation.length;
      if (i < j)
      {
        paramCharSequence = arrayOfAnnotation[i];
        String str = paramCharSequence.getValue();
        int k = localSpannableString.getSpanStart(paramCharSequence);
        int m = localSpannableString.getSpanEnd(paramCharSequence);
        paramCharSequence = null;
        if ("url".equals(str)) {
          paramCharSequence = new LearnMoreSpan(paramString);
        }
        for (;;)
        {
          if (paramCharSequence != null) {
            localSpannableStringBuilder.setSpan(paramCharSequence, k, m, localSpannableString.getSpanFlags(paramCharSequence));
          }
          i += 1;
          break;
          if ("admin_details".equals(str)) {
            paramCharSequence = new LearnMoreSpan(paramEnforcedAdmin);
          }
        }
      }
      return localSpannableStringBuilder;
    }
    
    public void onClick(View paramView)
    {
      Object localObject = paramView.getContext();
      if (this.mEnforcedAdmin != null)
      {
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent((Context)localObject, this.mEnforcedAdmin);
        return;
      }
      localObject = HelpUtils.getHelpIntent((Context)localObject, getURL(), ((Context)localObject).getClass().getName());
      try
      {
        paramView.startActivityForResult((Intent)localObject, 0);
        return;
      }
      catch (ActivityNotFoundException paramView)
      {
        Log.w("FingerprintSettings", "Actvity was not found for intent, " + ((Intent)localObject).toString());
      }
    }
    
    public void updateDrawState(TextPaint paramTextPaint)
    {
      super.updateDrawState(paramTextPaint);
      paramTextPaint.setUnderlineText(false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fingerprint\FingerprintSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */