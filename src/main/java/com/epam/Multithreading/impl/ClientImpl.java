package com.epam.Multithreading.impl;

import com.epam.Multithreading.IClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class ClientImpl implements IClient {
    public static Logger logger = LogManager.getLogger();
    private final String serverIP;
    private final int portNumber;

    public ClientImpl(String serverIP, int portNumber) {
        this.serverIP = serverIP;
        this.portNumber = portNumber;
    }

    @Override
    public void logInToChat() throws IOException {
        try (Socket socket = new Socket(serverIP, portNumber);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
             InputStream socketInputStream = socket.getInputStream();
             BufferedReader socketReader = new BufferedReader(new InputStreamReader(socketInputStream));
             PrintWriter socketWriter = new PrintWriter(socket.getOutputStream())) {
            while (!socket.isInputShutdown()) {
                if (socketReader.ready()) {
                    String serverMessage = socketReader.readLine();
                    logger.info('\"' + serverMessage + '\"' + " was received from the server");
                    System.out.println(serverMessage);
                    if (serverMessage.equals("Input your nickname")) {
                        logger.debug("Init nickname");
                        socketWriter.println(consoleReader.readLine());
                        socketWriter.flush();
                    }
                }
                if (consoleReader.ready()) {
                    String inputMessage = consoleReader.readLine();
                    logger.info('\"' + inputMessage + '\"' + " was received from th console for sending");
                    socketWriter.println(inputMessage);
                    socketWriter.flush();
                    if (inputMessage.equals("!EXIT")) {
                        logger.debug("End of the client working");
                        break;
                    }
                }
            }

        }
    }
}
