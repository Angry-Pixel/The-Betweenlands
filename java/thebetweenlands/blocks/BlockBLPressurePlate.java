package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Bart on 16-6-2015.
 */
public class BlockBLPressurePlate extends BlockBasePressurePlate {
    private BlockPressurePlate.Sensitivity sensitivity;

    public BlockBLPressurePlate(String name, Material material, BlockPressurePlate.Sensitivity sensitivity1) {
        super(name, material);

        sensitivity = sensitivity1;
        this.setBlockName("thebetweenlands." + name + "PressurePlate");
        textureName = "thebetweenlands:" + name;
        setCreativeTab(ModCreativeTabs.blocks);
    }

    @Override
    protected int func_150065_e(World world, int x, int y, int z) {
        List list = null;

        if (this.sensitivity == BlockPressurePlate.Sensitivity.everything) {
            list = world.getEntitiesWithinAABBExcludingEntity((Entity)null, this.func_150061_a(x, y, z));
        }

        if (this.sensitivity == BlockPressurePlate.Sensitivity.mobs) {
            list = world.getEntitiesWithinAABB(EntityLivingBase.class, this.func_150061_a(x, y, z));
        }

        if (this.sensitivity == BlockPressurePlate.Sensitivity.players) {
            list = world.getEntitiesWithinAABB(EntityPlayer.class, this.func_150061_a(x, y, z));
        }

        if (list != null && !list.isEmpty()) {

            for (Object aList : list) {
                Entity entity = (Entity) aList;

                if (!entity.doesEntityNotTriggerPressurePlate()) {
                    return 15;
                }
            }
        }

        return 0;
    }

    @Override
    protected int func_150066_d(int meta) {
        return meta > 0 ? 1 : 0;
    }

    @Override
    protected int func_150060_c(int meta) {
        return meta == 1 ? 15 : 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(textureName);
    }
}
