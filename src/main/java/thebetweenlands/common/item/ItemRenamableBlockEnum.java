package thebetweenlands.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import thebetweenlands.api.item.IRenamableItem;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;

public class ItemRenamableBlockEnum<T extends Enum<T> & IStringSerializable> extends ItemBlockEnum<T> implements IRenamableItem {
	public static <T extends Enum<T> & IStringSerializable> ItemRenamableBlockEnum<T> create(Block block, Class<T> cls) {
		return new ItemRenamableBlockEnum<T>(block, cls.getEnumConstants(), '.', IGenericMetaSelector.class.isAssignableFrom(cls));
	}

	public static <T extends Enum<T> & IStringSerializable> ItemRenamableBlockEnum<T> create(Block block, Class<T> cls, char separator) {
		return new ItemRenamableBlockEnum<T>(block, cls.getEnumConstants(), separator, IGenericMetaSelector.class.isAssignableFrom(cls));
	}

	protected ItemRenamableBlockEnum(Block block, T[] values, char separator, boolean hasGenericMetaSelector) {
		super(block, values, separator, hasGenericMetaSelector);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(player.isSneaking()) {
			ItemStack stack = player.getHeldItem(hand);

			if(!world.isRemote) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ITEM_RENAMING, world, hand == EnumHand.MAIN_HAND ? 0 : 1, 0, 0);
			}

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}

		return super.onItemRightClick(world, player, hand);
	}
}
