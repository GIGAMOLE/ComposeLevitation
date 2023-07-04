[![](/media/header.png)](https://intive.com/)

![](https://jitpack.io/v/GIGAMOLE/ComposeLevitation.svg?style=flat-square) | [Setup Guide](#setup)
| [Report new issue](https://github.com/GIGAMOLE/ComposeLevitation/issues/new)

# ComposeLevitation

The `ComposeLevitation` is a powerful Android Compose library that empowers developers with the ability to easily create and customize levitation effects, adding an
interactive and mesmerizing touch to their UI elements.

![](/media/demo.gif)

Features:

- **Levitation Effect:** Simulates a pressed surface with a shadow, creating a sense of depth and interactivity.
- **Gesture Responsiveness:** Reacts to press and drag gestures for intuitive user interaction.
- **Customization:** Fully customizable parameters such as camera distance, press angle, animation, upscale/downscale, shadow translation, offset, and color.
- **Advanced Shadows:** Enhances the levitation shadow effect with [ComposeShadowsPlus](https://github.com/GIGAMOLE/ComposeShadowsPlus).
- **Sample App:** Explore and experiment with [sample app](#sample-app).
- **Holographic Effect:** Explore and experiment with [holographic effect](#holographic-effect).

## Sample App

| Shape Levitation | Shape Press | Shape Shadow |
|-|-|-|
| <img src="/media/shape-levitation.gif" width="248"/> | <img src="/media/shape-press.gif" width="248"/> | <img src="/media/shape-shadow.gif" width="248"/> |

| Image Levitation | Image Press | Image Shadow |
|-|-|-|
| <img src="/media/image-levitation.gif" width="248"/> | <img src="/media/image-press.gif" width="248"/> | <img src="/media/image-shadow.gif" width="248"/> |

Download or clone this repository to discover the sample app.

## Holographic Effect

First ever **Android Holographic Effect**:

https://github.com/GIGAMOLE/ComposeLevitation/assets/7150913/803bd0db-fc6b-4cb2-a741-7d13b26a30e9

Inspired by the [Halo Lab -](https://dribbble.com/halolab) [Holographic Guide in Figma](https://dribbble.com/shots/15004777-Holographic-Guide-in-Figma)
and [junhoyeo -](https://github.com/) [Holographic
Effect Generator](https://holo.junho.io/).

Download or clone this repository to discover
the [holographic effect sample](https://github.com/GIGAMOLE/ComposeLevitation/blob/main/app/src/main/kotlin/com/gigamole/composelevitation/sample/MainScreenHolographicContent.kt)
.

## Setup

Add to the root `build.gradle.kts`:

``` groovy
allprojects {
    repositories {
        ...
        maven("https://jitpack.io")
    }
}
```

Add to the package `build.gradle.kts`:

``` groovy
dependencies {
    implementation("com.github.GIGAMOLE:ComposeLevitation:{latest-version}")
}
```

Latest version: ![](https://jitpack.io/v/GIGAMOLE/ComposeLevitation.svg?style=flat-square).

Also, it's possible to download the latest artifact from the [releases page](https://github.com/GIGAMOLE/ComposeLevitation/releases).

## Guide

`ComposeLevitation` comes with two main components: [`LevitationContainer`](#LevitationContainer) and [`LevitationState`](#LevitationState).

For more technical and detailed documentation, read the library `KDoc`.

### LevitationContainer

The `LevitationContainer` applies levitation effect to the provided content based on the provided [`LevitationState`](#LevitationState).

You can enable or disable the levitation effect with `isEnabled` attribute.

### LevitationState

The `LevitationState` consists of two required components: [`LevitationConfig`](#LevitationConfig) and [`PressConfig`](#PressConfig). And one optional
component: [`ShadowConfig`](#ShadowConfig).

To create a `LevitationState`, use the utility function: `rememberLevitationState(...)` or make it on your own.

#### LevitationConfig

The `LevitationConfig` setups the core levitation effect:

|Param|Description|
|-|-|
|`orientation`|The levitation orientation: `All`, `Horizontal`, and `Vertical`.|
|`degree`|The levitation degree (angle).|
|`pivot`|The levitation origin pivot point.|
|`cameraDistance`|The levitation camera distance.|
|`isBounded`|Indicates whether levitation is bounded to its size or not.|
|`shape`|The shape of the levitation content and its shadow.|
|`isClipped`|Indicates whether gesture events are clipped (and the content) within their shape or not.|

#### PressConfig

The `PressConfig` setups the levitation gesture (press, drag, and release) effect:

|Param|Description|
|-|-|
|`type`|The levitation press type: `Ranged`(interpolated), `Full`, and `None`.|
|`downscale`|The levitation press downscale or upscale.|
|`pressAnimationSpec`|The levitation press animation specification.|
|`isAwaitPressAnimation`|Indicates whether to await full press animation on a release or not.|
|`isPivotedWhenReleased`|Indicates whether to pivot the press point when released or not.|

#### ShadowConfig

An optional `ShadowConfig` setups the levitation shadow effect:

|Param|Description|
|-|-|
|`degreeMultiplier`|The levitation shadow degree (angle) multiplier.|
|`downscaleMultiplier`|The levitation shadow downscale or upscale multiplier.|
|`cameraDistanceMultiplier`|The levitation shadow camera distance multiplier.|
|`translationOffset`|The levitation shadow translation offset.|
|`isPivotedWhenPressed`|Indicates whether the levitation shadow is pivoted when fully pressed at the pivot.|
|`radius`|The levitation shadow radius.|
|`type`|The levitation shadow type: `SoftLayer`, `RSBlur`, and `Elevation`.|

The `SoftLayer` shadow type is required to be used with `SoftLayerShadowContainer`.

> The `ComposeLevitation` library advises against using the `Elevation` shadow due to a rendering issue where the shadow appears cropped and only visible at the content
> border,
> creating an empty rectangle below the surface.

The levitation shadow effect is powered by the [ComposeShadowsPlus](https://github.com/GIGAMOLE/ComposeShadowsPlus). You can explore it for more.

## License

MIT License. See the [LICENSE](https://github.com/GIGAMOLE/ComposeLevitation/blob/master/LICENSE) file for more details.

## Credits

Special thanks to the [GoDaddy](https://github.com/godaddy) for the amazing [color picker library](https://github.com/godaddy/compose-color-picker).

Inspired by the uncompleted Android Google `elevation` feature and `Steam Trading Cards` hover behavior:

|Steam Example 1|Steam Example 2|
|-|-|
|<img src="/media/steam-1.gif" width="392"/>|<img src="/media/steam-2.gif" width="378"/>|

Created at [intive](https://intive.com).  
**We spark digital excitement.**

[![](/media/credits.png)](https://intive.com/)

## Author:

[Basil Miller](https://www.linkedin.com/in/gigamole/)  
[gigamole53@gmail.com](mailto:gigamole53@gmail.com)

[![](/media/footer.png)](https://intive.com/careers)
