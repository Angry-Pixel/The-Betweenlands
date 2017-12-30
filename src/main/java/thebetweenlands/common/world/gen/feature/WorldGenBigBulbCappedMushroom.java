package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.DecorationHelper;

public class WorldGenBigBulbCappedMushroom extends WorldGenerator {
	private IBlockState head;
	private IBlockState stalk;

	public void generateHead(World world, BlockPos pos){
		world.setBlockState(pos, head, 2);
		world.setBlockState(pos.add(0, -1, 0), head, 2);
		int startY = pos.getY();
		for(int yy = startY; yy >= startY - 4; yy--){
			pos = new BlockPos(pos.getX(), yy, pos.getZ());
			world.setBlockState(pos.add(1, 0, 0), head, 2);
			world.setBlockState(pos.add(-1, 0, 0), head, 2);
			world.setBlockState(pos.add(0, 0, 1), head, 2);
			world.setBlockState(pos.add(0, 0, -1), head, 2);
			world.setBlockState(pos.add(1, 0, -1), head, 2);
			world.setBlockState(pos.add(-1, 0, -1), head, 2);
			world.setBlockState(pos.add(1, 0, 1), head, 2);
			world.setBlockState(pos.add(-1, 0, 1), head, 2);
			if(yy >= startY - 3 && yy <= startY - 1){
				world.setBlockState(pos.add(-2, 0, 0), head, 2);
				world.setBlockState(pos.add(2, 0, 0), head, 2);
				world.setBlockState(pos.add(0, 0, -2), head, 2);
				world.setBlockState(pos.add(0, 0, 2), head, 2);
				world.setBlockState(pos.add(-2, 0, -1), head, 2);
				world.setBlockState(pos.add(-2, 0, 1), head, 2);
				world.setBlockState(pos.add(2, 0, -1), head, 2);
				world.setBlockState(pos.add(2, 0, 1), head, 2);
				world.setBlockState(pos.add(-1, 0, 2), head, 2);
				world.setBlockState(pos.add(1, 0, 2), head, 2);
				world.setBlockState(pos.add(-1, 0, -2), head, 2);
				world.setBlockState(pos.add(1, 0, -2), head, 2);
			}
		}
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		this.head = BlockRegistry.BULB_CAPPED_MUSHROOM_CAP.getDefaultState();
		this.stalk = BlockRegistry.BULB_CAPPED_MUSHROOM_STALK.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.Y);

		int height = rand.nextInt(2) + 8;
		int maxRadius = 2;

		MutableBlockPos checkPos = new MutableBlockPos();

		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();

		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!world.isAirBlock(checkPos.setPos(xx, yy, zz)))
						return false;


		for (int yy = y; yy < y + height; ++yy) {
			if (yy == y) {
				world.setBlockState(new BlockPos(x, yy, z), stalk, 2);
			}
			else
				world.setBlockState(new BlockPos(x, yy, z), stalk, 2);

			if(yy == y + height -1)
				generateHead(world, new BlockPos(x, yy, z));

		}
		DecorationHelper.GEN_BULB_CAPPED_MUSHROOMS.generate(world, rand, position);
		return true;
	}

}
