# CrossEvents [![Build Status](http://ci.thomas15v.net/job/CrossEvents/badge/icon)](http://ci.thomas15v.net/job/CrossEvents/)
A small Library to call sponge events across servers  

#For Plugin developers
##Maven and Gradle dependencies 
(If the repo is down, ping thomas15v on esper.net)
```xml
<project>
    [...]
    <repositories>
        [...]
        <repository>
          <id>thomas5v-repo</id>
          <url>http://repo.thomas15v.net/</url>
        </repository>
    </repositories>
    [...]
      <dependencies>
        [...]
        <dependency>
        <groupId>com.thomas15v</groupId>
        <artifactId>crossevents</artifactId>
        <version>0.1</version>
      </dependency>
    </dependencies>
    [...]
</project>


```
```gradle
repositories {
    mavenCentral()
    maven {
        name 'Thomas15v-repo'
        url 'http://repo.thomas15v.net'
    }
}

dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.11'
    compile "com.thomas15v:crossevents:0.1"
}
```

[##Examples](https://github.com/thomas15v/CrossEvents/tree/master/src/examples/java)

