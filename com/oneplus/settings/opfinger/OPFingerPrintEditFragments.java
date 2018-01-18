package com.oneplus.settings.opfinger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.RemovalCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.lib.widget.OPEditText;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPProgressDialog;
import java.io.PrintStream;
import java.util.List;

public class OPFingerPrintEditFragments
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceClickListener
{
  private static final int DELETED_MESSAGE = 2;
  private static final int DELETED_MESSAGE_DELAYED = 100;
  private static final int DELETED_MESSAGE_FAILED = 3;
  private static final int DELETING_MESSAGE = 1;
  public static final String FINGERPRINT_PARCELABLE = "fingerprint_parcelable";
  private static final String KEY_OPFINGER_DELETE = "opfingerprint_delete";
  private static final String KEY_OPFINGER_EDIT = "key_opfinger_edit";
  private static final String KEY_OPFINGER_RENAME = "opfingerprint_rename";
  private static final int MSG_REFRESH_FINGERPRINT_TEMPLATES = 5;
  private static final int SHOW_DELETE_DIALOG_MESSAGE = 7;
  private static final int SHOW_RENAME_DIALOG_MESSAGE = 4;
  private static final int SHOW_WARNING_DIALOG = 6;
  private static final String TAG = "OPFingerPrintEditFragments";
  private boolean isDeleteDialogShow;
  private boolean isRenameDialogShow;
  private boolean isWarnDialogShow;
  private AlertDialog mDeleteDialog;
  private Preference mDeltePreference;
  private Fingerprint mFingerprint;
  private FingerprintManager mFingerprintManager;
  private CharSequence mFingerprintName;
  private Handler mHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      switch (paramAnonymousMessage.what)
      {
      case 2: 
      default: 
        return;
      case 5: 
        Toast.makeText(SettingsBaseApplication.mApplication, 2131689954, 0).show();
        if (OPFingerPrintEditFragments.-get5(OPFingerPrintEditFragments.this) != null) {
          OPFingerPrintEditFragments.-get5(OPFingerPrintEditFragments.this).dismiss();
        }
        OPFingerPrintEditFragments.this.finish();
        return;
      case 1: 
        paramAnonymousMessage = OPFingerPrintEditFragments.this.getActivity();
        if ((paramAnonymousMessage == null) || (paramAnonymousMessage.isDestroyed())) {
          return;
        }
        OPFingerPrintEditFragments.-get5(OPFingerPrintEditFragments.this).setMessage(OPFingerPrintEditFragments.this.getResources().getString(2131689953));
        OPFingerPrintEditFragments.-get5(OPFingerPrintEditFragments.this).show();
        return;
      case 6: 
        OPFingerPrintEditFragments.this.showWarnigDialog((Fingerprint)paramAnonymousMessage.obj);
        return;
      case 3: 
        OPFingerPrintEditFragments.-get5(OPFingerPrintEditFragments.this).dismiss();
        Toast.makeText(SettingsBaseApplication.mApplication, 2131689955, 0).show();
        return;
      case 4: 
        OPFingerPrintEditFragments.this.showRenameDialog();
        return;
      }
      OPFingerPrintEditFragments.this.showDeleteDialog((Fingerprint)paramAnonymousMessage.obj);
    }
  };
  private OPFingerPrintEditCategory mOPFingerPrintEditViewCategory;
  private OPProgressDialog mProgressDialog;
  private FingerprintManager.RemovalCallback mRemoveCallback = new FingerprintManager.RemovalCallback()
  {
    public void onRemovalError(Fingerprint paramAnonymousFingerprint, int paramAnonymousInt, CharSequence paramAnonymousCharSequence)
    {
      System.out.println("zhuyang--mRemoveCallback--onRemovalError");
      paramAnonymousFingerprint = OPFingerPrintEditFragments.this.getActivity();
      if (paramAnonymousFingerprint != null) {
        Toast.makeText(paramAnonymousFingerprint, paramAnonymousCharSequence, 0);
      }
    }
    
    public void onRemovalSucceeded(Fingerprint paramAnonymousFingerprint)
    {
      System.out.println("zhuyang--mRemoveCallback--onRemovalSucceeded");
      OPFingerPrintEditFragments.-get3(OPFingerPrintEditFragments.this).obtainMessage(5, paramAnonymousFingerprint.getFingerId(), 0).sendToTarget();
    }
  };
  private AlertDialog mRenameDialog;
  private Preference mRenamePreference;
  private int mUserId;
  private AlertDialog mWarnDialog;
  private String renameData;
  private OPEditText renameEdit;
  
  private void deleteFingerPrint(Fingerprint paramFingerprint)
  {
    this.mFingerprintManager.remove(paramFingerprint, this.mUserId, this.mRemoveCallback);
  }
  
  private void initViews()
  {
    this.mOPFingerPrintEditViewCategory = ((OPFingerPrintEditCategory)findPreference("key_opfinger_edit"));
    this.mRenamePreference = findPreference("opfingerprint_rename");
    this.mDeltePreference = findPreference("opfingerprint_delete");
    this.mOPFingerPrintEditViewCategory.setFingerprintName(this.mFingerprintName);
  }
  
  private void renameFingerPrint(int paramInt, String paramString)
  {
    this.mFingerprintManager.rename(paramInt, this.mUserId, paramString);
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      this.isRenameDialogShow = paramBundle.getBoolean("renamedialog");
      this.isDeleteDialogShow = paramBundle.getBoolean("deletedialog");
      this.isWarnDialogShow = paramBundle.getBoolean("warndialog");
      this.renameData = paramBundle.getString("renamedata");
    }
    super.onCreate(paramBundle);
    this.mUserId = getActivity().getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
    this.mFingerprint = ((Fingerprint)getArguments().getParcelable("fingerprint_parcelable"));
    if (paramBundle != null) {
      this.mFingerprintName = paramBundle.getCharSequence("fingerprint_name");
    }
    do
    {
      for (;;)
      {
        this.mProgressDialog = new OPProgressDialog(getActivity());
        this.mFingerprintManager = ((FingerprintManager)getActivity().getSystemService("fingerprint"));
        addPreferencesFromResource(2131230792);
        initViews();
        if (!this.isRenameDialogShow) {
          break;
        }
        showRenameDialog();
        return;
        if (this.mFingerprint != null) {
          this.mFingerprintName = this.mFingerprint.getName();
        }
      }
      if (this.isWarnDialogShow)
      {
        showWarnigDialog(this.mFingerprint);
        return;
      }
    } while (!this.isDeleteDialogShow);
    showDeleteDialog(this.mFingerprint);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
  }
  
  public void onDetach()
  {
    super.onDetach();
  }
  
  public void onPause()
  {
    super.onPause();
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    return false;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    paramPreference = paramPreference.getKey();
    if ("opfingerprint_rename".equals(paramPreference)) {
      this.mHandler.sendEmptyMessage(4);
    }
    for (;;)
    {
      return true;
      if ("opfingerprint_delete".equals(paramPreference)) {
        new Thread(new Runnable()
        {
          public void run()
          {
            if (OPFingerPrintEditFragments.-get1(OPFingerPrintEditFragments.this) != null)
            {
              Message localMessage = OPFingerPrintEditFragments.-get3(OPFingerPrintEditFragments.this).obtainMessage();
              if (OPFingerPrintEditFragments.-get1(OPFingerPrintEditFragments.this).getEnrolledFingerprints().size() == 1) {}
              for (localMessage.what = 6;; localMessage.what = 7)
              {
                localMessage.obj = OPFingerPrintEditFragments.-get0(OPFingerPrintEditFragments.this);
                OPFingerPrintEditFragments.-get3(OPFingerPrintEditFragments.this).sendMessage(localMessage);
                return;
              }
            }
            OPFingerPrintEditFragments.-get3(OPFingerPrintEditFragments.this).sendEmptyMessage(3);
          }
        }).start();
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (this.mRenameDialog != null)
    {
      paramBundle.putBoolean("renamedialog", this.mRenameDialog.isShowing());
      paramBundle.putString("renamedata", this.renameEdit.getText().toString());
    }
    if (this.mWarnDialog != null) {
      paramBundle.putBoolean("warndialog", this.mWarnDialog.isShowing());
    }
    if (this.mDeleteDialog != null) {
      paramBundle.putBoolean("deletedialog", this.mDeleteDialog.isShowing());
    }
    paramBundle.putCharSequence("fingerprint_name", this.mFingerprintName);
  }
  
  public void showDeleteDialog(final Fingerprint paramFingerprint)
  {
    this.mDeleteDialog = new AlertDialog.Builder(getActivity()).setTitle(2131691089).setMessage(2131690291).setPositiveButton(2131690994, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.dismiss();
        OPFingerPrintEditFragments.-get3(OPFingerPrintEditFragments.this).sendEmptyMessage(1);
        OPFingerPrintEditFragments.-wrap0(OPFingerPrintEditFragments.this, paramFingerprint);
        OPFingerPrintEditFragments.-get3(OPFingerPrintEditFragments.this).sendEmptyMessageDelayed(2, 100L);
      }
    }).setNegativeButton(2131690993, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.dismiss();
      }
    }).create();
    this.mDeleteDialog.show();
  }
  
  public void showRenameDialog()
  {
    View localView = LayoutInflater.from(getActivity()).inflate(2130968810, null);
    this.renameEdit = ((OPEditText)localView.findViewById(2131362325));
    this.renameEdit.setHint(this.mFingerprintName);
    this.renameEdit.requestFocus();
    this.renameEdit.setText(this.renameData);
    this.renameEdit.addTextChangedListener(new TextWatcher()
    {
      public void afterTextChanged(Editable paramAnonymousEditable) {}
      
      public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
    });
    this.mRenameDialog = new AlertDialog.Builder(getActivity()).setTitle(2131693056).setView(localView).setCancelable(true).setPositiveButton(2131690994, null).setNegativeButton(2131689965, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.dismiss();
      }
    }).create();
    this.mRenameDialog.show();
    this.mRenameDialog.getButton(-1).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = OPFingerPrintEditFragments.-get7(OPFingerPrintEditFragments.this).getText().toString().trim();
        if ("".equals(paramAnonymousView))
        {
          paramAnonymousView = (String)OPFingerPrintEditFragments.-get2(OPFingerPrintEditFragments.this);
          Toast.makeText(OPFingerPrintEditFragments.this.getActivity(), 2131690204, 0).show();
          return;
        }
        OPFingerPrintEditFragments.-set0(OPFingerPrintEditFragments.this, paramAnonymousView);
        OPFingerPrintEditFragments.-wrap1(OPFingerPrintEditFragments.this, OPFingerPrintEditFragments.-get0(OPFingerPrintEditFragments.this).getFingerId(), paramAnonymousView);
        OPFingerPrintEditFragments.-set0(OPFingerPrintEditFragments.this, paramAnonymousView);
        OPFingerPrintEditFragments.-get4(OPFingerPrintEditFragments.this).setFingerprintName(paramAnonymousView);
        OPFingerPrintEditFragments.-get6(OPFingerPrintEditFragments.this).dismiss();
      }
    });
  }
  
  public void showWarnigDialog(final Fingerprint paramFingerprint)
  {
    this.mWarnDialog = new AlertDialog.Builder(getActivity()).setTitle(2131691113).setMessage(2131691114).setPositiveButton(2131690994, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.dismiss();
        OPFingerPrintEditFragments.-get3(OPFingerPrintEditFragments.this).sendEmptyMessage(1);
        OPFingerPrintEditFragments.-wrap0(OPFingerPrintEditFragments.this, paramFingerprint);
        OPFingerPrintEditFragments.-get3(OPFingerPrintEditFragments.this).sendEmptyMessageDelayed(2, 100L);
      }
    }).setNegativeButton(2131690993, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.dismiss();
      }
    }).create();
    this.mWarnDialog.show();
  }
  
  public class RenameDialog
    extends DialogFragment
  {
    public RenameDialog() {}
    
    public void onAttach(Activity paramActivity)
    {
      super.onAttach(paramActivity);
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      paramBundle = LayoutInflater.from(getActivity()).inflate(2130968810, null);
      final OPEditText localOPEditText = (OPEditText)paramBundle.findViewById(2131362325);
      localOPEditText.setHint(OPFingerPrintEditFragments.-get2(OPFingerPrintEditFragments.this));
      localOPEditText.requestFocus();
      localOPEditText.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable) {}
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          if (paramAnonymousCharSequence == " ") {
            localOPEditText.setText("");
          }
        }
      });
      new AlertDialog.Builder(getActivity()).setTitle(2131693056).setView(paramBundle).setCancelable(true).setPositiveButton(2131690994, null).setNegativeButton(2131689965, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface.dismiss();
        }
      }).create();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\opfinger\OPFingerPrintEditFragments.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */