package thebetweenlands.common.block.container;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.tile.TileEntityAnimator;
import thebetweenlands.util.GLUProjection;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockAnimator extends BlockContainer {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    private final Random rand = new Random();

    public BlockAnimator() {
        super(Material.ROCK);
        setHardness(2.0F);
        setSoundType(SoundType.STONE);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityAnimator();
    }


    @Override
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        if (world.getTileEntity(pos) instanceof TileEntityAnimator) {
            TileEntityAnimator animator = (TileEntityAnimator) world.getTileEntity(pos);
            if (animator.fuelConsumed < animator.requiredFuelCount) {
                playerIn.openGui(TheBetweenlands.INSTANCE, CommonProxy.GUI_ANIMATOR, world, pos.getX(), pos.getY(), pos.getZ());
            } else {
                AnimatorRecipe recipe = AnimatorRecipe.getRecipe(animator.itemToAnimate);
                if (recipe == null || recipe.onRetrieved(animator, world, pos.getX(), pos.getY(), pos.getZ())) {
                    playerIn.openGui(TheBetweenlands.INSTANCE, CommonProxy.GUI_ANIMATOR, world, pos.getX(), pos.getY(), pos.getZ());
                }
                animator.fuelConsumed = 0;
            }
            animator.itemToAnimate = null;
            animator.itemAnimated = false;
        }

        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityAnimator animator = (TileEntityAnimator) world.getTileEntity(pos);
        if (animator != null) {
            for (int i1 = 0; i1 < animator.getSizeInventory(); ++i1) {
                ItemStack itemstack = animator.getStackInSlot(i1);

                if (itemstack != null) {
                    float f = this.rand.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.stackSize > 0) {
                        int j1 = this.rand.nextInt(21) + 10;

                        if (j1 > itemstack.stackSize) {
                            j1 = itemstack.stackSize;
                        }

                        itemstack.stackSize -= j1;
                        EntityItem entityitem = new EntityItem(world, (double) ((float) pos.getX() + f), (double) ((float) pos.getY() + f1), (double) ((float) pos.getZ() + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

                        if (itemstack.hasTagCompound()) {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                        }

                        float f3 = 0.05F;
                        entityitem.motionX = (double) ((float) this.rand.nextGaussian() * f3);
                        entityitem.motionY = (double) ((float) this.rand.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double) ((float) this.rand.nextGaussian() * f3);
                        world.spawnEntityInWorld(entityitem);
                    }
                }
            }

            //world.func_147453_f(pos, block);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntityAnimator te = (TileEntityAnimator) worldIn.getTileEntity(pos);
        if (te != null && te.isSlotInUse(0) && te.isCrystalInslot() && te.isSulfurInslot() && te.fuelConsumed < te.requiredFuelCount && te.isValidFocalItem()) {
            List<GLUProjection.Vector3D> points = new ArrayList<GLUProjection.Vector3D>();

            // Sulfur Particles
            if (te.getStackInSlot(2) != null) {
                rand = te.getWorld().rand;
                points.add(new GLUProjection.Vector3D(te.getPos().getX() + 0.5D + rand.nextFloat() * 0.6D - 0.3D, te.getPos().getY() + 0.1D, te.getPos().getZ() + 0.5D + rand.nextFloat() * 0.6D - 0.3D));
                points.add(new GLUProjection.Vector3D(te.getPos().getX() + 0.5D, te.getPos().getY() + 1, te.getPos().getZ() + 0.5D));
                points.add(new GLUProjection.Vector3D(te.getPos().getX() + 0.5D, te.getPos().getY() + 1, te.getPos().getZ() + 0.5D));
                points.add(new GLUProjection.Vector3D(te.getPos().getX() + 0.5D + rand.nextFloat() * 0.5D - 0.25D, te.getPos().getY() + 1.2, te.getPos().getZ() + 0.5D + rand.nextFloat() * 0.5D - 0.25D));
                points.add(new GLUProjection.Vector3D(te.getPos().getX() + 0.5D, te.getPos().getY() + 1.5D, te.getPos().getZ() + 0.5D));
                //TODO dodgy particle stuff
                //Minecraft.getMinecraft().effectRenderer.addEffect(new EntityAnimatorFX(te.getWorldObj(), te.getPos().getX() + 0.5D, te.getPos().getY(), te.getPos().getZ() + 0.5D, 0, 0, 0, points, ItemGeneric.createStack(EnumItemGeneric.SULFUR).getIconIndex(), 0.01F));
            }

            // Life Crystal Particles
            if (te.getStackInSlot(1) != null) {
                points = new ArrayList<GLUProjection.Vector3D>();
                points.add(new GLUProjection.Vector3D(te.getPos().getX() + 0.5D + rand.nextFloat() * 0.3D - 0.15D, te.getPos().getY() + 0.5D, te.getPos().getZ() + 0.5D + rand.nextFloat() * 0.3D - 0.15D));
                points.add(new GLUProjection.Vector3D(te.getPos().getX() + 0.5D, te.getPos().getY() + 1, te.getPos().getZ() + 0.5D));
                points.add(new GLUProjection.Vector3D(te.getPos().getX() + 0.5D, te.getPos().getY() + 1, te.getPos().getZ() + 0.5D));
                points.add(new GLUProjection.Vector3D(te.getPos().getX() + 0.5D + rand.nextFloat() * 0.5D - 0.25D, te.getPos().getY() + 1.2, te.getPos().getZ() + 0.5D + rand.nextFloat() * 0.5D - 0.25D));
                points.add(new GLUProjection.Vector3D(te.getPos().getX() + 0.5D, te.getPos().getY() + 1.5D, te.getPos().getZ() + 0.5D));
                //TODO dodgy particle stuff
                //Minecraft.getMinecraft().effectRenderer.addEffect(new EntityAnimatorFX(te.getWorldObj(), te.getPos().getX() + 0.5D, te.getPos().getY(), te.getPos().getZ() + 0.5D, 0, 0, 0, points, new ItemStack(BLItemRegistry.lifeCrystal).getIconIndex(), 0.0003F));
            }

            int meta = te.getBlockMetadata();

            double xOff = 0;
            double zOff = 0;

            switch (meta) {
                case 0:
                    xOff = -0.5F;
                    zOff = 0.14F;
                    break;
                case 1:
                    xOff = -0.14F;
                    zOff = -0.5F;
                    break;
                case 2:
                    xOff = 0.5F;
                    zOff = -0.14F;
                    break;
                case 3:
                    xOff = 0.14F;
                    zOff = 0.5F;
                    break;
            }

            // Runes
            points = new ArrayList<GLUProjection.Vector3D>();
            points.add(new GLUProjection.Vector3D(te.getPos().getX() + 0.5D + (rand.nextFloat() - 0.5F) * 0.3D + xOff, te.getPos().getY() + 0.9, te.getPos().getZ() + 0.5 + (rand.nextFloat() - 0.5F) * 0.3D + zOff));
            points.add(new GLUProjection.Vector3D(te.getPos().getX() + 0.5D + (rand.nextFloat() - 0.5F) * 0.3D + xOff, te.getPos().getY() + 1.36, te.getPos().getZ() + 0.5 + (rand.nextFloat() - 0.5F) * 0.3D + zOff));
            points.add(new GLUProjection.Vector3D(te.getPos().getX() + 0.5D, te.getPos().getY() + 1.5D, te.getPos().getZ() + 0.5D));

            //TODO dodgy particle stuff
            //Minecraft.getMinecraft().effectRenderer.addEffect(new EntityAnimatorFX2(te.getWorldObj(), te.getPos().getX(), te.getPos().getY() + 0.9, te.getPos().getZ() + 0.65, 0, 0, 0, points));
            // Smoke
            //Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySmokFX(te.getWorldObj(), te.getPos().getX() + 0.5 + rand.nextFloat() * 0.3D - 0.15D, te.getPos().getY() + 0.3, te.getPos().getZ() + 0.5 + rand.nextFloat() * 0.3D - 0.15D, 0, 0, 0, (rand.nextFloat() / 2.0F) + 1F));
            BLParticles.SMOKE.spawn(worldIn, te.getPos().getX() + 0.5 + rand.nextFloat() * 0.3D - 0.15D, te.getPos().getY() + 0.3, te.getPos().getZ() + 0.5 + rand.nextFloat() * 0.3D - 0.15D);
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }
}