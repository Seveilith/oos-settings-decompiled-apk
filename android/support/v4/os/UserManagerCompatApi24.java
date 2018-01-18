package android.support.v4.os;

import android.content.Context;
import android.os.UserManager;
import android.support.annotation.RestrictTo;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class UserManagerCompatApi24
{
  public static boolean isUserUnlocked(Context paramContext)
  {
    return ((UserManager)paramContext.getSystemService(UserManager.class)).isUserUnlocked();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\os\UserManagerCompatApi24.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */