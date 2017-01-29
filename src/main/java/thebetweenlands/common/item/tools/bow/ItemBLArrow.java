package thebetweenlands.common.item.tools.bow;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.TranslationHelper;

public class ItemBLArrow extends ItemArrow {
	private EnumArrowType type;

	public ItemBLArrow(EnumArrowType type) {
		this.type = type;
	}

	@Override
	public EntityBLArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
		EntityBLArrow entityArrow = new EntityBLArrow(worldIn, shooter);
		entityArrow.setType(this.type);
		return entityArrow;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4) {
		ItemBLArrow item = (ItemBLArrow) stack.getItem();

		if (item == ItemRegistry.OCTINE_ARROW) {
			list.add(TranslationHelper.translateToLocal("tooltip.arrow.octine"));
		}

		if (item == ItemRegistry.BASILISK_ARROW) {
			list.add(TranslationHelper.translateToLocal("tooltip.arrow.basilisk"));
		}
	}

	public EnumArrowType getType() {
		return this.type;
	}
}
