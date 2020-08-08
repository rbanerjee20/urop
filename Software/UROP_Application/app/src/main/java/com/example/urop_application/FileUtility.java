package com.example.urop_application;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

public class FileUtility {
    private static final String TAG = "FileUtility";

    private static final String trackingFileName = "TrackingFile.txt";
    private static final int START_STOP_ID = 0;
    private static final int GEOFENCE_TEST_ID = 50;
    private static final double[] ids = new double[]
            {1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6};
    private static final String COMMA_DELIMITER = ",";


    public static void writeToTrackingFile(MainActivity.TrackingActivity trackType, String message, Context context) {
        String timestampString = Calendar.getInstance().getTime().toString();
        double uniqueID;
        if (trackType.equals(MainActivity.TrackingActivity.TRACKING_START_STOP)) {
            uniqueID = START_STOP_ID;
        } else if (trackType.equals(MainActivity.TrackingActivity.GEOFENCE_TEST)) {
            uniqueID = GEOFENCE_TEST_ID;
        } else {
            uniqueID = ids[trackType.ordinal()];
        }
        String data = timestampString + ", Unique ID: " + uniqueID + ", Message: " + message + "\n";
        writeToInternalFile(data, context);
    }

    //  Function for writing to file from Stack Overflow
    public static void writeToInternalFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(trackingFileName, Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    //  Function for reading from file from Stack Overflow
    public static String readFromInternalFile(Context context, String fileName) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                    stringBuilder.append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
//                Log.d(TAG, ret);
                return ret;
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    /* Neither of these work */
    public static void clearFile(Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(trackingFileName, Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

//        try {
//            new PrintWriter(trackingFileName).close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

//        File temp = new File(trackingFileName);
//        if (temp.exists()) {
//            RandomAccessFile raf = null;
//            try {
//                raf = new RandomAccessFile(temp, "rw");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            try {
//                assert raf != null;
//                raf.setLength(0);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }


    }

    public static List<String> readFromProc(String fileName) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader mounts = new BufferedReader(new FileReader(fileName));
            String line;

            /* Ignore the first line with headings */
            mounts.readLine();
            while ((line = mounts.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find " + fileName);
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading " + fileName);
        }
//        Log.d(TAG, "TCPbutton: \n" + connections);
        return lines;
    }

//    public static List<List<String>> readFromCSV(String fileName) {
//        List<List<String>> records = new ArrayList<>();
//        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] values = line.split(COMMA_DELIMITER);
//                records.add(Arrays.asList(values));
//                Log.d(TAG, Arrays.toString(values));
//            }
//        } catch (IOException e) {
//            Log.d(TAG, "Ran into problems reading " + fileName);
//        }
//        return records;
//    }

    public static List<List<String>> readFromCSV(String path, Context context) {
        Reader reader;
        try {
            List<List<String>> info = new ArrayList<>();
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(path)));
            CSVReader csvReader = new CSVReader(reader);
            for (int i = 0; i < 101; i++) {
                String[] nextRecord = csvReader.readNext();
                info.add(Arrays.asList(nextRecord));
                Log.d(TAG, "readFromCSV: " + Arrays.toString(nextRecord));
            }
            return info;
        } catch (IOException e) {
            Log.e(TAG, "readUsingOpenCSV: ", e);
        }
        return null;
    }


    public static void lineTokenizer(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, ": ");
        List<String> tokens = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String nextToken = tokenizer.nextToken();
            tokens.add(nextToken);
            Log.d(TAG, "lineTokenizer: " + nextToken);
        }
//        return new TCP(tokens);
    }

    public static String getTrackingFileName() {
        return trackingFileName;
    }
}
