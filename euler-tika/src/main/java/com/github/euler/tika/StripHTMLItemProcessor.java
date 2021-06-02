package com.github.euler.tika;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.github.euler.common.CommonContext;
import com.github.euler.common.CommonMetadata;
import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessingContext.Action;

public class StripHTMLItemProcessor implements ItemProcessor {

    private final StreamFactory sf;
    private final StorageStrategy parsedContentStrategy;

    public StripHTMLItemProcessor(StreamFactory sf, StorageStrategy parsedContentStrategy) {
        super();
        this.sf = sf;
        this.parsedContentStrategy = parsedContentStrategy;
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        ProcessingContext ctx = item.ctx;
        URI uri = ctx.context(CommonContext.PARSED_CONTENT_FILE, null);
        boolean isEmpty = uri == null || sf.isEmpty(uri, ctx);
        boolean isDirectory = ctx.metadata(CommonMetadata.IS_DIRECTORY, false);

        if (!isDirectory && !isEmpty) {
            URI parsedContent = parsedContentStrategy.createFile(uri, ".txt");
            InputStream in = null;
            Writer out = null;
            try {
                in = sf.openInputStream(uri, ctx);
                out = new OutputStreamWriter(sf.openOutputStream(parsedContent, ctx), "utf-8");

                Document doc = Jsoup.parse(in, "utf-8", uri.toURL().toString());
                out.write(doc.text());
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            return ProcessingContext.builder()
                    .context(CommonContext.PARSED_CONTENT_FILE, parsedContent)
                    .setAction(Action.OVERWRITE)
                    .build();
        }
        return ProcessingContext.EMPTY;
    }

}
