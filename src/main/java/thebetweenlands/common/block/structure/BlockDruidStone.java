package thebetweenlands.common.block.structure;

import java.util.Random;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockDruidStone extends BasicBlock implements BlockRegistry.ISubtypeBlock {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockDruidStone(Material blockMaterialIn, String blockName) {
        super(blockMaterialIn);
        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(ACTIVE, false)
        );
        setHardness(1.5F);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 0);
        setLightLevel(0.8F);
        setCreativeTab(BLCreativeTabs.BLOCKS);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ACTIVE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
            .withProperty(FACING, EnumFacing.getHorizontal(meta))
            .withProperty(ACTIVE, (meta & 4) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() | (state.getValue(ACTIVE) ? 4 : 0);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        double pixel = 0.625;
        if (!world.getBlockState(pos).getValue(ACTIVE) && rand.nextInt(3) == 0) {
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos side = pos.offset(facing);
                if (!world.getBlockState(side).isOpaqueCube()) {
                    double dx = rand.nextFloat() - 0.5, dy = rand.nextFloat() - 0.5, dz = rand.nextFloat() - 0.5;
                    int vx = facing.getFrontOffsetX();
                    int vy = facing.getFrontOffsetY();
                    int vz = facing.getFrontOffsetZ();
                    dx *= (1 - Math.abs(vx));
                    dy *= (1 - Math.abs(vy));
                    dz *= (1 - Math.abs(vz));
                    double particleX = pos.getX() + 0.5 + dx + vx * pixel;
                    double particleY = pos.getY() + 0.5 + dy + vy * pixel;
                    double particleZ = pos.getZ() + 0.5 + dz + vz * pixel;
                    //TODO enable when particles are added
                    //BLParticle.DRUID_MAGIC_BIG.spawn(world, particleX, particleY, particleZ, (rand.nextFloat() - rand.nextFloat()) *0.1, 0, (rand.nextFloat() - rand.nextFloat())*0.1, rand.nextFloat() + 0.5F);
                }                    
            }
        }
    }

    @Override
    public int getSubtypeNumber() {
        return 2;
    }

    @Override
    public String getSubtypeName(int meta) {
        switch(meta) {
        default:
        case 0:
            return "%s";
        case 1:
            return "%s_active";
        }
    }
}
