package thebetweenlands.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.event.debugging.DebugHandlerClient;
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
		return "command.tickspeed.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length < 1) {
			throw new CommandException("command.tickspeed.usage");
		}
		float ticksPerSecond = parseFloat(args[0]);
		DebugHandlerClient.setSleepPerTick((long) (1000 / ticksPerSecond));
		TheBetweenlands.networkWrapper.sendToAll(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketTickspeed(ticksPerSecond)));
        func_152373_a(sender, this, "command.tickspeed.success", String.valueOf(ticksPerSecond));
	}

	public static float parseFloat(String string) {
		try {
			float value = Float.parseFloat(string);

			if (!Floats.isFinite(value)) {
				throw new NumberInvalidException("command.generic.num.invalid", string);
			} else {
				return value;
			}
		} catch (NumberFormatException numberformatexception) {
			throw new NumberInvalidException("command.generic.num.invalid", string);
		}
	}
}
