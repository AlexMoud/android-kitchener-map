package gr.hua.it21533.kitchenerMap.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class ConnectionChangeReceiver extends BroadcastReceiver {

    static ConnectionChangeInterface connectionChangeInterface;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (connectionChangeInterface != null) {
            connectionChangeInterface.connectionChanged();
        }
    }


    public static void register(ConnectionChangeInterface connInterface) {
        connectionChangeInterface = connInterface;
    }

    public interface ConnectionChangeInterface {
        void connectionChanged();
    }
}