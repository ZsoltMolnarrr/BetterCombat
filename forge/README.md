Uses [LukeGrahamLandry/ForgedFabric](https://github.com/LukeGrahamLandry/ForgedFabric) to provide the fabric api used by the common source set.

Changes From Original Fabric Version
- all your code is moved into common/src/main/java/...
- bettercombat.mixins.json and fabric.mod.json are moved into fabric/src/main/resources/...
- pretty sure the only changes to your code is adding a call to FabricLoader.getInstance().isModLoaded("forge") before registering sounds (SoundHelper#registerSounds) and before registering item model properties (BetterCombatClient#onInitializeClient) as these must be handled differently on forge

Problems 
- Start Client -> Join World -> Exit World -> Join World = crash: screen says `IndeOutOfBoundException` (present in original forge version as well) but log only shows what seems like a problem handling the packet kicking you for crashing `Received class net.minecraft.network.protocol.game.ClientboundDisconnectPacket that couldn't be processed java.lang.ClassCastException: class net.minecraft.server.network.ServerGamePacketListenerImpl cannot be cast to class net.minecraft.network.protocol.game.ClientGamePacketListener `
- can't use `relocate` in `shadowJar` unless i use `shadowCommon` in `dependencies` but then it isn't loaded as a mod
