package thebetweenlands.event.player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.item.ItemStack;
import thebetweenlands.event.item.ItemTooltipHandler;
import thebetweenlands.forgeevent.client.ClientBlockDamageEvent;
import thebetweenlands.items.ICorrodible;

public class ItemCorrosionHandler {
	public static final ItemCorrosionHandler INSTANCE = new ItemCorrosionHandler();

	private static final Field field_currentItemHittingBlock = ReflectionHelper.findField(PlayerControllerMP.class, "currentItemHittingBlock", "field_85183_f", "f");
	private static final List<String> exclusions = new ArrayList<String>();

	static {
		exclusions.add("Corrosion");
	}

	@SubscribeEvent
	public void onPreDamageBlock(ClientBlockDamageEvent.Pre event) {
		try {
			PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
			ItemStack prev = (ItemStack) field_currentItemHittingBlock.get(controller);
			ItemStack current = Minecraft.getMinecraft().thePlayer.getHeldItem();
			if(current != null && prev != null && !prev.equals(current) && prev.getItem() instanceof ICorrodible) {
				if(ItemTooltipHandler.areItemStackTagsEqual(prev, current, exclusions)) {
					field_currentItemHittingBlock.set(controller, current);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
