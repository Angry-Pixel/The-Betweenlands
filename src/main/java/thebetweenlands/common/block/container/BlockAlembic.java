package thebetweenlands.common.block.container;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.tools.ItemBucketInfusion;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityAlembic;

public class BlockAlembic extends BlockContainer {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockAlembic() {
        super(Material.ROCK);
        setHardness(2.0F);
        setResistance(5.0F);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        setDefaultState(blockState.getBaseState().with(FACING, EnumFacing.NORTH));
    }


    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
        return this.getDefaultState().with(FACING, placer.getHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing()), 2);
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if (!world.isRemote) {
            if (world.getTileEntity(pos) instanceof TileEntityAlembic) {
                TileEntityAlembic tile = (TileEntityAlembic) world.getTileEntity(pos);

                if (player.isSneaking())
                    return false;

                if (!player.getHeldItem(hand).isEmpty()) {
                    ItemStack heldStack = player.getHeldItem(hand);
                    if (heldStack.getItem() == ItemRegistry.BL_BUCKET_INFUSION) {
                        if (!tile.isFull()) {
                            tile.addInfusion(heldStack);
                            if (!player.abilities.isCreativeMode)
                                player.setHeldItem(hand, ItemBucketInfusion.getEmptyBucket(heldStack));
                        }
                    } else if (heldStack.getItem() == ItemRegistry.DENTROTHYST_VIAL && (heldStack.getItemDamage() == 0 || heldStack.getItemDamage() == 2)) {
                        if (tile.hasFinished()) {
                            ItemStack result = tile.getElixir(heldStack.getItemDamage() == 0 ? 0 : 1);
                            EntityItem itemEntity = player.dropItem(result, false);
                            if (itemEntity != null) itemEntity.setPickupDelay(0);
                            if (!player.abilities.isCreativeMode) heldStack.shrink(1);
                        }
                    }
                }
            }
        }
        return true;
    }


    @Override
    public void animateTick(IBlockState stateIn, World world, BlockPos pos, Random rand) {
        if (world.getTileEntity(pos) instanceof TileEntityAlembic) {
            TileEntityAlembic alembic = (TileEntityAlembic) world.getTileEntity(pos);
            if (alembic.isRunning()) {
                float xx = (float) pos.getX() + 0.5F;
                float yy = (float) (pos.getY() + 0.25F + rand.nextFloat() * 0.5F);
                float zz = (float) pos.getZ() + 0.5F;
                float fixedOffset = 0.25F;
                float randomOffset = rand.nextFloat() * 0.6F - 0.3F;
                BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx - fixedOffset), (double) yy + 0.250D, (double) (zz + randomOffset));
                BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + fixedOffset), (double) yy + 0.250D, (double) (zz + randomOffset));
                BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + randomOffset), (double) yy + 0.250D, (double) (zz - fixedOffset));
                BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + randomOffset), (double) yy + 0.250D, (double) (zz + fixedOffset));
                EnumFacing facing = (EnumFacing) stateIn.getProperties().get(FACING);
                switch (facing) {
                    case NORTH:
                        BLParticles.FLAME.spawn(world, pos.getX() + 0.65F + (rand.nextFloat() - 0.5F) * 0.1F, pos.getY(), pos.getZ() + 0.6F + (rand.nextFloat() - 0.5F) * 0.1F, ParticleFactory.ParticleArgs.get().withMotion((rand.nextFloat() - 0.5F) * 0.01F, 0.01F, 0F));
                        break;
                    case SOUTH:
                        BLParticles.FLAME.spawn(world, pos.getX() + 0.375F + (rand.nextFloat() - 0.5F) * 0.1F, pos.getY(), pos.getZ() + 0.375F + (rand.nextFloat() - 0.5F) * 0.1F, ParticleFactory.ParticleArgs.get().withMotion((rand.nextFloat() - 0.5F) * 0.01F, 0.01F, 0F));
                        break;
                    case EAST:
                        BLParticles.FLAME.spawn(world, pos.getX() + 0.375F + (rand.nextFloat() - 0.5F) * 0.1F, pos.getY(), pos.getZ() + 0.6F + (rand.nextFloat() - 0.5F) * 0.1F, ParticleFactory.ParticleArgs.get().withMotion((rand.nextFloat() - 0.5F) * 0.01F, 0.01F, 0F));
                        break;
                    case WEST:
                        BLParticles.FLAME.spawn(world, pos.getX() + 0.6F + (rand.nextFloat() - 0.5F) * 0.1F, pos.getY(), pos.getZ() + 0.375F + (rand.nextFloat() - 0.5F) * 0.1F, ParticleFactory.ParticleArgs.get().withMotion((rand.nextFloat() - 0.5F) * 0.01F, 0.01F, 0F));
                        break;
                }
            }
        }
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityAlembic();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().with(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.get(FACING).getIndex();
    }
    
    @Override
    public BlockFaceShape getBlockFaceShape(IWorldReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
}
