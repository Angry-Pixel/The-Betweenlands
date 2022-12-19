package thebetweenlands.common.block.structure;

import java.util.Locale;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.item.EnumBLDyeColor;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockMudBrickShingleStained extends BlockMudBricks implements BlockRegistry.ICustomItemBlock, BlockRegistry.ISubtypeItemBlockModelDefinition {
    public static final PropertyEnum<EnumBLDyeColor> COLOR = PropertyEnum.create("color", EnumBLDyeColor.class);

    public BlockMudBrickShingleStained() {
        super();
        this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, EnumBLDyeColor.DULL_LAVENDER));
    }

    @Override
    public ItemBlock getItemBlock() {
        return ItemBlockEnum.create(this, EnumBLDyeColor.class);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return (state.getValue(COLOR)).getMetadata();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
        EnumBLDyeColor[] var3 = EnumBLDyeColor.values();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            EnumBLDyeColor enumdyecolor = var3[var5];
            items.add(new ItemStack(this, 1, enumdyecolor.getMetadata()));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, COLOR);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(COLOR, EnumBLDyeColor.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumBLDyeColor)state.getValue(COLOR)).getMetadata();
    }


    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this, 1, (state.getValue(COLOR)).getMetadata());
    }

    @Override
    public int getSubtypeNumber() {
        return EnumBLDyeColor.values().length;
    }

    @Override
    public String getSubtypeName(int meta) {
        return "mud_brick_shingle_stained_" + EnumBLDyeColor.values()[meta].name().toLowerCase(Locale.ENGLISH);
    }
}
