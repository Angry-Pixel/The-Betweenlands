package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.util.config.ConfigHandler;

//TODO: Rewrite with new AI and movement (see EntityGhast)
public class EntityFirefly extends EntityFlying implements IMob, IEntityBL {
	public int courseChangeCooldown;
	public double waypointX;
	public double waypointY;
	public double waypointZ;
	public BlockPos lastLight = BlockPos.ORIGIN;

	public double aboveLayer = 6.0D;

	public EntityFirefly(World world) {
		super(world);
		this.setSize(0.6F, 0.6F);
		this.ignoreFrustumCheck = true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		if (this.worldObj.isRemote) {
			return;
		}
		double deltaX = this.waypointX - this.posX;
		double deltaY = this.waypointY - this.posY;
		double deltaZ = this.waypointZ - this.posZ;
		double dist = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
		if (dist < 1.0D || dist > 3600.0D) {
			this.waypointX = this.posX + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
			if (this.posY > WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer) {
				this.waypointY = this.posY + (-this.rand.nextFloat() * 2.0F) * 16.0F;
			} else {
				float rndFloat = this.rand.nextFloat() * 2.0F - 1.0F;
				if (rndFloat > 0.0D) {
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
			dist = Math.min(MathHelper.sqrt_double(dist), 23); //Limit steps
			if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, dist)) {
				this.motionX += deltaX / dist * 0.03D;
				this.motionY += deltaY / dist * 0.03D;
				if (this.posY > WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer) {
					this.motionY -= ((1.0D - (WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer - this.posY) / this.aboveLayer) + 1.0D) / 100.0D;
				}
				this.motionZ += deltaZ / dist * 0.03D;
				if (this.posY > WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer) {
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
		double deltaX = (this.waypointX - this.posX) / step;
		double deltaY = (this.waypointY - this.posY) / step;
		double deltaZ = (this.waypointZ - this.posZ) / step;
		for (int i = 1; i < step; ++i) {
			if (!this.worldObj.getCollisionBoxes(this, this.getEntityBoundingBox().offset(deltaX*i, deltaY*i, deltaZ*i)).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	private void lightUp(World world, BlockPos pos) {
		world.setLightFor(EnumSkyBlock.BLOCK, pos, 15);
		for (int offsetX = -1; offsetX < 2; offsetX++) {
			for (int offsetY = -1; offsetY < 2; offsetY++) {
				for (int offsetZ = -1; offsetZ < 2; offsetZ++) {
					BlockPos offset = pos.add(offsetX, offsetY, offsetZ);
					if (!offset.equals(this.lastLight) || this.isDead) {
						world.checkLightFor(EnumSkyBlock.BLOCK, this.lastLight.add(offsetX, offsetY, offsetZ));
						this.lastLight = pos;
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void switchOff() {
		this.worldObj.checkLightFor(EnumSkyBlock.BLOCK, this.lastLight);
		this.worldObj.checkLightFor(EnumSkyBlock.BLOCK, this.getPosition());
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.worldObj.isRemote) {
			if (this.lastLight != this.getPosition()) {
				if (ConfigHandler.fireflyLighting && !ShaderHelper.INSTANCE.isWorldShaderActive()) {
					this.switchOff();
					this.lightUp(this.worldObj, this.getPosition());
				}
			}
		} else {
			if (this.isInWater()) {
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
			if (ConfigHandler.fireflyLighting && !ShaderHelper.INSTANCE.isWorldShaderActive()) {
				switchOff();
			}
		}
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}
}
