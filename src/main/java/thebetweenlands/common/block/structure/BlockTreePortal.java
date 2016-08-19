package thebetweenlands.common.block.structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.teleporter.TeleporterHandler;

public class BlockTreePortal extends BasicBlock {
    public BlockTreePortal() {
        super(Material.PORTAL);
        setLightLevel(1.0F);
        setBlockUnbreakable();
        setSoundType2(SoundType.GLASS);
    }
/*
 * PORTAL_CORNER_TOP_LEFT,
        PORTAL_TOP,
        PORTAL_CORNER_TOP_RIGHT,
        PORTAL_SIDE_RIGHT,
        PORTAL_SIDE_LEFT,
        PORTAL_CORNER_BOTTOM_LEFT,
        PORTAL_BOTTOM,
        PORTAL_CORNER_BOTTOM_RIGHT;getStateFromMeta(8), 2);
 */
    public static boolean makePortalX(World world, BlockPos pos) {
        world.setBlockState(pos.add(0, 2, - 1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_TOP_RIGHT).withProperty(BlockPortalFrame.X_AXIS, true));
        world.setBlockState(pos.add(0, 2, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.TOP).withProperty(BlockPortalFrame.X_AXIS, true));
        world.setBlockState(pos.add(0, 2, 1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_TOP_LEFT).withProperty(BlockPortalFrame.X_AXIS, true));
        world.setBlockState(pos.add(0, 1, - 1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_RIGHT).withProperty(BlockPortalFrame.X_AXIS, true));
        world.setBlockState(pos.add(0, 1, 1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_LEFT).withProperty(BlockPortalFrame.X_AXIS, true));
        world.setBlockState(pos.add(0, 0, - 1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_RIGHT).withProperty(BlockPortalFrame.X_AXIS, true));
        world.setBlockState(pos.add(0, 0, 1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_LEFT).withProperty(BlockPortalFrame.X_AXIS, true));
        world.setBlockState(pos.add(0, - 1, - 1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_BOTTOM_RIGHT).withProperty(BlockPortalFrame.X_AXIS, true));
        world.setBlockState(pos.add(0, - 1, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.BOTTOM).withProperty(BlockPortalFrame.X_AXIS, true));
        world.setBlockState(pos.add(0, - 1, 1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_BOTTOM_LEFT).withProperty(BlockPortalFrame.X_AXIS, true));

        if (isPatternValidX(world, pos)) {
            world.setBlockState(pos, BlockRegistry.TREE_PORTAL.getStateFromMeta(0), 2);//TODO Add variants to THIS Block
            world.setBlockState(pos.up(), BlockRegistry.TREE_PORTAL.getStateFromMeta(0), 2);//TODO Add variants to THIS Block
            return true;
        }
        return false;
    }

    public static boolean makePortalZ(World world, BlockPos pos) {
        world.setBlockState(pos.add(- 1, 2, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_TOP_RIGHT).withProperty(BlockPortalFrame.X_AXIS, false));
        world.setBlockState(pos.add(0, 2, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.TOP).withProperty(BlockPortalFrame.X_AXIS, false));
        world.setBlockState(pos.add(1, 2, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_TOP_LEFT).withProperty(BlockPortalFrame.X_AXIS, false));
        world.setBlockState(pos.add(- 1, 1, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_RIGHT).withProperty(BlockPortalFrame.X_AXIS, false));
        world.setBlockState(pos.add(1, 1, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_LEFT).withProperty(BlockPortalFrame.X_AXIS, false));
        world.setBlockState(pos.add(- 1, 0, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_RIGHT).withProperty(BlockPortalFrame.X_AXIS, false));
        world.setBlockState(pos.add(1, 0, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_LEFT).withProperty(BlockPortalFrame.X_AXIS, false));
        world.setBlockState(pos.add(- 1, - 1, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_BOTTOM_RIGHT).withProperty(BlockPortalFrame.X_AXIS, false));
        world.setBlockState(pos.add(0, - 1, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.BOTTOM).withProperty(BlockPortalFrame.X_AXIS, false));
        world.setBlockState(pos.add(1, - 1, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_BOTTOM_LEFT).withProperty(BlockPortalFrame.X_AXIS, false));

        if (isPatternValidZ(world, pos)) {
        	world.setBlockState(pos, BlockRegistry.TREE_PORTAL.getStateFromMeta(1), 2);//TODO Add variants to THIS Block
            world.setBlockState(pos.up(), BlockRegistry.TREE_PORTAL.getStateFromMeta(1), 2);//TODO Add variants to THIS Block
            return true;
        }
        return false;
    }
    
