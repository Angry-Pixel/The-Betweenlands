package thebetweenlands.common.world.gen.feature.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.IBlockStateAccessOnly;
import thebetweenlands.util.BlockPosUtil;
import thebetweenlands.util.CatmullRomSpline;
import thebetweenlands.util.ISpline;
import thebetweenlands.util.MathUtil;

import java.util.*;

public class WorldGenGiantRoot extends WorldGenHelper {
    public final BlockPos start, end;

    protected double minWidth, maxWidth, randXZOffsetRange, randYOffsetRange, maxArcHeight;
    protected boolean genLeafyBranches, genSmallerRoots, genLeaves, genFungi;

    protected AABB genBounds;

    public WorldGenGiantRoot(BlockPos start, BlockPos end, int archHeight) {
        this(start, end, 2, 3, 8, 5, archHeight, true, false, true, true, null);
    }

    public WorldGenGiantRoot(BlockPos start, BlockPos end, double minWidth, double maxWidth, double randXZOffsetRange, double randYOffsetRange, double maxArcHeight, boolean genLeafyBranches, boolean genLeaves, boolean genFungi, boolean genSmallerRoots, AABB genBounds) {
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

    public WorldGenGiantRoot setMaxWidth(int size) {
        this.maxWidth = size;
        return this;
    }

    public WorldGenGiantRoot setMinWidth(int size) {
        this.minWidth = size;
        return this;
    }

    public WorldGenGiantRoot setGenBounds(AABB aabb) {
        this.genBounds = aabb;
        return this;
    }

    @Override
    public boolean generate(WorldGenLevel worldIn, Random rand, BlockPos position) {
        return this.generate(IBlockStateAccessOnly.from(worldIn), 0, 0, false, rand, position);
    }

    @Override
    public boolean generate(Level worldIn, Random rand, BlockPos position) {
        return this.generate(IBlockStateAccessOnly.from(worldIn), 0, 0, false, rand, position);
    }

    public boolean generate(IBlockStateAccessOnly worldIn, int chunkX, int chunkZ, boolean checkChunk, Random rand, BlockPos position) {
        boolean generated = true;

        Vec3 up = new Vec3(0, 1, 0);
        Vec3 diff = BlockPosUtil.toVec3(this.end.subtract(this.start));
        Vec3 dir = diff.normalize();

        Vec3 offsetDir = up.cross(dir);

        Vec3 controlPtStart = BlockPosUtil.toVec3(this.start).subtract(dir).add(0, -16, 0);

        Vec3 controlPtEnd = BlockPosUtil.toVec3(this.end).add(dir).add(0, -16, 0);

        List<Vec3> pts = new ArrayList<>();

        pts.add(controlPtStart);
        pts.add(BlockPosUtil.toVec3(this.start));

        int randPts = 5;
        for(int i = 0; i < randPts; i++) {
            Vec3 pt = BlockPosUtil.toVec3(this.start).add(diff.scale(i / (float)randPts)).add(offsetDir.x * (rand.nextDouble() - 0.5D) * this.randXZOffsetRange * 2.0D, (rand.nextDouble() - 0.5D) * this.randYOffsetRange * 2.0D + Math.sin(i / (float)randPts * Math.PI) * this.maxArcHeight, offsetDir.z * (rand.nextDouble() - 0.5D) * this.randXZOffsetRange * 2.0D);
            pts.add(pt);
        }

        pts.add(BlockPosUtil.toVec3(this.end));
        pts.add(controlPtEnd);

        ISpline spline = new CatmullRomSpline(pts.toArray(new Vec3[0]));

        BlockState bark = BlockRegistry.GIANT_ROOT.get().defaultBlockState();
        BlockState leaves = BlockRegistry.LEAVES_WEEDWOOD_TREE.get().defaultBlockState();//.withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);
        BlockState root = Blocks.DRIPSTONE_BLOCK.defaultBlockState(); // BlockRegistry.ROOT.get().defaultBlockState();
        BlockState hanger = BlockRegistry.HANGER.get().defaultBlockState();//.withProperty(BlockHanger.CAN_GROW, false);
        BlockState fungus = BlockRegistry.SHELF_FUNGUS.get().defaultBlockState();

        int steps = 20 + (int)(this.start.distSqr(this.end) * 4);//MathHelper.ceil(spline.getArcLength()) * 4;

        long rootWidthRandSeed = rand.nextLong();
        Random rootWidthRand = new Random();

        Random smallerRootRand = new Random();
        smallerRootRand.setSeed(rand.nextLong());

        rootWidthRand.setSeed(rootWidthRandSeed);
        BlockPos prevPos = BlockPos.ZERO;
        for(int i = 0; i < steps; i++) {
            BlockPos pos = new BlockPos(spline.interpolate(i / (float)steps));

            if(pos.equals(prevPos)) {
                continue;
            }
            prevPos = pos;

            if(this.genLeafyBranches && smallerRootRand.nextInt(12) == 0) {
                WorldGenGiantRoot smallRoot = new WorldGenGiantRoot(pos, pos.offset(offsetDir.x * (smallerRootRand.nextDouble() - 0.5D) * 16, smallerRootRand.nextDouble() * 8, offsetDir.z * (smallerRootRand.nextDouble() - 0.5D) * 16), 0.5D, 0.5D, 3, 3, 0, false, true, false, false, this.genBounds);
                smallRoot.generate(worldIn, chunkX, chunkZ, checkChunk, smallerRootRand, pos);
            }

            if(this.genSmallerRoots && smallerRootRand.nextInt(12) == 0) {
                WorldGenGiantRoot smallRoot = new WorldGenGiantRoot(pos, pos.offset(offsetDir.x * (smallerRootRand.nextDouble() - 0.5D) * 16, -smallerRootRand.nextDouble() * 8, offsetDir.z * (smallerRootRand.nextDouble() - 0.5D) * 16), 0.5D, 0.5D, 3, 3, 0, false, false, false, false, this.genBounds);
                smallRoot.generate(worldIn, chunkX, chunkZ, checkChunk, smallerRootRand, pos);
            }

            double widthMul = 1 - ((i > steps / 2) ? ((steps - i) / (float)steps * 2) : (i / (float)steps * 2));

            int radius = (int)((rootWidthRand.nextDouble() * (this.maxWidth - this.minWidth) * widthMul) + this.minWidth);

            if(checkChunk && !this.shouldCheckAtPos(pos, chunkX, chunkZ, radius)) {
                continue;
            }

            for(int xo = -radius; xo <= radius; xo++) {
                for(int yo = -radius; yo <= radius; yo++) {
                    for(int zo = -radius; zo <= radius; zo++) {
                        BlockPos genPos = pos.offset(xo, yo, zo);
                        if(this.isInBounds(genPos)) {
                            if(xo*xo+yo*yo+zo*zo <= radius*radius) {
                                worldIn.setBlockState(genPos, bark, 2);
                            }
                        }
                    }
                }
            }
        }

        if(this.genLeaves) {
            rootWidthRand.setSeed(rootWidthRandSeed);
            prevPos = BlockPos.ZERO;
            for(int i = 0; i < steps; i++) {
                BlockPos pos = new BlockPos(spline.interpolate(i / (float)steps));

                if(pos.equals(prevPos)) {
                    continue;
                }
                prevPos = pos;

                double widthMul = 1 - ((i > steps / 2) ? ((steps - i) / (float)steps * 2) : (i / (float)steps * 2));

                int radius = 1 + (int)((rootWidthRand.nextDouble() * (this.maxWidth - this.minWidth) * widthMul) + this.minWidth);

                if(checkChunk && !this.shouldCheckAtPos(pos, chunkX, chunkZ, radius)) {
                    continue;
                }

                for(int xo = -radius; xo <= radius; xo++) {
                    for(int yo = -radius; yo <= radius; yo++) {
                        for(int zo = -radius; zo <= radius; zo++) {
                            BlockPos genPos = pos.offset(xo, yo, zo);
                            if(this.isInBounds(genPos) && worldIn.getBlockState(genPos) != bark) {
                                if(xo*xo+yo*yo+zo*zo > (radius-1)*(radius-1) && xo*xo+yo*yo+zo*zo <= radius*radius) {
                                    worldIn.setBlockState(genPos, leaves, 2);
                                }
                            }
                        }
                    }
                }
            }
        }

        if(this.genFungi) {
            rootWidthRand.setSeed(rootWidthRandSeed);
            prevPos = BlockPos.ZERO;
            for(int i = 0; i < steps; i++) {
                BlockPos pos = new BlockPos(spline.interpolate(i / (float)steps));

                if(pos.equals(prevPos)) {
                    continue;
                }
                prevPos = pos;

                double widthMul = 1 - ((i > steps / 2) ? ((steps - i) / (float)steps * 2) : (i / (float)steps * 2));

                int radius = (int)((rootWidthRand.nextDouble() * (this.maxWidth - this.minWidth) * widthMul) + this.minWidth) - 1;

                if(checkChunk && !this.shouldCheckAtPos(pos, chunkX, chunkZ, radius + 4 + radius / 5)) {
                    continue;
                }

                for(int xo = -radius; xo <= radius; xo++) {
                    for(int yo = -radius+1; yo <= radius-1; yo++) {
                        for(int zo = -radius; zo <= radius; zo++) {
                            BlockPos genPos = pos.offset(xo, yo, zo);
                            if(xo*xo+yo*yo+zo*zo >= radius*radius) {
                                Random fungiRand = new Random();
                                fungiRand.setSeed(MathUtil.getCoordinateRandom(genPos.getX(), genPos.getY(), genPos.getZ()));
                                if(fungiRand.nextInt(160) == 0) {
                                    int fungiRadius = fungiRand.nextInt(2) + radius / 5 + 2;
                                    for(int fx = -fungiRadius; fx <= fungiRadius; fx++) {
                                        for(int fz = -fungiRadius; fz <= fungiRadius; fz++) {
                                            BlockPos fungiPos = genPos.offset(fx, 0, fz);
                                            if((fx+0.5D)*(fx+0.5D) + (fz+0.5D)*(fz+0.5D) <= fungiRadius*fungiRadius && this.isInBounds(fungiPos) && worldIn.getBlockState(fungiPos) != bark) {
                                                worldIn.setBlockState(fungiPos, fungus);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Random foliageRand = new Random(); //Use a seperate rand foliage so that it doesn't mess with the main rand when skipped!
        foliageRand.setSeed(rand.nextLong());

        rootWidthRand.setSeed(rootWidthRandSeed);
        prevPos = BlockPos.ZERO;
        for(int i = 0; i < steps; i++) {
            BlockPos pos = new BlockPos(spline.interpolate(i / (float)steps));

            if(pos.equals(prevPos)) {
                continue;
            }
            prevPos = pos;

            double widthMul = 1 - ((i > steps / 2) ? ((steps - i) / (float)steps * 2) : (i / (float)steps * 2));

            int radius = 1 + (int)((rootWidthRand.nextDouble() * (this.maxWidth - this.minWidth) * widthMul) + this.minWidth);

            if(checkChunk && !this.shouldCheckAtPos(pos, chunkX, chunkZ, radius)) {
                continue;
            }

            for(int xo = -radius; xo <= radius; xo++) {
                for(int yo = -radius; yo <= radius; yo++) {
                    for(int zo = -radius; zo <= radius; zo++) {
                        BlockPos genPos = pos.offset(xo, yo, zo);

                        if(!this.genLeaves) {
                            //Roots and hangers are just +-Y offsets, doesn't care about surrounding chunks
                            if(this.isInBounds(genPos) && foliageRand.nextInt(60) == 0 && yo >= radius - 2 && xo*xo+yo*yo+zo*zo <= (radius-1)*(radius-1) && !worldIn.isAirBlock(genPos) && worldIn.isAirBlock(genPos.above()) && worldIn.isAirBlock(genPos.above(2))) {
                                int maxRootHeight = 2 + foliageRand.nextInt(3);
                                for(int r = 0; r < maxRootHeight; r++) {
                                    BlockPos rootPos = genPos.offset(0, 1 + r, 0);
                                    if(worldIn.isAirBlock(rootPos)) {
                                        worldIn.setBlockState(rootPos, root, 2);
                                    } else {
                                        break;
                                    }
                                }
                            }
                            if(this.isInBounds(genPos) && foliageRand.nextInt(10) == 0 && yo <= radius - 2 && xo*xo+yo*yo+zo*zo <= (radius-1)*(radius-1) && !worldIn.isAirBlock(genPos) && worldIn.isAirBlock(genPos.below()) && worldIn.isAirBlock(genPos.below(2))) {
                                int maxHangersHeight = 3 + foliageRand.nextInt(16);
                                for(int r = 0; r < maxHangersHeight; r++) {
                                    BlockPos hangerPos = genPos.offset(0, -1 - r, 0);
                                    if(worldIn.isAirBlock(hangerPos)) {
                                        worldIn.setBlockState(hangerPos, hanger, 2);
                                    } else {
                                        break;
                                    }
                                }
                            }

                            //Moss is just one block so it needn't care about other chunks either, if it gets replaced so be it
                            if(this.isInBounds(genPos) && foliageRand.nextInt(10) == 0 && xo*xo+yo*yo+zo*zo > (radius-1)*(radius-1) && worldIn.isAirBlock(genPos)) {
                                List<Direction> dirs = Arrays.asList(Arrays.copyOf(Direction.values(), Direction.values().length));
                                Collections.shuffle(dirs, foliageRand);
                                for(Direction facing : dirs) {
                                    //Check if offset pos is giant root
                                    if((xo+facing.getStepX())*(xo+facing.getStepX())+(yo+facing.getStepY())*(yo+facing.getStepY())+(zo+facing.getStepZ())*(zo+facing.getStepZ()) <= (radius-1)*(radius-1)) {
                                        // Oops, multi face block implementation may have broken this
                                        //worldIn.setBlockState(genPos, BlockRegistry.MOSS.get().defaultBlockState().setValue(BetweenlandsMoss., facing.getOpposite()), 2);
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

    protected boolean shouldCheckAtPos(BlockPos pos, int chunkX, int chunkZ, int radius) {
        double dx = pos.getX() >= chunkX * 16 && pos.getX() <= chunkX * 16 + 16 ? 0 : Math.min(Math.abs(chunkX * 16 - pos.getX()), Math.abs(pos.getX() - (chunkX * 16 + 16)));
        double dz = pos.getZ() >= chunkZ * 16 && pos.getZ() <= chunkZ * 16 + 16 ? 0 : Math.min(Math.abs(chunkZ * 16 - pos.getZ()), Math.abs(pos.getZ() - (chunkZ * 16 + 16)));
        return dx*dx + dz*dz <= radius*radius;
    }

    protected boolean isInBounds(BlockPos pos) {
        return this.genBounds == null || (this.genBounds.minX <= pos.getX() && this.genBounds.minY <= pos.getY() && this.genBounds.minZ <= pos.getZ() && this.genBounds.maxX >= pos.getX() && this.genBounds.maxY >= pos.getY() && this.genBounds.maxZ >= pos.getZ());
    }
}
