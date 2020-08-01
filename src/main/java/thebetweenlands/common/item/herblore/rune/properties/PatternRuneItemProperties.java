package thebetweenlands.common.item.herblore.rune.properties;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.rune.IRuneContainerFactory;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.common.herblore.rune.RuneMarkPattern;
import thebetweenlands.common.item.herblore.rune.DefaultRuneContainerFactory;
import thebetweenlands.common.item.herblore.rune.ItemRune.RuneItemProperties;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.util.NBTHelper;

public class PatternRuneItemProperties extends RuneItemProperties {
	private static final String NBT_PATTERN_CENTER_X = "thebetweenlands.pattern_rune.pattern_center_x";
	private static final String NBT_PATTERN_CENTER_Y = "thebetweenlands.pattern_rune.pattern_center_y";
	private static final String NBT_PATTERN_CENTER_Z = "thebetweenlands.pattern_rune.pattern_center_z";
	private static final String NBT_PATTERN_BLOCKS = "thebetweenlands.pattern_rune.pattern_blocks";

	private final ResourceLocation regName;

	public PatternRuneItemProperties(ResourceLocation regName) {
		this.regName = regName;
	}

	@Override
	public IRuneContainerFactory getFactory(ItemStack stack) {
		List<BlockPos> pattern = new ArrayList<>();

		if(stack.hasTagCompound()) {
			NBTTagList blocks = stack.getTagCompound().getTagList(NBT_PATTERN_BLOCKS, Constants.NBT.TAG_LONG);

			for(int i = 0; i < blocks.tagCount(); i++) {
				pattern.add(BlockPos.fromLong(((NBTTagLong)blocks.get(i)).getLong()));
			}
		}

		return new DefaultRuneContainerFactory(this.regName, new RuneMarkPattern.Blueprint(
				RuneStats.builder()
				.aspect(AspectRegistry.ORDANIIS, 1)
				.duration(5.0f)
				.build(),
				pattern));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {

		ItemStack stack = player.getHeldItem(hand);

		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);

		boolean hasCenter = nbt.hasKey(NBT_PATTERN_CENTER_X, Constants.NBT.TAG_INT) && nbt.hasKey(NBT_PATTERN_CENTER_Y, Constants.NBT.TAG_INT) && nbt.hasKey(NBT_PATTERN_CENTER_Z, Constants.NBT.TAG_INT);
		BlockPos center = new BlockPos(nbt.getInteger(NBT_PATTERN_CENTER_X), nbt.getInteger(NBT_PATTERN_CENTER_Y), nbt.getInteger(NBT_PATTERN_CENTER_Z));

		if(player.isSneaking() || !hasCenter) {
			if(!player.isSneaking()) {
				//TODO Message
			} else {
				if(center.equals(pos)) {
					nbt.removeTag(NBT_PATTERN_CENTER_X);
					nbt.removeTag(NBT_PATTERN_CENTER_Y);
					nbt.removeTag(NBT_PATTERN_CENTER_Z);
					nbt.removeTag(NBT_PATTERN_BLOCKS);
				} else {
					nbt.setInteger(NBT_PATTERN_CENTER_X, pos.getX());
					nbt.setInteger(NBT_PATTERN_CENTER_Y, pos.getY());
					nbt.setInteger(NBT_PATTERN_CENTER_Z, pos.getZ());
				}
			}
		} else {
			NBTTagList blocks = nbt.getTagList(NBT_PATTERN_BLOCKS, Constants.NBT.TAG_LONG);

			boolean contained = false;

			for(int i = 0; i < blocks.tagCount(); i++) {
				BlockPos block = BlockPos.fromLong(((NBTTagLong)blocks.get(i)).getLong()).add(center);

				if(block.equals(pos)) {
					blocks.removeTag(i);
					contained = true;
				}
			}

			if(!contained) {
				blocks.appendTag(new NBTTagLong(pos.subtract(center).toLong()));
			}

			nbt.setTag(NBT_PATTERN_BLOCKS, blocks);
		}

		return EnumActionResult.SUCCESS;
	}
}
