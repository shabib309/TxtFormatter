import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

    private static BufferedReader br;

    private static final ArrayList<String> list_of_bad_words = new ArrayList<String>() {
        static final long serialVersionUID = 1L;
        {
            try {
                br = new BufferedReader(new FileReader(".\\resources\\bad_words.txt"));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.contains("#")) {
                        add(line);
                    }
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private static final ArrayList<String> list_of_correct_words = new ArrayList<String>() {
        static final long serialVersionUID = 1L;
        {
            try {
                br = new BufferedReader(new FileReader(".\\resources\\good_words.txt"));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.contains("#")) {
                        add(line);
                    }
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private static final ArrayList<String> list_of_function_names = new ArrayList<String>() {
        static final long serialVersionUID = 1L;
        {
            try {
                br = new BufferedReader(new FileReader(".\\resources\\function_names.txt"));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.contains("#")) {
                        add(line);
                    }
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static void main(String[] args) {
        // Copied from:
        // https://stackoverflow.com/questions/3154488/how-do-i-iterate-through-the-files-in-a-directory-in-java
        Scanner input = new Scanner(System.in);
        System.out.println("Paste directory path here: ");
        File[] directoryListing = new File(input.nextLine()).listFiles();
        input.close();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.isDirectory()) {
                    for (File file : child.listFiles()) {
                        processFile(file);
                    }
                }
                processFile(child);
            }
        }
        System.out.println("Done");
    }

    private static void processFile(File file) {
        boolean bad_word_found = false;
        try {
            br = new BufferedReader(new FileReader(file));
            StringBuffer inputBuffer = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            inputBuffer.deleteCharAt(inputBuffer.length() - 1);
            br.close();
            String inputStr = inputBuffer.toString();
            for (String bad_word : list_of_bad_words) {
                if (inputStr.contains(bad_word)) {
                    for (String good_word : list_of_correct_words) {
                        if (bad_word.equalsIgnoreCase(good_word)) {
                            bad_word_found = true;
                            inputStr = inputStr.replace(bad_word, good_word);
                        }
                    }
                }
            }
            for (String function_name : list_of_function_names) {
                Pattern pattern = Pattern.compile(function_name + ";");
                Matcher matcher = pattern.matcher(inputStr);
                if (matcher.find()) {
                    for (String good_word : list_of_correct_words) {
                        if (function_name.equalsIgnoreCase(good_word)) {
                            inputStr = inputStr.replace(function_name + ";", good_word + "();");
                        }
                    }
                }
            }
            if (bad_word_found) {
                FileOutputStream fileOut = new FileOutputStream(file.getAbsolutePath());
                fileOut.write(inputStr.getBytes());
                fileOut.close();
            }
        } catch (IOException e) {
            return;
        }
    }
}