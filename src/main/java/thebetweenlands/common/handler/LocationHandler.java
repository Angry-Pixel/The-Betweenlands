package thebetweenlands.common.handler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationStorage;

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
				if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
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
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if(player != null) {
			BlockPos pos = event.getPos();
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(event.getWorld());
			List<LocationStorage> locations = worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, pos.getX(), pos.getZ(), location -> location.isInside(pos));

			for(LocationStorage location : locations) {
				location.onBreakBlock(event);
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
		EnumFacing facing = event.getFace();
		Vec3d hitVec = event.getHitVec();
		EntityPlayer player = event.getEntityPlayer();
		if(facing != null && hitVec != null && !player.isCreative() && !event.getItemStack().isEmpty() && Block.getBlockFromItem(event.getItemStack().getItem()) != Blocks.AIR) {
			BlockPos resultingPos = event.getPos();
			IBlockState blockState = player.world.getBlockState(resultingPos);
			if(!blockState.getBlock().isReplaceable(player.world, resultingPos)) {
				resultingPos = resultingPos.offset(facing);
			}
			List<LocationStorage> locations = LocationStorage.getLocations(player.world, new Vec3d(resultingPos));
			for(LocationStorage location : locations) {
				if(location != null && location.getGuard() != null && location.getGuard().isGuarded(player.world, player, resultingPos)) {
					event.setUseItem(Result.DENY);
					if(event.getWorld().isRemote) {
						BLParticles.BLOCK_PROTECTION.spawn(event.getWorld(), hitVec.x + facing.getXOffset() * 0.025F, hitVec.y + facing.getYOffset() * 0.025F, hitVec.z + facing.getZOffset() * 0.025F, ParticleArgs.get().withData(facing));
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
				if(player.world.isRemote && player.swingProgressInt == 0) {
					spawnBreakSpeedParticle(event.getPos(), player);
				}

				event.setNewSpeed(0.0F);
				event.setCanceled(true);
				return;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static void spawnBreakSpeedParticle(BlockPos pos, EntityPlayer player) {
		if(player instanceof EntityPlayerSP && Minecraft.getMinecraft().playerController.getIsHittingBlock()) {
			RayTraceResult rayTrace = Minecraft.getMinecraft().objectMouseOver;

			if(rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK && pos.equals(rayTrace.getBlockPos())) {
				Vec3d hitVec = rayTrace.hitVec;
				BLParticles.BLOCK_PROTECTION.spawn(player.world, hitVec.x + rayTrace.sideHit.getXOffset() * 0.025F, hitVec.y + rayTrace.sideHit.getYOffset() * 0.025F, hitVec.z + rayTrace.sideHit.getZOffset() * 0.025F, ParticleArgs.get().withData(rayTrace.sideHit));
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

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onLeftClickBlock(LeftClickBlock event) {
		EnumFacing facing = event.getFace();
		Vec3d hitVec = event.getHitVec();
		if(hitVec != null && !event.getEntityPlayer().isCreative() && facing != null) {
			List<LocationStorage> locations = LocationStorage.getLocations(event.getWorld(), new Vec3d(event.getPos()));
			for(LocationStorage location : locations) {
				if(location != null && location.getGuard() != null && location.getGuard().isGuarded(event.getWorld(), event.getEntityPlayer(), event.getPos())) {
					BLParticles.BLOCK_PROTECTION.spawn(event.getWorld(), hitVec.x + facing.getXOffset() * 0.025F, hitVec.y + facing.getYOffset() * 0.025F, hitVec.z + facing.getZOffset() * 0.025F, ParticleArgs.get().withData(facing));
					break;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDestroyBlock(LivingDestroyBlockEvent event) {
		if(isProtected(event.getEntityLiving().world, event.getEntityLiving(), event.getPos())) {
			event.setCanceled(true);
		}
	}

	public static boolean isProtected(World world, @Nullable Entity entity, BlockPos pos) {
		List<LocationStorage> locations = LocationStorage.getLocations(world, new Vec3d(pos));
		for(LocationStorage location : locations) {
			if(location != null && location.getGuard() != null && location.getGuard().isGuarded(world, entity, pos)) {
				return true;
			}
		}
		return false;
	}
}
