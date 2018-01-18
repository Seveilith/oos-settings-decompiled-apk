package com.android.internal.policy;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IOPFaceSettingService
  extends IInterface
{
  public abstract int checkState(int paramInt)
    throws RemoteException;
  
  public abstract void removeFace(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IOPFaceSettingService
  {
    private static final String DESCRIPTOR = "com.android.internal.policy.IOPFaceSettingService";
    static final int TRANSACTION_checkState = 1;
    static final int TRANSACTION_removeFace = 2;
    
    public Stub()
    {
      attachInterface(this, "com.android.internal.policy.IOPFaceSettingService");
    }
    
    public static IOPFaceSettingService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.android.internal.policy.IOPFaceSettingService");
      if ((localIInterface != null) && ((localIInterface instanceof IOPFaceSettingService))) {
        return (IOPFaceSettingService)localIInterface;
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
        paramParcel2.writeString("com.android.internal.policy.IOPFaceSettingService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.android.internal.policy.IOPFaceSettingService");
        paramInt1 = checkState(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("com.android.internal.policy.IOPFaceSettingService");
      removeFace(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IOPFaceSettingService
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
      
      public int checkState(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.internal.policy.IOPFaceSettingService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "com.android.internal.policy.IOPFaceSettingService";
      }
      
      public void removeFace(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.android.internal.policy.IOPFaceSettingService");
          localParcel1.writeInt(paramInt);
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


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\internal\policy\IOPFaceSettingService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */