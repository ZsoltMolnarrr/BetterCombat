# 1.5
- Added `️Attack instead of mining` feature (client side configurable). When an enemy is in reach, even if aiming at a near mineable block
- Reduced friendly fire from big weapon swings (So nearby players and villagers won't be hit accidentally, server side configurable)
- Added Swing through grass blacklist (Mod menu > Better Combat > Settings), partial or full item IDs can be entered as a regex to disable this feature for matching items
- Added client side config to hide the debug weapon hitbox renderer
- Fix clientside attack logic (Compatibility with Visuality mod) #156

# 1.4
- Add fallback compatibility for weapon types: `maul`, `rod`, `stave`
- Add Farmers Delight knives to default blacklist
- Improve obstacle detection
- Fix wrong arm shown when left arm set as main #132
- Fix rare special cases of weapon collision detection #131
- Fix sometimes not hitting target straight ahead
- Fix some missing events on Forge versions (fixes some issues with Botania)