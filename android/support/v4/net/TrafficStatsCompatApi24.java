package android.support.v4.net;

import android.net.TrafficStats;
import android.support.annotation.RestrictTo;
import java.net.DatagramSocket;
import java.net.SocketException;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class TrafficStatsCompatApi24
{
  public static void tagDatagramSocket(DatagramSocket paramDatagramSocket)
    throws SocketException
  {
    TrafficStats.tagDatagramSocket(paramDatagramSocket);
  }
  
  public static void untagDatagramSocket(DatagramSocket paramDatagramSocket)
    throws SocketException
  {
    TrafficStats.untagDatagramSocket(paramDatagramSocket);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\net\TrafficStatsCompatApi24.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */