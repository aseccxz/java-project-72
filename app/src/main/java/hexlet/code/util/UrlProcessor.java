package hexlet.code.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlProcessor {
    public static String normalizeUrl(String urlString) throws URISyntaxException, MalformedURLException {
        URL url = new URI(urlString).toURL();
        String port = url.getPort() == -1 ? "" : ":" + url.getPort();
        return String.format("%s://%s%s", url.getProtocol(), url.getHost(), port);
    }
}
