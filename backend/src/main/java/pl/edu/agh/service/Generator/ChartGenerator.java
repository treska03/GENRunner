package pl.edu.agh.service.Generator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import pl.edu.agh.dto.AggregatedExperimentDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartGenerator {
    public ChartGenerator() {
    }

    public byte[] generateChartContent(
            List<AggregatedExperimentDto> aggregatedResults
    ) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, List<AggregatedExperimentDto>> groupedResults = aggregatedResults.stream()
                .collect(Collectors.groupingBy(result -> result.algorithm() + "|" + result.problem() + "|" + result.indicator()));

        for (Map.Entry<String, List<AggregatedExperimentDto>> entry : groupedResults.entrySet()) {
            String[] keys = entry.getKey().split("\\|");
            String algorithm = keys[0];
            String problem = keys[1];
            String indicator = keys[2];
            String seriesTitle = algorithm + " - " + problem + " - " + indicator;

            for (AggregatedExperimentDto result : entry.getValue()) {
                dataset.addValue((Number) result.average(), seriesTitle, result.iteration());
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Aggregated Results",
                "Iteration",
                "Average",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(outputStream, chart, 800, 600);
        return outputStream.toByteArray();
    }
}
