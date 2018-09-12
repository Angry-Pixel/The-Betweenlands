package thebetweenlands.common.world.gen.feature.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class WorldGenSpiritTreeStructure extends WorldGenerator {
	private static final WorldGenSpiritTree GEN_SPIRIT_TREE = new WorldGenSpiritTree(true);

	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		if(GEN_SPIRIT_TREE.generate(world, rand, position)) {
			this.generateWispCircle(world, rand, position, 6, 1, 2);
			this.generateWispCircle(world, rand, position, 14, 1, 1);

			for(int i = 0; i < 20; i++) {
				double dir = rand.nextDouble() * Math.PI * 2;
				double dx = Math.cos(dir);
				double dz = Math.sin(dir);
				double bx = dx * 16 + dx * rand.nextDouble() * 16;
				double bz = dz * 16 + dz * rand.nextDouble() * 16;
				BlockPos root = position.add(bx, 0, bz);
				root = this.findGroundPosition(world, root);
				if(root != null && world.isAirBlock(root.up())) {
					this.generateRoot(world, rand, root);
				}
			}

			return true;
		}

		return false;
	}

	@Nullable
	private BlockPos findGroundPosition(World world, BlockPos pos) {
		boolean hadAir = false;
		for(int yo = 6; yo >= -6; yo--) {
			BlockPos offsetPos = pos.up(yo);
			IBlockState state = world.getBlockState(offsetPos);
			if(hadAir && SurfaceType.MIXED_GROUND.test(state)) {
				return offsetPos.up();
			}
			if(state.getBlock().isAir(state, world, offsetPos)) {
				hadAir = true;
			}
		}
		return null;
	}

	private void generateWispCircle(World world, Random rand, BlockPos center, int radius, int minHeight, int heightVar) {
		List<BlockPos> circle = this.generateCircle(world, center, radius);
		for(int i = 0; i < circle.size(); i += 2 + rand.nextInt(2)) {
			if(i == circle.size() - 1) {
				break;
			}
			BlockPos pos = circle.get(i);
			pos = this.findGroundPosition(world, pos);
			if(pos != null && !world.getBlockState(pos).getMaterial().isLiquid()) {
				int height = minHeight + rand.nextInt(heightVar + 1);
				for(int yo = 0; yo < height; yo++) {
					world.setBlockState(pos.up(yo), BlockRegistry.MOSSY_BETWEENSTONE_BRICK_WALL.getDefaultState());
				}
				if(rand.nextInt(3) == 0) {
					world.setBlockState(pos.up(height), BlockRegistry.WISP.getDefaultState());
				}
			}
		}
	}

	private void generateRoot(World world, Random rand, BlockPos pos) {
		List<BlockPos> potentialBlocks = GEN_SPIRIT_TREE.generateBranchPositions(rand, pos, rand.nextInt(7), 32, 0.4D, 0.3D, (i, remainingBlocks) -> i < 2 ? 1 : (i > 4 ? -1 : 0), (i, length) -> true);
		int length = 0;
		for(int i = 0; i < potentialBlocks.size(); i++) {
			BlockPos block = potentialBlocks.get(i);
			if(i > 2 && !world.isAirBlock(block)) {
				length = i + 1;
				break;
			}
		}
		BlockPos prevPos = null;
		for(int i = 0; i < length; i++) {
			BlockPos block = potentialBlocks.get(i);
			if(prevPos != null) {
				int xo = block.getX() - prevPos.getX();
				int yo = block.getY() - prevPos.getY();
				int zo = block.getZ() - prevPos.getZ();
				int moves = Math.abs(xo) + Math.abs(yo) + Math.abs(zo);
				if(moves > 1) {
					List<BlockPos> choices = new ArrayList<>();
					if(xo != 0) {
						choices.add(new BlockPos(xo, 0, 0));
					}
					if(yo != 0) {
						choices.add(new BlockPos(0, yo, 0));
					}
					if(zo != 0) {
						choices.add(new BlockPos(0, 0, zo));
					}
					BlockPos filler = prevPos;
					for(int j = 0; j < moves; j++) {
						filler = filler.add(choices.remove(rand.nextInt(choices.size())));
						this.setBlockAndNotifyAdequately(world, filler, BlockRegistry.LOG_SPIRIT_TREE.getDefaultState());
					}
				}
			}
			this.setBlockAndNotifyAdequately(world, block, BlockRegistry.LOG_SPIRIT_TREE.getDefaultState());
			prevPos = block;
		}
	}

	private List<BlockPos> generateCircle(World world, BlockPos pos, int radius) {
		int xo = radius;
		int zo = 0;
		int err = 1 - radius;

		List<BlockPos> s1 = new ArrayList<>();
		List<BlockPos> s2 = new ArrayList<>();
		List<BlockPos> s3 = new ArrayList<>();
		List<BlockPos> s4 = new ArrayList<>();
		List<BlockPos> s5 = new ArrayList<>();
		List<BlockPos> s6 = new ArrayList<>();
		List<BlockPos> s7 = new ArrayList<>();
		List<BlockPos> s8 = new ArrayList<>();

		while (xo >= zo) {
			s1.add(pos.add(xo, 0, zo));
			if(xo != zo) s2.add(pos.add(zo, 0, xo));
			if(zo != 0) s3.add(pos.add(-zo, 0, xo));
			if(xo != zo) s4.add(pos.add(-xo, 0, zo));
			if(zo != 0) s5.add(pos.add(-xo, 0, -zo));
			if(xo != zo) s6.add(pos.add(-zo, 0, -xo));
			if(zo != 0) s7.add(pos.add(zo, 0, -xo));
			if(xo != zo && zo != 0) s8.add(pos.add(xo, 0, -zo));

			if(err < 0) {
				zo++;
				err = err + 2 * zo + 1;
			} else {
				zo++;
				xo--;
				err = err + 2 * (zo - xo) + 1;
			}
		}

		for(ListIterator<BlockPos> it = s2.listIterator(s2.size()); it.hasPrevious();) {
			s1.add(it.previous());
		}
		s1.addAll(s3);
		for(ListIterator<BlockPos> it = s4.listIterator(s4.size()); it.hasPrevious();) {
			s1.add(it.previous());
		}
		s1.addAll(s5);
		for(ListIterator<BlockPos> it = s6.listIterator(s6.size()); it.hasPrevious();) {
			s1.add(it.previous());
		}
		s1.addAll(s7);
		for(ListIterator<BlockPos> it = s8.listIterator(s8.size()); it.hasPrevious();) {
			s1.add(it.previous());
		}

		return s1;
	}
}
