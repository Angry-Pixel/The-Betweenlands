package thebetweenlands.common.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.api.item.IDecayFood;
import thebetweenlands.api.item.IFoodSicknessItem;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.decay.DecayStats;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.util.config.ConfigHandler;

public class FoodSicknessHandler {
	private FoodSicknessHandler() { }

	private static EnumHand lastHand = EnumHand.MAIN_HAND;
	private static ItemStack lastUsedItem = null;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
		if(player != null) {
			if(lastUsedItem != null && (player.getHeldItem(lastHand) == null || !player.getHeldItem(lastHand).isItemEqual(lastUsedItem))) {
				lastUsedItem = null;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static void addSicknessMessage(EntityPlayer player, ItemStack item, FoodSickness sickness) {
		if(lastUsedItem == null || !item.isItemEqual(lastUsedItem)) {
			player.sendMessage(new TextComponentString(String.format(sickness.getRandomLine(player.getRNG()), item.getDisplayName())));
		}
		lastUsedItem = item;
	}

	@SubscribeEvent
	public static void onStartItemUse(LivingEntityUseItemEvent.Start event) {
		EntityPlayer player = event.getEntity() instanceof EntityPlayer ? (EntityPlayer) event.getEntity() : null;
		ItemStack itemStack = event.getItem();

		if (player != null && player.dimension == ConfigHandler.dimensionId && itemStack != null && itemStack.getItem() instanceof IFoodSicknessItem && ((IFoodSicknessItem)itemStack.getItem()).canGetSickOf(itemStack)) {
			if(player.hasCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null)) {
				IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
				ItemFood food = (ItemFood) itemStack.getItem();
				FoodSickness sickness = cap.getSickness(food);

				if(player.world.isRemote && sickness == FoodSickness.SICK) {
					addSicknessMessage(player, itemStack, sickness);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onFinishItemUse(LivingEntityUseItemEvent.Finish event) {
		EntityPlayer player = event.getEntity() instanceof EntityPlayer ? (EntityPlayer) event.getEntity() : null;
		ItemStack itemStack = event.getItem();

		if (player != null && player.dimension == ConfigHandler.dimensionId && itemStack != null && itemStack.getItem() instanceof IFoodSicknessItem && ((IFoodSicknessItem)itemStack.getItem()).canGetSickOf(itemStack)) {
			if(player.hasCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null)) {
				IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);

				ItemFood food = (ItemFood) itemStack.getItem();

				if(player.world.isRemote && cap.getLastSickness() == FoodSickness.SICK) {
					addSicknessMessage(player, itemStack, cap.getSickness(food));
				}

				int prevFoodHatred = cap.getFoodHatred(food);
				FoodSickness currentSickness = cap.getSickness(food);

				if(currentSickness == FoodSickness.SICK) {
					int foodLevel = ((ItemFood)itemStack.getItem()).getHealAmount(itemStack);
					double foodLoss = 1.0D / 3.0D * 2.0;

					if(player.world.isRemote) {
						//Remove all gained food on client side and wait for sync
						player.getFoodStats().addStats(-Math.min(MathHelper.ceil(foodLevel * foodLoss), foodLevel), 0.0F);
					} else {
						int minFoodGain = player.world.rand.nextInt(4) == 0 ? 1 : 0;
						player.getFoodStats().addStats(-Math.min(MathHelper.ceil(foodLevel * foodLoss), Math.max(foodLevel - minFoodGain, 0)), 0.0F);
					}

					if(itemStack.getItem() instanceof IDecayFood && player.hasCapability(CapabilityRegistry.CAPABILITY_DECAY, null)) {
						IDecayCapability decayCap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
						int decayLevel = ((IDecayFood)itemStack.getItem()).getDecayHealAmount(itemStack);
						DecayStats decayStats = decayCap.getDecayStats();
						double decayLoss = 1.0D / 3.0D * 2.0;

						if(player.world.isRemote) {
							//Remove all gained decay on client side and wait for sync
							decayStats.addStats(-Math.min(MathHelper.ceil(decayLevel * decayLoss), decayLevel), 0.0F);
						} else {
							int minDecayGain = player.world.rand.nextInt(4) == 0 ? 1 : 0;
							decayStats.addStats(-Math.min(MathHelper.ceil(decayLevel * decayLoss), Math.max(decayLevel - minDecayGain, 0)), 0.0F);
						}
					}

					if(!player.world.isRemote) {
						cap.increaseFoodHatred(food, 5, 0);
					}
				} else {
					if(!player.world.isRemote) {
						cap.increaseFoodHatred(food, 5, prevFoodHatred <= 2 * 5 ? 4 : 3);
					}
				}
			}
		}
	}
}
