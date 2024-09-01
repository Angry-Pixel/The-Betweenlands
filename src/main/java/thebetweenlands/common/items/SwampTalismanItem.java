package thebetweenlands.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.PortalFrameBlock;
import thebetweenlands.common.block.TreePortalBlock;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.gen.feature.tree.PortalTree;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationPortal;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class SwampTalismanItem extends Item {

	public SwampTalismanItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction facing = context.getClickedFace();
		Player player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		if (!player.mayUseItemAt(pos, facing, stack)) {
			return InteractionResult.FAIL;
		} else {
			BlockState state = level.getBlockState(pos);
			if (this.isPortalWood(state)) {
				BlockPos offsetPos = pos.relative(facing);
				for (int yo = 3; yo > 0; yo--) {
					BlockPos portalPos = offsetPos.below(yo);
					Direction frameAxis = this.getPortalWoodFrameAxis(level, portalPos);
					if (frameAxis != null) {
						if (!level.isClientSide()) {
							Direction closestDir = null;
							for (Direction dir : Direction.values()) {
								if (dir.getAxis() == frameAxis.getAxis()) {
									if (closestDir == null || pos.relative(dir).distToCenterSqr(player.position()) <= pos.relative(closestDir).distToCenterSqr(player.position())) {
										closestDir = dir;
									}
								}
							}
							TreePortalBlock.makePortal(level, portalPos.above(), frameAxis);
							if (TreePortalBlock.isPatternValid(level, portalPos.above())) {
								//Only create new location is none exists
								if (this.getPortalAt(level, portalPos.above()) == null) {
									BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.get(level);
									if (worldStorage != null) {
										LocationPortal location = new LocationPortal(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), portalPos.relative(closestDir).below());
										location.addBounds(new AABB(portalPos.above()).inflate(1, 2, 1).expandTowards(0, -0.5D, 0));
										location.setSeed(level.getRandom().nextLong());
										location.setDirty(true);
										location.setVisible(false);
										//worldStorage.getLocalStorageHandler().addLocalStorage(location);
									}
								}

								level.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundRegistry.PORTAL_ACTIVATE.get(), SoundSource.PLAYERS, 0.5F, level.getRandom().nextFloat() * 0.4F + 0.8F);
							}
						}
						return InteractionResult.SUCCESS;
					}
				}
			}

			if (state.is(BlockTags.SAPLINGS)) {
				if (BetweenlandsConfig.returnDimension != player.level().dimension() && player.level().dimension() != DimensionRegistries.DIMENSION_KEY) {
					player.displayClientMessage(Component.translatable("chat.talisman.wrongdimension"), true);
				} else {
					if (PortalTree.place(level, level.getRandom(), pos, null)) {
						level.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundRegistry.PORTAL_ACTIVATE.get(), SoundSource.PLAYERS, 0.5F, level.getRandom().nextFloat() * 0.4F + 0.8F);
						player.moveTo(pos.getX() + 0.5D, pos.getY() + 2D, pos.getZ() + 0.5D, player.getYRot(), player.getXRot());
						if (player instanceof ServerPlayer sp) {
							sp.connection.teleport(pos.getX() + 0.5D, pos.getY() + 2D, pos.getZ() + 0.5D, player.getYRot(), player.getXRot());
						}
					} else {
						player.displayClientMessage(Component.translatable("chat.talisman.noplace"), true);
					}
				}
				return InteractionResult.SUCCESS;
			} else if (!stack.has(DataComponentRegistry.TALISMAN_LINK) && !(player instanceof FakePlayer)) {
				LocationPortal portal = this.getPortalAt(level, pos);
				if (portal != null) {
					if (!level.isClientSide()) {
						stack.set(DataComponentRegistry.TALISMAN_LINK, new GlobalPos(level.dimension(), portal.getPortalPosition()));

						player.displayClientMessage(Component.translatable("chat.talisman.linked"), true);

						level.playSound(null, player.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 0.8F, 0.7F);
					}
					return InteractionResult.SUCCESS;
				}
			} else if (stack.has(DataComponentRegistry.TALISMAN_LINK) && !(player instanceof FakePlayer)) {
				GlobalPos link = stack.get(DataComponentRegistry.TALISMAN_LINK);
				if (!level.isClientSide()) {
					stack.remove(DataComponentRegistry.TALISMAN_LINK);

					level.playSound(null, player.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 0.8F, 0.7F);
				}

				BlockPos otherPortalPos = link.pos();
				LocationPortal portal = this.getPortalAt(level, pos);
				if (portal != null) {
					if (level instanceof ServerLevel server) {
						ResourceKey<Level> linkDim = link.dimension();
						if (linkDim != level.dimension() && (linkDim == DimensionRegistries.DIMENSION_KEY || level.dimension() == DimensionRegistries.DIMENSION_KEY)) {
							ServerLevel otherWorld = server.getServer().getLevel(linkDim);
							if (otherWorld != null) {
								double moveFactor = DimensionType.getTeleportationScale(otherWorld.dimensionType(), level.dimensionType());
								if (portal.getPortalPosition().getCenter().distanceTo(new Vec3(otherPortalPos.getX() * moveFactor, portal.getPortalPosition().getY(), otherPortalPos.getZ() * moveFactor)) <= BetweenlandsConfig.portalMaxLinkDist) {
									LocationPortal linkPortal = this.getLinkPortal(otherWorld, otherPortalPos);
									if (linkPortal != null) {
										linkPortal.setOtherPortalPosition(level.dimension(), portal.getPortalPosition());
										portal.setOtherPortalPosition(linkDim, linkPortal.getPortalPosition());
										player.displayClientMessage(Component.translatable("chat.talisman.portal_linked"), true);
									} else {
										player.displayClientMessage(Component.translatable("chat.talisman.cant_link"), true);
									}
								} else {
									player.displayClientMessage(Component.translatable("chat.talisman.too_far", String.valueOf(BetweenlandsConfig.portalMaxLinkDist)), true);
								}
							}
						} else {
							player.displayClientMessage(Component.translatable("chat.talisman.cant_link"), true);
						}
					}
				}
				return InteractionResult.SUCCESS;
			}
		}
		return super.useOn(context);
	}

	@Nullable
	protected LocationPortal getPortalAt(Level level, BlockPos pos) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.get(level);
		if (worldStorage != null) {
//			List<LocationPortal> portals = worldStorage.getLocalStorageHandler().getLocalStorages(LocationPortal.class, pos.getX() + 0.5D, pos.getZ() + 0.5D, location -> location.isInside(pos.getCenter()));
//			if (!portals.isEmpty()) {
//				return portals.getFirst();
//			}
		}
		return null;
	}

	@Nullable
	protected LocationPortal getLinkPortal(ServerLevel level, BlockPos portal2Pos) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.get(level);
		if (worldStorage != null) {
//			List<LocationPortal> portals = worldStorage.getLocalStorageHandler().getLocalStorages(LocationPortal.class, portal2Pos.getX() + 0.5D, portal2Pos.getZ() + 0.5D, location -> location.isInside(portal2Pos.getCenter()) && portal2Pos.equals(location.getPortalPosition()));
//			if (!portals.isEmpty()) {
//				return portals.getFirst();
//			}
		}
		return null;
	}

	@Nullable
	protected Direction getPortalWoodFrameAxis(Level world, BlockPos pos) {
		Direction north = Direction.NORTH;
		Direction south = Direction.SOUTH;
		if (this.isPortalWood(world.getBlockState(pos)) && this.isPortalWood(world.getBlockState(pos.relative(north))) && this.isPortalWood(world.getBlockState(pos.relative(south)))
			&& this.isPortalWood(world.getBlockState(pos.above().relative(north))) && this.isPortalWood(world.getBlockState(pos.above().relative(south)))
			&& this.isPortalWood(world.getBlockState(pos.above(2).relative(north))) && this.isPortalWood(world.getBlockState(pos.above(2).relative(south)))
			&& this.isPortalWood(world.getBlockState(pos.above(3))) && this.isPortalWood(world.getBlockState(pos.above(3).relative(north))) && this.isPortalWood(world.getBlockState(pos.above(3).relative(south)))) {
			return Direction.WEST;
		}

		Direction east = Direction.EAST;
		Direction west = Direction.WEST;
		if (this.isPortalWood(world.getBlockState(pos)) && this.isPortalWood(world.getBlockState(pos.relative(east))) && this.isPortalWood(world.getBlockState(pos.relative(west)))
			&& this.isPortalWood(world.getBlockState(pos.above().relative(east))) && this.isPortalWood(world.getBlockState(pos.above().relative(west)))
			&& this.isPortalWood(world.getBlockState(pos.above(2).relative(east))) && this.isPortalWood(world.getBlockState(pos.above(2).relative(west)))
			&& this.isPortalWood(world.getBlockState(pos.above(3))) && this.isPortalWood(world.getBlockState(pos.above(3).relative(east))) && this.isPortalWood(world.getBlockState(pos.above(3).relative(west)))) {
			return Direction.NORTH;
		}

		return null;
	}

	protected boolean isPortalWood(BlockState state) {
		return state.getBlock() instanceof PortalFrameBlock || state.is(BlockRegistry.PORTAL_LOG);
	}
}
