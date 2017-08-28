package com.olegsagenadatrytwo.w4_w_googleplacesapi;

import android.content.Context;

/**
 * Created by omcna on 8/27/2017.
 */

public interface BasePresenter<V extends BaseView>  {
    void attachView(V view);
    void removeView();
    void setContext(Context context);
}
