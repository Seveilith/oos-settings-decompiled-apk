package com.android.settings.dashboard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.InstrumentedFragment;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.search.Index;
import java.util.HashMap;

public class SearchResultsSummary
  extends InstrumentedFragment
{
  private static char ELLIPSIS = '…';
  private static final String EMPTY_QUERY = "";
  private static final String LOG_TAG = "SearchResultsSummary";
  private static final String SAVE_KEY_SHOW_RESULTS = ":settings:show_results";
  private ViewGroup mLayoutResults;
  private ViewGroup mLayoutSuggestions;
  private String mQuery;
  private SearchResultsAdapter mResultsAdapter;
  private ListView mResultsListView;
  private SearchView mSearchView;
  private boolean mShowResults;
  private SuggestionsAdapter mSuggestionsAdapter;
  private ListView mSuggestionsListView;
  private UpdateSearchResultsTask mUpdateSearchResultsTask;
  private UpdateSuggestionsTask mUpdateSuggestionsTask;
  
  private void clearAllTasks()
  {
    if (this.mUpdateSearchResultsTask != null)
    {
      this.mUpdateSearchResultsTask.cancel(false);
      this.mUpdateSearchResultsTask = null;
    }
    if (this.mUpdateSuggestionsTask != null)
    {
      this.mUpdateSuggestionsTask.cancel(false);
      this.mUpdateSuggestionsTask = null;
    }
  }
  
  private void clearResults()
  {
    if (this.mUpdateSearchResultsTask != null)
    {
      this.mUpdateSearchResultsTask.cancel(false);
      this.mUpdateSearchResultsTask = null;
    }
    setResultsCursor(null);
  }
  
  private void clearSuggestions()
  {
    if (this.mUpdateSuggestionsTask != null)
    {
      this.mUpdateSuggestionsTask.cancel(false);
      this.mUpdateSuggestionsTask = null;
    }
    setSuggestionsCursor(null);
  }
  
  private String getFilteredQueryString(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < paramCharSequence.length())
    {
      char c = paramCharSequence.charAt(i);
      if ((Character.isLetterOrDigit(c)) || (Character.isSpaceChar(c))) {
        localStringBuilder.append(c);
      }
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  private void saveQueryToDatabase()
  {
    Index.getInstance(getActivity()).addSavedQuery(this.mQuery);
  }
  
  private void setResultsCursor(Cursor paramCursor)
  {
    if (this.mResultsAdapter == null) {
      return;
    }
    paramCursor = this.mResultsAdapter.swapCursor(paramCursor);
    if (paramCursor != null) {
      paramCursor.close();
    }
  }
  
  private void setResultsVisibility(boolean paramBoolean)
  {
    ViewGroup localViewGroup;
    if (this.mLayoutResults != null)
    {
      localViewGroup = this.mLayoutResults;
      if (!paramBoolean) {
        break label24;
      }
    }
    label24:
    for (int i = 0;; i = 8)
    {
      localViewGroup.setVisibility(i);
      return;
    }
  }
  
  private void setSuggestionsCursor(Cursor paramCursor)
  {
    if (this.mSuggestionsAdapter == null) {
      return;
    }
    paramCursor = this.mSuggestionsAdapter.swapCursor(paramCursor);
    if (paramCursor != null) {
      paramCursor.close();
    }
  }
  
  private void setSuggestionsVisibility(boolean paramBoolean)
  {
    ViewGroup localViewGroup;
    if (this.mLayoutSuggestions != null)
    {
      localViewGroup = this.mLayoutSuggestions;
      if (!paramBoolean) {
        break label24;
      }
    }
    label24:
    for (int i = 0;; i = 8)
    {
      localViewGroup.setVisibility(i);
      return;
    }
  }
  
  private void updateSearchResults()
  {
    clearAllTasks();
    if (TextUtils.isEmpty(this.mQuery))
    {
      setResultsVisibility(false);
      setResultsCursor(null);
      return;
    }
    this.mUpdateSearchResultsTask = new UpdateSearchResultsTask(null);
    this.mUpdateSearchResultsTask.execute(new String[] { this.mQuery });
  }
  
  private void updateSuggestions()
  {
    clearAllTasks();
    if (this.mQuery == null)
    {
      setSuggestionsCursor(null);
      return;
    }
    this.mUpdateSuggestionsTask = new UpdateSuggestionsTask(null);
    this.mUpdateSuggestionsTask.execute(new String[] { this.mQuery });
  }
  
  protected int getMetricsCategory()
  {
    return 34;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mResultsAdapter = new SearchResultsAdapter(getActivity());
    this.mSuggestionsAdapter = new SuggestionsAdapter(getActivity());
    if (paramBundle != null) {
      this.mShowResults = paramBundle.getBoolean(":settings:show_results");
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = paramLayoutInflater.inflate(2130968971, paramViewGroup, false);
    this.mLayoutSuggestions = ((ViewGroup)paramLayoutInflater.findViewById(2131362532));
    this.mLayoutResults = ((ViewGroup)paramLayoutInflater.findViewById(2131362534));
    this.mResultsListView = ((ListView)paramLayoutInflater.findViewById(2131362535));
    this.mResultsListView.setAdapter(this.mResultsAdapter);
    this.mResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        paramAnonymousInt -= 1;
        if (paramAnonymousInt < 0) {
          return;
        }
        Object localObject1 = SearchResultsSummary.SearchResultsAdapter.-get0(SearchResultsSummary.-get1(SearchResultsSummary.this));
        ((Cursor)localObject1).moveToPosition(paramAnonymousInt);
        Object localObject2 = ((Cursor)localObject1).getString(6);
        String str1 = ((Cursor)localObject1).getString(7);
        String str2 = ((Cursor)localObject1).getString(9);
        paramAnonymousAdapterView = ((Cursor)localObject1).getString(13);
        paramAnonymousView = (SettingsActivity)SearchResultsSummary.this.getActivity();
        paramAnonymousView.needToRevertToInitialFragment();
        if (TextUtils.isEmpty(str2))
        {
          localObject1 = new Bundle();
          ((Bundle)localObject1).putString(":settings:fragment_args_key", paramAnonymousAdapterView);
          Utils.startWithFragment(paramAnonymousView, (String)localObject2, (Bundle)localObject1, null, 0, -1, str1);
          SearchResultsSummary.-wrap0(SearchResultsSummary.this);
          return;
        }
        localObject2 = new Intent(str2);
        str1 = ((Cursor)localObject1).getString(10);
        localObject1 = ((Cursor)localObject1).getString(11);
        if ((TextUtils.isEmpty(str1)) || (TextUtils.isEmpty((CharSequence)localObject1))) {}
        for (;;)
        {
          ((Intent)localObject2).putExtra(":settings:fragment_args_key", paramAnonymousAdapterView);
          paramAnonymousView.startActivity((Intent)localObject2);
          break;
          ((Intent)localObject2).setComponent(new ComponentName(str1, (String)localObject1));
        }
      }
    });
    this.mResultsListView.addHeaderView(LayoutInflater.from(getActivity()).inflate(2130968972, this.mResultsListView, false), null, false);
    this.mSuggestionsListView = ((ListView)paramLayoutInflater.findViewById(2131362533));
    this.mSuggestionsListView.setAdapter(this.mSuggestionsAdapter);
    this.mSuggestionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        paramAnonymousInt -= 1;
        if (paramAnonymousInt < 0) {
          return;
        }
        paramAnonymousAdapterView = SearchResultsSummary.SuggestionsAdapter.-get0(SearchResultsSummary.-get3(SearchResultsSummary.this));
        paramAnonymousAdapterView.moveToPosition(paramAnonymousInt);
        SearchResultsSummary.-set1(SearchResultsSummary.this, true);
        SearchResultsSummary.-set0(SearchResultsSummary.this, paramAnonymousAdapterView.getString(0));
        SearchResultsSummary.-get2(SearchResultsSummary.this).setQuery(SearchResultsSummary.-get0(SearchResultsSummary.this), false);
      }
    });
    this.mSuggestionsListView.addHeaderView(LayoutInflater.from(getActivity()).inflate(2130968973, this.mSuggestionsListView, false), null, false);
    return paramLayoutInflater;
  }
  
  public void onDestroy()
  {
    clearSuggestions();
    clearResults();
    this.mResultsListView = null;
    this.mResultsAdapter = null;
    this.mUpdateSearchResultsTask = null;
    this.mSuggestionsListView = null;
    this.mSuggestionsAdapter = null;
    this.mUpdateSuggestionsTask = null;
    this.mSearchView = null;
    super.onDestroy();
  }
  
  public boolean onQueryTextChange(String paramString)
  {
    this.mQuery = getFilteredQueryString(paramString);
    if (TextUtils.isEmpty(this.mQuery))
    {
      this.mShowResults = false;
      setResultsVisibility(false);
      updateSuggestions();
      return true;
    }
    this.mShowResults = true;
    setSuggestionsVisibility(false);
    updateSearchResults();
    return true;
  }
  
  public boolean onQueryTextSubmit(String paramString)
  {
    this.mQuery = getFilteredQueryString(paramString);
    this.mShowResults = true;
    setSuggestionsVisibility(false);
    updateSearchResults();
    saveQueryToDatabase();
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    if (!this.mShowResults) {
      showSomeSuggestions();
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean(":settings:show_results", this.mShowResults);
  }
  
  public void onStop()
  {
    super.onStop();
  }
  
  public void setSearchView(SearchView paramSearchView)
  {
    this.mSearchView = paramSearchView;
  }
  
  public void showSomeSuggestions()
  {
    setResultsVisibility(false);
    this.mQuery = "";
    updateSuggestions();
  }
  
  private static class SearchResult
  {
    public Context context;
    public String entries;
    public int iconResId;
    public String key;
    public String summaryOff;
    public String summaryOn;
    public String title;
    
    public SearchResult(Context paramContext, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt, String paramString5)
    {
      this.context = paramContext;
      this.title = paramString1;
      this.summaryOn = paramString2;
      this.summaryOff = paramString3;
      this.entries = paramString4;
      this.iconResId = paramInt;
      this.key = paramString5;
    }
  }
  
  private static class SearchResultsAdapter
    extends BaseAdapter
  {
    private static final String DOLLAR_REPLACE = "$s";
    private static final String PERCENT_RECLACE = "%s";
    private Context mContext;
    private HashMap<String, Context> mContextMap = new HashMap();
    private Cursor mCursor;
    private boolean mDataValid;
    private LayoutInflater mInflater;
    
    public SearchResultsAdapter(Context paramContext)
    {
      this.mContext = paramContext;
      this.mInflater = ((LayoutInflater)this.mContext.getSystemService("layout_inflater"));
      Log.d("SearchResultsSummary", "SearchResultsAdapter mDataValid false");
      this.mDataValid = false;
    }
    
    public int getCount()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("SearchResultsAdapter !mDataValid ＝ ");
      boolean bool;
      if (this.mDataValid)
      {
        bool = false;
        Log.d("SearchResultsSummary", bool + "mCursor =  " + this.mCursor);
        if (this.mCursor != null) {
          Log.d("SearchResultsSummary", "SearchResultsAdapter mCursor.isClosed()＝ " + this.mCursor.isClosed());
        }
        if ((this.mDataValid) && (this.mCursor != null)) {
          break label109;
        }
      }
      label109:
      while (this.mCursor.isClosed())
      {
        return 0;
        bool = true;
        break;
      }
      return this.mCursor.getCount();
    }
    
    public Object getItem(int paramInt)
    {
      if ((this.mDataValid) && (this.mCursor.moveToPosition(paramInt)))
      {
        String str1 = this.mCursor.getString(1);
        String str2 = this.mCursor.getString(2);
        String str3 = this.mCursor.getString(3);
        String str4 = this.mCursor.getString(4);
        String str5 = this.mCursor.getString(8);
        Object localObject = this.mCursor.getString(6);
        String str6 = this.mCursor.getString(10);
        String str7 = this.mCursor.getString(13);
        if ((!TextUtils.isEmpty((CharSequence)localObject)) || (TextUtils.isEmpty(str6)))
        {
          localObject = this.mContext;
          if (!TextUtils.isEmpty(str5)) {
            break label243;
          }
        }
        label243:
        for (paramInt = 2130837908;; paramInt = Integer.parseInt(str5))
        {
          return new SearchResultsSummary.SearchResult((Context)localObject, str1, str2, str3, str4, paramInt, str7);
          Context localContext = (Context)this.mContextMap.get(str6);
          localObject = localContext;
          if (localContext != null) {
            break;
          }
          try
          {
            localObject = this.mContext.createPackageContext(str6, 0);
            this.mContextMap.put(str6, localObject);
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException)
          {
            Log.e("SearchResultsSummary", "Cannot create Context for package: " + str6);
            return null;
          }
        }
      }
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return 0L;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if ((!this.mDataValid) && (paramView == null)) {
        throw new IllegalStateException("this should only be called when the cursor is valid");
      }
      if (!this.mCursor.moveToPosition(paramInt)) {
        throw new IllegalStateException("couldn't move cursor to position " + paramInt);
      }
      if (paramView == null) {
        paramView = this.mInflater.inflate(2130968974, paramViewGroup, false);
      }
      for (;;)
      {
        Object localObject = (TextView)paramView.findViewById(2131361894);
        ImageView localImageView = (ImageView)paramView.findViewById(2131361793);
        paramViewGroup = (SearchResultsSummary.SearchResult)getItem(paramInt);
        ((TextView)localObject).setText(paramViewGroup.title);
        if (paramViewGroup.iconResId == 2130837908) {
          break;
        }
        localObject = paramViewGroup.context;
        try
        {
          localObject = ((Context)localObject).getDrawable(paramViewGroup.iconResId);
          TypedValue localTypedValue = new TypedValue();
          this.mContext.getTheme().resolveAttribute(16843829, localTypedValue, true);
          ((Drawable)localObject).setTint(this.mContext.getColor(localTypedValue.resourceId));
          localImageView.setImageDrawable((Drawable)localObject);
          return paramView;
        }
        catch (Resources.NotFoundException localNotFoundException)
        {
          Log.e("SearchResultsSummary", "Cannot load Drawable for " + paramViewGroup.title);
          return paramView;
        }
      }
      localNotFoundException.setImageDrawable(null);
      localNotFoundException.setBackgroundResource(2130837908);
      return paramView;
    }
    
    public Cursor swapCursor(Cursor paramCursor)
    {
      if (paramCursor == this.mCursor) {
        return null;
      }
      Cursor localCursor = this.mCursor;
      this.mCursor = paramCursor;
      if (paramCursor != null)
      {
        this.mDataValid = true;
        notifyDataSetChanged();
        return localCursor;
      }
      Log.d("SearchResultsSummary", "SearchResultsAdapter swapCursor mDataValid false");
      this.mDataValid = false;
      notifyDataSetInvalidated();
      return localCursor;
    }
  }
  
  private static class SuggestionItem
  {
    public String query;
    
    public SuggestionItem(String paramString)
    {
      this.query = paramString;
    }
  }
  
  private static class SuggestionsAdapter
    extends BaseAdapter
  {
    private static final int COLUMN_SUGGESTION_QUERY = 0;
    private static final int COLUMN_SUGGESTION_TIMESTAMP = 1;
    private Context mContext;
    private Cursor mCursor;
    private boolean mDataValid = false;
    private LayoutInflater mInflater;
    
    public SuggestionsAdapter(Context paramContext)
    {
      this.mContext = paramContext;
      this.mInflater = ((LayoutInflater)this.mContext.getSystemService("layout_inflater"));
      Log.d("SearchResultsSummary", "SuggestionsAdapter mDataValid false");
      this.mDataValid = false;
    }
    
    public int getCount()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("SuggestionsAdapter !mDataValid ＝ ");
      boolean bool;
      if (this.mDataValid)
      {
        bool = false;
        Log.d("SearchResultsSummary", bool + "mCursor =  " + this.mCursor);
        if (this.mCursor != null) {
          Log.d("SearchResultsSummary", "SuggestionsAdapter mCursor.isClosed()＝ " + this.mCursor.isClosed());
        }
        if ((this.mDataValid) && (this.mCursor != null)) {
          break label109;
        }
      }
      label109:
      while (this.mCursor.isClosed())
      {
        return 0;
        bool = true;
        break;
      }
      return this.mCursor.getCount();
    }
    
    public Object getItem(int paramInt)
    {
      if ((this.mDataValid) && (this.mCursor.moveToPosition(paramInt))) {
        return new SearchResultsSummary.SuggestionItem(this.mCursor.getString(0));
      }
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return 0L;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if ((!this.mDataValid) && (paramView == null)) {
        throw new IllegalStateException("this should only be called when the cursor is valid");
      }
      if (!this.mCursor.moveToPosition(paramInt)) {
        throw new IllegalStateException("couldn't move cursor to position " + paramInt);
      }
      if (paramView == null) {
        paramView = this.mInflater.inflate(2130968975, paramViewGroup, false);
      }
      for (;;)
      {
        ((TextView)paramView.findViewById(2131361894)).setText(((SearchResultsSummary.SuggestionItem)getItem(paramInt)).query);
        return paramView;
      }
    }
    
    public Cursor swapCursor(Cursor paramCursor)
    {
      if (paramCursor == this.mCursor) {
        return null;
      }
      Cursor localCursor = this.mCursor;
      this.mCursor = paramCursor;
      if (paramCursor != null)
      {
        this.mDataValid = true;
        notifyDataSetChanged();
        return localCursor;
      }
      Log.d("SearchResultsSummary", "SuggestionsAdapter swapCursor mDataValid false");
      this.mDataValid = false;
      notifyDataSetInvalidated();
      return localCursor;
    }
  }
  
  private class UpdateSearchResultsTask
    extends AsyncTask<String, Void, Cursor>
  {
    private UpdateSearchResultsTask() {}
    
    protected Cursor doInBackground(String... paramVarArgs)
    {
      return Index.getInstance(SearchResultsSummary.this.getActivity()).search(paramVarArgs[0]);
    }
    
    protected void onPostExecute(Cursor paramCursor)
    {
      boolean bool = false;
      if (!isCancelled())
      {
        MetricsLogger.action(SearchResultsSummary.this.getContext(), 226, paramCursor.getCount());
        SearchResultsSummary.-wrap1(SearchResultsSummary.this, paramCursor);
        localSearchResultsSummary = SearchResultsSummary.this;
        if (paramCursor.getCount() > 0) {
          bool = true;
        }
        SearchResultsSummary.-wrap2(localSearchResultsSummary, bool);
      }
      while (paramCursor == null)
      {
        SearchResultsSummary localSearchResultsSummary;
        return;
      }
      paramCursor.close();
    }
  }
  
  private class UpdateSuggestionsTask
    extends AsyncTask<String, Void, Cursor>
  {
    private UpdateSuggestionsTask() {}
    
    protected Cursor doInBackground(String... paramVarArgs)
    {
      return Index.getInstance(SearchResultsSummary.this.getActivity()).getSuggestions(paramVarArgs[0]);
    }
    
    protected void onPostExecute(Cursor paramCursor)
    {
      boolean bool = false;
      if (!isCancelled())
      {
        SearchResultsSummary.-wrap3(SearchResultsSummary.this, paramCursor);
        localSearchResultsSummary = SearchResultsSummary.this;
        if (paramCursor.getCount() > 0) {
          bool = true;
        }
        SearchResultsSummary.-wrap4(localSearchResultsSummary, bool);
      }
      while (paramCursor == null)
      {
        SearchResultsSummary localSearchResultsSummary;
        return;
      }
      paramCursor.close();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\SearchResultsSummary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */