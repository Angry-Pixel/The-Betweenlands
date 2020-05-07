package thebetweenlands.common.world.biome.spawning.spawners;

import java.util.function.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.AreaMobSpawner.BLSpawnEntry;

/**
 * Spawns entities in the sky, the further up the higher the spawn weight.
 * Spawn weight is also larger when players are nearby.
 */
public class SkySpawnEntry extends BLSpawnEntry {
	public SkySpawnEntry(int id, Class<? extends EntityLiving> entityType, Function<World, ? extends EntityLiving> entityCtor) {
		super(id, entityType, entityCtor);
	}

	public SkySpawnEntry(int id, Class<? extends EntityLiving> entityType, Function<World, ? extends EntityLiving> entityCtor, short weight) {
		super(id, entityType, entityCtor, weight);
	}

	@Override
	public void update(World world, BlockPos pos) {
		int skyHeight = WorldProviderBetweenlands.LAYER_HEIGHT + 30;
		short spawnWeight = this.getBaseWeight();
		if(pos.getY() < skyHeight) {
			spawnWeight = 0;
		} else {
			float weightPercent = MathHelper.clamp((pos.getY() - skyHeight) / 16.0f, 0, 1);

			float closestDistanceSq = Float.MAX_VALUE;
			for(EntityPlayer player : world.playerEntities) {
				if(!player.isSpectator()) {
					float dstSq = (float)player.getDistanceSq(pos.getX() + 0.5f, player.posY, pos.getZ() + 0.5f);
					float dy = Math.max(skyHeight, (float)player.posY) - pos.getY();
					dstSq += dy * dy * 1.5f;
					if(dstSq < closestDistanceSq) {
						closestDistanceSq = dstSq;
					}
				}
			}
			float playerWeight = 0.1f + 0.9f * MathHelper.clamp(1 - (float)(Math.sqrt(closestDistanceSq) - 32) / 32.0f, 0, 1);

			weightPercent *= playerWeight;

			spawnWeight = (short)MathHelper.floor(weightPercent * spawnWeight);
		}
		
		this.setWeight(spawnWeight);
	}

	@Override
	public boolean canSpawn(World world, Chunk chunk, BlockPos pos, IBlockState blockState, IBlockState surfaceBlockState) {
		return blockState.getBlock().isAir(blockState, world, pos) && surfaceBlockState.getBlock().isAir(surfaceBlockState, world, pos.down());
	}
}
