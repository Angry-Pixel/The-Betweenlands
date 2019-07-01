package thebetweenlands.common.capability.collision;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.common.TheBetweenlands;
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






	private boolean isPhasing;
	private boolean isViewObstructed;
	private double obstructionDistance;

	@Override
	public boolean isPhasing() {
		return this.isPhasing;
	}

	@Override
	public boolean isViewObstructed() {
		return this.isViewObstructed;
	}

	@Override
	public double getObstructionDistance() {
		return this.obstructionDistance;
	}

	@Override
	public double getObstructionCheckDistance() {
		return 0.25D;
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

		this.isPhasing = false;
		this.isViewObstructed = false;
		this.obstructionDistance = Double.MAX_VALUE;

		EntityPlayer player = this.getEntity();
		ItemStack stack = getRing(player);

		if(!stack.isEmpty()) {
			ItemRingOfNoClip item = (ItemRingOfNoClip) stack.getItem();

			AtomicBoolean ringActiveState = new AtomicBoolean(false);

			if(item.canPhase(player, stack)) {
				BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(player.world);
				final AxisAlignedBB locationCheckAabb = aabb.grow(8);
				List<LocationStorage> guardedLocations = worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, locationCheckAabb, (location) -> {
					return location.intersects(locationCheckAabb) && location.getGuard() != null;
				});

				//Remove all normally collected collision boxes because they
				//need to be filtered
				collisionBoxes.clear();

				final double floor = player.posY + 0.001D;

				final AxisAlignedBB originalAabb = aabb;

				final double checkReach = this.getObstructionCheckDistance();

				collisionBoxHelper.getCollisionBoxes(player, aabb.grow(checkReach, 0, checkReach).expand(0, checkReach, 0), EntityCollisionPredicate.ALL, new BlockCollisionPredicate() {
					@Override
					public boolean isColliding(Entity entity, AxisAlignedBB aabb, MutableBlockPos pos, IBlockState state, @Nullable AxisAlignedBB blockAabb) {
						if(blockAabb == null) {
							return true;
						}

						boolean isCollisionForced = false;

						if(blockAabb.maxY <= floor) {
							isCollisionForced = true;
						}

						if(!isCollisionForced && state.getPlayerRelativeBlockHardness(player, player.world, pos) < 0.0001F) {
							isCollisionForced = true;
						}

						if(!isCollisionForced) {
							for(LocationStorage location : guardedLocations) {
								if(location.getGuard().isGuarded(player.world, player, pos)) {
									isCollisionForced = true;
								}
							}
						}

						if(!isCollisionForced) {
							double dist;

							if(blockAabb.intersects(originalAabb)) {
								double dx = Math.max(blockAabb.minX - originalAabb.maxX, originalAabb.minX - blockAabb.maxX);
								double dy = Math.max(blockAabb.minY - originalAabb.maxY, originalAabb.minY - blockAabb.maxY);
								double dz = Math.max(blockAabb.minZ - originalAabb.maxZ, originalAabb.minZ - blockAabb.maxZ);
								dist = Math.max(dx, Math.max(dy, dz));
							} else {
								double dx = Math.max(0, Math.max(blockAabb.minX - originalAabb.maxX, originalAabb.minX - blockAabb.maxX));
								double dy = Math.max(0, Math.max(blockAabb.minY - originalAabb.maxY, originalAabb.minY - blockAabb.maxY));
								double dz = Math.max(0, Math.max(blockAabb.minZ - originalAabb.maxZ, originalAabb.minZ - blockAabb.maxZ));
								dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
							}

							if(dist < RingOfNoClipEntityCapability.this.obstructionDistance) {
								RingOfNoClipEntityCapability.this.obstructionDistance = dist;
							}
						}

						if(originalAabb.intersects(blockAabb)) {
							if(isCollisionForced) {
								return true;
							}

							ringActiveState.set(true);
							RingOfNoClipEntityCapability.this.isPhasing = true;

							if(player.world.isRemote) {
								RingOfNoClipEntityCapability.this.checkAndSetViewObstruction(player, item, stack, blockAabb);
							}

							return false;
						}

						return false;
					}
				}, collisionBoxes);
			}

			boolean newRingActiveState = ringActiveState.get();
			if(item.isActive(stack) != newRingActiveState) {
				item.setActive(stack, newRingActiveState);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void checkAndSetViewObstruction(EntityPlayer player, ItemRingOfNoClip item, ItemStack stack, AxisAlignedBB blockAabb) {
		if(blockAabb.contains(ActiveRenderInfo.projectViewFromEntity(player, 1))) {
			this.isViewObstructed = true;
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
	public static void onPlayerUpdate(PlayerTickEvent event) {
		EntityPlayer player = event.player;

		if(player != TheBetweenlands.proxy.getClientPlayer()) {
			IEntityCustomCollisionsCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS, null);

			if(cap != null) {
				//For other players than the client player the collision logic isn't run
				//so we need to force it to be run to do the obstruction distance calculations
				player.world.getCollisionBoxes(player, player.getEntityBoundingBox());
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
