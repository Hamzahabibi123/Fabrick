package com.example.fabrick.util;

public final class ErrorCodes {

    private ErrorCodes() {} // evita istanziazione

    public static final String API_GENERIC_ERROR = "API000";
    public static final String API_BALANCE_ERROR = "API001";
    public static final String API_TRANSACTIONS_ERROR = "API002";
    public static final String API_TRANSFER_ERROR = "API003";

    // Messaggi
    public static final String MSG_GENERIC_ERROR = "Errore tecnico: contattare il supporto.";
    public static final String MSG_BALANCE_ERROR = "Errore durante il recupero del saldo.";
    public static final String MSG_TRANSACTIONS_ERROR = "Errore durante il recupero delle transazioni.";
    public static final String MSG_TRANSFER_ERROR = "Errore tecnico: condizione BP049 non prevista per il conto id ";
}
