package thebetweenlands.common.world.gen.feature.tree;

import com.mojang.serialization.Codec;
import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.WorldGenHelper;
import thebetweenlands.util.CatmullRomSpline;
import thebetweenlands.util.ISpline;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class HearthgroveTree extends WorldGenHelper<NoneFeatureConfiguration> {

	public HearthgroveTree(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return this.generate(context.level(), context.random(), context.origin());
	}

	public boolean generate(LevelAccessor level, RandomSource rand, BlockPos pos) {
		final int x = pos.getX();
		final int y = pos.getY();
		final int z = pos.getZ();

		int height = rand.nextInt(4) + 9;
		int canopySize = (int)(((rand.nextDouble() * height) / 12.0D) * 4 + 5);

		if(!this.rotatedCubeCantReplace(level, x, y + 2, z, 0, 0, 0, 1, height, 1, 0) &&
			!this.rotatedCubeCantReplace(level, x, y + 2, z, -canopySize+2, 0, -canopySize+2, canopySize*2-4, height, canopySize*2-4, 0)) {

			BlockState log = BlockRegistry.HEARTHGROVE_LOG.get().defaultBlockState();
			BlockState logY = BlockRegistry.HEARTHGROVE_LOG.get().defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
			BlockState leaves = BlockRegistry.HEARTHGROVE_LEAVES.get().defaultBlockState();
			BlockState hangers = BlockRegistry.HANGER.get().defaultBlockState();

			int rootHeight = rand.nextInt(3) + 1;

			int canopyStart = canopySize / 2 - 1;

			for(int i = rootHeight; i <= height - canopyStart - 1; i++) {
				this.setBlock(level, pos.above(i), logY);
			}

			List<BlockPos> blobs = new ArrayList<>();
			for(int i = 0; i < 4; i++) {
				blobs.add(new BlockPos(x + rand.nextInt(canopySize*2) - canopySize, y + height - canopyStart / 2, z + rand.nextInt(canopySize*2) - canopySize));
			}

			double circumference = Math.PI * (canopySize - 1);

			int branches = (int)(circumference / 4.0D) + 1;

			double angleStep = Math.PI * 2 / (branches-1);

			List<Pair<BlockPos, Float>> leavesSupportPoints = new ArrayList<>();

			List<BlockPos> subbranchEndpoints = new ArrayList<>();

			if(canopySize > 3) {
				for(int i = 0; i < branches; i++) {
					BlockPos branchStart = pos.above(height - canopyStart - 2);
					double randRot = rand.nextDouble() * angleStep / 3.0D;
					BlockPos branchEnd = pos.offset((int) (Math.cos(Math.PI * 2 / (branches-1) * i + randRot) * (canopySize - 4)), height - canopyStart, (int) (Math.sin(Math.PI * 2 / (branches-1) * i + randRot) * (canopySize - 4)));

					List<Vec3> branchPoints = new ArrayList<>();

					branchPoints.add(new Vec3(branchStart.above().getCenter().toVector3f()));
					branchPoints.add(new Vec3(branchStart.getCenter().toVector3f()));


					branchPoints.add(new Vec3(branchEnd.getCenter().toVector3f()));
					branchPoints.add(new Vec3(branchEnd.above().getCenter().toVector3f()));

					ISpline spline = new CatmullRomSpline(branchPoints.toArray(new Vec3[0]));

					BlockPos setBranchPos = null;


					boolean support = false;
					int steps = 30;
					for(int j = 0; j < steps; j++) {
						BlockPos branchPos = BlockPos.containing(spline.interpolate(1.0F / (steps-1) * j));
						if(!branchPos.equals(setBranchPos) && level.getBlockState(branchPos).isAir()) {
							this.setBlock(level, branchPos, log);

							if(!support && branchPos.distToCenterSqr(x, branchPos.getY(), z) >= 2.5D) {
								Direction supportDir = Direction.getNearest(branchPos.getX() - x, 0, branchPos.getZ() - z);

								int supportYO = 0;
								int supportXO = 0;
								int supportZO = 0;

								int supportHeight = height - (branchPos.getY() - y) + (canopySize <= 6 ? -1 : 0);

								for(int k = 0; k < supportHeight; k++) {
									this.setBlock(level,branchPos.offset(supportXO, 1 + supportYO, supportZO), logY);

									if((k+1) % 3 == 0) {
										supportXO += supportDir.getStepX();
										supportZO += supportDir.getStepZ();
										k++;
									}
									supportYO++;
								}

								leavesSupportPoints.add(Pair.of(branchPos.offset(supportXO, 1 + supportYO, supportZO), 6.0F));

								support = true;
							}
						}
						setBranchPos = branchPos;
					}
					if(setBranchPos != null) {
						leavesSupportPoints.add(Pair.of(setBranchPos, 10.0F));
						if(canopySize >= 5) {
							subbranchEndpoints.add(setBranchPos);
						}
					}
				}
			}

			float centerSquashX = 0.8F + rand.nextFloat() * 0.4F;
			float centerSquashZ = 0.8F + rand.nextFloat() * 0.4F;
			for(int i = 0; i < canopyStart + 2; i++) {
				final int k = i;
				this.generateCanopyLayer(level, pos.above(height - canopyStart + i), canopySize - i, leaves, p -> {
					boolean nearSupport = false;
					Random supportRand = new Random();
					for(Pair<BlockPos, Float> support : leavesSupportPoints) {
						BlockPos supportPos = support.getKey();
						supportRand.setSeed(Mth.getSeed(supportPos.getX(), supportPos.getY(), supportPos.getZ()));
						float squashX = 0.6F + supportRand.nextFloat() * 1.2F;
						float squashY = 0.6F + supportRand.nextFloat() * 1.2F;
						float squashZ = 0.6F + supportRand.nextFloat() * 1.2F;
						if((supportPos.getX()-p.getX())*(supportPos.getX()-p.getX())*squashX +
							(supportPos.getY()-p.getY())*(supportPos.getY()-p.getY())*squashY +
							(supportPos.getZ()-p.getZ())*(supportPos.getZ()-p.getZ())*squashZ <= support.getValue() + rand.nextFloat() * 0.8F) {
							nearSupport = true;
							break;
						}
					}
					if(!nearSupport) return false;
					return level.getBlockState(p).isAir() && (k > 1 || (canopySize - k) < 4 || Math.sqrt((p.getX()-x)*(p.getX()-x)*centerSquashX+(p.getZ()-z)*(p.getZ()-z)*centerSquashZ) >= canopySize - k - 3 - rand.nextFloat() * 0.8F);
				}, p -> {
					if(k == 0 && rand.nextInt(6) == 0) {
						int hangerLength = rand.nextInt(5) + 1;
						for(int yo = 0; yo < hangerLength; yo++) {
							BlockPos hangerPos = p.below(1 + yo);
							if(level.getBlockState(hangerPos).isAir()) {
								this.setBlock(level, hangerPos, hangers);
							} else {
								break;
							}
						}
					}
				});
			}

			for(BlockPos endpoint : subbranchEndpoints) {
				Direction branchDir = Direction.getNearest(endpoint.getX() - x, 0, endpoint.getZ() - z);
				BlockPos branch1 = endpoint.offset(branchDir.getNormal()).offset(branchDir.getClockWise().getNormal());
				BlockPos branch2 = endpoint.offset(branchDir.getNormal()).offset(branchDir.getCounterClockWise().getNormal());
				if(level.getBlockState(branch1).isAir()) this.setBlock(level, branch1, log);
				if(level.getBlockState(branch2).isAir()) this.setBlock(level, branch2, log);
			}

			boolean[] generatedRoots = new boolean[5*5];

			int roots = 0;

			for(int i = 0; i < 50 && roots < 3; i++) {
				int rootX = rand.nextInt(3) - 1;
				int rootZ = rand.nextInt(3) - 1;

				if(rootX != 0 || rootZ != 0) {
					int rootIndex = rootX + 1 + 1 + (rootZ + 1 + 1) * 5;
					int rootIndexN = rootIndex - 5;
					int rootIndexS = rootIndex + 5;
					int rootIndexE = rootIndex + 1;
					int rootIndexW = rootIndex - 1;

					if(!generatedRoots[rootIndex] && !generatedRoots[rootIndexN] && !generatedRoots[rootIndexS] &&
						!generatedRoots[rootIndexE] && !generatedRoots[rootIndexW]) {
						int rootXO = 0;
						int rootZO = 0;

						for(int j = rootHeight; j >= -4; j--) {
							if(j < rootHeight && rand.nextInt(3) == 0) {
								Direction offset = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
								rootXO += rootX * Math.abs(offset.getStepX());
								rootZO += rootZ * Math.abs(offset.getStepZ());
							}
							BlockPos rootPos = pos.offset(rootX + rootXO, j, rootZ + rootZO);
							if(level.getBlockState(rootPos).getBlock().defaultBlockState().canBeReplaced()) {
								this.setBlock(level, rootPos, log);
							} else {
								break;
							}
						}

						generatedRoots[rootIndex] = true;
						roots++;
					}
				}
			}

			return true;
		}

		return false;
	}

	protected void generateCanopyLayer(LevelAccessor level, BlockPos center, final int size, BlockState leaves, final @Nullable Predicate<BlockPos> pred, @Nullable Consumer<BlockPos> postprocessor) {
		int x = center.getX();
		int y = center.getY();
		int z = center.getZ();

		switch(size) {
			default:
				int radius = size - 1;
				int span = Mth.floor(size / 2.0F);
				span += (span-1) % 2;

				boolean[] setStates = new boolean[(size*2)*(size*2)];

				Predicate<BlockPos> fullPred = pos -> {
					if(pred.test(pos)) {
						int xo = pos.getX() - x;
						int zo = pos.getZ() - z;
						int index = xo + radius + 1 + (zo + radius + 1) * (size*2);
						return !setStates[index];
					}
					return false;
				};

				Consumer<BlockPos> consumer = pos -> {
					int xo = pos.getX() - x;
					int zo = pos.getZ() - z;
					int index = xo + radius + 1 + (zo + radius + 1) * (size*2);
					if(!setStates[index]) {
						setStates[index] = true;
					}
					if(postprocessor != null) {
						postprocessor.accept(pos);
					}
				};

				for(int i = 0; i <= span; i++) {
					this.rotatedCubeVolume(level, fullPred, x, y, z, -(radius-i), 0, -(span+2*i-1)/2, leaves, (radius-i)*2+1, 1, span+2*i, Direction.from3DDataValue(0), consumer);
					this.rotatedCubeVolume(level, fullPred, x, y, z, -(span+2*i-1)/2, 0, -(radius-i), leaves, span+2*i, 1, (radius-i)*2+1, Direction.from3DDataValue(0), consumer);
				}
				break;
			case 4:
				this.rotatedCubeVolume(level, pred, x, y, z, -2, 0, -2, leaves, 5, 1, 5, Direction.from3DDataValue(0), postprocessor);
				this.rotatedCubeVolume(level, pred, x, y, z, -3, 0, 0, leaves, 1, 1, 1, Direction.from3DDataValue(0), postprocessor);
				this.rotatedCubeVolume(level, pred, x, y, z, 3, 0, 0, leaves, 1, 1, 1, Direction.from3DDataValue(0), postprocessor);
				this.rotatedCubeVolume(level, pred, x, y, z, 0, 0, -3, leaves, 1, 1, 1, Direction.from3DDataValue(0), postprocessor);
				this.rotatedCubeVolume(level, pred, x, y, z, 0, 0, 3, leaves, 1, 1, 1, Direction.from3DDataValue(0), postprocessor);
				break;
			case 3:
				this.rotatedCubeVolume(level, pred, x, y, z, -1, 0, -1, leaves, 3, 1, 3, Direction.from3DDataValue(0), postprocessor);
				this.rotatedCubeVolume(level, pred, x, y, z, -2, 0, 0, leaves, 1, 1, 1, Direction.from3DDataValue(0), postprocessor);
				this.rotatedCubeVolume(level, pred, x, y, z, 2, 0, 0, leaves, 1, 1, 1, Direction.from3DDataValue(0), postprocessor);
				this.rotatedCubeVolume(level, pred, x, y, z, 0, 0, -2, leaves, 1, 1, 1, Direction.from3DDataValue(0), postprocessor);
				this.rotatedCubeVolume(level, pred, x, y, z, 0, 0, 2, leaves, 1, 1, 1, Direction.from3DDataValue(0), postprocessor);
				break;
			case 2:
				this.rotatedCubeVolume(level, pred, x, y, z, 0, 0, 0, leaves, 2, 1, 1, Direction.from3DDataValue(0), postprocessor);
				this.rotatedCubeVolume(level, pred, x, y, z, -1, 0, 0, leaves, 1, 1, 1, Direction.from3DDataValue(0), postprocessor);
				this.rotatedCubeVolume(level, pred, x, y, z, 0, 0, 1, leaves, 1, 1, 1, Direction.from3DDataValue(0), postprocessor);
				this.rotatedCubeVolume(level, pred, x, y, z, 0, 0, -1, leaves, 1, 1, 1, Direction.from3DDataValue(0), postprocessor);
				break;
			case 1:
				this.rotatedCubeVolume(level, pred, x, y, z, 0, 0, 0, leaves, 1, 1, 1, Direction.from3DDataValue(0), postprocessor);
				break;
		}
	}
}
