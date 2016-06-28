package thebetweenlands.common.item.tools;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.corrosion.CorrosionHelper;
import thebetweenlands.common.item.ICorrodible;


public class ItemBLAxe extends ItemTool implements ICorrodible {
	public ItemBLAxe(ToolMaterial material) {
		super(8.0F, -3.2F, material, Sets.newHashSet(new Block[]{}));

		CorrosionHelper.addCorrosionPropertyOverrides(this);
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state) {
		Material material = state.getMaterial();
		return material != Material.WOOD && material != Material.PLANTS && material != Material.VINE ? super.getStrVsBlock(stack, state) : this.efficiencyOnProperMaterial;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation[] getCorrodibleVariants() {
		return CorrosionHelper.getVariantFromUnlocalizedName(this.getUnlocalizedName());
	}
}
