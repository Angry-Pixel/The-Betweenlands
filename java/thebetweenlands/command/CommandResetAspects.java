package thebetweenlands.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import thebetweenlands.herblore.aspects.AspectManager;

public class CommandResetAspects extends CommandBase {
	private boolean confirm = false;

	public String getCommandName() {
		return "resetAspects";
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/resetAspects";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(sender.getEntityWorld() == null) {
			sender.addChatMessage(new ChatComponentText("World is null"));
			return;
		}
		if (args.length < 1) {
			this.confirm = true;
			sender.addChatMessage(new ChatComponentText("Confirm with '/resetAspects confirm'"));
			return;
		}
		if(!this.confirm) {
			return;
		}

		this.confirm = false;

		String arg1 = args[0];

		if(arg1.equals("confirm")) {
			World world = sender.getEntityWorld();
			AspectManager.get(world).resetStaticAspects(AspectManager.getAspectsSeed(world.getSeed()));
			sender.addChatMessage(new ChatComponentText("The aspects have been reset"));
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		List<String> completions = null;
		if (this.confirm && args.length == 1) {
			completions = new ArrayList<String>();
			completions.add("confirm");
		}
		return completions == null ? null : getListOfStringsMatchingLastWord(args, completions.toArray(new String[0]));
	}
}
