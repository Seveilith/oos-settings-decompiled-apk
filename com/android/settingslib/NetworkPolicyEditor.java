package com.android.settingslib;

import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkTemplate;
import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.Time;
import com.android.internal.util.Preconditions;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;

public class NetworkPolicyEditor
{
  public static final boolean ENABLE_SPLIT_POLICIES = false;
  private ArrayList<NetworkPolicy> mPolicies = Lists.newArrayList();
  private NetworkPolicyManager mPolicyManager;
  
  public NetworkPolicyEditor(NetworkPolicyManager paramNetworkPolicyManager)
  {
    this.mPolicyManager = ((NetworkPolicyManager)Preconditions.checkNotNull(paramNetworkPolicyManager));
  }
  
  @Deprecated
  private static NetworkPolicy buildDefaultPolicy(NetworkTemplate paramNetworkTemplate)
  {
    int i;
    Object localObject;
    if (paramNetworkTemplate.getMatchRule() == 4)
    {
      i = -1;
      localObject = "UTC";
    }
    for (boolean bool = false;; bool = true)
    {
      return new NetworkPolicy(paramNetworkTemplate, i, (String)localObject, -1L, -1L, -1L, -1L, bool, true);
      localObject = new Time();
      ((Time)localObject).setToNow();
      i = ((Time)localObject).monthDay;
      localObject = ((Time)localObject).timezone;
    }
  }
  
  private static NetworkTemplate buildUnquotedNetworkTemplate(NetworkTemplate paramNetworkTemplate)
  {
    if (paramNetworkTemplate == null) {
      return null;
    }
    String str1 = paramNetworkTemplate.getNetworkId();
    String str2 = WifiInfo.removeDoubleQuotes(str1);
    if (!TextUtils.equals(str2, str1)) {
      return new NetworkTemplate(paramNetworkTemplate.getMatchRule(), paramNetworkTemplate.getSubscriberId(), str2);
    }
    return null;
  }
  
  public NetworkPolicy getOrCreatePolicy(NetworkTemplate paramNetworkTemplate)
  {
    NetworkPolicy localNetworkPolicy2 = getPolicy(paramNetworkTemplate);
    NetworkPolicy localNetworkPolicy1 = localNetworkPolicy2;
    if (localNetworkPolicy2 == null)
    {
      localNetworkPolicy1 = buildDefaultPolicy(paramNetworkTemplate);
      this.mPolicies.add(localNetworkPolicy1);
    }
    return localNetworkPolicy1;
  }
  
  public NetworkPolicy getPolicy(NetworkTemplate paramNetworkTemplate)
  {
    Iterator localIterator = this.mPolicies.iterator();
    while (localIterator.hasNext())
    {
      NetworkPolicy localNetworkPolicy = (NetworkPolicy)localIterator.next();
      if (localNetworkPolicy.template.equals(paramNetworkTemplate)) {
        return localNetworkPolicy;
      }
    }
    return null;
  }
  
  public int getPolicyCycleDay(NetworkTemplate paramNetworkTemplate)
  {
    paramNetworkTemplate = getPolicy(paramNetworkTemplate);
    if (paramNetworkTemplate != null) {
      return paramNetworkTemplate.cycleDay;
    }
    return -1;
  }
  
  public long getPolicyLimitBytes(NetworkTemplate paramNetworkTemplate)
  {
    paramNetworkTemplate = getPolicy(paramNetworkTemplate);
    if (paramNetworkTemplate != null) {
      return paramNetworkTemplate.limitBytes;
    }
    return -1L;
  }
  
  public NetworkPolicy getPolicyMaybeUnquoted(NetworkTemplate paramNetworkTemplate)
  {
    NetworkPolicy localNetworkPolicy = getPolicy(paramNetworkTemplate);
    if (localNetworkPolicy != null) {
      return localNetworkPolicy;
    }
    return getPolicy(buildUnquotedNetworkTemplate(paramNetworkTemplate));
  }
  
  public boolean getPolicyMetered(NetworkTemplate paramNetworkTemplate)
  {
    paramNetworkTemplate = getPolicy(paramNetworkTemplate);
    if (paramNetworkTemplate != null) {
      return paramNetworkTemplate.metered;
    }
    return false;
  }
  
