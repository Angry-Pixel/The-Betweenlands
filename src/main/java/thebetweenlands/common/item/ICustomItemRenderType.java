package thebetweenlands.common.item;

public interface ICustomItemRenderType {
    //something like item/handheld. item/generated is the default
    String getCustomRenderType(int meta);
}
