package thebetweenlands.common.event;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thebetweenlands.common.entity.capability.DecayEntityCapability;
import thebetweenlands.common.entity.capability.IDecayCapability;
import thebetweenlands.common.item.food.IDecayFood;

public class PlayerDecayHandler {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;

		if(!player.worldObj.isRemote && player.hasCapability(DecayEntityCapability.CAPABILITY, null)) {
			IDecayCapability capability = player.getCapability(DecayEntityCapability.CAPABILITY, null);

			if(capability.isDecayEnabled()) {
				float currentMaxHealth = (float) player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
				float maxHealth = (int)(capability.getMaxPlayerHealth() / 2.0F) * 2;

				//Only reset the health to default once, hopefully should improve mod compatilibity?
				boolean requiresHealthReset = capability.getDecayStats().getDecayLevel() != capability.getDecayStats().getPrevDecayLevel() && currentMaxHealth < 20.0F && maxHealth >= 20.0F;

				//Clamp health
				if(maxHealth < 20.0F || requiresHealthReset) {
					player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
					if(player.getHealth() > maxHealth) {
						player.setHealth(maxHealth);
					}
				}

				int decay = capability.getDecayStats().getDecayLevel();

				if (decay >= 16) {
					event.player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("slowness"), 1, 2, true, false));
					event.player.jumpMovementFactor = 0.001F;
				} else if (decay >= 13) {
					event.player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("slowness"), 1, 1, true, false));
					event.player.jumpMovementFactor = 0.002F;
				} else if (decay >= 10) {
					event.player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("slowness"), 1, 0, true, false));
				}

				capability.getDecayStats().onUpdate(player);
			}
		}
	}

	@SubscribeEvent
	public static void onUseItem(LivingEntityUseItemEvent.Finish event) {
		if (event.getItem() != null && event.getItem().getItem() instanceof IDecayFood && event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if(player.hasCapability(DecayEntityCapability.CAPABILITY, null)) {
				IDecayCapability capability = player.getCapability(DecayEntityCapability.CAPABILITY, null);
				IDecayFood food = (IDecayFood) event.getItem().getItem();
				capability.getDecayStats().addStats(-food.getDecayHealAmount(event.getItem()), food.getDecayHealSaturation(event.getItem()));
			}
		}
	}

	@SubscribeEvent
	public static void onStartUsingItem(LivingEntityUseItemEvent.Start event) {
		if(event.getItem() != null && event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			boolean isDecayFood = event.getItem().getItem() instanceof IDecayFood;
			if(isDecayFood) {
				boolean canEatFood = player.getFoodStats().needFood() && event.getItem().getItem() instanceof ItemFood && ((ItemFood)event.getItem().getItem()).getHealAmount(event.getItem()) > 0;
				boolean canEatDecayFood = false;
				if(player.hasCapability(DecayEntityCapability.CAPABILITY, null)) {
					IDecayCapability capability = player.getCapability(DecayEntityCapability.CAPABILITY, null);
					canEatDecayFood = capability.getDecayStats().getDecayLevel() > 0;
				}
				if (!canEatFood && !canEatDecayFood) {
					event.setDuration(-1);
					event.setCanceled(true);
				}
			}
		}
	}
}
