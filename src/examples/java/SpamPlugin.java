import com.google.gson.Gson;
import com.thomas15v.crossevents.api.CrossEventService;
import com.thomas15v.crossevents.api.Returnable;
import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;

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

        public spamEvent(int integer, String data){
            this.integer = integer;
            this.data = data;
        }

        public int getInteger() {
            return integer;
        }

        public String getData() {
            return data;
        }
    }

    @Subscribe
    public void onEnabled(ServerStartedEvent event){
        this.service = event.getGame().getServiceManager().provide(CrossEventService.class).get();

        event.getGame().getAsyncScheduler().runRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                System.out.println(new Gson().toJson(new spamEvent(1711515,"Hello")));
                service.callEvent(new spamEvent(1711515,"Hello"));
                System.out.println("Event ended");
                long endTime = System.currentTimeMillis();
                long duration = (endTime - startTime);
                System.out.println(duration);
            }
        }, TimeUnit.MILLISECONDS, 1);
    }



}
