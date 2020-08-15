package thebetweenlands.common.item.herblore.rune.properties;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.rune.IRuneContainerFactory;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.herblore.rune.TokenRuneItem;
import thebetweenlands.common.item.herblore.rune.DefaultRuneContainerFactory;
import thebetweenlands.common.item.herblore.rune.ItemRune.RuneItemProperties;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemTokenRuneItemProperties extends RuneItemProperties {
	private static final String NBT_ITEM_DATA = "thebetweenlands.block_rune.block_data";

	private final ResourceLocation regName;

	public ItemTokenRuneItemProperties(ResourceLocation regName) {
		this.regName = regName;
	}

	public Item getItemType(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();

		if(nbt != null && nbt.hasKey(NBT_ITEM_DATA, Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound itemNbt = nbt.getCompoundTag(NBT_ITEM_DATA);

			if(itemNbt.hasKey("id", Constants.NBT.TAG_STRING)) {
				return Item.REGISTRY.getObject(new ResourceLocation(itemNbt.getString("id")));
			}
		}

		return null;
	}

	@Override
	public IRuneContainerFactory getFactory(ItemStack stack) {
		return new DefaultRuneContainerFactory(this.regName, () -> new TokenRuneItem.Blueprint(
				RuneStats.builder()
				.aspect(AspectRegistry.ORDANIIS, 1)
				.duration(5.0f)
				.build(),
				this.getItemType(stack)));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);

		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);

		if(playerIn.isSneaking()) {
			if(nbt.hasKey(NBT_ITEM_DATA)) {
				nbt.removeTag(NBT_ITEM_DATA);

				playerIn.swingArm(handIn);

				return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
			}
		} else {
			Vec3d start = playerIn.getPositionEyes(1);
			Vec3d dir = playerIn.getLookVec();

			float reach = 6.0f;
			int steps = 20;
			float size = 0.2f;

			float scale = 1.0f / steps * reach;

			EntityItem hit = null;

			for(int i = 0; i < 20; i++) {
				AxisAlignedBB aabb = new AxisAlignedBB(
						start.x + dir.x * scale * i - size, start.y + dir.y * scale * i - size, start.z + dir.z * scale * i - size,
						start.x + dir.x * scale * i + size, start.y + dir.y * scale * i + size, start.z + dir.z * scale * i + size);

				List<EntityItem> entities = worldIn.getEntitiesWithinAABB(EntityItem.class, aabb);

				EntityItem closest = null;
				double closestDstSq = Double.MAX_VALUE;

				for(EntityItem entity : entities) {
					double dstSq = entity.getDistanceSq(start.x, start.y, start.z);
					if(dstSq < closestDstSq) {
						closest = entity;
						closestDstSq = dstSq;
					}
				}

				if(closest != null) {
					hit = closest;
					break;
				}
			}

			if(hit != null) {
				ItemStack hitStack = hit.getItem();

				if(!hitStack.isEmpty()) {
					NBTTagCompound itemNbt = new NBTTagCompound();

					itemNbt.setString("id", hitStack.getItem().getRegistryName().toString());

					//TODO Also filter meta?

					nbt.setTag(NBT_ITEM_DATA, itemNbt);
				}

				playerIn.swingArm(handIn);

				return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
			}
		}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		Item itemType = this.getItemType(stack);

		if(itemType != null) {
			String itemName = I18n.translateToLocal(itemType.getUnlocalizedNameInefficiently(new ItemStack(itemType)) + ".name").trim();
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocalFormatted("tooltip.thebetweenlands.rune.token_item.bound", itemName), 0));
		} else {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocal("tooltip.thebetweenlands.rune.token_item.unbound"), 0));
		}
	}
}
