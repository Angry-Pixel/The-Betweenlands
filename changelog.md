* Most item corrosion is now managed by tags and components
  * The `thebetweenlands:corrodible/default` tag will apply default corrosion (255 max corrosion, 600 max coating) to any item
  * Custom (java) corrosion handling can be done by tagging items with `thebetweenlands:corrodible/custom` and implementing `thebetweenlands.api.item.ICustomCorrodible` in the item class
  * Maximum corrosion and coating can be modified by the `thebetweenlands:max_corrosion` and `thebetweenlands:max_coating` item components (custom corrosion handlers don't have to abide by this, but it is recommended nonetheless)
* Rotting/Tainting/etc. whitelists and blacklists reworked
  * Data tags accepted (e.g. `#minecraft:food`)
  * Item components can now be used, either alone or as part of a greater item definition (note that general beats specific: `[minecraft:food={nutrition:10}]` will accept both `[minecraft:food={nutrition:10,saturation:0}]` and `[minecraft:food={nutrition:10,saturation:10}]`, for example)
  * Damage value now defaults to `*` instead of `0` when not specified
