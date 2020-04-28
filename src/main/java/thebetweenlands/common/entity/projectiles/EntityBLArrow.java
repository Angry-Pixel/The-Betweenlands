package thebetweenlands.common.entity.projectiles;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
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
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
import thebetweenlands.common.entity.EntityShock;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWormHelper;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityBLArrow extends EntityArrow implements IThrowableEntity /*for shooter sync*/ {
	@SuppressWarnings("unchecked")
	private static final Predicate<Entity> ARROW_TARGETS = Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, new Predicate<Entity>() {
		@Override
		public boolean apply(@Nullable Entity entity) {
			return entity.canBeCollidedWith();
		}
	});

	private static final DataParameter<String> DW_TYPE = EntityDataManager.<String>createKey(EntityBLArrow.class, DataSerializers.STRING);

	private int ticksSpentInAir = 0;

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
	@Nullable
	protected Entity findEntityOnPath(Vec3d start, Vec3d end) {
		List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D), ARROW_TARGETS);

		Entity hit = null;
		double minDstSq = 0.0D;

		for(Entity entity : list) {
			if(this.isNotShootingEntity(entity) || this.ticksSpentInAir >= 5) {
				AxisAlignedBB checkBox = entity.getEntityBoundingBox().grow(0.3D);
				RayTraceResult rayTrace = checkBox.calculateIntercept(start, end);

				if(rayTrace != null) {
					double dstSq = start.squareDistanceTo(rayTrace.hitVec);

					if(dstSq < minDstSq || minDstSq == 0.0D) {
						hit = entity;
						minDstSq = dstSq;
					}
				}
			}
		}

		return hit;
	}

	private boolean isNotShootingEntity(Entity entity) {
		if(entity == this.shootingEntity) {
			return false;
		} else if(this.shootingEntity instanceof EntityPlayer == false && this.shootingEntity != null && this.shootingEntity.getRidingEntity() == entity) {
			return false;
		} else if(this.shootingEntity instanceof EntityPlayer && this.shootingEntity != null && entity instanceof IEntityOwnable &&
				((IEntityOwnable) entity).getOwner() == this.shootingEntity && this.shootingEntity.getRecursivePassengers().contains(entity)) {
			return false;
		}
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.inGround) {
			this.ticksSpentInAir = 0;
		} else {
			this.ticksSpentInAir++;
		}

		if(this.world.isRemote && (this.getArrowType() == EnumArrowType.SHOCK || this.getArrowType() == EnumArrowType.CHIROMAW_SHOCK_BARB)) {
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
				this.world.spawnEntity(new EntityShock(this.world, this, living, this.isWet() || this.isInWater() || this.world.isRainingAt(this.getPosition().up())));
			}
			break;
		case CHIROMAW_BARB:
			if(living.isNonBoss()) {
				living.addPotionEffect(ElixirEffectRegistry.EFFECT_PETRIFY.createEffect(40, 1));
			} 
			break;
		case CHIROMAW_SHOCK_BARB:
			if(!this.world.isRemote) {
				this.world.spawnEntity(new EntityShock(this.world, this, living, this.isWet() || this.isInWater() || this.world.isRainingAt(this.getPosition().up())));
			}
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
			if(getArrowType() == EnumArrowType.CHIROMAW_BARB || getArrowType() == EnumArrowType.CHIROMAW_SHOCK_BARB) {
				if(soundIn == SoundEvents.ENTITY_ARROW_HIT)
					soundIn = SoundRegistry.CHIROMAW_MATRIARCH_BARB_HIT;
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
			return new ItemStack(ItemRegistry.ANGLER_TOOTH_ARROW);
		default:
			return ItemStack.EMPTY;
		}
	}
}
