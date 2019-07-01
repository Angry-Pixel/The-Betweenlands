package thebetweenlands.common.capability.collision;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
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
	private double viewObstructionDistance;
	private double obstructionDistance;

	@Override
	public boolean isPhasing() {
		return this.isPhasing;
	}

	@Override
	public double getViewObstructionDistance() {
		return this.viewObstructionDistance;
	}

	@Override
	public double getViewObstructionCheckDistance() {
		return 0.25D;
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

	private double calculateAABBDistance(AxisAlignedBB aabb1, AxisAlignedBB aabb2) {
		double dist;

		if(aabb1.intersects(aabb2)) {
			double dx = Math.max(aabb1.minX - aabb2.maxX, aabb2.minX - aabb1.maxX);
			double dy = Math.max(aabb1.minY - aabb2.maxY, aabb2.minY - aabb1.maxY);
			double dz = Math.max(aabb1.minZ - aabb2.maxZ, aabb2.minZ - aabb1.maxZ);
			dist = Math.max(dx, Math.max(dy, dz));
		} else {
			double dx = Math.max(0, Math.max(aabb1.minX - aabb2.maxX, aabb2.minX - aabb1.maxX));
			double dy = Math.max(0, Math.max(aabb1.minY - aabb2.maxY, aabb2.minY - aabb1.maxY));
			double dz = Math.max(0, Math.max(aabb1.minZ - aabb2.maxZ, aabb2.minZ - aabb1.maxZ));
			dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
		}

		return dist;
	}

	@Override
	public void getCustomCollisionBoxes(CollisionBoxHelper collisionBoxHelper, AxisAlignedBB aabb,
			List<AxisAlignedBB> collisionBoxes) {

		this.isPhasing = false;
		this.viewObstructionDistance = Double.MAX_VALUE;
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
				final AxisAlignedBB viewAabb = new AxisAlignedBB(player.posX, player.posY + player.getEyeHeight(), player.posZ, player.posX, player.posY + player.getEyeHeight(), player.posZ).grow(0.25D);

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
							double playerDist = RingOfNoClipEntityCapability.this.calculateAABBDistance(blockAabb, originalAabb);

							if(playerDist < RingOfNoClipEntityCapability.this.obstructionDistance) {
								RingOfNoClipEntityCapability.this.obstructionDistance = playerDist;
							}

							double viewDist = RingOfNoClipEntityCapability.this.calculateAABBDistance(blockAabb, viewAabb);

							if(viewDist < RingOfNoClipEntityCapability.this.getViewObstructionCheckDistance() && viewDist < RingOfNoClipEntityCapability.this.viewObstructionDistance) {
								RingOfNoClipEntityCapability.this.viewObstructionDistance = viewDist;
							}
						}

						if(originalAabb.intersects(blockAabb)) {
							if(isCollisionForced) {
								return true;
							}

							ringActiveState.set(true);
							RingOfNoClipEntityCapability.this.isPhasing = true;

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

	@SubscribeEvent
	public static void onSPPlayerPushOut(PlayerSPPushOutOfBlocksEvent event) {
		EntityPlayer player = event.getEntityPlayer();

		IEntityCustomCollisionsCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS, null);

		if(cap != null && cap.isPhasing()) {
			ItemStack stack = getRing(player);

			if(!stack.isEmpty()) {
				ItemRingOfNoClip item = (ItemRingOfNoClip) stack.getItem();

				if(item.canPhase(player, stack)) {
					event.setCanceled(true);
				}
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

			IEntityCustomCollisionsCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS, null);

			if(cap != null && cap.isPhasing()) {
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
}
