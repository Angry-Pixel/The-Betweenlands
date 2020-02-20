package thebetweenlands.common.item.tools;

import java.util.List;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;

public class ItemBLShovel extends ItemSpade implements ICorrodible, IAnimatorRepairable {
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
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		return CorrosionHelper.getDestroySpeed(super.getDestroySpeed(stack, state), stack, state);
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		CorrosionHelper.updateCorrosion(itemStack, world, holder, slot, isHeldItem);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		return CorrosionHelper.getAttributeModifiers(super.getAttributeModifiers(slot, stack), slot, stack, ATTACK_DAMAGE_MODIFIER, this.attackDamage);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		CorrosionHelper.addCorrosionTooltips(stack, tooltip, flagIn.isAdvanced());
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
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

				player.getHeldItem(hand).damageItem(1, player);

				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}
	
	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(this.toolMaterial);
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(this.toolMaterial);
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(this.toolMaterial);
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(this.toolMaterial);
	}
}
