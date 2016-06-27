package thebetweenlands.common.item.tools;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.ICorrodible;
import thebetweenlands.util.CorrodibleItemHelper;

public class ItemBLSword extends ItemSword implements ICorrodible {
	public ItemBLSword(ToolMaterial material) {
		super(material);

		CorrodibleItemHelper.addCorrosionPropertyOverrides(this);

		this.addPropertyOverride(new ResourceLocation("gem"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return 0; //TODO: Get from NBT
			}
		});
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation[] getCorrodibleVariants() {
		//Add all corrodible sword variants
		return new ResourceLocation[] {
				new ResourceLocation("thebetweenlands", "weedwood_sword"),
				new ResourceLocation("thebetweenlands", "bone_sword"),
				new ResourceLocation("thebetweenlands", "octine_sword"),
				new ResourceLocation("thebetweenlands", "valonite_sword")
		};
	}
}
