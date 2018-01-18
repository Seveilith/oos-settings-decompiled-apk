package android.support.v13.app;

import android.app.Fragment;

class FragmentCompatICSMR1
{
  public static void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean)
  {
    if (paramFragment.getFragmentManager() != null) {
      paramFragment.setUserVisibleHint(paramBoolean);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v13\app\FragmentCompatICSMR1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */