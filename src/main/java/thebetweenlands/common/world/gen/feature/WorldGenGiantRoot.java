package thebetweenlands.common.world.gen.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.plant.BlockMoss;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.IBlockStateAccessOnly;
import thebetweenlands.util.CatmullRomSpline;
import thebetweenlands.util.ISpline;

public class WorldGenGiantRoot extends WorldGenerator {
	public final BlockPos start, end;

	protected double minWidth, maxWidth, randXZOffsetRange, randYOffsetRange, maxArcHeight;
	protected boolean genLeafyBranches, genSmallerRoots, genLeaves, genFungi;

	protected AxisAlignedBB genBounds;

	public WorldGenGiantRoot(BlockPos start, BlockPos end, int archHeight) {
		this(start, end, 2, 3, 8, 5, archHeight, true, false, true, true, null);
	}

	public WorldGenGiantRoot(BlockPos start, BlockPos end, double minWidth, double maxWidth, double randXZOffsetRange, double randYOffsetRange, double maxArcHeight, boolean genLeafyBranches, boolean genLeaves, boolean genFungi, boolean genSmallerRoots, AxisAlignedBB genBounds) {
		this.start = start;
		this.end = end;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.randXZOffsetRange = randXZOffsetRange;
		this.randYOffsetRange = randYOffsetRange;
		this.maxArcHeight = maxArcHeight;
		this.genLeafyBranches = genLeafyBranches;
		this.genLeaves = genLeaves;
		this.genFungi = genFungi;
		this.genSmallerRoots = genSmallerRoots;
		this.genBounds = genBounds;
	}

	public WorldGenGiantRoot setGenBounds(AxisAlignedBB aabb) {
		this.genBounds = aabb;
		return this;
	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		return this.generate(IBlockStateAccessOnly.from(worldIn), 0, 0, false, rand, position);
	}

