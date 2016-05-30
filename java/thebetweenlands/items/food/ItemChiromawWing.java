package thebetweenlands.items.food;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesFood;
import thebetweenlands.event.player.FoodSicknessEventHandler.Sickness;
import thebetweenlands.manual.IManualEntryItem;

public class ItemChiromawWing extends ItemFood implements IManualEntryItem {
	public ItemChiromawWing() {
		super(0, 0, false);
		this.setUnlocalizedName("thebetweenlands.chiromawWing");
		this.setTextureName("thebetweenlands:chiromawWing");
		this.setCreativeTab(BLCreativeTabs.items);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		super.onFoodEaten(stack, world, player);

		if(player != null) {
			EntityPropertiesFood property = BLEntityPropertiesRegistry.HANDLER.getProperties(player, EntityPropertiesFood.class);
			if(property != null) {
				if(Sickness.getSicknessForHatred(property.getFoodHatred(this)) != Sickness.SICK) {
					property.increaseFoodHatred(this, 64, 32);
				} else {
					player.addPotionEffect(new PotionEffect(Potion.hunger.id, 600, 2));
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		list.add(StatCollector.translateToLocal("chiromawWing.taste"));
		EntityPropertiesFood prop = BLEntityPropertiesRegistry.HANDLER.getProperties(player, EntityPropertiesFood.class);
		if(prop != null) {
			if(prop.getSickness(this) == Sickness.SICK) {
				list.add(StatCollector.translateToLocal("chiromawWing.dontEat"));
			}
		}
	}

	@Override
	public String manualName(int meta) {
		return "chiromawWing";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[0];
	}

	@Override
	public int metas() {
		return 0;
	}
}
