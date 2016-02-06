package thebetweenlands.tileentities.connection;

import thebetweenlands.connection.ConnectionLogic;
import thebetweenlands.connection.ConnectionType;
import thebetweenlands.connection.Light;
import thebetweenlands.tileentities.TileEntityConnectionFastener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.utils.Catenary;
import thebetweenlands.utils.vectormath.Point3f;

public abstract class Connection {
	protected TileEntityConnectionFastener fastener;

	protected World worldObj;

	private boolean isOrigin;

	private Catenary catenary;

	private Catenary prevCatenary;

	private Light[] lightPoints;

	protected boolean shouldRecalculateCatenary;

	private int toX;

	private int toY;

	private int toZ;

	private int fromX;

	private int fromY;

	private int fromZ;

	private boolean isDirty;

	private final ConnectionType type;

	private final ConnectionLogic logic;

	public Connection(ConnectionType type, TileEntityConnectionFastener fairyLightsFastener, World worldObj) {
		this(type, fairyLightsFastener, worldObj, false, null);
	}

	public Connection(ConnectionType type, TileEntityConnectionFastener fastener, World worldObj, boolean isOrigin, NBTTagCompound compound) {
		this.type = type;
		this.fastener = fastener;
		setWorldObj(worldObj);
		this.isOrigin = isOrigin;
		shouldRecalculateCatenary = true;
		logic = type.createLogic(this);
		if (compound != null) {
			readDetailsFromNBT(compound);
		}
	}

	public Catenary getCatenary() {
		return catenary;
	}

	public Catenary getPrevCatenary() {
		return prevCatenary;
	}

	public final ConnectionType getType() {
		return type;
	}

	public final ConnectionLogic getLogic() {
		return logic;
	}

	public abstract Point3f getTo();

	public abstract int getToX();

	public abstract int getToY();

	public abstract int getToZ();

	public void setWorldObj(World worldObj) {
		this.worldObj = worldObj;
	}

	public World getWorldObj() {
		return worldObj;
	}

	public boolean isOrigin() {
		return isOrigin;
	}

	public boolean shouldRecalculateCatenery() {
		return shouldRecalculateCatenary;
	}

	public TileEntityConnectionFastener getFastener() {
		return fastener;
	}

	public void onRemove() {
	}

	public abstract boolean shouldDisconnect();

	public void update(Point3f from) {
		prevCatenary = catenary;
		logic.onUpdate();
		if (shouldRecalculateCatenary) {
			Point3f to = getTo();
			if (to == null) {
				return;
			}
			to.sub(from);
			if (to.x == 0 && to.y == 0 && to.z == 0) {
				return;
			}
			this.fromX = fastener.xCoord;
			this.fromY = fastener.yCoord;
			this.fromZ = fastener.zCoord;
			this.toX = getToX();
			this.toY = getToY();
			this.toZ = getToZ();
			catenary = logic.createCatenary(to);
			shouldRecalculateCatenary = false;
			logic.onRecalculateCatenary();
		}
		logic.onUpdateEnd();
	}

	public void writeDetailsToNBT(NBTTagCompound compound) {
		logic.writeToNBT(compound);
	}

	public void readDetailsFromNBT(NBTTagCompound compound) {
		logic.readFromNBT(compound);
	}

	public void readFromNBT(NBTTagCompound compound) {
		isOrigin = compound.getBoolean("isOrigin");
		isDirty = compound.getBoolean("isDirty");
		readDetailsFromNBT(compound);
	}

	public void writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("isOrigin", isOrigin);
		compound.setBoolean("isDirty", isDirty);
		writeDetailsToNBT(compound);
	}
}
