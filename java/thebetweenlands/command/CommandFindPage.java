package thebetweenlands.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import thebetweenlands.manual.ManualManager;
import thebetweenlands.world.WorldProviderBetweenlands;

import java.util.List;

/**
 * Created by Bart on 28/11/2015.
 */
public class CommandFindPage extends CommandBase {

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
        if (ManualManager.findablePagesGuideBook.contains(args[0]))
            ManualManager.findPage((EntityPlayer) sender, args[0], ManualManager.EnumManual.GUIDEBOOK);
        else if (ManualManager.findablePagesHL.contains(args[0]))
            ManualManager.findPage((EntityPlayer) sender, args[0], ManualManager.EnumManual.HL);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, ManualManager.findablePagesAll.toArray(new String[0])) : null;
    }

}
