package thebetweenlands.common.item.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.common.base.CaseFormat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.common.item.IGenericItem;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.gen.feature.structure.WorldGenWeedwoodPortalTree;

public class ItemSwampTalisman extends Item implements ItemRegistry.ISingleJsonSubItems{
	public ItemSwampTalisman() {
		this.setMaxDamage(0);
		this.maxStackSize = 1;
		this.setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (EnumTalisman type : EnumTalisman.values())
			list.add(type.create(1));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands." + IGenericItem.getFromStack(EnumTalisman.class, stack).getUnlocalizedName();
		} catch (Exception e) {
			return "item.thebetweenlands.unknownTalisman";
		}
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (!playerIn.canPlayerEdit(pos, facing, stack)) {
				return EnumActionResult.FAIL;
			} else {
				if (EnumTalisman.SWAMP_TALISMAN_0.isItemOf(stack)) {
					Block block = worldIn.getBlockState(pos).getBlock();
					if (this.isBlockSapling(block)) {
						if(new WorldGenWeedwoodPortalTree().generate(worldIn, itemRand, pos)) {
							worldIn.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundRegistry.PORTAL_ACTIVATE, SoundCategory.PLAYERS, 0.5F, itemRand.nextFloat() * 0.4F + 0.8F);
							playerIn.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 2D, pos.getZ() + 0.5D, playerIn.rotationYaw, playerIn.rotationPitch);
						} else {
							playerIn.addChatMessage(new TextComponentTranslation("talisman.noplace"));
						}
					}
					stack.damageItem(1, playerIn);
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.FAIL;
	}

	protected boolean isBlockSapling(Block block) {
		if(block instanceof BlockSapling) {
			return true;
		}
		List<ItemStack> dict = OreDictionary.getOres("treeSapling");
		for(ItemStack stack : dict) {
			if(stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock() == block) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> getTypes() {
		List<String> models = new ArrayList<String>();
		for (EnumTalisman type : EnumTalisman.values())
			models.add(type.name());
		return models;
	}

	public enum EnumTalisman implements IGenericItem {
		SWAMP_TALISMAN_0,
		SWAMP_TALISMAN_1,
		SWAMP_TALISMAN_2,
		SWAMP_TALISMAN_3,
		SWAMP_TALISMAN_4;

		private final String unlocalizedName;
		private final String modelName;

		EnumTalisman() {
			this.modelName = this.name().toLowerCase(Locale.ENGLISH);
			this.unlocalizedName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.modelName);
		}

		@Override
		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

		@Override
		public String getModelName() {
			return this.modelName;
		}

		@Override
		public int getID() {
			return this.ordinal();
		}

		@Override
		public Item getItem() {
			return ItemRegistry.SWAMP_TALISMAN;
		}
	}
}
