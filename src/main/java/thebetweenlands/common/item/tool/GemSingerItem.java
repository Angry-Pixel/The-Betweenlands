package thebetweenlands.common.item.tool;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import javax.annotation.Nullable;

import thebetweenlands.common.component.item.GemSingerTarget;
import thebetweenlands.common.network.clientbound.SoundRipplePacket;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsChunkStorage;

import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public class GemSingerItem extends Item {
	public enum Target implements Predicate<BlockState>, StringRepresentable {
		AQUA_MIDDLE_GEM(state -> state.is(BlockRegistry.AQUA_MIDDLE_GEM_ORE)),
		CRIMSON_MIDDLE_GEM(state -> state.is(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE)),
		GREEN_MIDDLE_GEM(state -> state.is(BlockRegistry.GREEN_MIDDLE_GEM_ORE)),
		LIFE_CRYSTAL(state -> state.is(BlockRegistry.LIFE_CRYSTAL_ORE_STALACTITE));

		public static final StringRepresentable.EnumCodec<Target> CODEC = StringRepresentable.fromEnum(Target::values);
		public static final IntFunction<Target> BY_ID = ByIdMap.continuous(Target::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
		public static final StreamCodec<ByteBuf, Target> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Target::ordinal);

		private final Predicate<BlockState> predicate;

		Target(Predicate<BlockState> predicate) {
			this.predicate = predicate;
		}

		@Override
		public boolean test(BlockState state) {
			return this.predicate.test(state);
		}

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}

	public GemSingerItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if(!level.isClientSide()) {
			ItemStack stack = player.getItemInHand(hand);
			if(player.isShiftKeyDown()) {
				this.setTarget(stack, null, null);
			} else {
				final int chunkRange = 6;
				final int maxDelay = 80;
				final int attempts = 64;

				BlockPos gem = this.getTargetPosition(stack);

				if(gem != null) {
					Target target = this.getTargetType(stack);

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
								EnumMap<Target, BlockPos> foundGems = new EnumMap<>(Target.class);

								for(Target target : Target.values()) {
									BlockPos foundGem = storage.findRandomGem(target, level.getRandom(), player.blockPosition(), chunkRange * 16);

									if(foundGem != null) {
										foundGems.put(target, foundGem);
									}
								}

								if(!foundGems.isEmpty()) {
									List<Map.Entry<Target, BlockPos>> foundGemEntries = new ArrayList<>(foundGems.entrySet());
									Map.Entry<Target, BlockPos> picked = foundGemEntries.get(level.getRandom().nextInt(foundGemEntries.size()));

									gem = picked.getValue();
									this.setTarget(stack, gem, picked.getKey());
									this.spawnEffect(player, gem, chunkRange * 16, maxDelay);

									stack.hurtAndBreak(1, player, Player.getSlotForHand(hand));
									break;
								}
							}
						}
					}
				} else {
					this.spawnEffect(player, gem, chunkRange * 16, maxDelay);
					stack.hurtAndBreak(1, player, Player.getSlotForHand(hand));
				}

				player.getCooldowns().addCooldown(stack.getItem(), 60);
			}
		}

		if(level.isClientSide() && !player.isShiftKeyDown()) {
			level.playSound(player, player.blockPosition(), SoundRegistry.GEM_SINGER.get(), SoundSource.PLAYERS, 2, 1);
		}

		return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
	}

	protected void spawnEffect(Player player, BlockPos target, int maxRangeBlocks, int maxDelay) {
		if(player instanceof ServerPlayer sp) {
			int delay = Math.min((int)(Math.sqrt(player.distanceToSqr(Vec3.atCenterOf(target))) / (float)maxRangeBlocks * maxDelay), maxDelay);
			PacketDistributor.sendToPlayer(sp, new SoundRipplePacket(target, delay));
		}
	}

	protected void setTarget(ItemStack stack, @Nullable BlockPos pos, @Nullable Target target) {
		if(pos != null && target != null) {
			stack.set(DataComponentRegistry.GEM_SINGER_TARGET, new GemSingerTarget(pos, target));
		} else if(stack.has(DataComponentRegistry.GEM_SINGER_TARGET)) {
			stack.remove(DataComponentRegistry.GEM_SINGER_TARGET);
		}
	}

	@Nullable
	protected BlockPos getTargetPosition(ItemStack stack) {
		if(stack.has(DataComponentRegistry.GEM_SINGER_TARGET)) {
			return stack.get(DataComponentRegistry.GEM_SINGER_TARGET).pos();
		}
		return null;
	}

	@Nullable
	protected Target getTargetType(ItemStack stack) {
		if(stack.has(DataComponentRegistry.GEM_SINGER_TARGET)) {
			return stack.get(DataComponentRegistry.GEM_SINGER_TARGET).target();
		}
		return null;
	}
}
