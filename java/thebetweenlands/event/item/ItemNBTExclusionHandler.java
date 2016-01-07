package thebetweenlands.event.item;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thebetweenlands.forgeevent.client.ClientBlockDamageEvent;

public class ItemNBTExclusionHandler {
	public static final ItemNBTExclusionHandler INSTANCE = new ItemNBTExclusionHandler();

	private static Field field_currentItemHittingBlock;
	private static final Field field_itemInUse = ReflectionHelper.findField(EntityPlayer.class, "itemInUse", "field_71074_e", "f");
	private static final List<String> exclusions = new ArrayList<String>();

	/**
	 * Add any NBT tag exclusions here
	 */
	static {
		exclusions.add("Corrosion");
		exclusions.add("throwing");
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPreDamageBlock(ClientBlockDamageEvent.Pre event) {
		try {
			if(field_currentItemHittingBlock == null) {
				field_currentItemHittingBlock = ReflectionHelper.findField(PlayerControllerMP.class, "currentItemHittingBlock", "field_85183_f", "f");
			}
			PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
			ItemStack prev = (ItemStack) field_currentItemHittingBlock.get(controller);
			ItemStack current = Minecraft.getMinecraft().thePlayer.getHeldItem();
			if(current != null && prev != null && !prev.equals(current)) {
				if(ItemTooltipHandler.areItemStackTagsEqual(prev, current, exclusions)) {
					field_currentItemHittingBlock.set(controller, current);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		try {
			EntityPlayer player = event.player;
			ItemStack prev = (ItemStack) field_itemInUse.get(player);
			ItemStack current = event.player.getHeldItem();
			if(current != null && prev != null && !prev.equals(current)) {
				if(ItemTooltipHandler.areItemStackTagsEqual(prev, current, exclusions)) {
					field_itemInUse.set(player, current);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
