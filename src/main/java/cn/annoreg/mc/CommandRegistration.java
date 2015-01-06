package cn.annoreg.mc;

import java.lang.annotation.Annotation;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegistryType;
import cn.annoreg.core.RegistryTypeDecl;
import cn.annoreg.core.ctor.ConstructorUtils;
import cn.annoreg.core.ctor.Ctor;

@RegistryTypeDecl
public class CommandRegistration extends RegistryType {

	public CommandRegistration() {
		super(RegCommand.class, "Command");
	}

	@Override
	public boolean registerClass(AnnotationData data) {
		Class<?> cmdClass = data.getTheClass();
		try {
			ICommand cmd;
			if (cmdClass.isAnnotationPresent(Ctor.class)) {
				cmd = (ICommand) ConstructorUtils.newInstance(cmdClass, cmdClass.getAnnotation(Ctor.class));
			} else {
				cmd = (ICommand) cmdClass.newInstance();
			}
			CommandHandler ch = (CommandHandler) MinecraftServer.getServer().getCommandManager();
			ch.registerCommand(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Return false to allow registration when the server is started again.
		return false;
	}

	@Override
	public boolean registerField(AnnotationData data) {
		return false;
	}

	@Override
	public void checkLoadState() {
		//Command is not loaded at the beginning.
	}
}
