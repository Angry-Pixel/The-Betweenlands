package thebetweenlands.commands;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.ChatComponentText;
import thebetweenlands.world.events.EnvironmentEvent;
import thebetweenlands.world.events.EnvironmentEventRegistry;

public class CommandToggleEvent implements ICommand {

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "blevent";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "blevent <event name>";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(!sender.getEntityWorld().isRemote) {
			String eventName = "";
			for(int i = 0; i < args.length; i++) {
				eventName += args[i] + " ";
			}
			eventName = eventName.substring(0, eventName.length() - 1);
			EnvironmentEvent event = EnvironmentEventRegistry.forName(eventName);
			if(event != null) {
				event.setActive(!event.isActive());
				sender.addChatMessage(new ChatComponentText("Toggled event: " + eventName));
			} else {
				sender.addChatMessage(new ChatComponentText("Could not find event '" + eventName + "'"));
			}
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if(sender instanceof EntityPlayerMP){
			EntityPlayerMP player = (EntityPlayerMP)sender;
			UserListOpsEntry userlistopsentry = (UserListOpsEntry)player.mcServer.getConfigurationManager().func_152603_m().func_152683_b(player.getGameProfile());
			return !player.mcServer.isDedicatedServer() || userlistopsentry != null;
		}
		return false;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idk) {
		return false;
	}

}
