package com.android.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.UserDictionary.Words;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import com.android.settings.inputmethod.UserDictionaryAddWordFragment;
import com.android.settings.inputmethod.UserDictionarySettingsUtils;
import java.util.Locale;

public class UserDictionarySettings
  extends ListFragment
{
  private static final String DELETE_SELECTION_WITHOUT_SHORTCUT = "word=? AND shortcut is null OR shortcut=''";
  private static final String DELETE_SELECTION_WITH_SHORTCUT = "word=? AND shortcut=?";
  private static final int INDEX_SHORTCUT = 2;
  private static final int OPTIONS_MENU_ADD = 1;
  private static final String[] QUERY_PROJECTION = { "_id", "word", "shortcut" };
  private static final String QUERY_SELECTION = "locale=?";
  private static final String QUERY_SELECTION_ALL_LOCALES = "locale is null";
  private static final String TAG = "UserDictionarySettings";
  private Cursor mCursor;
  protected String mLocale;
  
  private ListAdapter createAdapter()
  {
    return new MyAdapter(getActivity(), 2130969099, this.mCursor, new String[] { "word", "shortcut" }, new int[] { 16908308, 16908309 }, this);
  }
  
  private Cursor createCursor(String paramString)
  {
    if ("".equals(paramString)) {
      return getActivity().managedQuery(UserDictionary.Words.CONTENT_URI, QUERY_PROJECTION, "locale is null", null, "UPPER(word)");
    }
    if (paramString != null) {}
    for (;;)
    {
      return getActivity().managedQuery(UserDictionary.Words.CONTENT_URI, QUERY_PROJECTION, "locale=?", new String[] { paramString }, "UPPER(word)");
      paramString = Locale.getDefault().toString();
    }
  }
  
  public static void deleteWord(String paramString1, String paramString2, ContentResolver paramContentResolver)
  {
    if (TextUtils.isEmpty(paramString2))
    {
      paramContentResolver.delete(UserDictionary.Words.CONTENT_URI, "word=? AND shortcut is null OR shortcut=''", new String[] { paramString1 });
      return;
    }
    paramContentResolver.delete(UserDictionary.Words.CONTENT_URI, "word=? AND shortcut=?", new String[] { paramString1, paramString2 });
  }
  
  private String getShortcut(int paramInt)
  {
    if (this.mCursor == null) {
      return null;
    }
    this.mCursor.moveToPosition(paramInt);
    if (this.mCursor.isAfterLast()) {
      return null;
    }
    return this.mCursor.getString(this.mCursor.getColumnIndexOrThrow("shortcut"));
  }
  
  private String getWord(int paramInt)
  {
    if (this.mCursor == null) {
      return null;
    }
    this.mCursor.moveToPosition(paramInt);
    if (this.mCursor.isAfterLast()) {
      return null;
    }
    return this.mCursor.getString(this.mCursor.getColumnIndexOrThrow("word"));
  }
  
  private void showAddOrEditDialog(String paramString1, String paramString2)
  {
    Bundle localBundle = new Bundle();
    if (paramString1 == null) {}
    for (int i = 1;; i = 0)
    {
      localBundle.putInt("mode", i);
      localBundle.putString("word", paramString1);
      localBundle.putString("shortcut", paramString2);
      localBundle.putString("locale", this.mLocale);
      ((SettingsActivity)getActivity()).startPreferencePanel(UserDictionaryAddWordFragment.class.getName(), localBundle, 2131692266, null, null, 0);
      return;
    }
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    Object localObject = null;
    super.onActivityCreated(paramBundle);
    getActivity().getActionBar().setTitle(2131692263);
    paramBundle = getActivity().getIntent();
    Bundle localBundle;
    if (paramBundle == null)
    {
      paramBundle = null;
      localBundle = getArguments();
      if (localBundle != null) {
        break label145;
      }
      label42:
      if (localObject == null) {
        break label155;
      }
      paramBundle = (Bundle)localObject;
    }
    for (;;)
    {
      this.mLocale = paramBundle;
      this.mCursor = createCursor(paramBundle);
      paramBundle = (TextView)getView().findViewById(16908292);
      if (paramBundle != null) {
        paramBundle.setText(2131692279);
      }
      localObject = getListView();
      ((ListView)localObject).setAdapter(createAdapter());
      ((ListView)localObject).setFastScrollEnabled(true);
      ((ListView)localObject).setEmptyView(paramBundle);
      setHasOptionsMenu(true);
      getActivity().getActionBar().setSubtitle(UserDictionarySettingsUtils.getLocaleDisplayName(getActivity(), this.mLocale));
      return;
      paramBundle = paramBundle.getStringExtra("locale");
      break;
      label145:
      localObject = localBundle.getString("locale");
      break label42;
      label155:
      if (paramBundle == null) {
        paramBundle = null;
      }
    }
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 1, 0, 2131692265).setIcon(2130838206).setShowAsAction(5);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return paramLayoutInflater.inflate(17367225, paramViewGroup, false);
  }
  
  public void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    paramListView = getWord(paramInt);
    paramView = getShortcut(paramInt);
    if (paramListView != null) {
      showAddOrEditDialog(paramListView, paramView);
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 1)
    {
      showAddOrEditDialog(null, null);
      return true;
    }
    return false;
  }
  
  private static class MyAdapter
    extends SimpleCursorAdapter
    implements SectionIndexer
  {
    private AlphabetIndexer mIndexer;
    private final SimpleCursorAdapter.ViewBinder mViewBinder = new SimpleCursorAdapter.ViewBinder()
    {
      public boolean setViewValue(View paramAnonymousView, Cursor paramAnonymousCursor, int paramAnonymousInt)
      {
        if (paramAnonymousInt == 2)
        {
          paramAnonymousCursor = paramAnonymousCursor.getString(2);
          if (TextUtils.isEmpty(paramAnonymousCursor)) {
            paramAnonymousView.setVisibility(8);
          }
          for (;;)
          {
            paramAnonymousView.invalidate();
            return true;
            ((TextView)paramAnonymousView).setText(paramAnonymousCursor);
            paramAnonymousView.setVisibility(0);
          }
        }
        return false;
      }
    };
    
    public MyAdapter(Context paramContext, int paramInt, Cursor paramCursor, String[] paramArrayOfString, int[] paramArrayOfInt, UserDictionarySettings paramUserDictionarySettings)
    {
      super(paramInt, paramCursor, paramArrayOfString, paramArrayOfInt);
      if (paramCursor != null)
      {
        paramContext = paramContext.getString(17040447);
        this.mIndexer = new AlphabetIndexer(paramCursor, paramCursor.getColumnIndexOrThrow("word"), paramContext);
      }
      setViewBinder(this.mViewBinder);
    }
    
    public int getPositionForSection(int paramInt)
    {
      if (this.mIndexer == null) {
        return 0;
      }
      return this.mIndexer.getPositionForSection(paramInt);
    }
    
    public int getSectionForPosition(int paramInt)
    {
      if (this.mIndexer == null) {
        return 0;
      }
      return this.mIndexer.getSectionForPosition(paramInt);
    }
    
    public Object[] getSections()
    {
      if (this.mIndexer == null) {
        return null;
      }
      return this.mIndexer.getSections();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\UserDictionarySettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */