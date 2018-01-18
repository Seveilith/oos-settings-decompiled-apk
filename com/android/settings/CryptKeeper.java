package com.android.settings;

import android.app.Activity;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.storage.IMountService;
import android.os.storage.IMountService.Stub;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockPatternView.Cell;
import com.android.internal.widget.LockPatternView.DisplayMode;
import com.android.internal.widget.LockPatternView.OnPatternListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CryptKeeper
  extends Activity
  implements TextView.OnEditorActionListener, View.OnKeyListener, View.OnTouchListener, TextWatcher
{
  static final String ACTION_EMERGENCY_DIAL = "com.android.phone.EmergencyDialer.DIAL";
  private static final int COOL_DOWN_ATTEMPTS = 10;
  private static final String DECRYPT_STATE = "trigger_restart_framework";
  private static final String EXTRA_FORCE_VIEW = "com.android.settings.CryptKeeper.DEBUG_FORCE_VIEW";
  private static final int FAKE_ATTEMPT_DELAY = 1000;
  private static final String FORCE_VIEW_ERROR = "error";
  private static final String FORCE_VIEW_PASSWORD = "password";
  private static final String FORCE_VIEW_PROGRESS = "progress";
  private static final int MAX_FAILED_ATTEMPTS = 30;
  private static final int MESSAGE_CHECK_IMEI_READY = 3;
  private static final int MESSAGE_NOTIFY = 2;
  private static final int MESSAGE_UPDATE_PROGRESS = 1;
  protected static final int MIN_LENGTH_BEFORE_REPORT = 4;
  private static final int RIGHT_PATTERN_CLEAR_TIMEOUT_MS = 500;
  private static final String STATE_COOLDOWN = "cooldown";
  private static final String TAG = "CryptKeeper";
  private static final String VALID_IMEI_FOR_US_VERSION = "01483600";
  private static final int WRONG_PATTERN_CLEAR_TIMEOUT_MS = 1500;
  private static final int sWidgetsToDisable = 52887552;
  private AudioManager mAudioManager;
  protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener()
  {
    public void onPatternCellAdded(List<LockPatternView.Cell> paramAnonymousList) {}
    
    public void onPatternCleared() {}
    
    public void onPatternDetected(List<LockPatternView.Cell> paramAnonymousList)
    {
      CryptKeeper.-get2(CryptKeeper.this).setEnabled(false);
      if (paramAnonymousList.size() >= 4)
      {
        new CryptKeeper.DecryptTask(CryptKeeper.this, null).execute(new String[] { LockPatternUtils.patternToString(paramAnonymousList) });
        return;
      }
      CryptKeeper.-wrap3(CryptKeeper.this, CryptKeeper.-get2(CryptKeeper.this));
    }
    
    public void onPatternStart()
    {
      CryptKeeper.-get2(CryptKeeper.this).removeCallbacks(CryptKeeper.-get0(CryptKeeper.this));
    }
  };
  private final Runnable mClearPatternRunnable = new Runnable()
  {
    public void run()
    {
      CryptKeeper.-get2(CryptKeeper.this).clearPattern();
    }
  };
  private boolean mCooldown = false;
  private boolean mCorrupt;
  private boolean mEncryptionGoneBad;
  private final Runnable mFakeUnlockAttemptRunnable = new Runnable()
  {
    public void run()
    {
      CryptKeeper.-wrap4(CryptKeeper.this, Integer.valueOf(1));
    }
  };
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        CryptKeeper.-wrap12(CryptKeeper.this);
        return;
      case 2: 
        CryptKeeper.-wrap5(CryptKeeper.this);
        return;
      }
      CryptKeeper.-wrap11(CryptKeeper.this);
    }
  };
  private boolean mImeiReady = false;
  private boolean mImeiValid = false;
  private LockPatternView mLockPatternView;
  private boolean mMdtpActivated;
  private int mNotificationCountdown = 0;
  private EditText mPasswordEntry;
  private int mReleaseWakeLockCountdown = 0;
  private StatusBarManager mStatusBar;
  private int mStatusString = 2131692739;
  private boolean mValidationComplete;
  private boolean mValidationRequested;
  PowerManager.WakeLock mWakeLock;
  
  private void beginAttempt()
  {
    ((TextView)findViewById(2131362057)).setText(2131692745);
  }
  
  private void checkImei()
  {
    String str = SystemProperties.get("oem.device.imeicache");
    if (TextUtils.isEmpty(str))
    {
      this.mImeiReady = false;
      this.mImeiValid = false;
    }
    for (;;)
    {
      Log.d("CryptKeeper", "checkImei imei=" + str + " mImeiReady=" + this.mImeiReady + " mImeiValid=" + this.mImeiValid);
      return;
      if ((str.length() >= 15) && ((!str.startsWith("0")) || (str.startsWith("01483600"))))
      {
        this.mImeiReady = true;
        this.mImeiValid = true;
      }
      else
      {
        this.mImeiReady = true;
        this.mImeiValid = false;
      }
    }
  }
  
  private void cooldown()
  {
    if (this.mPasswordEntry != null) {
      this.mPasswordEntry.setEnabled(false);
    }
    if (this.mLockPatternView != null) {
      this.mLockPatternView.setEnabled(false);
    }
    ((TextView)findViewById(2131362057)).setText(2131691132);
  }
  
  private void delayAudioNotification()
  {
    this.mNotificationCountdown = 20;
  }
  
  private static void disableCryptKeeperComponent(Context paramContext)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    paramContext = new ComponentName(paramContext, CryptKeeper.class);
    Log.d("CryptKeeper", "Disabling component " + paramContext);
    localPackageManager.setComponentEnabledSetting(paramContext, 2, 1);
  }
  
  private void encryptionProgressInit()
  {
    Log.d("CryptKeeper", "Encryption progress screen initializing.");
    if (this.mWakeLock == null)
    {
      Log.d("CryptKeeper", "Acquiring wakelock.");
      this.mWakeLock = ((PowerManager)getSystemService("power")).newWakeLock(26, "CryptKeeper");
      this.mWakeLock.acquire();
    }
    ((ProgressBar)findViewById(2131362056)).setIndeterminate(true);
    setBackFunctionality(false);
    updateProgress();
  }
  
  private void fakeUnlockAttempt(View paramView)
  {
    beginAttempt();
    paramView.postDelayed(this.mFakeUnlockAttemptRunnable, 1000L);
  }
  
  private IMountService getMountService()
  {
    IBinder localIBinder = ServiceManager.getService("mount");
    if (localIBinder != null) {
      return IMountService.Stub.asInterface(localIBinder);
    }
    return null;
  }
  
  private TelecomManager getTelecomManager()
  {
    return (TelecomManager)getSystemService("telecom");
  }
  
  private TelephonyManager getTelephonyManager()
  {
    return (TelephonyManager)getSystemService("phone");
  }
  
  private void handleBadAttempt(Integer paramInteger)
  {
    if (this.mLockPatternView != null)
    {
      this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
      this.mLockPatternView.removeCallbacks(this.mClearPatternRunnable);
      this.mLockPatternView.postDelayed(this.mClearPatternRunnable, 1500L);
    }
    if (paramInteger.intValue() % 10 == 0)
    {
      this.mCooldown = true;
      cooldown();
      return;
    }
    TextView localTextView = (TextView)findViewById(2131362057);
    int i = 30 - paramInteger.intValue();
    if (i < 10) {
      localTextView.setText(TextUtils.expandTemplate(getText(2131691133), new CharSequence[] { Integer.toString(i) }));
    }
    for (;;)
    {
      if (this.mLockPatternView != null)
      {
        this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
        this.mLockPatternView.setEnabled(true);
      }
      if (this.mPasswordEntry == null) {
        break;
      }
      this.mPasswordEntry.setEnabled(true);
      ((InputMethodManager)getSystemService("input_method")).showSoftInput(this.mPasswordEntry, 0);
      setBackFunctionality(true);
      return;
      i = 0;
      try
      {
        int j = getMountService().getPasswordType();
        i = j;
        if (i == 3) {
          localTextView.setText(2131692744);
        }
      }
      catch (Exception paramInteger)
      {
        for (;;)
        {
          Log.e("CryptKeeper", "Error calling mount service " + paramInteger);
        }
        if (i == 2) {
          localTextView.setText(2131692742);
        } else {
          localTextView.setText(2131692743);
        }
      }
    }
  }
  
  private boolean hasMultipleEnabledIMEsOrSubtypes(InputMethodManager paramInputMethodManager, boolean paramBoolean)
  {
    Object localObject1 = paramInputMethodManager.getEnabledInputMethodList();
    int i = 0;
    localObject1 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (InputMethodInfo)((Iterator)localObject1).next();
      if (i > 1) {
        return true;
      }
      localObject2 = paramInputMethodManager.getEnabledInputMethodSubtypeList((InputMethodInfo)localObject2, true);
      if (((List)localObject2).isEmpty())
      {
        i += 1;
      }
      else
      {
        int j = 0;
        Iterator localIterator = ((Iterable)localObject2).iterator();
        while (localIterator.hasNext()) {
          if (((InputMethodSubtype)localIterator.next()).isAuxiliary()) {
            j += 1;
          }
        }
        if ((((List)localObject2).size() - j > 0) || ((paramBoolean) && (j > 1))) {
          i += 1;
        }
      }
    }
    return (i > 1) || (paramInputMethodManager.getEnabledInputMethodSubtypeList(null, false).size() > 1);
  }
  
  private boolean isDebugView()
  {
    return getIntent().hasExtra("com.android.settings.CryptKeeper.DEBUG_FORCE_VIEW");
  }
  
  private boolean isDebugView(String paramString)
  {
    return paramString.equals(getIntent().getStringExtra("com.android.settings.CryptKeeper.DEBUG_FORCE_VIEW"));
  }
  
  private boolean isEmergencyCallCapable()
  {
    return getResources().getBoolean(17956957);
  }
  
  private void launchEmergencyDialer()
  {
    Intent localIntent = new Intent("com.android.phone.EmergencyDialer.DIAL");
    localIntent.setFlags(276824064);
    setBackFunctionality(true);
    startActivity(localIntent);
  }
  
  private void notifyUser()
  {
    if (this.mNotificationCountdown > 0) {
      this.mNotificationCountdown -= 1;
    }
    for (;;)
    {
      this.mHandler.removeMessages(2);
      this.mHandler.sendEmptyMessageDelayed(2, 5000L);
      if (this.mWakeLock.isHeld())
      {
        if (this.mReleaseWakeLockCountdown <= 0) {
          break;
        }
        this.mReleaseWakeLockCountdown -= 1;
      }
      return;
      if (this.mAudioManager != null) {
        try
        {
          this.mAudioManager.playSoundEffect(5, 100);
        }
        catch (Exception localException)
        {
          Log.w("CryptKeeper", "notifyUser: Exception while playing sound: " + localException);
        }
      }
    }
    this.mWakeLock.release();
  }
  
  private void passwordEntryInit(int paramInt)
  {
    final Object localObject1;
    if (paramInt != 2)
    {
      this.mPasswordEntry = ((EditText)findViewById(2131362054));
      if (this.mPasswordEntry != null)
      {
        this.mPasswordEntry.setOnEditorActionListener(this);
        this.mPasswordEntry.requestFocus();
        this.mPasswordEntry.setOnKeyListener(this);
        this.mPasswordEntry.setOnTouchListener(this);
        this.mPasswordEntry.addTextChangedListener(this);
      }
      if (!getTelephonyManager().isVoiceCapable())
      {
        localObject1 = findViewById(2131362053);
        if (localObject1 != null)
        {
          Log.d("CryptKeeper", "Removing the emergency Call button");
          ((View)localObject1).setVisibility(8);
        }
      }
      Object localObject2 = findViewById(2131362066);
      localObject1 = (InputMethodManager)getSystemService("input_method");
      if ((paramInt != 2) && (localObject2 != null) && (hasMultipleEnabledIMEsOrSubtypes((InputMethodManager)localObject1, false)))
      {
        ((View)localObject2).setVisibility(0);
        ((View)localObject2).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            localObject1.showInputMethodPicker(false);
          }
        });
      }
      if (this.mWakeLock == null)
      {
        Log.d("CryptKeeper", "Acquiring wakelock.");
        localObject2 = (PowerManager)getSystemService("power");
        if (localObject2 != null)
        {
          this.mWakeLock = ((PowerManager)localObject2).newWakeLock(26, "CryptKeeper");
          this.mWakeLock.acquire();
          this.mReleaseWakeLockCountdown = 96;
        }
      }
      if ((paramInt != 2) && (this.mLockPatternView == null) && (!this.mCooldown)) {
        break label303;
      }
    }
    for (;;)
    {
      updateEmergencyCallButtonState();
      this.mHandler.removeMessages(2);
      this.mHandler.sendEmptyMessageDelayed(2, 120000L);
      getWindow().addFlags(4718592);
      return;
      this.mLockPatternView = ((LockPatternView)findViewById(2131362017));
      if (this.mLockPatternView == null) {
        break;
      }
      this.mLockPatternView.setOnPatternListener(this.mChooseNewLockPatternListener);
      break;
      label303:
      getWindow().setSoftInputMode(5);
      this.mHandler.postDelayed(new Runnable()
      {
        public void run()
        {
          localObject1.showSoftInputUnchecked(0, null);
        }
      }, 0L);
    }
  }
  
  private final void setAirplaneModeIfNecessary()
  {
    if (getTelephonyManager().getLteOnCdmaMode() == 1) {}
    for (int i = 1;; i = 0)
    {
      if (i == 0)
      {
        Log.d("CryptKeeper", "Going into airplane mode.");
        Settings.Global.putInt(getContentResolver(), "airplane_mode_on", 1);
        Intent localIntent = new Intent("android.intent.action.AIRPLANE_MODE");
        localIntent.putExtra("state", true);
        sendBroadcastAsUser(localIntent, UserHandle.ALL);
      }
      return;
    }
  }
  
  private final void setBackFunctionality(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mStatusBar.disable(52887552);
      return;
    }
    this.mStatusBar.disable(57081856);
  }
  
  private void setupUi()
  {
    if ((this.mEncryptionGoneBad) || (isDebugView("error")))
    {
      setContentView(2130968662);
      showFactoryReset(this.mCorrupt, this.mMdtpActivated);
      return;
    }
    if ((!"".equals(SystemProperties.get("vold.encrypt_progress"))) || (isDebugView("progress")))
    {
      setContentView(2130968662);
      encryptionProgressInit();
    }
    do
    {
      return;
      if ((this.mValidationComplete) || (isDebugView("password")))
      {
        new AsyncTask()
        {
          String owner_info;
          int passwordType = 0;
          boolean password_visible;
          boolean pattern_visible;
          
          public Void doInBackground(Void... paramAnonymousVarArgs)
          {
            boolean bool2 = false;
            for (;;)
            {
              try
              {
                paramAnonymousVarArgs = CryptKeeper.-wrap0(CryptKeeper.this);
                this.passwordType = paramAnonymousVarArgs.getPasswordType();
                this.owner_info = paramAnonymousVarArgs.getField("OwnerInfo");
                if (!"0".equals(paramAnonymousVarArgs.getField("PatternVisible"))) {
                  continue;
                }
                bool1 = false;
                this.pattern_visible = bool1;
                if (!"0".equals(paramAnonymousVarArgs.getField("PasswordVisible"))) {
                  continue;
                }
                bool1 = bool2;
                this.password_visible = bool1;
              }
              catch (Exception paramAnonymousVarArgs)
              {
                boolean bool1;
                Log.e("CryptKeeper", "Error calling mount service " + paramAnonymousVarArgs);
                continue;
              }
              return null;
              bool1 = true;
              continue;
              bool1 = true;
            }
          }
          
          public void onPostExecute(Void paramAnonymousVoid)
          {
            boolean bool = true;
            paramAnonymousVoid = CryptKeeper.this.getContentResolver();
            int i;
            if (this.password_visible)
            {
              i = 1;
              Settings.System.putInt(paramAnonymousVoid, "show_password", i);
              if (this.passwordType != 3) {
                break label190;
              }
              CryptKeeper.this.setContentView(2130968660);
              CryptKeeper.-set3(CryptKeeper.this, 2131692740);
            }
            for (;;)
            {
              ((TextView)CryptKeeper.this.findViewById(2131362057)).setText(CryptKeeper.-get4(CryptKeeper.this));
              paramAnonymousVoid = (TextView)CryptKeeper.this.findViewById(2131362065);
              paramAnonymousVoid.setText(this.owner_info);
              paramAnonymousVoid.setSelected(true);
              CryptKeeper.-wrap6(CryptKeeper.this, this.passwordType);
              CryptKeeper.this.findViewById(16908290).setSystemUiVisibility(4194304);
              if (CryptKeeper.-get2(CryptKeeper.this) != null)
              {
                paramAnonymousVoid = CryptKeeper.-get2(CryptKeeper.this);
                if (this.pattern_visible) {
                  bool = false;
                }
                paramAnonymousVoid.setInStealthMode(bool);
              }
              if (CryptKeeper.-get1(CryptKeeper.this))
              {
                CryptKeeper.-wrap7(CryptKeeper.this, false);
                CryptKeeper.-wrap2(CryptKeeper.this);
              }
              return;
              i = 0;
              break;
              label190:
              if (this.passwordType == 2)
              {
                CryptKeeper.this.setContentView(2130968658);
                CryptKeeper.-wrap7(CryptKeeper.this, false);
                CryptKeeper.-set3(CryptKeeper.this, 2131692741);
              }
              else
              {
                CryptKeeper.this.setContentView(2130968656);
                CryptKeeper.-set3(CryptKeeper.this, 2131692739);
              }
            }
          }
        }.execute(new Void[0]);
        return;
      }
    } while (this.mValidationRequested);
    new ValidationTask(null).execute((Void[])null);
    this.mValidationRequested = true;
  }
  
  private void showFactoryReset(final boolean paramBoolean1, final boolean paramBoolean2)
  {
    findViewById(2131362059).setVisibility(8);
    Object localObject = (Button)findViewById(2131362060);
    ((Button)localObject).setVisibility(0);
    ((Button)localObject).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (paramBoolean2)
        {
          Log.d("CryptKeeper", "  Calling encryptStorage with wipe");
          try
          {
            CryptKeeper.-wrap0(CryptKeeper.this).encryptWipeStorage(1, "");
            return;
          }
          catch (RemoteException paramAnonymousView)
          {
            Log.w("CryptKeeper", "Unable to call MountService properly");
            return;
          }
        }
        Object localObject = new File(new File("/cache"), "encryptfailed");
        paramAnonymousView = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
          localObject = new FileWriter((File)localObject);
          ((FileWriter)localObject).write(paramAnonymousView.format(new Date()));
          ((FileWriter)localObject).write("\n");
          ((FileWriter)localObject).close();
          paramAnonymousView = new Intent("android.intent.action.MASTER_CLEAR");
          paramAnonymousView.addFlags(268435456);
          paramAnonymousView.putExtra("android.intent.extra.REASON", "CryptKeeper.showFactoryReset() corrupt=" + paramBoolean1);
          CryptKeeper.this.sendBroadcast(paramAnonymousView);
          return;
        }
        catch (IOException paramAnonymousView)
        {
          for (;;)
          {
            paramAnonymousView.printStackTrace();
          }
        }
      }
    });
    if (paramBoolean1)
    {
      ((TextView)findViewById(2131361894)).setText(2131691137);
      ((TextView)findViewById(2131362057)).setText(2131691138);
    }
    for (;;)
    {
      localObject = findViewById(2131362061);
      if (localObject != null) {
        ((View)localObject).setVisibility(0);
      }
      return;
      ((TextView)findViewById(2131361894)).setText(2131691135);
      ((TextView)findViewById(2131362057)).setText(2131691136);
    }
  }
  
  private void takeEmergencyCallAction()
  {
    TelecomManager localTelecomManager = getTelecomManager();
    if (localTelecomManager.isInCall())
    {
      localTelecomManager.showInCallScreen(false);
      return;
    }
    launchEmergencyDialer();
  }
  
  private void updateEmergencyCallButtonState()
  {
    Button localButton = (Button)findViewById(2131362053);
    if (localButton == null) {
      return;
    }
    if (isEmergencyCallCapable())
    {
      localButton.setVisibility(0);
      localButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          CryptKeeper.-wrap10(CryptKeeper.this);
        }
      });
      if (!getTelecomManager().isInCall()) {
        break label67;
      }
    }
    label67:
    for (int i = 2131692833;; i = 2131692832)
    {
      localButton.setText(i);
      return;
      localButton.setVisibility(8);
      return;
    }
  }
  
  private void updateFactoryReset()
  {
    checkImei();
    if (!this.mImeiReady)
    {
      this.mHandler.removeMessages(3);
      this.mHandler.sendEmptyMessageDelayed(3, 1000L);
      return;
    }
    showFactoryReset(this.mCorrupt, this.mMdtpActivated);
  }
  
  private void updateProgress()
  {
    Object localObject1 = SystemProperties.get("vold.encrypt_progress");
    if ("error_partially_encrypted".equals(localObject1))
    {
      showFactoryReset(false, false);
      return;
    }
    localCharSequence2 = getText(2131691130);
    for (int i = 0;; i = j)
    {
      try
      {
        boolean bool = isDebugView();
        if (!bool) {
          break label201;
        }
        i = 50;
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          Object localObject2;
          label201:
          Log.w("CryptKeeper", "Error parsing progress: " + localException1.toString());
        }
      }
      localObject2 = Integer.toString(i);
      Log.v("CryptKeeper", "Encryption progress: " + (String)localObject2);
      localObject1 = localObject2;
      try
      {
        i = Integer.parseInt(SystemProperties.get("vold.encrypt_time_remaining"));
        localObject1 = localObject2;
        localCharSequence1 = localCharSequence2;
        if (i >= 0)
        {
          localObject1 = localObject2;
          localObject2 = DateUtils.formatElapsedTime((i + 9) / 10 * 10);
          localObject1 = localObject2;
          localCharSequence1 = getText(2131691131);
          localObject1 = localObject2;
        }
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          int j;
          CharSequence localCharSequence1 = localCharSequence2;
        }
      }
      localObject2 = (TextView)findViewById(2131362057);
      if (localObject2 != null) {
        ((TextView)localObject2).setText(TextUtils.expandTemplate(localCharSequence1, new CharSequence[] { localObject1 }));
      }
      this.mHandler.removeMessages(1);
      this.mHandler.sendEmptyMessageDelayed(1, 1000L);
      return;
      j = Integer.parseInt((String)localObject1);
    }
  }
  
  public void afterTextChanged(Editable paramEditable) {}
  
  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  public void onBackPressed() {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    String str = SystemProperties.get("vold.decrypt");
    boolean bool = SystemProperties.getBoolean("ro.alarm_boot", false);
    if (((!isDebugView()) && (("".equals(str)) || ("trigger_restart_framework".equals(str)))) || (bool))
    {
      disableCryptKeeperComponent(this);
      finish();
      return;
    }
    try
    {
      if (getResources().getBoolean(2131558447)) {
        setRequestedOrientation(-1);
      }
      this.mStatusBar = ((StatusBarManager)getSystemService("statusbar"));
      this.mStatusBar.disable(52887552);
      if (paramBundle != null) {
        this.mCooldown = paramBundle.getBoolean("cooldown");
      }
      setAirplaneModeIfNecessary();
      this.mAudioManager = ((AudioManager)getSystemService("audio"));
      paramBundle = getLastNonConfigurationInstance();
      if ((paramBundle instanceof NonConfigurationInstanceState))
      {
        this.mWakeLock = ((NonConfigurationInstanceState)paramBundle).wakelock;
        Log.d("CryptKeeper", "Restoring wakelock from NonConfigurationInstanceState");
      }
      return;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      for (;;) {}
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (this.mWakeLock != null)
    {
      Log.d("CryptKeeper", "Releasing and destroying wakelock");
      this.mWakeLock.release();
      this.mWakeLock = null;
    }
  }
  
  public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 0) || (paramInt == 6))
    {
      paramKeyEvent = paramTextView.getText().toString();
      if (TextUtils.isEmpty(paramKeyEvent)) {
        return true;
      }
      paramTextView.setText(null);
      this.mPasswordEntry.setEnabled(false);
      setBackFunctionality(false);
      if (paramKeyEvent.length() >= 4)
      {
        new DecryptTask(null).execute(new String[] { paramKeyEvent });
        return true;
      }
      fakeUnlockAttempt(this.mPasswordEntry);
      return true;
    }
    return false;
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    delayAudioNotification();
    return false;
  }
  
  public Object onRetainNonConfigurationInstance()
  {
    NonConfigurationInstanceState localNonConfigurationInstanceState = new NonConfigurationInstanceState(this.mWakeLock);
    Log.d("CryptKeeper", "Handing wakelock off to NonConfigurationInstanceState");
    this.mWakeLock = null;
    return localNonConfigurationInstanceState;
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putBoolean("cooldown", this.mCooldown);
  }
  
  public void onStart()
  {
    super.onStart();
    setupUi();
  }
  
  public void onStop()
  {
    super.onStop();
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(2);
  }
  
  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
    delayAudioNotification();
  }
  
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    delayAudioNotification();
    return false;
  }
  
  private class DecryptTask
    extends AsyncTask<String, Void, Integer>
  {
    private DecryptTask() {}
    
    private void hide(int paramInt)
    {
      View localView = CryptKeeper.this.findViewById(paramInt);
      if (localView != null) {
        localView.setVisibility(8);
      }
    }
    
    protected Integer doInBackground(String... paramVarArgs)
    {
      IMountService localIMountService = CryptKeeper.-wrap0(CryptKeeper.this);
      try
      {
        int i = localIMountService.decryptStorage(paramVarArgs[0]);
        return Integer.valueOf(i);
      }
      catch (Exception paramVarArgs)
      {
        Log.e("CryptKeeper", "Error while decrypting...", paramVarArgs);
      }
      return Integer.valueOf(-1);
    }
    
    protected void onPostExecute(Integer paramInteger)
    {
      if (paramInteger.intValue() == 0)
      {
        if (CryptKeeper.-get2(CryptKeeper.this) != null)
        {
          CryptKeeper.-get2(CryptKeeper.this).removeCallbacks(CryptKeeper.-get0(CryptKeeper.this));
          CryptKeeper.-get2(CryptKeeper.this).postDelayed(CryptKeeper.-get0(CryptKeeper.this), 500L);
        }
        ((TextView)CryptKeeper.this.findViewById(2131362057)).setText(2131692746);
        hide(2131362054);
        hide(2131362066);
        hide(2131362017);
        hide(2131362065);
        hide(2131362053);
        return;
      }
      if (paramInteger.intValue() == 30)
      {
        if (CryptKeeper.-get3(CryptKeeper.this))
        {
          Log.d("CryptKeeper", "  CryptKeeper.MAX_FAILED_ATTEMPTS, calling encryptStorage with wipe");
          try
          {
            CryptKeeper.-wrap0(CryptKeeper.this).encryptWipeStorage(1, "");
            return;
          }
          catch (RemoteException paramInteger)
          {
            Log.w("CryptKeeper", "Unable to call MountService properly");
            return;
          }
        }
        paramInteger = new Intent("android.intent.action.MASTER_CLEAR");
        paramInteger.addFlags(268435456);
        paramInteger.putExtra("android.intent.extra.REASON", "CryptKeeper.MAX_FAILED_ATTEMPTS");
        CryptKeeper.this.sendBroadcast(paramInteger);
        return;
      }
      if (paramInteger.intValue() == -1)
      {
        CryptKeeper.this.setContentView(2130968662);
        CryptKeeper.-wrap9(CryptKeeper.this, true, false);
        return;
      }
      CryptKeeper.-wrap4(CryptKeeper.this, paramInteger);
    }
    
    protected void onPreExecute()
    {
      super.onPreExecute();
      CryptKeeper.-wrap1(CryptKeeper.this);
    }
  }
  
  private static class NonConfigurationInstanceState
  {
    final PowerManager.WakeLock wakelock;
    
    NonConfigurationInstanceState(PowerManager.WakeLock paramWakeLock)
    {
      this.wakelock = paramWakeLock;
    }
  }
  
  private class ValidationTask
    extends AsyncTask<Void, Void, Boolean>
  {
    int state;
    
    private ValidationTask() {}
    
    protected Boolean doInBackground(Void... paramVarArgs)
    {
      boolean bool2 = true;
      paramVarArgs = CryptKeeper.-wrap0(CryptKeeper.this);
      try
      {
        Log.d("CryptKeeper", "Validating encryption state.");
        this.state = paramVarArgs.getEncryptionState();
        if (this.state == 1)
        {
          Log.w("CryptKeeper", "Unexpectedly in CryptKeeper even though there is no encryption.");
          return Boolean.valueOf(true);
        }
        paramVarArgs = CryptKeeper.this;
        if (this.state != -5)
        {
          if (this.state != 2) {
            break label108;
          }
          bool1 = true;
          CryptKeeper.-set2(paramVarArgs, bool1);
          bool1 = bool2;
          if (this.state != 0) {
            if (this.state != 2) {
              break label113;
            }
          }
        }
        label108:
        label113:
        for (boolean bool1 = bool2;; bool1 = false)
        {
          return Boolean.valueOf(bool1);
          bool1 = true;
          break;
          bool1 = false;
          break;
        }
        return Boolean.valueOf(false);
      }
      catch (RemoteException paramVarArgs)
      {
        Log.w("CryptKeeper", "Unable to get encryption state properly");
      }
    }
    
    protected void onPostExecute(Boolean paramBoolean)
    {
      boolean bool = true;
      CryptKeeper.-set4(CryptKeeper.this, true);
      if (Boolean.FALSE.equals(paramBoolean))
      {
        Log.w("CryptKeeper", "Incomplete, or corrupted encryption detected. Prompting user to wipe.");
        CryptKeeper.-set1(CryptKeeper.this, true);
        paramBoolean = CryptKeeper.this;
        if (this.state == -4) {
          CryptKeeper.-set0(paramBoolean, bool);
        }
      }
      for (;;)
      {
        CryptKeeper.-wrap8(CryptKeeper.this);
        return;
        bool = false;
        break;
        Log.d("CryptKeeper", "Encryption state validated. Proceeding to configure UI");
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\CryptKeeper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */