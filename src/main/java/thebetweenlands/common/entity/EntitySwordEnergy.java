package thebetweenlands.common.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AnimationMathHelper;

public class EntitySwordEnergy extends Entity implements IEntityAdditionalSpawnData {
	private static final AxisAlignedBB RENDER_BOUNDING_BOX = new AxisAlignedBB(-9, -2, -9, 10, 3, 10);

	private static final DataParameter<Float> PART_POS_1 = EntityDataManager.<Float>createKey(EntitySwordEnergy.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> PART_POS_2 = EntityDataManager.<Float>createKey(EntitySwordEnergy.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> PART_POS_3 = EntityDataManager.<Float>createKey(EntitySwordEnergy.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> PART_POS_4 = EntityDataManager.<Float>createKey(EntitySwordEnergy.class, DataSerializers.FLOAT);

	public float pulseFloat;
	public float pos1, pos2, pos3, pos4, lastPos1, lastPos2, lastPos3, lastPos4;
	AnimationMathHelper pulse = new AnimationMathHelper();

	public EntitySwordEnergy(World world) {
		super(world);
		this.setSize(1F, 1F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return RENDER_BOUNDING_BOX.offset(this.posX, this.posY, this.posZ);
	}

	@Override
	protected void entityInit() {
		dataManager.register(PART_POS_1, Float.valueOf(3.5F));
		dataManager.register(PART_POS_2, Float.valueOf(3.5F));
		dataManager.register(PART_POS_3, Float.valueOf(3.5F));
		dataManager.register(PART_POS_4, Float.valueOf(3.5F));
	}

	@Override
	public void applyEntityCollision(Entity entity) {
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		pulseFloat = pulse.swing(0.3F, 0.75F, false);
		motionY = 0;
		if (!world.isRemote) {
			if(ticksExisted%140 == 0)
				world.playSound(null, posX, posY, posZ, SoundRegistry.FORTRESS_PUZZLE_ORB, SoundCategory.BLOCKS, 1.0F, 1.0F);

			if (getSwordPart1Pos() > 0 && getSwordPart1Pos() < 3.5F)
				setSwordPart1Pos(getSwordPart1Pos() - 0.05F);

			if (getSwordPart2Pos() > 0 && getSwordPart2Pos() < 3.5F)
				setSwordPart2Pos(getSwordPart2Pos() - 0.05F);

			if (getSwordPart3Pos() > 0 && getSwordPart3Pos() < 3.5F)
				setSwordPart3Pos(getSwordPart3Pos() - 0.05F);

			if (getSwordPart4Pos() > 0 && getSwordPart4Pos() < 3.5F)
				setSwordPart4Pos(getSwordPart4Pos() - 0.05F);

			if (getSwordPart1Pos() <= 0 && getSwordPart2Pos() <= 0 && getSwordPart3Pos() <= 0 && getSwordPart4Pos() <= 0) {
				world.playSound(null, posX, posY, posZ, SoundRegistry.FORTRESS_PUZZLE_SWORD, SoundCategory.BLOCKS, 1.0F, 1.0F);
				EntityItem entityItem = new EntityShockwaveSwordItem(world, posX, posY, posZ, new ItemStack(ItemRegistry.SHOCKWAVE_SWORD));
				entityItem.motionX = 0;
				entityItem.motionY = 0;
				entityItem.motionZ = 0;
				world.spawnEntity(entityItem);
				setDead();
			}
		} else {
			this.lastPos1 = this.pos1;
			this.lastPos2 = this.pos2;
			this.lastPos3 = this.pos3;
			this.lastPos4 = this.pos4;
			this.pos1 = this.getSwordPart1Pos();
			this.pos2 = this.getSwordPart2Pos();
			this.pos3 = this.getSwordPart3Pos();
			this.pos4 = this.getSwordPart4Pos();
		}
	}

	public float getSwordPart1Pos() {
		return dataManager.get(PART_POS_1);
	}

	public void setSwordPart1Pos(float pos) {
		dataManager.set(PART_POS_1, Float.valueOf(pos));
	}

	public float getSwordPart2Pos() {
		return dataManager.get(PART_POS_2);
	}

	public void setSwordPart2Pos(float pos) {
		dataManager.set(PART_POS_2, Float.valueOf(pos));
	}

	public float getSwordPart3Pos() {
		return dataManager.get(PART_POS_3);
	}

	public void setSwordPart3Pos(float pos) {
		dataManager.set(PART_POS_3, Float.valueOf(pos));
	}

	public float getSwordPart4Pos() {
		return dataManager.get(PART_POS_4);
	}

	public void setSwordPart4Pos(float pos) {
		dataManager.set(PART_POS_4, Float.valueOf(pos));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setFloat("partPos1", getSwordPart1Pos());
		nbt.setFloat("partPos2", getSwordPart2Pos());
		nbt.setFloat("partPos3", getSwordPart3Pos());
		nbt.setFloat("partPos4", getSwordPart4Pos());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		setSwordPart1Pos(nbt.getFloat("partPos1"));
		setSwordPart2Pos(nbt.getFloat("partPos2"));
		setSwordPart3Pos(nbt.getFloat("partPos3"));
		setSwordPart4Pos(nbt.getFloat("partPos4"));
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeFloat(dataManager.get(PART_POS_1));
		buffer.writeFloat(dataManager.get(PART_POS_2));
		buffer.writeFloat(dataManager.get(PART_POS_3));
		buffer.writeFloat(dataManager.get(PART_POS_4));
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		dataManager.set(PART_POS_1, additionalData.readFloat());
		dataManager.set(PART_POS_2, additionalData.readFloat());
		dataManager.set(PART_POS_3, additionalData.readFloat());
		dataManager.set(PART_POS_4, additionalData.readFloat());
	}

}

