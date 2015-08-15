package thebetweenlands.event.player;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.property.EntityPropertiesDecay;
import thebetweenlands.manager.DecayManager;
import thebetweenlands.network.message.MessageSyncPlayerDecay;
import thebetweenlands.utils.IDecayFood;

import java.util.Map;
import java.util.UUID;

public class DecayEventHandler {
	public static DecayEventHandler INSTANCE = new DecayEventHandler();

	public Map<UUID, Integer> corruptionBuffer = Maps.newHashMap();

	@SubscribeEvent
	public void entityConstructing(EntityEvent.EntityConstructing event) {
		if (event.entity instanceof EntityPlayer) {
			event.entity.registerExtendedProperties(EntityPropertiesDecay.getId(), new EntityPropertiesDecay());
		}
	}

	@SubscribeEvent
	public void joinWorld(EntityJoinWorldEvent event) {
		if (!event.world.isRemote && event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity;

			int decayLevel = DecayManager.getDecayLevel(player);

			TheBetweenlands.networkWrapper.sendTo(new MessageSyncPlayerDecay(decayLevel), (EntityPlayerMP) player);
		} else if (event.world.isRemote && event.entity instanceof EntityPlayer) {
			TheBetweenlands.proxy.corruptPlayerSkin((EntityPlayer) event.entity, DecayManager.getCorruptionLevel((EntityPlayer) event.entity));
		}
	}

	@SubscribeEvent
	public void useItem(PlayerUseItemEvent.Finish event) {
		if (DecayManager.enableDecay(event.entityPlayer) && event.item.getItem() instanceof IDecayFood) {
			IDecayFood food = (IDecayFood) event.item.getItem();
			DecayManager.setDecayLevel(DecayManager.getDecayLevel(event.entityPlayer) + food.getDecayHealAmount(), event.entityPlayer);
		}
	}

	@SubscribeEvent
	public void useItemStart(PlayerUseItemEvent.Start event) {
		if (DecayManager.enableDecay(event.entityPlayer) && event.item.getItem() instanceof IDecayFood && DecayManager.getDecayLevel(event.entityPlayer) >= 20) {
			event.duration = -1;
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		if (DecayManager.enableDecay(event.player)) {
			event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(DecayManager.getPlayerHearts(event.player));

			if (DecayManager.getDecayLevel(event.player) <= 4) {
				event.player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 1, 2, true));
				event.player.jumpMovementFactor = 0.0F;
			} else if (DecayManager.getDecayLevel(event.player) <= 7) {
				event.player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 1, 1, true));
				event.player.jumpMovementFactor = 0.002F;
			} else if (DecayManager.getDecayLevel(event.player) <= 10) {
				event.player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 1, 0, true));
			}

			// Oli, if you ever want to change decay speed, change the number
			// that the ticksExisted is divided by.
			// Right now every multiple of 2500 the decay level is decreased by 1
			int decayTicks = 2500;
			if(event.player.isInWater()) decayTicks = decayTicks / 3 * 2;
			if (event.player.ticksExisted % decayTicks == 0) {
				DecayManager.setDecayLevel(DecayManager.getDecayLevel(event.player) - 1, event.player);
			}
		} else if (event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue() != 20d) {
			event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20d);
		} else if (event.player.isPotionActive(Potion.moveSlowdown.getId())) {
			event.player.removePotionEffect(Potion.moveSlowdown.getId());
		}

		if (event.player.worldObj.isRemote) {
			if (!corruptionBuffer.containsKey(event.player.getPersistentID())) {
				corruptionBuffer.put(event.player.getPersistentID(), DecayManager.getCorruptionLevel(event.player));
				TheBetweenlands.proxy.corruptPlayerSkin(event.player, DecayManager.getCorruptionLevel(event.player));
			} else if (corruptionBuffer.get(event.player.getPersistentID()) != DecayManager.getCorruptionLevel(event.player)) {
				corruptionBuffer.put(event.player.getPersistentID(), DecayManager.getCorruptionLevel(event.player));
				TheBetweenlands.proxy.corruptPlayerSkin(event.player, DecayManager.getCorruptionLevel(event.player));
			}
		}
	}
}
