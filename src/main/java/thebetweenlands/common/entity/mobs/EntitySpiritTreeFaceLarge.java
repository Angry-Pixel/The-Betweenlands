package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.BossType;
import thebetweenlands.api.entity.IBLBoss;
import thebetweenlands.api.entity.IEntityWithLootModifier;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleRootSpike;
import thebetweenlands.common.entity.EntityRootGrabber;
import thebetweenlands.common.entity.EntitySpikeWave;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.tree.WorldGenSpiritTreeStructure;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.SpiritTreeKillToken;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;
import thebetweenlands.util.BlockShapeUtils;

public class EntitySpiritTreeFaceLarge extends EntitySpiritTreeFace implements IEntityWithLootModifier, IBLBoss, IMob {
	private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS)).setDarkenSky(false);

	public static final byte EVENT_BLOW_ATTACK = 40;

	protected static final UUID STRENGTH_MULTIPLIER_ATTRIBUTE_UUID = UUID.fromString("8a8dccae-273d-445d-b581-81d4a8a979a5");
	protected static final UUID HEALTH_MULTIPLIER_ATTRIBUTE_UUID = UUID.fromString("e29b66a3-2ed2-4598-a44c-ed82a8f03eb2");

	private static final DataParameter<Integer> BLOW_STATE = EntityDataManager.createKey(EntitySpiritTreeFaceLarge.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> SPIT_STATE = EntityDataManager.createKey(EntitySpiritTreeFaceLarge.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> ROTATING_WAVE_STATE = EntityDataManager.createKey(EntitySpiritTreeFaceLarge.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> CRAWLING_WAVE_STATE = EntityDataManager.createKey(EntitySpiritTreeFaceLarge.class, DataSerializers.VARINT);
	private static final DataParameter<Float> WISP_STRENGTH_MODIFIER = EntityDataManager.createKey(EntitySpiritTreeFaceLarge.class, DataSerializers.FLOAT);
	private static final DataParameter<BlockPos> LAST_LOCKED_WISP = EntityDataManager.createKey(EntitySpiritTreeFaceLarge.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<Optional<UUID>> BOSSINFO_ID = EntityDataManager.createKey(EntitySpiritTreeFaceLarge.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	private int blowTicks = 0;

	private float rotatingWaveStart = 0;
	private int rotatingWaveTicks = 0;

	protected static final int CRAWLING_WAVE_RANGE = 36;

	private float crawlingWaveAngle = 0;
	private int crawlingWaveTicks = 0;

	protected static final int DEFAULT_XP_DROPPED = 300;

	protected static final int DEFAULT_SPIT_DELAY = 10;
	protected static final int DEFAULT_BLOW_DELAY = 30;
	protected static final int DEFAULT_ROTATING_WAVE_DELAY = 40;
	protected static final int DEFAULT_CRAWLING_WAVE_DELAY = 40;

	protected int spitDelay = DEFAULT_SPIT_DELAY;
	protected int blowDelay = DEFAULT_BLOW_DELAY;
	protected int rotatingWaveDelay = DEFAULT_ROTATING_WAVE_DELAY;
	protected int crawlingWaveDelay = DEFAULT_CRAWLING_WAVE_DELAY;

	public EntitySpiritTreeFaceLarge(World world) {
		super(world);
		this.setSize(1.8F, 1.8F);
		this.experienceValue = DEFAULT_XP_DROPPED;
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.targetTasks.addTask(0, new EntityAIHurtByTargetImproved(this, true));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));

		this.tasks.addTask(0, new AITrackTargetSpiritTreeFace(this));
		this.tasks.addTask(1, new AIAttackMelee(this, 1, true));
		this.tasks.addTask(2, new AISpit(this, 4.5F) {
			@Override
			protected float getSpitDamage() {
				return (float) EntitySpiritTreeFaceLarge.this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() / 3.0F;
			}
		});
		this.tasks.addTask(3, new AIBlowAttack(this));
		this.tasks.addTask(4, new AIRotatingWaveAttack(this));
		this.tasks.addTask(5, new AICrawlingWaveAttack(this));
		this.tasks.addTask(6, new AIGrabAttack(this));
		this.tasks.addTask(7, new AIRespawnSmallFaces(this));
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		this.dataManager.register(BLOW_STATE, 0);
		this.dataManager.register(SPIT_STATE, 0);
		this.dataManager.register(ROTATING_WAVE_STATE, 0);
		this.dataManager.register(CRAWLING_WAVE_STATE, 0);
		this.dataManager.register(WISP_STRENGTH_MODIFIER, 1.0F);
		this.dataManager.register(LAST_LOCKED_WISP, BlockPos.ORIGIN);
		this.dataManager.register(BOSSINFO_ID, Optional.absent());
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15D);		
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0D);
		//this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(600.0D);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SPIRIT_TREE_FACE_LARGE;
	}

	@Nullable
	@Override
	public Map<String, Float> getLootModifiers(@Nullable LootContext context, boolean isEntityProperty) {
		ImmutableMap.Builder<String, Float> builder = ImmutableMap.builder();
		builder.put("strength", this.dataManager.get(WISP_STRENGTH_MODIFIER));
		return builder.build();
	}
	
	@Override
	public List<BlockPos> findNearbyBlocksForMovement() {
		List<LocationSpiritTree> locations = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, this.getEntityBoundingBox(), loc -> loc.isInside(this));
		if(!locations.isEmpty()) {
			List<BlockPos> positions = new ArrayList<>();
			positions.addAll(locations.get(0).getLargeFacePositions());
			if(!positions.isEmpty()) {
				return positions;
			}
		}
		return super.findNearbyBlocksForMovement();
	}

	protected List<BlockPos> findSmallFacesBlocks() {
		List<LocationSpiritTree> locations = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, this.getEntityBoundingBox(), loc -> loc.isInside(this));
		if(!locations.isEmpty()) {
			List<BlockPos> positions = locations.get(0).getSmallFacePositions();
			if(!positions.isEmpty()) {
				return positions;
			}
		}
		return this.findNearbyBlocksForMovement();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SPIRIT_TREE_FACE_LARGE_DEATH;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SPIRIT_TREE_FACE_LARGE_LIVING;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_ATTACKED) {
			Vec3d frontCenter = this.getFrontCenter();
			for(int i = 0; i < 16; i++) {
				float rx = this.world.rand.nextFloat() * 2.0F - 1.0F;
				float ry = this.world.rand.nextFloat() * 2.0F - 1.0F;
				float rz = this.world.rand.nextFloat() * 2.0F - 1.0F;
				Vec3d vec = new Vec3d(rx, ry, rz);
				vec = vec.normalize();
				this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, frontCenter.x + rx, frontCenter.y + ry, frontCenter.z + rz, vec.x * 1.5F, vec.y * 1.5F, vec.z * 1.5F, Block.getIdFromBlock(BlockRegistry.LOG_SPIRIT_TREE));
			}
		} else if(id == EVENT_BLOW_ATTACK) {
			Vec3d frontCenter = this.getFrontCenter();
			for(int i = 0; i < 64; i++) {
				Random rnd = world.rand;
				float rx = rnd.nextFloat() * 6.0F - 3.0F + this.getFacing().getXOffset() * 2;
				float ry = rnd.nextFloat() * 6.0F - 3.0F + this.getFacing().getYOffset() * 2;
				float rz = rnd.nextFloat() * 6.0F - 3.0F + this.getFacing().getZOffset() * 2;
				Vec3d vec = new Vec3d(rx, ry, rz);
				vec = vec.normalize();
				ParticleRootSpike particle = (ParticleRootSpike) BLParticles.ROOT_SPIKE.spawn(world, frontCenter.x, frontCenter.y - 0.25D, frontCenter.z, ParticleArgs.get().withMotion(vec.x * 0.45F, vec.y * 0.45F + 0.2F, vec.z * 0.45F));
				particle.setUseSound(this.rand.nextInt(15) == 0);
			}
			frontCenter = this.getFrontCenter();
			for(int i = 0; i < 32; i++) {
				Random rnd = world.rand;
				float rx = rnd.nextFloat() - 0.5F + this.getFacing().getXOffset() * 0.5F;
				float ry = rnd.nextFloat() - 0.5F + this.getFacing().getYOffset() * 0.5F;
				float rz = rnd.nextFloat() - 0.5F + this.getFacing().getZOffset() * 0.5F;
				Vec3d vec = new Vec3d(rx, ry, rz);
				vec = vec.normalize();
				BLParticles.MOTION_ITEM_BREAKING.spawn(world, frontCenter.x, frontCenter.y - 0.25D, frontCenter.z, ParticleArgs.get().withMotion(vec.x * 0.25F, vec.y * 0.25F, vec.z * 0.25F).withData(ItemRegistry.SAP_SPIT, 0));
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(this.getWispStrengthModifier() > 1.0F) {
			amount /= 1.0F + (this.getWispStrengthModifier() - 1.0F) * 2.0F;
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);

		if(!this.world.isRemote) {
			List<SpiritTreeKillToken> killTokens = BetweenlandsWorldStorage.forWorld(this.world).getSpiritTreeKillTokens();
			if(killTokens.size() > 32) {
				killTokens.remove(0);
			}
			killTokens.add(new SpiritTreeKillToken(this.getPosition(), this.getWispStrengthModifier()));

			List<LocationSpiritTree> locations = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, this.getEntityBoundingBox(), loc -> loc.isInside(this));
			if(!locations.isEmpty()) {
				LocationSpiritTree location = locations.get(0);

				List<EntitySpiritTreeFaceSmall> smallFaces = this.world.getEntitiesWithinAABB(EntitySpiritTreeFaceSmall.class, location.getEnclosingBounds());
				for(EntitySpiritTreeFaceSmall face : smallFaces) {
					face.setDropItemsWhenDead(false);
					face.setDead();
				}

				List<BlockPos> wispPositions = new ArrayList<>();
				wispPositions.addAll(location.getGeneratedWispPositions());
				wispPositions.addAll(location.getNotGeneratedWispPositions());
				for(BlockPos wisp : wispPositions) {
					if(this.world.getBlockState(wisp).getBlock() == BlockRegistry.WISP) {
						this.world.setBlockToAir(wisp);
					}
				}

				List<BlockPos> positions = location.getLargeFacePositions();
				BlockPos lowest = null;
				for(BlockPos pos : positions) {
					if(lowest == null || lowest.getY() > pos.getY()) {
						lowest = pos;
					}
				}
				if(lowest != null) {
					int radius = 5;
					for(int xo = -radius; xo <= radius; xo++) {
						for(int yo = -radius; yo <= radius; yo++) {
							for(int zo = -radius; zo <= radius; zo++) {
								if(xo*xo + yo*yo + zo*zo <= radius*radius) {
									BlockPos pos = lowest.add(xo, yo, zo);
									IBlockState state = this.world.getBlockState(pos);

									if(SurfaceType.GRASS_AND_DIRT.matches(state) && !this.world.getBlockState(pos.up()).isNormalCube() && this.world.rand.nextInt(3) == 0) {
										this.world.setBlockState(pos, BlockRegistry.SPREADING_SLUDGY_DIRT.getDefaultState());
									}

									if(state.getBlock() == BlockRegistry.LOG_SPIRIT_TREE && this.world.rand.nextInt(5) == 0) {
										this.world.setBlockState(pos, BlockRegistry.LOG_SPREADING_ROTTEN_BARK.getDefaultState());
									}
								}
							}
						}
					}
				}

				for(BlockPos pos : location.getSmallFacePositions()) {
					IBlockState state = this.world.getBlockState(pos);
					if(state.getBlock() == BlockRegistry.LOG_SPIRIT_TREE && this.world.rand.nextInt(10) == 0) {
						this.world.setBlockState(pos, BlockRegistry.LOG_SPREADING_ROTTEN_BARK.getDefaultState());
					}
				}

				location.getGuard().clear(this.world);
				location.setVisible(false);
				location.setDirty(true);
			}
		}
	}

	@Override
	public boolean isAttacking() {
		return super.isAttacking() || this.blowTicks > 0;
	}

	@Override
	protected void playSpitSound() {
		this.playSound(SoundRegistry.SPIRIT_TREE_FACE_LARGE_SPIT, 1, 0.8F + this.rand.nextFloat() * 0.3F);
	}

	@Override
	protected void playEmergeSound() {
		this.playSound(SoundRegistry.SPIRIT_TREE_FACE_LARGE_EMERGE, 1, 0.8F + this.rand.nextFloat() * 0.3F);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);

		if(LAST_LOCKED_WISP.equals(key) && this.world.isRemote) {
			this.wispLockEffect(this.dataManager.get(LAST_LOCKED_WISP));
		}
	}

	@SideOnly(Side.CLIENT)
	protected void wispLockEffect(BlockPos pos) {
		for(int i = 0; i < 64; i++) {
			Vec3d dir = new Vec3d(this.rand.nextFloat() - 0.5F, this.rand.nextFloat() - 0.5F + 0.25F, this.rand.nextFloat() - 0.5F);
			dir = dir.normalize().scale(2);
			BLParticles.CORRUPTED.spawn(this.world, pos.getX() + 0.5D + this.rand.nextFloat() / 2.0F - 0.25F, pos.getY() + 0.5D + this.rand.nextFloat() / 2.0F - 0.25F, pos.getZ() + 0.5D + this.rand.nextFloat() / 2.0F - 0.25F, ParticleArgs.get().withMotion(dir.x, dir.y, dir.z));
		}
		this.world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundRegistry.DAMAGE_REDUCTION, SoundCategory.HOSTILE, 0.65F, 0.5F, false);
	}

	@Override
	public boolean isActive() {
		return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (!this.world.isRemote) {
			this.dataManager.set(BOSSINFO_ID, Optional.of(this.bossInfo.getUniqueId()));
		}
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
	}

	@Override
	protected void onDeathUpdate() {
		super.onDeathUpdate();
		this.bossInfo.setPercent(0);
	}

	@Override
	public void setCustomNameTag(String name) {
		super.setCustomNameTag(name);
		this.bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void addTrackingPlayer(EntityPlayerMP player) {
		super.addTrackingPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(EntityPlayerMP player) {
		super.removeTrackingPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.isAnchored()) {
			this.setSize(1.8F, 1.8F);
		} else {
			this.setSize(1.8F, 0.2F);
		}
		
		if(this.dataManager.get(BLOW_STATE) != 0 || this.dataManager.get(ROTATING_WAVE_STATE) != 0 || this.dataManager.get(CRAWLING_WAVE_STATE) != 0 || this.dataManager.get(SPIT_STATE) != 0) {
			this.setGlowTicks(20);
		}

		if(!this.world.isRemote) {
			if(this.isActive() && this.isEntityAlive() && this.getAttackTarget() != null) {
				if(this.ticksExisted % 20 == 0) {
					this.updateWispStrengthModifier();
				}

				if(this.ticksExisted % 10 == 0) {
					List<LocationSpiritTree> locations = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, this.getEntityBoundingBox(), loc -> loc.isInside(this));
					if(!locations.isEmpty()) {
						LocationSpiritTree location = locations.get(0);

						List<BlockPos> wispPositions = new ArrayList<>();

						wispPositions.addAll(location.getNotGeneratedWispPositions());

						if(this.getHealth() < this.getMaxHealth() / 2) {
							wispPositions.addAll(location.getGeneratedWispPositions());
						}

						for(BlockPos wispPosition : wispPositions) {
							if(!location.getGuard().isGuarded(this.world, null, wispPosition) && this.world.getBlockState(wispPosition).getBlock() == BlockRegistry.WISP) {
								location.getGuard().setGuarded(this.world, wispPosition, true);
								this.dataManager.set(LAST_LOCKED_WISP, wispPosition);
								break;
							}
						}
					}
				}
			}

			float strengthModifier = this.getWispStrengthModifier();
			if(strengthModifier <= 1.0F) {
				this.experienceValue = (int) (DEFAULT_XP_DROPPED * strengthModifier);
			} else {
				this.experienceValue = (int) (DEFAULT_XP_DROPPED * (1 + (strengthModifier - 1) * 6));
			}

			IAttributeInstance attackAttribute = this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			attackAttribute.removeModifier(STRENGTH_MULTIPLIER_ATTRIBUTE_UUID);
			attackAttribute.applyModifier(new AttributeModifier(STRENGTH_MULTIPLIER_ATTRIBUTE_UUID, "Wisp strength modifier", strengthModifier - 1.0F, 2));

			if(this.blowTicks > 0) {
				if(this.blowTicks > 20 + this.blowDelay) {
					this.dataManager.set(BLOW_STATE, 3);

					if((this.blowTicks - (21 + this.blowDelay)) % 15 == 0) {
						this.doBlowAttack();
						this.world.setEntityState(this, EVENT_BLOW_ATTACK);
						this.playSound(SoundRegistry.SPIRIT_TREE_FACE_SPIT_ROOT_SPIKES, 1, 0.9F + this.rand.nextFloat() * 0.2F);
					}
				} else {
					if(this.blowTicks > this.blowDelay) {
						this.dataManager.set(BLOW_STATE, 2);
						if(this.blowTicks == this.blowDelay + 1) {
							this.playSound(SoundRegistry.SPIRIT_TREE_FACE_SUCK, 1, 1);
						}
					} else {
						this.dataManager.set(BLOW_STATE, 1);
					}
				}

				if(this.blowTicks > 160 + this.blowDelay) {
					this.dataManager.set(BLOW_STATE, 0);
					this.blowTicks = 0;
				} else {
					this.blowTicks++;
				}
			}

			if(this.rotatingWaveTicks > 0) {
				if(this.rotatingWaveTicks > this.rotatingWaveDelay) {
					this.dataManager.set(ROTATING_WAVE_STATE, 2);

					if((this.rotatingWaveTicks - 1 - this.rotatingWaveDelay) % 3 == 0) {
						for(int i = 0; i < 2; i++) {
							double increment = Math.PI * 2 / 20;

							double a1 = (this.rotatingWaveStart + (this.rotatingWaveTicks - this.rotatingWaveDelay) / 3 * increment) * (i == 0 ? 1 : -1);
							double a2 = (this.rotatingWaveStart + ((this.rotatingWaveTicks - this.rotatingWaveDelay) / 3 + 1) * increment) * (i == 0 ? 1 : -1);

							double start = Math.min(a1, a2);
							double end = Math.max(a1, a2);

							List<BlockPos> blocks = BlockShapeUtils.getRingSegment(this.getAnchor(), start, end, WorldGenSpiritTreeStructure.RADIUS_INNER_CIRLCE + 0.5D, WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE + 0.5D, false, new ArrayList<>());

							List<BlockPos> spawnBlocks = new ArrayList<>();

							for(BlockPos pos : blocks) {
								BlockPos spawnPos = this.getWaveGroundPos(pos, 0);

								if(spawnPos != null) {
									spawnBlocks.add(spawnPos);
								}
							}

							if(!spawnBlocks.isEmpty()) {
								EntitySpikeWave spikeWave = new EntitySpikeWave(this.world);
								spikeWave.delay = 2;
								spikeWave.setAttackDamage(10.0F * this.getWispStrengthModifier());
								for(BlockPos pos : spawnBlocks) {
									spikeWave.addPosition(pos);
								}
								this.world.spawnEntity(spikeWave);
							}
						}
					}
				} else {
					this.dataManager.set(ROTATING_WAVE_STATE, 1);
				}

				if(this.rotatingWaveTicks >= 3 * 20 * 3 + this.rotatingWaveDelay) {
					this.rotatingWaveTicks = 0;
					this.dataManager.set(ROTATING_WAVE_STATE, 0);
				} else {
					this.rotatingWaveTicks++;
				}
			}

			if(this.crawlingWaveTicks > 0) {
				final int ticksPerWave = CRAWLING_WAVE_RANGE / 3 * 4;

				if(this.crawlingWaveTicks > this.crawlingWaveDelay) {
					this.dataManager.set(CRAWLING_WAVE_STATE, 2);

					if(this.getAttackTarget() != null) {
						if((this.crawlingWaveTicks - 1 - this.crawlingWaveDelay) % ticksPerWave == 0) {
							this.crawlingWaveAngle = (float) Math.atan2(this.getAttackTarget().posZ - this.posZ, this.getAttackTarget().posX - this.posX);
						}

						if((this.crawlingWaveTicks - 1 - this.crawlingWaveDelay) % 4 == 0) {
							int dist = WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE + ((this.crawlingWaveTicks - 1 - this.crawlingWaveDelay) / 4 * 3) % CRAWLING_WAVE_RANGE;

							double a1 = this.crawlingWaveAngle - Math.PI / 16;
							double a2 = this.crawlingWaveAngle + Math.PI / 16;

							List<BlockPos> blocks = BlockShapeUtils.getRingSegment(this.getAnchor(), a1, a2, dist, dist + 3, false, new ArrayList<>());

							List<BlockPos> spawnBlocks = new ArrayList<>();

							for(BlockPos pos : blocks) {
								BlockPos spawnPos = this.getWaveGroundPos(pos, 4);

								if(spawnPos != null) {
									spawnBlocks.add(spawnPos);
								}
							}

							if(!spawnBlocks.isEmpty()) {
								EntitySpikeWave spikeWave = new EntitySpikeWave(this.world);
								spikeWave.delay = 2;
								spikeWave.setAttackDamage(10.0F * this.getWispStrengthModifier());
								for(BlockPos pos : spawnBlocks) {
									spikeWave.addPosition(pos);
								}
								this.world.spawnEntity(spikeWave);
							}
						}
					}
				} else {
					this.dataManager.set(CRAWLING_WAVE_STATE, 1);
				}

				if(this.crawlingWaveTicks >= ticksPerWave * 3 + this.crawlingWaveDelay) {
					this.crawlingWaveTicks = 0;
					this.dataManager.set(CRAWLING_WAVE_STATE, 0);
				} else {
					this.crawlingWaveTicks++;
				}
			}
		} else {
			int blowState = this.dataManager.get(BLOW_STATE);
			if(blowState == 2) {
				Vec3d frontCenter = this.getFrontCenter();
				for(int i = 0; i < 4; i++) {
					Random rnd = world.rand;
					float rx = rnd.nextFloat() * 4.0F - 2.0F + this.getFacing().getXOffset() * 4;
					float ry = rnd.nextFloat() * 4.0F - 2.0F + this.getFacing().getYOffset() * 4;
					float rz = rnd.nextFloat() * 4.0F - 2.0F + this.getFacing().getZOffset() * 4;
					Vec3d vec = new Vec3d(rx, ry, rz);
					vec = vec.normalize();
					this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, frontCenter.x + rx, frontCenter.y - 0.75D + ry, frontCenter.z + rz, -vec.x * 0.5F, -vec.y * 0.5F, -vec.z * 0.5F);
				}
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		nbt.setFloat("wispStrengthModifier", this.dataManager.get(WISP_STRENGTH_MODIFIER));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		this.dataManager.set(WISP_STRENGTH_MODIFIER, nbt.getFloat("wispStrengthModifier"));

		if(this.hasCustomName()) {
			this.bossInfo.setName(this.getDisplayName());
		}
	}

	@Nullable
	protected BlockPos getWaveGroundPos(BlockPos pos, int yOff) {
		MutableBlockPos checkPos = new MutableBlockPos();

		for(int yo = 0; yo < 16; yo++) {
			checkPos.setPos(pos.getX(), pos.getY() - yo + yOff, pos.getZ());

			IBlockState state = this.world.getBlockState(checkPos);

			if(state.getCollisionBoundingBox(world, checkPos) != null) {
				if(state.isSideSolid(world, checkPos, EnumFacing.UP)) {
					return checkPos.toImmutable();
				}
				break;
			}
		}

		return null;
	}

	public boolean isTargetInBlowRange(EntityLivingBase target) {
		if(target.getDistanceSq(this) <= 5 * 5) {
			Vec3d center = this.getFrontCenter();
			Vec3d targetCenter = new Vec3d(target.posX, target.posY + target.getEyeHeight(), target.posZ);
			Vec3d dir = targetCenter.subtract(center).normalize();
			Vec3d facing = new Vec3d(this.getFacing().getXOffset(), this.getFacing().getYOffset(), this.getFacing().getZOffset());
			float angle = (float)(Math.acos(facing.dotProduct(dir)) / 2 / Math.PI) * 360.0F;
			return angle >= 0.0F && angle <= 90.0F;
		}
		return false;
	}

	public void doBlowAttack() {
		Vec3d frontCenter = this.getFrontCenter();
		List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(frontCenter.x - 5, frontCenter.y - 5, frontCenter.z - 5, frontCenter.x + 5, frontCenter.y + 5, frontCenter.z + 5));
		for(EntityLivingBase target : targets) {
			if(target != this && this.isTargetInBlowRange(target)) {
				this.attackEntityAsMob(target);
			}
		}
	}

	public void startBlowAttack() {
		this.blowTicks = 1;
		if(this.getWispStrengthModifier() > 1.0F) {
			this.blowDelay = (int)(DEFAULT_BLOW_DELAY / (1.0F + (this.getWispStrengthModifier() - 1.0F) * 2.0F));
		} else {
			this.blowDelay = DEFAULT_BLOW_DELAY;
		}
	}

	public boolean isTargetInRotatingWaveAttackRange(EntityLivingBase target) {
		double dx = this.posX - target.posX;
		double dz = this.posZ - target.posZ;
		double dstSq = dx*dx + dz*dz;
		int innerSq = WorldGenSpiritTreeStructure.RADIUS_INNER_CIRLCE * WorldGenSpiritTreeStructure.RADIUS_INNER_CIRLCE;
		int outerSq = WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE * WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE;
		return dstSq >= innerSq && dstSq <= outerSq;
	}

	public void startRotatingWaveAttack() {
		this.rotatingWaveTicks = 1;
		this.rotatingWaveStart = this.rand.nextFloat() * (float)Math.PI * 2;
		if(this.getWispStrengthModifier() > 1.0F) {
			this.rotatingWaveDelay = (int)(DEFAULT_ROTATING_WAVE_DELAY / (1.0F + (this.getWispStrengthModifier() - 1.0F) * 2.0F));
		} else {
			this.rotatingWaveDelay = DEFAULT_ROTATING_WAVE_DELAY;
		}
		this.dataManager.set(ROTATING_WAVE_STATE, 1);
	}

	public boolean isTargetInCrawlingWaveAttackRange(EntityLivingBase target) {
		double dx = this.posX - target.posX;
		double dz = this.posZ - target.posZ;
		double dstSq = dx*dx + dz*dz;
		int innerSq = WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE * WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE;
		int outerSq = (WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE + CRAWLING_WAVE_RANGE) * (WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE + CRAWLING_WAVE_RANGE);
		return dstSq >= innerSq && dstSq <= outerSq;
	}

	public void startCrawlingWaveAttack() {
		this.crawlingWaveTicks = 1;
		if(this.getWispStrengthModifier() > 1.0F) {
			this.crawlingWaveDelay = (int)(DEFAULT_CRAWLING_WAVE_DELAY / (1.0F + (this.getWispStrengthModifier() - 1.0F) * 2.0F));
		} else {
			this.crawlingWaveDelay = DEFAULT_CRAWLING_WAVE_DELAY;
		}
		this.dataManager.set(CRAWLING_WAVE_STATE, 1);
	}

	public boolean isTargetInGrabAttackRange(EntityLivingBase target) {
		double dx = this.posX - target.posX;
		double dz = this.posZ - target.posZ;
		double dstSq = dx*dx + dz*dz;
		int outerSq = (WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE + CRAWLING_WAVE_RANGE) * (WorldGenSpiritTreeStructure.RADIUS_OUTER_CIRCLE + CRAWLING_WAVE_RANGE);
		return dstSq <= outerSq;
	}

	public boolean startGrabAttack() {
		if(this.getAttackTarget() != null) {
			EntityLivingBase target = this.getAttackTarget();

			for(int i = 0; i < 6; i++) {
				BlockPos pos = new BlockPos(target.posX + this.rand.nextInt(3) - 1, target.posY - 1, target.posZ + this.rand.nextInt(3) - 1);

				if(this.world.isAirBlock(pos.up()) && this.world.isAirBlock(pos.up(2))) {
					boolean validPos = true;
					for(int xo = -1; xo <= 1; xo++) {
						for(int zo = -1; zo <= 1; zo++) {
							if(!this.world.isBlockNormalCube(pos.add(xo, 0, zo), false)) {
								validPos = false;
							}
						}
					}

					if(validPos) {
						EntityRootGrabber grabber = new EntityRootGrabber(this.world);
						grabber.setPosition(pos, (int)(40 / this.getWispStrengthModifier()));
						this.world.spawnEntity(grabber);
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public void startSpit(float spitDamage) {
		super.startSpit(spitDamage);
		if(this.getWispStrengthModifier() > 1.0F) {
			this.spitDelay = (int)(DEFAULT_SPIT_DELAY / (1.0F + (this.getWispStrengthModifier() - 1.0F) * 2.0F));
		} else {
			this.spitDelay = DEFAULT_SPIT_DELAY;
		}
		this.dataManager.set(SPIT_STATE, 1);
	}

	@Override
	protected void updateSpitAttack() {
		if(this.spitTicks == this.spitDelay) {
			this.world.setEntityState(this, EVENT_SPIT);
			this.setGlowTicks(10);
			this.playSpitSound();
		}

		if(this.spitTicks > 6 + this.spitDelay) {
			this.doSpitAttack();
			this.dataManager.set(SPIT_STATE, 0);
			this.spitTicks = 0;
		} else {
			this.spitTicks++;
		}
	}

	protected void updateWispStrengthModifier() {
		List<LocationSpiritTree> locations = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, this.getEntityBoundingBox(), loc -> loc.isInside(this));
		if(!locations.isEmpty()) {
			LocationSpiritTree location = locations.get(0);

			int activeWisps = location.getActiveWisps();
			int generatedWisps = location.getGeneratedWispPositions().size();

			float newModifier;
			if(activeWisps < generatedWisps) {
				newModifier = 0.5F + activeWisps / (float)generatedWisps * 0.5F;
			} else {
				newModifier = Math.min(1.0F + (activeWisps - generatedWisps) / 6.0F, 2.0F);
			}

			float decay = (float) Math.pow(this.getHealth() / this.getMaxHealth(), 6) * 0.33F;

			this.dataManager.set(WISP_STRENGTH_MODIFIER, decay * newModifier + (1 - decay) * this.dataManager.get(WISP_STRENGTH_MODIFIER));
		}
	}

	public float getWispStrengthModifier() {
		return this.dataManager.get(WISP_STRENGTH_MODIFIER);
	}

	@Override
	public UUID getBossInfoUuid() {
		return this.dataManager.get(BOSSINFO_ID).or(new UUID(0, 0));
	}

	@Override
	public BossType getBossType() {
		return BossType.MINI_BOSS;
	}

	@Override
	public Vec3d getMiniBossTagOffset(float partialTicks) {
		return new Vec3d(this.getFacing().getXOffset() * (this.width / 2), this.height + 0.5D, this.getFacing().getZOffset() * (this.width / 2));
	}

	public static class AIRespawnSmallFaces extends EntityAIBase {
		protected final EntitySpiritTreeFaceLarge entity;

		protected int executeCheckCooldown = 0;

		protected boolean shouldContinue = true;

		protected int spawnCooldown = 0;

		public AIRespawnSmallFaces(EntitySpiritTreeFaceLarge entity) {
			this.entity = entity;
		}

		protected int countSmallFaces() {
			return this.entity.world.getEntitiesWithinAABB(EntitySpiritTreeFaceSmall.class, this.entity.getEntityBoundingBox().grow(40.0D)).size();
		}

		protected boolean hasEnoughSmallFaces() {
			return this.countSmallFaces() >= 8;
		}

		@Override
		public boolean shouldExecute() {
			if(this.entity.isEntityAlive() && this.entity.isActive()) {
				if(this.executeCheckCooldown <= 0) {
					this.executeCheckCooldown = 20 + this.entity.rand.nextInt(20);
					return !this.hasEnoughSmallFaces();
				}
				this.executeCheckCooldown--;
			}
			return false;
		}

		@Override
		public void startExecuting() {
			this.shouldContinue = true;
		}

		@Override
		public void updateTask() {
			if(this.spawnCooldown <= 0) {
				this.spawnCooldown = (int)((180 + this.entity.rand.nextInt(100)) / this.entity.getWispStrengthModifier());
				List<BlockPos> blocks = this.entity.findSmallFacesBlocks();

				if(!blocks.isEmpty()) {
					EntitySpiritTreeFaceSmall face = new EntitySpiritTreeFaceSmall(this.entity.world);

					spawnLoop: for(int i = 0; i < 16; i++) {
						BlockPos anchor = blocks.get(this.entity.rand.nextInt(blocks.size()));

						List<EnumFacing> facings = new ArrayList<>();
						facings.addAll(Arrays.asList(EnumFacing.VALUES));
						Collections.shuffle(facings, this.entity.rand);

						for(EnumFacing facing : facings) {
							EnumFacing facingUp = facing.getAxis().isVertical() ? EnumFacing.HORIZONTALS[this.entity.rand.nextInt(EnumFacing.HORIZONTALS.length)] : EnumFacing.UP;
							if(face.checkAnchorAt(anchor, facing, facingUp, AnchorChecks.ALL) == 0) {
								face.onInitialSpawn(this.entity.world.getDifficultyForLocation(anchor), null);
								face.setPositionToAnchor(anchor, facing, facingUp);
								this.entity.world.spawnEntity(face);
								break spawnLoop;
							}
						}
					}

					if(this.hasEnoughSmallFaces()) {
						this.shouldContinue = false;
					}
				}
			}
			this.spawnCooldown--;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.entity.isActive() && this.shouldContinue;
		}
	}

	public static class AIBlowAttack extends EntityAIBase {
		protected final EntitySpiritTreeFaceLarge entity;

		protected int cooldown = 30;

		public AIBlowAttack(EntitySpiritTreeFaceLarge entity) {
			this.entity = entity;
		}

		@Override
		public boolean shouldExecute() {
			if(this.entity.isActive() && !this.entity.isAttacking() && this.entity.getAttackTarget() != null && this.entity.isTargetInBlowRange(this.entity.getAttackTarget())) {
				if(this.cooldown <= 0) {
					this.cooldown = (int)((30 + this.entity.rand.nextInt(30)) / this.entity.getWispStrengthModifier());
					return true;
				}
				this.cooldown--;
			}
			return false;
		}

		@Override
		public void startExecuting() {
			this.entity.startBlowAttack();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}
	}

	public static class AIRotatingWaveAttack extends EntityAIBase {
		protected final EntitySpiritTreeFaceLarge entity;

		protected int cooldown = 30;

		public AIRotatingWaveAttack(EntitySpiritTreeFaceLarge entity) {
			this.entity = entity;
		}

		@Override
		public boolean shouldExecute() {
			if(this.entity.isActive() && this.entity.rotatingWaveTicks == 0 && this.entity.getAttackTarget() != null && this.entity.isTargetInRotatingWaveAttackRange(this.entity.getAttackTarget())) {
				if(this.cooldown <= 0) {
					this.cooldown = (int)((60 + this.entity.rand.nextInt(80)) / this.entity.getWispStrengthModifier());
					return true;
				}
				this.cooldown--;
			}
			return false;
		}

		@Override
		public void startExecuting() {
			this.entity.startRotatingWaveAttack();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}
	}

	public static class AICrawlingWaveAttack extends EntityAIBase {
		protected final EntitySpiritTreeFaceLarge entity;

		protected int cooldown = 30;

		public AICrawlingWaveAttack(EntitySpiritTreeFaceLarge entity) {
			this.entity = entity;
		}

		@Override
		public boolean shouldExecute() {
			if(this.entity.isActive() && this.entity.crawlingWaveTicks == 0 && this.entity.getAttackTarget() != null && this.entity.isTargetInCrawlingWaveAttackRange(this.entity.getAttackTarget())) {
				if(this.cooldown <= 0) {
					this.cooldown = (int)((60 + this.entity.rand.nextInt(80)) / this.entity.getWispStrengthModifier());
					return true;
				}
				this.cooldown--;
			}
			return false;
		}

		@Override
		public void startExecuting() {
			this.entity.startCrawlingWaveAttack();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}
	}

	public static class AIGrabAttack extends EntityAIBase {
		protected final EntitySpiritTreeFaceLarge entity;

		protected int cooldown = 30;

		public AIGrabAttack(EntitySpiritTreeFaceLarge entity) {
			this.entity = entity;
		}

		@Override
		public boolean shouldExecute() {
			if(this.entity.isActive() && !this.entity.isAttacking() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().onGround && this.entity.isTargetInGrabAttackRange(this.entity.getAttackTarget())) {
				if(this.cooldown <= 0) {
					this.cooldown = (int)((60 + this.entity.rand.nextInt(80)) / this.entity.getWispStrengthModifier());
					return true;
				}
				this.cooldown--;
			}
			return false;
		}

		@Override
		public void startExecuting() {
			this.entity.startGrabAttack();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}
	}
}
