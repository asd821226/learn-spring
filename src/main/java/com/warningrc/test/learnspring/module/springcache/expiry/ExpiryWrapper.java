package com.warningrc.test.learnspring.module.springcache.expiry;

import java.io.Serializable;

import lombok.Value;

@Value
class ExpiryWrapper implements Serializable {
    private static final long serialVersionUID = 1L;
    private long saveTime;
    private long expiryTime;
    private Object value;
}
