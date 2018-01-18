package org.codeaurora.wfcservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IWFCService
  extends IInterface
{
  public abstract int getWifiCallingPreference()
    throws RemoteException;
  
  public abstract boolean getWifiCallingStatus()
    throws RemoteException;
  
  public abstract void registerCallback(IWFCServiceCB paramIWFCServiceCB)
    throws RemoteException;
  
  public abstract void setWifiCalling(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void unregisterCallback(IWFCServiceCB paramIWFCServiceCB)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IWFCService
  {
    private static final String DESCRIPTOR = "org.codeaurora.wfcservice.IWFCService";
    static final int TRANSACTION_getWifiCallingPreference = 5;
    static final int TRANSACTION_getWifiCallingStatus = 4;
    static final int TRANSACTION_registerCallback = 1;
    static final int TRANSACTION_setWifiCalling = 3;
    static final int TRANSACTION_unregisterCallback = 2;
    
    public Stub()
    {
      attachInterface(this, "org.codeaurora.wfcservice.IWFCService");
    }
    
    public static IWFCService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("org.codeaurora.wfcservice.IWFCService");
      if ((localIInterface != null) && ((localIInterface instanceof IWFCService))) {
        return (IWFCService)localIInterface;
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
      int i = 0;
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("org.codeaurora.wfcservice.IWFCService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("org.codeaurora.wfcservice.IWFCService");
        registerCallback(IWFCServiceCB.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("org.codeaurora.wfcservice.IWFCService");
        unregisterCallback(IWFCServiceCB.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("org.codeaurora.wfcservice.IWFCService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setWifiCalling(bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("org.codeaurora.wfcservice.IWFCService");
        bool = getWifiCallingStatus();
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("org.codeaurora.wfcservice.IWFCService");
      paramInt1 = getWifiCallingPreference();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IWFCService
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
        return "org.codeaurora.wfcservice.IWFCService";
      }
      
      public int getWifiCallingPreference()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("org.codeaurora.wfcservice.IWFCService");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean getWifiCallingStatus()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	org/codeaurora/wfcservice/IWFCService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_4
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 46 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 49	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 52	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 55	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 55	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 55	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 55	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   40	2	1	i	int
        //   46	14	2	bool	boolean
        //   3	68	3	localParcel1	Parcel
        //   7	59	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	41	63	finally
      }
      
      public void registerCallback(IWFCServiceCB paramIWFCServiceCB)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("org.codeaurora.wfcservice.IWFCService");
          if (paramIWFCServiceCB != null) {
            localIBinder = paramIWFCServiceCB.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void setWifiCalling(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("org.codeaurora.wfcservice.IWFCService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void unregisterCallback(IWFCServiceCB paramIWFCServiceCB)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("org.codeaurora.wfcservice.IWFCService");
          if (paramIWFCServiceCB != null) {
            localIBinder = paramIWFCServiceCB.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
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


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\org\codeaurora\wfcservice\IWFCService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */