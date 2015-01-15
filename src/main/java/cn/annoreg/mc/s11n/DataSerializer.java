package cn.annoreg.mc.s11n;

import net.minecraft.nbt.NBTBase;

public interface DataSerializer<T> {

	/**
	 * Note that obj can be null. When it's null, this function should create an instance.
	 * In any cases, the result is returned.
	 */
	T readData(NBTBase nbt, T obj) throws Exception;
	NBTBase writeData(T obj) throws Exception;
	
}
