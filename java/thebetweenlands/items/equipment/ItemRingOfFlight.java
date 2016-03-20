package thebetweenlands.items.equipment;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.items.loot.ItemRing;
import thebetweenlands.manual.IManualEntryItem;

public class ItemRingOfFlight extends ItemRing implements IManualEntryItem {
	public ItemRingOfFlight() {
		this.setMaxDamage(1800);
		this.setUnlocalizedName("thebetweenlands.ringOfFlight");
		this.setTextureName("thebetweenlands:ringOfFlight");
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(StatCollector.translateToLocal("ring.flight.bonus"));
	}

	@Override
	public String manualName(int meta) {
		return "ringOfFlight";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{6};
	}

	@Override
	public int metas() {
		return 0;
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity) {
		if(entity instanceof EntityPlayer) {
			boolean isFlying = ((EntityPlayer)entity).capabilities.isFlying;
			if(!isFlying) {
				if(!entity.onGround) {
					if(entity.isSneaking()) {
						entity.motionY = -0.2F;
					} else {
						double actualPosY = entity.posY + (entity == TheBetweenlands.proxy.getClientPlayer() ? -1.65D : 0.0D);
						int height = entity.worldObj.getHeightValue(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posZ));
						Vec3 dir = Vec3.createVectorHelper(entity.getLookVec().xCoord, 0, entity.getLookVec().zCoord).normalize();
						if(((EntityPlayer) entity).moveForward > 0) {
							entity.motionX = dir.xCoord * 0.25F;
							if(entity.getLookVec().yCoord > 0) {
								double mul = ((height - (actualPosY - 2.0D)));
								entity.motionY = entity.getLookVec().yCoord * 0.25F * mul;
							} else {
								entity.motionY = 0;
							}
							if(actualPosY - 2.0D > height && entity.motionY > 0) {
								entity.motionY = 0;
							}
							entity.motionZ = dir.zCoord * 0.25F;
						} else {
							entity.motionY = 0;
						}
						if(actualPosY - 2.0D > height + 1) {
							entity.motionY = -0.2F;
						}
					}

					entity.fallDistance = 0.0F;

					if(entity.worldObj.isRemote) {
						BLParticle.LEAF_SWIRL.spawn(entity.worldObj, entity.posX, entity.posY, entity.posZ, 0, 0, 0, 1, entity);
					}
				}
			}
		}
	}
}
