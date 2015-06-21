package thebetweenlands.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.event.debugging.DebugHandler;
import thebetweenlands.network.packets.PacketTickspeed;

import com.google.common.primitives.Floats;

public class CommandTickSpeed extends CommandBase {
	@Override
	public String getCommandName() {
		return "tickspeed";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.tickspeed.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length < 1) {
			throw new CommandException("commands.tickspeed.usage", new Object[0]);
		}
		float ticksPerSecond = parseFloat(args[0]);
		DebugHandler.setSleepPerTick((long) (1000 / ticksPerSecond));
		TheBetweenlands.networkWrapper.sendToAll(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketTickspeed(ticksPerSecond)));
        func_152373_a(sender, this, "commands.tickspeed.success", new Object[] { String.valueOf(ticksPerSecond) });
	}

	public static float parseFloat(String string) {
		try {
			float value = Float.parseFloat(string);

			if (!Floats.isFinite(value)) {
				throw new NumberInvalidException("commands.generic.num.invalid", new Object[] { string });
			} else {
				return value;
			}
		} catch (NumberFormatException numberformatexception) {
			throw new NumberInvalidException("commands.generic.num.invalid", new Object[] { string });
		}
	}
}
