package android.support.v4.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;

class LayoutInflaterCompatBase
{
  static LayoutInflaterFactory getFactory(LayoutInflater paramLayoutInflater)
  {
    paramLayoutInflater = paramLayoutInflater.getFactory();
    if ((paramLayoutInflater instanceof FactoryWrapper)) {
      return ((FactoryWrapper)paramLayoutInflater).mDelegateFactory;
    }
    return null;
  }
  
  static void setFactory(LayoutInflater paramLayoutInflater, LayoutInflaterFactory paramLayoutInflaterFactory)
  {
    FactoryWrapper localFactoryWrapper = null;
    if (paramLayoutInflaterFactory != null) {
      localFactoryWrapper = new FactoryWrapper(paramLayoutInflaterFactory);
    }
    paramLayoutInflater.setFactory(localFactoryWrapper);
  }
  
  static class FactoryWrapper
    implements LayoutInflater.Factory
  {
    final LayoutInflaterFactory mDelegateFactory;
    
    FactoryWrapper(LayoutInflaterFactory paramLayoutInflaterFactory)
    {
      this.mDelegateFactory = paramLayoutInflaterFactory;
    }
    
    public View onCreateView(String paramString, Context paramContext, AttributeSet paramAttributeSet)
    {
      return this.mDelegateFactory.onCreateView(null, paramString, paramContext, paramAttributeSet);
    }
    
    public String toString()
    {
      return getClass().getName() + "{" + this.mDelegateFactory + "}";
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\LayoutInflaterCompatBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */