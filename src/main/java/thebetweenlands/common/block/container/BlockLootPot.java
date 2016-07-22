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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.List;

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
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
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
        return "%s_" + EnumLootPot.values()[(meta - meta % 4) / 4].getName();
    }
}
