package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.creativetabs.ModCreativeTabs;

import java.util.List;

/**
 * Created by Bart on 12-6-2015.
 */
public class BlockWoodChipPath extends Block {
    public BlockWoodChipPath() {
        super(Material.wood);
        this.setBlockBounds(0, 0.0F, 0, 1.0F, 0.125F, 1.0F);
        this.setBlockName("thebetweenlands.weedwoodChipPath");
        this.setBlockTextureName("thebetweenlands:weedwoodBarkGround");
        this.setCreativeTab(ModCreativeTabs.blocks);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return blockAccess.getBlock(x, y, z) != this && super.shouldSideBeRendered(blockAccess, x, y, z, side);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(world, x, y - 1, z);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(world, x, y - 1, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) {
            world.setBlock(x, y, z, Blocks.air);
        }
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        System.out.println(entity instanceof EntityLiving);
        if (entity instanceof EntityLivingBase && !((EntityLivingBase) entity).isPotionActive(Potion.moveSpeed))
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 2, 0));
    }
}


