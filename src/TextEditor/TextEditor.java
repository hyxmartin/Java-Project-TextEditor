//Andrew ID: yuxiangh
//Name: Yuxiang Hu

package hw2;

import java.io.File;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/** TextEditor is a simple application to demonstrate how to develop an 
 * end-to-end application with text-file as 'Model', 
 * GUI as 'View', and various event-handlers as 'Controllers'  
 */
public class TextEditor extends Application {

	Stage stage;
	BorderPane root = new BorderPane(); 	//holds all GUI components
	TextArea fileTextArea = new TextArea(); //displays the file content
	Label statusLabel = new Label();		//shows the status of various actions
	String searchString;					//used in Search function
	StringBuilder fileContent;				//holds the file text

	FileUtilities fileUtilities = new FileUtilities();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		setScreen();
		Scene scene = new Scene(root, 500, 600);
		stage.setTitle("Text Reader");
		stage.setScene(scene);
		stage.show();
	}

	public void setScreen() {
		//create menus
		Menu fileMenu = new Menu("File");
		Menu toolsMenu = new Menu("Tools");
		Menu helpMenu = new Menu("Help");

		//attach File menu items and their event handlers
		MenuItem openFileMenuItem = new MenuItem("Open");
		openFileMenuItem.setOnAction(new OpenFileHandler());
		MenuItem closeFileMenuItem = new MenuItem("Close");
		closeFileMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				fileTextArea.clear();
				statusLabel.setText("");
				fileContent.setLength(0);
			}
		});
		MenuItem exitFileMenuItem = new MenuItem("Exit");
		exitFileMenuItem.setOnAction(actionEvent->Platform.exit());

		//attach Tools menu items and their event handlers
		MenuItem searchToolsMenuItem = new MenuItem("Search");
		searchToolsMenuItem.setOnAction(new SearchToolHandler());
		MenuItem replaceToolsMenuItem = new MenuItem("Replace");
		replaceToolsMenuItem.setOnAction(new ReplaceToolHandler());
		MenuItem wordCountToolsMenuItem = new MenuItem("Word Count");
		wordCountToolsMenuItem.setOnAction(new WordCountToolHandler());

		//set Help menu
		MenuItem aboutHelpMenuItem = new MenuItem("About");
		aboutHelpMenuItem.setOnAction(new AboutHandler());

		//set menubar
		MenuBar menuBar = new MenuBar();

		fileMenu.getItems().addAll(openFileMenuItem, closeFileMenuItem, new SeparatorMenuItem(), exitFileMenuItem);
		toolsMenu.getItems().addAll(searchToolsMenuItem, replaceToolsMenuItem, new SeparatorMenuItem(),wordCountToolsMenuItem);
		helpMenu.getItems().addAll(aboutHelpMenuItem);
		menuBar.getMenus().addAll(fileMenu, toolsMenu, helpMenu);	

		//set status bar
		statusLabel.setPrefWidth(this.stage.getMaxWidth());
		statusLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

		//set layout 
		root.setTop(menuBar);
		root.setCenter(fileTextArea);//Added for display content.
		root.setBottom(statusLabel);
	}

	/**OpenHandler provides the functionality for opening a file when the File-Open menu option 
	 * is selected by the user. 
	 */
	private class OpenFileHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			FileChooser fc = new FileChooser();
			File selectedFile = fc.showOpenDialog(null);
			if (selectedFile != null) {
				fileContent = fileUtilities.readFile(selectedFile.getAbsolutePath());
				statusLabel.setText(selectedFile.getName());
				fileTextArea.setText(fileContent.toString());
			}
		}
	}

	/** getString() is a helper method that opens a dialog box to take user input for a 'purpose'. 
	 * This 'purpose' is meant to be either 'Search' or 'Replace' which
	 * appears as the title and label in the dialog box.
	 * The method returns the string entered by the user.
	 * When user presses Cancel, it returns null.
	 */
	private String getString(String purpose) {   
		String inputString = null;
		TextInputDialog searchTextInputDialog = new TextInputDialog();
		searchTextInputDialog.setTitle(purpose);
		searchTextInputDialog.setHeaderText(String.format("%s string", purpose));
		searchTextInputDialog.setContentText(String.format("Enter %s string ", purpose));
		Optional <String> searchStringOptional = searchTextInputDialog.showAndWait();
		if (searchStringOptional.isPresent()) {
			statusLabel.setText("");
			inputString = searchStringOptional.get();
		}
		if (inputString ==  null || inputString.isEmpty()) return null;  //to handle empty string on Cancel
		else return inputString;
	}

	/**SearchToolHandler provides the functionality for searching a string when the Tool-Search menu option
	 * is selected by the user. It uses the getString() method as a helper method to capture the user input. 
	 */
	private class SearchToolHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
		    String searchString = new String();
		    int[] searchResults;
		    int caretPosition;
		    searchString = getString("search");
		    searchResults = fileUtilities.searchAll(fileContent, searchString);
			//If the search String is null then it will display "Search Cancelled"
		    if (searchString == null || searchString.isEmpty()) {
				statusLabel.setText("Search cancelled");
			} else {
				//If the content is empty or no file has been chosen or nothing return from search, 
				//then the status displays "not found"
				if (searchResults == null || fileContent ==  null || fileContent.toString().isEmpty()) {
					statusLabel.setText(searchString + " not found");
				} else {
					//Display found
					statusLabel.setText(searchString + " found "+ searchResults.length + " times");
					caretPosition = fileTextArea.getCaretPosition();
					for (int c : searchResults) {
						if (c >= caretPosition) {
							//Find the cursor and highlight the next searched string after the cursor 
							fileTextArea.positionCaret(c);
							fileTextArea.selectPositionCaret(c + searchString.length());
							break;
						}						
					}
				}
			}			

		}
	}

	/**ReplaceToolHandler provides the functionality for searching and replaceing a string when the Tool-Replace menu option
	 * is selected by the user. It uses the getString() method as a helper method to capture the user input. 
	 */
	private class ReplaceToolHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			
		    String searchString = new String();
		    int[] searchResults;
		    String replaceString = new String();
		    int caretPosition;
		    int numberReplaced;
		    searchString = getString("search");
		    searchResults = fileUtilities.searchAll(fileContent, searchString);
		    //If the search String is null then it will display "Search Cancelled"
			if (searchString == null || searchString.isEmpty()) {
				statusLabel.setText("Search cancelled");
			} else {
				//If the content is empty or no file has been chosen or nothing return from search, 
				//then the status displays "not found"
				if (searchResults == null || fileContent ==  null || fileContent.toString().isEmpty()) {
					statusLabel.setText(searchString + " not found");
				} else {
					//Display found
					statusLabel.setText(searchString + " found "+ searchResults.length + " times");
					caretPosition = fileTextArea.getCaretPosition();
					for (int c : searchResults) {
						if (c >= caretPosition) {
							//Find the cursor and highlight the next searched string after the cursor
							fileTextArea.positionCaret(c);
							fileTextArea.selectPositionCaret(c + searchString.length());
							break;
						}						
					}
					//When search found, another popup windows allows input replace string
					replaceString = getString("replace");
					//If replace string is empty or click cancel, then it displays "Replace cancelled"
					if (replaceString == null || replaceString.isEmpty()) {
						statusLabel.setText("Replace cancelled");
					} else {
						//If replace string is valid, then replace searched string with replace string.
						numberReplaced = fileUtilities.replace(fileContent, searchString, replaceString);
						fileTextArea.setText(fileContent.toString());
						statusLabel.setText(searchString + " found and replaced with " + replaceString +" at " + numberReplaced + " places");
					}
				}
			}
		}
	}

	/**WordCountToolHandler provides the functionality for counting the total number of words 
	 * in the text file that is currently open.  
	 */
	private class WordCountToolHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			//If content is valid then it displays the count of words, if not display 0 word.
			if (fileContent ==  null || fileContent.toString().isEmpty()) {
				statusLabel.setText("0 word");
			} else {
				statusLabel.setText(fileUtilities.countWords(fileContent)+" words");
			}
			
		}
	}

	private class AboutHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("About");
			alert.setHeaderText("Martin's Text Editor");
			alert.setContentText("Version 1.0 \nRelease 1.0\nCopyright Martin(Yuxiang) Hu\nDeveloped by Martin(Yuxiang) Hu!");
			Image image = new Image(this.getClass().getResource("image.png").toString());
			ImageView imageView = new ImageView();
			imageView.setImage(image);
			imageView.setFitWidth(150);
			imageView.setPreserveRatio(true);
			imageView.setSmooth(true);
			alert.setGraphic(imageView);
			alert.showAndWait();
		}
	}
}
