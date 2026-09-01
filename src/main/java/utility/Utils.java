package utility;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Utils {

    public static String readBody(HttpExchange exchange) throws IOException
    {
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))){
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
