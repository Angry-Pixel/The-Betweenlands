package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.proxy.ClientProxy;

/**
 * Created by Bart on 12-6-2015.
 */
public class BlockWalkway extends Block {
    public IIcon icon;

    public BlockWalkway() {
        super(Material.wood);
        this.setBlockName("thebetweenlands.walkway");
        this.setCreativeTab(ModCreativeTabs.blocks);
        this.setBlockBounds(0, 0.0F, 0, 1.0F, 0.6F, 1.0F);
    }

    @Override
    public int getRenderType() {
        return ClientProxy.BlockRenderIDs.WALKWAY.id();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icon = register.registerIcon("thebetweenlands:walkway");
    }

    @Override
    public boolean isOpaqueCube(){
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isNormalCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isBlockNormalCube(){
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
        int facing = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

        if (facing == 1 || facing == 3)
            world.setBlockMetadataWithNotify(x, y, z, 1, 2);
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        double boost = 2.3D;
        entity.motionX *= boost;
        entity.motionZ *= boost;
    }

}
