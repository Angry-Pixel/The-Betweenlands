package thebetweenlands.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesAspects;
import thebetweenlands.entities.properties.list.EntityPropertiesAspects.AspectDiscovery;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.AspectManager.AspectItem;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class CommandDiscoverAspects extends CommandBase {
	public String getCommandName() {
		return "aspectDiscovery";
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/aspectDiscovery <reset|discover> <held|all>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(sender instanceof EntityPlayer == false) {
			throw new CommandException("Must be player");
		}
		EntityPlayer player = (EntityPlayer) sender;
		EntityPropertiesAspects properties = BLEntityPropertiesRegistry.HANDLER.getProperties(player, EntityPropertiesAspects.class);
		if(properties == null) {
			throw new CommandException("Aspect properties not available");
		}
		if(args.length < 2) {
			throw new CommandException("Not enough arguments");
		}
		WorldServer blWorld = DimensionManager.getWorld(ConfigHandler.DIMENSION_ID);
		if(blWorld == null) {
			throw new CommandException("Betweenlands world is null");
		}
		AspectManager manager = AspectManager.get(blWorld);
		switch(args[0]) {
		case "discover":
			switch(args[1]) {
			case "held":
				if(player.getHeldItem() == null) {
					throw new CommandException("Held item must not be null");
				}
				AspectDiscovery discovery = properties.discover(manager, new AspectItem(player.getHeldItem()));
				sender.addChatMessage(new ChatComponentText("Result: " + discovery.result.toString() + " Aspect: " + (discovery.discovered == null ? "null" : discovery.discovered.aspect.getName())));
				break;
			case "all":
				properties.discoverAll();
				sender.addChatMessage(new ChatComponentText("Discovered all aspects of all items"));
				break;
			default:
				throw new CommandException("Incorrect usage");
			}
			break;
		case "reset":
			switch(args[1]) {
			case "held":
				if(player.getHeldItem() == null) {
					throw new CommandException("Held item must not be null");
				}
				properties.resetDiscovery(new AspectItem(player.getHeldItem()));
				sender.addChatMessage(new ChatComponentText("Removed discovered aspects"));
				break;
			case "all":
				properties.resetAllDiscovery();
				sender.addChatMessage(new ChatComponentText("Removed discovered aspects from all items"));
				break;
			default:
				throw new CommandException("Incorrect usage");
			}
			break;
		default:
			throw new CommandException("Incorrect usage");
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		List<String> completions = new ArrayList<String>();
		if(args.length == 1) {
			completions.add("reset");
			completions.add("discover");
		} else if(args.length == 2) {
			completions.add("all");
			completions.add("held");
		}
		return getListOfStringsMatchingLastWord(args, completions.toArray(new String[0]));
	}
}
