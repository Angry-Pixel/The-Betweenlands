package thebetweenlands.event.player;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemFood;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesFood;

import java.util.ArrayList;
import java.util.List;

public class PlayerItemEventHandler {
	public static final PlayerItemEventHandler INSTANCE = new PlayerItemEventHandler();

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

	public enum Sickness {
		FINE(0),
		HALF(5),
		SICK(10);

		private String[] lines;
		private int minHatred;

		Sickness(int minHatred) {
			this.minHatred = minHatred;
			int index = 0;
			List<String> lineList = new ArrayList<>();
			while (StatCollector.canTranslate("chat.food_sickness." + name().toLowerCase() + "." + index)) {
				lineList.add(StatCollector.translateToLocal("chat.food_sickness." + name().toLowerCase() + "." + index));
				index++;
			}
			lines = lineList.toArray(new String[index]);
		}

		public String[] getLines() {
			return lines;
		}

		public static Sickness getSicknessForHatred(int hatred) {
			for (Sickness sickness : VALUES) {
				if (sickness.minHatred < hatred) {
					return sickness;
				}
			}
			return VALUES[VALUES.length - 1];
		}

		public static Sickness[] VALUES = values();
	}
}
