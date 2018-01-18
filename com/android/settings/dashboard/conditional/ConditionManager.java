package com.android.settings.dashboard.conditional;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.Xml;
import com.oneplus.settings.SettingsBaseApplication;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class ConditionManager
{
  private static final String ATTR_CLASS = "cls";
  private static final Comparator<Condition> CONDITION_COMPARATOR = new Comparator()
  {
    public int compare(Condition paramAnonymousCondition1, Condition paramAnonymousCondition2)
    {
      return Long.compare(paramAnonymousCondition1.getLastChange(), paramAnonymousCondition2.getLastChange());
    }
  };
  private static final boolean DEBUG = false;
  private static final String FILE_NAME = "condition_state.xml";
  private static final String PKG = "com.android.settings.dashboard.conditional.";
  private static final String TAG = "ConditionManager";
  private static final String TAG_CONDITION = "c";
  private static final String TAG_CONDITIONS = "cs";
  private static ConditionManager sInstance;
  private final ArrayList<Condition> mConditions;
  private final Context mContext;
  private final ArrayList<ConditionListener> mListeners = new ArrayList();
  private File mXmlFile;
  
  private ConditionManager(Context paramContext, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.mConditions = new ArrayList();
    if (paramBoolean)
    {
      paramContext = new ConditionLoader(null);
      paramContext.onPostExecute(paramContext.doInBackground(new Void[0]));
      return;
    }
    new ConditionLoader(null).execute(new Void[0]);
  }
  
  private void addIfMissing(Class<? extends Condition> paramClass, ArrayList<Condition> paramArrayList)
  {
    if (getCondition(paramClass, paramArrayList) == null) {
      paramArrayList.add(createCondition(paramClass));
    }
  }
  
  private void addMissingConditions(ArrayList<Condition> paramArrayList)
  {
    addIfMissing(AirplaneModeCondition.class, paramArrayList);
    addIfMissing(HotspotCondition.class, paramArrayList);
    addIfMissing(DndCondition.class, paramArrayList);
    addIfMissing(BatterySaverCondition.class, paramArrayList);
    addIfMissing(CellularDataCondition.class, paramArrayList);
    addIfMissing(BackgroundDataCondition.class, paramArrayList);
    addIfMissing(WorkModeCondition.class, paramArrayList);
    addIfMissing(OPOTACondition.class, paramArrayList);
    Collections.sort(paramArrayList, CONDITION_COMPARATOR);
  }
  
  private Condition createCondition(Class<?> paramClass)
  {
    if (AirplaneModeCondition.class == paramClass) {
      return new AirplaneModeCondition(this);
    }
    if (HotspotCondition.class == paramClass) {
      return new HotspotCondition(this);
    }
    if (DndCondition.class == paramClass) {
      return new DndCondition(this);
    }
    if (BatterySaverCondition.class == paramClass) {
      return new BatterySaverCondition(this);
    }
    if (CellularDataCondition.class == paramClass) {
      return new CellularDataCondition(this);
    }
    if (BackgroundDataCondition.class == paramClass) {
      return new BackgroundDataCondition(this);
    }
    if (WorkModeCondition.class == paramClass) {
      return new WorkModeCondition(this);
    }
    if (OPOTACondition.class == paramClass) {
      return new OPOTACondition(this);
    }
    throw new RuntimeException("Unexpected Condition " + paramClass);
  }
  
  public static ConditionManager get(Context paramContext)
  {
    return get(paramContext, true);
  }
  
  public static ConditionManager get(Context paramContext, boolean paramBoolean)
  {
    if (sInstance == null) {
      sInstance = new ConditionManager(paramContext.getApplicationContext(), paramBoolean);
    }
    return sInstance;
  }
  
  private <T extends Condition> T getCondition(Class<T> paramClass, List<Condition> paramList)
  {
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      if (paramClass.equals(((Condition)paramList.get(i)).getClass())) {
        return (Condition)paramList.get(i);
      }
      i += 1;
    }
    return null;
  }
  
  private void readFromXml(File paramFile, ArrayList<Condition> paramArrayList)
  {
    FileReader localFileReader;
    for (;;)
    {
      try
      {
        XmlPullParser localXmlPullParser = Xml.newPullParser();
        localFileReader = new FileReader(paramFile);
        localXmlPullParser.setInput(localFileReader);
        int i = localXmlPullParser.getEventType();
        if (i == 1) {
          break;
        }
        if ("c".equals(localXmlPullParser.getName()))
        {
          i = localXmlPullParser.getDepth();
          String str = localXmlPullParser.getAttributeValue("", "cls");
          paramFile = str;
          if (!str.startsWith("com.android.settings.dashboard.conditional.")) {
            paramFile = "com.android.settings.dashboard.conditional." + str;
          }
          paramFile = createCondition(Class.forName(paramFile));
          paramFile.restoreState(PersistableBundle.restoreFromXml(localXmlPullParser));
          paramArrayList.add(paramFile);
          if (localXmlPullParser.getDepth() > i)
          {
            localXmlPullParser.next();
            continue;
          }
        }
        i = localXmlPullParser.next();
      }
      catch (XmlPullParserException|IOException|ClassNotFoundException paramFile)
      {
        Log.w("ConditionManager", "Problem reading condition_state.xml", paramFile);
        return;
      }
    }
    localFileReader.close();
  }
  
  private void saveToXml()
  {
    for (;;)
    {
      int i;
      try
      {
        XmlSerializer localXmlSerializer = Xml.newSerializer();
        FileWriter localFileWriter = new FileWriter(this.mXmlFile);
        localXmlSerializer.setOutput(localFileWriter);
        localXmlSerializer.startDocument("UTF-8", Boolean.valueOf(true));
        localXmlSerializer.startTag("", "cs");
        int j = this.mConditions.size();
        i = 0;
        if (i < j)
        {
          PersistableBundle localPersistableBundle = new PersistableBundle();
          if (((Condition)this.mConditions.get(i)).saveState(localPersistableBundle))
          {
            localXmlSerializer.startTag("", "c");
            localXmlSerializer.attribute("", "cls", ((Condition)this.mConditions.get(i)).getClass().getSimpleName());
            localPersistableBundle.saveToXml(localXmlSerializer);
            localXmlSerializer.endTag("", "c");
          }
        }
        else
        {
          localXmlSerializer.endTag("", "cs");
          localXmlSerializer.flush();
          localFileWriter.close();
          return;
        }
      }
      catch (XmlPullParserException|IOException localXmlPullParserException)
      {
        Log.w("ConditionManager", "Problem writing condition_state.xml", localXmlPullParserException);
        return;
      }
      i += 1;
    }
  }
  
  public void addListener(ConditionListener paramConditionListener)
  {
    this.mListeners.add(paramConditionListener);
    paramConditionListener.onConditionsChanged();
  }
  
  public <T extends Condition> T getCondition(Class<T> paramClass)
  {
    return getCondition(paramClass, this.mConditions);
  }
  
  public List<Condition> getConditions()
  {
    return this.mConditions;
  }
  
  Context getContext()
  {
    return SettingsBaseApplication.mApplication;
  }
  
  public List<Condition> getVisibleConditions()
  {
    ArrayList localArrayList = new ArrayList();
    int j = this.mConditions.size();
    int i = 0;
    while (i < j)
    {
      if (((Condition)this.mConditions.get(i)).shouldShow()) {
        localArrayList.add((Condition)this.mConditions.get(i));
      }
      i += 1;
    }
    return localArrayList;
  }
  
  public void notifyChanged(Condition paramCondition)
  {
    saveToXml();
    Collections.sort(this.mConditions, CONDITION_COMPARATOR);
    int j = this.mListeners.size();
    int i = 0;
    while (i < j)
    {
      ((ConditionListener)this.mListeners.get(i)).onConditionsChanged();
      i += 1;
    }
  }
  
  public void refreshAll()
  {
    int j = this.mConditions.size();
    int i = 0;
    while (i < j)
    {
      ((Condition)this.mConditions.get(i)).refreshState();
      i += 1;
    }
  }
  
  public void remListener(ConditionListener paramConditionListener)
  {
    this.mListeners.remove(paramConditionListener);
  }
  
  public static abstract interface ConditionListener
  {
    public abstract void onConditionsChanged();
  }
  
  private class ConditionLoader
    extends AsyncTask<Void, Void, ArrayList<Condition>>
  {
    private ConditionLoader() {}
    
    protected ArrayList<Condition> doInBackground(Void... paramVarArgs)
    {
      paramVarArgs = new ArrayList();
      ConditionManager.-set0(ConditionManager.this, new File(ConditionManager.-get1(ConditionManager.this).getFilesDir(), "condition_state.xml"));
      if (ConditionManager.-get3(ConditionManager.this).exists()) {
        ConditionManager.-wrap1(ConditionManager.this, ConditionManager.-get3(ConditionManager.this), paramVarArgs);
      }
      ConditionManager.-wrap0(ConditionManager.this, paramVarArgs);
      return paramVarArgs;
    }
    
    protected void onPostExecute(ArrayList<Condition> paramArrayList)
    {
      ConditionManager.-get0(ConditionManager.this).clear();
      ConditionManager.-get0(ConditionManager.this).addAll(paramArrayList);
      int j = ConditionManager.-get2(ConditionManager.this).size();
      int i = 0;
      while (i < j)
      {
        ((ConditionManager.ConditionListener)ConditionManager.-get2(ConditionManager.this).get(i)).onConditionsChanged();
        i += 1;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\conditional\ConditionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */