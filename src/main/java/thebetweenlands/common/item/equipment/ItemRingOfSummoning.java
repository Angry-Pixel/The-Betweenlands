package thebetweenlands.common.item.equipment;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.IEquipmentCapability;
import thebetweenlands.common.capability.summoning.ISummoningCapability;
import thebetweenlands.common.entity.mobs.EntityMummyArm;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemRingOfSummoning extends ItemRing {
	public static final int MAX_USE_TIME = 100;
	public static final int USE_COOLDOWN = 120;
	public static final int MAX_ARMS = 32;

	public ItemRingOfSummoning() {
		this.setMaxDamage(256);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advancedTooltips) {
		list.add(I18n.format("ring.summoning.bonus"));
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			String toolTip = I18n.format("item.thebetweenlands.ringOfSummoning.tooltip");
			//list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
			list.add(toolTip);
		} else {
			list.add(I18n.format("item.thebetweenlands.press.shift"));
		}
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(!entity.worldObj.isRemote && entity instanceof EntityPlayer && entity.hasCapability(CapabilityRegistry.CAPABILITY_SUMMON, null)) {
			ISummoningCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_SUMMON, null);

			if(cap.getCooldownTicks() > 0) {
				cap.setCooldownTicks(cap.getCooldownTicks() - 1);
			} else {
				if(cap.isActive()) {
					cap.setActiveTicks(cap.getActiveTicks() + 1);

					if(cap.getActiveTicks() > MAX_USE_TIME) {
						cap.setActive(false);
						cap.setCooldownTicks(USE_COOLDOWN);
					} else {
						int arms = entity.worldObj.getEntitiesWithinAABB(EntityMummyArm.class, entity.getEntityBoundingBox().expand(18, 18, 18), e -> e.getDistanceToEntity(entity) <= 18.0D).size();

						if(arms < MAX_ARMS) {
							List<EntityLivingBase> targets = entity.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox().expand(16, 16, 16),
									e -> e instanceof EntityLiving && e.getDistanceToEntity(entity) <= 16.0D && e != entity);

							BlockPos targetPos = null;

							if(!targets.isEmpty()) {
								EntityLivingBase target = targets.get(entity.worldObj.rand.nextInt(targets.size()));
								boolean isAttacked = !entity.worldObj.getEntitiesWithinAABB(EntityMummyArm.class, target.getEntityBoundingBox()).isEmpty();
								if(!isAttacked) {
									targetPos = target.getPosition();
								}
							}

							if(targetPos == null && entity.worldObj.rand.nextInt(3) == 0) {
								targetPos = entity.getPosition().add(
										entity.worldObj.rand.nextInt(16) - 8, 
										entity.worldObj.rand.nextInt(6) - 3, 
										entity.worldObj.rand.nextInt(16) - 8);
								boolean isAttacked = !entity.worldObj.getEntitiesWithinAABB(EntityMummyArm.class, new AxisAlignedBB(targetPos)).isEmpty();
								if(isAttacked) {
									targetPos = null;
								}
							}

							if(targetPos != null && entity.worldObj.getBlockState(targetPos.down()).isSideSolid(entity.worldObj, targetPos.down(), EnumFacing.UP)) {
								EntityMummyArm arm = new EntityMummyArm(entity.worldObj);
								arm.setLocationAndAngles(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D, 0, 0);

								if(arm.worldObj.getCollisionBoxes(arm, arm.getEntityBoundingBox()).isEmpty()) {
									this.drainPower(stack, entity);
									arm.setOwner(entity);
									entity.worldObj.spawnEntityInWorld(arm);
								}
							}
						}
					}
				}
			}
		}
	}

	public static boolean isRingActive(Entity entity) {
		if(entity.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
			IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			IInventory inv = cap.getInventory(EnumEquipmentInventory.RING);

			boolean hasRing = false;

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if(stack != null && stack.getItem() == ItemRegistry.RING_OF_SUMMONING && ((ItemRing) stack.getItem()).isActive(stack)) {
					hasRing = true;
					break;
				}
			}

			return hasRing;
		}
		return false;
	}
}
