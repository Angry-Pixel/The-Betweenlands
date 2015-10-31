package thebetweenlands.event.render;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import thebetweenlands.items.BLItemRegistry;

public class FovHandler {
	public static final FovHandler INSTANCE = new FovHandler();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onUpdateFOV(FOVUpdateEvent event) {
		float fov = event.newfov;
		if (event.entity.isUsingItem()) {
			ItemStack stack = event.entity.getItemInUse();
			if(stack.getItem() == BLItemRegistry.weedwoodBow 
					|| (stack.getItem() == BLItemRegistry.elixir
					&& stack.stackTagCompound != null && stack.stackTagCompound.hasKey("throwing") && stack.stackTagCompound.getBoolean("throwing"))) {
				float maxUseTime = 8.0F;
				if(stack.getItem() == BLItemRegistry.elixir) {
					maxUseTime = 20.0F;
				}
				int duration = event.entity.getItemInUseDuration();
				float multiplier = duration / maxUseTime;
				if (multiplier > 1.0F) {
					multiplier = 1.0F;
				} else {
					multiplier *= multiplier;
				}
				fov = 1.0F - multiplier * 0.15F;
			}
		}
		event.newfov = fov;
	}
}
