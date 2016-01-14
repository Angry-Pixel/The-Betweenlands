package thebetweenlands.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
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
			throw new CommandException("command.generic.noplayer");
		}
		EntityPlayer player = (EntityPlayer) sender;
		EntityPropertiesAspects properties = BLEntityPropertiesRegistry.HANDLER.getProperties(player, EntityPropertiesAspects.class);
		if(args.length < 2) {
			throw new CommandException("commands.generic.syntax");
		}
		WorldServer blWorld = DimensionManager.getWorld(ConfigHandler.DIMENSION_ID);
		AspectManager manager = AspectManager.get(blWorld);
		switch(args[0]) {
		case "discover":
			switch(args[1]) {
			case "held":
				if(player.getHeldItem() == null) {
					throw new CommandException("command.aspectdiscovery.held.null");
				}
				AspectDiscovery discovery = properties.discover(manager, new AspectItem(player.getHeldItem()));
				sender.addChatMessage(new ChatComponentTranslation("command.aspectdiscovery.discovered.held", new ChatComponentText(discovery.result.toString()), new ChatComponentText(discovery.discovered == null ? "null" : discovery.discovered.aspect.getName())));
				break;
			case "all":
				properties.discoverAll();
				sender.addChatMessage(new ChatComponentTranslation("command.aspectdiscovery.discovered.all"));
				break;
			default:
				throw new CommandException("commands.generic.syntax");
			}
			break;
		case "reset":
			switch(args[1]) {
			case "held":
				if(player.getHeldItem() == null) {
					throw new CommandException("command.aspectdiscovery.held.null");
				}
				properties.resetDiscovery(new AspectItem(player.getHeldItem()));
				sender.addChatMessage(new ChatComponentTranslation("command.aspectdiscovery.reset.held"));
				break;
			case "all":
				properties.resetAllDiscovery();
				sender.addChatMessage(new ChatComponentTranslation("command.aspectdiscovery.reset.all"));
				break;
			default:
				throw new CommandException("commands.generic.syntax");
			}
			break;
		default:
			throw new CommandException("commands.generic.syntax");
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
