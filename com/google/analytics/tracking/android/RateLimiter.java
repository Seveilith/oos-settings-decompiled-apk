package com.google.analytics.tracking.android;

abstract interface RateLimiter
{
  public static final int DEFAULT_MAX_TOKEN_COUNT = 60;
  public static final long DEFAULT_MILLISECONDS_PER_TOKEN = 2000L;
  
  public abstract boolean tokenAvailable();
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\RateLimiter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */