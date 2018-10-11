package com.example.nhan.assignmentapp.helper;

import java.io.IOException;

public interface IDataStore {
    void saveState() throws IOException;
    void loadState() throws IOException, ClassNotFoundException;
}
