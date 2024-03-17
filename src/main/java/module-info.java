module com.example.calendrier_ceri_ines_maryem {
    requires org.mnode.ical4j.core;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires org.json;
    opens com.example.calendrier_ceri_ines_maryem to javafx.fxml;
    exports com.example.calendrier_ceri_ines_maryem;
}