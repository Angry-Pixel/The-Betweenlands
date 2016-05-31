package thebetweenlands.common.block.structure;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry.IHasCustomItem;
import thebetweenlands.common.registries.BlockRegistry.ISubBlocksBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BlockPortalFrame extends BasicBlock implements IHasCustomItem, ISubBlocksBlock {
    public static final PropertyEnum<EnumPortalFrame> FRAME_POSITION = PropertyEnum.create("frame_position", EnumPortalFrame.class);
    public static final PropertyBool X_AXIS = PropertyBool.create("x_axis");

    public BlockPortalFrame() {
        super(Material.WOOD);
        setHardness(2.0F);
        setSoundType(SoundType.WOOD);
        setCreativeTab(BLCreativeTabs.PLANTS);
        setDefaultState(this.blockState.getBaseState().withProperty(FRAME_POSITION, EnumPortalFrame.PORTAL_CORNER_TOP_LEFT));
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        //drops.add(new ItemStack(Item.getItemFromBlock(Registries.INSTANCE.blockRegistry.portalBark)));
        return drops;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public List<String> getModels() {
        List<String> models = new ArrayList<String>();
        for (EnumPortalFrame type : EnumPortalFrame.values())
            models.add(type.getName());
        return models;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumPortalFrame type : EnumPortalFrame.values())
            list.add(new ItemStack(item, 1, type.ordinal()));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FRAME_POSITION, X_AXIS);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FRAME_POSITION, EnumPortalFrame.values()[meta > 7 ? meta - 8 : meta]).withProperty(X_AXIS, meta > 7);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        EnumPortalFrame type = state.getValue(FRAME_POSITION);
        return type.ordinal() + (state.getValue(X_AXIS) ? 8 : 0);
    }

    @Override
    public ItemBlock getItemBlock() {
        return ItemBlockEnum.create(this, EnumPortalFrame.class);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (placer.getHorizontalFacing().getAxis() == EnumFacing.Axis.X)
            worldIn.setBlockState(pos, state.withProperty(X_AXIS, true));
    }

    public enum EnumPortalFrame implements IStringSerializable {
        PORTAL_CORNER_TOP_LEFT,
        PORTAL_TOP,
        PORTAL_CORNER_TOP_RIGHT,
        PORTAL_SIDE_RIGHT,
        PORTAL_SIDE_LEFT,
        PORTAL_CORNER_BOTTOM_LEFT,
        PORTAL_BOTTOM,
        PORTAL_CORNER_BOTTOM_RIGHT;

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }
}
