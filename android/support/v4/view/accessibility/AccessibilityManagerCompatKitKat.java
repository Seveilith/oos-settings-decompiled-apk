package android.support.v4.view.accessibility;

import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener;

class AccessibilityManagerCompatKitKat
{
  public static boolean addTouchExplorationStateChangeListener(AccessibilityManager paramAccessibilityManager, Object paramObject)
  {
    return paramAccessibilityManager.addTouchExplorationStateChangeListener((AccessibilityManager.TouchExplorationStateChangeListener)paramObject);
  }
  
  public static Object newTouchExplorationStateChangeListener(TouchExplorationStateChangeListenerBridge paramTouchExplorationStateChangeListenerBridge)
  {
    new AccessibilityManager.TouchExplorationStateChangeListener()
    {
      public void onTouchExplorationStateChanged(boolean paramAnonymousBoolean)
      {
        this.val$bridge.onTouchExplorationStateChanged(paramAnonymousBoolean);
      }
    };
  }
  
  public static boolean removeTouchExplorationStateChangeListener(AccessibilityManager paramAccessibilityManager, Object paramObject)
  {
    return paramAccessibilityManager.removeTouchExplorationStateChangeListener((AccessibilityManager.TouchExplorationStateChangeListener)paramObject);
  }
  
  static abstract interface TouchExplorationStateChangeListenerBridge
  {
    public abstract void onTouchExplorationStateChanged(boolean paramBoolean);
  }
  
  public static class TouchExplorationStateChangeListenerWrapper
    implements AccessibilityManager.TouchExplorationStateChangeListener
  {
    final Object mListener;
    final AccessibilityManagerCompatKitKat.TouchExplorationStateChangeListenerBridge mListenerBridge;
    
    public TouchExplorationStateChangeListenerWrapper(Object paramObject, AccessibilityManagerCompatKitKat.TouchExplorationStateChangeListenerBridge paramTouchExplorationStateChangeListenerBridge)
    {
      this.mListener = paramObject;
      this.mListenerBridge = paramTouchExplorationStateChangeListenerBridge;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (TouchExplorationStateChangeListenerWrapper)paramObject;
      if (this.mListener == null) {
        return ((TouchExplorationStateChangeListenerWrapper)paramObject).mListener == null;
      }
      return this.mListener.equals(((TouchExplorationStateChangeListenerWrapper)paramObject).mListener);
    }
    
    public int hashCode()
    {
      if (this.mListener == null) {
        return 0;
      }
      return this.mListener.hashCode();
    }
    
    public void onTouchExplorationStateChanged(boolean paramBoolean)
    {
      this.mListenerBridge.onTouchExplorationStateChanged(paramBoolean);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\accessibility\AccessibilityManagerCompatKitKat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */