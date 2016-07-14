package thebetweenlands.common.block.terrain;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.BasicBlock;

public class BlockDentrothyst extends BasicBlock {
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

    public enum EnumDentrothyst implements IStringSerializable {
        GREEN(0),
        ORANGE(1);

        int meta;

        EnumDentrothyst(int meta) {
            this.meta = meta;
        }

        public String toString() {
            return this.getName();
        }

        public String getName() {
            return this == GREEN ? "green" : "orange";
        }
    }
}
