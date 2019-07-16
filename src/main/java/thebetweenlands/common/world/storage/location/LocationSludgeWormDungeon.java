package thebetweenlands.common.world.storage.location;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.client.render.shader.postprocessing.GroundFog.GroundFogVolume;
import thebetweenlands.client.render.shader.postprocessing.WorldShader;
import thebetweenlands.common.network.datamanager.GenericDataManager;

public class LocationSludgeWormDungeon extends LocationGuarded {
	protected static final DataParameter<Float> GROUND_FOG_STRENGTH = GenericDataManager.createKey(LocationSludgeWormDungeon.class, DataSerializers.FLOAT);

	private BlockPos structurePos;

	public LocationSludgeWormDungeon(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		super(worldStorage, id, region, "sludge_worm_dungeon", EnumLocationType.DUNGEON);
		this.dataManager.register(GROUND_FOG_STRENGTH, 1.0F);
	}

	/**
	 * Sets the structure entrance
	 * @param pos
	 */
	public void setStructurePos(BlockPos pos) {
		this.structurePos = pos;
		this.setDirty(true);
	}

	/**
	 * Returns the structure entrance
	 * @return
	 */
	public BlockPos getStructurePos() {
		return this.structurePos;
	}
	
	public boolean hasGroundFog(BlockPos pos) {
		//TODO Check if pos is in maze bounding box
		return this.dataManager.get(GROUND_FOG_STRENGTH) > 0.01F;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setFloat("groundFogStrength", this.dataManager.get(GROUND_FOG_STRENGTH));
		nbt.setLong("structurePos", this.structurePos.toLong());
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.dataManager.set(GROUND_FOG_STRENGTH, nbt.getFloat("groundFogStrength"));
		this.structurePos = BlockPos.fromLong(nbt.getLong("structurePos"));
	}

	@Override
	protected void writeSharedNbt(NBTTagCompound nbt) {
		super.writeSharedNbt(nbt);
		nbt.setLong("structurePos", this.structurePos.toLong());
	}
	
	@Override
	protected void readSharedNbt(NBTTagCompound nbt) {
		super.readSharedNbt(nbt);
		this.structurePos = BlockPos.fromLong(nbt.getLong("structurePos"));
	}
	
	@Override
	public void update() {
		super.update();

		//TODO Clear fog strength when dungeon is conquered
	}

	@SideOnly(Side.CLIENT)
	public boolean addGroundFogVolumesToShader(WorldShader shader) {
		float globalStrength = this.dataManager.get(GROUND_FOG_STRENGTH);

		if(globalStrength > 0) {
			for(int floor = 0; floor < 7; floor++) {
				float floorStrength = globalStrength / 7.0f * (floor + 1);
				
				float fogBrightness = 0.25F;
				float inScattering = 0.035F - 0.015F * floorStrength;
				float extinction = 6.0F - 4.2F * floorStrength;
				
				float height = 4.0f + 8.0f * floorStrength;
				
				shader.addGroundFogVolume(new GroundFogVolume(new Vec3d(this.structurePos.getX(), this.structurePos.getY() - 5.2D - floor * 6, this.structurePos.getZ()), new Vec3d(29, height, 29), inScattering, extinction, fogBrightness, fogBrightness, fogBrightness));
			}

			return true;
		}

		return false;
	}
	
	public int getFloor(BlockPos pos) {
		return (this.structurePos.getY() - 1 - pos.getY()) / 6;
	}
}
