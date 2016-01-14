package thebetweenlands.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.decay.DecayManager;

public class CommandDecay extends CommandBase {
	public String getCommandName() {
		return "setDecay";
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/setDecay <decay>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(sender.getEntityWorld() == null || sender instanceof EntityPlayer == false) {
			throw new CommandException("command.generic.noplayer");
		}
		String arg = args[0];
		try {
			DecayManager.setDecayLevel(Integer.parseInt(arg), (EntityPlayer)sender);
		} catch(Exception ex) {
			throw new CommandException("command.decay.noint");
		}
	}
}
