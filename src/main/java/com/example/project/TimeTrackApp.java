package com.example.project;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The main class for the Time Tracking application.
 */
public class TimeTrackApp extends Application {
    /**
     * Flag to indicate if the time tracking is in progress.
     */
    static boolean isTracking = false;
    /**
     * The Start datetime in milliseconds.
     */
    static long startDatetimeInMillis = 0;
    /**
     * The Complete datetime in milliseconds.
     */
    static long completeDatetimeInMillis = 0;
    /**
     * The List of records.
     */
    static ArrayList<Record> records = new ArrayList<>();

    /**
     * The Button text.
     */
    Text buttonText;
    /**
     * The Input of  project name.
     */
    TextField inputProjectName;
    /**
     * The Text of time.
     */
    Text txtTime;
    /**
     * The Table of record list.
     */
    TableView<Record> tableRecordList;

    /**
     * The entry point of application.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        DataFile.read(records);
        printRecords();
        launch(args);
    }

    /**
     * Prints all records in the records list.
     *
     */
    private static void printRecords() {
        for (int i = 0; i < records.size(); i++) {
            Record rec = records.get(i);
            System.out.println(rec.getProjName() + "," + rec.getStartDate() + "," + rec.getCompleteDate() + "," + Long.toString(rec.getDurationInSecs()));
        }
    }

    /**
     * Create start btn event handler event handler.
     *
     * @return the (mouse-click) event handler of start button
     */
    EventHandler<MouseEvent> createStartBtnEventHandler() {

        return new EventHandler<>() {
            Timer timer = null;

            @Override
            public void handle(MouseEvent e) {
                // check if the time tracking is in progress
                if (isTracking) {
                    // stop time tracking
                    isTracking = false;
                    buttonText.setText("Start");
                    // make the project name input box editable
                    inputProjectName.setEditable(true);
                    // stop timer
                    if (timer != null) {
                        timer.cancel();
                    }
                    // use "N/A" if no input for project name
                    String projectName = inputProjectName.getText().strip();
                    if (projectName.isEmpty()) {
                        projectName = "N/A";
                    }
                    // get the complete date in milliseconds
                    completeDatetimeInMillis = System.currentTimeMillis();
                    // format the start & complete date to string
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    String startDate = sdf.format(new Date(startDatetimeInMillis));
                    String completeDate = sdf.format(new Date(completeDatetimeInMillis));
                    // calculate the duration in seconds
                    long durationInSecs = (completeDatetimeInMillis - startDatetimeInMillis) / 1000;
                    // check if the duration in seconds is zero or not
                    if (durationInSecs != 0) {
                        // create new record
                        Record rec = new Record(
                                projectName,
                                startDate,
                                completeDate,
                                durationInSecs);
                        records.add(rec);
                        // refresh the list
                        tableRecordList.getItems().add(rec);
                        // print all records for debug purpose
                        printRecords();
                        // update the data file
                        DataFile.write(records);
                    } // otherwise ignore it
                } else {
                    // start time tracking
                    isTracking = true;
                    buttonText.setText("Stop");
                    // make the project name input box non-editable
                    inputProjectName.setEditable(false);
                    // create timer task
                    MyTimerTask timerTask = new MyTimerTask(txtTime);
                    // get the start date in milliseconds
                    startDatetimeInMillis = System.currentTimeMillis();
                    timerTask.setStartTimeMillis(startDatetimeInMillis);
                    // create timer and kick it off at 1s fixed rate
                    timer = new Timer(true);
                    timer.scheduleAtFixedRate(timerTask, 0, 1000);
                }
            }
        };
    }

    /**
     * Create report btn event handler event handler.
     *
     * @return the (mouse-click) event handler of report button
     */
    EventHandler<MouseEvent> createReportBtnEventHandler() {

        return new EventHandler<>() {

            @Override
            public void handle(MouseEvent mouseEvent) {

                // create a PieChart data
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

                // aggregate records by the project name
                Map<String, Long> projNameToDurationInSecs = new HashMap<>();
                for (Record record : records) {

                    String projectName = record.getProjName();
                    if (projNameToDurationInSecs.containsKey(projectName)) {
                        long durationInSecs = projNameToDurationInSecs.get(projectName);
                        projNameToDurationInSecs.put(projectName, durationInSecs + record.getDurationInSecs());
                    } else {
                        projNameToDurationInSecs.put(projectName, record.getDurationInSecs());
                    }
                }
                // reorder aggregated project-duration pairs in descending order by duration
                List<Map.Entry<String, Long>> projects = new ArrayList<>(projNameToDurationInSecs.entrySet());
                projects.sort(Map.Entry.comparingByValue());
                Collections.reverse(projects);
                // select the top 10 projects regarding the duration of time
                // the total of durations of all records in seconds
                long totalDurationInSecs = 0;
                for (int i = 0; i < Long.min(10, projects.size()); i++) {
                    Map.Entry<String, Long> project = projects.get(i);
                    totalDurationInSecs += project.getValue();
                }
                for (int i = 0; i < Long.min(10, projects.size()); i++) {
                    Map.Entry<String, Long> project = projects.get(i);
                    String projName = project.getKey();
                    // shorten long project name,
                    // if greater than 8, gets truncated to 7 characters followed by "..."
                    if (projName.length() > 8) {
                        projName = projName.substring(0, 7) + "...";
                    }
                    long durationInSecs = project.getValue();
                    pieChartData.add(new PieChart.Data(
                            projName + "\n" + String.format("%.1f%%", durationInSecs * 1.0f / totalDurationInSecs * 100),
                            project.getValue()
                    ));
                }

                // create a dialog
                Dialog<String> dialog = new Dialog<String>();
                dialog.setTitle("Task Report");
                dialog.setWidth(600);
                dialog.setHeight(600);

                // create a pie-chart
                PieChart pieChart = new PieChart(pieChartData);
                pieChart.setTitle("Project Time Distribution");
                pieChart.setStyle("-fx-font-family: 'Inter'; -fx-font-weight: Normal; -fx-font-size: 12px;");

                // create a vbox
                VBox dialogPane = new VBox();
                dialogPane.setPadding(new Insets(40, 0, 0, 0));
                dialogPane.getChildren().add(pieChart);

                // set up the dialog pane
                dialog.getDialogPane().setContent(dialogPane);

                // make dialog closable
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
                closeButton.setVisible(false);

                // show the dialog
                dialog.showAndWait();
            }
        };
    }

