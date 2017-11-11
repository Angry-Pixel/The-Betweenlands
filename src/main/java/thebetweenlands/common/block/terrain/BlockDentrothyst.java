package thebetweenlands.common.block.terrain;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockDentrothyst extends BasicBlock implements BlockRegistry.ICustomItemBlock, BlockRegistry.ISubtypeBlock {
    public static PropertyEnum<EnumDentrothyst> TYPE = PropertyEnum.create("type", EnumDentrothyst.class);

    public BlockDentrothyst(Material materialIn) {
        super(materialIn);
        setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumDentrothyst.GREEN));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, meta == 0 ? EnumDentrothyst.GREEN : EnumDentrothyst.ORANGE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public ItemBlock getItemBlock() {
        return ItemBlockEnum.create(this, EnumDentrothyst.class);
    }

    @Override
    public int getSubtypeNumber() {
        return EnumDentrothyst.values().length;
    }

    @Override
    public String getSubtypeName(int meta) {
        return "%s_" + EnumDentrothyst.values()[meta].getName();
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, EnumDentrothyst.GREEN.getMeta()));
        list.add(new ItemStack(this, 1, EnumDentrothyst.ORANGE.getMeta()));
    }

    public enum EnumDentrothyst implements IStringSerializable {
        GREEN(0),
        ORANGE(1);

        int meta;

        EnumDentrothyst(int meta) {
            this.meta = meta;
        }

        @Override
		public String toString() {
            return this.getName();
        }

        public int getMeta() {
            return this.meta;
        }

        @Override
		public String getName() {
            return this == GREEN ? "green" : "orange";
        }
    }
}
