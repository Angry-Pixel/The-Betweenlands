package thebetweenlands.items.misc;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.world.feature.structure.WorldGenWightTower;

public class ItemTestItem extends ItemSword implements IManualEntryItem {
    public ItemTestItem() {
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
        if(!world.isRemote && player.isSneaking()) {
        	WorldGenWightTower tower = new WorldGenWightTower();
            tower.generate(world, rand, x, y + 1, z);
           // WorldGenSmallRuins ruin = new WorldGenSmallRuins();
          //  ruin.generate(world, rand, x, y + 1, z);
            return true;
        }
        return false;
    }

    @Override
    public String manualName(int meta) {
        return "testItem";
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public int[] recipeType(int meta) {
        return new int[]{4};
    }

    @Override
    public int metas() {
        return 0;
    }
}
