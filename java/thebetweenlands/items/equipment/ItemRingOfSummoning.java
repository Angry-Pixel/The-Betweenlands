package thebetweenlands.items.equipment;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.entities.mobs.EntityMummyArm;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesRingInput;
import thebetweenlands.event.item.ItemTooltipHandler;
import thebetweenlands.items.loot.ItemRing;
import thebetweenlands.manual.IManualEntryItem;

public class ItemRingOfSummoning extends ItemRing implements IManualEntryItem {
	public static final int MAX_USE_TIME = 100;
	public static final int USE_COOLDOWN = 120;

	public ItemRingOfSummoning() {
		super();
		this.setMaxDamage(256);
		this.setUnlocalizedName("thebetweenlands.ringOfSummoning");
		this.setTextureName("thebetweenlands:ringOfSummoning");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(StatCollector.translateToLocal("ring.summoning.bonus"));
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			String toolTip = StatCollector.translateToLocal("item.thebetweenlands.ringOfSummoning.tooltip");
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(StatCollector.translateToLocal("item.thebetweenlands.press.shift"));
		}
	}

	@Override
	public String manualName(int meta) {
		return "ringOfSummoning";
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
	protected float getXPConversionRate(ItemStack stack, EntityPlayer player) {
		//1 xp = 2 damage repaired
		return 2.0F;
	}

	@Override
	public void drainPower(ItemStack stack, Entity entity) {
		/*if(entity instanceof EntityPlayer) {
			EntityPropertiesRingInput prop = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesRingInput.class);
			if(prop != null) {
				if(prop.isInUse()) {
					super.drainPower(stack, entity);
				} else {
					return;
				}
			}
		}*/
		super.drainPower(stack, entity);
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity) {
		if(entity instanceof EntityPlayer) {
			EntityPropertiesRingInput prop = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesRingInput.class);
			if(prop != null) {
				if(stack.stackTagCompound == null)
					stack.stackTagCompound = new NBTTagCompound();
				NBTTagCompound nbt = stack.stackTagCompound;
				int useTime = nbt.getInteger("useTime");
				int useCooldown = nbt.getInteger("useCooldown");
				if(prop.isInUse() && useTime < MAX_USE_TIME && useCooldown <= 0) {
					nbt.setInteger("useTime", useTime + 1);
					if(!entity.worldObj.isRemote) {
						if(!nbt.getBoolean("wasUsing"))
							entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, "thebetweenlands:peatMummyCharge", 0.6F, (entity.worldObj.rand.nextFloat() * 0.4F + 0.8F) * 0.8F);
						List<EntityMummyArm> arms = entity.worldObj.getEntitiesWithinAABB(EntityMummyArm.class, entity.boundingBox.expand(18, 18, 18));
						int armCount = 0;
						for(EntityMummyArm arm : arms) {
							if(arm.getDistanceToEntity(entity) <= 18.0D)
								armCount++;
						}
						if(armCount < 32) {
							int sx = 0;
							int sy = 0;
							int sz = 0;
							boolean hasTarget = false;
							List<EntityLivingBase> targets = entity.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, entity.boundingBox.expand(16, 16, 16));
							Iterator<EntityLivingBase> targetIT = targets.iterator();
							while(targetIT.hasNext()) {
								EntityLivingBase living = targetIT.next();
								if(living instanceof EntityMummyArm || living == entity)
									targetIT.remove();
							}
							if(!targets.isEmpty()) {
								EntityLivingBase target = targets.get(entity.worldObj.rand.nextInt(targets.size()));
								List<EntityMummyArm> armsUnderTarget = entity.worldObj.getEntitiesWithinAABB(EntityMummyArm.class, target.boundingBox);
								if(armsUnderTarget.isEmpty()) {
									sx = MathHelper.floor_double(target.posX);
									sy = MathHelper.floor_double(target.posY + 0.5D);
									sz = MathHelper.floor_double(target.posZ);
									hasTarget = true;
								}
							}
							if(!hasTarget && entity.worldObj.rand.nextInt(3) == 0) {
								sx = MathHelper.floor_double(entity.posX) + entity.worldObj.rand.nextInt(16) - 8;
								sy = MathHelper.floor_double(entity.posY + 0.5D) + entity.worldObj.rand.nextInt(6) - 3;
								sz = MathHelper.floor_double(entity.posZ) + entity.worldObj.rand.nextInt(16) - 8;
								List<EntityMummyArm> armsUnderTarget = entity.worldObj.getEntitiesWithinAABB(EntityMummyArm.class, AxisAlignedBB.getBoundingBox(sx, sy, sz, sx+1, sy+1, sz+1));
								if(armsUnderTarget.isEmpty()) {
									hasTarget = true;
								}
							}
							if(hasTarget && entity.worldObj.getBlock(sx, sy - 1, sz).isSideSolid(entity.worldObj, sx, sy - 1, sz, ForgeDirection.UP)) {
								EntityMummyArm arm = new EntityMummyArm(entity.worldObj);
								arm.setLocationAndAngles(sx + 0.5D, sy, sz + 0.5D, 0, 0);
								if(arm.worldObj.getCollidingBoundingBoxes(arm, arm.boundingBox).isEmpty()) {
									this.drainPower(stack, entity);
									arm.setOwner(entity.getUniqueID().toString());
									entity.worldObj.spawnEntityInWorld(arm);
								}
							}
						}
					}
					nbt.setBoolean("wasUsing", true);
				} else {
					if(!prop.isInUse()) {
						if(nbt.getBoolean("wasUsing"))
							nbt.setInteger("useCooldown", USE_COOLDOWN);
						nbt.setBoolean("wasUsing", false);
						nbt.setInteger("useTime", 0);
					}
					if(useCooldown > 0)
						nbt.setInteger("useCooldown", useCooldown - 1);
				}
			}
		}
	}
}