  public long getPolicyWarningBytes(NetworkTemplate paramNetworkTemplate)
  {
    paramNetworkTemplate = getPolicy(paramNetworkTemplate);
    if (paramNetworkTemplate != null) {
      return paramNetworkTemplate.warningBytes;
    }
    return -1L;
  }
  
  public boolean hasLimitedPolicy(NetworkTemplate paramNetworkTemplate)
  {
    boolean bool2 = false;
    paramNetworkTemplate = getPolicy(paramNetworkTemplate);
    boolean bool1 = bool2;
    if (paramNetworkTemplate != null)
    {
      bool1 = bool2;
      if (paramNetworkTemplate.limitBytes != -1L) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public void read()
  {
    NetworkPolicy[] arrayOfNetworkPolicy = this.mPolicyManager.getNetworkPolicies();
    int i = 0;
    this.mPolicies.clear();
    int j = 0;
    int k = arrayOfNetworkPolicy.length;
    while (j < k)
    {
      NetworkPolicy localNetworkPolicy = arrayOfNetworkPolicy[j];
      if (localNetworkPolicy.limitBytes < -1L)
      {
        localNetworkPolicy.limitBytes = -1L;
        i = 1;
      }
      if (localNetworkPolicy.warningBytes < -1L)
      {
        localNetworkPolicy.warningBytes = -1L;
        i = 1;
      }
      this.mPolicies.add(localNetworkPolicy);
      j += 1;
    }
    if (i != 0) {
      writeAsync();
    }
  }
  
  public void setPolicyCycleDay(NetworkTemplate paramNetworkTemplate, int paramInt, String paramString)
  {
    paramNetworkTemplate = getOrCreatePolicy(paramNetworkTemplate);
    paramNetworkTemplate.cycleDay = paramInt;
    paramNetworkTemplate.cycleTimezone = paramString;
    paramNetworkTemplate.inferred = false;
    paramNetworkTemplate.clearSnooze();
    writeAsync();
  }
  
  public void setPolicyLimitBytes(NetworkTemplate paramNetworkTemplate, long paramLong)
  {
    paramNetworkTemplate = getOrCreatePolicy(paramNetworkTemplate);
    paramNetworkTemplate.limitBytes = paramLong;
    paramNetworkTemplate.inferred = false;
    paramNetworkTemplate.clearSnooze();
    writeAsync();
  }
  
  public void setPolicyMetered(NetworkTemplate paramNetworkTemplate, boolean paramBoolean)
  {
    int j = 0;
    NetworkPolicy localNetworkPolicy = getPolicy(paramNetworkTemplate);
    int i;
    if (paramBoolean) {
      if (localNetworkPolicy == null)
      {
        localNetworkPolicy = buildDefaultPolicy(paramNetworkTemplate);
        localNetworkPolicy.metered = true;
        localNetworkPolicy.inferred = false;
        this.mPolicies.add(localNetworkPolicy);
        i = 1;
      }
    }
    for (;;)
    {
      paramNetworkTemplate = getPolicy(buildUnquotedNetworkTemplate(paramNetworkTemplate));
      if (paramNetworkTemplate != null)
      {
        this.mPolicies.remove(paramNetworkTemplate);
        i = 1;
      }
      if (i != 0) {
        writeAsync();
      }
      return;
      i = j;
      if (!localNetworkPolicy.metered)
      {
        localNetworkPolicy.metered = true;
        localNetworkPolicy.inferred = false;
        i = 1;
        continue;
        i = j;
        if (localNetworkPolicy != null)
        {
          i = j;
          if (localNetworkPolicy.metered)
          {
            localNetworkPolicy.metered = false;
            localNetworkPolicy.inferred = false;
            i = 1;
          }
        }
      }
    }
  }
  
  public void setPolicyWarningBytes(NetworkTemplate paramNetworkTemplate, long paramLong)
  {
    paramNetworkTemplate = getOrCreatePolicy(paramNetworkTemplate);
    paramNetworkTemplate.warningBytes = paramLong;
    paramNetworkTemplate.inferred = false;
    paramNetworkTemplate.clearSnooze();
    writeAsync();
  }
  
  public void write(NetworkPolicy[] paramArrayOfNetworkPolicy)
  {
    this.mPolicyManager.setNetworkPolicies(paramArrayOfNetworkPolicy);
  }
  
  public void writeAsync()
  {
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        NetworkPolicyEditor.this.write(this.val$policies);
        return null;
      }
    }.execute(new Void[0]);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\NetworkPolicyEditor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */