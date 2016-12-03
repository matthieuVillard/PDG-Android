package eventail.eventail;

/**
 * Created by matthieu.villard on 19.10.2016.
 */
import android.app.Application;

public class Eventail extends Application {

    public static final String TAG = Eventail.class.getSimpleName();

    private static String token = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void login(String token){
        if(token != null && !token.isEmpty())
            Eventail.token = token;
    }

    public static void  logout(){
        token = null;
    }

    public static String getToken(){
        return token;
    }
}