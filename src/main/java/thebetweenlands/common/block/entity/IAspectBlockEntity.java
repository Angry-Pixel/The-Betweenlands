package thebetweenlands.common.block.entity;

import javax.annotation.Nullable;

import thebetweenlands.api.aspect.Aspect;

public interface IAspectBlockEntity {

    void setAspect(@Nullable Aspect aspect);
    
    @Nullable
    Aspect getAspect();


}
