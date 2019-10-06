package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemBarkAmulet extends Item implements IEquippable {
	public ItemBarkAmulet() {
		this.setMaxDamage(14400);
		this.setMaxStackSize(1);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		IEquippable.addEquippedPropertyOverrides(this);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public EnumEquipmentInventory getEquipmentCategory(ItemStack stack) {
		return EnumEquipmentInventory.MISC;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, EntityPlayer player, Entity target) {
		return true;
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityPlayer player, Entity target) {
		return player == target && EquipmentHelper.getEquipment(EnumEquipmentInventory.MISC, target, this).isEmpty();
	}

	@Override
	public boolean canUnequip(ItemStack stack, EntityPlayer player, Entity target, IInventory inventory) {
		return true;
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, IInventory inventory) {
		return true;
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) { }

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { }

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(!entity.world.isRemote && entity instanceof EntityLivingBase && entity.ticksExisted % 20 == 0) {
			stack.damageItem(1, (EntityLivingBase) entity);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		if(!Minecraft.getMinecraft().isGamePaused()) {
			Entity view = Minecraft.getMinecraft().getRenderViewEntity();
			if(view != null) {
				if(!EquipmentHelper.getEquipment(EnumEquipmentInventory.MISC, view, ItemRegistry.BARK_AMULET).isEmpty() || (view instanceof EntityLivingBase && ((EntityLivingBase) view).isPotionActive(ElixirEffectRegistry.ENLIGHTENED))) {
					final float range = 12.0F;

					List<EntityLivingBase> entities = view.world.getEntitiesWithinAABB(EntityLivingBase.class, view.getEntityBoundingBox().grow(range), e -> e.getDistanceSq(view) <= range * range);

					for(EntityLivingBase entity : entities) {
						if(entity != view && entity.ticksExisted % 50 == 0) {
							BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.LIFE_ESSENCE.create(entity.world, 0, entity.height + 0.2D, 0, ParticleArgs.get().withScale(entity.width / 2.0F).withData(entity, entity.ticksExisted)));
						}
					}
				}
			}
		}
	}
}
