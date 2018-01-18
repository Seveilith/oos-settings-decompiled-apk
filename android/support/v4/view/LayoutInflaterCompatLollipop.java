package android.support.v4.view;

import android.view.LayoutInflater;

class LayoutInflaterCompatLollipop
{
  static void setFactory(LayoutInflater paramLayoutInflater, LayoutInflaterFactory paramLayoutInflaterFactory)
  {
    LayoutInflaterCompatHC.FactoryWrapperHC localFactoryWrapperHC = null;
    if (paramLayoutInflaterFactory != null) {
      localFactoryWrapperHC = new LayoutInflaterCompatHC.FactoryWrapperHC(paramLayoutInflaterFactory);
    }
    paramLayoutInflater.setFactory2(localFactoryWrapperHC);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\LayoutInflaterCompatLollipop.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */