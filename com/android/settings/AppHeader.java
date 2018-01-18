package com.android.settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.InstalledAppDetails;

public class AppHeader
{
  public static final String EXTRA_HIDE_INFO_BUTTON = "hideInfoButton";
  private static final int INSTALLED_APP_DETAILS = 1;
  
  public static void createAppHeader(Activity paramActivity, Drawable paramDrawable, CharSequence paramCharSequence, String paramString, int paramInt, ViewGroup paramViewGroup)
  {
    View localView = paramActivity.getLayoutInflater().inflate(2130968613, paramViewGroup, false);
    setupHeaderView(paramActivity, paramDrawable, paramCharSequence, paramString, paramInt, false, 0, localView, null);
    paramViewGroup.addView(localView);
  }
  
  public static void createAppHeader(SettingsPreferenceFragment paramSettingsPreferenceFragment, Drawable paramDrawable, CharSequence paramCharSequence, String paramString, int paramInt)
  {
    createAppHeader(paramSettingsPreferenceFragment, paramDrawable, paramCharSequence, paramString, paramInt, 0, null);
  }
  
  public static void createAppHeader(SettingsPreferenceFragment paramSettingsPreferenceFragment, Drawable paramDrawable, CharSequence paramCharSequence, String paramString, int paramInt1, int paramInt2)
  {
    createAppHeader(paramSettingsPreferenceFragment, paramDrawable, paramCharSequence, paramString, paramInt1, paramInt2, null);
  }
  
  public static void createAppHeader(SettingsPreferenceFragment paramSettingsPreferenceFragment, Drawable paramDrawable, CharSequence paramCharSequence, String paramString, int paramInt1, int paramInt2, Intent paramIntent)
  {
    View localView = paramSettingsPreferenceFragment.setPinnedHeaderView(2130968613);
    setupHeaderView(paramSettingsPreferenceFragment.getActivity(), paramDrawable, paramCharSequence, paramString, paramInt1, includeAppInfo(paramSettingsPreferenceFragment), paramInt2, localView, paramIntent);
  }
  
  public static void createAppHeader(SettingsPreferenceFragment paramSettingsPreferenceFragment, Drawable paramDrawable, CharSequence paramCharSequence, String paramString, int paramInt, Intent paramIntent)
  {
    createAppHeader(paramSettingsPreferenceFragment, paramDrawable, paramCharSequence, paramString, paramInt, 0, paramIntent);
  }
  
  public static boolean includeAppInfo(Fragment paramFragment)
  {
    Bundle localBundle = paramFragment.getArguments();
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (localBundle != null)
    {
      bool1 = bool2;
      if (localBundle.getBoolean("hideInfoButton", false)) {
        bool1 = false;
      }
    }
    paramFragment = paramFragment.getActivity().getIntent();
    bool2 = bool1;
    if (paramFragment != null)
    {
      bool2 = bool1;
      if (paramFragment.getBooleanExtra("hideInfoButton", false)) {
        bool2 = false;
      }
    }
    return bool2;
  }
  
  public static View setupHeaderView(final Activity paramActivity, Drawable paramDrawable, CharSequence paramCharSequence, final String paramString, final int paramInt1, boolean paramBoolean, int paramInt2, View paramView, final Intent paramIntent)
  {
    ImageView localImageView = (ImageView)paramView.findViewById(2131361951);
    localImageView.setImageDrawable(paramDrawable);
    int i;
    if (paramDrawable != null)
    {
      localImageView.setVisibility(0);
      i = paramActivity.getResources().getDimensionPixelSize(2131755343);
      paramView.getLayoutParams().height = i;
      if (paramInt2 != 0) {
        localImageView.setImageTintList(ColorStateList.valueOf(paramActivity.getColor(paramInt2)));
      }
      ((TextView)paramView.findViewById(2131361952)).setText(paramCharSequence);
      if ((paramString != null) && (!paramString.equals("os"))) {
        break label129;
      }
    }
    label129:
    do
    {
      return paramView;
      localImageView.setVisibility(8);
      i = paramActivity.getResources().getDimensionPixelSize(2131755342);
      paramView.getLayoutParams().height = i;
      break;
      paramView.setClickable(true);
      paramView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (this.val$includeAppInfo)
          {
            AppInfoBase.startAppInfoFragment(InstalledAppDetails.class, 2131692076, paramString, paramInt1, paramActivity, 1);
            return;
          }
          paramActivity.finish();
        }
      });
    } while (paramIntent == null);
    paramDrawable = paramView.findViewById(2131361953);
    paramDrawable.setVisibility(0);
    paramDrawable.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        this.val$activity.startActivity(paramIntent);
      }
    });
    return paramView;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\AppHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */