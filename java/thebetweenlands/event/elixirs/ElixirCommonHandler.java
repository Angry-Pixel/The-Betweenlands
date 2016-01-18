package thebetweenlands.event.elixirs;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import thebetweenlands.herblore.elixirs.ElixirEffectRegistry;

public class ElixirCommonHandler {
	public static final ElixirCommonHandler INSTANCE = new ElixirCommonHandler();

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
			if(event.entityLiving instanceof EntityLiving && ((EntityLiving)event.entityLiving).getAttackTarget() != null && !ElixirEffectRegistry.EFFECT_MASKING.canEntityBeSeenBy(((EntityLiving)event.entityLiving).getAttackTarget(), event.entityLiving)) {
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
		if(player != null) {
			if(ElixirEffectRegistry.EFFECT_SWIFTARM.isActive(player) && ElixirEffectRegistry.EFFECT_SWIFTARM.getStrength(player) >= 0) {
				event.newSpeed *= 1.0F + (ElixirEffectRegistry.EFFECT_SWIFTARM.getStrength(player) + 1) * 0.3F;
			}
			if(ElixirEffectRegistry.EFFECT_SLUGARM.isActive(player) && ElixirEffectRegistry.EFFECT_SLUGARM.getStrength(player) >= 0) {
				event.newSpeed /= 1.0F + (ElixirEffectRegistry.EFFECT_SLUGARM.getStrength(player) + 1) * 0.3F;
			}
		}
	}

	@SubscribeEvent
	public void onStartUseItem(PlayerUseItemEvent.Start event) {
		EntityPlayer player = event.entityPlayer;
		if(player != null) {
			if(ElixirEffectRegistry.EFFECT_SWIFTARM.isActive(player) && ElixirEffectRegistry.EFFECT_SWIFTARM.getStrength(player) >= 0) {
				float newDuration = event.duration;
				newDuration *= 1.0F - 0.5F / 4.0F * (ElixirEffectRegistry.EFFECT_SWIFTARM.getStrength(player) + 1);
				event.duration = MathHelper.ceiling_float_int(newDuration);
			}
			if(ElixirEffectRegistry.EFFECT_SLUGARM.isActive(player) && ElixirEffectRegistry.EFFECT_SLUGARM.getStrength(player) >= 0) {
				if(event.item != null && event.item.getItem() instanceof ItemBow == false) {
					float newDuration = event.duration;
					newDuration /= 1.0F - 0.5F / 4.0F * (ElixirEffectRegistry.EFFECT_SLUGARM.getStrength(player) + 1);
					event.duration = MathHelper.ceiling_float_int(newDuration);
				}
			}
		}
	}

	@SubscribeEvent
	public void onShootArrow(ArrowLooseEvent event) {
		if(ElixirEffectRegistry.EFFECT_WEAKBOW.isActive(event.entityLiving)) {
			event.charge = Math.min(event.charge, 10);
			event.charge *= 1.0F - (ElixirEffectRegistry.EFFECT_WEAKBOW.getStrength(event.entityLiving) + 1) / 4.0F * 0.75F;
		}
	}

	private static final AttributeModifier FOLLOW_RANGE_MODIFIER = new AttributeModifier("24ce3c60-ae87-4e31-8fc3-bf3f40ab37ca", 10, 0);

	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entityLivingBase = event.entityLiving;
		if(ElixirEffectRegistry.EFFECT_SPIDERBREED.isActive(entityLivingBase)) {
			int strength = ElixirEffectRegistry.EFFECT_SPIDERBREED.getStrength(entityLivingBase);
			float relStrength = Math.min((strength + 1) / 4.0F, 1.0F);
			Vec3 lookVec = entityLivingBase.getLookVec().normalize();
			if(entityLivingBase.moveForward < 0.0F) {
				lookVec.yCoord *= -1;
			}
			if((!entityLivingBase.onGround || this.isEntityOnWall(entityLivingBase)) && (entityLivingBase.isCollidedHorizontally || entityLivingBase.isCollidedVertically)) {
				if(entityLivingBase instanceof EntityPlayer) {
					entityLivingBase.motionY = lookVec.yCoord * 0.22F * relStrength;
				} else {
					entityLivingBase.motionY = 0.22F * relStrength;
				}
			}
			if(!entityLivingBase.onGround && this.isEntityOnWall(entityLivingBase)) {
				if(entityLivingBase.motionY < 0.0F && (lookVec.yCoord > 0.0F || (entityLivingBase.moveForward == 0.0F && entityLivingBase.moveStrafing == 0.0F))) {
					entityLivingBase.motionY *= 0.9F - relStrength * 0.5F;
				}
				if(entityLivingBase.isSneaking()) {
					entityLivingBase.motionY *= 0.15F * (1.0F - relStrength);
				}
				entityLivingBase.motionX *= relStrength;
				entityLivingBase.motionZ *= relStrength;
				entityLivingBase.fallDistance = 0.0F;
			}
		}

