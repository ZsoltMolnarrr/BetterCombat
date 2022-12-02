# 1.5.1
- Reduced upswing (aka windup) from 50% to 25% of attack cooldown (server configurable), attacks are faster to initiate
- Reduced attack cooldown after cancelling mining
- Improved several bundled animations
- Allow sweeping server config, when set to false disables Sweeping Edge enchantment 

# 1.5
- Added `️Attack instead of mining` feature (client side configurable). When an enemy is in reach, even if aiming at a near mineable block
- Reduced friendly fire from big weapon swings (So nearby players and villagers won't be hit accidentally, server side configurable)
- Added Swing through grass blacklist (Mod menu > Better Combat > Settings), partial or full item IDs can be entered as a regex to disable this feature for matching items
- Added client side config to hide the debug weapon hitbox renderer
- Fix clientside attack logic (Compatibility with Visuality mod) #156

# 1.4.5
- Add fallback compatibility for weapon types: `maul`, `rod`, `stave`
- Add Farmers Delight knives to default blacklist
- Improve obstacle detection
- Fix wrong arm shown when left arm set as main #132
- Fix rare special cases of weapon collision detection #131
- Fix sometimes not hitting target straight ahead
- Fix some missing events on Forge versions (fixes some issues with Botania)

# 1.4.4
- Fix off hand item breaking main hand item #126

# 1.4.3
- Remix all weapon sounds into mono, so they work with directional audio correctly
- Fix mod enabled on vanilla servers after playing single player #117
- Fix attacking while hand is busy (no longer able to attack while rowing boat, compatibility for PlayerRevive mod) #112
- Improve the positioning of attack range attribute line on item tooltip

# 1.4.2
- Fully disable the mod on when no server side installation
- Updated project licence

# 1.4.1
- Feint keybind not bound by default

# 1.4.0
Changes:
- Added raycasting into target finder logic, so attacks are no longer possible thru solid obstacles (server side configurable)
- Improved interaction with other mods in general by weapon swings adjusting itemUseCooldown and miningCooldown (client side)
- Improved localisations, thanks to @Chronos_Sacaria#0001 and @Fleyzi#7111
- Fixed malformed weapon attributes resolved from ItemStack NBT causing performance issues

API Changes - @Partner :
- Added new attack conditions `NO_OFFHAND_ITEM` `OFF_HAND_SHIELD`
- Added `EntityPlayer_BetterCombat` to `api` package, for accessing combo state while hitting
- Added `MinecraftClient_BetterCombat` to `api` package, for accessing client-side information about weapon swing related data
- Maven version numbers now include `fabric` / `forge` tag
- TinyConfig now correctly embedded, so third party dev environment no longer crashes, thanks to @KosmX#7620

# 1.3.1
- Movement speed while attacking on a mount is no longer reduced by default

# 1.3.0
- Improve walk pose transition
- Added movement speed reduction while attacking (server configurable), default setting: 50% speed

(Can be set between 0% - 100%, can be configured to be applied instantly or smoothly)

# 1.2.1
- Fixed launch crash when trying to use outdated dependencies
- Fixed backpedaling and strafing not considered for pose-walk transitions
- Added selected item slot for server network error logs

# 1.2.0
- Added reading WeaponAttributes from ItemStack NBT (paving the way to support modular items)
- Added client side setting to hide inactive hand while attacking in first person
- Added support for full body weapon poses
- Fixed corrupted fallback compatibility configuration file causing crash on launch #86
- Fixed invisible body when sleeping #67
- PlayerAnimator dependency no longer being embedded
- Removed smooth animation transition client config

# 1.1.1
- Added weapon swing sound volume slider
- Improved First-person Model compatibility #59
- Improved weapon collision detection (should work better against large targets)
- Lowered volume of rapier sound effects
- Fix compatibility with Utility Belt mod #78
- Improved stability

# 1.1.0
New things:

- Added protection for pets (pets can only be hit by their owners if faced directly, not from cleaving)
- Added smooth reversing animation for feinting
- Added `coral_blade` preset with unique uppercut animation
- Added `twin_blade` preset with unique blade swapping animations
- Added integration with Pehkui mod (player attack range now scales with player size)
- Improved smooth crossfade between attack animations (dual wielding slow weapons no longer oppress the animation of fast weapons)
- Improved client stability by adding some safeguards
- Restored vanilla Minecraft Sweeping Edge mechanic

Fixes:

- Fixed skin customization being overridden
- Fixed incompatibility with The Box Trot #52
- Fixed attack animations incorrectly played while sneaking #47
- Fixed attack range of some weapon presets
- Fixed (some) enchantments while dual wielding incorrectly fetched from the wrong hand
- Fixed `two_handed_slash_horizontal_right` attack animation not adjusted vertically

API Changes:

- Improved inheritance of `two_handed` field (now doesn't default to false when unspecified)
- Added `off_hand_pose` field that can be specified alongside `pose`, so one handed poses can now be applied selectively to any hand
- Added new attack condition `NOT_DUAL_WIELDING`

# 1.0.13
- Added claw preset with swipe animation, and fallback config
- Added machete keyword to default fallback config
- Added item model predicate for integrating mods
- Renamed staff preset to battlestaff (staff is still available but deprecated)
- Improve stability of server side attack request handling (added null check)

# 1.0.12
- Added
- new weapon attribute property called: category
- Added new attack conditions: DUAL_WIELDING_SAME_CATEGORY
- Added new presets: soul_knife, heavy_axe, cutlass
- Removed two handed pose from the following presets to be more true to MCD: anchor, double_axe

# 1.0.11
- Added harvest lock
  (No keybind needed, it just works. Mining with any tool should act much closer to Vanilla Minecraft, harvesting is not interrupted by targeting air)
- Added client side configuration to enable/disable the playback of weapon sounds
  (Maybe its too much diversion from Vanilla for some people)
- Added blacklist to fallback compatibility
  (So items can now be easily excluded)
- Fallback compatibility only applied to items with attack damage attribute
  (So nonsense items like armor, blocks etc... will not be automatically given weapon behaviour)

# 1.0.10
- Fixed shield blocking state being stuck
- Removed dedicated compatibility for vanilla axe items, using fallback compatibility, so players can easily disable them

# 1.0.9
- Support Minecraft versions 1.19.X
- Replace CompleteConfig dependency with ClothConfig
- Fix fallback compatibility applying axe presets to pickaxes

# 1.0.8
- Added smooth transition between attack animations
- Slightly improved first person attack animations

# 1.0.7
- Added fallback compatibility - weapons without attribute file, will be automatically assigned a matching weapon preset, based on guessing by item id. **Most mods become automatically compatible** (Server-side configurable)
- Added new presets: trident, rapier, anchor, sickle, wand (some of these come with fresh animations and sounds)

# 1.0.6
- Attack animations are vertically adjusted to the player's look orientation
- Add halberd preset
- Add german, spanish, chinse translations. Thanks to @Chronos_Sacaria !
- Update russian translations. Thanks to @Fleyzi !
- Fix player not rotating upon attacking in mutliplayer #11
- Fix claymore pose obscuring view in first person view

# 1.0.5
- Add keybind to toggle mine with weapons client-side config
- Replace all weapon sound effects

# 1.0.4
- Fine tune trident attributes
- Improve dual wield speed modifier coding
- Add Russian Translation, thanks to @Fleyzi
- Fix immersive portals compatibility #21
- Fix Ender Dragon parts can not be hit #26

# 1.0.3
- Add Two-Handed line to weapon tooltip
- Improve compatibility in general
- Fix compatibility with LevelZ #17
- Fix compatibility with Universal Enchants #8
- Fix sounds sometimes not available #16
- Fix shadow appearing under player in first person
- Fix two-handed weapons in off-hand slot #7
- Fix rendering sleeve in first person #14
- Fix body auto rotation upon attacking #11
- Fix first person render issues #5 #10

# 1.0.2
- Added attack range attribute to weapon tooltips (client side configurable)
- Fix right click item usage while attacking
- Fix compatibility with Custom Crosshair mod (however not recommended for use)
- Fix compatibility with FirstPersonModel mod
- Fix several first person rendering issues (cape, elytra, pumpkin, arrows etc...)