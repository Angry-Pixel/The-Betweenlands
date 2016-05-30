package thebetweenlands.common.block.structure;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BlockPortalFrame extends BasicBlock implements BlockRegistry.ISubBlocksBlock {
    public static final PropertyEnum FRAME_POSITION = PropertyEnum.create("frame_position", EnumPortalFrame.class);

    public BlockPortalFrame() {
        super(Material.WOOD);
        setHardness(2.0F);
        setSoundType(SoundType.WOOD);
        setCreativeTab(BLCreativeTabs.PLANTS);
        setDefaultState(this.blockState.getBaseState().withProperty(FRAME_POSITION, EnumPortalFrame.PORTAL_BOTTOM));
    }

    @Override
    public String getUnlocalizedName() {
        return "tile.thebetweenlands.portalBarkFrame";
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
        List<String> models = new ArrayList<>();
        for (EnumPortalFrame frame : EnumPortalFrame.VALUES)
            models.add(frame.name);
        return models;
    }


    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < EnumPortalFrame.VALUES.length; i++)
            list.add(new ItemStack(item, 1, i));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FRAME_POSITION);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        for (EnumPortalFrame frame : EnumPortalFrame.VALUES)
            if (frame.ordinal() == meta)
                return this.getDefaultState().withProperty(FRAME_POSITION, frame);
        return this.getDefaultState().withProperty(FRAME_POSITION, EnumPortalFrame.PORTAL_BOTTOM);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (state.getValue(FRAME_POSITION) instanceof EnumPortalFrame)
            return ((EnumPortalFrame) state.getValue(FRAME_POSITION)).ordinal();
        return 0;
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

        public static final EnumPortalFrame[] VALUES = values();
        public final String name;


        EnumPortalFrame() {
            name = name().toLowerCase(Locale.ENGLISH);
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
