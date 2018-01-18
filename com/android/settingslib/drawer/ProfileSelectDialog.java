package com.android.settingslib.drawer;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.widget.ListAdapter;
import com.android.settingslib.R.string;
import java.util.ArrayList;

public class ProfileSelectDialog
  extends DialogFragment
  implements DialogInterface.OnClickListener
{
  private static final String ARG_SELECTED_TILE = "selectedTile";
  private Tile mSelectedTile;
  
  public static void show(FragmentManager paramFragmentManager, Tile paramTile)
  {
    ProfileSelectDialog localProfileSelectDialog = new ProfileSelectDialog();
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("selectedTile", paramTile);
    localProfileSelectDialog.setArguments(localBundle);
    localProfileSelectDialog.show(paramFragmentManager, "select_profile");
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    paramDialogInterface = (UserHandle)this.mSelectedTile.userHandle.get(paramInt);
    this.mSelectedTile.intent.putExtra("show_drawer_menu", true);
    this.mSelectedTile.intent.addFlags(32768);
    getActivity().startActivityAsUser(this.mSelectedTile.intent, paramDialogInterface);
    ((SettingsDrawerActivity)getActivity()).onProfileTileOpen();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mSelectedTile = ((Tile)getArguments().getParcelable("selectedTile"));
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    Object localObject = getActivity();
    paramBundle = new AlertDialog.Builder((Context)localObject);
    localObject = UserAdapter.createUserAdapter(UserManager.get((Context)localObject), (Context)localObject, this.mSelectedTile.userHandle);
    paramBundle.setTitle(R.string.choose_profile).setAdapter((ListAdapter)localObject, this);
    return paramBundle.create();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\drawer\ProfileSelectDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */