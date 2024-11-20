package org.br.models;

public class Machine {
    private int port;
    private String host;

    public Machine(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "port=" + port +
                ", host='" + host + '\'' +
                '}';
    }

    public Machine() {
    }

    // Method to create a Machine instance from a string
    public static Machine fromString(String str) {
        // Remove "Machine{" prefix and trailing '}'
        str = str.replace("Machine{", "").replace("}", "");

        // Split the string by comma to separate fields
        String[] fields = str.split(", ");

        // Create a new Machine instance
        Machine machine = new Machine();

        // Parse each field and set the corresponding value
        for (String field : fields) {
            String[] keyValue = field.split("=");
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            switch (key) {
                case "port":
                    machine.port = Integer.parseInt(value);
                    break;
                case "host":
                    machine.host = value.replace("'", ""); // Remove single quotes
                    break;
                default:
                    throw new IllegalArgumentException("Unknown field: " + key);
            }
        }

        return machine;
    }
}
