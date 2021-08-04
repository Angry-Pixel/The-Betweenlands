package thebetweenlands.common.tile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.block.misc.BlockOfferingTable;
import thebetweenlands.common.handler.PlayerRespawnHandler;
import thebetweenlands.common.item.equipment.ItemRing;
import thebetweenlands.common.item.misc.ItemBoneWayfinder;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.PlayerUtil;
import thebetweenlands.util.StatePropertyHelper;

public class TileEntityOfferingTable extends TileEntityGroundItem implements ITickable {
	private Map<EntityPlayer, Integer> teleportTicks = new WeakHashMap<>();

	@Override
	public void update() {
		ItemStack stack = this.getStack();
		if(!stack.isEmpty() && stack.getItem() == ItemRegistry.BONE_WAYFINDER) {
			double radius = 2.5D;

			AxisAlignedBB aabb = new AxisAlignedBB(this.getPos()).grow(radius);

			Set<EntityPlayer> teleportingPlayers = null;
			for(EntityPlayer player : this.world.getEntitiesWithinAABB(EntityPlayer.class, aabb, p -> p.isSneaking() && p.getDistanceSq(this.pos.getX() + 0.5f, this.pos.getY() + 0.5f, this.pos.getZ() + 0.5f) <= radius*radius)) {
				if(teleportingPlayers == null) {
					teleportingPlayers = new HashSet<>();
				}
				teleportingPlayers.add(player);

				int ticks = this.teleportTicks.getOrDefault(player, 0);

				if(ticks >= 0 && !this.updateTeleport(player, ticks, stack)) {
					this.teleportTicks.put(player, -100);
				} else {
					this.teleportTicks.put(player, ticks + 1);
				}
				
				this.setStack(stack);
			}

			if(teleportingPlayers == null) {
				this.teleportTicks.clear();
			} else if(!this.teleportTicks.isEmpty()) {
				Iterator<EntityPlayer> it = this.teleportTicks.keySet().iterator();
				while(it.hasNext()) {
					EntityPlayer player = it.next();
					if(!teleportingPlayers.contains(player)) {
						it.remove();
					}
				}
			}
		} else {
			this.teleportTicks.clear();
		}
	}

	private boolean updateTeleport(EntityPlayer entity, int ticks, ItemStack stack) {
		if(ticks >= 100) {
			if(!entity.world.isRemote && stack.getItemDamage() < stack.getMaxDamage()) {
				BlockPos waystone = ((ItemBoneWayfinder) stack.getItem()).getBoundWaystone(stack);
				if(waystone != null) {
					BlockPos spawnPoint = PlayerRespawnHandler.getSpawnPointNearPos(entity.world, waystone, 8, false, 4, 0);

					if(spawnPoint != null) {
						if(entity.getDistanceSq(spawnPoint) > 24) {
							this.playThunderSounds(entity.world, entity.posX, entity.posY, entity.posZ);
						}

						PlayerUtil.teleport(entity, spawnPoint.getX() + 0.5D, spawnPoint.getY(), spawnPoint.getZ() + 0.5D);

						this.playThunderSounds(entity.world, entity.posX, entity.posY, entity.posZ);

						entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60, 1));

						stack.damageItem(1, entity);
					} else if(entity instanceof EntityPlayerMP) {
						((EntityPlayerMP) entity).sendStatusMessage(new TextComponentTranslation("chat.waystone.obstructed"), true);
					}
				}
			}
		} else {
			if(stack.getItemDamage() < stack.getMaxDamage()) {
				if(!entity.world.isRemote) {
					if(ticks >= 40) {
						int count = ticks - 40;
						
						if(entity.hurtTime > 0) {
							entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1, 1);
						}

						if(entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative() && count < 60 && entity.ticksExisted % 3 == 0) {
							int removed = ItemRing.removeXp((EntityPlayer) entity, 1);
							if(removed == 0) {
								entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1, 1);
								return false;
							}
						}
					}
					
					if(ticks >= 15 && ticks < 90 && (ticks - 15) % 20 == 0) {
						entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundRegistry.PORTAL_TRAVEL, SoundCategory.PLAYERS, 0.05F + 0.4F * (float)MathHelper.clamp(80 - ticks, 1, 80) / 80.0F, 0.9F + entity.world.rand.nextFloat() * 0.2F);
					}
				} else {
					if(ticks >= 15 && ticks % 4 == 0) {
						this.spawnChargingParticles(this.world.getTotalWorldTime() * 0.035f, this.pos.getX() + 0.5f, this.pos.getY() + 0.4f, this.pos.getZ() + 0.5f);
					}
					
					Random rand = entity.world.rand;
					for(int i = 0; i < MathHelper.clamp(60 - ticks, 1, 60); i++) {
						entity.world.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, entity.posX + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 6, entity.posY + rand.nextFloat() * 4 - 2, entity.posZ + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 6, 0, 0.2D, 0);
					}
				}
			} else if(!entity.world.isRemote) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1, 1);
				return false;
			}
		}
		
		return true;
	}

	private void playThunderSounds(World world, double x, double y, double z) {
		world.playSound(null, x, y, z, SoundRegistry.RIFT_CREAK, SoundCategory.PLAYERS, 2, 1);
		world.playSound(null, x, y, z, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 0.75F, 0.75F);
	}

	@SideOnly(Side.CLIENT)
	private void spawnChargingParticles(float rot, float x, float y, float z) {
		float step = (float)Math.PI * 2 / 20;
		for(int i = 0; i < 20; i++) {
			float dx = (float)Math.cos(rot + step * i);
			float dz = (float)Math.sin(rot + step * i);

			BLParticles.CORRUPTED.spawn(this.world, x, y, z,
					ParticleArgs.get()
					.withMotion(dx * 0.075f, 0.15f, dz * 0.075f)
					.withData(80, true, 0.1f, true));
		}
	}
	
	@Override
	public boolean hasRandomOffset() {
		return false;
	}

	@Override
	public float getYOffset() {
		return 0.5f;
	}

	@Override
	public boolean isItemUpsideDown() {
		return false;
	}

	@Override
	public float getYRotation(float randomRotation) {
		EnumFacing facing = StatePropertyHelper.getStatePropertySafely(this, BlockOfferingTable.class, BlockOfferingTable.FACING, EnumFacing.NORTH);
		return -facing.getHorizontalAngle();
	}

	@Override
	public float getTiltRotation() {
		return 0;
	}

	@Override
	public float getItemScale() {
		return 0.5f;
	}
}