    /**
     * The entry point for the JavaFX application.
     * Initializes and displays the main user interface.
     *
     * @param primaryStage The primary stage for this application, onto which the application scene is set.
     */
    @Override
    public void start(Stage primaryStage) {

        Text txtTrackTime = new Text("Track Time");
        txtTrackTime.setFont(Font.font("Inter", FontWeight.NORMAL, 24));

        // Create TextField for inputting project name
        inputProjectName = new TextField();
        inputProjectName.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2px;-fx-border-radius: 40px; -fx-background-radius: 40px; -fx-alignment: center");
        inputProjectName.setMinWidth(480);
        inputProjectName.setPrefWidth(480);
        inputProjectName.setMaxWidth(480);
        inputProjectName.setPrefHeight(30);

        // Create Text for displaying time
        txtTime = new Text("00:00:00");
        txtTime.setFont(Font.font("Inter", FontWeight.NORMAL, 24));
        txtTime.setStyle("-fx-text-width: 120;");

        //Create ImageView for button effect.
        ImageView buttonEffect = new ImageView("buttonEffect2.png");
        buttonEffect.setFitHeight(110);
        buttonEffect.setFitWidth(110);

        // Create Text for button label.
        buttonText = new Text("Start");
        buttonText.setFont(Font.font("Inter", FontWeight.NORMAL, 20));
        buttonText.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#D003F1")),
                new Stop(1, Color.web("#18303F"))));

        // Create a StackPane to combine button effect and label
        StackPane buttonStyle = new StackPane();
        buttonStyle.getChildren().addAll(buttonEffect, buttonText);
        buttonStyle.setPrefSize(80, 80);
        buttonStyle.setAlignment(Pos.CENTER);
        // Create the round Start button.
        Button btnStart = new Button();
        btnStart.setShape(new Circle(40));
        btnStart.setGraphic(buttonStyle);
        btnStart.setContentDisplay(ContentDisplay.CENTER);
        btnStart.setMaxWidth(80);
        btnStart.setMaxHeight(80);
        btnStart.setMinWidth(80);
        btnStart.setMinHeight(80);
        btnStart.setStyle(
                "-fx-cursor: hand; -fx-background-size: contain; -fx-background-color: transparent; -fx-border-color: transparent;");

        // Add event handler for Start button
        btnStart.addEventFilter(MouseEvent.MOUSE_CLICKED, createStartBtnEventHandler());

        Text txtProjectTaskList = new Text("Project Task List");
        txtProjectTaskList.setFont(Font.font("Inter", FontWeight.NORMAL, 24));

        // Create TableView for displaying project records
        tableRecordList = new TableView<>();
        tableRecordList.setMaxWidth(436);
        tableRecordList.setPrefHeight(188);

        // Create TableColumn for project name
        TableColumn<Record, String> column1 =
                new TableColumn<>("Project Name");
        column1.setCellValueFactory(
                new PropertyValueFactory<>("projName"));
        column1.setPrefWidth(110);

        // Create TableColumn for start date
        TableColumn<Record, String> column2 =
                new TableColumn<>("Start Date");
        column2.setCellValueFactory(
                new PropertyValueFactory<>("startDate"));
        column2.setPrefWidth(113);

        // Create TableColumn for complete date
        TableColumn<Record, String> column3 =
                new TableColumn<>("Complete Date");
        column3.setCellValueFactory(
                new PropertyValueFactory<>("completeDate"));
        column3.setPrefWidth(113);

        // Create TableColumn for duration
        TableColumn<Record, String> column4 =
                new TableColumn<>("Duration");
        column4.setCellValueFactory(
                new PropertyValueFactory<>("duration"));
        column4.setPrefWidth(100);

        // Add records to the table
        tableRecordList.getItems().addAll(records);

        // Apply the CSS style to the cells of each column
        String cellStyle = "-fx-font-family: 'Inter'; -fx-font-weight: Normal; -fx-font-size: 10px; -fx-alignment: CENTER;";

        column1.setStyle(cellStyle);
        column2.setStyle(cellStyle);
        column3.setStyle(cellStyle);
        column4.setStyle(cellStyle);

        tableRecordList.getColumns().add(column1);
        tableRecordList.getColumns().add(column2);
        tableRecordList.getColumns().add(column3);
        tableRecordList.getColumns().add(column4);

        HBox hBox3rdRow = new HBox();
        hBox3rdRow.getChildren().addAll(tableRecordList);

        // Create VBox to hold "Project Task List" text and the table
        VBox vbox1 = new VBox(24);
        vbox1.getChildren().addAll(txtProjectTaskList, tableRecordList);
        vbox1.setAlignment(Pos.TOP_CENTER);
        vbox1.setPadding(new Insets(24, 0, 0, 0));

        // Create rectangle for background.
        Rectangle rectangle1 = new Rectangle(480, 300, Color.WHITE);
        rectangle1.setArcWidth(40.0);
        rectangle1.setArcHeight(40.0);

        // Create StackPane to hold the vbox1 and background rectangle
        StackPane projectTaskList = new StackPane();
        projectTaskList.getChildren().addAll(rectangle1, vbox1);
        StackPane.setAlignment(rectangle1, Pos.CENTER);

        Text txtReport = new Text("Report");
        txtReport.setFont(Font.font("Inter", FontWeight.NORMAL, 20));
        txtReport.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#D003F1")),
                new Stop(1, Color.web("#18303F"))));

        // Create ImageView for report button effect.
        ImageView vectorBt = new ImageView("vector.png");
        vectorBt.setFitHeight(14);
        vectorBt.setFitWidth(8);

        Button reportBt = new Button();
        reportBt.setGraphic(vectorBt);
        reportBt.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        reportBt.setContentDisplay(ContentDisplay.CENTER);
        reportBt.setPrefWidth(8);
        reportBt.setPrefHeight(14);

        // Add event handler for report button
        reportBt.addEventFilter(MouseEvent.MOUSE_CLICKED, createReportBtnEventHandler());

        // Create HBox to hold "Report" text and report button
        HBox reportPane = new HBox();
        reportPane.getChildren().addAll(txtReport, reportBt);
        reportPane.setSpacing(210);

        reportPane.setAlignment(Pos.CENTER);
        reportPane.setLayoutX(30);
        reportPane.setLayoutY(10);

        // Create background rectangle for report pane
        Rectangle rectangleBg = new Rectangle(324, 50);
        rectangleBg.setArcWidth(40.0);
        rectangleBg.setArcHeight(40.0);
        rectangleBg.setX(30);
        rectangleBg.setY(24);

        // Create ImageView for background effect
        ImageView imageView = new ImageView("bar.png");
        imageView.setFitWidth(324); // Set your desired width
        imageView.setFitHeight(50); // Set your desired height

        ImageInput imageInput = new ImageInput();
        imageInput.setSource(imageView.getImage());

        // Apply effect to background rectangle
        rectangleBg.setEffect(imageInput);

        StackPane reportPaneBg = new StackPane();
        reportPaneBg.setPrefSize(324, 50);
        reportPaneBg.setAlignment(Pos.CENTER);
        reportPaneBg.getChildren().addAll(rectangleBg, reportPane);

        // Create the grid pane for all layout display.
        GridPane layoutGridPane = new GridPane();
        layoutGridPane.setAlignment(Pos.CENTER);
        layoutGridPane.setPadding(new Insets(20, 16, 20, 16));

        layoutGridPane.add(txtTrackTime, 0, 0);
        layoutGridPane.add(inputProjectName, 0, 1);
        layoutGridPane.add(txtTime, 0, 2);
        layoutGridPane.add(btnStart, 0, 3);
        layoutGridPane.add(projectTaskList, 0, 4);
        layoutGridPane.add(reportPaneBg, 0, 5);

        GridPane.setMargin(txtTrackTime, new Insets(20, 230, 20, 230));
        GridPane.setMargin(inputProjectName, new Insets(0, 40, 40, 40));
        GridPane.setMargin(txtTime, new Insets(0, 260, 0, 260));
        GridPane.setMargin(btnStart, new Insets(20, 235, 40, 235));
        GridPane.setMargin(projectTaskList, new Insets(10, 40, 24, 40));
        GridPane.setMargin(reportPaneBg, new Insets(10, 68, 30, 68));

        GridPane.setHalignment(txtTrackTime, HPos.CENTER);
        GridPane.setHalignment(inputProjectName, HPos.CENTER);
        GridPane.setHalignment(txtTime, HPos.CENTER);
        GridPane.setHalignment(btnStart, HPos.CENTER);

        Scene scene = new Scene(layoutGridPane, 540, 740);
        primaryStage.setTitle("Remote Project Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}



