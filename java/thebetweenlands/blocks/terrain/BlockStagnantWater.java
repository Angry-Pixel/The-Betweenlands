package thebetweenlands.blocks.terrain;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.manager.DecayManager;
import thebetweenlands.recipes.BLMaterials;

/**
 * Created by Bart on 29-8-2015.
 */
public class BlockStagnantWater extends BlockFluidClassic {

    @SideOnly(Side.CLIENT)
    protected IIcon stillIcon, flowingIcon;
    public BlockStagnantWater() {
        super(BLFluidRegistry.stagnantWater, Material.water);
        setBlockName("thebetweenlands.stagnantWater");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        stillIcon = register.registerIcon("thebetweenlands:stagnantWater");
        flowingIcon = register.registerIcon("thebetweenlands:stagnantWaterFlowing");
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (entity instanceof EntityPlayer) {
            DecayManager.setDecayLevel(DecayManager.getDecayLevel((EntityPlayer) entity) - 1, (EntityPlayer) entity);
        }
    }

    @Override
    public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
        return !world.getBlock(x, y, z).getMaterial().isLiquid() && super.canDisplace(world, x, y, z);
    }

    @Override
    public boolean displaceIfPossible(World world, int x, int y, int z) {
        return !world.getBlock(x, y, z).getMaterial().isLiquid() && super.displaceIfPossible(world, x, y, z);
    }
}
