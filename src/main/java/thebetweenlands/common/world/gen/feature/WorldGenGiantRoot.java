package thebetweenlands.common.world.gen.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.plant.BlockMoss;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.CatmullRomSpline;
import thebetweenlands.util.ISpline;
import thebetweenlands.util.ReparameterizedSpline;

public class WorldGenGiantRoot extends WorldGenerator {
	protected final BlockPos start, end;
	protected final double minWidth, maxWidth, randXZOffsetRange, randYOffsetRange, maxArcHeight;
	protected final boolean genSmallerRoots, genLeaves;

	public WorldGenGiantRoot(BlockPos start, BlockPos end) {
		this(start, end, 2, 3, 8, 5, 10, true, false);
	}

	public WorldGenGiantRoot(BlockPos start, BlockPos end, double minWidth, double maxWidth, double randXZOffsetRange, double randYOffsetRange, double maxArcHeight, boolean genSmallerRoots, boolean genLeaves) {
		this.start = start;
		this.end = end;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.randXZOffsetRange = randXZOffsetRange;
		this.randYOffsetRange = randYOffsetRange;
		this.maxArcHeight = maxArcHeight;
		this.genSmallerRoots = genSmallerRoots;
		this.genLeaves = genLeaves;
	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		boolean generated = true;

		Vec3d up = new Vec3d(0, 1, 0);
		Vec3d diff = new Vec3d(this.end.subtract(this.start));
		Vec3d dir = diff.normalize();

		Vec3d offsetDir = up.crossProduct(dir);

		Vec3d controlPtStart = new Vec3d(this.start).subtract(dir).addVector(0, -16, 0);

		Vec3d controlPtEnd = new Vec3d(this.end).add(dir).addVector(0, -16, 0);

		List<Vec3d> pts = new ArrayList<>();

		pts.add(controlPtStart);
		pts.add(new Vec3d(this.start));

		int randPts = 5;
		for(int i = 0; i < randPts; i++) {
			Vec3d pt = new Vec3d(this.start).add(diff.scale(i / (float)randPts)).addVector(offsetDir.x * (rand.nextDouble() - 0.5D) * this.randXZOffsetRange * 2.0D, (rand.nextDouble() - 0.5D) * this.randYOffsetRange * 2.0D + Math.sin(i / (float)randPts * Math.PI) * this.maxArcHeight, offsetDir.z * (rand.nextDouble() - 0.5D) * this.randXZOffsetRange * 2.0D);
			pts.add(pt);
		}

		pts.add(new Vec3d(this.end));
		pts.add(controlPtEnd);

		ISpline spline = new ReparameterizedSpline(new CatmullRomSpline(pts.toArray(new Vec3d[0]))).init(MathHelper.ceil(diff.lengthVector() / 8.0D) + 2, 8);

		IBlockState bark = BlockRegistry.GIANT_ROOT_BLOCK.getDefaultState();
		IBlockState leaves = BlockRegistry.LEAVES_WEEDWOOD_TREE.getDefaultState();
		IBlockState root = BlockRegistry.ROOT.getDefaultState();
		IBlockState hanger = BlockRegistry.HANGER.getDefaultState();

		int steps = MathHelper.ceil(spline.getArcLength()) * 4;

		for(int i = 0; i < steps; i++) {
			BlockPos pos = new BlockPos(spline.interpolate(i / (float)steps));

			if(this.genSmallerRoots && rand.nextInt(20) == 0) {
				WorldGenGiantRoot smallRoot = new WorldGenGiantRoot(pos, pos.add(offsetDir.x * (rand.nextDouble() - 0.5D) * 16, rand.nextDouble() * 8, offsetDir.z * (rand.nextDouble() - 0.5D) * 16), 0.5D, 0.5D, 3, 3, 0, false, true);
				smallRoot.generate(worldIn, rand, pos);
			}

			double widthMul = 1 - ((i > steps / 2) ? ((steps - i) / (float)steps * 2) : (i / (float)steps * 2));

			int radius = (int)((rand.nextDouble() * (this.maxWidth - this.minWidth) * widthMul) + this.minWidth);
			for(int xo = -radius; xo <= radius; xo++) {
				for(int yo = -radius; yo <= radius; yo++) {
					for(int zo = -radius; zo <= radius; zo++) {
						BlockPos genPos = pos.add(xo, yo, zo);
						if(worldIn.isAirBlock(genPos)) {
							if(Math.sqrt(xo*xo+yo*yo+zo*zo) <= radius) {
								worldIn.setBlockState(genPos, bark, 2);
							}
						}

						if(!this.genLeaves && worldIn.rand.nextInt(30) == 0 && yo >= radius - 1 && Math.sqrt(xo*xo+yo*yo+zo*zo) <= radius && worldIn.isAirBlock(genPos.up()) && worldIn.isAirBlock(genPos.up(2))) {
							int maxRootHeight = 2 + worldIn.rand.nextInt(3);
							for(int r = 0; r < maxRootHeight; r++) {
								BlockPos rootPos = genPos.add(0, 1 + r, 0);
								if(worldIn.isAirBlock(rootPos)) {
									worldIn.setBlockState(rootPos, root, 2);
								} else {
									break;
								}
							}
						}
					}
				}
			}
		}

		if(this.genLeaves) {
			for(int i = 0; i < steps; i++) {
				BlockPos pos = new BlockPos(spline.interpolate(i / (float)steps));

				double widthMul = 1 - ((i > steps / 2) ? ((steps - i) / (float)steps * 2) : (i / (float)steps * 2));

				int radius = 1 + (int)((rand.nextDouble() * (this.maxWidth - this.minWidth) * widthMul) + this.minWidth);
				for(int xo = -radius; xo <= radius; xo++) {
					for(int yo = -radius; yo <= radius; yo++) {
						for(int zo = -radius; zo <= radius; zo++) {
							BlockPos genPos = pos.add(xo, yo, zo);
							if(worldIn.isAirBlock(genPos)) {
								if(Math.sqrt(xo*xo+yo*yo+zo*zo) > radius - 1 && Math.sqrt(xo*xo+yo*yo+zo*zo) <= radius) {
									worldIn.setBlockState(genPos, leaves, 2);
								}
							}
						}
					}
				}
			}
		} else {
			for(int i = 0; i < steps; i++) {
				BlockPos pos = new BlockPos(spline.interpolate(i / (float)steps));

				double widthMul = 1 - ((i > steps / 2) ? ((steps - i) / (float)steps * 2) : (i / (float)steps * 2));

				int radius = 1 + (int)((rand.nextDouble() * (this.maxWidth - this.minWidth) * widthMul) + this.minWidth);
				for(int xo = -radius; xo <= radius; xo++) {
					for(int yo = -radius; yo <= radius; yo++) {
						for(int zo = -radius; zo <= radius; zo++) {
							BlockPos genPos = pos.add(xo, yo, zo);
							if(worldIn.rand.nextInt(80) == 0 && yo >= radius - 2 && Math.sqrt(xo*xo+yo*yo+zo*zo) <= radius - 1 && !worldIn.isAirBlock(genPos) && worldIn.isAirBlock(genPos.up()) && worldIn.isAirBlock(genPos.up(2))) {
								int maxRootHeight = 2 + worldIn.rand.nextInt(3);
								for(int r = 0; r < maxRootHeight; r++) {
									BlockPos rootPos = genPos.add(0, 1 + r, 0);
									if(worldIn.isAirBlock(rootPos)) {
										worldIn.setBlockState(rootPos, root, 2);
									} else {
										break;
									}
								}
							}
							if(worldIn.rand.nextInt(20) == 0 && yo <= radius - 2 && Math.sqrt(xo*xo+yo*yo+zo*zo) <= radius - 1 && !worldIn.isAirBlock(genPos) && worldIn.isAirBlock(genPos.down()) && worldIn.isAirBlock(genPos.down(2))) {
								int maxHangersHeight = 3 + worldIn.rand.nextInt(16);
								for(int r = 0; r < maxHangersHeight; r++) {
									BlockPos hangerPos = genPos.add(0, -1 - r, 0);
									if(worldIn.isAirBlock(hangerPos)) {
										worldIn.setBlockState(hangerPos, hanger, 2);
									} else {
										break;
									}
								}
							}
							if(worldIn.rand.nextInt(5) == 0 && Math.sqrt(xo*xo+yo*yo+zo*zo) <= radius && worldIn.isAirBlock(genPos)) {
								List<EnumFacing> dirs = Arrays.asList(Arrays.copyOf(EnumFacing.VALUES, EnumFacing.VALUES.length));
								Collections.shuffle(dirs, rand);
								for(EnumFacing facing : dirs) {
									if(worldIn.getBlockState(genPos.offset(facing)) == bark) {
										worldIn.setBlockState(genPos, BlockRegistry.MOSS.getDefaultState().withProperty(BlockMoss.FACING, facing.getOpposite()));
									}
								}
							}
						}
					}
				}
			}
		}

		return generated;
	}
}