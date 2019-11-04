package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability.BlockCollisionPredicate;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability.EntityCollisionPredicate;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IEntityCustomBlockCollisions;
import thebetweenlands.api.entity.IEntityWithLootModifier;
import thebetweenlands.client.render.model.SpikeRenderer;
import thebetweenlands.client.render.model.entity.ModelBoulderSprite;
import thebetweenlands.common.handler.CustomEntityCollisionsHandler;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class EntityBoulderSprite extends EntityMob implements IEntityCustomBlockCollisions, IEntityAdditionalSpawnData, IEntityWithLootModifier, IEntityBL {
	public static final byte EVENT_STEP = 40;

	protected static final UUID ROLLING_ATTACK_MODIFIER_ATTRIBUTE_UUID = UUID.fromString("6c403225-c522-4d69-aa2c-e7c67463a8c7");

	protected static final DataParameter<Float> ROLL_SPEED = EntityDataManager.createKey(EntityBoulderSprite.class, DataSerializers.FLOAT);

	protected EnumFacing hideoutEntrance = null;
	protected BlockPos hideout = null;

	protected boolean isAiHiding = false;

	private float prevRollAnimationInAirWeight = 0.0F;
	private float prevRollAnimation = 0.0F;
	private float prevRollAnimationWeight = 0.0F;
	private float rollAnimationInAirWeight = 0.0F;
	private float rollAnimationSpeed = 0.0F;
	private float rollAnimation = 0.0F;
	private float rollAnimationWeight = 0.0F;

	protected boolean rollSoundPlayed = false;

	protected double rollingSpeed = 0;
	protected int rollingTicks = 0;
	protected int rollingAccelerationTime = 0;
	protected int rollingDecelerationTime = 0;
	protected int rollingDuration = 0;
	protected Vec3d rollingDir = null;

	protected long stalactitesSeed = 0;
	protected int numStalactites = 0;
	protected int[] stalactiteHeights;

	@Nullable
	@SideOnly(Side.CLIENT)
	public List<SpikeRenderer> stalactites;

	public EntityBoulderSprite(World worldIn) {
		super(worldIn);
		this.setSize(0.9F, 1.2F);
		this.setStalactitesSeed(worldIn.rand.nextLong());
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(ROLL_SPEED, 0.0F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(28);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4);
	}

	@Override
	protected void initEntityAI() {
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false) {
			@Override
			public double getTargetDistance() {
				return 12.0D;
			}
		});
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true) {
			@Override
			public void startExecuting() {
				if(EntityBoulderSprite.this.getAttackTarget() != EntityBoulderSprite.this.getRevengeTarget()) {
					//Cancel hiding
					EntityBoulderSprite.this.setHideout(null);
				}
				super.startExecuting();
			}
		});

		this.tasks.addTask(0, new AIRollTowardsTargetFromHideout(this, 8, 1.2D));
		this.tasks.addTask(1, new AIMoveToHideout(this, 1.5D));
		this.tasks.addTask(2, new AIHide(this, 0.8D));
		this.tasks.addTask(3, new AIFindRandomHideoutFlee(this, 8));
		this.tasks.addTask(4, new AIRollTowardsTarget(this));
		this.tasks.addTask(5, new EntityAIAttackMelee(this, 1, true) {
			@Override
			public boolean shouldExecute() {
				return !EntityBoulderSprite.this.isHiddenOrInWall() && super.shouldExecute();
			}

			@Override
			public boolean shouldContinueExecuting() {
				return !EntityBoulderSprite.this.isHiddenOrInWall() && super.shouldContinueExecuting();
			}
		});
		this.tasks.addTask(6, new EntityAIWander(this, 0.9D));
		this.tasks.addTask(7, new AIFindRandomHideout(this, 8, 10));
	}

	protected void setStalactitesSeed(long seed) {
		this.stalactitesSeed = seed;

		Random seededRng = new Random();
		seededRng.setSeed(this.stalactitesSeed);

		if(seededRng.nextInt(2) == 0) {
			this.numStalactites = 2 + seededRng.nextInt(5);
		} else {
			this.numStalactites = 0;
		}

		this.stalactiteHeights = new int[this.numStalactites];
		for(int i = 0; i < this.numStalactites; i++) {
			this.stalactiteHeights[i] = 1 + seededRng.nextInt(3);
		}
	}

	@SideOnly(Side.CLIENT)
	public void initStalactiteModels() {
		if(this.stalactites == null) {
			this.stalactites = new ArrayList<>();

			Random seededRng = new Random();
			seededRng.setSeed(this.stalactitesSeed);

			if(this.numStalactites > 0) {
				TextureMap altas = Minecraft.getMinecraft().getTextureMapBlocks();

				for(int i = 0; i < numStalactites; i++) {
					Vec3d offset = new Vec3d(-0.08D + seededRng.nextDouble() * 0.5D - 0.25D, 0.5D, -0.15D + seededRng.nextDouble() * 0.5D - 0.25D);
					float scale = (0.2F + seededRng.nextFloat() * 0.4F) * 0.6F;
					SpikeRenderer renderer = new SpikeRenderer(this.stalactiteHeights[i], scale, scale, scale, seededRng.nextLong(), offset.x, offset.y, offset.z)
							.build(DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL, altas.getAtlasSprite(ModelBoulderSprite.StalactitesModelRenderer.SPRITE_BOTTOM.toString()), altas.getAtlasSprite(ModelBoulderSprite.StalactitesModelRenderer.SPRITE_MID.toString()));
					this.stalactites.add(renderer);
				}
			}
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeLong(this.stalactitesSeed);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		this.setStalactitesSeed(buffer.readLong());
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		if(this.hideout != null) {
			nbt.setLong("hideout", this.hideout.toLong());
		}

		if(this.hideoutEntrance != null) {
			nbt.setString("hideoutEntrance", this.hideoutEntrance.getName());
		}

		nbt.setDouble("rollingSpeed", this.rollingSpeed);
		nbt.setInteger("rollingTicks", this.rollingTicks);
		nbt.setInteger("rollingAccelerationTime", this.rollingAccelerationTime);
		nbt.setInteger("rollingDecelerationTime", this.rollingDecelerationTime);
		nbt.setInteger("rollingDuration", this.rollingDuration);

		if(this.rollingDir != null) {
			nbt.setDouble("rollingDirX", this.rollingDir.x);
			nbt.setDouble("rollingDirY", this.rollingDir.y);
			nbt.setDouble("rollingDirZ", this.rollingDir.z);
		}

		nbt.setLong("stalactitesSeed", this.stalactitesSeed);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		if(nbt.hasKey("hideout", Constants.NBT.TAG_LONG)) {
			this.hideout = BlockPos.fromLong(nbt.getLong("hideout"));
		}

		if(nbt.hasKey("hideoutEntrance", Constants.NBT.TAG_STRING)) {
			this.hideoutEntrance = EnumFacing.byName(nbt.getString("hideoutEntrance"));
		}

		this.rollingSpeed = nbt.getDouble("rollingSpeed");
		this.rollingTicks = nbt.getInteger("rollingTicks");
		this.rollingAccelerationTime = nbt.getInteger("rollingAccelerationTime");
		this.rollingDecelerationTime = nbt.getInteger("rollingDecelerationTime");
		this.rollingDuration = nbt.getInteger("rollingDuration");

		if(nbt.hasKey("rollingDirX", Constants.NBT.TAG_DOUBLE) && nbt.hasKey("rollingDirY", Constants.NBT.TAG_DOUBLE) && nbt.hasKey("rollingDirZ", Constants.NBT.TAG_DOUBLE)) {
			this.rollingDir = new Vec3d(nbt.getDouble("rollingDirX"), nbt.getDouble("rollingDirY"), nbt.getDouble("rollingDirZ"));
		}

		this.setStalactitesSeed(nbt.getLong("stalactitesSeed"));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if(!this.isHiddenOrInWall()) {
			return SoundRegistry.BOULDER_SPRITE_LIVING;
		}
		return null;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegistry.BOULDER_SPRITE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.BOULDER_SPRITE_DEATH;
	}

	protected void playRollSound() {
		this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.BOULDER_SPRITE_ROLL, SoundCategory.HOSTILE, this.getSoundVolume(), this.getSoundPitch());
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		float distanceWalked = this.distanceWalkedOnStepModified;

		if(!this.isRolling()) {
			if(this.isHiddenOrInWall()) {
				distanceWalked = this.distanceWalkedOnStepModified + 0.9F;
			} else {
				distanceWalked = this.distanceWalkedOnStepModified + 0.5F;
			}
			this.playSound(SoundEvents.BLOCK_STONE_HIT, 0.6F, 1.0F);
		} else {
			distanceWalked = this.distanceWalkedOnStepModified + 0.7F;
			this.playSound(SoundEvents.BLOCK_STONE_HIT, 0.35F, 1.0F);
			this.playSound(SoundEvents.BLOCK_GRAVEL_BREAK, 0.08F, 1.0F);
		}

		this.distanceWalkedOnStepModified = distanceWalked;

		this.world.setEntityState(this, EVENT_STEP);
	}

	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_STEP && this.isHiddenOrInWall()) {
			IBlockState state = this.world.getBlockState(this.getPosition());
			if(!state.getBlock().isAir(state, this.world, this.getPosition())) {
				int stateId = Block.getStateId(state);
				for(int i = 0; i < 24; i++) {
					double dx = this.rand.nextDouble() - 0.5D;
					double dy = this.rand.nextDouble();
					double dz = this.rand.nextDouble() - 0.5D;
					this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX + this.motionX + dx, this.posY + this.motionY + dy, this.posZ + this.motionZ + dz, dx * 0.2D, dy * 0.2D, dz * 0.2D, stateId);
				}
			}
		}
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.BOULDER_SPRITE;
	}

	@Override
	public Map<String, Float> getLootModifiers(LootContext context, boolean isEntityProperty) {
		int stalactiteBlocks = this.stalactiteHeights != null ? IntStream.of(this.stalactiteHeights).sum() : 0;
		return ImmutableMap.<String, Float>builder()
				.put("stalactites", (float) this.numStalactites)
				.put("stalactite_blocks", (float) stalactiteBlocks)
				.build();
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) { }

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender() {
		if(this.isEntityInsideOpaqueBlock()) {
			AxisAlignedBB renderAABB = this.getEntityBoundingBox().grow(0.1D, 0.1D, 0.1D);
			Iterable<MutableBlockPos> it = MutableBlockPos.getAllInBoxMutable(new BlockPos(renderAABB.minX, this.posY + this.getEyeHeight(), renderAABB.minZ), new BlockPos(renderAABB.maxX, renderAABB.maxY, renderAABB.maxZ));
			int brightestBlock = 0;
			int brightestSky = 0;
			for(MutableBlockPos pos : it) {
				int brightness = this.getBrightnessForRenderAt(pos);
				int brightnessBlock = ((brightness >> 4) & 0b11111111);
				int brightnessSky = ((brightness >> 20) & 0b11111111);
				if(brightnessBlock > brightestBlock) {
					brightestBlock = brightnessBlock;
				}
				if(brightnessSky > brightestSky) {
					brightestSky = brightnessSky;
				}
			}
			return (brightestSky << 20) | (brightestBlock << 4);
		} else {
			return super.getBrightnessForRender();
		}
	}

	private int getBrightnessForRenderAt(BlockPos.MutableBlockPos pos) {
		if(this.world.isBlockLoaded(pos)) {
			return this.world.getCombinedLight(pos, 0);
		} else {
			return 0;
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.isRolling() || this.isHiddenOrInWall() ? null : this.getEntityBoundingBox();
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return this.isRolling() || this.isHiddenOrInWall() ? null : this.getEntityBoundingBox();
	}

	@Override
	public void getCustomCollisionBoxes(AxisAlignedBB aabb, List<AxisAlignedBB> collisionBoxes) {
		collisionBoxes.clear();
		final int floor = MathHelper.floor(aabb.minY) + 1;
		CustomEntityCollisionsHandler.HELPER.getCollisionBoxes(this, aabb, EntityCollisionPredicate.ALL, new BlockCollisionPredicate() {
			@Override
			public boolean isColliding(Entity entity, AxisAlignedBB aabb, MutableBlockPos pos, IBlockState state, @Nullable AxisAlignedBB blockAabb) {
				return !EntityBoulderSprite.this.isHiddenOrInWall() || pos.getY() < floor || !EntityBoulderSprite.this.isValidHideoutBlock(pos);
			}
		}, collisionBoxes);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		EntityLivingBase attacker = source.getImmediateSource() instanceof EntityLivingBase ? (EntityLivingBase)source.getImmediateSource() : null;
		if(attacker != null && attacker.getActiveHand() != null) {
			ItemStack item = attacker.getHeldItem(attacker.getActiveHand());
			if(!item.isEmpty() && item.getItem() instanceof ItemPickaxe) {
				amount *= 3.0F;
			}
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if(this.isHiddenOrInWall()) {
			return false;
		}
		return super.attackEntityAsMob(entityIn);
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return source == DamageSource.IN_WALL || super.isEntityInvulnerable(source);
	}

	@Override
	public void onUpdate() {
		double prevMotionX = this.motionX;
		double prevMotionZ = this.motionZ;

		if(this.isEntityInsideOpaqueBlock()) {
			this.setSize(this.width, 0.95F);
		} else {
			this.setSize(this.width, 1.2F);
		}

		if(this.isRolling()) {
			this.stepHeight = 1.1F;
		} else {
			this.stepHeight = 0.6F;
		}

		super.onUpdate();

		if(!this.world.isRemote) {
			if(this.getHideout() != null && !this.isValidHideoutBlock(this.getHideout())) {
				this.setHideout(null);
			}

			if(this.getAIMoveSpeed() > 0.3F && this.moveForward != 0) {
				this.dataManager.set(ROLL_SPEED, 0.05F + (this.getAIMoveSpeed() - 0.3F) / 3.0F);
				if(!this.rollSoundPlayed) {
					this.playRollSound();
				}
				this.rollSoundPlayed = true;
			} else {
				this.dataManager.set(ROLL_SPEED, 0.0F);
				this.rollSoundPlayed = false;
			}

			IAttributeInstance attackAttribute = this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

			if(this.isRolling()) {
				if(this.collidedHorizontally) {
					boolean pg = this.onGround;
					double pmx = this.motionX;
					double pmy = this.motionY;
					double pmz = this.motionZ;
					double px = this.posX;
					double py = this.posY;
					double pz = this.posZ;

					this.move(MoverType.SELF, MathHelper.clamp(prevMotionX, -4, 4), 0, 0);

					boolean cx = Math.abs(this.posX - px) < Math.abs(MathHelper.clamp(prevMotionX, -4, 4)) / 2.0D;

					this.onGround = pg;
					this.motionX = pmx;
					this.motionY = pmy;
					this.motionZ = pmz;
					this.setPosition(px, py, pz);

					this.move(MoverType.SELF, 0, 0, MathHelper.clamp(prevMotionZ, -4, 4));

					boolean cz = Math.abs(this.posZ - pz) < Math.abs(MathHelper.clamp(prevMotionZ, -4, 4)) / 2.0D;

					this.onGround = pg;
					this.motionX = pmx;
					this.motionY = pmy;
					this.motionZ = pmz;
					this.setPosition(px, py, pz);

					this.onRollIntoWall(cx, cz, MathHelper.clamp(prevMotionX, -4, 4), MathHelper.clamp(prevMotionZ, -4, 4));
				}

				attackAttribute.removeModifier(ROLLING_ATTACK_MODIFIER_ATTRIBUTE_UUID);
				attackAttribute.applyModifier(new AttributeModifier(ROLLING_ATTACK_MODIFIER_ATTRIBUTE_UUID, "Rolling attack modifier", Math.min(Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 20.0F, 10.0F), 0));

				Vec3d motionDir = new Vec3d(this.motionX, 0, this.motionZ).normalize();
				List<Entity> collidingEntities = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(0.35D, 0, 0.35D), EntitySelectors.getTeamCollisionPredicate(this));
				for(Entity collidingEntity : collidingEntities) {
					if(collidingEntity.hurtResistantTime <= 0) {
						double dot = motionDir.dotProduct(collidingEntity.getPositionVector().subtract(this.getPositionVector()));
						if(dot >= -0.5D && dot <= Math.sqrt(this.width * this.width) * 0.5D) {
							if(this.attackEntityAsMob(collidingEntity) && collidingEntity instanceof EntityLivingBase) {
								((EntityLivingBase) collidingEntity).knockBack(this, (float) Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 3.0F, -this.motionX, -this.motionZ);

								if(collidingEntity instanceof EntityPlayerMP) {
									AdvancementCriterionRegistry.ROLLED_OVER_BY_BOULDER_SPRITE.trigger((EntityPlayerMP) collidingEntity);
								}
							}
						}
					}
				}
			} else {
				attackAttribute.removeModifier(ROLLING_ATTACK_MODIFIER_ATTRIBUTE_UUID);
			}

			if(this.rollingTicks > 0 && this.rollingDir != null) {
				double speed;
				if(this.rollingDuration - this.rollingTicks < this.rollingAccelerationTime) {
					speed = 0.5D + (this.rollingSpeed - 0.5D) / this.rollingAccelerationTime * (this.rollingDuration - this.rollingTicks);
				} else if(this.rollingTicks < this.rollingDecelerationTime) {
					speed = 0.5D + (this.rollingSpeed - 0.5D) / this.rollingDecelerationTime * this.rollingTicks;
				} else {
					speed = this.rollingSpeed;
				}

				this.getMoveHelper().setMoveTo(this.posX + this.rollingDir.x, this.posY, this.posZ + this.rollingDir.z, speed);

				this.rollingTicks--;
			}
		} else {
			if(this.isEntityAlive() && this.isRolling()) {
				this.setRollSpeed(this.dataManager.get(ROLL_SPEED));
			}

			this.updateRollAnimationState();

			if(this.onGround) {
				double particleTiming = 0.75D;
				if(this.prevRollAnimation % 1 < particleTiming && this.rollAnimation % 1 >= particleTiming) {
					BlockPos posBelow = this.getPosition().down();
					IBlockState stateBelow = this.world.getBlockState(posBelow);
					if(!stateBelow.getBlock().isAir(stateBelow, this.world, posBelow)) {
						int stateId = Block.getStateId(stateBelow);
						int betweenstoneId = Block.getStateId(BlockRegistry.BETWEENSTONE.getDefaultState());
						for(int i = 0; i < 28; i++) {
							double dx = this.rand.nextDouble() - 0.5D;
							double dz = this.rand.nextDouble() - 0.5D;
							this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX + this.motionX + dx, this.posY - 0.2D, this.posZ + this.motionZ + dz, dx * 0.3D, 0.3D, dz * 0.3D, stateId);
						}
						for(int i = 0; i < 12; i++) {
							double dx = this.rand.nextDouble() - 0.5D;
							double dz = this.rand.nextDouble() - 0.5D;
							this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX + this.motionX + dx, this.posY - 0.2D, this.posZ + this.motionZ + dz, dx * 0.3D, 0.3D, dz * 0.3D, betweenstoneId);
						}
					}
				}
			}
		}
	}

	protected void onRollIntoWall(boolean cx, boolean cz, double mx, double mz) {
		if(this.onGround) {
			if(this.motionY <= 3.0D) {
				this.motionY += Math.min(Math.sqrt(mx * mx + mz * mz) * 3, 0.7F);
				this.velocityChanged = true;
			}

			if(cx && Math.abs(this.motionX) <= 3.0D) {
				this.motionX -= mx * 4;
				this.velocityChanged = true;
			}

			if(cz && Math.abs(this.motionZ) <= 3.0D) {
				this.motionZ -= mz * 4;
				this.velocityChanged = true;
			}

			if(cx) {
				this.bumpWall(EnumFacing.getFacingFromVector((float) mx, 0, 0));
			}

			if(cz) {
				this.bumpWall(EnumFacing.getFacingFromVector(0, 0, (float) mz));
			}
		}

		this.stopRolling();
	}

	protected void bumpWall(EnumFacing dir) {
		if(ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
			for(int so = -1; so <= 1; so++) {
				for(int yo = 0; yo <= 1; yo++) {
					BlockPos pos = this.getPosition().offset(dir).add(dir.getXOffset() == 0 ? so : 0, yo, dir.getZOffset() == 0 ? so : 0);
					if(this.world.isAirBlock(pos.offset(dir.getOpposite()))) {
						IBlockState hitState = this.world.getBlockState(pos);
						float hardness = hitState.getBlockHardness(this.world, pos);
						if(!hitState.getBlock().isAir(hitState, this.world, pos) && hardness >= 0 && hardness <= 2.5F && this.rand.nextInt(yo + so + 2) == 0
								&& hitState.getBlock().canEntityDestroy(hitState, this.world, pos, this)
								&& ForgeEventFactory.onEntityDestroyBlock(this, pos, hitState)) {
							this.world.playEvent(2001, pos, Block.getStateId(hitState));
							this.world.setBlockToAir(pos);
						}
					}
				}
			}
		}
	}

	public void startRolling(int duration, int accelerationTime, int decelerationTime, Vec3d dir, double rollingSpeed) {
		this.rollingTicks = duration;
		this.rollingAccelerationTime = accelerationTime;
		this.rollingDecelerationTime = decelerationTime;
		this.rollingDuration = duration;
		this.rollingDir = dir;
		this.rollingSpeed = rollingSpeed + 1.5D;
	}

	public int getRollingTicks() {
		return this.rollingTicks;
	}

	public void stopRolling() {
		this.rollingTicks = Math.min(this.rollingDecelerationTime, this.rollingTicks);
	}

	public boolean isRolling() {
		return this.dataManager.get(ROLL_SPEED) > 0.04F;
	}

	public void setRollSpeed(float speed) {
		this.rollAnimationSpeed = speed;
	}

	protected void updateRollAnimationState() {
		this.prevRollAnimationInAirWeight = this.rollAnimationInAirWeight;
		this.prevRollAnimation = this.rollAnimation;
		this.prevRollAnimationWeight = this.rollAnimationWeight;

		if(this.rollAnimationSpeed > 0) {
			if(!this.onGround) {
				this.rollAnimationInAirWeight = Math.min(1, this.rollAnimationInAirWeight + 0.2F);
			} else {
				this.rollAnimationInAirWeight = Math.max(0, this.rollAnimationInAirWeight - 0.2F);
			}

			if(this.rollAnimationSpeed < 0.04F) {
				double p = this.rollAnimation % 1;
				double incr = Math.pow((1 - (this.rollAnimation % 1)) * this.rollAnimationSpeed, 0.65D);
				this.rollAnimation += incr;
				this.rollAnimationWeight = (float) Math.max(0, this.rollAnimationWeight - incr / (1 - (this.rollAnimation % 1)) / 4);
				if(this.rollAnimation % 1 < p) {
					this.prevRollAnimation = this.rollAnimation = 0;
					this.prevRollAnimationWeight = this.rollAnimationWeight = 0;
					this.rollAnimationSpeed = 0;
					this.rollAnimationInAirWeight = 0;
				}
			} else {
				this.rollAnimation += this.rollAnimationSpeed;
				this.rollAnimationWeight = Math.min(1, this.rollAnimationWeight + 0.1F);
				this.rollAnimationSpeed *= 0.5F;
			}
		}
	}

	public float getRollAnimation(float partialTicks) {
		return this.prevRollAnimation + (this.rollAnimation - this.prevRollAnimation) * partialTicks;
	}

	public float getRollAnimationWeight(float partialTicks) {
		return this.prevRollAnimationWeight + (this.rollAnimationWeight - this.prevRollAnimationWeight) * partialTicks;
	}

	public float getRollAnimationInAirWeight(float partialTicks) {
		return this.prevRollAnimationInAirWeight + (this.rollAnimationInAirWeight - this.prevRollAnimationInAirWeight) * partialTicks;
	}

	public boolean isHiddenOrInWall() {
		return this.isAiHiding || this.isEntityInsideOpaqueBlock();
	}

	public void setHideout(@Nullable BlockPos pos) {
		this.hideout = pos;
	}

	@Nullable
	public BlockPos getHideout() {
		return this.hideout;
	}

	public void setHideoutEntrance(@Nullable EnumFacing entrance) {
		this.hideoutEntrance = entrance;
	}

	@Nullable
	protected EnumFacing getHideoutEntrance() {
		return this.hideoutEntrance;
	}

	protected boolean isValidHideoutBlock(BlockPos pos) {
		return SurfaceType.UNDERGROUND.matches(this.world.getBlockState(pos));
	}

	protected static class AIRollTowardsTarget extends EntityAIBase {
		protected final EntityBoulderSprite entity;

		protected int cooldown = 18;
		protected Vec3d rollDir;

		public AIRollTowardsTarget(EntityBoulderSprite entity) {
			this.entity = entity;
			this.setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			if(this.cooldown-- <= 0) {
				return this.entity.isEntityAlive() && this.entity.getAttackTarget() != null && this.entity.getRollingTicks() <= 0 && this.entity.onGround && this.entity.getAttackTarget().isEntityAlive() && this.entity.getEntitySenses().canSee(this.entity.getAttackTarget());
			}
			return false;
		}

		@Override
		public void startExecuting() {
			Entity target = this.entity.getAttackTarget();
			if(target != null) {
				this.rollDir = new Vec3d(target.posX - this.entity.posX, 0, target.posZ - this.entity.posZ).normalize();
				this.entity.startRolling(160, 35, 15, this.rollDir, 1.8D);
			}
		}

		@Override
		public void resetTask() {
			this.cooldown = 20 + this.entity.getRNG().nextInt(26);
		}

		@Override
		public boolean shouldContinueExecuting() {
			//Keep task active while rolling to block other movement tasks
			if(this.entity.getRollingTicks() > 0) {
				Entity target = this.entity.getAttackTarget();
				if(target != null) {
					double overshoot = this.rollDir.dotProduct(new Vec3d(this.entity.posX - target.posX, 0, this.entity.posZ - target.posZ));
					if(overshoot >= 2) {
						this.entity.stopRolling();
					}
				}
				return true;
			}
			return false;
		}
	}

	protected static class AIMoveToHideout extends EntityAIBase {
		protected final EntityBoulderSprite entity;
		protected double speed;

		protected List<EnumFacing> potentialEntrances = new ArrayList<>();

		protected BlockPos targetHideout;
		protected EnumFacing targetEntrance;
		protected BlockPos target;
		protected Path path;

		protected int delayCounter;
		protected int pathingFails;

		protected double approachSpeedFar;
		protected double approachSpeedNear;

		protected double lastFinalPositionDistSq;
		protected int stuckCounter;

		protected boolean finished;

		public AIMoveToHideout(EntityBoulderSprite entity, double speed) {
			this.entity = entity;
			this.speed = this.approachSpeedFar = this.approachSpeedNear = speed;
			this.setMutexBits(3 | 0b10000);
		}

		@Override
		public boolean shouldExecute() {
			if(this.entity.isEntityAlive() && this.entity.getHideout() != null && !this.entity.isHiddenOrInWall()) {
				EnumFacing entrance;
				if(this.entity.getHideoutEntrance() == null) {
					if(this.potentialEntrances.isEmpty()) {
						for(EnumFacing dir : EnumFacing.HORIZONTALS) {
							BlockPos offset = this.entity.getHideout().offset(dir);
							PathNodeType node = this.entity.getNavigator().getNodeProcessor().getPathNodeType(this.entity.world, offset.getX(), offset.getY(), offset.getZ());
							if(node == PathNodeType.OPEN || node == PathNodeType.WALKABLE) {
								this.potentialEntrances.add(dir);
							}
						}
						if(this.potentialEntrances.isEmpty()) {
							return false;
						}
						Collections.sort(this.potentialEntrances, (f1, f2) -> 
						Double.compare(
								this.entity.getHideout().offset(f2).distanceSq(this.entity.posX, this.entity.posY, this.entity.posZ),
								this.entity.getHideout().offset(f1).distanceSq(this.entity.posX, this.entity.posY, this.entity.posZ)
								));
					}
					entrance = this.potentialEntrances.remove(this.potentialEntrances.size() - 1);
				} else {
					entrance = this.entity.getHideoutEntrance();
				}
				BlockPos entrancePos = this.entity.getHideout().offset(entrance);
				this.path = this.entity.getNavigator().getPathToPos(entrancePos);
				if(this.path != null && this.path.getFinalPathPoint().x == entrancePos.getX() && this.path.getFinalPathPoint().y == entrancePos.getY() && this.path.getFinalPathPoint().z == entrancePos.getZ()) {
					this.entity.setHideoutEntrance(entrance);
					this.target = entrancePos;
					this.targetEntrance = entrance;
					this.targetHideout = this.entity.getHideout();
					return true;
				}
			}
			return false;
		}

		@Override
		public void startExecuting() {
			this.entity.getNavigator().setPath(this.path, this.speed);
			this.approachSpeedFar = this.approachSpeedNear = this.entity.getAIMoveSpeed();
		}

		@Override
		public void updateTask() {
			if(this.delayCounter-- <= 0) {
				double dist = this.entity.getDistanceSq(this.target.getX() + 0.5D, this.target.getY() + 0.5D, this.target.getZ() + 0.5D);

				this.delayCounter = 4 + this.entity.getRNG().nextInt(7);

				if(dist > 1024.0D) {
					this.delayCounter += 10;
				} else if(dist > 256.0D) {
					this.delayCounter += 5;
				}

				if(!this.entity.getNavigator().tryMoveToXYZ(this.target.getX(), this.target.getY(), this.target.getZ(), this.speed)) {
					this.delayCounter += 15;
					this.pathingFails++;
				}
			}

			double dstSq = this.entity.getDistanceSq(this.target.getX() + 0.5D, this.target.getY(), this.target.getZ() + 0.5D);

			if(this.entity.getNavigator().noPath()) {
				if(this.path.isFinished()) {
					this.entity.getMoveHelper().setMoveTo(this.target.getX() + 0.5D, this.target.getY(), this.target.getZ() + 0.5D, this.approachSpeedNear / this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
					this.entity.getLookHelper().setLookPosition(this.target.getX() - this.targetEntrance.getXOffset() + 0.5D, this.target.getY() + this.entity.getEyeHeight(), this.target.getZ() - this.targetEntrance.getZOffset() + 0.5D, 30, 30);

					this.approachSpeedNear = this.approachSpeedNear * 0.9D + Math.min((dstSq + 0.2D) / 4.0D, 0.4D / 4.0D) * 0.1D;

					if(this.lastFinalPositionDistSq == 0) {
						this.lastFinalPositionDistSq = dstSq;
					} else {
						if(dstSq > this.lastFinalPositionDistSq - 0.05D) {
							this.stuckCounter += this.entity.getRNG().nextInt(3) + 1;
						} else {
							this.lastFinalPositionDistSq = dstSq;
						}
						if(this.stuckCounter >= 80) {
							this.finished = true;
						}
					}

					if(this.entity.getDistanceSq(this.target.getX() + 0.5D, this.target.getY(), this.target.getZ() + 0.5D) < 0.015D && this.entity.getAIMoveSpeed() <= 0.1D) {
						this.finished = true;
					}
				} else {
					this.finished = true;
				}
			} else {
				if(dstSq <= this.entity.getAIMoveSpeed() * 5 * 5) {
					double decay = (this.entity.getAIMoveSpeed() * 5 * 5 - dstSq) / (this.entity.getAIMoveSpeed() * 5 * 5) * 0.33D;

					this.approachSpeedNear = this.approachSpeedFar = this.approachSpeedFar * (1 - decay) + Math.min(0.6D / 4.0D, this.speed / 4.0D) * decay;
					this.entity.getNavigator().setSpeed(this.approachSpeedFar / this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				} else {
					this.approachSpeedFar = this.approachSpeedNear = this.entity.getAIMoveSpeed();
				}

				if(this.entity.getNavigator().getPath() != this.path) {
					this.finished = true;
				}
			}
		}

		@Override
		public void resetTask() {
			this.potentialEntrances.clear();
			this.path = null;
			this.entity.getNavigator().clearPath();
			this.pathingFails = 0;
			this.finished = false;
			this.lastFinalPositionDistSq = 0;
			this.target = null;
			this.targetEntrance = null;
			this.stuckCounter = 0;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.entity.isEntityAlive() && this.entity.getHideout() != null && this.targetHideout != null && this.targetHideout.equals(this.entity.getHideout()) && this.pathingFails < 3 && !this.finished;
		}
	}

	protected static class AIHide extends EntityAIBase {
		protected final EntityBoulderSprite entity;
		protected double speed;

		protected BlockPos hideout;
		protected EnumFacing entrance;

		public AIHide(EntityBoulderSprite entity, double speed) {
			this.entity = entity;
			this.speed = speed;
			this.setMutexBits(3 | 0b10000);
		}

		@Override
		public boolean shouldExecute() {
			if(this.entity.isEntityAlive() && this.entity.getHideout() != null && this.entity.getHideoutEntrance() != null && this.entity.isValidHideoutBlock(this.entity.getHideout())) {
				BlockPos entrance = this.entity.getHideout().offset(this.entity.getHideoutEntrance());
				if(entrance.distanceSqToCenter(this.entity.posX, this.entity.posY, this.entity.posZ) <= 0.33D) {
					this.hideout = this.entity.getHideout();
					this.entrance = this.entity.getHideoutEntrance();
					return true;
				}
			}
			return false;
		}

		@Override
		public void updateTask() {
			this.entity.isAiHiding = true;
			BlockPos hideoutPos = this.hideout.offset(this.entrance.getOpposite());
			double dstSq = hideoutPos.distanceSqToCenter(this.entity.posX, this.entity.posY, this.entity.posZ);
			if(dstSq >= 0.5D) {
				this.entity.getMoveHelper().setMoveTo(hideoutPos.getX() + 0.5D, hideoutPos.getY(), hideoutPos.getZ() + 0.5D, this.speed);
			}
		}

		@Override
		public void resetTask() {
			this.entity.isAiHiding = false;
			this.entity.setHideout(null);
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.entity.isEntityAlive() && this.entity.getHideout() == this.hideout && this.hideout.getY() >= MathHelper.floor(this.entity.posY) && this.entity.isValidHideoutBlock(this.hideout);
		}
	}

	protected static class AIRollTowardsTargetFromHideout extends EntityAIBase {
		protected final EntityBoulderSprite entity;
		protected double rollSpeed;
		protected int chance;

		protected Vec3d rollDir;
		protected boolean finished = false;

		public AIRollTowardsTargetFromHideout(EntityBoulderSprite entity, int chance, double rollSpeed) {
			this.entity = entity;
			this.rollSpeed = rollSpeed;
			this.chance = chance;
			this.setMutexBits(3 | 0b10000);
		}

		@Override
		public boolean shouldExecute() {
			return this.entity.isHiddenOrInWall() && this.entity.isEntityAlive() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isEntityAlive() && Math.abs(this.entity.posY - this.entity.getAttackTarget().posY) <= 3 && this.entity.getRNG().nextInt(this.chance) == 0;
		}

		@Override
		public void startExecuting() {
			this.finished = false;
		}

		@Override
		public void updateTask() {
			Entity target = this.entity.getAttackTarget();

			if(target != null) {
				if(this.entity.getRollingTicks() <= 0) {
					if(!this.shouldExecute()) {
						this.finished = true;
					} else {
						this.rollDir = new Vec3d(target.posX - this.entity.posX, 0, target.posZ - this.entity.posZ).normalize();
						this.entity.startRolling(80, 10, 10, this.rollDir, this.rollSpeed);
						this.entity.isAiHiding = false;
						this.entity.setHideout(null);
					}
				} else if(this.rollDir != null && target != null) {
					double overshoot = this.rollDir.dotProduct(new Vec3d(this.entity.posX - target.posX, 0, this.entity.posZ - target.posZ));
					if(overshoot >= 2) {
						this.entity.stopRolling();
					}
				} else {
					this.entity.stopRolling();
				}
			} else {
				this.entity.stopRolling();
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return !this.finished;
		}
	}

	protected static class AIFindRandomHideout extends EntityAIBase {
		protected final EntityBoulderSprite entity;

		protected int chance;
		protected int range;

		public AIFindRandomHideout(EntityBoulderSprite entity, int range, int chance) {
			this.entity = entity;
			this.range = range;
			this.chance = chance;
			this.setMutexBits(3 | 0b10000);
		}

		@Override
		public boolean shouldExecute() {
			return this.entity.isEntityAlive() && this.entity.onGround && this.entity.getRNG().nextInt(this.chance) == 0;
		}

		@Override
		public void startExecuting() {
			MutableBlockPos pos = new MutableBlockPos();
			for(int i = 0; i < 32; i++) {
				pos.setPos(this.entity.posX + this.entity.getRNG().nextInt(this.range * 2) - this.range, this.entity.posY, this.entity.posZ + this.entity.getRNG().nextInt(this.range * 2) - this.range);
				if(this.entity.isValidHideoutBlock(pos) && (!this.entity.world.isAirBlock(pos.up()) || this.entity.getRNG().nextInt(10) == 0)) {
					boolean hasPotentialEntrance = false;
					for(EnumFacing facing : EnumFacing.HORIZONTALS) {
						if(this.entity.world.isAirBlock(pos.offset(facing))) {
							hasPotentialEntrance = true;
							break;
						}
					}
					if(hasPotentialEntrance) {
						this.entity.setHideout(pos.toImmutable());
						this.entity.setHideoutEntrance(null);
						break;
					}
				}
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}
	}

	protected static class AIFindRandomHideoutFlee extends AIFindRandomHideout {
		public AIFindRandomHideoutFlee(EntityBoulderSprite entity, int range) {
			super(entity, range, 2);
			this.setMutexBits(0b10000);
		}

		@Override
		public boolean shouldExecute() {
			return this.entity.isEntityAlive() && this.entity.getHealth() <= this.entity.getMaxHealth() / 3 && this.entity.getRNG().nextInt(this.chance) == 0;
		}
	}
}
