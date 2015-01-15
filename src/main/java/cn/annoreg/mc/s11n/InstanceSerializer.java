package cn.annoreg.mc.s11n;

import net.minecraft.nbt.NBTBase;

public interface InstanceSerializer<T> {
	
	T readInstance(NBTBase nbt) throws Exception;
	NBTBase writeInstance(T obj) throws Exception;
	
}