	public boolean generate(IBlockStateAccessOnly worldIn, int chunkX, int chunkZ, boolean checkChunk, Random rand, BlockPos position) {
		boolean generated = true;

		//System.out.println("START TEST");

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

		//long time = System.nanoTime();
		//System.out.println("REPARAM");

		ISpline spline = /*new ReparameterizedSpline(*/new CatmullRomSpline(pts.toArray(new Vec3d[0]))/*).init(MathHelper.ceil(diff.lengthVector() / 8.0D) + 2, 8)*/;

		//System.out.println("REPARAM DONE: " + ((System.nanoTime() - time) / 1000000.0F));

		IBlockState bark = BlockRegistry.GIANT_ROOT_BLOCK.getDefaultState();
		IBlockState leaves = BlockRegistry.LEAVES_WEEDWOOD_TREE.getDefaultState();
		IBlockState root = BlockRegistry.ROOT.getDefaultState();
		IBlockState hanger = BlockRegistry.HANGER.getDefaultState();

		int steps = 20 + (int)(this.start.getDistance(this.end.getX(), this.end.getY(), this.end.getZ()) * 4);//MathHelper.ceil(spline.getArcLength()) * 4;

		BlockPos prevPos = BlockPos.ORIGIN;
		for(int i = 0; i < steps; i++) {
			BlockPos pos = new BlockPos(spline.interpolate(i / (float)steps));

			if(pos.equals(prevPos)) {
				continue;
			}

			//System.out.println("TEST");

			if(this.genLeafyBranches && rand.nextInt(20) == 0) {
				WorldGenGiantRoot smallRoot = new WorldGenGiantRoot(pos, pos.add(offsetDir.x * (rand.nextDouble() - 0.5D) * 16, rand.nextDouble() * 8, offsetDir.z * (rand.nextDouble() - 0.5D) * 16), 0.5D, 0.5D, 3, 3, 0, false, true, false, false, this.genBounds);
				smallRoot.generate(worldIn, chunkX, chunkZ, checkChunk, rand, pos);
			}

			if(this.genSmallerRoots && rand.nextInt(20) == 0) {
				WorldGenGiantRoot smallRoot = new WorldGenGiantRoot(pos, pos.add(offsetDir.x * (rand.nextDouble() - 0.5D) * 16, -rand.nextDouble() * 8, offsetDir.z * (rand.nextDouble() - 0.5D) * 16), 0.5D, 0.5D, 3, 3, 0, false, false, false, false, this.genBounds);
				smallRoot.generate(worldIn, chunkX, chunkZ, checkChunk, rand, pos);
			}

			double widthMul = 1 - ((i > steps / 2) ? ((steps - i) / (float)steps * 2) : (i / (float)steps * 2));

			int radius = (int)((rand.nextDouble() * (this.maxWidth - this.minWidth) * widthMul) + this.minWidth);
			
			if(checkChunk && !this.shouldCheckAtPos(pos, chunkX, chunkZ, radius)) {
				continue;
			}
			
			for(int xo = -radius; xo <= radius; xo++) {
				for(int yo = -radius; yo <= radius; yo++) {
					for(int zo = -radius; zo <= radius; zo++) {
						BlockPos genPos = pos.add(xo, yo, zo);
						if(this.isInBounds(genPos)) {
							if(Math.sqrt(xo*xo+yo*yo+zo*zo) <= radius) {
								worldIn.setBlockState(genPos, bark, 2);
							}
						}
					}
				}
			}
		}

		if(this.genLeaves) {
			prevPos = BlockPos.ORIGIN;
			for(int i = 0; i < steps; i++) {
				BlockPos pos = new BlockPos(spline.interpolate(i / (float)steps));

				if(pos.equals(prevPos)) {
					continue;
				}

				double widthMul = 1 - ((i > steps / 2) ? ((steps - i) / (float)steps * 2) : (i / (float)steps * 2));

				int radius = 1 + (int)((rand.nextDouble() * (this.maxWidth - this.minWidth) * widthMul) + this.minWidth);
				
				if(checkChunk && !this.shouldCheckAtPos(pos, chunkX, chunkZ, radius)) {
					continue;
				}
				
				for(int xo = -radius; xo <= radius; xo++) {
					for(int yo = -radius; yo <= radius; yo++) {
						for(int zo = -radius; zo <= radius; zo++) {
							BlockPos genPos = pos.add(xo, yo, zo);
							if(this.isInBounds(genPos) && worldIn.getBlockState(genPos) != bark) {
								if(Math.sqrt(xo*xo+yo*yo+zo*zo) > radius - 1 && Math.sqrt(xo*xo+yo*yo+zo*zo) <= radius) {
									worldIn.setBlockState(genPos, leaves, 2);
								}
							}
						}
					}
				}
			}
		}

		/*List<Fungus> fungi = new ArrayList<>();

		prevPos = BlockPos.ORIGIN;
		for(int i = 0; i < steps; i++) {
			BlockPos pos = new BlockPos(spline.interpolate(i / (float)steps));

			if(checkChunk && !this.shouldCheckAtPos(pos, chunkX, chunkZ)) {
				continue;
			}

			if(pos.equals(prevPos)) {
				continue;
			}

			double widthMul = 1 - ((i > steps / 2) ? ((steps - i) / (float)steps * 2) : (i / (float)steps * 2));

			int radius = 1 + (int)((rand.nextDouble() * (this.maxWidth - this.minWidth) * widthMul) + this.minWidth);
			for(int xo = -radius; xo <= radius; xo++) {
				for(int yo = -radius; yo <= radius; yo++) {
					for(int zo = -radius; zo <= radius; zo++) {
						BlockPos genPos = pos.add(xo, yo, zo);
						if(this.genFungi) {
							if(rand.nextInt(80) == 0 && Math.sqrt(xo*xo+yo*yo+zo*zo) >= radius) {
								if(worldIn.isAirBlock(genPos)) {
									for(EnumFacing offset : EnumFacing.HORIZONTALS) {
										if(worldIn.getBlockState(genPos.offset(offset)) == bark && worldIn.getBlockState(genPos.offset(offset).down()) == bark && worldIn.getBlockState(genPos.offset(offset).up()) == bark) {
											float distance = MathHelper.sqrt(xo * xo + zo * zo);
											int fungusX = (int) (-xo / distance * 2);
											int fungusZ = (int) (-zo / distance * 2);
											int fungusRadius = rand.nextInt(2) + radius / 5 + 2;
											fungi.add(new Fungus(genPos.add(fungusX, 0, fungusZ), fungusRadius));
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		for (Fungus fungus : fungi) {
			fungus.generate(worldIn, rand);
		}*/

		for(int i = 0; i < steps; i++) {
			BlockPos pos = new BlockPos(spline.interpolate(i / (float)steps));

			double widthMul = 1 - ((i > steps / 2) ? ((steps - i) / (float)steps * 2) : (i / (float)steps * 2));

			int radius = 1 + (int)((rand.nextDouble() * (this.maxWidth - this.minWidth) * widthMul) + this.minWidth);
			
			Random foliageRand = new Random(); //Use a seperate rand foliage so that it doesn't mess with the main rand when skipped!
			foliageRand.setSeed(rand.nextLong());
			
			if(checkChunk && !this.shouldCheckAtPos(pos, chunkX, chunkZ, radius)) {
				continue;
			}
			
			for(int xo = -radius; xo <= radius; xo++) {
				for(int yo = -radius; yo <= radius; yo++) {
					for(int zo = -radius; zo <= radius; zo++) {
						BlockPos genPos = pos.add(xo, yo, zo);

						if(!this.genLeaves) {
							//Roots and hangers are just +-Y offsets, doesn't care about surrounding chunks
							if(this.isInBounds(genPos) && foliageRand.nextInt(100) == 0 && yo >= radius - 2 && Math.sqrt(xo*xo+yo*yo+zo*zo) <= radius - 1 && !worldIn.isAirBlock(genPos) && worldIn.isAirBlock(genPos.up()) && worldIn.isAirBlock(genPos.up(2))) {
								int maxRootHeight = 2 + foliageRand.nextInt(3);
								for(int r = 0; r < maxRootHeight; r++) {
									BlockPos rootPos = genPos.add(0, 1 + r, 0);
									if(worldIn.isAirBlock(rootPos)) {
										worldIn.setBlockState(rootPos, root, 2);
									} else {
										break;
									}
								}
							}
							if(this.isInBounds(genPos) && foliageRand.nextInt(15) == 0 && yo <= radius - 2 && Math.sqrt(xo*xo+yo*yo+zo*zo) <= radius - 1 && !worldIn.isAirBlock(genPos) && worldIn.isAirBlock(genPos.down()) && worldIn.isAirBlock(genPos.down(2))) {
								int maxHangersHeight = 3 + foliageRand.nextInt(16);
								for(int r = 0; r < maxHangersHeight; r++) {
									BlockPos hangerPos = genPos.add(0, -1 - r, 0);
									if(worldIn.isAirBlock(hangerPos)) {
										worldIn.setBlockState(hangerPos, hanger, 2);
									} else {
										break;
									}
								}
							}
							
							//Moss is just one block so it needn't care about other chunks either, if it gets replaced so be it
							if(this.isInBounds(genPos) && foliageRand.nextInt(15) == 0 && Math.sqrt(xo*xo+yo*yo+zo*zo) > radius - 1 && Math.sqrt(xo*xo+yo*yo+zo*zo) <= radius - 0.5D && worldIn.isAirBlock(genPos)) {
								EnumFacing facing = EnumFacing.getFacingFromVector(xo, yo, zo);
								worldIn.setBlockState(genPos, BlockRegistry.MOSS.getDefaultState().withProperty(BlockMoss.FACING, facing), 2);
								
								/*List<EnumFacing> dirs = Arrays.asList(Arrays.copyOf(EnumFacing.VALUES, EnumFacing.VALUES.length));
								Collections.shuffle(dirs, rand);
								for(EnumFacing facing : dirs) {
									if(worldIn.getBlockState(genPos.offset(facing)) == bark) {
										worldIn.setBlockState(genPos, BlockRegistry.MOSS.getDefaultState().withProperty(BlockMoss.FACING, facing.getOpposite()), 2);
									}
								}*/
							}
						}
					}
				}
			}
		}

		return generated;
	}

	protected boolean shouldCheckAtPos(BlockPos pos, int chunkX, int chunkZ, int radius) {
		double dx = pos.getX() >= chunkX * 16 && pos.getX() <= chunkX * 16 + 16 ? 0 : Math.min(Math.abs(chunkX * 16 - pos.getX()), Math.abs(pos.getX() - (chunkX * 16 + 16)));
		double dz = pos.getZ() >= chunkZ * 16 && pos.getZ() <= chunkZ * 16 + 16 ? 0 : Math.min(Math.abs(chunkZ * 16 - pos.getZ()), Math.abs(pos.getZ() - (chunkZ * 16 + 16)));
		return Math.sqrt(dx*dx + dz*dz) <= radius;
	}

	protected boolean isInBounds(BlockPos pos) {
		return this.genBounds == null || (this.genBounds.minX <= pos.getX() && this.genBounds.minY <= pos.getY() && this.genBounds.minZ <= pos.getZ() && this.genBounds.maxX >= pos.getX() && this.genBounds.maxY >= pos.getY() && this.genBounds.maxZ >= pos.getZ());
	}
}