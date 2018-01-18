package com.google.tagmanager;

class ObjectAndStatic<T>
{
  private final boolean mIsStatic;
  private final T mObject;
  
  ObjectAndStatic(T paramT, boolean paramBoolean)
  {
    this.mObject = paramT;
    this.mIsStatic = paramBoolean;
  }
  
  public T getObject()
  {
    return (T)this.mObject;
  }
  
  public boolean isStatic()
  {
    return this.mIsStatic;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\ObjectAndStatic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */