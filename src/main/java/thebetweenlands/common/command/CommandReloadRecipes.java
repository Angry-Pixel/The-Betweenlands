package thebetweenlands.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
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
		return "/blReloadRecipes";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		CustomRecipeRegistry.loadCustomRecipes();
		notifyCommandListener(sender, this, "command.blreloadrecipes.success");
	}
}
