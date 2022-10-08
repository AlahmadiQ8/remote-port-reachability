package io.momonet.portcheckerv2.models;

public enum Status {
    REACHABLE,
    UNREACHABLE,
    UNKNOWN,
    // TODO: Use loading status for disabling buttons
    LOADING,
}
