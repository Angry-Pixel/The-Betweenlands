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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thebetweenlands.common.herblore.aspect.AspectManager;

public class CommandResetAspects extends CommandBase {
    private boolean confirm = false;

    @Override
    public String getName() {
        return "resetAspects";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.aspect.reset.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> completions = null;
        if (this.confirm && args.length == 1) {
            completions = new ArrayList<String>();
            completions.add("confirm");
        }
        return completions == null ? null : getListOfStringsMatchingLastWord(args, completions.toArray(new String[0]));
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender.getEntityWorld() == null || !(sender instanceof EntityPlayer)) {
            throw new CommandException("command.generic.noplayer");
        }
        if (args.length < 1) {
            this.confirm = true;
            sender.sendMessage(new TextComponentTranslation("command.aspect.reset.confirm"));
            return;
        }
        if (!this.confirm) {
            return;
        }

        this.confirm = false;

        String arg1 = args[0];

        if (arg1.equals("confirm")) {
            World world = sender.getEntityWorld();
            AspectManager.get(world).resetStaticAspects(AspectManager.getAspectsSeed(world.getSeed()));
            sender.sendMessage(new TextComponentTranslation("command.aspect.reset.success"));
        }
    }
}
