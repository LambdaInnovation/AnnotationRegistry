/**
 * Copyright (c) Lambda Innovation, 2013-2015
 * 本作品版权由Lambda Innovation所有。
 * http://www.li-dev.cn/
 *
 * This project is open-source, and it is distributed under
 * the terms of GNU General Public License. You can modify
 * and distribute freely as long as you follow the license.
 * 本项目是一个开源项目，且遵循GNU通用公共授权协议。
 * 在遵照该协议的情况下，您可以自由传播和修改。
 * http://www.gnu.org/licenses/gpl.html
 */
package cn.annoreg.asm;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

import cn.annoreg.core.Registrant;

public class InnerClassVisitor extends ClassVisitor {
    
    List<String> innerClasses = new ArrayList();
    boolean isReg = false;
    boolean clientOnly = false;

    public InnerClassVisitor(int api) {
        super(api);
    }
    
    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        innerClasses.add(name.replace('/', '.'));
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (desc.equals(Type.getDescriptor(SideOnly.class))) {
            //We need to know if it's client only.
            return new AnnotationVisitor(api) {
                @Override
                public void visitEnum(String name, String desc, String value) {
                    if (value == Side.CLIENT.toString()) {
                        InnerClassVisitor.this.clientOnly = true;
                    }
                }
            };
        }
        if (desc.equals(Type.getDescriptor(Registrant.class))) {
            isReg = true;
        }
        return null;
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
