//package sample;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;


public class QuizViewer extends Application {

    static int status=0;
    static int res = 0;
    @Override
    public void start(Stage primaryStage) throws Exception{

        StackPane pane = new StackPane();


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open the Quiz");

        Button button = new Button("Load file");
        button.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if(file!=null) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    primaryStage.close();
                    Quiz quiz = Quiz.loadFromFile(file.toString());
                    Collections.shuffle(quiz.getQuestions());
                    BorderPane pane1 = new BorderPane();
                    TextArea textArea = new TextArea();

                    textArea.setEditable(false);

                    Button bright = new Button(">>");
                    Button bleft = new Button("<<");
                    Button bcheck = new Button("Check Answers");
                    Label label = new Label();
                    HBox hBox = new HBox(50);
                    hBox.getChildren().addAll(label,bcheck);

                    HashMap<String,Node> map = new HashMap<>();



                    if(quiz.getQuestions().get(status) instanceof FillIn){
                        TextField textField = new TextField();
                        pane1.setCenter(textField);
                    }
                    pane1.setTop(textArea);
                    pane1.setLeft(bleft);
                    pane1.setRight(bright);
                    pane1.setBottom(hBox);

                    label.setText("Status: "+status+"/"+quiz.getQuestions().size()+" questions");

                    for (int i = 0; i <quiz.getQuestions().size() ; i++) {
                        if(quiz.getQuestions().get(i) instanceof FillIn){
                            TextField textField = new TextField();
                            map.put(quiz.getQuestions().get(i).getDescription(),textField);
                        }
                        else if(quiz.getQuestions().get(i) instanceof Test){
                            VBox vBox = new VBox();
                            ToggleGroup togle=new ToggleGroup();
                            ArrayList list = new ArrayList();
                            for (int j = 0; j <4 ; j++) {
                                if(((Test) quiz.getQuestions().get(i)).getOptionAt(j)==null){
                                    RadioButton radioButton = new RadioButton(quiz.getQuestions().get(i).getAnswer());
                                    list.add(radioButton);
//                                    vBox.getChildren().add(radioButton);
                                    radioButton.setToggleGroup(togle);
                                }
                                else{

                                    RadioButton radioButton = new RadioButton(((Test) quiz.getQuestions().get(i)).getOptionAt(j));
//                                    vBox.getChildren().add(radioButton);
                                    list.add(radioButton);
                                    radioButton.setToggleGroup(togle);
                                }
                            }
                            Collections.shuffle(list);
                            for (int j = 0; j <list.size() ; j++) {
                                vBox.getChildren().add((Node) list.get(j));
                            }

                            map.put(quiz.getQuestions().get(i).getDescription(),vBox);

                        }

                    }

                    textArea.setText(quiz.getQuestions().get(status).getDescription().replace("{blank}","_____"));
                    pane1.setCenter((Node) map.get(quiz.getQuestions().get(status).getDescription()));




                    bright.setOnAction(event1 -> {
                        if(status<quiz.getQuestions().size()-1){
                            status++;
                            label.setText("Status: "+(status+1)+"/"+quiz.getQuestions().size()+" questions");
                            textArea.setText(quiz.getQuestions().get(status).getDescription().replace("{blank}","_____"));
                            pane1.setCenter((Node) map.get(quiz.getQuestions().get(status).getDescription()));
                        }
                    });

                    bleft.setOnAction(event1 -> {
                        if(status>0){
                            status--;
                            label.setText("Status: "+(status+1)+"/"+quiz.getQuestions().size()+" questions");
                            textArea.setText(quiz.getQuestions().get(status).getDescription().replace("{blank}","_____"));
                            pane1.setCenter((Node) map.get(quiz.getQuestions().get(status).getDescription()));
                        }
                    });
                    bcheck.setOnAction(event1 -> {
                        for(int i=0;i<quiz.getQuestions().size();i++){
                            if(quiz.getQuestions().get(i) instanceof FillIn){
                                TextField tex =(TextField) map.get(quiz.getQuestions().get(i).getDescription());
                                if(tex.getText().toLowerCase().equals(quiz.getQuestions().get(i).getAnswer().toLowerCase()))     {
                                    res++;
                                }
                            }
                            else if(quiz.getQuestions().get(i) instanceof Test){
                                VBox vb = (VBox) map.get(quiz.getQuestions().get(i).getDescription());
                                for (int j = 0; j < vb.getChildren().size(); j++) {
                                    RadioButton rb =(RadioButton) vb.getChildren().get(j);
                                    if(rb.isSelected()){
                                        if(rb.getText().equals(quiz.getQuestions().get(i).getAnswer())){
                                            res++;
                                        }
                                    }
                                }
                            }
                        }
                        Alert results = new Alert(Alert.AlertType.CONFIRMATION);
                        results.setHeaderText("Number of correct answers: "+res+"/"+quiz.getQuestions().size());
                        results.setContentText("You may try again");
                        results.show();

                        res=0;
                    });


                    Stage stage = new Stage();
                    Scene scene = new Scene(pane1,800,400);
                    stage.setScene(scene);
                    stage.show();
                    stage.setResizable(false);



                } catch (Exception e) {
                    primaryStage.show();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("InvalidQuizFormatException");
                    alert.setTitle("QuizViewer: ERROR");
                    alert.setContentText("The file selected does not fit the requirements for a standard Quiz text file format...");
                    alert.show();

                }
            }
        });
        pane.getChildren().add(button);





        Scene scene = new Scene(pane,600,600);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);

    }



    public static void main(String[] args) {
        launch(args);

    }
}
