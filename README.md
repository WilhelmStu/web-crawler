# Clean Code (622.060, 24S)
## Web-Crawler

This is a simple web crawler tool built in Java that allows to crawl websites up to a specified depth.
It will give an overview of headings and links present on the website in an exported .md file.
Multiple websites can be parsed in parallel, the parsing process itself also runs in parallel to maximize speed.
Parsing the same website multiple times with higher depths may lead to timeouts on those sites.
Furthermore, it can translate the content to specific target languages using the 
<a href="https://rapidapi.com/microsoft-azure-org-microsoft-cognitive-services/api/microsoft-translator-text">Microsoft Translator Text</a> API

## How to Use

1. The project uses <a href="https://maven.apache.org/">Maven</a>. To built and test the project you can use:

   ```bash
   mvn clean install
   ```

2. To run it execute the JAR file. You will be prompted to enter the URLs to crawl, the depth of websites to crawl, the target language code (e.g., en, de, fr, it, es, or none), and the domain(s) to be crawled.

   ```bash
   java -jar target/web-crawler.jar
   ```

3. The crawler will generate an out.md file with the results
4. Example results can be viewed in export_orf_at_depth_1.md and export_orf_at_depth_2_en.md


