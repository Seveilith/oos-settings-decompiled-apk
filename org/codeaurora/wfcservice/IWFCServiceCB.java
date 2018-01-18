package org.codeaurora.wfcservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IWFCServiceCB
  extends IInterface
{
  public abstract void updateWFCMessage(String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IWFCServiceCB
  {
    private static final String DESCRIPTOR = "org.codeaurora.wfcservice.IWFCServiceCB";
    static final int TRANSACTION_updateWFCMessage = 1;
    
    public Stub()
    {
      attachInterface(this, "org.codeaurora.wfcservice.IWFCServiceCB");
    }
    
    public static IWFCServiceCB asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("org.codeaurora.wfcservice.IWFCServiceCB");
      if ((localIInterface != null) && ((localIInterface instanceof IWFCServiceCB))) {
        return (IWFCServiceCB)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("org.codeaurora.wfcservice.IWFCServiceCB");
        return true;
      }
      paramParcel1.enforceInterface("org.codeaurora.wfcservice.IWFCServiceCB");
      updateWFCMessage(paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IWFCServiceCB
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getInterfaceDescriptor()
      {
        return "org.codeaurora.wfcservice.IWFCServiceCB";
      }
      
      public void updateWFCMessage(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("org.codeaurora.wfcservice.IWFCServiceCB");
          localParcel1.writeString(paramString);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\org\codeaurora\wfcservice\IWFCServiceCB.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */