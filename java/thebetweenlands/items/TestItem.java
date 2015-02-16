package thebetweenlands.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import thebetweenlands.world.feature.structure.WorlGenDruidCircle;

import java.util.Random;

public class TestItem
        extends ItemSword
{

    public TestItem() {
        super(Item.ToolMaterial.IRON);
    }

    @Override
    public boolean getIsRepairable(ItemStack itemStack1, ItemStack itemStack2) {
        return Items.iron_ingot == itemStack2.getItem() || super.getIsRepairable(itemStack1, itemStack2);
    }

    // Remove onItemUse method completely after testing is over!!!!
    @Override
    public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Random rand = new Random();
        if( !world.isRemote && player.isSneaking() ) {
            new WorlGenDruidCircle().generateStructure(world, rand, x, y + 1, z);
            return true;
        }
        return false;
    }
}