    public static boolean isPatternValidX(World world, BlockPos pos) {
        // Layer 0
        if (!check(world, pos.down(), BlockRegistry.PORTAL_FRAME) && !checkPortal(world, pos.down(), BlockRegistry.TREE_PORTAL, 0))
            return false;

        // Layer 1
        if (!check(world, pos.north(), BlockRegistry.PORTAL_FRAME))
            return false;
        if (!check(world, pos.south(), BlockRegistry.PORTAL_FRAME))
            return false;

        // Layer 2
        if (!check(world, pos.up().north(), BlockRegistry.PORTAL_FRAME))
            return false;
        if (!check(world, pos.up().south(), BlockRegistry.PORTAL_FRAME))
            return false;

        // Layer 3
        if (!check(world, pos.up(2), BlockRegistry.PORTAL_FRAME))
            return false;

        return true;
    }

    public static boolean isPatternValidZ(World world, BlockPos pos) {
        // Layer 0
        if (!check(world, pos.down(), BlockRegistry.PORTAL_FRAME) && !checkPortal(world, pos.down(), BlockRegistry.TREE_PORTAL, 1))
            return false;

        // Layer 1
        if (!check(world, pos.down(), BlockRegistry.PORTAL_FRAME))
            return false;
        if (!check(world, pos.east(), BlockRegistry.PORTAL_FRAME))
            return false;

        // Layer 2
        if (!check(world, pos.west().up(), BlockRegistry.PORTAL_FRAME))
            return false;
        if (!check(world, pos.east().up(), BlockRegistry.PORTAL_FRAME))
            return false;

        // Layer 3
        if (!check(world, pos.up(2), BlockRegistry.PORTAL_FRAME))
            return false;

        return true;
    }

    private static boolean check(World world, BlockPos pos, Block target) {
        return world.getBlockState(pos).getBlock() == target;
    }

    private static boolean checkPortal(World world, BlockPos pos, Block target, int meta) {
    	IBlockState state = world.getBlockState(pos);
        return state.getBlock() == target && state.getBlock().getMetaFromState(state) == meta;
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        canBlockStay(world, pos);
    }

    public boolean canBlockStay(IBlockAccess world, BlockPos pos) {
        /*if(checkPortal(world, x, y + 1, z, BlockRegistry.treePortalBlock, 0) && isPatternValidX(world, x, y, z))
            return true;
        if(checkPortal(world, x, y - 1, z, BlockRegistry.treePortalBlock, 0) && isPatternValidX(world, x, y - 1, z))
            return true;
        if(checkPortal(world, x, y + 1, z, BlockRegistry.treePortalBlock, 1) && isPatternValidZ(world, x, y, z))
            return true;
        if(checkPortal(world, x, y - 1, z, BlockRegistry.treePortalBlock, 1) && isPatternValidZ(world, x, y - 1, z))
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

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return NULL_AABB;
	}

    
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (!entityIn.isRiding() && !entityIn.isBeingRidden() && entityIn.timeUntilPortal <= 0) {
			if (entityIn.dimension == 0)
				TeleporterHandler.transferToBL(entityIn);
			else
				TeleporterHandler.transferToOverworld(entityIn);
			if (entityIn != null)
				entityIn.timeUntilPortal = 10;
			return;
		}
	}

    @Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
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
