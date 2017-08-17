# Spinney
[![Build Status](https://travis-ci.org/piruin/spinney.svg?branch=master)](https://travis-ci.org/piruin/spinney)
[![Download](https://api.bintray.com/packages/blazei/maven/Spinney/images/download.svg)](https://bintray.com/blazei/maven/Spinney/_latestVersion)

Android spinner with Super power!

## Getting Started
### Download

- **Step 1** - set JCenter repository (This step not require for modern android project)
- **Step 2** - Add dependencies on app module

```groovy
dependencies {
  compile 'me.piruin:spinney:VERSION'
}
```

## Use with Kotlin
With `kotlin`, Because of Spinney's generic type make `kotlin-android-extension` can't synthetic it properly, I recommend to initialize Spinney with `Lazy`. see example

```kotlin
class AwesomeActivity : AppCompatActivity {
  val awesomeSpinney by lazy { findViewById(R.id.awesomeSpinney) as Spinney<Awesome> }
  ...
}
```

## License
This project under [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0) license
