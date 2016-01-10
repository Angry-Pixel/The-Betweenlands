package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import thebetweenlands.decay.DecayManager;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesDecay;
import thebetweenlands.items.food.IDecayFood;
import thebetweenlands.world.BLGamerules;

public class DecayEventHandler {
	public static DecayEventHandler INSTANCE = new DecayEventHandler();

	private int syncTimer = 0;

	private boolean isInRenderRange(EntityPlayer player, double dst) {
		double d1 = player.boundingBox.getAverageEdgeLength();
		d1 *= 64.0D * player.renderDistanceWeight;
		return dst < d1 * d1;
	}

	@SubscribeEvent
	public void useItem(PlayerUseItemEvent.Finish event) {
		if (DecayManager.isDecayEnabled(event.entityPlayer) && event.item.getItem() instanceof IDecayFood) {
			IDecayFood food = (IDecayFood) event.item.getItem();
			DecayManager.getDecayStats(event.entityPlayer).addStats(food.getDecayHealAmount(), 0.6F);
		}
	}

	@SubscribeEvent
	public void useItemStart(PlayerUseItemEvent.Start event) {
		boolean isDecayFood = event.item.getItem() instanceof IDecayFood;
		if(isDecayFood) {
			boolean eatFood = event.entityPlayer.getFoodStats().needFood() && event.item.getItem() instanceof ItemFood;
			boolean eatDecay = DecayManager.getDecayLevel(event.entityPlayer) < 20;
			if (!eatFood && !eatDecay) {
				event.duration = -1;
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		if (DecayManager.isDecayEnabled(event.player) && event.phase == Phase.END) {
			float maxHealth = (int)(DecayManager.getPlayerHearts(event.player) / 2.0F) * 2;
			event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealth);
			if(event.player.getHealth() > maxHealth) {
				event.player.setHealth(maxHealth);
			}

			if (DecayManager.getDecayLevel(event.player) <= 4) {
				event.player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 1, 2, true));
				event.player.jumpMovementFactor = 0.001F;
			} else if (DecayManager.getDecayLevel(event.player) <= 7) {
				event.player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 1, 1, true));
				event.player.jumpMovementFactor = 0.002F;
			} else if (DecayManager.getDecayLevel(event.player) <= 10) {
				event.player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 1, 0, true));
			}

			if(BLGamerules.getGameRuleBooleanValue(BLGamerules.BL_DECAY)) {
				EntityPropertiesDecay prop = BLEntityPropertiesRegistry.HANDLER.getProperties(event.player, EntityPropertiesDecay.class);
				if(!event.player.worldObj.isRemote) {
					prop.decayStats.onUpdate(event.player);
				}
			}
		} else if (event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue() != 20D) {
			event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20D);
		} else if(!event.player.worldObj.isRemote && !BLGamerules.getGameRuleBooleanValue(BLGamerules.BL_DECAY)) {
			if(DecayManager.getDecayLevel(event.player) != 20) DecayManager.setDecayLevel(20, event.player);
		}
	}
}
