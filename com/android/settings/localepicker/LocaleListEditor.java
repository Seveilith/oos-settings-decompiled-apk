package com.android.settings.localepicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocalePickerWithRegion;
import com.android.internal.app.LocalePickerWithRegion.LocaleSelectedListener;
import com.android.internal.app.LocaleStore;
import com.android.internal.app.LocaleStore.LocaleInfo;
import com.android.settings.SettingsPreferenceFragment;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Locale;

public class LocaleListEditor
  extends SettingsPreferenceFragment
  implements LocalePickerWithRegion.LocaleSelectedListener
{
  private static final String CFGKEY_REMOVE_DIALOG = "showingLocaleRemoveDialog";
  private static final String CFGKEY_REMOVE_MODE = "localeRemoveMode";
  private static final int MENU_ID_REMOVE = 2;
  private LocaleDragAndDropAdapter mAdapter;
  private View mAddLanguage;
  private Menu mMenu;
  private boolean mRemoveMode;
  private boolean mShowingRemoveDialog;
  
  private void configureDragAndDrop(View paramView)
  {
    paramView = (RecyclerView)paramView.findViewById(2131362194);
    LocaleLinearLayoutManager localLocaleLinearLayoutManager = new LocaleLinearLayoutManager(getContext(), this.mAdapter);
    localLocaleLinearLayoutManager.setAutoMeasureEnabled(true);
    paramView.setLayoutManager(localLocaleLinearLayoutManager);
    paramView.setHasFixedSize(true);
    this.mAdapter.setRecyclerView(paramView);
    paramView.setAdapter(this.mAdapter);
  }
  
  private static List<LocaleStore.LocaleInfo> getUserLocaleList(Context paramContext)
  {
    ArrayList localArrayList = new ArrayList();
    LocaleList localLocaleList = LocalePicker.getLocales();
    int i = 0;
    while (i < localLocaleList.size())
    {
      Locale localLocale = localLocaleList.get(i);
      paramContext = localLocale;
      if ("zh-CN".equals(LocaleStore.getLocaleInfo(localLocale).getId())) {
        paramContext = Locale.forLanguageTag("zh-Hans-CN");
      }
      localArrayList.add(LocaleStore.getLocaleInfo(paramContext));
      i += 1;
    }
    return localArrayList;
  }
  
  private void setRemoveMode(boolean paramBoolean)
  {
    this.mRemoveMode = paramBoolean;
    this.mAdapter.setRemoveMode(paramBoolean);
    getFragmentManager().invalidateOptionsMenu();
  }
  
  private void showRemoveLocaleWarningDialog()
  {
    boolean bool = false;
    int i = this.mAdapter.getCheckedCount();
    if (i == 0)
    {
      if (this.mRemoveMode) {}
      for (;;)
      {
        setRemoveMode(bool);
        return;
        bool = true;
      }
    }
    if (i == this.mAdapter.getItemCount())
    {
      this.mShowingRemoveDialog = true;
      new AlertDialog.Builder(getActivity()).setTitle(2131690978).setMessage(2131690979).setPositiveButton(17039379, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
      }).setOnDismissListener(new DialogInterface.OnDismissListener()
      {
        public void onDismiss(DialogInterface paramAnonymousDialogInterface)
        {
          LocaleListEditor.-set1(LocaleListEditor.this, false);
        }
      }).create().show();
      return;
    }
    String str = getResources().getQuantityString(2131951619, i);
    this.mShowingRemoveDialog = true;
    new AlertDialog.Builder(getActivity()).setTitle(str).setMessage(2131690977).setNegativeButton(17039369, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        LocaleListEditor.-wrap0(LocaleListEditor.this, false);
      }
    }).setPositiveButton(17039379, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        LocaleListEditor.-set0(LocaleListEditor.this, false);
        LocaleListEditor.-set1(LocaleListEditor.this, false);
        LocaleListEditor.-get0(LocaleListEditor.this).removeChecked();
        LocaleListEditor.-wrap0(LocaleListEditor.this, false);
      }
    }).setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        LocaleListEditor.-set1(LocaleListEditor.this, false);
      }
    }).create().show();
  }
  
  private void updateVisibilityOfRemoveMenu()
  {
    if (this.mMenu == null) {
      return;
    }
    MenuItem localMenuItem = this.mMenu.findItem(2);
    int i;
    boolean bool;
    if (localMenuItem != null)
    {
      if (!this.mRemoveMode) {
        break label68;
      }
      i = 2;
      localMenuItem.setShowAsAction(i);
      if (!this.mRemoveMode) {
        break label78;
      }
      if (this.mAdapter.getItemCount() <= 1) {
        break label73;
      }
      bool = true;
    }
    for (;;)
    {
      localMenuItem.setVisible(bool);
      return;
      label68:
      i = 0;
      break;
      label73:
      bool = false;
      continue;
      label78:
      if (this.mAdapter.getItemCount() > 2) {
        bool = true;
      } else {
        bool = false;
      }
    }
  }
  
  protected int getMetricsCategory()
  {
    return 344;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setHasOptionsMenu(true);
    LocaleStore.fillCache(getContext());
    paramBundle = getUserLocaleList(getContext());
    this.mAdapter = new LocaleDragAndDropAdapter(getContext(), paramBundle, new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        try
        {
          paramAnonymousView = LocalePickerWithRegion.createLanguagePicker(LocaleListEditor.this.getContext(), LocaleListEditor.this, true);
          LocaleListEditor.this.getFragmentManager().beginTransaction().setTransition(4097).replace(LocaleListEditor.this.getId(), paramAnonymousView).addToBackStack("localeListEditor").commit();
          return;
        }
        catch (ConcurrentModificationException paramAnonymousView) {}
      }
    });
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    MenuItem localMenuItem = paramMenu.add(0, 2, 0, 2131690975);
    localMenuItem.setShowAsAction(4);
    localMenuItem.setIcon(2130837970);
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    this.mMenu = paramMenu;
    updateVisibilityOfRemoveMenu();
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramViewGroup = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    paramLayoutInflater = paramLayoutInflater.inflate(2130968741, (ViewGroup)paramViewGroup);
    getActivity().setTitle(2131690974);
    configureDragAndDrop(paramLayoutInflater);
    return paramViewGroup;
  }
  
  public void onLocaleSelected(LocaleStore.LocaleInfo paramLocaleInfo)
  {
    this.mAdapter.addLocale(paramLocaleInfo);
    paramLocaleInfo = getFragmentManager();
    if (paramLocaleInfo != null) {
      paramLocaleInfo.invalidateOptionsMenu();
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    }
    do
    {
      return super.onOptionsItemSelected(paramMenuItem);
      if (this.mRemoveMode)
      {
        showRemoveLocaleWarningDialog();
        return true;
      }
      setRemoveMode(true);
      return true;
    } while (!this.mRemoveMode);
    setRemoveMode(false);
    return true;
  }
  
  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    updateVisibilityOfRemoveMenu();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean("localeRemoveMode", this.mRemoveMode);
    paramBundle.putBoolean("showingLocaleRemoveDialog", this.mShowingRemoveDialog);
    this.mAdapter.saveState(paramBundle);
  }
  
  public void onViewStateRestored(Bundle paramBundle)
  {
    super.onViewStateRestored(paramBundle);
    if (paramBundle != null)
    {
      this.mRemoveMode = paramBundle.getBoolean("localeRemoveMode", false);
      this.mShowingRemoveDialog = paramBundle.getBoolean("showingLocaleRemoveDialog", false);
    }
    setRemoveMode(this.mRemoveMode);
    this.mAdapter.restoreState(paramBundle);
    if (this.mShowingRemoveDialog) {
      showRemoveLocaleWarningDialog();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\localepicker\LocaleListEditor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */