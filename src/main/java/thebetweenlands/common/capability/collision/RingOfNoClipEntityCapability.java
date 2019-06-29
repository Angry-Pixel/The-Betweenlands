package thebetweenlands.common.capability.collision;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.item.equipment.ItemRingOfNoClip;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class RingOfNoClipEntityCapability extends EntityCapability<RingOfNoClipEntityCapability, IEntityCustomCollisionsCapability, EntityPlayer> implements IEntityCustomCollisionsCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "ring_of_no_clip");
	}

	@Override
	protected Capability<IEntityCustomCollisionsCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS;
	}

	@Override
	protected Class<IEntityCustomCollisionsCapability> getCapabilityClass() {
		return IEntityCustomCollisionsCapability.class;
	}

	@Override
	protected RingOfNoClipEntityCapability getDefaultCapabilityImplementation() {
		return new RingOfNoClipEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}

	private static ItemStack getRing(EntityPlayer player) {
		IEquipmentCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if (cap != null) {
			IInventory inv = cap.getInventory(EnumEquipmentInventory.RING);

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);

				if(!stack.isEmpty() && stack.getItem() instanceof ItemRingOfNoClip) {
					return stack;
				}
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void getCustomCollisionBoxes(CollisionBoxHelper collisionBoxHelper, AxisAlignedBB aabb,
			List<AxisAlignedBB> collisionBoxes) {
		EntityPlayer player = this.getEntity();
		ItemStack stack = getRing(player);

		if(!stack.isEmpty()) {
			ItemRingOfNoClip item = (ItemRingOfNoClip) stack.getItem();

			if(item.canPhase(player, stack)) {
				BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(player.world);
				AxisAlignedBB locationAABB = aabb.grow(8);
				List<LocationStorage> guardedLocations = worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, locationAABB, (location) -> {
					return location.intersects(locationAABB) && location.getGuard() != null;
				});

				collisionBoxes.clear();
				final double floor = player.posY + 0.001D;
				collisionBoxHelper.getCollisionBoxes(player, aabb, EntityCollisionPredicate.ALL, new BlockCollisionPredicate() {
					@Override
					public boolean isColliding(Entity entity, AxisAlignedBB aabb, MutableBlockPos pos, IBlockState state, @Nullable AxisAlignedBB blockAabb) {
						if(blockAabb == null || blockAabb.maxY <= floor) {
							return true;
						}

						for(LocationStorage location : guardedLocations) {
							if(location.getGuard().isGuarded(player.world, player, pos)) {
								return true;
							}
						}

						item.setPhasing(stack);

						if(player.world.isRemote && blockAabb != null) {
							RingOfNoClipEntityCapability.this.checkAndSetViewObstruction(player, item, stack, blockAabb);
						}

						return false;
					}
				}, collisionBoxes);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void checkAndSetViewObstruction(EntityPlayer player, ItemRingOfNoClip item, ItemStack stack, AxisAlignedBB blockAabb) {
		if(blockAabb.contains(ActiveRenderInfo.projectViewFromEntity(player, 1))) {
			//TODO Set view obstruction and render overlay
		}
	}

	@SubscribeEvent
	public static void onSPPlayerPushOut(PlayerSPPushOutOfBlocksEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = getRing(player);

		if(!stack.isEmpty()) {
			ItemRingOfNoClip item = (ItemRingOfNoClip) stack.getItem();

			if(item.canPhase(player, stack)) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingAttacked(LivingAttackEvent event) {
		if(event.getEntity() instanceof EntityPlayer && event.getSource().getDamageType().equals(DamageSource.IN_WALL.getDamageType())) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			ItemStack stack = getRing(player);

			if(!stack.isEmpty()) {
				ItemRingOfNoClip item = (ItemRingOfNoClip) stack.getItem();

				if(item.canPhase(player, stack)) {
					event.setCanceled(true);
				}
			}
		}
	}
}
