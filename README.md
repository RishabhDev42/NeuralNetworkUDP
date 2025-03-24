# Neural Network Client-Server Application

## Overview

This project implements a client-server application for training and testing a neural network to learn a truth table. The client sends requests to the server, which processes the requests and returns the appropriate responses.

## Project Structure

- `src/main/java/ds/NeuralNetworkClient.java`: Contains the client-side code for sending requests and receiving responses.
- `src/main/java/ds/NeuralNetworkServer.java`: Contains the server-side code for processing requests and managing the neural network.
- `pom.xml`: Maven configuration file for managing dependencies.

## Dependencies

- Java 11 or higher
- Maven
- Gson library for JSON parsing

## Setup

1. Clone the repository:
    ```sh
    git clone <repository-url>
    cd <repository-directory>
    ```

2. Build the project using Maven:
    ```sh
    mvn clean install
    ```

## Running the Application

### Server

1. Navigate to the `src/main/java/ds` directory.
2. Run the `NeuralNetworkServer` class:
    ```sh
    java ds.NeuralNetworkServer
    ```

### Client

1. Navigate to the `src/main/java/ds` directory.
2. Run the `NeuralNetworkClient` class:
    ```sh
    java ds.NeuralNetworkClient
    ```

## Usage

### Client Menu

The client provides a menu with the following options:

1. Display the current truth table.
2. Provide four inputs for the range of the two-input truth table and build a new neural network.
3. Perform a single training step.
4. Perform multiple training steps.
5. Test the neural network with a pair of inputs.
6. Exit the program.

### Example

To display the current truth table, select option `0` from the menu. The client will send a request to the server, and the server will respond with the current truth table values.

## Error Handling

The application includes error handling for malformed JSON and invalid user inputs. If an error occurs, an appropriate message will be displayed to the user.