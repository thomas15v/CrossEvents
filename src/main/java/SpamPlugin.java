import com.google.gson.Gson;
import com.thomas15v.crossevents.api.CrossEventService;
import com.thomas15v.crossevents.api.Returnable;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by thomas15v on 9/06/15.
 *
 * This plugin is should only be used for testing :).
 *
 */
@Plugin(id = "SpamPlugin" , name = "SpamPlugin", version = "0.1", dependencies = "required-after:CrossEvents;")
public class SpamPlugin {

    private CrossEventService service;


    public class spamEvent extends AbstractEvent implements Returnable{

        private int integer;
        private String data;
        private Text text;

        public spamEvent(int integer, String data){
            this.integer = integer;
            this.data = data;
            text = Texts.builder().append(Texts.of("Hey")).color(TextColors.BLACK).build();
        }

        public int getInteger() {
            return integer;
        }

        public String getData() {
            return data;
        }

        public Text getText() {
            return text;
        }
    }

    @Subscribe
    public void onEnabled(final ServerStartedEvent event){
        this.service = event.getGame().getServiceManager().provide(CrossEventService.class).get();


        long startTime = System.currentTimeMillis();
        System.out.println(new Gson().toJson(new spamEvent(1711515, "Hello")));
        service.callEvent((new spamEvent(1711515, "Hello")));
                System.out.println("Event ended");
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        System.out.println(duration);

        event.getGame().getAsyncScheduler().runRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                System.out.println(new Gson().toJson(new spamEvent(1711515,"Hello")));
                service.callEvent((new spamEvent(1711515, "Hello")));
                System.out.println("Event ended");
                long endTime = System.currentTimeMillis();
                long duration = (endTime - startTime);
                System.out.println(duration);
            }
        }, TimeUnit.SECONDS, 1);
    }



}
