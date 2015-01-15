package cn.annoreg.mc.s11n;

import cn.annoreg.base.RegistrationClassSimple;
import cn.annoreg.core.RegistryTypeDecl;

@RegistryTypeDecl
public class SerializableRegistration extends RegistrationClassSimple<RegSerializable, Object> {

	public SerializableRegistration() {
		super(RegSerializable.class, "Serializable");
	}

	@Override
	protected void register(Class<? extends Object> theClass, RegSerializable anno) throws Exception {
		if (SerializationManager.INSTANCE.getInstanceSerializer(theClass) == null) {
			if (!anno.instance().equals(InstanceSerializer.class)) {
				SerializationManager.INSTANCE.setInstanceSerializerFor(theClass, anno.instance().newInstance());
			}
		}
		if (!SerializationManager.INSTANCE.hasDataSerializer(theClass)) {
			if (anno.data().equals(DataSerializer.class)) {
				SerializationManager.INSTANCE.setDataSerializerFor(theClass, 
						SerializationManager.INSTANCE.createAutoSerializerFor(theClass));
			} else {
				SerializationManager.INSTANCE.setDataSerializerFor(theClass, anno.data().newInstance());
			}
		}
	}

}
