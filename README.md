
# Client-Server Project

This project demonstrates a client-server application where the **client** monitors a directory for `.properties` file changes and sends the content to the **server** for processing. The **server** processes the received data and stores the filtered results in a file.

## Project Overview

- **Client**: Monitors a directory for new or modified `.properties` files and sends the file content to the server.
- **Server**: Receives the file content from the client, processes it, and stores the filtered results.

## Prerequisites

1. **Java Development Kit (JDK)**: Version 8 or later is required.  
   You can download it from [here](https://www.oracle.com/java/technologies/javase-downloads.html).

2. **Make**: Used to automate the build and execution process. If you don't have Make installed, follow the steps below to install it.

## Step 1: Install Make

If you don't already have Make installed, install it using your package manager:

- On macOS: `brew install make`
- On Linux: `sudo apt-get install make` (for Ubuntu/Debian)
- On Windows: You can use tools like Gow or MinGW to install make.

## Step 2: Build the Project

Once Make is installed, navigate to the project directory and run the following make command:

```bash
make
```

This will:

1. Clean up any previous build artifacts.
2. Create the `bin` directory if it doesn't exist.
3. Copy the necessary configuration files (`client_config.properties` and `server_config.properties`) into the `bin` directory.
4. Compile the Java source files into the `bin` directory.

## Step 3: Run the Client and Server

To run the **server** application:

```bash
make run-server
```

To run the **client** application:

```bash
make run-client
```

The client will start monitoring the specified directory for `.properties` file changes and send the file contents to the server.

## Step 4: Optional - Package the Project into a JAR

If you want to package the project into a JAR file, use the following command:

```bash
make package
```

This will create a `propsmonitor.jar` file in the `target` directory.

## Step 5: Monitor the Directory

The client will continuously monitor the specified directory (as defined in the `client_config.properties` file) for new or modified `.properties` files. When a new or modified file is detected, the client sends its contents to the server for processing.

The server processes the received data and stores the filtered results in a file (as defined in the `server_config.properties` file).

## Configuration Files

- **client_config.properties**: Contains the client-side configuration such as the server address, port, and the directory to monitor.
- **server_config.properties**: Contains the server-side configuration such as the file to store processed data.

These configuration files are copied to the `bin` directory during the build process.

## Makefile Targets

The Makefile provides the following targets to automate the build and execution process:

- **all**: The default target that builds the project.
- **clean**: Cleans up the build artifacts (removes the `bin` directory).
- **build**: Compiles the Java source files and prepares the project for execution.
- **copy-config**: Copies the configuration files into the `bin` directory.
- **run-client**: Builds the project and runs the client application.
- **run-server**: Builds the project and runs the server application.
- **package**: Packages the project into a JAR file.
- **rebuild**: Cleans the project and rebuilds it.

## Notes

- Make sure to update the `client_config.properties` and `server_config.properties` with the correct paths and settings before running the client and server.
- The client will continuously monitor the specified directory for new or modified `.properties` files.
- The server will store the filtered results in a file as per its configuration.