		if(ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.isActive(entityLivingBase) && !entityLivingBase.isInWater()) {
			Block blockBelow = entityLivingBase.worldObj.getBlock(MathHelper.floor_double(entityLivingBase.posX), MathHelper.floor_double(entityLivingBase.boundingBox.minY - 0.1D), MathHelper.floor_double(entityLivingBase.posZ));
			if(blockBelow.getMaterial().isLiquid()) {
				float relStrength = Math.min((ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.getStrength(entityLivingBase)) / 4.0F, 1.0F);
				entityLivingBase.motionX *= 0.1F + relStrength * 0.9F;
				entityLivingBase.motionZ *= 0.1F + relStrength * 0.9F;
				if(entityLivingBase.motionY < 0.0D) entityLivingBase.motionY = 0.0D;
				entityLivingBase.onGround = true;
			}
		}

		if(entityLivingBase.isInWater() && ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.isActive(entityLivingBase)) {
			if(entityLivingBase.motionY > -0.1F) entityLivingBase.motionY -= 0.04F;
		}

		if(ElixirEffectRegistry.EFFECT_CATSEYES.isActive(entityLivingBase)) {
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), ElixirEffectRegistry.EFFECT_CATSEYES.getDuration(entityLivingBase), ElixirEffectRegistry.EFFECT_CATSEYES.getStrength(entityLivingBase)));
			ElixirEffectRegistry.EFFECT_CATSEYES.removeElixir(entityLivingBase);
		}

		if(ElixirEffectRegistry.EFFECT_POISONSTING.isActive(entityLivingBase)) {
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.poison.getId(), ElixirEffectRegistry.EFFECT_POISONSTING.getDuration(entityLivingBase), ElixirEffectRegistry.EFFECT_POISONSTING.getStrength(entityLivingBase)));
			ElixirEffectRegistry.EFFECT_POISONSTING.removeElixir(entityLivingBase);
		}

		if(ElixirEffectRegistry.EFFECT_DRUNKARD.isActive(entityLivingBase)) {
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.confusion.getId(), ElixirEffectRegistry.EFFECT_DRUNKARD.getDuration(entityLivingBase), ElixirEffectRegistry.EFFECT_DRUNKARD.getStrength(entityLivingBase)));
			ElixirEffectRegistry.EFFECT_DRUNKARD.removeElixir(entityLivingBase);
		}

		if(ElixirEffectRegistry.EFFECT_BLINDMAN.isActive(entityLivingBase)) {
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.blindness.getId(), ElixirEffectRegistry.EFFECT_BLINDMAN.getDuration(entityLivingBase), ElixirEffectRegistry.EFFECT_BLINDMAN.getStrength(entityLivingBase)));
			ElixirEffectRegistry.EFFECT_BLINDMAN.removeElixir(entityLivingBase);
		}

		//Stenching
		if(entityLivingBase instanceof EntityPlayer == false && entityLivingBase instanceof EntityMob) {
			EntityLiving entityLiving = (EntityLiving) entityLivingBase;
			IAttributeInstance followRangeAttrib = entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
			if(followRangeAttrib != null) {
				List<EntityPlayer> stenchingPlayers = this.getStenchingPlayersInRange(entityLiving);
				if(stenchingPlayers.isEmpty()) {
					if(followRangeAttrib.getModifier(FOLLOW_RANGE_MODIFIER.getID()) != null) {
						followRangeAttrib.removeModifier(FOLLOW_RANGE_MODIFIER);
					}
				} else {
					AttributeModifier currentModifier = followRangeAttrib.getModifier(FOLLOW_RANGE_MODIFIER.getID());
					if(entityLiving.getAttackTarget() == null || 
							entityLiving.getAITarget() == null ||
							(entityLiving instanceof EntityCreature && ((EntityCreature)entityLiving).getEntityToAttack() == null)) {
						EntityPlayer closestPlayer = null;
						for(EntityPlayer player : stenchingPlayers) {
							if(closestPlayer == null || player.getDistanceSqToEntity(entityLiving) < closestPlayer.getDistanceSqToEntity(entityLiving))
								closestPlayer = player;
						}
						if(entityLiving.ticksExisted % 20 == 0) {
							int strength = ElixirEffectRegistry.EFFECT_STENCHING.getStrength(closestPlayer);
							AttributeModifier stenchModifier = this.getFollowRangeModifier(strength);
							boolean shouldApplyModifier = currentModifier == null || currentModifier.getAmount() < stenchModifier.getAmount();
							if(shouldApplyModifier) {
								if(currentModifier != null) {
									followRangeAttrib.removeModifier(currentModifier);
								}
								followRangeAttrib.applyModifier(this.getFollowRangeModifier(strength));
							}
							entityLiving.setAttackTarget(closestPlayer);
							entityLiving.setRevengeTarget(closestPlayer);
							if(entityLiving instanceof EntityCreature) {
								((EntityCreature)entityLiving).setTarget(closestPlayer);
							}
						}
					}
				}
			}
		}
	}
	private AttributeModifier getFollowRangeModifier(int strength) {
		return new AttributeModifier(FOLLOW_RANGE_MODIFIER.getID(), FOLLOW_RANGE_MODIFIER.getName() + " " + strength, FOLLOW_RANGE_MODIFIER.getAmount() / 4.0D * (Math.min(strength, 4)), FOLLOW_RANGE_MODIFIER.getOperation());
	}
	private List<EntityPlayer> getStenchingPlayersInRange(EntityLivingBase entity) {
		List<EntityPlayer> playerList = new ArrayList<EntityPlayer>();
		for(EntityPlayer player : (List<EntityPlayer>) entity.worldObj.playerEntities) {
			if(ElixirEffectRegistry.EFFECT_STENCHING.isActive(player)) {
				int strength = ElixirEffectRegistry.EFFECT_STENCHING.getStrength(player);
				double spottingRange = this.getSpottingRange(entity, strength);
				if(player.getDistanceSqToEntity(entity) <= spottingRange * spottingRange) {
					playerList.add(player);
				}
			}
		}
		return playerList;
	}
	private double getSpottingRange(EntityLivingBase entity, int strength) {
		IAttributeInstance followRangeAttrib = entity.getEntityAttribute(SharedMonsterAttributes.followRange);
		AttributeModifier rangeMod = followRangeAttrib.getModifier(FOLLOW_RANGE_MODIFIER.getID());
		if(rangeMod != null) {
			followRangeAttrib.removeModifier(rangeMod);
		}
		AttributeModifier tempRangeMod = this.getFollowRangeModifier(strength);
		followRangeAttrib.applyModifier(tempRangeMod);
		double spottingRange = followRangeAttrib == null ? (16.0D + FOLLOW_RANGE_MODIFIER.getAmount() / 4.0D * Math.min(strength, 4)) : followRangeAttrib.getAttributeValue();
		followRangeAttrib.removeModifier(tempRangeMod);
		if(rangeMod != null) {
			followRangeAttrib.applyModifier(rangeMod);
		}
		return spottingRange;
	}
	private boolean isEntityOnWall(EntityLivingBase entity) {
		AxisAlignedBB bb = entity.boundingBox.expand(0.05D, 0.05D, 0.05D);
		int mX = MathHelper.floor_double(bb.minX);
		int mY = MathHelper.floor_double(bb.minY + 0.06D);
		int mZ = MathHelper.floor_double(bb.minZ);
		for (int y2 = mY; y2 < bb.maxY - 0.06D; y2++) {
			for (int x2 = mX; x2 < bb.maxX; x2++) {
				for (int z2 = mZ; z2 < bb.maxZ; z2++) {
					Block block = entity.worldObj.getBlock(x2, y2, z2);
					if (block != null && block.isCollidable()) {
						AxisAlignedBB boundingBox = block.getCollisionBoundingBoxFromPool(entity.worldObj, x2, y2, z2);
						if(boundingBox != null && boundingBox.intersectsWith(bb)) return true;
					}
				}
			}
		}
		return false;
	}

	@SubscribeEvent
	public void onLivingJump(LivingJumpEvent event) {
		EntityLivingBase living = event.entityLiving;
		if(ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.isActive(living)) {
			float relStrength = Math.min((ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.getStrength(living)) / 9.0F, 0.4F);
			living.motionY *= 1.0F + relStrength;
		}
	}
}