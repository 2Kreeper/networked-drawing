package eu.barononline.network_classes;

import com.sun.istack.internal.NotNull;

import java.util.HashMap;
import java.util.Map;

public class NetworkCommand<E extends Enum<E>> {

    private static final String COMMAND_TYPE_HEADER_NAME = "Command-Type";
    //private static final Pattern DATA_REGEX = Pattern.compile("(?<headers>(.*:.*\n)*)\n(?<body>.*)");

    private E commandType;
    private HashMap<String, String> headers = new HashMap<>();
    private String body;

    public NetworkCommand(@NotNull E commandType, @NotNull String body) {
        this.body = body;
        this.commandType = commandType;

        addHeader(COMMAND_TYPE_HEADER_NAME, commandType.toString());
    }

    public static <E extends Enum<E>> NetworkCommand<E> parse(@NotNull String raw, @NotNull Class<E> enumClass) throws ClassCastException {
        String[] split = raw.split("\n\n");
        if(split.length != 2)
            throw new ClassCastException();

        try {
            HashMap<String, String> headers = parseHeaders(split[0]);
            E commandType = Enum.valueOf(enumClass, headers.get(COMMAND_TYPE_HEADER_NAME));
            NetworkCommand<E> command = new NetworkCommand<>(commandType, split[1]);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                command.addHeader(header.getKey(), header.getValue());
            }

            return command;
        } catch (NullPointerException e) {
            throw new ClassCastException("Command type not specified!");
        } catch (IllegalArgumentException e) {
            throw new ClassCastException("Command type unknown! Did you specify the correct enum?");
        }
    }

    /**
     * NOTE: This function expects header to be formatted correctly!
     * @param headerString
     * @return
     */
    private static HashMap<String, String> parseHeaders(@NotNull String headerString) {
        HashMap<String, String> out = new HashMap<>();

        for(String line : headerString.split("\n")) {
            int splitIndex = line.indexOf(": ");
            if(splitIndex == -1) {
                throw new ClassCastException("Invalid header: " + line);
            }

            String key = line.substring(0, splitIndex);
            String value = line.substring(splitIndex + 2, line.length());

            out.put(key, value);
        }

        return out;
    }

    public NetworkCommand addHeader(@NotNull String key, @NotNull String value) {
        if(key == null || value == null) {
            return this;
        }

        if(!key.equals(COMMAND_TYPE_HEADER_NAME) || !headers.containsKey(COMMAND_TYPE_HEADER_NAME)) {
            headers.put(key, value);
        }
        return this;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public boolean containsHeader(String key) {
        return headers.containsKey(key);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        for(Map.Entry<String, String > pair : headers.entrySet()) {
            out.append(pair.getKey() + ": " + pair.getValue() + "\n");
        }
        out.append("\n");

        out.append(body);

        return out.toString();
    }

    public String getBody() {
        return body;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public E getCommandType() {
        return commandType;
    }
}
