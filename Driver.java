/**
 * Driver class implements the program, filters book records from original files, and then displays on the console,
 * allowing users to see book lists and records without any syntax and semantic errors.
 * Name(s) and ID(s) Yihuan Liu 26966462; Rania Maoukout  40249281
 * COMP249
 * Assignment 3
 * Due Date 2023/03/29
 *
 * @authors: Yihuan Liu & Rania Maoukout
 * @version: 1.0
 */

// -----------------------------------------------------------
// Assignment 4

// Written by: Yihuan Liu 26966462; Rania Maoukout  40249281
// -----------------------------------------------------------

import java.io.*;
import java.util.Scanner;

public class Driver {
    public static void main(String[] args)  {
        try {
            do_part1();
        } catch (IOException | TooFewFieldsException | TooManyFieldsException | MissingFieldException |
                 UnknownGenreException e){
            e.printStackTrace();
        }

        try {
            do_part2();
        } catch (IOException | BadPriceException | BadIsbn10Exception | BadIsbn13Exception | BadIsbnException |
                 BadYearException e){
            e.printStackTrace();
        }

        try{
            do_part3();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * do_part1()  reads book records from a number of CSV-formatted text files, checking only for syntax errors, check if there are too many/few fields, a missing field or an unknown genre
     */
    public static void do_part1() throws TooManyFieldsException, TooFewFieldsException, MissingFieldException, UnknownGenreException, IOException {
        //first check if all 16 files exist
        //get file names
        BufferedReader br = null;
        BufferedWriter bw = null;
        String[] fileNames = new String[17];
        try {
            br = new BufferedReader(new FileReader("part1_input_file_names.txt"));
            for (int i = 0; i < 17; i++) {
                fileNames[i] = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //check if all files exist
        File[] csvFiles = new File[16];
        for (int i = 0; i < 16; i++) {
            csvFiles[i] = new File(fileNames[i + 1]);
            if (!csvFiles[i].exists()) {
                System.out.println("The file " + fileNames[i + 1] + " does not exist.");
            }
        }
        System.out.println("\nAll 16 files exist, will starting examine their syntax.\n");

        String[][][] bookContent;          //first dimension: year; second dimension: which book, third dimension: book info
        String[][][] booksNoSynError;
        String line = null;
        String[] tempLine;
        String[] tempContent;
        int count = 0;                    //number of comma in a line
        int synErrCount = 0, tooManyFieldsErr = 0, missingAuthor = 0, missingISBN = 0, missingGenre = 0, missingYear = 0, missingFieldsErr = 0, unknownGenreErr =0;

        //save the info for all book lists
        for (int index = 0; index < csvFiles.length; index++) {
            //validate files' content
            //count book numbers in one list
            int bookNb = 0;
            try{
                br = new BufferedReader(new FileReader(csvFiles[index]));
                while(br.readLine() != null){
                    bookNb++;
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
            finally {
                br.close();
            }
            bookContent = new String[16][bookNb][6];                //with missing fields
            booksNoSynError = new String[16][bookNb][6];            //with no syntax error

            if(csvFiles[index].length() != 0){
                //first split content by quotation and comma
                try {
                    br = new BufferedReader(new FileReader(csvFiles[index]));
                    line = br.readLine();
                    for(int lineNb = 0; lineNb < bookNb; lineNb++) {
                        try {
                            if (line.contains("\"")) {
                                tempLine = line.trim().split("\"");
                                for (int i = 0; i < tempLine[2].length(); i++) {
                                    if (tempLine[2].charAt(i) == ',') {
                                        count++;
                                    }
                                }
                                //too many fields
                                if (count > 5) {
                                    count = 0;
                                    throw new TooManyFieldsException();
                                }

                                //too few fields
                                if (count < 5) {
                                    count = 0;
                                    throw new TooFewFieldsException();
                                }

                                if (count == 5) {
                                    tempContent = tempLine[2].trim().split(",");
                                    for (int i = 0; i < tempContent.length; i++) {
                                        bookContent[index][lineNb][i] = tempContent[i];
                                    }
                                    bookContent[index][lineNb][0] = "\"" + tempLine[1] + "\"";
                                    if (tempContent.length < 6) {
                                        for (int j = tempContent.length; j < 6; j++) {
                                            bookContent[index][lineNb][j] = "\t"; // holds empty line for missing year
                                        }
                                    }
                                    count = 0;
                                }
                            } else {
                                for (int i = 0; i < line.length(); i++) {
                                    if (line.charAt(i) == ',') {
                                        count++;
                                    }
                                }
                                //too many fields
                                if (count > 5) {
                                    count = 0;
                                    throw new TooManyFieldsException();
                                }
                                //too few fields
                                if (count < 5) {
                                    count = 0;
                                    throw new TooFewFieldsException();
                                } else if (count == 5) {
                                    tempContent = line.trim().split(",");
                                    count = 0;

                                    for (int i = 0; i < tempContent.length; i++) {
                                        bookContent[index][lineNb][i] = tempContent[i];
                                    }
                                    if (tempContent.length < 6) {
                                        for (int j = tempContent.length; j < 6; j++) {
                                            bookContent[index][lineNb][j] = "\t";
                                        }
                                    }
                                    if (!bookContent[index][lineNb][0].contains("\"") && bookContent[index][lineNb][0].contains(",")) {
                                        //missing fields
                                        throw new MissingFieldException();
                                    }
                                }
                            }
                        } catch (TooManyFieldsException tme) {
                            //write to the syntax error file
                            try {
                                bw = new BufferedWriter(new FileWriter("syntax_error_file.txt", true));
                                bw.write("syntax error in file: " + csvFiles[index] + "\n========================\nError: too many fields\nRecord: " + line + "\n\n");
                                synErrCount++;
                                tooManyFieldsErr++;
                                line = br.readLine();
                                continue;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            }
                        } catch (TooFewFieldsException tme) {
                            //write to the syntax error file
                            try {
                                bw = new BufferedWriter(new FileWriter("syntax_error_file.txt", true));
                                bw.write("syntax error in file: " + csvFiles[index] + "\n========================\nError: too few fields\nRecord: " + line + "\n\n");
                                synErrCount++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            }
                        } catch (MissingFieldException mfe) {
                            //write to the syntax error file
                            try {
                                bw = new BufferedWriter(new FileWriter("syntax_error_file.txt", true));
                                bw.write("syntax error in file: " + csvFiles[index] + "\n========================\nError: missing fields\nRecord: " + line + "\n\n");
                                synErrCount++;
                                missingFieldsErr++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            }
                        }

                    
                
                        booksNoSynError[index][lineNb] = bookContent[index][lineNb];

                        //check title
                        if (bookContent[index][lineNb][0] == null || bookContent[index][lineNb][0].isBlank()) {
                            bookContent[index][lineNb][0] = "\t";
                            booksNoSynError[index][lineNb] = null;
                            try {
                                bw = new BufferedWriter(new FileWriter("syntax_error_file.txt", true));
                                bw.write("syntax error in file: " + csvFiles[index] + "\n========================\nError: missing title\nRecord: ");
                                for (int i = 0; i < 6; i++) {
                                    bw.write(bookContent[index][lineNb][i] + "\t");
                                }
                                bw.write("\n\n");
                                synErrCount++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            } 
                        }
                        //check author
                        else if (bookContent[index][lineNb][1] == null || bookContent[index][lineNb][1].isBlank()) {
                            bookContent[index][lineNb][1] = "\t";
                            booksNoSynError[index][lineNb] = null;
                            try {
                                bw = new BufferedWriter(new FileWriter("syntax_error_file.txt", true));
                                bw.write("syntax error in file: " + csvFiles[index] + "\n========================\nError: missing author\nRecord: ");
                                for (int i = 0; i < 6; i++) {
                                    bw.write(bookContent[index][lineNb][i] + "\t");
                                }
                                bw.write("\n\n");
                                synErrCount++;
                                missingAuthor++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            }
                        }
                        //check price
                        else if (bookContent[index][lineNb][2] == null || bookContent[index][lineNb][2].isBlank()) {
                            bookContent[index][lineNb][2] = "\t";
                            booksNoSynError[index][lineNb] = null;
                            try {
                                bw = new BufferedWriter(new FileWriter("syntax_error_file.txt", true));
                                bw.write("syntax error in file: " + csvFiles[index] + "\n========================\nError: missing price\nRecord: ");
                                for (int i = 0; i < 6; i++) {
                                    bw.write(bookContent[index][lineNb][i] + "\t");
                                }
                                bw.write("\n\n");
                                synErrCount++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            }
                        }
                        //check isbn
                        else if (bookContent[index][lineNb][3] == null || bookContent[index][lineNb][3].isBlank()) {
                            bookContent[index][lineNb][3] = "\t";
                            booksNoSynError[index][lineNb] = null;
                            try {
                                bw = new BufferedWriter(new FileWriter("syntax_error_file.txt", true));
                                bw.write("syntax error in file: " + csvFiles[index] + "\n========================\nError: missing isbn\nRecord: ");
                                for (int i = 0; i < 6; i++) {
                                    bw.write(bookContent[index][lineNb][i] + "\t");
                                }
                                bw.write("\n\n");
                                synErrCount++;
                                missingISBN++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            }
                        }
                        //check genre
                        else if (bookContent[index][lineNb][4] == null || bookContent[index][lineNb][4].isBlank()) {
                            bookContent[index][lineNb][4] = "\t";
                            booksNoSynError[index][lineNb] = null;
                            try {
                                bw = new BufferedWriter(new FileWriter("syntax_error_file.txt", true));
                                bw.write("syntax error in file: " + csvFiles[index] + "\n========================\nError: missing genre\nRecord: ");
                                for (int i = 0; i < 6; i++) {
                                    bw.write(bookContent[index][lineNb][i] + "\t");
                                }
                                bw.write("\n\n");
                                synErrCount++;
                                missingGenre++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            }
                        }

                        //check year
                        else if (bookContent[index][lineNb][5] == null || bookContent[index][lineNb][5].isBlank()) {
                            bookContent[index][lineNb][5] = "\t";
                            booksNoSynError[index][lineNb] = null;
                            try {
                                bw = new BufferedWriter(new FileWriter("syntax_error_file.txt", true));
                                bw.write("syntax error in file: " + csvFiles[index] + "\n========================\nError: missing year\nRecord: ");
                                for (int i = 0; i < 6; i++) {
                                    bw.write(bookContent[index][lineNb][i] + "\t");
                                }
                                bw.write("\n\n");
                                synErrCount++;
                                missingYear++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            }
                        }
                        //write books without syntax errors into csv file
                        if (booksNoSynError[index][lineNb] != null) {
                            //CCB
                            switch (booksNoSynError[index][lineNb][4]) {
                                case "CCB" -> {
                                    try {
                                        bw = new BufferedWriter(new FileWriter("Cartoons_Comics_Books.csv", true));
                                        writeIntoCSV(bw, booksNoSynError[index][lineNb]);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        bw.close();
                                    }
                                }

                                //HCB: Hobbies_Collectibles_Books.csv
                                case "HCB" -> {
                                    try {
                                        bw = new BufferedWriter(new FileWriter("Hobbies_Collectibles_Books.csv", true));
                                        writeIntoCSV(bw, booksNoSynError[index][lineNb]);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        bw.close();
                                    }
                                }

                                //MTV: Movies_TV.csv
                                case "MTV" -> {
                                    try {
                                        bw = new BufferedWriter(new FileWriter("Movies_TV.csv", true));
                                        writeIntoCSV(bw, booksNoSynError[index][lineNb]);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        bw.close();
                                    }
                                }

                                //MRB: Music_Radio_Books.csv
                                case "MRB" -> {
                                    try {
                                        bw = new BufferedWriter(new FileWriter("Music_Radio_Books.csv", true));
                                        writeIntoCSV(bw, booksNoSynError[index][lineNb]);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        bw.close();
                                    }
                                }

                                //NEB: Nostalgia_Eclectic_Books.csv
                                case "NEB" -> {
                                    try {
                                        bw = new BufferedWriter(new FileWriter("Nostalgia_Eclectic_Books.csv", true));
                                        writeIntoCSV(bw, booksNoSynError[index][lineNb]);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        bw.close();
                                    }
                                }

                                //OTR: Old_Time_Radio.csv
                                case "OTR" -> {
                                    try {
                                        bw = new BufferedWriter(new FileWriter("Old_Time_Radio.csv", true));
                                        writeIntoCSV(bw, booksNoSynError[index][lineNb]);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        bw.close();
                                    }
                                }

                                //SSM:  Sports_Sports_Memorabilia.csv
                                case "SSM" -> {
                                    try {
                                        bw = new BufferedWriter(new FileWriter("Sports_Sports_Memorabilia.csv", true));
                                        writeIntoCSV(bw, booksNoSynError[index][lineNb]);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        bw.close();
                                    }
                                }

                                //TPA: Trains_Planes_Automobiles.csv
                                case "TPA" -> {
                                    try {
                                        bw = new BufferedWriter(new FileWriter("Trains_Planes_Automobiles.csv", true));
                                        writeIntoCSV(bw, booksNoSynError[index][lineNb]);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        bw.close();
                                    }
                                }
                                default -> {
                                    try {
                                        throw new UnknownGenreException();
                                    } catch (UnknownGenreException ung) {
                                        //write to the syntax error file
                                        try {
                                            bw = new BufferedWriter(new FileWriter("syntax_error_file.txt", true));
                                            bw.write("syntax error in file: " + csvFiles[index] + "\n========================\nError: unknown genre error\nRecord: " + line + "\n\n");
                                            synErrCount++;
                                            unknownGenreErr++;
                                            line = br.readLine();
                                            continue;
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        } finally {
                                            bw.close();
                                        }
                                    }
                                }
                            }
                        }
                            line = br.readLine();
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("The number of total syntax error is: " + synErrCount);
        System.out.println("\nThe number of too many fields error is: " + tooManyFieldsErr);
        System.out.println("The number of too few fields error is: " + missingFieldsErr);
        System.out.println("The number of missing author error is: " + missingAuthor);
        System.out.println("The number of missing isbn error is: " + missingISBN);
        System.out.println("The number of missing genre error is: " + missingGenre);
        System.out.println("The number of missing year error is: " + missingYear);
        System.out.println("The number of missing fields error is: " + missingFieldsErr);
        System.out.println("The number of total missing fields is: " + (missingAuthor + missingISBN + missingGenre + missingYear + missingFieldsErr));
        System.out.println("The number of unknown genre error is: " + unknownGenreErr);
        System.out.println("--------------------------------------------This is the end of part 1--------------------------------------------");
    }


private static void writeIntoCSV(BufferedWriter bw, String[] book) throws IOException {
        for(int i = 0; i < book.length; i++){
            bw.write(book[i] + "\n");
        }
}


    /**
     * do_part2() validates semantics, reads the genre files each into arrays of Book objects, then serializes the arrays of Book objects each into binary files.
     */

    public static void do_part2() throws FileNotFoundException, IOException, BadIsbn10Exception, BadIsbn13Exception, BadIsbnException, BadPriceException, BadYearException {
        //read from csv file and check semantics, write the good ones into binary files
        String[][][] bookRecord;
        Book[][] goodBookRecord;
        Book[][] bookRecordForP2;
        Scanner sc = null;
        BufferedWriter bw = null;
        ObjectOutputStream oos = null;
        int lineCount = 0, bookCount = 0, semErrCount = 0, priceErr = 0, yearErr = 0, isbn10Err = 0, isbn13Err = 0, isbnErr = 0;
        String title;
        String author;
        String price;
        String isbn;
        String genre;
        String year;
        File[] csvFiles = new File[8];
        String[] fileNames = new String[8];

        fileNames[0] = "Cartoons_Comics_Books.csv";
        fileNames[1] ="Hobbies_Collectibles_Books.csv";
        fileNames[2] = "Movies_TV.csv";
        fileNames[3] = "Music_Radio_Books.csv";
        fileNames[4] = "Nostalgia_Eclectic_Books.csv";
        fileNames[5] = "Old_Time_Radio.csv";
        fileNames[6] = "Sports_Sports_Memorabilia.csv";
        fileNames[7] = "Trains_Planes_Automobiles.csv";
        for(int i = 0; i < 8; i++){
            csvFiles[i] = new File(fileNames[i]);
        }

        for (int index = 0; index < csvFiles.length; index++) {
            try {
                sc = new Scanner(csvFiles[index]);
                while (sc.hasNextLine()) {
                    ++lineCount;
                    sc.nextLine();
                }
                bookCount = lineCount / 6;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                sc.close();
            }
            bookRecord = new String[8][bookCount][6];
            goodBookRecord = new Book[8][bookCount];
            try {
                sc = new Scanner(csvFiles[index]);
                while(sc.hasNextLine()) {
                    for (int i = 0; i < bookCount; i++) {
                        goodBookRecord[index][i] = new Book();

                        title = sc.nextLine();
                        author = sc.nextLine();
                        price = sc.nextLine();
                        isbn = sc.nextLine();
                        genre = sc.nextLine();
                        year = sc.nextLine();

                        bookRecord[index][i][0] = title;
                        bookRecord[index][i][1] = author;
                        bookRecord[index][i][2] = price;
                        bookRecord[index][i][3] = isbn;
                        bookRecord[index][i][4] = genre;
                        bookRecord[index][i][5] = year;

                        try {
                            if (testPrice(price) && testISBN(isbn) && testYear(year)) {
                                goodBookRecord[index][i].setTitle(bookRecord[index][i][0]);
                                goodBookRecord[index][i].setAuthor(bookRecord[index][i][1]);
                                goodBookRecord[index][i].setPrice(Double.parseDouble(bookRecord[index][i][2]));
                                goodBookRecord[index][i].setIsbn(bookRecord[index][i][3]);
                                goodBookRecord[index][i].setGenre(bookRecord[index][i][4]);
                                goodBookRecord[index][i].setYear(Integer.parseInt(bookRecord[index][i][5]));
                            } else if (!testPrice(price)) {
                                goodBookRecord[index][i] = null;
                                priceErr++;
                                throw new BadPriceException();
                            } else if (!testISBN(isbn)) {
                                if (isbn.length() == 10) {
                                    goodBookRecord[index][i] = null;
                                    isbn10Err++;
                                    throw new BadIsbn10Exception();
                                } else if (isbn.length() == 13) {
                                    goodBookRecord[index][i] = null;
                                    isbn13Err++;
                                    throw new BadIsbn13Exception();
                                }
                                else{
                                    goodBookRecord[index][i] = null;
                                    isbnErr++;
                                    throw new BadIsbnException();
                                }
                            } else if (!testYear(year)) {
                                goodBookRecord[index][i] = null;
                                yearErr++;
                                throw new BadYearException();
                            }
                            else {
                                goodBookRecord[index][i] = null;
                                semErrCount++;
                            }
                        }
                        catch (BadPriceException e){
                            try {
                                bw = new BufferedWriter(new FileWriter("semantic_error_file.txt", true));
                                bw.write("semantic error in file: " + csvFiles[index] + "\n========================\nError: invalid price\n");
                                for(int j =0; j < 6; j++){
                                    bw.write(bookRecord[index][i][j] + "\t");
                                }
                                bw.write("\n\n");
                                semErrCount++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            }
                        } catch (BadIsbn10Exception e){
                            try {
                                bw = new BufferedWriter(new FileWriter("semantic_error_file.txt", true));
                                bw.write("semantic error in file: " + csvFiles[index] + "\n========================\nError: invalid 10-digit isbn\nRecord: ");
                                for(int j =0; j < 6; j++){
                                    bw.write(bookRecord[index][i][j] + "\t");
                                }
                                bw.write("\n\n");
                                semErrCount++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            }

                        } catch (BadIsbn13Exception e){
                            try {
                                bw = new BufferedWriter(new FileWriter("semantic_error_file.txt", true));
                                bw.write("semantic error in file: " + csvFiles[index] + "\n========================\nError: invalid 13-digit isbn\nRecord: ");
                                for(int j =0; j < 6; j++){
                                    bw.write(bookRecord[index][i][j] + "\t");
                                }
                                bw.write("\n\n");
                                semErrCount++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            }

                        } catch (BadIsbnException e){
                            try {
                                bw = new BufferedWriter(new FileWriter("semantic_error_file.txt", true));
                                bw.write("semantic error in file: " + csvFiles[index] + "\n========================\nError: invalid isbn\nRecord: ");
                                for(int j =0; j < 6; j++){
                                    bw.write(bookRecord[index][i][j] + "\t");
                                }
                                bw.write("\n\n");
                                semErrCount++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            }
                        } catch (NumberFormatException e){
                            e.printStackTrace();
                        } catch (BadYearException e){
                            try {
                                bw = new BufferedWriter(new FileWriter("semantic_error_file.txt", true));
                                bw.write("semantic error in file: " + csvFiles[index] + "\n========================\nError: invalid year\nRecord: ");
                                for(int j =0; j < 6; j++){
                                    bw.write(bookRecord[index][i][j] + "\t");
                                }
                                bw.write("\n\n");
                                semErrCount++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                bw.close();
                            }
                        }
                    }

                    int nullNb = 0;
                    for(int m = 0; m < goodBookRecord[index].length; m++){
                        if(goodBookRecord[index][m] == null){
                            nullNb++;
                        }
                    }

                    bookRecordForP2 = new Book[8][goodBookRecord[index].length-nullNb];
                    for(int m=0, n=0; m< goodBookRecord[index].length; m++){
                        if(goodBookRecord[index][m] != null){
                            bookRecordForP2[index][n] = goodBookRecord[index][m];
                            n++;
                        }
                    }

                    for(int m = 0; m < bookRecordForP2[index].length; m++){
                        //write into binary files
                        try{
                            oos = new ObjectOutputStream(new FileOutputStream(fileNames[index]+".ser"));
                            if(bookRecordForP2[index][m] != null){
                                oos.writeObject(bookRecordForP2[index]);
                            }
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                        finally {
                            oos.close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                sc.close();
            }

        lineCount = 0;
        }
        System.out.println("The number of total semantic error is: " + semErrCount);
        System.out.println("\nThe number of invliad price error: " + priceErr + "\nThe number of invalid isbn10 error: " + isbn10Err + "\nThe number of invalid isbn13 error: "
                + isbn13Err + "\nThe number of invalid isbn error: " + isbnErr + "\nThe number of invalid year error: " + yearErr);
        System.out.println("--------------------------------------------This is the end of part 2--------------------------------------------\n\n");
    }

    /**
     * It tests the validity of price. It should be non-negative
     * @param p passes a String of price
     * @return a boolean value of the validity of price
     * @throws BadPriceException
     */
    private static boolean testPrice(String p) throws BadPriceException {
        if(p == null || Double.parseDouble(p) < 0.0) {
            return false;
        }
        else return true;
    }

    /**
     * It tests the validity of year, it should be within [1995,2010]
     * @param y passes a String of year
     * @return a boolean of validity of the year
     * @throws BadYearException
     */
    private static boolean testYear(String y) throws BadYearException{
        if(Integer.parseInt(y) >= 1995 && Integer.parseInt(y) <= 2010){
            return true;
        }
        else return false;
    }

    /**
     * It tests whether the isbn is valid or not
     * @param isbn passes a String of isbn. It should be either 10-digit or 13-digit, and meet corresponding requirements.
     * @return a boolean value of the validity of isbn
     * @throws BadIsbn10Exception
     * @throws BadIsbn13Exception
     * @throws BadIsbnException
     */
    private static boolean testISBN(String isbn) throws BadIsbn10Exception, BadIsbn13Exception, BadIsbnException{
        int digit = isbn.length();
            int isbnNb = 0;
            if(digit != 10 && digit != 13){
                return false;
            }
            else if (digit == 10) {
                int[] num = new int[10];
                for (int j = 0; j < 10; j++) {
                    num[j] = Character.getNumericValue(isbn.charAt(j));
                }
                if (isbn.charAt(9) == 'X') {
                   num[9] =10;
                } else {
                        num[9] = Character.getNumericValue(isbn.charAt(9));
                }
                //validate the 10-digit isbn
                for (int m = 0; m < 10; m++) {
                    isbnNb += num[m]*(10-m);
                }
                //not valid if it is not a multiple of 11
                if ((isbnNb % 11) != 0) {
                    return false;
                }
                else
                    return true;
            }
            else if (digit == 13) {
            int[] num = new int[13];
            for (int j = 0; j < 13; j++) {
                num[j] = Character.getNumericValue(isbn.charAt(j));
            }
            //validate the 13-digit isbn
            for (int m = 0; m < 13; m++) {
                if(m%2 != 0){
                    num[m] *= 3;
                }
                isbnNb += num[m];
            }
            //not valid if it is not a multiple of 10
            if ((isbnNb % 10) != 0) {
                return false;
            }
            else
                return true;
            }
            else return false;
    }


    /**
     * do_part3() reads each of the 8 binary files created in do_part2(), deserializes each book record in each file in an array of books
     * It also provides an interactive program for the user to choose a file, and navia to allow the user to navigate the arrays.
     */
    public static void do_part3() throws IOException {
        int totalBooks = 0;
        Scanner key = new Scanner(System.in);
        ObjectInputStream ois = null;
        Book[][] books = new Book[8][200];
        File[] binaryFiles = new File[8];
        int[] fileRecords = new int[8];
        String[] fileNames = new String[8];
        fileNames[0] = "Cartoons_Comics_Books.csv.ser";
        fileNames[1] = "Hobbies_Collectibles_Books.csv.ser";
        fileNames[2] = "Movies_TV.csv.ser";
        fileNames[3] = "Music_Radio_Books.csv.ser";
        fileNames[4] = "Nostalgia_Eclectic_Books.csv.ser";
        fileNames[5] = "Old_Time_Radio.csv.ser";
        fileNames[6] = "Sports_Sports_Memorabilia.csv.ser";
        fileNames[7] = "Trains_Planes_Automobiles.csv.ser";

        for (int i = 0; i < 8; i++) {
            binaryFiles[i] = new File(fileNames[i]);
        }

        for (int index = 0; index < 8; index++) {
            try {
                ois = new ObjectInputStream(new FileInputStream(binaryFiles[index]));
                while (true) {
                    try {
                        books[index] = (Book[]) ois.readObject();
                    } catch (EOFException e) {
                        ois.close();
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                ois.close();
            }
            fileRecords[index] = books[index].length;
            totalBooks += fileRecords[index];
        }

        System.out.println("Total books created without syntax or semantic errors: " + totalBooks + "\n\n");

        System.out.println("Welcome to the book system designed by Yihuan and Rania. We hope you will have fun :)\n");
        showMainMenu(0);
        int bookNb = 1, currentIndex = 0, lastIndex;
        String n;
        String choice = key.next();

        while(!choice.equals("x")){
            if(choice.equals("v")){
            //show books in the book array, by default, it will show the first one
                System.out.println("viewing: " + fileNames[bookNb-1] + "\t(" + fileRecords[bookNb-1] + " records)");
                System.out.println("Please enter the number of books you would like to see. You can enter any integers. " +
                        "\nPositive integers means to show the books after the current one," +
                        "\nnegative integers means to show the books before the current one, " +
                        "\nand 0 means to end the session: ");

                n = key.next();          //number of books to display
                while(Integer.parseInt(n) != 0){
                    if(Integer.parseInt(n) > 0){
                        if((currentIndex + Integer.parseInt(n) ) <= books[bookNb-1].length){
                            for(int i = currentIndex; i < (Integer.parseInt(n) + currentIndex); i++){
                                System.out.println(books[bookNb-1][i]);
                            }
                            currentIndex = currentIndex + Integer.parseInt(n) - 1;
                            System.out.println("Please enter the number of books you would like to see:");
                        }
                        else{
                            System.out.println("End of the file has been reached. Please enter another number.");
                        }
                        n = key.next();
                    }

                    else if(Integer.parseInt(n) < 0){
                        if((currentIndex + Integer.parseInt(n) ) >= 0){
                            lastIndex = currentIndex;
                            currentIndex = currentIndex + Integer.parseInt(n) + 1;
                            for(int i = currentIndex; i < lastIndex + 1; i++){
                                System.out.println(books[bookNb-1][i]);
                            }
                            System.out.println("Please enter the number of books you would like to see:");
                        }
                        else{
                            System.out.println("Beginning of the file has been reached. Please enter another number.");
                        }
                        n = key.next();
                    }
                }
                showMainMenu(0);
                choice = key.next();
            }
            else if (choice.equals("s")) {
                //show sub menu
                showSubMenu();
                bookNb = key.nextInt();                 //user  enters book number
                if(bookNb == 9){                        //go back to main menu and display default message, if user enters 9
                    bookNb = 1;
                }
                showMainMenu(bookNb-1);         //to view or select another file
                choice = key.next();
            }

        }
        //user enters x, exit the system
        System.out.println("Thank you for using the system, hope you enjoyed it. Looking forward to seeing you again!");
            key.close();
        }

        /**
     * It displays the main menu, allowing users to view the selected book, or view book list, or exit the program
     */
    private static void showMainMenu (int fileIndex) {
        String[] fileRecords = new String[8];
        fileRecords[0] = "Cartoons_Comics_Books.csv.ser (26 records)";
        fileRecords[1] = "Hobbies_Collectibles_Books.csv.ser (36 records)";
        fileRecords[2] = "Movies_TV.csv.ser (717 records)";
        fileRecords[3] = "Music_Radio_Books.csv.ser (502 records)";
        fileRecords[4] = "Nostalgia_Eclectic_Books.csv.ser (52 records)";
        fileRecords[5] = "Old_Time_Radio.csv.ser (8 records)";
        fileRecords[6] = "Sports_Sports_Memorabilia.csv.ser (189 records)";
        fileRecords[7] = "Trains_Planes_Automobiles.csv.ser (37 records)";

        System.out.println("--------------------------------------------------------------------------\n" +
                "                            Main Menu                       \n" +
                "--------------------------------------------------------------------------\n" +
                "v View the selected file:" + fileRecords[fileIndex] + "\n" +
                "s Select a file to view\n" +
                "x Exit\n" +
                "--------------------------------------------------------------------------\n\n" +
                "Enter Your Choice:");
    }
/**
 * It displays the sub menu containing 8 book titles and number of records
 */
    private static void showSubMenu(){
        String[] fileRecords = new String[8];
        fileRecords[0] = "Cartoons_Comics_Books.csv.ser         (26 records)";
        fileRecords[1] = "Hobbies_Collectibles_Books.csv.ser    (36 records)";
        fileRecords[2] = "Movies_TV.csv.ser                     (717 records)";
        fileRecords[3] = "Music_Radio_Books.csv.ser             (502 records)";
        fileRecords[4] = "Nostalgia_Eclectic_Books.csv.ser      (52 records)";
        fileRecords[5] = "Old_Time_Radio.csv.ser                (8 records)";
        fileRecords[6] = "Sports_Sports_Memorabilia.csv.ser     (189 records)";
        fileRecords[7] = "Trains_Planes_Automobiles.csv.ser     (37 records)";
        System.out.println("--------------------------------------------------------------------------\n                            File Sub-Menu                       " +
                "\n--------------------------------------------------------------------------");
        for (int i = 1; i < 9; i++) {
            System.out.printf("%d %s %-40s %s", i, "\t", fileRecords[i - 1], "\n");
        }
        System.out.printf("%d %s %s", 9, "\t", "Exit");
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("Enter Your Choice: ");
    }
}

