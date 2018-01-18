package com.android.settings.applications;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.service.voice.VoiceInteractionServiceInfo;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.app.AssistUtils;
import com.android.settings.AppListPreferenceWithSettings;
import java.util.ArrayList;
import java.util.List;

public class DefaultAssistPreference
  extends AppListPreferenceWithSettings
{
  private static final String TAG = DefaultAssistPreference.class.getSimpleName();
  private final AssistUtils mAssistUtils;
  private final List<Info> mAvailableAssistants = new ArrayList();
  
  public DefaultAssistPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setShowItemNone(true);
    setDialogTitle(2131693431);
    this.mAssistUtils = new AssistUtils(paramContext);
  }
  
  private void addAssistActivities()
  {
    List localList = getContext().getPackageManager().queryIntentActivities(new Intent("android.intent.action.ASSIST"), 65536);
    int i = 0;
    while (i < localList.size())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localList.get(i);
      this.mAvailableAssistants.add(new Info(new ComponentName(localResolveInfo.activityInfo.packageName, localResolveInfo.activityInfo.name)));
      i += 1;
    }
  }
  
  private void addAssistServices()
  {
    PackageManager localPackageManager = getContext().getPackageManager();
    List localList = localPackageManager.queryIntentServices(new Intent("android.service.voice.VoiceInteractionService"), 128);
    int i = 0;
    if (i < localList.size())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localList.get(i);
      VoiceInteractionServiceInfo localVoiceInteractionServiceInfo = new VoiceInteractionServiceInfo(localPackageManager, localResolveInfo.serviceInfo);
      if (!localVoiceInteractionServiceInfo.getSupportsAssist()) {}
      for (;;)
      {
        i += 1;
        break;
        this.mAvailableAssistants.add(new Info(new ComponentName(localResolveInfo.serviceInfo.packageName, localResolveInfo.serviceInfo.name), localVoiceInteractionServiceInfo));
      }
    }
  }
  
  private Info findAssistantByPackageName(String paramString)
  {
    int i = 0;
    while (i < this.mAvailableAssistants.size())
    {
      Info localInfo = (Info)this.mAvailableAssistants.get(i);
      if (localInfo.component.getPackageName().equals(paramString)) {
        return localInfo;
      }
      i += 1;
    }
    return null;
  }
  
  private String getDefaultRecognizer()
  {
    ResolveInfo localResolveInfo = getContext().getPackageManager().resolveService(new Intent("android.speech.RecognitionService"), 128);
    if ((localResolveInfo == null) || (localResolveInfo.serviceInfo == null))
    {
      Log.w(TAG, "Unable to resolve default voice recognition service.");
      return "";
    }
    return new ComponentName(localResolveInfo.serviceInfo.packageName, localResolveInfo.serviceInfo.name).flattenToShortString();
  }
  
  private void setAssistActivity(Info paramInfo)
  {
    Settings.Secure.putString(getContext().getContentResolver(), "assistant", paramInfo.component.flattenToShortString());
    Settings.Secure.putString(getContext().getContentResolver(), "voice_interaction_service", "");
    Settings.Secure.putString(getContext().getContentResolver(), "voice_recognition_service", getDefaultRecognizer());
    setSummary(getEntry());
    setSettingsComponent(null);
  }
  
  private void setAssistNone()
  {
    Settings.Secure.putString(getContext().getContentResolver(), "assistant", "");
    Settings.Secure.putString(getContext().getContentResolver(), "voice_interaction_service", "");
    Settings.Secure.putString(getContext().getContentResolver(), "voice_recognition_service", getDefaultRecognizer());
    setSummary(getContext().getText(2131693430));
    setSettingsComponent(null);
  }
  
  private void setAssistService(Info paramInfo)
  {
    Object localObject = null;
    String str1 = paramInfo.component.flattenToShortString();
    String str2 = new ComponentName(paramInfo.component.getPackageName(), paramInfo.voiceInteractionServiceInfo.getRecognitionService()).flattenToShortString();
    Settings.Secure.putString(getContext().getContentResolver(), "assistant", str1);
    Settings.Secure.putString(getContext().getContentResolver(), "voice_interaction_service", str1);
    Settings.Secure.putString(getContext().getContentResolver(), "voice_recognition_service", str2);
    setSummary(getEntry());
    str1 = paramInfo.voiceInteractionServiceInfo.getSettingsActivity();
    if (str1 == null) {}
    for (paramInfo = (Info)localObject;; paramInfo = new ComponentName(paramInfo.component.getPackageName(), str1))
    {
      setSettingsComponent(paramInfo);
      return;
    }
  }
  
  public ComponentName getCurrentAssist()
  {
    return this.mAssistUtils.getAssistComponentForUser(UserHandle.myUserId());
  }
  
  protected boolean persistString(String paramString)
  {
    paramString = findAssistantByPackageName(paramString);
    if (paramString == null)
    {
      setAssistNone();
      return true;
    }
    if (paramString.isVoiceInteractionService())
    {
      setAssistService(paramString);
      return true;
    }
    setAssistActivity(paramString);
    return true;
  }
  
  public void refreshAssistApps()
  {
    Object localObject1 = null;
    this.mAvailableAssistants.clear();
    addAssistServices();
    addAssistActivities();
    Object localObject2 = new ArrayList();
    int i = 0;
    if (i < this.mAvailableAssistants.size())
    {
      localObject3 = ((Info)this.mAvailableAssistants.get(i)).component.getPackageName();
      if (((List)localObject2).contains(localObject3)) {}
      for (;;)
      {
        i += 1;
        break;
        ((List)localObject2).add(localObject3);
      }
    }
    Object localObject3 = getCurrentAssist();
    localObject2 = (CharSequence[])((List)localObject2).toArray(new String[((List)localObject2).size()]);
    if (localObject3 == null) {}
    for (;;)
    {
      setPackageNames((CharSequence[])localObject2, (CharSequence)localObject1);
      return;
      localObject1 = ((ComponentName)localObject3).getPackageName();
    }
  }
  
  private static class Info
  {
    public final ComponentName component;
    public final VoiceInteractionServiceInfo voiceInteractionServiceInfo;
    
    Info(ComponentName paramComponentName)
    {
      this.component = paramComponentName;
      this.voiceInteractionServiceInfo = null;
    }
    
    Info(ComponentName paramComponentName, VoiceInteractionServiceInfo paramVoiceInteractionServiceInfo)
    {
      this.component = paramComponentName;
      this.voiceInteractionServiceInfo = paramVoiceInteractionServiceInfo;
    }
    
    public boolean isVoiceInteractionService()
    {
      return this.voiceInteractionServiceInfo != null;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\DefaultAssistPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */