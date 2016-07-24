package thebetweenlands.common.item.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.IGenericItem;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.feature.structure.WorldGenWeedWoodPortalTree;

public class ItemSwampTalisman extends Item implements ItemRegistry.ISingleJsonSubItems{
	public ItemSwampTalisman() {
		this.setMaxDamage(0);
		this.maxStackSize = 1;
		this.setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void getSubItems(Item item, CreativeTabs tab, List list) {
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
			if (!playerIn.canPlayerEdit(pos, facing, stack))
				return EnumActionResult.FAIL;
			else {
				if (EnumTalisman.SWAMP_TALISMAN_0.isItemOf(stack)) {
					Block block = worldIn.getBlockState(pos).getBlock();
					if (block instanceof BlockSapling) {
						if(new WorldGenWeedWoodPortalTree().generate(worldIn, itemRand, pos)) {
							//  worldIn.playSound(playerIn, pos, "thebetweenlands:portalActivate", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
							playerIn.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 2D, pos.getZ() + 0.5D, playerIn.rotationYaw, playerIn.rotationPitch);
						}
					}
					stack.damageItem(1, playerIn);
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.FAIL;
	}

	@Override
	public List<String> getTypes() {
		List<String> models = new ArrayList<String>();
		for (EnumTalisman type : EnumTalisman.values())
			models.add(type.name());
		return models;
	}

	public enum EnumTalisman implements IGenericItem {
		SWAMP_TALISMAN_0(0),
		SWAMP_TALISMAN_1(1),
		SWAMP_TALISMAN_2(2),
		SWAMP_TALISMAN_3(3),
		SWAMP_TALISMAN_4(4);

		private final int id;
		private final String unlocalizedName;

		EnumTalisman(int id) {
			this.id = id;
			this.unlocalizedName = this.name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

		@Override
		public int getID() {
			return this.id;
		}

		@Override
		public Item getItem() {
			return ItemRegistry.SWAMP_TALISMAN;
		}
	}
}
