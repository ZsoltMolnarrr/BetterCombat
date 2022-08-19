
Changes From Original Fabric Version
- all your code is moved into common/src/main/java/...
- bettercombat.mixins.json and fabric.mod.json are moved into fabric/src/main/resources/...
- pretty sure the only changes to your code is adding a call to FabricLoader.getInstance().isModLoaded("forge") before registering sounds (SoundHelper#registerSounds) and before registering item model properties (BetterCombatClient#onInitializeClient) as these must be handled differently on forge

Problems 
- the built jar won't work in production without `"refmap": "bettercombat-common-refmap.json"` in `bettercombat.mixins.json` but with that it won't run in the dev environment
- `ForgeLivingEntityRendererMixin` makes the start of animations look wrong

Uses [LukeGrahamLandry/ForgedFabric](https://github.com/LukeGrahamLandry/ForgedFabric) to provide the fabric api used by the common source set. 

