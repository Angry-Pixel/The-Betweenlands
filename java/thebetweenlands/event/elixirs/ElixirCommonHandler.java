package thebetweenlands.event.elixirs;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
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

	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase living = event.entityLiving;
		if(ElixirEffectRegistry.EFFECT_SPIDERBREED.isActive(living)) {
			int strength = ElixirEffectRegistry.EFFECT_SPIDERBREED.getStrength(living);
			float relStrength = Math.min((strength + 1) / 4.0F, 1.0F);
			Vec3 lookVec = living.getLookVec().normalize();
			if(living.moveForward < 0.0F) {
				lookVec.yCoord *= -1;
			}
			if((!living.onGround || this.isEntityOnWall(living)) && (living.isCollidedHorizontally || living.isCollidedVertically)) {
				if(living instanceof EntityPlayer) {
					living.motionY = lookVec.yCoord * 0.22F * relStrength;
				} else {
					living.motionY = 0.22F * relStrength;
				}
			}
			if(!living.onGround && this.isEntityOnWall(living)) {
				if(living.motionY < 0.0F && (lookVec.yCoord > 0.0F || (living.moveForward == 0.0F && living.moveStrafing == 0.0F))) {
					living.motionY *= 0.9F - relStrength * 0.5F;
				}
				if(living.isSneaking()) {
					living.motionY *= 0.15F * (1.0F - relStrength);
				}
				living.motionX *= relStrength;
				living.motionZ *= relStrength;
				living.fallDistance = 0.0F;
			}
		}

		if(ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.isActive(living) && !living.isInWater()) {
			Block blockBelow = living.worldObj.getBlock(MathHelper.floor_double(living.posX), MathHelper.floor_double(living.boundingBox.minY - 0.1D), MathHelper.floor_double(living.posZ));
			if(blockBelow.getMaterial().isLiquid()) {
				float relStrength = Math.min((ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.getStrength(living)) / 4.0F, 1.0F);
				living.motionX *= 0.1F + relStrength * 0.9F;
				living.motionZ *= 0.1F + relStrength * 0.9F;
				if(living.motionY < 0.0D) living.motionY = 0.0D;
				living.onGround = true;
			}
		}

		if(living.isInWater() && ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.isActive(living)) {
			if(living.motionY > -0.1F) living.motionY -= 0.04F;
		}

		if(ElixirEffectRegistry.EFFECT_CATSEYES.isActive(living)) {
			living.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), ElixirEffectRegistry.EFFECT_CATSEYES.getDuration(living), ElixirEffectRegistry.EFFECT_CATSEYES.getStrength(living)));
			ElixirEffectRegistry.EFFECT_CATSEYES.removeElixir(living);
		}

		if(ElixirEffectRegistry.EFFECT_POISONSTING.isActive(living)) {
			living.addPotionEffect(new PotionEffect(Potion.poison.getId(), ElixirEffectRegistry.EFFECT_POISONSTING.getDuration(living), ElixirEffectRegistry.EFFECT_POISONSTING.getStrength(living)));
			ElixirEffectRegistry.EFFECT_POISONSTING.removeElixir(living);
		}

		if(ElixirEffectRegistry.EFFECT_DRUNKARD.isActive(living)) {
			living.addPotionEffect(new PotionEffect(Potion.confusion.getId(), ElixirEffectRegistry.EFFECT_DRUNKARD.getDuration(living), ElixirEffectRegistry.EFFECT_DRUNKARD.getStrength(living)));
			ElixirEffectRegistry.EFFECT_DRUNKARD.removeElixir(living);
		}

		if(ElixirEffectRegistry.EFFECT_BLINDMAN.isActive(living)) {
			living.addPotionEffect(new PotionEffect(Potion.blindness.getId(), ElixirEffectRegistry.EFFECT_BLINDMAN.getDuration(living), ElixirEffectRegistry.EFFECT_BLINDMAN.getStrength(living)));
			ElixirEffectRegistry.EFFECT_BLINDMAN.removeElixir(living);
		}
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