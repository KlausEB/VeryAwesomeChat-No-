package com.epam.Multithreading;

import java.io.IOException;

public interface IServer {
    void serverStart() throws IOException;

    void serverExit() throws IOException;
}
