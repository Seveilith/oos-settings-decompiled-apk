package com.android.settings.inputmethod;

import android.app.Activity;
import android.content.Intent;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.hardware.input.KeyboardLayout;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.view.InputDevice;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.util.Preconditions;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class KeyboardLayoutPickerFragment2
  extends SettingsPreferenceFragment
  implements InputManager.InputDeviceListener
{
  public static final String EXTRA_INPUT_DEVICE_IDENTIFIER = "input_device_identifier";
  public static final String EXTRA_INPUT_METHOD_INFO = "input_method_info";
  public static final String EXTRA_INPUT_METHOD_SUBTYPE = "input_method_subtype";
  private InputManager mIm;
  private InputMethodInfo mImi;
  private int mInputDeviceId = -1;
  private InputDeviceIdentifier mInputDeviceIdentifier;
  private KeyboardLayout[] mKeyboardLayouts;
  private Map<Preference, KeyboardLayout> mPreferenceMap = new HashMap();
  private InputMethodSubtype mSubtype;
  
  private PreferenceScreen createPreferenceHierarchy()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
    KeyboardLayout[] arrayOfKeyboardLayout = this.mKeyboardLayouts;
    int i = 0;
    int j = arrayOfKeyboardLayout.length;
    while (i < j)
    {
      KeyboardLayout localKeyboardLayout = arrayOfKeyboardLayout[i];
      Preference localPreference = new Preference(getPrefContext());
      localPreference.setTitle(localKeyboardLayout.getLabel());
      localPreference.setSummary(localKeyboardLayout.getCollection());
      localPreferenceScreen.addPreference(localPreference);
      this.mPreferenceMap.put(localPreference, localKeyboardLayout);
      i += 1;
    }
    localPreferenceScreen.setTitle(PhysicalKeyboardFragment.KeyboardInfoPreference.getDisplayName(getContext(), this.mImi, this.mSubtype));
    return localPreferenceScreen;
  }
  
  protected int getMetricsCategory()
  {
    return 58;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = (Activity)Preconditions.checkNotNull(getActivity());
    this.mInputDeviceIdentifier = ((InputDeviceIdentifier)paramBundle.getIntent().getParcelableExtra("input_device_identifier"));
    this.mImi = ((InputMethodInfo)paramBundle.getIntent().getParcelableExtra("input_method_info"));
    this.mSubtype = ((InputMethodSubtype)paramBundle.getIntent().getParcelableExtra("input_method_subtype"));
    if ((this.mInputDeviceIdentifier == null) || (this.mImi == null)) {
      paramBundle.finish();
    }
    this.mIm = ((InputManager)paramBundle.getSystemService(InputManager.class));
    this.mKeyboardLayouts = this.mIm.getKeyboardLayoutsForInputDevice(this.mInputDeviceIdentifier);
    Arrays.sort(this.mKeyboardLayouts);
    setPreferenceScreen(createPreferenceHierarchy());
  }
  
  public void onInputDeviceAdded(int paramInt) {}
  
  public void onInputDeviceChanged(int paramInt) {}
  
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
    KeyboardLayout localKeyboardLayout = (KeyboardLayout)this.mPreferenceMap.get(paramPreference);
    if (localKeyboardLayout != null)
    {
      this.mIm.setKeyboardLayoutForInputDevice(this.mInputDeviceIdentifier, this.mImi, this.mSubtype, localKeyboardLayout.getDescriptor());
      getActivity().finish();
      return true;
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
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\KeyboardLayoutPickerFragment2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */