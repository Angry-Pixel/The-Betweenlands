package thebetweenlands.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.ManualManager;

/**
 * Created by Bart on 28/11/2015.
 */
public class CommandFindPage extends CommandBase {

	@Override
	public String getCommandName() {
		return "findPage";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/findPage (pageName)";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (sender.getEntityWorld() == null || !(sender instanceof EntityPlayer)) {
			throw new CommandException("command.generic.noplayer");
		}
		if (ManualManager.findablePagesGuideBook.contains(args[0]))
			ManualManager.findPage((EntityPlayer) sender, args[0], BLItemRegistry.manualGuideBook);
		else if (ManualManager.findablePagesHL.contains(args[0]))
			ManualManager.findPage((EntityPlayer) sender, args[0], BLItemRegistry.manualHL);
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, ManualManager.findablePagesAll.toArray(new String[0])) : null;
	}

}
