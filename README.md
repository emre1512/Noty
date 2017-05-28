# Noty

![https://github.com/emre1512/Noty](https://img.shields.io/badge/platform-Android-green.svg?style=flat-square)
![https://github.com/emre1512/Noty](https://img.shields.io/badge/API-16+-orange.svg?style=flat-square)
![https://www.apache.org/licenses/LICENSE-2.0](https://img.shields.io/badge/licence-Apache%20v2.0-blue.svg?style=flat-square)
![https://github.com/emre1512/Noty](https://img.shields.io/badge/version-v1.0-ff69b4.svg?style=flat-square)

A simple library for creating animated warnings/notifications for Android.

## Examples

![](https://media.giphy.com/media/3og0IEROGpv8Y8t1GU/giphy.gif) ![](https://media.giphy.com/media/xUA7aMFBW9TTLnZELm/giphy.gif) 
![](https://media.giphy.com/media/3og0ISeKMdFB8yFgd2/giphy.gif) ![](https://media.giphy.com/media/xUA7aP21RJInulbwHu/giphy.gif) 
![](https://media.giphy.com/media/xUA7bk4Qp1eVzGohB6/giphy.gif) ![](https://media.giphy.com/media/3og0Iyzt3OMbrZq920/giphy.gif)

## Installation

- 1st way: **Noty** is just a simple class. Just put it into your project wherever you want. 

## Usage

### Simplest

```java
Noty.init(YourActivity.this, "Your warning message", yourLayout, Noty.WarningStyle.SIMPLE).show();
```

### Simple with action

```java
Noty.init(YourActivity.this, "Your warning message", yourLayout, 
	Noty.WarningStyle.ACTION)
	.setActionText("OK").show();
```

### Some customization

```java
Noty.init(YourActivity.this, "Your warning message", yourLayout,
        Noty.WarningStyle.ACTION)
        .setActionText("OK")
        .setWarningBoxBgColor("#ff5c33")
        .setWarningTappedColor("#ff704d")
        .setWarningBoxPosition(Noty.WarningPos.BOTTOM)
        .setAnimation(Noty.RevealAnim.FADE_IN, Noty.DismissAnim.BACK_TO_BOTTOM, 400,400)
        .show();     	
```

### Add tap listener

```java
Noty.init(YourActivity.this, "Your warning message", yourLayout,
        Noty.WarningStyle.SIMPLE)
        .setTapListener(new Noty.TapListener() {
            @Override
            public void onTap(Noty warning) {
                // do something...
            }
        }).show();
```

### Add click listener

```java
Noty.init(YourActivity.this, "Your warning message", yourLayout,
        Noty.WarningStyle.ACTION)
        .setClickListener(new Noty.ClickListener() {
            @Override
            public void onClick(Noty warning) {
                // do something...
            }
        }).show();
```

### Add animation listener

```java
Noty.init(YourActivity.this, "Your warning message", yourLayout,
        Noty.WarningStyle.ACTION)
        .setAnimationListener(new Noty.AnimListener() {
            @Override
            public void onRevealStart(Noty warning) {
                // Start of reveal animation
            }

            @Override
            public void onRevealEnd(Noty warning) {
               // End of reveal animation
            }

	    @Override
            public void onDismissStart(Noty warning) {
               // Start of dismiss animation
            }

            @Override
            public void onDismissEnd(Noty warning) {
               // End of dismiss animation
            }
        }).show();
```

## Detailed Documentation

- Detailed documentation will be added soon.

## LICENSE

Copyright 2017 M. Emre Davarci

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
