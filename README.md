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

##Example Project
```java
import com.thomas15v.crossevents.api.CrossEventService;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerChatEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Texts;

@Plugin(id="CrossChat", name="CrossChat", version="0.1", dependencies="required-after:CrossEvents;")
public class CrossChat
{
    private CrossEventService service;
    private Game game;

    @Subscribe
    public void onEnable(ServerStartedEvent event)
    {
        this.game = event.getGame();
        this.service = ((CrossEventService)this.game.getServiceManager().provide(CrossEventService.class).get());
    }

    public class CrossChatEvent
            extends AbstractEvent
    {
        private String message;

        public CrossChatEvent(String message)
        {
            this.message = message;
        }

        public String getMessage()
        {
            return this.message;
        }
    }

    @Subscribe
    public void PlayerChatEvent(CrossChatEvent event)
    {
        this.game.getServer().getBroadcastSink().sendMessage(Texts.parseJson(event.getMessage()));
    }

    @Subscribe
    public void PlayerChatEvent(PlayerChatEvent event)
    {
        this.service.callEvent(new CrossChatEvent(Texts.toJson(event.getNewMessage())));
    }
}
```
