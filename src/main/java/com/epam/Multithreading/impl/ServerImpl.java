package com.epam.Multithreading.impl;

import com.epam.Multithreading.IHistory;
import com.epam.Multithreading.IServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerImpl implements IServer {
    public static Logger logger = LogManager.getLogger();
    private final ServerSocket server;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final CopyOnWriteArraySet<PrintWriter> clientOutputStreamList = new CopyOnWriteArraySet<>();
    private final IHistory history = new HistoryImpl();

    public ServerImpl(int portNumber) throws IOException {
        server = new ServerSocket(portNumber);
    }

    @Override
    public void serverStart() throws IOException {
        logger.debug("Starting the server " + server.getInetAddress());
        while (!server.isClosed()) {
            Socket socket = server.accept();
            logger.info("New connect: " + socket.getInetAddress());
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter socketWriter = new PrintWriter(socket.getOutputStream());
            executor.execute(
                    new ConnectedThread(socket, clientOutputStreamList, history, socketReader, socketWriter));
        }

    }

    @Override
    public void serverExit() throws IOException {
        logger.debug("End of the server working " + server.getInetAddress());
        executor.shutdownNow();
        server.close();
    }
}
