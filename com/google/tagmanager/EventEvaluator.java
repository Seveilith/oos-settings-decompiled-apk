package com.google.tagmanager;

class EventEvaluator
{
  private final ResourceUtil.ExpandedResource mResource;
  private final Runtime mRuntime;
  
  public EventEvaluator(Runtime paramRuntime, ResourceUtil.ExpandedResource paramExpandedResource)
  {
    if (paramRuntime != null)
    {
      this.mRuntime = paramRuntime;
      if (paramExpandedResource == paramRuntime.getResource()) {
        this.mResource = paramRuntime.getResource();
      }
    }
    else
    {
      throw new NullPointerException("runtime cannot be null");
    }
    throw new IllegalArgumentException("resource must be the same as the resource in runtime");
  }
  
  void evaluateEvent(String paramString)
  {
    throw new UnsupportedOperationException("this code not yet written");
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\EventEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */