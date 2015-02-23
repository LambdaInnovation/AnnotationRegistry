package cn.annoreg.asm;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;

public class InnerClassVisitor extends ClassVisitor {
    List<String> innerClasses = new ArrayList();
    boolean isReg = false;
    boolean clientOnly = false;

    public InnerClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }
    
    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        innerClasses.add(name.replace('/', '.'));
        super.visitInnerClass(name, outerName, innerName, access);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(desc, visible);
        if (desc.equals("Lcn/annoreg/core/RegistrationClass;")) {
            isReg = true;
        }
        if (desc.equals("Lcpw/mods/fml/relauncher/SideOnly;")) {
            clientOnly = true;
            //Just check existence. SideOnly(SERVER) is not allowed. 
        }
        return av;
    }
    
    public List<String> getInnerClassList() {
        if (isReg && (FMLLaunchHandler.side() == Side.CLIENT || !clientOnly))
            return innerClasses;
        else {
            innerClasses.clear();
            return null;
        }
    }
}
