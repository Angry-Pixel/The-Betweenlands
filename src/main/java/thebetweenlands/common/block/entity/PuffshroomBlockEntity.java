package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class PuffshroomBlockEntity extends SyncedBlockEntity {

	public int animation_1 = 0, prev_animation_1 = 0, cooldown = 0;
	public int animation_2 = 0, prev_animation_2 = 0;
	public int animation_3 = 0, prev_animation_3 = 0;
	public int animation_4 = 0, prev_animation_4 = 0;
	public boolean active_1 = false, active_2 = false, active_3 = false, active_4 = false, active_5 = false, pause = true;
	public int renderTicks = 0, prev_renderTicks = 0, pause_count = 30;


	public PuffshroomBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockEntityRegistry.PUFFSHROOM.get(), pos, blockState);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, PuffshroomBlockEntity entity) {
		entity.prev_animation_1 = entity.animation_1;
		entity.prev_animation_2 = entity.animation_2;
		entity.prev_animation_3 = entity.animation_3;
		entity.prev_animation_4 = entity.animation_4;
		entity.prev_renderTicks = entity.renderTicks;

		if (!level.isClientSide() && entity.cooldown <= 0 && level.getGameTime() % 5 == 0)
			entity.findEnemyToAttack(level, pos, state);

		if (entity.active_1 || entity.active_5) {
			if (level.isClientSide()) {
				if (entity.animation_1 < 3) {
					double px = pos.getX() + 0.5D;
					double py = pos.getY() + 1.0625D;
					double pz = pos.getZ() + 0.5D;
					for (int i = 0, amount = 5 + level.getRandom().nextInt(2); i < amount; i++) {
						double ox = level.getRandom().nextDouble() * 0.1F - 0.05F;
						double oz = level.getRandom().nextDouble() * 0.1F - 0.05F;
						double motionX = level.getRandom().nextDouble() * 0.2F - 0.1F;
						double motionY = level.getRandom().nextDouble() * 0.1F + 0.075F;
						double motionZ = level.getRandom().nextDouble() * 0.2F - 0.1F;
						level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockRegistry.MUD_TILES.get().defaultBlockState()), px + ox, py, pz + oz, motionX, motionY, motionZ);

					}
				}
			}
		}

		if (!level.isClientSide()) {
			if (entity.active_4) {
				if (entity.animation_4 <= 1)
					level.playSound(null, pos, SoundRegistry.PUFF_SHROOM.get(), SoundSource.BLOCKS, 0.5F, 0.95F + level.getRandom().nextFloat() * 0.2F);
				if (entity.animation_4 == 10) {
//					SporeJet jet = new SporeJet(level);
//					jet.setPos(pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D);
//					level.addFreshEntity(jet);
				}
			}
		}

		if (entity.active_1) {
			if (entity.animation_1 <= 8)
				entity.animation_1++;
			if (entity.animation_1 > 8) {
				entity.prev_animation_1 = entity.animation_1 = 8;
				entity.active_2 = true;
				entity.active_1 = false;
			}
		}

		if (entity.active_2) {
			if (entity.animation_2 <= 8)
				entity.animation_2++;
			if (entity.animation_2 == 8)
				entity.active_3 = true;
			if (entity.animation_2 > 8) {
				entity.prev_animation_2 = entity.animation_2 = 8;
				entity.active_2 = false;
			}
		}

		if (entity.active_3) {
			if (entity.animation_3 <= 8)
				entity.animation_3++;
			if (entity.animation_3 > 8) {
				entity.prev_animation_3 = entity.animation_3 = 8;
				entity.active_3 = false;
				entity.active_4 = true;
			}
		}

		if (entity.active_4) {
			if (entity.animation_4 <= 12)
				entity.animation_4++;
			if (entity.animation_4 > 12) {
				entity.prev_animation_4 = entity.animation_4 = 12;
				entity.active_4 = false;
			}
		}

		if (entity.pause) {
			if (entity.animation_4 >= 12) {
				if (entity.pause_count > 0)
					entity.pause_count--;
				if (entity.pause_count <= 0) {
					entity.pause = false;
					entity.pause_count = 30;
					entity.active_5 = true;
				}
			}
		}

		if (entity.active_5) {
			entity.prev_animation_4 = entity.animation_4 = 0;
			if (entity.animation_1 >= 0)
				entity.animation_3--;
			if (entity.animation_3 <= 0)
				entity.animation_2--;
			if (entity.animation_2 <= 0)
				entity.animation_1--;
			if (entity.animation_3 <= 0)
				entity.prev_animation_3 = entity.animation_3 = 0;
			if (entity.animation_2 <= 0)
				entity.prev_animation_2 = entity.animation_2 = 0;
			if (entity.animation_1 <= 0) {
				entity.prev_animation_1 = entity.animation_1 = 0;
				entity.active_5 = false;
			}
		}

		if (entity.cooldown >= 0)
			entity.cooldown--;
		if (entity.cooldown < 0)
			entity.cooldown = 0;

		entity.renderTicks++;
	}

	protected void findEnemyToAttack(Level level, BlockPos pos, BlockState state) {
		if (!this.active_1 && this.animation_1 == 0) {
			List<Player> list = level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(2.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
			if (!list.isEmpty()) {
				this.active_1 = true;
				this.cooldown = 120;
				this.pause = true;
				level.sendBlockUpdated(pos, state, state, 2);
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putBoolean("active_1", this.active_1);
		tag.putBoolean("active_2", this.active_2);
		tag.putBoolean("active_3", this.active_3);
		tag.putBoolean("active_4", this.active_4);
		tag.putBoolean("active_5", this.active_5);
		tag.putBoolean("pause", this.pause);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.active_1 = tag.getBoolean("active_1");
		this.active_2 = tag.getBoolean("active_2");
		this.active_3 = tag.getBoolean("active_3");
		this.active_4 = tag.getBoolean("active_4");
		this.active_5 = tag.getBoolean("active_5");
		this.pause = tag.getBoolean("pause");
	}
}
