package thebetweenlands.common.item.tools;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
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
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;

import javax.annotation.Nullable;


public class ItemShockwaveSword extends ItemBLSword implements ICorrodible {
	public ItemShockwaveSword(ToolMaterial material) {
		super(material);
		this.addPropertyOverride(new ResourceLocation("charging"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
				return stack.getTagCompound() != null && stack.getTagCompound().getInteger("cooldown") < 60 ? 1 : 0;
			}
		});
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(I18n.format("tooltip.shockwaveSword.usage"));
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeldItem) {
		CorrosionHelper.updateCorrosion(stack, world, entity, slot, isHeldItem);
		
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
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
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
							int originX = MathHelper.floor(pos.getX() + 0.5D - Math.sin(direction) * distance - diag.x * distance2 * 0.25D);
							int originY = pos.getY() + yo;
							int originZ = MathHelper.floor(pos.getZ() + 0.5D + Math.cos(direction) * distance + diag.z * distance2 * 0.25D);
							BlockPos origin = new BlockPos(originX, originY, originZ);

							if(spawnedPos.contains(origin))
								continue;

							spawnedPos.add(origin);

							IBlockState block = world.getBlockState(new BlockPos(originX, originY, originZ));

							if (block.isNormalCube() && !block.getBlock().hasTileEntity(block)
									&& block.getBlockHardness(world, origin) <= 5.0F && block.getBlockHardness(world, origin) >= 0.0F
									&& !world.getBlockState(origin.up()).isOpaqueCube()) {
								stack.getTagCompound().setInteger("blockID", Block.getIdFromBlock(world.getBlockState(origin).getBlock()));
								stack.getTagCompound().setInteger("blockMeta", world.getBlockState(origin).getBlock().getMetaFromState(world.getBlockState(origin)));

								EntityShockwaveBlock shockwaveBlock = new EntityShockwaveBlock(world);
								shockwaveBlock.setOrigin(origin, MathHelper.floor(Math.sqrt(distance*distance+distance2*distance2)), pos.getX() + 0.5D, pos.getZ() + 0.5D, player);
								shockwaveBlock.setLocationAndAngles(originX + 0.5D, originY, originZ + 0.5D, 0.0F, 0.0F);
								shockwaveBlock.setBlock(Block.getBlockById(stack.getTagCompound().getInteger("blockID")), stack.getTagCompound().getInteger("blockMeta"));
								world.spawnEntity(shockwaveBlock);
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
}
