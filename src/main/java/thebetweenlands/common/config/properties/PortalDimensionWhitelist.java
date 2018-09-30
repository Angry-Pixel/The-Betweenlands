package thebetweenlands.common.config.properties;

import thebetweenlands.common.config.BetweenlandsConfig;

public class PortalDimensionWhitelist extends IntSetProperty {
	public PortalDimensionWhitelist() {
		super(() -> BetweenlandsConfig.WORLD_AND_DIMENSION.portalDimensionWhitelist);
	}

	@Override
	protected void init() {
		super.init();
		this.add(BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);
	}
}
