/**
 * 
 */
/**
 * @author tbuzzelli
 *
 */
module veris {
	exports com.verisjudge.ui;
	exports com.verisjudge.checker;
	exports com.verisjudge.utils;
	exports com.verisjudge;

	opens com.verisjudge.ui;
	opens com.verisjudge.checker;
	opens com.verisjudge.utils;
	opens com.verisjudge;
	
	requires gson;
	requires diffutils;
	requires org.apache.commons.lang3;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.web;
	requires javafx.media;
}