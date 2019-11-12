package thebetweenlands.common.item.misc;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.herblore.ItemCrushed.EnumItemCrushed;
import thebetweenlands.common.item.herblore.ItemPlantDrop.EnumItemPlantDrop;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemOctineIngot extends Item {
	public ItemOctineIngot() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("tooltip.bl.octine.fire"));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		RayTraceResult result = this.rayTrace(worldIn, playerIn, true);
		if(result != null && result.typeOfHit == Type.BLOCK) {
			BlockPos offsetPos = result.getBlockPos().offset(result.sideHit);
			boolean hasTinder = false;
			boolean isBlockTinder = false;
			IBlockState blockState = worldIn.getBlockState(result.getBlockPos());
			if(((ItemOctineIngot)itemStackIn.getItem()).isTinder(itemStackIn, ItemStack.EMPTY, blockState)) {
				hasTinder = true;
				isBlockTinder = true;
			} else {
				List<EntityItem> tinder = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(offsetPos), entity -> !entity.getItem().isEmpty() && ((ItemOctineIngot)itemStackIn.getItem()).isTinder(itemStackIn, entity.getItem(), null));
				if(!tinder.isEmpty()) {
					hasTinder = true;
				}
			}
			if((hasTinder || isBlockTinder) && blockState.getBlock() != Blocks.FIRE) {
				playerIn.setActiveHand(hand);
				return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
			}
		}
		return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase playerIn, int count) {
		if(playerIn instanceof EntityPlayer) {
			World worldIn = playerIn.world;
			RayTraceResult result = this.rayTrace(worldIn, (EntityPlayer) playerIn, true);
			if(result != null && result.typeOfHit == Type.BLOCK) {
				BlockPos pos = result.getBlockPos();
				BlockPos offsetPos = pos.offset(result.sideHit);
				boolean hasTinder = false;
				boolean isBlockTinder = false;
				IBlockState blockState = worldIn.getBlockState(pos);
				if(((ItemOctineIngot)stack.getItem()).isTinder(stack, ItemStack.EMPTY, blockState)) {
					hasTinder = true;
					isBlockTinder = true;
				} else {
					List<EntityItem> tinder = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(offsetPos), entity -> !entity.getItem().isEmpty() && ((ItemOctineIngot)stack.getItem()).isTinder(stack, entity.getItem(), null));
					if(!tinder.isEmpty()) {
						hasTinder = true;
					}
				}
				if(hasTinder) {
					if(worldIn.rand.nextInt(count / 10 + 1) == 0) {
						worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, 
								result.hitVec.x + worldIn.rand.nextFloat()*0.2-0.1, 
								result.hitVec.y + worldIn.rand.nextFloat()*0.2-0.1, 
								result.hitVec.z + worldIn.rand.nextFloat()*0.2-0.1, 0, 0.1, 0);
						worldIn.spawnParticle(EnumParticleTypes.FLAME, 
								result.hitVec.x + worldIn.rand.nextFloat()*0.2-0.1, 
								result.hitVec.y + worldIn.rand.nextFloat()*0.2-0.1, 
								result.hitVec.z + worldIn.rand.nextFloat()*0.2-0.1, 0, 0.1, 0);
					}
					if(!worldIn.isRemote) {
						if(count <= 1) {
							if(playerIn instanceof EntityPlayerMP) {
								AdvancementCriterionRegistry.OCTINE_INGOT_FIRE.trigger((EntityPlayerMP)playerIn);
								
								if(worldIn.getBlockState(isBlockTinder ? pos.down() : offsetPos.down()).getBlock() == BlockRegistry.PEAT) {
									AdvancementCriterionRegistry.PEAT_FIRE.trigger((EntityPlayerMP)playerIn);
								}
							}
							
							if(isBlockTinder) {
								worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState());
							} else {
								if(worldIn.getBlockState(offsetPos).getMaterial().isReplaceable()) {
									worldIn.setBlockState(offsetPos, Blocks.FIRE.getDefaultState());
								}
							}
							worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1, 1);
						}
					}
				}
			}
		}
	}

	public boolean isTinder(ItemStack ingot, ItemStack stack, @Nullable IBlockState blockState) {
		if(blockState != null) {
			Block block = blockState.getBlock();
			return block == BlockRegistry.CAVE_MOSS || 
					block == BlockRegistry.MOSS ||
					block == BlockRegistry.LICHEN ||
					block == BlockRegistry.DEAD_MOSS ||
					block == BlockRegistry.DEAD_LICHEN ||
					block == BlockRegistry.THORNS;
		}
		if(!stack.isEmpty()) {
			if(stack.getItem() instanceof ItemBlock) {
				ItemBlock itemBlock = (ItemBlock) stack.getItem();
				return isTinder(ingot, ItemStack.EMPTY, itemBlock.getBlock().getDefaultState());
			}
			return EnumItemPlantDrop.CAVE_MOSS_ITEM.isItemOf(stack) ||
					EnumItemPlantDrop.MOSS_ITEM.isItemOf(stack) ||
					EnumItemPlantDrop.LICHEN_ITEM.isItemOf(stack) ||
					EnumItemPlantDrop.THORNS_ITEM.isItemOf(stack) ||
					EnumItemCrushed.GROUND_CAVE_MOSS.isItemOf(stack) ||
					EnumItemCrushed.GROUND_MOSS.isItemOf(stack) ||
					EnumItemCrushed.GROUND_LICHEN.isItemOf(stack) ||
					EnumItemCrushed.GROUND_THORNS.isItemOf(stack);
		}
		return false;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}
}
