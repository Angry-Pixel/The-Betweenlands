package thebetweenlands.event.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.decay.DecayStats;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesDecay;
import thebetweenlands.entities.properties.list.EntityPropertiesFood;
import thebetweenlands.items.food.IDecayFood;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class PlayerItemEventHandler {
	public static final PlayerItemEventHandler INSTANCE = new PlayerItemEventHandler();

	private ItemStack lastUsedItem = null;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
		if(player != null) {
			if(this.lastUsedItem != null && (player.getHeldItem() == null || !player.getHeldItem().isItemEqual(this.lastUsedItem))) {
				this.lastUsedItem = null;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void addSicknessMessage(EntityPlayer player, ItemStack item, Sickness sickness) {
		if(this.lastUsedItem == null || !item.isItemEqual(this.lastUsedItem))
			player.addChatComponentMessage(new ChatComponentText(sickness.getRandomLine(player.getRNG())));
		this.lastUsedItem = item;
	}

	@SubscribeEvent
	public void onStartItemUse(PlayerUseItemEvent.Start event) {
		if (event.entityPlayer != null && event.entityPlayer.dimension == ConfigHandler.DIMENSION_ID && event.item != null && event.item.getItem() instanceof ItemFood) {
			EntityPropertiesFood property = BLEntityPropertiesRegistry.HANDLER.getProperties(event.entityPlayer, EntityPropertiesFood.class);
			ItemFood food = (ItemFood) event.item.getItem();
			Sickness sickness = property.getSickness(food);
			if(event.entityPlayer.worldObj.isRemote && sickness == Sickness.SICK)
				this.addSicknessMessage(event.entityPlayer, event.item, sickness);
		}
	}

	@SubscribeEvent
	public void onFinishItemUse(PlayerUseItemEvent.Finish event) {
		if (event.entityPlayer != null && event.entityPlayer.dimension == ConfigHandler.DIMENSION_ID && event.item != null && event.item.getItem() instanceof ItemFood) {
			EntityPropertiesFood property = BLEntityPropertiesRegistry.HANDLER.getProperties(event.entityPlayer, EntityPropertiesFood.class);
			ItemFood food = (ItemFood) event.item.getItem();

			if(event.entityPlayer.worldObj.isRemote && property.getLastSickness() == Sickness.SICK)
				this.addSicknessMessage(event.entityPlayer, event.item, property.getSickness(food));

			Sickness currentSickness = property.getSickness(food);
			if(currentSickness == Sickness.SICK) {
				int foodLevel = ((ItemFood)event.item.getItem()).func_150905_g(event.item);
				double foodLoss = 1.0D / 3.0D * 2.0;
				if(event.entityPlayer.worldObj.isRemote) {
					//Remove all gained food on client side and wait for sync
					event.entityPlayer.getFoodStats().addStats(-Math.min(MathHelper.ceiling_double_int(foodLevel * foodLoss), foodLevel), 0.0F);
				} else {
					int minFoodGain = event.entityPlayer.worldObj.rand.nextInt(4) == 0 ? 1 : 0;
					event.entityPlayer.getFoodStats().addStats(-Math.min(MathHelper.ceiling_double_int(foodLevel * foodLoss), Math.max(foodLevel - minFoodGain, 0)), 0.0F);
				}
				if(event.item.getItem() instanceof IDecayFood) {
					int decayLevel = ((IDecayFood)event.item.getItem()).getDecayHealAmount(event.item);
					EntityPropertiesDecay props = BLEntityPropertiesRegistry.HANDLER.getProperties(event.entityPlayer, EntityPropertiesDecay.class);
					if(props != null) {
						DecayStats decayStats = props.decayStats;
						double decayLoss = 1.0D / 3.0D * 2.0;
						if(event.entityPlayer.worldObj.isRemote) {
							//Remove all gained decay on client side and wait for sync
							decayStats.addStats(-Math.min(MathHelper.ceiling_double_int(decayLevel * decayLoss), decayLevel), 0.0F);
						} else {
							int minDecayGain = event.entityPlayer.worldObj.rand.nextInt(4) == 0 ? 1 : 0;
							decayStats.addStats(-Math.min(MathHelper.ceiling_double_int(decayLevel * decayLoss), Math.max(decayLevel - minDecayGain, 0)), 0.0F);
						}
					}
				}
				if(!event.entityPlayer.worldObj.isRemote)
					property.increaseFoodHatred(food, 1, 0);
			} else {
				if(!event.entityPlayer.worldObj.isRemote)
					property.increaseFoodHatred(food, 1, currentSickness == Sickness.FINE ? 2 : 1);
			}
		}
	}

	public enum Sickness {
		FINE(22),
		HALF(34),
		SICK(50);

		public final String[] lines;
		public final int maxHatred;

		private Sickness(int maxHatred) {
			this.maxHatred = maxHatred;
			int index = 0;
			List<String> lineList = new ArrayList<>();
			while (StatCollector.canTranslate("chat.food_sickness." + name().toLowerCase() + "." + index)) {
				lineList.add(StatCollector.translateToLocal("chat.food_sickness." + name().toLowerCase() + "." + index));
				index++;
			}
			this.lines = lineList.toArray(new String[index]);
		}

		public String[] getLines() {
			return lines;
		}

		public String getRandomLine(Random rnd) {
			return getLines()[rnd.nextInt(getLines().length)];
		}

		public static Sickness getSicknessForHatred(int hatred) {
			for (Sickness sickness : VALUES) {
				if (sickness.maxHatred > hatred) {
					return sickness;
				}
			}
			return VALUES[VALUES.length - 1];
		}

		public static Sickness[] VALUES = values();
	}
}
