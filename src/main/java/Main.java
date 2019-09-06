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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Java application for extracting links from URL
 *
 * @version 1.0
 * @autor Alina Ponkratieva
 */
public class Main {

    public static void main(String[] args) throws IOException {

        final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
        final String HTML_A_HREF_TAG_PATTERN =
                "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
        final String HTML_DOMAIN_PATTERN = ".*\\://(?:www.)?([^\\/]+)";

        String urlPath = "https://www.bahn.de/p/view/index.shtml";
        String inputHTML = readFromInputStream(urlPath);


        // --------------------Via Regex
        List<String> tagsRegex = getRegexList(inputHTML, HTML_A_TAG_PATTERN);
        List<String> linksRegex = tagsRegex.stream().filter(tag -> !getRegexList(tag, HTML_A_HREF_TAG_PATTERN).isEmpty()).map(tag -> getRegexList(tag, HTML_A_HREF_TAG_PATTERN).get(0)).collect(Collectors.toList());
        List<String> domainsRegex = linksRegex.stream().filter(link -> !getRegexList(link, HTML_DOMAIN_PATTERN).isEmpty()).map(link -> getRegexList(link, HTML_DOMAIN_PATTERN).get(0).trim().replace("href=", "").replace("\"", "").split("\\?")[0]).collect(Collectors.toList());
        List<String> domainsRegexUnique = domainsRegex.stream().distinct().collect(Collectors.toList());

        int j = 1;
        System.out.println("=========GETTING DOMAINS VIA REGEX=========");
        for (String temp : domainsRegexUnique) {

            System.out.println(temp.replace("http://", "").replace("https://", "").replace("www.", "") + " - " + j);
            j++;
        }


        // ------------Via HTML-parser
        Document doc = Jsoup.parse(inputHTML);
        Elements links = doc.select("a[href]");

        List<String> hosts = getAllLinks(links);

        // Printing all unique hosts
        List<String> hostsUnique = hosts.stream().distinct().collect(Collectors.toList());
        int i = 1;
        System.out.println("=========GETTING DOMAINS VIA HTML-PARSER=========");
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
                String host = uri.getHost();
                if (host != null)
                    hosts.add(host.replace("www.", ""));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return hosts;
    }

    /**
     * Method get all string values satisfied to the template
     *
     * @param input - string value, pattern - Regex-pattern
     * @return regexList - array of string values
     */
    private static List<String> getRegexList(String input, String pattern) {
        List<String> regexList = new ArrayList<>();

        Pattern patternTag = Pattern.compile(pattern);
        Matcher matcherTag = patternTag.matcher(input);
        while (matcherTag.find()) {
            regexList.add(input.substring(matcherTag.start(), matcherTag.end()));
        }
        return regexList;
    }
}
