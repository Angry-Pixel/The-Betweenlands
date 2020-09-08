package thebetweenlands.common.item.misc;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.item.ITintedItem;

public class ItemMobAnadia extends ItemMob implements ITintedItem {

	// TODO Item property overrides for jsons to show types and colour status (brown, silver, smoked, rotten)

	public int decayTime = 24000; // 20 minutes

	@SuppressWarnings("unchecked")
	public <T extends Entity> ItemMobAnadia(int maxStackSize, @Nullable Class<T> defaultMob, @Nullable Consumer<T> defaultMobSetter) {
		super(1, defaultMob, defaultMobSetter);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if(!world.isRemote) {
			Entity entity = this.createCapturedEntity(world, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stack);
			if(entity != null) {
				if(entity instanceof EntityAnadia && (((EntityAnadia) entity).getFishColour() == 2 || ((EntityAnadia) entity).getFishColour() == 3))
					return EnumActionResult.PASS;
				
				if(facing.getXOffset() != 0) {
					entity.setPosition(entity.posX + facing.getXOffset() * entity.width * 0.5f, entity.posY, entity.posZ);
				}
				if(facing.getYOffset() < 0) {
					entity.setPosition(entity.posX, entity.posY - entity.height, entity.posZ);
				}
				if(facing.getZOffset() != 0) {
					entity.setPosition(entity.posX, entity.posY, entity.posZ + facing.getZOffset() * entity.width * 0.5f);
				}

				if(world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty()) {
					this.spawnCapturedEntity(player, world, entity);
					stack.shrink(1);
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}
	
	public boolean isRotten(World world, ItemStack stack) {
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND))
			if(stack.getTagCompound().getCompoundTag("Entity").getByte("fishColour") != 0)
				if(stack.getTagCompound().getCompoundTag("Entity").hasKey("rottingTime")) {
					long timeFished = stack.getTagCompound().getCompoundTag("Entity").getLong("rottingTime");
					long worldTime = world.getTotalWorldTime();
					return timeFished - worldTime <= 0;
		}
		return false;
	}

	@Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND))
			if(stack.getTagCompound().getCompoundTag("Entity").getByte("fishColour") != 0 && stack.getTagCompound().getCompoundTag("Entity").getByte("fishColour") != 1)
				if(stack.getTagCompound().getCompoundTag("Entity").hasKey("rottingTime"))
					if(world.getTotalWorldTime() >= stack.getTagCompound().getCompoundTag("Entity").getLong("rottingTime"))
						stack.getTagCompound().getCompoundTag("Entity").setByte("fishColour", (byte) 3);
    }

	@Override
	public void onCapturedByPlayer(EntityPlayer player, EnumHand hand, ItemStack stack) {
		if(!player.getEntityWorld().isRemote) {
			if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND))
				stack.getTagCompound().getCompoundTag("Entity").setLong("rottingTime", player.getEntityWorld().getTotalWorldTime() + decayTime);
			Entity entity = this.createCapturedEntity(player.getEntityWorld(), 0, 0, 0, stack);
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) entity;
				if (living instanceof EntityAnadia) {
					if (((EntityAnadia) living).isBeingRidden())
						((EntityAnadia) living).removePassengers();
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(worldIn != null) {
			Entity entity = this.createCapturedEntity(worldIn, 0, 0, 0, stack);
			if(entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) entity;
				if (living instanceof EntityAnadia) {
					tooltip.add(I18n.format(living.getName()));
					if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
						if(stack.getTagCompound().getCompoundTag("Entity").getByte("fishColour") != 0) {
							if(stack.getTagCompound().getCompoundTag("Entity").hasKey("rottingTime")) {
								long rottingTime = stack.getTagCompound().getCompoundTag("Entity").getLong("rottingTime");
								//TODO localisation
								if(rottingTime - worldIn.getTotalWorldTime() > 19200)
									tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.status") + I18n.format("tooltip.bl.item_mob_anadia.rotting_1"));
								else if(rottingTime - worldIn.getTotalWorldTime() <= 19200 && rottingTime - worldIn.getTotalWorldTime() > 14400)
									tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.status") + I18n.format("tooltip.bl.item_mob_anadia.rotting_2"));
								else if(rottingTime - worldIn.getTotalWorldTime() <= 14400 && rottingTime - worldIn.getTotalWorldTime() > 9600)
									tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.status") + I18n.format("tooltip.bl.item_mob_anadia.rotting_3"));
								else if(rottingTime - worldIn.getTotalWorldTime() <= 9600 && rottingTime - worldIn.getTotalWorldTime() > 4800)
									tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.status") + I18n.format("tooltip.bl.item_mob_anadia.rotting_4"));
								else if(rottingTime - worldIn.getTotalWorldTime() <= 4800 && rottingTime - worldIn.getTotalWorldTime() > 0)
									tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.status") + I18n.format("tooltip.bl.item_mob_anadia.rotting_5"));
								else if(rottingTime - worldIn.getTotalWorldTime() <= 0)
									tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.status") + I18n.format("tooltip.bl.item_mob_anadia.rotten"));
							}
						}
						else
							tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.status") + I18n.format("tooltip.bl.item_mob_anadia.smoked"));
					}
					tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.health", MathHelper.ceil(living.getHealth()), MathHelper.ceil((living.getMaxHealth()))));
					tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.size", ((EntityAnadia) living).getFishSize()));
					tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.speed", (living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue())));
					tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.strength", (living.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue())));
					tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.stamina", ((EntityAnadia) living).getStaminaMods()));
				}
				else
					tooltip.add(I18n.format("tooltip.bl.item_mob.health", MathHelper.ceil(living.getHealth() / 2), MathHelper.ceil(living.getMaxHealth() / 2)));
			}
		}
	}

	@Override
	public int getColorMultiplier(ItemStack stack, int tintIndex) {
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
			if(stack.getTagCompound().getCompoundTag("Entity").getByte("fishColour") == 2)
				return 0xC2B3DB;
			if(stack.getTagCompound().getCompoundTag("Entity").getByte("fishColour") == 0)
				return 0x747479;
			if(stack.getTagCompound().getCompoundTag("Entity").getByte("fishColour") == 1)
				return 0x5FB050;
		}
		return 0x717A51;
	}
}
