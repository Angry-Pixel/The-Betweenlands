package thebetweenlands.common.item.misc;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;

public class ItemChiromawTame extends ItemMob {
	private final boolean electric;

	public ItemChiromawTame(boolean electric) {
		super(1, EntityChiromawTame.class, entity -> entity.setElectricBoogaloo(electric));
		this.electric = electric;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return this.electric;
	}
}
