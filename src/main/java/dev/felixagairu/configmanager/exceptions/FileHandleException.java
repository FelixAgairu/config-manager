package dev.felixagairu.configmanager.exceptions;

public class FileHandleException extends RuntimeException {
    public FileHandleException(String message) {
        super("[config-manager] File IO Exception:\n " + message);
    }
}
