//COLLABORATOR - GPT5

package sets;
import java.util.*;
import java.io.*;

public class DuplicateWords {

 public static String cleanString(String originalString) {
     String cleanedString = originalString.replaceAll("\\p{Punct}", "");
     cleanedString = cleanedString.replaceAll("[“’”]+", "");
     return cleanedString;
 }

 public static void main(String[] args) throws IOException {

     Set<String> singles = new HashSet<>();    // words seen exactly once
     
     
     Set<String> duplicates = new HashSet<>(); // words seen 2+ times

     File file = new File("little_women.txt"); // file is in project root

     try (Scanner sc = new Scanner(file, "UTF-8")) {
         while (sc.hasNextLine()) {
             String line = sc.nextLine();
             String[] tokens = line.split("\\s+");

             for (String t : tokens) {
                 String w = cleanString(t).toLowerCase(Locale.ROOT).trim();
                 if (w.isEmpty()) continue;

                 if (duplicates.contains(w)) {
                     continue; // already counted as duplicate
                 }
                 if (!singles.add(w)) { // which means it was already occured once
                	 
                     singles.remove(w);
                     duplicates.add(w);
                 }
             }
         }
     }

     System.out.println("number of duplicate words: " + duplicates.size());
     System.out.println("number of single words: " + singles.size());
 }
}


