package cn.annoreg.mc;

import java.lang.annotation.Annotation;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import cn.annoreg.base.RegistrationClassRepeat;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryTypeDecl;

@RegistryTypeDecl
public class CommandRegistration extends RegistrationClassRepeat<RegCommand, ICommand> {

	public CommandRegistration() {
		super(RegCommand.class, "Command");
	}

	@Override
	protected void register( Class<? extends ICommand> theClass, RegCommand anno) throws Exception {
		CommandHandler ch = (CommandHandler) MinecraftServer.getServer().getCommandManager();
		ch.registerCommand(theClass.newInstance());
	}
}
