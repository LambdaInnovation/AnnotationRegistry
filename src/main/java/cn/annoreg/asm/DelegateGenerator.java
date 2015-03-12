package cn.annoreg.asm;

import net.minecraft.entity.player.EntityPlayer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import cpw.mods.fml.relauncher.Side;
import cn.annoreg.mc.network.NetworkCallDelegate;
import cn.annoreg.mc.network.NetworkCallManager;
import cn.annoreg.mc.s11n.StorageOption;

public class DelegateGenerator {
    
    /**
     * Class loader used to generate delegate class.
     *
     */
    private static class DelegateClassLoader extends ClassLoader {
        public DelegateClassLoader() {
            super(NetworkCallDelegate.class.getClassLoader());
        }
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
    
    private static final DelegateClassLoader classLoader = new DelegateClassLoader();
    
    private static int delegateNextID = 0;
    
    public static MethodVisitor generateStaticMethod(MethodVisitor parent, 
            String className, String methodName, String desc, final Side side) {
        
        //This method is a little bit complicated.
        //We need to generate a delegate class implementing NetworkCallDelegate and a redirect
        //the code that originally generated here in parent, into the delegate class,
        //by returning a MethodVisitor under the ClassVisitor of the delegate class.
        //Besides, we should generate a call to NetworkCallManager into parent.
        
        //delegateName is a string used by both sides to identify a network-call delegate.
        final String delegateName = className + ":" + methodName + ":" + desc;
        final Type[] args = Type.getArgumentTypes(desc);
        final Type ret = Type.getReturnType(desc);
        
        //Check types
        for (Type t : args) {
            if (!t.getDescriptor().startsWith("L") && !t.getDescriptor().startsWith("[")) {
                throw new RuntimeException("Unsupported argument type in network call. ");
            }
        }
        if (!ret.equals(Type.VOID_TYPE)) {
            throw new RuntimeException("Unsupported return value type in network call. " + 
                    "Only void is supported.");
        }
        
        //Generate call to NetworkCallManager in parent.
        parent.visitCode();
        //First parameter
        parent.visitLdcInsn(delegateName);
        //Second parameter: object array
        parent.visitLdcInsn(args.length); //array size
        parent.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
        for (int i = 0; i < args.length; ++i) {
            parent.visitInsn(Opcodes.DUP);
            parent.visitLdcInsn(i);
            parent.visitVarInsn(Opcodes.ALOAD, i);
            parent.visitInsn(Opcodes.AASTORE);
        }
        //Call cn.annoreg.mc.network.NetworkCallManager.onNetworkCall
        parent.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(NetworkCallManager.class), 
                "onNetworkCall", "(Ljava/lang/String;[Ljava/lang/Object;)V");
        parent.visitInsn(Opcodes.RETURN);
        parent.visitMaxs(5, args.length);
        parent.visitEnd();
        
        //Create delegate object.
        final ClassWriter cw = new ClassWriter(Opcodes.ASM4);
        final String delegateClassName = "cn.annoreg.asm.NetworkCallDelegate_" + Integer.toString(delegateNextID++);
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, delegateClassName.replace('.', '/'), null, 
                "java/lang/Object", new String[]{"cn/annoreg/mc/network/NetworkCallDelegate"});
        //package cn.annoreg.asm;
        //class NetworkCallDelegate_? implements NetworkCallDelegate {
        {
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        //public NetworkCallDelegate_?() {}
        {
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "invoke", "([Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            for (int i = 0; i < args.length; ++i) {
                mv.visitVarInsn(Opcodes.ALOAD, 1); //0 is this
                mv.visitLdcInsn(i);
                mv.visitInsn(Opcodes.AALOAD);
                mv.visitTypeInsn(Opcodes.CHECKCAST, args[i].getInternalName());
            }
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, delegateClassName.replace('.', '/'), "delegated", desc);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(args.length + 2, 2);
            mv.visitEnd();
        }
        //@Override public void invoke(Object[] args) {
        //    delegated((Type0) args[0], (Type1) args[1], ...);
        //}
        
        //The returned MethodVisitor will visit the original version of the method,
        //including its annotation, where we can get StorageOptions.
        return new MethodVisitor(Opcodes.ASM4, 
                cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "delegated", desc, null, null)) {
            
            //Remember storage options for each argument
            StorageOption.Option[] options = new StorageOption.Option[args.length];
            int targetIndex = -1;
            
            {
                for (int i = 0; i < options.length; ++i) {
                    options[i] = StorageOption.Option.NULL; //set default value
                }
            }
            
            @Override
            public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
                Type type = Type.getType(desc);
                if (type.equals(Type.getType(StorageOption.Data.class))) {
                    options[parameter] = StorageOption.Option.DATA;
                } else if (type.equals(Type.getType(StorageOption.Instance.class))) {
                    options[parameter] = StorageOption.Option.INSTANCE;
                } else if (type.equals(Type.getType(StorageOption.Update.class))) {
                    options[parameter] = StorageOption.Option.UPDATE;
                } else if (type.equals(Type.getType(StorageOption.Null.class))) {
                    options[parameter] = StorageOption.Option.NULL;
                } else if (type.equals(Type.getType(StorageOption.Target.class))) {
                    if (!args[parameter].equals(Type.getType(EntityPlayer.class))) {
                        throw new RuntimeException("Target annotation can only be used on EntityPlayer.");
                    }
                    options[parameter] = StorageOption.Option.INSTANCE;
                    targetIndex = parameter;
                }
                return super.visitParameterAnnotation(parameter, desc, visible);
            }
            
            @Override
            public void visitEnd() {
                super.visitEnd();
                //This is the last method in the delegate class.
                //Finish the class and do the registration.
                cw.visitEnd();
                try {
                    Class<?> clazz = classLoader.defineClass(delegateClassName, cw.toByteArray());
                    NetworkCallDelegate delegateObj = (NetworkCallDelegate) clazz.newInstance(); 
                    if (side == Side.CLIENT) {
                        NetworkCallManager.registerClientDelegateClass(delegateName, delegateObj, options, targetIndex);
                    } else {
                        NetworkCallManager.registerServerDelegateClass(delegateName, delegateObj, options);
                    }
                } catch (Throwable e) {
                    throw new RuntimeException("Can not create delegate for network call.", e);
                }
            }
        };
        //public static void delegated(Type0 arg0, Type1, arg1, ...) {
        //    //Code generated by caller.
        //}
        //}
    }
    
}
