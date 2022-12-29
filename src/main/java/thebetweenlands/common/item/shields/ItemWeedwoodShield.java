package thebetweenlands.common.item.shields;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.tools.ItemBLShield;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemWeedwoodShield extends ItemBLShield {
	public ItemWeedwoodShield() {
		super(BLMaterialRegistry.TOOL_WEEDWOOD);
		this.addPropertyOverride(new ResourceLocation("burning"), (stack, worldIn, entityIn) ->
				((ItemWeedwoodShield)stack.getItem()).getBurningTicks(stack) > 0 ? 1.0F : 0.0F);
	}

	public void setBurningTicks(ItemStack stack, int ticks) {
		stack.setTagInfo("burningTicks", new NBTTagInt(ticks));
	}

	public int getBurningTicks(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		return tag != null && tag.hasKey("burningTicks", Constants.NBT.TAG_INT) ? tag.getInteger("burningTicks") : 0;
	}

	@Override
	public void onAttackBlocked(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
		super.onAttackBlocked(stack, attacked, damage, source);
		if(!attacked.world.isRemote && source.getTrueSource() != null) {
			Entity attacker;
			if(source instanceof EntityDamageSourceIndirect) {
				attacker = ((EntityDamageSourceIndirect)source).getTrueSource();
			} else {
				attacker = source.getTrueSource();
			}
			if((attacker.isBurning() || attacker instanceof EntitySmallFireball) && attacked.world.rand.nextFloat() < 0.5F) {
				this.setBurningTicks(stack, 80);
			} else if(attacker instanceof EntityLivingBase && attacked.world.rand.nextFloat() < 0.25F) {
				ItemStack activeItem = ((EntityLivingBase)attacker).getActiveItemStack();
				if(!activeItem.isEmpty()) {
					Item item = activeItem.getItem();
					if(item == ItemRegistry.OCTINE_AXE || item == ItemRegistry.OCTINE_PICKAXE || item == ItemRegistry.OCTINE_SHIELD || 
							item == ItemRegistry.OCTINE_SHOVEL || item == ItemRegistry.OCTINE_SWORD)
						stack.setTagInfo("burningTicks", new NBTTagInt(120));
				}
			}
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if(!worldIn.isRemote && entityIn != null) {
			int burningTicks = this.getBurningTicks(stack);
			if(burningTicks > 0) {
				this.setBurningTicks(stack, burningTicks - 1);
				if(burningTicks % 5 == 0)
					worldIn.playSound((EntityPlayer)null, (double)((float)entityIn.posX), (double)((float)entityIn.posY + entityIn.getEyeHeight()), (double)((float)entityIn.posZ), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + worldIn.rand.nextFloat(), worldIn.rand.nextFloat() * 0.7F + 0.3F);
				if(burningTicks % 10 == 0 && worldIn.rand.nextFloat() < 0.3F)
					entityIn.world.setEntityState(entityIn, (byte)30);
				if(burningTicks % 3 == 0 && entityIn instanceof EntityLivingBase)
					stack.damageItem(1, (EntityLivingBase)entityIn);
				if(stack.getCount() <= 0 && entityIn instanceof EntityLivingBase) {
					if(entityIn instanceof EntityPlayer) {
						((EntityPlayer)entityIn).inventory.setInventorySlotContents(itemSlot, ItemStack.EMPTY);
						EntityLivingBase entityLiving = (EntityLivingBase) entityIn;
						if(entityLiving.getHeldItemOffhand() == stack)
							entityLiving.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		ItemStack stack = entityItem.getItem();
		if(!entityItem.world.isRemote) {
			int burningTicks = this.getBurningTicks(stack);
			if(burningTicks > 0) {
				this.setBurningTicks(stack, burningTicks - 1);
				if(burningTicks % 5 == 0)
					entityItem.world.playSound((EntityPlayer)null, (double)((float)entityItem.posX), (double)((float)entityItem.posY + entityItem.height / 2.0F), (double)((float)entityItem.posZ), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + entityItem.world.rand.nextFloat(), entityItem.world.rand.nextFloat() * 0.7F + 0.3F);
				if(burningTicks % 3 == 0) {
					if (stack.attemptDamageItem(1, entityItem.world.rand, null)) {
						this.renderBrokenItemStack(entityItem.world, entityItem.posX, entityItem.posY + entityItem.height / 2.0F, entityItem.posZ, stack);
						stack.shrink(1);
						if (stack.getCount() < 0) {
							stack.setCount(0);
						}
						entityItem.setDead();
					}
				}
			}
		}
		return false;
	}

	private static final ImmutableList<String> STACK_NBT_EXCLUSIONS = ImmutableList.of("burningTicks");

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		boolean wasBurning = ((ItemWeedwoodShield)oldStack.getItem()).getBurningTicks(oldStack) > 0;
		boolean isBurning = newStack.getItem() instanceof ItemWeedwoodShield ? ((ItemWeedwoodShield)newStack.getItem()).getBurningTicks(newStack) > 0 : false;
		return (super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !isBurning || isBurning != wasBurning) || !NBTHelper.areItemStackTagsEqual(oldStack, newStack, STACK_NBT_EXCLUSIONS);
	}

	protected void renderBrokenItemStack(World world, double x, double y, double z, ItemStack stack) {
		world.playSound((EntityPlayer)null, x, y, z, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.NEUTRAL, 0.8F, 0.8F + world.rand.nextFloat() * 0.4F);
		for (int i = 0; i < 5; ++i) {
			Vec3d motion = new Vec3d(((double)world.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			world.spawnParticle(EnumParticleTypes.ITEM_CRACK, x, y, z, motion.x, motion.y + 0.05D, motion.z, new int[] {Item.getIdFromItem(stack.getItem())});
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		boolean isBurning = ((ItemWeedwoodShield)itemStackIn.getItem()).getBurningTicks(itemStackIn) > 0;
		return isBurning ? new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn) : super.onItemRightClick(worldIn, playerIn, hand);
	}
}
