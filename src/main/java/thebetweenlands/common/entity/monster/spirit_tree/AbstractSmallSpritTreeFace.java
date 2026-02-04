package thebetweenlands.common.entity.monster.spirit_tree;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.WorldStorageGetter;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;

import java.util.ArrayList;
import java.util.List;

public class AbstractSmallSpritTreeFace extends AbstractSpiritTreeFace implements IEntityWithComplexSpawn, VariantHolder<Integer> {

	private int variant;
	private static final EntityDimensions ANCHORED_DIMENSIONS = EntityDimensions.scalable(0.9F, 0.9F);
	private static final EntityDimensions UNANCHORED_DIMENSIONS = EntityDimensions.scalable(0.9F, 0.2F);

	public AbstractSmallSpritTreeFace(EntityType<? extends  AbstractSmallSpritTreeFace> type, Level level) {
		super(type, level);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new SpiritTreeTrackTargetGoal(this, true, 28.0D));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, true));
		this.goalSelector.addGoal(2, new SpitGoal(this, 3.0F));
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
	}

	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		this.setVariant(this.getRandom().nextInt(2));
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	@Override
	public List<BlockPos> findNearbyBlocksForMovement() {
		IWorldStorage storage = WorldStorageGetter.getNullable(this.level());
		if (storage != null) {
			List<LocationSpiritTree> locations = storage.getLocalStorageHandler().getLocalStorages(this.level(), LocationSpiritTree.class, this.getBoundingBox(), loc -> loc.isInside(this));
			if (!locations.isEmpty()) {
				List<BlockPos> positions = new ArrayList<>(locations.getFirst().getSmallFacePositions());
				if (!positions.isEmpty()) {
					return positions;
				}
			}
		}
		return super.findNearbyBlocksForMovement();
	}

	@Override
	protected EntityDimensions getDefaultDimensions(Pose pose) {
		return this.isAnchored() ? ANCHORED_DIMENSIONS : UNANCHORED_DIMENSIONS;
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (key.equals(ANCHORED)) {
			this.refreshDimensions();
		}
		super.onSyncedDataUpdated(key);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("variant", this.getVariant());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setVariant(tag.getInt("variant"));
	}

	@Override
	public void setVariant(Integer variant) {
		this.variant = variant;
	}

	@Override
	public Integer getVariant() {
		return this.variant;
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
		buffer.writeInt(this.variant);
	}

	@Override
	public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
		this.variant = additionalData.readInt();
	}

	@Override
	protected void playSpitSound() {
		this.playSound(SoundRegistry.SPIRIT_TREE_FACE_SMALL_SPIT.get(), 1, 0.8F + this.getRandom().nextFloat() * 0.3F);
	}

	@Override
	protected void playEmergeSound() {
		this.playSound(SoundRegistry.SPIRIT_TREE_FACE_SMALL_EMERGE.get(), 1, 0.8F + this.getRandom().nextFloat() * 0.3F);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SPIRIT_TREE_FACE_SMALL_LIVING.get();
	}

	@Override
	protected float getSoundVolume() {
		return super.getSoundVolume() * 0.5F;
	}

	@Override
	public float getVoicePitch() {
		return super.getVoicePitch() * 1.3F;
	}
}
