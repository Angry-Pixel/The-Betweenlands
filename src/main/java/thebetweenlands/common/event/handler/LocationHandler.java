package thebetweenlands.common.event.handler;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.location.LocationCragrockTower;
import thebetweenlands.common.world.storage.world.shared.location.LocationStorage;

public class LocationHandler {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == Phase.END) {
			EntityPlayer player = event.player;

			if(player != null && !player.isCreative() && !player.worldObj.isRemote) {
				BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(player.worldObj);
				List<LocationCragrockTower> locations = worldStorage.getSharedStorageAt(LocationCragrockTower.class, location -> location.isInside(player), player.posX, player.posZ);

				for(LocationCragrockTower location : locations) {
					BlockPos structurePos = location.getStructurePos();

					if(!location.wasEntered()) {
						location.setEntered(true);
					}

					if(location.getInnerBoundingBox().isVecInside(player.getPositionVector()) && player.posY - structurePos.getY() >= 45) {
						if(!location.isTopReached()) {
							location.setTopReached(true);
						}
					} else if(!location.isTopReached() && !location.getInnerBoundingBox().expand(0.5D, 0.5D, 0.5D).isVecInside(player.getPositionVector()) && player.posY - structurePos.getY() > 12) {
						//Player trying to bypass tower, teleport to entrance

						player.dismountRidingEntity();
						if(player instanceof EntityPlayerMP) {
							EntityPlayerMP playerMP = (EntityPlayerMP) player;
							playerMP.connection.setPlayerLocation(structurePos.getX() + 0.5D, structurePos.getY(), structurePos.getZ() + 0.5D, player.rotationYaw, player.rotationPitch);
						} else {
							player.setLocationAndAngles(structurePos.getX() + 0.5D, structurePos.getY(), structurePos.getZ() + 0.5D, player.rotationYaw, player.rotationPitch);
						}
						player.fallDistance = 0.0F;
						player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60, 2));
						player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.FORTRESS_BOSS_TELEPORT, SoundCategory.AMBIENT, 1, 1);
					} else if(location.isTopReached() && player.posY - structurePos.getY() <= 42 && !location.isCrumbling() && location.getCrumblingTicks() == 0) {
						location.setCrumbling(true);
						location.restoreBlockade(4);
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
				BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(event.getWorld());
				List<LocationCragrockTower> towers = worldStorage.getSharedStorageAt(LocationCragrockTower.class, location -> location.isInside(pos), pos.getX(), pos.getZ());

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
				List<LocationStorage> locations = LocationStorage.getLocations(player.worldObj, new Vec3d(pos));
				for(LocationStorage location : locations) {
					if(location != null && location.getGuard() != null && location.getGuard().isGuarded(player.worldObj, player, pos)) {
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
		if(!player.isCreative() && event.getItemStack() != null && Block.getBlockFromItem(event.getItemStack().getItem()) != null) {
			BlockPos resultingPos = event.getPos();
			IBlockState blockState = player.worldObj.getBlockState(resultingPos);
			if(!blockState.getBlock().isReplaceable(player.worldObj, resultingPos)) {
				resultingPos = resultingPos.offset(event.getFace());
			}
			List<LocationStorage> locations = LocationStorage.getLocations(player.worldObj, new Vec3d(resultingPos));
			for(LocationStorage location : locations) {
				if(location != null && location.getGuard() != null && location.getGuard().isGuarded(player.worldObj, player, resultingPos)) {
					event.setUseItem(Result.DENY);
					if(event.getWorld().isRemote) {
						Vec3d hitVec = event.getHitVec();
						BLParticles.BLOCK_PROTECTION.spawn(event.getWorld(), hitVec.xCoord + event.getFace().getFrontOffsetX() * 0.025F, hitVec.yCoord + event.getFace().getFrontOffsetY() * 0.025F, hitVec.zCoord + event.getFace().getFrontOffsetZ() * 0.025F, ParticleArgs.get().withData(event.getFace()));
					}
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		EntityPlayer player = event.getEntityPlayer();
		List<LocationStorage> locations = LocationStorage.getLocations(player.worldObj, new Vec3d(event.getPos()));
		for(LocationStorage location : locations) {
			if(location != null && location.getGuard() != null && location.getGuard().isGuarded(player.worldObj, player, event.getPos())) {
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
		BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(world);

		Long2ObjectMap<List<LocationStorage>> locationCache = new Long2ObjectOpenHashMap<List<LocationStorage>>();
		List<LocationStorage> affectedLocations = new ArrayList<LocationStorage>();

		for(BlockPos pos : explosion.getAffectedBlockPositions()) {
			long chunkId = ChunkPos.chunkXZ2Int(pos.getX() / 16, pos.getZ() / 16);
			List<LocationStorage> locations = locationCache.get(chunkId);

			if(locations == null) {
				locations = worldStorage.getSharedStorageAt(LocationStorage.class, storage -> storage.getGuard() != null, pos.getX(), pos.getZ());
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
						BLParticles.BLOCK_PROTECTION.spawn(event.getWorld(), hitVec.xCoord + event.getFace().getFrontOffsetX() * 0.025F, hitVec.yCoord + event.getFace().getFrontOffsetY() * 0.025F, hitVec.zCoord + event.getFace().getFrontOffsetZ() * 0.025F, ParticleArgs.get().withData(event.getFace()));
					}
					event.setCanceled(true);
					break;
				}
			}
		}
	}
}
