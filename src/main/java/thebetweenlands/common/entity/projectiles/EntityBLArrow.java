package thebetweenlands.common.entity.projectiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWormHelper;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.network.clientbound.MessageShockArrowHit;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityBLArrow extends EntityArrow implements IThrowableEntity /*for shooter sync*/ {
	private static final DataParameter<String> DW_TYPE = EntityDataManager.<String>createKey(EntityBLArrow.class, DataSerializers.STRING);

	public EntityBLArrow(World worldIn) {
		super(worldIn);
	}

	public EntityBLArrow(World worldIn, EntityLivingBase shooter) {
		super(worldIn, shooter);
	}

	@Override
	public Entity getThrower() {
		return this.shootingEntity;
	}
	
	@Override
	public void setThrower(Entity entity) {
		this.shootingEntity = entity;
	}
	
	@Override
	public void entityInit() {
		super.entityInit();
		this.dataManager.register(DW_TYPE, "");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setString("arrowType", this.getArrowType().getName());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.setType(EnumArrowType.getEnumFromString(nbt.getString("arrowType")));
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
	
		if(this.world.isRemote && this.getArrowType() == EnumArrowType.SHOCK) {
			this.spawnLightningArcs();
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void spawnLightningArcs() {
		if(this.world.rand.nextInt(!this.inGround ? 2 : 20) == 0) {
			float ox = this.world.rand.nextFloat() - 0.5f + (!this.inGround ? (float)this.motionX : 0);
			float oy = this.world.rand.nextFloat() - 0.5f + (!this.inGround ? (float)this.motionY : 0);
			float oz = this.world.rand.nextFloat() - 0.5f + (!this.inGround ? (float)this.motionZ : 0);
			
			Particle particle = BLParticles.LIGHTNING_ARC.create(this.world, this.posX, this.posY, this.posZ, 
					ParticleArgs.get()
					.withMotion(!this.inGround ? this.motionX : 0, !this.inGround ? this.motionY : 0, !this.inGround ? this.motionZ : 0)
					.withColor(0.3f, 0.5f, 1.0f, 0.9f)
					.withData(new Vec3d(this.posX + ox, this.posY + oy, this.posZ + oz)));
			
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, particle);
		}
	}

	@Override
	protected void arrowHit(EntityLivingBase living) {
		switch(this.getArrowType()) {
		case ANGLER_POISON:
			living.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 2));
			break;
		case OCTINE:
			if(living.isBurning()) {
				living.setFire(9);
			} else {
				living.setFire(5);
			}
			break;
		case BASILISK:
			if(living.isNonBoss()) {
				living.addPotionEffect(ElixirEffectRegistry.EFFECT_PETRIFY.createEffect(100, 1));
			} else {
				living.addPotionEffect(ElixirEffectRegistry.EFFECT_PETRIFY.createEffect(40, 1));
			}
			break;
		case WORM:
			if (!getEntityWorld().isRemote) {
				EntityTinySludgeWormHelper worm = new EntityTinySludgeWormHelper(getEntityWorld());
				worm.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
				worm.setAttackTarget(living);
				if(this.shootingEntity instanceof EntityPlayer) {
					worm.setOwnerId(this.shootingEntity.getUniqueID());
				}
				getEntityWorld().spawnEntity(worm);
				this.setDead();
			}
			break;
		case SHOCK:
			if(!this.world.isRemote) {
				DamageSource damagesource;
	            if (this.shootingEntity == null) {
	                damagesource = DamageSource.causeArrowDamage(this, this);
	            } else {
	                damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
	            }
				
				List<Pair<Entity, Entity>> chain = new ArrayList<>();
				
				Set<EntityLivingBase> targets = new HashSet<>();

				targets.add(living);
				
				int iters = 2 + this.world.rand.nextInt(3);
				
				if(this.isWet() || this.isInWater() || this.world.isRainingAt(this.getPosition().up())) {
					iters = iters * 2;
				}
				
				for(int i = 0; i < iters; i++) {
					Set<EntityLivingBase> newTargets = new HashSet<>();
					
					entityLoop: for(Entity entity : targets) {
						boolean isWet = entity.isWet() || entity.isInWater() || this.world.isRainingAt(entity.getPosition().up());
						
						List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox().grow(isWet ? 6 : 4));
						
						if(entities.size() > 1) {
							Collections.sort(entities, (e1, e2) -> Double.compare(e1.getDistanceSq(entity), e2.getDistanceSq(entity)));
							
							for(int j = 1; j < entities.size(); j++) {
								EntityLivingBase newTarget = entities.get(j);
								
								if(!targets.contains(newTarget) && !newTargets.contains(newTarget)) {
									newTargets.add(newTarget);
									
									chain.add(Pair.of(entity, newTarget));
									
						            float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
						            float damage = MathHelper.ceil((double)f * this.getDamage());
						            if (this.getIsCritical()) {
						            	damage += this.rand.nextInt(i / 2 + 2);
						            }
						            
						            newTarget.attackEntityFrom(damagesource, isWet ? 2 * damage : damage);
									
									continue entityLoop;
								}
							}
						}
					}
					
					targets.addAll(newTargets);
				}
				
				TheBetweenlands.networkWrapper.sendToAllTracking(new MessageShockArrowHit(chain), this);
			}
			break;
		case CHIROMAW_BARB:
			if(living.isNonBoss()) {
				living.addPotionEffect(ElixirEffectRegistry.EFFECT_PETRIFY.createEffect(40, 1));
			} 
			break;
		default:
		}
	}

	@Override
	protected void onHit(RayTraceResult raytrace) {
		super.onHit(raytrace);
		
		if(raytrace.entityHit == null && raytrace.getBlockPos() != null && raytrace.sideHit != null && this.getArrowType() == EnumArrowType.OCTINE) {
			BlockPos pos = raytrace.getBlockPos().offset(raytrace.sideHit);
			IBlockState state = this.world.getBlockState(pos);
			
			if(ItemRegistry.OCTINE_INGOT.isTinder(new ItemStack(ItemRegistry.OCTINE_INGOT), ItemStack.EMPTY, state)) {
				this.world.setBlockState(pos, Blocks.FIRE.getDefaultState());
			}
		}
	}

	@Override
	public void playSound(SoundEvent soundIn, float volume, float pitch) {
		if (!this.isSilent()) {
			if(getArrowType() == EnumArrowType.CHIROMAW_BARB) {
				if(soundIn == SoundEvents.ENTITY_ARROW_HIT)
					soundIn = SoundEvents.ENTITY_ITEMFRAME_REMOVE_ITEM; // TODO awaiting sounds
			}	
		}
		super.playSound(soundIn, volume, pitch);
	}

	/**
	 * Sets the arrow type
	 * @param type
	 */
	public void setType(EnumArrowType type) {
		this.dataManager.set(DW_TYPE, type.getName());
	}

	/**
	 * Returns the arrow type
	 * @return
	 */
	public EnumArrowType getArrowType(){
		return EnumArrowType.getEnumFromString(this.dataManager.get(DW_TYPE));
	}

	@Override
	protected ItemStack getArrowStack() {
		switch(this.getArrowType()) {
		case ANGLER_POISON:
			return new ItemStack(ItemRegistry.POISONED_ANGLER_TOOTH_ARROW);
		case OCTINE:
			return new ItemStack(ItemRegistry.OCTINE_ARROW);
		case BASILISK:
			return new ItemStack(ItemRegistry.BASILISK_ARROW);
		case WORM:
			return new ItemStack(ItemRegistry.SLUDGE_WORM_ARROW);
		case SHOCK:
			return new ItemStack(ItemRegistry.SHOCK_ARROW);
		case CHIROMAW_BARB:
			return new ItemStack(ItemRegistry.CHIROMAW_BARB);
		case DEFAULT:
		default:
			return new ItemStack(ItemRegistry.ANGLER_TOOTH_ARROW);
		}
	}
}
