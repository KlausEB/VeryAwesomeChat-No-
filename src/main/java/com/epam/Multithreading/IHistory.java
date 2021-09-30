package com.epam.Multithreading;

import java.io.IOException;

public interface IHistory {
    String callUpHistory() throws IOException;

    void updateHistory(String message) throws IOException;
}
