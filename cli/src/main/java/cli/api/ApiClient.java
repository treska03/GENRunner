package cli.api;

import cli.dto.*;
import cli.model.Filters;
import cli.model.OutputType;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/experiments";
    private final HttpConnectionManager connectionManager;
    private final JsonHandler jsonHandler;
    private final ApiService apiService;

    public ApiClient() {
        this.connectionManager = new HttpConnectionManager();
        this.jsonHandler = new JsonHandler();
        this.apiService = new ApiService();
    }

    public void startExperiment(CreateExperimentRequestDto request) {
        try {
            HttpURLConnection conn = connectionManager.createConnection(BASE_URL, "POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.getOutputStream().write(jsonHandler.toJson(request).getBytes());

            apiService.handleResponse(conn, conn.getResponseCode(), "Experiment started successfully!");
        } catch (IOException e) {
            System.out.println("Failed to start experiment: " + e.getMessage());
        }
    }

    public void createOrAddGroup(GroupedExperimentRequestDto request){
        try {
            HttpURLConnection connection = connectionManager.createConnection(BASE_URL + "/group","POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.getOutputStream().write(jsonHandler.toJson(request).getBytes());

            apiService.handleResponse(connection,connection.getResponseCode(),"Experiment successfully added to group");
        } catch (IOException e) {
            System.out.println("Failed to creating/adding group experiment: " + e.getMessage());
        }
    }

    public void listExperiments() {
        try {
            HttpURLConnection conn = connectionManager.createConnection(BASE_URL, "GET");
            apiService.handleListExperimentsResponse(conn);
        } catch (IOException e) {
            System.out.println("Failed to list experiments: " + e.getMessage());
        }
    }

    public void listFilteredExperiments(Filters filters) {
        try {
            String url = apiService.buildUrlWithFilters(BASE_URL + "/filtered", filters);
            HttpURLConnection conn = connectionManager.createConnection(url, "GET");
            apiService.handleListExperimentsResponse(conn);
        } catch (IOException e) {
            System.out.println("Failed to list filtered experiments: " + e.getMessage());
        }
    }

    public void getStatus(String experimentId) {
        try {
            HttpURLConnection conn = connectionManager.createConnection(BASE_URL + "/" + experimentId + "/status", "GET");
            apiService.handleGetStatusResponse(conn);
        } catch (IOException e) {
            System.out.println("Failed to fetch experiment status: " + e.getMessage());
        }
    }

    public void getResults(String experimentId) {
        try {
            HttpURLConnection conn = connectionManager.createConnection(BASE_URL + "/" + experimentId + "/results", "GET");
            apiService.handleGetResultsResponse(conn);
        } catch (IOException e) {
            System.out.println("Failed to fetch experiment results: " + e.getMessage());
        }
    }

    public void getAggregatedResults(Filters filters, OutputType output) {
        String url;
        HttpURLConnection conn;

        try {
            switch (output) {
                case DEFAULT -> {
                    url = apiService.buildUrlWithFilters(BASE_URL + "/aggregate", filters);
                    conn = connectionManager.createConnection(url, "GET");
                    apiService.handleAggregatedResultsResponse(conn);
                }
                case CSV -> {
                    url = apiService.buildUrlWithFilters(BASE_URL + "/aggregate/csv", filters);
                    conn = connectionManager.createConnection(url, "GET");
                    apiService.saveFileFromResponse(conn, "aggregated_results.csv");
                }
                case CHART -> {
                    url = apiService.buildUrlWithFilters(BASE_URL + "/aggregate/chart", filters);
                    conn = connectionManager.createConnection(url, "GET");
                    apiService.saveFileFromResponse(conn, "aggregated_results.png");
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to process aggregated results: " + e.getMessage());
        }
    }

    public void getExperimentGroup(String groupName){
        try {
            HttpURLConnection conn = connectionManager.createConnection(BASE_URL + "/group/" + groupName,"GET");
            apiService.handleListExperimentsResponse(conn);
        } catch (IOException e) {
            System.out.println("Failed to view experiment group: " + e.getMessage());
        }

    }

    public void deleteExperimentById(String experimentId) {
        try {
            HttpURLConnection conn = connectionManager.createConnection(BASE_URL + "/" + experimentId, "DELETE");
            apiService.handleResponse(conn, conn.getResponseCode(), "Experiment deleted successfully!");
        } catch (IOException e) {
            System.out.println("Failed to delete experiment: " + e.getMessage());
        }
    }

    public void deleteExperimentGroup(String groupName){
        try {
            HttpURLConnection connection = connectionManager.createConnection(BASE_URL + "/group/" + groupName, "DELETE");
            apiService.handleResponse(connection,connection.getResponseCode(), "Experiment group deleted successfully!");
        } catch (IOException e) {
            System.out.println("Failed to delete experiment: " + e.getMessage());
        }
    }


}