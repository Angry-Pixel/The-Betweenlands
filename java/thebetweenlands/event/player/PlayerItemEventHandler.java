package thebetweenlands.event.player;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesFood;
import thebetweenlands.network.message.MessageSyncFoodHatred;

public class PlayerItemEventHandler {
	public static final PlayerItemEventHandler INSTANCE = new PlayerItemEventHandler();

	@SubscribeEvent
	public void onPlayerJoin(EntityEvent.EntityConstructing event) {
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP) {
			EntityPropertiesFood property = BLEntityPropertiesRegistry.HANDLER.getProperties(event.entity, EntityPropertiesFood.class);
			TheBetweenlands.networkWrapper.sendTo(new MessageSyncFoodHatred(property.getHatredMap()), (EntityPlayerMP) event.entity);
		}
	}

	@SubscribeEvent
	public void onStartItemUse(PlayerUseItemEvent.Start event) {
		if (event.item != null && event.item.getItem() instanceof ItemFood) {
			EntityPropertiesFood property = BLEntityPropertiesRegistry.HANDLER.getProperties(event.entityPlayer, EntityPropertiesFood.class);
			ItemFood food = (ItemFood) event.item.getItem();
			if (property.getFoodHatred(food) > 5) {
				event.setCanceled(true);
				event.entityPlayer.addChatComponentMessage(new ChatComponentText("Ew " + FMLCommonHandler.instance().getEffectiveSide()));
			}
		}
	}

	@SubscribeEvent
	public void onFinishItemUse(PlayerUseItemEvent.Finish event) {
		if (event.item != null && event.item.getItem() instanceof ItemFood) {
			EntityPropertiesFood property = BLEntityPropertiesRegistry.HANDLER.getProperties(event.entityPlayer, EntityPropertiesFood.class);
			ItemFood food = (ItemFood) event.item.getItem();
			property.increaseFoodHatred(food);
		}
	}
}
