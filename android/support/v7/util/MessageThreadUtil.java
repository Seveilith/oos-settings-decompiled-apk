package android.support.v7.util;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ParallelExecutorCompat;
import android.util.Log;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

class MessageThreadUtil<T>
  implements ThreadUtil<T>
{
  public ThreadUtil.BackgroundCallback<T> getBackgroundProxy(final ThreadUtil.BackgroundCallback<T> paramBackgroundCallback)
  {
    new ThreadUtil.BackgroundCallback()
    {
      static final int LOAD_TILE = 3;
      static final int RECYCLE_TILE = 4;
      static final int REFRESH = 1;
      static final int UPDATE_RANGE = 2;
      private Runnable mBackgroundRunnable = new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            MessageThreadUtil.SyncQueueItem localSyncQueueItem = MessageThreadUtil.2.this.mQueue.next();
            if (localSyncQueueItem == null)
            {
              MessageThreadUtil.2.this.mBackgroundRunning.set(false);
              return;
            }
            switch (localSyncQueueItem.what)
            {
            default: 
              Log.e("ThreadUtil", "Unsupported message, what=" + localSyncQueueItem.what);
              break;
            case 1: 
              MessageThreadUtil.2.this.mQueue.removeMessages(1);
              this.val$callback.refresh(localSyncQueueItem.arg1);
              break;
            case 2: 
              MessageThreadUtil.2.this.mQueue.removeMessages(2);
              MessageThreadUtil.2.this.mQueue.removeMessages(3);
              this.val$callback.updateRange(localSyncQueueItem.arg1, localSyncQueueItem.arg2, localSyncQueueItem.arg3, localSyncQueueItem.arg4, localSyncQueueItem.arg5);
              break;
            case 3: 
              this.val$callback.loadTile(localSyncQueueItem.arg1, localSyncQueueItem.arg2);
              break;
            case 4: 
              this.val$callback.recycleTile((TileList.Tile)localSyncQueueItem.data);
            }
          }
        }
      };
      AtomicBoolean mBackgroundRunning = new AtomicBoolean(false);
      private final Executor mExecutor = ParallelExecutorCompat.getParallelExecutor();
      final MessageThreadUtil.MessageQueue mQueue = new MessageThreadUtil.MessageQueue();
      
      private void maybeExecuteBackgroundRunnable()
      {
        if (this.mBackgroundRunning.compareAndSet(false, true)) {
          this.mExecutor.execute(this.mBackgroundRunnable);
        }
      }
      
      private void sendMessage(MessageThreadUtil.SyncQueueItem paramAnonymousSyncQueueItem)
      {
        this.mQueue.sendMessage(paramAnonymousSyncQueueItem);
        maybeExecuteBackgroundRunnable();
      }
      
      private void sendMessageAtFrontOfQueue(MessageThreadUtil.SyncQueueItem paramAnonymousSyncQueueItem)
      {
        this.mQueue.sendMessageAtFrontOfQueue(paramAnonymousSyncQueueItem);
        maybeExecuteBackgroundRunnable();
      }
      
      public void loadTile(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(3, paramAnonymousInt1, paramAnonymousInt2));
      }
      
      public void recycleTile(TileList.Tile<T> paramAnonymousTile)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(4, 0, paramAnonymousTile));
      }
      
      public void refresh(int paramAnonymousInt)
      {
        sendMessageAtFrontOfQueue(MessageThreadUtil.SyncQueueItem.obtainMessage(1, paramAnonymousInt, null));
      }
      
      public void updateRange(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5)
      {
        sendMessageAtFrontOfQueue(MessageThreadUtil.SyncQueueItem.obtainMessage(2, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4, paramAnonymousInt5, null));
      }
    };
  }
  
  public ThreadUtil.MainThreadCallback<T> getMainThreadProxy(final ThreadUtil.MainThreadCallback<T> paramMainThreadCallback)
  {
    new ThreadUtil.MainThreadCallback()
    {
      static final int ADD_TILE = 2;
      static final int REMOVE_TILE = 3;
      static final int UPDATE_ITEM_COUNT = 1;
      private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
      private Runnable mMainThreadRunnable = new Runnable()
      {
        public void run()
        {
          MessageThreadUtil.SyncQueueItem localSyncQueueItem = MessageThreadUtil.1.this.mQueue.next();
          if (localSyncQueueItem != null)
          {
            switch (localSyncQueueItem.what)
            {
            default: 
              Log.e("ThreadUtil", "Unsupported message, what=" + localSyncQueueItem.what);
            }
            for (;;)
            {
              localSyncQueueItem = MessageThreadUtil.1.this.mQueue.next();
              break;
              this.val$callback.updateItemCount(localSyncQueueItem.arg1, localSyncQueueItem.arg2);
              continue;
              this.val$callback.addTile(localSyncQueueItem.arg1, (TileList.Tile)localSyncQueueItem.data);
              continue;
              this.val$callback.removeTile(localSyncQueueItem.arg1, localSyncQueueItem.arg2);
            }
          }
        }
      };
      final MessageThreadUtil.MessageQueue mQueue = new MessageThreadUtil.MessageQueue();
      
      private void sendMessage(MessageThreadUtil.SyncQueueItem paramAnonymousSyncQueueItem)
      {
        this.mQueue.sendMessage(paramAnonymousSyncQueueItem);
        this.mMainThreadHandler.post(this.mMainThreadRunnable);
      }
      
      public void addTile(int paramAnonymousInt, TileList.Tile<T> paramAnonymousTile)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(2, paramAnonymousInt, paramAnonymousTile));
      }
      
      public void removeTile(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(3, paramAnonymousInt1, paramAnonymousInt2));
      }
      
      public void updateItemCount(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(1, paramAnonymousInt1, paramAnonymousInt2));
      }
    };
  }
  
  static class MessageQueue
  {
    private MessageThreadUtil.SyncQueueItem mRoot;
    
    MessageThreadUtil.SyncQueueItem next()
    {
      try
      {
        MessageThreadUtil.SyncQueueItem localSyncQueueItem = this.mRoot;
        if (localSyncQueueItem == null) {
          return null;
        }
        localSyncQueueItem = this.mRoot;
        this.mRoot = MessageThreadUtil.SyncQueueItem.-get0(this.mRoot);
        return localSyncQueueItem;
      }
      finally {}
    }
    
    void removeMessages(int paramInt)
    {
      try
      {
        while ((this.mRoot != null) && (this.mRoot.what == paramInt))
        {
          MessageThreadUtil.SyncQueueItem localSyncQueueItem1 = this.mRoot;
          this.mRoot = MessageThreadUtil.SyncQueueItem.-get0(this.mRoot);
          localSyncQueueItem1.recycle();
        }
        if (this.mRoot == null) {
          break label105;
        }
      }
      finally {}
      Object localObject3 = this.mRoot;
      Object localObject2 = MessageThreadUtil.SyncQueueItem.-get0((MessageThreadUtil.SyncQueueItem)localObject3);
      if (localObject2 != null)
      {
        MessageThreadUtil.SyncQueueItem localSyncQueueItem2 = MessageThreadUtil.SyncQueueItem.-get0((MessageThreadUtil.SyncQueueItem)localObject2);
        if (((MessageThreadUtil.SyncQueueItem)localObject2).what == paramInt)
        {
          MessageThreadUtil.SyncQueueItem.-set0((MessageThreadUtil.SyncQueueItem)localObject3, localSyncQueueItem2);
          ((MessageThreadUtil.SyncQueueItem)localObject2).recycle();
        }
        for (;;)
        {
          localObject2 = localSyncQueueItem2;
          break;
          localObject3 = localObject2;
        }
      }
      label105:
    }
    
    void sendMessage(MessageThreadUtil.SyncQueueItem paramSyncQueueItem)
    {
      try
      {
        if (this.mRoot == null)
        {
          this.mRoot = paramSyncQueueItem;
          return;
        }
        for (MessageThreadUtil.SyncQueueItem localSyncQueueItem = this.mRoot; MessageThreadUtil.SyncQueueItem.-get0(localSyncQueueItem) != null; localSyncQueueItem = MessageThreadUtil.SyncQueueItem.-get0(localSyncQueueItem)) {}
        MessageThreadUtil.SyncQueueItem.-set0(localSyncQueueItem, paramSyncQueueItem);
        return;
      }
      finally {}
    }
    
    void sendMessageAtFrontOfQueue(MessageThreadUtil.SyncQueueItem paramSyncQueueItem)
    {
      try
      {
        MessageThreadUtil.SyncQueueItem.-set0(paramSyncQueueItem, this.mRoot);
        this.mRoot = paramSyncQueueItem;
        return;
      }
      finally
      {
        paramSyncQueueItem = finally;
        throw paramSyncQueueItem;
      }
    }
  }
  
  static class SyncQueueItem
  {
    private static SyncQueueItem sPool;
    private static final Object sPoolLock = new Object();
    public int arg1;
    public int arg2;
    public int arg3;
    public int arg4;
    public int arg5;
    public Object data;
    private SyncQueueItem next;
    public int what;
    
    static SyncQueueItem obtainMessage(int paramInt1, int paramInt2, int paramInt3)
    {
      return obtainMessage(paramInt1, paramInt2, paramInt3, 0, 0, 0, null);
    }
    
    static SyncQueueItem obtainMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Object paramObject)
    {
      synchronized (sPoolLock)
      {
        if (sPool == null)
        {
          localSyncQueueItem = new SyncQueueItem();
          localSyncQueueItem.what = paramInt1;
          localSyncQueueItem.arg1 = paramInt2;
          localSyncQueueItem.arg2 = paramInt3;
          localSyncQueueItem.arg3 = paramInt4;
          localSyncQueueItem.arg4 = paramInt5;
          localSyncQueueItem.arg5 = paramInt6;
          localSyncQueueItem.data = paramObject;
          return localSyncQueueItem;
        }
        SyncQueueItem localSyncQueueItem = sPool;
        sPool = sPool.next;
        localSyncQueueItem.next = null;
      }
    }
    
    static SyncQueueItem obtainMessage(int paramInt1, int paramInt2, Object paramObject)
    {
      return obtainMessage(paramInt1, paramInt2, 0, 0, 0, 0, paramObject);
    }
    
    void recycle()
    {
      this.next = null;
      this.arg5 = 0;
      this.arg4 = 0;
      this.arg3 = 0;
      this.arg2 = 0;
      this.arg1 = 0;
      this.what = 0;
      this.data = null;
      synchronized (sPoolLock)
      {
        if (sPool != null) {
          this.next = sPool;
        }
        sPool = this;
        return;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\util\MessageThreadUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */