package com.aw.propsmonitor.client;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

public class Client {
    public static void main(String[] args) throws Exception {
        // Check if config file is passed as argument
        String configFilePath = args.length > 0 ? args[0] : "client_config.properties";
        System.out.println("Client started with config file: " + configFilePath);

        Properties config = loadConfig(configFilePath);

        String serverAddress = config.getProperty("serverAddress");
        int serverPort = Integer.parseInt(config.getProperty("serverPort"));
        String inputDirectory = config.getProperty("inputDirectory");

        // Debugging: Check the inputDirectory value
        if (inputDirectory == null || inputDirectory.trim().isEmpty()) {
            System.err.println("Error: inputDirectory is not set in the configuration file.");
            return; // Exit the program if the directory is not specified
        }

        System.out.println("Input directory: " + inputDirectory);

        // Watch the directory for changes
        watchDirectory(inputDirectory, serverAddress, serverPort);
    }

    private static Properties loadConfig(String configFile) throws IOException {
        Properties config = new Properties();
        File configFilePath = new File(configFile);
        System.out.println("Loading config from: " + configFilePath.getAbsolutePath());

        try (InputStream inputStream = new FileInputStream(configFilePath)) {
            config.load(inputStream);
        }

        System.out.println("Config loaded successfully.");
        return config;
    }

    private static void watchDirectory(String directoryPath, String serverAddress, int serverPort) throws IOException {
        Path path = Paths.get(directoryPath);
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

            System.out.println("Monitoring directory: " + directoryPath);

            while (true) {
                WatchKey key;
                try {
                    // Wait for a key to be signaled
                    key = watchService.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                // Process all the events for the events that have occurred
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path filename = (Path) event.context();

                    // Print event details
                    System.out.println(kind.name() + ": " + filename);

                    // If a new file is created or an existing file is modified, send it to the server
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        File file = new File(directoryPath + File.separator + filename);
                        if (file.exists() && file.isFile() && filename.toString().endsWith(".properties")) {
                            // Re-establish connection and send file data
                            sendFileToServer(file, serverAddress, serverPort);
                        }
                    }
                }

                // Reset the key to be able to receive further events
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        }
    }

    private static void sendFileToServer(File file, String serverAddress, int serverPort) throws IOException {
        System.out.println("Processing file: " + file.getName());

        // Establish connection to the server for each file
        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to server at " + serverAddress + ":" + serverPort);

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Send each line to the server
                    writer.println(line);
                    System.out.println("Sending to server: " + line);
                }
            }

            // Close the socket output stream after sending all data
            writer.flush();
            System.out.println("All properties sent. Closing connection.");
        }
    }
}
