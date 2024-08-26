package cloud.qasino.games.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QasinoUtils {

    public static String prettyPrint(Object uglyString)throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(uglyString);
        return prettyJson;
    }

}
