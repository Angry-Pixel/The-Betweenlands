package thebetweenlands.common.command;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import thebetweenlands.api.aspect.DiscoveryContainer;
import thebetweenlands.api.aspect.DiscoveryContainer.AspectDiscovery;
import thebetweenlands.common.herblore.aspect.AspectManager;

public class CommandAspectDiscovery extends CommandBase {
	public String getName() {
		return "aspectDiscovery";
	}

	public String getUsage(ICommandSender sender) {
		return "/aspectDiscovery <reset|discover> <held|all>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
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
	

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer == false) {
			throw new CommandException("command.generic.noplayer");
		}
		if(args.length < 2) {
			throw new CommandException("commands.generic.syntax");
		}
		EntityPlayer player = (EntityPlayer) sender;
		if(!DiscoveryContainer.hasDiscoveryProvider(player)) {
			throw new CommandException("command.aspectdiscovery.book.none");
		}
		AspectManager manager = AspectManager.get(sender.getEntityWorld());
		switch(args[0]) {
		case "discover":
			switch(args[1]) {
			case "held":
				if(player.getHeldItemMainhand() == null) {
					throw new CommandException("command.aspectdiscovery.held.null");
				}
				DiscoveryContainer<?> mergedKnowledge = DiscoveryContainer.getMergedDiscoveryContainer(player);
				AspectDiscovery discovery = mergedKnowledge.discover(manager, manager.getAspectItem(player.getHeldItemMainhand()));
				if(discovery.discovered != null) {
					DiscoveryContainer.addDiscoveryToContainers(player, manager.getAspectItem(player.getHeldItemMainhand()), discovery.discovered.type);
					sender.sendMessage(new TextComponentTranslation("command.aspectdiscovery.discover.held", new TextComponentString(discovery.result.toString()), new TextComponentString(discovery.discovered == null ? "null" : discovery.discovered.type.getName())));
				}
				break;
			case "all":
				List<DiscoveryContainer<?>> discoveryContainers = DiscoveryContainer.getWritableDiscoveryContainers(player);
				for(DiscoveryContainer<?> container : discoveryContainers)
					container.discoverAll(manager);
				sender.sendMessage(new TextComponentTranslation("command.aspectdiscovery.discover.all"));
				break;
			default:
				throw new CommandException("commands.generic.syntax");
			}
			break;
		case "reset":
			List<DiscoveryContainer<?>> discoveryContainers = DiscoveryContainer.getWritableDiscoveryContainers(player);
			switch(args[1]) {
			case "held":
				if(player.getHeldItemMainhand() == null) {
					throw new CommandException("command.aspectdiscovery.held.null");
				}
				for(DiscoveryContainer<?> container : discoveryContainers)
					container.resetDiscovery(manager.getAspectItem(player.getHeldItemMainhand()));
				sender.sendMessage(new TextComponentTranslation("command.aspectdiscovery.reset.held"));
				break;
			case "all":
				for(DiscoveryContainer<?> container : discoveryContainers)
					container.resetAllDiscovery();
				sender.sendMessage(new TextComponentTranslation("command.aspectdiscovery.reset.all"));
				break;
			default:
				throw new CommandException("commands.generic.syntax");
			}
			break;
		default:
			throw new CommandException("commands.generic.syntax");
		}
	}
}
