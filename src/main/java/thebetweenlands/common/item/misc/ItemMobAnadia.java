package thebetweenlands.common.item.misc;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IRotSmellCapability;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityFishingTackleBoxSeat;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.entity.mobs.EntityAnadia.EnumAnadiaColor;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ItemMobAnadia extends ItemMob implements ITintedItem {

	public <T extends Entity> ItemMobAnadia(int maxStackSize, @Nullable Class<T> defaultMob, @Nullable Consumer<T> defaultMobSetter) {
		super(1, defaultMob, defaultMobSetter);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	public int getDecayTime(ItemStack stack) {
		return 24000; // 20 minutes
	}
	
	@Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		if (!attacker.getEntityWorld().isRemote) {
			if (target instanceof EntitySwampHag && !target.isRiding() && attacker instanceof EntityPlayer) {
				if (attacker.getEntityWorld().getBlockState(target.getPosition().down()).getMaterial().blocksMovement()) {
					EntityFishingTackleBoxSeat entitySeat = new EntityFishingTackleBoxSeat(attacker.getEntityWorld(), true);
					entitySeat.setPosition(target.posX, target.posY - 0.55D, target.posZ);
					attacker.getEntityWorld().spawnEntity(entitySeat);
					target.startRiding(entitySeat, true);
				}
			}
		}
		return false;
	}
	
	@Override
	protected EnumActionResult spawnCapturedEntity(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, Entity entity, boolean isNewEntity) {
		if(entity instanceof EntityAnadia && !((EntityAnadia) entity).getFishColour().isAlive()) {
			return EnumActionResult.PASS;
		}
		return super.spawnCapturedEntity(player, world, pos, hand, facing, hitX, hitY, hitZ, entity, isNewEntity);
	}
	
	public void setRotten(World world, ItemStack stack, boolean rotten) {
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
			if(stack.getTagCompound().getCompoundTag("Entity").getByte("fishColour") != 0) {
				if(rotten) {
					stack.getTagCompound().getCompoundTag("Entity").setLong("rottingTime", world.getTotalWorldTime());
				} else {
					stack.getTagCompound().getCompoundTag("Entity").setLong("rottingTime", world.getTotalWorldTime() + this.getDecayTime(stack));
				}
			}
		}
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
						stack.getTagCompound().getCompoundTag("Entity").setByte("fishColour", (byte) 1);
		
		if(isRotten(world, stack) & entity instanceof EntityPlayer) {
			if(!world.isRemote)
				addSmell((EntityLivingBase) entity);
		}
    }

	public static void addSmell(EntityLivingBase entity) {
		IRotSmellCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_ROT_SMELL, null);
		if(cap != null) {
			if(!cap.isSmellingBad()) {
				cap.setSmellingBad(Math.max(cap.getRemainingSmellyTicks(), 24000));
			}
		}
	}

	@Override
	public void onCapturedByPlayer(EntityPlayer player, EnumHand hand, ItemStack stack, EntityLivingBase entity) {
		if(!player.getEntityWorld().isRemote) {
			if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND))
				stack.getTagCompound().getCompoundTag("Entity").setLong("rottingTime", player.getEntityWorld().getTotalWorldTime() + this.getDecayTime(stack));
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
			Entity entity = this.createCapturedEntity(worldIn, 0, 0, 0, stack, false);
			if(entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) entity;
				if (living instanceof EntityAnadia) {
					NBTTagCompound entityNbt = this.getEntityData(stack);
					
					if(entityNbt != null) {
						tooltip.add(I18n.format(living.getName()));
						
						if(entityNbt.getByte("fishColour") != 0) {
							if(entityNbt.hasKey("rottingTime")) {
								long rottingTime = entityNbt.getLong("rottingTime");
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
						} else {
							tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.status") + I18n.format("tooltip.bl.item_mob_anadia.smoked"));
						}
						
						tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.health", MathHelper.ceil(living.getHealth()), MathHelper.ceil((living.getMaxHealth()))));
						tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.size", ((EntityAnadia) living).getFishSize()));
						tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.speed", (living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue())));
						tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.strength", (Math.floor(((EntityAnadia) living).getStrengthMods() + 0.5D))));
						tooltip.add(I18n.format("tooltip.bl.item_mob_anadia.stamina", ((EntityAnadia) living).getStaminaMods()));
					}
				} else {
					tooltip.add(I18n.format("tooltip.bl.item_mob.health", MathHelper.ceil(living.getHealth() / 2), MathHelper.ceil(living.getMaxHealth() / 2)));
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorMultiplier(ItemStack stack, int tintIndex) {

		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {

			// smoked
			if(stack.getTagCompound().getCompoundTag("Entity").getByte("fishColour") == 0)
				return 0x747479;

			// rotten
			if (stack.getTagCompound().getCompoundTag("Entity").hasKey("rottingTime")) {
				long rottingTime = stack.getTagCompound().getCompoundTag("Entity").getLong("rottingTime");
				if (rottingTime - Minecraft.getMinecraft().world.getTotalWorldTime() <= 0)
					return 0x5FB050;
			}

			// silver
			if(stack.getTagCompound().getCompoundTag("Entity").getByte("fishColour") == 3)
				return 0xC2B3DB;
		}

		return 0x717A51; //default to base/brown
	}
}
