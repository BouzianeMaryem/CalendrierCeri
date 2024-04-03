package com.example.calendrier_ceri_ines_maryem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;


public class Preferences {
    private static String getPreferencesFileName(String userId) {
        return "userPreferences_" + userId + ".txt";
    }

    public static void savePreferences(String userId, Map<String, String> preferences) {
        String fileName = getPreferencesFileName(userId);
        try (PrintWriter out = new PrintWriter(fileName)) {
            for (Map.Entry<String, String> entry : preferences.entrySet()) {
                out.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> loadPreferences(String userId) {
        String fileName = getPreferencesFileName(userId);
        Map<String, String> preferences = new HashMap<>();
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(":");
                if (parts.length == 2) {
                    preferences.put(parts[0], parts[1]);
                }
            }
        } catch (FileNotFoundException e) {

        }
        return preferences;
    }
}