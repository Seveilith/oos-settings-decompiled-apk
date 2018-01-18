package android.support.v7.preference;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;

public final class PreferenceScreen
  extends PreferenceGroup
{
  private boolean mShouldUseGeneratedIds = true;
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public PreferenceScreen(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, R.attr.preferenceScreenStyle, 16842891));
  }
  
  protected boolean isOnSameScreenAsChildren()
  {
    return false;
  }
  
  protected void onClick()
  {
    if ((getIntent() != null) || (getFragment() != null)) {}
    while (getPreferenceCount() == 0) {
      return;
    }
    PreferenceManager.OnNavigateToScreenListener localOnNavigateToScreenListener = getPreferenceManager().getOnNavigateToScreenListener();
    if (localOnNavigateToScreenListener != null) {
      localOnNavigateToScreenListener.onNavigateToScreen(this);
    }
  }
  
  public void setShouldUseGeneratedIds(boolean paramBoolean)
  {
    if (isAttached()) {
      throw new IllegalStateException("Cannot change the usage of generated IDs while attached to the preference hierarchy");
    }
    this.mShouldUseGeneratedIds = paramBoolean;
  }
  
  public boolean shouldUseGeneratedIds()
  {
    return this.mShouldUseGeneratedIds;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\preference\PreferenceScreen.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */