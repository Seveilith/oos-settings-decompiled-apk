package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.pm.UserInfo;
import android.net.http.SslCertificate;
import android.net.http.SslCertificate.DName;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewPropertyAnimator;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.android.internal.widget.LockPatternUtils;
import com.android.settingslib.RestrictedLockUtils;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntConsumer;

class TrustedCredentialsDialogBuilder
  extends AlertDialog.Builder
{
  private final DialogEventHandler mDialogEventHandler;
  
  public TrustedCredentialsDialogBuilder(Activity paramActivity, DelegateInterface paramDelegateInterface)
  {
    super(paramActivity);
    this.mDialogEventHandler = new DialogEventHandler(paramActivity, paramDelegateInterface);
    initDefaultBuilderParams();
  }
  
  private void initDefaultBuilderParams()
  {
    setTitle(17040626);
    setView(DialogEventHandler.-get3(this.mDialogEventHandler));
    setPositiveButton(2131692888, null);
    setNegativeButton(17039370, null);
  }
  
  public AlertDialog create()
  {
    AlertDialog localAlertDialog = super.create();
    localAlertDialog.setOnShowListener(this.mDialogEventHandler);
    this.mDialogEventHandler.setDialog(localAlertDialog);
    return localAlertDialog;
  }
  
  public TrustedCredentialsDialogBuilder setCertHolder(TrustedCredentialsSettings.CertHolder paramCertHolder)
  {
    if (paramCertHolder == null) {}
    TrustedCredentialsSettings.CertHolder[] arrayOfCertHolder;
    for (paramCertHolder = new TrustedCredentialsSettings.CertHolder[0];; paramCertHolder = arrayOfCertHolder)
    {
      return setCertHolders(paramCertHolder);
      arrayOfCertHolder = new TrustedCredentialsSettings.CertHolder[1];
      arrayOfCertHolder[0] = paramCertHolder;
    }
  }
  
  public TrustedCredentialsDialogBuilder setCertHolders(TrustedCredentialsSettings.CertHolder[] paramArrayOfCertHolder)
  {
    this.mDialogEventHandler.setCertHolders(paramArrayOfCertHolder);
    return this;
  }
  
  public static abstract interface DelegateInterface
  {
    public abstract List<X509Certificate> getX509CertsFromCertHolder(TrustedCredentialsSettings.CertHolder paramCertHolder);
    
    public abstract void removeOrInstallCert(TrustedCredentialsSettings.CertHolder paramCertHolder);
    
    public abstract boolean startConfirmCredentialIfNotConfirmed(int paramInt, IntConsumer paramIntConsumer);
  }
  
  private static class DialogEventHandler
    implements DialogInterface.OnShowListener, View.OnClickListener
  {
    private static final long IN_DURATION_MS = 200L;
    private static final long OUT_DURATION_MS = 300L;
    private final Activity mActivity;
    private TrustedCredentialsSettings.CertHolder[] mCertHolders = new TrustedCredentialsSettings.CertHolder[0];
    private int mCurrentCertIndex = -1;
    private View mCurrentCertLayout = null;
    private final TrustedCredentialsDialogBuilder.DelegateInterface mDelegate;
    private AlertDialog mDialog;
    private final DevicePolicyManager mDpm;
    private boolean mNeedsApproval;
    private Button mNegativeButton;
    private Button mPositiveButton;
    private final LinearLayout mRootContainer;
    private final UserManager mUserManager;
    
    public DialogEventHandler(Activity paramActivity, TrustedCredentialsDialogBuilder.DelegateInterface paramDelegateInterface)
    {
      this.mActivity = paramActivity;
      this.mDpm = ((DevicePolicyManager)paramActivity.getSystemService(DevicePolicyManager.class));
      this.mUserManager = ((UserManager)paramActivity.getSystemService(UserManager.class));
      this.mDelegate = paramDelegateInterface;
      this.mRootContainer = new LinearLayout(this.mActivity);
      this.mRootContainer.setOrientation(1);
    }
    
    private void addAndAnimateNewContent(View paramView)
    {
      this.mCurrentCertLayout = paramView;
      this.mRootContainer.removeAllViews();
      this.mRootContainer.addView(paramView);
      this.mRootContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
      {
        public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
        {
          TrustedCredentialsDialogBuilder.DialogEventHandler.-get3(TrustedCredentialsDialogBuilder.DialogEventHandler.this).removeOnLayoutChangeListener(this);
          paramAnonymousInt1 = TrustedCredentialsDialogBuilder.DialogEventHandler.-get3(TrustedCredentialsDialogBuilder.DialogEventHandler.this).getWidth();
          TrustedCredentialsDialogBuilder.DialogEventHandler.-get1(TrustedCredentialsDialogBuilder.DialogEventHandler.this).setTranslationX(paramAnonymousInt1);
          TrustedCredentialsDialogBuilder.DialogEventHandler.-get1(TrustedCredentialsDialogBuilder.DialogEventHandler.this).animate().translationX(0.0F).setInterpolator(AnimationUtils.loadInterpolator(TrustedCredentialsDialogBuilder.DialogEventHandler.-get0(TrustedCredentialsDialogBuilder.DialogEventHandler.this), 17563662)).setDuration(200L).start();
        }
      });
    }
    
    private void animateOldContent(Runnable paramRunnable)
    {
      this.mCurrentCertLayout.animate().alpha(0.0F).setDuration(300L).setInterpolator(AnimationUtils.loadInterpolator(this.mActivity, 17563663)).withEndAction(paramRunnable).start();
    }
    
    private void animateViewTransition(final View paramView)
    {
      animateOldContent(new Runnable()
      {
        public void run()
        {
          TrustedCredentialsDialogBuilder.DialogEventHandler.-wrap0(TrustedCredentialsDialogBuilder.DialogEventHandler.this, paramView);
        }
      });
    }
    
    private static int getButtonConfirmation(TrustedCredentialsSettings.CertHolder paramCertHolder)
    {
      if (paramCertHolder.isSystemCert())
      {
        if (paramCertHolder.isDeleted()) {
          return 2131692889;
        }
        return 2131692890;
      }
      return 2131692891;
    }
    
    private static int getButtonLabel(TrustedCredentialsSettings.CertHolder paramCertHolder)
    {
      if (paramCertHolder.isSystemCert())
      {
        if (paramCertHolder.isDeleted()) {
          return 2131692886;
        }
        return 2131692885;
      }
      return 2131692887;
    }
    
    private LinearLayout getCertLayout(TrustedCredentialsSettings.CertHolder paramCertHolder)
    {
      final ArrayList localArrayList = new ArrayList();
      Object localObject = new ArrayList();
      paramCertHolder = this.mDelegate.getX509CertsFromCertHolder(paramCertHolder);
      if (paramCertHolder != null)
      {
        paramCertHolder = paramCertHolder.iterator();
        while (paramCertHolder.hasNext())
        {
          SslCertificate localSslCertificate = new SslCertificate((X509Certificate)paramCertHolder.next());
          localArrayList.add(localSslCertificate.inflateCertificateView(this.mActivity));
          ((ArrayList)localObject).add(localSslCertificate.getIssuedTo().getCName());
        }
      }
      localObject = new ArrayAdapter(this.mActivity, 17367048, (List)localObject);
      ((ArrayAdapter)localObject).setDropDownViewResource(17367049);
      paramCertHolder = new Spinner(this.mActivity);
      paramCertHolder.setAdapter((SpinnerAdapter)localObject);
      paramCertHolder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
        public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          int i = 0;
          if (i < localArrayList.size())
          {
            paramAnonymousAdapterView = (View)localArrayList.get(i);
            if (i == paramAnonymousInt) {}
            for (int j = 0;; j = 8)
            {
              paramAnonymousAdapterView.setVisibility(j);
              i += 1;
              break;
            }
          }
        }
        
        public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
      });
      localObject = new LinearLayout(this.mActivity);
      ((LinearLayout)localObject).setOrientation(1);
      ((LinearLayout)localObject).addView(paramCertHolder);
      int i = 0;
      if (i < localArrayList.size())
      {
        paramCertHolder = (View)localArrayList.get(i);
        if (i == 0) {}
        for (int j = 0;; j = 8)
        {
          paramCertHolder.setVisibility(j);
          ((LinearLayout)localObject).addView(paramCertHolder);
          i += 1;
          break;
        }
      }
      return (LinearLayout)localObject;
    }
    
    private TrustedCredentialsSettings.CertHolder getCurrentCertInfo()
    {
      if (this.mCurrentCertIndex < this.mCertHolders.length) {
        return this.mCertHolders[this.mCurrentCertIndex];
      }
      return null;
    }
    
    private boolean isUserSecure(int paramInt)
    {
      LockPatternUtils localLockPatternUtils = new LockPatternUtils(this.mActivity);
      if (localLockPatternUtils.isSecure(paramInt)) {
        return true;
      }
      UserInfo localUserInfo = this.mUserManager.getProfileParent(paramInt);
      if (localUserInfo == null) {
        return false;
      }
      return localLockPatternUtils.isSecure(localUserInfo.id);
    }
    
    private void nextOrDismiss()
    {
      for (this.mCurrentCertIndex += 1; (this.mCurrentCertIndex < this.mCertHolders.length) && (getCurrentCertInfo() == null); this.mCurrentCertIndex += 1) {}
      if (this.mCurrentCertIndex >= this.mCertHolders.length)
      {
        this.mDialog.dismiss();
        return;
      }
      updateViewContainer();
      updatePositiveButton();
      updateNegativeButton();
    }
    
    private void onClickOk()
    {
      nextOrDismiss();
    }
    
    private void onClickRemove()
    {
      final TrustedCredentialsSettings.CertHolder localCertHolder = getCurrentCertInfo();
      new AlertDialog.Builder(this.mActivity).setMessage(getButtonConfirmation(localCertHolder)).setPositiveButton(17039379, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          TrustedCredentialsDialogBuilder.DialogEventHandler.-get2(TrustedCredentialsDialogBuilder.DialogEventHandler.this).removeOrInstallCert(localCertHolder);
          paramAnonymousDialogInterface.dismiss();
          TrustedCredentialsDialogBuilder.DialogEventHandler.-wrap1(TrustedCredentialsDialogBuilder.DialogEventHandler.this);
        }
      }).setNegativeButton(17039369, null).show();
    }
    
    private void onClickTrust()
    {
      TrustedCredentialsSettings.CertHolder localCertHolder = getCurrentCertInfo();
      if (!this.mDelegate.startConfirmCredentialIfNotConfirmed(localCertHolder.getUserId(), new -void_onClickTrust__LambdaImpl0()))
      {
        this.mDpm.approveCaCert(localCertHolder.getAlias(), localCertHolder.getUserId(), true);
        nextOrDismiss();
      }
    }
    
    private void onCredentialConfirmed(int paramInt)
    {
      if ((this.mDialog.isShowing()) && (this.mNeedsApproval) && (getCurrentCertInfo() != null) && (getCurrentCertInfo().getUserId() == paramInt)) {
        onClickTrust();
      }
    }
    
    private Button updateButton(int paramInt, CharSequence paramCharSequence)
    {
      this.mDialog.setButton(paramInt, paramCharSequence, (DialogInterface.OnClickListener)null);
      Button localButton = this.mDialog.getButton(paramInt);
      localButton.setText(paramCharSequence);
      localButton.setOnClickListener(this);
      return localButton;
    }
    
    private void updateNegativeButton()
    {
      Object localObject = getCurrentCertInfo();
      if (this.mUserManager.hasUserRestriction("no_config_credentials", new UserHandle(((TrustedCredentialsSettings.CertHolder)localObject).getUserId())))
      {
        i = 0;
        this.mNegativeButton = updateButton(-2, this.mActivity.getText(getButtonLabel((TrustedCredentialsSettings.CertHolder)localObject)));
        localObject = this.mNegativeButton;
        if (i == 0) {
          break label74;
        }
      }
      label74:
      for (int i = 0;; i = 8)
      {
        ((Button)localObject).setVisibility(i);
        return;
        i = 1;
        break;
      }
    }
    
    private void updatePositiveButton()
    {
      boolean bool2 = false;
      Object localObject = getCurrentCertInfo();
      boolean bool1 = bool2;
      if (!((TrustedCredentialsSettings.CertHolder)localObject).isSystemCert())
      {
        bool1 = bool2;
        if (isUserSecure(((TrustedCredentialsSettings.CertHolder)localObject).getUserId()))
        {
          if (!this.mDpm.isCaCertApproved(((TrustedCredentialsSettings.CertHolder)localObject).getAlias(), ((TrustedCredentialsSettings.CertHolder)localObject).getUserId())) {
            break label113;
          }
          bool1 = bool2;
        }
      }
      this.mNeedsApproval = bool1;
      if (RestrictedLockUtils.getProfileOrDeviceOwner(this.mActivity, ((TrustedCredentialsSettings.CertHolder)localObject).getUserId()) != null)
      {
        i = 1;
        label76:
        localObject = this.mActivity;
        if ((i != 0) || (!this.mNeedsApproval)) {
          break label123;
        }
      }
      label113:
      label123:
      for (int i = 2131692888;; i = 17039370)
      {
        this.mPositiveButton = updateButton(-1, ((Activity)localObject).getText(i));
        return;
        bool1 = true;
        break;
        i = 0;
        break label76;
      }
    }
    
    private void updateViewContainer()
    {
      LinearLayout localLinearLayout = getCertLayout(getCurrentCertInfo());
      if (this.mCurrentCertLayout == null)
      {
        this.mCurrentCertLayout = localLinearLayout;
        this.mRootContainer.addView(this.mCurrentCertLayout);
        return;
      }
      animateViewTransition(localLinearLayout);
    }
    
    public void onClick(View paramView)
    {
      if (paramView == this.mPositiveButton) {
        if (this.mNeedsApproval) {
          onClickTrust();
        }
      }
      while (paramView != this.mNegativeButton)
      {
        return;
        onClickOk();
        return;
      }
      onClickRemove();
    }
    
    public void onShow(DialogInterface paramDialogInterface)
    {
      nextOrDismiss();
    }
    
    public void setCertHolders(TrustedCredentialsSettings.CertHolder[] paramArrayOfCertHolder)
    {
      this.mCertHolders = paramArrayOfCertHolder;
    }
    
    public void setDialog(AlertDialog paramAlertDialog)
    {
      this.mDialog = paramAlertDialog;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\TrustedCredentialsDialogBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */