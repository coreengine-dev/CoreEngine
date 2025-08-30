.
├── build.gradle.kts
├── CHANGELOG.md
├── coreengine-android-host
│   ├── build.gradle.kts
│   ├── consumer-rules.pro
│   ├── proguard-rules.pro
│   └── src
│       ├── androidTest
│       └── main
├── coreengine-api
│   ├── api
│   │   └── coreengine-api.api
│   ├── build.gradle.kts
│   ├── consumer-rules.pro
│   ├── proguard-rules.pro
│   └── src
│       └── main
├── coreengine-integration
│   ├── build.gradle.kts
│   ├── LICENSE
│   ├── README.md
│   └── src
│       └── main
├── coreengine-render-canvas
│   ├── build.gradle.kts
│   ├── consumer-rules.pro
│   ├── proguard-rules.pro
│   └── src
│       └── main
├── coreengine-render-gl
│   ├── build.gradle.kts
│   ├── consumer-rules.pro
│   ├── proguard-rules.pro
│   └── src
│       └── main
├── coreengine-runtime
│   ├── build.gradle.kts
│   ├── consumer-rules.pro
│   ├── proguard-rules.pro
│   └── src
│       ├── main
│       └── test
├── docs
│   ├── api
│   ├── architecture
│   └── tutorials
├── gradle
│   ├── libs.versions.toml
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradle.properties
├── gradlew
├── gradlew.bat
├── ide
│   ├── plugin
│   │   ├── api
│   │   ├── build.gradle.kts
│   │   └── src
│   └── standalone
├── LICENSE
├── local.properties
├── MANIFESTO.md
├── PROJECT_TREE.md
├── README.md
├── samples
│   └── demo-app
│       ├── build.gradle.kts
│       └── src
├── settings.gradle.kts
├── tests
└── web
    ├── public
    └── src

40 directories, 35 files
#Console
tree -I 'build|.gradle|.git|.idea|out|*.iml' -L 3 > PROJECT_TREE.md

#Create GRADLE_MODULES.txt
./gradlew projects > GRADLE_MODULES.txt

