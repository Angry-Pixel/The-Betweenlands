package thebetweenlands.common.world.biome.spawning.spawners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.entity.mobs.EntityGreebling;

public class GreeblingSpawnEntry extends SurfaceSpawnEntry {
	private EnumFacing facing = EnumFacing.NORTH;

	public GreeblingSpawnEntry(int id, short weight) {
		super(id, EntityGreebling.class, EntityGreebling::new, weight);
	}

	@Override
	public boolean canSpawn(World world, Chunk chunk, BlockPos pos, IBlockState spawnBlockState, IBlockState surfaceBlockState) {
		if(super.canSpawn(world, chunk, pos, spawnBlockState, surfaceBlockState)) {
			List<EnumFacing> facings = new ArrayList<>(4);
			facings.addAll(Arrays.asList(EnumFacing.HORIZONTALS));

			Collections.shuffle(facings, world.rand);

			for(EnumFacing facing : facings) {
				BlockPos offset = pos.offset(facing);
				if(world.isBlockLoaded(offset) && world.isAirBlock(offset) && world.isAirBlock(offset.down())) {
					this.facing = facing;
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void onSpawned(EntityLivingBase entity) {
		EntityGreebling greebling = (EntityGreebling) entity;
		greebling.setFacing(this.facing);
	}
}
