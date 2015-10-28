package thebetweenlands.blocks.tree;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemGeneric;
import thebetweenlands.items.ItemGeneric.EnumItemGeneric;
import thebetweenlands.proxy.ClientProxy;

import java.util.List;
import java.util.Random;

/**
 * Created by Bart on 8-8-2015.
 */
public class BlockHollowLog extends Block {
    @SideOnly(Side.CLIENT)
    private IIcon iconHoles, iconNoHoles, iconSide, iconMoss;


    public BlockHollowLog() {
        super(Material.wood);
        setHardness(0.7F);
        setStepSound(soundTypeWood);
        setCreativeTab(ModCreativeTabs.blocks);
        setBlockName("thebetweenlands.hollowLog");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 0 ? (meta == 0 || meta == 1 || meta == 4 || meta == 5 ? iconNoHoles : iconHoles) : side == 1 ? (meta == 0 || meta == 2 || meta == 4 || meta == 6 ? iconNoHoles : iconHoles) : (side == 2 || side == 3) && meta > 3 || (side == 4 || side == 5) && meta <= 3 ? iconSide : iconMoss;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        iconNoHoles = reg.registerIcon("thebetweenlands:rottenLog1");
        iconHoles = reg.registerIcon("thebetweenlands:rottenLog2");
        iconSide = reg.registerIcon("thebetweenlands:rottenLog3");
        iconMoss = reg.registerIcon("thebetweenlands:rottenLog4");
    }

    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public int getRenderType() {
        return ClientProxy.BlockRenderIDs.HOLLOW_LOG.id();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB box, List list, Entity entity) {
        float pixel = 0.0625F; // 1 pixel
        // bottom
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, pixel, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, box, list, entity);

        // top
        setBlockBounds(0.0F, 1.0F - pixel, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, box, list, entity);

        // east
        setBlockBounds(1.0F - pixel, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, box, list, entity);

        // south
        setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F + pixel, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, box, list, entity);

        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack is) {
        int l = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

        Random random = new Random();
        int meta = random.nextInt(4) + (l == 0 || l == 2 ? 0 : 4);
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ItemGeneric.createStack(EnumItemGeneric.ROTTEN_BARK).getItem();
	}

	@Override
	public int damageDropped(int meta) {
        return EnumItemGeneric.ROTTEN_BARK.ordinal();
    }
	
    @Override
    public int quantityDropped(Random rand) {
        return 1 + rand.nextInt(4);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (world.rand.nextInt(2000) == 0) {
            BLParticle.MOTH.spawn(world, x, y, z);
        }
    }
}
