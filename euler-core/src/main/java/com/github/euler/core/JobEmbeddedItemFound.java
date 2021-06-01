package com.github.euler.core;

public class JobEmbeddedItemFound extends JobItemFound implements EulerCommand {

    public JobEmbeddedItemFound(EmbeddedItemFound msg) {
        super(msg.uri, msg.itemURI, msg.ctx);
    }

}
