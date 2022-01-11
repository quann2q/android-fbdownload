# N2Q FB-Download

Add it in your root build.gradle (or settings.gradle) at the end of repositories

```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
            ...
        }
    }
```

Add the dependency (app/build.gradle)

```
    dependencies {
        implementation 'com.github.quann2q:android-fbdownload:1.0.0'
    }
```