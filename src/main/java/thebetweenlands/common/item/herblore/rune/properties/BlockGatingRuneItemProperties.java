package thebetweenlands.common.item.herblore.rune.properties;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.runechain.container.IRuneContainerFactory;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.herblore.rune.GatingRuneBlock;
import thebetweenlands.common.item.herblore.rune.DefaultRuneContainerFactory;
import thebetweenlands.common.item.herblore.rune.ItemRune.RuneItemProperties;
import thebetweenlands.util.NBTHelper;

public class BlockGatingRuneItemProperties extends RuneItemProperties {
	private static final String NBT_BLOCK_DATA = "thebetweenlands.block_rune.block_data";

	private final ResourceLocation regName;

	public BlockGatingRuneItemProperties(ResourceLocation regName) {
		this.regName = regName;
	}

	public Block getBlockType(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();

		if(nbt != null && nbt.hasKey(NBT_BLOCK_DATA, Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound blockNbt = nbt.getCompoundTag(NBT_BLOCK_DATA);

			if(blockNbt.hasKey("id", Constants.NBT.TAG_STRING)) {
				return Block.REGISTRY.getObject(new ResourceLocation(blockNbt.getString("id")));
			}
		}

		return null;
	}

	@Override
	public IRuneContainerFactory getFactory(ItemStack stack) {
		return new DefaultRuneContainerFactory(this.regName, () -> new GatingRuneBlock.Blueprint(this.getBlockType(stack)));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {

		ItemStack stack = player.getHeldItem(hand);

		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);

		if(player.isSneaking()) {
			if(nbt.hasKey(NBT_BLOCK_DATA)) {
				nbt.removeTag(NBT_BLOCK_DATA);

				player.swingArm(hand);

				return EnumActionResult.PASS;
			}
		} else {
			IBlockState state = worldIn.getBlockState(pos);

			NBTTagCompound blockNbt = new NBTTagCompound();

			blockNbt.setString("id", state.getBlock().getRegistryName().toString());

			//TODO Also filter meta?

			nbt.setTag(NBT_BLOCK_DATA, blockNbt);

			player.swingArm(hand);

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		Block blockType = this.getBlockType(stack);

		if(blockType != null) {
			Item blockItem = Item.getItemFromBlock(blockType);

			String blockName;
			if(blockItem != Items.AIR) {
				blockName = I18n.translateToLocal(blockItem.getUnlocalizedNameInefficiently(new ItemStack(blockItem)) + ".name").trim();
			} else {
				blockName = I18n.translateToLocal(blockType.getTranslationKey() + ".name");
			}

			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocalFormatted("tooltip.thebetweenlands.rune.gating_block.bound", blockName), 0));
		} else {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocal("tooltip.thebetweenlands.rune.gating_block.unbound"), 0));
		}
	}
}
