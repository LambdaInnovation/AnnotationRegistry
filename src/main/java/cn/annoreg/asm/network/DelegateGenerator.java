package cn.annoreg.asm.network;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import cn.annoreg.mc.network.NetworkCallDelegate;
import cn.annoreg.mc.network.NetworkCallManager;

public class DelegateGenerator {

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
            String className, String methodName, String desc) {
        
        final String delegateName = className + ":" + methodName + ":" + desc;
        final Type[] args = Type.getArgumentTypes(desc);
        final Type ret = Type.getReturnType(desc);
        
        //Generate call to NetworkCallManager in parent.
        //TODO push params
        parent.visitCode();
        parent.visitLdcInsn(delegateName);
        parent.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/annoreg/mc/network/NetworkCallManager", 
                "onNetworkCall", "(Ljava/lang/String;)V");
        parent.visitInsn(Opcodes.RETURN);
        parent.visitMaxs(1, 0);
        parent.visitEnd();
        
        //Create delegate object.
        final ClassWriter cw = new ClassWriter(Opcodes.ASM4);
        final String delegateClassName = "cn.annoreg.asm.NetworkCallDelegate_" + Integer.toString(delegateNextID++);
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, delegateClassName.replace('.', '/'), null, 
                "java/lang/Object", new String[]{"cn/annoreg/mc/network/NetworkCallDelegate"});
        {
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "invoke", "([Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            //TODO call method delegated
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 2);
            mv.visitEnd();
        }
        return new MethodVisitor(Opcodes.ASM4, 
                cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "delegated", desc, null, null)) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                cw.visitEnd();
                try {
                    Class<?> clazz = classLoader.defineClass(delegateClassName, cw.toByteArray());
                    NetworkCallDelegate delegateObj = (NetworkCallDelegate) clazz.newInstance(); 
                    NetworkCallManager.registerDelegateClass(delegateName, delegateObj);
                } catch (Throwable e) {
                    throw new RuntimeException("Can not create delegate for network call.", e);
                }
            }
        };
    }
}
