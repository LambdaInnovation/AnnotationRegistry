/**
 * Copyright (c) Lambda Innovation, 2013-2015
 * 本作品版权由Lambda Innovation所有。
 * http://www.li-dev.cn/
 *
 * AnnotationRegistry is open-source, and it is distributed under 
 * the terms of GNU General Public License. You can modify
 * and distribute freely as long as you follow the license.
 * AnnotationRegistry是一个开源项目，且遵循GNU通用公共授权协议。
 * 在遵照该协议的情况下，您可以自由传播和修改。
 * http://www.gnu.org/licenses/gpl.html
 */
package cn.annoreg.mc.s11n;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import cn.annoreg.ARModContainer;
import cn.annoreg.mc.ProxyHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class SerializationManager {
	
	public static final SerializationManager INSTANCE = new SerializationManager();
	
	static {
		INSTANCE.initInternalSerializers();
	}
	
	private Map<Class, DataSerializer> dataSerializers = new HashMap();
	private Map<Class, InstanceSerializer> instanceSerializers = new HashMap();

	public NBTBase serialize(Object obj, StorageOption.Option option) {
		Class clazz = obj.getClass();
		DataSerializer d = getDataSerializer(clazz);
		InstanceSerializer i = getInstanceSerializer(clazz);
		NBTTagCompound ret = new NBTTagCompound();
		ret.setString("class", clazz.getName());
		ret.setInteger("option", option.ordinal());
		switch (option) {
		case DATA:
			try {
				ret.setTag("data", d.writeData(obj));
				return ret;
			} catch (Exception e) {
				ARModContainer.log.error("Failed in data serialization. Class: {}.", clazz.getCanonicalName());
				e.printStackTrace();
				return null;
			}
		case INSTANCE:
			try {
				ret.setTag("instance", i.writeInstance(obj));
				return ret;
			} catch (Exception e) {
				ARModContainer.log.error("Failed in instance serialization. Class: {}.", clazz.getCanonicalName());
				e.printStackTrace();
				return null;
			}
		case UPDATE:
			try {
				ret.setTag("instance", i.writeInstance(obj));
				ret.setTag("data", d.writeData(obj));
				return ret;
			} catch (Exception e) {
				ARModContainer.log.error("Failed in update serialization. Class: {}.", clazz.getCanonicalName());
				e.printStackTrace();
				return null;
			}
		default:
			ARModContainer.log.error("Failed in serialization. Class: {}. Unknown option.",
					clazz.getCanonicalName());
			Thread.dumpStack();
			return null;
		}
	}
	
	//use null in obj if you are using INSTANCE or UPDATE option.
	public Object deserialize(Object obj, NBTBase nbt, StorageOption.Option option) {
		NBTTagCompound tag = (NBTTagCompound) nbt;
		if (tag.getInteger("option") != option.ordinal()) {
			ARModContainer.log.error("Failed in deserialization. Class: {}.", tag.getString("class"));
			Thread.dumpStack();
		}
		Class<?> clazz;
		try {
			clazz = Class.forName(tag.getString("class"));
		} catch (ClassNotFoundException e) {
			ARModContainer.log.error("Failed in deserialization. Class: {}.", tag.getString("class"));
			e.printStackTrace();
			return null;
		}
		NBTBase data = tag.getTag("data");
		NBTBase ins = tag.getTag("instance");
		switch (option) {
		case DATA:
		{
			DataSerializer ser = getDataSerializer(clazz);
			try {
				return ser.readData(data, obj);
			} catch (Exception e) {
				ARModContainer.log.error("Failed in data deserialization. Class: {}.",
						clazz.getCanonicalName());
				e.printStackTrace();
				return null;
			}
		}
		case INSTANCE:
		{
			InstanceSerializer ser = getInstanceSerializer(clazz);
			try {
				return ser.readInstance(ins);
			} catch (Exception e) {
				ARModContainer.log.error("Failed in instance deserialization. Class: {}.",
						clazz.getCanonicalName());
				e.printStackTrace();
				return null;
			}
		}
		case UPDATE:
		{
			DataSerializer d = getDataSerializer(clazz);
			InstanceSerializer i = getInstanceSerializer(clazz);
			try {
				Object objIns = i.readInstance(ins);
				if (objIns == null) {
					throw new Exception("Instance is null.");
				}
				d.readData(data, objIns);
				return objIns;
			} catch (Exception e) {
				ARModContainer.log.error("Failed in update deserialization. Class: {}.",
						clazz.getCanonicalName());
				e.printStackTrace();
				return null;
			}
		}
		default:
			ARModContainer.log.error("Failed in deserialization. Class: {}. Unknown option.",
					clazz.getCanonicalName());
			Thread.dumpStack();
			return null;
		}
	}
	
	public <T> InstanceSerializer<T> getInstanceSerializer(Class<T> clazz) {
		return instanceSerializers.get(clazz);
	}
	
	public <T> DataSerializer<T> getDataSerializer(Class<T> clazz) {
		DataSerializer<T> ser = dataSerializers.get(clazz);
		if (ser == null && clazz.isAnnotationPresent(RegSerializable.class)) {
			ser = createAutoSerializerFor(clazz);
		}
		return ser;
	}
	
	public boolean hasDataSerializer(Class clazz) {
		return dataSerializers.containsKey(clazz);
	}
	
	private Set<Class> autoSerializerCreating = new HashSet();
	
	DataSerializer createAutoSerializerFor(Class<?> clazz) {
		if (autoSerializerCreating.contains(clazz)) {
			throw new RuntimeException("Circular dependencies in auto serializer.");
		}
		autoSerializerCreating.add(clazz);
		DataSerializer ret = new ReflectionAutoSerializer(clazz);
		autoSerializerCreating.remove(clazz);
		return ret;
	}
	
	void setDataSerializerFor(Class<?> clazz, DataSerializer serializer) {
		dataSerializers.put(clazz, serializer);
	}
	
	void setInstanceSerializerFor(Class<?> clazz, InstanceSerializer serializer) {
		instanceSerializers.put(clazz, serializer);
	}
	
	private void initInternalSerializers() {
		//First part: java internal class.
		{
			DataSerializer ser = new DataSerializer<Byte>() {
				@Override
				public Byte readData(NBTBase nbt, Byte obj) throws Exception {
					return ((NBTTagByte) nbt).func_150290_f();
				}

				@Override
				public NBTBase writeData(Byte obj) throws Exception {
					return new NBTTagByte(obj);
				}
			};
			setDataSerializerFor(Byte.TYPE, ser);
			setDataSerializerFor(Byte.class, ser);
		}
		{
			DataSerializer ser = new DataSerializer<Byte[]>() {
				@Override
				public Byte[] readData(NBTBase nbt, Byte[] obj) throws Exception {
					return ArrayUtils.toObject(((NBTTagByteArray) nbt).func_150292_c());
				}

				@Override
				public NBTBase writeData(Byte[] obj) throws Exception {
					return new NBTTagByteArray(ArrayUtils.toPrimitive(obj));
				}
			};
			setDataSerializerFor(Byte[].class, ser);
		}
		{
			DataSerializer ser = new DataSerializer<byte[]>() {
				@Override
				public byte[] readData(NBTBase nbt, byte[] obj) throws Exception {
					return ((NBTTagByteArray) nbt).func_150292_c();
				}

				@Override
				public NBTBase writeData(byte[] obj) throws Exception {
					return new NBTTagByteArray(obj);
				}
			};
			setDataSerializerFor(byte[].class, ser);
		}
		{
			DataSerializer ser = new DataSerializer<Double>() {
				@Override
				public Double readData(NBTBase nbt, Double obj) throws Exception {
					return ((NBTTagDouble) nbt).func_150286_g();
				}

				@Override
				public NBTBase writeData(Double obj) throws Exception {
					return new NBTTagDouble(obj);
				}
			};
			setDataSerializerFor(Double.TYPE, ser);
			setDataSerializerFor(Double.class, ser);
		}
		{
			DataSerializer ser = new DataSerializer<Float>() {
				@Override
				public Float readData(NBTBase nbt, Float obj) throws Exception {
					return ((NBTTagFloat) nbt).func_150288_h();
				}

				@Override
				public NBTBase writeData(Float obj) throws Exception {
					return new NBTTagFloat(obj);
				}
			};
			setDataSerializerFor(Float.TYPE, ser);
			setDataSerializerFor(Float.class, ser);
		}
		{
			DataSerializer ser = new DataSerializer<Integer>() {
				@Override
				public Integer readData(NBTBase nbt, Integer obj) throws Exception {
					return ((NBTTagInt) nbt).func_150287_d();
				}

				@Override
				public NBTBase writeData(Integer obj) throws Exception {
					return new NBTTagInt(obj);
				}
			};
			setDataSerializerFor(Integer.TYPE, ser);
			setDataSerializerFor(Integer.class, ser);
		}
		{
			DataSerializer ser = new DataSerializer<Integer[]>() {
				@Override
				public Integer[] readData(NBTBase nbt, Integer[] obj) throws Exception {
					return ArrayUtils.toObject(((NBTTagIntArray) nbt).func_150302_c());
				}

				@Override
				public NBTBase writeData(Integer[] obj) throws Exception {
					return new NBTTagIntArray(ArrayUtils.toPrimitive(obj));
				}
			};
			setDataSerializerFor(Integer[].class, ser);
		}
		{
			DataSerializer ser = new DataSerializer<int[]>() {
				@Override
				public int[] readData(NBTBase nbt, int[] obj) throws Exception {
					return ((NBTTagIntArray) nbt).func_150302_c();
				}

				@Override
				public NBTBase writeData(int[] obj) throws Exception {
					return new NBTTagIntArray(obj);
				}
			};
			setDataSerializerFor(int[].class, ser);
		}
		{
			DataSerializer ser = new DataSerializer<Long>() {
				@Override
				public Long readData(NBTBase nbt, Long obj) throws Exception {
					return ((NBTTagLong) nbt).func_150291_c();
				}

				@Override
				public NBTBase writeData(Long obj) throws Exception {
					return new NBTTagLong(obj);
				}
			};
			setDataSerializerFor(Long.TYPE, ser);
			setDataSerializerFor(Long.class, ser);
		}
		{
			DataSerializer ser = new DataSerializer<Short>() {
				@Override
				public Short readData(NBTBase nbt, Short obj) throws Exception {
					return ((NBTTagShort) nbt).func_150289_e();
				}

				@Override
				public NBTBase writeData(Short obj) throws Exception {
					return new NBTTagShort(obj);
				}
			};
			setDataSerializerFor(Short.TYPE, ser);
			setDataSerializerFor(Short.class, ser);
		}
		{
			DataSerializer ser = new DataSerializer<String>() {
				@Override
				public String readData(NBTBase nbt, String obj) throws Exception {
					return ((NBTTagString) nbt).func_150285_a_();
				}

				@Override
				public NBTBase writeData(String obj) throws Exception {
					return new NBTTagString(obj);
				}
			};
			setDataSerializerFor(String.class, ser);
		}
		//Second part: Minecraft objects.
		{
			InstanceSerializer ser = new InstanceSerializer<Entity>() {
				@Override
				public Entity readInstance(NBTBase nbt) throws Exception {
					int[] ids = ((NBTTagIntArray) nbt).func_150302_c();
					World world = ProxyHelper.getWorld(ids[0]);
					if (world != null) {
						return world.getEntityByID(ids[1]);
					}
					return null;
				}

				@Override
				public NBTBase writeInstance(Entity obj) throws Exception {
					return new NBTTagIntArray(new int[] { obj.dimension, obj.getEntityId() });
				}
			};
			setInstanceSerializerFor(Entity.class, ser);
		}
		{
			InstanceSerializer ser = new InstanceSerializer<TileEntity>() {
				@Override
				public TileEntity readInstance(NBTBase nbt) throws Exception {
					int[] ids = ((NBTTagIntArray) nbt).func_150302_c();
					World world = ProxyHelper.getWorld(ids[0]);
					if (world != null) {
						return world.getTileEntity(ids[1], ids[2], ids[3]);
					}
					return null;
				}

				@Override
				public NBTBase writeInstance(TileEntity obj) throws Exception {
					return new NBTTagIntArray(new int[] { obj.getWorldObj().provider.dimensionId,
							obj.xCoord, obj.yCoord, obj.zCoord });
				}
			};
			setInstanceSerializerFor(TileEntity.class, ser);
		}
		{
			//TODO this implementation can not be used to serialize player's inventory container.
			InstanceSerializer ser = new InstanceSerializer<Container>() {
				@Override
				public Container readInstance(NBTBase nbt) throws Exception {
					int[] ids = ((NBTTagIntArray) nbt).func_150302_c();
					World world = ProxyHelper.getWorld(ids[0]);
					if (world != null) {
						Entity entity = world.getEntityByID(ids[1]);
						if (entity instanceof EntityPlayer) {
							return ProxyHelper.getPlayerContainer((EntityPlayer) entity, ids[2]);
						}
					}
					return ProxyHelper.getPlayerContainer(null, ids[2]);
				}

				@Override
				public NBTBase writeInstance(Container obj) throws Exception {
					EntityPlayer player = ProxyHelper.getThePlayer();
					if (player != null) {
						//This is on client. The server needs player to get the Container.
						return new NBTTagIntArray(new int[] { player.worldObj.provider.dimensionId,
								player.getEntityId(), obj.windowId});
					} else {
						//This is on server. The client doesn't need player (just use thePlayer), use MAX_VALUE here.
						return new NBTTagIntArray(new int[] { Integer.MAX_VALUE, 0, obj.windowId});
					}
				}
			};
			setInstanceSerializerFor(Container.class, ser);
		}
		{
			DataSerializer ser = new DataSerializer<ItemStack>() {
				@Override
				public ItemStack readData(NBTBase nbt, ItemStack obj) throws Exception {
					if (obj == null) {
						return ItemStack.loadItemStackFromNBT((NBTTagCompound) nbt);
					} else {
						obj.readFromNBT((NBTTagCompound) nbt);
						return obj;
					}
				}

				@Override
				public NBTBase writeData(ItemStack obj) throws Exception {
					NBTTagCompound nbt = new NBTTagCompound();
					obj.writeToNBT(nbt);
					return nbt;
				}
			};
			setDataSerializerFor(ItemStack.class, ser);
		}
	}
}
