package thebetweenlands.common.handler;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemBow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;

import java.util.ArrayList;
import java.util.List;

public class ElixirCommonHandler {

    public static final ElixirCommonHandler INSTANCE = new ElixirCommonHandler();

    public ElixirCommonHandler() { }

    //This can be used to stop the entities from attacking if the player is out of "sight"
    //Not needed for now, but I'll keep it here just in case
	/*@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if(event.phase == TickEvent.Phase.START) {
			for(Entity e : event.world.loadedEntityList) {
				if(e instanceof EntityLivingBase) {
					EntityLivingBase living = (EntityLivingBase)e;
					if(living.getRevengeTarget() != null && !ElixirEffectRegistry.EFFECT_MASKING.canEntityBeSeenBy(living.getRevengeTarget(), living)) {
						living.setRevengeTarget(null);
					}
					if(living instanceof EntityCreature) {
						EntityCreature creature = (EntityCreature)living;
						if(creature.getAttackTarget() instanceof EntityLivingBase) {
							if(!ElixirEffectRegistry.EFFECT_MASKING.canEntityBeSeenBy(creature.getAttackTarget(), creature)) {
								creature.setAttackTarget(null);
							}
						}
					}
					if(living instanceof EntityLiving) {
						EntityLiving entityLiving = (EntityLiving)living;
						if(entityLiving.getAttackTarget() != null && !ElixirEffectRegistry.EFFECT_MASKING.canEntityBeSeenBy(entityLiving.getAttackTarget(), entityLiving)) {
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
            if(event.getEntityLiving() instanceof EntityLiving && (event.getTarget() != null && !ElixirEffectRegistry.EFFECT_MASKING.canEntityBeSeenBy(event.getTarget(), event.getEntityLiving()))) {
                this.ignoreSetAttackTarget = true;
                ((EntityLiving)event.getEntityLiving()).setAttackTarget(null);
            }
        } else {
            this.ignoreSetAttackTarget = false;
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        EntityPlayer player = event.getEntityPlayer();
        if(player != null) {
            if(ElixirEffectRegistry.EFFECT_SWIFTARM.isActive(player) && ElixirEffectRegistry.EFFECT_SWIFTARM.getStrength(player) >= 0) {
                event.setNewSpeed(event.getNewSpeed() * (1.0F + (ElixirEffectRegistry.EFFECT_SWIFTARM.getStrength(player) + 1) * 0.3F));
            }
            if(ElixirEffectRegistry.EFFECT_SLUGARM.isActive(player) && ElixirEffectRegistry.EFFECT_SLUGARM.getStrength(player) >= 0) {
                event.setNewSpeed(event.getNewSpeed() / (1.0F + (ElixirEffectRegistry.EFFECT_SLUGARM.getStrength(player) + 1) * 0.3F));
            }
        }
    }

    @SubscribeEvent
    public void onStartUseItem(LivingEntityUseItemEvent.Start event) {
        EntityLivingBase living = event.getEntityLiving();
        if(living != null) {
            if(ElixirEffectRegistry.EFFECT_SWIFTARM.isActive(living) && ElixirEffectRegistry.EFFECT_SWIFTARM.getStrength(living) >= 0) {
                float newDuration = event.getDuration();
                newDuration *= 1.0F - 0.5F / 4.0F * (ElixirEffectRegistry.EFFECT_SWIFTARM.getStrength(living) + 1);
                event.setDuration(MathHelper.ceil(newDuration));
            }
            if(ElixirEffectRegistry.EFFECT_SLUGARM.isActive(living) && ElixirEffectRegistry.EFFECT_SLUGARM.getStrength(living) >= 0) {
                if(!event.getItem().isEmpty() && !(event.getItem().getItem() instanceof ItemBow)) {
                    float newDuration = event.getDuration();
                    newDuration /= 1.0F - 0.5F / 4.0F * (ElixirEffectRegistry.EFFECT_SLUGARM.getStrength(living) + 1);
                    event.setDuration(MathHelper.ceil(newDuration));
                }
            }
        }
    }

    @SubscribeEvent
    public void onShootArrow(ArrowLooseEvent event) {
        if(ElixirEffectRegistry.EFFECT_WEAKBOW.isActive(event.getEntityLiving())) {
            event.setCharge(Math.min(event.getCharge(), 10));
            event.setCharge((int) (event.getCharge() * (1.0F - (ElixirEffectRegistry.EFFECT_WEAKBOW.getStrength(event.getEntityLiving()) + 1) / 4.0F * 0.75F)));
        }
    }

    private static final AttributeModifier FOLLOW_RANGE_MODIFIER = new AttributeModifier("24ce3c60-ae87-4e31-8fc3-bf3f40ab37ca", 10, 0);

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entityLivingBase = event.getEntityLiving();
        
        if(ElixirEffectRegistry.EFFECT_SPIDERBREED.isActive(entityLivingBase)) {
            int strength = ElixirEffectRegistry.EFFECT_SPIDERBREED.getStrength(entityLivingBase);
            float relStrength = Math.min((strength + 1) / 4.0F, 1.0F);
            Vec3d lookVec = entityLivingBase.getLookVec().normalize();
            if(entityLivingBase.moveForward < 0.0F) {
                lookVec = new Vec3d(lookVec.x, lookVec.y * -1, lookVec.z);
            }
            if((!entityLivingBase.onGround || this.isEntityOnWall(entityLivingBase)) && (entityLivingBase.collidedHorizontally || entityLivingBase.collidedVertically)) {
                if(entityLivingBase instanceof EntityPlayer) {
                    entityLivingBase.motionY = lookVec.y * 0.22F * relStrength;
                } else {
                    entityLivingBase.motionY = 0.22F * relStrength;
                }
            }
            if(!entityLivingBase.onGround && this.isEntityOnWall(entityLivingBase)) {
                if(entityLivingBase.motionY < 0.0F && (lookVec.y > 0.0F || (entityLivingBase.moveForward == 0.0F && entityLivingBase.moveStrafing == 0.0F))) {
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

        if(ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.isActive(entityLivingBase) && !entityLivingBase.isInWater() && !entityLivingBase.isSneaking()) {
            IBlockState state = entityLivingBase.world.getBlockState(new BlockPos(entityLivingBase.posX, entityLivingBase.getEntityBoundingBox().minY + Math.min(-0.1D, entityLivingBase.motionY), entityLivingBase.posZ));
            if(state.getMaterial().isLiquid()) {
                float relStrength = Math.min((ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.getStrength(entityLivingBase)) / 4.0F, 1.0F);
                entityLivingBase.motionX *= 0.1F + relStrength * 0.9F;
                entityLivingBase.motionZ *= 0.1F + relStrength * 0.9F;
                if(entityLivingBase.motionY < 0.0D) entityLivingBase.motionY = 0.0D;
                entityLivingBase.onGround = true;
                entityLivingBase.fallDistance = 0.0F;
            }
        }

        if(ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.isActive(entityLivingBase) && entityLivingBase.isInWater()) {
            if(entityLivingBase.motionY > -0.1F) {
            	entityLivingBase.motionY -= 0.005F + 0.035F / 5.0F *  (1 + ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.getStrength(entityLivingBase));
            }
        }

        if(!entityLivingBase.world.isRemote) {
	        if(ElixirEffectRegistry.EFFECT_CATSEYES.isActive(entityLivingBase)) {
	            entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, ElixirEffectRegistry.EFFECT_CATSEYES.getDuration(entityLivingBase), ElixirEffectRegistry.EFFECT_CATSEYES.getStrength(entityLivingBase)));
	            ElixirEffectRegistry.EFFECT_CATSEYES.removeElixir(entityLivingBase);
	        }
	
	        if(ElixirEffectRegistry.EFFECT_POISONSTING.isActive(entityLivingBase)) {
	            entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.POISON, ElixirEffectRegistry.EFFECT_POISONSTING.getDuration(entityLivingBase), ElixirEffectRegistry.EFFECT_POISONSTING.getStrength(entityLivingBase)));
	            ElixirEffectRegistry.EFFECT_POISONSTING.removeElixir(entityLivingBase);
	        }
	
	        if(ElixirEffectRegistry.EFFECT_DRUNKARD.isActive(entityLivingBase)) {
	            entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, ElixirEffectRegistry.EFFECT_DRUNKARD.getDuration(entityLivingBase), ElixirEffectRegistry.EFFECT_DRUNKARD.getStrength(entityLivingBase)));
	            ElixirEffectRegistry.EFFECT_DRUNKARD.removeElixir(entityLivingBase);
	        }
	
	        if(ElixirEffectRegistry.EFFECT_BLINDMAN.isActive(entityLivingBase)) {
	            entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, ElixirEffectRegistry.EFFECT_BLINDMAN.getDuration(entityLivingBase), ElixirEffectRegistry.EFFECT_BLINDMAN.getStrength(entityLivingBase)));
	            ElixirEffectRegistry.EFFECT_BLINDMAN.removeElixir(entityLivingBase);
	        }
	
	        //Stenching
	        if(!(entityLivingBase instanceof EntityPlayer) && entityLivingBase instanceof EntityMob && entityLivingBase.ticksExisted % 20 == 0) {
	            EntityLiving entityLiving = (EntityLiving) entityLivingBase;
	            IAttributeInstance followRangeAttrib = entityLiving.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
	            if(followRangeAttrib != null) {
	                List<EntityPlayer> stenchingPlayers = this.getStenchingPlayersInRange(entityLiving);
	                if(stenchingPlayers.isEmpty()) {
	                    if(followRangeAttrib.getModifier(FOLLOW_RANGE_MODIFIER.getID()) != null) {
	                        followRangeAttrib.removeModifier(FOLLOW_RANGE_MODIFIER);
	                    }
	                } else {
	                    AttributeModifier currentModifier = followRangeAttrib.getModifier(FOLLOW_RANGE_MODIFIER.getID());
	                    if(entityLiving.getAttackTarget() == null || entityLiving.getRevengeTarget() == null) {
	                        EntityPlayer closestPlayer = null;
	                        for(EntityPlayer player : stenchingPlayers) {
	                            if(closestPlayer == null || player.getDistanceSq(entityLiving) < closestPlayer.getDistanceSq(entityLiving))
	                                closestPlayer = player;
	                        }
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
	                    }
	                }
	            }
	        }
        }
        
        if(entityLivingBase.getActivePotionEffect(ElixirEffectRegistry.ROOT_BOUND) != null) {
        	entityLivingBase.setInWeb();
        	entityLivingBase.motionX = entityLivingBase.motionZ = 0;
		}
        
        if(ElixirEffectRegistry.EFFECT_BASILISK.isActive(entityLivingBase) || ElixirEffectRegistry.EFFECT_PETRIFY.isActive(entityLivingBase)) {
        	entityLivingBase.motionX = entityLivingBase.motionZ = 0;
        }
    }
    private AttributeModifier getFollowRangeModifier(int strength) {
        return new AttributeModifier(FOLLOW_RANGE_MODIFIER.getID(), FOLLOW_RANGE_MODIFIER.getName() + " " + strength, FOLLOW_RANGE_MODIFIER.getAmount() / 4.0D * (Math.min(strength, 4)), FOLLOW_RANGE_MODIFIER.getOperation());
    }
    private List<EntityPlayer> getStenchingPlayersInRange(EntityLivingBase entity) {
        List<EntityPlayer> playerList = new ArrayList<>();
        for(EntityPlayer player : entity.world.playerEntities) {
            if(ElixirEffectRegistry.EFFECT_STENCHING.isActive(player)) {
                int strength = ElixirEffectRegistry.EFFECT_STENCHING.getStrength(player);
                double spottingRange = this.getSpottingRange(entity, strength);
                if(player.getDistanceSq(entity) <= spottingRange * spottingRange) {
                    playerList.add(player);
                }
            }
        }
        return playerList;
    }
    private double getSpottingRange(EntityLivingBase entity, int strength) {
        IAttributeInstance followRangeAttrib = entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
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
        AxisAlignedBB bb = entity.getEntityBoundingBox().grow(0.05D, 0.05D, 0.05D);
        int mX = MathHelper.floor(bb.minX);
        int mY = MathHelper.floor(bb.minY + 0.06D);
        int mZ = MathHelper.floor(bb.minZ);
        for (int y2 = mY; y2 < bb.maxY - 0.06D; y2++) {
            for (int x2 = mX; x2 < bb.maxX; x2++) {
                for (int z2 = mZ; z2 < bb.maxZ; z2++) {
                    BlockPos pos = new BlockPos(x2, y2, z2);
                    IBlockState state = entity.world.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block != null && block.isCollidable()) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox(state, entity.world, pos);
                        if(boundingBox != null && boundingBox.offset(pos).intersects(bb))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        
        if(ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.isActive(living)) {
            float relStrength = Math.min((ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.getStrength(living)) / 9.0F, 0.4F);
            living.motionY *= 1.0F + relStrength;
        }
        
        if(living.getActivePotionEffect(ElixirEffectRegistry.ROOT_BOUND) != null || ElixirEffectRegistry.EFFECT_BASILISK.isActive(living) || ElixirEffectRegistry.EFFECT_PETRIFY.isActive(living)) {
        	living.motionX = 0;
        	living.motionZ = 0;
			if(living.motionY > -0.1D) {
				living.motionY = -0.1D;
				living.velocityChanged = true;
			}
		}
    }
}
