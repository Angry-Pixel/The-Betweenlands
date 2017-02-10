package thebetweenlands.common.item.tools;

import java.util.List;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.item.corrosion.CorrosionHelper;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemBLShovel extends ItemSpade implements ICorrodible {
	public ItemBLShovel(ToolMaterial material) {
		super(material);

		CorrosionHelper.addCorrosionPropertyOverrides(this);
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return CorrosionHelper.shouldCauseBlockBreakReset(oldStack, newStack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return CorrosionHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state) {
		return CorrosionHelper.getStrVsBlock(super.getStrVsBlock(stack, state), stack, state); 
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		CorrosionHelper.onUpdate(itemStack, world, holder, slot, isHeldItem);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		return CorrosionHelper.getAttributeModifiers(super.getAttributeModifiers(slot, stack), slot, stack, ItemTool.ATTACK_DAMAGE_MODIFIER, this.damageVsEntity);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> lines, boolean advancedItemTooltips) {
		CorrosionHelper.addInformation(itemStack, player, lines, advancedItemTooltips);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(facing == EnumFacing.UP) {
			boolean dug = false;
			IBlockState blockState = world.getBlockState(pos);

			if(blockState.getBlock() == BlockRegistry.COARSE_SWAMP_DIRT) {
				world.setBlockState(pos, BlockRegistry.DUG_SWAMP_DIRT.getDefaultState());
				dug = true;
			}

			if(blockState.getBlock() == BlockRegistry.SWAMP_DIRT) {
				world.setBlockState(pos, BlockRegistry.DUG_SWAMP_DIRT.getDefaultState());
				dug = true;
			}

			if(blockState.getBlock() == BlockRegistry.SWAMP_GRASS) {
				world.setBlockState(pos, BlockRegistry.DUG_SWAMP_GRASS.getDefaultState());
				dug = true;
			}

			if(blockState.getBlock() == BlockRegistry.PURIFIED_SWAMP_DIRT) {
				world.setBlockState(pos, BlockRegistry.DUG_PURIFIED_SWAMP_DIRT.getDefaultState());
				dug = true;
			}

			if(dug) {
				if(world.isRemote) {
					for(int i = 0; i < 80; i++) {
						world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F, (world.rand.nextFloat() - 0.5F) * 0.1F, world.rand.nextFloat() * 0.3f, (world.rand.nextFloat() - 0.5F) * 0.1F, new int[] {Block.getStateId(blockState)});
					}
				}

				SoundType sound = blockState.getBlock().getSoundType(blockState, world, pos, player);
				for(int i = 0; i < 3; i++) {
					world.playSound(null, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, sound.getBreakSound(), SoundCategory.PLAYERS, 1, 0.5f + world.rand.nextFloat() * 0.5f);
				}

				stack.damageItem(1, player);

				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}
}
