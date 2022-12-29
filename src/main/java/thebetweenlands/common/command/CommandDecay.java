package thebetweenlands.common.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public class CommandDecay extends CommandBase {
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getName() {
		return "setDecay";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.set_decay.usage";
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		List<String> completions = new ArrayList<String>();
		if(args.length == 1) {
			Collections.addAll(completions, server.getOnlinePlayerNames());
		}
		return getListOfStringsMatchingLastWord(args, completions.toArray(new String[0]));
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length < 2) {
			throw new CommandException("command.set_decay.usage");
		}
		EntityPlayer player = getPlayer(server, sender, args[0]);
		int decay;
		try {
			decay = Integer.parseInt(args[1]);
		} catch(Exception ex) {
			throw new CommandException("command.decay.noint");
		}
		int saturation = -1;
		if(args.length >= 3) {
			try {
				saturation = Integer.parseInt(args[2]);
			} catch(Exception ex) {
				throw new CommandException("command.decay_saturation.noint");
			}
		}
		IDecayCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
		if(cap != null) {
			cap.getDecayStats().setDecayLevel(decay);
			if(saturation != -1) {
				cap.getDecayStats().setDecaySaturationLevel(Math.max(saturation, 0));
			}
		}
	}
}