package com.github.euler.common;

import java.net.URI;

import org.apache.commons.codec.digest.DigestUtils;

import com.github.euler.core.Item;

public class URIHashIdCalculator implements IdCalculator {

    @Override
    public String calculate(Item item) {
        URI uri = item.itemURI.normalize();
        return DigestUtils.md5Hex(uri.toString()).toLowerCase();
    }

}
