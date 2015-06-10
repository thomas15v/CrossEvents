package com.thomas15v.crossevents.formatting.Formatters;

import com.google.common.base.Optional;
import com.google.gson.*;
import com.thomas15v.crossevents.formatting.Formatter;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.User;

import java.lang.reflect.Type;

/**
 * Created by thomas15v on 9/06/15.
 */

/**
 * Waiting on UserStorage implementation
 */
public class UserFormatter implements Formatter<User> {
    //todo finish this when sponge is ready

    private Game game;

    public UserFormatter(Game game){
        this.game = game;
    }

    @Override
    public Player deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Optional<Player> player = Optional.absent();
        if (player.isPresent())
            return player.get();
        else
            return null;
    }

    @Override
    public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getUniqueId().toString());
    }
}
