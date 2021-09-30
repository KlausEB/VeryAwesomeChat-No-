package com.epam.Multithreading.impl;

import com.epam.Multithreading.IHistory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArraySet;

public class ConnectedThread implements Runnable {
    public static Logger logger = LogManager.getLogger();
    private final Socket socket;
    private final CopyOnWriteArraySet<PrintWriter> clientOutputStreamList;
    private final IHistory history;
    private final BufferedReader socketReader;
    private final PrintWriter socketWriter;

    public ConnectedThread(Socket socket, CopyOnWriteArraySet<PrintWriter> clientOutputStreamList, IHistory history, BufferedReader socketReader, PrintWriter socketWriter) throws IOException {
        this.socket = socket;
        this.clientOutputStreamList = clientOutputStreamList;
        this.history = history;
        this.socketReader = socketReader;
        this.socketWriter = socketWriter;
    }

    @Override
    public void run() {
        try {
            logger.debug("Starting of thread init");
            clientOutputStreamList.add(socketWriter);
            String nickname = takeNicknameFromClient();
            sendHistoryToClient();
            logger.debug("Ending of thread init");
            logger.debug("Starting listening to a socket connection");
            while (!socket.isInputShutdown()) {
                if (socketReader.ready()) {
                    String message = socketReader.readLine();
                    logger.info('\"' + message + '\"' + " was received from the client");
                    boolean clientIsLogOut = false;
                    if (message.equals("!EXIT")) {
                        clientOutputStreamList.remove(socketWriter);
                        message = "User " + nickname + "log out of chat";
                        clientIsLogOut = true;
                    } else {
                        message = nickname + message;
                    }
                    logger.info("Message after processing: \"" + message + '\"');
                    sendMessageToAllClients(message);
                    logger.debug("The message has been sent to all customers");
                    history.updateHistory(message);
                    logger.debug("History was update");

                    if (clientIsLogOut) {
                        logger.debug("End of the socket working");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            logger.catching(e);
        } finally {
            logger.debug("Closing I/O");
            socketWriter.close();
            try {
                socketReader.close();
                socket.close();
            } catch (IOException e) {
                logger.catching(e);
            }
        }
    }

    public String takeNicknameFromClient() throws IOException {
        socketWriter.println("Input your nickname");
        socketWriter.flush();
        String nickname = socketReader.readLine();
        logger.info("A new client named " + nickname);
        return "<" + nickname + "> ";
    }

    public void sendHistoryToClient() throws IOException {
        String stringHistory = history.callUpHistory();
        socketWriter.println(stringHistory);
        socketWriter.flush();
    }

    public void sendMessageToAllClients(String message) {
        for (PrintWriter bw : clientOutputStreamList) {
            if (!bw.equals(socketWriter)) {
                bw.println(message);
                bw.flush();
            }
        }
    }
}
