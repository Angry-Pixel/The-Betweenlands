package thebetweenlands.common.world.teleporter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.feature.structure.WorldGenWeedWoodPortalTree;
import thebetweenlands.util.config.ConfigHandler;


public final class TeleporterBetweenlands extends Teleporter {
	private final WorldServer worldServerInstance;
	private final Long2ObjectMap<Teleporter.PortalPosition> destinationCoordinateCache = new Long2ObjectOpenHashMap<Teleporter.PortalPosition>(4096);
	private final List<Long> destinationCoordinateKeys = new ArrayList<Long>();

	public TeleporterBetweenlands(WorldServer worldServer) {
		super(worldServer);
		worldServerInstance = worldServer;
	}

	@Override
	public void placeInPortal(Entity entityIn, float rotationYaw) {
		if (!placeInExistingPortal(entityIn, rotationYaw)) {
			makePortal(entityIn);
			placeInExistingPortal(entityIn, rotationYaw);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
		int checkRadius = 32;
		double distToPortal = -1.0;
		int posX = 0;
		int posY = 0;
		int posZ = 0;
		int roundX = MathHelper.floor_double(entityIn.posX);
		int roundZ = MathHelper.floor_double(entityIn.posZ);
		long coordPair = ChunkPos.chunkXZ2Int(roundX, roundZ);
		boolean portalNotSaved = true;
		BlockPos blockpos = BlockPos.ORIGIN;
		if (destinationCoordinateCache.containsKey(coordPair)) {
			PortalPosition pos = destinationCoordinateCache.get(coordPair);
			distToPortal = 0.0;
			posX = pos.getX();
			posY = pos.getY();
			posZ = pos.getZ();
			pos.lastUpdateTime = worldServerInstance.getTotalWorldTime();
			portalNotSaved = false;
		} else
			for (int i = roundX - checkRadius; i <= roundX + checkRadius; i++)

				for (int j = roundZ - checkRadius; j <= roundZ + checkRadius; j++)
					for (int h = worldServerInstance.getActualHeight() - 1; h >= 0; h--) {
						Block block = worldServerInstance.getBlockState(new BlockPos(i, h, j)).getBlock();
						if (block == BlockRegistry.TREE_PORTAL) {
							while(worldServerInstance.getBlockState(new BlockPos(i, --h, j)).getBlock() == BlockRegistry.TREE_PORTAL);
							double X = i + 0.5 - entityIn.posX;
							double Z = j + 0.5 - entityIn.posZ;
							double Y = h + 2.5 - entityIn.posY;
							double dist = X * X + Y * Y + Z * Z;

							if (distToPortal < 0.0 || dist < distToPortal) {
								distToPortal = dist;
								posX = i;
								posY = h + 2;
								posZ = j;
							}
						}
					}
		if (distToPortal >= 0.0) {
			if (portalNotSaved) {
				destinationCoordinateCache.put(coordPair, new Teleporter.PortalPosition(blockpos, worldServerInstance.getTotalWorldTime()));
				destinationCoordinateKeys.add(Long.valueOf(coordPair));
			}

			entityIn.motionX = entityIn.motionY = entityIn.motionZ = 0.0;
			
			entityIn.setLocationAndAngles(posX + 0.5D, posY, posZ + 0.5D, entityIn.rotationYaw, entityIn.rotationPitch);
			if(entityIn.worldObj.provider.getDimension() != ConfigHandler.INSTANCE.dimensionId)
				setDefaultPlayerSpawnLocation(entityIn);
			return true;
		}
		return false;
	}

	@Override
	public boolean makePortal(Entity entity) {
		int posX = MathHelper.floor_double(entity.posX);
		int posZ = MathHelper.floor_double(entity.posZ);
		int maxPortalSpawnHeight;
		int minSpawnHeight;
		//System.out.println(entity.dimension);
		if (entity.dimension == 0) {
			maxPortalSpawnHeight = 100;
			minSpawnHeight = 64;
		} else {
			maxPortalSpawnHeight = 85;
			minSpawnHeight = 80;
		}
		//System.out.println(maxPortalSpawnHeight + "," + minSpawnHeight);
		for (int x = posX - 127; x < posX + 127; x++) {
			for (int z = posZ - 127; z < posZ + 127; z++) {
				for (int y = maxPortalSpawnHeight; y >= minSpawnHeight; y--) {
					Block block = worldServerInstance.getBlockState(new BlockPos(x, y, z)).getBlock();
					if (block != Blocks.AIR) {
						if (canGenerate(worldServerInstance, x, y, z)) {
							new WorldGenWeedWoodPortalTree().generate(worldServerInstance, worldServerInstance.rand, new BlockPos(x, y, z));
							entity.setLocationAndAngles(x, y + 2, z, entity.rotationYaw, entity.rotationPitch);
							return true;
						} else {
							for (int yy = y; yy <= maxPortalSpawnHeight; yy++) {
								if (canGenerate(worldServerInstance, x, yy, z)) {
									new WorldGenWeedWoodPortalTree().generate(worldServerInstance, worldServerInstance.rand, new BlockPos(x, yy, z));
									entity.setLocationAndAngles(x, yy + 2, z, entity.rotationYaw, entity.rotationPitch);
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public boolean canGenerate(World world, int posX, int posY, int posZ){
		int height = 16;
		int maxRadius = 9;
		for (int xx = posX - maxRadius; xx <= posX + maxRadius; xx++)
			for (int zz = posZ - maxRadius; zz <= posZ + maxRadius; zz++)
				for (int yy = posY + 2; yy < posY + height; yy++) {
					IBlockState blockState = world.getBlockState(new BlockPos(xx, yy, zz));
					if ((!world.isAirBlock(new BlockPos(xx, yy, zz)) && blockState.isNormalCube()) || blockState.getBlock() instanceof BlockLeaves)
						return false;
				}
		return true;
	}

	@Override
	public void removeStalePortalLocations(long timer) {
		if (timer % 100L == 0L) {
			Iterator<Long> iterator = destinationCoordinateKeys.iterator();
			while (iterator.hasNext()) {
				Long hashedPortalPos = iterator.next();
				PortalPosition position = destinationCoordinateCache.get(hashedPortalPos.longValue());

				if (position == null || position.lastUpdateTime < timer - 600L) {
					iterator.remove();
					destinationCoordinateCache.remove(hashedPortalPos.longValue());
				}
			}
		}
	}

	public void setDefaultPlayerSpawnLocation(Entity entity) {
		if (!(entity instanceof EntityPlayerMP))
			return;

		EntityPlayerMP player = (EntityPlayerMP) entity;
		BlockPos coords = player.getBedLocation(ConfigHandler.dimensionId);

		if (coords == null) {
			coords = player.getPosition();
			int spawnFuzz = 64;
			int spawnFuzzHalf = spawnFuzz / 2;
			BlockPos spawnPlace = worldServerInstance.getTopSolidOrLiquidBlock(coords.add(worldServerInstance.rand.nextInt(spawnFuzz) - spawnFuzzHalf, 0, worldServerInstance.rand.nextInt(spawnFuzz) - spawnFuzzHalf));
			player.setSpawnChunk(spawnPlace, true, ConfigHandler.dimensionId);
		}
	}

}