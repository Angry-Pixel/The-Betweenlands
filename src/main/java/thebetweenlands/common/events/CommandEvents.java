package thebetweenlands.common.events;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.common.commands.ShaderDebug;

@Mod.EventBusSubscriber(modid = "thebetweenlands")
public class CommandEvents {

	@SubscribeEvent
	public static void onCommandsRegister(RegisterCommandsEvent event) {
		new ShaderDebug(event.getDispatcher());
	}
}
