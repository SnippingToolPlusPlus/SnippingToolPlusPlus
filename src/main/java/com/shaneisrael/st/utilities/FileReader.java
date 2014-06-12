package com.shaneisrael.st.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileReader
{
    public static String readFile(String pathname) throws IOException
    {
        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int) file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");

        try
        {
            while (scanner.hasNextLine())
            {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally
        {
            scanner.close();
        }
    }

    public static void writeFile(String path, String contents) throws FileNotFoundException
    {
        PrintWriter out = new PrintWriter(path);
        out.println(contents);
        out.flush();
        out.close();
    }
}
