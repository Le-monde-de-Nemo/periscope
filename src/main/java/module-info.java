module fr.eirb.lemondedenemo.periscope {
    requires javafx.controls;
    requires javafx.fxml;


    opens fr.eirb.lemondedenemo.periscope to javafx.fxml;
    exports fr.eirb.lemondedenemo.periscope;
}