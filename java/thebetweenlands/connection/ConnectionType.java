package thebetweenlands.connection;

import thebetweenlands.client.model.connection.ModelConnection;
import thebetweenlands.client.model.connection.ModelConnectionLights;
import thebetweenlands.tileentities.connection.Connection;
import net.minecraft.util.MathHelper;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.lanterns.ItemConnection;

public enum ConnectionType {
	FAIRY_LIGHTS() {
		@Override
		public ConnectionLogic createLogic(Connection connection) {
			return new ConnectionLogicFairyLights(connection);
		}

		@Override
		public ItemConnection getItem() {
			return (ItemConnection) BLItemRegistry.fairyLights;
		}

		@Override
		public ModelConnection createRenderer() {
			return new ModelConnectionLights();
		}

		@Override
		public boolean isConnectionThis(Connection connection) {
			return connection.getLogic() instanceof ConnectionLogicFairyLights;
		}
	};

	public abstract ConnectionLogic createLogic(Connection connection);

	public abstract ItemConnection getItem();

	public abstract ModelConnection createRenderer();

	public abstract boolean isConnectionThis(Connection connection);

	public static ConnectionType from(int ordinal) {
		ConnectionType[] values = values();
		return values[MathHelper.clamp_int(ordinal, 0, values.length - 1)];
	}
}
