package thebetweenlands.common.item.tools.bow;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nullable;

public class ItemBLArrow extends ItemArrow {
	private EnumArrowType type;

	public static final BehaviorProjectileDispense BLArrowBehaviour = new BehaviorProjectileDispense()
	{
	    protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn)
	    {
		EntityBLArrow entity = new EntityBLArrow(worldIn);
		Item item = stackIn.getItem();
		if(item instanceof ItemBLArrow) {
		    entity.setType(((ItemBLArrow)item).getType());
		} else {
		    entity.setType(EnumArrowType.DEFAULT);
		}
		entity.setPosition(position.getX(), position.getY(), position.getZ());
		entity.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
		return entity;
	    }
	};


	public ItemBLArrow(EnumArrowType type) {
	    this.type = type;
	    BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, ItemBLArrow.BLArrowBehaviour);
	}

	@Override
	public EntityBLArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
		EntityBLArrow entityArrow = new EntityBLArrow(worldIn, shooter);
		entityArrow.setType(this.type);
		return entityArrow;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ItemBLArrow item = (ItemBLArrow) stack.getItem();

		if (item == ItemRegistry.OCTINE_ARROW) {
			tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.arrow.octine"));
		}

		if (item == ItemRegistry.BASILISK_ARROW) {
			tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.arrow.basilisk"));
		}
	}

	public EnumArrowType getType() {
		return this.type;
	}
}
