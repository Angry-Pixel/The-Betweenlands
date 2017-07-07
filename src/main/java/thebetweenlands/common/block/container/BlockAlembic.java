package thebetweenlands.common.block.container;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityAlembic;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockAlembic extends BlockContainer {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockAlembic() {
        super(Material.ROCK);
        setHardness(2.0F);
        setResistance(5.0F);
        setCreativeTab(BLCreativeTabs.HERBLORE);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }


    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (world.getTileEntity(pos) instanceof TileEntityAlembic) {
                TileEntityAlembic tile = (TileEntityAlembic) world.getTileEntity(pos);

                if (player.isSneaking())
                    return false;

                if (player.getHeldItem(hand) != null) {
                    ItemStack heldStack = player.getHeldItem(hand);
                    if (heldStack.getItem() == ItemRegistry.WEEDWOOD_BUCKET_INFUSION) {
                        if (!tile.isFull()) {
                            tile.addInfusion(heldStack);
                            if (!player.capabilities.isCreativeMode)
                                player.setHeldItem(hand, new ItemStack(ItemRegistry.WEEDWOOD_BUCKET, 1));
                        }
                    } else if (heldStack.getItem() == ItemRegistry.DENTROTHYST_VIAL && (heldStack.getItemDamage() == 0 || heldStack.getItemDamage() == 2)) {
                        if (tile.hasFinished()) {
                            ItemStack result = tile.getElixir(heldStack.getItemDamage() == 0 ? 0 : 1);
                            EntityItem itemEntity = player.dropItem(result, false);
                            if (itemEntity != null) itemEntity.setPickupDelay(0);
                            if (!player.capabilities.isCreativeMode) heldStack.shrink(1);
                        }
                    }
                }
            }
        }
        return true;
    }


    @Override
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand) {
        if (world.getTileEntity(pos) instanceof TileEntityAlembic) {
            TileEntityAlembic alembic = (TileEntityAlembic) world.getTileEntity(pos);
            if (alembic.isRunning()) {
                float xx = (float) pos.getX() + 0.5F;
                float yy = (float) (pos.getY() + 0.35F + rand.nextFloat() * 0.5F);
                float zz = (float) pos.getZ() + 0.5F;
                float fixedOffset = 0.25F;
                float randomOffset = rand.nextFloat() * 0.6F - 0.3F;
                BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx - fixedOffset), (double) yy + 0.75D, (double) (zz + randomOffset));
                BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + fixedOffset), (double) yy + 0.75D, (double) (zz + randomOffset));
                BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + randomOffset), (double) yy + 0.75D, (double) (zz - fixedOffset));
                BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + randomOffset), (double) yy + 0.75D, (double) (zz + fixedOffset));
                int meta = stateIn.getBlock().getMetaFromState(stateIn);
                switch (meta) {
                    case 2:
                        BLParticles.FLAME.spawn(world, pos.getX() + 0.65F + (rand.nextFloat() - 0.5F) * 0.1F, pos.getY(), pos.getZ() + 0.6F + (rand.nextFloat() - 0.5F) * 0.1F, ParticleFactory.ParticleArgs.get().withMotion((rand.nextFloat() - 0.5F) * 0.01F, 0.01F, 0F));
                        break;
                    case 3:
                        BLParticles.FLAME.spawn(world, pos.getX() + 0.375F + (rand.nextFloat() - 0.5F) * 0.1F, pos.getY(), pos.getZ() + 0.375F + (rand.nextFloat() - 0.5F) * 0.1F, ParticleFactory.ParticleArgs.get().withMotion((rand.nextFloat() - 0.5F) * 0.01F, 0.01F, 0F));
                        break;
                    case 4:
                        BLParticles.FLAME.spawn(world, pos.getX() + 0.6F + (rand.nextFloat() - 0.5F) * 0.1F, pos.getY(), pos.getZ() + 0.375F + (rand.nextFloat() - 0.5F) * 0.1F, ParticleFactory.ParticleArgs.get().withMotion((rand.nextFloat() - 0.5F) * 0.01F, 0.01F, 0F));
                        break;
                    case 5:
                        BLParticles.FLAME.spawn(world, pos.getX() + 0.375F + (rand.nextFloat() - 0.5F) * 0.1F, pos.getY(), pos.getZ() + 0.6F + (rand.nextFloat() - 0.5F) * 0.1F, ParticleFactory.ParticleArgs.get().withMotion((rand.nextFloat() - 0.5F) * 0.01F, 0.01F, 0F));
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
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }
}
