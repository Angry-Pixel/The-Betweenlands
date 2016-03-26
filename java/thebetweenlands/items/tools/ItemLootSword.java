package thebetweenlands.items.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import thebetweenlands.creativetabs.BLCreativeTabs;

public class ItemLootSword extends ItemSwordBL {
	private List<Class<? extends EntityLivingBase>> instantKills = new ArrayList<Class<? extends EntityLivingBase>>();

	public ItemLootSword(ToolMaterial material) {
		super(material);
		this.setCreativeTab(BLCreativeTabs.gears);
	}

	public ItemLootSword addInstantKills(Class<? extends EntityLivingBase>... instantKills) {
		this.instantKills.addAll(Arrays.asList(instantKills));
		return this;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase attacked, EntityLivingBase attacker) {
		if(!this.instantKills.isEmpty() && this.instantKills.contains(attacked.getClass())) {
			attacked.attackEntityFrom(DamageSource.magic, attacked.getMaxHealth());
		}
		return super.hitEntity(stack, attacked, attacker);
	}
}
