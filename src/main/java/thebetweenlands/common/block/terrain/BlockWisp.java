package thebetweenlands.common.block.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.tile.TileEntityWisp;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;

import java.util.Random;

public class BlockWisp extends Block implements ITileEntityProvider {

    public BlockWisp() {
        super(BLMaterialRegistry.WISP);
        setSoundType(SoundType.STONE);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        setHardness(0);
        //setBlockTextureName("thebetweenlands:wisp");
    }

    public static boolean canSee(World world) {
        if(world.provider instanceof WorldProviderBetweenlands) {
            WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
            EnvironmentEventRegistry eeRegistry = provider.getWorldData().getEnvironmentEventRegistry();
            if(eeRegistry.AURORAS.isActive()) {
                return true;
            }
        }
        return false;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return new AxisAlignedBB(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end){
        if(canSee(world))
            return super.collisionRayTrace(blockState, world, pos, start, end);
        return null;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
        if(!world.isRemote && canSee(world)) {
            EntityItem wispItem = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, new ItemStack(Item.getItemFromBlock(this), 1));
            world.spawnEntityInWorld(wispItem);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityWisp();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState s) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, this.getStateFromMeta(world.rand.nextInt(this.colors.length / 2) * 2), 2);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    // Colors can be added here, always add a pair of colors for outer color and inner color
    public int[] colors = new int[] {
            0xFF7F1659, 0xFFFFFFFF, // Pink/White
            0xFF0707C8, 0xFFC8077B, // Blue/Pink
            0xFF0E2E0B, 0xFFC8077B, // Green/Yellow/White
            0xFF9A6908, 0xFF4F0303 // Red/Yellow/White
    };

    /**
     * Sets the block at the giving position to a wisp block with a random color
     *
     * @param world
     * @param pos
     */
    public void generateBlock(World world, BlockPos pos) {
        world.setBlockState(pos, this.getStateFromMeta(world.rand.nextInt(this.colors.length / 2) * 2), 2);
    }
}
