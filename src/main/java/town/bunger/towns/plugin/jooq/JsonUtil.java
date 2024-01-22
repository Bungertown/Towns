package town.bunger.towns.plugin.jooq;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jooq.JSON;

public class JsonUtil {

    public static JsonObject fromJooq(JSON json) {
        return JsonParser.parseString(json.data()).getAsJsonObject();
    }

    public static JSON toJooq(JsonObject object) {

        return JSON.valueOf(object.toString());
    }
}
