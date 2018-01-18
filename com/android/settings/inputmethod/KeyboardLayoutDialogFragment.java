package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.hardware.input.KeyboardLayout;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;

public class KeyboardLayoutDialogFragment
  extends DialogFragment
  implements InputManager.InputDeviceListener, LoaderManager.LoaderCallbacks<Keyboards>
{
  private static final String KEY_INPUT_DEVICE_IDENTIFIER = "inputDeviceIdentifier";
  private KeyboardLayoutAdapter mAdapter;
  private boolean mHasShownLayoutSelectionScreen;
  private InputManager mIm;
  private int mInputDeviceId = -1;
  private InputDeviceIdentifier mInputDeviceIdentifier;
  
  public KeyboardLayoutDialogFragment() {}
  
  public KeyboardLayoutDialogFragment(InputDeviceIdentifier paramInputDeviceIdentifier)
  {
    this.mInputDeviceIdentifier = paramInputDeviceIdentifier;
  }
  
  private void onKeyboardLayoutClicked(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.mAdapter.getCount()))
    {
      KeyboardLayout localKeyboardLayout = (KeyboardLayout)this.mAdapter.getItem(paramInt);
      if (localKeyboardLayout != null) {
        this.mIm.setCurrentKeyboardLayoutForInputDevice(this.mInputDeviceIdentifier, localKeyboardLayout.getDescriptor());
      }
      dismiss();
    }
  }
  
  private void onSetupLayoutsButtonClicked()
  {
    ((OnSetupKeyboardLayoutsListener)getTargetFragment()).onSetupKeyboardLayouts(this.mInputDeviceIdentifier);
  }
  
  private void showSetupKeyboardLayoutsIfNecessary()
  {
    if (((AlertDialog)getDialog() == null) || (this.mAdapter.getCount() != 1) || (this.mAdapter.getItem(0) != null) || (this.mHasShownLayoutSelectionScreen)) {
      return;
    }
    this.mHasShownLayoutSelectionScreen = true;
    ((OnSetupKeyboardLayoutsListener)getTargetFragment()).onSetupKeyboardLayouts(this.mInputDeviceIdentifier);
  }
  
  private void updateSwitchHintVisibility()
  {
    Object localObject = (AlertDialog)getDialog();
    if (localObject != null)
    {
      localObject = ((AlertDialog)localObject).findViewById(16909100);
      if (this.mAdapter.getCount() <= 1) {
        break label38;
      }
    }
    label38:
    for (int i = 0;; i = 8)
    {
      ((View)localObject).setVisibility(i);
      return;
    }
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    show(getActivity().getFragmentManager(), "layout");
  }
  
  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    paramActivity = paramActivity.getBaseContext();
    this.mIm = ((InputManager)paramActivity.getSystemService("input"));
    this.mAdapter = new KeyboardLayoutAdapter(paramActivity);
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    super.onCancel(paramDialogInterface);
    dismiss();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null) {
      this.mInputDeviceIdentifier = ((InputDeviceIdentifier)paramBundle.getParcelable("inputDeviceIdentifier"));
    }
    getLoaderManager().initLoader(0, null, this);
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    paramBundle = getActivity();
    LayoutInflater localLayoutInflater = LayoutInflater.from(paramBundle);
    paramBundle = new AlertDialog.Builder(paramBundle).setTitle(2131692258).setPositiveButton(2131692259, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        KeyboardLayoutDialogFragment.-wrap1(KeyboardLayoutDialogFragment.this);
      }
    }).setSingleChoiceItems(this.mAdapter, -1, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        KeyboardLayoutDialogFragment.-wrap0(KeyboardLayoutDialogFragment.this, paramAnonymousInt);
      }
    }).setView(localLayoutInflater.inflate(2130968733, null));
    updateSwitchHintVisibility();
    return paramBundle.create();
  }
  
  public Loader<Keyboards> onCreateLoader(int paramInt, Bundle paramBundle)
  {
    return new KeyboardLayoutLoader(getActivity().getBaseContext(), this.mInputDeviceIdentifier);
  }
  
  public void onInputDeviceAdded(int paramInt) {}
  
  public void onInputDeviceChanged(int paramInt)
  {
    if ((this.mInputDeviceId >= 0) && (paramInt == this.mInputDeviceId)) {
      getLoaderManager().restartLoader(0, null, this);
    }
  }
  
  public void onInputDeviceRemoved(int paramInt)
  {
    if ((this.mInputDeviceId >= 0) && (paramInt == this.mInputDeviceId)) {
      dismiss();
    }
  }
  
  public void onLoadFinished(Loader<Keyboards> paramLoader, Keyboards paramKeyboards)
  {
    this.mAdapter.clear();
    this.mAdapter.addAll(paramKeyboards.keyboardLayouts);
    this.mAdapter.setCheckedItem(paramKeyboards.current);
    paramLoader = (AlertDialog)getDialog();
    if (paramLoader != null) {
      paramLoader.getListView().setItemChecked(paramKeyboards.current, true);
    }
    updateSwitchHintVisibility();
    showSetupKeyboardLayoutsIfNecessary();
  }
  
  public void onLoaderReset(Loader<Keyboards> paramLoader)
  {
    this.mAdapter.clear();
    updateSwitchHintVisibility();
  }
  
  public void onPause()
  {
    this.mIm.unregisterInputDeviceListener(this);
    this.mInputDeviceId = -1;
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    this.mIm.registerInputDeviceListener(this, null);
    InputDevice localInputDevice = this.mIm.getInputDeviceByDescriptor(this.mInputDeviceIdentifier.getDescriptor());
    if (localInputDevice == null)
    {
      dismiss();
      return;
    }
    this.mInputDeviceId = localInputDevice.getId();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putParcelable("inputDeviceIdentifier", this.mInputDeviceIdentifier);
  }
  
  private static final class KeyboardLayoutAdapter
    extends ArrayAdapter<KeyboardLayout>
  {
    private int mCheckedItem = -1;
    private final LayoutInflater mInflater;
    
    public KeyboardLayoutAdapter(Context paramContext)
    {
      super(17367272);
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
    }
    
    private View inflateOneLine(View paramView, ViewGroup paramViewGroup, String paramString, boolean paramBoolean)
    {
      View localView = paramView;
      if ((paramView == null) || (isTwoLine(paramView)))
      {
        localView = this.mInflater.inflate(17367055, paramViewGroup, false);
        setTwoLine(localView, false);
      }
      paramView = (CheckedTextView)localView.findViewById(16908308);
      paramView.setText(paramString);
      paramView.setChecked(paramBoolean);
      return localView;
    }
    
    private View inflateTwoLine(View paramView, ViewGroup paramViewGroup, String paramString1, String paramString2, boolean paramBoolean)
    {
      Object localObject = paramView;
      if ((paramView != null) && (isTwoLine(paramView))) {
        paramView = (View)localObject;
      }
      for (;;)
      {
        paramViewGroup = (TextView)paramView.findViewById(16908308);
        localObject = (TextView)paramView.findViewById(16908309);
        RadioButton localRadioButton = (RadioButton)paramView.findViewById(16909199);
        paramViewGroup.setText(paramString1);
        ((TextView)localObject).setText(paramString2);
        localRadioButton.setChecked(paramBoolean);
        return paramView;
        paramView = this.mInflater.inflate(17367272, paramViewGroup, false);
        setTwoLine(paramView, true);
      }
    }
    
    private static boolean isTwoLine(View paramView)
    {
      return paramView.getTag() == Boolean.TRUE;
    }
    
    private static void setTwoLine(View paramView, boolean paramBoolean)
    {
      paramView.setTag(Boolean.valueOf(paramBoolean));
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      Object localObject = (KeyboardLayout)getItem(paramInt);
      String str;
      if (localObject != null)
      {
        str = ((KeyboardLayout)localObject).getLabel();
        localObject = ((KeyboardLayout)localObject).getCollection();
        if (paramInt != this.mCheckedItem) {
          break label77;
        }
      }
      label77:
      for (boolean bool = true;; bool = false)
      {
        if (!((String)localObject).isEmpty()) {
          break label83;
        }
        return inflateOneLine(paramView, paramViewGroup, str, bool);
        str = getContext().getString(2131692261);
        localObject = "";
        break;
      }
      label83:
      return inflateTwoLine(paramView, paramViewGroup, str, (String)localObject, bool);
    }
    
    public void setCheckedItem(int paramInt)
    {
      this.mCheckedItem = paramInt;
    }
  }
  
  private static final class KeyboardLayoutLoader
    extends AsyncTaskLoader<KeyboardLayoutDialogFragment.Keyboards>
  {
    private final InputDeviceIdentifier mInputDeviceIdentifier;
    
    public KeyboardLayoutLoader(Context paramContext, InputDeviceIdentifier paramInputDeviceIdentifier)
    {
      super();
      this.mInputDeviceIdentifier = paramInputDeviceIdentifier;
    }
    
    public KeyboardLayoutDialogFragment.Keyboards loadInBackground()
    {
      KeyboardLayoutDialogFragment.Keyboards localKeyboards = new KeyboardLayoutDialogFragment.Keyboards();
      Object localObject = (InputManager)getContext().getSystemService("input");
      String[] arrayOfString = ((InputManager)localObject).getEnabledKeyboardLayoutsForInputDevice(this.mInputDeviceIdentifier);
      int j = arrayOfString.length;
      int i = 0;
      while (i < j)
      {
        KeyboardLayout localKeyboardLayout = ((InputManager)localObject).getKeyboardLayout(arrayOfString[i]);
        if (localKeyboardLayout != null) {
          localKeyboards.keyboardLayouts.add(localKeyboardLayout);
        }
        i += 1;
      }
      Collections.sort(localKeyboards.keyboardLayouts);
      localObject = ((InputManager)localObject).getCurrentKeyboardLayoutForInputDevice(this.mInputDeviceIdentifier);
      if (localObject != null)
      {
        j = localKeyboards.keyboardLayouts.size();
        i = 0;
      }
      for (;;)
      {
        if (i < j)
        {
          if (((KeyboardLayout)localKeyboards.keyboardLayouts.get(i)).getDescriptor().equals(localObject)) {
            localKeyboards.current = i;
          }
        }
        else
        {
          if (localKeyboards.keyboardLayouts.isEmpty())
          {
            localKeyboards.keyboardLayouts.add(null);
            localKeyboards.current = 0;
          }
          return localKeyboards;
        }
        i += 1;
      }
    }
    
    protected void onStartLoading()
    {
      super.onStartLoading();
      forceLoad();
    }
    
    protected void onStopLoading()
    {
      super.onStopLoading();
      cancelLoad();
    }
  }
  
  public static final class Keyboards
  {
    public int current = -1;
    public final ArrayList<KeyboardLayout> keyboardLayouts = new ArrayList();
  }
  
  public static abstract interface OnSetupKeyboardLayoutsListener
  {
    public abstract void onSetupKeyboardLayouts(InputDeviceIdentifier paramInputDeviceIdentifier);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\KeyboardLayoutDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */