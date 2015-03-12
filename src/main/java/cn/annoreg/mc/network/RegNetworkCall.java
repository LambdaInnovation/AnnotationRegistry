package cn.annoreg.mc.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.annoreg.mc.s11n.StorageOption;

import cpw.mods.fml.relauncher.Side;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RegNetworkCall {
    
    //For static method, use the default NULL.
    //For non-static method, you must use another option.
    StorageOption.Option thisStorage() default StorageOption.Option.NULL;
    
    //The side is the callee's side.
    //If a method is call by client and run in server, the side should be SERVER.
    Side side();
}
