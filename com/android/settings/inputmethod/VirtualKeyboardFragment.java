package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.util.Preconditions;
import com.android.settings.SettingsPreferenceFragment;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class VirtualKeyboardFragment
  extends SettingsPreferenceFragment
{
  private static final String ADD_VIRTUAL_KEYBOARD_SCREEN = "add_virtual_keyboard_screen";
  private static final Drawable NO_ICON = new ColorDrawable(0);
  private Preference mAddVirtualKeyboardScreen;
  private DevicePolicyManager mDpm;
  private InputMethodManager mImm;
  private final ArrayList<InputMethodPreference> mInputMethodPreferenceList = new ArrayList();
  
  private void updateInputMethodPreferenceViews()
  {
    this.mInputMethodPreferenceList.clear();
    List localList1 = this.mDpm.getPermittedInputMethodsForCurrentUser();
    Context localContext = getPrefContext();
    List localList2 = this.mImm.getEnabledInputMethodList();
    int i;
    if (localList2 == null) {
      i = 0;
    }
    for (;;)
    {
      j = 0;
      label40:
      if (j < i)
      {
        Object localObject2 = (InputMethodInfo)localList2.get(j);
        boolean bool;
        if (localList1 != null) {
          bool = localList1.contains(((InputMethodInfo)localObject2).getPackageName());
        }
        try
        {
          for (;;)
          {
            Drawable localDrawable = getActivity().getPackageManager().getApplicationIcon(((InputMethodInfo)localObject2).getPackageName());
            localObject2 = new InputMethodPreference(localContext, (InputMethodInfo)localObject2, false, bool, null);
            ((InputMethodPreference)localObject2).setIcon(localDrawable);
            this.mInputMethodPreferenceList.add(localObject2);
            j += 1;
            break label40;
            i = localList2.size();
            break;
            bool = true;
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            localObject1 = NO_ICON;
          }
        }
      }
    }
    final Object localObject1 = Collator.getInstance();
    Collections.sort(this.mInputMethodPreferenceList, new Comparator()
    {
      public int compare(InputMethodPreference paramAnonymousInputMethodPreference1, InputMethodPreference paramAnonymousInputMethodPreference2)
      {
        return paramAnonymousInputMethodPreference1.compareTo(paramAnonymousInputMethodPreference2, localObject1);
      }
    });
    getPreferenceScreen().removeAll();
    int j = 0;
    while (j < i)
    {
      localObject1 = (InputMethodPreference)this.mInputMethodPreferenceList.get(j);
      ((InputMethodPreference)localObject1).setOrder(j);
      getPreferenceScreen().addPreference((Preference)localObject1);
      InputMethodAndSubtypeUtil.removeUnnecessaryNonPersistentPreference((Preference)localObject1);
      ((InputMethodPreference)localObject1).updatePreferenceViews();
      j += 1;
    }
    this.mAddVirtualKeyboardScreen.setIcon(2130837930);
    this.mAddVirtualKeyboardScreen.setOrder(i);
    getPreferenceScreen().addPreference(this.mAddVirtualKeyboardScreen);
  }
  
  protected int getMetricsCategory()
  {
    return 345;
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString)
  {
    paramBundle = (Activity)Preconditions.checkNotNull(getActivity());
    addPreferencesFromResource(2131230877);
    this.mImm = ((InputMethodManager)Preconditions.checkNotNull((InputMethodManager)paramBundle.getSystemService(InputMethodManager.class)));
    this.mDpm = ((DevicePolicyManager)Preconditions.checkNotNull((DevicePolicyManager)paramBundle.getSystemService(DevicePolicyManager.class)));
    this.mAddVirtualKeyboardScreen = ((Preference)Preconditions.checkNotNull(findPreference("add_virtual_keyboard_screen")));
  }
  
  public void onResume()
  {
    super.onResume();
    updateInputMethodPreferenceViews();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\VirtualKeyboardFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */