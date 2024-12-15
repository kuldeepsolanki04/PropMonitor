# Variables
PROJECT_DIR := $(shell pwd)
SRC_DIR := $(PROJECT_DIR)/src/main/java
BIN_DIR := $(PROJECT_DIR)/bin
LIB_DIR := $(PROJECT_DIR)/lib
CLIENT_CLASS := com.aw.propsmonitor.client.Client
SERVER_CLASS := com.aw.propsmonitor.server.Server
CLIENT_CONFIG := $(PROJECT_DIR)/src/main/java/com/aw/propsmonitor/config/client_config.properties
SERVER_CONFIG := $(PROJECT_DIR)/src/main/java/com/aw/propsmonitor/config/server_config.properties
JAVAC := javac
JAVA := java
JAR := jar
CP := $(BIN_DIR):$(LIB_DIR)/*

# Default target (build the project)
all: build

# Clean up build artifacts
clean:
	rm -rf $(BIN_DIR)

# Create the bin directory if it doesn't exist
$(BIN_DIR):
	mkdir -p $(BIN_DIR)
	
# Copy config files to the bin directory
copy-config:
	cp $(CLIENT_CONFIG) $(BIN_DIR)
	cp $(SERVER_CONFIG) $(BIN_DIR)

# Compile the project (including all Java files)
build: clean $(BIN_DIR) copy-config
	$(JAVAC) -d $(BIN_DIR) $(SRC_DIR)/com/aw/propsmonitor/**/*.java

# Run the client
run-client: build
	$(JAVA) -cp $(CP) $(CLIENT_CLASS) $(BIN_DIR)/client_config.properties

# Run the server
run-server: build
	$(JAVA) -cp $(CP) $(SERVER_CLASS) $(BIN_DIR)/server_config.properties

# Package the project into a JAR (Optional - if you want to generate a JAR file)
package: build
	$(JAR) cvf $(PROJECT_DIR)/target/propsmonitor.jar -C $(BIN_DIR) .

# Rebuild the project (clean, then build)
rebuild: clean build

# Run all tests (assuming test files are placed under src/test/java)
test: build
	$(JAVA) -cp $(CP) org.junit.runner.JUnitCore com.aw.propsmonitor.test.AppTest

.PHONY: all build run-client run-server clean package rebuild test
