package thebetweenlands.entities.mobs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.IManualEntryEntity;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.WorldProviderBetweenlands;

public class EntityFirefly extends EntityFlying implements IMob, IEntityBL, IManualEntryEntity {

	public int courseChangeCooldown;
	public double waypointX;
	public double waypointY;
	public double waypointZ;
	public int lastLightX;
	public int lastLightY;
	public int lastLightZ;

	public double aboveLayer = 6.0D;

	public EntityFirefly(World world) {
		super(world);
		this.ignoreFrustumCheck = true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
	}

	@Override
	protected void updateEntityActionState() {
		super.updateEntityActionState();

		if (this.worldObj.isRemote) {
			return;
		}
		
		double dx = this.waypointX - this.posX;
		double dy = this.waypointY - this.posY;
		double dz = this.waypointZ - this.posZ;
		double dist = dx * dx + dy * dy + dz * dz;

		if (dist < 1.0D || dist > 3600.0D) {
			this.waypointX = this.posX + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
			if(this.posY > WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer) {
				this.waypointY = this.posY + (-this.rand.nextFloat() * 2.0F) * 16.0F;
			} else {
				float rndFloat = this.rand.nextFloat() * 2.0F - 1.0F;
				if(rndFloat > 0.0D) {
					rndFloat -= 0.5D;
					double maxRange = WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer - this.posY;
					this.waypointY = this.posY + (-this.rand.nextFloat() * 2.0F) * maxRange;
				} else {
					this.waypointY = this.posY + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
				}
			}
			this.waypointZ = this.posZ + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
		}

		if (this.courseChangeCooldown-- <= 0) {
			this.courseChangeCooldown += this.rand.nextInt(5) + 2;
			dist = MathHelper.sqrt_double(dist);

			if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, dist)) {
				this.motionX += dx / dist * 0.03D;
				this.motionY += dy / dist * 0.03D;
				if(this.posY > WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer) {
					this.motionY -= ((1.0D - (WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer - this.posY) / this.aboveLayer) + 1.0D) / 100.0D;
				}
				this.motionZ += dz / dist * 0.03D;
				if(this.posY > WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer) {
					this.waypointX = this.posX;
					this.waypointY = this.posY;
					this.waypointZ = this.posZ;
				}
			} else {
				this.waypointX = this.posX;
				this.waypointY = this.posY;
				this.waypointZ = this.posZ;
			}
		}
	}

	private boolean isCourseTraversable(double x, double y, double z, double step) {
		double d4 = (this.waypointX - this.posX) / step;
		double d5 = (this.waypointY - this.posY) / step;
		double d6 = (this.waypointZ - this.posZ) / step;
		AxisAlignedBB axisalignedbb = this.boundingBox.copy();

		for (int i = 1; i < step; ++i)
		{
			axisalignedbb.offset(d4, d5, d6);

			if (!this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb).isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.rand.nextInt(8) == 0 && super.getCanSpawnHere();
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	private void lightUp(World world, int x, int y, int z) {
		world.setLightValue(EnumSkyBlock.Block, x, y, z, 15);
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				for (int k = -1; k < 2; k++) {
					if (x + i != this.lastLightX || y + j != this.lastLightY || z + k != this.lastLightZ || this.isDead) {
						world.updateLightByType(EnumSkyBlock.Block, this.lastLightX + i, this.lastLightY + j, this.lastLightZ + k);
						this.lastLightX = x;
						this.lastLightY = y;
						this.lastLightZ = z;
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void switchOff() {
		this.worldObj.updateLightByType(EnumSkyBlock.Block, this.lastLightX, this.lastLightY, this.lastLightZ);
		this.worldObj.updateLightByType(EnumSkyBlock.Block, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.worldObj.isRemote) {
			if(this.lastLightX != MathHelper.floor_double(this.posX) || 
					this.lastLightY != MathHelper.floor_double(this.posY) ||
					this.lastLightZ != MathHelper.floor_double(this.posZ)) {
				if(ConfigHandler.FIREFLY_LIGHTING && !ShaderHelper.INSTANCE.canUseShaders()) {
					this.switchOff();
					this.lightUp(this.worldObj, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
				}
			}
		} else {
			if(this.isInWater()) {
				this.motionY += 0.01D;
				this.waypointX = this.posX;
				this.waypointY = this.posY + 0.1D;
				this.waypointZ = this.posZ;
			}
		}
	}

	@Override
	public void setDead() {
		super.setDead();
		if (this.worldObj.isRemote) {
			if(ConfigHandler.FIREFLY_LIGHTING && !ShaderHelper.INSTANCE.canUseShaders()) {
				switchOff();
			}
		}
	}

	@Override
	public String manualPictureLocation() {
		return "thebetweenlands:textures/gui/manual/test.png";
	}

	@Override
	public int pictureWidth() {
		return 100;
	}

	@Override
	public int pictureHeight() {
		return 180;
	}

	@Override
	public String manualStats() {
		return "Attack:0/health:2/stat:num/stat:num/stat:num";
	}

	@Override
	public String manualName() {
		return "firefly";
	}

	@Override
	public Entity getEntity() {
		return this;
	}
}
