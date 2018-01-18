package com.android.settings.notification;

import android.content.ComponentName;
import android.net.Uri;

public class ZenRuleInfo
{
  public ComponentName configurationActivity;
  public Uri defaultConditionId;
  public boolean isSystem;
  public CharSequence packageLabel;
  public String packageName;
  public int ruleInstanceLimit = -1;
  public ComponentName serviceComponent;
  public String settingsAction;
  public String title;
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (ZenRuleInfo)paramObject;
    if (this.isSystem != ((ZenRuleInfo)paramObject).isSystem) {
      return false;
    }
    if (this.ruleInstanceLimit != ((ZenRuleInfo)paramObject).ruleInstanceLimit) {
      return false;
    }
    if (this.packageName != null)
    {
      if (!this.packageName.equals(((ZenRuleInfo)paramObject).packageName)) {
        break label211;
      }
      if (this.title == null) {
        break label213;
      }
      if (!this.title.equals(((ZenRuleInfo)paramObject).title)) {
        break label220;
      }
      label99:
      if (this.settingsAction == null) {
        break label222;
      }
      if (!this.settingsAction.equals(((ZenRuleInfo)paramObject).settingsAction)) {
        break label229;
      }
      label120:
      if (this.configurationActivity == null) {
        break label231;
      }
      if (!this.configurationActivity.equals(((ZenRuleInfo)paramObject).configurationActivity)) {
        break label238;
      }
      label141:
      if (this.defaultConditionId == null) {
        break label240;
      }
      if (!this.defaultConditionId.equals(((ZenRuleInfo)paramObject).defaultConditionId)) {
        break label247;
      }
      label162:
      if (this.serviceComponent == null) {
        break label249;
      }
      if (!this.serviceComponent.equals(((ZenRuleInfo)paramObject).serviceComponent)) {
        break label256;
      }
      label183:
      if (this.packageLabel == null) {
        break label258;
      }
      bool = this.packageLabel.equals(((ZenRuleInfo)paramObject).packageLabel);
    }
    label211:
    label213:
    label220:
    label222:
    label229:
    label231:
    label238:
    label240:
    label247:
    label249:
    label256:
    label258:
    while (((ZenRuleInfo)paramObject).packageLabel == null)
    {
      return bool;
      if (((ZenRuleInfo)paramObject).packageName == null) {
        break;
      }
      return false;
      if (((ZenRuleInfo)paramObject).title == null) {
        break label99;
      }
      return false;
      if (((ZenRuleInfo)paramObject).settingsAction == null) {
        break label120;
      }
      return false;
      if (((ZenRuleInfo)paramObject).configurationActivity == null) {
        break label141;
      }
      return false;
      if (((ZenRuleInfo)paramObject).defaultConditionId == null) {
        break label162;
      }
      return false;
      if (((ZenRuleInfo)paramObject).serviceComponent == null) {
        break label183;
      }
      return false;
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenRuleInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */