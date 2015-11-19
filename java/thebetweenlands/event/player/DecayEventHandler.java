package thebetweenlands.event.player;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.decay.DecayManager;
import thebetweenlands.entities.property.BLEntityPropertiesRegistry;
import thebetweenlands.entities.property.EntityPropertiesDecay;
import thebetweenlands.items.food.IDecayFood;
import thebetweenlands.network.message.MessageSyncPlayerDecay;
import thebetweenlands.world.BLGamerules;

public class DecayEventHandler {
	public static DecayEventHandler INSTANCE = new DecayEventHandler();

	@SubscribeEvent
	public void joinWorld(EntityJoinWorldEvent event) {
		if (!event.world.isRemote && event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity;

			int decayLevel = DecayManager.getDecayLevel(player);

			for(EntityPlayer p : (List<EntityPlayer>)event.world.playerEntities) {
				if(this.isInRenderRange(p, player.getDistanceToEntity(p))) {
					TheBetweenlands.networkWrapper.sendTo(new MessageSyncPlayerDecay(decayLevel, p.getUniqueID()), (EntityPlayerMP) player);
				}
			}
		}
	}

	private boolean isInRenderRange(EntityPlayer player, double dst) {
		double d1 = player.boundingBox.getAverageEdgeLength();
		d1 *= 64.0D * player.renderDistanceWeight;
		return dst < d1 * d1;
	}

	@SubscribeEvent
	public void useItem(PlayerUseItemEvent.Finish event) {
		if (DecayManager.isDecayEnabled(event.entityPlayer) && event.item.getItem() instanceof IDecayFood) {
			IDecayFood food = (IDecayFood) event.item.getItem();
			DecayManager.setDecayLevel(DecayManager.getDecayLevel(event.entityPlayer) + food.getDecayHealAmount(), event.entityPlayer);
		}
	}

	@SubscribeEvent
	public void useItemStart(PlayerUseItemEvent.Start event) {
		if (DecayManager.isDecayEnabled(event.entityPlayer) && event.item.getItem() instanceof IDecayFood && DecayManager.getDecayLevel(event.entityPlayer) >= 20 && (!event.entityPlayer.getFoodStats().needFood() || event.item.getItem() instanceof ItemFood == true)) {
			event.duration = -1;
			event.setCanceled(true);
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
				EntityPropertiesDecay prop = BLEntityPropertiesRegistry.INSTANCE.<EntityPropertiesDecay>getProperties(event.player, BLEntityPropertiesRegistry.DECAY);
				prop.decayTimer -= event.player.isInWater() ? (event.player.worldObj.rand.nextFloat() < 0.6F ? 2 : 1) : 1;
				if (prop.decayTimer < 0) {
					prop.decayTimer = 2000;
					DecayManager.setDecayLevel(DecayManager.getDecayLevel(event.player) - 1, event.player);
				}

				//Send decay to clients
				if(!event.player.worldObj.isRemote) {
					prop.syncTimer--;
					if(prop.syncTimer < 0) {
						prop.syncTimer = 80;
						TheBetweenlands.networkWrapper.sendToAllAround(new MessageSyncPlayerDecay(DecayManager.getDecayLevel(event.player), event.player.getUniqueID()), new TargetPoint(event.player.dimension, event.player.posX, event.player.posY, event.player.posZ, 64));
					}
				}
			}
		} else if (event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue() != 20D) {
			event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20D);
		} else if(!event.player.worldObj.isRemote && !BLGamerules.getGameRuleBooleanValue(BLGamerules.BL_DECAY)) {
			if(DecayManager.getDecayLevel(event.player) != 20) DecayManager.setDecayLevel(20, event.player);
		}
	}
}
