package thebetweenlands.common.item.tools;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		super.addInformation(stack, player, list, flag);
		list.add("Shift, right-click on the ground to create a shockwave");
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
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
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1000;
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {	
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			return EnumActionResult.FAIL;
		}
		if (facing == EnumFacing.UP) {
			if (!world.isRemote) {
				if (stack.getTagCompound().getInteger("uses") < 3) {
					stack.damageItem(2, player);
					world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.SHOCKWAVE_SWORD, SoundCategory.BLOCKS, 1.0F, 2.0F);
					double direction = Math.toRadians(player.rotationYaw);
					Vec3d diag = new Vec3d(Math.sin(direction + Math.PI / 2.0D), 0, Math.cos(direction + Math.PI / 2.0D)).normalize();
					for (int distance = 1; distance < 16; distance++) {
						for(int distance2 = -(distance-2); distance2 < (distance-2); distance2++) {
							int originX = MathHelper.floor_double(player.posX - Math.sin(direction) * distance - diag.xCoord * distance2 * 0.25D);
							int originY = pos.getY();
							int originZ = MathHelper.floor_double(player.posZ + Math.cos(direction) * distance + diag.zCoord * distance2 * 0.25D);

							IBlockState block = world.getBlockState(new BlockPos(originX, originY, originZ));

							if (block != null && block.isNormalCube() && !block.getBlock().hasTileEntity(block) 
									&& block.getBlockHardness(world, new BlockPos(originX, originY, originZ)) <= 5.0F && block.getBlockHardness(world, new BlockPos(originX, originY, originZ)) >= 0.0F
									&& (world.isAirBlock(new BlockPos(originX, originY+1, originZ)) || world.getBlockState(new BlockPos(originX, originY+1, originZ)).getBlock().isReplaceable(world, new BlockPos(originX, originY+1, originZ)))) {
								stack.getTagCompound().setInteger("blockID", Block.getIdFromBlock(world.getBlockState(new BlockPos(originX, originY, originZ)).getBlock()));
								stack.getTagCompound().setInteger("blockMeta", world.getBlockState(new BlockPos(originX, originY, originZ)).getBlock().getMetaFromState(world.getBlockState(new BlockPos(originX, originY, originZ))));

								EntityShockwaveBlock shockwaveBlock = new EntityShockwaveBlock(world);
								shockwaveBlock.setOrigin(originX, originY, originZ, MathHelper.floor_double(Math.sqrt(distance*distance+distance2*distance2)), player.posX, player.posZ, player);
								shockwaveBlock.setLocationAndAngles(originX, originY, originZ, 0.0F, 0.0F);
								shockwaveBlock.setBlock(Block.getBlockById(stack.getTagCompound().getInteger("blockID")), stack.getTagCompound().getInteger("blockMeta"));
								world.setBlockToAir(new BlockPos(originX, originY, originZ));
								world.notifyBlockUpdate(new BlockPos(originX, originY, originZ), block, block, 3);
								world.spawnEntityInWorld(shockwaveBlock);
							}
						}
					}
					stack.getTagCompound().setInteger("uses", stack.getTagCompound().getInteger("uses") + 1);
					if (stack.getTagCompound().getInteger("uses") >= 3) {
						stack.getTagCompound().setInteger("uses", 3);
						stack.getTagCompound().setInteger("cooldown", 0);
					}
					return EnumActionResult.PASS;
				}
			}
		}
		return EnumActionResult.FAIL;
	}

	private static final ImmutableList<String> STACK_NBT_EXCLUSIONS = ImmutableList.of("cooldown");

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		boolean wasCharging = oldStack.getTagCompound() != null && oldStack.getTagCompound().getInteger("cooldown") < 60;
		boolean isCharging = newStack.getTagCompound() != null && newStack.getTagCompound().getInteger("cooldown") < 60;
		return (super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !isCharging || isCharging != wasCharging) || !NBTHelper.areItemStackTagsEqual(oldStack, newStack, STACK_NBT_EXCLUSIONS);
	}
}
