package thebetweenlands.items.misc;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.world.feature.trees.WorldGenGiantTreeAlive;

public class ItemTestItem2 extends ItemSword implements IManualEntryItem {
	public ItemTestItem2() {
		super(Item.ToolMaterial.IRON);
		this.setCreativeTab(null);
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
			/*EntityFortressBossTeleporter tp = new EntityFortressBossTeleporter(world);
			tp.setLocationAndAngles(x+hitX, y+hitY, z+hitZ, 0, 0);
			tp.setTeleportDestination(Vec3.createVectorHelper(x+hitX, y+hitY+10, z+hitZ));
			tp.setBossSpawnPosition(Vec3.createVectorHelper(x+hitX, y+hitY+15, z+hitZ));
			world.spawnEntityInWorld(tp);*/
			WorldGenGiantTreeAlive gen = new WorldGenGiantTreeAlive();
			gen.generateTree(world, world.rand, x, y, z);
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
