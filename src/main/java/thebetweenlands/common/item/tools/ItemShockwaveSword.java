package thebetweenlands.common.item.tools;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.item.corrosion.CorrosionHelper;
import thebetweenlands.common.item.corrosion.ICorrodible;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;


public class ItemShockwaveSword extends ItemSword implements ICorrodible {
	public ItemShockwaveSword(ToolMaterial material) {
		super(material);
		this.addPropertyOverride(new ResourceLocation("charging"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
				return stack.getTagCompound() != null && stack.getTagCompound().getInteger("cooldown") < 60 ? 1 : 0;
			}
		});
		CorrosionHelper.addCorrosionPropertyOverrides(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advancedItemTooltips) {
		super.addInformation(stack, player, list, advancedItemTooltips);
		list.add(I18n.format("shockwaveSword.usage"));
		CorrosionHelper.addInformation(stack, player, list, advancedItemTooltips);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeldItem) {
		CorrosionHelper.onUpdate(stack, world, entity, slot, isHeldItem);
		
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		if (!stack.getTagCompound().hasKey("cooldown"))
			stack.getTagCompound().setInteger("cooldown", 0);
		if (!stack.getTagCompound().hasKey("uses"))
			stack.getTagCompound().setInteger("uses", 0);

		if(stack.getTagCompound().getInteger("uses") == 3) {
			if (stack.getTagCompound().getInteger("cooldown") < 60)
				stack.getTagCompound().setInteger("cooldown", stack.getTagCompound().getInteger("cooldown") + 1);
			if (stack.getTagCompound().getInteger("cooldown") >= 60) {
				stack.getTagCompound().setInteger("cooldown", 60);
				stack.getTagCompound().setInteger("uses", 0);
			}
		}
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {	
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			return EnumActionResult.PASS;
		}
		if (stack.getTagCompound().getInteger("uses") < 3) {
			if (!world.isRemote) {
				stack.damageItem(2, player);
				world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.SHOCKWAVE_SWORD, SoundCategory.BLOCKS, 1.0F, 2.0F);
				double direction = Math.toRadians(player.rotationYaw);
				Vec3d diag = new Vec3d(Math.sin(direction + Math.PI / 2.0D), 0, Math.cos(direction + Math.PI / 2.0D)).normalize();
				List<BlockPos> spawnedPos = new ArrayList<BlockPos>();
				for (int distance = -1; distance <= 16; distance++) {
					for(int distance2 = -distance; distance2 <= distance; distance2++) {
						for(int yo = -1; yo <= 1; yo++) {
							int originX = MathHelper.floor_double(pos.getX() + 0.5D - Math.sin(direction) * distance - diag.xCoord * distance2 * 0.25D);
							int originY = pos.getY() + yo;
							int originZ = MathHelper.floor_double(pos.getZ() + 0.5D + Math.cos(direction) * distance + diag.zCoord * distance2 * 0.25D);
							BlockPos origin = new BlockPos(originX, originY, originZ);

							if(spawnedPos.contains(origin))
								continue;

							spawnedPos.add(origin);

							IBlockState block = world.getBlockState(new BlockPos(originX, originY, originZ));

							if (block != null && block.isNormalCube() && !block.getBlock().hasTileEntity(block) 
									&& block.getBlockHardness(world, origin) <= 5.0F && block.getBlockHardness(world, origin) >= 0.0F
									&& !world.getBlockState(origin.up()).isOpaqueCube()) {
								stack.getTagCompound().setInteger("blockID", Block.getIdFromBlock(world.getBlockState(origin).getBlock()));
								stack.getTagCompound().setInteger("blockMeta", world.getBlockState(origin).getBlock().getMetaFromState(world.getBlockState(origin)));

								EntityShockwaveBlock shockwaveBlock = new EntityShockwaveBlock(world);
								shockwaveBlock.setOrigin(origin, MathHelper.floor_double(Math.sqrt(distance*distance+distance2*distance2)), pos.getX() + 0.5D, pos.getZ() + 0.5D, player);
								shockwaveBlock.setLocationAndAngles(originX + 0.5D, originY, originZ + 0.5D, 0.0F, 0.0F);
								shockwaveBlock.setBlock(Block.getBlockById(stack.getTagCompound().getInteger("blockID")), stack.getTagCompound().getInteger("blockMeta"));
								world.spawnEntityInWorld(shockwaveBlock);
								break;
							}
						}
					}
				}
				stack.getTagCompound().setInteger("uses", stack.getTagCompound().getInteger("uses") + 1);
				if (stack.getTagCompound().getInteger("uses") >= 3) {
					stack.getTagCompound().setInteger("uses", 3);
					stack.getTagCompound().setInteger("cooldown", 0);
				}
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	private static final ImmutableList<String> STACK_NBT_EXCLUSIONS = ImmutableList.of("cooldown");

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		boolean wasCharging = oldStack.getTagCompound() != null && oldStack.getTagCompound().getInteger("cooldown") < 60;
		boolean isCharging = newStack.getTagCompound() != null && newStack.getTagCompound().getInteger("cooldown") < 60;
		return (super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !isCharging || isCharging != wasCharging) || !NBTHelper.areItemStackTagsEqual(oldStack, newStack, STACK_NBT_EXCLUSIONS);
	}
	
	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return CorrosionHelper.shouldCauseBlockBreakReset(oldStack, newStack);
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state) {
		return CorrosionHelper.getStrVsBlock(super.getStrVsBlock(stack, state), stack, state); 
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return CorrosionHelper.getAttributeModifiers(slot, stack, ItemTool.ATTACK_DAMAGE_MODIFIER, this.getDamageVsEntity());
	}
}
