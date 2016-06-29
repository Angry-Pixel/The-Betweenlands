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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterial;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemBLShield extends ItemShield {
	private ToolMaterial material;
	//TODO add good way for rendering multiple shields, also add some stuff that is done for the shield item

	public ItemBLShield(ToolMaterial material) {
		this.material = material;
		this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
			}
		});

	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("damage")) {
			return ((double) stack.getTagCompound().getInteger("damage") / (double) getMaxMetaDamage());
		} else
			return 1;
	}


	public int getMaxMetaDamage() {
		return material.getMaxUses() * 2;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return stack.getTagCompound() != null && stack.getTagCompound().hasKey("damage") && stack.getTagCompound().getInteger("damage") > 0;
	}

	public String getItemStackDisplayName(ItemStack stack) {
		return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return BLCreativeTabs.GEARS;
	}


	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		/*if (material == BLMaterial.TOOL_WEEDWOOD) {
            return repair.getItem() == Item.getItemFromBlock(BlockRegistry.WEEDWOOD);
        } else */
		if (material == BLMaterial.TOOL_BONE) {
			return repair.getItem() == Item.getItemFromBlock(BlockRegistry.BETWEENSTONE);
		} else if (material == BLMaterial.TOOL_OCTINE) {
			return EnumItemMisc.OCTINE_INGOT.isItemOf(repair);
		} else if (material == BLMaterial.TOOL_VALONITE) {
			return EnumItemMisc.VALONITE_SHARD.isItemOf(repair);
		}
		return false;
	}

	@Override
	public boolean updateItemStackNBT(NBTTagCompound nbt) {
		return super.updateItemStackNBT(nbt);
	}


	public boolean damageShield(int i, ItemStack stack, EntityLivingBase entityIn) {
		if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("damage")) {
			NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setInteger("damage", material.getMaxUses() * 2);
			stack.setTagCompound(new NBTTagCompound());
		}

		int damage = stack.getTagCompound().getInteger("damage") + i;
		System.out.println(damage);
		if (damage <= 0) {
			entityIn.renderBrokenItemStack(stack);
			--stack.stackSize;

			if (entityIn instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entityIn;
				entityplayer.addStat(StatList.getObjectBreakStats(stack.getItem()));
			}

			if (stack.stackSize < 0) {
				stack.stackSize = 0;
			}

			stack.getTagCompound().setInteger("damage", 0);
		} else {
			stack.getTagCompound().setInteger("damage", damage);
		}

		return true;
	}
}
