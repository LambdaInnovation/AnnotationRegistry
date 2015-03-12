package cn.annoreg.mc.network;

import java.util.HashMap;
import java.util.Map;

import cn.annoreg.ARModContainer;

public class NetworkCallManager {
    private static Map<String, NetworkCallDelegate> delegateMap = new HashMap();
    
    public static void onNetworkCall(String delegateName) {
        ARModContainer.log.debug("OK");
    }
    
    public static void onNetworkCall(String delegateName, Object... args) {
        //TODO call through network
        if (!delegateMap.containsKey(delegateName)) {
            ARModContainer.log.fatal("Unknown network call. Delegate name: \"{}\"", delegateName);
        } else {
            delegateMap.get(delegateName).invoke(args);
        }
    }
    
    public static void registerDelegateClass(String delegateName, NetworkCallDelegate delegate) {
        delegateMap.put(delegateName, delegate);
    }
}
