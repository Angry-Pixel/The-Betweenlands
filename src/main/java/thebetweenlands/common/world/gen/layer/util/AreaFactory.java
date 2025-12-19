package thebetweenlands.common.world.gen.layer.util;

import thebetweenlands.api.world.biome.layer.Area;

@FunctionalInterface
public interface AreaFactory<A extends Area> {
    A make();
}
