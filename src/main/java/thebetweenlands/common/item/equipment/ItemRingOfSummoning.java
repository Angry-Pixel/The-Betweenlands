package thebetweenlands.common.item.equipment;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.capability.ISummoningCapability;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.entity.mobs.EntityMummyArm;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemRingOfSummoning extends ItemRing {
	public static final int MAX_USE_TIME = 100;
	public static final int USE_COOLDOWN = 120;
	public static final int MAX_ARMS = 32;

	public ItemRingOfSummoning() {
		this.setMaxDamage(256);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.ring.summoning.bonus"), 0));
		if (GuiScreen.isShiftKeyDown()) {
			String toolTip = I18n.format("tooltip.bl.ring.summoning", KeyBindRegistry.RADIAL_MENU.getDisplayName(), KeyBindRegistry.USE_RING.getDisplayName(), KeyBindRegistry.USE_SECONDARY_RING.getDisplayName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.format("tooltip.bl.press.shift"));
		}
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(!entity.world.isRemote && entity instanceof EntityPlayer) {
			ISummoningCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_SUMMON, null);
			if (cap != null) {
				NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);

				if (cap.getCooldownTicks() > 0) {
					cap.setCooldownTicks(cap.getCooldownTicks() - 1);
					nbt.setBoolean("ringActive", false);
				} else {
					if (cap.isActive()) {
						cap.setActiveTicks(cap.getActiveTicks() + 1);

						nbt.setBoolean("ringActive", true);

						if (cap.getActiveTicks() > MAX_USE_TIME) {
							cap.setActive(false);
							cap.setCooldownTicks(USE_COOLDOWN);
						} else {
							int arms = entity.world.getEntitiesWithinAABB(EntityMummyArm.class, entity.getEntityBoundingBox().grow(18), e -> e.getDistance(entity) <= 18.0D).size();

							if (arms < MAX_ARMS) {
								List<EntityLivingBase> targets = entity.world.getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox().grow(16), e -> e instanceof EntityLiving && e.getDistance(entity) <= 16.0D && e != entity && (e instanceof EntityMob || e instanceof IMob));

								BlockPos targetPos = null;

								if (!targets.isEmpty()) {
									EntityLivingBase target = targets.get(entity.world.rand.nextInt(targets.size()));
									boolean isAttacked = !entity.world.getEntitiesWithinAABB(EntityMummyArm.class, target.getEntityBoundingBox()).isEmpty();
									if (!isAttacked) {
										targetPos = target.getPosition();
									}
								}

								if (targetPos == null && entity.world.rand.nextInt(3) == 0) {
									targetPos = entity.getPosition().add(entity.world.rand.nextInt(16) - 8, entity.world.rand.nextInt(6) - 3, entity.world.rand.nextInt(16) - 8);
									boolean isAttacked = !entity.world.getEntitiesWithinAABB(EntityMummyArm.class, new AxisAlignedBB(targetPos)).isEmpty();
									if (isAttacked) {
										targetPos = null;
									}
								}

								if (targetPos != null && entity.world.getBlockState(targetPos.down()).isSideSolid(entity.world, targetPos.down(), EnumFacing.UP)) {
									EntityMummyArm arm = new EntityMummyArm(entity.world, (EntityPlayer) entity);
									arm.setLocationAndAngles(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D, 0, 0);

									if (arm.world.getCollisionBoxes(arm, arm.getEntityBoundingBox()).isEmpty()) {
										this.drainPower(stack, entity);
										entity.world.spawnEntity(arm);
									}
								}
							}
						}
					} else {
						nbt.setBoolean("ringActive", false);
					}
				}
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { 
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean("ringActive", false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean("ringActive");
	}

	public static boolean isRingActive(Entity entity) {
		IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if(cap != null) {
			IInventory inv = cap.getInventory(EnumEquipmentInventory.RING);
			boolean hasRing = false;

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if(!stack.isEmpty() && stack.getItem() == ItemRegistry.RING_OF_SUMMONING && ((ItemRing) stack.getItem()).canBeUsed(stack)) {
					hasRing = true;
					break;
				}
			}

			return hasRing;
		}
		return false;
	}

	@Override
	public void onKeybindState(EntityPlayer player, ItemStack stack, IInventory inventory, boolean active) {
		ISummoningCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_SUMMON, null);

		if (cap != null) {
			if(!active && cap.isActive()) {
				cap.setActive(false);
				cap.setCooldownTicks(ItemRingOfSummoning.USE_COOLDOWN);
			} else if(active && !cap.isActive() && cap.getCooldownTicks() <= 0 && ItemRingOfSummoning.isRingActive(player)) {
				cap.setActive(true);
				cap.setActiveTicks(0);
				player.world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.PEAT_MUMMY_CHARGE, SoundCategory.PLAYERS, 0.4F, (player.world.rand.nextFloat() * 0.4F + 0.8F) * 0.8F);
			}
		}
	}
}
