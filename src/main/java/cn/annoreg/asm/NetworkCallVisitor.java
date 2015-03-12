package cn.annoreg.asm;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cn.annoreg.asm.network.NetworkCallTransformer;
import cn.annoreg.mc.network.NetworkCallManager;

public class NetworkCallVisitor extends ClassVisitor {
    
    private boolean isReg;
    private List<ClassMethod> methods = new ArrayList();
    private String className;

    public NetworkCallVisitor(int api, String className) {
        super(api);
        this.className = className;
    }
    
    public class ClassMethod {
        public String name;
        public String desc;
        
        public ClassMethod(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }
    }

    public boolean needTransform() {
        return isReg && !methods.isEmpty();
    }
    
    public ClassVisitor getTransformer(ClassWriter cw) {
        return new NetworkCallTransformer(this.api, cw, className, methods);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new NetworkCallMethodVisitor(this.api, name, desc);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (desc.equals("Lcn/annoreg/core/RegistrationClass;")) {
            isReg = true;
        }
        return null;
    }
    
    private class NetworkCallMethodVisitor extends MethodVisitor {
        private String name;
        private String desc;
        
        public NetworkCallMethodVisitor(int api, String name, String desc) {
            super(api);
            this.name = name;
            this.desc = desc;
        }
        
        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (desc.equals("Lcn/annoreg/mc/network/RegNetworkCall;")) {
                methods.add(new ClassMethod(this.name, this.desc));
            }
            return null;
        }
    }
     
}
