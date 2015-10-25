package thebetweenlands.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import thebetweenlands.herblore.aspects.IAspect;
import thebetweenlands.manager.AspectManager;

public class CommandAspect extends CommandBase {
	public String getCommandName() {
		return "aspect";
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/aspect <aspect>";
	}

	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.addChatMessage(new ChatComponentText("args.length < 1"));
			return;
		} else if (!(sender instanceof EntityPlayer)) {
			sender.addChatMessage(new ChatComponentText("!(sender instanceof EntityPlayer)"));
			return;
		}

		EntityPlayer player = (EntityPlayer) sender;
		ItemStack itemStack = player.getCurrentEquippedItem();
		IAspect aspect = AspectManager.getAspectByName(args[0]);

		if (itemStack == null) {
			player.addChatComponentMessage(new ChatComponentText("itemStack == null"));
			return;
		} else if (aspect == null) {
			player.addChatComponentMessage(new ChatComponentText("aspect == null"));
			return;
		}

		AspectManager.addAspectToStack(aspect, itemStack);
	}
}
