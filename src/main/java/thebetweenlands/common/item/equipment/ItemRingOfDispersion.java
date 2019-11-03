package thebetweenlands.common.item.equipment;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;
import thebetweenlands.util.PlayerUtil;

public class ItemRingOfDispersion extends ItemRing {
	public static final String NBT_ACTIVE = "ring_of_dispersion.active";
	public static final String NBT_TIMER = "ring_of_dispersion.timer";
	public static final String NBT_LAST_VALID_POS = "ring_of_dispersion.last_valid_pos";

	public ItemRingOfDispersion() {
		this.setMaxDamage(300);
	}

	public void setActive(ItemStack stack, boolean active) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean(NBT_ACTIVE, active);
	}

	public boolean isActive(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean(NBT_ACTIVE);
	}

	public void setTimer(ItemStack stack, int ticks) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setInteger(NBT_TIMER, ticks);
	}

	public int getTimer(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger(NBT_TIMER) : 0;
	}

	public void setLastValidPos(ItemStack stack, @Nullable BlockPos pos) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		if(pos != null) {
			nbt.setLong(NBT_LAST_VALID_POS, pos.toLong());
		} else {
			nbt.removeTag(NBT_LAST_VALID_POS);
		}
	}

	@Nullable
	public BlockPos getLastValidPos(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_LAST_VALID_POS, Constants.NBT.TAG_LONG) ? BlockPos.fromLong(stack.getTagCompound().getLong(NBT_LAST_VALID_POS)) : null;
	}

	public int getMaxPhasingDuration(ItemStack stack) {
		return 300;
	}

	public boolean canPhase(EntityPlayer player, ItemStack stack) {
		return stack.getItemDamage() < stack.getMaxDamage() && !player.isSpectator() && player.isSneaking() && !player.getCooldownTracker().hasCooldown(this);
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(!entity.world.isRemote) {
			if(this.isActive(stack) && entity.ticksExisted % 20 == 0) {
				this.drainPower(stack, entity);
			}

			BlockPos currentPos = new BlockPos(entity);
			BlockPos storedPos = this.getLastValidPos(stack);

			int storedTimer = this.getTimer(stack);

			boolean requiresTeleport = false;

			if(!this.isActive(stack)) {
				if(!currentPos.equals(storedPos)) {
					this.setLastValidPos(stack, currentPos);
				}

				if(storedTimer != 0) {
					if(entity instanceof EntityPlayer && !this.canPhase((EntityPlayer) entity, stack)) {
						requiresTeleport = true;
					}
					this.setTimer(stack, 0);
				}
			} else {
				int maxDuration = this.getMaxPhasingDuration(stack);

				if(storedTimer >= maxDuration) {
					requiresTeleport = true;

					//Add cooldown to prevent players from immediately phasing again if they
					//manage to cancel teleport somehow
					if(entity instanceof EntityPlayer) {
						((EntityPlayer) entity).getCooldownTracker().setCooldown(this, 300);
					}
				} else {
					this.setTimer(stack, storedTimer + 1);
				}
			}

			if(requiresTeleport && storedPos != null) {
				PlayerUtil.teleport(entity, storedPos.getX() + 0.5D, storedPos.getY(), storedPos.getZ() + 0.5D);

				this.setLastValidPos(stack, null);
				this.setTimer(stack, 0);

				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundRegistry.RING_OF_DISPERSION_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean(NBT_ACTIVE, false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return this.isActive(stack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.ring.dispersion.bonus"), 0));
		if (GuiScreen.isShiftKeyDown()) {
			String toolTip = I18n.format("tooltip.ring.dispersion", KeyBindRegistry.RADIAL_MENU.getDisplayName(), Minecraft.getMinecraft().gameSettings.keyBindSneak.getDisplayName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.format("tooltip.press.shift"));
		}
	}

	@Override
	protected float getXPConversionRate(ItemStack stack, EntityPlayer player) {
		//1 xp = 2 damage repaired
		return 2.0F;
	}
}
