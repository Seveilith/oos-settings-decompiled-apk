package com.airbnb.lottie.model;

import android.os.AsyncTask;
import com.airbnb.lottie.Cancellable;
import com.airbnb.lottie.LottieComposition;

public abstract class CompositionLoader<Params>
  extends AsyncTask<Params, Void, LottieComposition>
  implements Cancellable
{
  public void cancel()
  {
    cancel(true);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\CompositionLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */