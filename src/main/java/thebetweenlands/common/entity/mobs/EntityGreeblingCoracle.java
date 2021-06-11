package thebetweenlands.common.entity.mobs;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.entity.ai.EntityAvoidEntityFlatPath;
import thebetweenlands.common.entity.movement.PathNavigateAboveWater;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.util.NonNullDelegateList;

public class EntityGreeblingCoracle extends EntityCreature implements IEntityBL {
	protected static final byte EVENT_DISAPPEAR = 41;
	protected static final byte EVENT_SPOUT = 42;
	private static final DataParameter<Integer> SINKING_TICKS = EntityDataManager.createKey(EntityGreeblingCoracle.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> LOOT_CLICKS = EntityDataManager.createKey(EntityGreeblingCoracle.class, DataSerializers.VARINT);
	EntityAvoidEntityFlatPath avoidPlayer;
	AIWaterWander waterWander;
	EntityAILookIdle lookIdle;
	boolean hasSetAIForEmptyBoat = false;
	private boolean looted = false;
	private NonNullList<ItemStack> loot = NonNullList.create();
	private int shutUpFFSTime;
	public int rowTicks;
	public float rowSpeed = 0.5f;

    public EntityGreeblingCoracle(World worldIn) {
        super(worldIn);
        setSize(1.0F, 1.0F);
		setPathPriority(PathNodeType.WALKABLE, -100.0F);
		setPathPriority(PathNodeType.BLOCKED, -100.0F);
		setPathPriority(PathNodeType.LAVA, -100.0F);
		setPathPriority(PathNodeType.WATER, 16.0F);
		stepHeight = 0;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SINKING_TICKS, 0);
        dataManager.register(LOOT_CLICKS, 0);
    }

