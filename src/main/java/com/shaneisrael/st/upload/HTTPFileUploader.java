package com.shaneisrael.st.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPFileUploader
{
    private static final String CRLF = "\r\n";
    private final String boundary;
    private final String endpoint;
    private final String userAgent;
    private String clientId;
    private String fileField;
    private File fileToUpload;

    private final Map<String, String> headers;
    private final Map<String, String> fields;

    private HttpURLConnection connection;
    private PrintWriter out;
    private OutputStream outputStream;

    public HTTPFileUploader(String endpoint, String userAgent)
    {
        boundary = generateBoundary();
        this.endpoint = endpoint;
        this.userAgent = userAgent;

        headers = new LinkedHashMap<>();
        fields = new LinkedHashMap<>();
    }

    public void setHeader(String fieldName, String value)
    {
        headers.put(fieldName, value);
    }

    public void setField(String fieldName, String value)
    {
        fields.put(fieldName, value);
    }

    public void setFile(String fieldName, File fileToUpload)
    {
        this.fileField = fieldName;
        this.fileToUpload = fileToUpload;
    }

    public void startUpload() throws IOException
    {
        URL url = new URL(endpoint);
        connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.setDoOutput(true); //needed for POST/PUT methods
        connection.setRequestProperty(
            "Content-Type", "multipart/form-data; boundary=" + boundary
            );
        connection.setRequestProperty("User-Agent", userAgent);
        connection.setRequestProperty("Authorization", "Client-ID " + clientId);
        outputStream = connection.getOutputStream();
        out = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        generateRequest(outputStream);
    }

    public String finish() throws IOException
    {
        StringBuilder response = new StringBuilder();

        out.append(CRLF).flush();
        out.append("--" + boundary + "--")
            .append(CRLF);
        out.close();

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream())
            );
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            response.append(line);
        }
        reader.close();
        connection.disconnect();

        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK)
        {
            System.err.println(response.toString());
            throw new IOException("Server returned non-OK status: " + status);
        }
        return response.toString();
    }

    private void generateRequest(OutputStream outputStream) throws IOException
    {
        out = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        for (Map.Entry<String, String> header : headers.entrySet())
        {
            addHeader(header.getKey(), header.getValue());
        }
        for (Map.Entry<String, String> field : fields.entrySet())
        {
            addField(field.getKey(), field.getValue());
        }
        addFile(fileField, fileToUpload);
    }

    private void addHeader(String fieldName, String value)
    {
        out.append(fieldName + ": " + value)
            .append(CRLF);
        out.flush();
    }

    private void addField(String fieldName, String value)
    {
        out.append("--" + boundary)
            .append(CRLF);
        out.append("Content-Disposition: form-data; name=\"" + fieldName + "\"")
            .append(CRLF);
        out.append("Content-Type: text/plain; charset=UTF-8")
            .append(CRLF);
        out.append(CRLF);
        out.append(value)
            .append(CRLF);
        out.flush();
    }

    private void addFile(String fieldName, File fileToUpload)
        throws IOException
    {
        String fileName = fileToUpload.getName();
        String contentType = Files.probeContentType(
            Paths.get(fileToUpload.getAbsolutePath())
            );

        out.append("--" + boundary)
            .append(CRLF);
        out.append("Content-Disposition: form-data")
            .append("; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"")
            .append(CRLF);
        out.append("Content-Type: " + contentType)
            .append(CRLF);
        out.append("Content-Transfer-Encoding: binary")
            .append(CRLF);
        out.append(CRLF);
        out.flush();

        FileInputStream inputStream = new FileInputStream(fileToUpload);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1)
        {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        out.append(CRLF);
        out.flush();
    }

    private static String generateBoundary()
    {
        return "===" + System.currentTimeMillis() + "===";
    }

    /**
     * Sets the Authorization header to Client-ID {clientId}
     * @param clientId
     */
    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }
}
