package thebetweenlands.common.block.structure;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockTreePortal extends BasicBlock {
    public BlockTreePortal() {
        super(Material.PORTAL);
        setLightLevel(1.0F);
        setBlockUnbreakable();
        setStepSound2(SoundType.GLASS);
    }

    /*public static boolean makePortalX(World world, int x, int y, int z) {
        world.setBlock(x, y + 2, z - 1, BLBlockRegistry.portalBarkFrame, 8, 2);
        world.setBlock(x, y + 2, z, BLBlockRegistry.portalBarkFrame, 9, 2);
        world.setBlock(x, y + 2, z + 1, BLBlockRegistry.portalBarkFrame, 10, 2);
        world.setBlock(x, y + 1, z - 1, BLBlockRegistry.portalBarkFrame, 11, 2);
        world.setBlock(x, y + 1, z + 1, BLBlockRegistry.portalBarkFrame, 12, 2);
        world.setBlock(x, y, z - 1, BLBlockRegistry.portalBarkFrame, 11, 2);
        world.setBlock(x, y, z + 1, BLBlockRegistry.portalBarkFrame, 12, 2);
        world.setBlock(x, y - 1, z - 1, BLBlockRegistry.portalBarkFrame, 13, 2);
        world.setBlock(x, y - 1, z, BLBlockRegistry.portalBarkFrame, 14, 2);
        world.setBlock(x, y - 1, z + 1, BLBlockRegistry.portalBarkFrame, 15, 2);

        if (isPatternValidX(world, x, y, z)) {
            world.setBlock(x, y, z, BLBlockRegistry.treePortalBlock, 0, 2);
            world.setBlock(x, y + 1, z, BLBlockRegistry.treePortalBlock, 0, 2);
            return true;
        }
        return false;
    }

    public static boolean makePortalZ(World world, int x, int y, int z) {
        world.setBlock(x - 1, y + 2, z, BLBlockRegistry.portalBarkFrame, 0, 2);
        world.setBlock(x, y + 2, z, BLBlockRegistry.portalBarkFrame, 1, 2);
        world.setBlock(x + 1, y + 2, z, BLBlockRegistry.portalBarkFrame, 2, 2);
        world.setBlock(x - 1, y + 1, z, BLBlockRegistry.portalBarkFrame, 3, 2);
        world.setBlock(x + 1, y + 1, z, BLBlockRegistry.portalBarkFrame, 4, 2);
        world.setBlock(x - 1, y, z, BLBlockRegistry.portalBarkFrame, 3, 2);
        world.setBlock(x + 1, y, z, BLBlockRegistry.portalBarkFrame, 4, 2);
        world.setBlock(x - 1, y - 1, z, BLBlockRegistry.portalBarkFrame, 5, 2);
        world.setBlock(x, y - 1, z, BLBlockRegistry.portalBarkFrame, 6, 2);
        world.setBlock(x + 1, y - 1, z, BLBlockRegistry.portalBarkFrame, 7, 2);

        if (isPatternValidZ(world, x, y, z)) {
            world.setBlock(x, y, z, BLBlockRegistry.treePortalBlock, 1, 2);
            world.setBlock(x, y + 1, z, BLBlockRegistry.treePortalBlock, 1, 2);
            return true;
        }
        return false;
    }*/
    /*
    public static boolean isPatternValidX(World world, int x, int y, int z) {
        // Layer 0
        if (!check(world, x, y - 1, z, BLBlockRegistry.portalBarkFrame) && !checkPortal(world, x, y - 1, z, BLBlockRegistry.treePortalBlock, 0))
            return false;

        // Layer 1
        if (!check(world, x, y, z - 1, BLBlockRegistry.portalBarkFrame))
            return false;
        if (!check(world, x, y, z + 1, BLBlockRegistry.portalBarkFrame))
            return false;

        // Layer 2
        if (!check(world, x, y + 1, z - 1, BLBlockRegistry.portalBarkFrame))
            return false;
        if (!check(world, x, y + 1, z + 1, BLBlockRegistry.portalBarkFrame))
            return false;

        // Layer 3
        if (!check(world, x, y + 2, z, BLBlockRegistry.portalBarkFrame))
            return false;

        return true;
    }

    public static boolean isPatternValidZ(World world, int x, int y, int z) {
        // Layer 0
        if (!check(world, x, y - 1, z, BLBlockRegistry.portalBarkFrame) && !checkPortal(world, x, y - 1, z, BLBlockRegistry.treePortalBlock, 1))
            return false;

        // Layer 1
        if (!check(world, x - 1, y, z, BLBlockRegistry.portalBarkFrame))
            return false;
        if (!check(world, x + 1, y, z, BLBlockRegistry.portalBarkFrame))
            return false;

        // Layer 2
        if (!check(world, x - 1, y + 1, z, BLBlockRegistry.portalBarkFrame))
            return false;
        if (!check(world, x + 1, y + 1, z, BLBlockRegistry.portalBarkFrame))
            return false;

        // Layer 3
        if (!check(world, x, y + 2, z, BLBlockRegistry.portalBarkFrame))
            return false;

        return true;
    }

    private static boolean check(World world, int x, int y, int z, Block target) {
        return world.getBlock(x, y, z) == target;
    }

    private static boolean checkPortal(World world, int x, int y, int z, Block target, int meta) {
        return world.getBlock(x, y, z) == target && world.getBlockMetadata(x, y, z) == meta;
    }*/

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        canBlockStay(world, pos);
    }

    public boolean canBlockStay(IBlockAccess world, BlockPos pos) {
        /*if(checkPortal(world, x, y + 1, z, BLBlockRegistry.treePortalBlock, 0) && isPatternValidX(world, x, y, z))
            return true;
        if(checkPortal(world, x, y - 1, z, BLBlockRegistry.treePortalBlock, 0) && isPatternValidX(world, x, y - 1, z))
            return true;
        if(checkPortal(world, x, y + 1, z, BLBlockRegistry.treePortalBlock, 1) && isPatternValidZ(world, x, y, z))
            return true;
        if(checkPortal(world, x, y - 1, z, BLBlockRegistry.treePortalBlock, 1) && isPatternValidZ(world, x, y - 1, z))
            return true;
        else {
            world.playAuxSFXAtEntity(null, 2001, x, y, z, Block.getIdFromBlock(world.getBlock(x, y, z)));
            world.setBlockToAir(x, y, z);
        }*/
        return true;
    }

    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entity) {
        /*if (entity.ridingEntity == null && entity.riddenByEntity == null && entity.timeUntilPortal <= 0) {
            if(entity instanceof EntityPlayer){
                EntityPropertiesPortal props = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesPortal.class);
                props.inPortal = true;
            } else if(!world.isRemote) {
                if (entity.dimension == 0)
                    TeleporterHandler.transferToBL(entity);
                else
                    TeleporterHandler.transferToOverworld(entity);
                entity.timeUntilPortal = 10;
            }
        }*/
    }


    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return side != EnumFacing.DOWN || side != EnumFacing.UP;
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        for (int i = 0; i < 4; i++) {
            double particleX = pos.getX() + rand.nextFloat();
            double particleY = pos.getY() + rand.nextFloat();
            double particleZ = pos.getZ() + rand.nextFloat();
            double motionX;
            double motionY;
            double motionZ;
            int multi = rand.nextInt(2) * 2 - 1;

            motionX = (rand.nextFloat() - 0.5D) * 0.5D;
            motionY = (rand.nextFloat() - 0.5D) * 0.5D;
            motionZ = (rand.nextFloat() - 0.5D) * 0.5D;

            if (worldIn.getBlockState(pos.add(-1, 0, 0)).getBlock() != this && worldIn.getBlockState(pos.add(1, 0, 0)).getBlock() != this) {
                particleX = pos.getX() + 0.5D + 0.25D * multi;
                motionX = rand.nextFloat() * 2.0F * multi;
            } else {
                particleZ = pos.getZ() + 0.5D + 0.25D * multi;
                motionZ = rand.nextFloat() * 2.0F * multi;
            }

            //BLParticle.PORTAL.spawn(worldIn, particleX, particleY, particleZ, motionX, motionY, motionZ, 0);
        }
        if (rand.nextInt(100) == 0) {
            //world.playSound((double)x + .5, (double)y + .5, (double)z + .5, "thebetweenlands:portal", 0.2F, rand.nextFloat() * 0.4F + 0.8F, false);
        }
    }
}
