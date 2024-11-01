module SchemaInsght {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.commons.csv;
    requires java.sql;

    exports com.schemainsight;
    opens com.schemainsight to javafx.fxml;

}