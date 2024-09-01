package thebetweenlands.common.world.event;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.common.block.BLSnowLayerBlock;
import thebetweenlands.common.block.entity.PresentBlockEntity;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class WinterEvent extends SeasonalEnvironmentEvent {

	private static final long WINTER_DATE = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.DECEMBER, 24, 0, 0).getTime().getTime();

	@Override
	public long getStartDateInMs() {
		return WINTER_DATE;
	}

	@Override
	public int getDurationInDays() {
		return 7;
	}

	public static boolean isFroooosty(Level level) {
		if (level != null) {
//			WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(level);
//			if(provider != null) {
//				return provider.getEnvironmentEventRegistry().winter.isActive();
//			}
		}
		return false;
	}

	@Override
	public void setActive(Level level, boolean active) {
		//Mark blocks in range for render update to update block textures
		if (active != this.isActive() && level.isClientSide() && BetweenlandsClient.getClientPlayer() != null) {
			updateModelActiveState(active);

			Player player = BetweenlandsClient.getClientPlayer();
			int px = Mth.floor(player.getX()) - 256;
			int py = Mth.floor(player.getY()) - 256;
			int pz = Mth.floor(player.getZ()) - 256;
			Minecraft.getInstance().levelRenderer.setBlocksDirty(px, py, pz, px + 512, py + 512, pz + 512);
		}

		super.setActive(level, active);
	}

	@Override
	public void tick(Level level) {
		super.tick(level);

		if (!level.isClientSide() && this.isActive()) {
			if (level instanceof ServerLevel server && level.getRandom().nextInt(10) == 0) {
				for (ChunkHolder chunkholder : server.getChunkSource().chunkMap.getChunks()) {
					LevelChunk levelchunk = chunkholder.getTickingChunk();
					if (levelchunk != null) {
						int cbx = level.getRandom().nextInt(16);
						int cbz = level.getRandom().nextInt(16);
						BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, levelchunk.getPos().getWorldPosition().offset(cbx, 0, cbz)).below();
						if (level.isEmptyBlock(pos.above()) && level.getBlockState(pos).is(BlockRegistry.SWAMP_WATER)) {
							if (level.getRandom().nextInt(3) == 0) {
								boolean hasSuitableNeighbourBlock = false;
								BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
								for (Direction dir : Direction.Plane.HORIZONTAL) {
									checkPos.set(pos.getX() + dir.getStepX(), pos.getY(), pos.getZ() + dir.getStepZ());
									if (level.isLoaded(checkPos)) {
										if (!hasSuitableNeighbourBlock) {
											BlockState neighourState = level.getBlockState(checkPos);
											if (neighourState.is(BlockRegistry.BLACK_ICE) || neighourState.isFaceSturdy(level, checkPos, dir.getOpposite())) {
												hasSuitableNeighbourBlock = true;
											}
										}
									} else {
										hasSuitableNeighbourBlock = false;
										break;
									}
								}
								if (hasSuitableNeighbourBlock) {
									level.setBlockAndUpdate(pos, BlockRegistry.BLACK_ICE.get().defaultBlockState());
								}
							}
						} else if (level.getRandom().nextInt(3000) == 0) {
							BlockState state = level.getBlockState(pos);
							if (state.is(BlockTags.LEAVES)) {
								BlockPos offsetPos = pos;

								for (int i = 0; i < 6; i++) {
									offsetPos = offsetPos.below();
									state = level.getBlockState(offsetPos);

									if (!state.is(BlockTags.LEAVES)) {
										if (level.isEmptyBlock(offsetPos) && level.getBrightness(LightLayer.BLOCK, offsetPos) == 0 &&
											(state.isFaceSturdy(level, offsetPos.above(), Direction.DOWN) || Block.canSupportCenter(level, offsetPos.above(), Direction.DOWN))) {
//											level.setBlockAndUpdate(offsetPos, BlockRegistry.BAUBLE.getDefaultState().setValue(BlockBauble.DIAGONAL, level.getRandom().nextBoolean()).setValue(BlockBauble.COLOR, level.getRandom().nextInt(8)));
										}

										break;
									}
								}
							}
						}

						if (level.getRandom().nextInt(3000) == 0 && level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 64.0D, false) == null) {
							BlockState stateAbove = level.getBlockState(pos.above());
							if (stateAbove.isFaceSturdy(level, pos, Direction.UP)) {
								if (stateAbove.isAir() || (stateAbove.getBlock() instanceof BLSnowLayerBlock && stateAbove.getValue(BLSnowLayerBlock.LAYERS) <= 5)) {
									level.setBlockAndUpdate(pos.above(), BuiltInRegistries.BLOCK.getOrCreateTag(BLBlockTagProvider.PRESENTS).getRandomElement(level.getRandom()).get().value().defaultBlockState());
									BlockEntity tile = level.getBlockEntity(pos.above());
									if (tile instanceof PresentBlockEntity present) {
										present.setLootTable(LootTableRegistry.PRESENT, level.getRandom().nextLong());
										present.setChanged();
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected void showStatusMessage(Player player) {
		player.displayClientMessage(Component.translatable("chat.event.winter"), true);
	}

//	public static void onClientTick(ClientTickEvent event) {
//		World world = Minecraft.getMinecraft().world;
//		if (world != null && world.provider instanceof WorldProviderBetweenlands) {
//			updateModelActiveState(((WorldProviderBetweenlands) world.provider).getEnvironmentEventRegistry().winter.isActive());
//		} else {
//			updateModelActiveState(false);
//		}
//	}

	private static void updateModelActiveState(boolean active) {
//		ModelRegistry.WINTER_EVENT.setActive(active);
	}

	//TODO
//	public static void onUpdateFog(UpdateFogEvent event) {
//		World world = event.getWorld();
//		if (world.provider instanceof WorldProviderBetweenlands && ((WorldProviderBetweenlands) world.provider).getEnvironmentEventRegistry().winter.isActive()) {
//			Fog targetFog = event.getAmbientFog();
//			float interp = (float) MathHelper.clamp((Minecraft.getMinecraft().player.posY - WorldProviderBetweenlands.CAVE_START + 10) / 10.0F, 0.0F, 1.0F);
//			float snowingStrength = ((WorldProviderBetweenlands) world.provider).getEnvironmentEventRegistry().snowfall.getSnowingStrength();
//			FogState state = event.getFogState();
//			Fog.MutableFog newFog = new Fog.MutableFog(event.getAmbientFog());
//			float newStart = Math.min(2.0F + event.getFarPlaneDistance() * 0.8F / (1.0F + snowingStrength), targetFog.getStart());
//			newFog.setStart(targetFog.getStart() + (newStart - targetFog.getStart()) * interp);
//			float newEnd = Math.min(8.0F + event.getFarPlaneDistance() / (1.0F + snowingStrength * 0.5F), targetFog.getEnd());
//			newFog.setEnd(targetFog.getEnd() + (newEnd - targetFog.getEnd()) * interp);
//			float fogBrightness = MathHelper.clamp(0.8F / 4.0F * snowingStrength, 0.5F, 0.8F);
//			newFog.setColor(
//				targetFog.getRed() + (fogBrightness - targetFog.getRed()) * interp,
//				targetFog.getGreen() + (fogBrightness - targetFog.getGreen()) * interp,
//				targetFog.getBlue() + (fogBrightness - targetFog.getBlue()) * interp
//			);
//			newFog.setColorIncrement(Math.max(targetFog.getColorIncrement(), 0.008F));
//			state.setTargetFog(newFog.toImmutable());
//		}
//	}
}
