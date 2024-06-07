package aau.cc;

import aau.cc.model.Language;
import aau.cc.model.UserInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInputHandler {
    private static final Scanner scanner = new Scanner(System.in);

    public static UserInput askUserForInput() {
        UserInput userInput = new UserInput();
        userInput.setUrls(askUserForURLs());
        userInput.setDepth(askUserForCrawlingDepth());
        userInput.setTargetLanguage(askUserForTargetLanguage());
        userInput.setDomains(askUserForDomainsToBeCrawled());
        System.out.println("Got input parameters.");
        return userInput;
    }

    public static List<String> askUserForURLs() { //todo handle InputMismatchException
        List<String> urls = new ArrayList<>();
        System.out.println("Enter multiple URLs to be crawled (empty line to proceed):");
        String url;
        while (!(url = scanner.nextLine()).isEmpty()) {
            urls.add(url);
        }
        if (urls.isEmpty()) {
            System.out.println("Error. At least 1 URL is required!");
            askUserForURLs();
        }
        return urls;
    }

    public static int askUserForCrawlingDepth() {
        System.out.print("Enter the depth of websites to crawl: ");
        int depth = scanner.nextInt(); // todo catch exception
        scanner.nextLine();
        return depth;
    }

    public static Language askUserForTargetLanguage() {
        Language targetLanguage;
        do {
            System.out.print("Enter the target language code (e.g., en, de, fr, it, es, or none): ");
            String inputLanguage = scanner.nextLine().trim().toLowerCase();
            if (inputLanguage.equals("none") || inputLanguage.isEmpty()) {
                targetLanguage = null;
                break;
            }
            targetLanguage = Language.findLanguage(inputLanguage);
        } while (targetLanguage == null);
        return targetLanguage;
    }

    public static List<String> askUserForDomainsToBeCrawled() {
        List<String> domains = new ArrayList<>();
        System.out.println("Enter the domain(s) to be crawled (leave empty if all)\ne.g. website.at, sub.website.at, website.de:");
        String domain;
        while (!(domain = scanner.nextLine()).isEmpty()) {
            domains.add(domain);
        }
        return domains;
    }
}
