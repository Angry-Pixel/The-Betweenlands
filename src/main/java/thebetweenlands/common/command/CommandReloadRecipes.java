package thebetweenlands.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import thebetweenlands.common.registries.CustomRecipeRegistry;

public class CommandReloadRecipes extends CommandBase {
	@Override
	public String getName() {
		return "blReloadRecipes";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.blreloadrecipes.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
		if (CustomRecipeRegistry.loadCustomRecipes())
			notifyCommandListener(sender, this, "command.blreloadrecipes.success");
		else
			notifyCommandListener(sender, this, "command.blreloadrecipes.failed");
	}
}
