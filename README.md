# Document Search App

The Document Search App is a JavaFX application designed to facilitate the search and organization of documents based on a list of filenames or keywords. It allows users to select a file containing the search criteria (either in `.txt` or `.xlsx` format), specify a directory to search within, and choose a target directory for the found documents. This application is especially useful for managing large collections of documents and automating the process of finding and organizing files according to specific criteria.

## Features

- **File Selection:** Choose a `.txt` or `.xlsx` file containing a list of filenames or keywords to search for.
- **Directory Selection:** Specify the search directory where the application should look for the files and the target directory where the found files will be copied.
- **Support for Multiple Formats:** Works with text (`*.txt`) and Excel (`*.xlsx`) files for search criteria.
- **Preview of Search Criteria:** Displays the list of search criteria from the selected file.
- **Search Process Logging:** Logs the outcome of the search process, including errors and the status of found files.

## Prerequisites

Before you can run the Document Search App, ensure you have the following installed:
- JDK 8 or later
- JavaFX SDK
- Apache POI library (for handling `.xlsx` files)

## Setup

1. **Download the Source Code:** Clone or download this repository to your local machine.
2. **Import the Project:** Open your IDE (e.g., Eclipse, IntelliJ IDEA) and import the project.
3. **Configure JavaFX:** Ensure JavaFX is correctly configured in your project's build path or module settings.
4. **Add External Libraries:** Add the Apache POI library to your project's build path.

## Running the Application

To run the Document Search App, follow these steps:

1. **Launch the Application:** Run the `DocumentSearchApp` class. This will open the application's GUI.
2. **Select Search Criteria File:** Click on the "Select File" button and choose a `.txt` or `.xlsx` file that contains the list of filenames or keywords to search for.
3. **Select Search Directory:** Click on the "Select Search Directory" button to specify the directory where the application should look for the files.
4. **Select Target Directory:** Click on the "Select Target Directory" button to choose the directory where the found files will be copied.
5. **Start the Search:** Click on the "Start Search" button to initiate the search process. The outcomes will be logged in the application's interface.

## Development

This application is built using JavaFX for the graphical user interface and Apache POI for handling Excel files. The main components are:

- `DocumentSearchApp.java`: The entry point of the application, responsible for launching the JavaFX application.
- `DocumentSearchController.java`: Handles the application logic, including file and directory selection, reading the search criteria file, searching for documents, and copying found files.

### Customization

You can customize the application by modifying the source code. For example, you can change the GUI by editing the `GUIdesign.fxml` file with Scene Builder or directly in your IDE.

## Contributing

Contributions to the Document Search App are welcome! If you have suggestions for improvements or bug fixes, please feel free to fork the repository, make your changes, and submit a pull request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
