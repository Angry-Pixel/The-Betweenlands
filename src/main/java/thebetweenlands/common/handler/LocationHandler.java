package thebetweenlands.common.handler;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationCragrockTower;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.util.config.ConfigHandler;

import java.util.ArrayList;
import java.util.List;

public class LocationHandler {

	public static List<LocationStorage> getLocations(Entity entity) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(entity.world);
		return worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, entity.posX, entity.posZ, location -> location.isInside(entity));
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == Phase.END) {
			EntityPlayer player = event.player;

			if(player != null && !player.world.isRemote) {
				if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).world.provider.getDimension() == ConfigHandler.dimensionId) {
					if (player.posY < WorldProviderBetweenlands.CAVE_START - 10) {
						AdvancementCriterionRegistry.LOCATION.trigger((EntityPlayerMP) player, "caverns");
					} else {
						AdvancementCriterionRegistry.LOCATION.trigger((EntityPlayerMP) player, "wilderness");
					}
				}

				List<LocationStorage> locations = getLocations(player);
				for(LocationStorage loc : locations) {
					if (player instanceof EntityPlayerMP) {
						AdvancementCriterionRegistry.LOCATION.trigger((EntityPlayerMP) player, loc.getName());
					}
					if (loc instanceof LocationCragrockTower && !player.isCreative()) {
						LocationCragrockTower location = (LocationCragrockTower) loc;
						BlockPos structurePos = location.getStructurePos();

						if (!location.wasEntered()) {
							location.setEntered(true);
						}

						if (location.getInnerBoundingBox().contains(player.getPositionVector()) && player.posY - structurePos.getY() >= 45) {
							if (!location.isTopReached()) {
								location.setTopReached(true);
								if (player instanceof EntityPlayerMP) {
									AdvancementCriterionRegistry.CRAGROCK_TOP.trigger((EntityPlayerMP) player);
								}
							}
						} else if (!location.isTopReached() && !location.getInnerBoundingBox().grow(0.5D, 0.5D, 0.5D).contains(player.getPositionVector()) && player.posY - structurePos.getY() > 12) {
							//Player trying to bypass tower, teleport to entrance

							player.dismountRidingEntity();
							if (player instanceof EntityPlayerMP) {
								EntityPlayerMP playerMP = (EntityPlayerMP) player;
								playerMP.connection.setPlayerLocation(structurePos.getX() + 0.5D, structurePos.getY(), structurePos.getZ() + 0.5D, player.rotationYaw, player.rotationPitch);
							} else {
								player.setLocationAndAngles(structurePos.getX() + 0.5D, structurePos.getY(), structurePos.getZ() + 0.5D, player.rotationYaw, player.rotationPitch);
							}
							player.fallDistance = 0.0F;
							player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60, 2));
							player.world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.FORTRESS_BOSS_TELEPORT, SoundCategory.AMBIENT, 1, 1);
						} else if (location.isTopReached() && player.posY - structurePos.getY() <= 42 && !location.isCrumbling() && location.getCrumblingTicks() == 0) {
							location.setCrumbling(true);
							location.restoreBlockade(4);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if(player != null && !event.getWorld().isRemote) {
			BlockPos pos = event.getPos();
			IBlockState blockState = event.getState();

			if(blockState.getBlock() == BlockRegistry.MOB_SPAWNER) {
				BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(event.getWorld());
				List<LocationCragrockTower> towers = worldStorage.getLocalStorageHandler().getLocalStorages(LocationCragrockTower.class, pos.getX(), pos.getZ(), location -> location.isInside(pos));

				for(LocationCragrockTower tower : towers) {
					int level = tower.getLevel(pos.getY());

					if(level != -1) {
						tower.setSpawnerState(level, false);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBlockPlace(BlockEvent.PlaceEvent event) {
		EntityPlayer player = event.getPlayer();
		if(!player.isCreative()) {
			List<BlockPos> positions = new ArrayList<BlockPos>();
			if(event instanceof MultiPlaceEvent) {
				MultiPlaceEvent multiPlaceEvent = (MultiPlaceEvent) event;
				for(BlockSnapshot snapshot : multiPlaceEvent.getReplacedBlockSnapshots()) {
					positions.add(snapshot.getPos());
				}
			} else {
				positions.add(event.getPos());
			}
			for(BlockPos pos : positions) {
				List<LocationStorage> locations = LocationStorage.getLocations(player.world, new Vec3d(pos));
				for(LocationStorage location : locations) {
					if(location != null && location.getGuard() != null && location.getGuard().isGuarded(player.world, player, pos)) {
						event.setCanceled(true);
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBlockRightClick(RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		if(!player.isCreative() && !event.getItemStack().isEmpty() && Block.getBlockFromItem(event.getItemStack().getItem()) != Blocks.AIR) {
			BlockPos resultingPos = event.getPos();
			IBlockState blockState = player.world.getBlockState(resultingPos);
			if(!blockState.getBlock().isReplaceable(player.world, resultingPos)) {
				resultingPos = resultingPos.offset(event.getFace());
			}
			List<LocationStorage> locations = LocationStorage.getLocations(player.world, new Vec3d(resultingPos));
			for(LocationStorage location : locations) {
				if(location != null && location.getGuard() != null && location.getGuard().isGuarded(player.world, player, resultingPos)) {
					event.setUseItem(Result.DENY);
					if(event.getWorld().isRemote) {
						Vec3d hitVec = event.getHitVec();
						BLParticles.BLOCK_PROTECTION.spawn(event.getWorld(), hitVec.x + event.getFace().getFrontOffsetX() * 0.025F, hitVec.y + event.getFace().getFrontOffsetY() * 0.025F, hitVec.z + event.getFace().getFrontOffsetZ() * 0.025F, ParticleArgs.get().withData(event.getFace()));
					}
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		EntityPlayer player = event.getEntityPlayer();
		List<LocationStorage> locations = LocationStorage.getLocations(player.world, new Vec3d(event.getPos()));
		for(LocationStorage location : locations) {
			if(location != null && location.getGuard() != null && location.getGuard().isGuarded(player.world, player, event.getPos())) {
				event.setNewSpeed(0.0F);
				event.setCanceled(true);
				return;
			}
		}
	}

	@SubscribeEvent
	public static void onExplosion(ExplosionEvent.Detonate event) {
		Explosion explosion = event.getExplosion();
		World world = event.getWorld();
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);

		Long2ObjectMap<List<LocationStorage>> locationCache = new Long2ObjectOpenHashMap<List<LocationStorage>>();
		List<LocationStorage> affectedLocations = new ArrayList<LocationStorage>();

		for(BlockPos pos : explosion.getAffectedBlockPositions()) {
			long chunkId = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
			List<LocationStorage> locations = locationCache.get(chunkId);

			if(locations == null) {
				locations = worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, pos.getX(), pos.getZ(), storage -> storage.getGuard() != null);
				locationCache.put(chunkId, locations);
			}

			for(LocationStorage location : locations) {
				if(location.getGuard().isGuarded(world, explosion.getExplosivePlacedBy(), pos) && !affectedLocations.contains(location)) {
					affectedLocations.add(location);
				}
			}
		}

		for(LocationStorage location : affectedLocations) {
			location.getGuard().handleExplosion(world, explosion);
		}
	}

	@SubscribeEvent
	public static void onLeftClickBlock(LeftClickBlock event) {
		if(!event.getEntityPlayer().isCreative()) {
			List<LocationStorage> locations = LocationStorage.getLocations(event.getWorld(), new Vec3d(event.getPos()));
			for(LocationStorage location : locations) {
				if(location != null && location.getGuard() != null && location.getGuard().isGuarded(event.getWorld(), event.getEntityPlayer(), event.getPos())) {
					if(event.getWorld().isRemote && event.getEntityPlayer().swingProgressInt == 0) {
						Vec3d hitVec = event.getHitVec();
						BLParticles.BLOCK_PROTECTION.spawn(event.getWorld(), hitVec.x + event.getFace().getFrontOffsetX() * 0.025F, hitVec.y + event.getFace().getFrontOffsetY() * 0.025F, hitVec.z + event.getFace().getFrontOffsetZ() * 0.025F, ParticleArgs.get().withData(event.getFace()));
					}
					event.setCanceled(true);
					break;
				}
			}
		}
	}
}
