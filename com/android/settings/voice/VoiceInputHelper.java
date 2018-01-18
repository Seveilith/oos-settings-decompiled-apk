package com.android.settings.voice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.provider.Settings.Secure;
import android.service.voice.VoiceInteractionServiceInfo;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public final class VoiceInputHelper
{
  static final String TAG = "VoiceInputHelper";
  final ArrayList<InteractionInfo> mAvailableInteractionInfos = new ArrayList();
  final List<ResolveInfo> mAvailableRecognition;
  final ArrayList<RecognizerInfo> mAvailableRecognizerInfos = new ArrayList();
  final List<ResolveInfo> mAvailableVoiceInteractions;
  final Context mContext;
  ComponentName mCurrentRecognizer;
  ComponentName mCurrentVoiceInteraction;
  
  public VoiceInputHelper(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAvailableVoiceInteractions = this.mContext.getPackageManager().queryIntentServices(new Intent("android.service.voice.VoiceInteractionService"), 128);
    this.mAvailableRecognition = this.mContext.getPackageManager().queryIntentServices(new Intent("android.speech.RecognitionService"), 128);
  }
  
  public void buildUi()
  {
    Object localObject1 = Settings.Secure.getString(this.mContext.getContentResolver(), "voice_interaction_service");
    ArraySet localArraySet;
    int j;
    int i;
    label53:
    Object localObject2;
    if ((localObject1 == null) || (((String)localObject1).isEmpty()))
    {
      this.mCurrentVoiceInteraction = null;
      localArraySet = new ArraySet();
      j = this.mAvailableVoiceInteractions.size();
      i = 0;
      if (i >= j) {
        break label235;
      }
      localObject1 = (ResolveInfo)this.mAvailableVoiceInteractions.get(i);
      localObject2 = new VoiceInteractionServiceInfo(this.mContext.getPackageManager(), ((ResolveInfo)localObject1).serviceInfo);
      if (((VoiceInteractionServiceInfo)localObject2).getParseError() == null) {
        break label182;
      }
      Log.w("VoiceInteractionService", "Error in VoiceInteractionService " + ((ResolveInfo)localObject1).serviceInfo.packageName + "/" + ((ResolveInfo)localObject1).serviceInfo.name + ": " + ((VoiceInteractionServiceInfo)localObject2).getParseError());
    }
    for (;;)
    {
      i += 1;
      break label53;
      this.mCurrentVoiceInteraction = ComponentName.unflattenFromString((String)localObject1);
      break;
      label182:
      this.mAvailableInteractionInfos.add(new InteractionInfo(this.mContext.getPackageManager(), (VoiceInteractionServiceInfo)localObject2));
      localArraySet.add(new ComponentName(((ResolveInfo)localObject1).serviceInfo.packageName, ((VoiceInteractionServiceInfo)localObject2).getRecognitionService()));
    }
    label235:
    Collections.sort(this.mAvailableInteractionInfos);
    localObject1 = Settings.Secure.getString(this.mContext.getContentResolver(), "voice_recognition_service");
    if ((localObject1 == null) || (((String)localObject1).isEmpty())) {
      this.mCurrentRecognizer = null;
    }
    for (;;)
    {
      j = this.mAvailableRecognition.size();
      i = 0;
      label286:
      if (i < j)
      {
        ResolveInfo localResolveInfo = (ResolveInfo)this.mAvailableRecognition.get(i);
        if (localArraySet.contains(new ComponentName(localResolveInfo.serviceInfo.packageName, localResolveInfo.serviceInfo.name))) {}
        Object localObject11 = localResolveInfo.serviceInfo;
        Object localObject5 = null;
        Object localObject7 = null;
        localObject1 = null;
        Object localObject4 = null;
        Object localObject9 = null;
        Object localObject10 = null;
        String str1 = null;
        String str2 = str1;
        Object localObject6 = localObject9;
        Object localObject8 = localObject10;
        try
        {
          localObject2 = ((ServiceInfo)localObject11).loadXmlMetaData(this.mContext.getPackageManager(), "android.speech");
          if (localObject2 == null)
          {
            localObject4 = localObject2;
            str2 = str1;
            localObject5 = localObject2;
            localObject6 = localObject9;
            localObject7 = localObject2;
            localObject8 = localObject10;
            localObject1 = localObject2;
            throw new XmlPullParserException("No android.speech meta-data for " + ((ServiceInfo)localObject11).packageName);
          }
        }
        catch (XmlPullParserException localXmlPullParserException)
        {
          localObject1 = localObject4;
          Log.e("VoiceInputHelper", "error parsing recognition service meta-data", localXmlPullParserException);
          localObject1 = str2;
          if (localObject4 != null)
          {
            ((XmlResourceParser)localObject4).close();
            localObject1 = str2;
          }
          this.mAvailableRecognizerInfos.add(new RecognizerInfo(this.mContext.getPackageManager(), localResolveInfo.serviceInfo, (String)localObject1));
          i += 1;
          break label286;
          this.mCurrentRecognizer = ComponentName.unflattenFromString((String)localObject1);
          continue;
          localObject4 = localXmlPullParserException;
          str2 = str1;
          localObject5 = localXmlPullParserException;
          localObject6 = localObject9;
          localObject7 = localXmlPullParserException;
          localObject8 = localObject10;
          localObject1 = localXmlPullParserException;
          localObject11 = this.mContext.getPackageManager().getResourcesForApplication(((ServiceInfo)localObject11).applicationInfo);
          localObject4 = localXmlPullParserException;
          str2 = str1;
          localObject5 = localXmlPullParserException;
          localObject6 = localObject9;
          localObject7 = localXmlPullParserException;
          localObject8 = localObject10;
          localObject1 = localXmlPullParserException;
          localAttributeSet = Xml.asAttributeSet(localXmlPullParserException);
          int k;
          do
          {
            localObject4 = localXmlPullParserException;
            str2 = str1;
            localObject5 = localXmlPullParserException;
            localObject6 = localObject9;
            localObject7 = localXmlPullParserException;
            localObject8 = localObject10;
            localObject1 = localXmlPullParserException;
            k = localXmlPullParserException.next();
          } while ((k != 1) && (k != 2));
          localObject4 = localXmlPullParserException;
          str2 = str1;
          localObject5 = localXmlPullParserException;
          localObject6 = localObject9;
          localObject7 = localXmlPullParserException;
          localObject8 = localObject10;
          localObject1 = localXmlPullParserException;
          if (!"recognition-service".equals(localXmlPullParserException.getName()))
          {
            localObject4 = localXmlPullParserException;
            str2 = str1;
            localObject5 = localXmlPullParserException;
            localObject6 = localObject9;
            localObject7 = localXmlPullParserException;
            localObject8 = localObject10;
            localObject1 = localXmlPullParserException;
            throw new XmlPullParserException("Meta-data does not start with recognition-service tag");
          }
        }
        catch (IOException localIOException)
        {
          for (;;)
          {
            AttributeSet localAttributeSet;
            localObject1 = localObject5;
            Log.e("VoiceInputHelper", "error parsing recognition service meta-data", localIOException);
            localObject1 = localObject6;
            if (localObject5 != null)
            {
              ((XmlResourceParser)localObject5).close();
              localObject1 = localObject6;
              continue;
              localObject4 = localIOException;
              str2 = str1;
              localObject5 = localIOException;
              localObject6 = localObject9;
              localObject7 = localIOException;
              localObject8 = localObject10;
              localObject1 = localIOException;
              localObject11 = ((Resources)localObject11).obtainAttributes(localAttributeSet, R.styleable.RecognitionService);
              localObject4 = localIOException;
              str2 = str1;
              localObject5 = localIOException;
              localObject6 = localObject9;
              localObject7 = localIOException;
              localObject8 = localObject10;
              localObject1 = localIOException;
              str1 = ((TypedArray)localObject11).getString(0);
              localObject4 = localIOException;
              str2 = str1;
              localObject5 = localIOException;
              localObject6 = str1;
              localObject7 = localIOException;
              localObject8 = str1;
              localObject1 = localIOException;
              ((TypedArray)localObject11).recycle();
              localObject1 = str1;
              if (localIOException != null)
              {
                localIOException.close();
                localObject1 = str1;
              }
            }
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          for (;;)
          {
            localObject1 = localObject7;
            Log.e("VoiceInputHelper", "error parsing recognition service meta-data", localNameNotFoundException);
            localObject1 = localObject8;
            if (localObject7 != null)
            {
              ((XmlResourceParser)localObject7).close();
              localObject1 = localObject8;
            }
          }
        }
        finally
        {
          if (localObject1 != null) {
            ((XmlResourceParser)localObject1).close();
          }
        }
      }
    }
    Collections.sort(this.mAvailableRecognizerInfos);
  }
  
  public boolean hasItems()
  {
    return (this.mAvailableVoiceInteractions.size() > 0) || (this.mAvailableRecognition.size() > 0);
  }
  
  public static class BaseInfo
    implements Comparable
  {
    public final CharSequence appLabel;
    public final ComponentName componentName;
    public final String key;
    public final CharSequence label;
    public final String labelStr;
    public final ServiceInfo service;
    public final ComponentName settings;
    
    public BaseInfo(PackageManager paramPackageManager, ServiceInfo paramServiceInfo, String paramString)
    {
      this.service = paramServiceInfo;
      this.componentName = new ComponentName(paramServiceInfo.packageName, paramServiceInfo.name);
      this.key = this.componentName.flattenToShortString();
      if (paramString != null) {
        localComponentName = new ComponentName(paramServiceInfo.packageName, paramString);
      }
      this.settings = localComponentName;
      this.label = paramServiceInfo.loadLabel(paramPackageManager);
      this.labelStr = this.label.toString();
      this.appLabel = paramServiceInfo.applicationInfo.loadLabel(paramPackageManager);
    }
    
    public int compareTo(Object paramObject)
    {
      return this.labelStr.compareTo(((BaseInfo)paramObject).labelStr);
    }
  }
  
  public static class InteractionInfo
    extends VoiceInputHelper.BaseInfo
  {
    public final VoiceInteractionServiceInfo serviceInfo;
    
    public InteractionInfo(PackageManager paramPackageManager, VoiceInteractionServiceInfo paramVoiceInteractionServiceInfo)
    {
      super(paramVoiceInteractionServiceInfo.getServiceInfo(), paramVoiceInteractionServiceInfo.getSettingsActivity());
      this.serviceInfo = paramVoiceInteractionServiceInfo;
    }
  }
  
  public static class RecognizerInfo
    extends VoiceInputHelper.BaseInfo
  {
    public RecognizerInfo(PackageManager paramPackageManager, ServiceInfo paramServiceInfo, String paramString)
    {
      super(paramServiceInfo, paramString);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\voice\VoiceInputHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */