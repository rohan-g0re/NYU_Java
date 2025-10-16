//COLLABORATOR - Claude 4.5

package movies;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;




class MovieLineParseException extends RuntimeException {
    public MovieLineParseException(String message) {
        super(message);
    }
    
    public MovieLineParseException(String message, Throwable cause) {
        super(message, cause);
    }
}


public class MovieAnalyzer {
    
    /**
     * 
     * Doc Strings that for the MAIN FUNCTION WHICH CREATES THE MOVIE OBJECTS FROM ROW OF FILE
     * 
     * Creates a Movie object from a line of the CSV file
     * @param inline A line from the CSV file (tab-delimited)
     * @return A Movie object
     * @throws MovieLineParseException if the line cannot be parsed
     */
	
	
    public static Movie createMovie(String inline) {
        try {
        	
//        	split using commas
        	
            String[] parts = inline.split(",");
            
            
            // Check if we have all 4 fields
            if (parts.length != 4) {
                throw new MovieLineParseException("Line does not have 4 fields: " + inline);
            }
            
            // Parse the fields
            String title = parts[0].trim();
            int year = Integer.parseInt(parts[1].trim());
            String genre = parts[2].trim();
            double rating = Double.parseDouble(parts[3].trim());
            
            
            // Validate that fields are not empty
            if (title.isEmpty() || genre.isEmpty()) {
                throw new MovieLineParseException("Title or genre is empty: " + inline);
            }
            
            return new Movie(title, year, genre, rating);
            
        } catch (NumberFormatException e) {
            throw new MovieLineParseException("Invalid number format in line: " + inline, e);
        } catch (Exception e) {
            throw new MovieLineParseException("Error parsing line: " + inline, e);
        }
    }
    
    
    /**
     * 
     * Doc Strings for the MAIN FUNCTION WHICH RUNS CreateMovie for EVERY ROW
     * 
     * 
     * Reads movies from a file and returns them as an ArrayList
     * @param filename The name of the file to read
     * @return ArrayList of Movie objects
     */
    
    public static ArrayList<Movie> readMovies(String filename) {
        ArrayList<Movie> movies = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            
            while ((line = br.readLine()) != null) {
                lineNumber++;
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    Movie movie = createMovie(line);
                    movies.add(movie);
                } catch (MovieLineParseException e) {
                    // Print error message and continue
                    System.err.println("Skipping line " + lineNumber + ": " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return movies;
    }
    
    /**
     * Analyze function having 2 lambdas
     * 
     * 1. Descending order by rating
     * 2. Ascending order by genre
     */
    
    public static void analyze(ArrayList<Movie> movieList) {
        // Print sorted by rating (descending)
        System.out.println("========================================");
        System.out.println("MOVIES SORTED BY RATING (DESCENDING):");
        System.out.println("========================================");
        
        // Create a copy to avoid modifying original list
        ArrayList<Movie> ratingSort = new ArrayList<>(movieList);
        
        // Sort by rating descending using lambda
        ratingSort.sort((m1, m2) -> Double.compare(m2.getRating(), m1.getRating()));
        
        for (Movie movie : ratingSort) {
            System.out.println(movie);
        }

        System.out.println();

        // Print sorted by genre (ascending)
        System.out.println("========================================");
        System.out.println("MOVIES SORTED BY GENRE (ASCENDING):");
        System.out.println("========================================");
        
        // Create a copy to avoid modifying original list
        ArrayList<Movie> genreSort = new ArrayList<>(movieList);
        
        // Sort by genre ascending using lambda
        genreSort.sort((m1, m2) -> m1.getGenre().compareTo(m2.getGenre()));
        
        for (Movie movie : genreSort) {
            System.out.println(movie);
        }
    }
    

    
    public static void main(String[] args) {
        // Read movies from the file
        String filename = "movies.csv";  // Change to "movies_short.csv" for testing
        
        System.out.println("Reading movies from " + filename + "...\n");
        ArrayList<Movie> movies = readMovies(filename);
        
        System.out.println("\nSuccessfully loaded " + movies.size() + " movies.\n");
        
        // Analyze the movies
        analyze(movies);
    }

    
    
}