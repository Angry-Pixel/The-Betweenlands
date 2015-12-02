package thebetweenlands.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import thebetweenlands.manual.ManualManager;
import thebetweenlands.world.WorldProviderBetweenlands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Bart on 28/11/2015.
 */
public class CommandFindPage extends CommandBase {
    public static List<String> childCommands = new ArrayList<>();

    @Override
    public String getCommandName() {
        return "findPage";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "/findPage (pageName)";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (sender.getEntityWorld() == null || !(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText("World is null or entity is not a player"));
            return;
        }
        if (childCommands.contains(args[0]))
            ManualManager.findPage((EntityPlayer) sender, args[0]);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (!(sender.getEntityWorld().provider instanceof WorldProviderBetweenlands)) {
            return null;
        }
        List<String> completions = null;
        if (args.length == 1) {
            completions = childCommands;
        }
        return completions == null ? null : getListOfStringsMatchingLastWord(args, completions.toArray(new String[0]));
    }

}
