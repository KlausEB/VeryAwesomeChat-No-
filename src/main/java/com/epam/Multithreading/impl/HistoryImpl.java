package com.epam.Multithreading.impl;

import com.epam.Multithreading.IHistory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HistoryImpl implements IHistory {
    private final static File RESOURCE_HISTORY_FILE = new File("src\\main\\resources\\History.txt");
    private final static int NUMBER_OF_MESSAGE_FOR_OUTPUT = 5;
    public static Logger logger = LogManager.getLogger();
    private final Queue<String> messageQueue;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();
    private final BufferedReader bufferedReader;

    public HistoryImpl() throws FileNotFoundException {
        this.bufferedReader = new BufferedReader(new FileReader(RESOURCE_HISTORY_FILE));
        this.messageQueue = new ArrayDeque<>(5);
    }

    public HistoryImpl(BufferedReader bufferedReader, Queue<String> messageQueue) throws FileNotFoundException {
        this.bufferedReader = bufferedReader;
        this.messageQueue = messageQueue;
    }

    @Override
    public String callUpHistory() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (messageQueue.size() == 0) {
                logger.debug("Filling an empty queue with data from a file (with optimization)");
                findLatestHistoryAndPutInQueue();
            }
            readLock.lock();
            for (String message : messageQueue) {
                stringBuilder.append(message).append('\n');
            }
            readLock.unlock();
        } finally {
            bufferedReader.close();
        }
        return stringBuilder.toString();
    }

    @Override
    public void updateHistory(String message) throws IOException {
        writeLock.lock();
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(RESOURCE_HISTORY_FILE, true))) {
            printWriter.println(message);
        }
        if (messageQueue.size() == NUMBER_OF_MESSAGE_FOR_OUTPUT) {
            messageQueue.remove();
        }
        messageQueue.add(message);
        writeLock.unlock();
    }

    public void findLatestHistoryAndPutInQueue() throws IOException {
        String currentMessage = bufferedReader.readLine();
        writeLock.lock();
        while (currentMessage != null) {
            if (messageQueue.size() == NUMBER_OF_MESSAGE_FOR_OUTPUT) {
                messageQueue.remove();
            }
            messageQueue.add(currentMessage);
            currentMessage = bufferedReader.readLine();
        }
        writeLock.unlock();
    }
}
