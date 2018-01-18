package com.android.settings.inputmethod;

import android.app.Activity;
import android.content.Intent;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.hardware.input.KeyboardLayout;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.view.InputDevice;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class KeyboardLayoutPickerFragment
  extends SettingsPreferenceFragment
  implements InputManager.InputDeviceListener
{
  public static final String EXTRA_INPUT_DEVICE_IDENTIFIER = "input_device_identifier";
  private InputManager mIm;
  private int mInputDeviceId = -1;
  private InputDeviceIdentifier mInputDeviceIdentifier;
  private KeyboardLayout[] mKeyboardLayouts;
  private HashMap<CheckBoxPreference, KeyboardLayout> mPreferenceMap = new HashMap();
  
  private PreferenceScreen createPreferenceHierarchy()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
    getActivity();
    KeyboardLayout[] arrayOfKeyboardLayout = this.mKeyboardLayouts;
    int i = 0;
    int j = arrayOfKeyboardLayout.length;
    while (i < j)
    {
      KeyboardLayout localKeyboardLayout = arrayOfKeyboardLayout[i];
      CheckBoxPreference localCheckBoxPreference = new CheckBoxPreference(getPrefContext());
      localCheckBoxPreference.setTitle(localKeyboardLayout.getLabel());
      localCheckBoxPreference.setSummary(localKeyboardLayout.getCollection());
      localPreferenceScreen.addPreference(localCheckBoxPreference);
      this.mPreferenceMap.put(localCheckBoxPreference, localKeyboardLayout);
      i += 1;
    }
    return localPreferenceScreen;
  }
  
  private void updateCheckedState()
  {
    String[] arrayOfString = this.mIm.getEnabledKeyboardLayoutsForInputDevice(this.mInputDeviceIdentifier);
    Arrays.sort(arrayOfString);
    Iterator localIterator = this.mPreferenceMap.entrySet().iterator();
    if (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      CheckBoxPreference localCheckBoxPreference = (CheckBoxPreference)localEntry.getKey();
      if (Arrays.binarySearch(arrayOfString, ((KeyboardLayout)localEntry.getValue()).getDescriptor()) >= 0) {}
      for (boolean bool = true;; bool = false)
      {
        localCheckBoxPreference.setChecked(bool);
        break;
      }
    }
  }
  
  protected int getMetricsCategory()
  {
    return 58;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mInputDeviceIdentifier = ((InputDeviceIdentifier)getActivity().getIntent().getParcelableExtra("input_device_identifier"));
    if (this.mInputDeviceIdentifier == null) {
      getActivity().finish();
    }
    this.mIm = ((InputManager)getSystemService("input"));
    this.mKeyboardLayouts = this.mIm.getKeyboardLayoutsForInputDevice(this.mInputDeviceIdentifier);
    Arrays.sort(this.mKeyboardLayouts);
    setPreferenceScreen(createPreferenceHierarchy());
  }
  
  public void onInputDeviceAdded(int paramInt) {}
  
  public void onInputDeviceChanged(int paramInt)
  {
    if ((this.mInputDeviceId >= 0) && (paramInt == this.mInputDeviceId)) {
      updateCheckedState();
    }
  }
  
  public void onInputDeviceRemoved(int paramInt)
  {
    if ((this.mInputDeviceId >= 0) && (paramInt == this.mInputDeviceId)) {
      getActivity().finish();
    }
  }
  
  public void onPause()
  {
    this.mIm.unregisterInputDeviceListener(this);
    this.mInputDeviceId = -1;
    super.onPause();
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if ((paramPreference instanceof CheckBoxPreference))
    {
      CheckBoxPreference localCheckBoxPreference = (CheckBoxPreference)paramPreference;
      KeyboardLayout localKeyboardLayout = (KeyboardLayout)this.mPreferenceMap.get(localCheckBoxPreference);
      if (localKeyboardLayout != null)
      {
        if (localCheckBoxPreference.isChecked()) {
          this.mIm.addKeyboardLayoutForInputDevice(this.mInputDeviceIdentifier, localKeyboardLayout.getDescriptor());
        }
        for (;;)
        {
          return true;
          this.mIm.removeKeyboardLayoutForInputDevice(this.mInputDeviceIdentifier, localKeyboardLayout.getDescriptor());
        }
      }
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onResume()
  {
    super.onResume();
    this.mIm.registerInputDeviceListener(this, null);
    InputDevice localInputDevice = this.mIm.getInputDeviceByDescriptor(this.mInputDeviceIdentifier.getDescriptor());
    if (localInputDevice == null)
    {
      getActivity().finish();
      return;
    }
    this.mInputDeviceId = localInputDevice.getId();
    updateCheckedState();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\KeyboardLayoutPickerFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */