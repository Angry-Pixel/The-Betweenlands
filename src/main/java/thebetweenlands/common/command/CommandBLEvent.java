package thebetweenlands.common.command;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.event.EnvironmentEvent;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;

public class CommandBLEvent extends CommandBase {
	private List<String> childCommands = Arrays.asList("toggle", "on", "off", "list", "enable", "disable");

	@Override
	public String getCommandName() {
		return "blevent";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "command.blevent.usage";
	}

	private void checkArg(String[] args, int length, String usage) throws CommandException {
		if (args.length < length) {
			throw new CommandException("command.blevent.usage." + usage);
		}
	}

	private void processToggle(ICommandSender sender, String[] args) throws PlayerNotFoundException, CommandException {
		checkArg(args, 2, "toggle");
		String eventName = getChatComponentFromNthArg(sender, args, 1).getUnformattedText();
		EnvironmentEvent event = getEnvironentEvent(sender, eventName);
		boolean isActive = event.isActive();
		event.setActive(!isActive, true);
		notifyCommandListener(sender, this, "command.blevent.success." + (isActive ? "off" : "on"), eventName);
	}

	private void processOn(ICommandSender sender, String[] args) throws PlayerNotFoundException, CommandException {
		checkArg(args, 2, "on");
		processEventState(sender, getChatComponentFromNthArg(sender, args, 1).getUnformattedText(), true);
	}

	private void processOff(ICommandSender sender, String[] args) throws PlayerNotFoundException, CommandException {
		if (args.length < 2) {
			EnvironmentEventRegistry environmentEventRegistry = getEnvironmentEventRegistry(sender);
			for (EnvironmentEvent event : environmentEventRegistry.getActiveEvents()) {
				event.setActive(false, true);
			}
			notifyCommandListener(sender, this, "command.blevent.success.alloff");
		} else {
			processEventState(sender, getChatComponentFromNthArg(sender, args, 1).getUnformattedText(), false);
		}
	}

	private void processEnable(ICommandSender sender, String[] args) throws CommandException {
		EnvironmentEventRegistry environmentEventRegistry = getEnvironmentEventRegistry(sender);
		if (environmentEventRegistry.isEnabled()) {
			throw new CommandException("command.blevent.failure.alreadyenabled");
		}
		environmentEventRegistry.enable();
		notifyCommandListener(sender, this, "command.blevent.success.enable");
	}

	private void processDisable(ICommandSender sender, String[] args) throws CommandException {
		EnvironmentEventRegistry environmentEventRegistry = getEnvironmentEventRegistry(sender);
		if (environmentEventRegistry.isDisabled()) {
			throw new CommandException("command.blevent.failure.alreadydisabled");
		}
		environmentEventRegistry.disable();
		for (EnvironmentEvent event : environmentEventRegistry.getActiveEvents()) {
			event.setActive(false, true);
		}
		notifyCommandListener(sender, this, "command.blevent.success.disable");
	}

	private void processList(ICommandSender sender) throws CommandException {
		EnvironmentEventRegistry environmentEventRegistry = getEnvironmentEventRegistry(sender);
		sender.addChatMessage(new TextComponentString(environmentEventRegistry.getGrammaticalActiveEventNameList()));
	}

	private void processEventState(ICommandSender sender, String eventName, boolean isActive) throws CommandException {
		EnvironmentEvent event = getEnvironentEvent(sender, eventName);
		if (event.isActive() == isActive) {
			throw new CommandException("command.blevent.failure.already" + (isActive ? "on" : "off"), eventName);
		} else {
			event.setActive(isActive, true);
			notifyCommandListener(sender, this, "command.blevent.success." + (isActive ? "on" : "off"), eventName);
		}
	}

	@Override
	public List getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (!(sender.getEntityWorld().provider instanceof WorldProviderBetweenlands)) {
			return null;
		}
		List<String> completions = null;
		if (args.length == 1) {
			completions = childCommands;
		} else if (args.length == 2) {
			try {
				switch (args[0]) {
				case "toggle":
					completions = getEnvironmentEventRegistry(sender).getEventNames();
					break;
				case "on":
					completions = getEnvironmentEventRegistry(sender).getEventNamesOfState(false);
					break;
				case "off":
					completions = getEnvironmentEventRegistry(sender).getEventNamesOfState(true);
					break;
				}
			} catch(Exception ex) { }
		}
		return completions == null ? null : getListOfStringsMatchingLastWord(args, completions.toArray(new String[0]));
	}

	private EnvironmentEventRegistry getEnvironmentEventRegistry(ICommandSender sender) throws CommandException {
		World world = sender.getEntityWorld();
		if (world.provider instanceof WorldProviderBetweenlands) {
			return ((WorldProviderBetweenlands) world.provider).getWorldData().getEnvironmentEventRegistry();
		} else {
			throw new CommandException("command.blevent.failure.wrongdimension");
		}
	}

	private EnvironmentEvent getEnvironentEvent(ICommandSender sender, String eventName) throws CommandException {
		EnvironmentEventRegistry environmentEventRegistry = getEnvironmentEventRegistry(sender);
		EnvironmentEvent event = environmentEventRegistry.forName(eventName);
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
