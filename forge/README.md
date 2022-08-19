
Any parts of the fabric api that are used must be reimplemented in this package. 
The api classes must have exactly the same names, methods and package structure.
Then their functionality is reimplimented using forge events or mixins. 

The signature of methods and fields must match the fabric api exactly to avoid NoSuchFieldError/NoSuchMethodError. 
For example, it is not enough for `ServerPlayConnectionEvents#register` to accept a `TriConsumer<ServerPlayNetworkHandler, PacketSender, MinecraftServer>`, 
it must take a `Join` which is a functional interface that exposes a `onPlayReady` method corresponding to that consumer. 
Also, interfaces must be in the same package/class as they are in the fabric api. In that example, the `PackeSender` must be an inner class since it will not be found if it is its own file in the package. 
Make sure the return types of methods are the same! Even though the result is never used by the mod, `ServerPlayNetworking#registerGlobalReceiver` must return a boolean or the method will not be recognised. 



- all your code is moved into common/src/main/resources
- bettercombat.mixins.json is moved into fabric/src/main/resources
- Pretty sure the only changes to your code is adding a call to FabricLoader.getInstance().isModLoaded("forge") before registering sounds (SoundHelper#registerSounds) and item model properties (BetterCombatClient#onInitializeClient)

## Licensing

- The structure of the api is from Fabric API under the [Apache License 2.0](https://github.com/FabricMC/fabric/blob/1.19.2/LICENSE). 
- Luke's code reimplimenting that is under the [Creative Commons Zero License](https://creativecommons.org/share-your-work/public-domain/cc0/).

