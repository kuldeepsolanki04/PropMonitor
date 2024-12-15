package com.aw.propsmonitor.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int BUFFER_SIZE = 8192;

    public static void main(String[] args) throws Exception {
        // Check if config file is passed as argument
        String configFilePath = args.length > 0 ? args[0] : "server_config.properties";
        System.out.println("Server started with config file: " + configFilePath);

        Properties config = loadConfig(configFilePath);

        String outputDirectory = config.getProperty("outputDirectory");
        int port = Integer.parseInt(config.getProperty("port"));

        // Start the server to listen on the port
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);

            // Thread pool for handling multiple clients
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            System.out.println("Thread pool created with 10 threads.");

            while (true) {
                System.out.println("Waiting for client connections...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from client: " + clientSocket.getInetAddress());
                executorService.submit(new ClientHandler(clientSocket, outputDirectory));
            }
        }
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

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private String outputDirectory;

        public ClientHandler(Socket clientSocket, String outputDirectory) {
            this.clientSocket = clientSocket;
            this.outputDirectory = outputDirectory;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                StringBuilder content = new StringBuilder();
                String line;

                // Read all lines from client until the connection is closed
                while ((line = reader.readLine()) != null) {
                    System.out.println("Received line: " + line); // Debugging line
                    content.append(line).append("\n");
                }

                if (content.length() == 0) {
                    System.out.println("No content received from client.");
                } else {
                    System.out.println("Received content from client: ");
                    System.out.println(content);
                }

                // Write the properties file
                String filename = "filtered_" + System.currentTimeMillis() + ".properties";
                File file = new File(outputDirectory + "/" + filename);
                System.out.println("Writing filtered content to: " + file.getPath());
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(content.toString());
                    writer.flush(); // Ensure the content is written
                }
                System.out.println("Filtered properties written successfully to: " + file.getPath());
            } catch (IOException e) {
                System.err.println("Error while handling client: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close(); // Close the socket after processing
                    System.out.println("Client connection closed.");
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }
}
