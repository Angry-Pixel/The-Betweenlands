package thebetweenlands.common.block.misc;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry;
import java.util.Locale;

public class BlockFilteredSiltGlassStained extends BlockFilteredSiltGlass implements BlockRegistry.ICustomItemBlock, BlockRegistry.ISubtypeItemBlockModelDefinition {
    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.<EnumDyeColor>create("color", EnumDyeColor.class);

    public BlockFilteredSiltGlassStained() {
        super();
        this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, EnumDyeColor.WHITE));
    }

    @Override
    public ItemBlock getItemBlock() {
        return ItemBlockEnum.create(this, EnumDyeColor.class);
    }

    @Override
    public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
        IExtendedBlockState state = (IExtendedBlockState) oldState;
        return this.getExtendedConnectedTextureState(state, worldIn, pos, new IConnectionRules() {
            @Override
            public boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing face, BlockPos to) {
                EnumFacing.Axis axis = face.getAxis();
                boolean onSamePlane = (axis != EnumFacing.Axis.X || (to.getX() - pos.getX()) == 0) && (axis != EnumFacing.Axis.Y || (to.getY() - pos.getY()) == 0) && (axis != EnumFacing.Axis.Z || (to.getZ() - pos.getZ()) == 0);
                return onSamePlane && world.getBlockState(to).getBlock() instanceof BlockFilteredSiltGlassStained && !(world.getBlockState(to).getBlock() instanceof BlockFilteredSiltGlassStainedSeparated);
            }

            @Override
            public boolean canConnectThrough(IBlockAccess world, BlockPos pos, EnumFacing face, BlockPos to) {
                EnumFacing.Axis axis = face.getAxis();
                if((axis == EnumFacing.Axis.X && to.getX() - pos.getX() != 0) || (axis == EnumFacing.Axis.Y && to.getY() - pos.getY() != 0) || (axis == EnumFacing.Axis.Z && to.getZ() - pos.getZ() != 0)) {
                    return !world.isSideSolid(to, face.getOpposite(), false) && world.getBlockState(to).getBlock() instanceof BlockFilteredSiltGlassStained == false;
                }
                return false;
            }
        });
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return ((EnumDyeColor)state.getValue(COLOR)).getMetadata();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
        EnumDyeColor[] var3 = EnumDyeColor.values();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            EnumDyeColor enumdyecolor = var3[var5];
            items.add(new ItemStack(this, 1, enumdyecolor.getMetadata()));
        }
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this, 1, ((EnumDyeColor)state.getValue(COLOR)).getMetadata());
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumDyeColor)state.getValue(COLOR)).getMetadata();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[] {COLOR}, new IUnlistedProperty[0]));
    }

    @Override
    public int getSubtypeNumber() {
        return EnumDyeColor.values().length;
    }

    @Override
    public String getSubtypeName(int meta) {
        return "filtered_silt_glass_stained_" + EnumDyeColor.values()[meta].name().toLowerCase(Locale.ENGLISH);
    }
}
