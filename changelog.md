* Most item corrosion is now managed by tags and components
  * The `thebetweenlands:corrodible/default` tag will apply default corrosion (255 max corrosion, 600 max coating) to any item
  * Custom (java) corrosion handling can be done by tagging items with `thebetweenlands:corrodible/custom` and implementing `thebetweenlands.api.item.ICustomCorrodible` in the item class
  * Maximum corrosion and coating can be modified by the `thebetweenlands:max_corrosion` and `thebetweenlands:max_coating` item components (custom corrosion handlers don't have to abide by this, but it is recommended nonetheless)
* Rotting/Tainting/etc. whitelists and blacklists reworked
  * Data tags accepted (e.g. `#minecraft:food`)
  * Item components can now be used, either alone or as part of a greater item definition (note that general beats specific: `[minecraft:food={nutrition:10}]` will accept both `[minecraft:food={nutrition:10,saturation:0}]` and `[minecraft:food={nutrition:10,saturation:10}]`, for example)
  * Damage value now defaults to `*` instead of `0` when not specified
* Implemented waterlogging mechanics for many betweenlands blocks. All blocks that can be waterlogged in the mod can be waterlogged with both vanilla and swamp water.
  * Most non-full betweenlands blocks can also now be waterlogged.
  * Certain machines cannot function when waterlogged, like censers or alembics.
  * Crab pots can now also be placed outside of water, but they wont be functional.
* All Betweenlands recipes are now unhardcoded and controlled via jsons like vanilla recipes are
* Censer recipes are now a custom registry
* Simulacrum effects are now a custom registry
* Elixir Effects are now a custom registry
* Environment events are now a custom registry
* Amphibious armor upgrades are now a custom registry
* Overhauled the aspect system. Aspect Types and Items are now defined via datapack registries, more info can be found here: _**TODO**_ gist link
* Elixir recipes are now defined via a custom datapack registry
* Frog variants are now defined via a custom datapack registry
* Fixed Herblore book table of contents not flipping to the proper pages
* Improved how items visually bob when in the steeping pot, infuser, and mortar
* Fixed various plants having weird tinting as items
* Circle gem overlays now use a dynamic system that will render on all armors.
  * All armors will use the same layer by default, but a resource pack can make certain armors render with a different overlay.
  * Add a texture to `assets/modid/models/armor/circle_gems` and name it `{material}_{gem_type}_{1/2}.png` to register a layer for that armor set
  * _**TODO**_ finish item system and explain that here too
* Items that give food sickness are now controlled via the `thebetweenlands:gives_food_sickness` tag
* Items that heal decay are now controlled via the `thebetweenlands:decay_foods` data map
* Items that can be inserted into compost bins are controlled via the `thebetweenlands:compost_bin_compostables` data map
* Items that can yield more output in sulfur furnaces when limestone flux is used is controlled via the `thebetweenlands:flux_multiplier` data map
* Worms can now be added to fishing rods byt right clicking them while holding a worm in your cursor, similar to how bundles work
* Amphibious armor now displays what upgrades are on it in the item's tooltip
* Lurker skin pouches and silk bundles now display a tooltip of what items are inside them
* Gave most complex blocks better fitting hitboxes
* Unhardcoded all sickle logic. Sickle plant drops are now controlled via the block's loot table
* Mob items now render 3D on normal item frames as well
* Changed the default color of swamp water so it doesnt look like piss now
* added EMI support
* Sleeping Emberlings now display a unique snoozing particle
* Light sources that stalkers cant break are now controlled via the `thebetweenlands:stalker_ignored_light_sources` block tag.
* All instakill weapons now define what mobs they kill with entity tags.
* Biomes that the dreadful peat mummy can be summoned in are now controlled via the `thebetweenlands:dreadul_peat_mummy_summonable` biome tag
* Some Betweenlands plants can now be put in flower pots. This includes all saplings and 12 other plants