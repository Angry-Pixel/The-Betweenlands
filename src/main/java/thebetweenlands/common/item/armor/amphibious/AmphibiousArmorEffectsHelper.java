package thebetweenlands.common.item.armor.amphibious;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IBLBoss;
import thebetweenlands.common.entity.EntityFishVortex;
import thebetweenlands.common.entity.EntityShock;
import thebetweenlands.common.entity.EntityUrchinSpikeAOE;

public class AmphibiousArmorEffectsHelper {

	private static final String NBT_URCHIN_AOE_COOLDOWN = "thebetweenlands.urchin_aoe_cooldown";
	private static final String NBT_ELECTRIC_COOLDOWN = "thebetweenlands.electric_cooldown";

	protected AxisAlignedBB proximityBox(EntityPlayer player, double xSize, double ySize, double zSize) {
		return new AxisAlignedBB(player.getPosition()).grow(xSize, ySize, zSize);
	}

	public void activateFishVortex(World world, EntityPlayer player, int vortexCount) {
		List<EntityLivingBase> list = findNearbyEntities(world, player, proximityBox(player, 8D, 4D, 8D));
		for (int entityCount = 0; entityCount < Math.min(vortexCount, list.size()); entityCount++) {
			EntityLivingBase entity = pickRandomEntityFromList(list);
			if (entity != null && entity instanceof IBLBoss == false)
				if (!(entity instanceof EntityPlayer)) {
					spawnFishVortex(world, entity);
					list.remove(0);
				}
		}
	}

	public void activateUrchinSpikes(World world, EntityPlayer player, int urchinCount, NBTTagCompound nbt) {
		List<EntityLivingBase> list = findNearbyEntities(world, player, proximityBox(player, 2D, 2D, 2D));
		if (!list.isEmpty()) {
			EntityLivingBase entity = list.get(0);
			if (entity != null)
				if (!(entity instanceof EntityPlayer)) {
					spawnUrchinSpikes(world, player, urchinCount);
					nbt.setLong(NBT_URCHIN_AOE_COOLDOWN, world.getTotalWorldTime() + 50);
				}
		}
	}

	public void activateElectricEntity(World world, EntityPlayer player, int electricCount, NBTTagCompound nbt) {
		if (player.getRevengeTarget() != null) {
			if (!(player.getRevengeTarget() instanceof EntityPlayer)) {
				spawnElectricEntity(world, player, player.getRevengeTarget(), electricCount);
				nbt.setLong(NBT_ELECTRIC_COOLDOWN, world.getTotalWorldTime() + 50);
			}
		}
	}

	private void spawnFishVortex(World world, EntityLivingBase entity) {
		EntityFishVortex vortex = new EntityFishVortex(world);
		vortex.setPosition(entity.posX, entity.posY + 0.25D, entity.posZ);
		world.spawnEntity(vortex);
		entity.startRiding(vortex, true);
	}

	public void spawnUrchinSpikes(World world, EntityPlayer player, int damage) {
		EntityUrchinSpikeAOE urchinSpikes = new EntityUrchinSpikeAOE(world, player, damage);
		urchinSpikes.setPosition(player.posX, player.posY + player.height * 0.5D, player.posZ);
		world.spawnEntity(urchinSpikes);
		urchinSpikes.shootSpikes();
	}

	private void spawnElectricEntity(World world, EntityPlayer player, EntityLivingBase entity, int electricCount) {
		EntityShock electric = new EntityShock(world, player, entity, electricCount, entity.isWet() || entity.isInWater() || world.isRainingAt(entity.getPosition().up()));
		world.spawnEntity(electric);
	}

	private List findNearbyEntities(World world, EntityPlayer player, AxisAlignedBB box) {
		return world.getEntitiesWithinAABB(EntityLivingBase.class, box, e -> e instanceof IMob);
	}

	private EntityLivingBase pickRandomEntityFromList(List<EntityLivingBase> list) {
		Collections.shuffle(list);
		return list.get(0);
	}

}
