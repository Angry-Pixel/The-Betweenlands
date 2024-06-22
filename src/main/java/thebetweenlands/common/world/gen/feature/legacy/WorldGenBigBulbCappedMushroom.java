package thebetweenlands.common.world.gen.feature.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.Random;

public class WorldGenBigBulbCappedMushroom extends WorldGenHelper {
	private BlockState head;
	private BlockState stalk;

	public void generateHead(WorldGenLevel world, BlockPos pos) {
		world.setBlock(pos, head, 2);
		world.setBlock(pos.offset(0, -1, 0), head, 2);
		int startY = pos.getY();
		for (int yy = startY; yy >= startY - 4; yy--) {
			pos = new BlockPos(pos.getX(), yy, pos.getZ());
			world.setBlock(pos.offset(1, 0, 0), head, 2);
			world.setBlock(pos.offset(-1, 0, 0), head, 2);
			world.setBlock(pos.offset(0, 0, 1), head, 2);
			world.setBlock(pos.offset(0, 0, -1), head, 2);
			world.setBlock(pos.offset(1, 0, -1), head, 2);
			world.setBlock(pos.offset(-1, 0, -1), head, 2);
			world.setBlock(pos.offset(1, 0, 1), head, 2);
			world.setBlock(pos.offset(-1, 0, 1), head, 2);
			if (yy >= startY - 3 && yy <= startY - 1) {
				world.setBlock(pos.offset(-2, 0, 0), head, 2);
				world.setBlock(pos.offset(2, 0, 0), head, 2);
				world.setBlock(pos.offset(0, 0, -2), head, 2);
				world.setBlock(pos.offset(0, 0, 2), head, 2);
				world.setBlock(pos.offset(-2, 0, -1), head, 2);
				world.setBlock(pos.offset(-2, 0, 1), head, 2);
				world.setBlock(pos.offset(2, 0, -1), head, 2);
				world.setBlock(pos.offset(2, 0, 1), head, 2);
				world.setBlock(pos.offset(-1, 0, 2), head, 2);
				world.setBlock(pos.offset(1, 0, 2), head, 2);
				world.setBlock(pos.offset(-1, 0, -2), head, 2);
				world.setBlock(pos.offset(1, 0, -2), head, 2);
			}
		}
	}

	@Override
	public boolean generate(WorldGenLevel world, Random rand, BlockPos position) {
		this.head = BlockRegistry.BULB_CAPPED_MUSHROOM_CAP.get().defaultBlockState();
		this.stalk = BlockRegistry.BULB_CAPPED_MUSHROOM_STALK.get().defaultBlockState();//.withProperty(BlockLog.LOG_AXIS, EnumAxis.Y);

		int height = rand.nextInt(2) + 8;
		int maxRadius = 2;

		BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();

		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!world.getBlockState(checkPos.set(xx, yy, zz)).isAir())
						return false;


		for (int yy = y; yy < y + height; ++yy) {
			if (yy == y) {
				world.setBlock(new BlockPos(x, yy, z), stalk, 2);
			} else
				world.setBlock(new BlockPos(x, yy, z), stalk, 2);

			if (yy == y + height - 1)
				generateHead(world, new BlockPos(x, yy, z));

		}
		//DecorationHelper.GEN_BULB_CAPPED_MUSHROOMS.generate(world, rand, position);
		return true;
	}

	public void generateHead(Level world, BlockPos pos) {
		world.setBlock(pos, head, 2);
		world.setBlock(pos.offset(0, -1, 0), head, 2);
		int startY = pos.getY();
		for (int yy = startY; yy >= startY - 4; yy--) {
			pos = new BlockPos(pos.getX(), yy, pos.getZ());
			world.setBlock(pos.offset(1, 0, 0), head, 2);
			world.setBlock(pos.offset(-1, 0, 0), head, 2);
			world.setBlock(pos.offset(0, 0, 1), head, 2);
			world.setBlock(pos.offset(0, 0, -1), head, 2);
			world.setBlock(pos.offset(1, 0, -1), head, 2);
			world.setBlock(pos.offset(-1, 0, -1), head, 2);
			world.setBlock(pos.offset(1, 0, 1), head, 2);
			world.setBlock(pos.offset(-1, 0, 1), head, 2);
			if (yy >= startY - 3 && yy <= startY - 1) {
				world.setBlock(pos.offset(-2, 0, 0), head, 2);
				world.setBlock(pos.offset(2, 0, 0), head, 2);
				world.setBlock(pos.offset(0, 0, -2), head, 2);
				world.setBlock(pos.offset(0, 0, 2), head, 2);
				world.setBlock(pos.offset(-2, 0, -1), head, 2);
				world.setBlock(pos.offset(-2, 0, 1), head, 2);
				world.setBlock(pos.offset(2, 0, -1), head, 2);
				world.setBlock(pos.offset(2, 0, 1), head, 2);
				world.setBlock(pos.offset(-1, 0, 2), head, 2);
				world.setBlock(pos.offset(1, 0, 2), head, 2);
				world.setBlock(pos.offset(-1, 0, -2), head, 2);
				world.setBlock(pos.offset(1, 0, -2), head, 2);
			}
		}
	}

	@Override
	public boolean generate(Level world, Random rand, BlockPos position) {
		this.head = BlockRegistry.BULB_CAPPED_MUSHROOM_CAP.get().defaultBlockState();
		this.stalk = BlockRegistry.BULB_CAPPED_MUSHROOM_STALK.get().defaultBlockState();//.withProperty(BlockLog.LOG_AXIS, EnumAxis.Y);

		int height = rand.nextInt(2) + 8;
		int maxRadius = 2;

		BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();

		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!world.getBlockState(checkPos.set(xx, yy, zz)).isAir())
						return false;


		for (int yy = y; yy < y + height; ++yy) {
			if (yy == y) {
				world.setBlock(new BlockPos(x, yy, z), stalk, 2);
			} else
				world.setBlock(new BlockPos(x, yy, z), stalk, 2);

			if (yy == y + height - 1)
				generateHead(world, new BlockPos(x, yy, z));

		}
		//DecorationHelper.GEN_BULB_CAPPED_MUSHROOMS.generate(world, rand, position);
		return true;
	}
}
