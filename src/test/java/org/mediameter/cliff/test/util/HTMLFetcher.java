package org.mediameter.cliff.test.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import de.l3s.boilerpipe.sax.HTMLDocument;

/**
 * A very simple HTTP/HTML fetcher, really just for demo purposes.
 * 
 * @author Christian Kohlschütter
 */
public class HTMLFetcher {
        private HTMLFetcher() {
        }
        
        private static final Pattern PAT_CHARSET = Pattern.compile("charset=([^; ]+)$");
        
        /**
         * Fetches the document at the given URL, using {@link URLConnection}.
         * @param url
         * @return
         * @throws IOException
         */
         
        //      Instead of using URLConnection in java, if you use HttpURLConnection
        //      we can able to access the requested web page from java.
        //      Try the following code
        //
        //      HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
        //      httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
        //
        //      Normal java using urlConnection wont accept to access the internet.
        //      If access the browser it will allow to perform a search
        //      without this exception "HTTP response code : 403 for URL"
        //  exception caused:
        //  de.l3s.boilerpipe.BoilerpipeProcessingException: java.io.IOException:
        //  Server returned HTTP response code: 403 for URL:
        //  http://petapixel.com/2013/05/13/sony-xperia-zr-smartphone-doubles-as-an-underwater-camera/
        //  Changes done by: Daniel da Silva Souza, University of Brasilia (UnB), Brazil
        public static HTMLDocument fetch(final URL url) throws IOException {
                final HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
                final String ct = httpcon.getContentType();

                Charset cs = Charset.forName("Cp1252");
                if (ct != null) {
                        Matcher m = PAT_CHARSET.matcher(ct);
                        if(m.find()) {
                                final String charset = m.group(1);
                                try {
                                        cs = Charset.forName(charset);
                                } catch (UnsupportedCharsetException e) {
                                        // keep default
                                }
                        }
                }
                
                InputStream in = httpcon.getInputStream();

                final String encoding = httpcon.getContentEncoding();
                if(encoding != null) {
                        if("gzip".equalsIgnoreCase(encoding)) {
                                in = new GZIPInputStream(in);
                        } else {
                                System.err.println("WARN: unsupported Content-Encoding: "+encoding);
                        }
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int r;
                while ((r = in.read(buf)) != -1) {
                        bos.write(buf, 0, r);
                }
                in.close();

                final byte[] data = bos.toByteArray();
                
                return new HTMLDocument(data, cs);
        }
}