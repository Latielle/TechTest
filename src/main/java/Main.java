import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Java application for extracting links from URL
 *
 * @version 1.0
 * @autor Alina Ponkratieva
 */
public class Main {

    public static void main(String[] args) throws IOException {

        String urlPath = "https://www.bahn.de/p/view/index.shtml";
        String inputHTML = readFromInputStream(urlPath);

        Document doc = Jsoup.parse(inputHTML);
        Elements links = doc.select("a[href]");

        List<String> hosts = getAllLinks(links);

        // Printing all unique hosts
        List<String> hostsUnique = hosts.stream().distinct().collect(Collectors.toList());
        int i = 1;
        for (String temp : hostsUnique) {
            if (temp != null) {
                System.out.println(temp + " - " + i);
                i++;
            }
        }
    }

    /**
     * Method reed HTML-page context as InputStream from Internet and return this contex as String
     *
     * @param urlPath HTML-pages
     * @return string HTML-contex
     * @throws IOException
     */
    private static String readFromInputStream(String urlPath)
            throws IOException {
        URL urlObject = new URL(urlPath);
        URLConnection urlConnection = urlObject.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        String resultStringBuilder;
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            resultStringBuilder = br.lines().map(line -> line + "\n").collect(Collectors.joining());
        }
        return resultStringBuilder;
    }

    /**
     * Method get host from all links HTML-page and return all link as String List
     *
     * @param links - array of links
     * @return hosts - array of hosts
     */
    private static List<String> getAllLinks(Elements links) {
        List<String> hosts = new ArrayList<>();

        for (Element link : links) {
            try {
                URI uri = new URI(link.attr("href"));
                hosts.add(uri.getHost());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return hosts;
    }
}
