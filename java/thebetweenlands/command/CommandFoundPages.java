package thebetweenlands.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import thebetweenlands.manual.ManualManager;

/**
 * Created by Bart on 28/11/2015.
 */
public class CommandFoundPages extends CommandBase {
    @Override
    public String getCommandName() {
        return "foundPage";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "/foundPage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(sender.getEntityWorld() == null || !(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText("World is null or entity is not a player"));
            return;
        }
        for (String s:ManualManager.getFoundPages((EntityPlayer)sender))
            sender.addChatMessage(new ChatComponentText(s));
    }
}
