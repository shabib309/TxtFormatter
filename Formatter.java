import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

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
        File[] directoryListing = new File("H:\\Repository\\mrd-al - Kopie\\Objects").listFiles();
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
    }

    private static boolean processFile(File file) {
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
        } catch (FileNotFoundException f) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bad_word_found;
    }
}