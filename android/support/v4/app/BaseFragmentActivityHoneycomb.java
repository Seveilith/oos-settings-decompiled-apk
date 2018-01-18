package android.support.v4.app;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;

abstract class BaseFragmentActivityHoneycomb
  extends BaseFragmentActivityGingerbread
{
  public View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    View localView = dispatchFragmentsOnCreateView(paramView, paramString, paramContext, paramAttributeSet);
    if ((localView == null) && (Build.VERSION.SDK_INT >= 11)) {
      return super.onCreateView(paramView, paramString, paramContext, paramAttributeSet);
    }
    return localView;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\app\BaseFragmentActivityHoneycomb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */