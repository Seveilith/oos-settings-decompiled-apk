package com.android.settings.users;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RestrictedProfileSettings
  extends AppRestrictionsFragment
  implements EditUserInfoController.OnContentChangedCallback
{
  private static final int DIALOG_CONFIRM_REMOVE = 2;
  static final int DIALOG_ID_EDIT_USER_INFO = 1;
  public static final String FILE_PROVIDER_AUTHORITY = "com.android.settings.files";
  private ImageView mDeleteButton;
  private EditUserInfoController mEditUserInfoController = new EditUserInfoController();
  private View mHeaderView;
  private ImageView mUserIconView;
  private TextView mUserNameView;
  
  private void removeUser()
  {
    getView().post(new Runnable()
    {
      public void run()
      {
        RestrictedProfileSettings.this.mUserManager.removeUser(RestrictedProfileSettings.this.mUser.getIdentifier());
        RestrictedProfileSettings.this.finishFragment();
      }
    });
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    this.mHeaderView = setPinnedHeaderView(2130969100);
    this.mHeaderView.setOnClickListener(this);
    this.mUserIconView = ((ImageView)this.mHeaderView.findViewById(16908294));
    this.mUserNameView = ((TextView)this.mHeaderView.findViewById(16908310));
    this.mDeleteButton = ((ImageView)this.mHeaderView.findViewById(2131362675));
    this.mDeleteButton.setOnClickListener(this);
    super.onActivityCreated(paramBundle);
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    this.mEditUserInfoController.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onClick(View paramView)
  {
    if (paramView == this.mHeaderView)
    {
      showDialog(1);
      return;
    }
    if (paramView == this.mDeleteButton)
    {
      showDialog(2);
      return;
    }
    super.onClick(paramView);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null) {
      this.mEditUserInfoController.onRestoreInstanceState(paramBundle);
    }
    init(paramBundle);
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    if (paramInt == 1) {
      return this.mEditUserInfoController.createDialog(this, this.mUserIconView.getDrawable(), this.mUserNameView.getText(), 2131691056, this, this.mUser);
    }
    if (paramInt == 2) {
      UserDialogs.createRemoveDialog(getActivity(), this.mUser.getIdentifier(), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          RestrictedProfileSettings.-wrap0(RestrictedProfileSettings.this);
        }
      });
    }
    return null;
  }
  
  public void onLabelChanged(CharSequence paramCharSequence)
  {
    this.mUserNameView.setText(paramCharSequence);
  }
  
  public void onPhotoChanged(Drawable paramDrawable)
  {
    this.mUserIconView.setImageDrawable(paramDrawable);
  }
  
  public void onResume()
  {
    super.onResume();
    UserInfo localUserInfo = com.android.settings.Utils.getExistingUser(this.mUserManager, this.mUser);
    if (localUserInfo == null)
    {
      finishFragment();
      return;
    }
    ((TextView)this.mHeaderView.findViewById(16908310)).setText(localUserInfo.name);
    ((ImageView)this.mHeaderView.findViewById(16908294)).setImageDrawable(com.android.settingslib.Utils.getUserIcon(getActivity(), this.mUserManager, localUserInfo));
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    this.mEditUserInfoController.onSaveInstanceState(paramBundle);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    this.mEditUserInfoController.startingActivityForResult();
    super.startActivityForResult(paramIntent, paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\users\RestrictedProfileSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */