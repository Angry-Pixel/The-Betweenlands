package thebetweenlands.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import thebetweenlands.manual.ManualManager;
import thebetweenlands.world.WorldProviderBetweenlands;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Bart on 28/11/2015.
 */
public class CommandFindPage extends CommandBase {
    private List<String> childCommands = Arrays.asList("toggle", "on", "off", "list", "enable", "disable");

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
        if(sender.getEntityWorld() == null || !(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText("World is null or entity is not a player"));
            return;
        }
        ManualManager.findPage((EntityPlayer)sender, args[0]);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (!(sender.getEntityWorld().provider instanceof WorldProviderBetweenlands)) {
            return null;
        }
        List<String> completions = null;
        if (args.length == 0) {
            completions = childCommands;
        }
        return completions == null ? null : getListOfStringsMatchingLastWord(args, completions.toArray(new String[0]));
    }

}
