package thebetweenlands.common.world.storage.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.registries.BlockRegistry;

public class LocationSpiritTree extends LocationGuarded {
	private List<BlockPos> wispPosts = new ArrayList<BlockPos>();
	private List<BlockPos> generatedWisps = new ArrayList<BlockPos>();

	public LocationSpiritTree(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region, "spirit_tree", EnumLocationType.SPIRIT_TREE);
	}

	public void addGeneratedWisp(BlockPos pos) {
		this.generatedWisps.add(pos);
		this.setDirty(true);
	}

	public List<BlockPos> getGeneratedWisps() {
		return Collections.unmodifiableList(this.generatedWisps);
	}
	
	public void addWispPost(BlockPos pos) {
		this.wispPosts.add(pos);
		this.setDirty(true);
	}

	public List<BlockPos> getWispPosts() {
		return Collections.unmodifiableList(this.wispPosts);
	}

	public int getActiveWisps() {
		int i = 0;
		for(BlockPos pos : this.wispPosts) {
			if(this.getWorldStorage().getWorld().getBlockState(pos).getBlock() == BlockRegistry.WISP) {
				i++;
			}
		}
		return i;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		this.saveBlockList(nbt, "generatedWisps", this.generatedWisps);
		this.saveBlockList(nbt, "wispPosts", this.wispPosts);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.readBlockList(nbt, "generatedWisps", this.generatedWisps);
		this.readBlockList(nbt, "wispPosts", this.wispPosts);
	}

	protected void saveBlockList(NBTTagCompound nbt, String name, List<BlockPos> blocks) {
		NBTTagList blockList = new NBTTagList();
		for(BlockPos pos : blocks) {
			blockList.appendTag(new NBTTagLong(pos.toLong()));
		}
		nbt.setTag(name, blockList);
	}

	protected void readBlockList(NBTTagCompound nbt, String name, List<BlockPos> blocks) {
		blocks.clear();
		NBTTagList blockList = nbt.getTagList(name, Constants.NBT.TAG_LONG);
		for(int i = 0; i < blockList.tagCount(); i++) {
			NBTTagLong posNbt = (NBTTagLong) blockList.get(i);
			blocks.add(BlockPos.fromLong(posNbt.getLong()));
		}
	}

	@Override
	public void update() {
		super.update();

		if(this.getWorldStorage().getWorld().isRemote) {
			this.updateAmbientParticles();
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateAmbientParticles() {
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();

		if(view != null && view.world.rand.nextInt(20) == 0 && this.isInside(view)) {
			World world = view.world;

			MutableBlockPos checkPos = new MutableBlockPos();

			int x = MathHelper.floor(view.posX);
			int y = MathHelper.floor(view.posY);
			int z = MathHelper.floor(view.posZ);

			for(int xo = -8; xo <= 8; xo++) {
				for(int zo = -8; zo <= 8; zo++) {
					if(xo*xo + zo+zo <= 64) {
						boolean hadAir = false;

						for(int yo = 2; yo >= -2; yo--) {
							checkPos.setPos(x + xo, y + yo, z + zo);

							IBlockState state = world.getBlockState(checkPos);

							if(!state.getBlock().isAir(state, world, checkPos)) {
								if(hadAir) {
									if(!state.isSideSolid(world, checkPos, EnumFacing.UP)) {
										break;
									}
									AxisAlignedBB aabb = world.getBlockState(checkPos).getBoundingBox(world, checkPos);
									if(aabb != null) {
										double px = checkPos.getX() + aabb.minX + world.rand.nextDouble() * (aabb.maxX - aabb.minX);
										double py = checkPos.getY() + aabb.maxY;
										double pz = checkPos.getZ() + aabb.minZ + world.rand.nextDouble() * (aabb.maxZ - aabb.minZ);
										world.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, px, py, pz, 0, 0, 0);
									}
								}
								break;
							} else {
								hadAir = true;
							}
						}
					}
				}
			}
		}
	}
}
