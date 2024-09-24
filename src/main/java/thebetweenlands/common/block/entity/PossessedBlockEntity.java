package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.block.structure.PossessedBlock;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AnimationMathHelper;

import java.util.List;

public class PossessedBlockEntity extends SyncedBlockEntity {

	public int animationTicks, coolDown;
	public boolean active;
	private final AnimationMathHelper headShake = new AnimationMathHelper();
	public float moveProgress;

	public PossessedBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.POSSESSED_BLOCK.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, PossessedBlockEntity entity) {
		if (!level.isClientSide()) {
			entity.findEnemyToAttack(level, pos, state);
			if (entity.active) {
				entity.activateBlock(level, pos, state);
				if (entity.animationTicks == 0)
					level.playSound(null, pos, SoundRegistry.POSSESSED_SCREAM.get(), SoundSource.BLOCKS, 0.25F, 1.25F - level.getRandom().nextFloat() * 0.5F);
				if (entity.animationTicks <= 24)
					entity.animationTicks++;
				if (entity.animationTicks == 24) {
					entity.setActive(level, pos, state, false);
					entity.coolDown = 200;
				}
			}
			if (!entity.active) {
				if (entity.animationTicks >= 1)
					entity.animationTicks--;
				if (entity.coolDown >= 0)
					entity.coolDown--;
			}
			level.sendBlockUpdated(pos, state, state, 3);
		}
		entity.moveProgress = 1 + entity.headShake.swing(4, 1F, false);
		if (level.isClientSide())
			if (!entity.active && entity.animationTicks % 8 > 0)
				entity.spawnParticles(level, pos, state);
	}

	private void spawnParticles(Level level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(PossessedBlock.FACING);
		float xx = (float) pos.getX() + 0.5F + facing.getStepX();
		float yy = (float) pos.getY() + 0.5F;
		float zz = (float) pos.getZ() + 0.5F + facing.getStepZ();
		float randomOffset = level.getRandom().nextFloat() * 0.6F - 0.3F;
//		BLParticles.SMOKE.spawn(level, (double) (xx - randomOffset), (double) (yy + randomOffset), (double) (zz + randomOffset));
//		BLParticles.SMOKE.spawn(level, (double) (xx + randomOffset), (double) (yy - randomOffset), (double) (zz + randomOffset));
//		BLParticles.SMOKE.spawn(level, (double) (xx + randomOffset), (double) (yy + randomOffset), (double) (zz - randomOffset));
//		BLParticles.SMOKE.spawn(level, (double) (xx + randomOffset), (double) (yy - randomOffset), (double) (zz + randomOffset));
	}

	public void setActive(Level level, BlockPos pos, BlockState state, boolean isActive) {
		this.active = isActive;
		level.sendBlockUpdated(pos, state, state, 3);
	}

	protected void findEnemyToAttack(Level level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(PossessedBlock.FACING);
		float x = facing.getStepX() * 1.25F;
		float z = facing.getStepZ() * 1.25F;
		List<Player> list = level.getEntitiesOfClass(Player.class, new AABB(pos.relative(facing)).expandTowards(x + 1, 1, z + 1));
		if (!list.isEmpty()) {
			if (!this.active && this.animationTicks == 0 && this.coolDown <= 0) {
				this.setActive(level, pos, state, true);
			}
		}
	}

	protected void activateBlock(Level level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(PossessedBlock.FACING);
		float x = facing.getStepX() * 1.25F;
		float z = facing.getStepZ() * 1.25F;
		List<Player> list = level.getEntitiesOfClass(Player.class, new AABB(pos.relative(facing)).expandTowards(x + 1, 1, z + 1));
		if (this.animationTicks == 1)
			for (Player player : list) {
				int knockback = 4;
				player.addDeltaMovement(new Vec3(Mth.sin(player.getYRot() * Mth.DEG_TO_RAD) * knockback * 0.2F, 0.3D, -Mth.cos(player.getYRot() * Mth.DEG_TO_RAD) * knockback * 0.2F));
				player.hurt(level.damageSources().generic(), 2);
			}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("animation_ticks", this.animationTicks);
		tag.putBoolean("active", this.active);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.animationTicks = tag.getInt("animation_ticks");
		this.active = tag.getBoolean("active");
	}
}
