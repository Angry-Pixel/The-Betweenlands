package thebetweenlands.common.block.structure;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.render.json.JsonRenderGenerator;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.ICustomJsonGenerationBlock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BlockDruidStone extends BasicBlock implements ICustomJsonGenerationBlock {
    public static final PropertyInteger RANDOM = PropertyInteger.create("random", 0, 7);
    private String type;

    public BlockDruidStone(Material blockMaterialIn, String blockName) {
        super(blockMaterialIn);
        setHardness(1.5F);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 0);
        setLightLevel(0.8F);
        setCreativeTab(BLCreativeTabs.blocks);
        this.type = blockName;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, RANDOM);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(RANDOM, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(RANDOM);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int rot = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 2.5D) + 2 & 3;
        worldIn.setBlockState(pos, getStateFromMeta(rot), 3);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        double pixel = 0.0625D;
        if (world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)) <= 3 && rand.nextInt(3) == 0) {
            for (int l = 0; l <= 5; l++) {
                double particleX = pos.getX() + rand.nextFloat();
                double particleY = pos.getY() + rand.nextFloat();
                double particleZ = pos.getZ() + rand.nextFloat();

                if (l == 0 && !world.getBlockState(pos.up()).isOpaqueCube())
                    particleY = pos.getY() + 1 + pixel;

                if (l == 1 && !world.getBlockState(pos.down()).isOpaqueCube())
                    particleY = pos.getY() - pixel;

                if (l == 2 && !world.getBlockState(pos.south()).isOpaqueCube())
                    particleZ = pos.getZ() + 1 + pixel;

                if (l == 3 && !world.getBlockState(pos.north()).isOpaqueCube())
                    particleZ = pos.getZ() - pixel;

                if (l == 4 && !world.getBlockState(pos.east()).isOpaqueCube())
                    particleX = pos.getX() + 1 + pixel;

                if (l == 5 && !world.getBlockState(pos.west()).isOpaqueCube())
                    particleX = pos.getX() - pixel;
                //TODO enable when particles are added
                /*if (particleX < pos.getX() || particleX > pos.getX() + 1 || particleY < pos.getY() || particleY > pos.getY() + 1 || particleZ < pos.getZ() || particleZ > pos.getZ() + 1)
                    BLParticle.DRUID_MAGIC_BIG.spawn(world, particleX, particleY, particleZ, (rand.nextFloat() - rand.nextFloat()) *0.1, 0, (rand.nextFloat() - rand.nextFloat())*0.1, rand.nextFloat() + 0.5F);*/
            }
        }
    }

    @Override
    public String getBlockStateText() {
        Map<String, String> map = new HashMap<>();
        map.put("0", "\"thebetweenlands:" + type + "\"");
        map.put("1", "\"thebetweenlands:" + type + "\", \"y\": 90");
        map.put("2", "\"thebetweenlands:" + type + "\", \"y\": 180");
        map.put("3", "\"thebetweenlands:" + type + "\", \"y\": -90");
        map.put("4", "\"thebetweenlands:" + type + "_active\"");
        map.put("5", "\"thebetweenlands:" + type + "_active\", \"y\": 90");
        map.put("6", "\"thebetweenlands:" + type + "_active\", \"y\": 180");
        map.put("7", "\"thebetweenlands:" + type + "_active\", \"y\": -90");
        return JsonRenderGenerator.complexBlockStates("random", map);
    }

    @Override
    public String getBlockModelText(int meta) {
        String format;
        if (meta == 0)
            format = String.format(JsonRenderGenerator.BLOCK_CUBE_FORMAT, type, "stone", "stone", type, "stone", "stone", "stone");
        else
            format = String.format(JsonRenderGenerator.BLOCK_CUBE_FORMAT, type + "_active", "stone", "stone", type + "_active", "stone", "stone", "stone");
        return format.replace("thebetweenlands:blocks/stone", "blocks/stone");
    }

    @Override
    public String getFileNameFromMeta(int meta) {
        return meta == 0 ? type : type + "Active";
    }

    @Override
    public void getMetas(List<Integer> list) {
        list.add(0);
        list.add(4);
    }

    @Override
    public String getBlockModelForItem() {
        return null;
    }
}
