package cli;

import cli.api.ApiClient;
import cli.dto.CreateExperimentRequestDto;
import cli.dto.GroupedExperimentRequestDto;
import cli.model.Filter;
import cli.model.Filters;
import cli.model.OutputType;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Command(name = "genrunner-cli", mixinStandardHelpOptions = true, version = "genrunner-cli 0.1",
        description = "CLI for MOEA experiments",
        subcommands = {Client.StartCommand.class, Client.ListCommand.class, Client.ListFilteredCommand.class, Client.StatusCommand.class, Client.ResultsCommand.class, Client.AggregateCommand.class, Client.DeleteCommand.class, Client.GetGroupExperiment.class, Client.GroupExperiments.class})
public class Client implements Runnable {

    public static void main(String[] args) {
        new CommandLine(new Client()).execute(args);
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("Welcome to GENRunner CLI. Type 'exit' to quit.");
        System.out.println("Type '--help' to see available commands.");
        while (true) {
            System.out.print("> ");
            command = scanner.nextLine();
            if (command.equalsIgnoreCase("exit")) {
                break;
            }

            String[] cmdArgs = command.split(" ");
            CommandLine cmd = new CommandLine(new Client());
            cmd.setUnmatchedArgumentsAllowed(true); // Allow unmatched arguments
            try {
                int exitCode = cmd.execute(cmdArgs);
                if (exitCode != 0) {
                    System.out.println("Command failed with exit code " + exitCode);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        scanner.close();
    }

    @Command(name = "start", description = "Start a new experiment")
    static class StartCommand implements Runnable {
        @Option(names = "--algorithms", required = true, description = "Algorithms to use", split = ",")
        Set<String> algorithms;

        @Option(names = "--problems", required = true, description = "Problems to solve", split = ",")
        Set<String> problems;

        @Option(names = "--metrics", required = true, description = "Metrics to evaluate", split = ",")
        Set<String> metrics;

        @Option(names = "--budget", required = true, description = "Budget for the experiment")
        int budget;

        @Option(names = "--runs", required = true, description = "Number of runs for each algorithm")
        int runs;

        @Override
        public void run() {
            ApiClient apiClient = new ApiClient();
            CreateExperimentRequestDto request = new CreateExperimentRequestDto(algorithms, problems, metrics, budget, runs);
            apiClient.startExperiment(request);
        }
    }

    @Command(name = "group-add", description = "Group Experiments")
    static class GroupExperiments implements Runnable {

        @Option(names = "--name", description = "Experiment Group Name")
        String groupName;

        @Option(names = "--ids", description = "Experiment IDs to be grouped", split = ",")
        Set<String> ids;

        @Override
        public void run() {
            ApiClient apiClient = new ApiClient();
            GroupedExperimentRequestDto request = new GroupedExperimentRequestDto(groupName, ids);
            apiClient.createOrAddGroup(request);

        }
    }


    @Command(name = "group-get", description = "Get experiment group")
    static class GetGroupExperiment implements Runnable {

        @Option(names = "--name", description = "Group Name")
        String groupName;

        @Override
        public void run() {
            ApiClient apiClient = new ApiClient();
            apiClient.getExperimentGroup(groupName);
        }
    }

    @Command(name = "list", description = "List all experiments")
    static class ListCommand implements Runnable {
        @Override
        public void run() {
            ApiClient apiClient = new ApiClient();
            apiClient.listExperiments();
        }
    }

    @Command(name = "list-filtered", description = "List filtered experiments")
    static class ListFilteredCommand implements Runnable {
        @Option(names = "--algorithms", description = "Algorithms to filter by", split = ",")
        Set<String> algorithms;

        @Option(names = "--metrics", description = "Metrics to filter by", split = ",")
        Set<String> metrics;

        @Option(names = "--problems", description = "Problems to filter by", split = ",")
        Set<String> problems;

        @Option(names = "--group", description = "Add results to group")
        String groupName;

        @Override
        public void run() {
            ApiClient apiClient = new ApiClient();
            List<Filter> filterList = new ArrayList<>();
            if (algorithms != null && !algorithms.isEmpty()) {
                filterList.add(new Filter("algorithms", String.join(",", algorithms)));
            }
            if (metrics != null && !metrics.isEmpty()) {
                filterList.add(new Filter("metrics", String.join(",", metrics)));
            }
            if (problems != null && !problems.isEmpty()) {
                filterList.add(new Filter("problems", String.join(",", problems)));
            }
            if (groupName != null && !groupName.isEmpty()) {
                filterList.add(new Filter("groupName", groupName));
            }
            Filters filters = new Filters(filterList);
            apiClient.listFilteredExperiments(filters);
        }
    }

    @Command(name = "status", description = "Get status of an experiment")
    static class StatusCommand implements Runnable {
        @Option(names = "--id", required = true, description = "Experiment ID")
        String id;

        @Override
        public void run() {
            ApiClient apiClient = new ApiClient();
            apiClient.getStatus(id);
        }
    }

    @Command(name = "results", description = "Get results of an experiment")
    static class ResultsCommand implements Runnable {
        @Option(names = "--id", required = true, description = "Experiment ID")
        String id;

        @Override
        public void run() {
            ApiClient apiClient = new ApiClient();
            apiClient.getResults(id);
        }
    }

    @Command(name = "aggregate", description = "Get aggregated results from /experiments/aggregate")
    static class AggregateCommand implements Runnable {

        @Option(names = "--startDate", description = "Start date (ISO-8601 format), e.g. 2023-01-01T00:00:00Z")
        String startDate;

        @Option(names = "--endDate", description = "End date (ISO-8601 format), e.g. 2023-01-31T23:59:59Z")
        String endDate;

        @Option(names = "--algorithms", description = "Algorithms to filter by", split = ",")
        Set<String> algorithms;

        @Option(names = "--metrics", description = "Metrics to filter by", split = ",")
        Set<String> metrics;

        @Option(names = "--problems", description = "Problems to filter by", split = ",")
        Set<String> problems;

        @Option(names = "--group", description = "Experiment Group to filter by")
        String groupName;

        @Option(names = "--output", description = "Output file [default, csv, chart]")
        String output;

        @Override
        public void run() {
            ApiClient apiClient = new ApiClient();
            List<Filter> filterList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));
            if (startDate != null && !startDate.isBlank()) {
                filterList.add(new Filter("startDate", formatter.format(Instant.parse(startDate))));
            }
            if (endDate != null && !endDate.isBlank()) {
                filterList.add(new Filter("endDate", formatter.format(Instant.parse(endDate))));
            }
            if (algorithms != null && !algorithms.isEmpty()) {
                filterList.add(new Filter("algorithms", String.join(",", algorithms)));
            }
            if (metrics != null && !metrics.isEmpty()) {
                filterList.add(new Filter("metrics", String.join(",", metrics)));
            }
            if (problems != null && !problems.isEmpty()) {
                filterList.add(new Filter("problems", String.join(",", problems)));
            }
            if (groupName != null && !groupName.isEmpty()) {
                filterList.add(new Filter("groupName", String.join(",", groupName)));
            }
            Filters filters = new Filters(filterList);
            apiClient.getAggregatedResults(filters, OutputType.fromValue(output));
        }
    }

    @Command(name = "delete", description = "Deletes an experiment")
    static class DeleteCommand implements Runnable {
        @Option(names = "--id", description = "Experiment ID")
        String id;

        @Option(names = "--group", description = "Experiment Group Name")
        String groupName;

        @Override
        public void run() {
            ApiClient apiClient = new ApiClient();
            if (id != null) {
                apiClient.deleteExperimentById(id);
            }
            if (groupName != null) {
                apiClient.deleteExperimentGroup(groupName);
            }
        }
    }

}