package thebetweenlands.common.entity.creature;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.audio.EntitySoundInstance;
import thebetweenlands.api.entity.MusicPlayer;
import thebetweenlands.client.audio.EntityMusicLayers;
import thebetweenlands.client.audio.GreeblingMusicInstance;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class Greebling extends Mob implements BLEntity, MusicPlayer {
	protected static final byte EVENT_START_DISAPPEARING = 40;
	protected static final byte EVENT_DISAPPEAR = 41;

	private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(Greebling.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Direction> FACING = SynchedEntityData.defineId(Greebling.class, EntityDataSerializers.DIRECTION);

	public int disappearTimer = 0;

	public Greebling(EntityType<? extends Mob> type, Level level) {
		super(type, level);
	}

	private static final class GreeblingGroup implements SpawnGroupData {
		public boolean hasType1;
		public boolean hasType2;
		public int count;
	}

	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		spawnGroupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);


		if (spawnGroupData == null) {
			spawnGroupData = new GreeblingGroup();
		}

		if (spawnGroupData instanceof GreeblingGroup group) {
			if (group.count > 0 && (!group.hasType1 || !group.hasType2)) {
				if (!group.hasType1) {
					this.setGreeblingType(0);
				} else {
					this.setGreeblingType(1);
				}
			} else {
				this.setGreeblingType(this.getRandom().nextInt(2));
				if (this.getGreeblingType() == 0) {
					group.hasType1 = true;
				} else {
					group.hasType2 = true;
				}
			}

			group.count++;
		} else {
			this.setGreeblingType(this.getRandom().nextInt(2));
		}

		this.getEntityData().set(FACING, Direction.Plane.HORIZONTAL.getRandomDirection(this.getRandom()));

		return spawnGroupData;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(TYPE, 0);
		builder.define(FACING, Direction.NORTH);
	}

	public void setGreeblingType(int type) {
		this.getEntityData().set(TYPE, type);
	}

	public int getGreeblingType() {
		return this.getEntityData().get(TYPE);
	}

	@Override
	public void tick() {
		super.tick();

		this.setXRot(this.xRotO = 0);
		this.setYRot(this.yRotO = this.getEntityData().get(FACING).toYRot());
		this.setYBodyRot(this.yBodyRotO = this.getEntityData().get(FACING).toYRot());

		if (this.disappearTimer > 0 && this.disappearTimer < 8) this.disappearTimer++;

		if (!this.level().isClientSide()) {
			if (this.disappearTimer == 5) this.level().broadcastEntityEvent(this, EVENT_DISAPPEAR);
			if (this.disappearTimer >= 8) this.discard();

			List<Player> nearPlayers = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(4.5, 5, 4.5), player -> !player.isCreative() && !player.isInvisible());
			if (this.disappearTimer == 0 && !nearPlayers.isEmpty()) {
				this.disappearTimer++;
				this.level().playSound(null, this.blockPosition(), SoundRegistry.GREEBLING_VANISH.get(), SoundSource.NEUTRAL, 1, 1);
				this.level().broadcastEntityEvent(this, EVENT_START_DISAPPEARING);
			}
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if (id == EVENT_START_DISAPPEARING) {
			this.disappearTimer = 1;
		} else if (id == EVENT_DISAPPEAR) {
			this.doLeafEffects();
		}
	}

	private void doLeafEffects() {
		if (this.level().isClientSide()) {
			int leafCount = 40;
			double x = this.getX();
			double y = this.getY() + 1.3D;
			double z = this.getZ();
			while (leafCount-- > 0) {
				float dx = this.level().getRandom().nextFloat() - 0.5F;
				float dy = this.level().getRandom().nextFloat() - 0.1F;
				float dz = this.level().getRandom().nextFloat() - 0.5F;
				float mag = 0.08F + this.level().getRandom().nextFloat() * 0.07F;
				TheBetweenlands.createParticle(ParticleRegistry.WEEDWOOD_LEAF.get(), this.level(), x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("type", this.getGreeblingType());
		compound.putInt("facing", this.getEntityData().get(FACING).get2DDataValue());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setGreeblingType(compound.getInt("type"));
		this.getEntityData().set(FACING, Direction.from2DDataValue(compound.getInt("facing")));
	}

	@Override
	public void knockback(double strength, double x, double z) {

	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void pushEntities() {

	}

	@Override
	public void push(Entity entity) {

	}

	@Override
	public SoundEvent getMusicFile(Player listener) {
		return null;
	}

	@Override
	public EntitySoundInstance getMusicSound(Player listener) {
		return new GreeblingMusicInstance(this.getGreeblingType(), this, 0.75F);
	}

	@Override
	public double getMusicRange(Player listener) {
		return 40.0D;
	}

	@Override
	public boolean isMusicActive(Player listener) {
		return this.isAlive();
	}

	@Override
	public int getMusicLayer(Player listener) {
		return this.getGreeblingType() == 0 ? EntityMusicLayers.GREEBLING_1 : EntityMusicLayers.GREEBLING_2;
	}

	@Override
	public boolean canInterruptOtherEntityMusic(Player listener) {
		return false;
	}

	public void setFacing(Direction facing) {
		this.getEntityData().set(FACING, facing);
	}
}