    @Override
    protected void initEntityAI() {
    	avoidPlayer = new EntityAvoidEntityFlatPath<>(this, EntityPlayer.class, 16F, 4D, 8D);
    	waterWander = new EntityGreeblingCoracle.AIWaterWander(this, 0.5D, 30);
    	lookIdle = new EntityAILookIdle(this);
    	tasks.addTask(0, avoidPlayer);
    	tasks.addTask(1, waterWander);
        tasks.addTask(2, lookIdle);
    }

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
	}

	@Override
    protected PathNavigate createNavigator(World world) {
		return new PathNavigateAboveWater(this, world); 
	}

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F + world.getLightBrightness(pos) - 0.5F : super.getBlockPathWeight(pos);
    }

	@Override
	public boolean getCanSpawnHere() {
		int y = MathHelper.floor(getEntityBoundingBox().minY);
		if(y <= WorldProviderBetweenlands.LAYER_HEIGHT && y > WorldProviderBetweenlands.CAVE_START)
			return getEntityWorld().checkNoEntityCollision(getEntityBoundingBox()) && getEntityWorld().getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && getEntityWorld().isMaterialInBB(getEntityBoundingBox(), Material.WATER);
		return false;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (world.isRemote && getSinkingTicks() <= 0) {
			this.rowTicks++;

			if (!isSilent() && posX != lastTickPosX && posZ != lastTickPosZ) {
				float rowAngle1 = MathHelper.cos(this.rowTicks * rowSpeed);
				float rowAngle2 = MathHelper.cos((this.rowTicks + 1) * rowSpeed);
				if(rowAngle1 <= 0.8f && rowAngle2 > 0.8f) {
					world.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_SWIM, getSoundCategory(), 0.2F, 0.8F + 0.4F * this.rand.nextFloat(), false);
				}
			}
		}

		if(shutUpFFSTime > 0) {
			shutUpFFSTime--;
			livingSoundTime = -getTalkInterval();
		}

		if (getEntityWorld().containsAnyLiquid(getEntityBoundingBox()) && getSinkingTicks() <= 200)
			motionY += 0.06D;

		if (getSinkingTicks() > 0) {
			motionX *= 0.975D;
			motionZ *= 0.975D;
		}

		if (isGreeblingAboveWater() && getSinkingTicks() <= 200) {
			if (motionY < 0.0D)
				motionY = 0.0D;
			fallDistance = 0.0F;
		}
		else {
			motionY = -0.0075D;
		}

		limbSwing += 0.5D;
		if (posX != lastTickPosX && posZ != lastTickPosZ)
			limbSwingAmount += 0.5D;

		if(!getEntityWorld().isRemote) {
			if (getSinkingTicks() > 0 && getSinkingTicks() < 400)
				setSinkingTicks(getSinkingTicks() + 1);

			if (getSinkingTicks() == 5)
				getEntityWorld().setEntityState(this, EVENT_DISAPPEAR);

			if (getSinkingTicks() == 200 && isGreeblingAboveWater())
				world.playSound(null, getPosition(), SoundRegistry.CORACLE_SINK, SoundCategory.NEUTRAL, 1F, 1F);

			if (getSinkingTicks() >= 200 && getSinkingTicks() <= 400 && isGreeblingAboveWater())
				getEntityWorld().setEntityState(this, EVENT_SPOUT);

			if (getSinkingTicks() > 0 && !hasSetAIForEmptyBoat) {
				tasks.removeTask(avoidPlayer);
				tasks.removeTask(waterWander);
				tasks.removeTask(lookIdle);
				if(getNavigator().getPath() != null) {
					getNavigator().clearPath();
				}
				hasSetAIForEmptyBoat = true;
			}

			if (getSinkingTicks() >= 400)
				setDead();

			List<EntityPlayer> nearPlayers = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().grow(2.5, 2.5, 2.5), e -> !e.capabilities.isCreativeMode && !e.isInvisible());
			if (getSinkingTicks() == 0 && !nearPlayers.isEmpty()) {
				setSinkingTicks(getSinkingTicks() + 1);
				getEntityWorld().playSound(null, posX, posY, posZ, SoundRegistry.GREEBLING_VANISH, SoundCategory.NEUTRAL, 1F, 1F);
			}
		}
	}

	public boolean isGreeblingAboveWater() {
		AxisAlignedBB floatingBox = new AxisAlignedBB(getEntityBoundingBox().minX + 0.25D, getEntityBoundingBox().minY + 0.12F, getEntityBoundingBox().minZ + 0.25D, getEntityBoundingBox().maxX - 0.25D, getEntityBoundingBox().minY + 0.0625D, getEntityBoundingBox().maxZ - 0.25D);
		return getEntityWorld().containsAnyLiquid(floatingBox);
	}

    public void setSinkingTicks(int count) {
        dataManager.set(SINKING_TICKS, count);
    }

    public int getSinkingTicks() {
        return dataManager.get(SINKING_TICKS);
    }

    public void setLootClicks(int count) {
        dataManager.set(LOOT_CLICKS, count);
    }

    public int getLootClicks() {
        return dataManager.get(LOOT_CLICKS);
    }

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("sinkingTicks", getSinkingTicks());
		nbt.setBoolean("Looted", looted);
		nbt.setInteger("LootCount", loot.size());
		nbt.setInteger("LootClicks", getLootClicks());
		nbt.setTag("Loot", ItemStackHelper.saveAllItems(new NBTTagCompound(), loot, false));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setSinkingTicks(nbt.getInteger("sinkingTicks"));
		looted = nbt.getBoolean("Looted");
		loot = NonNullList.withSize(nbt.getInteger("LootCount"), ItemStack.EMPTY);
		setLootClicks(nbt.getInteger("lootClicks"));
		ItemStackHelper.loadAllItems(nbt.getCompoundTag("Loot"), loot);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		if(id == EVENT_DISAPPEAR)
			doLeafEffects();
		if(id == EVENT_SPOUT)
			doSpoutEffects();
	}

	private void doSpoutEffects() {
		if(getEntityWorld().isRemote) {
			int count = getSinkingTicks() <= 240 ? 40 : 10;
			float x = (float) (posX);
			float y = (float) (posY + 0.25F);
			float z = (float) (posZ);
			while (count-- > 0) {
				float dx = getEntityWorld().rand.nextFloat() * 0.25F - 0.1255f;
				float dy = getEntityWorld().rand.nextFloat() * 0.25F - 0.1255f;
				float dz = getEntityWorld().rand.nextFloat() * 0.25F - 0.1255f;
				float mag = 0.08F + getEntityWorld().rand.nextFloat() * 0.07F;
				if(getSinkingTicks() <= 240)
					BLParticles.SPLASH.spawn(getEntityWorld(), x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
				else if(getSinkingTicks() > 240 && getSinkingTicks() <= 400 && getSinkingTicks()%5 == 0)
					BLParticles.BUBBLE_PURIFIER.spawn(getEntityWorld(), x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

	private void doLeafEffects() {
		if(getEntityWorld().isRemote) {
			int leafCount = 40;
			float x = (float) (posX);
			float y = (float) (posY + 0.75F);
			float z = (float) (posZ);
			while (leafCount-- > 0) {
				float dx = getEntityWorld().rand.nextFloat() * 1 - 0.5f;
				float dy = getEntityWorld().rand.nextFloat() * 1f - 0.1F;
				float dz = getEntityWorld().rand.nextFloat() * 1 - 0.5f;
				float mag = 0.08F + getEntityWorld().rand.nextFloat() * 0.07F;
				BLParticles.WEEDWOOD_LEAF.spawn(getEntityWorld(), x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

	@Override
    protected float getSoundVolume() {
        return 0.75F;
    }

	@Override
	protected SoundEvent getAmbientSound() {
		if(getSinkingTicks() <= 0) {
			if(this.rand.nextInt(4) == 0 && shutUpFFSTime <= 0) {
				shutUpFFSTime = 120;
				return SoundRegistry.GREEBLING_HUM;
			}
			else
				return SoundRegistry.GREEBLING_GIGGLE;
		}
		return null;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.getTrueSource() instanceof EntityLivingBase)
			if (getSinkingTicks() == 0)
				if (!getEntityWorld().isRemote) {
					setSinkingTicks(getSinkingTicks() + 1);
					getEntityWorld().playSound(null, posX, posY, posZ, SoundRegistry.GREEBLING_VANISH, SoundCategory.NEUTRAL, 1F, 1F);
				}
		return false;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (getSinkingTicks() > 0 && getSinkingTicks() < 200 && hand == EnumHand.MAIN_HAND) {
			player.swingArm(hand);
			if (!getEntityWorld().isRemote) {
				dropLoot((EntityPlayer) player);
				setLootClicks(getLootClicks() +1);
				if(getLootClicks() >= loot.size())
					setSinkingTicks(200);
				SoundType soundType = SoundType.WOOD;
				getEntityWorld().playSound((EntityPlayer)null,posX, posY, posZ, soundType.getHitSound(), SoundCategory.NEUTRAL, (soundType.getVolume() + 1.0F) / 4.0F, soundType.getPitch() * 0.5F);
				return true;
			}
		}
		return super.processInteract(player, hand);
	}

	//temp C&P loot thing with tweaks
	public void dropLoot(EntityPlayer player) {
		if(!getEntityWorld().isRemote) {
			if(!looted) {
				looted = true;

				LootTable lootTable = getEntityWorld().getLootTableManager().getLootTableFromLocation(LootTableRegistry.GREEBLING_CORPSE);
				LootContext.Builder builder = (new LootContext.Builder((WorldServer)getEntityWorld())).withLootedEntity(this).withPlayer(player).withLuck(player.getLuck());

				loot = new NonNullDelegateList<ItemStack>(lootTable.generateLootForPools(rand, builder.build()), ItemStack.EMPTY);
			}

			ItemStack stack = loot.get(getLootClicks());
			if (!stack.isEmpty()) {
				entityDropItem(stack, 0.0F);
				loot.set(getLootClicks(), ItemStack.EMPTY);
			}
		}
	}

	public class AIWaterWander extends EntityAIWander {

		private final EntityGreeblingCoracle coracle;

		public AIWaterWander(EntityGreeblingCoracle coracleIn, double speedIn, int chance) {
			super(coracleIn, speedIn, chance);
			setMutexBits(1);
			coracle = coracleIn;
		}

		@Nullable
		@Override
		protected Vec3d getPosition() {
			return RandomPositionGenerator.findRandomTarget(coracle, 16, 0);
		}
	}
}
