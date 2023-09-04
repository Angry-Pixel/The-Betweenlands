package thebetweenlands.common.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class CommandBLEvent extends CommandBase {
	private List<String> childCommands = Arrays.asList("toggle", "on", "off", "list", "enable", "disable");

	@Override
	public String getName() {
		return "blevent";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.blevent.usage";
	}

	private void checkArg(String[] args, int length, String usage) throws CommandException {
		if (args.length < length) {
			throw new CommandException("command.blevent.usage." + usage);
		}
	}

	private void processToggle(ICommandSender sender, String[] args) throws PlayerNotFoundException, CommandException {
		checkArg(args, 2, "toggle");
		ResourceLocation eventName = new ResourceLocation(getChatComponentFromNthArg(sender, args, 1).getUnformattedText());
		IEnvironmentEvent event = getEnvironentEvent(sender, eventName);
		boolean isActive = event.isActive();
		event.setActive(!isActive);
		if(event.isActive() == !isActive) {
			notifyCommandListener(sender, this, "command.blevent.success." + (isActive ? "off" : "on"), eventName);
		} else {
			throw new CommandException("command.blevent.failure." + (isActive ? "off" : "on"), eventName);
		}
	}

	private void processOn(ICommandSender sender, String[] args) throws PlayerNotFoundException, CommandException {
		checkArg(args, 2, "on");
		processEventState(sender, new ResourceLocation(getChatComponentFromNthArg(sender, args, 1).getUnformattedText()), true);
	}

	private void processOff(ICommandSender sender, String[] args) throws PlayerNotFoundException, CommandException {
		if (args.length < 2) {
			BLEnvironmentEventRegistry environmentEventRegistry = getEnvironmentEventRegistry(sender);
			for (IEnvironmentEvent event : environmentEventRegistry.getActiveEvents()) {
				event.setActive(false);
			}
			notifyCommandListener(sender, this, "command.blevent.success.alloff");
		} else {
			processEventState(sender, new ResourceLocation(getChatComponentFromNthArg(sender, args, 1).getUnformattedText()), false);
		}
	}

	private void processEnable(ICommandSender sender, String[] args) throws CommandException {
		BLEnvironmentEventRegistry environmentEventRegistry = getEnvironmentEventRegistry(sender);
		if (environmentEventRegistry.isEnabled()) {
			throw new CommandException("command.blevent.failure.alreadyenabled");
		}
		environmentEventRegistry.enable();
		notifyCommandListener(sender, this, "command.blevent.success.enable");
	}

	private void processDisable(ICommandSender sender, String[] args) throws CommandException {
		BLEnvironmentEventRegistry environmentEventRegistry = getEnvironmentEventRegistry(sender);
		if (environmentEventRegistry.isDisabled()) {
			throw new CommandException("command.blevent.failure.alreadydisabled");
		}
		environmentEventRegistry.disable();
		for (IEnvironmentEvent event : environmentEventRegistry.getActiveEvents()) {
			event.setActive(false);
		}
		notifyCommandListener(sender, this, "command.blevent.success.disable");
	}

	private void processList(ICommandSender sender) throws CommandException {
		BLEnvironmentEventRegistry environmentEventRegistry = getEnvironmentEventRegistry(sender);
		sender.sendMessage(new TextComponentString(environmentEventRegistry.getGrammaticalActiveEventNameList()));
	}

	private void processEventState(ICommandSender sender, ResourceLocation eventName, boolean isActive) throws CommandException {
		IEnvironmentEvent event = getEnvironentEvent(sender, eventName);
		if (event.isActive() == isActive) {
			throw new CommandException("command.blevent.failure.already" + (isActive ? "on" : "off"), eventName);
		} else {
			event.setActive(isActive);
			if(event.isActive() == isActive) {
				notifyCommandListener(sender, this, "command.blevent.success." + (isActive ? "on" : "off"), eventName);
			} else {
				throw new CommandException("command.blevent.failure." + (isActive ? "on" : "off"), eventName);
			}
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (BetweenlandsWorldStorage.forWorldNullable(sender.getEntityWorld()) == null) {
			return Collections.<String>emptyList();
		}
		List<String> completions = null;
		if (args.length == 1) {
			completions = childCommands;
		} else if (args.length == 2) {
			try {
				List<ResourceLocation> foundEvents = null;
				switch (args[0]) {
				case "toggle":
					foundEvents = getEnvironmentEventRegistry(sender).getEventNames();
					break;
				case "on":
					foundEvents = getEnvironmentEventRegistry(sender).getEventNamesOfState(false);
					break;
				case "off":
					foundEvents = getEnvironmentEventRegistry(sender).getEventNamesOfState(true);
					break;
				}
				if(foundEvents != null && !foundEvents.isEmpty()) {
					completions = new ArrayList<>();
					for(ResourceLocation event : foundEvents) {
						completions.add(event.toString());
					}
				}
			} catch(Exception ex) { }
		}
		return completions == null ? Collections.<String>emptyList() : getListOfStringsMatchingLastWord(args, completions.toArray(new String[0]));
	}

	private BLEnvironmentEventRegistry getEnvironmentEventRegistry(ICommandSender sender) throws CommandException {
		World world = sender.getEntityWorld();
		BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(world);
		if(storage != null) {
			return storage.getEnvironmentEventRegistry();
		} else {
			throw new CommandException("command.blevent.failure.wrongdimension");
		}
	}

	private IEnvironmentEvent getEnvironentEvent(ICommandSender sender, ResourceLocation eventName) throws CommandException {
		BLEnvironmentEventRegistry environmentEventRegistry = getEnvironmentEventRegistry(sender);
		IEnvironmentEvent event = environmentEventRegistry.forName(eventName);
		if (event == null) {
			throw new CommandException("command.blevent.failure.unknown", eventName);
		}
		return event;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new CommandException("command.blevent.usage");
		}
		switch (args[0]) {
		case "toggle":
			processToggle(sender, args);
			break;
		case "on":
			processOn(sender, args);
			break;
		case "off":
			processOff(sender, args);
			break;
		case "list":
			processList(sender);
			break;
		case "enable":
			processEnable(sender, args);
			break;
		case "disable":
			processDisable(sender, args);
			break;
		default:
			throw new CommandException("command.blevent.usage");
		}		
	}
}
