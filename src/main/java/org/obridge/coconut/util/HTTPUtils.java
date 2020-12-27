package org.obridge.coconut.util;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HTTPUtils {

    public static String getContent(String url) throws IOException, InterruptedException {

        HttpGet request = null;

        try {

            HttpClient client = HttpClientBuilder.create().build();
            request = new HttpGet(url);

            request.addHeader("User-Agent", "Mozilla/5.0 (compatible; newsloop)");
            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);

        } finally {

            if (request != null) {

                request.releaseConnection();
            }
        }

    }
}
