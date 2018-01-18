package android.support.v4.content;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

class ContextCompatJellybean
{
  public static void startActivities(Context paramContext, Intent[] paramArrayOfIntent, Bundle paramBundle)
  {
    paramContext.startActivities(paramArrayOfIntent, paramBundle);
  }
  
  public static void startActivity(Context paramContext, Intent paramIntent, Bundle paramBundle)
  {
    paramContext.startActivity(paramIntent, paramBundle);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\content\ContextCompatJellybean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */