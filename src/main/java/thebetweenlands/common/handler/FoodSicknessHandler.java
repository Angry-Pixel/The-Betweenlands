package thebetweenlands.common.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.api.item.IFoodSicknessItem;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.decay.DecayStats;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.config.properties.ItemDecayFoodProperty.DecayFoodStats;
import thebetweenlands.common.network.clientbound.MessageShowFoodSicknessLine;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.GameruleRegistry;

public class FoodSicknessHandler {
	private FoodSicknessHandler() { }

	private static EnumHand lastHand = EnumHand.MAIN_HAND;
	private static ItemStack lastUsedItem = ItemStack.EMPTY;
	private static FoodSickness lastSickness = null;

	public static boolean isFoodSicknessEnabled(World world) {
		if(GameruleRegistry.getGameRuleBooleanValue(GameruleRegistry.BL_FOOD_SICKNESS)) {
			if(world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && BetweenlandsConfig.GENERAL.useFoodSicknessInBetweenlands) {
				return true;
			} else if(world.provider.getDimension() != BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && BetweenlandsConfig.GENERAL.useFoodSicknessOutsideBetweenlands) {
				return true;
			}
		}
		
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
		if(player != null) {
			if(!lastUsedItem.isEmpty() && (player.getHeldItem(lastHand).isEmpty() || !player.getHeldItem(lastHand).isItemEqual(lastUsedItem))) {
				lastUsedItem = ItemStack.EMPTY;
				lastSickness = null;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected static void addSicknessMessage(EntityPlayer player, ItemStack item, FoodSickness sickness) {
		if(lastUsedItem.isEmpty() || !item.isItemEqual(lastUsedItem) || lastSickness == null || lastSickness != sickness) {
			player.sendStatusMessage(new TextComponentString(String.format(sickness.getRandomLine(player.getRNG()), item.getDisplayName())), true);
		}
		lastUsedItem = item;
		lastSickness = sickness;
	}

	@SubscribeEvent
	public static void onStartItemUse(LivingEntityUseItemEvent.Start event) {
		EntityPlayer player = event.getEntity() instanceof EntityPlayer ? (EntityPlayer) event.getEntity() : null;
		ItemStack itemStack = event.getItem();

		if (player != null && !itemStack.isEmpty() && FoodSicknessHandler.isFoodSicknessEnabled(event.getEntity().getEntityWorld()) && itemStack.getItem() instanceof IFoodSicknessItem && ((IFoodSicknessItem)itemStack.getItem()).canGetSickOf(player, itemStack)) {
			IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
			if(cap != null) {
				Item item = itemStack.getItem();
				FoodSickness sickness = cap.getSickness(item);

				if(player.world.isRemote && sickness == FoodSickness.SICK) {
					addSicknessMessage(player, itemStack, sickness);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onUseItemTick(LivingEntityUseItemEvent.Tick event) {
		//Check if item will be consumed this tick
		if(event.getEntityLiving().getEntityWorld().isRemote || event.getDuration() > 1) {
			return;
		}

		EntityPlayer player = event.getEntity() instanceof EntityPlayer ? (EntityPlayer) event.getEntity() : null;
		ItemStack itemStack = event.getItem();

		if (player == null || itemStack.isEmpty() || !FoodSicknessHandler.isFoodSicknessEnabled(event.getEntity().getEntityWorld()) || !(itemStack.getItem() instanceof IFoodSicknessItem) || !(((IFoodSicknessItem)itemStack.getItem()).canGetSickOf(player, itemStack))) {
			return;
		}

		IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
		if(cap == null) {
			return;
		}


		Item item = itemStack.getItem();

		FoodSickness lastSickness = cap.getLastSickness();

		int prevFoodHatred = cap.getFoodHatred(item);
		FoodSickness currentSickness = cap.getSickness(item);

		if(player.world.isRemote) {
			if(currentSickness != lastSickness && lastSickness == FoodSickness.SICK) {
				addSicknessMessage(player, itemStack, currentSickness);
			}
		}

		int sicknessIncrease = 5;

		//Nerf food if you're fully sick of it
		if(currentSickness == FoodSickness.SICK) {
			if(item instanceof ItemFood) {
				int foodLevel = ((ItemFood)itemStack.getItem()).getHealAmount(itemStack);
				double foodLoss = 2.0D / 3.0D;

				if(player.world.isRemote) {
					//Remove all gained food on client side and wait for sync
					player.getFoodStats().addStats(-Math.min(MathHelper.ceil(foodLevel * foodLoss), foodLevel), 0.0F);
				} else {
					int minFoodGain = player.world.rand.nextInt(4) == 0 ? 1 : 0;
					player.getFoodStats().addStats(-Math.min(MathHelper.ceil(foodLevel * foodLoss), Math.max(foodLevel - minFoodGain, 0)), 0.0F);
				}
			}

			DecayFoodStats decayFoodStats = OverworldItemHandler.getDecayFoodStats(itemStack);
			if(decayFoodStats != null) {
				IDecayCapability decayCap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
				if(decayCap != null) {
					int decayLevel = decayFoodStats.decay;
					DecayStats decayStats = decayCap.getDecayStats();
					double decayLoss = 2.0D / 3.0D;

					if (player.world.isRemote) {
						//Remove all gained decay on client side and wait for sync
						decayStats.addStats(-Math.min(MathHelper.ceil(decayLevel * decayLoss), decayLevel), 0.0F);
					} else {
						int minDecayGain = player.world.rand.nextInt(4) == 0 ? 1 : 0;
						decayStats.addStats(-Math.min(MathHelper.ceil(decayLevel * decayLoss), Math.max(decayLevel - minDecayGain, 0)), 0.0F);
					}
				}
			}

			if(!player.world.isRemote) {
				cap.increaseFoodHatred(item, sicknessIncrease, 0);
			}
		} else {
			if(!player.world.isRemote) {
				cap.increaseFoodHatred(item, sicknessIncrease, prevFoodHatred <= 2 * 5 ? 4 : 3);
			}
		}

		FoodSickness newSickness = cap.getSickness(item);

		if(!player.world.isRemote && player instanceof EntityPlayerMP) {
			if(newSickness != lastSickness) {
				TheBetweenlands.networkWrapper.sendTo(new MessageShowFoodSicknessLine(itemStack, newSickness), (EntityPlayerMP) player);
			}
		}

		cap.setLastSickness(newSickness);
	}
}
