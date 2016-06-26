package thebetweenlands.common.block.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BlockGenericStone extends Block implements BlockRegistry.IHasCustomItem, BlockRegistry.ISubBlocksBlock{

    public static final PropertyBool IS_SPOOPY = PropertyBool.create("isspoopy");
    public static final PropertyEnum<EnumStoneType> VARIANT = PropertyEnum.<EnumStoneType>create("variant", EnumStoneType.class);

    public BlockGenericStone() {
        super(Material.ROCK);
        setHardness(1.5F);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        //setBlockName("thebetweenlands.genericStone");
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumStoneType.CORRUPTBETWEENSTONE).withProperty(IS_SPOOPY, false));
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getSubBlocks(Item id, CreativeTabs tab, List list) {
        for (EnumStoneType type : EnumStoneType.values())
            list.add(new ItemStack(id, 1, type.ordinal()));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {VARIANT, IS_SPOOPY});
    }

    protected ItemStack createStackedBlock(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, ((EnumStoneType)state.getValue(VARIANT)).ordinal());
    }

    public int damageDropped(IBlockState state) {
        return ((EnumStoneType)state.getValue(VARIANT)).ordinal();
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(IS_SPOOPY, false).withProperty(VARIANT, EnumStoneType.values()[meta]);
    }

    @Override
    public ItemBlock getItemBlock() {
        return ItemBlockEnum.create(this, EnumStoneType.class);
    }

    @Override
    public List<String> getModels() {
        List<String> models = new ArrayList<String>();
        for (EnumStoneType type : EnumStoneType.values())
            models.add(type.getName());
        return models;
    }

    public static enum EnumStoneType implements IStringSerializable{
        CORRUPTBETWEENSTONE;

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH);
        }

    }
}
