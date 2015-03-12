package cn.annoreg.asm.network;

import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cpw.mods.fml.relauncher.Side;
import cn.annoreg.asm.NetworkCallVisitor.ClassMethod;
import cn.annoreg.mc.network.NetworkCallManager;

public class NetworkCallTransformer extends ClassVisitor {
    private List<ClassMethod> methods;
    private String className;
    
    public NetworkCallTransformer(int api, ClassVisitor cv, String className, List<ClassMethod> methods) {
        super(api, cv);
        this.className = className;
        this.methods = methods;
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        for (ClassMethod m : methods) {
            if (m.name == name && m.desc == desc) {
                switch (access) {
                case Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC:
                    return DelegateGenerator.generateStaticMethod(
                            super.visitMethod(access, name, desc, signature, exceptions),
                            className, name, desc, m.side);
                case Opcodes.ACC_PUBLIC:
                    //TODO support for non-static method
                default:
                    throw new RuntimeException("Unsupported access flag in network call.");
                }
            }
        }
        //Not found
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}