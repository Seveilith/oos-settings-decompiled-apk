package com.android.settings.users;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import com.android.settingslib.Utils;
import com.android.settingslib.drawable.CircleFramedDrawable;
import java.io.File;

public class EditUserInfoController
{
  private static final String KEY_AWAITING_RESULT = "awaiting_result";
  private static final String KEY_SAVED_PHOTO = "pending_photo";
  private Dialog mEditUserInfoDialog;
  private EditUserPhotoController mEditUserPhotoController;
  private Bitmap mSavedPhoto;
  private UserHandle mUser;
  private UserManager mUserManager;
  private boolean mWaitingForActivityResult = false;
  
  public void clear()
  {
    this.mEditUserPhotoController.removeNewUserPhotoBitmapFile();
    this.mEditUserInfoDialog = null;
    this.mSavedPhoto = null;
  }
  
  public Dialog createDialog(final Fragment paramFragment, final Drawable paramDrawable, final CharSequence paramCharSequence, int paramInt, final OnContentChangedCallback paramOnContentChangedCallback, UserHandle paramUserHandle)
  {
    Activity localActivity = paramFragment.getActivity();
    this.mUser = paramUserHandle;
    if (this.mUserManager == null) {
      this.mUserManager = UserManager.get(localActivity);
    }
    View localView = localActivity.getLayoutInflater().inflate(2130968694, null);
    UserInfo localUserInfo = this.mUserManager.getUserInfo(this.mUser.getIdentifier());
    final EditText localEditText = (EditText)localView.findViewById(2131362131);
    localEditText.setText(localUserInfo.name);
    ImageView localImageView = (ImageView)localView.findViewById(2131362130);
    if (this.mSavedPhoto != null) {
      paramUserHandle = CircleFramedDrawable.getInstance(localActivity, this.mSavedPhoto);
    }
    for (;;)
    {
      localImageView.setImageDrawable(paramUserHandle);
      this.mEditUserPhotoController = new EditUserPhotoController(paramFragment, localImageView, this.mSavedPhoto, paramUserHandle, this.mWaitingForActivityResult);
      this.mEditUserInfoDialog = new AlertDialog.Builder(localActivity).setTitle(2131691056).setView(localView).setCancelable(true).setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1)
          {
            paramAnonymousDialogInterface = localEditText.getText();
            if ((!TextUtils.isEmpty(paramAnonymousDialogInterface)) && ((paramCharSequence == null) || (!paramAnonymousDialogInterface.toString().equals(paramCharSequence.toString())))) {
              break label108;
            }
            paramAnonymousDialogInterface = EditUserInfoController.-get0(EditUserInfoController.this).getNewUserPhotoDrawable();
            Bitmap localBitmap = EditUserInfoController.-get0(EditUserInfoController.this).getNewUserPhotoBitmap();
            if ((paramAnonymousDialogInterface != null) && (localBitmap != null) && (!paramAnonymousDialogInterface.equals(paramDrawable))) {
              break label159;
            }
          }
          for (;;)
          {
            paramFragment.getActivity().removeDialog(1);
            EditUserInfoController.this.clear();
            return;
            label108:
            if (paramOnContentChangedCallback != null) {
              paramOnContentChangedCallback.onLabelChanged(paramAnonymousDialogInterface.toString());
            }
            EditUserInfoController.-get2(EditUserInfoController.this).setUserName(EditUserInfoController.-get1(EditUserInfoController.this).getIdentifier(), paramAnonymousDialogInterface.toString());
            break;
            label159:
            if (paramOnContentChangedCallback != null) {
              paramOnContentChangedCallback.onPhotoChanged(paramAnonymousDialogInterface);
            }
            new AsyncTask()
            {
              protected Void doInBackground(Void... paramAnonymous2VarArgs)
              {
                EditUserInfoController.-get2(EditUserInfoController.this).setUserIcon(EditUserInfoController.-get1(EditUserInfoController.this).getIdentifier(), EditUserInfoController.-get0(EditUserInfoController.this).getNewUserPhotoBitmap());
                return null;
              }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
          }
        }
      }).setNegativeButton(17039360, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          EditUserInfoController.this.clear();
        }
      }).create();
      this.mEditUserInfoDialog.getWindow().setSoftInputMode(4);
      return this.mEditUserInfoDialog;
      paramUserHandle = paramDrawable;
      if (paramDrawable == null) {
        paramUserHandle = Utils.getUserIcon(localActivity, this.mUserManager, localUserInfo);
      }
    }
  }
  
  public Dialog getDialog()
  {
    return this.mEditUserInfoDialog;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.mWaitingForActivityResult = false;
    if ((this.mEditUserInfoDialog != null) && (this.mEditUserInfoDialog.isShowing()) && (this.mEditUserPhotoController.onActivityResult(paramInt1, paramInt2, paramIntent))) {}
  }
  
  public void onRestoreInstanceState(Bundle paramBundle)
  {
    String str = paramBundle.getString("pending_photo");
    if (str != null) {
      this.mSavedPhoto = EditUserPhotoController.loadNewUserPhotoBitmap(new File(str));
    }
    this.mWaitingForActivityResult = paramBundle.getBoolean("awaiting_result", false);
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    if ((this.mEditUserInfoDialog != null) && (this.mEditUserInfoDialog.isShowing()) && (this.mEditUserPhotoController != null))
    {
      File localFile = this.mEditUserPhotoController.saveNewUserPhotoBitmap();
      if (localFile != null) {
        paramBundle.putString("pending_photo", localFile.getPath());
      }
    }
    if (this.mWaitingForActivityResult) {
      paramBundle.putBoolean("awaiting_result", this.mWaitingForActivityResult);
    }
  }
  
  public void startingActivityForResult()
  {
    this.mWaitingForActivityResult = true;
  }
  
  public static abstract interface OnContentChangedCallback
  {
    public abstract void onLabelChanged(CharSequence paramCharSequence);
    
    public abstract void onPhotoChanged(Drawable paramDrawable);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\users\EditUserInfoController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */