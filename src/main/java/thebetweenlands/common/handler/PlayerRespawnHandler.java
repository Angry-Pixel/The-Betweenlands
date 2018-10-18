package thebetweenlands.common.handler;

import java.util.Collections;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import thebetweenlands.api.entity.spawning.IWeightProvider;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.teleporter.TeleporterBetweenlands;
import thebetweenlands.common.world.teleporter.TeleporterHandler;
import thebetweenlands.util.WeightedList;

public class PlayerRespawnHandler {
	public static final String RESPAWN_IN_BL_NBT = "thebetweenlands.respawn_in_bl";
	
	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {
		if(event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			
			NBTTagCompound dataNbt = player.getEntityData();
			NBTTagCompound persistentNbt = dataNbt.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			
			BlockPos spawnPos = player.getBedLocation(player.dimension);

			BlockPos adjustedSpawnPos = spawnPos == null ? null : EntityPlayer.getBedSpawnLocation(player.world, spawnPos, player.isSpawnForced(player.dimension));
			
			boolean respawnInBL = BetweenlandsConfig.WORLD_AND_DIMENSION.startInBetweenlands && (!player.world.provider.canRespawnHere() || adjustedSpawnPos == null);
			
			persistentNbt.setBoolean(RESPAWN_IN_BL_NBT, respawnInBL);
			
			dataNbt.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentNbt);
		}
	}
	
	
	@SubscribeEvent
	public static void onRespawn(PlayerRespawnEvent event) {
		if(!event.player.world.isRemote) {
			BlockPos spawnPos = event.player.getBedLocation(event.player.dimension);

			BlockPos adjustedSpawnPos = spawnPos == null ? null : EntityPlayer.getBedSpawnLocation(event.player.world, spawnPos, event.player.isSpawnForced(event.player.dimension));

			NBTTagCompound dataNbt = event.player.getEntityData();
			NBTTagCompound persistentNbt = dataNbt.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			
			boolean shouldTeleportToBL = (BetweenlandsConfig.WORLD_AND_DIMENSION.startInBetweenlands && event.isEndConquered()) || persistentNbt.getBoolean(RESPAWN_IN_BL_NBT);
			
			if(shouldTeleportToBL && event.player.world instanceof WorldServer) {
				WorldServer blWorld = ((WorldServer) event.player.world).getMinecraftServer().getWorld(BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);
				
				TeleporterHandler.transferToDim(event.player, blWorld, false, false);
				
				spawnPos = event.player.getBedLocation(event.player.dimension);

				adjustedSpawnPos = spawnPos == null ? null : EntityPlayer.getBedSpawnLocation(event.player.world, spawnPos, event.player.isSpawnForced(event.player.dimension));
				
				if(adjustedSpawnPos != null) {
					event.player.setPosition(adjustedSpawnPos.getX() + 0.5D, adjustedSpawnPos.getY(), adjustedSpawnPos.getZ() + 0.5D);
					
					if(event.player instanceof EntityPlayerMP) {
						EntityPlayerMP playerMP = (EntityPlayerMP) event.player;

						playerMP.connection.setPlayerLocation(playerMP.posX, playerMP.posY, playerMP.posZ, playerMP.rotationYaw, playerMP.rotationPitch);
						playerMP.connection.sendPacket(new SPacketSpawnPosition(adjustedSpawnPos));
					}
				}
			}
			
			if(shouldTeleportToBL || event.player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
				boolean newRespawn = false;
				if(adjustedSpawnPos == null) {
					newRespawn = true;
				} else {
					IBlockState stateDown = event.player.world.getBlockState(adjustedSpawnPos.down());
					boolean isValidSpawnBlock = stateDown.getMaterial().blocksMovement() && !stateDown.getBlock().isLeaves(stateDown, event.player.world, adjustedSpawnPos.down()) && !stateDown.getBlock().isFoliage(event.player.world, adjustedSpawnPos.down());
					if(!isValidSpawnBlock) {
						newRespawn = true;
					}
				}

				if(newRespawn) {
					if(persistentNbt.hasKey(TeleporterBetweenlands.LAST_PORTAL_POS_NBT, Constants.NBT.TAG_LONG)) {
						BlockPos lastPortal = BlockPos.fromLong(persistentNbt.getLong(TeleporterBetweenlands.LAST_PORTAL_POS_NBT));

						respawnNearPos(event.player, lastPortal);
					}
				}
			}
		}
	}

	public static void respawnNearPos(Entity entity, BlockPos pos) {
		BlockPos newSpawn = getRespawnPointNearPos(entity.world, pos, 64);

		entity.setLocationAndAngles(newSpawn.getX() + 0.5D, newSpawn.getY(), newSpawn.getZ() + 0.5D, entity.rotationYaw, entity.rotationPitch);

		while (!entity.world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty() && entity.posY < 256.0D) {
			entity.setPosition(entity.posX, entity.posY + 1.0D, entity.posZ);
		}

		newSpawn = new BlockPos(entity);

		if(entity instanceof EntityPlayer) {
			((EntityPlayer)entity).setSpawnPoint(newSpawn, true);
		}

		if(entity instanceof EntityPlayerMP) {
			EntityPlayerMP playerMP = (EntityPlayerMP) entity;

			playerMP.connection.setPlayerLocation(playerMP.posX, playerMP.posY, playerMP.posZ, playerMP.rotationYaw, playerMP.rotationPitch);
			playerMP.connection.sendPacket(new SPacketSpawnPosition(newSpawn));
		}
	}

	public static BlockPos getRespawnPointNearPos(World world, BlockPos pos, int fuzz) {
		int spawnFuzz = fuzz;
		int spawnFuzzHalf = spawnFuzz / 2;

		class WeightedPos implements IWeightProvider {
			final BlockPos pos;
			short weight;

			WeightedPos(BlockPos pos) {
				this.pos = pos;
			}

			@Override
			public short getWeight() {
				return this.weight;
			}
		}

		short maxWeight = 0;

		WeightedList<WeightedPos> spawnCandidates = new WeightedList<>();

		for(int xo = -spawnFuzzHalf; xo <= spawnFuzzHalf; xo += 1 + world.rand.nextInt(3)) {
			for(int zo = -spawnFuzzHalf; zo <= spawnFuzzHalf; zo += 1 + world.rand.nextInt(3)) {
				BlockPos newPos = pos.add(xo, 0, zo);
				Chunk chunk = world.getChunkFromBlockCoords(newPos);
				newPos = new BlockPos(newPos.getX(), chunk.getHeight(newPos), newPos.getZ());
				if(Math.abs(newPos.getY() - pos.getY()) <= 16 && EntityPlayer.getBedSpawnLocation(world, newPos, true) != null) {
					IBlockState stateDown = chunk.getBlockState(newPos.down());
					if(stateDown.getMaterial().blocksMovement() && !stateDown.getBlock().isLeaves(stateDown, world, newPos.down()) && !stateDown.getBlock().isFoliage(world, newPos.down())) {
						WeightedPos p = new WeightedPos(newPos);
						p.weight = (short) Math.abs(newPos.getY() - pos.getY());
						maxWeight = (short)Math.max(maxWeight, p.weight);
						spawnCandidates.add(p);
					}
				}
			}
		}

		if(!spawnCandidates.isEmpty()) {
			Collections.shuffle(spawnCandidates, world.rand);

			for(WeightedPos p : spawnCandidates) {
				p.weight = (short) (maxWeight - p.weight);
				p.weight = (short) Math.pow(p.weight, 1.5D);
			}

			spawnCandidates.recalculateWeight();

			return spawnCandidates.getRandomItem(world.rand).pos;
		}

		return world.getTopSolidOrLiquidBlock(pos.add(world.rand.nextInt(spawnFuzz) - spawnFuzzHalf, 0, world.rand.nextInt(spawnFuzz) - spawnFuzzHalf));
	}
}
