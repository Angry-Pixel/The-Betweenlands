package thebetweenlands.common.item.shields;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.tools.ItemBLShield;

public class ItemWeedwoodShield extends ItemBLShield {
	public ItemWeedwoodShield() {
		super(BLMaterialRegistry.TOOL_WEEDWOOD);
		this.addPropertyOverride(new ResourceLocation("burning"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				NBTTagCompound tag = stack.getTagCompound();
				return tag != null && tag.hasKey("burning") && tag.getBoolean("burning") ? 1.0F : 0.0F;
			}
		});
	}
}
