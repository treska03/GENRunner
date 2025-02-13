package cli.api;

import cli.dto.AggregatedResultsDto;
import cli.dto.ExperimentResponseDto;
import cli.dto.ExperimentResultsResponseDto;
import cli.dto.ExperimentStatusResponseDto;
import cli.model.Filter;
import cli.model.Filters;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class ApiService {
    private final JsonHandler jsonHandler;
    private final ResponseFormatter responseFormatter;

    public ApiService() {
        this.jsonHandler = new JsonHandler();
        this.responseFormatter = new ResponseFormatter();
    }

    public void handleResponse(HttpURLConnection conn, int responseCode, String successMessage) throws IOException {
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
            printResponse(conn);
            System.out.println(successMessage);
        } else {
            handleError(conn, responseCode);
        }
    }

    public void handleGetStatusResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            ExperimentStatusResponseDto response = jsonHandler.fromJson(conn.getInputStream(), ExperimentStatusResponseDto.class);
            System.out.println(responseFormatter.formatStatusResponse(response));
        } else {
            handleError(conn, responseCode);
        }
    }

    public void handleGetResultsResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            ExperimentResultsResponseDto response = jsonHandler.fromJson(conn.getInputStream(), ExperimentResultsResponseDto.class);
            System.out.println(responseFormatter.formatResultsResponse(response));
        } else {
            handleError(conn, responseCode);
        }
    }

    public void handleListExperimentsResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            List<ExperimentResponseDto> response = jsonHandler.fromJson(conn.getInputStream(), new TypeReference<List<ExperimentResponseDto>>() {});
            System.out.println(responseFormatter.formatListResponse(response));
        } else {
            handleError(conn, responseCode);
        }
    }

    public void handleAggregatedResultsResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            List<AggregatedResultsDto> response = jsonHandler.fromJson(conn.getInputStream(), new TypeReference<List<AggregatedResultsDto>>() {});
            System.out.println(responseFormatter.formatAggregatedResultsResponse(response));
        } else {
            handleError(conn, responseCode);
        }
    }

    public void handleError(HttpURLConnection conn, int responseCode) throws IOException {
        String errorMessage = getErrorMessage(conn);
        if (responseCode >= 400 && responseCode < 500) {
            System.out.println("Client error (" + responseCode + "): " + errorMessage);
        } else if (responseCode >= 500) {
            System.out.println("Server error (" + responseCode + "): " + errorMessage);
        } else {
            System.out.println("Unexpected response (" + responseCode + "): " + errorMessage);
        }
    }

    public String getErrorMessage(HttpURLConnection conn) throws IOException {
        try (Scanner scanner = new Scanner(conn.getErrorStream())) {
            StringBuilder errorMessage = new StringBuilder();
            while (scanner.hasNextLine()) {
                errorMessage.append(scanner.nextLine());
            }
            return errorMessage.toString();
        }
    }

    public void printResponse(HttpURLConnection conn) throws IOException {
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
    }

    public String buildUrlWithFilters(String baseUrl, Filters filters) throws IOException {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        List<Filter> filterList = filters.filters();
        if (!filterList.isEmpty()) {
            urlBuilder.append("?");
            for (Filter filter : filterList) {
                urlBuilder.append(URLEncoder.encode(filter.name(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(filter.value(), StandardCharsets.UTF_8))
                        .append("&");
            }
            urlBuilder.setLength(urlBuilder.length() - 1); // Remove the trailing '&'
        }
        return urlBuilder.toString();
    }

    public void saveFileFromResponse(HttpURLConnection conn, String fileName) throws IOException {
        try (InputStream in = conn.getInputStream();
             FileOutputStream out = new FileOutputStream(fileName)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            System.out.println("File saved as " + fileName);
        }
    }
}