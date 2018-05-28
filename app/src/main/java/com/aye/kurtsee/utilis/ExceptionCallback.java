package com.aye.kurtsee.utilis;

import org.json.JSONException;

import java.io.IOException;


public abstract class ExceptionCallback {
    public abstract void onIOException(IOException e);
    public abstract void onJSONException(JSONException e);
}
