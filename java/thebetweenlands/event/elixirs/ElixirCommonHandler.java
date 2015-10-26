package thebetweenlands.event.elixirs;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import thebetweenlands.herblore.elixirs.effects.ElixirRegistry;

public class ElixirCommonHandler {
	public static final ElixirCommonHandler INSTANCE = new ElixirCommonHandler();

	private boolean ignoreAttackEvent = false;
	public static final float MAX_DAMAGE_MULTIPLIER = 1.0F;
	@SubscribeEvent
	public void onEntityAttack(LivingAttackEvent event) {
		if(this.ignoreAttackEvent) return;
		EntityLivingBase attackedEntity = event.entityLiving;
		DamageSource source = event.source;
		if(source.getSourceOfDamage() instanceof EntityLivingBase) {
			EntityLivingBase attackingEntity = (EntityLivingBase)source.getSourceOfDamage();
			if(ElixirRegistry.EFFECT_STRENGTH.isActive(attackingEntity)) {
				int strength = ElixirRegistry.EFFECT_STRENGTH.getStrength(attackingEntity) + 1;
				float damage = event.ammount;
				this.ignoreAttackEvent = true;
				attackedEntity.attackEntityFrom(source, MathHelper.ceiling_float_int(damage + damage / 4.0F * strength * MAX_DAMAGE_MULTIPLIER));
				this.ignoreAttackEvent = false;
				event.setCanceled(true);
			}
		} else if(source == DamageSource.cactus && ElixirRegistry.EFFECT_TOUGHSKIN.isActive(attackedEntity)) {
			event.setCanceled(true);
		}
	}

	//This can be used to stop the entities from attacking if the player is out of "sight"
	//Not needed for now, but I'll keep it here just in case
	/*@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		if(event.phase == Phase.START) {
			for(Entity e : (List<Entity>)event.world.loadedEntityList) {
				if(e instanceof EntityLivingBase) {
					EntityLivingBase living = (EntityLivingBase)e;
					if(living.getAITarget() != null && !ElixirRegistry.EFFECT_MASKING.canEntityBeSeenBy(living.getAITarget(), living)) {
						living.setRevengeTarget(null);
					}
					if(living instanceof EntityCreature) {
						EntityCreature creature = (EntityCreature)living;
						if(creature.getEntityToAttack() instanceof EntityLivingBase) {
							if(!ElixirRegistry.EFFECT_MASKING.canEntityBeSeenBy((EntityLivingBase)creature.getEntityToAttack(), creature)) {
								creature.setTarget(null);
							}
						}
					}
					if(living instanceof EntityLiving) {
						EntityLiving entityLiving = (EntityLiving)living;
						if(entityLiving.getAttackTarget() != null && !ElixirRegistry.EFFECT_MASKING.canEntityBeSeenBy(entityLiving.getAttackTarget(), entityLiving)) {
							entityLiving.setAttackTarget(null);
						}
					}
				}
			}
		}
	}*/

	private boolean ignoreSetAttackTarget = false;
	@SubscribeEvent
	public void onSetAttackTarget(LivingSetAttackTargetEvent event) {
		if(!this.ignoreSetAttackTarget) {
			if(((EntityLiving)event.entityLiving).getAttackTarget() != null && !ElixirRegistry.EFFECT_MASKING.canEntityBeSeenBy(((EntityLiving)event.entityLiving).getAttackTarget(), event.entityLiving)) {
				this.ignoreSetAttackTarget = true;
				((EntityLiving)event.entityLiving).setAttackTarget(null);
			}
		} else {
			this.ignoreSetAttackTarget = false;
		}
	}

	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event) {
		EntityPlayer player = event.entityPlayer;
		if(player != null && ElixirRegistry.EFFECT_SWIFTARM.isActive(player) && ElixirRegistry.EFFECT_SWIFTARM.getStrength(player) >= 0) {
			event.newSpeed *= 1.0F + (ElixirRegistry.EFFECT_SWIFTARM.getStrength(player) + 1) * 0.3F;
		}
	}

	@SubscribeEvent
	public void onStartUseItem(PlayerUseItemEvent.Start event) {
		EntityPlayer player = event.entityPlayer;
		if(player != null && ElixirRegistry.EFFECT_SWIFTARM.isActive(player) && ElixirRegistry.EFFECT_SWIFTARM.getStrength(player) >= 0) {
			float newDuration = event.duration;
			newDuration *= 1.0F - 0.5F / 4.0F * (ElixirRegistry.EFFECT_SWIFTARM.getStrength(player) + 1);
			event.duration = MathHelper.ceiling_float_int(newDuration);
		}
	}
}