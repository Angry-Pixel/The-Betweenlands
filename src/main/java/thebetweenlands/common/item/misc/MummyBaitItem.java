package thebetweenlands.common.item.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.tags.BLBiomeTagProvider;

public class MummyBaitItem extends Item {
	public MummyBaitItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		if (entity.tickCount % 10 == 0) {
			if (entity.onGround()) {
				Level level = entity.level();
				BlockPos pos = entity.blockPosition();
				BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
				Holder<Biome> biome = level.getBiome(mutable.set(entity.blockPosition()));
				if (biome.is(BLBiomeTagProvider.DREADFUL_PEAT_MUMMY_SUMMONABLE)) {
					boolean canSpawn = true;
					for (int yo = -3; yo <= -1; yo++) {
						for (int xo = -1; xo <= 1 && canSpawn; xo++) {
							for (int zo = -1; zo <= 1 && canSpawn; zo++) {
								BlockState state = level.getBlockState(mutable.set(pos.offset(xo, yo, zo)));
								if (!state.isRedstoneConductor(level, mutable) && !Block.isFaceFull(state.getCollisionShape(level, mutable), Direction.UP))
									canSpawn = false;
							}
						}
					}
					if (canSpawn) {
						if (level.isClientSide()) {
							for (int xo = -1; xo <= 1; xo++) {
								for (int zo = -1; zo <= 1; zo++) {
									BlockState state = level.getBlockState(mutable.set(pos.offset(xo, -1, zo)));
									for (int i = 0, amount = 12 + level.getRandom().nextInt(20); i < amount; i++) {
										double ox = level.getRandom().nextDouble();
										double oy = level.getRandom().nextDouble() * 3;
										double oz = level.getRandom().nextDouble();
										double motionX = level.getRandom().nextDouble() * 0.2 - 0.1;
										double motionY = level.getRandom().nextDouble() * 0.1 + 0.1;
										double motionZ = level.getRandom().nextDouble() * 0.2 - 0.1;
										level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), mutable.getX() + ox, mutable.getY(), mutable.getZ() + oz, motionX, motionY, motionZ);
										TheBetweenlands.createParticle(ParticleTypes.SMOKE, level, mutable.getX() + ox, mutable.getY() + oy, mutable.getZ() + oz, ParticleFactory.ParticleArgs.get().withColor(-1, 0xDEAD, 0xC0DE, 1).withMotion(0, 0.25F, 0).withScale(1));
									}
								}
							}
						} else {
//							DreadfulPeatMummy boss = new DreadfulPeatMummy(EntityRegistry.DREADFUL_PEAT_MUMMY.get(), level);
//							boss.moveTo(entity.getX(), entity.getY(), entity.getZ(), 0, 0);
//							if (EventHooks.finalizeMobSpawn(boss, (ServerLevelAccessor) level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null)) {
//								level.addFreshEntity(boss);
//								entity.discard();
//								return true;
//							}
						}
					}
				}
			}
		}
		return false;
	}
}
