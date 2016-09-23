package thebetweenlands.common.event;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thebetweenlands.common.entity.capability.DecayEntityCapability;
import thebetweenlands.common.entity.capability.IDecayCapability;

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
}
