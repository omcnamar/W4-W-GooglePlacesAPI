package com.olegsagenadatrytwo.w4_w_googleplacesapi.view.mapsactivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.olegsagenadatrytwo.w4_w_googleplacesapi.BasePresenter;
import com.olegsagenadatrytwo.w4_w_googleplacesapi.BaseView;
import com.olegsagenadatrytwo.w4_w_googleplacesapi.model.MyNearLocations;

/**
 * Created by omcna on 8/25/2017.
 */

public interface MapsActivityContract {

    interface View extends BaseView {

        void mapShowed(boolean isSaved);
        void nearLocationsReceived(MyNearLocations myNearLocations);
        void placeMarkers(MyNearLocations myNearLocations, GoogleMap mMap);
    }

    interface Presenter extends BasePresenter<View> {

        void showMap(SupportMapFragment mapFragmentIn);
    }
}
