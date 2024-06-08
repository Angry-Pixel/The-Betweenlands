package thebetweenlands.common.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.FeatureHelper;
import thebetweenlands.common.world.gen.feature.FeatureHelperConfiguration;

import java.util.Random;

public class IdolHead extends FeatureHelper<FeatureHelperConfiguration> {
    // Set in configuration
    private BlockState solid;// = BlockRegistry.CRAGROCK.get().defaultBlockState();//SMOOTH_CRAGROCK.getDefaultState();
    private BlockState slab;// = BlockRegistry.CRAGROCK.get().defaultBlockState();//SMOOTH_CRAGROCK_SLAB.getDefaultState();
    private BlockState stairs;// = BlockRegistry.CRAGROCK.get().defaultBlockState();//SMOOTH_CRAGROCK_STAIRS.getDefaultState();
    private BlockState eyes;// = BlockRegistry.OCTINE_ORE.get().defaultBlockState();//OCTINE_BLOCK.getDefaultState();

    public IdolHead(Codec<FeatureHelperConfiguration> codec) {
        super(codec);
        depth = 8;
        width = 8;
        height = 8;
    }

    @Override
    public boolean place(FeaturePlaceContext<FeatureHelperConfiguration> context) {

        solid = BlockRegistry.CRAGROCK.get().defaultBlockState();//SMOOTH_CRAGROCK.getDefaultState();
        slab = Blocks.STONE_SLAB.defaultBlockState();//SMOOTH_CRAGROCK_SLAB.getDefaultState();
        stairs = Blocks.STONE_STAIRS.defaultBlockState();//SMOOTH_CRAGROCK_STAIRS.getDefaultState();
        eyes = BlockRegistry.OCTINE_ORE.get().defaultBlockState();//OCTINE_BLOCK.getDefaultState();

        WorldGenLevel Level = context.level();
        BlockPos pos = context.origin();
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        Random rand = context.random();

        // air check
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        for (int xx = x - depth / 2; xx <= x + depth / 2; ++xx) {
            for(int zz = z - width / 2; zz <= z + width / 2; ++zz) {
                for(int yy = y + 1; yy < y + height; ++yy ) {
                    BlockState state = Level.getBlockState(checkPos.set(xx, yy, zz));
                    if(!state.isAir() && !isReplaceable(context,state))
                        return false;
                }
                for (int yy = y; yy >= y - 5; yy--) {
                    BlockState state = Level.getBlockState(checkPos.set(xx, yy, zz));
                    if(!state.isAir() && !isReplaceable(context,state))
                        break;
                    if(yy <= y - 5)
                        return false;
                }
            }
        }

        int direction = rand.nextInt(4);
        int headType = rand.nextInt(3);
        int xx = x;//- depth / 2;
        int zz = z;//- width / 2;

        switch (headType) {
            case 0:// Gold Head
                rotatedCubeVolume(Level, xx, y, zz, 1, 0, 2, solid, 6, 4, 5, direction);
                rotatedCubeVolume(Level, xx, y, zz, 0, 3, 4, solid, 1, 2, 2, direction);
                rotatedCubeVolume(Level, xx, y, zz, 7, 3, 4, solid, 1, 2, 2, direction);
                rotatedCubeVolume(Level, xx, y, zz, 1, 5, 1, solid, 6, 2, 6, direction);
                rotatedCubeVolume(Level, xx, y, zz, 1, 4, 3, solid, 6, 1, 4, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 3, 0, solid, 2, 4, 3, direction);
                rotatedCubeVolume(Level, xx, y, zz, 2, 3, 1, solid, 1, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 5, 3, 1, solid, 1, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 7, 1, solid, 2, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 7, 3, solid, 2, 1, 2, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 7, 6, solid, 2, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 6, 7, solid, 2, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 4, 7, solid, 2, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 2, 4, 3, eyes, 1, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 5, 4, 3, eyes, 1, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 2, 1, 1, slab, 4, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 2, 2, 1, slab.setValue(SlabBlock.TYPE, SlabType.TOP), 4, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 1, 1, 1,
                        direction == 0 ? stairs.setValue(StairBlock.FACING, Direction.WEST) :
                                direction == 2 ? stairs.setValue(StairBlock.FACING, Direction.EAST) :
                                        direction == 1 ? stairs.setValue(StairBlock.FACING, Direction.NORTH) :
                                                stairs.setValue(StairBlock.FACING, Direction.SOUTH), 1, 1, 1, direction); //bottom right
                rotatedCubeVolume(Level, xx, y, zz, 6, 1, 1,
                        direction == 0 ? stairs.setValue(StairBlock.FACING, Direction.EAST) :
                                direction == 2 ? stairs.setValue(StairBlock.FACING, Direction.WEST) :
                                        direction == 1 ? stairs.setValue(StairBlock.FACING, Direction.SOUTH) :
                                                stairs.setValue(StairBlock.FACING, Direction.NORTH), 1, 1, 1, direction); //bottom left
                rotatedCubeVolume(Level, xx, y, zz, 1, 2, 1,
                        direction == 0 ? stairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP) :
                                direction == 2 ? stairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP) :
                                        direction == 1 ? stairs.setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.TOP) :
                                                stairs.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP), 1, 1, 1, direction); //top right
                rotatedCubeVolume(Level, xx, y, zz, 6, 2, 1,
                        direction == 0 ? stairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP) :
                                direction == 2 ? stairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP) :
                                        direction == 1 ? stairs.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP) :
                                                stairs.setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.TOP), 1, 1, 1, direction); //top left
                break;

            case 1:// Silver Head
                rotatedCubeVolume(Level, xx, y, zz, 1, 0, 2, solid, 6, 4, 5, direction);
                rotatedCubeVolume(Level, xx, y, zz, 0, 1, 4, solid, 1, 5, 2, direction);
                rotatedCubeVolume(Level, xx, y, zz, 7, 1, 4, solid, 1, 5, 2, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 1, 7, solid, 2, 5, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 1, 4, 3, solid, 6, 3, 4, direction);
                rotatedCubeVolume(Level, xx, y, zz, 1, 7, 1, solid, 2, 1, 6, direction);
                rotatedCubeVolume(Level, xx, y, zz, 1, 6, 1, slab.setValue(SlabBlock.TYPE, SlabType.TOP), 2, 1, 2, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 7, 1, slab, 2, 1, 6, direction);
                rotatedCubeVolume(Level, xx, y, zz, 5, 7, 1, solid, 2, 1, 6, direction);
                rotatedCubeVolume(Level, xx, y, zz, 5, 6, 1, slab.setValue(SlabBlock.TYPE, SlabType.TOP), 2, 1, 2, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 3, 0, solid, 2, 3, 3, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 6, 1, solid, 2, 1, 2, direction);
                rotatedCubeVolume(Level, xx, y, zz, 1, 4, 2, slab, 2, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 5, 4, 2, slab, 2, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 2, 3, 1, solid, 4, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 2, 5, 3, eyes, 1, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 5, 5, 3, eyes, 1, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 1, 1,
                        direction == 0 ? stairs.setValue(StairBlock.FACING, Direction.WEST) :
                                direction == 2 ? stairs.setValue(StairBlock.FACING, Direction.EAST) :
                                        direction == 1 ? stairs.setValue(StairBlock.FACING, Direction.NORTH) :
                                                stairs.setValue(StairBlock.FACING, Direction.SOUTH), 1, 1, 1, direction); //bottom right
                rotatedCubeVolume(Level, xx, y, zz, 4, 1, 1,
                        direction == 0 ? stairs.setValue(StairBlock.FACING, Direction.EAST) :
                                direction == 2 ? stairs.setValue(StairBlock.FACING, Direction.WEST) :
                                        direction == 1 ? stairs.setValue(StairBlock.FACING, Direction.SOUTH) :
                                                stairs.setValue(StairBlock.FACING, Direction.NORTH), 1, 1, 1, direction); //bottom left
                rotatedCubeVolume(Level, xx, y, zz, 3, 2, 1,
                        direction == 0 ? stairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP) :
                                direction == 2 ? stairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP) :
                                        direction == 1 ? stairs.setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.TOP) :
                                                stairs.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP), 1, 1, 1, direction); //top right
                rotatedCubeVolume(Level, xx, y, zz, 4, 2, 1,
                        direction == 0 ? stairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP) :
                                direction == 2 ? stairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP) :
                                        direction == 1 ? stairs.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP) :
                                                stairs.setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.TOP), 1, 1, 1, direction); //top left
                break;

            case 2://Bronze Head
                rotatedCubeVolume(Level, xx, y, zz, 1, 0, 2, solid, 6, 4, 5, direction);
                rotatedCubeVolume(Level, xx, y, zz, 0, 1, 4, solid, 1, 5, 2, direction);
                rotatedCubeVolume(Level, xx, y, zz, 7, 1, 4, solid, 1, 5, 2, direction);
                rotatedCubeVolume(Level, xx, y, zz, 2, 2, 7, solid, 4, 4, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 1, 6, 1, solid, 6, 1, 6, direction);
                rotatedCubeVolume(Level, xx, y, zz, 2, 7, 1, solid, 4, 1, 2, direction);
                rotatedCubeVolume(Level, xx, y, zz, 2, 7, 4, solid, 4, 1, 2, direction);
                rotatedCubeVolume(Level, xx, y, zz, 1, 4, 3, solid, 6, 2, 4, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 3, 0, solid, 2, 4, 3, direction);
                rotatedCubeVolume(Level, xx, y, zz, 1, 4, 2, slab, 2, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 5, 4, 2, slab, 2, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 2, 3, 1, solid, 4, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 2, 5, 3, eyes, 1, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 5, 5, 3, eyes, 1, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 1, 1, slab, 2, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 3, 2, 1, slab.setValue(SlabBlock.TYPE, SlabType.TOP), 2, 1, 1, direction);
                rotatedCubeVolume(Level, xx, y, zz, 2, 1, 1,
                        direction == 0 ? stairs.setValue(StairBlock.FACING, Direction.WEST) :
                                direction == 2 ? stairs.setValue(StairBlock.FACING, Direction.EAST) :
                                        direction == 1 ? stairs.setValue(StairBlock.FACING, Direction.NORTH) :
                                                stairs.setValue(StairBlock.FACING, Direction.SOUTH), 1, 1, 1, direction); //bottom right
                rotatedCubeVolume(Level, xx, y, zz, 5, 1, 1,
                        direction == 0 ? stairs.setValue(StairBlock.FACING, Direction.EAST) :
                                direction == 2 ? stairs.setValue(StairBlock.FACING, Direction.WEST) :
                                        direction == 1 ? stairs.setValue(StairBlock.FACING, Direction.SOUTH) :
                                                stairs.setValue(StairBlock.FACING, Direction.NORTH), 1, 1, 1, direction); //bottom left
                rotatedCubeVolume(Level, xx, y, zz, 2, 2, 1,
                        direction == 0 ? stairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP) :
                                direction == 2 ? stairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP) :
                                        direction == 1 ? stairs.setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.TOP) :
                                                stairs.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP), 1, 1, 1, direction); //top right
                rotatedCubeVolume(Level, xx, y, zz, 5, 2, 1,
                        direction == 0 ? stairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP) :
                                direction == 2 ? stairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP) :
                                        direction == 1 ? stairs.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP) :
                                                stairs.setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.TOP), 1, 1, 1, direction); //top left
                break;
        }

        for(int i = 0; i < 40 + rand.nextInt(160); i++) {
            int type = rand.nextInt(4);
            switch(type) {
                default:
                case 0:
                    //this.crackGen.generate(Level, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
                    break;
                case 1:
                    //this.crackGenSlabs.generate(Level, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
                    break;
                case 2:
                    //this.crackGenStairs.generate(Level, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
                    break;
                case 3:
                    //this.mossGen.generate(Level, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
                    break;
            }

            if(rand.nextInt(4) == 0) {
                if(rand.nextInt(8) == 0) {
                    //this.lichenClusterGen.generate(Level, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
                } else {
                    //this.mossClusterGen.generate(Level, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
                }
            }
        }

        /* Temp disabled
        Level.setBlockState(new BlockPos(x, y - 1, z), BlockRegistry.WEEDWOOD_CHEST.getDefaultState());
        TileEntity tile = Level.getTileEntity(new BlockPos(x, y - 1, z));
        if (tile instanceof TileEntityChest) {
            ((TileEntityChest) tile).setLootTable(LootTableRegistry.IDOL_HEADS_CHEST, rand.nextLong());
        }
        */

        // TODO: store generation location in storage class
        // Temp disabled
        //BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(Level);
        AABB locationAABB = rotatedAABB(Level, xx, y, zz, 0, 0, 0, 8, 8, 8, direction).inflate(3, 3, 3);
        /* Temp disabled
        LocationStorage locationStorage = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(x, z), "idol_head", EnumLocationType.IDOL_HEAD);
        locationStorage.setSeed(rand.nextLong());
        locationStorage.addBounds(locationAABB);
        locationStorage.setVisible(false);
        locationStorage.setDirty(true);
        worldStorage.getLocalStorageHandler().addLocalStorage(locationStorage);
        */

        return true;
    }
}