package thebetweenlands.common.items;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.storage.BetweenlandsChunkStorage;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class GemSingerItem extends Item {
	public enum GemSingerTarget implements Predicate<BlockState> {
		AQUA_MIDDLE_GEM(0, state -> state.is(BlockRegistry.AQUA_MIDDLE_GEM_ORE)),
		CRIMSON_MIDDLE_GEM(1, state -> state.is(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE)),
		GREEN_MIDDLE_GEM(2, state -> state.is(BlockRegistry.GREEN_MIDDLE_GEM_ORE)),
		LIFE_CRYSTAL(3, state -> state.is(BlockRegistry.LIFE_CRYSTAL_STALACTITE) && state.getValue(BlockLifeCrystalStalactite.VARIANT) == EnumLifeCrystalType.ORE);

		private final int id;
		private final Predicate<BlockState> predicate;

		GemSingerTarget(int id, Predicate<BlockState> predicate) {
			this.id = id;
			this.predicate = predicate;
		}

		public int getId() {
			return this.id;
		}

		@Nullable
		public static GemSingerTarget byId(int id) {
			for(GemSingerTarget target : values()) {
				if(target.id == id) {
					return target;
				}
			}
			return null;
		}

		@Override
		public boolean test(BlockState state) {
			return this.predicate.test(state);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if(!level.isClientSide()) {
			ItemStack stack = player.getItemInHand(hand);
			if(player.isCrouching()) {
				this.setTarget(stack, null, null);
			} else {
				final int chunkRange = 6;
				final int maxDelay = 80;
				final int attempts = 64;

				BlockPos gem = this.getTargetPosition(stack);

				if(gem != null) {
					GemSingerTarget target = this.getTargetType(stack);

					boolean valid = false;

					if(target != null) {
						ChunkAccess chunk = level.getChunkSource().getChunkNow(gem.getX() >> 4, gem.getZ() >> 4);
						if(chunk != null) {
							BetweenlandsChunkStorage storage = BetweenlandsChunkStorage.forChunk(level, chunk);
							if(storage != null) {
								IntSet gems = storage.findGems(target);
								if(gems.contains(BetweenlandsChunkStorage.getGemSingerTargetIndex(gem.getX(), gem.getY(), gem.getZ()))) {
									valid = true;
								}
							}
						}
					}

					if(!valid) {
						this.setTarget(stack, null, null);
						gem = null;
					}
				}

				if(gem == null) {
					for(int i = 0; i < attempts; i++) {
						ChunkAccess chunk = level.getChunkSource().getChunkNow((Mth.floor(player.getX()) >> 4) + level.getRandom().nextInt(chunkRange * 2 + 1) - chunkRange, (Mth.floor(player.getZ()) >> 4) + level.getRandom().nextInt(chunkRange * 2 + 1) - chunkRange);

						if(chunk != null) {
							BetweenlandsChunkStorage storage = BetweenlandsChunkStorage.forChunk(level, chunk);

							if(storage != null) {
								EnumMap<GemSingerTarget, BlockPos> foundGems = new EnumMap<>(GemSingerTarget.class);

								for(GemSingerTarget target : GemSingerTarget.values()) {
									BlockPos foundGem = storage.findRandomGem(target, level.getRandom(), player.blockPosition(), chunkRange * 16);

									if(foundGem != null) {
										foundGems.put(target, foundGem);
									}
								}

								if(!foundGems.isEmpty()) {
									List<Map.Entry<GemSingerTarget, BlockPos>> foundGemEntries = new ArrayList<>(foundGems.entrySet());
									Map.Entry<GemSingerTarget, BlockPos> picked = foundGemEntries.get(level.getRandom().nextInt(foundGemEntries.size()));

									gem = picked.getValue();
									this.setTarget(stack, gem, picked.getKey());
									this.spawnEffect(player, gem, chunkRange * 16, maxDelay);

									stack.hurtAndBreak(1, player, hand);
									break;
								}
							}
						}
					}
				} else {
					this.spawnEffect(player, gem, chunkRange * 16, maxDelay);
					stack.hurtAndBreak(1, player, hand);
				}

				player.getCooldowns().addCooldown(stack.getItem(), 60);
			}
		}

		if(level.isClientSide() && !player.isCrouching()) {
			level.playSound(player, player.blockPosition(), SoundRegistry.GEM_SINGER, SoundSource.PLAYERS, 2, 1);
		}

		player.swing(hand);

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
	}

	protected void spawnEffect(Player player, BlockPos target, int maxRangeBlocks, int maxDelay) {
		if(player instanceof ServerPlayer) {
			int delay = Math.min((int)(Math.sqrt(player.distanceToSqr(Vec3.atCenterOf(target))) / (float)maxRangeBlocks * maxDelay), maxDelay);
			TheBetweenlands.networkWrapper.sendTo(new MessageSoundRipple(target, delay), (EntityPlayerMP) player);
		}
	}

	//TODO data component
	protected void setTarget(ItemStack stack, @Nullable BlockPos pos, @Nullable GemSingerTarget target) {
//		if(pos != null && target != null) {
//			CompoundTag nbt = NBTHelper.getStackNBTSafe(stack);
//			nbt.setLong("targetPos", pos.toLong());
//			nbt.setInteger("targetType", target.getId());
//		} else if(stack.getTagCompound() != null) {
//			stack.getTagCompound().removeTag("targetPos");
//			stack.getTagCompound().removeTag("targetType");
//		}
	}

	@Nullable
	protected BlockPos getTargetPosition(ItemStack stack) {
//		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("targetPos", Constants.NBT.TAG_LONG)) {
//			return BlockPos.fromLong(stack.getTagCompound().getLong("targetPos"));
//		}
		return null;
	}

	@Nullable
	protected GemSingerTarget getTargetType(ItemStack stack) {
//		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("targetType", Constants.NBT.TAG_INT)) {
//			return GemSingerTarget.byId(stack.getTagCompound().getInteger("targetType"));
//		}
		return null;
	}
}
