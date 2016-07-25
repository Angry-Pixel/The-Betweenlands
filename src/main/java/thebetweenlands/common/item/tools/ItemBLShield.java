package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemBLShield extends ItemShield {
	private ToolMaterial material;

	public ItemBLShield(ToolMaterial material) {
		this.material = material;
		this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
			}
		});

	}

	public int getMaxMetaDamage() {
		return this.material.getMaxUses() * 2;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return BLCreativeTabs.GEARS;
	}

	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		/*if (material == BLMaterialRegistry.TOOL_WEEDWOOD) {
            return repair.getItem() == Item.getItemFromBlock(BlockRegistry.WEEDWOOD);
        } else */
		if (material == BLMaterialRegistry.TOOL_BONE) {
			return repair.getItem() == Item.getItemFromBlock(BlockRegistry.BETWEENSTONE);
		} else if (material == BLMaterialRegistry.TOOL_OCTINE) {
			return EnumItemMisc.OCTINE_INGOT.isItemOf(repair);
		} else if (material == BLMaterialRegistry.TOOL_VALONITE) {
			return EnumItemMisc.VALONITE_SHARD.isItemOf(repair);
		}
		return false;
	}
}
