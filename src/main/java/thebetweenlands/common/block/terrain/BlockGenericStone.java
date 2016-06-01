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
        setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumStoneType.CRAGROCK).withProperty(IS_SPOOPY, false));
    }


    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random){
        if (!world.isRemote) {
            if (state.getValue(VARIANT) == EnumStoneType.MOSSYCRAGROCK_1) {
                BlockPos newPos = pos.add(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1);

                Block block = world.getBlockState(newPos).getBlock();
                int meta = world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
                if (block instanceof BlockGenericStone && meta == EnumStoneType.CRAGROCK.ordinal()) {
                    if (world.getBlockState(newPos.up()).getBlock() instanceof BlockGenericStone && world.getBlockState(newPos.up(2)).getBlock() == Blocks.AIR &&(world.getBlockState(newPos.up()).getBlock().getMetaFromState(world.getBlockState(newPos.up())) == EnumStoneType.MOSSYCRAGROCK_1.ordinal() || world.getBlockState(newPos.up()).getBlock().getMetaFromState(world.getBlockState(newPos.up())) == EnumStoneType.CRAGROCK.ordinal()))
                        world.setBlockState(newPos, state.withProperty(VARIANT, EnumStoneType.MOSSYCRAGROCK_2), 2);
                    else if (world.getBlockState(newPos).getBlock() instanceof BlockGenericStone && world.getBlockState(newPos.up()).getBlock() == Blocks.AIR)
                        world.setBlockState(newPos, state.withProperty(VARIANT, EnumStoneType.MOSSYCRAGROCK_1), 2);
                }
            }
        }
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
        CORRUPTBETWEENSTONE,
        CRAGROCK,
        MOSSYCRAGROCK_1,
        MOSSYCRAGROCK_2;

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH);
        }

    }
}
