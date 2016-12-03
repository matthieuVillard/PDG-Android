package eventail.eventail.service.tracker;

import android.location.Location;

/**
 * Created by matthieu.villard on 25.11.2016.
 */

public interface GPSListener {
    void onLocationChanged(Location location);
    void onInitialized(Location location);
    void onEnabled(Location location);
    void onDisabled();
}
