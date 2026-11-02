package br.com.financepro.financePro.transaction.exception;

public class InvalidTransactionUpdateException extends RuntimeException {
    public InvalidTransactionUpdateException(String message) {
        super(message);
    }
}
