package aau.cc;

import aau.cc.model.Language;
import aau.cc.model.UserInput;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class UserInputHandler {
    private static Scanner scanner = new Scanner(System.in);

    public static UserInput askUserForInput() {
        UserInput userInput = new UserInput();
        userInput.setUrls(askUserForURLs());
        userInput.setDepth(askUserForCrawlingDepth());
        userInput.setTargetLanguage(askUserForTargetLanguage());
        userInput.setDomains(askUserForDomainsToBeCrawled());
        System.out.println("Got input parameters.");
        return userInput;
    }

    public static List<String> askUserForURLs() {
        List<String> urls = new ArrayList<>();
        System.out.println("Enter multiple URLs to be crawled (empty line to proceed):");
        String url;
        while (!(url = scanner.nextLine()).isEmpty()) {
            urls.add(url);
        }
        if (urls.isEmpty()) {
            System.out.println("At least 1 URL is required!");
            return askUserForURLs();
        }
        return urls;
    }

    public static int askUserForCrawlingDepth() {
        System.out.print("Enter the depth of websites to crawl: ");
        int depth;
        while (true) {
            try {
                depth = scanner.nextInt();
                if (depth > 0) break;
                System.out.print("Depth must be a positive integer: ");
            } catch (InputMismatchException e) {
                System.err.print("Invalid input, need integer, try again: ");
            } finally {
                scanner.nextLine();
            }
        }
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

    protected static void setScanner(Scanner scanner) {
        UserInputHandler.scanner = scanner;
    }
}
