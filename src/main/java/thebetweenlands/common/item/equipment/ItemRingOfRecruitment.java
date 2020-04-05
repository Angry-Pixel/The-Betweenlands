package thebetweenlands.common.item.equipment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IPuppetCapability;
import thebetweenlands.api.capability.IPuppeteerCapability;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.entity.mobs.EntityFortressBossBlockade;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemRingOfRecruitment extends ItemRing {
	public static final String NBT_UUID = "ring_of_recruitment.uuid";

	public ItemRingOfRecruitment() {
		this.setMaxDamage(100);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.ring.recruitment.bonus"), 0));
		if (GuiScreen.isShiftKeyDown()) {
			String toolTip = I18n.format("tooltip.bl.ring.recruitment", KeyBindRegistry.RADIAL_MENU.getDisplayName(), Minecraft.getMinecraft().gameSettings.keyBindUseItem.getDisplayName(), KeyBindRegistry.USE_RING.getDisplayName(), KeyBindRegistry.USE_SECONDARY_RING.getDisplayName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.format("tooltip.bl.press.shift"));
		}
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(!entity.world.isRemote && entity instanceof EntityPlayer) {
			IPuppeteerCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);

			if(cap != null) {
				int puppets = cap.getPuppets().size();
				NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);

				if(puppets == 0) {
					nbt.setBoolean("ringActive", false);
				} else {
					nbt.setBoolean("ringActive", true);
				}
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { 
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean("ringActive", false);

		//Reset recruitment points
		stack.setItemDamage(0);
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) {
		//Set new ring UUID so that previously recruited but unloaded entities will be unlinked when they're loaded again
		this.setRingUuid(stack, UUID.randomUUID());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean("ringActive");
	}
	
	@Override
	public void onKeybindState(EntityPlayer player, ItemStack stack, IInventory inventory, boolean active) {
		if(!player.world.isRemote && active && !player.getCooldownTracker().hasCooldown(ItemRegistry.RING_OF_RECRUITMENT)) {
			IPuppeteerCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
			
			if(cap != null && cap.getShield() != null) {
				List<Entity> targets = cap.getPuppets();
				
				Set<Entity> spawned = new HashSet<>();
				
				for(Entity target : targets) {
					IPuppetCapability targetCap = target.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
					if(targetCap != null && target.onGround && ((!targetCap.getStay() && !targetCap.getGuard()) || target.getDistance(player) < 6)) {
						List<EntityFortressBossBlockade> collidingEntities = target.world.getEntitiesWithinAABB(EntityFortressBossBlockade.class, target.getEntityBoundingBox().grow(0.5D));
						for(EntityFortressBossBlockade collidingEntity : collidingEntities) {
							if(!spawned.contains(collidingEntity)) {
								collidingEntity.setDead();
							}
						}
						
						EntityFortressBossBlockade blockade = new EntityFortressBossBlockade(target.world, player);
						blockade.setLocationAndAngles(target.posX, target.posY - 0.15f, target.posZ, target.world.rand.nextFloat() * 360.0f, 0);
						blockade.setMaxDespawnTicks(30 + target.world.rand.nextInt(20));
						blockade.setTriangleSize(0.75f + target.width * 0.5f);
						
						spawned.add(blockade);
						
						target.world.spawnEntity(blockade);
					}
				}
				
				if(!spawned.isEmpty()) {
					player.world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.FORTRESS_BOSS_SUMMON_PROJECTILES, SoundCategory.HOSTILE, 0.8f, 0.9f + player.world.rand.nextFloat() * 0.15f);
				
					player.getCooldownTracker().setCooldown(ItemRegistry.RING_OF_RECRUITMENT, 40);
				}
			}
		}
	}

	@Nullable
	public UUID getRingUuid(ItemStack stack) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasUniqueId(NBT_UUID)) {
			return stack.getTagCompound().getUniqueId(NBT_UUID);
		}
		return null;
	}

	public void setRingUuid(ItemStack stack, UUID uuid) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null) {
			nbt = new NBTTagCompound();
		}
		nbt.setUniqueId(NBT_UUID, uuid);
		stack.setTagCompound(nbt);
	}

	public int getRecruitmentCost(EntityLivingBase target) {
		float damageMultiplier = 0.5f;

		IAttributeInstance damageAttrib = target.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		if(damageAttrib != null) {
			damageMultiplier = 1.0f + Math.min((float)(damageAttrib.getAttributeValue() - 2.0f) / 10.0f, 0.5f);
		}

		return Math.min(60, Math.max(MathHelper.floor(target.getMaxHealth() / 2.0f * damageMultiplier), 10));
	}

	public static boolean isRingActive(Entity user, @Nullable IPuppetCapability recruited) {
		return !getActiveRing(user, recruited).isEmpty();
	}

	@Nullable
	public static ItemStack getActiveRing(Entity user, @Nullable IPuppetCapability recruited) {
		if(user instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) user;

			if (player.experienceTotal <= 0 && player.experienceLevel <= 0 && player.experience <= 0) {
				return ItemStack.EMPTY;
			}
		}

		ItemStack ring = EquipmentHelper.getEquipment(EnumEquipmentInventory.RING, user, ItemRegistry.RING_OF_RECRUITMENT);

		if(!ring.isEmpty()) {
			UUID ringUuid = ((ItemRingOfRecruitment) ring.getItem()).getRingUuid(ring);

			if(recruited != null && ringUuid != null) {
				UUID recruitedRingUuid = recruited.getRingUuid();

				if(recruitedRingUuid != null && !ringUuid.equals(recruitedRingUuid)) {
					return ItemStack.EMPTY;
				}
			}

			return ring;
		}

		return ItemStack.EMPTY;
	}
}
