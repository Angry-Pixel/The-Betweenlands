package thebetweenlands.common.block.container;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityLootPot;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockLootPot extends BasicBlock implements ITileEntityProvider, BlockRegistry.ISubtypeBlock {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<EnumLootPot> VARIANT = PropertyEnum.create("type", EnumLootPot.class);

    public BlockLootPot() {
        super(Material.GLASS);
        setHardness(1.0f);
        setSoundType(SoundType.GLASS);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(VARIANT, EnumLootPot.POT_1));
    }


    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityLootPot();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, EnumLootPot.POT_1.getMetadata()));
        list.add(new ItemStack(this, 1, EnumLootPot.POT_2.getMetadata() * 4));
        list.add(new ItemStack(this, 1, EnumLootPot.POT_3.getMetadata() * 4));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int facing = meta % 4;

        return this.getDefaultState().withProperty(VARIANT, EnumLootPot.byMetadata((meta - facing) / 4)).withProperty(FACING, EnumFacing.getFront(facing + 2));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumLootPot) state.getValue(VARIANT)).getMetadata() * 4 + state.getValue(FACING).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{VARIANT, FACING});
    }

    @Override
    public int damageDropped(IBlockState state) {
        return ((EnumLootPot) state.getValue(VARIANT)).getMetadata() * 4 + state.getValue(FACING).getIndex();
    }

    @Nullable
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (worldIn.getTileEntity(pos) instanceof TileEntityLootPot) {
            TileEntityLootPot tile = (TileEntityLootPot) worldIn.getTileEntity(pos);
            if (tile != null && !worldIn.isRemote) {
                tile.setModelRotationOffset(worldIn.rand.nextInt(41) - 20);
                worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);
            }
        }

        int rotation = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        IBlockState state1 = state;
        if (rotation == 0)
            state1 = state.withProperty(FACING, EnumFacing.NORTH);
        if (rotation == 1)
            state1 = state.withProperty(FACING, EnumFacing.EAST);
        if (rotation == 2)
            state1 = state.withProperty(FACING, EnumFacing.SOUTH);
        if (rotation == 3)
            state1 = state.withProperty(FACING, EnumFacing.WEST);
        worldIn.setBlockState(pos, state1, 3);
    }


    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.getTileEntity(pos) instanceof TileEntityLootPot) {
            TileEntityLootPot tile = (TileEntityLootPot) worldIn.getTileEntity(pos);
            if (playerIn.getHeldItem(hand) != null) {
                ItemStack item = playerIn.getHeldItem(hand);
                ItemStack toAdd = item.copy();
                toAdd.stackSize = 1;
                for (int i = 0; i < 3; i++) {
                    if (tile != null && tile.getStackInSlot(i) == null) {
                        if (!worldIn.isRemote) {
                            tile.setInventorySlotContents(i, toAdd);
                            if (!playerIn.capabilities.isCreativeMode)
                                item.stackSize--;
                            worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);
                        }
                        return true;
                    } else {
                        ItemStack inSlot = tile.getStackInSlot(i);
                        if (inSlot != null && inSlot.stackSize < inSlot.getMaxStackSize() && inSlot.getItemDamage() == toAdd.getItemDamage() && inSlot.getItem() == toAdd.getItem() && inSlot.areItemStackTagsEqual(inSlot, toAdd)) {
                            if (!worldIn.isRemote) {
                                inSlot.stackSize++;
                                if (!playerIn.capabilities.isCreativeMode)
                                    item.stackSize--;
                                worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        IInventory tile = (IInventory) worldIn.getTileEntity(pos);
        if (tile != null)
            for (int i = 0; i < tile.getSizeInventory(); i++) {
                ItemStack stack = tile.getStackInSlot(i);
                if (stack != null) {
                    if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops")) {
                        float f = 0.7F;
                        double d0 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        double d1 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        double d2 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        EntityItem entityitem = new EntityItem(worldIn, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
                        entityitem.setPickupDelay(10);
                        worldIn.spawnEntityInWorld(entityitem);
                    }
                }
            }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote)
            if (worldIn.rand.nextInt(3) == 0) {
                /*TODO add when termites are added
                EntityTermite entity = new EntityTermite(world);
                entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getZ(), pos.getZ() + 0.5D, 0.0F, 0.0F);
                worldIn.spawnEntityInWorld(entity);*/
            }
        super.onBlockDestroyedByPlayer(worldIn, pos, state);
    }

    public enum EnumLootPot implements IStringSerializable {
        POT_1(0, "1"),
        POT_2(1, "2"),
        POT_3(2, "3");

        private static final EnumLootPot[] METADATA_LOOKUP = new EnumLootPot[values().length];
        private final int metadata;
        private final String name;

        private EnumLootPot(int metadataIn, String nameIn) {
            this.metadata = metadataIn;
            this.name = nameIn;
        }

        public int getMetadata() {
            return this.metadata;
        }

        public String toString() {
            return this.name;
        }

        public static EnumLootPot byMetadata(int metadata) {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length) {
                metadata = 0;
            }
            return METADATA_LOOKUP[metadata];
        }

        public String getName() {
            return this.name;
        }

        static {
            for (EnumLootPot type : values()) {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }


    @Override
    public int getSubtypeNumber() {
        return EnumLootPot.values().length;
    }

    @Override
    public String getSubtypeName(int meta) {
        return "%s_" + EnumLootPot.byMetadata(meta).getName();
    }
}
