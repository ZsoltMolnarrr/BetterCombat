# Animation guide

## Prerequisites

Basic understanding of keyframe animations. Check out the following [guide to get started](https://www.youtube.com/watch?v=SZJswvw9wEs).

Install [Blender](https://www.blender.org/download/).

## Creating an animation

Copy the `animation_template.blend` file into your repository, and rename it to suit your animation. Open it.

In Blender, go to `Animation` tab, start creating keyframes by moving or rotating parts.

Things to avoid:
- Editing keyframe at `0`.
- Removing any body parts
- Moving/rotating locked parts (`bend` parts)


### Anatomy of attack animations.

Suggested animation length
- one-handed attacks: 20 ticks
- two-handed attacks: 20 ticks

An attack animation has the following phases:
- Upswing (suggested duration: 0 - 50%)
  - During this phase to player is animated from its idle pose to a _ready to strike_ pose
  - Create a single keyframe at 50% of the animation length, so the animation is interpolated between from the idle pose, to the _ready to strike_ pose.
  - Suggested easing: `Ease in-out`
- Hit - Tipping point (suggested duration: 50%-60%)
  - Create a single keyframe at 60% of the animation length, representing the tipping point of the attack. (For example, incase of a stab attack: the most forward position of the weapon)
  - Suggested easing: `Ease In`
- Hit - End (suggested duration: 60%-70%)
  - Suggested easing: `Ease out`
- Downwind (suggested duration: 70%-100%) 
  - Suggested easing: `Ease in-out`

Open a few animations implemented by this mod, to see practical examples.

### Notice

Attack animation speed is scaled when it gets to be played, so it is executed under the same amount of ticks as the attack cooldown of the weapon.

Attack animations are the most natural looking when the timing of `Tipping point`, and the [upswing](../src/main/java/net/bettercombat/api/WeaponAttributes.java#L103) property inside the `Attack` object are exactly matching.

## Exporting

In Blender, go to `Export` tab.

Make sure to adjust the following properties:

- `emoteDescription` - What is this animation
- `author` - Your name
- `outputDirectory` - Relative path to `player_animations` resources folder of your mod
- `endFrame` - The last frame of your animation
- `stopFrame` - `endFrame` + 50% of your animation length

Hit the run script button ▶️